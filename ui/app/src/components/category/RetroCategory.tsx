import { Button, Container, Tooltip, withStyles, WithStyles } from "@material-ui/core";
import { Mood as SmileIcon, MoodBad as SadIcon, EmojiObjects as Bulb } from "@material-ui/icons";
import { CommentModel } from "../../models/CommentModel";
import { CommentType } from "../../models/CommentType";
import RetroComment from "../comment/RetroComment";
import { categoryStyles } from "./RetroCategory.styles";

interface Props extends WithStyles<typeof categoryStyles> {
    title: string;
    type: CommentType;
    items: CommentModel[];
    onAdd: any;
    onCommentDelete: any;
    onLike: any;
    onActionItem: any;
    onCommentEdit: any;
}

const RetroCategory: React.FC<Props> = ({ classes, title, type, items, onAdd, onCommentDelete, onLike, onActionItem, onCommentEdit }) => {

    const getMoodIcon = (type: CommentType) => {
        switch (type) {
            case CommentType.GOOD:
                return <SmileIcon />;
            case CommentType.BAD:
                return <SadIcon />;
            case CommentType.IMPROVE:
                return <Bulb />
        }
    }

    return (
        <Container className={classes.container}>
            <Tooltip title="click to add comments" placement="top-start">
                <Button
                    className={classes.button}
                    endIcon={getMoodIcon(type)}
                    onClick={onAdd}
                >
                    {title}
                </Button>
            </Tooltip>
            {items.sort((a, b) => a.likes > b.likes ? -1 : a.likes < b.likes ? 1 : 0).map((item: CommentModel) => {
                return (
                    <RetroComment item={item} onDelete={onCommentDelete} key={item.id} onLike={onLike} onActionItem={onActionItem} onCommentEdit={onCommentEdit} />
                );
            })}
        </Container>
    )
}


export default withStyles(categoryStyles)(RetroCategory);