package com.example.pass.entity;

import com.example.BaseEntity;
import com.example.pass.PassStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pass extends BaseEntity {     // 개인에게 지급하는 PT 이용권

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer passSeq;

    private Integer packageSeq;
    private String userId;

    @Enumerated(EnumType.STRING)
    private PassStatus status;
    private Integer remainingCount;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime expiredAt;

    public void updateStatusAndExpiredAt(PassStatus passStatus, LocalDateTime expiredAt){
        this.status = passStatus;
        this.expiredAt = expiredAt;
    }
}
