package kz.olzhas.dao;

import kz.olzhas.model.Trainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Repository
public class TrainerDao {
    private static final Logger LOGGER = Logger.getLogger(TrainerDao.class.getName());

    private Map<Long, Trainer> storage;
    private long lastId;

    @Autowired
    public void setStorage(@Qualifier("trainerStorage") Map<Long, Trainer> storage) {
        this.storage = storage;
    }

    public Trainer create(Trainer trainer) {
        for (Long id : storage.keySet()) {
            if (id > lastId) {
                lastId = id;
            }
        }
        trainer.setId(++lastId);
        storage.put(trainer.getId(), trainer);
        LOGGER.info("Created trainer " + trainer.getId());
        return trainer;
    }

    public boolean update(Trainer trainer) {
        if (!storage.containsKey(trainer.getId())) {
            return false;
        }
        storage.put(trainer.getId(), trainer);
        LOGGER.info("Updated trainer " + trainer.getId());
        return true;
    }

    public Trainer findById(long id) {
        return storage.get(id);
    }

    public List<Trainer> findAll() {
        return new ArrayList<>(storage.values());
    }
}
