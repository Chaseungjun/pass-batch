package com.example.userGroup;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class UserGroupMappingId implements Serializable {
    // 두개 다 다른 곳에서 PK역할을 함
    private String userGroupId;
    private String userId;
}
