import { Grid, LinearProgress, withStyles, WithStyles } from '@material-ui/core';
import { Client } from '@stomp/stompjs';
import React from 'react';
import { useState } from 'react';
import { useEffect } from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import { CommentModel } from '../../models/CommentModel';
import { CommentType } from '../../models/CommentType';
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
    const [isModalLoading, setModalLoading] = useState(false);
    const [isShowModal, setShowModal] = useState(false);
    const [currentType, setCurrentType] = useState(CommentType.NONE);
    const [isSubscribed, setSubscribed] = useState(false);
    let client = new Client();
    const { currentState, dispatch } = useCurrentContext();
    const [comment, setComment] = useState("");
    const history = useHistory();

    useEffect(() => {
        if (currentState.session.sessionId === "") {
            history.replace("/", null);
            history.push('/');
        }
    }, []);

    useEffect(() => {
        async function fetchComments() {
            await getComments();
        }

        fetchComments();

    }, [state]);

    useEffect(() => {
        if (currentState.session.sessionId !== "") {
            dispatch({ type: "SESSION", data: { ...currentState, session: state } });
        }
    }, [state]);

    useEffect(() => {
        if (!isSubscribed) {
            client.configure({
                brokerURL: 'wss://retrometer.azurewebsites.net/retro-updates',
                onConnect: () => {
                    console.log("connected");
                    client.subscribe(`/topic/comment/${state.sessionId}`, eventMsg => {
                        console.log(eventMsg);
                        const data = JSON.parse(eventMsg.body);
                        switch (data.type) {
                            case "ADDED":
                                console.log("ADDED");
                                setComments((cData) => [...cData, data.comment]);
                                break;
                            case "EDITED":
                                console.log("EDITED");
                                editComment(data.comment);
                                break;
                            case "REMOVED":
                                console.log("REMOVED");
                                removeComment(data.comment.id);
                                break;
                        }
                    });
                },
                debug: (str) => {
                    console.log(new Date(), str);
                }
            });
            client.activate();
            setSubscribed(true);
        }
        return () => {
            setSubscribed(false);
            client?.deactivate();
        };
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
        setModalLoading(true);
        const reqParams = {
            "sessionId": state.sessionId,
            "commentText": comment,
            "addedBy": currentState.name,
            "commentType": CommentType[currentType.toString() as keyof typeof CommentType]
        }
        await DataService.addComment(reqParams).then(res => {
            setModalLoading(false);
            setShowModal(false);
            setComments([...comments, res]);
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
        setComments((cData) => {
            const items = [...cData];
            const index = items.findIndex(c => c.id === id);
            if (index !== -1) {
                items.splice(index, 1);
            }
            return [...items];
        });
    }

    const handleLike = async (id: number, isLike: boolean) => {
        if (!isLike) {
            await DataService.unlike(id).then((res: CommentModel) => {
                editComment(res);
            });
        } else {
            await DataService.like(id).then((res: CommentModel) => {
                editComment(res);
            });
        }
    }

    const handleActionItem = async (id: number, isActionItem: boolean) => {
        if (!isActionItem) {
            await DataService.removeActionItem(id).then((res: CommentModel) => {
                editComment(res);
            });
        } else {
            await DataService.addAsActionItem(id).then((res: CommentModel) => {
                editComment(res);
            });
        }
    }

    const handleCommentEdit = async (id: number, comment: string) => {
        await DataService.editComment({ id: id, commentText: comment }).then(res => {
            editComment(res);
        });
    }

    const editComment = (comment: CommentModel) => {
        setComments((cData) => {
            const items = [...cData];
            const index = items.findIndex(c => c.id === comment.id);
            if (index !== -1) {
                items[index] = comment;
            }
            return [...items];
        });
    }

    return (
        <div>
            {isLoading ? <LinearProgress /> :
                < Grid container spacing={0} className={classes.gridContainer}>
                    <Grid item md={3} lg={4} xs={12}>
                        <RetroCategory key="GOOD" title="What went good"
                            type={CommentType.GOOD} items={comments.filter(c => c.commentType.toString() === CommentType[CommentType.GOOD])}
                            onAdd={() => handleOnCommentAdd(CommentType.GOOD)} onCommentDelete={handleCommentDelete}
                            onLike={handleLike} onActionItem={handleActionItem} onCommentEdit={handleCommentEdit} />
                    </Grid>
                    <Grid item md={3} lg={4} xs={12}>
                        <RetroCategory key="BAD" title="What went wrong"
                            type={CommentType.BAD} items={comments.filter(c => c.commentType.toString() === CommentType[CommentType.BAD])}
                            onAdd={() => handleOnCommentAdd(CommentType.BAD)} onCommentDelete={handleCommentDelete}
                            onLike={handleLike} onActionItem={handleActionItem} onCommentEdit={handleCommentEdit} />
                    </Grid>
                    <Grid item md={3} lg={4} xs={12}>
                        <RetroCategory key="IMPROVE" title="What can be improved"
                            type={CommentType.IMPROVE} items={comments.filter(c => c.commentType.toString() === CommentType[CommentType.IMPROVE])}
                            onAdd={() => handleOnCommentAdd(CommentType.IMPROVE)} onCommentDelete={handleCommentDelete}
                            onLike={handleLike} onActionItem={handleActionItem} onCommentEdit={handleCommentEdit} />
                    </Grid>
                </Grid>}
            <RetroCommentModal key="add" show={isShowModal} comment={comment} isNew={true} onSubmit={handleSubmit} onClose={handleClose} isLoading={isModalLoading} />
        </div >
    );
}


export default withStyles(commentsStyles)(RetroComments);