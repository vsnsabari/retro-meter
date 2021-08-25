package com.vsnsabari.retrometer.services;

import static com.vsnsabari.retrometer.DummyFactory.getTestSession;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.vsnsabari.retrometer.entities.Session;
import com.vsnsabari.retrometer.exceptions.SessionCreationException;
import com.vsnsabari.retrometer.repositories.SessionRepository;

@Transactional
@DataJpaTest
@ActiveProfiles("test")
@Import(SessionService.class)
public class SessionServiceTest {

    @Autowired
    private SessionRepository repository;

    @Autowired
    private SessionService service;

    @Test
    void createSession() {
        var testSession = getTestSession("createSession");
        Session session = service.createSession(testSession);
        assertNotNull(session);
        assertThat(session, samePropertyValuesAs(testSession));
    }

    @Test
    void getAllSession() {
        var testSession = getTestSession("getAllSession");
        service.createSession(testSession);
        assertThat(Arrays.stream(service.getAllSession()).collect(Collectors.toList()), hasSize(1));
        assertThat(Arrays.stream(service.getAllSession()).findFirst().get(), samePropertyValuesAs(testSession));
    }

    @Test
    void getAllSessionByCreatedDate() {
        var testSession = getTestSession("getAllSessionByCreatedDate");
        service.createSession(testSession);
        var sessions = service.getAllSessionByCreatedDate(LocalDate.now());
        assertThat(Arrays.stream(sessions).collect(Collectors.toList()),
                hasSize(1));
        assertThat(Arrays.stream(sessions).findFirst().get(), samePropertyValuesAs(testSession));
    }

    @Test
    void getSessionById() {
        var testSession = getTestSession("getSessionById");
        var createdSession = service.createSession(testSession);
        var session = service.getBySessionId(createdSession.getSessionId());
        assertNotNull(session);
        assertThat(session.getSessionId(), equalTo(createdSession.getSessionId()));
    }

    @Test
    void throwSessionCreationException() {
        assertThrows(SessionCreationException.class, () -> service.createSession(null));
    }

}