package excluz.excluz.domain.order.orderItem.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import excluz.excluz.common.entity.*;
import excluz.excluz.domain.cartItem.repository.CartItemRepository;
import excluz.excluz.domain.order.order.enums.OrderStatus;
import excluz.excluz.domain.order.order.repository.OrderRepository;
import excluz.excluz.domain.order.orderItem.dto.request.OrderItemRequestDto;
import excluz.excluz.domain.order.orderItem.dto.response.OrderItemResponseDto;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
class OrderItemServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private OrderItemService orderItemService;

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


    @Test
    @DisplayName("성공 - 주문 생성")
    void createOrderItemList() throws Exception {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);

        User user = User.builder()
                .name("유저" + uniqueSuffix)
                .nickName("유저 닉네임" + uniqueSuffix)
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .address("유저 주소" + uniqueSuffix)
                .email("user" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        userRepository.save(user);
        ReflectionTestUtils.setField(user, "id", 1);  // ID 강제 설정

        Streamer streamer = Streamer.builder()
                .name("스트리머" + uniqueSuffix)
                .nickName("스트리머" + uniqueSuffix)
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .email("streamer" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        streamerRepository.save(streamer);
        ReflectionTestUtils.setField(streamer, "id", 1);  // ID 강제 설정

        Store store = Store.builder()
                .streamer(streamer)
                .address("서울시 강남구 " + uniqueSuffix)
                .storeName("스토어" + uniqueSuffix)
                .registrationNumber("REG" + ((int) (Math.random() * 900000 + 100000)))
                .build();
        storeRepository.save(store);
        ReflectionTestUtils.setField(store, "id", 1);  // ID 강제 설정

        Item item1 = Item.builder()
                .store(store)
                .itemName("상품" + uniqueSuffix)
                .explanation("이것은 상품 설명입니다.")
                .price(1000) // 1000~10999원 랜덤 가격
                .remainingQuantity((int) (Math.random() * 50 + 1)) // 1~50개 랜덤 수량
                .build();
        itemRepository.save(item1);
        ReflectionTestUtils.setField(item1, "id", 1);  // ID 강제 설정

        Item item2 = Item.builder()
                .store(store)
                .itemName("상품" + uniqueSuffix)
                .explanation("이것은 상품 설명입니다.")
                .price(1000) // 1000~10999원 랜덤 가격
                .remainingQuantity((int) (Math.random() * 50 + 1)) // 1~50개 랜덤 수량
                .build();
        itemRepository.save(item2);
        ReflectionTestUtils.setField(item2, "id", 2);  // ID 강제 설정


        CartItem cartItem1 = new CartItem(user, item1, 1);
        cartItemRepository.save(cartItem1);
        ReflectionTestUtils.setField(cartItem1, "id", 1);  // ID 강제 설정

        CartItem cartItem2 = new CartItem(user, item2, 1);
        cartItemRepository.save(cartItem2);
        ReflectionTestUtils.setField(cartItem2, "id", 2);  // ID 강제 설정


        // cartItemRepository: 유저의 장바구니 항목 반환
        List<CartItem> cartItemList = List.of(cartItem1, cartItem2); // cartItem 리스트 생성
        Mockito.when(cartItemRepository.findByUserId(1)).thenReturn(cartItemList); // userOrStreamerId에 해당하는 cartItem 리스트 반환



        Point userPoint = new Point(UserRole.CUSTOMER, 1, 5000);
        pointRepository.save(userPoint);
        ReflectionTestUtils.setField(userPoint, "id", 1);  // ID 강제 설정


        Point streamerPoint = new Point(UserRole.STREAMER, 1, 0);
        pointRepository.save(streamerPoint);
        ReflectionTestUtils.setField(streamerPoint, "id", 1);  // ID 강제 설정




         // Mockito.when 설정
         Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user)); // user 반환

        // pointRepository: CUSTOMER와 STREAMER에 대해 Point 조회
        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(UserRole.CUSTOMER, 1))
                .thenReturn(Optional.of(userPoint)); // userPoint 반환

        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(UserRole.STREAMER, 1))
                .thenReturn(Optional.of(streamerPoint)); // streamerPoint 반환

        // itemRepository: 아이템 목록 반환
        List<Item> itemList = List.of(item1, item2); // item 리스트 생성
        List<Integer> itemIdList = List.of(1, 2); // itemIdList 생성
        Mockito.when(itemRepository.findAllById(itemIdList)).thenReturn(itemList); // 아이템 리스트 반환

        // 만약 포인트가 없다면 기본값으로 Point 객체 생성
        Mockito.when(pointRepository.findByUserRoleAndUserOrStreamerId(
                        UserRole.CUSTOMER,1))
                .thenReturn(Optional.of(userPoint)); // 기본 포인트 객체 반환


        List<OrderItemRequestDto> requestList = List.of(new OrderItemRequestDto(1, 1, "Test Address"),
                new OrderItemRequestDto(2, 1, "Test Address"));


        orderItemService.createOrderItemList(1, UserRole.CUSTOMER, requestList);


        ArgumentCaptor<CartItem> captorCartItem = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository, times(2)).save(captorCartItem.capture());
        List<CartItem> captorCartItemList = captorCartItem.getAllValues();


        ArgumentCaptor<Order> captorOrder = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(captorOrder.capture());
        List<Order> captorOrderList = captorOrder.getAllValues();


        ArgumentCaptor<List<OrderItem>> captorOrderItem = ArgumentCaptor.forClass(List.class);
        verify(orderItemRepository, times(1)).saveAll(captorOrderItem.capture());
        List<OrderItem> captorOrderItemList = captorOrderItem.getValue();


        ArgumentCaptor<Point> captorPoint = ArgumentCaptor.forClass(Point.class);
        verify(pointRepository, times(3)).save(captorPoint.capture());
        List<Point> captorPointList = captorPoint.getAllValues();


        ArgumentCaptor<PointTransaction> captorPointTransaction = ArgumentCaptor.forClass(PointTransaction.class);
        verify(pointTransactionRepository, times(1)).save(captorPointTransaction.capture());
        List<PointTransaction> captorPointTransactionList = captorPointTransaction.getAllValues();



        assertEquals(3000, userPoint.getAmount()); // 5000 - (1000 * 2)
        assertEquals(2000, streamerPoint.getAmount());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(any());
        verify(pointTransactionRepository, times(1)).save(any(PointTransaction.class));


        // 로그 출력 (테스트에서 로깅은 일반적으로 필요하지 않지만 디버깅용으로 추가)
        System.out.println("captorCartItemList: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(captorCartItemList));

        System.out.println("captorOrderList: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(captorOrderList));

        System.out.println("captorOrderItemList: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(captorOrderItemList));

        System.out.println("captorPointList: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(captorPointList));

        System.out.println("captorPointTransactionList: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(captorPointTransactionList));

    }


    @Test
    @DisplayName("성공 - CUSTOMER 역할로 주문 아이템 조회")
    void getOrderItemForCustomer() throws Exception {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);

        User user = User.builder()
                .name("유저" + uniqueSuffix)
                .nickName("유저 닉네임" + uniqueSuffix)
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .address("유저 주소" + uniqueSuffix)
                .email("user" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        userRepository.save(user);
        ReflectionTestUtils.setField(user, "id", 1);  // ID 강제 설정

        Streamer streamer = Streamer.builder()
                .name("스트리머" + uniqueSuffix)
                .nickName("스트리머" + uniqueSuffix)
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .email("streamer" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        streamerRepository.save(streamer);
        ReflectionTestUtils.setField(streamer, "id", 1);  // ID 강제 설정

        Store store = Store.builder()
                .streamer(streamer)
                .address("서울시 강남구 " + uniqueSuffix)
                .storeName("스토어" + uniqueSuffix)
                .registrationNumber("REG" + ((int) (Math.random() * 900000 + 100000)))
                .build();
        storeRepository.save(store);
        ReflectionTestUtils.setField(store, "id", 1);  // ID 강제 설정

        Item item1 = Item.builder()
                .store(store)
                .itemName("상품" + uniqueSuffix)
                .explanation("이것은 상품 설명입니다.")
                .price(1000) // 1000~10999원 랜덤 가격
                .remainingQuantity((int) (Math.random() * 50 + 1)) // 1~50개 랜덤 수량
                .build();
        itemRepository.save(item1);
        ReflectionTestUtils.setField(item1, "id", 1);  // ID 강제 설정


        Integer userOrStreamerId = 1;
        Integer orderItemId = 1;
        UserRole userRole = UserRole.CUSTOMER;

        Order order= new Order(user, OrderStatus.ORDERED, "address");

        OrderItem orderItem = new OrderItem(order, item1, item1.getRemainingQuantity());

        // orderItemRepository mock 설정
        Mockito.when(orderItemRepository.getByIdAndUserId(orderItemId, userOrStreamerId))
                .thenReturn(Optional.of(orderItem));

        // 서비스 호출
        OrderItemResponseDto responseDto = orderItemService.getOrderItem(userOrStreamerId, userRole, orderItemId);

        // 검증
        assertNotNull(responseDto);

        System.out.println("pointResponseDto: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(responseDto));
    }


    @Test
    @DisplayName("성공 - CUSTOMER 역할로 주문 아이템 조회")
    void getOrderItemForStreamer() throws Exception {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);

        User user = User.builder()
                .name("유저" + uniqueSuffix)
                .nickName("유저 닉네임" + uniqueSuffix)
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .address("유저 주소" + uniqueSuffix)
                .email("user" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        userRepository.save(user);
        ReflectionTestUtils.setField(user, "id", 1);  // ID 강제 설정

        Streamer streamer = Streamer.builder()
                .name("스트리머" + uniqueSuffix)
                .nickName("스트리머" + uniqueSuffix)
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .email("streamer" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        streamerRepository.save(streamer);
        ReflectionTestUtils.setField(streamer, "id", 1);  // ID 강제 설정

        Store store = Store.builder()
                .streamer(streamer)
                .address("서울시 강남구 " + uniqueSuffix)
                .storeName("스토어" + uniqueSuffix)
                .registrationNumber("REG" + ((int) (Math.random() * 900000 + 100000)))
                .build();
        storeRepository.save(store);
        ReflectionTestUtils.setField(store, "id", 1);  // ID 강제 설정

        Item item1 = Item.builder()
                .store(store)
                .itemName("상품" + uniqueSuffix)
                .explanation("이것은 상품 설명입니다.")
                .price(1000) // 1000~10999원 랜덤 가격
                .remainingQuantity((int) (Math.random() * 50 + 1)) // 1~50개 랜덤 수량
                .build();
        itemRepository.save(item1);
        ReflectionTestUtils.setField(item1, "id", 1);  // ID 강제 설정


        Integer userOrStreamerId = 1;
        Integer orderItemId = 1;
        UserRole userRole = UserRole.STREAMER;

        Order order= new Order(user, OrderStatus.ORDERED, "address");

        OrderItem orderItem = new OrderItem(order, item1, item1.getRemainingQuantity());

        // orderItemRepository mock 설정
        Mockito.when(orderItemRepository.getByIdAndStreamerId(orderItemId, userOrStreamerId))
                .thenReturn(Optional.of(orderItem));

        // 서비스 호출
        OrderItemResponseDto responseDto = orderItemService.getOrderItem(userOrStreamerId, userRole, orderItemId);

        // 검증
        assertNotNull(responseDto);

        System.out.println("pointResponseDto: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(responseDto));
    }
}