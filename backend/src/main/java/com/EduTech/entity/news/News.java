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
	
	@Builder.Default
	private Long viewCount = 0L;	//0으로 초기화 해서 Null값 방지
	
	@ManyToOne(fetch = FetchType.LAZY) //여러 개의 언론보도 게시글을 한 명의 Admin이 작성 가능
	@JoinColumn(name = "mem_id", nullable = false)
	private Member member;
	
	//하나의 기사에 여러 개의 파일 첨부 가능, 기사가 삭제되면 파일도 같이 삭제
	@Builder.Default
	@BatchSize(size = 10)
	@OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NewsFile> newsFiles = new ArrayList<>();
	
	public void increaseViewCount() { //조회수 증가
        this.viewCount++;
    }
	//비즈니스 로직  
    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
        this.setUpdatedAt(LocalDateTime.now()); //현재 시간
    }
    
    public void addNewsFile(NewsFile newsFile) { //새로운 첨부파일 공지사항과 연결
        this.newsFiles.add(newsFile);
        newsFile.setNews(this);
    }
    
    public void clearNoticeFiles() { //첨부파일 목록 초기화
        this.newsFiles.clear();
    }
	
}
