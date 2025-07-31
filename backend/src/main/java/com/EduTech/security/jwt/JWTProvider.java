package com.EduTech.security.jwt;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

public class JWTProvider {

	public static String KEY = "secretKey!@#accessToken!@#refreshToekn!@#generateToken!@#validateToken";

	public static String generateToken(Map<String, Object> claims, int min) {

		SecretKey key;

//		claims에 담긴 사용자 정보를 바탕으로 JWT 토큰 생성
		try {
			key = Keys.hmacShaKeyFor(JWTProvider.KEY.getBytes("UTF-8"));

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return Jwts.builder().setHeaderParam("typ", "JWT").setClaims(claims)
				.setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
				.setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant())).signWith(key).compact();

	}

	public static Map<String, Object> validateToken(String token) {
		Map<String, Object> claims = null;

//		전달된 토큰을 검증하고, 유효하면 claim(Map) 반환
		try {
			SecretKey key = Keys.hmacShaKeyFor(JWTProvider.KEY.getBytes("UTF-8"));
			claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
					.getBody();
		} catch (ExpiredJwtException e) {
			throw new JWTException("Expired"); // 액세스 토큰 만료 시 "Expired" 예외 발생
		} catch (JwtException e) {
			throw new JWTException("Invalid Token");
		} catch (Exception e) {
			throw new JWTException("Error");
		}
		return claims;
	}
}
