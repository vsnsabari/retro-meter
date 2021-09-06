import { Badge, Box, Card, CardActionArea, CardActions, CardContent, Collapse, IconButton, Tooltip, Typography, withStyles, WithStyles } from "@material-ui/core";
import {
    Edit as EditIcon,
    Delete as DeleteIcon, FavoriteBorder as LikeIcon, Favorite as LikedIcon,
    Bookmark as ActionItemIcon, BookmarkBorder as NonActionItemIcon, ExpandMore as ExpandMoreIcon,
} from '@material-ui/icons'
import clsx from "clsx";
import { Fragment, useState } from "react";
import { CommentModel } from "../../models/CommentModel";
import RetroCommentModal from "./RetroComment.modal";
import { commentStyles } from "./RetroComment.styles";

interface Props extends WithStyles<typeof commentStyles> {
    item: CommentModel;
    onDelete: any;
    onLike: any;
    onActionItem: any;
    onCommentEdit: any;
}

const RetroComment: React.FC<Props> = ({ item, classes, onDelete, onLike, onActionItem, onCommentEdit }) => {

    const [isShowModal, setShowModal] = useState(false);
    const [isLoading, setLoading] = useState(false);
    const [isLiked, setLiked] = useState(false);
    const [expanded, setExpanded] = useState(false);

    const handleLike = async () => {
        if (!isLiked) {
            onLike(item.id, true);
            setLiked(true);
        } else {
            onLike(item.id, false);
            setLiked(false);
        }
    }

    const handleActionItem = async () => {
        if (!item.actionItem) {
            onActionItem(item.id, true);
        } else {
            onActionItem(item.id, false);
        }
    }

    const handleEdit = async () => {
        setShowModal(true);
    }

    const handleDelete = async () => {
        onDelete(item.id);
    }

    const handleClose = () => {
        setShowModal(false);
    }

    const handleSubmit = async (comment: string) => {
        onCommentEdit(item.id, comment);
        setLoading(false);
        setShowModal(false);
    }

    const copyComment = async () => {
        await navigator.clipboard.writeText(item.commentText);
    }

    const handleExpandClick = () => {
        setExpanded(!expanded);
    };

    return (
        <Fragment >
            <Card className={classes.root} style={{ background: item.actionItem ? "lightGreen" : "" }}>
                <CardActionArea>
                    <CardContent>
                        <Box display="flex">
                            <Box flexGrow={1}>
                                <Tooltip title="click to copy" placement="bottom-end">
                                    <Typography className={classes.cardContent} variant="body2" color="textSecondary" component="p" onClick={copyComment}>
                                        {item.commentText}
                                    </Typography>
                                </Tooltip>
                            </Box>
                            <Box>
                                <Badge className={classes.badge} badgeContent={item.likes} color="secondary">

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
                            <Badge className={classes.badge} badgeContent={item.likes} color="secondary">
                                {isLiked ? <LikedIcon /> : <LikeIcon />}
                            </Badge>
                        </IconButton>
                        <IconButton onClick={handleActionItem}>
                            {item.actionItem ? <ActionItemIcon /> : <NonActionItemIcon />}
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
            <RetroCommentModal key="edit" show={isShowModal} comment={item.commentText} isNew={false} onSubmit={handleSubmit} onClose={handleClose} isLoading={isLoading} />
        </Fragment >
    );
};

export default withStyles(commentStyles)(RetroComment);