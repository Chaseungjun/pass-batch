package com.example.userGroup.entity;

import com.example.BaseEntity;
import com.example.userGroup.UserGroupMappingId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;


@Getter
@Setter
@ToString
@Entity
@Table(name = "user_group_mapping")
@IdClass(UserGroupMappingId.class)  // 복합키 클래스
public class UserGroupMapping extends BaseEntity {

    @Id
    private String userGroupId;
    @Id
    private String userId;

    private String userGroupName;
    private String description;
}
