package com.EduTech.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
