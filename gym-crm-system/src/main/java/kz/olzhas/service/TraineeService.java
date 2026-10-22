package kz.olzhas.service;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.exceptions.TraineeNotFoundException;
import kz.olzhas.model.Trainee;
import kz.olzhas.util.UserRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDao traineeDao;
    private UserRegistry userRegistry;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUserRegistry(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    public Trainee create(Trainee trainee) {
        trainee.assignCredentials(
                userRegistry.generateUsername(trainee.getFirstName(), trainee.getLastName()),
                userRegistry.generatePassword());
        Trainee created = traineeDao.create(trainee);
        LOGGER.info("Created trainee with ID: {}", created.getId());
        return created;
    }

    public boolean update(Trainee trainee) {
        LOGGER.debug("Updating trainee with ID: {}", trainee.getId());
        Trainee stored = findById(trainee.getId());
        stored.updateProfile(trainee.getFirstName(), trainee.getLastName(),
                trainee.getDateOfBirth(), trainee.getAddress());
        boolean updated = traineeDao.update(stored);
        if (updated) {
            LOGGER.info("Updated trainee with ID: {}", trainee.getId());
        }
        return updated;
    }

    public void delete(long id) {
        LOGGER.debug("Deleting trainee with ID: {}", id);
        traineeDao.delete(id);
        LOGGER.info("Deleted trainee with ID: {}", id);
    }

    public Trainee findById(long id) {
        LOGGER.debug("Finding trainee with ID: {}", id);
        return traineeDao.findById(id).orElseThrow(
            () -> {
                LOGGER.warn("Trainee with ID {} does not exist", id);
                return new TraineeNotFoundException("Trainee with ID " + id + " does not exist.");
            });
    }

    public List<Trainee> findAll() {
        LOGGER.debug("Finding all trainees");
        return traineeDao.findAll();
    }
}
