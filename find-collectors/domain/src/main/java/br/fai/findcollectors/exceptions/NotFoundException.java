package br.fai.findcollectors.exceptions;

public class NotFoundException extends BusinessException {

    public NotFoundException(ErrorCode code, String message) {
        super(code, message);
    }
}
