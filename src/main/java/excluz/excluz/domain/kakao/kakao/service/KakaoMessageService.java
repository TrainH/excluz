package excluz.excluz.domain.kakao.kakao.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoMessageService {

    @Value("${kakao.base-url}")
    private String BASE_URL;

    /**
     * 이벤트 응모 결과 메시지 전송
     */
    public void sendApplicationResultMessage(String kakaoAuthorizationHeader,
                                             EventApplicantResponseDto responseDto) {
        // 1) 메시지 구성
        String messageText = buildMessageText(responseDto);

        // 2) 카카오 API 요청 및 리다이렉트 준비
        String apiUrl = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
        String webUrl = BASE_URL + "api/v1/events/applicants/myinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", kakaoAuthorizationHeader);

        // 3) code만 쿼리 파라미터로 붙인 URL 생성 (email, password는 제외)
        String eventCode = responseDto.getEventCode(); // 예: "EVENT_7B922463"
        StringBuilder urlBuilder = new StringBuilder(webUrl);
        urlBuilder.append("?code=").append(eventCode);

        Map<String, String> linkObject = new HashMap<>();
        linkObject.put("web_url", urlBuilder.toString());
        linkObject.put("mobile_web_url", urlBuilder.toString());

        // 4) template_object 설정
        Map<String, Object> templateObject = new HashMap<>();
        templateObject.put("object_type", "text");
        templateObject.put("text", messageText);
        templateObject.put("link", linkObject);
        templateObject.put("button_title", "상세보기");

        String templateJson;
        try {
            templateJson = new ObjectMapper().writeValueAsString(templateObject);
        } catch (Exception e) {
            log.error("JSON 직렬화 오류: ", e);
            return;
        }

        MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("template_object", templateJson);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(bodyMap, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, requestEntity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("카카오 메시지 전송 결과: {}", response.getBody());
            } else {
                log.warn("카카오 메시지 전송 실패, status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("카카오 메시지 전송 중 예외 발생: ", e);
        }
    }

    /**
     *  메시지 본문 예시(이메일 포함, 비밀번호 제외)
     */
    private String buildMessageText(EventApplicantResponseDto responseDto) {
        ApplicantStatus status = responseDto.getApplicantStatus();
        // 이메일을 메시지 내용에만 표시
        String email = responseDto.getEmail();
        return String.format("이벤트 응모가 정상적으로 접수되었습니다.%n이메일: %s%n현재 상태: %s", email, status);
    }


}
