package com.aja.ott.exception;

public class CustomDatabaseException extends RuntimeException{
	public CustomDatabaseException(String msg) {
		super(msg);
	}
	public CustomDatabaseException(String msg,Throwable e) {
		super(msg,e);
	}

}
