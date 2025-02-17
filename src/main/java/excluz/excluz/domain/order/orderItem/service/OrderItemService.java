package excluz.excluz.domain.order.orderItem.service;

import excluz.excluz.common.entity.*;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.cartItem.repository.CartItemRepository;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.enums.OrderStatus;
import excluz.excluz.domain.order.order.repository.OrderRepository;
import excluz.excluz.domain.order.orderItem.dto.request.OrderItemRequestDto;
import excluz.excluz.domain.order.orderItem.dto.response.OrderItemResponseDto;
import excluz.excluz.domain.order.orderItem.repository.OrderItemRepository;
import excluz.excluz.domain.point.point.repository.PointRepository;
import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;
import excluz.excluz.domain.point.pointTransaction.repository.PointTransactionRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import excluz.excluz.domain.user.enums.UserRole;
import excluz.excluz.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final PointRepository pointRepository;
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional
    public void createOrderItemList(Integer userOrStreamerId, String userRole, List<OrderItemRequestDto> requestList) {
        /**
         * [주문 조건]
         * 1. 주문은 CUSTOMER만 가능
         * 2. CUSTOMER의 point가 없는 경우 충전해야한다고 에러 발생
         * 3. 요청시에 모든 주문의 배달장소는 1개로 동일해야함
         * 4. 요청 주문 아이템 id 갯수와 실제 주문 아이템 갯수가 일치하는지 확인
         * 5. 요청한 아이템들의 Store가 동일한지 확인
         */

        //  1. 주문은 CUSTOMER만 가능
        if (!userRole.equals(UserRole.CUSTOMER.getRole())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
        }

        // 2. CUSTOMER의 point가 없는 경우 충전해야한다고 에러 발생
        Point userPoint = pointRepository.findByUserRoleAndUserOrStreamerId(
                UserRole.CUSTOMER, userOrStreamerId
        ).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // 3. 요청시에 모든 주문의 배달장소는 1개로 동일해야함
        Set<String> AddressSet = requestList.stream()
                .map(OrderItemRequestDto::getAddress)
                .collect(Collectors.toSet());

        if (AddressSet.size() != 1) {
            throw new BadRequestException(ErrorCode.ITEM_NOT_FOUND); // 나중에 예외처리 코드 수정 필요
        }

        String address = requestList.get(0).getAddress();


        // 4. 요청 주문 아이템 id 갯수와 실제 주문 아이템 갯수가 일치하는지 확인
        List<Integer> itemIdList = requestList.stream()
                .map(OrderItemRequestDto::getItemId)
                .toList();

        List<Item> itemList = itemRepository.findAllById(itemIdList);

        if (itemList.size() != itemIdList.size()) {
            throw new NotFoundException(ErrorCode.ITEM_NOT_FOUND);
        }

        List<CartItem> cartItemList = cartItemRepository.findByUserId(userOrStreamerId);


        // 5. 모든 아이템의 Store가 동일한지 확인
        Set<Store> storeSet = itemList.stream()
                .map(Item::getStore)
                .collect(Collectors.toSet());

        if (storeSet.size() != 1) {
            throw new BadRequestException(ErrorCode.ITEM_NOT_FOUND); // 나중에 예외처리 코드 수정 필요
        }

        Store store = storeSet.iterator().next();



        // 주문 아이템 생성 및 총 금액 계산
        User user = userRepository.findById(userOrStreamerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Order order = new Order(user, OrderStatus.ORDERED, address);

        List<OrderItem> orderItemList = new ArrayList<>();

        List<CartItem> deleteCartItemList = new ArrayList<>(); // 제거해야할 장바구니 아이템 리스트

        int totalAmount = 0;

        for (OrderItemRequestDto dto : requestList) {
            Integer orderedItemId = dto.getItemId();
            Integer orderedItemQuantity = dto.getItemQuantity();

            Item item = itemList.stream()
                    .filter(i -> i.getId().equals(orderedItemId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND));


            CartItem cartItem = cartItemList.stream()
                    .filter(i -> i.getId().equals(orderedItemId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND));

            if (!orderedItemQuantity.equals(cartItem.getQuantity())) {
                throw new BadRequestException(ErrorCode.CART_ITEM_NOT_FOUND);
            }

            Integer orderedItemPrice = item.getPrice();

            // CartItem 제거
            deleteCartItemList.add(cartItem);

            // Item 잔여수량 차감
            item.removeRemainingQuantity(orderedItemQuantity);

            // 주문 아이템 추가
            orderItemList.add(new OrderItem(order, item, orderedItemQuantity));

            // 가격 합산
            totalAmount += orderedItemPrice * orderedItemQuantity;
        }

        // 포인트 거래 생성
        PointTransaction pointTransaction = PointTransaction.builder()
                .order(order)
                .user(user)
                .store(store)
                .transactionType(TransactionType.PURCHASE)
                .amount(totalAmount)
                .build();

        // Streamer의 포인트 조회 또는 초기화
        Point streamerPoint = pointRepository.findByUserRoleAndUserOrStreamerId(
                store.getStreamer().getUserRole(), store.getStreamer().getId()
        ).orElseGet(() -> new Point(store.getStreamer().getUserRole(), store.getStreamer().getId(), 0));


        // 포인트 추가
        userPoint.disChargeAmount(totalAmount);
        streamerPoint.chargeAmount(totalAmount);

        // 데이터 저장
        cartItemRepository.deleteAllInBatch(deleteCartItemList);
        orderRepository.save(order);
        pointRepository.save(streamerPoint);
        orderItemRepository.saveAll(orderItemList);
        pointTransactionRepository.save(pointTransaction);

    }

    @Transactional(readOnly = true)
    public Page<OrderItemResponseDto> getOrderItemList(Integer userOrStreamerId, String userRole, Pageable pageable) {

        if (userRole.equals(UserRole.CUSTOMER.getRole())) {
            return orderItemRepository.findByUserId(userOrStreamerId, pageable).map(OrderItemResponseDto::from);
        }

        if (userRole.equals(UserRole.STREAMER.getRole())) {
            return orderItemRepository.findByStreamerId(userOrStreamerId, pageable).map(OrderItemResponseDto::from);
        }
        throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
    }

    @Transactional(readOnly = true)
    public OrderItemResponseDto getOrderItem(Integer userOrStreamerId, String userRole, Integer orderItemId) {
        if (userRole.equals(UserRole.CUSTOMER.getRole())) {
            return orderItemRepository
                    .getByIdAndUserId(orderItemId, userOrStreamerId)
                    . map(OrderItemResponseDto::from).orElseThrow(
                            () -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND)
                    );
        }

        if (userRole.equals(UserRole.STREAMER.getRole())) {
            return orderItemRepository
                    .getByIdAndStreamerId(orderItemId, userOrStreamerId)
                    .map(OrderItemResponseDto::from).orElseThrow(
                            () -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND)
                    );
        }
        throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
    }
}
