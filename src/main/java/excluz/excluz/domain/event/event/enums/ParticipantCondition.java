package excluz.excluz.domain.event.event.enums;

public enum ParticipantCondition {
    ALL_USERS,           // 로그인 여부 상관 없음
    REGISTERED_USERS,    // 로그인한 회원만
    GUEST_USERS          // 비회원만
}