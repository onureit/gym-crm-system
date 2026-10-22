package kz.olzhas.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kz.olzhas.dao.TrainerDao;
import kz.olzhas.exceptions.TrainerNotFoundException;
import kz.olzhas.model.Trainer;
import kz.olzhas.util.UserRegistry;

@Service
public class TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);

    private TrainerDao trainerDao;
    private UserRegistry userRegistry;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUserRegistry(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    public Trainer create(Trainer trainer) {
        trainer.setUsername(userRegistry.generateUserName(trainer));
        trainer.setPassword(userRegistry.generatePassword());

        Trainer created = trainerDao.create(trainer);
        LOGGER.info("Created trainer with ID: {}", created.getId());
        return created;
    }

    public boolean update(Trainer trainer) {
        LOGGER.debug("Updating trainer with ID: {}", trainer.getId());
        boolean updated = trainerDao.update(trainer);
        if (updated) {
            LOGGER.info("Updated trainer with ID: {}", trainer.getId());
        }
        return updated;
    }

    public Trainer findById(long id) {
        LOGGER.debug("Finding trainer with ID: {}", id);
        return trainerDao.findById(id).orElseThrow(() -> {
            LOGGER.warn("Trainer with ID {} does not exist", id);
            return new TrainerNotFoundException("Trainer with ID " + id + " does not exist.");
        });
    }

    public List<Trainer> findAll() {
        LOGGER.debug("Finding all trainers");
        return trainerDao.findAll();
    }
}
