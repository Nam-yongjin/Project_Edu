package com.EduTech.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.member.Member;

public interface MemberRepository extends JpaRepository<Member, String>{

	// 이름과 전화번호로 아이디 찾기
	@Query("SELECT m.memId FROM Member m  WHERE m.name = :name AND m.phone = :phone")
	Optional<String> findMemIdByNameAndPhone(@Param("name") String name, @Param("phone") String phone);
	
	// 아이디와 전화번호로 인증 후 비밀번호 찾기(비빌먼호 변경창 이동)
	@Query("SELECT m FROM Member m  WHERE m.memId = :memId AND m.phone = :phone")
	Optional<Member> findByMemIdAndPhone(@Param("memId") String memId, @Param("phone") String phone);
}
