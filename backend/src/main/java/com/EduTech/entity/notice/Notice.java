package com.EduTech.entity.notice;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.member.Member;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notice")
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED) //Builder 패턴이나 팩토리 메서드를 통해 객체 생성을 통제하고 데이터 무결성 보장
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA 내부에서만 기본 생성자 호출을 허용하여 불완전한 객체 생성을 방지
@Data
public class Notice extends BaseEntity{

	@Id
	private Long noticeNum; //공지사항번호(PK)
	
	private String title; //제목
	
	private String content; //내용
	
	@Builder.Default //Builder 사용할 때 기본 값 유지(false)
	private boolean isPinned = false; //고정여부
	
	@Builder.Default
	private Long view = 0L;	//조회수, 0으로 초기화 해서 Null값 방지
	
	@ManyToOne(fetch = FetchType.LAZY) //여러 개의 공지사항 게시글을 한 명의 Admin이 작성 가능, 지연로딩
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;
	
}
