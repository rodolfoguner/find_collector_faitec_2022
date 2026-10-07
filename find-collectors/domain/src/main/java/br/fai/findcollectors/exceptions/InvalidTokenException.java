package br.fai.findcollectors.exceptions;

public class InvalidTokenException extends UnauthorizedException {

    public InvalidTokenException(String message) {
        super(ErrorCode.INVALID_TOKEN, message);
    }
}
