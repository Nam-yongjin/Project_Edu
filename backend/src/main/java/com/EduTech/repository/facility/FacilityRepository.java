package com.EduTech.repository.facility;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.facility.Facility;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    // 시설 이름으로 상세 조회.
    Optional<Facility> findByFacName(String facName);

    // 시설 + 이미지 fetch join 조회 (옵션).
    @Query("SELECT f FROM Facility f LEFT JOIN FETCH f.images WHERE f.facName = :facName")
    Optional<Facility> findWithImagesByFacName(@Param("facName") String facName);
    
    // 시설 이름 존재 여부 확인 (중복 방지용 등)
    boolean existsByFacName(String facName);

}