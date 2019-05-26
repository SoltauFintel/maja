package de.mwvb.maja.rest;

public class RestStatusException extends RuntimeException {
	private final int status;
	
	public RestStatusException(int status) {
		this("Status is " + status, status);
	}
	
	public RestStatusException(String msg, int status) {
	    super(msg);
	    this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
