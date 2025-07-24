package com.EduTech.repository.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.member.Company;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberState;
import com.EduTech.entity.member.Student;
import com.EduTech.entity.member.Teacher;

public interface MemberRepository extends JpaRepository<Member, String>,JpaSpecificationExecutor<Member>{

	// 아이디 중복 체크
	boolean existsById(String memId);

	// 학생회원정보 조회
	@Query("SELECT s FROM Student s WHERE s.memId = :memId")
	Optional<Student> findStudentById(@Param("memId") String memId);

	// 교사회원정보 조회
	@Query("SELECT t FROM Teacher t WHERE t.memId = :memId")
	Optional<Teacher> findTeacherById(@Param("memId") String memId);

	// 기업회원정보 조회
	@Query("SELECT c FROM Company c WHERE c.memId = :memId")
	Optional<Company> findCompanyById(@Param("memId") String memId);

	// 이름과 전화번호로 아이디 찾기
//	@Query("SELECT m.memId FROM Member m  WHERE m.name = :name AND m.phone = :phone")
//	Optional<String> findByNameAndPhone(@Param("name") String name, @Param("phone") String phone);

	// 아이디와 전화번호로 인증 후 비밀번호 찾기(비빌먼호 변경창 이동)
//	@Query("SELECT m FROM Member m  WHERE m.memId = :memId AND m.phone = :phone")
//	Optional<Member> findByMemIdAndPhone(@Param("memId") String memId, @Param("phone") String phone);

	// 회원탈퇴 신청한지 일주일지난 회원정보 자동삭제(LEAVE로 업데이트한 시각과의 차이를 매일 0시에 비교하고 삭제)
	@Modifying
	@Query(value = "DELETE FROM member WHERE role = 'LEAVE' AND updatedAt <= DATE_SUB(NOW(), INTERVAL 7 DAY)", nativeQuery = true)
	void deleteMembersAfterOneWeekLeave();

	// 다른 회원의 이메일과 카카오 이메일이 중복되는지 확인
	Optional<Member> findByEmail(String Email);
	
	// 카카오 회원인지 확인
	Optional<Member> findByKakao(String kakao);
	
	// 관리자가 멤버 상태 수정하는 쿼리문
	@Modifying
	@Query("update Member m set m.state = :state where m.memId in :memId")
	void updateMemberState(@Param("state") MemberState state, @Param("memId") List<String> memId);
	
}
