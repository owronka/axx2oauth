package com.axxessio.oauth2.server.common;

public class ApplicationException extends Exception {
	private static final long serialVersionUID = 1L;

	int    errorCode;
	String errorDescription;
	int    errorStatus;
	
	public ApplicationException (int newErrorCode, String newErrorDescription) {
		this.errorCode = newErrorCode;
		this.errorDescription = newErrorDescription;
	}

	public ApplicationException (int newErrorCode, String newErrorDescription, int newErrorStatus) {
		this.errorCode = newErrorCode;
		this.errorDescription = newErrorDescription;
		this.errorStatus = newErrorStatus;
	}

	public int getErrorCode() {
		return errorCode;
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}

	public int getErrorStatus() {
		return errorStatus;
	}
}
