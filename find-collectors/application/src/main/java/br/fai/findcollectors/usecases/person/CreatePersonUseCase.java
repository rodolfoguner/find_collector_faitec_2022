package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;

public interface CreatePersonUseCase {
    
    Person execute(Person person);
}
