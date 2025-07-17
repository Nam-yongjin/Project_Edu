package com.EduTech.dto.notice;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoticeUpdateRegisterDTO {
	
	@NotBlank(message = "제목을 입력하세요.")
	private String title; //제목
	
	@NotBlank(message = "내용을 입력하세요.")
	private String content; //내용
	
	private boolean isPinned; //게시판 고정
	
	private List<String> urlList; //새 파일
	
	private List<String> oldFiles; //남길 파일 목록

}
