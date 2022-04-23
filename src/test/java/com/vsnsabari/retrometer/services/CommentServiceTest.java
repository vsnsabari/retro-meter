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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.vsnsabari.retrometer.DummyFactory;
import com.vsnsabari.retrometer.exceptions.CommentCreationException;
import com.vsnsabari.retrometer.exceptions.CommentEditException;
import com.vsnsabari.retrometer.exceptions.CommentNotFoundException;
import com.vsnsabari.retrometer.repositories.CommentRepository;

@Transactional
@DataJpaTest
@ActiveProfiles("test")
@Import({CommentService.class, SessionService.class, MockSendUpdateServiceImpl.class})
public class CommentServiceTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CommentService service;

    @Autowired
    private CommentRepository repository;

    @Autowired
    private SendUpdateService sendUpdateService;

    @Test
    void addComment() {
        var session = sessionService.createSession(DummyFactory.getTestSession("addComment"));
        var testComment = DummyFactory.getTestComment("addComment", session.getSessionId());
        var comment = service.addComment(testComment, "client1");
        assertNotNull(comment);
        MatcherAssert.assertThat(comment, samePropertyValuesAs(testComment));
    }

    @Test
    void getAllCommentBySessionId() {
        var session = sessionService.createSession(DummyFactory.getTestSession("getAllCommentBySessionId"));
        var testComment = DummyFactory.getTestComment("getAllCommentBySessionId", session.getSessionId());
        service.addComment(testComment, "client1");
        var comments = service.getAllCommentBySessionId(testComment.getSessionId());
        assertNotNull(comments);
        assertThat(Arrays.stream(comments).collect(Collectors.toList()), hasSize(1));
        MatcherAssert.assertThat(Arrays.stream(comments).findFirst().get(), samePropertyValuesAs(testComment));
    }

    @Test
    void editComment() {
        var session = sessionService.createSession(DummyFactory.getTestSession("editComment"));
        var testComment = DummyFactory.getTestComment("editComment", session.getSessionId());
        var comment = service.addComment(testComment, "client1");
        comment.setLikes(3);
        comment.setActionItem(true);
        comment = service.editComment(comment);
        assertNotNull(comment);
        MatcherAssert.assertThat(comment.getLikes(), equalTo(3));
        MatcherAssert.assertThat(comment.isActionItem(), equalTo(true));
    }

    @Test
    void deleteComment() {
        var session = sessionService.createSession(DummyFactory.getTestSession("editComment"));
        var testComment = DummyFactory.getTestComment("deleteComment", session.getSessionId());
        var comment = service.addComment(testComment, "client1");
        service.deleteComment(comment.getId(), "client1");
        assertThrows(CommentNotFoundException.class, () -> service.getComment(comment.getId()));
    }

    @Test
    void editCommentOnlyComment() {
        var session = sessionService.createSession(DummyFactory.getTestSession("editCommentOnlyComment"));
        var testComment = DummyFactory.getTestComment("editCommentOnlyComment", session.getSessionId());
        var comment = service.addComment(testComment, "client1");
        comment.setCommentText("new comment");
        comment = service.editComment(comment);
        assertNotNull(comment);
        MatcherAssert.assertThat(comment.getCommentText(), equalTo("new comment"));
    }

    @Test
    void like() {
        var session = sessionService.createSession(DummyFactory.getTestSession("like"));
        var testComment = DummyFactory.getTestComment("like", session.getSessionId());
        var comment = service.addComment(testComment, "client1");
        comment = service.addRemoveLikes(testComment.getId(), "client1", true);
        assertNotNull(comment);
        MatcherAssert.assertThat(comment.getLikes(), equalTo(1));
    }

    @Test
    void unlike() {
        var session = sessionService.createSession(DummyFactory.getTestSession("unlike"));
        var testComment = DummyFactory.getTestComment("unlike", session.getSessionId());
        testComment.setLikes(10);
        var comment = service.addComment(testComment, "client1");
        comment = service.addRemoveLikes(testComment.getId(), "client1", false);
        assertNotNull(comment);
        MatcherAssert.assertThat(comment.getLikes(), equalTo(9));
    }

    @Test
    void actionItem() {
        var session = sessionService.createSession(DummyFactory.getTestSession("actionItem"));
        var testComment = DummyFactory.getTestComment("actionItem", session.getSessionId());
        testComment.setLikes(10);
        var comment = service.addComment(testComment, "client1");
        comment = service.addRemoveActionItem(testComment.getId(), "client1", true);
        assertNotNull(comment);
        MatcherAssert.assertThat(comment.isActionItem(), equalTo(true));
    }

    @Test
    void nonActionItem() {
        var session = sessionService.createSession(DummyFactory.getTestSession("nonActionItem"));
        var testComment = DummyFactory.getTestComment("nonActionItem", session.getSessionId());
        testComment.setLikes(10);
        var comment = service.addComment(testComment, "client1");
        comment = service.addRemoveActionItem(testComment.getId(), "client1", false);
        assertNotNull(comment);
        MatcherAssert.assertThat(comment.isActionItem(), equalTo(false));
    }

    @Test
    void throwsCommentCreationException() {
        var session = sessionService.createSession(DummyFactory.getTestSession("CreationException"));
        var testComment = DummyFactory.getTestComment("CreationException", session.getSessionId());
        testComment.setSessionId(null);
        assertThrows(CommentCreationException.class, () -> service.addComment(testComment, "client1"));
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