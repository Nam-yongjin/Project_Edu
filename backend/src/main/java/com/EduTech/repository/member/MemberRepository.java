package com.EduTech.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.member.Member;

public interface MemberRepository extends JpaRepository<Member, String>{

}
