package com.EduTech.service.Facility;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import com.EduTech.entity.facility.FacilityTime;
import com.EduTech.entity.facility.HolidayReason;
import com.EduTech.entity.member.Member;
import com.EduTech.repository.facility.FacilityHolidayRepository;
import com.EduTech.repository.facility.FacilityRepository;
import com.EduTech.repository.facility.FacilityReserveRepository;
import com.EduTech.repository.facility.FacilityTimeRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.facility.FacilityService;

@SpringBootTest
@Transactional
@Rollback(false)	// DB에 실제로 저장되도록 설정
class FacilityServiceTest {

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    FacilityTimeRepository facilityTimeRepository;

    @Autowired
    private FacilityReserveRepository reserveRepository;

    @Autowired
    private FacilityHolidayRepository holidayRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setupMember() {
        if (!memberRepository.existsById("user1")) {
            Member member = Member.builder()
                    .memId("user1")
                    .name("테스트회원")
                    .email("user1@test.com")
                    .build();
            memberRepository.save(member);
        }
    }
    
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
                .facItem("책상, 의자")
                .capacity(5)
                .build());

        LocalDate date = LocalDate.now().plusDays(1);

        // 예약 시간대 미리 등록
        FacilityTime time1 = FacilityTime.builder()
                .facility(facility)
                .facDate(date)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .available(true)
                .build();

        FacilityTime time2 = FacilityTime.builder()
                .facility(facility)
                .facDate(date)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(12, 0))
                .available(true)
                .build();

        facilityTimeRepository.save(time1);
        facilityTimeRepository.save(time2);

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
                .facItem("책상, 의자")
                .capacity(15)
                .build());

        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(11, 0);

        boolean result = facilityService.isReservable(facility.getFacilityNum(), date, start, end);
        assertThat(result).isTrue();

        System.out.println("[3] 테스트 완료\n");
    }

    @Test
    @DisplayName("[4] 예약 신청 처리 테스트")
    void testReserveFacility() {
        System.out.println("[4] 예약 신청 처리 테스트 시작");

        // [1] 사용자 선 저장 (연관관계용)
        Member member = Member.builder()
                .memId("user1")
                .name("테스트 사용자")
                .email("user1@example.com")
                .build();
        memberRepository.save(member);

        // [2] 시설 저장
        Facility facility = facilityRepository.save(Facility.builder()
                .facName("예약센터")
                .facInfo("예약정보")
                .capacity(20)
                .facItem("의자")
                .etc("없음")
                .member(member) // 시설 등록자도 설정하는 경우
                .build());

        // [3] 예약 DTO 생성
        FacilityReserveRequestDTO dto = FacilityReserveRequestDTO.builder()
                .facName("예약센터")
                .facDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .memId("user1")
                .build();

        // [4] 예약 신청
        facilityService.reserveFacility(dto);

        // [5] 예약 검증
        List<FacilityReserve> reserves = reserveRepository.findByMember_MemIdOrderByReserveAtDesc("user1");
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

        // [1] 관리자 회원 생성
        Member admin = Member.builder()
                .memId("admin")
                .name("관리자")
                .email("admin@test.com")
                .build();
        memberRepository.save(admin);

        // [2] 시설 생성
        Facility facility = facilityRepository.save(
                Facility.builder()
                        .facName("승인센터")
                        .facInfo("예약 테스트 시설")
                        .facItem("책상, 의자")
                        .capacity(10)
                        .member(admin) // 시설 등록자도 연관 적용 가능
                        .build()
        );

        // [3] 예약 생성
        FacilityReserve reserve = reserveRepository.save(
                FacilityReserve.builder()
                        .facility(facility)
                        .member(admin) // ✅ 변경
                        .facDate(LocalDate.now().plusDays(1))
                        .startTime(LocalTime.of(11, 0))
                        .endTime(LocalTime.of(12, 0))
                        .reserveAt(LocalDateTime.now())
                        .state(FacilityState.WAITING)
                        .build()
        );

        // [4] 승인 요청 DTO
        FacilityReserveApproveRequestDTO approveRequest = new FacilityReserveApproveRequestDTO();
        approveRequest.setFacRevNum(reserve.getFacRevNum());
        approveRequest.setState(FacilityState.APPROVED);

        // [5] 승인 처리
        boolean result = facilityService.updateReservationState(approveRequest);
        assertThat(result).isTrue();

        // [6] 상태 확인
        FacilityReserve updated = reserveRepository.findById(reserve.getFacRevNum()).orElseThrow();
        assertThat(updated.getState()).isEqualTo(FacilityState.APPROVED);

        System.out.println("[7] 테스트 완료\n");
    }

    //@Test
    @DisplayName("[8] 예약 취소 처리 테스트")
    void testCancelReserve() {
        System.out.println("[8] 예약 취소 테스트 시작");

        // [1] 회원 등록
        Member user3 = Member.builder()
                .memId("user3")
                .name("사용자3")
                .email("user3@test.com")
                .build();
        memberRepository.save(user3);

        // [2] 시설 등록
        Facility facility = facilityRepository.save(Facility.builder()
                .facName("취소센터")
                .facItem("책상, 의자")
                .capacity(5)
                .member(user3) // 시설 등록자 지정
                .build());

        // [3] 예약 등록
        FacilityReserve reserve = reserveRepository.save(FacilityReserve.builder()
                .facility(facility)
                .member(user3) // ✅ 변경된 부분
                .facDate(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .reserveAt(LocalDateTime.now())
                .state(FacilityState.WAITING)
                .build());

        // [4] 예약 취소 요청 (user3 본인 요청)
        boolean result = facilityService.cancelReservation(reserve.getFacRevNum(), "user3");
        assertThat(result).isTrue();

        // [5] 상태 확인
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
                .facItem("책상, 의자")
                .capacity(5)
                .build());

        LocalDate date = LocalDate.now().plusDays(5);

        // Step 2: 휴무일 등록
        FacilityHolidayDTO dto = new FacilityHolidayDTO();
        dto.setFacilityNum(facility.getFacilityNum());
        dto.setHolidayDate(date);
        dto.setReason(HolidayReason.HOLIDAY); 
        
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
                .facItem("책상, 의자")
                .capacity(5)
                .build());

        LocalDate date = LocalDate.now().plusDays(3);

        // Step 2. 휴무일 등록
        FacilityHolidayDTO dto = new FacilityHolidayDTO();
        dto.setFacilityNum(facility.getFacilityNum());
        dto.setHolidayDate(date);
        dto.setReason(HolidayReason.HOLIDAY); 
        facilityService.registerHoliday(dto);

        // Step 3. isHoliday 여부 확인
        boolean result = facilityService.isHoliday(facility.getFacilityNum(), date);
        assertThat(result).isTrue();

        System.out.println("[10] 테스트 완료\n");
    }
}
