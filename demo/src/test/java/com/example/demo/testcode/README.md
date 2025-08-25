# Spring Boot 테스트 - 생성자 주입 개선 예제

Spring Boot 테스트에서 의존성 주입을 개선하는 방법을 보여주는 프로젝트입니다.

## 개요

여러 `@Autowired` 어노테이션 사용 방식에서 record 기반 생성자 주입으로 개선하는 과정을 설명합니다.

## 변경사항

### 1. JUnit Platform 설정 추가
- **파일**: `src/test/resources/junit-platform.properties`
- **내용**: `spring.test.constructor.autowire.mode=all`
- **효과**: 생성자 파라미터 자동 주입 활성화

### 2. AS-IS: 기존 방식 (ServiceTestAsIs.java)

```java
@SpringBootTest
public class ServiceTestAsIs {
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NotificationService notificationService;
}
```

**문제점:**
- 각 의존성마다 `@Autowired` 어노테이션 필요
- 필드 주입으로 인한 가변성
- 보일러플레이트 코드 증가

### 3. TO-BE: 개선된 방식 (ServiceTestToBe.java)

```java
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
record ServiceTestToBe(
    UserService userService,
    EmailService emailService,
    NotificationService notificationService
) {
    // 테스트 메서드들...
}
```

**개선효과:**
- `@Autowired` 어노테이션 제거
- record를 통한 불변성 보장
- 생성자 주입의 장점 활용
- 컴파일 타임 안전성 제공
- 코드 간소화

## 기술 스택

- **Java 17+** (record 지원)
- **Spring Boot 3.x**
- **JUnit 5**
- **H2 Database** (인메모리 테스트)

## 참고 문서

- [Spring TestConstructor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/TestConstructor.html)
- [JUnit 5 User Guide](https://docs.junit.org/current/user-guide/)

## 실행 방법

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests ServiceTestAsIs
./gradlew test --tests ServiceTestToBe
```

## 핵심 포인트

1. **JUnit Platform 설정**: `junit-platform.properties`에서 생성자 자동 주입 활성화
2. **Record 활용**: Java 14+ record로 불변 테스트 클래스 구조 구성
3. **생성자 주입**: 생성자 주입으로 테스트 안정성 향상
4. **코드 간소화**: 반복적인 `@Autowired` 제거로 가독성 개선

## 테스트 커버리지

각 테스트 클래스는 다음을 검증합니다:
- 개별 서비스 기능 테스트
- 서비스 통합 시나리오 테스트
- 의존성 주입 검증

## 작업 히스토리

### 커밋 순서별 작업 내용

1. [**H2 인메모리 데이터베이스 설정**](https://github.com/junwoochoi-kurly/example-code/commit/3c7df5cf3ee0637ce600ea0a312af73a159bb320)
   - H2 의존성 추가 및 인메모리 데이터베이스 설정
   - 테스트 환경을 위한 기본 데이터베이스 구성
2. [**테스트용 서비스 클래스 생성**](https://github.com/junwoochoi-kurly/example-code/commit/02024f7767d2e63af46cb0e7278058f16a71d50b)
   - `UserService`: 사용자 조회 및 생성 기능
   - `EmailService`: 이메일 유효성 검증 및 발송 기능  
   - `NotificationService`: 알림 발송 기능
   - 의존성 주입 예제를 위한 서비스 레이어 구성
3. [**JUnit Platform 설정 추가**](https://github.com/junwoochoi-kurly/example-code/commit/023b61ec4332c44b57708bf104c74a930a3e5ff7)
   - `junit-platform.properties` 파일 생성
   - `spring.test.constructor.autowire.mode=all` 설정
   - 생성자 파라미터 자동 주입 활성화
4. [**AS-IS 테스트 예제 구현**](https://github.com/junwoochoi-kurly/example-code/commit/62e420bebede91e296189e2965cb51ce196a5802) 
   - `ServiceTestAsIs.java` 생성
   - 기존 `@Autowired` 어노테이션 방식 구현
   - 필드 주입을 통한 전통적인 테스트 작성법 시연
5. [**TO-BE 테스트 예제 구현**](https://github.com/junwoochoi-kurly/example-code/commit/bcaaee502bf0d47c0a71d9dc2ac5537de92a7ca4)
   - `ServiceTestToBe.java` 생성
   - record 기반 생성자 주입 방식 구현
   - `@TestConstructor` 어노테이션 활용 / JUnit 설정 활용
   - 개선된 테스트 작성법 시연 및 참고 문서 링크 포함

### 주요 개선 효과

- **코드 간소화**: `@Autowired` 어노테이션 3개 → 0개
- **불변성 보장**: 필드 주입 → 생성자 주입 (record 활용)
- **타입 안전성**: 컴파일 타임 의존성 검증
- **유지보수성**: 보일러플레이트 코드 제거

---

이 예제를 통해 Spring Boot 테스트에서 더 깔끔하고 안전한 의존성 주입 방법을 학습하세요.