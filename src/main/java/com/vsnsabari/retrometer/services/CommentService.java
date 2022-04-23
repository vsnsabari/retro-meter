package com.vsnsabari.retrometer.services;

import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.vsnsabari.retrometer.entities.Comment;
import com.vsnsabari.retrometer.exceptions.CommentCreationException;
import com.vsnsabari.retrometer.exceptions.CommentEditException;
import com.vsnsabari.retrometer.exceptions.CommentNotFoundException;
import com.vsnsabari.retrometer.models.EventDto;
import com.vsnsabari.retrometer.models.EventType;
import com.vsnsabari.retrometer.repositories.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final SendUpdateService sendUpdateService;

    public CommentService(CommentRepository commentRepository, SendUpdateService sendUpdateService) {

        this.commentRepository = commentRepository;
        this.sendUpdateService = sendUpdateService;
    }

    public Comment addComment(Comment comment, String clientId) {
        try {
            var commentAdded = commentRepository.save(comment);
            sendUpdateService.sendCommentUpdate(comment.getSessionId(), new EventDto(EventType.ADDED, commentAdded));
            return commentAdded;
        } catch (Exception ex) {
            throw new CommentCreationException(ex);
        }
    }

    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException(commentId));
    }

    public Comment[] getAllCommentBySessionId(String sessionId) {
        return StreamSupport.stream(commentRepository.getBySessionId(sessionId).spliterator(), false)
                .toArray(Comment[]::new);
    }

    public Comment editComment(Comment comment) {
        try {
            return commentRepository.save(comment);
        } catch (Exception ex) {
            throw new CommentEditException(ex);
        }
    }

    public Comment editComment(long commentId, String clientId, String comment) {
        try {
            var currentComment = getComment(commentId);
            currentComment.setCommentText(comment);
            var editedComment = commentRepository.save(currentComment);
            sendUpdateService.sendCommentUpdate(currentComment.getSessionId(),
                    new EventDto(EventType.EDITED, editedComment));
            return editedComment;
        } catch (Exception ex) {
            throw new CommentEditException(ex);
        }
    }

    public void deleteComment(long commentId, String clientId) {
        try {
            var comment = getComment(commentId);
            commentRepository.deleteById(commentId);
            sendUpdateService.sendCommentUpdate(comment.getSessionId(), new EventDto(EventType.REMOVED, comment));
        } catch (Exception ex) {
            throw new CommentNotFoundException(commentId);
        }
    }

    public synchronized Comment addRemoveLikes(long commentId, String clientId, boolean isAdd) {
        var comment = getComment(commentId);
        comment.setLikes(isAdd ? comment.getLikes() + 1 : comment.getLikes() - 1);
        var editedComment = editComment(comment);
        sendUpdateService.sendCommentUpdate(comment.getSessionId(), new EventDto(EventType.EDITED, editedComment));
        return editedComment;
    }

    public synchronized Comment addRemoveActionItem(long commentId, String clientId, boolean isAdd) {
        var comment = getComment(commentId);
        comment.setActionItem(isAdd);
        var editedComment = editComment(comment);
        sendUpdateService.sendCommentUpdate(comment.getSessionId(), new EventDto(EventType.EDITED, editedComment));
        return editedComment;
    }
}
