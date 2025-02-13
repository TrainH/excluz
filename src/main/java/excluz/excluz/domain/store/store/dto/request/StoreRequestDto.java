package excluz.excluz.domain.store.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreRequestDto {

	@NotBlank(message = "사업장 주소는 필수 입력값 입니다.")
	private final String address;

	@NotBlank(message = "스토어 이름은 필수 입력값 입니다.")
	private final String storeName;

	@NotBlank(message = "사업자 등록번호는 필수 입력값 입니다.")
	@Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "사업자 등록번호는 XXX-XX-XXXXX 형식으로 작성해 주세요.")
	private final String registrationNumber;
}
