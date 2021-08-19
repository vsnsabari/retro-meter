package com.vsnsabari.retrometer.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.vsnsabari.retrometer.DummyFactory;
import com.vsnsabari.retrometer.exceptions.CommentCreationException;
import com.vsnsabari.retrometer.exceptions.CommentEditException;
import com.vsnsabari.retrometer.exceptions.CommentNotFoundException;
import com.vsnsabari.retrometer.repositories.CommentRepository;

@Transactional
@DataJpaTest
@Import({CommentService.class, SessionService.class})
public class CommentServiceTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CommentService service;

    @Autowired
    private CommentRepository repository;

    @Test
    void addComment() {
        var session = sessionService.createSession(DummyFactory.getTestSession("addComment"));
        var testComment = DummyFactory.getTestComment("addComment", session.getSessionId());
        var comment = service.addComment(testComment);
        assertNotNull(comment);
        MatcherAssert.assertThat(comment, samePropertyValuesAs(testComment));
    }

    @Test
    void getAllCommentBySessionId() {
        var session = sessionService.createSession(DummyFactory.getTestSession("getAllCommentBySessionId"));
        var testComment = DummyFactory.getTestComment("getAllCommentBySessionId", session.getSessionId());
        service.addComment(testComment);
        var comments = service.getAllCommentBySessionId(testComment.getSessionId());
        assertNotNull(comments);
        assertThat(Arrays.stream(comments).collect(Collectors.toList()), hasSize(1));
        MatcherAssert.assertThat(Arrays.stream(comments).findFirst().get(), samePropertyValuesAs(testComment));
    }

    @Test
    void editComment() {
        var session = sessionService.createSession(DummyFactory.getTestSession("editComment"));
        var testComment = DummyFactory.getTestComment("editComment", session.getSessionId());
        var comment = service.addComment(testComment);
        comment.setUpVotes(15);
        comment.setDownVotes(17);
        comment = service.editComment(comment);
        assertNotNull(comment);
        MatcherAssert.assertThat(comment, Matchers.hasProperty("upVotes", equalTo(15)));
        MatcherAssert.assertThat(comment, Matchers.hasProperty("downVotes", equalTo(17)));
    }

    @Test
    void addUpVote() {
        var session = sessionService.createSession(DummyFactory.getTestSession("addUpVote"));
        var testComment = DummyFactory.getTestComment("addUpVote", session.getSessionId());
        var comment = service.addComment(testComment);
        comment = service.addUpVote(testComment.getId());
        assertNotNull(comment);
        MatcherAssert.assertThat(comment, Matchers.hasProperty("upVotes", equalTo(1)));
    }

    @Test
    void addDownVote() {
        var session = sessionService.createSession(DummyFactory.getTestSession("addDownVote"));
        var testComment = DummyFactory.getTestComment("addDownVote", session.getSessionId());
        var comment = service.addComment(testComment);
        comment = service.addDownVote(testComment.getId());
        assertNotNull(comment);
        MatcherAssert.assertThat(comment, Matchers.hasProperty("downVotes", equalTo(1)));
    }

    @Test
    void throwsCommentCreationException() {
        var session = sessionService.createSession(DummyFactory.getTestSession("CreationException"));
        var testComment = DummyFactory.getTestComment("CreationException", session.getSessionId());
        testComment.setSessionId(null);
        assertThrows(CommentCreationException.class, () -> service.addComment(testComment));
    }

    @Test
    void throwsCommentEditException() {
        var session = sessionService.createSession(DummyFactory.getTestSession("EditException"));
        var testComment = DummyFactory.getTestComment("EditException", session.getSessionId());
        testComment.setSessionId(null);
        assertThrows(CommentEditException.class, () -> service.editComment(testComment));
    }

    @Test
    void throwCommentNotFoundException() {
        assertThrows(CommentNotFoundException.class, () -> service.getComment(Long.MAX_VALUE));
    }
}