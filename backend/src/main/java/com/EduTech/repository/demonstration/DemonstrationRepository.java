package com.EduTech.repository.demonstration;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationSelectRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationSelectReserveDTO;
import com.EduTech.entity.demonstration.Demonstration;

public interface DemonstrationRepository extends JpaRepository<Demonstration, Long>{
		
		 
		@Transactional // 트랜잭션 처리 (실행중 오류가 발생했을때, rollback 처리를 하여 db 무결성을 해치지 않도록 함.)
		@Query("DELETE FROM demonstration_reserve WHERE demRevNum=:demRevNum")
		void deleteOneDemRes(@Param("demRevNum") long demRevNum); // 회원이 신청한 실증 신청 삭제
		
		@Transactional
		@Query("DELETE FROM demonstration_reserve WHERE demRevNum IN :demRevNum")
		void deleteMembersDemRes(@Param("demRevNum") List<Long> demRevNum); // 회원이 신청한 실증 신청 삭제(다수)

		@Transactional
		@Query("DELETE FROM demonstration_registration WHERE demRegNum=:demRegNum")
		void deleteOneDemReg(@Param("demRegNum") long demRegNum); // 기업이 신청한 실증 등록 삭제 
		
		@Transactional
		@Query("DELETE FROM demonstration_registration WHERE demRegNum IN:demRegNum")
		void deleteMembersDemReg(@Param("demRegNum") List<Long> demRegNum); // 기업이 신청한 실증 등록 삭제 (다수)
		
		@Transactional
		@Query("DELETE FROM demonstration WHERE demNum=:demNum")
		void deleteProductDem(@Param("demNum") long demNum); // 실증 신청 삭제 
		
		@Transactional
		@Query("DELETE FROM demonstration WHERE demNum IN:demNum")
		void deleteProductsDem(@Param("demNum") List<Long> demNum); // 실증 신청 삭제(다수)
		
		// 이미지 삭제와 실증 예약 시간 삭제같은 경우는 고아 처리를해 실증 신청을 삭제하면 전부 삭제되게 구현해 놓았기 때문에 하지않았음.
		
		
		@Transactional
		@Query("SELECT res.applyAt, res.state, res.memid, t.schoolName FROM demonstration_reserve res, teacher t")
		Page<DemonstrationSelectReserveDTO> selectPageDemRes(Pageable pageable); // (관리자 실증교사 신청 페이지) 받아올 dto 조정 필요함. (조인 추가해서)
		/* 
		 @Query("SELECT new com.EduTech.dto.demonstration.DemonstrationSelectRegistrationDTO(d.applyAt, d.state, d.memid) " +
       	"FROM DemonstrationReserve d")
		Page<DemonstrationSelectRegistrationDTO> selectAllDemRes(Pageable pageable);
		  오류 날 경우 이걸로 대체 */ 
		
		@Transactional
		@Query("SELECT reg.regDate, reg.state, reg.memId, dem.demName FROM demonstration_registration reg, demonstration dem, company c,  WHERE reg.demNum=dem.demNum AND reg.memId=c.memId")
		Page<DemonstrationSelectRegistrationDTO> selectPageDemReg(Pageable pageable); //  (관리자 실증기업 신청목록 페이지) 받아올 dto 조정 필요함 (조인추가해서)
		
		
		@Transactional
		@Query("SELECT demName, demMfr,itemNum FROM demonstration")
		Page<DemonstrationSelectRegistrationDTO> selectPageDem(Pageable pageable); // 실증 상품들을 페이지 별로 가져오는 쿼리문 (실증 상품 목록 페이지)
		
		
		
		
		/* @Query("SELECT e FROM Email e WHERE e.sentTime BETWEEN :startDate AND :endDate")
	    List<Email> findEmailsSentBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);  예시 작성 코드*/
}
