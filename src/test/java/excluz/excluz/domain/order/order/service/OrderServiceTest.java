package excluz.excluz.domain.order.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import excluz.excluz.common.entity.*;
import excluz.excluz.domain.cartItem.repository.CartItemRepository;
import excluz.excluz.domain.order.order.dto.request.OrderUpdateRequestDto;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.enums.OrderStatus;
import excluz.excluz.domain.order.order.repository.OrderRepository;
import excluz.excluz.domain.order.orderItem.repository.OrderItemRepository;
import excluz.excluz.domain.point.point.repository.PointRepository;
import excluz.excluz.domain.point.pointTransaction.repository.PointTransactionRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import excluz.excluz.domain.user.enums.UserRole;
import excluz.excluz.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserRepository userRepository;
    @Mock
    private StreamerRepository streamerRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PointRepository pointRepository;
    @Mock
    private PointTransactionRepository pointTransactionRepository;

    @InjectMocks
    private OrderService orderService;

    @Spy
    private Order order;  // 실제 객체를 Spy로 감싸기

    private OrderResponseDto orderResponseDto;

    private User user1;
    private Streamer streamer1;
    private Store store1;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {

        user1 = User.builder()
                .name("유저" + "1")
                .nickName("유저 닉네임" + "1")
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .address("유저 주소" + "1")
                .email("user" + "1" + "@example.com")
                .password("password")
                .build();
        userRepository.save(user1);
        ReflectionTestUtils.setField(user1, "id", 1);  // ID 강제 설정

        streamer1 = Streamer.builder()
                .name("스트리머" + "1")
                .nickName("스트리머" + "1")
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .email("streamer" + "1" + "@example.com")
                .password("password")
                .build();
        streamerRepository.save(streamer1);
        ReflectionTestUtils.setField(streamer1, "id", 1);  // ID 강제 설정

        store1 = Store.builder()
                .streamer(streamer1)
                .address("서울시 강남구 " + "1")
                .storeName("스토어" + "1")
                .registrationNumber("REG" + ((int) (Math.random() * 900000 + 100000)))
                .build();
        storeRepository.save(store1);
        ReflectionTestUtils.setField(store1, "id", 1);  // ID 강제 설정

        order1 = new Order(user1, OrderStatus.ORDERED, "address1");
        orderRepository.save(order1);
        ReflectionTestUtils.setField(order1, "id", 1);

        order2 = new Order(user1, OrderStatus.ORDERED, "address2");
        orderRepository.save(order2);
        ReflectionTestUtils.setField(order2, "id", 2);

        orderResponseDto = new OrderResponseDto();
    }


    @Test
    @DisplayName("성공 - CUSTOMER의 주문 목록 조회")
    void getOrderListAsCustomer() throws Exception {
        // Given
        Integer userId = 1;
        UserRole userRole = UserRole.CUSTOMER;
        Pageable pageable = PageRequest.of(0, 10);
        List<OrderResponseDto> orderDtoList = List.of(OrderResponseDto.from(order1), OrderResponseDto.from(order2)); // Mock 데이터

        Page<OrderResponseDto> orderPage = new PageImpl<>(orderDtoList, pageable, orderDtoList.size());

        Mockito.when(orderRepository.findByUserId(userId, pageable)).thenReturn(orderPage);

        // When
        Page<OrderResponseDto> result = orderService.getOrderList(userId, userRole, pageable);

        // Then
        assertEquals(2, result.getTotalElements()); // 주문 개수 검증
        verify(orderRepository, times(1)).findByUserId(userId, pageable);

        assertEquals("address1", result.getContent().get(0).getAddress());
        assertEquals("address2", result.getContent().get(1).getAddress());

        System.out.println("result: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(result));
    }

    @Test
    @DisplayName("성공 - STREAMER의 주문 목록 조회")
    void getOrderList_Streamer() throws Exception {
        // Given
        Integer streamerId = 1;
        UserRole userRole = UserRole.STREAMER;
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = List.of(order1, order2); // Mock 데이터

        List<OrderResponseDto> orderResponseList = orderList.stream()
                .map(OrderResponseDto::from)
                .toList();

        Page<OrderResponseDto> orderPage = new PageImpl<>(orderResponseList, pageable, orderResponseList.size());

        Mockito.when(orderRepository.findByStreamerId(streamerId, pageable)).thenReturn(orderPage);

        // When
        Page<OrderResponseDto> result = orderService.getOrderList(streamerId, userRole, pageable);

        // Then
        assertEquals(2, result.getTotalElements()); // 주문 개수 검증
        verify(orderRepository, times(1)).findByStreamerId(streamerId, pageable);

        System.out.println("result: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(result));
    }

    @Test
    @DisplayName("성공 - CUSTOMER의 주문 조회")
    void getOrderAsCustomer() throws Exception {
        // Given
        Integer userOrStreamerId = 1;
        UserRole userRole = UserRole.CUSTOMER;
        Integer orderId = 1;

        // Mock: 주문 조회
        Mockito.when(orderRepository.findByIdAndUserId(orderId, userOrStreamerId))
                .thenReturn(Optional.of(order1));

        // When
        OrderResponseDto result = orderService.getOrder(userOrStreamerId, userRole, orderId);

        // Then
        assertNotNull(result);
        assertEquals(order1.getId(), result.getOrderId());
        assertEquals(order1.getOrderStatus(), result.getOrderStatus());
        assertEquals(order1.getAddress(), result.getAddress());
        verify(orderRepository, times(1)).findByIdAndUserId(orderId, userOrStreamerId);

        System.out.println("result: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(result));
    }

    @Test
    @DisplayName("성공 - STREAMER의 주문 조회")
    void getOrderAsStreamer() throws Exception {
        // Given
        Integer userOrStreamerId = 1;
        UserRole userRole = UserRole.STREAMER;
        Integer orderId = 1;

        // Mock: 주문 조회
        Mockito.when(orderRepository.findByIdAndStreamerId(orderId, userOrStreamerId))
                .thenReturn(Optional.of(order1));

        // When
        OrderResponseDto result = orderService.getOrder(userOrStreamerId, userRole, orderId);

        // Then
        assertNotNull(result);
        assertEquals(order1.getId(), result.getOrderId());
        assertEquals(order1.getOrderStatus(), result.getOrderStatus());
        assertEquals(order1.getAddress(), result.getAddress());
        verify(orderRepository, times(1)).findByIdAndStreamerId(orderId, userOrStreamerId);

        System.out.println("result: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(result));
    }

    @Test
    @DisplayName("실패 - 주문이 존재하지 않음 (CUSTOMER)")
    void getOrderNotFoundAsCustomer() {
        // Given
        Integer userOrStreamerId = 1;
        UserRole userRole = UserRole.CUSTOMER;
        Integer orderId = 1;

        // Mock: 주문이 존재하지 않음
        Mockito.when(orderRepository.findByIdAndUserId(orderId, userOrStreamerId))
                .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                orderService.getOrder(userOrStreamerId, userRole, orderId)
        );

        // Then: 예외가 발생했는지 검증
        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
        verify(orderRepository, times(1)).findByIdAndUserId(orderId, userOrStreamerId); // 이 메서드가 한 번 호출되었는지 확인
    }

    @Test
    @DisplayName("실패 - 주문이 존재하지 않음 (STREAMER)")
    void getOrderNotFoundAsStreamer() {
        // Given
        Integer userOrStreamerId = 1;
        UserRole userRole = UserRole.STREAMER;
        Integer orderId = 1;

        // Mock: 주문이 존재하지 않음
        Mockito.when(orderRepository.findByIdAndStreamerId(orderId, userOrStreamerId))
                .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                orderService.getOrder(userOrStreamerId, userRole, orderId)
        );
        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
        verify(orderRepository, times(1)).findByIdAndStreamerId(orderId, userOrStreamerId);
    }

    @Test
    @DisplayName("성공 - CUSTOMER가 주문 상태를 업데이트")
    void updateOrderAsCustomer() throws Exception {
        // Given
        Integer userOrStreamerId = 1;
        UserRole userRole = UserRole.CUSTOMER;
        Integer orderId = 1;
        OrderUpdateRequestDto requestDto = new OrderUpdateRequestDto(OrderStatus.CANCELED, "address11");

        // Mock 객체 생성
        PointTransaction pointTransaction = Mockito.mock(PointTransaction.class);
        Store store = Mockito.mock(Store.class);
        Streamer streamer = Mockito.mock(Streamer.class);
        User user = Mockito.mock(User.class);

        // Store 객체 설정
        Mockito.when(pointTransaction.getStore()).thenReturn(store);
        Mockito.when(store.getStreamer()).thenReturn(streamer);
        Mockito.when(streamer.getUserRole()).thenReturn(UserRole.STREAMER);
        Mockito.when(streamer.getId()).thenReturn(2); // 스트리머 ID

        // User 객체 설정
        Mockito.when(pointTransaction.getUser()).thenReturn(user);
        Mockito.when(user.getUserRole()).thenReturn(UserRole.CUSTOMER);
        Mockito.when(user.getId()).thenReturn(userOrStreamerId); // 고객 ID

        // 환불할 금액 설정 (0보다 커야 함)
        Mockito.when(pointTransaction.getAmount()).thenReturn(1000); // 1000원으로 설정

        // 포인트 설정
        Point userPoint = new Point(UserRole.CUSTOMER, userOrStreamerId, 100000);
        ReflectionTestUtils.setField(userPoint, "id", 1);

        Point streamerPoint = new Point(UserRole.STREAMER, 2, 5000);
        ReflectionTestUtils.setField(streamerPoint, "id", 2);

        // Mock Repository 설정
        Mockito.when(orderRepository.findByIdAndUserId(orderId, userOrStreamerId)).thenReturn(Optional.of(order1));
        Mockito.when(pointTransactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(pointTransaction));

        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(
                UserRole.STREAMER, 2
        )).thenReturn(Optional.of(streamerPoint));

        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(
                UserRole.CUSTOMER, userOrStreamerId
        )).thenReturn(Optional.of(userPoint));

        // When
        orderService.updateOrder(userOrStreamerId, userRole, orderId, requestDto);

        // Then
        assertEquals(OrderStatus.CANCELED, order1.getOrderStatus());

        ArgumentCaptor<Order> captorOrder = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(3)).save(captorOrder.capture());
        List<Order> captorOrderList = captorOrder.getAllValues();

        System.out.println("captorOrder: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(captorOrderList.get(2)));
    }


    @Test
    @DisplayName("성공 - STREAMER가 주문 상태를 업데이트")
    void updateOrderAsStreamer() throws Exception {
        // Given
        Integer userOrStreamerId = 1;  // 스트리머 ID
        UserRole userRole = UserRole.STREAMER;
        Integer orderId = 1;
        OrderUpdateRequestDto requestDto = new OrderUpdateRequestDto(OrderStatus.CANCELED, "address11");

        // Mock 객체 생성
        PointTransaction pointTransaction = Mockito.mock(PointTransaction.class);
        Store store = Mockito.mock(Store.class);
        Streamer streamer = Mockito.mock(Streamer.class);
        User user = Mockito.mock(User.class);

        // Store 객체 설정
        Mockito.when(pointTransaction.getStore()).thenReturn(store);
        Mockito.when(store.getStreamer()).thenReturn(streamer);
        Mockito.when(streamer.getUserRole()).thenReturn(UserRole.STREAMER);
        Mockito.when(streamer.getId()).thenReturn(userOrStreamerId); // 스트리머 ID

        // User 객체 설정
        Mockito.when(pointTransaction.getUser()).thenReturn(user);
        Mockito.when(user.getUserRole()).thenReturn(UserRole.CUSTOMER);
        Mockito.when(user.getId()).thenReturn(2); // 고객 ID

        // 환불할 금액 설정 (0보다 커야 함)
        Mockito.when(pointTransaction.getAmount()).thenReturn(2000); // 2000원으로 설정

        // 포인트 설정
        Point userPoint = new Point(UserRole.CUSTOMER, 2, 50000);
        ReflectionTestUtils.setField(userPoint, "id", 2);

        Point streamerPoint = new Point(UserRole.STREAMER, userOrStreamerId, 10000);
        ReflectionTestUtils.setField(streamerPoint, "id", userOrStreamerId);

        // Mock Repository 설정
        Mockito.when(orderRepository.findByIdAndStreamerId(orderId, userOrStreamerId)).thenReturn(Optional.of(order1));
        Mockito.when(pointTransactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(pointTransaction));

        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(
                UserRole.STREAMER, userOrStreamerId
        )).thenReturn(Optional.of(streamerPoint));

        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(
                UserRole.CUSTOMER, 2
        )).thenReturn(Optional.of(userPoint));

        // When
        orderService.updateOrder(userOrStreamerId, userRole, orderId, requestDto);

        // Then
        assertEquals(OrderStatus.CANCELED, order1.getOrderStatus());

        ArgumentCaptor<Order> captorOrder = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(3)).save(captorOrder.capture());
        List<Order> captorOrderList = captorOrder.getAllValues();

        System.out.println("captorOrder: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(captorOrderList.get(2)));
    }

}