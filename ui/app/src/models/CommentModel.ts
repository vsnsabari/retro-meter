import { CommentType } from "./CommentType";

export class CommentModel {
    public id: number = 0;
    public commentText: string = "";
    public sessionId: string = "";
    public commentType: CommentType = CommentType.NONE;
    public upVotes: number = 0;
    public downVotes: number = 0;
    public createdDate: Date = new Date();
}