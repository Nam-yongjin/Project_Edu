package com.EduTech.repository.facility;

import com.EduTech.entity.facility.FacilityImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityImageRepository extends JpaRepository<FacilityImage, Long> {

    // 특정 시설에 연결된 이미지 리스트
    List<FacilityImage> findByFacility_FacilityNum(Long facilityNum);
}