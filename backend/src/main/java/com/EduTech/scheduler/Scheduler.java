package com.EduTech.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.member.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
	
	private final MemberRepository memberRepository;
	
	private final EventInfoRepository infoRepository;

	private final DemonstrationReserveRepository resRepository;
	
	// 회원탈퇴 일주일지난 회원정보 자동삭제
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul") // 10분 마다 실행
    public void cleanUpLeaveMembers() {
        memberRepository.deleteMembersAfterOneWeekLeave();
    }
    
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    public void updateEventStates() {
        int before = infoRepository.updateStateToBefore();
        int open = infoRepository.updateStateToOpen();
        int closed = infoRepository.updateStateToClosed();

        log.info("상태 갱신 완료 - BEFORE: {}, OPEN: {}, CLOSED: {}", before, open, closed);
    }
    
    @Scheduled(cron = "0 0 0 * * 0", zone = "Asia/Seoul")
    public void deleteResCancelState() {
    	resRepository.deleteResCancel(DemonstrationState.CANCEL);
    }
}
