package com.EduTech.service.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
	
	private final EventBannerRepository BannerRepository;
	private final EventInfoRepository InfoRepository;
	private final EventReserveRepository ReserveRepository;
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
		info.setState(EventState.valueOf(calculateStatus(dto.getApplyStartyPeriod(), dto.getApplyEndPeriod())));
		info.setDaysOfWeek(dto.getDaysOfWeek());

		setFileInfo(info, file);	// 현재 코드 구현안해서 에러 뜨는 상태
		InfoRepository.save(info);

	}
	
	
	
	
	
	
	
	
	
}
