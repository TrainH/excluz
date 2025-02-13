package excluz.excluz.domain.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import excluz.excluz.auth.util.PasswordEncoder;
import excluz.excluz.common.entity.User;
import excluz.excluz.domain.user.dto.request.UserSignupRequestDto;
import excluz.excluz.domain.user.dto.response.UserSignupResponseDto;
import excluz.excluz.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserSignupResponseDto userSignup(UserSignupRequestDto signupRequest) {

		// 가입된 유저의 이메일 여부를 확인
		Optional<User> existingUser = userRepository.findByEmail(signupRequest.getEmail());

		// 이미 가입된 유저의 경우의 예외
		if (existingUser.isPresent()) {
			throw new IllegalArgumentException("이미 가입된 유저의 이메일 입니다.");
		}

		// 리퀘스트 요청에 들어온 비밀번호와 재확인 비밀번호가 일치 하지 않을 시 예외
		if (!signupRequest.getPassword().equals(signupRequest.getReEnterPassword())) {
			throw new IllegalArgumentException("비밀번호가 재입력 비밀번호와 일치하지 않습니다.");
		}

		// 비밀번호 해싱 처리
		String bcryptPassword = passwordEncoder.encode(signupRequest.getPassword());

		User user = new User(
			signupRequest.getName(),
			signupRequest.getNickName(),
			signupRequest.getPhoneNumber(),
			signupRequest.getAddress(),
			signupRequest.getEmail(),
			bcryptPassword);

		userRepository.save(user);

		return new UserSignupResponseDto("회원가입이 완료되었습니다.", user);
	}
}
