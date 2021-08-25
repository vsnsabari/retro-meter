package com.vsnsabari.retrometer.services;

import java.time.LocalDate;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vsnsabari.retrometer.entities.Session;
import com.vsnsabari.retrometer.exceptions.SessionCreationException;
import com.vsnsabari.retrometer.exceptions.SessionNotFoundException;
import com.vsnsabari.retrometer.repositories.SessionRepository;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {

        this.sessionRepository = sessionRepository;
    }

    public Session createSession(Session session) {
        try {
            return sessionRepository.save(session);
        } catch (Exception ex) {
            throw new SessionCreationException(ex);
        }
    }

    public Session[] getAllSession() {
        return StreamSupport.stream(sessionRepository.findAll().spliterator(), false)
                .toArray(Session[]::new);
    }

    public Session getBySessionId(String sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(()-> new SessionNotFoundException(sessionId));
    }

    public Session[] getAllSessionByCreatedDate(LocalDate date) {
        var from = date.atTime(0, 0, 0);
        var to = date.atTime(23, 59, 59);
        return StreamSupport.stream(sessionRepository.getByCreatedDateBetween(from, to).spliterator(), false)
                .toArray(Session[]::new);
    }
}
