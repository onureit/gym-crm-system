package kz.olzhas.component;

import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

/** One context-owned storage, with a separate map bean for each entity namespace. */
@Component
public class InMemoryStorage {
    private Map<Long, Trainee> trainees;
    private Map<Long, Trainer> trainers;
    private Map<Long, Training> trainings;

    @Autowired
    public void setTrainees(@Qualifier("traineeStorage") Map<Long, Trainee> trainees) {
        this.trainees = trainees;
    }

    @Autowired
    public void setTrainers(@Qualifier("trainerStorage") Map<Long, Trainer> trainers) {
        this.trainers = trainers;
    }

    @Autowired
    public void setTrainings(@Qualifier("trainingStorage") Map<Long, Training> trainings) {
        this.trainings = trainings;
    }

    public Map<Long, Trainee> getTrainees() {
        return trainees;
    }

    public Map<Long, Trainer> getTrainers() {
        return trainers;
    }

    public Map<Long, Training> getTrainings() {
        return trainings;
    }
}
