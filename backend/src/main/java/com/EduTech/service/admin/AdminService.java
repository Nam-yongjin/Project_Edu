package com.EduTech.service.admin;

import java.util.List;

import com.EduTech.dto.admin.AdminMemberViewReqDTO;
import com.EduTech.dto.admin.AdminMemberViewResDTO;
import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;

public interface AdminService {
	void approveOrRejectDemRes(DemonstrationApprovalResDTO demonstrationApprovalResDTO); // 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	void approveOrRejectDemReg(DemonstrationApprovalRegDTO demonstrationApprovalRegDTO); // 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	void sendMessageForUser(AdminMessageDTO adminMessageDTO); // 관리자가 회원들에게 메시지를 보내는 기능
	List<AdminMemberViewResDTO> adminViewMembers(AdminMemberViewReqDTO adminMemberViewReqDTO); // 관리자 회원 목록 페이지 조회 기능
}