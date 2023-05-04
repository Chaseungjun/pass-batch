package com.example.notification.entity;

import com.example.BaseEntity;
import com.example.notification.NotificationEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationSeq;
    private String uuid;
    // 카카오 개발자 센터에서 메시지 api에 친구목록 가져오기가 있다 이 api에서 친구의 uuid를 조회할 수 있고 메시지를 발송한다.
    // User 엔티티의 meta에 들어가있는 uuid가 NotificationMapStruct를 통해 여기로 들어간다

    @Enumerated(EnumType.STRING)
    private NotificationEvent event;
    private String text;
    private boolean sent;
    private LocalDateTime sentAt;
}
