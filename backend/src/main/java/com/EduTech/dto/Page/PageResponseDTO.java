package com.EduTech.dto.Page;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Data
public class PageResponseDTO <T>{
	 private List<T> content;
	    private int totalPages;
	    private int currentPage;

	    public PageResponseDTO(Page<T> page) {
	        this.content = page.getContent(); // 페이지 요소
	        this.totalPages = page.getTotalPages(); // 전체 페이지
	        this.currentPage = page.getNumber(); // 현제 페이지 
	    }
	    
	 
}
