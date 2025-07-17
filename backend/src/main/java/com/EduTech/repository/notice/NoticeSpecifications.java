package com.EduTech.repository.notice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.EduTech.dto.notice.NoticeSearchDTO;
import com.EduTech.entity.notice.Notice;

import jakarta.persistence.criteria.Predicate;

//사용자 검색용
//NoticeEntity에 대한 정적 Specification 생성
public class NoticeSpecifications {
	//NoticeSearchDTO를 받아서 Specification 생성 //타입과 키워드를 조건으로 사용
	public static Specification<Notice> fromDTO(NoticeSearchDTO dto) {
		return Specification.where(searchFilter(dto.getSearchType(), dto.getKeyword()));
	}
	
	//사용자 검색 필터(단일 조건 - return)
	//검색 조건을 필드별로 생성해주는 메서드
	private static Specification<Notice> searchFilter(String searchType, String keyword) {
			return (root, query, cb) -> {
				//검색 타입과 키워드 중 하나가 true(null)이라면 조건이 무시되어 전체 검색
				if (searchType == null || keyword == null || keyword.trim().isEmpty()) {
					return null;								
				}
				
				switch (searchType) {
				case "제목":
					return cb.like(root.get("title"), "%" + keyword +"%");
					
				case "내용":
					return cb.like(root.get("content"), "%" + keyword +"%");
					
				case "작성자":
					return cb.and(
							//NullPointerException 방지
							cb.isNotNull(root.get("member")),
					        cb.like(root.get("member").get("name"), "%" + keyword + "%")
					    );
					
				default: //예상한 조건이 아닐 경우 null반환
					return null;
				}
			};
	}  
	
	//관리자 검색 필터(다중 조건 조합 - add, break)
	//BoardSearchDTO를 받아서 Specification 생성
	public static Specification<Notice> adminFilter(BoardSearchDTO dto) {
		return (root, query, cb) -> {
			//빈 리스트 생성
			List<Predicate> predicates = new ArrayList<>();
			//키워드가 null이나 empty가 아닐 경우 조건 추가
			if(dto.getSearchKeyword() != null && !dto.SearchKeyword().trim().isEmpty()) {
				
				switch (dto.getOption()) {
					case "제목":
						predicates.add(cb.like(root.get("title"), "%" + dto.getSearchKeyword() + "%"));
						break;
					case "작성자":
						predicates.add(cb.like(root.get("name"), "%" + dto.getSearchKeyword() + "%"));
						break;
					case "회원아이디":
						predicates.add(cb.like(root.get("writerId"), "%" + dto.getSearchKeyword() + "%"));
						break;
				}
			};
			//고정된 공지만 보여주는 조건
			if (dto.getShowPinnedOnly() != null && dto.getShowPinnedOnly()) {
			    predicates.add(cb.equal(root.get("isPinned"), true));
			}
			//고정이 아닌 공지만 보여주는 조건
			if (dto.getShowUnpinnedOnly() != null && dto.getShowUnpinnedOnly()) {
			    predicates.add(cb.equal(root.get("isPinned"), false));
			}
			//StartDate랑 EndDate가 null이 아닐 경우 조건 추가
			//설정한 시간 사이에 작성된 게시물만 필터링
			if (dto.getStartDate() != null && dto.getEndDate() != null) {
                predicates.add(cb.between(
                    root.get("createdAt"),
                    dto.getStartDate().atStartOfDay(),
                    dto.getEndDate().atTime(23, 59, 59)
                ));
            }	
			//모든 조건을 and로 묶어서 적용
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}