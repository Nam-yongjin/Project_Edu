package com.EduTech.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.entity.notice.Notice;
import com.EduTech.entity.notice.NoticeFile;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.notice.NoticeFileRepository;
import com.EduTech.repository.notice.NoticeRepository;

@SpringBootTest
public class NoticeRepositoryTest {
	
	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private NoticeFileRepository noticeFileRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	private Member adminMember;
	
	@BeforeEach //테스트용 데이터 세팅
    public void setup() {
        this.adminMember = Member.builder()
    		    .memId("admin")
    		    .pw("1234")
    		    .name("관리자")
    		    .email("admin@test.com")
    		    .birthDate(LocalDate.of(2025, 7, 15))
    		    .gender(MemberGender.MALE)
    		    .addr("테스트주소")
    		   	.addrDetail("테스트 상세주소")
				.checkSms(true)
				.checkEmail(false)
				.state(MemberState.NORMAL)
				.role(MemberRole.ADMIN)
				.build();
       
		memberRepository.save(adminMember);
	}
	
	@Test
	@Transactional
	@Rollback(false)
	@DisplayName("공지사항 등록")
	void createTest() {
		Notice notice = Notice.builder()
				.title("createTest")
				.content("공지사항 등록 테스트!!")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.isPinned(false)
				.member(adminMember)
				.build();
				
		NoticeFile file = NoticeFile.builder()
                .originalName("flower.png")
                .filePath("/files/flower.png")
                .fileType("png")
                .notice(notice)
                .build();

        notice.getNoticeFile().add(file);
        Notice saved = noticeRepository.save(notice);

        assertThat(saved.getNoticeFile()).hasSize(1);
        assertThat(saved.getNoticeFile().get(0).getOriginalName()).isEqualTo("flower.png");
	}
	
	


}
