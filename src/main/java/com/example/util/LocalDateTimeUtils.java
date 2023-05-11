package com.example.util;


import com.vladmihalcea.hibernate.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtils {

    // 해당하는 패턴으로 날짜를 포맷하는 포맷터 생성
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("YYYY_MM_DD_HH_MM");
    public static final DateTimeFormatter YYYY_MM_DD= DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String format(LocalDateTime localDateTime){
        return localDateTime.format(YYYY_MM_DD_HH_MM);
    }

    public static LocalDateTime parse(final String localDateTimeString) {
        if (StringUtils.isBlank(localDateTimeString)) {
            return null;
        }
        return LocalDateTime.parse(localDateTimeString, YYYY_MM_DD_HH_MM);

    }
}
