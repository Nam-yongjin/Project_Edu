package com.EduTech.repository;

import java.time.LocalDate;
import java.util.Optional;

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
import com.EduTech.repository.event.EventBannerRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.event.EventUseRepository;
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
	
//	@BeforeEach	// 테스트 실행전
//    public void setup() {
//        studentRepository.deleteAll();	// 순서 중요
//        teacherRepository.deleteAll();
//        companyRepository.deleteAll();
//        memberRepository.deleteAll();
//    }

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
	@MockBean
	private EventBannerRepository eventBannerRepository;
	@MockBean
	private EventInfoRepository eventInfoRepository;
	@MockBean
	private EventUseRepository eventUseRepository;

//	등록 테스트
//	@Test
	@Transactional
	@Commit
	public void testInsertMember() {

		Member member = Member.builder()
				.memId("oj4263")
				.pw(passwordEncoder.encode("qwer1234!@#$"))
				.name("남용진")
				.email("oj4263@test.com")
				.birthDate(LocalDate.of(2000, 7, 5))
				.gender(MemberGender.MALE)
				.phone("01012345678")
				.addr("테스트주소")
				.addrDetail("테스트 상세주소")
				.checkSms(true)
				.checkEmail(false)
				.state(MemberState.NORMAL)
				.role(MemberRole.USER)
				.build();
		memberRepository.save(member);
		memberRepository.flush();
		
	}
	
//	 관리자계정
//	@Test
	@Transactional
	@Commit
	public void testInsertAdmin() {

		Member member = Member.builder()
				.memId("admin")
				.pw(passwordEncoder.encode("admin"))
				.name("관리자")
				.email("admin@test.com")
				.birthDate(LocalDate.of(2000, 7, 5))
				.gender(MemberGender.MALE)
				.phone("01000000000")
				.addr("어드민")
				.addrDetail("어드민")
				.checkSms(true)
				.checkEmail(false)
				.state(MemberState.NORMAL)
				.role(MemberRole.ADMIN)
				.build();
		memberRepository.save(member);
		memberRepository.flush();
		
	}
	
	// 블랙리스트 계정
//		@Test
		@Transactional
		@Commit
		public void testInsertBen() {

			Member member = Member.builder()
					.memId("black")
					.pw(passwordEncoder.encode("black"))
					.name("블랙리스트")
					.email("black@test.com")
					.birthDate(LocalDate.of(2000, 7, 5))
					.gender(MemberGender.MALE)
					.phone("01099999999")
					.addr("블랙")
					.addrDetail("리스트")
					.checkSms(true)
					.checkEmail(false)
					.state(MemberState.BEN)
					.role(MemberRole.STUDENT)
					.build();
			memberRepository.save(member);
			memberRepository.flush();
			
		}
		// 탈퇴 계정
		@Test
		@Transactional
		@Commit
		public void testInsertLeave() {

			Member member = Member.builder()
					.memId("leave")
					.pw(passwordEncoder.encode("leave"))
					.name("탈퇴")
					.email("leave@test.com")
					.birthDate(LocalDate.of(2000, 7, 5))
					.gender(MemberGender.FEMALE)
					.phone("01088888888")
					.addr("탈퇴")
					.addrDetail("회원")
					.checkSms(true)
					.checkEmail(false)
					.state(MemberState.LEAVE)
					.role(MemberRole.COMPANY)
					.build();
			memberRepository.save(member);
			memberRepository.flush();
			
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
					.gender(MemberGender.FEMALE)
					.phone("0101234321" + i)
					.addr("테스트주소" + i)
					.addrDetail("테스트 상세주소"+i)
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
					.gender(MemberGender.MALE)
					.phone("0101234321" + i)
					.addr("테스트주소" + i)
					.addrDetail("테스트 상세주소"+i)
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
	
//	@Test
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
					.gender(MemberGender.FEMALE)
					.phone("0101234321" + i)
					.addr("테스트주소" + i)
					.addrDetail("테스트 상세주소"+i)
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
	
	
	// 조회 테스트
//	@Test
	@Transactional
	@Commit
	public void testFindMember() {
		 for (int i = 0; i < 10; i++) {
		        Member member = Member.builder()
		            .memId("user" + i)
		            .pw(passwordEncoder.encode("a" + i))
		            .name("USER" + i)
		            .email("aaa" + i + "@test.com")
		            .birthDate(LocalDate.of(2025, 7, 15))
		            .gender(MemberGender.FEMALE)
		            .phone("0101234321" + i)
		            .addr("테스트주소" + i)
		            .addrDetail("테스트 상세주소" + i)
		            .checkSms(true)
		            .checkEmail(false)
		            .state(MemberState.NORMAL)
		            .role(MemberRole.USER)
		            .build();

		    member = memberRepository.save(member); 
		    memberRepository.flush();  
		}

	    Optional<Member> result = memberRepository.findById("user5");
	    
	    Member user5 = result.get();
	    
		System.out.println(user5.toString());
	}
	
//	@Test
	@Transactional
	@Commit
	public void testFindStudent() {
		 
	    Optional<Student> result = studentRepository.findByIdWithMember("user5");
	    
	    Student user5 = result.get();
	    
		System.out.println(user5.getMember());
	}
	
	// 아이디찾기
//	@Test
	public void testMemberFindId() {

		
	}
	
	// 비밀번호 찾기(비밀번호 변경창으로 이동)
//	@Test
	public void testMemberFindPw() {
		

		
	}
}
