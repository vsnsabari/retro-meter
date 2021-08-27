import axios, { AxiosInstance } from "axios";
import { CommentModel } from "../models/CommentModel";
import { CommentType } from "../models/CommentType";
import { SessionModel } from "../models/SessionModel";

export class DataService {

    private constructor() {

    }

    private static api: AxiosInstance;

    public static init(clientId: string) {
        console.log("Initializing DataService for client : " + clientId);
        DataService.api = axios.create({
            baseURL: 'https://retrometer.azurewebsites.net/',
            headers: { 'X-Client-Id': clientId }
        });
    }



    public static async createSession(reqParams: { startedBy: string, team: string }): Promise<SessionModel> {
        const response = await DataService.api.put(`session/create`, reqParams);
        return response.data as SessionModel;
    }

    public static async getSession(sessionId: string): Promise<SessionModel> {
        const response = await DataService.api.get(`session/getbyid/${sessionId}`);
        return response.data as SessionModel;
    }

    public static async getCommentsForSession(sessionId: string): Promise<CommentModel[]> {
        const response = await DataService.api.get(`comment/getbysession/${sessionId}`);
        return response.data as CommentModel[];
    }

    public static async addComment(reqParams: { sessionId: string, commentText: string, commentType: CommentType }): Promise<CommentModel> {
        const response = await DataService.api.put(`comment/add`, reqParams);
        return response.data as CommentModel;
    }

    public static async deleteComment(id: number): Promise<Boolean> {
        const response = await DataService.api.post(`comment/delete/${id}`);
        return response.status === 200;
    }

    public static async editComment(reqParams: { id: number, commentText: string }): Promise<CommentModel> {
        const response = await DataService.api.post(`comment/edit`, reqParams);
        return response.data as CommentModel;
    }

    public static async upVoteComment(commentId: number) {
        const response = await DataService.api.post(`comment/upvote/${commentId}`);
        return response.data as CommentModel;
    }

    public static async downVoteComment(commentId: number) {
        const response = await DataService.api.post(`comment/downvote/${commentId}`);
        return response.data as CommentModel;
    }
}