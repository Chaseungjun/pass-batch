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
    private String uuid;    // 메시지를 받는 사람의 uuid로 User 클래스에 meta에 들어가있다

    @Enumerated(EnumType.STRING)
    private NotificationEvent event;
    private String text;
    private boolean sent;
    private LocalDateTime sentAt;
}
