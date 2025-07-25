package com.EduTech.controller.event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventBannerDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.event.EventService;
import com.EduTech.util.FileUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {
	
	private static final Logger log = LoggerFactory.getLogger(EventController.class);

	private final EventService eventService;
	private final MemberRepository memberRepository;
	private final FileUtil fileUtil;
	
	@Value("${file.upload.path}")
	private String uploadPath;

	// 관리자용 Api
		// 1. 배너 목록 조회
		@GetMapping("/banners")
		public ResponseEntity<List<EventBannerDTO>> getAllBanners() {
			return ResponseEntity.ok(eventService.getAllBanners());
		}

		// 1-1. 배너 등록
		@PostMapping("/banners/register")
		@PreAuthorize("hasRole('ADMIN')") // 나중에 권한 ADMIN말고 더 있을시 hasRole('Role') or 이거 추가
		public ResponseEntity<String> registerBanner(@ModelAttribute EventBannerDTO dto,
				@RequestParam("file") MultipartFile file) {
			eventService.registerBanner(dto, file);
			return ResponseEntity.ok("배너 등록 완료");
		}

		// 1-2. 배너 삭제
		@DeleteMapping("/banners/delete/{evtFileNum}")
		@PreAuthorize("hasRole('ADMIN')") // 나중에 권한 ADMIN말고 더 있을시 hasRole('Role') or 이거 추가
		public ResponseEntity<Void> deleteBanner(@PathVariable Long evtFileNum) {
			eventService.deleteBanner(evtFileNum);
			return ResponseEntity.noContent().build();
		}

		// 1-3. 배너 이미지 조회
		@GetMapping("/banners/view")
		public ResponseEntity<Resource> viewBannerImage(@RequestParam String filePath) {
			if (filePath == null || filePath.isBlank()) {
				return ResponseEntity.badRequest().build();
			}

			try {
				Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
				Path fullPath = basePath.resolve(filePath).normalize();

				if (!Files.exists(fullPath)) {
					return ResponseEntity.notFound().build();
				}

				Resource resource = new UrlResource(fullPath.toUri());
				String contentType = Files.probeContentType(fullPath);

				return ResponseEntity.ok()
						.contentType(
								MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
						.body(resource);
			} catch (IOException e) {
				log.error("배너 이미지 조회 중 오류", e);
				return ResponseEntity.internalServerError().build();
			}
		}
		

		// ----------------------------------------------------------------
		// 2. 전체 행사 목록 조회 (신청 종료 기간 기준 필터링 없이 전체)
		@GetMapping("/all")
		public ResponseEntity<List<EventInfoDTO>> getAllEvent() {
			return ResponseEntity.ok(eventService.getAllEvents());
		}

		// 3. 페이지네이션 + 검색 조건 포함 목록 조회
		@GetMapping("/admin/list")
		public ResponseEntity<Page<EventInfoDTO>> getAdminProgramList(@RequestParam(required = false) String option,
				@RequestParam(required = false) String query, @RequestParam(required = false) EventState status,
				Pageable pageable) {

			Page<EventInfoDTO> result = eventService.searchAdminEventList(pageable, option, query, status);
			return ResponseEntity.ok(result);
		}

		// 4. 행사 상세 조회
		@GetMapping("/{eventNum}")
		public ResponseEntity<EventInfoDTO> getEvent(@PathVariable Long eventNum) {
			return ResponseEntity.ok(eventService.getEvent(eventNum));
		}

		// 5. 행사 등록(파일 업로드 포함)
		@PostMapping("/register")
		public ResponseEntity<String> registerEvent(@ModelAttribute EventInfoDTO dto,
				@RequestParam(value = "file", required = false) MultipartFile file) {

			eventService.registerEvent(dto, file);
			return ResponseEntity.ok("등록 완료");
		}

		// 6. 수정(파일 업데이트 포함)
		@PutMapping("/update/{eventNum}")
		public ResponseEntity<Void> updateEvent(@PathVariable Long eventNum, @ModelAttribute EventInfoDTO dto,
				@RequestParam(value = "file", required = false) MultipartFile file) {

			eventService.updateEvent(eventNum, dto, file);
			return ResponseEntity.ok().build();
		}

		// 7. 삭제
		@DeleteMapping("/delete/{eventNum}")
		public ResponseEntity<Void> deleteProgram(@PathVariable Long eventNum) {
			eventService.deleteEvent(eventNum);
			return ResponseEntity.noContent().build();
		}

		// 8. 특정 행사의 신청 회원 리스트 조회
		@GetMapping("/{progNo}/applicants")
		public ResponseEntity<List<EventUseDTO>> getApplicantsByProgram(@PathVariable Long eventNum) {
			return ResponseEntity.ok(eventService.getApplicantsByEvent(eventNum));
		}

		// 파일 다운로드
		@GetMapping("/file/{eventNum}")
		public ResponseEntity<Resource> downloadFile(@PathVariable Long eventNum) {
			EventInfo event = eventService.getEventEntity(eventNum);
			return fileUtil.getFile(event.getFilePath(), event.getOriginalName());
		}

		// 사용자용 API
		// 1. 행사 신청
		@PostMapping("/apply")
		public ResponseEntity<String> applyEvent(@RequestBody EventApplyRequestDTO dto) {
			eventService.applyEvent(dto);
			return ResponseEntity.ok("신청이 완료되었습니다.");
		}

		// 2. 신청 여부 확인(중복 신청 방지용)
		@GetMapping("/applied")
		public ResponseEntity<Boolean> isAlreadyApplied(@RequestParam Long eventNum, @RequestParam String memId) {
			return ResponseEntity.ok(eventService.isAlreadyApplied(eventNum, memId));
		}

		// 3. 신청 가능 여부 확인(신청 마감 확인용)
		@GetMapping("/available/{eventNum}")
		public ResponseEntity<Boolean> isAvailable(@PathVariable Long eventNum) {
			return ResponseEntity.ok(eventService.isAvailable(eventNum));
		}

		// 4. 사용자 신청 내역 조회 (페이징)
		@GetMapping("/user/applied/page")
		public ResponseEntity<Page<EventUseDTO>> getUseListByMemberPaged(@RequestParam String memId, Pageable pageable) {

			return ResponseEntity.ok(eventService.getUseListByMemberPaged(memId, pageable));
		}

		// 5. 사용자 신청 취소
		@DeleteMapping("/cancel/{eventUseNo}")
		public ResponseEntity<Void> cancelEvent(@PathVariable Long eventUseNo) {
			eventService.cancelEvent(eventUseNo);
			return ResponseEntity.noContent().build();
		}

		// 6. 사용자 행사 목록 조회
		@GetMapping("/user/list")
		public ResponseEntity<Page<EventInfoDTO>> getUserEventList(@RequestParam(required = false) String option,
				@RequestParam(required = false) String query, @RequestParam(required = false) EventState status,
				Pageable pageable) {
			log.info("getUserEventList called with option: {}, query: {}, status: {}, pageable: {}", option, query,
					status, pageable);
			Page<EventInfoDTO> result = eventService.searchEventList(pageable, option, query, status);
			log.info("Returned {} programs. Total elements: {}", result.getContent().size(), result.getTotalElements());
			return ResponseEntity.ok(result);
		}

		@GetMapping("/admin/notEnd")
		public ResponseEntity<List<EventInfoDTO>> getUserProgramList() {
			List<EventInfoDTO> result = eventService.searchNotEndEventList();
			return ResponseEntity.ok(result);
		}
}
