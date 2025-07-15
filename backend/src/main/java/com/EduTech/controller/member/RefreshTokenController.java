package com.EduTech.controller.member;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.security.jwt.JWTException;
import com.EduTech.security.jwt.JWTProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

	// Access Token 재발급
	@RequestMapping("/api/member/refresh")
	public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken) {	

		// 유효성 검사
		if (refreshToken == null) {
			throw new JWTException("NULL_REFRASH");
		}

		if (authHeader == null || authHeader.length() < 7) {
			throw new JWTException("INVALID_STRING");
		}

		// Access Token에서 진짜 토큰 부분 추출
		String accessToken = authHeader.substring(7);
		// Access 토큰이 만료되지 않았다면 기존 토큰 그대로 반환
		if (checkExpiredToken(accessToken) == false) {
			return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
		}

		// Refresh토큰 검증
		Map<String, Object> claims = JWTProvider.validateToken(refreshToken);

		String newAccessToken = JWTProvider.generateToken(claims, 10);

		// Refresh Token도 갱신할지 결정
		String newRefreshToken = checkTime((Integer) claims.get("exp")) == true
				? JWTProvider.generateToken(claims, 60 * 24)
				: refreshToken;
		return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);

	}

	// 시간이 1시간 미만으로 남았다면
	private boolean checkTime(Integer exp) {

		// JWT exp를 날짜로 변환
		java.util.Date expDate = new java.util.Date((long) exp * (1000));

		// 현재 시간과의 차이 계산 - 밀리세컨즈
		long gap = expDate.getTime() - System.currentTimeMillis();

		// 분단위 계산
		long leftMin = gap / (1000 * 60);

		// 1시간도 안남았는지
		return leftMin < 60;
	}

	// AccessToken이 만료되었는지 확인
	private boolean checkExpiredToken(String token) {

		try {
			JWTProvider.validateToken(token);
		} catch (JWTException e) {
			if (e.getMessage().equals("Expired")) {
				return true;
			}
		}
		return false;
	}
}
