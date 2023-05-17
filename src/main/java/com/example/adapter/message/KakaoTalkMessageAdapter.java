package com.example.adapter.message;

import com.example.appconfig.KakaoTalkMessageConfig;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class KakaoTalkMessageAdapter {

    private final WebClient webClient;

    public KakaoTalkMessageAdapter(KakaoTalkMessageConfig config) {
        webClient = WebClient.builder()
                .baseUrl(config.getHost())      // 웹서비스 주소
                .defaultHeaders(h -> {          //  HTTP 요청시 설정될 기본 헤더값
                    h.setBearerAuth(config.getToken());   // set(AUTHORIZATION, "Bearer " + token);
                    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                }).build();
    }

    public boolean sendKakaoTalkMessage(final String uuid, final String text) {
        KakaoTalkMessageResponse response = webClient.post().uri("/v1/api/talk/friends/message/default/send")
                // 엔드포인트
                .body(BodyInserters.fromValue(new KakaoTalkMessageRequest(uuid, text)))
                .retrieve() // 요청을 실행하고 응답을 받아온다
                .bodyToMono(KakaoTalkMessageResponse.class) // 응답 본문을 KakaoTalkMessageResponse 클래스로 변환
                .block();  //  비동기적인 호출 결과를 동기적으로 처리하기 위해 사용

        if (response == null || response.getSuccessfulReceiverUuids() == null) {
            return false;
        }
        return response.getSuccessfulReceiverUuids().size() > 0;
    }
}
