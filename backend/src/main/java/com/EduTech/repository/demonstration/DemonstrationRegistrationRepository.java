package com.EduTech.repository.demonstration;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationState;
//실증 물품 등록 관련 레포지토리
public interface DemonstrationRegistrationRepository extends JpaRepository<DemonstrationRegistration, Long>{ 

	//  (관리자 실증기업 신청 조회 페이지) 받아올 dto 추가 필요함 (조인추가해서)
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO(reg.demRegNum,reg.regDate, reg.expDate, reg.state, reg.member.memId, c.companyName, c.position )FROM DemonstrationRegistration reg, Demonstration dem, Company c  WHERE reg.demonstration.demNum=dem.demNum AND reg.member.memId=c.memId")
	Page<DemonstrationListRegistrationDTO> selectPageDemReg( Pageable pageable); 

	
	 //  (관리자 실증기업 신청 조회 페이지 검색 추가 ) 받아올 dto 추가 필요함 (조인추가해서)
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO(reg.demRegNum,reg.regDate, reg.expDate, reg.state, reg.member.memId, c.companyName, c.position )FROM DemonstrationRegistration reg, Demonstration dem, Company c  WHERE reg.demonstration.demNum=dem.demNum AND reg.member.memId=c.memId AND c.companyName LIKE %:search%")
	Page<DemonstrationListRegistrationDTO> selectPageDemRegSearch( Pageable pageable,@Param("search") String search);
	
	// 실증 기업 신청 목록 페이지에서 승인 / 거부 버튼 클릭 시, state를 변경하는 쿼리문
	@Modifying 
	@Transactional
    @Query("UPDATE DemonstrationRegistration dr SET state=:state WHERE member.memId=:memId")
    int updateDemResChangeState(
     @Param("state") DemonstrationState state,
     @Param("memId") String memId
    ); 
}



