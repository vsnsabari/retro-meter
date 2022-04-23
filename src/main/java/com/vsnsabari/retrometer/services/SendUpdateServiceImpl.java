package com.vsnsabari.retrometer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.vsnsabari.retrometer.entities.Session;
import com.vsnsabari.retrometer.models.EventDto;

@Service
public class SendUpdateServiceImpl implements SendUpdateService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public SendUpdateServiceImpl(SimpMessagingTemplate simpMessagingTemplate) {

        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendCommentUpdate(String sessionId, EventDto eventDto) {
        simpMessagingTemplate.convertAndSend(String.format("/topic/comment/%s", sessionId), eventDto);
    }

    public void sendSessionUpdate(String sessionId, Session session) {
        simpMessagingTemplate.convertAndSend(String.format("/topic/session/%s", sessionId), session);
    }
}
