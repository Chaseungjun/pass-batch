package com.example.pass;

import com.example.bulkpass.BulkPassStatus;
import com.example.bulkpass.entity.BulkPass;
import com.example.bulkpass.repository.BulkPassRepository;
import com.example.pass.repository.PassRepository;
import com.example.userGroup.entity.UserGroupMapping;
import com.example.userGroup.repository.UserGroupMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AddPassesTaskletTest {

    @Mock
    private StepContribution stepContribution;

    @Mock
    private ChunkContext chunkContext;

    @Mock
    private PassRepository passRepository;

    @Mock
    private BulkPassRepository bulkPassRepository;

    @Mock
    private UserGroupMappingRepository userGroupMappingRepository;

    // @InjectMocks 클래스의 인스턴스를 생성하고 @Mock으로 생성된 객체를 주입합니다.
    @InjectMocks
    private AddPassesTasklet addPassesTasklet;


    @Test
    @DisplayName("excute")
    void excute() throws Exception {
        //given
        final String userGroupId = "GROUP";
        final String userId = "A1000000";
        final Integer packageSeq = 1;
        final Integer count = 10;

        final LocalDateTime now = LocalDateTime.now();

        final BulkPass bulkPass = new BulkPass();
        bulkPass.setPackageSeq(packageSeq);
        bulkPass.setUserGroupId(userGroupId);
        bulkPass.setStatus(BulkPassStatus.READY);
        bulkPass.setCount(count);
        bulkPass.setStartedAt(now);
        bulkPass.setEndedAt(now.plusDays(60));

        UserGroupMapping userGroupMapping = new UserGroupMapping();
        userGroupMapping.setUserGroupId(userGroupId);
        userGroupMapping.setUserId(userId);
        //when
        when(bulkPassRepository.findByStatusAndStartedAtGreaterThan(eq(BulkPassStatus.READY), any())).thenReturn(List.of(bulkPass));
        when(userGroupMappingRepository.findByUserGroupId(eq("GROUP"))).thenReturn(List.of(userGroupMapping));

        RepeatStatus repeatStatus = addPassesTasklet.execute(stepContribution, chunkContext);
        //then
    }
}