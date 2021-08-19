package com.vsnsabari.retrometer.repositories;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.vsnsabari.retrometer.entities.Session;

public interface SessionRepository extends CrudRepository<Session, UUID> {

    Iterable<Session> getByCreatedDateBetween(LocalDateTime from, LocalDateTime to);
}
