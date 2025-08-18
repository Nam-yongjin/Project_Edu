package com.EduTech.service.admin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.admin.AdminMemberViewReqDTO;
import com.EduTech.dto.admin.AdminMemberViewResDTO;
import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalReqDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.dto.demonstration.DemonstrationImageDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationSearchDTO;
import com.EduTech.dto.demonstration.ResRequestDTO;
import com.EduTech.entity.admin.BannerImage;
import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationRequest;
import com.EduTech.entity.demonstration.DemonstrationReserve;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.demonstration.RequestType;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.admin.BannerImageRepository;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationSpecs;
import com.EduTech.repository.demonstration.DemonstrationRequestRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveSpecs;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.member.MemberSpecs;
import com.EduTech.service.mail.MailService;
import com.EduTech.util.FileUtil;

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
	@Autowired
	DemonstrationImageRepository demonstrationImageRepository;
	@Autowired
	DemonstrationRequestRepository demonstrationRequestRepository;
	private final FileUtil fileUtil;
	private final BannerImageRepository bannerImageRepository;

	// 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	// String memId = JWTFilter.getMemId(); 나중에 로그인 구현되면 추가
	
	@Override
	public void approveOrRejectDemRes(DemonstrationApprovalResDTO demonstrationApprovalResDTO) {
		demonstrationReserveRepository.updateDemResChangeStateRev(demonstrationApprovalResDTO.getState(), demonstrationApprovalResDTO.getDemRevNum());
	}

	// 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	@Override
	public void approveOrRejectDemReg(DemonstrationApprovalRegDTO demonstrationApprovalRegDTO) {
		demonstrationRegistrationRepository.updateDemRegChangeStateReg(demonstrationApprovalRegDTO.getState(),demonstrationApprovalRegDTO.getDemRegNum());
	}

	// 연장 반납 신청 처리하는 기능
	@Override
	public void approveOrRejectDemReq(DemonstrationApprovalReqDTO demonstrationApprovalReqDTO) {
		System.out.println(demonstrationApprovalReqDTO);
		if(demonstrationApprovalReqDTO.getType().equals(RequestType.EXTEND)&&demonstrationApprovalReqDTO.getState().equals(DemonstrationState.ACCEPT))
		{
			DemonstrationRequest request=demonstrationRequestRepository.selectRequest(demonstrationApprovalReqDTO.getDemRevNum(),DemonstrationState.WAIT);
			demonstrationReserveRepository.updateDemResEndDate(request.getReserve().getDemRevNum(),request.getUpdateDate(),DemonstrationState.ACCEPT);
		}
		else if(demonstrationApprovalReqDTO.getType().equals(RequestType.RENTAL)&&demonstrationApprovalReqDTO.getState().equals(DemonstrationState.ACCEPT))
		{
			demonstrationReserveRepository.updateDemResChangeStateRev(DemonstrationState.EXPIRED,demonstrationApprovalReqDTO.getDemRevNum());
		}
		demonstrationRequestRepository.updateDemResChangeStateReq(demonstrationApprovalReqDTO.getState(), demonstrationApprovalReqDTO.getDemRevNum(),demonstrationApprovalReqDTO.getType(),DemonstrationState.WAIT);
	}
	// 관리자가 회원들에게 메시지 보내는 기능
	@Override
	public void sendMessageForUser(AdminMessageDTO adminMessageDTO) {
		mailService.sendMimeMessage(adminMessageDTO);
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

		return new PageResponseDTO<AdminMemberViewResDTO>(dtoList, memberPage.getTotalPages(), memberPage.getNumber(),memberPage.getTotalElements());
	}

	// 관리자가 회원 상태 수정하는 기능
	@Override
	public void MemberStateChange(List<String> memId, MemberState memberState) {
		memberRepository.updateMemberState(memberState, memId);

	}

	// 관리자 메인페이지의 배너 등록 기능
	@Transactional
	public List<BannerImage> uploadBanners(List<MultipartFile> files) {

		List<Object> savedFiles = fileUtil.saveFiles(files, "banners");

		int lastSequence = bannerImageRepository.findAll().size();
		List<BannerImage> newBanners = new ArrayList<>();

		for (Object info : savedFiles) {
			Map<String, String> map = (Map<String, String>) info;
			lastSequence++;
			BannerImage banner = BannerImage.builder()
					.originalName(map.get("originalName"))
					.imagePath(map.get("filePath"))
					.sequence(lastSequence)
					.build();
			newBanners.add(banner);
		}
		return bannerImageRepository.saveAll(newBanners);
	}

	// 배너 조회
	public List<BannerImage> getAllBanners() {
		return bannerImageRepository.findAll(Sort.by(Sort.Direction.ASC, "sequence"));
	}

	// 배너 삭제
	@Transactional
	public void deleteBanner(Long bannerNum) {
		BannerImage banner = bannerImageRepository.findById(bannerNum)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다."));

		fileUtil.deleteFiles(List.of(banner.getImagePath()));

		bannerImageRepository.deleteById(bannerNum);
	}

	// 배너 수정
	@Transactional
	public void updateBannerSequence(List<Long> bannerNums) {
		for (int i = 0; i < bannerNums.size(); i++) {
            final int sequence = i + 1;
            Long bannerNum = bannerNums.get(i);
            
            bannerImageRepository.findById(bannerNum).ifPresent(banner -> {
                banner.setSequence(sequence);
                bannerImageRepository.save(banner);
            });
        }
	}

	// 실증 기업 신청목록 조회 기능 (검색도 같이 구현할 것임.) - 관리자용
		@Override
		public PageResponseDTO<DemonstrationListRegistrationDTO> getAllDemReg(DemonstrationSearchDTO searchDTO) {
			  Integer pageCount = searchDTO.getPageCount();
			    String type = searchDTO.getType();
			    String search = searchDTO.getSearch();
			    String sortBy = searchDTO.getSortBy();
			    String sort = searchDTO.getSort();
			    String statusFilter = searchDTO.getStatusFilter();

			    if (pageCount == null || pageCount < 0)
			        pageCount = 0;
			    if (!StringUtils.hasText(sortBy))
			        sortBy = "regDate";
			    if (!StringUtils.hasText(sort))
			        sort = "desc";

			    Pageable pageable = PageRequest.of(pageCount, 10);

			    Specification<DemonstrationRegistration>spec = DemonstrationRegistrationSpecs.withSearchAndSortAdmin(type,search,sortBy,sort,statusFilter);

			    // DemonstrationReserve 페이징 조회
			    Page<DemonstrationRegistration> regPage = demonstrationRegistrationRepository.findAll(spec, pageable);

			    // 관련 demNum 리스트 추출 (중복 제거)
			    List<Long> demNums = regPage.stream()
			        .map(reg -> reg.getDemonstration().getDemNum())
			        .distinct()
			        .toList();

			    // 이미지 리스트 일괄 조회 (demNum 기준)
			    List<DemonstrationImageDTO> images = demonstrationImageRepository.selectDemImageIn(demNums);
			    Map<Long, List<DemonstrationImageDTO>> demNumToImages = images.stream()
			        .collect(Collectors.groupingBy(DemonstrationImageDTO::getDemNum));

			    // reserves에서 member.memId 리스트 추출 (중복 제거)
			    List<String> memIds = regPage.stream()
			        .map(reg -> reg.getMember().getMemId())
			        .distinct()
			        .toList();

			    // memberRepository에서 memId 리스트로 Member 리스트 조회
			    List<Member> members = memberRepository.findByMemIdIn(memIds);
			    Map<String, Member> memIdToMember = members.stream()
			        .collect(Collectors.toMap(Member::getMemId, m -> m));

			    // DTO 매핑
			    Page<DemonstrationListRegistrationDTO> dtoPage = regPage.map(reg -> {
			        Demonstration dem = reg.getDemonstration();

			        DemonstrationListRegistrationDTO dto = new DemonstrationListRegistrationDTO();

			        dto.setDemRegNum(reg.getDemRegNum());
			        dto.setRegDate(reg.getRegDate());
			        dto.setExpDate(reg.getExpDate());
			        dto.setState(reg.getState());
			        dto.setMemId(reg.getMember().getMemId());
			      
			        // Member 정보 가져오기
			        Member member = memIdToMember.get(reg.getMember().getMemId());
			        if (member != null) {
			            if (member.getCompany() != null) {
			                dto.setCompanyName(member.getCompany().getCompanyName());
			            }
			            dto.setAddr(member.getAddr());
			            dto.setPhone(member.getPhone());
			        }

			        if (dem != null) {
			            dto.setDemName(dem.getDemName());
			            dto.setItemNum(dem.getItemNum());
			            dto.setDemNum(dem.getDemNum());
			            dto.setImageList(demNumToImages.getOrDefault(dem.getDemNum(), List.of()));
			        }

			        return dto;
			    });

			    return new PageResponseDTO<>(dtoPage);
			}
		
		
		// 실증 교사 신청목록 조회 기능 (검색도 같이 구현할 것임.) -관리자용
		public PageResponseDTO<DemonstrationListReserveDTO> getAllDemRes(DemonstrationSearchDTO searchDTO) {

		    Integer pageCount = searchDTO.getPageCount();
		    String type = searchDTO.getType();
		    String search = searchDTO.getSearch();
		    String sortBy = searchDTO.getSortBy();
		    String sort = searchDTO.getSort();
		    String statusFilter = searchDTO.getStatusFilter();

		    if (pageCount == null || pageCount < 0)
		        pageCount = 0;
		    if (!StringUtils.hasText(sortBy))
		        sortBy = "applyAt";
		    if (!StringUtils.hasText(sort))
		        sort = "desc";

		    Pageable pageable = PageRequest.of(pageCount, 10);

		    Specification<DemonstrationReserve> spec = DemonstrationReserveSpecs.withResSearchAndSortAdmin(
		        type,
		        search,
		        sortBy,
		        sort,
		        statusFilter
		    );

		    // DemonstrationReserve 페이징 조회
		    Page<DemonstrationReserve> resPage = demonstrationReserveRepository.findAll(spec, pageable);

		    // 관련 demNum 리스트 추출 (중복 제거)
		    List<Long> demNums = resPage.stream()
		        .map(res -> res.getDemonstration().getDemNum())
		        .distinct()
		        .toList();

		    // 이미지 리스트 일괄 조회 (demNum 기준)
		    List<DemonstrationImageDTO> images = demonstrationImageRepository.selectDemImageIn(demNums);
		    Map<Long, List<DemonstrationImageDTO>> demNumToImages = images.stream()
		        .collect(Collectors.groupingBy(DemonstrationImageDTO::getDemNum));

		    // 요청 상태 리스트 일괄 조회 (demRevNum 기준)
		    List<Long> demRevNums = resPage.stream()
		        .map(DemonstrationReserve::getDemRevNum)
		        .distinct()
		        .toList();

		    List<DemonstrationRequest> requests = demonstrationRequestRepository.findStateByDemRevNumIn(demRevNums);

		    // reserves에서 member.memId 리스트 추출 (중복 제거)
		    List<String> memIds = resPage.stream()
		        .map(reserve -> reserve.getMember().getMemId())
		        .distinct()
		        .toList();

		    // memberRepository에서 memId 리스트로 Member 리스트 조회
		    List<Member> members = memberRepository.findByMemIdIn(memIds);
		    Map<String, Member> memIdToMember = members.stream()
		        .collect(Collectors.toMap(Member::getMemId, m -> m));

		    // DTO 매핑
		    Page<DemonstrationListReserveDTO> dtoPage = resPage.map(res -> {
		        Demonstration dem = res.getDemonstration();

		        DemonstrationListReserveDTO dto = new DemonstrationListReserveDTO();

		        dto.setDemRevNum(res.getDemRevNum());
		        dto.setApplyAt(res.getApplyAt());
		        dto.setStartDate(res.getStartDate());
		        dto.setEndDate(res.getEndDate());
		        dto.setState(res.getState());
		        dto.setMemId(res.getMember().getMemId());
		        dto.setBItemNum(res.getBItemNum());
		        dto.setDemNum(dem.getDemNum());

		        // Member 정보 가져오기
		        Member member = memIdToMember.get(res.getMember().getMemId());
		        if (member != null) {
		            if (member.getTeacher() != null) {
		                dto.setSchoolName(member.getTeacher().getSchoolName());
		            }
		            dto.setAddr(member.getAddr());
		            dto.setPhone(member.getPhone());
		        }

		        if (dem != null) {
		            dto.setDemName(dem.getDemName());
		            dto.setImageList(demNumToImages.getOrDefault(dem.getDemNum(), List.of()));
		        }

		        // demRevNum에 해당하는 모든 요청들을 리스트로 찾아서 DTO에 세팅
		        List<ResRequestDTO> relatedRequests = requests.stream()
		            .filter(r -> r.getReserve().getDemRevNum().equals(res.getDemRevNum()))
		            .map(r -> new ResRequestDTO(r.getType(), r.getState(),r.getUpdateDate()))
		            .collect(Collectors.toList());

		        dto.setRequestDTO(relatedRequests);

		        return dto;
		    });

		    return new PageResponseDTO<>(dtoPage);
		}
		
		public List<AdminMemberViewResDTO> getMembersByIds( AdminMemberViewReqDTO adminMemberViewReqDTO) {
			String sortField = adminMemberViewReqDTO.getSortField() != null ? adminMemberViewReqDTO.getSortField()
			        : "createdAt";
			Direction direction = adminMemberViewReqDTO.getSortDirection() != null
			        && adminMemberViewReqDTO.getSortDirection().equalsIgnoreCase("ASC") ? Direction.ASC : Direction.DESC;

			// 정렬 정보
			Sort sort = Sort.by(direction, sortField);

			// DTO 리스트
			List<AdminMemberViewResDTO> dtoList = new ArrayList<>();

			// 조건
			Specification<Member> spec = Specification.where(null);

			if (adminMemberViewReqDTO.getMemId() != null && !adminMemberViewReqDTO.getMemId().isBlank()) {
			    spec = spec.and(MemberSpecs.memIdContains(adminMemberViewReqDTO.getMemId()));
			} else if (adminMemberViewReqDTO.getName() != null && !adminMemberViewReqDTO.getName().isBlank()) {
			    spec = spec.and(MemberSpecs.nameContains(adminMemberViewReqDTO.getName()));
			} else if (adminMemberViewReqDTO.getEmail() != null && !adminMemberViewReqDTO.getEmail().isBlank()) {
			    spec = spec.and(MemberSpecs.emailContains(adminMemberViewReqDTO.getEmail()));
			} else if (adminMemberViewReqDTO.getPhone() != null && !adminMemberViewReqDTO.getPhone().isBlank()) {
			    spec = spec.and(MemberSpecs.phoneContains(adminMemberViewReqDTO.getPhone()));
			}

			if (adminMemberViewReqDTO.getRole() != null) {
			    spec = spec.and(MemberSpecs.hasRole(adminMemberViewReqDTO.getRole()));
			}
			if (adminMemberViewReqDTO.getState() != null) {
			    spec = spec.and(MemberSpecs.hasState(adminMemberViewReqDTO.getState()));
			}
			
			List<Member> members = memberRepository.findAll(spec, sort)
			        .stream()
			        .toList();

			for (Member member : members) {
			    AdminMemberViewResDTO dto = new AdminMemberViewResDTO();
			    dto.setMemId(member.getMemId());
			    dto.setName(member.getName());
			    dto.setPhone(member.getPhone());
			    dto.setEmail(member.getEmail());
			    dto.setCreatedAt(member.getCreatedAt());
			    dto.setRole(member.getRole());
			    dto.setState(member.getState());
			    dtoList.add(dto);
			}		

			return dtoList; 
		}

}
