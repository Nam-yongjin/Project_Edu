package com.EduTech.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.event.RevState;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // ModelMapper 사용 시 필요
@AllArgsConstructor
public class EventInfoDTO {

    private Long eventNum;

    @NotNull(message = "행사명은 필수입니다.")
    private String eventName;

    @NotNull(message = "행사 소개는 필수입니다.")
    private String eventInfo;

    private EventState state;
    private EventCategory category;

    @NotNull(message = "모집 인원은 필수입니다.")
    private Integer maxCapacity;

    private Integer currCapacity; // 현재 신청 인원

    @NotNull(message = "행사 장소는 필수입니다.")
    private String place;

    private String etc;

    // 상태 기본값 설정 (상세 페이지 신청 내역에서 사용 가능)
    @Builder.Default
    private RevState revState = RevState.WAITING;

    @NotNull(message = "신청 시작일은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime applyStartPeriod;

    @NotNull(message = "신청 종료일은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime applyEndPeriod;

    @NotNull(message = "행사 시작일은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime eventStartPeriod;

    @NotNull(message = "행사 종료일은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime eventEndPeriod;

    // 등록일 (백엔드 자동 세팅)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime applyAt;

    // 대표 이미지 또는 첨부파일
    private String originalName;
    private String filePath;

    // 요일 목록
    private List<Integer> daysOfWeek;

    // 상세 페이지에서만 사용됨 (리스트 응답에서는 비워둠)
    private List<EventFileDTO> attachList;
}
