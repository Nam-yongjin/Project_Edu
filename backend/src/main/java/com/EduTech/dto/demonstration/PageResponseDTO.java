package com.EduTech.dto.demonstration;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;
import lombok.ToString;

// 프론트에서 현재 페이지 요청 시 
// 요청한 페이지 정보를 전달하기 위한 dto
// 페이지 정보를 전달하는 종류가 많기에
// 제네릭 클래스를 사용하였음.
@ToString
@Data
public class PageResponseDTO<T> { 
	 private List<T> content;
	    private int totalPages;
	    private int currentPage;

	    public PageResponseDTO(Page<T> page) {
	        this.content = page.getContent(); // 페이지 요소
	        this.totalPages = page.getTotalPages(); // 전체 페이지
	        this.currentPage = page.getNumber(); // 현제 페이지 
	    }
}
