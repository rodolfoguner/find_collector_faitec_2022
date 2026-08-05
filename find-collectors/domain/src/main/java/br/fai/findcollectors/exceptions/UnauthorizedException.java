package br.fai.findcollectors.exceptions;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(ErrorCode code, String message) {
        super(code, message);
    }
}
