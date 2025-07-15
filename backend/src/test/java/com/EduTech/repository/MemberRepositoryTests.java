package com.EduTech.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.EduTech.entity.member.Member;
import com.EduTech.repository.member.MemberRepository;

@SpringBootTest
public class MemberRepositoryTests {

	@Autowired
	private MemberRepository memberRepository; 
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Test
	public void testInsertMember() {
		for(int i=0; i<10; i++) {
			Member member = Member.bui
		}
	}
}
