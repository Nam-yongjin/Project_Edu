package com.EduTech.service.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventUse;
import com.EduTech.entity.member.Member;
import com.EduTech.repository.event.EventBannerRepository;
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
	
	// ========================================
    // 1. 필드 및 상수 선언
    // ========================================
	
	private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);
	private final EventBannerRepository bannerRepository;
	private final EventInfoRepository infoRepository;
	private final EventUseRepository useRepository;
	private final MemberRepository memberRepository;
	private final FileUtil fileUtil;
	private final ModelMapper modelMapper;
	
	// ========================================
    // 2. 내부 유틸리티 메서드
    // ========================================
	
	// 현재 행사 신청 가능여부
	private String calculateStatus(LocalDateTime applyStartPeriod, LocalDateTime applyEndPeriod) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(applyStartPeriod)) return "신청전";
        else if (now.isAfter(applyEndPeriod)) return "신청마감";
        else return "신청중";
    }
	
	// 모집대상
	private boolean isEligible(String target, Member member) {
        if (target == null || target.isBlank() || "전체".equals(target)) return true;
        if (member.getBirthDate() == null) return false;

        int age = Period.between(member.getBirthDate(), LocalDate.now()).getYears();
        return switch (target) {
            case "어린이" -> age >= 0 && age <= 12;
            case "청소년" -> age >= 13 && age <= 18;
            case "성인" -> age >= 19;
            default -> false;
        };
    }
	
	// ========================================
    // 3. 행사 등록/수정/삭제
    // ========================================
	
	// 행사 등록
	@Override
    public void registerEvent(EventInfoDTO dto, MultipartFile file) {
        EventInfo info = modelMapper.map(dto, EventInfo.class);
        info.setStatus(calculateStatus(dto.getApplyStartPeriod(), dto.getApplyEndPeriod()));

        setFileInfo(info, file);
        infoRepository.save(info);
    }
	
	// 행사 수정
	@Override
    public void updateEvent(Long eventNum, EventInfoDTO dto, MultipartFile file) {
        EventInfo origin = infoRepository.findById(eventNum)
        		.orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

        String originalFilePath = origin.getFilePath();
        String originalFileName = origin.getOriginalName();

        modelMapper.map(dto, origin);

        if (file != null && !file.isEmpty()) {
            if (originalFilePath != null && !originalFilePath.isEmpty()) {
                fileUtil.deleteFiles(List.of(originalFilePath));
            }
            setFileInfo(origin, file);
        } else {
            origin.setFilePath(dto.getFilePath() != null ? dto.getFilePath() : originalFilePath);
            origin.setOriginalName(dto.getOriginalName() != null ? dto.getOriginalName() : originalFileName);
        }

        infoRepository.save(origin);
    }
	
	
	
	
	
	
	
	
	// ========================================
	// 9. 공통 메서드
	// ========================================
	private EventUseDTO toDTO(EventUse use) {
	    EventInfo info = use.getEventInfo();
	    Member member = use.getMember();

	    String state = info.getEventEndPeriod().isBefore(LocalDate.now()) ? "행사종료" : "신청완료";

	    String eventStartPeriod = info.getEventStartPeriod() != null ? info.getEventStartPeriod().toString() : "";
	    String eventEndPeriod = info.getEventEndPeriod() != null ? info.getEventEndPeriod().toString() : "";

	    return EventUseDTO.builder()
	            .evtRevNum(use.getEvtRevNum())
	            .applyAt(use.getApplyAt())
	            .eventNum(info.getEventNum())
	            .eventName(info.getEventName())
	            .eventStartPeriod(info.getEventStartPeriod())
	            .eventEndPeriod(info.getEventEndPeriod())
	            .place(info.getPlace())
	            .maxCapacity(info.getMaxCapacity())
	            .currCapacity(useRepository.countByEvent(info.getCurrCapacity()))
	            .revState(state)
	            .memId(member != null ? member.getMemId() : null)
	            .name(member != null ? member.getName() : null)
	            .email(member != null ? member.getEmail() : null)
	            .phone(member != null ? member.getPhone() : null)
	            .build();
	}

	private void setFileInfo(EventInfo info, MultipartFile file) {
	    if (file != null && !file.isEmpty()) {
	        String originalFilename = file.getOriginalFilename();
	        
	        if (originalFilename == null || originalFilename.isEmpty()) {
	            throw new IllegalArgumentException("파일 이름이 존재하지 않습니다.");
	        }

	        String lowerCaseFilename = originalFilename.toLowerCase();
	        boolean isAllowedDocument = lowerCaseFilename.endsWith(".hwp") || lowerCaseFilename.endsWith(".pdf");

	        if (!isAllowedDocument) {
	            throw new IllegalArgumentException("hwp 또는 pdf 파일만 업로드 가능합니다.");
	        }

	        String oldPath = info.getFilePath();
	        if (oldPath != null && !oldPath.isEmpty()) {
	            fileUtil.deleteFiles(List.of(oldPath));
	        }

	        List<Object> uploaded = fileUtil.saveFiles(List.of(file), "program");
	        if (!uploaded.isEmpty()) {
	            @SuppressWarnings("unchecked")
	            Map<String, String> fileInfoMap = (Map<String, String>) uploaded.get(0);
	            info.setOriginalName(fileInfoMap.get("originalName"));
	            info.setFilePath(fileInfoMap.get("filePath"));
	        }
	    }
	}
}
