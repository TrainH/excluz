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
import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
        List<Order> orderList = List.of(order1, order2); // Mock 데이터

        List<OrderResponseDto> orderDtoList = orderList.stream()
                .map(OrderResponseDto::from)
                .toList();

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
        // verify(orderRepository, times(1)).findByIdAndStreamerId(orderId, userOrStreamerId);
    }

    @Test
    @DisplayName("성공 - CUSTOMER가 주문 상태를 배송중(SHIPPING)을 배송완료(DELIVERED)로 업데이트")
    void updateShippingToDeliverdAsCustomer() throws Exception {
        // Given
        Integer userOrStreamerId = 1;
        UserRole userRole = UserRole.CUSTOMER;
        Integer orderId = 1;

        order = new Order(user1, OrderStatus.SHIPPING, "address1");
        orderRepository.save(order);
        ReflectionTestUtils.setField(order, "id", 1);

        OrderUpdateRequestDto requestDto = new OrderUpdateRequestDto(OrderStatus.DELIVERED, "address11");


        Mockito.when(orderRepository.findByIdAndUserId(orderId, userOrStreamerId)).thenReturn(Optional.of(order));
        // String roleName = userRole.replace("ROLE_", "").toUpperCase();

        // When
        orderService.updateOrder(userOrStreamerId, userRole, orderId, requestDto);

        // Then
        assertEquals(OrderStatus.DELIVERED, order.getOrderStatus()); // 주문 상태가 변경되었는지 검증

        ArgumentCaptor<Order> captorOrder = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(4)).save(captorOrder.capture()); // 위에서 save로 저장하기 때문에 3개로 해줌
        List<Order> captorOrderList = captorOrder.getAllValues();


        System.out.println("captorOrder: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(captorOrderList.get(2))); // service 단에서 .save는 맨마지만 꺼임

    }

    @Test
    @DisplayName("성공 - CUSTOMER가 주문 상태를 주문된(ORDERED)을 준비중(PREPARING)로 업데이트")
    void updateOrderedToShippingAsStreamer() throws Exception {
        // Given
        Integer userOrStreamerId = 1;
        UserRole userRole = UserRole.STREAMER;
        Integer orderId = 1;

        order = new Order(user1, OrderStatus.ORDERED, "address1");
        orderRepository.save(order);
        ReflectionTestUtils.setField(order, "id", 1);

        OrderUpdateRequestDto requestDto = new OrderUpdateRequestDto(OrderStatus.PREPARING, "address11");

        Mockito.when(orderRepository.findByIdAndStreamerId(orderId, userOrStreamerId)).thenReturn(Optional.of(order));
        // String roleName = userRole.replace("ROLE_", "").toUpperCase();

        // When
        orderService.updateOrder(userOrStreamerId, userRole, orderId, requestDto);

        // Then
        assertEquals(OrderStatus.PREPARING, order.getOrderStatus()); // 주문 상태가 변경되었는지 검증

        ArgumentCaptor<Order> captorOrder = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(4)).save(captorOrder.capture()); // 위에서 save로 저장하기 때문에 3개로 해줌
        List<Order> captorOrderList = captorOrder.getAllValues();


        System.out.println("captorOrder: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(captorOrderList.get(2))); // service 단에서 .save는 맨마지만 꺼임

    }

    @Test
    @DisplayName("성공 - CUSTOMER가 주문 상태를 주문된(ORDERED)을 주문취소(CANCELED)로 업데이트")
    void updateOrderedToCanceledAsCustomer() throws Exception {
        // Given
        Integer userOrStreamerId = 1;
        UserRole userRole = UserRole.CUSTOMER;
        Integer orderId = 1;


        order = new Order(user1, OrderStatus.ORDERED, "address1");
        orderRepository.save(order);
        ReflectionTestUtils.setField(order, "id", 1);
        Mockito.when(orderRepository.findByIdAndUserId(orderId, userOrStreamerId)).thenReturn(Optional.of(order));


        PointTransaction pointTransaction1 = new PointTransaction(null, user1, null, TransactionType.CHARGE,100000);
        pointTransactionRepository.save(pointTransaction1);
        ReflectionTestUtils.setField(pointTransaction1, "id", 1);
        Mockito.when(pointTransactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(pointTransaction1));


        PointTransaction pointTransaction2 = new PointTransaction(order1, user1, store1, TransactionType.PURCHASE,10000);
        pointTransactionRepository.save(pointTransaction2);
        ReflectionTestUtils.setField(pointTransaction2, "id", 2);
        Mockito.when(pointTransactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(pointTransaction2));


        Point userPoint = new Point(UserRole.CUSTOMER, 1, 100000);
        pointRepository.save(userPoint);
        ReflectionTestUtils.setField(userPoint, "id", 1);  // ID 강제 설정

        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(
                pointTransaction2.getUser().getUserRole(),
                pointTransaction2.getUser().getId())
        ).thenReturn(Optional.of(userPoint));


        Point streamerPoint = new Point(UserRole.STREAMER, 1, 100000);
        pointRepository.save(streamerPoint);
        ReflectionTestUtils.setField(streamerPoint, "id", 2);  // ID 강제 설정

        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(
                        pointTransaction2.getStore().getStreamer().getUserRole(),
                        pointTransaction2.getStore().getStreamer().getId())
                ).thenReturn(Optional.of(streamerPoint));



        OrderUpdateRequestDto requestDto = new OrderUpdateRequestDto(OrderStatus.CANCELED, "address1");




        // String roleName = userRole.replace("ROLE_", "").toUpperCase();

        // When
        orderService.updateOrder(userOrStreamerId, userRole, orderId, requestDto);


        // Then
        assertEquals(OrderStatus.CANCELED, order.getOrderStatus()); // 주문 상태가 변경되었는지 검증

        ArgumentCaptor<Order> captorOrder = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(4)).save(captorOrder.capture());
        List<Order> captorOrderList = captorOrder.getAllValues();

        ArgumentCaptor<Point> captorPoint = ArgumentCaptor.forClass(Point.class);
        verify(pointRepository, times(2)).save(captorPoint.capture());
        List<Point> captorPointList = captorPoint.getAllValues();

        ArgumentCaptor<PointTransaction> captorPointTransaction = ArgumentCaptor.forClass(PointTransaction.class);
        verify(pointTransactionRepository, times(3)).save(captorPointTransaction.capture());
        List<PointTransaction> captorPointTransactionList = captorPointTransaction.getAllValues();


        // Order
        assertEquals(OrderStatus.CANCELED, captorOrderList.get(3).getOrderStatus());

        //PointTransaction
        assertEquals(TransactionType.REFUND, captorPointTransactionList.get(2).getTransactionType());
        assertEquals(10000, captorPointTransactionList.get(2).getAmount());


//        System.out.println("----------------------------------");
//        System.out.println("captorOrderList: " + objectMapper.writerWithDefaultPrettyPrinter()
//                .writeValueAsString(captorOrderList.get(2))); // service 단에서 .save는 맨마지만 꺼임
//        System.out.println("----------------------------------");
//        System.out.println("captorPointList: " + objectMapper.writerWithDefaultPrettyPrinter()
//                .writeValueAsString(captorPointList)); // service 단에서 .save는 맨마지만 꺼임
//
//        System.out.println("----------------------------------");
//        System.out.println("captorPointTransactionList: " + objectMapper.writerWithDefaultPrettyPrinter()
//                .writeValueAsString(captorPointTransactionList)); // service 단에서 .save는 맨마지만 꺼임

    }

    @Test
    @DisplayName("성공 - STREAMER가 주문 상태를 주문된(ORDERED)을 주문취소(CANCELED)로 업데이트")
    void updateOrderedToCanceledAsStreamer() throws Exception {
        // Given
        Integer userOrStreamerId = 1;
        UserRole userRole = UserRole.STREAMER;
        Integer orderId = 1;


        order = new Order(user1, OrderStatus.ORDERED, "address1");
        orderRepository.save(order);
        ReflectionTestUtils.setField(order, "id", 1);
        Mockito.when(orderRepository.findByIdAndStreamerId(orderId, userOrStreamerId)).thenReturn(Optional.of(order));


        PointTransaction pointTransaction1 = new PointTransaction(null, user1, null, TransactionType.CHARGE,100000);
        pointTransactionRepository.save(pointTransaction1);
        ReflectionTestUtils.setField(pointTransaction1, "id", 1);
        Mockito.when(pointTransactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(pointTransaction1));


        PointTransaction pointTransaction2 = new PointTransaction(order1, user1, store1, TransactionType.PURCHASE,10000);
        pointTransactionRepository.save(pointTransaction2);
        ReflectionTestUtils.setField(pointTransaction2, "id", 2);
        Mockito.when(pointTransactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(pointTransaction2));


        Point userPoint = new Point(UserRole.CUSTOMER, 1, 100000);
        pointRepository.save(userPoint);
        ReflectionTestUtils.setField(userPoint, "id", 1);  // ID 강제 설정

        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(
                pointTransaction2.getUser().getUserRole(),
                pointTransaction2.getUser().getId())
        ).thenReturn(Optional.of(userPoint));


        Point streamerPoint = new Point(UserRole.STREAMER, 1, 100000);
        pointRepository.save(streamerPoint);
        ReflectionTestUtils.setField(streamerPoint, "id", 2);  // ID 강제 설정

        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(
                pointTransaction2.getStore().getStreamer().getUserRole(),
                pointTransaction2.getStore().getStreamer().getId())
        ).thenReturn(Optional.of(streamerPoint));



        OrderUpdateRequestDto requestDto = new OrderUpdateRequestDto(OrderStatus.CANCELED, "address1");




        // String roleName = userRole.replace("ROLE_", "").toUpperCase();

        // When
        orderService.updateOrder(userOrStreamerId, userRole, orderId, requestDto);


        // Then
        assertEquals(OrderStatus.CANCELED, order.getOrderStatus()); // 주문 상태가 변경되었는지 검증

        ArgumentCaptor<Order> captorOrder = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(4)).save(captorOrder.capture());
        List<Order> captorOrderList = captorOrder.getAllValues();

        ArgumentCaptor<Point> captorPoint = ArgumentCaptor.forClass(Point.class);
        verify(pointRepository, times(2)).save(captorPoint.capture());
        List<Point> captorPointList = captorPoint.getAllValues();

        ArgumentCaptor<PointTransaction> captorPointTransaction = ArgumentCaptor.forClass(PointTransaction.class);
        verify(pointTransactionRepository, times(3)).save(captorPointTransaction.capture());
        List<PointTransaction> captorPointTransactionList = captorPointTransaction.getAllValues();


        // Order
        assertEquals(OrderStatus.CANCELED, captorOrderList.get(3).getOrderStatus());

        //PointTransaction
        assertEquals(TransactionType.REFUND, captorPointTransactionList.get(2).getTransactionType());
        assertEquals(10000, captorPointTransactionList.get(2).getAmount());


//        System.out.println("----------------------------------");
//        System.out.println("captorOrderList: " + objectMapper.writerWithDefaultPrettyPrinter()
//                .writeValueAsString(captorOrderList.get(2))); // service 단에서 .save는 맨마지만 꺼임
//        System.out.println("----------------------------------");
//        System.out.println("captorPointList: " + objectMapper.writerWithDefaultPrettyPrinter()
//                .writeValueAsString(captorPointList)); // service 단에서 .save는 맨마지만 꺼임
//
//        System.out.println("----------------------------------");
//        System.out.println("captorPointTransactionList: " + objectMapper.writerWithDefaultPrettyPrinter()
//                .writeValueAsString(captorPointTransactionList)); // service 단에서 .save는 맨마지만 꺼임

    }
}