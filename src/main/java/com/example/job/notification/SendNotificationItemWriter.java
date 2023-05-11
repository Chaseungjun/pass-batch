package com.example.job.notification;

import com.example.adapter.message.KakaoTalkMessageAdapter;
import com.example.notification.entity.Notification;
import com.example.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendNotificationItemWriter implements ItemWriter<Notification> {

    private final NotificationRepository notificationRepository;
    private final KakaoTalkMessageAdapter kakaoTalkMessageAdapter;


    @Override
    public void write(List<? extends Notification> notifications) {
        int count = 0;


        for (Notification notificationEntity : notifications) {
            boolean successful = kakaoTalkMessageAdapter.sendKakaoTalkMessage(notificationEntity.getUuid(), notificationEntity.getText());
            // NotificationMapStruct 에서 만들어진 Notification의 정보를 활용

            if (successful) {
                notificationEntity.setSent(true);
                notificationEntity.setSentAt(LocalDateTime.now());
                notificationRepository.save(notificationEntity);
                count++;
            }

        }
        log.info("SendNotificationItemWriter - write: 수업 전 알람 {}/{}건 전송 성공", count, notifications.size());
    }
}
