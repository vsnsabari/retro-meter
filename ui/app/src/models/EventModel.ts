import { CommentModel } from "./CommentModel";

export class EventModel {

    constructor(public type: "ADDED" | "EDITED" | "REMOVED", public comment: CommentModel) {

    }
}