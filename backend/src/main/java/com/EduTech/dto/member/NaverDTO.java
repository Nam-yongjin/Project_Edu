package com.EduTech.dto.member;

import java.time.LocalDate;

import com.EduTech.entity.member.MemberGender;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NaverDTO {
    private String name;
    private String email;
    private LocalDate birthDate;
    private MemberGender gender;
    private String phone;
}
