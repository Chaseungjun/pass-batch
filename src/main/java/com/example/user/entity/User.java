package com.example.user.entity;

import com.example.user.UserStatus;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeDef(name = "json", typeClass = JsonType.class)
public class User {

    @Id
    private String userId;

    private String userName;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private String phone;

    @Type(type = "json")
    private Map<String, Object> meta;
    // 여기서 meta는 json 타입이다. (여기에 들어가있는 uuid는 메시지 api에서 설정한 uuid이다)

    public String getUuid() {
        String uuid = null;
        if (meta.containsKey("uuid")) {
            uuid = String.valueOf(meta.get("uuid"));
        }
        return uuid;
    }
}
