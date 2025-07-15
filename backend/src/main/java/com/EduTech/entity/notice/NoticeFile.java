package com.EduTech.entity.notice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
	@Column(nullable = false) //첨부파일번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notFileNum;
	
	@Column(nullable = false) //원본파일명
	private String originalName;
	
	@Column(nullable = false) //파일저장경로
	private String filePath;
	
	@Column(nullable = false) //파일종류
	private String fileType;
	
	@ManyToOne(fetch = FetchType.LAZY) //여러 개의 파일을 하나의 공지사항에 첨부
	@JoinColumn(name = "noticeNum", nullable = false) //공지사항번호
	private Notice notice;
	
}
