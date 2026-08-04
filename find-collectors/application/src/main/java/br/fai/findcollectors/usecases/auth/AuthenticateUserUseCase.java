package br.fai.findcollectors.usecases.auth;

import br.fai.findcollectors.entities.Person;

public interface AuthenticateUserUseCase {

    Person execute(String email, String password);
}
