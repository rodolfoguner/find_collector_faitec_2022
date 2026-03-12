package br.fai.findcollectors.findcollectorsapi.config.beans;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsdatabase.dao.CityDao;
import br.fai.findcollectors.findcollectorsdatabase.dao.CollectDao;
import br.fai.findcollectors.findcollectorsdatabase.dao.PersonDao;
import br.fai.findcollectors.findcollectorsdatabase.dao.StateDao;
import br.fai.findcollectors.findcollectorsdatabase.dao.impl.CityDaoImpl;
import br.fai.findcollectors.findcollectorsdatabase.dao.impl.CollectDaoImpl;
import br.fai.findcollectors.findcollectorsdatabase.dao.impl.PersonDaoImpl;
import br.fai.findcollectors.findcollectorsdatabase.dao.impl.StateDaoImpl;
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
    public StateDao getStateDao() {
        return new StateDaoImpl();
    }

    @Bean
    public CityDao getCityDao() {
        return new CityDaoImpl();
    }

}
