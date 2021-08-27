import { Grid, LinearProgress, withStyles, WithStyles } from '@material-ui/core';
import React from 'react';
import { useState } from 'react';
import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { eventBus } from '../../events/EventBus';
import { CommentModel } from '../../models/CommentModel';
import { CommentType } from '../../models/CommentType';
import { EventModel } from '../../models/EventModel';
import { SessionModel } from '../../models/SessionModel';
import { DataService } from '../../services/DataService';
import RetroCategory from '../category/RetroCategory';
import RetroCommentModal from '../comment/RetroComment.modal';
import { useCurrentContext } from '../context/ContextProvider';
import { commentsStyles } from './RetroComments.styles';

interface Props extends WithStyles<typeof commentsStyles> {

}

const RetroComments: React.FC<Props> = ({ classes }) => {

    const { state } = useLocation<SessionModel>();
    const [comments, setComments] = useState(new Array<CommentModel>());
    const [isLoading, setLoading] = useState(true);
    const [isShowModal, setShowModal] = useState(false);
    const [currentType, setCurrentType] = useState(CommentType.NONE);
    const [isSubscribed, setSubscribed] = useState(false);
    let sseEvent: EventSource | undefined = undefined;
    const { currentState, dispatch } = useCurrentContext();

    useEffect(() => {
        async function fetchComments() {
            await getComments();
        }

        fetchComments();

    }, [state]);

    useEffect(() => {
        dispatch({ type: "SESSION", data: { ...currentState, session: state } });
    }, [state]);

    useEffect(() => {
        if (!isSubscribed) {
            sseEvent = new EventSource(`https://retrometer.azurewebsites.net/feed/subscribe/${state.sessionId}_${currentState.clientId}`);
            sseEvent.onmessage = e => getRealtimeData(JSON.parse(e.data) as EventModel);
            sseEvent.onerror = (err: any) => {
                console.log(err);
                setSubscribed(false);
                sseEvent?.close();
            }
            setSubscribed(true);
        }
        return () => {
            setSubscribed(false);
            sseEvent?.close();
        };

        function getRealtimeData(data: EventModel) {
            switch (data.type) {
                case "ADDED":
                    console.log("ADDED");
                    getComments();
                    break;
                case "EDITED":
                    console.log("EDITED");
                    eventBus.dispatch("EDITED", data.comment);
                    break;
                case "REMOVED":
                    console.log("REMOVED");
                    getComments();
                    break;
            }
        }

    }, []);

    const getComments = async () => {
        await DataService.getCommentsForSession(state.sessionId).then(res => {
            setComments(res);
            setLoading(false);
        });
    }


    const handleClose = () => {
        setShowModal(false);
    }

    const handleOnCommentAdd = (type: CommentType) => {
        setCurrentType(type);
        setShowModal(true);
    }

    const handleSubmit = async (comment: string) => {
        const reqParams = {
            "sessionId": state.sessionId,
            "commentText": comment,
            "addedBy": currentState.name,
            "commentType": CommentType[currentType.toString() as keyof typeof CommentType]
        }
        await DataService.addComment(reqParams).then(res => {
            setShowModal(false);
            setComments([...comments, res])
        });
    }

    const handleCommentDelete = async (id: number) => {
        await DataService.deleteComment(id).then(res => {
            if (res) {
                removeComment(id);
            }
        });
    }

    const removeComment = (id: number) => {
        var items = [...comments];
        var index = items.findIndex(c => c.id === id);
        if (index !== -1) {
            items.splice(index, 1);
            setComments(items);
        }
    }

    return (
        <div>
            {isLoading ? <LinearProgress /> :
                < Grid container spacing={0} className={classes.gridContainer}>
                    <Grid item md={3} lg={4} xs={12}>
                        <RetroCategory key="GOOD" title="What went good"
                            type={CommentType.GOOD} items={comments.filter(c => c.commentType.toString() === CommentType[CommentType.GOOD])}
                            onAdd={() => handleOnCommentAdd(CommentType.GOOD)} onCommentDelete={handleCommentDelete} />
                    </Grid>
                    <Grid item md={3} lg={4} xs={12}>
                        <RetroCategory key="BAD" title="What went wrong"
                            type={CommentType.BAD} items={comments.filter(c => c.commentType.toString() === CommentType[CommentType.BAD])}
                            onAdd={() => handleOnCommentAdd(CommentType.BAD)} onCommentDelete={handleCommentDelete} />
                    </Grid>
                    <Grid item md={3} lg={4} xs={12}>
                        <RetroCategory key="IMPROVE" title="What can be improved"
                            type={CommentType.IMPROVE} items={comments.filter(c => c.commentType.toString() === CommentType[CommentType.IMPROVE])}
                            onAdd={() => handleOnCommentAdd(CommentType.IMPROVE)} onCommentDelete={handleCommentDelete} />
                    </Grid>
                </Grid>}
            <RetroCommentModal show={isShowModal} comment="" onSubmit={handleSubmit} onClose={handleClose} isLoading={isLoading} />
        </div >
    );
}


export default withStyles(commentsStyles)(RetroComments);