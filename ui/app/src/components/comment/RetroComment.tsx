import { Badge, Card, CardActionArea, CardActions, CardContent, IconButton, Tooltip, Typography, withStyles, WithStyles } from "@material-ui/core";
import {
    Edit as EditIcon,
    Delete as DeleteIcon, FavoriteBorder as LikeIcon, Favorite as LikedIcon,
    Bookmark as ActionItemIcon, BookmarkBorder as NonActionItemIcon
} from '@material-ui/icons'
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
    const [isLiked, setLiked] = useState(false);
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

    const handleLike = async () => {
        if (!isLiked) {
            await DataService.like(currentItem.id).then((res: CommentModel) => {
                setCurentItem(res);
                setLiked(true);
            });
        } else {
            await DataService.unlike(currentItem.id).then((res: CommentModel) => {
                setCurentItem(res);
                setLiked(false);
            });
        }
    }

    const handleActionItem = async () => {
        if (!currentItem.actionItem) {
            await DataService.addAsActionItem(currentItem.id).then((res: CommentModel) => {
                setCurentItem(res);
            });
        } else {
            await DataService.removeActionItem(currentItem.id).then((res: CommentModel) => {
                setCurentItem(res);
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
        <Fragment >
            <Card className={classes.root} style={{ background: currentItem.actionItem ? "lightGreen" : "" }}>
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
                    <IconButton onClick={handleLike}>
                        <Badge className={classes.badge} badgeContent={currentItem.likes} color="secondary">
                            {isLiked ? <LikedIcon /> : <LikeIcon />}
                        </Badge>
                    </IconButton>
                    <IconButton onClick={handleActionItem}>
                        {currentItem.actionItem ? <ActionItemIcon /> : <NonActionItemIcon />}
                    </IconButton>
                    <IconButton onClick={handleEdit}>
                        <EditIcon />
                    </IconButton>
                    <IconButton onClick={handleDelete} >
                        <DeleteIcon />
                    </IconButton>
                </CardActions>
            </Card>
            <RetroCommentModal key="edit" show={isShowModal} comment={currentItem.commentText} isNew={false} onSubmit={handleSubmit} onClose={handleClose} isLoading={isLoading} />
        </Fragment >
    );
};

export default withStyles(commentStyles)(RetroComment);