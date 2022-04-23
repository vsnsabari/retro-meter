package com.vsnsabari.retrometer.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;

import com.vsnsabari.retrometer.models.EventDto;

@Slf4j
@Profile("test")
public class MockSendUpdateServiceImpl implements SendUpdateService {
    @Override
    public void sendCommentUpdate(String sessionId, EventDto eventDto) {
        log.info("this is for test and ignoring the sendCommentUpdate");
    }
}
