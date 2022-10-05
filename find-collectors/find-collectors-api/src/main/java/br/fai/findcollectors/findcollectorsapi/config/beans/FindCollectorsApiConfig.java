package br.fai.findcollectors.findcollectorsapi.config.beans;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.entities.Recycler;
import br.fai.findcollectors.findcollectorsdatabase.dao.*;
import br.fai.findcollectors.findcollectorsdatabase.dao.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FindCollectorsApiConfig {

    @Bean
    public CollectDao<Collect> getCollectDao() {
        return new CollectDaoImpl();
    }

    @Bean
    public PersonDao<Person> getPersonDao() {
        return new PersonDaoImpl();
    }

    @Bean
    RecyclerDao<Recycler> getRecyclerDao() {
        return new RecyclerDaoImpl();
    }

    @Bean
    public StateDao getStateDao() {
        return new StateDaoImpl();
    }

    @Bean
    public CityDao getCityDao() {
        return new CityDaoImpl();
    }

}
