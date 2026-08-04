package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;

public interface UpdatePersonUseCase {

    Person execute(Long id, Person person);
}
