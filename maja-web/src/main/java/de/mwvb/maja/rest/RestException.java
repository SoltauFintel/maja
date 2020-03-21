package de.mwvb.maja.rest;

public class RestException extends RestStatusException {
    private final ErrorMessage m;

    public RestException(ErrorMessage m, int status) {
        super(m.toString(), status);
        this.m = m;
    }

    public ErrorMessage getErrorMessage() {
        return m;
    }
}
