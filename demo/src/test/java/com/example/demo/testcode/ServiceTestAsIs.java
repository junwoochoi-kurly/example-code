package com.example.demo.testcode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AS-IS: 기존 방식 - 여러 개의 @Autowired 어노테이션 사용
 * 각 의존성마다 @Autowired를 명시적으로 작성해야 함
 */
@SpringBootTest
public class ServiceTestAsIs {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Test
    void testUserService() {
        String result = userService.findUserById(1L);
        assertEquals("User 1", result);
        
        String createResult = userService.createUser("John");
        assertEquals("Created user: John", createResult);
    }
    
    @Test
    void testEmailService() {
        boolean valid = emailService.validateEmail("test@example.com");
        assertTrue(valid);
        
        boolean invalid = emailService.validateEmail("invalid-email");
        assertFalse(invalid);
    }
    
    @Test
    void testIntegration() {
        String user = userService.createUser("Jane");
        emailService.sendEmail("jane@example.com", "Welcome " + user);
        notificationService.sendNotification("User registration completed");
        
        assertNotNull(user);
    }
}