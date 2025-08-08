package com.EduTech.repository.facility;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.facility.Facility;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    // 시설 이름으로 상세 조회
    Optional<Facility> findByFacName(String facName);

    // 이름 기반 fetch join
    @Query("""
           SELECT f FROM Facility f
           LEFT JOIN FETCH f.images
           WHERE f.facName = :facName
           """)
    Optional<Facility> findWithImagesByFacName(@Param("facName") String facName);

    // PK 기반 fetch join (선택)
    @Query("""
           SELECT f FROM Facility f
           LEFT JOIN FETCH f.images
           WHERE f.facRevNum = :facRevNum
           """)
    Optional<Facility> findWithImagesById(@Param("facRevNum") Long facRevNum);

    // 이름 중복 체크
    boolean existsByFacName(String facName);
}
