package kz.olzhas.dao;

import kz.olzhas.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingDao {
    Training create(Training training);

    List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate,
            LocalDate toDate, String trainerName, String trainingType);

    List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate,
            LocalDate toDate, String traineeName);
}
