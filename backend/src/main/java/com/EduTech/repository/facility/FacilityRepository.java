package com.EduTech.repository.facility;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.facility.Facility;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    // PK(= facRevNum) 단건 조회(기본 제공: findById) - 필요 시 명시 메서드도 제공
    Optional<Facility> findByFacRevNum(Long facRevNum);

    // PK 기반 fetch join (상세 조회용 권장)
    @Query("""
           SELECT f FROM Facility f
           LEFT JOIN FETCH f.images
           WHERE f.facRevNum = :facRevNum
           """)
    Optional<Facility> findWithImagesById(@Param("facRevNum") Long facRevNum);

    // (선택) 이름 중복 체크는 등록 검증용으로 계속 유지
    boolean existsByFacName(String facName);

    // 목록 조회(페이징) - 이미지 로딩 최적화
    @EntityGraph(attributePaths = "images")
    @Query("select f from Facility f")
    Page<Facility> findPageWithImages(Pageable pageable);

    // 검색 + 페이징 (이름/소개 키워드)
    @EntityGraph(attributePaths = "images")
    @Query("""
        select f from Facility f
        where (:keyword is null or :keyword = '' 
               or lower(f.facName) like lower(concat('%', :keyword, '%'))
               or lower(f.facInfo) like lower(concat('%', :keyword, '%')))
        """)
    Page<Facility> searchPageWithImages(@Param("keyword") String keyword, Pageable pageable);
}
