package com.EduTech.repository.facility;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.facility.FacilityTime;

public interface FacilityTimeRepository extends JpaRepository<FacilityTime, Long> {

    // 시설의 모든 운영 타임슬롯
    List<FacilityTime> findByFacility_FacRevNum(Long facRevNum);

    // 특정 요일(1~7)의 운영 타임슬롯
    List<FacilityTime> findByFacility_FacRevNumAndDayOfWeek(Long facRevNum, Integer dayOfWeek);
}
