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
@NoArgsConstructor  // 기본 생성자 (ModelMapper가 필요로 함)
@AllArgsConstructor // 모든 필드를 초기화하는 생성자 (Builder와 함께 권장)
public class EventInfoDTO {

    private Long eventNum;              // 행사 아이디

    @NotNull(message = "행사명은 필수입니다.")
    private String eventName;           // 행사명

    @NotNull(message = "행사 소개는 필수입니다.")
    private String eventInfo;           // 소개

    private EventState state;           // 신청전, 신청중, 신청마감
    private EventCategory category;     // 모집대상 분류

    @NotNull(message = "모집 인원은 필수입니다.")
    private Integer maxCapacity;        // 모집인원

    private Integer currCapacity;       // 현재인원

    @NotNull(message = "행사 장소는 필수입니다.")
    private String place;               // 장소

    private String etc;                 // 기타 유의사항

    @Builder.Default
    private RevState revState = RevState.WAITING;   // 상태(대기, 수락, 거절)

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
    private LocalDateTime applyAt; // 등록일

    private String originalName;        // 대표 파일명
    private String filePath;            // 대표 파일 경로

    private List<Integer> daysOfWeek;   // 진행 요일 (0~6)

    // private List<String> dayNames;  // 요일명 리스트 (생략 or 별도 DTO 변환 시 추가)
}
