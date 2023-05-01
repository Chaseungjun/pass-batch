package com.example.bulkpass.entity;

import com.example.bulkpass.BulkPassStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "bulk_pass")
public class BulkPass {      // 그룹단위 PT 이용권 정보

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bulkPassSeq;

    private Integer packageSeq;
    private String userGroupId;         //  유저 그룹에 속해있는 다수의 유저에게 지급,

    @Enumerated(EnumType.STRING)
    private BulkPassStatus status;
    private Integer count;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;


}
