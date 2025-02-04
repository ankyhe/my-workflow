package com.gmail.at.ankyhe.my.workflow.exception;

public class WdlInvokingException extends RuntimeException {

    public WdlInvokingException() {}

    public WdlInvokingException(String message) {
        super(message);
    }

    public WdlInvokingException(String message, Throwable cause) {
        super(message, cause);
    }

    public WdlInvokingException(Throwable cause) {
        super(cause);
    }

    public WdlInvokingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
