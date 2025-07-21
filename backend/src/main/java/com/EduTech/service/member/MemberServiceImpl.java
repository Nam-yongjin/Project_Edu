package com.EduTech.service.member;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.member.CompanyRegisterDTO;
import com.EduTech.dto.member.MemberRegisterDTO;
import com.EduTech.dto.member.StudentRegisterDTO;
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
	private final TeacherRepository teacherRepository ;
	private final CompanyRepository companyRepository ;
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
		return memberRepository.existsByMemId(memId);
	}
}
