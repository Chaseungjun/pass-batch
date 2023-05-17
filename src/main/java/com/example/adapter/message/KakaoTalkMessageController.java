package com.example.adapter.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class KakaoTalkMessageController {

    private final KakaoTalkMessageAdapter messageAdapter;

    @PostMapping("/send") // 메시지 전송에 대한 엔드포인트와 HTTP 메서드를 지정합니다.
    public ResponseEntity<String> sendKakaoTalkMessage(@RequestBody KakaoTalkMessageRequest request) {
        List<String> receiverUuids = request.getReceiverUuids();
        boolean success;

        for (String uuid : receiverUuids) {
            success = messageAdapter.sendKakaoTalkMessage(uuid, request.getTemplateObject().getText());
            if (success) {
                return ResponseEntity.ok("KakaoTalk message sent successfully."); // 성공적인 응답
            }
        }
        return ResponseEntity.badRequest().body("Failed to send KakaoTalk message."); // 실패한 경우의 응답
    }
}
