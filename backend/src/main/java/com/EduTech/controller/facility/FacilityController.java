package com.EduTech.controller.facility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.facility.FacilityDetailDTO;
import com.EduTech.dto.facility.FacilityListDTO;
import com.EduTech.dto.facility.FacilityRegisterDTO;
import com.EduTech.dto.facility.FacilityReserveAdminDTO;
import com.EduTech.dto.facility.FacilityReserveApproveRequestDTO;
import com.EduTech.dto.facility.FacilityReserveListDTO;
import com.EduTech.dto.facility.FacilityReserveRequestDTO;
import com.EduTech.dto.facility.HolidayDayDTO;
import com.EduTech.dto.facility.ReservedBlockDTO;
import com.EduTech.dto.member.MemberDTO;
import com.EduTech.entity.facility.FacilityState;
import com.EduTech.service.facility.FacilityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/facility")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    // 시설 추가 (이미지 없어도 OK)(사용)
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerFacility(
        @RequestPart("dto") @Valid FacilityRegisterDTO dto,
        @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        facilityService.registerFacility(dto, images == null ? List.of() : images);
        return ResponseEntity.ok().build();
    }
    
    // 시설 조회(사용)
    @GetMapping("/list")
    public ResponseEntity<Page<FacilityListDTO>> list(
            @PageableDefault(page = 0, size = 12) Pageable pageable,
            @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<FacilityListDTO> res = facilityService.getFacilityList(pageable, keyword);
        return ResponseEntity.ok(res);
    }

    // 시설 상세 조회 (facRevNum 기준)(사용)
    @GetMapping("/facilityDetail")
    public ResponseEntity<FacilityDetailDTO> getFacilityDetail(@RequestParam("facRevNum") Long facRevNum) {
        return ResponseEntity.ok(facilityService.getFacilityDetail(facRevNum));
    }

    // 예약 신청(사용)
    @PostMapping("/reserve")
    public ResponseEntity<Map<String, String>> reserveFacility(
            @RequestBody @Valid FacilityReserveRequestDTO dto,
            @AuthenticationPrincipal(expression = "username") String memId
    ) {
        try {
            facilityService.reserveFacility(dto, memId);
            return ResponseEntity.ok(Map.of("message", "예약 신청이 완료되었습니다. (승인 대기)"));
        } catch (IllegalStateException | IllegalArgumentException e) {
            // 예: "해당 시설에서 오늘은 이미 예약하셨습니다. (하루 1회 제한)"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "예약 처리 중 오류가 발생했습니다."));
        }
    }
    
    // 예약 가능 시간 설정(현재 예약중인거 제외처리)
    @GetMapping("/facility/reserved")
    public List<ReservedBlockDTO> getReservedBlocks(
            @RequestParam("facRevNum") Long facRevNum,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return facilityService.getReservedBlocks(facRevNum, date);
    }

    // 예약 가능 여부 확인
    @GetMapping("/reservable")
    public ResponseEntity<Boolean> checkReservable(
            @RequestParam("facRevNum") Long facRevNum,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end
    ) {
        return ResponseEntity.ok(facilityService.isReservable(facRevNum, date, start, end));
    }

    // 사용자 예약 목록
    @GetMapping("/reservations")
    public ResponseEntity<Page<FacilityReserveListDTO>> getMyReservations(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @PageableDefault(sort = "reserveAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
            facilityService.getMyReservations(memberDTO.getMemId(), pageable)
        );
    }

    // 관리자 예약 목록 (상태/기간 필터)
    @GetMapping("/adminreservations")
    public ResponseEntity<List<FacilityReserveAdminDTO>> getReservationsForAdmin(
        @RequestParam(value = "state", required = false) FacilityState state,
        @RequestParam(value = "from",  required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(value = "to",    required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(facilityService.getReservationsForAdmin(state, from, to));
    }

    // 관리자: 예약 승인/거절 (reserveId 기준)
    @PostMapping("/approve")
    public ResponseEntity<Void> updateReservationState(
            @RequestBody @Valid FacilityReserveApproveRequestDTO dto) {

        boolean updated = facilityService.updateReservationState(dto);
        // 동시성으로 이미 다른 상태로 바뀐 경우
        if (!updated) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.ok().build();
    }

    // 사용자 예약 취소 (reserveId 기준)
    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelReservation(
            @RequestParam("reserveId") Long reserveId,
            @AuthenticationPrincipal(expression = "username") String memId
    ) {
        try {
            log.info("시설 예약 취소 요청: reserveId={}, 요청자 memId={}", reserveId, memId);
            facilityService.cancelReservation(reserveId, memId);
            return ResponseEntity.ok("예약이 정상적으로 취소되었습니다.");

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("해당 예약에 대한 취소 권한이 없습니다.");

        } catch (IllegalArgumentException e) { // 예약 없음 등
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("예약 내역이 존재하지 않습니다.");

        } catch (IllegalStateException e) { // 이미 시작/취소/처리 등
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (Exception e) {
            log.error("시설 예약 취소 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("예약 취소 중 오류가 발생했습니다.");
        }
    }


    // 관리자 강제 취소 (reserveId 기준)
    @PatchMapping("/admincancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminCancel(
            @RequestParam("reserveId") Long reserveId,
            @RequestParam(value = "requesterId", required = false) String requesterId
    ) {
        // 프론트에서 requesterId 안 보내면 토큰에서 가져오기
        if (requesterId == null || requesterId.isBlank()) {
            requesterId = com.EduTech.security.jwt.JWTFilter.getMemId();
        }

        boolean success = facilityService.cancelReservation(reserveId, requesterId);
        return ResponseEntity.ok(success ? "강제 취소 완료" : "취소 실패");
    }
    // 휴무일 등록
    @PostMapping("/addholiday")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> registerHoliday(@RequestBody @Valid HolidayDayDTO dto) {
        facilityService.registerHoliday(dto);
        return ResponseEntity.ok().build();
    }

    // 휴무일 삭제
    @DeleteMapping("/deleteHoliday")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHolidayByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        facilityService.deletePublicHolidayByDate(date);
        return ResponseEntity.ok().build();
    }

    // 휴무일 목록 조회 (시설 PK 기준)
    @GetMapping("/holidays")
    public ResponseEntity<List<HolidayDayDTO>> getHolidayDates(
            @RequestParam(value = "facRevNum", required = false) Long facRevNum) {
        return ResponseEntity.ok(facilityService.getHolidayDates(facRevNum));
    }


    // 특정 날짜 휴무 여부 (시설 PK 기준)
//    public ResponseEntity<Boolean> isHoliday(
//            @RequestParam(value = "facRevNum", required = false) Long facRevNum,
//            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        return ResponseEntity.ok(facilityService.isHoliday(facRevNum, date));
//    }
    
}
