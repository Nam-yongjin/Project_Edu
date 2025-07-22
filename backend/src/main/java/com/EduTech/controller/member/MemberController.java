package com.EduTech.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.service.member.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

	private final MemberService memberService;

	// 일반회원 회원가입
	@PostMapping("/register/member")
	public ResponseEntity<String> registerMember(@RequestBody @Valid MemberRegisterDTO memberRegisterDTO) {
		memberService.registerMember(memberRegisterDTO);
		return ResponseEntity.ok("일반 회원가입이 완료되었습니다.");
	}

	// 학생회원 회원가입
	@PostMapping("/register/student")
	public ResponseEntity<String> registerStudent(@RequestBody @Valid StudentRegisterDTO studentRegisterDTO) {
		memberService.registerStudent(studentRegisterDTO);
		return ResponseEntity.ok("학생 회원가입이 완료되었습니다.");
	}

	// 교사회원 회원가입
	@PostMapping("/register/teacher")
	public ResponseEntity<String> registerTeacher(@RequestBody @Valid TeacherRegisterDTO teacherRegisterDTO) {
		memberService.registerTeacher(teacherRegisterDTO);
		return ResponseEntity.ok("교사 회원가입이 완료되었습니다.");
	}

	// 기업회원 회원가입
	@PostMapping("/register/company")
	public ResponseEntity<String> registerCompany(@RequestBody @Valid CompanyRegisterDTO companyRegisterDTO) {
		memberService.registerCompany(companyRegisterDTO);
		return ResponseEntity.ok("기업 회원가입이 완료되었습니다.");
	}

	// 아이디 중복 체크
	@GetMapping("/checkId")
	public ResponseEntity<Boolean> checkDuplicateId(@RequestParam String memId) {
		boolean isDuplicated = memberService.isDuplicatedId(memId);
		return ResponseEntity.ok(isDuplicated);
	}

	// 일반회원 상세정보
	@GetMapping("/member/myInfo")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<MemberDetailDTO> memberInfo() {
		String memId = JWTFilter.getMemId();
		MemberDetailDTO memberDetailDTO = memberService.readMemberInfo(memId);
		return ResponseEntity.ok(memberDetailDTO);
	}

	// 학생회원 상세정보
	@GetMapping("/student/myInfo")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<StudentDetailDTO> studentInfo() {
		String memId = JWTFilter.getMemId();
		StudentDetailDTO studentDetailDTO = memberService.readStudentInfo(memId);
		return ResponseEntity.ok(studentDetailDTO);
	}

	// 교사회원 상세정보
	@GetMapping("/teacher/myInfo")
	@PreAuthorize("hasRole('TEACHER')")
	public ResponseEntity<TeacherDetailDTO> teacherInfo() {
		String memId = JWTFilter.getMemId();
		TeacherDetailDTO teacherDetailDTO = memberService.readTeacherInfo(memId);
		return ResponseEntity.ok(teacherDetailDTO);
	}

	// 기업회원 상세정보
	@GetMapping("/company/myInfo")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<CompanyDetailDTO> companyInfo() {
		String memId = JWTFilter.getMemId();
		CompanyDetailDTO companyDetailDTO = memberService.readCompanyInfo(memId);
		return ResponseEntity.ok(companyDetailDTO);
	}

	// 일반회원 정보수정
	@PutMapping("/modify/member")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> modifyMember(@RequestBody @Valid MemberModifyDTO memberModifyDTO) {
		String memId = JWTFilter.getMemId();
		memberService.modifyMemberInfo(memId, memberModifyDTO);
		return ResponseEntity.ok("회원정보가 수정되었습니다.");
	}

	// 학생회원 정보수정
	@PutMapping("/modify/student")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<String> modifyStudent(@RequestBody @Valid StudentModifyDTO studentModifyDTO) {
		String memId = JWTFilter.getMemId();
		memberService.modifyStudentInfo(memId, studentModifyDTO);
		return ResponseEntity.ok("회원정보가 수정되었습니다.");
	}

	// 교사회원 정보수정
	@PutMapping("/modify/teacher")
	@PreAuthorize("hasRole('TEACHER')")
	public ResponseEntity<String> modifyTeacher(@RequestBody @Valid TeacherModifyDTO teacherModifyDTO) {
		String memId = JWTFilter.getMemId();
		memberService.modifyTeacherInfo(memId, teacherModifyDTO);
		return ResponseEntity.ok("회원정보가 수정되었습니다.");
	}

	// 기업회원 정보수정
	@PutMapping("/modify/company")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<String> modifyCompany(@RequestBody @Valid CompanyModifyDTO companyModifyDTO) {
		String memId = JWTFilter.getMemId();
		memberService.modifyCompanyInfo(memId, companyModifyDTO);
		return ResponseEntity.ok("회원정보가 수정되었습니다.");
	}

	// 회원 탈퇴
	@DeleteMapping("/leave")
	public ResponseEntity<String> leaveMember() {
		String memId = JWTFilter.getMemId();
		memberService.leaveMember(memId);
		return ResponseEntity.ok("회원탈퇴가 완료되었습니다. 일주일뒤 재가입 가능합니다.");
	}
}
