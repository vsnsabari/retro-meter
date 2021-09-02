import {
    Button, CircularProgress, Dialog, DialogActions, DialogContent,
    DialogContentText, DialogTitle, TextareaAutosize, withStyles, WithStyles
} from "@material-ui/core";
import { BaseSyntheticEvent, useState } from "react";
import { commentStyles } from "./RetroComment.styles";

interface Props extends WithStyles<typeof commentStyles> {
    comment: string;
    show: boolean;
    onSubmit: any;
    onClose: any;
    isLoading: boolean;
    isNew: boolean;
}

const RetroCommentModal: React.FC<Props> = ({ classes, comment, show, onSubmit, onClose, isLoading, isNew }) => {
    const [currentComment, setCurrentComment] = useState(comment);

    const setTextValue = (event: BaseSyntheticEvent) => {
        setCurrentComment(event.target.value);
    }

    const handleSumbit = () => {
        onSubmit(currentComment);
        setCurrentComment(isNew ? "" : currentComment);
    }

    const handleClose = () => {
        onClose(false);
    }

    return (
        <Dialog open={show} onClose={handleClose} aria-labelledby="form-dialog-title" maxWidth={"sm"} fullWidth={true}>
            <DialogTitle id="form-dialog-title">Comment</DialogTitle>
            <DialogContent>
                <DialogContentText>
                    {"Add your valuable comments"}
                </DialogContentText>
                <TextareaAutosize
                    autoFocus
                    id="comments"
                    minRows={4}
                    minLength={5}
                    placeholder="add your comments here"
                    aria-label="maximum height"
                    value={currentComment}
                    onChange={(e) => setTextValue(e)}
                    className={classes.textArea}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} className={classes.smallButton} >
                    Cancel
                </Button>
                <div className={classes.wrapper}>
                    <Button onClick={handleSumbit} className={classes.smallButton} disabled={isLoading}>
                        Submit
                    </Button>
                    {isLoading && <CircularProgress size={24} className={classes.buttonProgress} />}
                </div>
            </DialogActions>
        </Dialog>
    );
}

export default withStyles(commentStyles)(RetroCommentModal);