package com.uber.uberapi.Services.Notification;

public class ConsoleNotificationService implements NotificationService {
    @Override
    public void notify(String phoneNumber, String message) {
        System.out.printf("Notified phone Number: %s with message : %s",phoneNumber,message);
    }
}
