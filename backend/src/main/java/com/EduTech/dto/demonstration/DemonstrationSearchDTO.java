package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DemonstrationSearchDTO {
	private String search;
	private String type;
	 private String sortBy;
	 private String sort;
	 private Integer pageCount;
}
