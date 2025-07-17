package com.EduTech.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.EduTech.dto.member.CompanyDetailDTO;
import com.EduTech.dto.member.StudentDetailDTO;
import com.EduTech.dto.member.StudentRegisterDTO;
import com.EduTech.dto.member.TeacherDetailDTO;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationTimeRepository;
import com.EduTech.repository.notice.NoticeFileRepository;

@SpringBootTest
public class MemberDTOTests {

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
    public void testStudentDetailDTO() {

        StudentDetailDTO studentDetailDTO = new StudentDetailDTO();
        studentDetailDTO.setMemId("user10");
        studentDetailDTO.setPw("pw123");
        studentDetailDTO.setName("홍길동");
        studentDetailDTO.setEmail("hong@test.com");
        studentDetailDTO.setBirthDate(LocalDate.of(2000, 1, 15));
        studentDetailDTO.setGender(MemberGender.MALE);
        studentDetailDTO.setPhone("01012345678");
        studentDetailDTO.setAddr("서울특별시 강남구");
        studentDetailDTO.setAddrDetail("101동 202호");
        studentDetailDTO.setCheckSms(true);
        studentDetailDTO.setCheckEmail(false);
        studentDetailDTO.setSchoolName("서울고등학교");

        assertEquals("user10", studentDetailDTO.getMemId());
        assertEquals("홍길동", studentDetailDTO.getName());
        assertEquals(LocalDate.of(2000, 1, 15), studentDetailDTO.getBirthDate());
        assertEquals(MemberGender.MALE, studentDetailDTO.getGender());
        assertEquals("서울고등학교", studentDetailDTO.getSchoolName());
        assertTrue(studentDetailDTO.isCheckSms());
        assertFalse(studentDetailDTO.isCheckEmail());
    }
	
//	@Test
    public void testTeacherDetailDTO() {

        TeacherDetailDTO teacherDetailDTO = new TeacherDetailDTO();
        teacherDetailDTO.setMemId("user10");
        teacherDetailDTO.setPw("pw123");
        teacherDetailDTO.setName("홍길동");
        teacherDetailDTO.setEmail("hong@test.com");
        teacherDetailDTO.setBirthDate(LocalDate.of(2000, 1, 15));
        teacherDetailDTO.setGender(MemberGender.MALE);
        teacherDetailDTO.setPhone("01012345678");
        teacherDetailDTO.setAddr("서울특별시 강남구");
        teacherDetailDTO.setAddrDetail("101동 202호");
        teacherDetailDTO.setCheckSms(true);
        teacherDetailDTO.setCheckEmail(false);
        teacherDetailDTO.setSchoolName("서울고등학교");

        assertEquals("user10", teacherDetailDTO.getMemId());
        assertEquals("홍길동", teacherDetailDTO.getName());
        assertEquals(LocalDate.of(2000, 1, 15), teacherDetailDTO.getBirthDate());
        assertEquals(MemberGender.MALE, teacherDetailDTO.getGender());
        assertEquals("서울고등학교", teacherDetailDTO.getSchoolName());
        assertTrue(teacherDetailDTO.isCheckSms());
        assertFalse(teacherDetailDTO.isCheckEmail());
    }
	
//	@Test
    public void testCompanyDetailDTO() {

        CompanyDetailDTO companyDetailDTO = new CompanyDetailDTO();
        companyDetailDTO.setMemId("user10");
        companyDetailDTO.setPw("pw123");
        companyDetailDTO.setName("홍길동");
        companyDetailDTO.setEmail("hong@test.com");
        companyDetailDTO.setBirthDate(LocalDate.of(2000, 1, 15));
        companyDetailDTO.setGender(MemberGender.MALE);
        companyDetailDTO.setPhone("01012345678");
        companyDetailDTO.setAddr("서울특별시 강남구");
        companyDetailDTO.setAddrDetail("101동 202호");
        companyDetailDTO.setCheckSms(true);
        companyDetailDTO.setCheckEmail(false);
        companyDetailDTO.setCompanyName("회사1");
        companyDetailDTO.setPosition("과장");

        assertEquals("user10", companyDetailDTO.getMemId());
        assertEquals("홍길동", companyDetailDTO.getName());
        assertEquals(LocalDate.of(2000, 1, 15), companyDetailDTO.getBirthDate());
        assertEquals(MemberGender.MALE, companyDetailDTO.getGender());
        assertEquals("회사1", companyDetailDTO.getCompanyName());
        assertEquals("과장", companyDetailDTO.getPosition());
        assertTrue(companyDetailDTO.isCheckSms());
        assertFalse(companyDetailDTO.isCheckEmail());
    }
    
    @Test
    public void testStudentRegisterDTO() {

    	StudentRegisterDTO studentRegisterDTO = new StudentRegisterDTO();
    	studentRegisterDTO.setMemId("user10");
    	studentRegisterDTO.setPw("pw123");
    	studentRegisterDTO.setName("홍길동");
    	studentRegisterDTO.setEmail("hong@test.com");
    	studentRegisterDTO.setBirthDate(LocalDate.of(2000, 1, 15));
    	studentRegisterDTO.setGender(MemberGender.MALE);
    	studentRegisterDTO.setPhone("01012345678");
    	studentRegisterDTO.setAddr("서울특별시 강남구");
    	studentRegisterDTO.setAddrDetail("101동 202호");
    	studentRegisterDTO.setCheckSms(true);
    	studentRegisterDTO.setCheckEmail(false);
    	studentRegisterDTO.setSchoolName("서울고등학교");

        assertEquals("user10", studentRegisterDTO.getMemId());
        assertEquals("홍길동", studentRegisterDTO.getName());
        assertEquals(LocalDate.of(2000, 1, 15), studentRegisterDTO.getBirthDate());
        assertEquals(MemberGender.MALE, studentRegisterDTO.getGender());
        assertEquals("서울고등학교", studentRegisterDTO.getSchoolName());
        assertTrue(studentRegisterDTO.isCheckSms());
        assertFalse(studentRegisterDTO.isCheckEmail());
    }
}
