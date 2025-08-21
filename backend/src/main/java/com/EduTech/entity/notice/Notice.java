package com.EduTech.entity.notice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.member.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
//Builder 패턴이나 팩토리 메서드를 통해 객체 생성을 통제하고 데이터 무결성 보장
@AllArgsConstructor(access = AccessLevel.PROTECTED)
//JPA 내부에서만 기본 생성자 호출을 허용하여 불완전한 객체 생성을 방지
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class Notice extends BaseEntity{

	@Id
	//식별자값 자동 생성, DB에서도 수정해줘야 함
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long noticeNum; //공지사항번호(PK)
	
	private String title; //제목
	
	@Column(columnDefinition = "TEXT")
	private String content; //내용
	
	@Builder.Default //Builder 사용할 때 기본 값 유지(false)
	private boolean isPinned = false; //고정여부
	
	@Builder.Default
	@Column(updatable = false)
	private Long viewCount = 0L; //조회수, 0으로 초기화 해서 Null값 방지
	
	@ManyToOne(fetch = FetchType.LAZY) //여러 개의 공지사항 게시글을 한 명의 Admin이 작성 가능, 지연로딩
	@JoinColumn(name = "mem_id", nullable = false) //회원아이디
	private Member member;
	
	//하나의 공지글에 여러 개의 파일 첨부 가능, 공지글이 삭제되면 파일도 같이 삭제
	@Builder.Default
	@BatchSize(size = 10)
	@OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NoticeFile> noticeFiles = new ArrayList<>(); //noticefiles -> notice 안에 있는 리스트
	
	public void increaseViewCount() { //조회수 증가
        this.viewCount++;
    }
	
	//비즈니스 로직  
    public void updateContent(String title, String content, boolean isPinned) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        this.setUpdatedAt(LocalDateTime.now()); //현재 시간
    }
    
    public void addNoticeFile(NoticeFile noticeFile) { //새로운 첨부파일을 공지사항과 연결
        this.noticeFiles.add(noticeFile); //공지사항에 새로운 파일 추가(부모->자식)
        noticeFile.setNotice(this); //추가된 noticefile 객체의 notice 필드에 현재 공지사항(this) 설정(자식->부모)
    }
    
    public void clearNoticeFiles() { //첨부파일 목록 초기화
        this.noticeFiles.clear();
    }

}
