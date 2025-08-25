package com.example.demo.testcode;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    public void sendNotification(String message) {
        System.out.println("Sending notification: " + message);
    }
    
    public void scheduleNotification(String message, int delaySeconds) {
        System.out.println("Scheduled notification: " + message + " in " + delaySeconds + " seconds");
    }
}