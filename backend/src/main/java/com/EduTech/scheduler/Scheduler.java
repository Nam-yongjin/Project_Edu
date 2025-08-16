package com.EduTech.scheduler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.EduTech.dto.demonstration.DemonstrationImageDTO;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRequestRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.facility.HolidaySyncService;
import com.EduTech.util.FileUtil;

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

	private final DemonstrationImageRepository demonstrationImageRepository;

	private final HolidaySyncService holidaySyncService;

	private final FileUtil fileUtil;

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

	// 매주 일요일 마다 res테이블의 상태가 CANCEL 인 상태의 행 제거
	@Scheduled(cron = "0 0 0 * * 0", zone = "Asia/Seoul")
	public void deleteResCancelState() {
		resRepository.deleteResCancel(DemonstrationState.CANCEL);
		resRepository.deleteResCancel(DemonstrationState.REJECT);
	}

	// 매주 일요일 마다 reg테이블의 상태가 CANCEL 인 물품번호를 가진 dem테이블의 행 제거
	@Scheduled(cron = "0 0 0 * * 0", zone = "Asia/Seoul")
	public void deleteDemCancelState() {
		List<Long> demNums = regRepository.selectRegDemNums(DemonstrationState.CANCEL);
		demNums.addAll(regRepository.selectRegDemNums(DemonstrationState.REJECT));
		demNums.addAll(regRepository.selectRegDemNums(DemonstrationState.EXPIRED));
		if (demNums.isEmpty())
			return;

		for (Long demNum : demNums) {
			demRepository.findById(demNum).ifPresent(demo -> {
				demRepository.delete(demo); // 영속성 컨텍스트 거쳐서 cascade 삭제됨
				List<DemonstrationImageDTO> deleteImageList = demonstrationImageRepository.selectDemImage(demNum);
				List<String> filePaths = new ArrayList<>();
				for (DemonstrationImageDTO dto : deleteImageList) {
					String path = dto.getImageUrl();
					String s_path = "s_" + dto.getImageUrl();
					filePaths.add(path);
					filePaths.add(s_path);
				}

				// 폴더에서 이미지 삭제
				fileUtil.deleteFiles(filePaths);

				// 기존 상품 이미지 삭제 후,
				demonstrationImageRepository.deleteDemNumImage(demNum);
			});
		}
	}

	// 매주 일요일 마다 req테이블의 상태가 CANCEL 인 행제거
	@Scheduled(cron = "0 0 0 * * 0", zone = "Asia/Seoul")
	public void deleteReqCancelState() {
		reqRepository.deleteReq(DemonstrationState.ACCEPT);
		reqRepository.deleteReq(DemonstrationState.REJECT);
	}

	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void updateExpiredStates() {
		LocalDate today = LocalDate.now();

		// Reservation (res 테이블) 상태 갱신
		resRepository.changeResExpiredState(today, DemonstrationState.EXPIRED);

		// Registration (reg 테이블) 상태 갱신
		regRepository.changeRegExpiredState(today, DemonstrationState.EXPIRED);
	}

}
