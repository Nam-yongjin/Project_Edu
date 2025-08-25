package com.EduTech.scheduler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.dto.admin.ExpiredUserDTO;
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
import com.EduTech.service.mail.MailService;
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

	private final MailService mailService;
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

	// 매주 일요일 마다 res테이블의 상태가 CANCEL 및 REJECT 인 상태의 행 제거
	@Scheduled(cron = "0 0 0 * * 0", zone = "Asia/Seoul")
	public void deleteResCancelState() {
		resRepository.deleteResCancel(DemonstrationState.CANCEL);
		resRepository.deleteResCancel(DemonstrationState.REJECT);
	}

	// 매주 일요일 마다 reg테이블의 상태가 CANCEL,REJECT,EXPIRED 인 물품번호를 가진 dem테이블의 행 제거
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
				List<DemonstrationImageDTO> deleteImageList = demonstrationImageRepository.selectDemImageIn(List.of(demNum));
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
				demonstrationImageRepository.deleteDemNumImage(List.of(demNum));
			});
		}
	}

	// 매주 일요일 마다 반납 / 대여 요청 완료한 행을 지움
	@Scheduled(cron = "0 0 0 * * 0", zone = "Asia/Seoul")
	public void deleteReqCancelState() {
		reqRepository.deleteReq(DemonstrationState.ACCEPT);
		reqRepository.deleteReq(DemonstrationState.REJECT);
	}

	// 하루마다 검사하여 물품 반납 기한, 물품 대여 기한을 넘겼으면 상태를 expired로 변경
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void updateExpiredStates() {
		LocalDate today = LocalDate.now();
		// Reservation (res 테이블) 상태 갱신
		resRepository.changeResExpiredState(today, DemonstrationState.EXPIRED,DemonstrationState.ACCEPT);

		// Registration (reg 테이블) 상태 갱신
		regRepository.changeRegExpiredState(today, DemonstrationState.EXPIRED,DemonstrationState.ACCEPT);
	}
	
	
	// 매주 일요일 마다 물품을 대여한 사람의 상태가 expired일 경우 메시지 전송
	@Scheduled(cron = "0 0 0 * * 0", zone = "Asia/Seoul")
	public void expiredMessage() {
	    try {
	        List<ExpiredUserDTO> expiredUsers = resRepository.findExpiredInfo(DemonstrationState.EXPIRED);
	        
	        if (expiredUsers.isEmpty()) {
	            System.out.println("만료된 대여 정보가 없습니다.");
	            return;
	        }
	        
	        LocalDate currentDate = LocalDate.now();
	        
	        // 각 만료 사용자별로 메일 발송
	        for (ExpiredUserDTO user : expiredUsers) {
	            // 경과 일수 계산
	            long expiredDays = ChronoUnit.DAYS.between(user.getEndDate(), currentDate);
	            
	            // AdminMessageDTO 생성
	            AdminMessageDTO adminMessageDTO = new AdminMessageDTO();
	            
	            // 수신자 설정 (이미 이메일 주소로 받아옴)
	            List<String> memberList = new ArrayList<>();
	            String userEmail = user.getEmail(); // 쿼리에서 m.email을 받아왔으므로 이미 이메일 주소
	            memberList.add(userEmail);
	            adminMessageDTO.setMemberList(memberList);
	            
	            // 제목 설정
	            adminMessageDTO.setTitle("대여 물품 반납 알림 - 반납 기한이 " + expiredDays + "일 경과했습니다");
	            
	            // 내용 설정
	            String content = String.format(
	                "<p>안녕하세요</p>" +
	                "<p>대여하신 물품의 반납 기한이 <strong>%d일</strong> 경과하였습니다.</p>" +
	                "<p><strong>반납 예정일:</strong> %s</p>" +
	                "<p><strong>경과 일수:</strong> %d일</p>" +
	                "<p>빠른 시일 내에 물품을 반납해 주시기 바랍니다.</p>" +
	                "<p>문의사항이 있으시면 관리자에게 연락해 주세요.</p>" +
	                "<p>감사합니다.</p>",
	                expiredDays,
	                user.getEndDate(),
	                expiredDays
	            );
	            
	            adminMessageDTO.setContent(content);
	            adminMessageDTO.setAttachmentFile(null); // 첨부파일 없음
	            
	            // 메일 발송
	            mailService.sendMimeMessage(adminMessageDTO);
	            
	            System.out.println("만료 알림 메일 발송 완료: " + userEmail + " (경과일수: " + expiredDays + "일)");
	        }
	        
	        System.out.println("총 " + expiredUsers.size() + "명에게 만료 알림 메일을 발송했습니다.");
	        
	    } catch (Exception e) {
	        System.err.println("만료 알림 메일 발송 중 오류 발생: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

}
