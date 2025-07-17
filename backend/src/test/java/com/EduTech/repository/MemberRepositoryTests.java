package com.EduTech.repository;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

import com.EduTech.entity.member.Company;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.entity.member.Student;
import com.EduTech.entity.member.Teacher;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationTimeRepository;
import com.EduTech.repository.member.CompanyRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.member.StudentRepository;
import com.EduTech.repository.member.TeacherRepository;
import com.EduTech.repository.notice.NoticeFileRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class MemberRepositoryTests {

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@BeforeEach	// 테스트 실행전
    public void setup() {
        studentRepository.deleteAll();	// 순서 중요
        teacherRepository.deleteAll();
        companyRepository.deleteAll();
        memberRepository.deleteAll();
    }

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
	public void testInsertMember() {
		for (int i = 0; i < 10; i++) {
			Member member = Member.builder()
					.memId("user" + i)
					.pw(passwordEncoder.encode("a" + i))
					.name("USER" + i)
					.email("aaa" + i + "@test.com")
					.birthDate(LocalDate.of(2025, 7, 15))
					.gender(MemberGender.MAN)
					.phone("0101234321" + i)
					.addr("테스트주소" + i)
					.checkSms(true)
					.checkEmail(false)
					.state(MemberState.NORMAL)
					.role(MemberRole.USER)
					.build();
			memberRepository.save(member);
		}
	}
	
//	@Test
	@Transactional
	@Commit
	public void testInsertStudent() {
		for (int i = 0; i < 10; i++) {
			Member member = Member.builder()
					.memId("user" + i)
					.pw(passwordEncoder.encode("a" + i))
					.name("USER" + i)
					.email("aaa" + i + "@test.com")
					.birthDate(LocalDate.of(2025, 7, 15))
					.gender(MemberGender.WOMAN)
					.phone("0101234321" + i)
					.addr("테스트주소" + i)
					.checkSms(true)
					.checkEmail(false)
					.state(MemberState.NORMAL)
					.role(MemberRole.STUDENT)
					.build();
			member = memberRepository.save(member); 
			memberRepository.flush();  // 영속 상태 확정

			Student student = Student.builder()
			        .schoolName("테스트학교"+i)
			        .member(member) // member 엔티티 주입
			        .build();

			studentRepository.save(student);
			studentRepository.flush();
		}
	}
	
//	@Test
	@Transactional
	@Commit
	public void testInsertTeacher() {
		for (int i = 0; i < 10; i++) {
			Member member = Member.builder()
					.memId("user" + i)
					.pw(passwordEncoder.encode("a" + i))
					.name("USER" + i)
					.email("aaa" + i + "@test.com")
					.birthDate(LocalDate.of(2025, 7, 15))
					.gender(MemberGender.MAN)
					.phone("0101234321" + i)
					.addr("테스트주소" + i)
					.checkSms(true)
					.checkEmail(false)
					.state(MemberState.NORMAL)
					.role(MemberRole.TEACHER)
					.build();
			member = memberRepository.save(member); 
			memberRepository.flush();  // 영속 상태 확정

			Teacher teacher = Teacher.builder()
			        .schoolName("테스트학교"+i)
			        .member(member) // member 엔티티 주입
			        .build();

			teacherRepository.save(teacher);
			teacherRepository.flush();
		}
	}
	
	@Test
	@Transactional
	@Commit
	public void testInsertCompany() {
		for (int i = 0; i < 10; i++) {
			Member member = Member.builder()
					.memId("user" + i)
					.pw(passwordEncoder.encode("a" + i))
					.name("USER" + i)
					.email("aaa" + i + "@test.com")
					.birthDate(LocalDate.of(2025, 7, 15))
					.gender(MemberGender.WOMAN)
					.phone("0101234321" + i)
					.addr("테스트주소" + i)
					.checkSms(true)
					.checkEmail(false)
					.state(MemberState.NORMAL)
					.role(MemberRole.COMPANY)
					.build();
			member = memberRepository.save(member); 
			memberRepository.flush();  // 영속 상태 확정

			Company company = Company.builder()
			        .companyName("테스트기업"+i)
			        .position("테스트직책"+i)
			        .member(member) // member 엔티티 주입
			        .build();

			companyRepository.save(company);
			companyRepository.flush();
		}
	}
	
}
