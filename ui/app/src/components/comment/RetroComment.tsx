import { Badge, Card, CardActionArea, CardActions, CardContent, IconButton, Tooltip, Typography, withStyles, WithStyles } from "@material-ui/core";
import { ThumbUp as ThumbUpIcon, ThumbDown as ThumbDownIcon, Edit as EditIcon, Delete as DeleteIcon } from '@material-ui/icons'
import { useEffect } from "react";
import { Fragment, useState } from "react";
import { eventBus } from "../../events/EventBus";
import { CommentModel } from "../../models/CommentModel";
import { DataService } from "../../services/DataService";
import RetroCommentModal from "./RetroComment.modal";
import { commentStyles } from "./RetroComment.styles";

interface Props extends WithStyles<typeof commentStyles> {
    item: CommentModel;
    onDelete: any;
}

const RetroComment: React.FC<Props> = ({ item, classes, onDelete }) => {

    const [currentItem, setCurentItem] = useState(item);
    const [isShowModal, setShowModal] = useState(false);
    const [isLoading, setLoading] = useState(false);
    const [isVoted, setVoted] = useState(false);
    const [isListening, setListening] = useState(false);

    useEffect(() => {
        if (!isListening) {
            eventBus.on("EDITED", (data: CommentModel) => {
                if (currentItem.id === data.id) {
                    setCurentItem(data);
                    console.log(currentItem);
                }
            });
            setListening(true);
        }
        return () => {
            eventBus.remove("EDITED", null);
        }
    }, [isListening, currentItem]);

    const handleVoting = async (isUp: boolean) => {
        if (isUp) {
            await DataService.upVoteComment(currentItem.id).then((res: CommentModel) => {
                setCurentItem(res);
                setVoted(true);
            });
        } else {
            await DataService.downVoteComment(currentItem.id).then((res: CommentModel) => {
                setCurentItem(res);
                setVoted(true);
            });
        }
    }

    const handleEdit = async () => {
        setShowModal(true);
    }

    const handleDelete = async () => {
        onDelete(currentItem.id);
    }

    const handleClose = () => {
        setShowModal(false);
    }

    const handleSubmit = async (comment: string) => {
        await DataService.editComment({ id: currentItem.id, commentText: comment }).then(res => {
            setLoading(false);
            setShowModal(false);
            setCurentItem(res);
        });
    }

    const copyComment = async () => {
        await navigator.clipboard.writeText(currentItem.commentText);
    }

    return (
        <Fragment>
            <Card className={classes.root}>
                <CardActionArea>
                    <CardContent>
                        <Tooltip title="click to copy" placement="bottom-end">
                            <Typography className={classes.cardContent} variant="body2" color="textSecondary" component="p" onClick={copyComment}>
                                {currentItem.commentText}
                            </Typography>
                        </Tooltip>
                    </CardContent>
                </CardActionArea>
                <CardActions disableSpacing>
                    <IconButton onClick={() => handleVoting(true)} disabled={isVoted}>
                        <Badge className={classes.badge} badgeContent={currentItem.upVotes} color="secondary">
                            <ThumbUpIcon />
                        </Badge>
                    </IconButton>
                    <IconButton onClick={() => handleVoting(false)} disabled={isVoted}>
                        <Badge className={classes.badge} badgeContent={currentItem.downVotes} color="secondary">
                            <ThumbDownIcon />
                        </Badge>
                    </IconButton>
                    <IconButton onClick={handleEdit}>
                        <EditIcon />
                    </IconButton>
                    <IconButton onClick={handleDelete}>
                        <DeleteIcon />
                    </IconButton>
                </CardActions>
            </Card>
            <RetroCommentModal show={isShowModal} comment={currentItem.commentText} onSubmit={handleSubmit} onClose={handleClose} isLoading={isLoading} />
        </Fragment >
    );
};

export default withStyles(commentStyles)(RetroComment);