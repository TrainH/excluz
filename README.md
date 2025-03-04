# README

## 컨트롤러 세부 버전
- v1: DB
- v2: 1차 개선  
  - Query Dsl(주문 조회, 이벤트 조회)  
  - Cursor(아이템 조회)  

---

## 프로젝트 스펙

**1. 빌드/플러그인 환경**  
> - 빌드 툴: Gradle  
> - 플러그인  
>   • Java  
>   • Spring Boot (v3.4.2)  
>   • Spring Dependency Management (v1.1.7)  
> - Java 버전: 17  
> - 프로젝트 그룹 & 버전  
>   • group = 'excluz'  

---

**2. 주요 의존성 (Spring Boot Starter)**  
> - spring-boot-starter-data-jpa  
>   • Spring Data JPA 기반 데이터 접근  
> - spring-boot-starter-thymeleaf  
>   • Bean Validation 처리  
> - spring-boot-starter-web  
>   • 웹 애플리케이션(REST API 포함) 구동  

---

**3. 추가 라이브러리 및 기능**  
> - Lombok  
>   • 코드 축약(게터/세터/빌더 등)  
> - MySQL Connector  
>   • MySQL DB 연동  
> - Slack API Client (com.slack.api:slack-api-client:1.30.0)  
>   • Slack과의 연동/메시지 전송  
> - Bcrypt (at.favre.lib:bcrypt:0.10.2)  
>   • 비밀번호 해싱 및 보안  
> - 이메일 발송  
>   • spring-boot-starter-mail, ognl  
> - JSON Web Token (JJWT)  
>   • jjwt-api, jjwt-impl, jjwt-jackson (v0.12.6)  
>   • JWT 기반 인증/인가 처리  
> - Spring Security  
>   • 보안 및 인증/인가 체계  
> - QueryDSL (v5.0.0)  
>   • 타입 세이프한 동적 쿼리 작성 및 apt 설정  
> - Spring Retry / Spring Aspects  
>   • 재시도 로직 지원  

---

**4. 테스트 환경**  
> - spring-boot-starter-test  
>   • JUnit, Mockito 기반 테스트 지원  
> - junit-platform-launcher (testRuntimeOnly)  

---

**5. 빌드/코드 생성 설정**  
> - QueryDSL 코드를 build/generated/querydsl 폴더에 생성  
> - sourceSets 설정을 통해 생성된 폴더를 컴파일에 포함  
> - clean 태스크 시 build/generated/querydsl 폴더 삭제  

---

**6. 배포 **
> - 깃헙엑션을 통해 release/{버전}이 깃헙 리파지토리로 push
> - release/{버전} 업데이트 될 때 자동으로 AWS ec2 리눅스 서버 배포

---

## 참고
위 스펙을 통해 Spring Boot 애플리케이션을 개발할 수 있으며, 컨트롤러 버전별(DB, QueryDSL, Cursor 기반 조회 등)로 단계적으로 개선/확장할 수 있습니다.
