package com.EduTech.controller.member;

import java.util.Map;
import java.util.NoSuchElementException;

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
import com.EduTech.dto.member.MemberDTO;
import com.EduTech.dto.member.MemberDetailDTO;
import com.EduTech.dto.member.MemberModifyDTO;
import com.EduTech.dto.member.MemberRegisterDTO;
import com.EduTech.dto.member.MemberResetPwDTO;
import com.EduTech.dto.member.StudentDetailDTO;
import com.EduTech.dto.member.StudentModifyDTO;
import com.EduTech.dto.member.StudentRegisterDTO;
import com.EduTech.dto.member.TeacherDetailDTO;
import com.EduTech.dto.member.TeacherModifyDTO;
import com.EduTech.dto.member.TeacherRegisterDTO;
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.security.jwt.JWTProvider;
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
		return ResponseEntity.ok("회원가입이 완료되었습니다.");
	}

	// 학생회원 회원가입
	@PostMapping("/register/student")
	public ResponseEntity<String> registerStudent(@RequestBody @Valid StudentRegisterDTO studentRegisterDTO) {
		memberService.registerStudent(studentRegisterDTO);
		return ResponseEntity.ok("회원가입이 완료되었습니다.");
	}

	// 교사회원 회원가입
	@PostMapping("/register/teacher")
	public ResponseEntity<String> registerTeacher(@RequestBody @Valid TeacherRegisterDTO teacherRegisterDTO) {
		memberService.registerTeacher(teacherRegisterDTO);
		return ResponseEntity.ok("회원가입이 완료되었습니다.");
	}

	// 기업회원 회원가입
	@PostMapping("/register/company")
	public ResponseEntity<String> registerCompany(@RequestBody @Valid CompanyRegisterDTO companyRegisterDTO) {
		memberService.registerCompany(companyRegisterDTO);
		return ResponseEntity.ok("회원가입이 완료되었습니다.");
	}

	// 아이디 중복 체크
	@GetMapping("/checkId")
	public ResponseEntity<Boolean> checkDuplicateId(@RequestParam("memId") String memId) {
		return ResponseEntity.ok(memberService.isDuplicatedId(memId));
	}

	// 일반회원 상세정보
	@GetMapping("/member/myInfo")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<MemberDetailDTO> memberInfo() {
		String memId = JWTFilter.getMemId();
		return ResponseEntity.ok(memberService.readMemberInfo(memId));
	}

	// 학생회원 상세정보
	@GetMapping("/student/myInfo")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<StudentDetailDTO> studentInfo() {
		String memId = JWTFilter.getMemId();
		return ResponseEntity.ok(memberService.readStudentInfo(memId));
	}

	// 교사회원 상세정보
	@GetMapping("/teacher/myInfo")
	@PreAuthorize("hasRole('TEACHER')")
	public ResponseEntity<TeacherDetailDTO> teacherInfo() {
		String memId = JWTFilter.getMemId();
		return ResponseEntity.ok(memberService.readTeacherInfo(memId));
	}

	// 기업회원 상세정보
	@GetMapping("/company/myInfo")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<CompanyDetailDTO> companyInfo() {
		String memId = JWTFilter.getMemId();
		return ResponseEntity.ok(memberService.readCompanyInfo(memId));
	}

	// 일반회원 정보수정
	@PutMapping("/member/modify")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> modifyMember(@RequestBody @Valid MemberModifyDTO memberModifyDTO) {
		String memId = JWTFilter.getMemId();
		memberService.modifyMemberInfo(memId, memberModifyDTO);
		return ResponseEntity.ok("회원정보가 수정되었습니다.");
	}

	// 학생회원 정보수정
	@PutMapping("/student/modify")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<String> modifyStudent(@RequestBody @Valid StudentModifyDTO studentModifyDTO) {
		String memId = JWTFilter.getMemId();
		memberService.modifyStudentInfo(memId, studentModifyDTO);
		return ResponseEntity.ok("회원정보가 수정되었습니다.");
	}

	// 교사회원 정보수정
	@PutMapping("/teacher/modify")
	@PreAuthorize("hasRole('TEACHER')")
	public ResponseEntity<String> modifyTeacher(@RequestBody @Valid TeacherModifyDTO teacherModifyDTO) {
		String memId = JWTFilter.getMemId();
		memberService.modifyTeacherInfo(memId, teacherModifyDTO);
		return ResponseEntity.ok("회원정보가 수정되었습니다.");
	}

	// 기업회원 정보수정
	@PutMapping("/company/modify")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<String> modifyCompany(@RequestBody @Valid CompanyModifyDTO companyModifyDTO) {
		String memId = JWTFilter.getMemId();
		memberService.modifyCompanyInfo(memId, companyModifyDTO);
		return ResponseEntity.ok("회원정보가 수정되었습니다.");
	}

	// 회원 탈퇴
	@DeleteMapping("/member/leave")
	public ResponseEntity<String> leaveMember() {
		String memId = JWTFilter.getMemId();
		memberService.leaveMember(memId);
		return ResponseEntity.ok("회원탈퇴가 완료되었습니다. 일주일뒤 재가입 가능합니다.");
	}

	// 아이디 찾기
	@GetMapping("/findId")
	public ResponseEntity<String> findId(@RequestParam("phone") String phone) {
		String memId = memberService.findId(phone);
		if (memId != null) {
			return ResponseEntity.ok(memId);
		} else {
			throw new NoSuchElementException("해당 전화번호로 등록된 아이디가 없습니다.");
		}
	}

	// 비밀번호 찾기(변경)
	@PutMapping("/resetPw")
	public ResponseEntity<String> resetPw(@RequestBody MemberResetPwDTO memberResetPwDTO) {
		memberService.resetPw(memberResetPwDTO.getMemId(), memberResetPwDTO);
		return ResponseEntity.ok("비밀번호가 변경되었습니다.");
	}

	// 카카오 로그인
	@GetMapping("/login/kakao")
	public Map<String, Object> getMemberFromKakao(@RequestParam("accessToken") String accessToken) {
		MemberDTO memberDTO = memberService.getKakaoMember(accessToken);
		Map<String, Object> claims = memberDTO.getClaims();
		String jwtAccessToken = JWTProvider.generateToken(claims, 10);
		String jwtRefreshToken = JWTProvider.generateToken(claims, 60 * 24);
		claims.put("accessToken", jwtAccessToken);
		claims.put("refreshToken", jwtRefreshToken);
		return claims;
	}
}
