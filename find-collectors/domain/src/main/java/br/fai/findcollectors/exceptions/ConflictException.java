package br.fai.findcollectors.exceptions;

public class ConflictException extends BusinessException {

    public ConflictException(ErrorCode code, String message) {
        super(code, message);
    }
}
