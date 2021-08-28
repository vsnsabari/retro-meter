import './App.css';
import { AppBar, createStyles, WithStyles, withStyles, Theme, Toolbar, Typography, IconButton, Tooltip, Box } from '@material-ui/core';
import { Menu as MenuIcon, FileCopyRounded as FileCopyIcon, CameraAltRounded as CameraIcon, ExitToAppRounded as LogoutIcon } from '@material-ui/icons';
import { Route, Switch, useHistory } from 'react-router-dom';
import RetroComments from './components/comments/RetroComments';
import { createRef, useCallback } from 'react';
import { toPng } from 'html-to-image';
import React from 'react';
import Home from './components/home/Home';
import { useCurrentContext } from './components/context/ContextProvider';
import { useLayoutEffect } from 'react';
import { DataService } from './services/DataService';
import { SessionModel } from './models/SessionModel';

const useStyles = (theme: Theme) => createStyles({
  toolbar: theme.mixins.toolbar,
  appBar: {
    background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
    color: 'white',
    height: '60px'
  },
});

interface Props extends WithStyles<typeof useStyles> {
}

function App(props: Props) {
  const ref = createRef();
  const { currentState: contextState, dispatch } = useCurrentContext();
  const history = useHistory();

  useLayoutEffect(() => {
    DataService.init(contextState.clientId);
  }, [contextState.clientId]);

  const onScreenShot = useCallback(() => {
    if (ref.current === null) {
      return
    }

    toPng(ref.current as HTMLElement, { cacheBust: true, })
      .then((dataUrl) => {
        const link = document.createElement('a')
        link.download = contextState.session.sessionId + ".png";
        link.href = dataUrl
        link.click()
      })
      .catch((err) => {
        console.log(err)
      })
  }, [ref, contextState.session.sessionId]);

  const onLogout = () => {
    DataService.removeEventSubscribtion(contextState.session.sessionId + "_" + contextState.clientId);
    dispatch({ type: "SESSION", data: { clientId: "", name: "", session: new SessionModel() } });
    dispatch({ type: "CLIENT_ID", data: { clientId: "", name: "", session: new SessionModel() } });
    dispatch({ type: "NAME", data: { clientId: "", name: "", session: new SessionModel() } });
    history.push('/');
  }

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
                <Tooltip title="copy session id" placement="top-start">
                  <IconButton onClick={() => navigator.clipboard.writeText(contextState.session.sessionId)}>
                    <FileCopyIcon />
                  </IconButton>
                </Tooltip>
                <Tooltip title="take screen shot" placement="top-start">
                  <IconButton onClick={onScreenShot}>
                    <CameraIcon />
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
    </div >
  );
}

export default withStyles(useStyles)(App);
