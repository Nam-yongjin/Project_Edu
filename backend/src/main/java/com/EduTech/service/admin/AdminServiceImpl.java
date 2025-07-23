package com.EduTech.service.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.admin.AdminMemberViewReqDTO;
import com.EduTech.dto.admin.AdminMemberViewResDTO;
import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {
	@Autowired 
	DemonstrationReserveRepository demonstrationReserveRepository;
	@Autowired
	DemonstrationRegistrationRepository demonstrationRegistrationRepository;

	// 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	// 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
		// String memId = JWTFilter.getMemId(); 나중에 로그인 구현되면 추가
		@Override
		public void approveOrRejectDemRes(DemonstrationApprovalResDTO demonstrationApprovalResDTO) {
			demonstrationReserveRepository.updateDemResChangeState(demonstrationApprovalResDTO.getDemonstrationState(),
					demonstrationApprovalResDTO.getMemId(), demonstrationApprovalResDTO.getDemRevNum());
		}

		// 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
		@Override
		public void approveOrRejectDemReg(DemonstrationApprovalRegDTO demonstrationApprovalRegDTO) {
			demonstrationRegistrationRepository.updateDemResChangeState(demonstrationApprovalRegDTO.getDemonstrationState(),
					demonstrationApprovalRegDTO.getMemId(), demonstrationApprovalRegDTO.getDemRegNum());
		}
		
		// 관리자가 회원들에게 메시지 보내는 기능
		@Override
		public void sendMessageForUser(AdminMessageDTO adminMessageDTO) {
			
		}
		
		// 관리자 회원 목록 페이지 조회 기능
		@Override
		public List<AdminMemberViewResDTO> adminViewMembers(AdminMemberViewReqDTO adminMemberViewReqDTO) {
			List<AdminMemberViewResDTO> hello=new ArrayList<>();
			return hello;
		}
		
		/*
		public List<Member> searchMembers(String searchType, String search, MemberRole role, MemberState state) {
		    Specification<Member> spec = Specification.where(null);

		    if (search != null && !search.isEmpty()) {
		        switch (searchType) {
		            case "id":
		                spec = spec.and(MemberSpecs.memIdContains(search));
		                break;
		            case "name":
		                spec = spec.and(MemberSpecs.nameContains(search));
		                break;
		            case "email":
		                spec = spec.and(MemberSpecs.emailContains(search));
		                break;
		            default:
		                spec = spec.and(
		                    Specification.where(MemberSpecs.memIdContains(search))
		                        .or(MemberSpecs.nameContains(search))
		                        .or(MemberSpecs.emailContains(search))
		                );
		        }
		    }

		    if (role != null) {
		        spec = spec.and(MemberSpecs.hasRole(role));
		    }
		    if (state != null) {
		        spec = spec.and(MemberSpecs.hasState(state));
		    }

		    return memberRepository.findAll(spec);
		} */
}
