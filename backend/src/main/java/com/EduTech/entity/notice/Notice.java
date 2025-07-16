package com.EduTech.entity.notice;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
	@Column(nullable = false) //공지사항번호(PK)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long noticeNum;
	
	@Column(nullable = false) //제목
	private String title;
	
	@Column(nullable = false) //내용
	private String content;
	
	@Builder.Default //Builder 사용할 때 기본 값 유지(false)
	@Column(nullable = false)
	private boolean isPinned = false; //고정여부
	
	@Builder.Default
	@Column(nullable = false) //조회수
	private Long view = 0L;	//0으로 초기화 해서 Null값 방지
	
	@ManyToOne(fetch = FetchType.LAZY) //여러 개의 공지사항 게시글을 한 명의 Admin이 작성 가능, 지연로딩
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;
	
}
