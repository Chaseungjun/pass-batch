package com.example.userGroup.repository;

import com.example.userGroup.entity.UserGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupMappingRepository extends JpaRepository<UserGroupMapping, String> {
    List<UserGroupMapping> findByUserGroupId(String userGroupId);   // userGroupId로 userGroup를 찾고 거기서 userId를 찾는다

}
