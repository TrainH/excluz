package excluz.excluz.domain.order.order.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OrderStatus {
    ORDERED("CUSTOMER"),          // 손님이 주문 생성
    PREPARING("STREAMER"),        // 가게에서 준비 중
    SHIPPING("STREAMER"),         // 가게에서 배달 시작
    DELIVERED("CUSTOMER"),        // 손님이 주문 완료
    CANCELED("CUSTOMER", "STREAMER"); // 손님 또는 가게가 주문 취소

    private final String[] actors;

    // 가변 인자(Varargs) 사용
    OrderStatus(String... actors) {
        this.actors = actors;
    }

    // 문자열로 OrderStatus 변환 (잘못된 값이면 예외 발생)
    public static OrderStatus of(String orderStatus) {
        return Arrays.stream(OrderStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(orderStatus))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid order status: " + orderStatus));
    }

    // 특정 actor가 포함된 상태 목록 반환
    public static List<OrderStatus> getStatusesByActor(String actor) {
        return Arrays.stream(OrderStatus.values())
                .filter(status -> Arrays.asList(status.actors).contains(actor.toUpperCase()))
                .collect(Collectors.toList());
    }

    public String[] getActors() {
        return actors;
    }
}