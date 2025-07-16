package com.EduTech.repository.demonstration;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationSelectDTO;
import com.EduTech.entity.demonstration.Demonstration;

public interface DemonstrationRepository extends JpaRepository<Demonstration, Long>{ // 실증 상품 관련 레포지토리
		
		@Modifying
		@Transactional
		@Query("DELETE FROM demonstration WHERE demNum=:demNum")
		void deleteProductDem(long demNum); // 실증 신청 삭제 
		
		@Modifying
		@Transactional
		@Query("DELETE FROM demonstration WHERE demNum IN:demNum")
		void deleteProductsDem(List<Long> demNum); // 실증 신청 삭제(다수)
		
		@Query("SELECT demNum,demName, demMfr,itemNum FROM demonstration")
		Page<DemonstrationSelectDTO> selectPageDem(Pageable pageable); // 실증 상품들을 페이지 별로 가져오는 쿼리문 (실증 장비 신청 목록 페이지) 
		
		@Query("SELECT d.demNum,d.demName,d.demMfr,d.itemNum,reg.expDate FROM demonstration d, demonstration_registration reg WHERE d.demNum=reg.demNum")
		Page<DemonstrationSelectDTO> selectPageDetailDem(Pageable pageable); // 실증 상품들을 페이지 별로 가져오는 쿼리문 (실증 장비 신청 상세 페이지) 
		
		@Query("SELECT d.demNum,d.demName,d.demMfr,d.itemNum,res.startDate, res.endDate, res.applyAt FROM demonstration d, demonstration_reserve res WHERE d.demNum=res.demNum")
		Page<DemonstrationSelectDTO> selectPageViewDem(Pageable pageable); // 대여한 실증 상품들을 페이지 별로 가져오는 쿼리문 (물품 대여 조회 페이지) 
		
		 @Modifying // JPQL에서 업데이트하는 방식 (기업의 실증 등록내용을 수정시켜주는 쿼리문)
		    @Query("UPDATE Demonstration d SET d.demName = :demName, d.demMfr = :demMfr, d.itemNum = :itemNum, d.demInfo = :demInfo WHERE d.demNum = :demNum")
		    int updateDem(
		         String demName,
		         String demMfr,
		         Long itemNum,
		         String demInfo,
		         Long demNum
		    );
		
		// 검색 같은 부분은 백단에서 구현함.
}

