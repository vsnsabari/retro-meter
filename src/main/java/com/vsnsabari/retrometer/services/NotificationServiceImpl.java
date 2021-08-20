package com.vsnsabari.retrometer.services;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vsnsabari.retrometer.models.EventDto;
import com.vsnsabari.retrometer.models.Member;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final EmitterService emitterService;

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
        emitterService.getEmitter(member).ifPresentOrElse(sseEmitter -> {
            try {
                log.debug("Sending event: {} for member: {}", event, member);
                sseEmitter.send(new EventDto("New Message"));
            } catch (IOException | IllegalStateException e) {
                log.debug("Error while sending event: {} for member: {} - exception: {}", event, member, e);
                emitterService.removeEmitter(member);
            }
        }, () -> log.error("No emitter for member {}", member));
    }
}
