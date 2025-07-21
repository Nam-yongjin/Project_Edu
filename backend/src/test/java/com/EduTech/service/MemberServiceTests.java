package com.EduTech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.EduTech.dto.member.MemberDetailDTO;
import com.EduTech.dto.member.MemberModifyDTO;
import com.EduTech.dto.member.StudentModifyDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.Student;
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
	
	@Autowired
	private ModelMapper modelMapper;
	
	@MockBean	// 오류나는것 제외하고 실행
	private NoticeFileRepository noticeFileRepository;

//	@Test
	public void testCheckId() {
		boolean result = memberService.isDuplicatedId("user5");
		System.out.println(result);
	}
	
//	@Test
	public void testReadMemberInfo() {
		MemberDetailDTO memberDetailDTO = memberService.readMemberInfo("user5");
        System.out.println(memberDetailDTO);
	}
	
//	@Test
	public void testModifyMemberInfo() {
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
	
//	@Test
	public void testModifyStudentInfo() {
		StudentModifyDTO studentModifyDTO = new StudentModifyDTO();
		studentModifyDTO.setPw(passwordEncoder.encode("123456"));
		studentModifyDTO.setName("남용진");
		studentModifyDTO.setEmail("test@gmail.com");
		studentModifyDTO.setPhone("01099994422");
		studentModifyDTO.setAddr("대전 서구 둔산동");
		studentModifyDTO.setAddrDetail("101호");
		studentModifyDTO.setCheckSms(false);
		studentModifyDTO.setCheckEmail(true);
		studentModifyDTO.setSchoolName("둔산중학교");
		
		memberService.modifyStudentInfo("user5", studentModifyDTO);
		
	    Member updatedMember = memberRepository.findById("user5").orElseThrow();
	    Student updatedStudent = memberRepository.findStudentById("user5").orElseThrow();
	    assertEquals("남용진", updatedMember.getName());
	    assertEquals("test@gmail.com", updatedMember.getEmail());
	    assertEquals("01099994422", updatedMember.getPhone());
	    assertEquals("대전 서구 둔산동", updatedMember.getAddr());
	    assertEquals("101호", updatedMember.getAddrDetail());
	    assertFalse(updatedMember.isCheckSms());
	    assertTrue(updatedMember.isCheckEmail());
	    assertEquals("둔산중학교", updatedStudent.getSchoolName());
	}
}
