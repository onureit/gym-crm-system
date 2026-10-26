package kz.olzhas.service;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.exceptions.AuthenticationException;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public AuthenticationService(TraineeDao traineeDao, TrainerDao trainerDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    public boolean matchTrainee(String username, String password) {
        return traineeDao.findByUsername(username)
                .map(trainee -> trainee.getPassword().equals(password))
                .orElse(false);
    }

    public boolean matchTrainer(String username, String password) {
        return trainerDao.findByUsername(username)
                .map(trainer -> trainer.getPassword().equals(password))
                .orElse(false);
    }

    public Trainee authenticateTrainee(String username, String password) {
        Trainee trainee = traineeDao.findByUsername(username)
                .filter(found -> found.getPassword().equals(password))
                .orElseThrow(() -> fail(username));
        LOGGER.debug("Trainee {} authenticated", username);

        return trainee;
    }

    public Trainer authenticateTrainer(String username, String password) {
        Trainer trainer = trainerDao.findByUsername(username)
                .filter(found -> found.getPassword().equals(password))
                .orElseThrow(() -> fail(username));
        LOGGER.debug("Trainer {} authenticated", username);

        return trainer;
    }

    private AuthenticationException fail(String username) {
        LOGGER.warn("Authentication failed for username: {}", username);

        return new AuthenticationException("Invalid username or password.");
    }
}
