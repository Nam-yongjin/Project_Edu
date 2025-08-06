package com.EduTech.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.admin.AdminMemberViewReqDTO;
import com.EduTech.dto.admin.AdminMemberViewResDTO;
import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.entity.member.MemberState;
import com.EduTech.service.admin.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
	private final AdminService adminService;
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/ResState")
	public ResponseEntity<String> DemResStateChange(
			@RequestBody DemonstrationApprovalResDTO demonstrationApprovalResDTO) {
		adminService.approveOrRejectDemRes(demonstrationApprovalResDTO);
		return ResponseEntity.ok("Res 상태 변경 성공");
	}

	// 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/RegState")
	public ResponseEntity<String> DemRegStateChange(
			@RequestBody DemonstrationApprovalRegDTO demonstrationApprovalRegDTO) {
		adminService.approveOrRejectDemReg(demonstrationApprovalRegDTO);
		return ResponseEntity.ok("Reg 상태 변경 성공");
	}

	// 관리자가 메시지 보내는 기능
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/sendMessage")
	public ResponseEntity<String> SendMessage(@ModelAttribute AdminMessageDTO adminMessageDTO) {
		adminService.sendMessageForUser(adminMessageDTO);
		return ResponseEntity.ok("메시지 전송 성공!");
	}

	// 관리자가 회원 정보 조회하는 기능
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/members")
	public PageResponseDTO<AdminMemberViewResDTO> adminViewMembers(AdminMemberViewReqDTO adminMemberViewDTO,
			@RequestParam("pageCount") Integer pageCount) {
		PageResponseDTO<AdminMemberViewResDTO> members = adminService.adminViewMembers(adminMemberViewDTO, pageCount);
		return members;
	}

	// 관리자가 회원 상태 수정하는 기능
	// 블랙리스트, 삭제 등등..
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/MemberStateChange")
	public ResponseEntity<String> memberStateChange(@RequestParam("memId") List<String> memId,
			@RequestParam("state") MemberState state) {
		adminService.MemberStateChange(memId, state);
		return ResponseEntity.ok("회원 상태 수정 완료");
	}
}
