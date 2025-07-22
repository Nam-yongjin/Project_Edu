package com.EduTech.service.member;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.member.CompanyDetailDTO;
import com.EduTech.dto.member.CompanyModifyDTO;
import com.EduTech.dto.member.CompanyRegisterDTO;
import com.EduTech.dto.member.MemberDetailDTO;
import com.EduTech.dto.member.MemberModifyDTO;
import com.EduTech.dto.member.MemberRegisterDTO;
import com.EduTech.dto.member.StudentDetailDTO;
import com.EduTech.dto.member.StudentModifyDTO;
import com.EduTech.dto.member.StudentRegisterDTO;
import com.EduTech.dto.member.TeacherDetailDTO;
import com.EduTech.dto.member.TeacherModifyDTO;
import com.EduTech.dto.member.TeacherRegisterDTO;
import com.EduTech.entity.member.Company;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.entity.member.Student;
import com.EduTech.entity.member.Teacher;
import com.EduTech.repository.member.CompanyRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.member.StudentRepository;
import com.EduTech.repository.member.TeacherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final CompanyRepository companyRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;

	// 일반회원 회원가입
	@Override
	@Transactional
	public void registerMember(MemberRegisterDTO memberRegisterDTO) {
		Member member = modelMapper.map(memberRegisterDTO, Member.class);
		member.setPw(passwordEncoder.encode(member.getPw()));
		member.setState(MemberState.NORMAL);
		member.setRole(MemberRole.USER);
		memberRepository.save(member);
	}

	// 학생회원 회원가입
	@Override
	@Transactional
	public void registerStudent(StudentRegisterDTO studentRegisterDTO) {
		Member member = modelMapper.map(studentRegisterDTO, Member.class);
		member.setPw(passwordEncoder.encode(member.getPw()));
		member.setState(MemberState.NORMAL);
		member.setRole(MemberRole.STUDENT);
		memberRepository.save(member);

		Student student = modelMapper.map(studentRegisterDTO, Student.class);
		student.setMember(member);
		student.setMemId(member.getMemId());
		studentRepository.save(student);
	}

	// 교사회원 회원가입
	@Override
	@Transactional
	public void registerTeacher(TeacherRegisterDTO teacherRegisterDTO) {
		Member member = modelMapper.map(teacherRegisterDTO, Member.class);
		member.setPw(passwordEncoder.encode(member.getPw()));
		member.setState(MemberState.NORMAL);
		member.setRole(MemberRole.TEACHER);
		memberRepository.save(member);

		Teacher teacher = modelMapper.map(teacherRegisterDTO, Teacher.class);
		teacher.setMember(member);
		teacher.setMemId(member.getMemId());
		teacherRepository.save(teacher);
	}

	// 기업회원 회원가입
	@Override
	@Transactional
	public void registerCompany(CompanyRegisterDTO companyRegisterDTO) {
		Member member = modelMapper.map(companyRegisterDTO, Member.class);
		member.setPw(passwordEncoder.encode(member.getPw()));
		member.setState(MemberState.NORMAL);
		member.setRole(MemberRole.COMPANY);
		memberRepository.save(member);

		Company company = modelMapper.map(companyRegisterDTO, Company.class);
		company.setMember(member);
		company.setMemId(member.getMemId());
		companyRepository.save(company);
	}

	// 아이디 중복 체크
	@Override
	@Transactional
	public boolean isDuplicatedId(String memId) {
		return memberRepository.existsById(memId);
	}

	// 일반회원정보 가져오기
	@Override
	@Transactional(readOnly = true)
	public MemberDetailDTO readMemberInfo(String memId) {
		Member member = memberRepository.findById(memId).orElseThrow();
		MemberDetailDTO memberDetailDTO = modelMapper.map(member, MemberDetailDTO.class);

		return memberDetailDTO;
	}

	// 학생회원정보 가져오기
	@Override
	@Transactional(readOnly = true)
	public StudentDetailDTO readStudentInfo(String memId) {
		Member member = memberRepository.findById(memId).orElseThrow();

		Student student = studentRepository.findById(memId).orElseThrow();

		StudentDetailDTO studentDetailDTO = new StudentDetailDTO();
		studentDetailDTO.setMemId(member.getMemId());
		studentDetailDTO.setName(member.getName());
		studentDetailDTO.setEmail(member.getEmail());
		studentDetailDTO.setPhone(member.getPhone());
		studentDetailDTO.setAddr(member.getAddr());
		studentDetailDTO.setAddrDetail(member.getAddrDetail());
		studentDetailDTO.setCheckSms(member.isCheckSms());
		studentDetailDTO.setCheckEmail(member.isCheckEmail());
		studentDetailDTO.setSchoolName(student.getSchoolName());

		return studentDetailDTO;
	}

	// 교사회원정보 가져오기
	@Override
	@Transactional(readOnly = true)
	public TeacherDetailDTO readTeacherInfo(String memId) {
		Member member = memberRepository.findById(memId).orElseThrow();

		Teacher teacher = teacherRepository.findById(memId).orElseThrow();

		TeacherDetailDTO teacherDetailDTO = new TeacherDetailDTO();
		teacherDetailDTO.setMemId(member.getMemId());
		teacherDetailDTO.setName(member.getName());
		teacherDetailDTO.setEmail(member.getEmail());
		teacherDetailDTO.setPhone(member.getPhone());
		teacherDetailDTO.setAddr(member.getAddr());
		teacherDetailDTO.setAddrDetail(member.getAddrDetail());
		teacherDetailDTO.setCheckSms(member.isCheckSms());
		teacherDetailDTO.setCheckEmail(member.isCheckEmail());
		teacherDetailDTO.setSchoolName(teacher.getSchoolName());

		return teacherDetailDTO;
	}

	// 기업회원정보 가져오기
	@Override
	@Transactional(readOnly = true)
	public CompanyDetailDTO readCompanyInfo(String memId) {
		Member member = memberRepository.findById(memId).orElseThrow();

		Company company = companyRepository.findById(memId).orElseThrow();

		CompanyDetailDTO companyDetailDTO = new CompanyDetailDTO();
		companyDetailDTO.setMemId(member.getMemId());
		companyDetailDTO.setName(member.getName());
		companyDetailDTO.setEmail(member.getEmail());
		companyDetailDTO.setPhone(member.getPhone());
		companyDetailDTO.setAddr(member.getAddr());
		companyDetailDTO.setAddrDetail(member.getAddrDetail());
		companyDetailDTO.setCheckSms(member.isCheckSms());
		companyDetailDTO.setCheckEmail(member.isCheckEmail());
		companyDetailDTO.setCompanyName(company.getCompanyName());
		companyDetailDTO.setPosition(company.getPosition());

		return companyDetailDTO;
	}

	// 일반회원정보 수정
	@Override
	@Transactional
	public void modifyMemberInfo(String memId, MemberModifyDTO memberModifyDTO) {
		Member member = memberRepository.findById(memId).orElseThrow();
		member.setPw(passwordEncoder.encode(memberModifyDTO.getPw()));
		member.setName(memberModifyDTO.getName());
		member.setEmail(memberModifyDTO.getEmail());
		member.setPhone(memberModifyDTO.getPhone());
		member.setAddr(memberModifyDTO.getAddr());
		member.setAddrDetail(memberModifyDTO.getAddrDetail());
		member.setCheckSms(memberModifyDTO.isCheckSms());
		member.setCheckEmail(memberModifyDTO.isCheckEmail());

		memberRepository.save(member);
	}

	// 학생회원정보 수정
	@Override
	@Transactional
	public void modifyStudentInfo(String memId, StudentModifyDTO studentModifyDTO) {
		Member member = memberRepository.findById(memId).orElseThrow();
		member.setPw(passwordEncoder.encode(studentModifyDTO.getPw()));
		member.setName(studentModifyDTO.getName());
		member.setEmail(studentModifyDTO.getEmail());
		member.setPhone(studentModifyDTO.getPhone());
		member.setAddr(studentModifyDTO.getAddr());
		member.setAddrDetail(studentModifyDTO.getAddrDetail());
		member.setCheckSms(studentModifyDTO.isCheckSms());
		member.setCheckEmail(studentModifyDTO.isCheckEmail());

		memberRepository.save(member);

		Student student = studentRepository.findById(memId).orElseThrow();

		student.setSchoolName(studentModifyDTO.getSchoolName());
		student.setMember(member);
		student.setMemId(member.getMemId());

		studentRepository.save(student);
	}

	// 교사회원정보 수정
	@Override
	@Transactional
	public void modifyTeacherInfo(String memId, TeacherModifyDTO teacherModifyDTO) {
		Member member = memberRepository.findById(memId).orElseThrow();
		member.setPw(passwordEncoder.encode(teacherModifyDTO.getPw()));
		member.setName(teacherModifyDTO.getName());
		member.setEmail(teacherModifyDTO.getEmail());
		member.setPhone(teacherModifyDTO.getPhone());
		member.setAddr(teacherModifyDTO.getAddr());
		member.setAddrDetail(teacherModifyDTO.getAddrDetail());
		member.setCheckSms(teacherModifyDTO.isCheckSms());
		member.setCheckEmail(teacherModifyDTO.isCheckEmail());

		memberRepository.save(member);

		Teacher teacher = teacherRepository.findById(memId).orElseThrow();

		teacher.setSchoolName(teacherModifyDTO.getSchoolName());
		teacher.setMember(member);
		teacher.setMemId(member.getMemId());

		teacherRepository.save(teacher);
	}

	// 기업회원정보 수정
	@Override
	@Transactional
	public void modifyCompanyInfo(String memId, CompanyModifyDTO companyModifyDTO) {
		Member member = memberRepository.findById(memId).orElseThrow();
		member.setPw(passwordEncoder.encode(companyModifyDTO.getPw()));
		member.setName(companyModifyDTO.getName());
		member.setEmail(companyModifyDTO.getEmail());
		member.setPhone(companyModifyDTO.getPhone());
		member.setAddr(companyModifyDTO.getAddr());
		member.setAddrDetail(companyModifyDTO.getAddrDetail());
		member.setCheckSms(companyModifyDTO.isCheckSms());
		member.setCheckEmail(companyModifyDTO.isCheckEmail());

		memberRepository.save(member);

		Company company = companyRepository.findById(memId).orElseThrow();

		company.setCompanyName(companyModifyDTO.getCompanyName());
		company.setPosition(companyModifyDTO.getPosition());
		company.setMember(member);
		company.setMemId(member.getMemId());

		companyRepository.save(company);
	}

	// 회원 탈퇴
	@Override
	public void leaveMember(String memId) {
		Member member = memberRepository.findById(memId).orElseThrow();
		
		// 현재 예약중인 시설, 행사, 실증대여 있을시 탈퇴불가 처리
		
		member.setState(MemberState.LEAVE);
		memberRepository.save(member);
	}
}
