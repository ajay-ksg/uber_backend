package com.uber.uberapi.Services.Notification;

import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    public void notify(String phoneNumber, String message);


}
