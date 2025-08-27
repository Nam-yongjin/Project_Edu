package com.EduTech.controller.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventSearchRequestDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.dto.member.MemberDTO;
import com.EduTech.entity.event.EventInfo;
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

    // ─────────────────────────────────────────────
    // 관리자용 API
    // ─────────────────────────────────────────────

    // 1. 이달의 배너 (진행 중인 행사) (완)
    @GetMapping("/banner")
    public ResponseEntity<List<EventInfoDTO>> getbannerEvent() {
        List<EventInfoDTO> events = eventService.getbannerEvent();
        return ResponseEntity.ok(events);
    }

    // 2. 전체 행사 목록 조회 (필터링 없이 전체)(완)
    @GetMapping("/list")
    public ResponseEntity<Page<EventInfoDTO>> getAllEventsWithoutFilter(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page
    ) {
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

    // 3. 행사 검색 (조건 필터링)
    @GetMapping("/search")
    public ResponseEntity<Page<EventInfoDTO>> searchEvents(
            @ModelAttribute EventSearchRequestDTO dto,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        Page<EventInfoDTO> result = eventService.searchEventList(dto, page);
        return ResponseEntity.ok(result);
    }

    // 4. 행사 상세 조회 (완)
    @GetMapping("/eventDetail")
    public ResponseEntity<EventInfoDTO> getEvent(@RequestParam("eventNum") Long eventNum) {
        return ResponseEntity.ok(eventService.getEvent(eventNum));
    }

    // 5. 행사 등록 (완)
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

    // 6. 행사 수정 (완)
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEvent(
            @RequestParam("eventNum") Long eventNum,
            @Valid @RequestPart("dto") EventInfoDTO dto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
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
        return ResponseEntity.ok("행사 수정이 완료되었습니다.");
    }

    // 7. 행사 삭제 (취소 처리) (완)
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEvent(@RequestParam("eventNum") Long eventNum) {
        try {
            log.info("행사 취소 요청: eventNum={}", eventNum);
            eventService.deleteEvent(eventNum);
            return ResponseEntity.ok("행사가 정상적으로 취소되었습니다.");
        } catch (Exception e) {
            log.error("행사 취소 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("행사 취소 중 오류가 발생했습니다.");
        }
    }

    // ─────────────────────────────────────────────
    // 사용자 기능
    // ─────────────────────────────────────────────

    // 1. 행사 신청 (완)
    @PostMapping("/apply")
    public ResponseEntity<Map<String, String>> applyEvent(@RequestBody EventApplyRequestDTO dto) {
        Map<String, String> response = new HashMap<>();
        try {
            eventService.applyEvent(dto);
            response.put("message", "신청이 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

//    // 2. 신청 여부 확인
//    @GetMapping("/applied")
//    public ResponseEntity<Boolean> isAlreadyApplied(
//            @RequestParam(name = "eventNum") Long eventNum,
//            @RequestParam(name = "memId") String memId
//    ) {
//        return ResponseEntity.ok(eventService.isAlreadyApplied(eventNum, memId));
//    }

    // 3. 사용자 신청 내역 조회 (페이징) (완)
    @GetMapping("/reservation")
    public ResponseEntity<Page<EventUseDTO>> getUseListByMemberPaged(
            @AuthenticationPrincipal MemberDTO memberDTO,
            Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getUseListByMemberPaged(memberDTO.getMemId(), pageable));
    }

    // 4. 사용자 신청 취소(완)
    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelEvent(
            @RequestParam("evtRevNum") Long evtRevNum,
            @AuthenticationPrincipal(expression = "username") String memId
    ) {
        try {
            log.info("예약 취소 요청: evtRevNum={}, 요청자 memId={}", evtRevNum, memId);
            eventService.cancelEvent(evtRevNum, memId);
            return ResponseEntity.ok("예약이 정상적으로 취소되었습니다.");

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("해당 예약에 대한 취소 권한이 없습니다.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("예약 내역이 존재하지 않습니다.");

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage()); // 예: "행사 시작 이후에는 취소할 수 없습니다."

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("예약 취소 중 오류가 발생했습니다.");
        }
    }
    
}
