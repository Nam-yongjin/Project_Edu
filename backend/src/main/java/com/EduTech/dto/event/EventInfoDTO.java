package com.EduTech.dto.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
@NoArgsConstructor
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
    private String place;
    private String etc;

    @Builder.Default
    private RevState revState = RevState.WAITTING;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime applyAt;

    // 대표 첨부파일
    private String originalName;
    private String filePath;

    // 대표 이미지
    private String mainImagePath;
    private String mainImageOriginalName;

    // 이미지 리스트
    @Builder.Default
    private List<EventImageDTO> imageList = new ArrayList<>();

    // 첨부파일 리스트
    @Builder.Default
    private List<EventFileDTO> attachList = new ArrayList<>();

    // 요일 배열
    private List<Integer> daysOfWeek;
}
