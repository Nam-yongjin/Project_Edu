package com.EduTech.service.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.entity.event.Event;
import com.EduTech.entity.event.EventState;
import com.EduTech.repository.event.EventBannerRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.event.EventReserveRepository;
import com.EduTech.util.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
	
	private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);
	
	private final EventBannerRepository bannerRepository;
	private final EventInfoRepository infoRepository;
	private final EventReserveRepository reserveRepository;
	private final FileUtil fileUtil;
	private final ModelMapper modelMapper;
	
	private static final String[] WEEK_KO = { "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일" };
	
	private List<String> convertToDayNames(List<Integer> days) {
		return days.stream().map(num -> WEEK_KO[num % 7]).collect(Collectors.toList());
	}
	
	private String calculateStatus(LocalDateTime applyStartyPeriod, LocalDateTime applyEndPeriod) {
		LocalDateTime now = LocalDateTime.now();
		LocalDate applyStartDate = applyStartyPeriod.toLocalDate();
		LocalDate applyEndDate = applyEndPeriod.toLocalDate();

		if (now.isBefore(applyStartyPeriod)) {
			return "신청전";
		} else if (now.isAfter(applyEndPeriod)) {
			return "신청마감";
		} else {
			return "신청중";
		}
	}
	
	// 수업 날짜 생성 메서드 (요일 포함된 실제 수업일 계산)
		private List<LocalDate> generateClassDates(LocalDate start, LocalDate end, List<Integer> daysOfWeek) {
			List<LocalDate> dates = new ArrayList<>();
			for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
				if (daysOfWeek.contains(date.getDayOfWeek().getValue())) {
					dates.add(date);
				}
			}
			return dates;
		}
	
	
	// 프로그램 등록
	@Override
	public void registerEvent(EventInfoDTO dto, MultipartFile file) {
		if (dto.getDaysOfWeek() == null || dto.getDaysOfWeek().isEmpty()) {
			throw new IllegalArgumentException("요일 정보가 누락되었습니다.");
		}

		Event info = modelMapper.map(dto, Event.class);
		info.setApplyAt(LocalDateTime.now());
		info.setState(EventState.valueOf(calculateStatus(dto.getApplyStartPeriod(), dto.getApplyEndPeriod())));
		info.setDaysOfWeek(dto.getDaysOfWeek());

		setFileInfo(info, file);	// 현재 코드 구현안해서 에러 뜨는 상태
		infoRepository.save(info);

	}
	
	// 프로그램 수정
		@Override
		public void updateEvent(Long eventNum, EventInfoDTO dto,  MultipartFile file) {

			Event origin = infoRepository.findById(eventNum)
					.orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

			// 기존 파일 경로, 이름 백업
			String originalFilePath = origin.getFilePath();
			String originalFileName = origin.getOriginalName();

			// DTO → 엔티티 매핑
			LocalDateTime originalApplyAt = origin.getApplyAt();
			modelMapper.map(dto, origin);
			origin.setApplyAt(originalApplyAt);

			// 파일이 비어있지 않다면 기존 파일 삭제 후 새 파일 저장
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
	
	
		
		
		
	
		// 파일 처리 공통 메서드
		private void setFileInfo(Event info, MultipartFile file) {
			log.info("setFileInfo called. file is null: {}, file is empty: {}", (file == null),
					(file != null && file.isEmpty()));

			// 파일이 넘어온 경우 (새 파일이 업로드 되었거나 기존 파일을 변경하는 경우)
			if (file != null && !file.isEmpty()) {
				String originalFilename = file.getOriginalFilename();

				if (originalFilename == null || originalFilename.isEmpty()) {
					throw new IllegalArgumentException("파일 이름이 존재하지 않습니다.");
				}

				// 파일 확장자 검사: .hwp 또는 .pdf 파일만 허용
				String lowerCaseFilename = originalFilename.toLowerCase();
				boolean isAllowedDocument = lowerCaseFilename.endsWith(".hwp") || lowerCaseFilename.endsWith(".pdf");

				if (!isAllowedDocument) { // .hwp 또는 .pdf 파일이 아닌 경우
					throw new IllegalArgumentException("hwp 또는 pdf 파일만 업로드 가능합니다.");
				}

				// 기존 파일 삭제 (있으면)
				String oldPath = info.getFilePath();
				if (oldPath != null && !oldPath.isEmpty()) {
					try {
						fileUtil.deleteFiles(List.of(oldPath));
						log.info("기존 파일 삭제 완료: {}", oldPath);
					} catch (RuntimeException e) {
						log.warn("기존 파일 삭제 실패: {}", oldPath, e);
						throw e;
					}
				}

				// 새 파일 저장
				List<Object> uploaded = fileUtil.saveFiles(List.of(file), "program");
				if (!uploaded.isEmpty()) {
					@SuppressWarnings("unchecked")
					Map<String, String> fileInfoMap = (Map<String, String>) uploaded.get(0);
					info.setOriginalName(fileInfoMap.get("originalName"));
					info.setFilePath(fileInfoMap.get("filePath"));
					log.info("New file saved. OriginalName: {}, FilePath: {}", fileInfoMap.get("originalName"),
							fileInfoMap.get("filePath"));
				}

			}
			// 파일이 전달되지 않은 경우 (file == null || file.isEmpty()) -> 기존 파일 유지
			else {
				log.info("파일이 전달되지 않음 → 기존 파일 유지");
			}
		}
	
	
	
	
	
}
