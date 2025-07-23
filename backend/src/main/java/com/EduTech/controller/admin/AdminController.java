package com.EduTech.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.admin.AdminMemberViewReqDTO;
import com.EduTech.dto.admin.AdminMemberViewResDTO;
import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
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
	public List<AdminMemberViewResDTO> adminViewMembers(AdminMemberViewReqDTO adminMemberViewDTO)
	{
		List<AdminMemberViewResDTO> hello=new ArrayList<>();
		AdminService.adminViewMembers(adminMemberViewDTO);
		return hello;
	}
	/*
	// 관리자 페이지에서 회원 관리하는 기능
	@GetMapping("/members")
	public 
	
	*/
}
