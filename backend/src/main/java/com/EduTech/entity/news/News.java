package com.EduTech.entity.news;

import java.util.ArrayList;
import java.util.List;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.member.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class News extends BaseEntity{

	@Id
	@Column(nullable = false) //뉴스번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long newsNum;
	
	@Column(nullable = false) //제목
	private String title;
	
	@Column(nullable = false) //내용
	private String content;
	
	@Column(nullable = false) //조회수
	private Long view;	
	
	@ManyToOne //여러 개의 언론보도 게시글을 한 명의 Admin이 작성 가능
	@JoinColumn(name = "memId", nullable = false)
	private Member member;
	
	@OneToMany(mappedBy = "news", cascade = CascadeType.ALL) //하나의 기사에 여러 개의 파일 첨부 가능, 기사가 삭제되면 파일도 같이 삭제
	private List<NewsFile> newsFile = new ArrayList<>();
	
}
