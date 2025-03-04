package excluz.excluz.domain.kakao.kakao.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("oauth/kakao")
public class KakaoController {

    @Value("${kakao.restapi.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    // 1) 카카오 로그인 URL로 리다이렉트
    @GetMapping("/login/kakao")
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

    // 2) 카카오에서 리다이렉트해줄 콜백: 인가코드(code) 수신
    @GetMapping()
    public ResponseEntity<Map<String, Object>> kakaoAuth(@RequestParam("code") String code) {

        String accessToken = getKakaoAccessToken(code);

        // JSON 형태로 응답
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("accessToken", accessToken);

        // 필요하다면 refreshToken 같은 것도 추출해서 넣을 수 있음
        return ResponseEntity.ok(responseBody);
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

}



