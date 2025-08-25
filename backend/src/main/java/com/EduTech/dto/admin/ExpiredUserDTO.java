package com.EduTech.dto.admin;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpiredUserDTO {
	String email;;
	LocalDate endDate;
}
