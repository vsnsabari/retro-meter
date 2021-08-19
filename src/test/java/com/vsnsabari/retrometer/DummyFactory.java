package com.vsnsabari.retrometer;

import java.time.LocalDate;
import java.util.UUID;

import com.vsnsabari.retrometer.entities.Comment;
import com.vsnsabari.retrometer.entities.Session;

public class DummyFactory {

    public static Session getTestSession(String name) {
        return Session.builder()
                .team("TEAM-1")
                .startedBy(name)
                .createdDate(LocalDate.now())
                .build();
    }

    public static Comment getTestComment(String name, UUID sessionId) {
        return Comment.builder()
                .commentText(name)
                .createdDate(LocalDate.now())
                .sessionId(sessionId)
                .downVotes(0)
                .upVotes(0)
                .build();
    }
}
