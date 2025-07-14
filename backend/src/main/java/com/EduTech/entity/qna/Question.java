package com.EduTech.entity.qna;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.member.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@AllArgsConstructor
@NoArgsConstructor
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
}
