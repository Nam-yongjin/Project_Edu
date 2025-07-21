package com.EduTech.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.member.CompanyRegisterDTO;
import com.EduTech.dto.member.MemberRegisterDTO;
import com.EduTech.dto.member.StudentRegisterDTO;
import com.EduTech.dto.member.TeacherRegisterDTO;
import com.EduTech.service.member.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
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
}
