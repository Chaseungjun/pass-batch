package com.example.pass;

import com.example.bulkpass.BulkPassStatus;
import com.example.bulkpass.entity.BulkPass;
import com.example.pass.entity.Pass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)     // 일치하지 않는 필드는 무시한다
public interface PassMapStruct {

    PassMapStruct INSTANCE = Mappers.getMapper(PassMapStruct.class);
    /**
     *  qualifiedByName을 사용하면, @Named 어노테이션을 사용하여 매핑 메소드의 이름을 지정합니다
     *      toPassEntity 메소드에서 status 필드를 매핑할 때는  @Named("defaultStatus") 메소드가 호출되어 매핑을 수행하게 됩니다.
     *      필드명이 같지 않거나 custom하게 매핑해주기 위해서는 @Mapping을 추가해주면 됩니다.
     */
    @Mapping(target = "status", qualifiedByName = "defaultStatus")
    @Mapping(target = "remainingCount", source = "bulkPass.count")
    Pass toPassEntity(BulkPass bulkPass, String userId);  // BulkPass와 userId로 Pass를 생성

    // BulkPassStatus와 관계 없이 PassStatus값을 설정합니다.
    @Named("defaultStatus")
    default PassStatus status(BulkPassStatus status) {
        return PassStatus.READY;
    }
}
