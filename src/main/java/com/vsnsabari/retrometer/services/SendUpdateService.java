package com.vsnsabari.retrometer.services;

import com.vsnsabari.retrometer.models.EventDto;

public interface SendUpdateService {
    void sendCommentUpdate(String sessionId, EventDto eventDto);
}
