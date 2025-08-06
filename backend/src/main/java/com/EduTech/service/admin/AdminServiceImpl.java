package com.EduTech.service.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.admin.AdminMemberViewReqDTO;
import com.EduTech.dto.admin.AdminMemberViewResDTO;
import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.member.MemberSpecs;
import com.EduTech.service.mail.MailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {
	@Autowired
	DemonstrationReserveRepository demonstrationReserveRepository;
	@Autowired
	DemonstrationRegistrationRepository demonstrationRegistrationRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	MailService mailService;

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
		demonstrationRegistrationRepository.updateDemRegChangeState(demonstrationApprovalRegDTO.getDemonstrationState(),
				demonstrationApprovalRegDTO.getMemId(), demonstrationApprovalRegDTO.getDemRegNum());
	}

	// 관리자가 회원들에게 메시지 보내는 기능
	@Override
	public void sendMessageForUser(AdminMessageDTO adminMessageDTO) {
		mailService.sendSimpleMailMessage(adminMessageDTO);
	}

	// 관리자 회원 목록 페이지 조회 기능 (+정렬기준, 정렬방향 추가)
	@Override
	public PageResponseDTO<AdminMemberViewResDTO> adminViewMembers(AdminMemberViewReqDTO adminMemberViewReqDTO,
			Integer pageCount) {
		// Specification은 동적 쿼리지만, 엔티티로만 조회 가능하다.
		// 직접 DTO로 받고 싶다면 QueryDSL 같은 방식으로 바꿔야 한다.

		String sortField = adminMemberViewReqDTO.getSortField() != null ? adminMemberViewReqDTO.getSortField()
				: "createdAt";
		Direction direction = adminMemberViewReqDTO.getSortDirection() != null
				&& adminMemberViewReqDTO.getSortDirection().equalsIgnoreCase("ASC") ? Direction.ASC : Direction.DESC;

		// 정렬 정보를 담은 Sort 객체 생성
		Sort sort = Sort.by(direction, sortField);

		// DTO로 받을 리스트
		List<AdminMemberViewResDTO> dtoList = new ArrayList<>();

		// 기본 조건을 null로
		Specification<Member> spec = Specification.where(null);

		// 아이디, 이름, 이메일 경우는 하나씩만 선택 가능하므로
		if (adminMemberViewReqDTO.getMemId() != null && !adminMemberViewReqDTO.getMemId().isBlank()) {
			spec = spec.and(MemberSpecs.memIdContains(adminMemberViewReqDTO.getMemId()));
		} else if (adminMemberViewReqDTO.getName() != null && !adminMemberViewReqDTO.getName().isBlank()) {
			spec = spec.and(MemberSpecs.nameContains(adminMemberViewReqDTO.getName()));
		} else if (adminMemberViewReqDTO.getEmail() != null && !adminMemberViewReqDTO.getEmail().isBlank()) {
			spec = spec.and(MemberSpecs.emailContains(adminMemberViewReqDTO.getEmail()));
		} else if (adminMemberViewReqDTO.getPhone() != null && !adminMemberViewReqDTO.getPhone().isBlank()) {
            spec = spec.and(MemberSpecs.phoneContains(adminMemberViewReqDTO.getPhone()));
        }

		// role, state 조건은 따로
		if (adminMemberViewReqDTO.getRole() != null) {
			spec = spec.and(MemberSpecs.hasRole(adminMemberViewReqDTO.getRole()));
		}
		if (adminMemberViewReqDTO.getState() != null) {
			spec = spec.and(MemberSpecs.hasState(adminMemberViewReqDTO.getState()));
		}

		// 기본 Pageable (페이지, 사이즈는 클라이언트에서 넘긴다고 가정)
		Pageable pageable = PageRequest.of(pageCount, 10, sort);

		Page<Member> memberPage = memberRepository.findAll(spec, pageable);

		for (Member member : memberPage) {
			// Member로부터 필요한 값만 받을 DTO
			// 밖에 선언하면 얕은 참조가 일어나 마지막 값으로
			// 리스트가 채워짐
			AdminMemberViewResDTO adminMemberViewResDTO = new AdminMemberViewResDTO();
			adminMemberViewResDTO.setMemId(member.getMemId());
			adminMemberViewResDTO.setName(member.getName());
			adminMemberViewResDTO.setPhone(member.getPhone());
			adminMemberViewResDTO.setEmail(member.getEmail());
			adminMemberViewResDTO.setCreatedAt(member.getCreatedAt());
			adminMemberViewResDTO.setRole(member.getRole());
			adminMemberViewResDTO.setState(member.getState());

			dtoList.add(adminMemberViewResDTO);
		}

		return new PageResponseDTO<AdminMemberViewResDTO>(dtoList, memberPage.getTotalPages(), memberPage.getNumber());
	}

	// 관리자가 회원 상태 수정하는 기능
	@Override
	public void MemberStateChange(List<String> memId, MemberState memberState) {
		memberRepository.updateMemberState(memberState, memId);

	}

}
