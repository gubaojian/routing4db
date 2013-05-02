package com.google.code.routing4db.exception;

public class RoutingException extends RuntimeException{
	private static final long serialVersionUID = 9104194486509552032L;

	public RoutingException() {
		super();
	}

	public RoutingException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoutingException(String message) {
		super(message);
	}

	public RoutingException(Throwable cause) {
		super(cause);
	}

}
