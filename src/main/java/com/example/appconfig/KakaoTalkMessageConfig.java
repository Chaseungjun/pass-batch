package com.example.appconfig;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "kakaotalk")  // yml에서 kakaotalk: 부분의 설정을 적용한다
public class KakaoTalkMessageConfig {

    private String host;
    private String token;
}
