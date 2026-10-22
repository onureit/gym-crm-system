package kz.olzhas.dao.impl;

import kz.olzhas.dao.TrainerDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import kz.olzhas.exceptions.TrainerNotFoundException;
import kz.olzhas.model.Trainer;

@Repository
public class InMemoryTrainerDao implements TrainerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryTrainerDao.class);

    private Map<Long, Trainer> storage;
    private long lastId;

    @Autowired
    public void setStorage(@Qualifier("trainerStorage") Map<Long, Trainer> storage) {
        this.storage = storage;
    }

    @Override
    public Trainer create(Trainer trainer) {
        for (Long id : storage.keySet()) {
            if (id > lastId) {
                lastId = id;
            }
        }
        trainer.setId(++lastId);
        storage.put(trainer.getId(), trainer);
        LOGGER.debug("Stored trainer with ID: {}", trainer.getId());

        return trainer;
    }

    @Override
    public boolean update(Trainer trainer) {
        if (!storage.containsKey(trainer.getId())) {
            LOGGER.warn("Cannot update trainer: ID {} does not exist", trainer.getId());
            throw new TrainerNotFoundException("Trainer with ID " + trainer.getId() + " does not exist.");
        }
        storage.put(trainer.getId(), trainer);
        LOGGER.debug("Updated stored trainer with ID: {}", trainer.getId());

        return true;
    }

    @Override
    public Optional<Trainer> findById(long id) {
        LOGGER.debug("Looking up trainer with ID: {}", id);
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        LOGGER.debug("Retrieving all trainers; count: {}", storage.size());
        return new ArrayList<>(storage.values());
    }
}
