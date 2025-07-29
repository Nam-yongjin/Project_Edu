package com.EduTech.entity.news;

import java.util.ArrayList;
import java.util.List;

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
	
	private String content; //내용
	
	@Builder.Default
	private Long view = 0L;	//0으로 초기화 해서 Null값 방지
	
	@ManyToOne //여러 개의 언론보도 게시글을 한 명의 Admin이 작성 가능
	@JoinColumn(name = "memId", nullable = false)
	private Member member;
	
	//하나의 기사에 여러 개의 파일 첨부 가능, 기사가 삭제되면 파일도 같이 삭제
	@Builder.Default
	@OneToMany(mappedBy = "news", cascade = CascadeType.ALL)
	private List<NewsFile> newsFile = new ArrayList<>();
	
}
