// package excluz.excluz;

// import excluz.excluz.common.entity.Event;
// import excluz.excluz.common.entity.EventApplicant;
// import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto;
// import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
// import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
// import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
// import excluz.excluz.domain.event.eventApplicant.service.EventApplicantService;
// import excluz.excluz.domain.event.event.repository.EventRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.Optional;

// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;



// @ExtendWith(MockitoExtension.class)
// class EventApplicantServiceTest {

//     @InjectMocks
//     private EventApplicantService eventApplicantService;

//     @Mock
//     private EventApplicantRepository eventApplicantRepository;

//     @Mock
//     private EventRepository eventRepository;

//     private Event event;
//     private EventApplicant eventApplicant;

//     @BeforeEach
//     void setUp() {
//         // Event 객체 생성
//         event = Event.builder()
//                 .generatedCode("TEST_CODE")
//                 .build();
//         event.setId(1);

//         // EventApplicant 객체 생성
//         eventApplicant = EventApplicant.builder()
//                 .event(event)
//                 .applicantName("Test User")
//                 .email("test@example.com")
//                 .applicantPassword("password123")
//                 .applicantStatus(ApplicantStatus.WAITING)
//                 .deliveryAddress("Sample Address")
//                 .build();
//         eventApplicant.setId(1);
//     }

//     @Test
//     void applyForEvent_Success() {
//         // 준비
//         String code = "TEST_CODE";
//         EventApplicantRequestDto requestDto = EventApplicantRequestDto.builder()
//                 .applicantName("Test User")
//                 .email("test@example.com")
//                 .applicantPassword("password123")
//                 .deliveryAddress("Sample Address")
//                 .build();

//         when(eventRepository.findByGeneratedCode(code)).thenReturn(Optional.of(event));
//         when(eventApplicantRepository.save(any(EventApplicant.class))).thenReturn(eventApplicant);

//         // 실행
//         EventApplicantResponseDto responseDto = eventApplicantService.applyForEvent(code, requestDto);

//         // 검증
//         assertNotNull(responseDto);
//         assertEquals(eventApplicant.getId(), responseDto.getId());
//         assertEquals(eventApplicant.getEmail(), responseDto.getEmail());
//         assertEquals(eventApplicant.getApplicantName(), responseDto.getApplicantName());
//         assertEquals(eventApplicant.getApplicantStatus(), responseDto.getApplicantStatus());

//         // 호출 검증
//         verify(eventRepository, times(1)).findByGeneratedCode(code);
//         verify(eventApplicantRepository, times(1)).save(any(EventApplicant.class));
//     }

//     @Test
//     void applyForEvent_EventNotFound() {
//         // 준비
//         String code = "INVALID_CODE";
//         EventApplicantRequestDto requestDto = EventApplicantRequestDto.builder()
//                 .applicantName("Test User")
//                 .email("test@example.com")
//                 .applicantPassword("password123")
//                 .deliveryAddress("Sample Address")
//                 .build();

//         when(eventRepository.findByGeneratedCode(code)).thenReturn(Optional.empty());

//         // 실행 및 검증
//         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//             eventApplicantService.applyForEvent(code, requestDto);
//         });

//         assertEquals("잘못된 이벤트 코드입니다.", exception.getMessage());

//         // 호출 검증
//         verify(eventRepository, times(1)).findByGeneratedCode(code);
//         verify(eventApplicantRepository, never()).save(any(EventApplicant.class));
//     }

//     @Test
//     void getEventApplication_Success() {
//         // 준비
//         String code = "TEST_CODE";
//         String email = "test@example.com";
//         String password = "password123";

//         when(eventRepository.findByGeneratedCode(code)).thenReturn(Optional.of(event));
//         when(eventApplicantRepository.findByEventAndEmailAndApplicantPassword(event, email, password))
//                 .thenReturn(Optional.of(eventApplicant));

//         // 실행
//         EventApplicantResponseDto responseDto = eventApplicantService.getEventApplication(code, email, password);

//         // 검증
//         assertNotNull(responseDto);
//         assertEquals(eventApplicant.getId(), responseDto.getId());
//         assertEquals(eventApplicant.getEmail(), responseDto.getEmail());
//         assertEquals(eventApplicant.getApplicantName(), responseDto.getApplicantName());
//         assertEquals(eventApplicant.getApplicantStatus(), responseDto.getApplicantStatus());

//         // 호출 검증
//         verify(eventRepository, times(1)).findByGeneratedCode(code);
//         verify(eventApplicantRepository, times(1)).findByEventAndEmailAndApplicantPassword(event, email, password);
//     }

//     @Test
//     void getEventApplication_EventNotFound() {
//         // 준비
//         String code = "INVALID_CODE";
//         String email = "test@example.com";
//         String password = "password123";

//         when(eventRepository.findByGeneratedCode(code)).thenReturn(Optional.empty());

//         // 실행 및 검증
//         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//             eventApplicantService.getEventApplication(code, email, password);
//         });

//         assertEquals("잘못된 이벤트 코드입니다.", exception.getMessage());

//         // 호출 검증
//         verify(eventRepository, times(1)).findByGeneratedCode(code);
//         verify(eventApplicantRepository, never()).findByEventAndEmailAndApplicantPassword(any(Event.class), anyString(), anyString());
//     }

//     @Test
//     void getEventApplication_InvalidCredentials() {
//         // 준비
//         String code = "TEST_CODE";
//         String email = "wrong@example.com";
//         String password = "wrongpassword";

//         when(eventRepository.findByGeneratedCode(code)).thenReturn(Optional.of(event));
//         when(eventApplicantRepository.findByEventAndEmailAndApplicantPassword(event, email, password))
//                 .thenReturn(Optional.empty());

//         // 실행 및 검증
//         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//             eventApplicantService.getEventApplication(code, email, password);
//         });

//         assertEquals("응모 정보를 찾을 수 없거나 잘못된 인증 정보입니다.", exception.getMessage());

//         // 호출 검증
//         verify(eventRepository, times(1)).findByGeneratedCode(code);
//         verify(eventApplicantRepository, times(1)).findByEventAndEmailAndApplicantPassword(event, email, password);
//     }
// }
