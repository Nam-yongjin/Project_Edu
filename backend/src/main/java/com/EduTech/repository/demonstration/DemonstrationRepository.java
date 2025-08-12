package com.EduTech.repository.demonstration;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationDetailDTO;
import com.EduTech.dto.demonstration.DemonstrationPageListDTO;
import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.demonstration.DemonstrationState;

public interface DemonstrationRepository extends JpaRepository<Demonstration, Long> { // 실증 상품 관련 레포지토리
	// 실증 상품들을 페이지 별로 가져오는 쿼리문 (실증 장비 신청 목록 페이지) 검색어 없을때,
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationPageListDTO(d.demNum, d.demName, d.demMfr, d.itemNum,reg.state) FROM Demonstration d,DemonstrationRegistration reg WHERE d.demNum=reg.demonstration.demNum AND reg.state=:state")
	Page<DemonstrationPageListDTO> selectPageDem(Pageable pageable,@Param("state")DemonstrationState state);
	
	// 실증 상품들을 페이지 별로 가져오는 쿼리문 (실증 장비 신청 목록 페이지) 상품명 기준,
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationPageListDTO(demNum, demName, demMfr, itemNum,reg.state) FROM Demonstration d,DemonstrationRegistration reg WHERE demName LIKE %:search% AND d.demNum=reg.demonstration.demNum AND reg.state=:state")
	Page<DemonstrationPageListDTO> selectPageDemName(Pageable pageable, @Param("search") String search,@Param("state")DemonstrationState state);

	// 실증 상품들을 페이지 별로 가져오는 쿼리문 (실증 장비 신청 목록 페이지) 제조사명 기준
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationPageListDTO(demNum, demName, demMfr, itemNum,reg.state) FROM Demonstration d,DemonstrationRegistration reg WHERE demMfr LIKE %:search% AND d.demNum=reg.demonstration.demNum AND reg.state=:state")
	Page<DemonstrationPageListDTO> selectPageDemMfr(Pageable pageable, @Param("search") String search,@Param("state")DemonstrationState state);

	// 실증 상품들을 페이지 별로 가져오는 쿼리문 (실증 장비 신청 상세 페이지)
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationDetailDTO(d.demNum, d.demName, d.demInfo,d.demMfr, d.itemNum, reg.expDate) FROM Demonstration d, DemonstrationRegistration reg WHERE d.demNum = reg.demonstration.demNum AND d.demNum=:demNum")
	DemonstrationDetailDTO selectPageDetailDem(@Param("demNum") Long demNum);

	@Modifying // JPQL에서 업데이트하는 방식 (기업의 실증 등록내용을 수정시켜주는 쿼리문)
	@Transactional
	@Query("UPDATE Demonstration d SET d.demName = :demName, d.demMfr = :demMfr, d.itemNum = :itemNum, d.demInfo = :demInfo WHERE d.demNum = :demNum")
	int updateDem(@Param("demName") String demName, @Param("demMfr") String demMfr, @Param("itemNum") Long itemNum,
			@Param("demInfo") String demInfo, @Param("demNum") Long demNum);

	@Modifying // JPQL에서 업데이트하는 방식 (실증 물품 갯수 업데이트 하는 쿼리문)
	@Transactional
	@Query("UPDATE Demonstration d SET d.itemNum = :itemNum WHERE d.demNum = :demNum")
	int updateItemNum(@Param("itemNum") Long itemNum, @Param("demNum") Long demNum);
	
	// 상품 번호를 받아와 해당 상품의 갯수를 가져오는 쿼리문
	@Query("SELECT itemNum from Demonstration WHERE demNum= :demNum")
	Long selectItemNum(@Param("demNum") Long demNum);
	
	// 스케줄러에서 state가 cancel인 값들에 대해 상품 번호를 받아와 일괄 삭제시키는 쿼리문
	@Modifying
	@Transactional 
	@Query("DELETE FROM Demonstration WHERE demNum IN:demNum")
	void deleteDems(@Param("demNum") List<Long> demNum); 
}
