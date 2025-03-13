package excluz.excluz.domain.kakao.kakao.controller;

import java.util.HashMap;
import java.util.Map;

import excluz.excluz.auth.util.JwtUtil;
import excluz.excluz.common.exception.UnauthorizedException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("oauth/kakao")
public class KakaoController {

    @Value("${kakao.restapi.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final UserService userService;
    private final JwtUtil jwtUtil; // 자체 JWT 토큰 생성을 위한 유틸 주입

    // 카카오 로그인 URL로 리다이렉트
    @GetMapping("/login")
    public ResponseEntity<?> kakaoLogin() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://kauth.kakao.com/oauth/authorize?");
        stringBuilder.append("client_id=").append(kakaoRestApiKey);
        stringBuilder.append("&redirect_uri=").append(kakaoRedirectUri);
        stringBuilder.append("&response_type=code");

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, stringBuilder.toString())
            .build();
    }

    // 카카오에서 리다이렉트해줄 콜백: 인가코드(code) 수신 및 자동 회원가입/로그인 처리 후 자체 JWT 토큰 발급
    @GetMapping
    public ResponseEntity<Map<String, Object>> kakaoAuth(@RequestParam(value = "code", required = false) String code) {
        // 인가 코드를 통해 Kakao 액세스 토큰 발급
        String accessToken = getKakaoAccessToken(code);
        if (accessToken == null) {
            throw new UnauthorizedException(ErrorCode.TOKEN_NOT_FOUND);
        }

        // 액세스 토큰으로 Kakao 사용자 정보 조회
        Map<String, Object> kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 사용자 정보에서 이메일 추출 (최상위에 없으면 kakao_account 내부에서)
        String email = (String) kakaoUserInfo.get("email");
        if (email == null) {
            Object kakaoAccountObj = kakaoUserInfo.get("kakao_account");
            if (kakaoAccountObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
                email = (String) kakaoAccount.get("email");
            }
        }

        // 사용자 이름 추출 (최상위에 없으면 kakao_account.profile.nickname)
        String name = (String) kakaoUserInfo.get("name");
        if (name == null) {
            Object kakaoAccountObj = kakaoUserInfo.get("kakao_account");
            if (kakaoAccountObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
                Object profileObj = kakaoAccount.get("profile");
                if (profileObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> profile = (Map<String, Object>) profileObj;
                    name = (String) profile.get("nickname");
                }
            }
        }

        log.info("Extracted email: {}", email);
        log.info("Extracted name: {}", name);

        // 자동 회원가입/로그인 처리 (이미 존재하면 로그인, 없으면 신규 가입)
        // 서비스에서 회원가입 후, 등록된 User 객체를 반환하도록 함
        User registeredUser = userService.OauthLogin(email, name);

        // 2-6. 자체 JWT 토큰 생성 (UserService로부터 받은 사용자 id 사용)
        String jwtToken = jwtUtil.createToken(email, registeredUser.getId(), UserRole.CUSTOMER);
        log.info("Generated JWT Token: {}", jwtToken);

        // 2-7. 응답 헤더에 JWT 토큰 추가 및 JSON 응답 작성
        HttpHeaders responseHeaders = new HttpHeaders();
        // JwtUtil.createToken()에서 이미 "Bearer " 접두어를 붙이고 있다면, 그대로 사용합니다.
        responseHeaders.set(HttpHeaders.AUTHORIZATION, jwtToken);
        // CORS 환경에서 클라이언트가 Authorization 헤더에 접근할 수 있도록 노출 설정
        responseHeaders.set("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("accessToken", jwtToken);

        return ResponseEntity.ok()
            .headers(responseHeaders)
            .body(responseBody);
    }

    // 3) 액세스 토큰 발급 메서드 (인가코드 → 액세스 토큰)
    private String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoRestApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            if (body != null) {
                Object tokenObj = body.get("access_token");
                if (tokenObj instanceof String) {
                    return (String) tokenObj;
                }
            }
        }
        return null;
    }

    // 액세스 토큰을 이용해 Kakao 사용자 정보를 조회하는 메서드
    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return new HashMap<>();
    }
}
