package com.vsnsabari.retrometer.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.vsnsabari.retrometer.entities.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    Iterable<Comment> getBySessionId(UUID sessionId);
}
