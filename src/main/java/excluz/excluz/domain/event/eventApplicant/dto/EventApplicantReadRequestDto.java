package excluz.excluz.domain.event.eventApplicant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventApplicantReadRequestDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @Size(max = 50, message = "이메일은 최대 50자까지 허용합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 30, message = "비밀번호는 최대 30자까지 허용합니다.")
    private String applicantPassword;

    public EventApplicantReadRequestDto(String email, String applicantPassword) {
        this.email = email;
        this.applicantPassword = applicantPassword;
    }
}
