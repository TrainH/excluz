package excluz.excluz.domain.store.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreDeleteRequestDto {

	@NotBlank(message = "비밀번호 입력은 필수입니다.")
	private String password;
}
