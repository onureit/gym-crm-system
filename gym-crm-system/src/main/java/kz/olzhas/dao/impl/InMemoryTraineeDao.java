package kz.olzhas.dao.impl;

import kz.olzhas.dao.TraineeDao;

import kz.olzhas.exceptions.TraineeNotFoundException;
import kz.olzhas.model.Trainee;

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
public class InMemoryTraineeDao implements TraineeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryTraineeDao.class);

    private Map<Long, Trainee> storage;
    private long lastId;

    @Autowired
    public void setStorage(@Qualifier("traineeStorage") Map<Long, Trainee> storage) {
        this.storage = storage;
    }

    @Override
    public Trainee create(Trainee trainee) {
        for (Long id : storage.keySet()) {
            if (id > lastId) {
                lastId = id;
            }
        }
        trainee.setId(++lastId);
        storage.put(trainee.getId(), trainee);
        LOGGER.debug("Stored trainee with ID: {}", trainee.getId());

        return trainee;
    }

    @Override
    public boolean update(Trainee trainee) {
        if (!storage.containsKey(trainee.getId())) {
            LOGGER.warn("Cannot update trainee: ID {} does not exist", trainee.getId());
            throw new TraineeNotFoundException("Trainee with ID " + trainee.getId() + " does not exist.");
        }

        storage.put(trainee.getId(), trainee);
        LOGGER.debug("Updated stored trainee with ID: {}", trainee.getId());

        return true;
    }

    @Override
    public void delete(long id) {
        if (storage.remove(id) != null) {
            LOGGER.debug("Removed trainee with ID: {}", id);
        } else {
            LOGGER.warn("Cannot delete trainee: ID {} does not exist", id);
            throw new TraineeNotFoundException("Trainee with ID " + id + " does not exist.");
        }
    }

    @Override
    public Optional<Trainee> findById(long id) {
        LOGGER.debug("Looking up trainee with ID: {}", id);
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainee> findAll() {
        LOGGER.debug("Retrieving all trainees; count: {}", storage.size());
        return new ArrayList<>(storage.values());
    }
}
