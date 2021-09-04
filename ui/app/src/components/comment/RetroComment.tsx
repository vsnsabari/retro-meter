import { Badge, Box, Card, CardActionArea, CardActions, CardContent, Collapse, IconButton, Tooltip, Typography, withStyles, WithStyles } from "@material-ui/core";
import {
    Edit as EditIcon,
    Delete as DeleteIcon, FavoriteBorder as LikeIcon, Favorite as LikedIcon,
    Bookmark as ActionItemIcon, BookmarkBorder as NonActionItemIcon, ExpandMore as ExpandMoreIcon,
} from '@material-ui/icons'
import clsx from "clsx";
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
    const [expanded, setExpanded] = useState(false);

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

    const handleExpandClick = () => {
        setExpanded(!expanded);
    };

    return (
        <Fragment >
            <Card className={classes.root} style={{ background: currentItem.actionItem ? "lightGreen" : "" }}>
                <CardActionArea>
                    <CardContent>
                        <Box display="flex">
                            <Box flexGrow={1}>
                                <Tooltip title="click to copy" placement="bottom-end">
                                    <Typography className={classes.cardContent} variant="body2" color="textSecondary" component="p" onClick={copyComment}>
                                        {currentItem.commentText}
                                    </Typography>
                                </Tooltip>
                            </Box>
                            <Box>
                                <Badge className={classes.badge} badgeContent={currentItem.likes} color="secondary">

                                    <IconButton className={clsx(classes.expand, { [classes.expandOpen]: expanded, })} onClick={handleExpandClick} aria-expanded={expanded} aria-label="show actions">
                                        <ExpandMoreIcon />
                                    </IconButton>
                                </Badge>
                            </Box>
                        </Box>
                    </CardContent>
                </CardActionArea>
                <Collapse in={expanded} timeout="auto" unmountOnExit>
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
                </Collapse>
            </Card>
            <RetroCommentModal key="edit" show={isShowModal} comment={currentItem.commentText} isNew={false} onSubmit={handleSubmit} onClose={handleClose} isLoading={isLoading} />
        </Fragment >
    );
};

export default withStyles(commentStyles)(RetroComment);