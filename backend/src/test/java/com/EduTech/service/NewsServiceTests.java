package com.EduTech.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.news.NewsDetailDTO;
import com.EduTech.dto.news.NewsListDTO;
import com.EduTech.dto.news.NewsSearchDTO;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.event.EventUse;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.news.News;
import com.EduTech.repository.event.EventUseRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.news.NewsRepository;
import com.EduTech.service.event.EventServiceImpl;
import com.EduTech.service.news.NewsService;
import com.EduTech.util.FileUtil;

@SpringBootTest
@Transactional
public class NewsServiceTests {

		@Autowired
		private NewsRepository newsRepository;

		@Autowired
		private NewsService newsService;

		@Autowired
		private MemberRepository memberRepository;

		@Autowired
		private FileUtil fileUtil;

		@Autowired
		private ModelMapper modelMapper;
		
		private Member testMember;
		
		@MockBean
		EventServiceImpl eventServiceImpl;
		@MockBean
		EventState eventState;
		@MockBean
		EventUse eventUse;
		@MockBean
		EventUseRepository eventUseRepository; 

		@BeforeEach // 테스트용 데이터
		void setUp() {
			
	        // 테스트 회원 생성
	        testMember = new Member();
	        testMember.setMemId("testUser");
	        testMember.setName("테스트사용자");
	        memberRepository.save(testMember);
	        
	        System.out.println("테스트 회원 생성 완료: " + testMember.getName());
	    }

//		@Test
	    @Transactional //테스트 후 DB삭제
	    @Commit //DB에 들어가는지 확인
	    @DisplayName("언론보도 상세 조회 - DTO 매핑 확인")
	    void testGetNewsDetail() {
	        System.out.println("===== 언론보도 상세 조회 테스트 =====");
	        
	        // Given
	        // 언론보도 생성 (JWT 우회) -> 자꾸 ID가 null로 나와서 직접 값을 넣어줬음
	        News news = News.builder()
	                .title("상세 조회 테스트")
	                .content("상세 조회 테스트 내용")
	                .view(0L)
	                .member(testMember)
	                .build();
	        
	        News savedNews = newsRepository.save(news); //DB에 저장
	        System.out.println("테스트용 보도자료 생성: " + savedNews.getTitle());
	        System.out.println("뉴스 번호: " + savedNews.getNewsNum());

	        // When - 상세 조회
	        NewsDetailDTO detailDTO = newsService.getNewsDetail(savedNews.getNewsNum());

	        // Then - 결과 확인
	        System.out.println("=== 조회 결과 ===");
	        System.out.println("제목: " + detailDTO.getTitle());
	        System.out.println("내용: " + detailDTO.getContent());
	        System.out.println("작성자: " + detailDTO.getName());
	        System.out.println("조회수: " + detailDTO.getView());

	        System.out.println("작성일: " + detailDTO.getCreatedAt());
	        
	        // 매핑 확인
	        boolean titleMatches = detailDTO.getTitle().equals(news.getTitle());
	        boolean contentMatches = detailDTO.getContent().equals(news.getContent());
	        boolean writerMatches = detailDTO.getName().equals(testMember.getName());
	        
	        System.out.println("=== 매핑 검증 ===");
	        System.out.println("제목 매핑 성공: " + titleMatches); //제목
	        System.out.println("내용 매핑 성공: " + contentMatches); //내용
	        System.out.println("작성자 매핑 성공: " + writerMatches); //작성자
	        
	        System.out.println("===== 상세 조회 테스트 완료 =====\n");
	    }
	    
//	    @Test
	    @Transactional
	    @Commit
	    @DisplayName("언론보도 목록 조회 - 페이징 확인")
	    void testGetNewsList() {
	        System.out.println("===== 언론보도 목록 조회 테스트 =====");
	        
	        // Given
	        // 보도자료 일괄 생성
	        for (int i = 1; i <= 5; i++) { //5번 반복
	            News news = News.builder()
	                    .title("언론보도 " + i)
	                    .content("내용 " + i)
	                    .view(10L)
	                    .member(testMember)
	                    .build();
	            newsRepository.save(news);
	        }
	        System.out.println("테스트용 보도자료 5개 생성 완료");

	        // When - 목록 조회
	        NewsSearchDTO searchDTO = new NewsSearchDTO();
	        Pageable pageable = PageRequest.of(0, 3); //페이지 크기 3
	        Page<NewsListDTO> result = newsService.getNewsList(searchDTO, pageable);

	        // Then - 결과 확인
	        System.out.println("=== 페이징 결과 ===");
	        System.out.println("전체 보도자료 개수: " + result.getTotalElements());
	        System.out.println("전체 페이지 수: " + result.getTotalPages());
	        System.out.println("현재 페이지 크기: " + result.getSize());
	        System.out.println("현재 페이지 실제 데이터 수: " + result.getContent().size());
	        
	        System.out.println("=== 조회된 언론보도 목록 ===");
	        for (int i = 0; i < result.getContent().size(); i++) {
	            NewsListDTO dto = result.getContent().get(i);
	            System.out.println((i+1) + ". " + dto.getTitle() + 
	                             " 작성자: " + dto.getName() + 
	                             ", 조회수: " + dto.getView());
	        }
	        
	        System.out.println("===== 목록 조회 테스트 완료 =====\n");
	    }
	    
//	    @Test
	    @Transactional
	    @Commit
	    @DisplayName("조회수 증가 로직")
	    void testIncreaseView() {
	        System.out.println("===== 조회수 증가 테스트 =====");
	        
	        // Given - 보도자료 생성
	        News news = News.builder()
	                .title("조회수 테스트")
	                .content("조회수 증가 테스트")
	                .view(0L)
	                .member(testMember)
	                .build();
	        
	        News savedNews = newsRepository.save(news);
	        Long newsNum = savedNews.getNewsNum();
	        System.out.println("초기 조회수: " + savedNews.getView());

	        // When & Then - 조회수 증가 테스트
	        for (int i = 1; i <= 3; i++) {
	            newsService.increaseView(newsNum);
	            
	            News updated = newsRepository.findById(newsNum).orElse(null);
	            if (updated != null) {
	                System.out.println(i + "번째 증가 후 조회수: " + updated.getView());
	            }
	        }

	        // 최종 확인
	        News finalNews = newsRepository.findById(newsNum).orElse(null);
	        System.out.println("최종 조회수: " + finalNews.getView());
	        System.out.println("총 증가량: " + finalNews.getView() + "회");
	        
	        System.out.println("===== 조회수 증가 테스트 완료 =====\n");
	    }
	    
//	    @Test
	    @Transactional
	    @Commit  
	    @DisplayName("언론보도 삭제 (단일)")
	    void testDeleteNews() {
	        System.out.println("===== 단일 삭제 테스트 =====");
	        
	        // Given - 보도자료 생성
	        News news = News.builder()
	                .title("삭제 테스트")
	                .content("삭제될 보도자료")
	                .view(0L)
	                .member(testMember)
	                .build();
	        
	        News savedNews = newsRepository.save(news);
	        Long newsNum = savedNews.getNewsNum();
	        
	        System.out.println("삭제 전 보도자료 개수: " + newsRepository.count());
	        System.out.println("삭제할 뉴스 번호: " + newsNum);

	        // When - 삭제 실행
	        newsService.deleteNews(newsNum);

	        // Then - 삭제 확인
	        long afterCount = newsRepository.count();
	        boolean exists = newsRepository.existsById(newsNum);
	        
	        System.out.println("삭제 후 보도자료 개수: " + afterCount);
	        System.out.println("해당 보도자료 존재 여부: " + exists);
	        
	        System.out.println("===== 단일 삭제 테스트 완료 =====\n");
	    }
	    
//	    @Test
	    @Transactional
	    @Commit
	    @DisplayName("언론보도 삭제 (일괄)")
	    void testDeleteNewsByIds() {
	        System.out.println("===== 일괄 삭제 테스트 =====");
	        
	        // Given - 보도자료 여러 개 생성
	        List<Long> newsIds = new ArrayList<>();
	        for (int i = 1; i <= 3; i++) {
	            News news = News.builder()
	                    .title("일괄 삭제 테스트 " + i)
	                    .content("삭제될 보도자료 " + i)
	                    .view(0L)
	                    .member(testMember)
	                    .build();
	            
	            News saved = newsRepository.save(news);
	            newsIds.add(saved.getNewsNum());
	        }
	        
	        System.out.println("삭제 전 보도자료 개수: " + newsRepository.count());
	        System.out.println("삭제할 보도자료 ID들: " + newsIds);

	        // When - 일괄 삭제 실행
	        newsService.deleteNewsByIds(newsIds);

	        // Then - 삭제 확인
	        long afterCount = newsRepository.count();
	        System.out.println("삭제 후 보도자료 개수: " + afterCount);
	        
	        for (Long id : newsIds) {
	            boolean exists = newsRepository.existsById(id);
	            System.out.println("ID " + id + " 존재 여부: " + exists);
	        }
	        
	        System.out.println("===== 일괄 삭제 테스트 완료 =====\n");
	    }
	    
//	    @Test
	    @Transactional
	    @DisplayName("존재하지 않는 언론보도 삭제 - 예외 처리")
	    void testDeleteNonExistentNews() {
	        System.out.println("===== 존재하지 않는 언론보도 삭제 테스트 =====");
	        
	        // Given - 존재하지 않는 ID
	        Long nonExistentId = 99999L;
	        System.out.println("존재하지 않는 ID: " + nonExistentId);

	        // When & Then - 예외 발생 확인
	        try {
	            newsService.deleteNews(nonExistentId);
	            System.out.println("예외가 발생하지 않음 - 테스트 실패!");
	        } catch (IllegalArgumentException e) {
	            System.out.println("예상된 예외 발생: " + e.getMessage());
	            System.out.println("적절한 예외 처리 확인됨");
	        } catch (Exception e) {
	            System.out.println("예상치 못한 예외 발생: " + e.getClass().getSimpleName());
	            System.out.println("예외 메시지: " + e.getMessage());
	        }
	        
	        System.out.println("===== 존재하지 않는 언론보도 삭제 테스트 완료 =====\n");
	    }
	    
	    @Test
	    @Transactional
	    @DisplayName("일괄 삭제 시 일부 존재하지 않는 ID - 예외 처리")
	    void testBatchDeleteWithNonExistentIds() {
	        System.out.println("===== 일괄 삭제 예외 처리 테스트 =====");
	        
	        // Given - 일부는 존재하고 일부는 존재하지 않는 ID
	        News news = News.builder()
	                .title("존재하는 보도자료")
	                .content("실제 존재하는 보도자료")
	                .view(0L)
	                .member(testMember)
	                .build();
	        
	        News savedNews = newsRepository.save(news);
	        
	        List<Long> mixedIds = Arrays.asList(
	            savedNews.getNewsNum(), // 존재하는 ID
	            99998L, // 존재하지 않는 ID
	            99999L  // 존재하지 않는 ID
	        );
	        
	        System.out.println("삭제 시도할 ID들: " + mixedIds);
	        System.out.println("실제 존재하는 ID: " + savedNews.getNewsNum());

	        // When & Then - 예외 발생 확인
	        try {
	            newsService.deleteNewsByIds(mixedIds);
	            System.out.println("예외가 발생하지 않음 - 테스트 실패!");
	        } catch (IllegalArgumentException e) {
	            System.out.println("예상된 예외 발생: " + e.getMessage());
	            System.out.println("존재하지 않는 ID들이 올바르게 식별됨");
	        } catch (Exception e) {
	            System.out.println("예상치 못한 예외 발생: " + e.getClass().getSimpleName());
	            System.out.println("예외 메시지: " + e.getMessage());
	        }
	        
	        System.out.println("===== 일괄 삭제 예외 처리 테스트 완료 =====\n");
	    }

}
