package com.EduTech.entity.qna;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "answer")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Answer extends BaseEntity{

	@Id
	@Column(nullable = false) //답변글번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long answerNum;
	
	@Column(nullable = false) //내용
	private String content;
	
	@ManyToOne //하나의 질문글에 여러 답변이 가능
	@JoinColumn(name = "questionNum", nullable = false) //질문글번호
	private Question question;
	
	@ManyToOne //여러 답변글을 하나의 회원이 작성
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;
}
