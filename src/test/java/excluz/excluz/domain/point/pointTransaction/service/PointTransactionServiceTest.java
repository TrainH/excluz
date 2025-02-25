package excluz.excluz.domain.point.pointTransaction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import excluz.excluz.common.entity.*;
import excluz.excluz.domain.cartItem.repository.CartItemRepository;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.enums.OrderStatus;
import excluz.excluz.domain.order.order.repository.OrderRepository;
import excluz.excluz.domain.order.orderItem.repository.OrderItemRepository;
import excluz.excluz.domain.point.point.repository.PointRepository;
import excluz.excluz.domain.point.pointTransaction.dto.response.PointTransactionResponseDto;
import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import excluz.excluz.domain.user.enums.UserRole;
import excluz.excluz.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import excluz.excluz.domain.point.pointTransaction.repository.PointTransactionRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class PointTransactionServiceTest {
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
    private PointTransactionService pointTransactionService;

    private User user1;
    private User user2;
    private Streamer streamer;
    private Store store;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);

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

        user2 = User.builder()
                .name("유저" + "2")
                .nickName("유저 닉네임" + "2")
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .address("유저 주소" + "2")
                .email("user" + "2" + "@example.com")
                .password("password")
                .build();
        userRepository.save(user2);
        ReflectionTestUtils.setField(user2, "id", 2);  // ID 강제 설정

       streamer = Streamer.builder()
                .name("스트리머" + "1")
                .nickName("스트리머" + "1")
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .email("streamer" + "1" + "@example.com")
                .password("password")
                .build();
        streamerRepository.save(streamer);
        ReflectionTestUtils.setField(streamer, "id", 1);  // ID 강제 설정

        store = Store.builder()
                .streamer(streamer)
                .address("서울시 강남구 " + "1")
                .storeName("스토어" + "1")
                .registrationNumber("REG" + ((int) (Math.random() * 900000 + 100000)))
                .build();
        storeRepository.save(store);
        ReflectionTestUtils.setField(store, "id", 1);  // ID 강제 설정

        order1 = new Order(user1, OrderStatus.ORDERED, "address1");
        orderRepository.save(order1);
        ReflectionTestUtils.setField(order1, "id", 1);

        order2 = new Order(user2, OrderStatus.ORDERED, "address2");
        orderRepository.save(order2);
        ReflectionTestUtils.setField(order2, "id", 2);
    }

    @Test
    @DisplayName("성공 - CUSTOMER 역할로 포인트 거래 내역 조회")
    void getPointTransactionList() throws Exception {

        // Given
        Pageable pageable = PageRequest.of(0, 10);

        List<PointTransaction> transactionList = List.of(
                new PointTransaction(order1, user1, store, TransactionType.PURCHASE,10000),
                new PointTransaction(order2, user2, store, TransactionType.PURCHASE,20000)
        );

        List<PointTransactionResponseDto> transactionDtoList = transactionList.stream()
                .map(PointTransactionResponseDto::from)
                .toList();

        Integer userOrStreamerId = 1;
        UserRole userRole = UserRole.CUSTOMER;


        Page<PointTransactionResponseDto> transactionDtoPage = new PageImpl<>(transactionDtoList, pageable, transactionDtoList.size());

        // When
        Mockito.when(pointTransactionRepository.findAllByUserRoleAndUserId(userRole, userOrStreamerId, pageable))
                .thenReturn(transactionDtoPage);

        Page<PointTransactionResponseDto> responseDto = pointTransactionService.getPointTransactionList(userOrStreamerId, userRole, pageable);

        // Then
        assertNotNull(responseDto);
        assertEquals(2, responseDto.getTotalElements());
        assertEquals(TransactionType.PURCHASE, responseDto.getContent().get(0).getTransactionType());
        assertEquals(10000, responseDto.getContent().get(0).getAmount());

        System.out.println("responseDto: " + objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(responseDto));
    }

}