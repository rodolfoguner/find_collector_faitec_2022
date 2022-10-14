package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsapi.service.CollectRestService;
import br.fai.findcollectors.findcollectorsapi.service.PersonRestService;
import br.fai.findcollectors.findcollectorsdatabase.dao.CollectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectRestServiceImpl implements CollectRestService<Collect> {

    @Autowired
    CollectDao<Collect> collectDao;

    @Autowired
    PersonRestService<Person> personRestService;

    @Override
    public List<Collect> find() {
        return collectDao.find();
    }

    @Override
    public Collect findById(int id) {
        if (id <= 0) {
            return null;
        }

        return collectDao.findById(id);
    }

    @Override
    public int create(Collect entity) {

        if (entity == null) {
            return -1;
        }

        Person recycler = personRestService.findById(entity.getRecyclerId());

        if (recycler == null) {
            return -1;
        }

        return collectDao.create(entity);
    }

    @Override
    public boolean update(int id, Collect entity) {
        return collectDao.update(entity);
    }

    @Override
    public boolean deleteById(int id) {

        if (id <= 0) {
            return false;
        }

        return collectDao.deleteById(id);
    }

    @Override
    public boolean acceptCollect(int id, Collect entity) {

        if (id <= 0) {
            return false;
        }


        return collectDao.acceptCollect(entity);
    }

    @Override
    public boolean closeCollect(int id, Collect entity) {

        if (id <= 0) {
            return false;
        }


        return collectDao.closeCollect(entity);
    }
}
