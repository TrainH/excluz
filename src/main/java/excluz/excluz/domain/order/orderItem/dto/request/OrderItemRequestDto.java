package excluz.excluz.domain.order.orderItem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemRequestDto {

        @NotNull(message = "itemId 는 필수 입력값입니다.")
        private Integer itemId;

        @NotNull(message = "itemQuantity 는 필수 입력값입니다.")
        @Min(value = 1, message = "itemQuantity 는 1 이상이어야 합니다.")
        private Integer itemQuantity;

        @NotBlank(message = "address 는 필수 입력값입니다.")
        private String address;

        public OrderItemRequestDto(Integer itemId, Integer itemQuantity, String address) {
                this.itemId = itemId;
                this.itemQuantity = itemQuantity;
                this.address = address;
        }
}
