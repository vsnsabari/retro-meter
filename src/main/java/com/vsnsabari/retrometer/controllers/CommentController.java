package com.vsnsabari.retrometer.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsnsabari.retrometer.entities.Comment;
import com.vsnsabari.retrometer.exceptions.CommentEditException;
import com.vsnsabari.retrometer.exceptions.CommentNotFoundException;
import com.vsnsabari.retrometer.services.CommentService;

@RestController
@RequestMapping("comment")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {

        this.commentService = commentService;
    }

    @PutMapping("add")
    public ResponseEntity<Object> addComment(@RequestHeader(value = "X-Client-Id") String clientId,
                                             @RequestBody Comment comment) {
        try {
            log.info("Received Request to create comment {} by client {}", comment , clientId);
            return new ResponseEntity<>(commentService.addComment(comment, clientId), HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("Error processing request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("edit")
    public synchronized ResponseEntity<Object> editComment(@RequestHeader(value = "X-Client-Id") String clientId,
                                                           @RequestBody Comment comment) {
        try {
            log.info("Received Request to edit comment {} by client {}", comment, clientId);
            return new ResponseEntity<>(commentService.editComment(comment.getId(), clientId, comment.getCommentText()),
                    HttpStatus.OK);
        } catch (CommentNotFoundException | CommentEditException ex) {
            log.error("Error processing upvote request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("delete/{id}")
    public synchronized ResponseEntity<Object> deleteComment(@RequestHeader(value = "X-Client-Id") String clientId,
                                                             @PathVariable("id") long commentId) {
        try {
            log.info("Received Request to delete comment {} by client {}", commentId, clientId);
            commentService.deleteComment(commentId, clientId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CommentNotFoundException | CommentEditException ex) {
            log.error("Error processing upvote request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("upvote/{commentId}")
    public synchronized ResponseEntity<Object> addUpVote(@RequestHeader(value = "X-Client-Id") String clientId,
                                                         @PathVariable("commentId") int commentId) {
        try {
            log.info("Received Request to up vote comment {} by client {}", commentId, clientId);
            return new ResponseEntity<>(commentService.addUpVote(commentId, clientId), HttpStatus.OK);
        } catch (CommentNotFoundException | CommentEditException ex) {
            log.error("Error processing upvote request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("downvote/{commentId}")
    public synchronized ResponseEntity<Object> addDownVote(@RequestHeader(value = "X-Client-Id") String clientId,
                                                           @PathVariable("commentId") int commentId) {
        try {
            log.info("Received Request to down vote comment {} by client {}", commentId, clientId);
            return new ResponseEntity<>(commentService.addDownVote(commentId, clientId), HttpStatus.OK);
        } catch (CommentNotFoundException | CommentEditException ex) {
            log.error("Error processing upvote request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("getbysession/{sessionId}")
    public ResponseEntity<Object> getBySessionId(@PathVariable("sessionId") String sessionId) {
        log.info("Received Request to get comments by session {}", sessionId);
        return new ResponseEntity<>(commentService.getAllCommentBySessionId(sessionId), HttpStatus.OK);
    }
}
