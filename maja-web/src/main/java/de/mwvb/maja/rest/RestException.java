package de.mwvb.maja.rest;

public class RestException extends RuntimeException {
	private ErrorMessage m;
	
	public RestException(ErrorMessage m) {
		super(m.toString());
		this.m = m;
	}
	
	public ErrorMessage getErrorMessage() {
		return m;
	}
}
