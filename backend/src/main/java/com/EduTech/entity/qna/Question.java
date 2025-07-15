package com.EduTech.entity.qna;

import java.util.ArrayList;
import java.util.List;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.member.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "question")
@Builder
@Data
public class Question extends BaseEntity{

	@Id
	@Column(nullable = false) //질문글번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long questionNum;
	
	@Column(nullable = false) //제목
	private String title;
	
	@Column(nullable = false) //내용
	private String content;
	
	@Enumerated(EnumType.STRING) //공개여부
	@Column(nullable = false)
	private QnaState state;
	
	@Column(nullable = false) //조회수
	private Long view;	
	
	@ManyToOne //여러 질문글을 하나의 회원이 작성
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;
	
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL) //하나의 질문에 여러 개의 답변 가능, 질문 삭제되면 답변도 같이 삭제
	private List<Answer> answer = new ArrayList<>();
}
