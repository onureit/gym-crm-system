package kz.olzhas.service;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.dao.TrainingDao;
import kz.olzhas.exceptions.TrainingNotFoundException;
import kz.olzhas.model.Training;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao trainingDao;
    private TrainerDao trainerDao;
    private TraineeDao traineeDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    public Training create(Training training) {
        Long traineeId = training.getTraineeId();
        Long trainerId = training.getTrainerId();

        if (traineeId != null && traineeDao.findById(traineeId).isEmpty()) {
            LOGGER.warn("Trainee with ID {} does not exist", traineeId);
            throw new IllegalArgumentException("Trainee with ID " + traineeId + " does not exist.");
        }

        if (trainerId != null && trainerDao.findById(trainerId).isEmpty()) {
            LOGGER.warn("Trainer with ID {} does not exist", trainerId);
            throw new IllegalArgumentException("Trainer with ID " + trainerId + " does not exist.");
        }

        Training created = trainingDao.create(training);
        LOGGER.info("Created training with ID: {}", created.getId());
        return created;
    }

    public Training findById(long id) {
        LOGGER.debug("Finding training with ID: {}", id);
        return trainingDao.findById(id).orElseThrow(() -> {
            LOGGER.warn("Training with ID {} does not exist", id);
            return new TrainingNotFoundException("Training with ID " + id + " does not exist.");
        });
    }

    public List<Training> findAll() {
        LOGGER.debug("Finding all trainings");
        return trainingDao.findAll();
    }
}
