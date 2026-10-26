package kz.olzhas;

import kz.olzhas.config.AppConfig;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;
import kz.olzhas.service.TraineeService;
import kz.olzhas.service.TrainerService;
import kz.olzhas.service.TrainingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            TraineeService traineeService = context.getBean(TraineeService.class);
            TrainerService trainerService = context.getBean(TrainerService.class);
            TrainingService trainingService = context.getBean(TrainingService.class);

            Trainer trainer = trainerService.create("Jane", "Doe", "YOGA");
            Trainee trainee = traineeService.create(
                    "John", "Smith", LocalDate.of(1995, 4, 15), "10 Main Street");

            traineeService.updateTrainers(trainee.getUsername(), trainee.getPassword(),
                    List.of(trainer.getUsername()));
            trainingService.create(trainee.getUsername(), trainee.getPassword(),
                    trainer.getUsername(), "Morning yoga", "YOGA", LocalDate.now(), 60);

            List<Training> trainings = trainingService.getTraineeTrainings(
                    trainee.getUsername(), trainee.getPassword(), null, null, null, null);
            LOGGER.info("Trainee {} has {} trainings", trainee.getUsername(), trainings.size());
        }
    }
}
