package com.EduTech.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRequestRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.facility.HolidaySyncService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
	
	private final MemberRepository memberRepository;
	
	private final EventInfoRepository infoRepository;

	private final DemonstrationReserveRepository resRepository;
	
	private final DemonstrationRepository demRepository;
	
	private final DemonstrationRegistrationRepository regRepository;
	
	private final DemonstrationRequestRepository reqRepository;
	
	private final HolidaySyncService holidaySyncService;
	
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
    
    @PostConstruct
    public void initHolidaySync() {
        int count = holidaySyncService.syncThisAndNextYear();
        log.info("앱 시작 시 공휴일 동기화 완료: {}건 추가됨", count);
    }
    
    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
    public void dailyHolidaySync() {
        int count = holidaySyncService.syncThisAndNextYear();
        log.info("정기 공휴일 동기화 완료: {}건 추가됨", count);
    }
    
    
    // 매주 일요일 마다
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul") // 10분 마다 실행
    public void deleteResCancelState() {
    	resRepository.deleteResCancel(DemonstrationState.CANCEL);
    }
    
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul") // 10분 마다 실행
    public void deleteDemCancelState() {
    	List<Long> demNums=regRepository.selectRegDemNums(DemonstrationState.CANCEL);
    	demRepository.deleteDems(demNums);
    }
    
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul") // 10분 마다 실행
    public void deleteReqCancelState() {
    	reqRepository.deleteReq(DemonstrationState.ACCEPT);
    	reqRepository.deleteReq(DemonstrationState.REJECT);
    }
    
    // @Scheduled(cron = "0 0 0 * * 0", zone = "Asia/Seoul")
}
