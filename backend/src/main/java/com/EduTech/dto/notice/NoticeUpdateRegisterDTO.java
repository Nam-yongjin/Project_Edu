package com.EduTech.dto.notice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoticeUpdateRegisterDTO {
	
	@NotBlank(message = "제목을 입력하세요.")
	@Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
	private String title; //제목
	
	@NotBlank(message = "내용을 입력하세요.")
	@Size(max = 2000, message = "내용은 최대 2000자까지 입력 가능합니다.")
	private String content; //내용
	
	private boolean isPinned; //게시판 고정
	
	private String name; //이름
	
	private String memId; //아이디
	
	private List<String> oldFiles; //기존 파일목록
	
	private List<MultipartFile> newFiles; //새로 업로드 할 파일

}
