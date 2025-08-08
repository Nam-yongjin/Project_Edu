package com.EduTech.controller.facility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.facility.FacilityDetailDTO;
import com.EduTech.dto.facility.FacilityHolidayDTO;
import com.EduTech.dto.facility.FacilityRegisterDTO;
import com.EduTech.dto.facility.FacilityReserveAdminDTO;
import com.EduTech.dto.facility.FacilityReserveApproveRequestDTO;
import com.EduTech.dto.facility.FacilityReserveListDTO;
import com.EduTech.dto.facility.FacilityReserveRequestDTO;
import com.EduTech.dto.facility.FacilityTimeDTO;
import com.EduTech.entity.facility.FacilityState;
import com.EduTech.service.facility.FacilityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/facility")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    // 시설 추가 (이미지 없어도 OK)
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerFacility(
        @RequestPart("dto") @Valid FacilityRegisterDTO dto,
        @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        facilityService.registerFacility(dto, images == null ? List.of() : images);
        return ResponseEntity.ok().build();
    }

    // 시설 상세 조회 (facName 기준)
    @GetMapping("/detail/{facName}")
    public ResponseEntity<FacilityDetailDTO> getFacilityDetail(@PathVariable String facName) {
        return ResponseEntity.ok(facilityService.getFacilityDetail(facName));
    }

    // 특정 날짜의 예약 가능 시간 조회 (시설 PK: facRevNum)
    @GetMapping("/times")
    public ResponseEntity<List<FacilityTimeDTO>> getAvailableTimes(
            @RequestParam("facRevNum") Long facRevNum,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(facilityService.getAvailableTimes(facRevNum, date));
    }

    // 예약 신청
    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveFacility(@RequestBody @Valid FacilityReserveRequestDTO dto) {
        facilityService.reserveFacility(dto);
        return ResponseEntity.ok().build();
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

    // 사용자 예약 목록 (JWT 도입 전 임시로 memId 파라미터)
    @GetMapping("/my-reservations")
    public ResponseEntity<List<FacilityReserveListDTO>> getMyReservations(@RequestParam String memId) {
        return ResponseEntity.ok(facilityService.getMyReservations(memId));
    }

    // 관리자 예약 목록 (상태/기간 필터)
    @GetMapping("/admin/reservations")
    public ResponseEntity<List<FacilityReserveAdminDTO>> getReservationsForAdmin(
            @RequestParam(required = false) FacilityState state,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(facilityService.getReservationsForAdmin(state, from, to));
    }

    // 관리자: 예약 승인/거절 (reserveId 기준)
    @PostMapping("/admin/approve")
    public ResponseEntity<Void> updateReservationState(@RequestBody @Valid FacilityReserveApproveRequestDTO dto) {
        facilityService.updateReservationState(dto);
        return ResponseEntity.ok().build();
    }

    // 사용자 예약 취소 (reserveId 기준)
    @PatchMapping("/my/reservation/{reserveId}/cancel")
    public ResponseEntity<String> userCancel(
            @PathVariable Long reserveId,
            @RequestParam String requesterId
    ) {
        boolean success = facilityService.cancelReservation(reserveId, requesterId);
        return ResponseEntity.ok(success ? "취소 완료" : "취소 실패");
    }

    // 관리자 강제 취소 (reserveId 기준)
    @PatchMapping("/admin/reservation/{reserveId}/cancel")
    public ResponseEntity<String> adminCancel(
            @PathVariable Long reserveId,
            @RequestParam String requesterId
    ) {
        boolean success = facilityService.cancelReservation(reserveId, requesterId);
        return ResponseEntity.ok(success ? "강제 취소 완료" : "취소 실패");
    }

    // 휴무일 등록 (dto.facRevNum 사용)
    @PostMapping("/admin/holiday")
    public ResponseEntity<Void> registerHoliday(@RequestBody @Valid FacilityHolidayDTO dto) {
        facilityService.registerHoliday(dto);
        return ResponseEntity.ok().build();
    }

    // 휴무일 삭제
    @DeleteMapping("/admin/holiday/{holidayId}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long holidayId) {
        facilityService.deleteHoliday(holidayId);
        return ResponseEntity.ok().build();
    }

    // 휴무일 목록 조회 (시설 PK 기준)
    @GetMapping("/holidays")
    public ResponseEntity<List<LocalDate>> getHolidayDates(@RequestParam("facRevNum") Long facRevNum) {
        return ResponseEntity.ok(facilityService.getHolidayDates(facRevNum));
    }

    // 특정 날짜 휴무 여부 (시설 PK 기준)
    @GetMapping("/holiday/check")
    public ResponseEntity<Boolean> isHoliday(
            @RequestParam("facRevNum") Long facRevNum,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(facilityService.isHoliday(facRevNum, date));
    }
}
