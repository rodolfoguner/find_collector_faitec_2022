package br.fai.findcollectors.exceptions;

public class UnprocessableEntityException extends BusinessException {

    public UnprocessableEntityException(ErrorCode code, String message) {
        super(code, message);
    }
}
