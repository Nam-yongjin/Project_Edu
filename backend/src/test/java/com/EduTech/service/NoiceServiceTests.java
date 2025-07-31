package com.EduTech.service;

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

import com.EduTech.dto.notice.NoticeDetailDTO;
import com.EduTech.dto.notice.NoticeListDTO;
import com.EduTech.dto.notice.NoticeSearchDTO;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.event.EventUse;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.notice.Notice;
import com.EduTech.repository.event.EventUseRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.notice.NoticeRepository;
import com.EduTech.service.event.EventServiceImpl;
import com.EduTech.service.notice.NoticeService;
import com.EduTech.util.FileUtil;

@SpringBootTest
@Transactional
public class NoiceServiceTests {

	@Autowired
	private NoticeRepository noticeRepository;

	@Autowired
	private NoticeService noticeService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FileUtil fileUtil;

	@Autowired
	private ModelMapper modelMapper;
	
	@MockBean
	EventServiceImpl eventServiceImpl;
	@MockBean
	EventState eventState;
	@MockBean
	EventUse eventUse;
	@MockBean
	EventUseRepository eventUseRepository; 

	

//	@BeforeEach // 테스트용 데이터
//	void setUp() {
//		
//        // 테스트 회원 생성
//        testMember = new Member();
//        testMember.setMemId("testUser");
//        testMember.setName("테스트사용자");
//        memberRepository.save(testMember);
//        
//        System.out.println("테스트 회원 생성 완료: " + testMember.getName());
//    }

//	@Test
    @Transactional //테스트 후 DB삭제
    @Commit //DB에 들어가는지 확인
    @DisplayName("공지사항 상세 조회 - DTO 매핑 확인")
    void testGetNoticeDetail() {
    	Member adminMember = memberRepository.findById("admin").orElseThrow();
        System.out.println("===== 공지사항 상세 조회 테스트 =====");
        
        // Given
        // 공지사항 생성 (JWT 우회) -> 자꾸 ID가 null로 나와서 직접 값을 넣어줬음
        Notice notice = Notice.builder()
                .title("상세 조회 테스트")
                .content("상세 조회 테스트 내용")
                .isPinned(false)
                .viewCount(0L)
                .member(adminMember)
                .build();
        
        Notice savedNotice = noticeRepository.save(notice); //DB에 저장
        System.out.println("테스트용 공지사항 생성: " + savedNotice.getTitle());
        System.out.println("공지사항 번호: " + savedNotice.getNoticeNum());

        // When - 상세 조회
        NoticeDetailDTO detailDTO = noticeService.getNoticeDetail(savedNotice.getNoticeNum());

        // Then - 결과 확인
        System.out.println("=== 조회 결과 ===");
        System.out.println("제목: " + detailDTO.getTitle());
        System.out.println("내용: " + detailDTO.getContent());
        System.out.println("작성자: " + detailDTO.getName());
        System.out.println("조회수: " + detailDTO.getViewCount());

        System.out.println("작성일: " + detailDTO.getCreatedAt());
        
        // 매핑 확인
        boolean titleMatches = detailDTO.getTitle().equals(notice.getTitle());
        boolean contentMatches = detailDTO.getContent().equals(notice.getContent());
        boolean writerMatches = detailDTO.getName().equals(adminMember.getName());
        
        System.out.println("=== 매핑 검증 ===");
        System.out.println("제목 매핑 성공: " + titleMatches); //제목
        System.out.println("내용 매핑 성공: " + contentMatches); //내용
        System.out.println("작성자 매핑 성공: " + writerMatches); //작성자
        
        System.out.println("===== 상세 조회 테스트 완료 =====\n");
    }
    
//    @Test
    @Transactional
    @Commit
    @DisplayName("공지사항 목록 조회 - 페이징 확인")
    void testGetNoticeList() {
    	Member adminMember = memberRepository.findById("admin").orElseThrow();
        System.out.println("===== 공지사항 목록 조회 테스트 =====");
        
        // Given
        // 공지사항 일괄 생성
        for (int i = 1; i <= 5; i++) { //5번 반복
            Notice notice = Notice.builder()
                    .title("공지사항 " + i)
                    .content("내용 " + i)
                    .isPinned(i % 2 == 0) // 짝수번째만 고정
                    .viewCount(10L)
                    .member(adminMember)
                    .build();
            noticeRepository.save(notice);
        }
        System.out.println("테스트용 공지사항 5개 생성 완료");

        // When - 목록 조회
        NoticeSearchDTO searchDTO = new NoticeSearchDTO();
        Pageable pageable = PageRequest.of(0, 3); //페이지 크기 3
        Page<NoticeListDTO> result = noticeService.getNoticeList(searchDTO);

        // Then - 결과 확인
        System.out.println("=== 페이징 결과 ===");
        System.out.println("전체 공지사항 개수: " + result.getTotalElements());
        System.out.println("전체 페이지 수: " + result.getTotalPages());
        System.out.println("현재 페이지 크기: " + result.getSize());
        System.out.println("현재 페이지 실제 데이터 수: " + result.getContent().size());
        
        System.out.println("=== 조회된 공지사항 목록 ===");
        for (int i = 0; i < result.getContent().size(); i++) {
            NoticeListDTO dto = result.getContent().get(i);
            System.out.println((i+1) + ". " + dto.getTitle() + 
                             " (작성자: " + dto.getName() + 
                             ", 조회수: " + dto.getViewCount() + 
                             ", 고정: " + dto.getIsPinned() + ")");
        }
        
        System.out.println("===== 목록 조회 테스트 완료 =====\n");
    }
    
//    @Test
    @Transactional 
    @Commit
    @DisplayName("고정 공지사항 조회 - 정렬 확인")
    void testFindPinned() {
    	Member adminMember = memberRepository.findById("admin").orElseThrow();
        System.out.println("===== 고정 공지사항 조회 테스트 =====");
        
        // Given - 고정/일반 공지사항 생성
        Notice pinnedNotice1 = Notice.builder()
                .title("고정 공지 1")
                .content("첫 번째 고정 공지")
                .isPinned(true)
                .viewCount(100L)
                .member(adminMember)
                .build();
        
        Notice normalNotice = Notice.builder()
                .title("일반 공지")
                .content("일반 공지사항")
                .isPinned(false)
                .viewCount(50L)
                .member(adminMember)
                .build();
        
        Notice pinnedNotice2 = Notice.builder()
                .title("고정 공지 2")
                .content("두 번째 고정 공지")
                .isPinned(true)
                .viewCount(200L)
                .member(adminMember)
                .build();
        
        noticeRepository.save(pinnedNotice1);
        noticeRepository.save(normalNotice);
        noticeRepository.save(pinnedNotice2);

        // When - 고정 공지사항만 조회
        List<NoticeListDTO> pinnedList = noticeService.getPinnedNotices();

        // Then - 결과 확인
        System.out.println("=== 고정 공지사항 조회 결과 ===");
        System.out.println("조회된 고정 공지사항 개수: " + pinnedList.size());
        
        for (int i = 0; i < pinnedList.size(); i++) {
            NoticeListDTO dto = pinnedList.get(i);
            System.out.println((i+1) + ". " + dto.getTitle() + 
                             " 고정: " + dto.getIsPinned());
        }
        
        // 정렬 확인 (최신순)
        if (pinnedList.size() >= 2) {
            boolean isCorrectOrder = pinnedList.get(0).getTitle().equals("고정 공지 2");
            System.out.println("최신순 정렬 확인: " + isCorrectOrder); //고정 공지 2가 첫 번째여야 성공
        }
        
        System.out.println("===== 고정 공지사항 조회 테스트 완료 =====\n");
    }
    
//    @Test
    @Transactional
    @Commit
    @DisplayName("조회수 증가 로직")
    void testIncreaseView() {
    	Member adminMember = memberRepository.findById("admin").orElseThrow();
        System.out.println("===== 조회수 증가 테스트 =====");
        
        // Given - 공지사항 생성
        Notice notice = Notice.builder()
                .title("조회수 테스트")
                .content("조회수 증가 테스트")
                .isPinned(false)
                .viewCount(0L)
                .member(adminMember)
                .build();
        
        Notice savedNotice = noticeRepository.save(notice);
        Long noticeNum = savedNotice.getNoticeNum();
        System.out.println("초기 조회수: " + savedNotice.getViewCount());

        // When & Then - 조회수 증가 테스트
        for (int i = 1; i <= 3; i++) {
            noticeService.increaseViewCount(noticeNum);
            
            Notice updated = noticeRepository.findById(noticeNum).orElse(null);
            if (updated != null) {
                System.out.println(i + "번째 증가 후 조회수: " + updated.getViewCount());
            }
        }

        // 최종 확인
        Notice finalNotice = noticeRepository.findById(noticeNum).orElse(null);
        System.out.println("최종 조회수: " + finalNotice.getViewCount());
        System.out.println("총 증가량: " + finalNotice.getViewCount() + "회");
        
        System.out.println("===== 조회수 증가 테스트 완료 =====\n");
    }
    
//    @Test
    @Transactional
    @Commit  
    @DisplayName("공지사항 삭제 (단일)")
    void testDeleteNotice() {
    	Member adminMember = memberRepository.findById("admin").orElseThrow();
        System.out.println("===== 단일 삭제 테스트 =====");
        
        // Given - 공지사항 생성
        Notice notice = Notice.builder()
                .title("삭제 테스트")
                .content("삭제될 공지사항")
                .isPinned(false)
                .viewCount(0L)
                .member(adminMember)
                .build();
        
        Notice savedNotice = noticeRepository.save(notice);
        Long noticeNum = savedNotice.getNoticeNum();
        
        System.out.println("삭제 전 공지사항 개수: " + noticeRepository.count());
        System.out.println("삭제할 공지사항 번호: " + noticeNum);

        // When - 삭제 실행
        noticeService.deleteNotice(noticeNum);

        // Then - 삭제 확인
        long afterCount = noticeRepository.count();
        boolean exists = noticeRepository.existsById(noticeNum);
        
        System.out.println("삭제 후 공지사항 개수: " + afterCount);
        System.out.println("해당 공지사항 존재 여부: " + exists);
        
        System.out.println("===== 단일 삭제 테스트 완료 =====\n");
    }
    
//    @Test
    @Transactional
    @Commit
    @DisplayName("공지사항 일괄 삭제")
    void testDeleteNotices() {
    	Member adminMember = memberRepository.findById("admin").orElseThrow();
        System.out.println("===== 일괄 삭제 테스트 =====");
        
        // Given - 공지사항 여러 개 생성
        List<Long> noticeIds = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Notice notice = Notice.builder()
                    .title("일괄 삭제 테스트 " + i)
                    .content("삭제될 공지사항 " + i)
                    .isPinned(false)
                    .viewCount(0L)
                    .member(adminMember)
                    .build();
            
            Notice saved = noticeRepository.save(notice);
            noticeIds.add(saved.getNoticeNum());
        }
        
        System.out.println("삭제 전 공지사항 개수: " + noticeRepository.count());
        System.out.println("삭제할 공지사항 ID들: " + noticeIds);

        // When - 일괄 삭제 실행
        noticeService.deleteNotices(noticeIds);

        // Then - 삭제 확인
        long afterCount = noticeRepository.count();
        System.out.println("삭제 후 공지사항 개수: " + afterCount);
        
        for (Long id : noticeIds) {
            boolean exists = noticeRepository.existsById(id);
            System.out.println("ID " + id + " 존재 여부: " + exists);
        }
        
        System.out.println("===== 일괄 삭제 테스트 완료 =====\n");
    }
    
//    @Test
    @Transactional
    @DisplayName("존재하지 않는 공지사항 삭제 - 예외 처리")
    void testDeleteNonExistentNotice() {
        System.out.println("===== 존재하지 않는 공지사항 삭제 테스트 =====");
        
        // Given - 존재하지 않는 ID
        Long nonExistentId = 99999L;
        System.out.println("존재하지 않는 ID: " + nonExistentId);

        // When & Then - 예외 발생 확인
        try {
            noticeService.deleteNotice(nonExistentId);
            System.out.println("예외가 발생하지 않음 - 테스트 실패!");
        } catch (IllegalArgumentException e) {
            System.out.println("예상된 예외 발생: " + e.getMessage());
            System.out.println("적절한 예외 처리 확인됨");
        } catch (Exception e) {
            System.out.println("예상치 못한 예외 발생: " + e.getClass().getSimpleName());
            System.out.println("예외 메시지: " + e.getMessage());
        }
        
        System.out.println("===== 존재하지 않는 공지사항 삭제 테스트 완료 =====\n");
    }
    
//    @Test
    @Transactional
    @DisplayName("일괄 삭제 시 일부 존재하지 않는 ID - 예외 처리")
    void testBatchDeleteWithNonExistentIds() {
    	Member adminMember = memberRepository.findById("admin").orElseThrow();
        System.out.println("===== 일괄 삭제 예외 처리 테스트 =====");
        
        // Given - 일부는 존재하고 일부는 존재하지 않는 ID
        Notice notice = Notice.builder()
                .title("존재하는 공지사항")
                .content("실제 존재하는 공지사항")
                .isPinned(false)
                .viewCount(0L)
                .member(adminMember)
                .build();
        
        Notice savedNotice = noticeRepository.save(notice);
        
        List<Long> mixedIds = Arrays.asList(
            savedNotice.getNoticeNum(), // 존재하는 ID
            99998L, // 존재하지 않는 ID
            99999L  // 존재하지 않는 ID
        );
        
        System.out.println("삭제 시도할 ID들: " + mixedIds);
        System.out.println("실제 존재하는 ID: " + savedNotice.getNoticeNum());

        // When & Then - 예외 발생 확인
        try {
            noticeService.deleteNotices(mixedIds);
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
