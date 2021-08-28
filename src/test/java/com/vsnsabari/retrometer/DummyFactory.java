package com.vsnsabari.retrometer;

import java.time.LocalDateTime;

import com.vsnsabari.retrometer.entities.Comment;
import com.vsnsabari.retrometer.entities.Session;
import com.vsnsabari.retrometer.models.CommentType;

public class DummyFactory {

    public static Session getTestSession(String name) {
        return Session.builder()
                .team("TEAM-1")
                .startedBy(name)
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static Comment getTestComment(String name, String sessionId) {
        return Comment.builder()
                .commentText(name)
                .createdDate(LocalDateTime.now())
                .sessionId(sessionId)
                .addedBy(name)
                .commentType(CommentType.GOOD)
                .likes(0)
                .isActionItem(false)
                .build();
    }
}
