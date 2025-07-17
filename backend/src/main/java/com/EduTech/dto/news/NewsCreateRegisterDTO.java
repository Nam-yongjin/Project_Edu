package com.EduTech.dto.news;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewsCreateRegisterDTO {
	
	@NotBlank(message = "제목을 입력하세요.")
	private String title; //제목
	
	@NotBlank(message = "내용을 입력하세요.")
	private String content; //내용
	
	private List<String> fileUrls; //첨부할 Url
	
	private List<MultipartFile> files; //첨부할 파일

}
