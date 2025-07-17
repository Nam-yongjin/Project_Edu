package com.EduTech.dto.admin;

import java.util.List;

import lombok.Data;

@Data
public class BoardTypeDTO {
	private String boardType; //게시판 유형

    private List<Long> numbers; //게시판 글 번호 리스트
}
