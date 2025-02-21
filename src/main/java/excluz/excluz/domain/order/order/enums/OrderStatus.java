package excluz.excluz.domain.order.order.enums;

import java.util.*;
import java.util.stream.Collectors;

public enum OrderStatus {
    ORDERED("ROLE_CUSTOMER"),          // 손님이 주문 생성
    PREPARING("ROLE_STREAMER"),        // 가게에서 준비 중
    SHIPPING("ROLE_STREAMER"),         // 가게에서 배달 시작
    DELIVERED("ROLE_CUSTOMER"),        // 손님이 주문 완료
    CANCELED("ROLE_CUSTOMER", "ROLE_STREAMER"); // 손님 또는 가게가 주문 취소

    private final String[] actorList;

    // 가변 인자(Varargs) 사용
    OrderStatus(String... actorList) {
        this.actorList = actorList;
    }

    // 문자열로 OrderStatus 변환 (잘못된 값이면 예외 발생)
    public static OrderStatus of(String orderStatus) {
        return Arrays.stream(OrderStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(orderStatus))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid order status: " + orderStatus));
    }

    // 특정 actor가 포함된 상태 목록 반환
    public static List<OrderStatus> getStatusListByActor(String actor) {
        return Arrays.stream(OrderStatus.values())
                .filter(status -> Arrays.asList(status.actorList).contains(actor.toUpperCase()))
                .collect(Collectors.toList());
    }

    public boolean canPerformAction(String actor) {
        return Arrays.asList(actorList).contains(actor.toUpperCase());
    }

    public String[] getActorList() {
        return actorList;
    }

    private static final Map<OrderStatus, List<OrderStatus>> validTransitions = new HashMap<>();

    static {
        validTransitions.put(ORDERED, List.of(PREPARING, CANCELED));   // ORDERED → PREPARING 또는 CANCELED
        validTransitions.put(PREPARING, List.of(SHIPPING));  // PREPARING → SHIPPING 또는 CANCELED
        validTransitions.put(SHIPPING, List.of(DELIVERED));            // SHIPPING → DELIVERED
        validTransitions.put(DELIVERED, Collections.emptyList());            // DELIVERED 이후는 상태 변경 불가
        validTransitions.put(CANCELED, Collections.emptyList());             // CANCELED 이후는 상태 변경 불가
    }

    // ✅ 특정 상태에서 새로운 상태로 변경 가능한지 확인
    public boolean canChangeTo(OrderStatus newStatus) {
        return validTransitions.getOrDefault(this, Collections.emptyList()).contains(newStatus);
    }
}