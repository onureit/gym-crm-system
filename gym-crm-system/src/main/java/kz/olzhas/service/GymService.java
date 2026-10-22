package kz.olzhas.service;

import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/** Application facade: all service dependencies use constructor injection. */
@Service
public class GymService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GymService.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymService(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee createTrainee(Trainee trainee) {
        LOGGER.debug("Request to create trainee");
        return traineeService.create(trainee);
    }

    public boolean updateTrainee(Trainee trainee) {
        LOGGER.debug("Request to update trainee with ID: {}", trainee.getId());
        return traineeService.update(trainee);
    }

    public void deleteTrainee(long id) {
        LOGGER.debug("Request to delete trainee with ID: {}", id);
        traineeService.delete(id);
    }

    public Trainee selectTrainee(long id) {
        LOGGER.debug("Request to select trainee with ID: {}", id);
        return traineeService.findById(id);
    }

    public List<Trainee> listTrainees() {
        LOGGER.debug("Request to list all trainees");
        return traineeService.findAll();
    }

    public Trainer createTrainer(Trainer trainer) {
        LOGGER.debug("Request to create trainer");
        return trainerService.create(trainer);
    }

    public boolean updateTrainer(Trainer trainer) {
        LOGGER.debug("Request to update trainer with ID: {}", trainer.getId());
        return trainerService.update(trainer);
    }

    public Trainer selectTrainer(long id) {
        LOGGER.debug("Request to select trainer with ID: {}", id);
        return trainerService.findById(id);
    }

    public List<Trainer> listTrainers() {
        LOGGER.debug("Request to list all trainers");
        return trainerService.findAll();
    }

    public Training createTraining(Training training) {
        LOGGER.debug("Request to create training");
        return trainingService.create(training);
    }

    public Training selectTraining(long id) {
        LOGGER.debug("Request to select training with ID: {}", id);
        return trainingService.findById(id);
    }

    public List<Training> listTrainings() {
        LOGGER.debug("Request to list all trainings");
        return trainingService.findAll();
    }
}
