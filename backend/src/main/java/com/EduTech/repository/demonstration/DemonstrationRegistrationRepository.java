package com.EduTech.repository.demonstration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationState;

//실증 물품 등록 관련 레포지토리
public interface DemonstrationRegistrationRepository extends JpaRepository<DemonstrationRegistration, Long>, JpaSpecificationExecutor<DemonstrationRegistration> {
	// 실증 기업 신청 페이지에서 반납 날짜를 불러오는 쿼리문
	@Query("SELECT reg.expDate FROM DemonstrationRegistration reg WHERE reg.demonstration.demNum=:demNum")
	LocalDate selectDemRegExpDate(@Param("demNum") Long demNum);

	// 실증 기업 신청 페이지에서 회원 아이디를 불러오는 쿼리문
	@Query("SELECT reg.member.memId FROM DemonstrationRegistration reg WHERE reg.demonstration.demNum=:demNum")
	String selectRegMemId(@Param("demNum") Long demNum);

	// 실증 기업 신청 페이지에서 물품 번호를 받아오는 쿼리문
	@Query("SELECT reg.demonstration.demNum FROM DemonstrationRegistration reg WHERE reg.state=:state")
	List<Long> selectRegDemNums(@Param("state") DemonstrationState state);

	// 관리자 실증 기업 신청 목록 페이지에서 승인 / 거부 버튼 클릭 시, state를 변경하는 쿼리문
	@Modifying
	@Transactional
	@Query("UPDATE DemonstrationRegistration SET state=:state WHERE demonstration.demNum IN :demNum")
	int updateDemRegChangeState(@Param("state") DemonstrationState state,@Param("demNum") List<Long> demNum);

	// demRegNum을 받아 상태 업데이트하는 쿼리문
	@Modifying
	@Transactional
	@Query("UPDATE DemonstrationRegistration SET state=:state WHERE demRegNum IN :demRegNum")
	int updateDemRegChangeStateReg(@Param("state") DemonstrationState state, @Param("demRegNum") List<Long> demRegNum);

	// 실증 신청 유효기간 업데이트 하는 쿼리문
	@Modifying
	@Transactional
	@Query("UPDATE DemonstrationRegistration SET expDate=:expDate WHERE member.memId=:memId AND demonstration.demNum=:demNum")
	int updateDemRegChangeExpDate(@Param("expDate") LocalDate expDate, @Param("demNum") Long demNum,@Param("memId") String memId);

	// 나중에 회원 탈퇴할때, 실증 등록 중인 상태이면 회원 탈퇴 못하도록 구현하기 위한 쿼리문
	@Query("SELECT COUNT(d) > 0 FROM DemonstrationRegistration d WHERE d.member.memId = :memId AND d.state = 'ACCEPT'")
	boolean existsAcceptedRegistrationByMemId(@Param("memId") String memId);

	// demonstrationRegistrationRepository
	List<DemonstrationRegistration> findByDemonstration_DemNumIn(List<Long> demNums);
	
	// 현재 날짜와 만료 날짜를 비고해서 만료날짜가 작거나 같을경우 상태를 만료로 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE DemonstrationRegistration r SET r.state = :expired WHERE r.expDate <= :today AND r.state=:accept")
    int changeRegExpiredState(@Param("today") LocalDate today, @Param("expired") DemonstrationState expired,@Param("accept") DemonstrationState accept);

 

}
