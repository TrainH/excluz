package excluz.excluz.domain.kakao.kakao.dto;

import lombok.Getter;

@Getter
public class KakaoMessageRequestDto {
    private String url; // 메시지 링크에 넣을 URL (nullable)
    private String contentText; // 보낼 메시지 내용
}