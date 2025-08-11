package com.EduTech.service.admin;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.admin.AdminMemberViewReqDTO;
import com.EduTech.dto.admin.AdminMemberViewResDTO;
import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalReqDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationSearchDTO;
import com.EduTech.entity.admin.BannerImage;
import com.EduTech.entity.member.MemberState;

public interface AdminService {
	void approveOrRejectDemRes(DemonstrationApprovalResDTO demonstrationApprovalResDTO); // 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	void approveOrRejectDemReg(DemonstrationApprovalRegDTO demonstrationApprovalRegDTO); // 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	void approveOrRejectDemReq(DemonstrationApprovalReqDTO demonstrationApprovalReqDTO); // 반납 / 대여 연기 신청 업데이트 기능
	void sendMessageForUser(AdminMessageDTO adminMessageDTO); // 관리자가 회원들에게 메시지를 보내는 기능
	PageResponseDTO<AdminMemberViewResDTO> adminViewMembers(AdminMemberViewReqDTO adminMemberViewReqDTO,Integer pageCount); // 관리자 회원 목록 페이지 조회 기능
	void MemberStateChange(List<String> memId,MemberState memberState); // 관리자가 회원 상태 수정하는 기능
	
	List<BannerImage> uploadBanners(List<MultipartFile> files);	// 관리자 메인페이지의 배너 등록 기능
	List<BannerImage> getAllBanners();	// 배너 조회
	void deleteBanner(Long bannerNum);	// 배너 삭제
	void updateBannerSequence(List<Long> bannerNums);	// 배너 순서 수정
	PageResponseDTO<DemonstrationListReserveDTO> getAllDemRes(DemonstrationSearchDTO searchDTO); // 실증 교사 신청목록 조회 기능 (검색도 같이 구현할 것임.)
	PageResponseDTO<DemonstrationListRegistrationDTO> getAllDemReg(DemonstrationSearchDTO searchDTO); // 실증 기업 신청목록 조회 가능(검색도 같이 구현할 것임.)
}