package br.fai.findcollectors.findcollectorsapi.config.beans;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.repositories.CityRepository;
import br.fai.findcollectors.repositories.CollectRepository;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.findcollectorsdatabase.dao.impl.CityDaoImpl;
import br.fai.findcollectors.findcollectorsdatabase.dao.impl.CollectDaoImpl;
import br.fai.findcollectors.findcollectorsdatabase.dao.impl.PersonDaoImpl;
import br.fai.findcollectors.findcollectorsdatabase.dao.impl.StateDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FindCollectorsApiConfig {

    @Bean
    public CollectRepository<Collect> getCollectDao() {
        return new CollectDaoImpl();
    }

    @Bean
    public PersonRepository<Person> getPersonDao() {
        return new PersonDaoImpl();
    }

    @Bean
    public br.fai.findcollectors.repositories.StateRepository getStateDao() {
        return new StateDaoImpl();
    }

    @Bean
    public CityRepository getCityDao() {
        return new CityDaoImpl();
    }

}
