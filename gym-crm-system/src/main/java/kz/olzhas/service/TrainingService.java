package kz.olzhas.service;

import kz.olzhas.dao.TrainerDao;
import kz.olzhas.dao.TrainingDao;
import kz.olzhas.dao.TrainingTypeDao;
import kz.olzhas.exceptions.TrainerNotFoundException;
import kz.olzhas.exceptions.ValidationException;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;
import kz.olzhas.model.TrainingType;
import kz.olzhas.util.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingDao trainingDao;
    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final AuthenticationService authenticationService;

    public TrainingService(TrainingDao trainingDao, TrainerDao trainerDao,
            TrainingTypeDao trainingTypeDao, AuthenticationService authenticationService) {
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
        this.trainingTypeDao = trainingTypeDao;
        this.authenticationService = authenticationService;
    }

    public Training create(String traineeUsername, String password, String trainerUsername,
            String trainingName, String trainingType, LocalDate trainingDate, Integer duration) {
        Trainee trainee = authenticationService.authenticateTrainee(traineeUsername, password);
        String trainerName = Validator.requireText(trainerUsername, "Trainer username");
        String name = Validator.requireText(trainingName, "Training name");
        String typeName = Validator.requireText(trainingType, "Training type");
        LocalDate date = Validator.requireNotNull(trainingDate, "Training date");
        int minutes = Validator.requirePositive(duration, "Training duration");

        Trainer trainer = trainerDao.findByUsername(trainerName).orElseThrow(() -> {
            LOGGER.warn("Trainer {} does not exist", trainerName);

            return new TrainerNotFoundException("Trainer " + trainerName + " does not exist.");
        });
        TrainingType type = trainingTypeDao.findByName(typeName).orElseThrow(() -> {
            LOGGER.warn("Unknown training type: {}", typeName);

            return new ValidationException("Unknown training type: " + typeName);
        });

        Training training = new Training(null, trainee, trainer, name, type, date, minutes);
        Training created = trainingDao.create(training);
        LOGGER.info("Created training '{}' for trainee {} with trainer {}",
                name, traineeUsername, trainerName);

        return created;
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(String username, String password,
            LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType) {
        authenticationService.authenticateTrainee(username, password);
        List<Training> trainings = trainingDao.findTraineeTrainings(
                username, fromDate, toDate, trainerName, trainingType);
        LOGGER.debug("Found {} trainings for trainee {}", trainings.size(), username);

        return trainings;
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(String username, String password,
            LocalDate fromDate, LocalDate toDate, String traineeName) {
        authenticationService.authenticateTrainer(username, password);
        List<Training> trainings = trainingDao.findTrainerTrainings(
                username, fromDate, toDate, traineeName);
        LOGGER.debug("Found {} trainings for trainer {}", trainings.size(), username);

        return trainings;
    }
}
