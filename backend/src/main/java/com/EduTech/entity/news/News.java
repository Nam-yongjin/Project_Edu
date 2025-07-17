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
	private Long newsNum; //뉴스번호(PK)
	
	private String title; //제목
	
	private String content; //내용
	
	@Builder.Default
	private Long view = 0L;	//0으로 초기화 해서 Null값 방지
	
	@ManyToOne //여러 개의 언론보도 게시글을 한 명의 Admin이 작성 가능
	@JoinColumn(name = "memId", nullable = false)
	private Member member;
	
	//하나의 기사에 여러 개의 파일 첨부 가능, 기사가 삭제되면 파일도 같이 삭제
	@OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NewsFile> newsFile = new ArrayList<>();
	
}
