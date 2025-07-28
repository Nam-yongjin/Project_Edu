package com.EduTech.service.Facility;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.facility.FacilityDetailDTO;
import com.EduTech.dto.facility.FacilityHolidayDTO;
import com.EduTech.dto.facility.FacilityReserveAdminDTO;
import com.EduTech.dto.facility.FacilityReserveApproveRequestDTO;
import com.EduTech.dto.facility.FacilityReserveListDTO;
import com.EduTech.dto.facility.FacilityReserveRequestDTO;
import com.EduTech.dto.facility.FacilityTimeDTO;
import com.EduTech.entity.facility.Facility;
import com.EduTech.entity.facility.FacilityHoliday;
import com.EduTech.entity.facility.FacilityImage;
import com.EduTech.entity.facility.FacilityReserve;
import com.EduTech.entity.facility.FacilityState;
import com.EduTech.repository.facility.FacilityHolidayRepository;
import com.EduTech.repository.facility.FacilityRepository;
import com.EduTech.repository.facility.FacilityReserveRepository;
import com.EduTech.service.facility.FacilityService;

@SpringBootTest
@ComponentScan(basePackages = "com.EduTech", excludeFilters = {
	    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.EduTech\\.service\\.mail\\..*")
	})
@Transactional
@Rollback(false)	// DB에 실제로 저장되도록 설정
class FacilityServiceTest {

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityReserveRepository reserveRepository;

    @Autowired
    private FacilityHolidayRepository holidayRepository;

    @Test
    @DisplayName("[1] 시설 상세 정보 조회 테스트 (이미지 포함)")
    void testGetFacilityWithImages() {
        System.out.println("[1] 시설 상세 정보 조회 테스트 시작");

        Facility facility = Facility.builder()
                .facName("테스트시설")
                .facInfo("테스트정보")
                .facItem("항목")
                .capacity(10)
                .etc("주의사항")
                .build();
        facility.addImage(FacilityImage.builder().imageName("img1.jpg").imageUrl("/img/img1.jpg").build());
        facilityRepository.save(facility);

        FacilityDetailDTO dto = facilityService.getFacilityDetail(facility.getFacName());
        assertThat(dto.getImages()).hasSize(1);

        System.out.println("[1] 테스트 완료\n");
    }

    //@Test
    @DisplayName("[2] 예약 가능 시간대 조회 테스트")
    void testGetAvailableTimes() {
        System.out.println("[2] 예약 가능 시간대 조회 테스트 시작");

        // given
        Facility facility = facilityRepository.save(Facility.builder()
                .facName("시간조회시설")
                .facInfo("시간조회테스트")
                .capacity(5)
                .build());

        LocalDate date = LocalDate.now().plusDays(1);

        // when
        List<FacilityTimeDTO> times = facilityService.getAvailableTimes(facility.getFacilityNum(), date);

        // then
        assertThat(times).isNotEmpty();
        System.out.println("예약 가능 시간대 수: " + times.size());
        System.out.println("[2] 테스트 완료\n");
    }

    //@Test
    @DisplayName("[3] 예약 가능 여부 판단 테스트")
    void testIsReservable() {
        System.out.println("[3] 예약 가능 여부 판단 테스트 시작");

        Facility facility = facilityRepository.save(Facility.builder()
                .facName("가능여부시설")
                .facInfo("예약 가능 여부 확인")
                .capacity(15)
                .build());

        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(11, 0);

        boolean result = facilityService.isReservable(facility.getFacilityNum(), date, start, end);
        assertThat(result).isTrue();

        System.out.println("[3] 테스트 완료\n");
    }

    //@Test
    @DisplayName("[4] 예약 신청 처리 테스트")
    void testReserveFacility() {
        System.out.println("[4] 예약 신청 처리 테스트 시작");

        // 시설 저장
        Facility facility = facilityRepository.save(Facility.builder()
                .facName("예약센터")
                .facInfo("예약정보")
                .capacity(20)
                .facItem("의자")
                .etc("없음")
                .build());

        // 예약 DTO 생성
        FacilityReserveRequestDTO dto = FacilityReserveRequestDTO.builder()
                .facName("예약센터")
                .facDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .memId("user1")
                .build();

        // 예약 신청
        facilityService.reserveFacility(dto); // <- 여기 수정됨

        // 예약 검증
        List<FacilityReserve> reserves = reserveRepository.findByMemIdOrderByReserveAtDesc("user1");
        assertThat(reserves).isNotEmpty();

        System.out.println("[4] 테스트 완료\n");
    }

    //@Test
    @DisplayName("[5] 내 예약 목록 조회 테스트")
    void testGetUserReserveList() {
        System.out.println("[5] 사용자 예약 목록 조회 테스트 시작");

        // 예약 정보가 있는 회원의 memId 사용
        String memId = "user1";

        List<FacilityReserveListDTO> list = facilityService.getMyReservations(memId);
        assertThat(list).isNotNull();
        System.out.println("예약 건수: " + list.size());

        System.out.println("[5] 테스트 완료\n");
    }

    //@Test
    @DisplayName("[6] 관리자 예약 목록 조회 테스트")
    void testAdminReserveList() {
        System.out.println("[6] 관리자 예약 목록 조회 테스트 시작");

        FacilityState state = null; // 모든 상태 조회 시 null
        LocalDate from = null;      // 기간 필터 없음
        LocalDate to = null;

        List<FacilityReserveAdminDTO> list = facilityService.getReservationsForAdmin(state, from, to);
        assertThat(list).isNotNull();
        System.out.println("관리자 예약 목록 수: " + list.size());

        System.out.println("[6] 테스트 완료\n");
    }

    //@Test
    @DisplayName("[7] 예약 승인/거절 처리 테스트")
    void testApproveOrReject() {
        System.out.println("[7] 예약 승인/거절 처리 테스트 시작");

        // Step 1. 시설 및 예약 저장
        Facility facility = facilityRepository.save(
                Facility.builder().facName("승인센터").build()
        );

        FacilityReserve reserve = reserveRepository.save(
                FacilityReserve.builder()
                        .facility(facility)
                        .memId("admin") // 필수 필드
                        .facDate(LocalDate.now().plusDays(1))
                        .startTime(LocalTime.of(11, 0))
                        .endTime(LocalTime.of(12, 0))
                        .reserveAt(LocalDateTime.now())
                        .state(FacilityState.WAITING)
                        .build()
        );

        // Step 2. 승인 요청 DTO 생성
        FacilityReserveApproveRequestDTO approveRequest = new FacilityReserveApproveRequestDTO();
        approveRequest.setFacRevNum(reserve.getFacRevNum());
        approveRequest.setState(FacilityState.APPROVED);

        // Step 3. 승인 처리 메서드 호출
        boolean result = facilityService.updateReservationState(approveRequest);
        assertThat(result).isTrue();

        // Step 4. 상태 확인
        FacilityReserve updated = reserveRepository.findById(reserve.getFacRevNum()).orElseThrow();
        assertThat(updated.getState()).isEqualTo(FacilityState.APPROVED);

        System.out.println("[7] 테스트 완료\n");
    }

    //@Test
    @DisplayName("[8] 예약 취소 처리 테스트")
    void testCancelReserve() {
        System.out.println("[8] 예약 취소 테스트 시작");

        // Step 1. 시설 저장
        Facility facility = facilityRepository.save(Facility.builder().facName("취소센터").build());

        // Step 2. 예약 저장 (memId는 String)
        FacilityReserve reserve = reserveRepository.save(FacilityReserve.builder()
                .facility(facility)
                .memId("user3")
                .facDate(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .reserveAt(LocalDateTime.now())
                .state(FacilityState.WAITING)
                .build());

        // Step 3. 예약 취소 요청 (일반 사용자 시나리오)
        boolean result = facilityService.cancelReservation(reserve.getFacRevNum(), false, "user3");
        assertThat(result).isTrue();

        // Step 4. 상태 확인
        FacilityReserve found = reserveRepository.findById(reserve.getFacRevNum()).orElseThrow();
        assertThat(found.getState()).isEqualTo(FacilityState.CANCELLED);

        System.out.println("[8] 테스트 완료\n");
    }

    //@Test
    @DisplayName("[9] 휴무일 등록/조회/삭제 테스트 (시설 기준)")
    void testHolidayCRUD() {
        System.out.println("[9] 휴무일 등록/조회/삭제 테스트 시작");

        // Step 1: 시설 생성
        Facility facility = facilityRepository.save(Facility.builder()
                .facName("휴무테스트시설")
                .facInfo("휴무정보")
                .capacity(5)
                .build());

        LocalDate date = LocalDate.now().plusDays(5);

        // Step 2: 휴무일 등록
        FacilityHolidayDTO dto = new FacilityHolidayDTO();
        dto.setFacilityNum(facility.getFacilityNum());
        dto.setHolidayDate(date);

        facilityService.registerHoliday(dto);

        // Step 3: 해당 시설의 휴무일 조회
        List<LocalDate> holidayDates = facilityService.getHolidayDates(facility.getFacilityNum());
        assertThat(holidayDates).contains(date);

        // Step 4: 휴무일 삭제 (삭제하려면 휴무 ID를 직접 조회)
        FacilityHoliday savedHoliday = holidayRepository
                .findByFacility_FacilityNumAndHolidayDate(facility.getFacilityNum(), date);

        facilityService.deleteHoliday(savedHoliday.getHolidayId());

        // Step 5: 삭제 확인
        boolean stillExists = holidayRepository.existsByFacility_FacilityNumAndHolidayDate(
                facility.getFacilityNum(), date);
        assertThat(stillExists).isFalse();

        System.out.println("[9] 테스트 완료\n");
    }

    //@Test
    @DisplayName("[10] 휴무일 여부 확인 테스트")
    void testIsHoliday() {
        System.out.println("[10] 휴무일 여부 확인 테스트 시작");

        // Step 1. 시설 저장
        Facility facility = facilityRepository.save(Facility.builder()
                .facName("휴무확인시설")
                .facInfo("테스트용 시설")
                .capacity(5)
                .build());

        LocalDate date = LocalDate.now().plusDays(3);

        // Step 2. 휴무일 등록
        FacilityHolidayDTO dto = new FacilityHolidayDTO();
        dto.setFacilityNum(facility.getFacilityNum());
        dto.setHolidayDate(date);
        facilityService.registerHoliday(dto);

        // Step 3. isHoliday 여부 확인
        boolean result = facilityService.isHoliday(facility.getFacilityNum(), date);
        assertThat(result).isTrue();

        System.out.println("[10] 테스트 완료\n");
    }
}
