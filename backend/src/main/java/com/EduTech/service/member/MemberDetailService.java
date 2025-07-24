package com.EduTech.service.member;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.EduTech.dto.member.MemberDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService{

    private final MemberRepository memberRepository;
    
    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
        
        if(member.getState() == MemberState.LEAVE) {
        	throw new UsernameNotFoundException("LEAVE_MEMBER");
        }
        
        MemberDTO memberDTO = memberService.entityToDTO(member);
        return memberDTO;
    }
    
	public UserDetails loadUserByKakao(String kakaoEmail) throws UsernameNotFoundException {
		System.out.println(">> 로그인 시도 ID: " + kakaoEmail);
		Member member = memberRepository.findByKakao(kakaoEmail)
				.orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
		
		MemberDTO memberDTO = memberService.entityToDTO(member);
		
		return memberDTO;
	}
}
