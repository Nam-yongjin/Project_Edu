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

        StudentDetailDTO student = new StudentDetailDTO();
        student.setMemId("user10");
        student.setPw("pw123");
        student.setName("홍길동");
        student.setEmail("hong@test.com");
        student.setBirthDate(LocalDate.of(2000, 1, 15));
        student.setGender(MemberGender.MALE);
        student.setPhone("01012345678");
        student.setAddr("서울특별시 강남구");
        student.setAddrDetail("101동 202호");
        student.setCheckSms(true);
        student.setCheckEmail(false);
        student.setSchoolName("서울고등학교");

        assertEquals("user10", student.getMemId());
        assertEquals("홍길동", student.getName());
        assertEquals(LocalDate.of(2000, 1, 15), student.getBirthDate());
        assertEquals(MemberGender.MALE, student.getGender());
        assertEquals("서울고등학교", student.getSchoolName());
        assertTrue(student.isCheckSms());
        assertFalse(student.isCheckEmail());
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
	
	@Test
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
}
