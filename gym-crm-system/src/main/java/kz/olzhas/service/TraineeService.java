package kz.olzhas.service;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.exceptions.TrainerNotFoundException;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.util.UserRegistry;
import kz.olzhas.util.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final AuthenticationService authenticationService;
    private final UserRegistry userRegistry;

    public TraineeService(TraineeDao traineeDao, TrainerDao trainerDao,
            AuthenticationService authenticationService, UserRegistry userRegistry) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.authenticationService = authenticationService;
        this.userRegistry = userRegistry;
    }

    public Trainee create(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        String first = Validator.requireText(firstName, "First name");
        String last = Validator.requireText(lastName, "Last name");

        Trainee trainee = new Trainee(first, last, true, dateOfBirth, address);
        trainee.assignCredentials(
                userRegistry.generateUsername(first, last), userRegistry.generatePassword());
        Trainee created = traineeDao.create(trainee);
        LOGGER.info("Created trainee with username: {}", created.getUsername());

        return created;
    }

    @Transactional(readOnly = true)
    public boolean matchCredentials(String username, String password) {
        return authenticationService.matchTrainee(username, password);
    }

    @Transactional(readOnly = true)
    public Trainee findByUsername(String username, String password) {
        return authenticationService.authenticateTrainee(username, password);
    }

    public void changePassword(String username, String password, String newPassword) {
        Trainee trainee = authenticationService.authenticateTrainee(username, password);
        trainee.changePassword(Validator.requireText(newPassword, "New password"));
        traineeDao.update(trainee);
        LOGGER.info("Changed password of trainee: {}", username);
    }

    public Trainee update(String username, String password, String firstName, String lastName,
            LocalDate dateOfBirth, String address) {
        Trainee trainee = authenticationService.authenticateTrainee(username, password);
        trainee.updateProfile(Validator.requireText(firstName, "First name"),
                Validator.requireText(lastName, "Last name"), dateOfBirth, address);
        Trainee updated = traineeDao.update(trainee);
        LOGGER.info("Updated trainee: {}", username);

        return updated;
    }

    public boolean toggleActive(String username, String password) {
        Trainee trainee = authenticationService.authenticateTrainee(username, password);
        trainee.toggleActive();
        traineeDao.update(trainee);
        LOGGER.info("Trainee {} is now {}", username, trainee.isActive() ? "active" : "inactive");

        return trainee.isActive();
    }

    public void delete(String username, String password) {
        Trainee trainee = authenticationService.authenticateTrainee(username, password);
        traineeDao.delete(trainee);
        LOGGER.info("Deleted trainee: {}", username);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String username, String password) {
        authenticationService.authenticateTrainee(username, password);

        return trainerDao.findNotAssignedToTrainee(username);
    }

    public List<Trainer> updateTrainers(String username, String password, List<String> trainerUsernames) {
        Trainee trainee = authenticationService.authenticateTrainee(username, password);
        Validator.requireNotNull(trainerUsernames, "Trainer list");

        Set<Trainer> trainers = new HashSet<>();
        for (String trainerUsername : trainerUsernames) {
            trainers.add(trainerDao.findByUsername(trainerUsername).orElseThrow(() -> {
                LOGGER.warn("Trainer {} does not exist", trainerUsername);

                return new TrainerNotFoundException(
                        "Trainer " + trainerUsername + " does not exist.");
            }));
        }
        trainee.replaceTrainers(trainers);
        traineeDao.update(trainee);
        LOGGER.info("Updated trainers of trainee {}: {}", username, trainerUsernames);

        return new ArrayList<>(trainee.getTrainers());
    }
}
