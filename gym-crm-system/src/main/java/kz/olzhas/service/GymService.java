package kz.olzhas.service;

import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;

import org.springframework.stereotype.Service;

import java.util.List;

/** Application facade: all service dependencies use constructor injection. */
@Service
public class GymService {
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
        return traineeService.create(trainee);
    }

    public boolean updateTrainee(Trainee trainee) {
        return traineeService.update(trainee);
    }

    public boolean deleteTrainee(long id) {
        return traineeService.delete(id);
    }

    public Trainee selectTrainee(long id) {
        return traineeService.findById(id);
    }

    public List<Trainee> listTrainees() {
        return traineeService.findAll();
    }

    public Trainer createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public boolean updateTrainer(Trainer trainer) {
        return trainerService.update(trainer);
    }

    public Trainer selectTrainer(long id) {
        return trainerService.findById(id);
    }

    public List<Trainer> listTrainers() {
        return trainerService.findAll();
    }

    public Training createTraining(Training training) {
        return trainingService.create(training);
    }

    public Training selectTraining(long id) {
        return trainingService.findById(id);
    }

    public List<Training> listTrainings() {
        return trainingService.findAll();
    }
}
