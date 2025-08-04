	package com.EduTech.controller.event;
	
	import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventBannerDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.EventFile;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;
import com.EduTech.repository.event.EventFileRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.event.EventService;
import com.EduTech.util.FileUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
	
	@RestController
	@RequiredArgsConstructor
	@RequestMapping("/api/event")
	public class EventController {
		
		private static final Logger log = LoggerFactory.getLogger(EventController.class);
	
		private final EventService eventService;
		private final EventInfoRepository infoRepository;
		private final MemberRepository memberRepository;
		private final FileUtil fileUtil;
		private final ModelMapper modelMapper;
		
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
			// 2. 전체 행사 목록 조회 (신청 가능한것만)
//			@GetMapping("/eventList")
//			public ResponseEntity<List<EventInfoDTO>> getAllEvent() {
//				return ResponseEntity.ok(eventService.getAllEvents());
//			}
			
			//  2. 전체 행사 목록 조회 (신청 종료 기간 기준 필터링 없이 전체)
			@GetMapping("/List")
			public ResponseEntity<Page<EventInfoDTO>> getAllEventsWithoutFilter(
			        @RequestParam(name = "page", required = false, defaultValue = "1") int page) {

				Pageable pageable = PageRequest.of(page - 1, 8, Sort.by(Sort.Direction.DESC, "applyAt"));
			    Page<EventInfo> result = infoRepository.findAll(pageable);

			    Page<EventInfoDTO> dtoPage = result.map(info -> {
			        EventInfoDTO dto = modelMapper.map(info, EventInfoDTO.class);
			        dto.setMainImagePath(info.getMainImagePath());
			        dto.setFilePath(info.getFilePath());
			        dto.setOriginalName(info.getOriginalName());
			        return dto;
			    });

			    return ResponseEntity.ok(dtoPage);
			}
	
//			// 3. 페이지네이션 + 검색 조건 포함 목록 조회
//			@GetMapping("/admin/list")
//			public ResponseEntity<Page<EventInfoDTO>> getAdminProgramList(@RequestParam(required = false) String option,
//					@RequestParam(required = false) String query, @RequestParam(required = false) EventState status,
//					Pageable pageable) {
//	
//				Page<EventInfoDTO> result = eventService.searchAdminEventList(pageable, option, query, status);
//				return ResponseEntity.ok(result);
//			}
//	
			// 4. 행사 상세 조회
			@GetMapping("/eventDetail")
			public ResponseEntity<EventInfoDTO> getEvent(@RequestParam("eventNum") Long eventNum) {
			    return ResponseEntity.ok(eventService.getEvent(eventNum));
			}
			
			@RestController
			@RequestMapping("/event")
			@RequiredArgsConstructor
			public class EventFileController {

			    private final EventFileRepository eventFileRepository;
			    private final FileUtil fileUtil;

			    @GetMapping("/download/{fileId}")
			    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
			        EventFile file = eventFileRepository.findById(fileId)
			                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

			        return fileUtil.getFile(file.getFilePath(), null); // 썸네일 아님
			    }
			}
	
			// 5. 행사 등록(파일 업로드 포함)
			@PostMapping("/register")
			@PreAuthorize("hasRole('ADMIN')")
			public ResponseEntity<?> registerEvent(
			    @Valid @RequestPart("dto") EventInfoDTO dto,
			    @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
			    @RequestPart(value = "imageList", required = false) List<MultipartFile> imageList,
			    @RequestPart(value = "mainFile", required = false) MultipartFile mainFile,
			    @RequestPart(value = "attachList", required = false) List<MultipartFile> attachList,
			    BindingResult bindingResult
			) {
			    if (bindingResult.hasErrors()) {
			        return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
			    }

			    eventService.registerEvent(dto, mainImage, imageList, mainFile, attachList);
			    return ResponseEntity.ok("행사 등록 완료");
			}
	
			// 6. 수정(파일 업데이트 포함)
			@PutMapping("/update")
			@PreAuthorize("hasRole('ADMIN')")
			public ResponseEntity<?> updateEvent(
			    @RequestParam("eventNum") Long eventNum,
			    @Valid @RequestPart("dto") EventInfoDTO dto,
			    @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,  // ✅ 수정됨
			    @RequestPart(value = "imageList", required = false) List<MultipartFile> imageList,
			    @RequestPart(value = "mainFile", required = false) MultipartFile mainFile,
			    @RequestPart(value = "attachList", required = false) List<MultipartFile> attachList,
			    BindingResult bindingResult
			) {
			    if (bindingResult.hasErrors()) {
			        log.warn("❌ 유효성 검사 실패: {}", bindingResult.getAllErrors());
			        return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
			    }

			    eventService.updateEvent(eventNum, dto, mainImage, imageList, mainFile, attachList);
			    return ResponseEntity.ok("✅ 행사 수정이 완료되었습니다.");
			}
	
			// 7. 삭제
			@DeleteMapping("/delete")
			@PreAuthorize("hasRole('ADMIN')")
			public ResponseEntity<Void> deleteEvent(@RequestParam("eventNum") Long eventNum) {
			    try {
			        log.info("삭제 요청: eventNum={}", eventNum);
			        eventService.deleteEvent(eventNum);
			        return ResponseEntity.noContent().build();
			    } catch (Exception e) {
			        log.error("삭제 실패: {}", e.getMessage(), e);
			        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			    }
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
			//------------------------
			
			//@PostMapping("/test-form")
			public ResponseEntity<String> testForm(@RequestParam("eventName") String eventName) {
			    log.info("eventName: {}", eventName);
			    return ResponseEntity.ok("OK");
			}
			
			//@PostMapping("/test-form")
			public ResponseEntity<String> testFormUpload(
			        @ModelAttribute EventInfoDTO dto,
			        @RequestParam(value = "imageList", required = false) List<MultipartFile> imageList,
			        @RequestParam(value = "attachList", required = false) List<MultipartFile> attachList
			) {
			    log.info("테스트용 컨트롤러 호출됨");

			    log.info("▶️ eventName: {}", dto.getEventName());
			    log.info("▶️ eventInfo: {}", dto.getEventInfo());
			    log.info("▶️ applyStartPeriod: {}", dto.getApplyStartPeriod());
			    log.info("▶️ applyEndPeriod: {}", dto.getApplyEndPeriod());
			    log.info("▶️ eventStartPeriod: {}", dto.getEventStartPeriod());
			    log.info("▶️ eventEndPeriod: {}", dto.getEventEndPeriod());
			    log.info("▶️ category: {}", dto.getCategory());
			    log.info("▶️ maxCapacity: {}", dto.getMaxCapacity());
			    log.info("▶️ place: {}", dto.getPlace());
			    log.info("▶️ daysOfWeek: {}", dto.getDaysOfWeek());

			    if (imageList != null) {
			        log.info("imageList count: {}", imageList.size());
			        imageList.forEach(file -> log.info("🖼️ 이미지 파일명: {}", file.getOriginalFilename()));
			    } else {
			        log.info("imageList: null");
			    }

			    if (attachList != null) {
			        log.info("📎 attachList count: {}", attachList.size());
			        attachList.forEach(file -> log.info("📎 첨부파일명: {}", file.getOriginalFilename()));
			    } else {
			        log.info("📎 attachList: null");
			    }

			    return ResponseEntity.ok("테스트 완료");
			}
	}
