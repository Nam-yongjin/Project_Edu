package com.EduTech.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
	private final AdminService AdminService;
	@PutMapping("/ResState")
	public ResponseEntity<String> DemResStateChange(
			@RequestBody DemonstrationApprovalResDTO demonstrationApprovalResDTO) {
		AdminService.approveOrRejectDemRes(demonstrationApprovalResDTO);
		return ResponseEntity.ok("Res 상태 변경 성공");
	}

	// 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	@PutMapping("/RegState")
	public ResponseEntity<String> DemRegStateChange(
			@RequestBody DemonstrationApprovalRegDTO demonstrationApprovalRegDTO) {
		AdminService.approveOrRejectDemReg(demonstrationApprovalRegDTO);
		return ResponseEntity.ok("Reg 상태 변경 성공");
	}
	
	// 관리자가 메시지 보내는 기능
	@PostMapping("/sendMessage")
	public ResponseEntity<String> SendMessage(@ModelAttribute AdminMessageDTO adminMessageDTO)
	{
		return ResponseEntity.ok("메시지 전송 성공!");
	}
	
	// 관리자가 회원 정보 조회하는 기능
	@GetMapping("/members")
	public PageResponseDTO<AdminMemberViewResDTO> adminViewMembers(AdminMemberViewReqDTO adminMemberViewDTO,@RequestParam("pageCount") Integer pageCount)
	{
		PageResponseDTO<AdminMemberViewResDTO> members=AdminService.adminViewMembers(adminMemberViewDTO,pageCount);
		return members;
	}
	
	// 관리자가 회원 상태 수정하는 기능
	// 블랙리스트, 삭제 등등..
	@PutMapping("/MemberStateChange")
	public ResponseEntity<String> memberStateChange(@RequestParam("memId") List<String> memId,@RequestParam("state") MemberState state)
	{
		
		return ResponseEntity.ok("회원 상태 수정 완료");
	}
	

	
	// 관리자 공지사항 글쓰기 기능
	
	// 관리자 언론보도 글쓰기 기능
	
	// 관리자 문의 사항 답변 기능
	
	// 행사 등록 (보류)
	
	// 좌석 등록 (보류)
	
	

}
