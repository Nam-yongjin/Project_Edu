package com.EduTech.entity.notice;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notice_file")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoticeFile {

	@Id
	private Long notFileNum; //첨부파일번호

	private String originalName; //원본파일명
	
	private String filePath; //파일저장경로
	
	private String fileType; //파일종류
	
	@ManyToOne(fetch = FetchType.LAZY) //여러 개의 파일을 하나의 공지사항에 첨부
	@JoinColumn(name = "noticeNum")
	private Notice notice; //공지사항번호
	
}
