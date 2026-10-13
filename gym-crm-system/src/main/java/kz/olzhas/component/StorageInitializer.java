package kz.olzhas.component;

import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;
import kz.olzhas.model.TrainingType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Properties;
import java.util.logging.Logger;

public class StorageInitializer implements BeanPostProcessor, ResourceLoaderAware {
    private static final Logger LOGGER = Logger.getLogger(StorageInitializer.class.getName());

    private String dataLocation;
    private ResourceLoader resourceLoader;

    @Value("${gym.storage.data.path}")
    public void setDataLocation(String dataLocation) {
        this.dataLocation = dataLocation;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof InMemoryStorage storage) {
            initialize(storage);
        }
        return bean;
    }

    public void initialize(InMemoryStorage storage) {
        Properties data = new Properties();
        try (Reader reader =
                new InputStreamReader(
                        resourceLoader.getResource(dataLocation).getInputStream(),
                        StandardCharsets.UTF_8)) {
            data.load(reader);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Cannot read storage data from " + dataLocation, exception);
        }

        int traineeCount = Integer.parseInt(data.getProperty("trainees.count", "0"));
        for (int index = 1; index <= traineeCount; index++) {
            String prefix = "trainee." + index + ".";
            Trainee trainee = new Trainee();
            trainee.setId(Long.parseLong(data.getProperty(prefix + "id")));
            trainee.setFirstName(data.getProperty(prefix + "firstName"));
            trainee.setLastName(data.getProperty(prefix + "lastName"));
            trainee.setUsername(data.getProperty(prefix + "username"));
            trainee.setPassword(data.getProperty(prefix + "password"));
            trainee.setActive(Boolean.parseBoolean(data.getProperty(prefix + "active")));
            String dateOfBirth = data.getProperty(prefix + "dateOfBirth");
            if (dateOfBirth != null && !dateOfBirth.isBlank()) {
                trainee.setDateOfBirth(LocalDate.parse(dateOfBirth));
            }
            trainee.setAddress(data.getProperty(prefix + "address"));
            storage.getTrainees().put(trainee.getId(), trainee);
        }

        int trainerCount = Integer.parseInt(data.getProperty("trainers.count", "0"));
        for (int index = 1; index <= trainerCount; index++) {
            String prefix = "trainer." + index + ".";
            Trainer trainer = new Trainer();
            trainer.setId(Long.parseLong(data.getProperty(prefix + "id")));
            trainer.setFirstName(data.getProperty(prefix + "firstName"));
            trainer.setLastName(data.getProperty(prefix + "lastName"));
            trainer.setUsername(data.getProperty(prefix + "username"));
            trainer.setPassword(data.getProperty(prefix + "password"));
            trainer.setActive(Boolean.parseBoolean(data.getProperty(prefix + "active")));
            trainer.setSpecialization(
                    new TrainingType(data.getProperty(prefix + "specialization")));
            storage.getTrainers().put(trainer.getId(), trainer);
        }

        int trainingCount = Integer.parseInt(data.getProperty("trainings.count", "0"));
        for (int index = 1; index <= trainingCount; index++) {
            String prefix = "training." + index + ".";
            Training training = new Training();
            training.setId(Long.parseLong(data.getProperty(prefix + "id")));
            training.setTraineeId(Long.parseLong(data.getProperty(prefix + "traineeId")));
            training.setTrainerId(Long.parseLong(data.getProperty(prefix + "trainerId")));
            training.setTrainingName(data.getProperty(prefix + "name"));
            training.setTrainingType(new TrainingType(data.getProperty(prefix + "type")));
            training.setTrainingDate(LocalDate.parse(data.getProperty(prefix + "date")));
            training.setTrainingDuration(
                    Duration.ofMinutes(
                            Long.parseLong(data.getProperty(prefix + "durationMinutes"))));
            storage.getTrainings().put(training.getId(), training);
        }

        LOGGER.info(
                "Loaded "
                        + traineeCount
                        + " trainees, "
                        + trainerCount
                        + " trainers, "
                        + trainingCount
                        + " trainings");
    }
}
