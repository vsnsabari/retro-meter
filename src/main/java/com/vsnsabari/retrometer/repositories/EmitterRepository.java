package com.vsnsabari.retrometer.repositories;

import java.util.Optional;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.vsnsabari.retrometer.models.Member;

public interface EmitterRepository {
    void addOrReplaceEmitter(Member member, SseEmitter emitter);

    void remove(Member member);

    Optional<SseEmitter> get(Member member);
}
