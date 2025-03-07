package excluz.excluz.domain.store.storeRevenue.enums;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.error.ErrorCode;

public enum RevenuePeriod {
    DAY(ChronoUnit.DAYS, 1),
    MONTH(ChronoUnit.MONTHS, 1),
    YEAR(ChronoUnit.YEARS, 1);

    private final ChronoUnit unit;  // 시간 단위 (분, 시간, 일 등)
    private final long amount;       // 해당 단위의 기간 (1분, 2분, 1시간 등)

    RevenuePeriod(ChronoUnit unit, long amount) {
        this.unit = unit;
        this.amount = amount;
    }

    /**
     * 현재 시간 기준으로 시작 시간 계산
     * @param now 기준 시간 (현재 시간)
     * @return startDatetime (시작 시간)
     */
    public LocalDateTime getStartDatetime(LocalDateTime now) {
        if (isMinuteOrHour()) {
            return now.truncatedTo(unit).minus(amount, unit);
        }
        return now.toLocalDate().atStartOfDay().minus(amount, unit);
    }

    /**
     * 현재 시간 기준으로 종료 시간 계산
     * @param now 기준 시간 (현재 시간)
     * @return endDatetime (종료 시간)
     */
    public LocalDateTime getEndDatetime(LocalDateTime now) {
        if (isMinuteOrHour()) {
            return now.truncatedTo(unit);
        }
        return now.toLocalDate().atStartOfDay();
    }

    /**
     * 분 또는 시간 단위인지 확인
     * @return true if MINUTES or HOURS
     */
    private boolean isMinuteOrHour() {
        return unit == ChronoUnit.MINUTES || unit == ChronoUnit.HOURS;
    }

    /**
     * 대소문자 구분 없이 Enum 값 변환
     */
    public static RevenuePeriod valueOfIgnoreCase(String period) {
        for (RevenuePeriod revenuePeriod : values()) {
            if (revenuePeriod.name().equalsIgnoreCase(period)) {
                return revenuePeriod;
            }
        }
        throw new BadRequestException(ErrorCode.PERIOD_NOT_MATCH);
    }
}