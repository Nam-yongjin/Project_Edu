package com.EduTech.repository;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
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
			Member member = Member.builder()
					.memId("user"+i)
					.pw(passwordEncoder.encode("aaa"+i))
					.name("USER"+i)
					.email("aaa"+i+"@test.com")
					.birthDate(LocalDate.now())
					.gender(MemberGender.MAN)
					.phone("0101234321"+i)
					.addr("테스트주소"+i)
					.checkSms(true)
					.checkEmail(false)
					.state(MemberState.NORMAL)
					.role(MemberRole.USER)
					.build();
			memberRepository.save(member);
		}
	}
}
