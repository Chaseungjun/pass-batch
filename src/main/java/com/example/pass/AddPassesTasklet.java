package com.example.pass;

import com.example.bulkpass.BulkPassStatus;
import com.example.bulkpass.entity.BulkPass;
import com.example.bulkpass.repository.BulkPassRepository;
import com.example.pass.entity.Pass;
import com.example.pass.repository.PassRepository;
import com.example.userGroup.entity.UserGroupMapping;
import com.example.userGroup.repository.UserGroupMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class AddPassesTasklet implements Tasklet {

    private final PassRepository passRepository;
    private final BulkPassRepository bulkPassRepository;
    private final UserGroupMappingRepository userGroupMappingRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        final LocalDateTime startedAt = LocalDateTime.now().minusDays(1);
        final List<BulkPass> bulkPassEntities = bulkPassRepository.findByStatusAndStartedAtGreaterThan(BulkPassStatus.READY, startedAt);

        int count = 0;
        // 이용권 시작 일시 1일 전 user group 내 각 사용자에게 이용권을 추가해줍니다.
        for (BulkPass bulkPass : bulkPassEntities) {
            // userGroupId로 userGroupId과 UserId를 PK로 가지고 있는 UserGroupMapping 찾고 userId들을 조회합니다.
            final List<String> userIds = userGroupMappingRepository.findByUserGroupId(bulkPass.getUserGroupId())
                    .stream().map(UserGroupMapping::getUserId).collect(Collectors.toList());

            // 해당하는 유저그룹에 있는 유저들에게 이용권을 지급하고 카운트
            count += addPasses(bulkPass, userIds);
        }
        log.info("AddPassesTasklet - execute: 이용권 {}건 추가 완료, startedAt={}", count, startedAt);
        return RepeatStatus.FINISHED;

    }

    private int addPasses(BulkPass bulkPass, List<String> userIds) {
        List<Pass> passEntities = new ArrayList<>();
        for (String userId : userIds) {
            Pass passEntity = PassMapStruct.INSTANCE.toPassEntity(bulkPass, userId);   // 지급완료되어 READY로 세팅
            passEntities.add(passEntity);
        }
        return passRepository.saveAll(passEntities).size();
    }
}
