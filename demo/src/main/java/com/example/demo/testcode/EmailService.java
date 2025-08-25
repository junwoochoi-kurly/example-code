package com.example.demo.testcode;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    public void sendEmail(String to, String message) {
        System.out.println("Sending email to: " + to + ", message: " + message);
    }
    
    public boolean validateEmail(String email) {
        return email != null && email.contains("@");
    }
}