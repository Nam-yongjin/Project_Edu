package com.EduTech.service.member;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.EduTech.dto.member.MemberDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService{

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	System.out.println(">> 로그인 시도 ID: " + username);
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
        if(member.getState() == MemberState.LEAVE) {
        	throw new UsernameNotFoundException("LEAVE_MEMBER");
        }

        return User.builder()
                .username(member.getMemId())
                .password(member.getPw()) // bcrypt 인코딩된 비밀번호
                .roles("USER") // 권한
                .build();
    }
    
	public UserDetails loadUserByKakao(String kakaoEmail) throws UsernameNotFoundException {
		System.out.println(">> 로그인 시도 ID: " + kakaoEmail);
		Member member = memberRepository.findByKakao(kakaoEmail)
				.orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
		
		MemberDTO memberDTO = memberService.entityToDTO(member);
		
		return memberDTO;
	}
}
