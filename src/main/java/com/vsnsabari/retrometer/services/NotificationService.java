package com.vsnsabari.retrometer.services;

import com.vsnsabari.retrometer.models.EventDto;
import com.vsnsabari.retrometer.models.Member;

public interface NotificationService {
    void sendNotification(Member member, EventDto event);
}
