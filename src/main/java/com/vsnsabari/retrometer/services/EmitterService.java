package com.vsnsabari.retrometer.services;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.vsnsabari.retrometer.models.Member;
import com.vsnsabari.retrometer.repositories.EmitterRepository;

@Service
@Slf4j
public class EmitterService {

    private final long eventsTimeout;
    private final EmitterRepository repository;

    @Autowired
    private EmitterService(@Value("${events.connection.timeout:3600000}") long eventsTimeout,
                           EmitterRepository repository) {

        this.eventsTimeout = eventsTimeout;
        this.repository = repository;
    }

    public SseEmitter createEmitter(Member member) {
        SseEmitter emitter = new SseEmitter(eventsTimeout);
        emitter.onCompletion(() -> repository.remove(member));
        emitter.onTimeout(() -> repository.remove(member));
        emitter.onError(e -> {
            log.error("Create SseEmitter exception", e);
            repository.remove(member);
        });
        repository.addOrReplaceEmitter(member, emitter);
        return emitter;
    }

    public Optional<SseEmitter> getEmitter(Member member) {
        return repository.get(member);
    }

    public Optional<SseEmitter[]> getEmitters(Member member) {
        return repository.getBySessionExcludingCurrentClient(member.getSessionId(), member.getClientId());
    }

    public void removeEmitter(Member member) {
        repository.remove(member);
    }
}
