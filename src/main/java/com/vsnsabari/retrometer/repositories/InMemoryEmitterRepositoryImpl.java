package com.vsnsabari.retrometer.repositories;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.vsnsabari.retrometer.models.Member;

@Slf4j
@Repository
public class InMemoryEmitterRepositoryImpl implements EmitterRepository {

    private final Map<Member, SseEmitter> userEmitterMap = new ConcurrentHashMap<>();

    @Override
    public void addOrReplaceEmitter(Member member, SseEmitter emitter) {
        userEmitterMap.put(member, emitter);
    }

    @Override
    public void remove(Member member) {
        if (userEmitterMap.containsKey(member)) {
            log.debug("Removing emitter for member: {}", member);
            userEmitterMap.remove(member);
        } else {
            log.debug("No emitter to remove for member: {}", member);
        }
    }

    @Override
    public Optional<SseEmitter> get(Member member) {
        return Optional.ofNullable(userEmitterMap.get(member));
    }
}
