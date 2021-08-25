package com.vsnsabari.retrometer.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;

import com.vsnsabari.retrometer.models.EventDto;
import com.vsnsabari.retrometer.models.Member;

@Slf4j
@Profile("test")
public class MockNotificationServiceImpl implements NotificationService {
    @Override
    public void sendNotification(Member member, EventDto event) {
      log.info("this is for test and ignoring the sendNotification");
    }
}
