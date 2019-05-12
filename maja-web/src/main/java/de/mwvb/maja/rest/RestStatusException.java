package de.mwvb.maja.rest;

public class RestStatusException extends RuntimeException {
	private final int status;
	
	public RestStatusException(int status) {
		super("Status is " + status);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
