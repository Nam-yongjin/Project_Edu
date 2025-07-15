package com.EduTech.entity.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "event_file")
@Builder
@Data
public class EventFile {

	@Id
	@Column(nullable = false) //첨부파일번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long evtFileNum;
	
	@Column(nullable = false) //원본파일명
	private String originalName;
	
	@Column(nullable = false) //파일저장경로
	private String filePath;
	
	@Column(nullable = false) //파일종류
	private String fileType;
	
	@ManyToOne(fetch = FetchType.LAZY) //여러 개의 파일을 하나의 행사에 첨부
	@JoinColumn(name = "eventNum", nullable = false) //행사아이디
	private Event event;
}

//첨부파일 형식은 jpg, PNG, hwp, pdf만 가능