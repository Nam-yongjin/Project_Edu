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
public class NoticeFileDTO { //파일저장
		
	private String originalName; //원본파일명
	
	private String filePath; //파일경로(서버 내부에 저장된 실제 경로 -> 외부 노출 금지)
	
	private String fileType; //파일종류
	
	private String downloadUrl; //사용자가 파일을 다운로드 할 때 사용(노출 가능)
	
	private String savedName; //저장된 파일명 (이미지 표시용)
		
}
