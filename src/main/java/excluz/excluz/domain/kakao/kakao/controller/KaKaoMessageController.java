package excluz.excluz.domain.kakao.kakao.controller;

import excluz.excluz.domain.kakao.kakao.dto.KakaoMessageRequestDto;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/kakao")
public class KaKaoMessageController {
    /**
     * POST /kakao/sendMessageJson
     * 요청 헤더:  Authorization: Bearer {카카오 Access Token}
     * 요청 바디(JSON):
     *   {
     *     "url": "https://www.naver.com",
     *     "contentText": "테스트 메시지를 보냅니다!"
     *   }
     */
    @PostMapping("/sendMessageJson")
    public ResponseEntity<Map<String, Object>> sendMessageJson(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody KakaoMessageRequestDto requestBody
    ) throws JsonProcessingException {

        // 1) URL, 메시지 내용
        String url = (requestBody.getUrl() != null) ? requestBody.getUrl() : "https://www.naver.com";
        String contentText = (requestBody.getContentText() != null) ? requestBody.getContentText() : "기본 메시지";

        // 2) 카카오 메시지 전송 endpoint
        String apiUrl = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        // 3) 헤더 설정 (포스트맨에서 받은 Authorization 헤더 그대로 사용)
        //    예: Authorization: Bearer xxxxx
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", authorizationHeader);

        // 4) template_object 구성 (JSON)
        Map<String, Object> templateObject = new HashMap<>();
        templateObject.put("object_type", "text");
        templateObject.put("text", contentText);

        Map<String, String> linkObj = new HashMap<>();
        linkObj.put("web_url", url);
        linkObj.put("mobile_web_url", url);
        templateObject.put("link", linkObj);

        templateObject.put("button_title", "상세보기");

        // 직렬화
        String templateJson = new ObjectMapper().writeValueAsString(templateObject);

        // 5) x-www-form-urlencoded 바디 생성
        MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("template_object", templateJson);

        // RestTemplate POST 요청
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(bodyMap, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> kakaoResponse = restTemplate.postForEntity(apiUrl, requestEntity, Map.class);

        // 결과 JSON 생성
        Map<String, Object> result = new HashMap<>();

        if (kakaoResponse.getStatusCode() == HttpStatus.OK) {
            Map<?, ?> responseBody = kakaoResponse.getBody();
            if (responseBody != null && responseBody.get("result_code") != null) {
                int resultCode = (Integer) responseBody.get("result_code");
                if (resultCode == 0) {
                    result.put("status", "SUCCESS");
                    result.put("message", "나에게 메시지 전송 성공!");
                } else {
                    result.put("status", "FAIL");
                    result.put("message", "나에게 메시지 전송 실패");
                    result.put("details", responseBody);
                }
            }
        } else {
            result.put("status", "ERROR");
            result.put("httpStatus", kakaoResponse.getStatusCode());
        }

        return ResponseEntity.ok(result);
    }
}
