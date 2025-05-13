package com.aja.ott.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<Map<String, Object>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
		Map<String, Object> errorResponse = new HashMap<>();

		errorResponse.put("status", HttpStatus.CONFLICT.value());
		errorResponse.put("error", "Conflict");
		errorResponse.put("message", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler(InvalidUserNamePasswordException.class)
	public ResponseEntity<Map<String, String>> handleInvalidUserNameOrPasswordException(
			InvalidUserNamePasswordException ex) {
		Map<String, String> response = new HashMap<>();
		response.put("error", ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ScoreException.class)
	public ResponseEntity<Map<String, String>> handleScoreException(ScoreException ex) {
		Map<String, String> response = new HashMap<>();
		response.put("error", ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
	}
	
	 @ExceptionHandler(ResourceNotFoundException.class)
	    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
	        Map<String, Object> body = new HashMap<>();
	        body.put("timestamp", LocalDateTime.now());
	        body.put("status", HttpStatus.NOT_FOUND.value());
	        body.put("error", "Resource Not Found");
	        body.put("message", ex.getMessage());

	        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	    }

}
