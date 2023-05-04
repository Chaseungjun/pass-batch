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

    @Mapping(target = "uuid", source = "booking.user.uuid")  // get으로 가져옴
    @Mapping(target = "text", source = "booking.startedAt", qualifiedByName = "text")
    Notification toNotificationEntity(Booking booking, NotificationEvent event);

    @Named("text")
    default String text(LocalDateTime startedAt) {
        return String.format("안녕하세요. %s 수업 시작합니다. 수업 전 출석 체크 부탁드립니다. \uD83D\uDE0A", LocalDateTimeUtils.format(startedAt));
    }
}
