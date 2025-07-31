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
@NoArgsConstructor //기본 생성자 생성
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder
@Data
public class NoticeUpdateDTO { //수정
	
	private String title; //제목
	
	private String content; //내용
	
	private Boolean isPinned; //게시판 고정
	
	private List<String> deleteFileIds; // 삭제할 파일 ID 목록
	
	private List<MultipartFile> newFiles; //새로 업로드 할 파일

}
