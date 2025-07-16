package com.EduTech.repository.demonstration;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationSelectRegistrationDTO;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationState;

public interface DemonstrationRegistrationRepository extends JpaRepository<DemonstrationRegistration, Long>{ // 실증 물품 등록 관련 레포지토리
	@Modifying
	@Transactional
	@Query("DELETE FROM demonstration_registration WHERE demRegNum=:demRegNum")
	void deleteOneDemReg(long demRegNum); // 기업이 신청한 실증 등록 삭제 
	
	@Modifying
	@Transactional
	@Query("DELETE FROM demonstration_registration WHERE demRegNum IN:demRegNum")
	void deleteMembersDemReg(List<Long> demRegNum); // 기업이 신청한 실증 등록 삭제 (다수)
	
	@Query("SELECT reg.demRegNum,reg.regDate, reg.state, reg.memId, dem.demName,c.companyName, c.position FROM demonstration_registration reg, demonstration dem, company c  WHERE reg.demNum=dem.demNum AND reg.memId=c.memId")
	Page<DemonstrationSelectRegistrationDTO> selectPageDemReg(Pageable pageable); //  (관리자 실증기업 신청 조회 페이지) 받아올 dto 추가 필요함 (조인추가해서)

	@Modifying // 실증 기업 신청 목록 페이지에서 승인 / 거부 버튼 클릭 시, state를 변경하는 쿼리문
    @Query("UPDATE demonstration_registration dr SET state=:state WHERE memId=:memId")
    int updateDemResChangeState(
     DemonstrationState state,
     String memId
    );
}


