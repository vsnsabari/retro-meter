package com.vsnsabari.retrometer.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.vsnsabari.retrometer.models.Member;
import com.vsnsabari.retrometer.repositories.EmitterRepository;
import com.vsnsabari.retrometer.repositories.InMemoryEmitterRepositoryImpl;

@DataJpaTest
@Import({EmitterService.class, InMemoryEmitterRepositoryImpl.class})
public class EmitterServiceTest {

    @Autowired
    private EmitterService emitterService;

    @Autowired
    private EmitterRepository emitterRepository;

    @Test
    void createEmitter() {
        SseEmitter emitter = emitterService.createEmitter(new Member("Session1", "Client1"));
        assertNotNull(emitter);
    }

    @Test
    void getEmitter() {
        Member member = new Member("Session2", "Client2");
        emitterService.createEmitter(member);
        Optional<SseEmitter> sseEmitter = emitterService.getEmitter(member);
        assertNotNull(sseEmitter);
        assertTrue(sseEmitter.isPresent());
    }

    @Test
    void removeEmitter() {
        Member member = new Member("Session3", "Client3");
        SseEmitter emitter = emitterService.createEmitter(member);
        emitterService.removeEmitter(member);
        Optional<SseEmitter> sseEmitter = emitterService.getEmitter(member);
        assertFalse(sseEmitter.isPresent());
    }
}