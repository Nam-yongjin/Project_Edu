package com.EduTech.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.demonstration.DemonstrationImage;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationReserve;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.demonstration.DemonstrationTime;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationTimeRepository;
import com.EduTech.repository.member.MemberRepository;

@SpringBootTest
public class DemonstrationRepositoryTests { // 5개 엔티티 데이터 값 삽입 결과 문제 없었음. 07/15
	@Autowired
	private DemonstrationRepository demonstrationRepository; 
	
	@Autowired
	private DemonstrationImageRepository demonstrationImageRepository;
	
	@Autowired
	private DemonstrationRegistrationRepository demonstrationRegistrationRepository;
	
	@Autowired
	private DemonstrationReserveRepository demonstrationReserveRepository;
	
	@Autowired
	private DemonstrationTimeRepository demonstrationTimeRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MemberRepository memberRepository; 
	
	//@Test
	public void testDemonstration() {
		for(int i=0; i<10; i++) {
			Demonstration demonstration=Demonstration.builder()
					.demName("product"+i)
					.demInfo("설명"+i)
					.demMfr("제조사다.")
					.itemNum(Long.valueOf(i+1))
					.build();
			
			demonstrationRepository.save(demonstration);
		}
	}
	
	public Demonstration testDemon() {
		Demonstration demonstration=Demonstration.builder()
				.demName("product")
				.demInfo("설명")
				.demMfr("제조사다.")
				.itemNum(Long.valueOf(1))
				.build();
		
		  return demonstrationRepository.save(demonstration);
	}
	
	
	public Member testMember() {
		Member member=Member.builder()
				.memId("user")
				.pw(passwordEncoder.encode("a"))
				.name("USER")
				.email("aaa@test.com")
				.birthDate(LocalDate.of(2025, 7, 15))
				.gender(MemberGender.MAN)
				.phone("0101234321")
				.addr("테스트주소")
				.checkSms(true)
				.checkEmail(false)
				.state(MemberState.NORMAL)
				.role(MemberRole.USER)
				.build();
		
		return memberRepository.save(member);
	}
	//@Test
	public void testDemonstrationImage() {
		
			Demonstration demonstration=testDemon();
		for(int i=0; i<10; i++) {
			DemonstrationImage demonstrationImage=DemonstrationImage.builder()
					.imageName("이미지 이름")
					.imageUrl("URL 입니다.")
					.demonstration(demonstration)
					.build();
			
			demonstrationImageRepository.save(demonstrationImage);
		}
	}
	
	//@Test
		public void testDemonstrationRegistration() {
			
				Demonstration demonstration=testDemon();
				Member member=testMember();
			for(int i=0; i<10; i++) {
				DemonstrationRegistration demonstrationRegistory=DemonstrationRegistration.builder()
						.regDate(LocalDate.now())
						.expDate(LocalDate.now())
						.state(DemonstrationState.WAIT)
						.demonstration(demonstration)
						.member(member)
						.build();
						
				
				demonstrationRegistrationRepository.save(demonstrationRegistory);
				
			}
		}
		
				//@Test
				public void testDemonstrationReserve() {
					
						Demonstration demonstration=testDemon();
						Member member=testMember();
					for(int i=0; i<10; i++) {
						DemonstrationReserve demonstrationReserveRegistory=DemonstrationReserve.builder()
								.applyAt(LocalDate.now())
								.startDate(LocalDate.now())
								.endDate(LocalDate.now())
								.state(DemonstrationState.WAIT)
								.demonstration(demonstration)
								.member(member)
								.build();
								
						
						demonstrationReserveRepository.save(demonstrationReserveRegistory);
						
					}
				}
				
				
				//@Test
				public void testDemonstrationTime() {
					
						Demonstration demonstration=testDemon();
						Member member=testMember();
					for(int i=0; i<10; i++) {
						DemonstrationTime demonstrationTimeRegistory=DemonstrationTime.builder()
								.demDate(LocalDate.now())
								.state(true)
								.demonstration(demonstration)
								.build();
								
						
						demonstrationTimeRepository.save(demonstrationTimeRegistory);
						
					}
				}
				
			
				
			
}







