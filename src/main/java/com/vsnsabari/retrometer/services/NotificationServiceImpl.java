package com.vsnsabari.retrometer.services;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.vsnsabari.retrometer.models.EventDto;
import com.vsnsabari.retrometer.models.Member;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final EmitterService emitterService;
    private final ExecutorService nonBlockingService = Executors
            .newCachedThreadPool();

    @Autowired
    public NotificationServiceImpl(EmitterService emitterService) {
        this.emitterService = emitterService;
    }

    @Override
    public void sendNotification(Member member, EventDto event) {
        if (event == null) {
            log.debug("No server event to send to device.");
            return;
        }
        doSendNotification(member, event);
    }

    private void doSendNotification(Member member, EventDto event) {
        emitterService.getEmitters(member).ifPresentOrElse(sseEmitters -> {
            nonBlockingService.execute(() -> {
                try {
                    log.debug("Sending event: {} for member: {}", event, member);
                    for (var emitter : sseEmitters) {
                        emitter.send(event, MediaType.APPLICATION_JSON);
                    }
                } catch (IOException | IllegalStateException e) {
                    log.debug("Error while sending event: {} for member: {} - exception: {}", event, member, e);
                    emitterService.removeEmitter(member);
                }
            });
        }, () -> log.error("No emitter for member {}", member));
    }
}
