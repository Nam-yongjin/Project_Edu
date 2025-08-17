package com.EduTech.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminEmailMembersDTO {
	private String memId;
	private String email;
	private String name;
}
