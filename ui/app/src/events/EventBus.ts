import { CommentModel } from "../models/CommentModel";

export const eventBus = {
    on(event: any, callback: any) {
        console.log(event + "received");
        document.addEventListener(event, (e) => callback(e.detail));
    },
    dispatch(event: any, data: CommentModel) {
        document.dispatchEvent(new CustomEvent(event, { detail: data }));
    },
    remove(event: any, callback: any) {
        document.removeEventListener(event, (e) => callback);
    },
};