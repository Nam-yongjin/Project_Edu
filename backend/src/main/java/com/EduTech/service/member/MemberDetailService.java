package com.EduTech.service.member;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.EduTech.dto.member.MemberDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.member.MemberRepository;

import lombok.RequiredArgsConstructor;

// UserDetailsService: Spring Security가 제공하는 인터페이스, 사용자 이름(username)으로 사용자 정보를 가져오는 역할
// 로그인 시 AuthenticationManager → UserDetailsService.loadUserByUsername() 호출
@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService{ 

    private final MemberRepository memberRepository;
    
    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
        
        MemberDTO memberDTO = memberService.entityToDTO(member);
        return memberDTO;
    }
    
}
