package com.EduTech.entity.news;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.member.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news")
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

	
}
