package br.fai.findcollectors.exceptions;

public class BusinessRuleException extends UnprocessableEntityException {

    public BusinessRuleException(String message) {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
