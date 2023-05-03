package com.example.booking.entity;


import com.example.BaseEntity;
import com.example.booking.BookingStatus;
import com.example.pass.entity.Pass;
import com.example.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@Entity
@Table(name = "booking")
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingSeq;
    private Integer passSeq;
    private String userId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    private boolean usedPass;  // 이용권 사용여부
    private boolean attended;  // 출석 여부

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime cancelledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passSeq", insertable = false, updatable = false)
    private Pass pass;

    // endedAt 기준, yyyy-MM-HH 00:00:00
    //  해당 일자의 모든 데이터를 조회하기위해 시간, 분, 초, 나노초를 모두 0으로 설정하여 하루의 시작 시간으로 만들어주고, 그 값을 LocalDateTime 형태로 반환
    public LocalDateTime getStatisticsAt() {
        return endedAt.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }


}
