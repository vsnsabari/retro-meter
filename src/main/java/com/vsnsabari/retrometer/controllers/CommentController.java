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
            log.info("Received Request to create comment {} by client {}", comment, clientId);
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

    @GetMapping("getbysession/{sessionId}")
    public ResponseEntity<Object> getBySessionId(@PathVariable("sessionId") String sessionId) {
        log.info("Received Request to get comments by session {}", sessionId);
        return new ResponseEntity<>(commentService.getAllCommentBySessionId(sessionId), HttpStatus.OK);
    }

    @PostMapping("like/{commentId}")
    public ResponseEntity<Object> like(@RequestHeader(value = "X-Client-Id") String clientId,
                                            @PathVariable("commentId") int commentId) {
        try {
            log.info("Received Request to favourite comment {} by client {}", commentId, clientId);
            return new ResponseEntity<>(commentService.addRemoveLikes(commentId, clientId, true),
                    HttpStatus.OK);
        } catch (CommentNotFoundException | CommentEditException ex) {
            log.error("Error processing favourite request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("unlike/{commentId}")
    public ResponseEntity<Object> unlike(@RequestHeader(value = "X-Client-Id") String clientId,
                                            @PathVariable("commentId") int commentId) {
        try {
            log.info("Received Request to favourite comment {} by client {}", commentId, clientId);
            return new ResponseEntity<>(commentService.addRemoveLikes(commentId, clientId, false),
                    HttpStatus.OK);
        } catch (CommentNotFoundException | CommentEditException ex) {
            log.error("Error processing favourite request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("action/add/{commentId}")
    public ResponseEntity<Object> actionItem(@RequestHeader(value = "X-Client-Id") String clientId,
                                              @PathVariable("commentId") int commentId) {
        try {
            log.info("Received Request to mark as action comment {} by client {}", commentId, clientId);
            return new ResponseEntity<>(commentService.addRemoveActionItem(commentId, clientId, true),
                    HttpStatus.OK);
        } catch (CommentNotFoundException | CommentEditException ex) {
            log.error("Error processing mark as action request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("action/remove/{commentId}")
    public ResponseEntity<Object> nonActionItem(@RequestHeader(value = "X-Client-Id") String clientId,
                                         @PathVariable("commentId") int commentId) {
        try {
            log.info("Received Request to remove as action comment {} by client {}", commentId, clientId);
            return new ResponseEntity<>(commentService.addRemoveActionItem(commentId, clientId, false),
                    HttpStatus.OK);
        } catch (CommentNotFoundException | CommentEditException ex) {
            log.error("Error processing to remove as action request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
