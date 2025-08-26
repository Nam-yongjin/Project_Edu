package com.EduTech.repository.notice;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.entity.notice.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice>{ //대상이 되는 Entity, PK의 속성 타입
	
	@EntityGraph(attributePaths = {"noticeFiles"})	//파일 삭제에서 N+1문제가 계속 발생해서 넣음 //Specification 적용하기 위해 추가
	Page<Notice> findAll(Specification<Notice> spec, Pageable pageable); //Specification 조건에 맞는 공지사항을 페이징 처리해서 가져옴	
	List<Notice> findAllByIsPinned(boolean isPinned, Sort sort); //고정여부에 따라 공지 조회, 원하는 정렬 방식 전달
	
	// 공지사항 상세 조회 시 첨부파일까지 같이 가져오기
	
	// Notice 엔티티를 n이라는 별칭으로 조회 -> Notice 엔티티에 연관된 noticeFiles를 즉시 가져옴 -> 조건:특정 공지사항 번호에 해당하는 Notice만 가져오기
	// JOIN: 두 테이블(또는 엔티티)를 연결해서 가져옴, LEFT JOIN: 왼쪽(Notice)은 무조건 가져오고 오른쪽(noticeFiles)은 없으면 NULL처리, FETCH: 연관된 엔티티까지 같이 SELECT해서 영속성 컨텍스트에 로딩
	// JOIN FETCH를 쓰면 Notice와 noticeFiles를 한 번에 가져오기 때문에 N+1문제 방지
	@Query("SELECT n FROM Notice n LEFT JOIN FETCH n.noticeFiles WHERE n.noticeNum = :noticeNum")
	// Optional: 조회 결과가 존재하지 않을 수도 있음을 표현
	Optional<Notice> findByIdWithFiles(@Param("noticeNum") Long noticeNum);
	
	// 조회수 증가
	
	// Notice의 viewCount 필드를 현재 값에서 + 1 증가 시킴 -> 특정 공지사항 번호에 해당하는 row만 업데이트
	@Modifying
	// @Modifying이 붙은 메서드는 Spring Data JPA가 내부적으로 트랜잭션을 열어주고 flush까지 해주기 때문에 DB에 반영 -> increaseViewCount에는 @Transactional이 없어도 동작
	@Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.noticeNum = :noticeNum")
	// 리턴 타입이 int인 이유: UPDATE, DELETE 쿼리는 영향받은 row 수를 리턴 -> 즉, 여기선 1이 리턴됨(공지사항 하나만 업데이트 하니까)
	int increaseViewCount(@Param("noticeNum") Long noticeNum);
	
	// 일괄 삭제
	
	// Notice 엔티티를 삭제 -> 전달 받은 noticeNums 리스트 안에 있는 noticeNum들을 모두 삭제
	@Modifying //DELETE, UPDATE 같은 데이터 변경 쿼리를 실행할 땐 @Modifying를 붙여야 함
	@Transactional //DB에 반영되어야 해서 필요(굳이 안 붙여도 되지만 삭제의 경우 트랜젝션 범위를 명확하게 하는 게 더 안전)
    @Query("DELETE FROM Notice n WHERE n.noticeNum IN :noticeNums")
	// void: 굳이 영향 받은 row 수를 알 필요 없을 때
    void deleteByNoticeNumIn(@Param("noticeNums") List<Long> noticeNums); //일괄삭제(noticeNum값들의 목록)
		
}
