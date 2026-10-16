package kz.olzhas.dao;

import kz.olzhas.model.Training;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Repository
public class TrainingDao {
    private static final Logger LOGGER = Logger.getLogger(TrainingDao.class.getName());

    private Map<Long, Training> storage;
    private long lastId;

    @Autowired
    public void setStorage(@Qualifier("trainingStorage") Map<Long, Training> storage) {
        this.storage = storage;
    }

    public Training create(Training training) {
        for (Long id : storage.keySet()) {
            if (id > lastId) {
                lastId = id;
            }
        }
        training.setId(++lastId);
        storage.put(training.getId(), training);
        LOGGER.info("Created training " + training.getId());
        return training;
    }

    public Training findById(long id) {
        return storage.get(id);
    }

    public List<Training> findAll() {
        return new ArrayList<>(storage.values());
    }
}
