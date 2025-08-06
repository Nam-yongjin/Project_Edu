package com.EduTech.dto.admin;

import java.util.List;

import com.EduTech.entity.member.MemberState;

import lombok.Data;

@Data
public class MemberStateChangeDto {
	private List<String> memId;
	private MemberState state;
}