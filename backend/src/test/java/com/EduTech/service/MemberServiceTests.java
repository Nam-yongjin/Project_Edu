package com.EduTech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

import com.EduTech.dto.member.CompanyModifyDTO;
import com.EduTech.dto.member.CompanyRegisterDTO;
import com.EduTech.dto.member.MemberDetailDTO;
import com.EduTech.dto.member.MemberModifyDTO;
import com.EduTech.dto.member.StudentModifyDTO;
import com.EduTech.entity.member.Company;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberState;
import com.EduTech.entity.member.Student;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationTimeRepository;
import com.EduTech.repository.facility.FacilityHolidayRepository;
import com.EduTech.repository.facility.FacilityImageRepository;
import com.EduTech.repository.facility.FacilityRepository;
import com.EduTech.repository.facility.FacilityReserveRepository;
import com.EduTech.repository.facility.FacilityTimeRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.notice.NoticeFileRepository;
import com.EduTech.service.member.MemberService;

import jakarta.transaction.Transactional;

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
	@MockBean
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
	private FacilityHolidayRepository facilityHolidayRepository;
	@MockBean
	private FacilityRepository facilityRepository;
	@MockBean
	private FacilityReserveRepository facilityReserveRepository;
	@MockBean
	private FacilityImageRepository facilityImageRepository;
	@MockBean
	private FacilityTimeRepository facilityTimeRepository;

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
	
//	@Test
	public void testRegisterCompanyInfo() {
		CompanyRegisterDTO companyRegisterDTO = new CompanyRegisterDTO();
		companyRegisterDTO.setMemId("companytest");
		companyRegisterDTO.setPw(passwordEncoder.encode("123456"));
		companyRegisterDTO.setName("남용진");
		companyRegisterDTO.setEmail("test12@gmail.com");
		companyRegisterDTO.setPhone("01044665566");
		companyRegisterDTO.setAddr("대전 서구 둔산동");
		companyRegisterDTO.setAddrDetail("101호");
		companyRegisterDTO.setCheckSms(false);
		companyRegisterDTO.setCheckEmail(true);
		companyRegisterDTO.setCompanyName("둔산제철");
		companyRegisterDTO.setPosition("과장");
		
		memberService.registerCompany(companyRegisterDTO);
		
	    Member updatedMember = memberRepository.findById("companytest").orElseThrow();
	    Company updatedCompany = memberRepository.findCompanyById("companytest").orElseThrow();
	    assertEquals("남용진", updatedMember.getName());
	    assertEquals("test12@gmail.com", updatedMember.getEmail());
	    assertEquals("01044665566", updatedMember.getPhone());
	    assertEquals("대전 서구 둔산동", updatedMember.getAddr());
	    assertEquals("101호", updatedMember.getAddrDetail());
	    assertFalse(updatedMember.isCheckSms());
	    assertTrue(updatedMember.isCheckEmail());
	    assertEquals("둔산제철", updatedCompany.getCompanyName());
	    assertEquals("과장", updatedCompany.getPosition());
	}
	
	@Test
	public void testRegisterModifyCompanyInfo() {
		CompanyModifyDTO companyModifyDTO = new CompanyModifyDTO();
		companyModifyDTO.setPw(passwordEncoder.encode("123456"));
		companyModifyDTO.setName("수정정보");
		companyModifyDTO.setEmail("test12@gmail.com");
		companyModifyDTO.setPhone("01044665566");
		companyModifyDTO.setAddr("대전 서구 둔산동");
		companyModifyDTO.setAddrDetail("101호");
		companyModifyDTO.setCheckSms(false);
		companyModifyDTO.setCheckEmail(true);
		companyModifyDTO.setCompanyName("둔산제철");
		companyModifyDTO.setPosition("과장");
		
		memberService.modifyCompanyInfo("companytest", companyModifyDTO);
		
	    Member updatedMember = memberRepository.findById("companytest").orElseThrow();
	    Company updatedCompany = memberRepository.findCompanyById("companytest").orElseThrow();
	    assertEquals("수정정보", updatedMember.getName());
	    assertEquals("test12@gmail.com", updatedMember.getEmail());
	    assertEquals("01044665566", updatedMember.getPhone());
	    assertEquals("대전 서구 둔산동", updatedMember.getAddr());
	    assertEquals("101호", updatedMember.getAddrDetail());
	    assertFalse(updatedMember.isCheckSms());
	    assertTrue(updatedMember.isCheckEmail());
	    assertEquals("둔산제철", updatedCompany.getCompanyName());
	    assertEquals("과장", updatedCompany.getPosition());
	}
	
//	@Test
	public void testLeaveMember() {
		Member member = memberRepository.findById("user9").orElseThrow();
		memberService.leaveMember("user9");
		assertEquals(MemberState.LEAVE, member.getState());
	}
	
//	@Test
	void testPasswordMatch() {
	    String rawPw = "qwer1234!@#$";
	    String encodedPw = "$2a$10$Y8yJJQCmUIMDCQSBqHi31OMYCvcDva6qf58wmX7MByYhs1zwdtuRC";

	    boolean matches = passwordEncoder.matches(rawPw, encodedPw);

	    System.out.println("비밀번호 일치 여부: " + matches); // true 나와야 함
	}
}
