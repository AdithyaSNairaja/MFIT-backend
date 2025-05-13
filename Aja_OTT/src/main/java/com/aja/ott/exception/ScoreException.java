package com.aja.ott.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class ScoreException extends RuntimeException{
	public ScoreException(String message) {
		super(message);
	}
}
