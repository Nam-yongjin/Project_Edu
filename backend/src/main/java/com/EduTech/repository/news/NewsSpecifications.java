package com.EduTech.repository.news;

import org.springframework.data.jpa.domain.Specification;

import com.EduTech.dto.news.NewsSearchDTO;
import com.EduTech.entity.news.News;

public class NewsSpecifications { //NewsEntity에 대한 정적 Specifications 생성
	
	public static Specification<News> fromDTO(NewsSearchDTO dto) { //NewsSearchDTO를 받아서 Specification 생성
		return Specification
				.where(searchFilter(dto.getSearchType(), dto.getKeyword())); //타입과 키워드를 조건으로 사용	
	}
	
	private static Specification<News> searchFilter(String searchType, String keyword) { //검색 조건을 필드별로 생성해주는 메서드 
			return (root, query, cb) -> {
				if (searchType == null || keyword == null || keyword.trim().isEmpty()) { //검색 타입과 키워드가 null이나 공백이 아니어야 조건 생성
					return null; //null 반환: 조건 무시되어 전체 검색
				}
				
				switch (searchType) {
				case "제목": //제목에 검색어가 포함되는 조건 생성
					return cb.like(root.get("title"), "%" + keyword +"%");
					
				case "내용": //내용에 검색어가 포함되는 조건 생성
					return cb.like(root.get("content"), "%" + keyword +"%");
					
				case "작성자": //member가 null이 아닐 때만 검색어가 포함되는 조건 생성
					return cb.and(
							cb.isNotNull(root.get("member")), //NullPointerException 방지
					        cb.like(root.get("member").get("name"), "%" + keyword + "%")
					    );
					
				default:
					return null;
				}
			};
	}

}
