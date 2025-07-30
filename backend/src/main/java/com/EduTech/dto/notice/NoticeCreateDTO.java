package com.EduTech.dto.notice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder
@Data
public class NoticeCreateDTO { // 생성

	private String title; // 제목

	private String content; // 내용

	private boolean isPinned; // 게시판 고정

	private List<MultipartFile> files; // 첨부할 파일

}
