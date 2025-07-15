package com.EduTech.entity.notice;

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
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "notice")
@Builder
@Data
public class Notice extends BaseEntity{

	@Id
	@Column(nullable = false) //공지사항번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long noticeNum;
	
	@Column(nullable = false) //제목
	private String title;
	
	@Column(nullable = false) //내용
	private String content;
	
	@Column(nullable = false) //조회수
	private Long view;	
	
	@ManyToOne //여러 개의 공지사항 게시글을 한 명의 Admin이 작성 가능
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;
	
}
