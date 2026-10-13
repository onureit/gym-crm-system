package kz.olzhas.dao;

import kz.olzhas.model.Trainee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Repository
public class TraineeDao {
    private static final Logger LOGGER = Logger.getLogger(TraineeDao.class.getName());

    private Map<Long, Trainee> storage;
    private long lastId;

    @Autowired
    public void setStorage(@Qualifier("traineeStorage") Map<Long, Trainee> storage) {
        this.storage = storage;
    }

    public Trainee create(Trainee trainee) {
        for (Long id : storage.keySet()) {
            if (id > lastId) {
                lastId = id;
            }
        }
        trainee.setId(++lastId);
        storage.put(trainee.getId(), trainee);
        LOGGER.info("Created trainee " + trainee.getId());
        return trainee;
    }

    public boolean update(Trainee trainee) {
        if (!storage.containsKey(trainee.getId())) {
            return false;
        }
        storage.put(trainee.getId(), trainee);
        LOGGER.info("Updated trainee " + trainee.getId());
        return true;
    }

    public boolean delete(long id) {
        boolean deleted = storage.remove(id) != null;
        if (deleted) {
            LOGGER.info("Deleted trainee " + id);
        }
        return deleted;
    }

    public Trainee findById(long id) {
        return storage.get(id);
    }

    public List<Trainee> findAll() {
        return new ArrayList<>(storage.values());
    }
}
