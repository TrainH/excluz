package excluz.excluz.domain.kakao.kakao.service;

import java.util.Collections;
import java.util.Map;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import excluz.excluz.auth.config.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// 기본 OAuth2User 정보를 로드
		OAuth2User oAuth2User = super.loadUser(userRequest);
		Map<String, Object> attributes = oAuth2User.getAttributes();

		// email 추출: 최상위에 없으면 kakao_account 내부에서 추출
		String email = (String) attributes.get("email");
		if (email == null) {
			Object kakaoAccountObj = attributes.get("kakao_account");
			if (kakaoAccountObj instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
				email = (String) kakaoAccount.get("email");
			}
		}

		// 사용자 id 추출: "id" 속성이 포함되어 있다고 가정 (Kakao의 경우 숫자 타입 또는 문자열일 수 있음)
		Object idObj = attributes.get("id");
		Integer userId = 0;
		if (idObj instanceof Number) {
			userId = ((Number) idObj).intValue();
		} else if (idObj instanceof String) {
			try {
				userId = Integer.parseInt((String) idObj);
			} catch (NumberFormatException e) {
				log.warn("사용자 id 변환 실패: {}", idObj);
			}
		}

		// password: OAuth2의 경우 일반적으로 비밀번호가 없으므로 빈 문자열 처리
		String password = "";

		// 권한: 예시로 ROLE_CUSTOMER를 부여
		return new CustomUserDetails(email, password, Collections.singleton(() -> "ROLE_CUSTOMER"), userId);
	}
}
