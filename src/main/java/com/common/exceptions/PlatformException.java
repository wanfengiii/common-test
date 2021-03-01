package com.common.exceptions;

public class PlatformException extends RuntimeException {

    private static final long serialVersionUID = -8940053480099633377L;

    public PlatformException() {
        super();
    }

    public PlatformException(String message, Throwable cause) {
        super(message, cause);
    }

}