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

    /*
    메시지를 받는 사람의 식별자인 uuid가 들어가있음
    String 데이터가 Json형태로 바뀌어서 Map에 세팅이 된다
    meta 필드의 하위에 uuid가 저장되어있다
     */
    @Type(type = "json")
    private Map<String, Object> meta;



    public String getUuid() {    // meta 필드에서 uuid 값을 가져오는 메서드
        String uuid = null;
        if (meta.containsKey("uuid")) {
            uuid = String.valueOf(meta.get("uuid"));
        }
        return uuid;
    }
}
