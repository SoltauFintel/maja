package de.mwvb.maja.rest;

public class ErrorMessage {
	/** For identifying the JSON as an error message. */
	private final String error = "1";
	private final String message;
	private final String errorClass;
	
	public ErrorMessage(Exception e) {
		message = e.getMessage();
		errorClass = e.getClass().getSimpleName();
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getErrorClass() {
		return errorClass;
	}
	
	public String getClassMessage() {
		if (message == null || message.isEmpty()) {
			return errorClass;
		} else {
			return errorClass + ": " + message;
		}
	}
	
	@Override
	public String toString() {
		if (message == null || message.isEmpty()) {
			return errorClass;
		} else {
			return message;
		}
	}
}
