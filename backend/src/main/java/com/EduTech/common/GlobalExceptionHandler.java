package com.EduTech.common;

import java.nio.file.AccessDeniedException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.EduTech.security.jwt.JWTException;

// 전역예외처리 따로 관리
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 유효성 검증 실패
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
	
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<String> handleIllegalState(IllegalStateException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
	
	// 권한 에러
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAccessDenied(AccessDeniedException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
	
	// JWT 에러
	@ExceptionHandler(JWTException.class)
	public ResponseEntity<String> handleJWTException(JWTException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	// 서버에러
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleOther(Exception e) {
		return ResponseEntity.internalServerError().body(e.getMessage());
	}
}
