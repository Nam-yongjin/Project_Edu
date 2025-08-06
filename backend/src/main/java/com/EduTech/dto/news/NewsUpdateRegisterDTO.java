package com.EduTech.dto.news;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class NewsUpdateRegisterDTO {
	
	@NotBlank(message = "제목을 입력하세요.")
	@Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
	private String title; //제목
	
	@NotBlank(message = "내용을 입력하세요.")
	private String content; //내용
	
	private List<String> deleteFileIds; // 삭제할 파일 ID 목록
	
	private List<MultipartFile> newFiles; //새로 업로드 할 파일

}
