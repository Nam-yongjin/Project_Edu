package com.EduTech.service.facility;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.EduTech.dto.facility.FacilityDetailDTO;
import com.EduTech.dto.facility.FacilityImageDTO;
import com.EduTech.dto.facility.FacilityListDTO;
import com.EduTech.dto.facility.FacilityRegisterDTO;
import com.EduTech.dto.facility.FacilityReserveAdminDTO;
import com.EduTech.dto.facility.FacilityReserveApproveRequestDTO;
import com.EduTech.dto.facility.FacilityReserveListDTO;
import com.EduTech.dto.facility.FacilityReserveRequestDTO;
import com.EduTech.dto.facility.FacilityUpdateRequestDTO;
import com.EduTech.dto.facility.HolidayDayDTO;
import com.EduTech.dto.facility.ReservedBlockDTO;
import com.EduTech.entity.facility.Facility;
import com.EduTech.entity.facility.FacilityImage;
import com.EduTech.entity.facility.FacilityReserve;
import com.EduTech.entity.facility.FacilityState;
import com.EduTech.entity.facility.PublicHoliday;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.repository.facility.FacilityImageRepository;
import com.EduTech.repository.facility.FacilityRepository;
import com.EduTech.repository.facility.FacilityReserveRepository;
import com.EduTech.repository.facility.PublicHolidayRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.util.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;
    private final FacilityImageRepository facilityImageRepository;
    private final FacilityReserveRepository facilityReserveRepository;
    private final PublicHolidayRepository publicHolidayRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final FileUtil fileUtil;

    // ----------------------------------------------------------------------
    // 이미지 파일 저장 (FileUtil 사용)
    private FacilityImage toFacilityImage(MultipartFile file) {
        Map<String, String> saved = fileUtil.saveImage(file, "facility");
        String originalName = saved.get("originalName");
        String imagePath    = saved.get("filePath");
        return new FacilityImage(originalName, "/upload/" + imagePath, false);
    }

    // 요일 매핑: Java(MON=1..SUN=7) -> 시스템 규칙(SUN=1..SAT=7)
    private int mapJavaDayOfWeekTo1to7(LocalDate date) {
        int javaDow = date.getDayOfWeek().getValue(); // MON=1..SUN=7
        return (javaDow == DayOfWeek.SUNDAY.getValue()) ? 1 : javaDow + 1; // SUN->1, MON->2 .. SAT->7
    }

    private static final EnumSet<FacilityState> ACTIVE_STATES =
            EnumSet.of(FacilityState.WAITING, FacilityState.APPROVED);

    /** FacilityReserve → FacilityReserveListDTO 매핑 (대표 이미지: 첫 번째 이미지) */
    private FacilityReserveListDTO toDTO(FacilityReserve r) {
        // 엔티티 이미지 목록
        List<FacilityImage> imgEntities =
                (r.getFacility() == null || r.getFacility().getImages() == null)
                        ? List.of()
                        : r.getFacility().getImages();

        // 전체 URL 리스트
        List<String> images = imgEntities.stream()
                .map(FacilityImage::getImageUrl)
                .filter(Objects::nonNull)
                .toList();

        // ✅ 대표 이미지: 첫 번째 이미지(플래그 의존 X)
        String mainImageUrl = images.isEmpty() ? null : images.get(0);

        return FacilityReserveListDTO.builder()
                .reserveId(r.getReserveId())
                .facRevNum(r.getFacility().getFacRevNum())
                .facName(r.getFacility().getFacName())
                .facDate(r.getFacDate())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .state(r.getState())
                .mainImageUrl(mainImageUrl) // 대표 이미지 1장
                .build();
    }

    // ----------------------------------------------------------------------

    // 시설 등록(사용중)
    @Override
    @Transactional
    public void registerFacility(FacilityRegisterDTO dto, List<MultipartFile> images) {
        // 1) 기본 필드 매핑
        Facility facility = modelMapper.map(dto, Facility.class);

        // 2) 예약 요약 시간(선택) 검증
        LocalTime start = facility.getReserveStart();
        LocalTime end   = facility.getReserveEnd();
        if (start != null || end != null) {
            if (start == null || end == null) {
                throw new IllegalArgumentException("예약 시작/종료 시간은 둘 다 입력하거나 둘 다 비워두세요.");
            }
            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("예약 시작시간은 종료시간보다 앞서야 합니다.");
            }
        }

        // 3) 이미지 처리
        if (images != null) {
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    FacilityImage imgEntity = toFacilityImage(image);
                    facility.addImage(imgEntity);
                }
            }
        }

        // 4) 저장
        facilityRepository.save(facility);
    }
    
    // 시설 수정
    @Override
    @Transactional
    public void updateFacility(FacilityUpdateRequestDTO dto,
                               @Nullable List<MultipartFile> addImages) {

        if (dto.getFacRevNum() == null) {
            throw new IllegalArgumentException("facRevNum이 필요합니다.");
        }

        Facility facility = facilityRepository.findById(dto.getFacRevNum())
                .orElseThrow(() -> new IllegalArgumentException("시설 정보를 찾을 수 없습니다. facRevNum=" + dto.getFacRevNum()));

        // 1) 기본 필드 수정
        if (dto.getFacName() != null)   facility.setFacName(dto.getFacName());
        if (dto.getFacInfo() != null)   facility.setFacInfo(dto.getFacInfo());
        if (dto.getCapacity() != null)  facility.setCapacity(dto.getCapacity());
        if (dto.getFacItem() != null)   facility.setFacItem(dto.getFacItem());
        if (dto.getEtc() != null)       facility.setEtc(dto.getEtc());

        LocalTime start = dto.getReserveStart() != null ? dto.getReserveStart() : facility.getReserveStart();
        LocalTime end   = dto.getReserveEnd()   != null ? dto.getReserveEnd()   : facility.getReserveEnd();

        // 운영시간 갱신(둘 중 하나만 넘어온 경우 포함)
        if (dto.getReserveStart() != null) facility.setReserveStart(dto.getReserveStart());
        if (dto.getReserveEnd()   != null) facility.setReserveEnd(dto.getReserveEnd());

        // 2) 운영시간 검증 (둘 다 세팅된 경우만)
        if (start != null && end != null) {
            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("예약 시작시간은 종료시간보다 앞서야 합니다.");
            }
        }

        // 3) 이미지 삭제
        if (dto.getRemoveImageIds() != null && !dto.getRemoveImageIds().isEmpty()) {
            List<FacilityImage> toRemove = facility.getImages().stream()
                    .filter(img -> dto.getRemoveImageIds().contains(img.getFacImageNum()))
                    .collect(Collectors.toList());

            // 실제 파일 삭제가 필요하면 FileUtil에 삭제 메서드를 추가/호출하세요.
            // 예: fileUtil.deleteByUrl(img.getImageUrl());
            for (FacilityImage img : toRemove) {
                facility.removeImage(img); // 연관 제거 (orphanRemoval=true 권장)
                facilityImageRepository.delete(img); // 명시적 삭제
            }
        }

        // 4) 새 이미지 추가
        if (addImages != null) {
            for (MultipartFile image : addImages) {
                if (image != null && !image.isEmpty()) {
                    FacilityImage imgEntity = toFacilityImage(image); // 기존 저장 방식 재사용
                    facility.addImage(imgEntity);
                }
            }
        }

        // 5) 저장(더티체킹)
        facilityRepository.save(facility);
    }
    
    // 시설 삭제
    @Override
    @Transactional
    public void deleteFacility(Long facRevNum) {
        if (facRevNum == null) {
            throw new IllegalArgumentException("facRevNum이 필요합니다.");
        }

        Facility facility = facilityRepository.findById(facRevNum)
                .orElseThrow(() -> new IllegalArgumentException("시설 정보를 찾을 수 없습니다. facRevNum=" + facRevNum));

        // 오늘 이후 대기/승인 예약이 있으면 삭제 금지
        boolean hasActiveFuture = facilityReserveRepository
                .existsByFacility_FacRevNumAndFacDateGreaterThanEqualAndStateIn(
                        facRevNum, LocalDate.now(), ACTIVE_STATES);

        if (hasActiveFuture) {
            throw new IllegalStateException("미래 일정에 대기/승인된 예약이 있어 삭제할 수 없습니다.");
        }

        // 이미지 실제 파일 삭제가 필요하면 FileUtil에 메서드 추가해서 호출
        // for (FacilityImage img : facility.getImages()) {
        //     fileUtil.deleteByUrl(img.getImageUrl());
        // }

        // 연관 이미지 먼저 제거(OrphanRemoval=true면 생략 가능)
        List<FacilityImage> imgs = new ArrayList<>(facility.getImages());
        for (FacilityImage img : imgs) {
            facility.removeImage(img);
            facilityImageRepository.delete(img);
        }

        facilityRepository.delete(facility);
    }

    // 시설 목록(사용중)
    @Override
    public Page<FacilityListDTO> getFacilityList(Pageable pageable, String keyword) {
        // 기본 정렬: facRevNum DESC (pageable에 정렬이 없을 때만)
        Sort sort = pageable.getSort().isUnsorted()
                ? Sort.by(Sort.Direction.DESC, "facRevNum")
                : pageable.getSort();

        Pageable pageReq = PageRequest.of(
                Math.max(0, pageable.getPageNumber()),
                Math.max(1, pageable.getPageSize()),
                sort
        );

        String kw = (keyword == null) ? "" : keyword.trim();

        Page<Facility> result = kw.isEmpty()
                ? facilityRepository.findPageWithImages(pageReq)
                : facilityRepository.searchPageWithImages(kw, pageReq);

        return result.map(f -> modelMapper.map(f, FacilityListDTO.class));
    }

    // 시설 상세 (이미지 포함)(사용중)
    @Override
    @Transactional(readOnly = true)
    public FacilityDetailDTO getFacilityDetail(Long facRevNum) {
        Facility facility = facilityRepository.findWithImagesById(facRevNum)
                .orElseThrow(() -> new RuntimeException("시설 정보를 찾을 수 없습니다. (facRevNum=" + facRevNum + ")"));

        // fetch join으로 가져온 이미지 매핑
        List<FacilityImageDTO> imageDTOs = facility.getImages().stream()
                .map(img -> {
                    FacilityImageDTO d = new FacilityImageDTO();
                    d.setFacImageNum(img.getFacImageNum());
                    d.setImageName(img.getImageName());
                    d.setImageUrl(img.getImageUrl());
                    return d;
                })
                .collect(Collectors.toList());

        FacilityDetailDTO d = new FacilityDetailDTO();
        d.setFacRevNum(facility.getFacRevNum());
        d.setFacName(facility.getFacName());
        d.setFacInfo(facility.getFacInfo());
        d.setCapacity(facility.getCapacity());
        d.setFacItem(facility.getFacItem());
        d.setEtc(facility.getEtc());
        d.setReserveStart(facility.getReserveStart());
        d.setReserveEnd(facility.getReserveEnd());
        d.setImages(imageDTOs);

        return d;
    }

    // 예약 가능 여부
    @Override
    @Transactional(readOnly = true)
    public boolean isReservable(Long facRevNum, LocalDate date, LocalTime start, LocalTime end) {
        if (facRevNum == null || date == null || start == null || end == null) return false;
        if (!end.isAfter(start)) return false;
        if (LocalDateTime.of(date, end).isBefore(LocalDateTime.now())) return false;

        // 공휴일
        if (publicHolidayRepository.existsByDate(date)) return false;

        // 운영시간 범위
        Facility fac = facilityRepository.findById(facRevNum).orElse(null);
        if (fac == null) return false;
        LocalTime rs = fac.getReserveStart(), re = fac.getReserveEnd();
        if (rs != null && re != null && (start.isBefore(rs) || end.isAfter(re))) return false;

        // 최대 4시간 연속
        if (java.time.Duration.between(start, end).toHours() > 4) return false;

        // 활성 상태(대기/수락)만 충돌로 간주
        boolean overlaps =
                facilityReserveRepository
                        .existsByFacility_FacRevNumAndFacDateAndStartTimeLessThanAndEndTimeGreaterThanAndStateIn(
                                facRevNum, date, end, start, ACTIVE_STATES);

        return !overlaps;
    }

    // 예약 신청 처리
    @Override
    @Transactional
    public void reserveFacility(final FacilityReserveRequestDTO req, final String memId) {
        requireLogin(memId);

        final Facility facility = facilityRepository.findById(req.getFacRevNum())
                .orElseThrow(() -> new IllegalArgumentException("시설 정보가 존재하지 않습니다."));

        if (!isReservable(facility.getFacRevNum(), req.getFacDate(), req.getStartTime(), req.getEndTime())) {
            throw new IllegalStateException("해당 시간은 이미 예약되었거나 예약할 수 없습니다.");
        }

        final Member member = memberRepository.findById(memId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        // 같은 시설 + 같은 날짜 + 같은 사용자 (대기/승인 상태만 충돌로 간주)
        final boolean already = facilityReserveRepository
                .existsByFacility_FacRevNumAndMember_MemIdAndFacDateAndStateIn(
                        facility.getFacRevNum(), memId, req.getFacDate(), ACTIVE_STATES);
        if (already) {
            throw new IllegalStateException("해당 시설에서 오늘은 이미 예약하셨습니다. (하루 1회 제한)");
        }

        final FacilityReserve reserve = new FacilityReserve();
        reserve.setFacility(facility);
        reserve.setMember(member);
        reserve.setFacDate(req.getFacDate());
        reserve.setStartTime(req.getStartTime());
        reserve.setEndTime(req.getEndTime());
        reserve.setReserveAt(LocalDateTime.now());
        reserve.setState(FacilityState.WAITING);

        try {
            facilityReserveRepository.save(reserve);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("이미 오늘 예약이 존재합니다. (중복 예약 불가)", e);
        }
    }

    private static void requireLogin(String memId) {
        if (memId == null || memId.isBlank()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
    }

    // 예약 가능 시간 확인(현재 예약중인 시간 제외)(사용)
    @Override
    @Transactional(readOnly = true)
    public List<ReservedBlockDTO> getReservedBlocks(Long facRevNum, LocalDate date) {
        if (facRevNum == null || date == null) return List.of();

        var blocks = facilityReserveRepository.findByFacility_FacRevNumAndFacDateAndStateIn(
                facRevNum, date, List.of(FacilityState.WAITING, FacilityState.APPROVED));

        // (선택) 겹치는 구간 병합
        var sorted = blocks.stream()
                .sorted(Comparator.comparing(FacilityReserve::getStartTime))
                .map(r -> new ReservedBlockDTO(r.getStartTime(), r.getEndTime()))
                .collect(Collectors.toList());

        if (sorted.isEmpty()) return sorted;

        List<ReservedBlockDTO> merged = new java.util.ArrayList<>();
        LocalTime curS = sorted.get(0).getStart();
        LocalTime curE = sorted.get(0).getEnd();

        for (int i = 1; i < sorted.size(); i++) {
            var b = sorted.get(i);
            if (!b.getStart().isAfter(curE)) { // 겹치거나 맞닿음
                if (b.getEnd().isAfter(curE)) curE = b.getEnd();
            } else {
                merged.add(new ReservedBlockDTO(curS, curE));
                curS = b.getStart();
                curE = b.getEnd();
            }
        }
        merged.add(new ReservedBlockDTO(curS, curE));
        return merged;
    }

    // 내 예약 목록 (마이페이지)
    @Override
    @Transactional(readOnly = true)
    public Page<FacilityReserveListDTO> getMyReservations(String memId, Pageable pageable) {
        if (memId == null || memId.isBlank()) {
            throw new IllegalArgumentException("회원 ID는 null 또는 빈 값일 수 없습니다.");
        }
        // 리포지토리에 @EntityGraph(attributePaths={"facility","facility.images"}) 권장
        return facilityReserveRepository.findByMember_MemId(memId, pageable).map(this::toDTO);
    }

    // 관리자 예약 목록
    @Override
    @Transactional(readOnly = true)
    public List<FacilityReserveAdminDTO> getReservationsForAdmin(FacilityState state, LocalDate from, LocalDate to) {
        return facilityReserveRepository.searchForAdmin(state, from, to)
                .stream()
                .map(r -> {
                    FacilityReserveAdminDTO d = new FacilityReserveAdminDTO();
                    d.setReserveId(r.getReserveId());
                    d.setFacRevNum(r.getFacility().getFacRevNum());
                    d.setFacName(r.getFacility().getFacName());
                    d.setMemId(r.getMember() != null ? r.getMember().getMemId() : null);
                    d.setFacDate(r.getFacDate());
                    d.setStartTime(r.getStartTime());
                    d.setEndTime(r.getEndTime());
                    d.setState(r.getState());
                    d.setReserveAt(r.getReserveAt());
                    return d;
                }).collect(Collectors.toList());
    }

    // 관리자: 승인/거절 (현재 WAITING일 때만 상태 변경)
    @Override
    @Transactional
    public boolean updateReservationState(FacilityReserveApproveRequestDTO approveRequest) {
        int updated = facilityReserveRepository.updateStateIfCurrent(
                approveRequest.getReserveId(),
                approveRequest.getState(),
                FacilityState.WAITING
        );
        return updated > 0;
    }

    // 예약 취소 (reserveId 기준)
    @Override
    @Transactional
    public boolean cancelReservation(Long reserveId, String requesterId) {
        // 404 매핑용
        FacilityReserve reserve = facilityReserveRepository.findById(reserveId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        boolean isAdmin = requester.getRole() == MemberRole.ADMIN;

        // 403 매핑용
        if (!isAdmin && (reserve.getMember() == null || !reserve.getMember().getMemId().equals(requesterId))) {
            throw new AccessDeniedException("본인의 예약만 취소할 수 있습니다.");
        }

        // 400 매핑용
        if (reserve.getState() == FacilityState.CANCELLED || reserve.getState() == FacilityState.REJECTED) {
            throw new IllegalStateException("이미 취소/처리된 예약입니다.");
        }
        if (LocalDateTime.of(reserve.getFacDate(), reserve.getStartTime()).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("이미 시작된 예약은 취소할 수 없습니다.");
        }

        reserve.setState(FacilityState.CANCELLED);
        return true;
    }

    // 휴무일 리스트 (사용)
    @Override
    @Transactional(readOnly = true)
    public List<HolidayDayDTO> getHolidayDates(@org.springframework.lang.Nullable Long facRevNum) {
        return publicHolidayRepository.findAll()
                .stream()
                .map(ph -> HolidayDayDTO.builder()
                        .date(ph.getDate())
                        .label(ph.getName())
                        .type("PUBLIC")
                        .build())
                .sorted(Comparator.comparing(HolidayDayDTO::getDate))
                .toList();
    }

    // 휴무일 등록
    @Override
    @Transactional
    public void registerHoliday(HolidayDayDTO dto) {
        if (dto.getDate() == null || dto.getLabel() == null || dto.getLabel().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date/label이 필요합니다.");
        }
        try {
            if (publicHolidayRepository.existsByDate(dto.getDate())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 휴무일입니다.");
            }
            PublicHoliday holiday = PublicHoliday.builder()
                    .date(dto.getDate())
                    .name(dto.getLabel())
                    .isLunar(false)
                    .build();
            publicHolidayRepository.saveAndFlush(holiday);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 휴무일입니다.", e);
        }
    }

    // 휴무일 삭제
    @Override
    @Transactional
    public void deletePublicHolidayByDate(LocalDate date) {
        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date가 필요합니다.");
        }
        long affected = publicHolidayRepository.deleteByDate(date);
        if (affected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 날짜의 공휴일이 없습니다.");
        }
    }
}