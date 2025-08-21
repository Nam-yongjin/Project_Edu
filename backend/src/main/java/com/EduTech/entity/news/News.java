package com.EduTech.entity.news;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.member.Member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news")
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class News extends BaseEntity{

	@Id
	//식별자값 자동 생성
	//ALTER TABLE news MODIFY COLUMN news_num BIGINT AUTO_INCREMENT; --> 뒤늦게 추가했다면 DB에 적기
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long newsNum; //뉴스번호(PK)
	
	private String title; //제목
	
	@Column(columnDefinition = "TEXT")
	private String content; //내용
	
	private String imageUrl; //썸네일 이미지 경로 또는 URL
	
	private String linkUrl; //외부 기사 링크
	
	@Builder.Default
	@Column(updatable = false)
	private Long viewCount = 0L;	//0으로 초기화 해서 Null값 방지
	
	public void increaseViewCount() { //조회수 증가
        this.viewCount++;
    }
	
	@ManyToOne(fetch = FetchType.LAZY) //여러 개의 언론보도 게시글을 한 명의 Admin이 작성 가능
	@JoinColumn(name = "mem_id", nullable = false)
	private Member member;
	
	//비즈니스 로직  
    public void updateContent(String title, String content, String imageUrl, String linkUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.setUpdatedAt(LocalDateTime.now()); //현재 시간
    }
    
	
}
