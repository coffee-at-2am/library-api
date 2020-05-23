package com.transistorwebservices.libraryapi.exception;

public class LibraryResourceUnauthorizedException extends Exception {

    private String traceId;

    public LibraryResourceUnauthorizedException(String traceId, String message) {
        super(message);
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }
}
