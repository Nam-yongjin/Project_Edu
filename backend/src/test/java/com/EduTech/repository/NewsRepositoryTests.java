package com.EduTech.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Commit;

import com.EduTech.entity.member.Member;
import com.EduTech.entity.news.News;
import com.EduTech.entity.news.NewsFile;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationTimeRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.news.NewsFileRepository;
import com.EduTech.repository.news.NewsRepository;
import com.EduTech.util.FileUtil;

import jakarta.transaction.Transactional;

@SpringBootTest
public class NewsRepositoryTests {
	
	@Autowired
	private NewsRepository newsRepository;
	
	@Autowired
	private NewsFileRepository newsFileRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private FileUtil fileutil;
	
	
	
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
	
////	@BeforeEach //테스트용 데이터
//    public void setup() {
//        this.adminMember = Member.builder() //필요한 데이터만 설정
//    		    .memId("admin")
//    		    .pw("1234")
//    		    .name("관리자")
//    		    .email("admin@test.com")
//    		    .birthDate(LocalDate.of(2025, 7, 15))
//    		    .gender(MemberGender.MALE)
//    		    .addr("테스트주소")
//    		   	.addrDetail("테스트 상세주소")
//				.checkSms(true)
//				.checkEmail(false)
//				.state(MemberState.NORMAL)
//				.role(MemberRole.ADMIN)
//				.build();
//       
//		memberRepository.save(adminMember);
//	}
	
//	@Test
	@Transactional
	@DisplayName("언론보도 등록")
	@Commit //flush()를 사용하려면 commit도 같이 써야함!!
	public void createTest() {
		Member adminMember = memberRepository.findById("admin").orElseThrow();
		News news = News.builder()
				.title("createTest")
				.content("언론보도 등록 테스트!!")
				.member(adminMember)
				.build();
				
		NewsFile file = NewsFile.builder()
                .originalName("create.png")
                .filePath("/files/create.png")
                .fileType("png")
                .news(news)
                .build();

        news.getNewsFile().add(file);
        News saved = newsRepository.save(news);
        newsRepository.flush(); //영속성 컨텍스트 변경 내용을 DB에 저장
        
        assertThat(saved.getNewsNum()).isNotNull(); //Null이 아닐경우
        assertThat(saved.getNewsFile()).hasSize(1); //크기 설정 확인
        assertThat(saved.getNewsFile().get(0).getOriginalName()).isEqualTo("create.png");
	}
	
//	@Test
	@Transactional
	@DisplayName("언론보도 조회")
	@Commit
	public void readTest() {
		Member adminMember = memberRepository.findById("admin").orElseThrow();
		News news = News.builder()
				.title("readTest")
				.content("언론보도 조회 테스트!!")
				.member(adminMember)
				.build();
				
		NewsFile file = NewsFile.builder()
                .originalName("read.pdf")
                .filePath("/files/read.pdf")
                .fileType("pdf")
                .news(news)
                .build();

        news.getNewsFile().add(file);
        News saved = newsRepository.save(news);
        newsRepository.flush();
        
        News found = newsRepository.findById(saved.getNewsNum()).orElseThrow();
        assertThat(saved.getNewsNum()).isNotNull();
        assertThat(found.getTitle()).isEqualTo("readTest");
        assertThat(found.getNewsFile()).hasSize(1);
	}
	
//	@Test
	@Transactional
	@DisplayName("언론보도 수정")
	@Commit
	public void updateTest() {
		Member adminMember = memberRepository.findById("admin").orElseThrow();
		News news = News.builder()
				.title("기존 제목")
				.content("기존 내용")
				.member(adminMember)
				.build();
				
		NewsFile file = NewsFile.builder()
                .originalName("old.jpg")
                .filePath("/files/old.jpg")
                .fileType("jpg")
                .news(news)
                .build();

        news.getNewsFile().add(file);
        News saved = newsRepository.save(news);
        newsRepository.flush();
        
        saved.getNewsFile().clear(); // 기존 파일 제거
                
        saved.setTitle("수정한 제목"); //제목 수정
        saved.setContent("수정한 내용"); //내용 수정
        
        NewsFile newfile = NewsFile.builder() //새로운 파일 추가
                .originalName("new.jpg")
                .filePath("/files/new.jpg")
                .fileType("jpg")
                .news(saved)
                .build();
        saved.getNewsFile().add(newfile);
               
        News update = newsRepository.save(saved);
        
        assertThat(update.getNewsNum()).isNotNull();
        assertThat(update.getTitle()).isEqualTo("수정한 제목");
        assertThat(update.getNewsFile()).hasSize(1);						//파일이름 잘 맞추기
        assertThat(update.getNewsFile().get(0).getOriginalName()).isEqualTo("new.jpg");
	}
	
	@Test
	@Transactional
	@DisplayName("언론보도 삭제")
	@Commit
	public void deleteTest() {
		Member adminMember = memberRepository.findById("admin").orElseThrow();
		News news = News.builder()
				.title("deleteTest")
				.content("언론보도 삭제 테스트!!")
				.member(adminMember)
				.build();
				
		NewsFile file = NewsFile.builder()
                .originalName("delete.hwp")
                .filePath("/files/delete.hwp")
                .fileType("hwp")
                .news(news)
                .build();

        news.getNewsFile().add(file);
        News saved = newsRepository.save(news);
        newsRepository.flush();
        
        Long newsNum = saved.getNewsNum(); //게시물 번호
        Long newsFileNum = saved.getNewsFile().get(0).getNewsFileNum(); //첨부파일 번호
        
        newsRepository.deleteById(newsNum);
        newsRepository.flush();
        
        boolean newsExists = newsRepository.findById(newsNum).isPresent();
        boolean fileExists = newsFileRepository.findById(newsNum).isPresent();
        //false가 나와야 삭제된 것(양방향 매핑 확인, cascade = CascadeType.ALL 확인)
        System.out.println("언론보도 삭제 여부 확인: " + newsExists);
        System.out.println("첨부파일 삭제 여부 확인: " + fileExists);
        
        assertThat(newsExists).isFalse();
	}

}
