package com.EduTech.dto.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsFileDTO {
	
	private Long newsFileNum; //파일 번호
	
	private String originalName; //원본파일명
	
	private String filePath; //파일경로(노출 금지)
	
	private String fileType; //파일종류
	
	private String downloadUrl; //사용자가 파일을 다운로드 할 때 사용(노출 가능)
	
	private String savedName; //저장된 파일명 (이미지 표시용)

}
