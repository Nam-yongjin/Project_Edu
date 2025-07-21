package com.EduTech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.EduTech.dto.member.MemberModifyDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationTimeRepository;
import com.EduTech.repository.event.EventReserveRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.notice.NoticeFileRepository;
import com.EduTech.service.member.MemberService;

@SpringBootTest
public class MemberServiceTests {

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@MockBean	// 오류나는것 제외하고 실행
	private DemonstrationReserveRepository demonstrationReserveRepository;
	@MockBean
	private DemonstrationRepository demonstrationRepository;
	@MockBean
	private DemonstrationImageRepository demonstrationImageRepository;
	@MockBean
	private DemonstrationTimeRepository demonstrationTimeRepository;
	@MockBean
	private DemonstrationRegistrationRepository demonstrationRegistrationRepository;
	@MockBean
	private NoticeFileRepository noticeFileRepository;

//	@Test
	public void testCheckId() {
		boolean result = memberService.isDuplicatedId("user5");
		System.out.println(result);
	}
	
	@Test
	public void testModifyMemberInfo() {
		Optional<Member> member = memberRepository.findById("user5");
		MemberModifyDTO memberModifyDTO = new MemberModifyDTO();
		memberModifyDTO.setPw(passwordEncoder.encode("123456"));
		memberModifyDTO.setName("남용진");
		memberModifyDTO.setEmail("test@gmail.com");
		memberModifyDTO.setPhone("01099994422");
		memberModifyDTO.setAddr("대전 서구 둔산동");
		memberModifyDTO.setAddrDetail("101호");
		memberModifyDTO.setCheckSms(false);
		memberModifyDTO.setCheckEmail(true);
		
		memberService.modifyMemberInfo("user5", memberModifyDTO);
		
	    Member updatedMember = memberRepository.findById("user5").orElseThrow();
	    assertEquals("남용진", updatedMember.getName());
	    assertEquals("test@gmail.com", updatedMember.getEmail());
	    assertEquals("01099994422", updatedMember.getPhone());
	    assertEquals("대전 서구 둔산동", updatedMember.getAddr());
	    assertEquals("101호", updatedMember.getAddrDetail());
	    assertFalse(updatedMember.isCheckSms());
	    assertTrue(updatedMember.isCheckEmail());
	}
}
