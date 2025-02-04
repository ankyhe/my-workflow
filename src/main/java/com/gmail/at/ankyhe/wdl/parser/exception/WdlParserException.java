package com.gmail.at.ankyhe.wdl.parser.exception;

import java.io.Serial;

public class WdlParserException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8833791819287516921L;

    public WdlParserException() {}

    public WdlParserException(final String message) {
        super(message);
    }

    public WdlParserException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WdlParserException(final Throwable cause) {
        super(cause);
    }

    public WdlParserException(
        final String message,
        final Throwable cause,
        final boolean enableSuppression,
        final boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
