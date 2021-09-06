import './App.css';
import { AppBar, createStyles, WithStyles, withStyles, Theme, Toolbar, Typography, IconButton, Tooltip, Box, Menu, MenuItem } from '@material-ui/core';
import {
  Menu as MenuIcon, FileCopyRounded as FileCopyIcon, CameraAltRounded as CameraIcon, ExitToAppRounded as LogoutIcon,
  SettingsRounded as SettingsIcon, ViewListRounded as ExcelIcon
} from '@material-ui/icons';
import { Route, Switch, useHistory } from 'react-router-dom';
import RetroComments from './components/comments/RetroComments';
import { createRef, useCallback, useRef } from 'react';
import { toPng } from 'html-to-image';
import React from 'react';
import Home from './components/home/Home';
import { useCurrentContext } from './components/context/ContextProvider';
import { useLayoutEffect } from 'react';
import { DataService } from './services/DataService';
import { SessionModel } from './models/SessionModel';
import { CSVLink } from 'react-csv';
import { CommentModel } from './models/CommentModel';

const useStyles = (theme: Theme) => createStyles({
  toolbar: theme.mixins.toolbar,
  appBar: {
    background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
    color: 'white',
    height: '60px'
  },
  csvLinkText: {
    textDecoration: 'none',
    color: 'black'
  }
});

interface Props extends WithStyles<typeof useStyles> {
}

function App(props: Props) {
  const ref = createRef();
  const csvLinkRef = useRef<CSVLink & HTMLAnchorElement & { link: HTMLAnchorElement }>(null);
  const { currentState: contextState, dispatch } = useCurrentContext();
  const history = useHistory();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const [comments, setComments] = React.useState(new Array<CommentModel>());

  useLayoutEffect(() => {
    DataService.init(contextState.clientId);
  }, [contextState.clientId]);

  const onScreenShot = useCallback(() => {
    handleClose();
    if (ref.current === null) {
      return
    }

    toPng(ref.current as HTMLElement, { cacheBust: true, })
      .then((dataUrl) => {
        const link = document.createElement('a')
        link.download = contextState.session.sessionId + ".png";
        link.href = dataUrl
        link.click();
      })
      .catch((err) => {
        console.log(err);
      })
  }, [ref, contextState.session.sessionId]);

  const onExcelExport = async () => {
    handleClose();
    await DataService.getCommentsForSession(contextState.session.sessionId).then(res => {
      setComments(res);
      console.log("clicked")
      csvLinkRef.current?.link.click();
    });
  }

  const onLogout = () => {
    DataService.removeEventSubscribtion(contextState.session.sessionId + "_" + contextState.clientId);
    dispatch({ type: "SESSION", data: { clientId: "", name: "", session: new SessionModel() } });
    dispatch({ type: "CLIENT_ID", data: { clientId: "", name: "", session: new SessionModel() } });
    dispatch({ type: "NAME", data: { clientId: "", name: "", session: new SessionModel() } });
    history.push('/');
  }

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <div className="App" ref={ref as React.RefObject<HTMLDivElement>}>
      <header className="App-header">
        <AppBar className={props.classes.appBar} position='sticky'>
          <Toolbar>
            <IconButton><MenuIcon /></IconButton>
            <Typography variant="h6" style={{ flex: 1 }}>RETRO METER</Typography>
            {/* <HeaderUserbox /> */}
            {contextState.session.sessionId !== "" ?
              <Box>
                <Tooltip title="Options" placement="top-start">
                  <IconButton onClick={handleMenu}>
                    <SettingsIcon />
                  </IconButton>
                </Tooltip>
                <Tooltip title="copy session id" placement="top-start">
                  <IconButton onClick={() => navigator.clipboard.writeText(contextState.session.sessionId)}>
                    <FileCopyIcon />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Exit" placement="top-start">
                  <IconButton onClick={onLogout}>
                    <LogoutIcon />
                  </IconButton>
                </Tooltip>
              </Box> : null}
          </Toolbar>
        </AppBar>
        <Switch>
          <Route exact path="/">
            <Home />
          </Route>
          <Route exact path="/main">
            <RetroComments />
          </Route>
        </Switch>
      </header>
      <Menu
        id="menu-appbar"
        anchorEl={anchorEl}
        anchorOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
        keepMounted
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
        open={open}
        onClose={handleClose}
      >
        <MenuItem onClick={onScreenShot}>
          <IconButton>
            <CameraIcon />
          </IconButton>
          Screenshot
        </MenuItem>
        <MenuItem onClick={onExcelExport}>
          <IconButton>
            <ExcelIcon />
          </IconButton> Export Excel
        </MenuItem>
        <CSVLink data={comments} ref={csvLinkRef} filename={contextState.session.sessionId + ".csv"} />
      </Menu>
    </div >
  );
}

export default withStyles(useStyles)(App);
