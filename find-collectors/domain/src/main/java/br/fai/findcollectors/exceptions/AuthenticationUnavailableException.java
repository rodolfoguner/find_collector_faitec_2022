package br.fai.findcollectors.exceptions;

public class AuthenticationUnavailableException extends RuntimeException {
    public AuthenticationUnavailableException(Throwable cause) {
        super("Authentication is temporarily unavailable", cause);
    }
}
