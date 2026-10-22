package kz.olzhas.dao.impl;

import kz.olzhas.dao.TrainingDao;

import kz.olzhas.model.Training;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryTrainingDao implements TrainingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryTrainingDao.class);

    private Map<Long, Training> storage;
    private long lastId;

    @Autowired
    public void setStorage(@Qualifier("trainingStorage") Map<Long, Training> storage) {
        this.storage = storage;
    }

    @Override
    public Training create(Training training) {
        for (Long id : storage.keySet()) {
            if (id > lastId) {
                lastId = id;
            }
        }
        training.setId(++lastId);
        storage.put(training.getId(), training);
        LOGGER.debug("Stored training with ID: {}", training.getId());

        return training;
    }

    @Override
    public Optional<Training> findById(long id) {
        LOGGER.debug("Looking up training with ID: {}", id);
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Training> findAll() {
        LOGGER.debug("Retrieving all trainings; count: {}", storage.size());
        return new ArrayList<>(storage.values());
    }
}
