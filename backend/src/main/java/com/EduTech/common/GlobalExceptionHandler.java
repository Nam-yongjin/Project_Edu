package com.EduTech.common;

import java.nio.file.AccessDeniedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import com.EduTech.security.jwt.JWTException;

// 전역예외처리 따로 관리
@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
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
	
	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<String> handleMultipartException(MultipartException e) {
	    logger.error("Multipart 처리 중 오류 발생", e);
	    // 에러 메시지를 좀 더 친절하게 변경 가능
	    String msg = "파일 업로드 중 문제가 발생했습니다. 파일 크기 제한이나 형식을 확인하세요. (" + e.getMessage() + ")";
	    return ResponseEntity.status(400).body(msg);
	}
}
