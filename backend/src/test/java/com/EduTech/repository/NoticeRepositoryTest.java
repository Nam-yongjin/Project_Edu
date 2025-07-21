package com.EduTech.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Commit;

import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.entity.notice.Notice;
import com.EduTech.entity.notice.NoticeFile;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationTimeRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.notice.NoticeFileRepository;
import com.EduTech.repository.notice.NoticeRepository;
import com.EduTech.util.FileUtil;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;


@SpringBootTest
public class NoticeRepositoryTest {
	
	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private NoticeFileRepository noticeFileRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private FileUtil fileutil;
	
	private Member adminMember;
	
	@MockBean //오류 나는 거 제외하고 테스트(Repository관련만 MockBean해야 함)
	private DemonstrationReserveRepository demonstrationReserveRepository;
	@MockBean
	private DemonstrationRepository demonstrationRepository;
	@MockBean
	private DemonstrationImageRepository demonstrationImageRepository;
	@MockBean
	private DemonstrationTimeRepository demonstrationTimeRepository;
	@MockBean
	private DemonstrationRegistrationRepository demonstrationRegistrationRepository;
	
	@BeforeEach //테스트용 데이터
    public void setup() {
        this.adminMember = Member.builder() //필요한 데이터만 설정
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
	
//	@Test
	@Transactional
	@DisplayName("공지사항 등록")
	@Commit //flush()를 사용하려면 commit도 같이 써야함!!
	public void createTest() {
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
                .filePath("/files/create.png")
                .fileType("png")
                .notice(notice)
                .build();

        notice.getNoticeFile().add(file);
        Notice saved = noticeRepository.save(notice);
        noticeRepository.flush(); //영속성 컨텍스트 변경 내용을 DB에 저장
        
        assertThat(saved.getNoticeNum()).isNotNull(); //Null이 아닐경우
        assertThat(saved.getNoticeFile()).hasSize(1); //크기 설정 확인
        assertThat(saved.getNoticeFile().get(0).getOriginalName()).isEqualTo("create.png");
	}
	
//	@Test
	@Transactional
	@DisplayName("공지사항 조회")
	@Commit
	public void readTest() {
		Notice notice = Notice.builder()
				.title("readTest")
				.content("공지사항 조회 테스트!!")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.isPinned(false)
				.member(adminMember)
				.build();
				
		NoticeFile file = NoticeFile.builder()
                .originalName("read.png")
                .filePath("/files/read.png")
                .fileType("png")
                .notice(notice)
                .build();

        notice.getNoticeFile().add(file);
        Notice saved = noticeRepository.save(notice);
        noticeRepository.flush();
        
        Notice found = noticeRepository.findById(saved.getNoticeNum()).orElseThrow();
        assertThat(saved.getNoticeNum()).isNotNull();
        assertThat(found.getTitle()).isEqualTo("readTest");
        assertThat(found.getNoticeFile()).hasSize(1);
	}
	
//	@Test
	@Transactional
	@DisplayName("공지사항 수정")
	@Commit
	public void updateTest() {
		Notice notice = Notice.builder()
				.title("기존 제목")
				.content("기존 내용")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.isPinned(false)
				.member(adminMember)
				.build();
				
		NoticeFile file = NoticeFile.builder()
                .originalName("old.png")
                .filePath("/files/old.png")
                .fileType("png")
                .notice(notice)
                .build();

        notice.getNoticeFile().add(file);
        Notice saved = noticeRepository.save(notice);
        noticeRepository.flush();
        
        saved.getNoticeFile().clear(); // 기존 파일 제거
                
        saved.setTitle("수정한 제목"); //제목 수정
        saved.setContent("수정한 내용"); //내용 수정
        
        NoticeFile newfile = NoticeFile.builder() //새로운 파일 추가
                .originalName("new.png")
                .filePath("/files/new.png")
                .fileType("png")
                .notice(saved)
                .build();
        saved.getNoticeFile().add(newfile);
               
        Notice update = noticeRepository.save(saved);
        
        assertThat(update.getNoticeNum()).isNotNull();
        assertThat(update.getTitle()).isEqualTo("수정한 제목");
        assertThat(update.getNoticeFile()).hasSize(1);
        assertThat(update.getNoticeFile().get(0).getOriginalName()).isEqualTo("new.png");
	}
	
	@Test
	@Transactional
	@DisplayName("공지사항 삭제")
	@Commit
	public void deleteTest() {
		Notice notice = Notice.builder()
				.title("deleteTest")
				.content("공지사항 삭제 테스트!!")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.isPinned(false)
				.member(adminMember)
				.build();
				
		NoticeFile file = NoticeFile.builder()
                .originalName("delete.png")
                .filePath("/files/delete.png")
                .fileType("png")
                .notice(notice)
                .build();

        notice.getNoticeFile().add(file);
        Notice saved = noticeRepository.save(notice);
        noticeRepository.flush();
        
        Long noticeNum = saved.getNoticeNum(); //게시물 번호
        Long notFileNum = saved.getNoticeFile().get(0).getNotFileNum(); //첨부파일 번호
        
        noticeRepository.deleteById(noticeNum);
        noticeRepository.flush();
        
//        assertThatThrownBy(() -> {
//            noticeRepository.findById(noticeNum).orElseThrow(() -> new EntityNotFoundException("공지사항 없음"));
//        }).isInstanceOf(EntityNotFoundException.class);
//
//        assertThat(noticeFileRepository.findById(noticeNum)).isEmpty(); // 이건 Optional로 처리
        
        boolean noticeExists = noticeRepository.findById(noticeNum).isPresent();
        boolean fileExists = noticeFileRepository.findById(noticeNum).isPresent();
        System.out.println("삭제 후 공지사항 존재 여부: " + noticeExists);
        System.out.println("삭제 후 첨부파일 존재 여부: " + fileExists);
        
        assertThat(noticeExists).isFalse();
	}
	
	 
	
	
}
	

