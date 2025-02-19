package excluz.excluz.domain.point.point.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import excluz.excluz.common.entity.*;
import excluz.excluz.domain.point.point.dto.response.PointResponseDto;
import excluz.excluz.domain.point.point.repository.PointRepository;
import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;
import excluz.excluz.domain.point.pointTransaction.repository.PointTransactionRepository;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import excluz.excluz.domain.user.enums.UserRole;
import excluz.excluz.domain.user.repository.UserRepository;
import excluz.excluz.domain.point.point.dto.request.PointChargeRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PointServiceTest  {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    PointService pointService;

    @Mock
    UserRepository userRepository;
    @Mock
    StreamerRepository streamerRepository;
    @Mock
    PointRepository pointRepository;
    @Mock
    PointTransactionRepository pointTransactionRepository;

    @BeforeEach
    public void setup() {
        // 각 테스트마다 중복데이터 방지를 위해 고유 접미사 사용
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);

        User user = User.builder()
                .name("유저" + uniqueSuffix)
                .nickName("유저 닉네임" + uniqueSuffix)
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .address("유저 주소" + uniqueSuffix)
                .email("user" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        // user = userRepository.save(user);

        Mockito.when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        // Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

        Streamer streamer = Streamer.builder()
                .name("스트리머" + uniqueSuffix)
                .nickName("스트리머" + uniqueSuffix)
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .email("streamer" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        // streamer = streamerRepository.save(streamer);

        Mockito.when(streamerRepository.findById(anyInt())).thenReturn(Optional.of(streamer));
        // Mockito.when(streamerRepository.save(any(Streamer.class))).thenReturn(streamer);
    }

    @Test
    @DisplayName("포인트 충전 성공 테스트")
    void chargePoint() throws Exception {
        // given: 테스트 데이터 준비
        Integer amount = 1000; // 충전금액
        Integer userOrStreamerId = 1;
        String userRole = UserRole.CUSTOMER.getRole();
        PointChargeRequestDto requestDto = new PointChargeRequestDto(amount);


        // when: 테스트 실행
        // 포인트 충전 서비스 실행
        pointService.chargePoint(userOrStreamerId, userRole, requestDto);


        // then: 검증
        // 1. 포인트가 올바르게 저장됐는지 검증
        ArgumentCaptor<Point> captorPoint = ArgumentCaptor.forClass(Point.class);
        verify(pointRepository, times(1)).save(captorPoint.capture());
        Point point = captorPoint.getValue();

        // 2. 거래 내역 저장이 올바르게 수행됐는지 검증
        ArgumentCaptor<PointTransaction> captorPointTransaction = ArgumentCaptor.forClass(PointTransaction.class);
        verify(pointTransactionRepository, times(1)).save(captorPointTransaction.capture());
        PointTransaction pointTransaction = captorPointTransaction.getValue();


        assertEquals(amount, point.getAmount(), "포인트 충전 후 금액이 올바르지 않습니다.");
        assertEquals(TransactionType.CHARGE, pointTransaction.getTransactionType(), "거래 타입이 올바르지 않습니다.");
        assertEquals(amount, pointTransaction.getAmount(), "충전 금액이 올바르지 않습니다.");


        // 로그 출력 (테스트에서 로깅은 일반적으로 필요하지 않지만 디버깅용으로 추가)
        System.out.println("point: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(point));
        System.out.println("pointTransaction: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(pointTransaction));
    }

    @Test
    @DisplayName("포인트 조회 성공 테스트")
    void getPoint() throws Exception {
        // given: 테스트 데이터 준비
        Integer userOrStreamerId = 1;
        String userRole = UserRole.CUSTOMER.getRole();

        Point point = new Point(UserRole.CUSTOMER, userOrStreamerId, 2000); // 포인트 1000

        // when: 테스트 실행
        when(pointRepository.findByUserRoleAndUserOrStreamerId(UserRole.CUSTOMER, userOrStreamerId))
                .thenReturn(Optional.of(point));

        PointResponseDto responseDto = pointService.getPoint(userOrStreamerId, userRole);

        // then: 검증
        assertEquals(point.getAmount(), responseDto.getAmount(), "조회된 포인트 금액이 올바르지 않습니다.");

        // 로그 출력 (디버깅용)
        System.out.println("pointResponseDto: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(responseDto));
    }
}