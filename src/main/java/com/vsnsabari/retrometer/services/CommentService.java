package com.vsnsabari.retrometer.services;

import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.vsnsabari.retrometer.entities.Comment;
import com.vsnsabari.retrometer.exceptions.CommentCreationException;
import com.vsnsabari.retrometer.exceptions.CommentEditException;
import com.vsnsabari.retrometer.exceptions.CommentNotFoundException;
import com.vsnsabari.retrometer.repositories.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {

        this.commentRepository = commentRepository;
    }

    public Comment addComment(Comment comment) {
        try {
            return commentRepository.save(comment);
        } catch (Exception ex) {
            throw new CommentCreationException(ex);
        }
    }

    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException(String.format("No Comment found with id : %d", commentId)));
    }

    public Comment[] getAllCommentBySessionId(UUID sessionId) {
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

    public Comment addUpVote(long commentId) {
        var comment = getComment(commentId);
        comment.setUpVotes(comment.getUpVotes() + 1);
        return editComment(comment);
    }

    public Comment addDownVote(long commentId) {
        var comment = getComment(commentId);
        comment.setDownVotes(comment.getDownVotes() + 1);
        return editComment(comment);
    }
}
