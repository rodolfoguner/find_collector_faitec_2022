package br.fai.findcollectors.exceptions;

import java.util.Objects;

public abstract class BusinessException extends RuntimeException {

    private final ErrorCode code;

    protected BusinessException(ErrorCode code, String message) {
        super(message);
        this.code = Objects.requireNonNull(code, "code must not be null");
    }

    public ErrorCode getCode() {
        return code;
    }
}
