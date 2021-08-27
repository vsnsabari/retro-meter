import './App.css';
import { AppBar, createStyles, WithStyles, withStyles, Theme, Toolbar, Typography, IconButton, Tooltip } from '@material-ui/core';
import { Menu as MenuIcon, FileCopyRounded as FileCopyIcon, CameraAltRounded as CameraIcon } from '@material-ui/icons';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import RetroComments from './components/comments/RetroComments';
import { createRef, useCallback } from 'react';
import { toPng } from 'html-to-image';
import React from 'react';
import Home from './components/home/Home';
import { useCurrentContext } from './components/context/ContextProvider';
import { useLayoutEffect } from 'react';
import { DataService } from './services/DataService';

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
  const { currentState: state } = useCurrentContext();

  useLayoutEffect(() => {
    DataService.init(state.clientId);
  }, [state.clientId]);

  const onButtonClick = useCallback(() => {
    if (ref.current === null) {
      return
    }

    toPng(ref.current as HTMLElement, { cacheBust: true, })
      .then((dataUrl) => {
        const link = document.createElement('a')
        link.download = state.session.sessionId + ".png";
        link.href = dataUrl
        link.click()
      })
      .catch((err) => {
        console.log(err)
      })
  }, [ref, state.session.sessionId]);

  return (
    <BrowserRouter>
      <div className="App" ref={ref as React.RefObject<HTMLDivElement>}>
        <header className="App-header">
          <AppBar className={props.classes.appBar} position='sticky'>
            <Toolbar>
              <IconButton><MenuIcon /></IconButton>
              <Typography variant="h6" style={{ flex: 1 }}>RETRO METER</Typography>
              {/* <HeaderUserbox /> */}
              <Tooltip title="copy session id" placement="top-start">
                <IconButton onClick={() => navigator.clipboard.writeText(state.session.sessionId)}>
                  <FileCopyIcon />
                </IconButton>
              </Tooltip>
              <Tooltip title="take screen shot" placement="top-start">
                <IconButton onClick={onButtonClick}>
                  <CameraIcon />
                </IconButton>
              </Tooltip>
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
    </BrowserRouter>
  );
}

export default withStyles(useStyles)(App);
