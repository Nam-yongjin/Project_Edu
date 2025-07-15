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

	public static String key;

	public static String generateToken(Map<String, Object> valueMap, int min) {

		SecretKey key = null;

//		claims에 담긴 사용자 정보를 바탕으로 JWT 토큰 생성
		try {
			key = Keys.hmacShaKeyFor(JWTProvider.key.getBytes("UTF-8"));

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		String jwtStr = Jwts.builder().setHeader(Map.of("typ", "JWT")).setClaims(valueMap)
				.setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
				.setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant())).signWith(key).compact();

		return jwtStr;

	}

	public static Map<String, Object> validateToken(String token) {
		Map<String, Object> claim = null;

//		전달된 토큰을 검증하고, 유효하면 claim(Map) 반환
		try {
			SecretKey key = Keys.hmacShaKeyFor(JWTProvider.key.getBytes("UTF-8"));
			claim = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
					.getBody();
		} catch (MalformedJwtException malformedJwtException) {
			throw new JWTException("MalFormed");
		} catch (ExpiredJwtException expiredJwtException) {
			throw new JWTException("Expired");
		} catch (InvalidClaimException invalidClaimException) {
			throw new JWTException("Invalid");
		} catch (JwtException jwtException) {
			throw new JWTException("JWTError");
		} catch (Exception e) {
			throw new JWTException("Error");
		}
		return claim;
	}
}
