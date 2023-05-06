package com.example.notification;

import com.example.booking.entity.Booking;
import com.example.notification.entity.Notification;
import com.example.util.LocalDateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapStruct {

    NotificationMapStruct INSTANCE = Mappers.getMapper(NotificationMapStruct.class);

    /**
     *     User 엔티티의 meta 필드에서 uuid 값을 가져와 매핑한다.
     *     getUuid메서드를 직접 사용하지 않아도 MapStruct가  getUuid() 메서드를 호출하여 해당 값을 가져옵니다.
     *     booking 정보를 가져와 uuid 와 text가 들어간 Notification 객체가 만들어지며 나머지는 null값이 들어간다.
     */
    @Mapping(target = "uuid", source = "booking.user.uuid") //getUuid() 호출
    @Mapping(target = "text", source = "booking.startedAt", qualifiedByName = "text")
    Notification toNotificationEntity(Booking booking, NotificationEvent event);

    @Named("text")
    default String text(LocalDateTime startedAt) {
        return String.format("안녕하세요. %s 수업 시작합니다. 수업 전 출석 체크 부탁드립니다. \uD83D\uDE0A", LocalDateTimeUtils.format(startedAt));
    }
}
