package excluz.excluz.domain.order.orderItem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDto(
        @NotNull(message = "itemId 는 필수 입력값입니다.")
        Integer itemId,

        @NotNull(message = "itemQuantity 는 필수 입력값입니다.")
        @Min(value = 1, message = "itemQuantity 는 1 이상이어야 합니다.")
        Integer itemQuantity,

        @NotBlank(message = "address 는 필수 입력값입니다.")
        String address
) {
}
