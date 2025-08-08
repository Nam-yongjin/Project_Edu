package com.EduTech.service.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventFileDTO;
import com.EduTech.dto.event.EventImageDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventSearchRequestDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventFile;
import com.EduTech.entity.event.EventImage;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.event.EventUse;
import com.EduTech.entity.event.RevState;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.event.EventUseRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.util.FileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class EventServiceImpl implements EventService {

    // ─────────────────────────────────────────────────────
    // 1. 필드 및 상수
    // ─────────────────────────────────────────────────────
    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventInfoRepository infoRepository;
    private final EventUseRepository useRepository;
    private final MemberRepository memberRepository;
    private final FileUtil fileUtil;
    private final ModelMapper modelMapper;

    // ─────────────────────────────────────────────────────
    // 2. 내부 유틸리티 메서드
    // ─────────────────────────────────────────────────────

    // 행사 상태 계산
    private EventState calculateState(LocalDateTime applyStartPeriod, LocalDateTime applyEndPeriod, LocalDateTime eventEndPeriod) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(eventEndPeriod)) return EventState.COMPLETE;
        if (applyStartPeriod == null || applyEndPeriod == null) return EventState.BEFORE;
        if (now.isBefore(applyStartPeriod)) return EventState.BEFORE;
        if (now.isAfter(applyEndPeriod)) return EventState.CLOSED;
        return EventState.OPEN;
    }

    // 행사 신청 가능한 권한 분류
    private boolean isEligible(EventCategory category, Member member) {
        if (category == EventCategory.USER) return true;
        return switch (category) {
            case STUDENT -> member.getRole() == MemberRole.STUDENT;
            case TEACHER -> member.getRole() == MemberRole.TEACHER;
            default -> false;
        };
    }

    // ─────────────────────────────────────────────────────
    // 3. 행사 등록 / 수정 / 삭제
    // ─────────────────────────────────────────────────────

    // 행사 생성
    @Override
    public void registerEvent(EventInfoDTO dto, MultipartFile mainImage, List<MultipartFile> imageList,
                              MultipartFile mainFile, List<MultipartFile> attachList) {

        // 1. 대표 파일 저장
        String mainImagePath = null, mainImageOriginal = null, mainFilePath = null, mainFileOriginal = null;

        if (mainImage != null && !mainImage.isEmpty()) {
            Map<String, String> info = saveSingleFile(mainImage, "Event/mainImage");
            mainImagePath = info.get("filePath");
            mainImageOriginal = info.get("originalName");
        }

        if (mainFile != null && !mainFile.isEmpty()) {
            Map<String, String> info = saveSingleFile(mainFile, "Event/mainFile");
            mainFilePath = info.get("filePath");
            mainFileOriginal = info.get("originalName");
        }

        // 2. Entity 변환
        EventInfo event = modelMapper.map(dto, EventInfo.class);
        event.setCurrCapacity(0);
        event.setApplyAt(LocalDateTime.now());
        event.setState(calculateState(dto.getApplyStartPeriod(), dto.getApplyEndPeriod(), dto.getEventEndPeriod()));
        event.setMainImagePath(mainImagePath);
        event.setMainImageOriginalName(mainImageOriginal);
        event.setFilePath(mainFilePath);
        event.setOriginalName(mainFileOriginal);

        event.setImageList(new ArrayList<>());
        event.setAttachList(new ArrayList<>());

        // 3. 서브 이미지
        if (imageList != null) {
            for (MultipartFile file : imageList) {
                if (!file.isEmpty()) {
                    Map<String, String> result = saveSingleFile(file, "Event/images");
                    event.getImageList().add(EventImage.builder()
                        .filePath(result.get("filePath"))
                        .originalName(result.get("originalName"))
                        .isMain(false)
                        .eventInfo(event)
                        .build());
                }
            }
        }

        // 4. 첨부파일
        if (attachList != null) {
            for (MultipartFile file : attachList) {
                if (!file.isEmpty()) {
                    Map<String, String> result = saveSingleFile(file, "Event/files");
                    event.getAttachList().add(EventFile.builder()
                        .filePath(result.get("filePath"))
                        .originalName(result.get("originalName"))
                        .eventInfo(event)
                        .build());
                }
            }
        }

        infoRepository.save(event);
    }

    // 행사 수정
    @Override
    public void updateEvent(Long eventNum, EventInfoDTO dto, MultipartFile mainImage,
                            List<MultipartFile> imageList, MultipartFile mainFile, List<MultipartFile> attachList) {

        EventInfo origin = infoRepository.findById(eventNum)
            .orElseThrow(() -> new IllegalArgumentException("해당 행사가 존재하지 않습니다."));

        // 이전 파일 정보 보존
        String oldImagePath = origin.getMainImagePath();
        String oldImageName = origin.getMainImageOriginalName();
        String oldFilePath = origin.getFilePath();
        String oldFileName = origin.getOriginalName();

        // DTO 매핑
        dto.setEventNum(null); // 기존 PK 유지
        dto.setAttachList(null);
        dto.setImageList(null);
        modelMapper.map(dto, origin);
        origin.setCurrCapacity(dto.getCurrCapacity() != null ? dto.getCurrCapacity() : 0);
        origin.setState(calculateState(dto.getApplyStartPeriod(), dto.getApplyEndPeriod(), dto.getEventEndPeriod()));


        // 대표 이미지 수정
        if (mainImage != null && !mainImage.isEmpty()) {
            if (oldImagePath != null) fileUtil.deleteFiles(List.of(oldImagePath));
            Map<String, String> fileInfo = saveSingleFile(mainImage, "Event/mainImage");
            origin.setMainImagePath(fileInfo.get("filePath"));
            origin.setMainImageOriginalName(fileInfo.get("originalName"));
        } else {
            origin.setMainImagePath(oldImagePath);
            origin.setMainImageOriginalName(oldImageName);
        }

        // 대표 파일 수정
        if (mainFile != null && !mainFile.isEmpty()) {
            if (oldFilePath != null) fileUtil.deleteFiles(List.of(oldFilePath));
            Map<String, String> fileInfo = saveSingleFile(mainFile, "Event/mainFile");
            origin.setFilePath(fileInfo.get("filePath"));
            origin.setOriginalName(fileInfo.get("originalName"));
        } else {
            origin.setFilePath(oldFilePath);
            origin.setOriginalName(oldFileName);
        }

        // 서브 이미지 수정
        if (imageList != null && !imageList.isEmpty()) {
            fileUtil.deleteFiles(origin.getImageList().stream().map(EventImage::getFilePath).toList());
            origin.getImageList().clear();
            for (MultipartFile image : imageList) {
                if (!image.isEmpty()) {
                    Map<String, String> fileInfo = saveSingleFile(image, "Event/images");
                    origin.getImageList().add(EventImage.builder()
                        .filePath(fileInfo.get("filePath"))
                        .originalName(fileInfo.get("originalName"))
                        .isMain(false)
                        .eventInfo(origin)
                        .build());
                }
            }
        }

        // 첨부파일 수정
        if (attachList != null && !attachList.isEmpty()) {
            fileUtil.deleteFiles(origin.getAttachList().stream().map(EventFile::getFilePath).toList());
            origin.getAttachList().clear();
            for (MultipartFile file : attachList) {
                if (!file.isEmpty()) {
                    Map<String, String> fileInfo = saveSingleFile(file, "Event/files");
                    origin.getAttachList().add(EventFile.builder()
                        .filePath(fileInfo.get("filePath"))
                        .originalName(fileInfo.get("originalName"))
                        .eventInfo(origin)
                        .build());
                }
            }
        }

        infoRepository.save(origin);
    }

    // 행사 취소
    @Override
    public void deleteEvent(Long eventNum) {
        EventInfo event = infoRepository.findById(eventNum)
            .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

        event.setState(EventState.CANCEL);
        List<EventUse> uses = useRepository.findByEventInfo_EventNum(eventNum);
        uses.forEach(use -> use.setRevState(RevState.CANCEL));

        infoRepository.save(event);
        useRepository.saveAll(uses);
    }

    // ─────────────────────────────────────────────────────
    // 4. 행사 조회
    // ─────────────────────────────────────────────────────

    // 행소 조회
    @Override
    public List<EventInfoDTO> getAllEventsWithoutFilter(int page) {
        Pageable pageable = PageRequest.of(page - 1, 8);
        return infoRepository.findAll(pageable).stream()
            .map(this::toInfoDTO)
            .toList();
    }

    // 행사 검색
    @Override
    public Page<EventInfoDTO> searchEventList(EventSearchRequestDTO dto, int page) {
        Sort.Direction direction = Sort.Direction.fromString(dto.getSortOrder());
        Pageable pageable = PageRequest.of(page - 1, 8, Sort.by(direction, "applyStartPeriod"));
        return infoRepository.searchEvents(dto, pageable).map(this::toInfoDTO);
    }

    // 행사 이미지 및 파일 로딩
    @Override
    public EventInfoDTO getEvent(Long eventNum) {
        EventInfo event = infoRepository.findById(eventNum)
            .orElseThrow(() -> new RuntimeException("해당 이벤트를 찾을 수 없습니다."));

        EventInfoDTO dto = toInfoDTO(event);
        dto.setImageList(event.getImageList().stream().map(this::toImageDTO).toList());
        dto.setAttachList(event.getAttachList().stream().map(this::toFileDTO).toList());
        return dto;
    }
    
    // 행사 첨부파일 다운로드
    @Override
    public EventInfo getEventEntity(Long eventNum) {
        return infoRepository.findById(eventNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));
    }

    // ─────────────────────────────────────────────────────
    // 5. 사용자 신청/취소/확인
    // ─────────────────────────────────────────────────────

    // 행사 신청
    @Override
    public void applyEvent(EventApplyRequestDTO dto) {
        validateApplyConditions(dto);

        EventInfo event = infoRepository.findById(dto.getEventNum()).get();
        Member member = memberRepository.findById(dto.getMemId()).get();

        EventUse use = EventUse.builder()
            .eventInfo(event)
            .member(member)
            .revState(RevState.APPROVED)
            .applyAt(LocalDateTime.now())
            .build();

        useRepository.save(use);
        event.increaseCurrCapacity();
    }

    // 행사 취소
    @Override
    public void cancelEvent(Long evtRevNum, String memId) {
        EventUse use = useRepository.findById(evtRevNum)
            .orElseThrow(() -> new IllegalArgumentException("신청 내역이 존재하지 않습니다."));

        if (!use.getMember().getMemId().equals(memId)) {
            throw new AccessDeniedException("예약 취소 권한이 없습니다.");
        }

        EventInfo event = use.getEventInfo();
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(event.getEventStartPeriod())) {
            throw new IllegalStateException("행사 시작 이후에는 취소할 수 없습니다.");
        }

        if (use.getRevState() == RevState.APPROVED) {
            event.decreaseCurrCapacity();
        }

        use.setRevState(RevState.CANCEL);
    }

    // 행사 신청 유무 확인
    @Override
    public boolean isAlreadyApplied(Long eventNum, String memId) {
        return useRepository.existsByEventInfo_EventNumAndMember_MemIdAndRevState(eventNum, memId, RevState.APPROVED);
    }

    // 행사 신청 가능 유무 확인
    @Override
    public boolean isAvailable(Long eventNum) {
        LocalDateTime end = infoRepository.findById(eventNum)
            .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."))
            .getApplyEndPeriod();
        return end != null && end.toLocalDate().isAfter(LocalDate.now());
    }

    // 행사 신청 내역 확인
    @Override
    public Page<EventUseDTO> getUseListByMemberPaged(String memId, Pageable pageable) {
        if (memId == null || memId.isBlank()) {
            throw new IllegalArgumentException("회원 ID는 null 또는 빈 값일 수 없습니다.");
        }
        return useRepository.findByMember_MemId(memId, pageable).map(this::toDTO);
    }

    // ─────────────────────────────────────────────────────
    // 6. 배너
    // ─────────────────────────────────────────────────────

    // 현재 신청 가능한 행사 배너
    @Override
    public List<EventInfoDTO> getbannerEvent() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59);

        return infoRepository.findByEventStartPeriodBetweenAndState(start, end, EventState.OPEN)
            .stream().map(this::toInfoDTO).toList();
    }

    // ─────────────────────────────────────────────────────
    // 7. 공통 변환 메서드
    // ─────────────────────────────────────────────────────

    private EventInfoDTO toInfoDTO(EventInfo info) {
        EventInfoDTO dto = modelMapper.map(info, EventInfoDTO.class);
        dto.setMainImagePath(info.getMainImagePath());
        dto.setFilePath(info.getFilePath());
        dto.setOriginalName(info.getOriginalName());
        return dto;
    }

    private EventImageDTO toImageDTO(EventImage img) {
        return EventImageDTO.builder()
            .id(img.getId())
            .filePath(img.getFilePath())
            .originalName(img.getOriginalName())
            .build();
    }

    private EventFileDTO toFileDTO(EventFile f) {
        return EventFileDTO.builder()
            .id(f.getId())
            .filePath(f.getFilePath())
            .originalName(f.getOriginalName())
            .build();
    }

    public EventUseDTO toDTO(EventUse use) {
        EventInfo info = use.getEventInfo();
        Member member = use.getMember();
        int currentCapacity = useRepository.countByEventInfo(info.getEventNum());

        return EventUseDTO.builder()
            .evtRevNum(use.getEvtRevNum())
            .eventNum(info.getEventNum())
            .eventName(info.getEventName())
            .eventStartPeriod(info.getEventStartPeriod())
            .eventEndPeriod(info.getEventEndPeriod())
            .place(info.getPlace())
            .maxCapacity(info.getMaxCapacity())
            .currCapacity(currentCapacity)
            .revState(use.getRevState())
            .applyAt(use.getApplyAt())
            .memId(member != null ? member.getMemId() : null)
            .name(member != null ? member.getName() : null)
            .email(member != null ? member.getEmail() : null)
            .phone(member != null ? member.getPhone() : null)
            .mainImagePath(info.getMainImagePath())
            .build();
    }

    // 첨부파일 저장
    private Map<String, String> saveSingleFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) return null;
        @SuppressWarnings("unchecked")
        Map<String, String> fileInfo = (Map<String, String>) fileUtil.saveFiles(List.of(file), folder).get(0);
        return fileInfo;
    }

    // 신청 조건
    private void validateApplyConditions(EventApplyRequestDTO dto) {
        if (dto.getMemId() == null || dto.getMemId().isBlank()) throw new IllegalStateException("로그인 필요");
        if (isAlreadyApplied(dto.getEventNum(), dto.getMemId())) throw new IllegalStateException("이미 신청함");

        EventInfo event = infoRepository.findById(dto.getEventNum())
            .orElseThrow(() -> new IllegalArgumentException("행사 없음"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(event.getApplyStartPeriod())) throw new IllegalStateException("신청 전");
        if (now.isAfter(event.getApplyEndPeriod())) throw new IllegalStateException("신청 종료");

        if (event.getCurrCapacity() >= event.getMaxCapacity()) throw new IllegalStateException("정원 초과");

        Member member = memberRepository.findById(dto.getMemId())
            .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        if (member.getState() == MemberState.BEN) throw new IllegalStateException("정지된 회원");
        if (member.getState() == MemberState.LEAVE) throw new IllegalStateException("탈퇴한 회원");

        if (!isEligible(event.getCategory(), member)) throw new IllegalStateException("신청 대상 아님");
    }
}
