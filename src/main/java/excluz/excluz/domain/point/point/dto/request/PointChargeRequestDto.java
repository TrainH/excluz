package excluz.excluz.domain.point.point.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointChargeRequestDto {

        @NotNull(message = "충전금액을 입력하세요.")
        @Min(value = 1000, message = "충전금액은 1000원 이상이어야 합니다.")
        private Integer amount;

        public PointChargeRequestDto(Integer amount) {
                this.amount = amount;
        }
}