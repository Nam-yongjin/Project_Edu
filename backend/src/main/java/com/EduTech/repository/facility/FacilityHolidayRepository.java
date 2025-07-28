package com.EduTech.repository.facility;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.facility.FacilityHoliday;

public interface FacilityHolidayRepository extends JpaRepository<FacilityHoliday, Long> {

    // 특정 시설의 모든 휴무일 조회.
    List<FacilityHoliday> findByFacility_FacilityNum(Long facilityNum);

    // 특정 시설 + 날짜가 휴무인지 여부.
    boolean existsByFacility_FacilityNumAndHolidayDate(Long facilityNum, LocalDate date);

    // 삭제 전 중복 체크용 (동일 날짜/시설 휴무 있는지 확인).
    boolean existsByFacility_FacilityNumAndHolidayDateAndIdNot(Long facilityNum, LocalDate date, Long excludeId);
}