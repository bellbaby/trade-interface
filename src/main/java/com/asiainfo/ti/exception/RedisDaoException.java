package com.asiainfo.ti.exception;

public class RedisDaoException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RedisDaoException() {
		super();
	}

	public RedisDaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public RedisDaoException(String message) {
		super(message);
	}

	public RedisDaoException(Throwable cause) {
		super(cause);
	}

	
}
