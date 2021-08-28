import { CommentType } from "./CommentType";

export class CommentModel {
    public id: number = 0;
    public commentText: string = "";
    public sessionId: string = "";
    public commentType: CommentType = CommentType.NONE;
    public likes: number = 0;
    public actionItem: boolean = false;
    public createdDate: Date = new Date();
}