import { createStyles, Theme } from "@material-ui/core";
import { green } from "@material-ui/core/colors";

export const commentStyles = (theme: Theme) =>
  createStyles({
    root: {
      display: "flex",
      flexFlow: "column",
      marginTop: 5,
    },
    badge: {
      padding: "0 4px",
    },
    textArea: {
      width: "100%",
    },
    smallButton: {
      background: "linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)",
      border: 0,
      borderRadius: 3,
      boxShadow: "0 3px 5px 2px rgba(255, 105, 135, .3)",
      color: "white",
    },
    wrapper: {
      margin: theme.spacing(1),
      position: "relative",
    },
    buttonProgress: {
      color: green[500],
      position: "absolute",
      top: "50%",
      left: "50%",
      marginTop: -12,
      marginLeft: -12,
    },
    cardContent: {
      wordWrap: "break-word",
    },
    expand: {
      transform: "rotate(0deg)",
      marginLeft: "auto",
      transition: theme.transitions.create("transform", {
        duration: theme.transitions.duration.shortest,
      }),
    },
    expandOpen: {
      transform: "rotate(180deg)",
    },
  });
