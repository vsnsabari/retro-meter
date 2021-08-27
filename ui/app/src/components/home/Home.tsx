import React, { useState } from 'react';
import {
    Box, Button, Dialog, DialogActions, DialogContent,
    DialogContentText, DialogTitle, CircularProgress,
    TextField, Typography, WithStyles, withStyles, Container
} from '@material-ui/core';
import { homeStyles } from './Home.styles';
import { useHistory } from 'react-router-dom';
import { Add, Link } from '@material-ui/icons';
import { DataService } from '../../services/DataService';
import { useCurrentContext } from '../context/ContextProvider';

interface Props extends WithStyles<typeof homeStyles> {
}

const Home: React.FC<Props> = (props) => {
    const [isOpen, setOpen] = useState(false);
    const [isNewSession, setNewSession] = useState(false);
    const [sessionParams, setSessionParams] = useState({ name: "", team: "", id: "" });
    const [isLoading, setLoading] = useState(false);
    const history = useHistory();
    const { currentState, dispatch } = useCurrentContext();

    const handleClose = () => {
        setOpen(false);
    }

    const handleNewSessionClick = () => {
        setOpen(true);
        setNewSession(true);
    }

    const handleExistingSessionClick = () => {
        setOpen(true);
        setNewSession(false);
    }

    const onCreate = async () => {
        setLoading(true);
        const reqParams = {
            "startedBy": sessionParams.name,
            "team": sessionParams.team
        }
        await DataService.createSession(reqParams).then(res => {
            setLoading(false);
            dispatch({ type: "SESSION", data: { ...currentState, session: res } });
            history.push('/main', res);
        });
        setOpen(false);
    }

    const onJoin = async () => {
        setLoading(true);
        await DataService.getSession(sessionParams.id).then(res => {
            setLoading(false);
            dispatch({ type: "SESSION", data: { ...currentState, session: res } });
            history.push('/main', res);
        });
    }

    const setTextValue = (event: React.BaseSyntheticEvent, field: "Team" | "SessionId" | "Name") => {
        switch (field) {
            case "Name":
                setSessionParams({ ...sessionParams, name: event.target.value });
                dispatch({ type: "NAME", data: { ...currentState, name: event.target.value } });
                break;
            case "Team":
                setSessionParams({ ...sessionParams, team: event.target.value });
                break;
            case "SessionId":
                setSessionParams({ ...sessionParams, id: event.target.value });
                break;
        }
    }

    return (
        <Container className={props.classes.container}>
            <Typography variant="h6">Welcome to Retro Meter</Typography>
            <Typography variant="h6">Please choose New Session to create a new session or Existing Session to join existing one.</Typography>
            <br />
            <Box display="flex">
                <Box m={2}>
                    <Button className={props.classes.button} onClick={handleNewSessionClick} endIcon={<Add />}>New Session</Button>
                </Box>
                <Box m={2}>
                    <Button className={props.classes.button} onClick={handleExistingSessionClick} endIcon={<Link />}>Existing Session</Button>
                </Box>
            </Box>
            <Dialog open={isOpen} onClose={handleClose} aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">{isNewSession ? "New" : "Existing"} Session</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        {isNewSession ? "To start a new session, please prove your name and team" : "To join existing session, please provide session id"}
                    </DialogContentText>
                    <TextField
                        autoFocus
                        margin="dense"
                        id="name"
                        label="Name"
                        type="text"
                        fullWidth
                        value={sessionParams.name}
                        onChange={(e) => setTextValue(e, "Name")}
                    />
                    {isNewSession ?
                        <TextField
                            margin="dense"
                            id="team"
                            label="Team"
                            type="text"
                            value={sessionParams.team}
                            onChange={(e) => setTextValue(e, "Team")}
                            fullWidth
                        /> : null}
                    {!isNewSession ?
                        <TextField
                            margin="dense"
                            id="sessionId"
                            label="Session Id"
                            type="text"
                            value={sessionParams.id}
                            onChange={(e) => setTextValue(e, "SessionId")}
                            fullWidth
                        /> : null}
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} className={props.classes.smallButton} >
                        Cancel
                    </Button>
                    <div className={props.classes.wrapper}>
                        <Button onClick={isNewSession ? onCreate : onJoin} className={props.classes.smallButton} disabled={isLoading}>
                            {isNewSession ? "Create" : "Join"}
                        </Button>
                        {isLoading && <CircularProgress size={24} className={props.classes.buttonProgress} />}
                    </div>
                </DialogActions>
            </Dialog>
        </Container>
    )
}

export default withStyles(homeStyles)(Home);