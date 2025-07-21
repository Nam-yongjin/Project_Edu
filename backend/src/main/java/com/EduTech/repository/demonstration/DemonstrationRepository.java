package com.EduTech.repository.demonstration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationListDTO;
import com.EduTech.entity.demonstration.Demonstration;

public interface DemonstrationRepository extends JpaRepository<Demonstration, Long> { // 실증 상품 관련 레포지토리
	// 실증 상품들을 페이지 별로 가져오는 쿼리문 (실증 장비 신청 목록 페이지)
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationListDTO(demNum, demName, demMfr, itemNum) FROM Demonstration")
	Page<DemonstrationListDTO> selectPageDem(Pageable pageable);

	// 실증 상품들을 페이지 별로 가져오는 쿼리문 (실증 장비 신청 상세 페이지)
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationListDTO(d.demNum, d.demName, d.demInfo, d.itemNum, reg.expDate) FROM Demonstration d, DemonstrationRegistration reg WHERE d.demNum = reg.demonstration.demNum")
	Page<DemonstrationListDTO> selectPageDetailDem(Pageable pageable);

	// 대여한 실증 상품들을 페이지 별로 가져오는 쿼리문 (물품 대여 조회 페이지)
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationListDTO(d.demNum, d.demName, c.companyName, d.itemNum, res.startDate, res.endDate, res.applyAt) FROM Demonstration d, DemonstrationReserve res, Company c WHERE d.demNum = res.demonstration.demNum AND res.member.memId=c.memId AND res.member.memId=:memId")
	Page<DemonstrationListDTO> selectPageViewDem(Pageable pageable,@Param("memId") String memId);

	// 대여한 실증 상품들을 페이지 별로 가져오는 쿼리문 (물품 대여 조회 페이지, 기업명 검색기능 추가)
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationListDTO(d.demNum, d.demName, c.companyName, d.itemNum, res.startDate, res.endDate, res.applyAt) FROM Demonstration d, DemonstrationReserve res, Company c WHERE d.demNum = res.demonstration.demNum AND res.member.memId=c.memId AND res.member.memId=:memId And c.companyName LIKE %:search%")
	Page<DemonstrationListDTO> selectPageViewDemSearch(Pageable pageable,@Param("memId") String memId, @Param("search") String search);

	@Modifying // JPQL에서 업데이트하는 방식 (기업의 실증 등록내용을 수정시켜주는 쿼리문)
	@Transactional
	@Query("UPDATE Demonstration d SET d.demName = :demName, d.demMfr = :demMfr, d.itemNum = :itemNum, d.demInfo = :demInfo WHERE d.demNum = :demNum")
	int updateDem(@Param("demName") String demName, @Param("demMfr") String demMfr, @Param("itemNum") Long itemNum,
			@Param("demInfo") String demInfo, @Param("demNum") Long demNum);

	// 검색 같은 부분은 백단에서 구현함.
}
