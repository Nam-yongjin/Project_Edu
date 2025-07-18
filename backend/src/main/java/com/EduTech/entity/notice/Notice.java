package com.EduTech.entity.notice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.news.NewsFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	private Long noticeNum; //공지사항번호(PK)
	
	private String title; //제목
	
	private String content; //내용
	
	private LocalDateTime createdAt; //작성일
	
	private LocalDateTime updatedAt; //수정일
	
	@Builder.Default //Builder 사용할 때 기본 값 유지(false)
	private boolean isPinned = false; //고정여부
	
	@Builder.Default
	private Long view = 0L;	//조회수, 0으로 초기화 해서 Null값 방지
	
	@ManyToOne(fetch = FetchType.LAZY) //여러 개의 공지사항 게시글을 한 명의 Admin이 작성 가능, 지연로딩
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;
	
	//하나의 공지글에 여러 개의 파일 첨부 가능, 기사가 삭제되면 파일도 같이 삭제
	@OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
	private List<NoticeFile> noticeFile = new ArrayList<>();
	
}
