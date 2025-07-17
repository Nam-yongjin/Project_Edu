package com.EduTech.dto.notice;

import java.util.List;

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
public class NoticeDTO { //조회용
	
	private String title; //제목
	
	private String content; //내용
	
	private String memId; //회원아이디(작성자 식별)
	
	private boolean isPinned; //게시판 고정
	
	private List<String> urlList; //첨부된 파일 URL목록
	

}
