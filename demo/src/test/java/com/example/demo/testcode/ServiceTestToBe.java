package com.example.demo.testcode;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TO-BE: record 사용 방식 - junit-platform.properties 설정 활용
 * spring.test.constructor.autowire.mode=all 설정으로 인해
 * @Autowired 어노테이션 없이도 생성자 주입이 자동으로 동작
 * 
 * Reference: 
 * - Spring: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/TestConstructor.html
 * - JUnit: https://docs.junit.org/current/user-guide/#writing-tests-display-name-generator-default
 * 
 * record를 사용하여 더 간결하고 불변한 테스트 클래스 구성
 */
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
record ServiceTestToBe(
    UserService userService,
    EmailService emailService,
    NotificationService notificationService
) {
    
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
    
    @Test
    void testAllServicesAreInjected() {
        // record의 장점: 컴파일 타임에 null 체크 가능
        assertNotNull(userService, "UserService should be injected");
        assertNotNull(emailService, "EmailService should be injected");
        assertNotNull(notificationService, "NotificationService should be injected");
    }
}
