package br.fai.findcollectors.service.impl;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.service.CollectRestService;
import br.fai.findcollectors.service.PersonRestService;
import br.fai.findcollectors.repositories.CollectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectRestServiceImpl implements CollectRestService<Collect> {

    @Autowired
    CollectRepository collectDao;

    @Autowired
    PersonRestService<Person> personRestService;

    @Override
    public List<Collect> find() {
        return collectDao.find();
    }

    @Override
    public Collect findById(int id) {
//        if (id <= 0) {
//            return null;
//        }
//
//        return collectDao.findById(id);
        return null;
    }

    @Override
    public int create(Collect entity) {

        if (entity == null) {
            return -1;
        }

//        Person recycler = personRestService.findById(entity.getRecyclerId());
//
//        if (recycler == null) {
//            return -1;
//        }

//        return collectDao.create(entity);
        return -1;
    }

    @Override
    public boolean update(int id, Collect entity) {
        return false;
//        return collectDao.update(entity);
    }

    @Override
    public boolean deleteById(int id) {

        if (id <= 0) {
            return false;
        }

//        return collectDao.deleteById(id);
        return false;
    }

    @Override
    public boolean acceptCollect(int id, Collect entity) {
//
//        if (id <= 0) {
//            return false;
//        }
//
//
//        return collectDao.acceptCollect(entity);
        return false;
    }

    @Override
    public boolean closeCollect(int id, Collect entity) {

//        if (id <= 0) {
//            return false;
//        }
//
//
//        return collectDao.closeCollect(entity);
        return false;
    }

    @Override
    public List<Collect> findFreeCollects(int id) {
        return collectDao.findPendingCollects((long)id);
    }

    @Override
    public List<Collect> myCollects(int id) {

//        List<Collect> myCollects = collectDao.MyCollects(id);
//
//        if (myCollects.isEmpty()) {
//            return null;
//        }
//
//        return myCollects;
        return null;
    }

    @Override
    public List<Collect> acceptedCollects(int id) {

        if (id <= 0) {
            return null;
        }

//        return collectDao.acceptedCollects(id);
        return null;
    }


}