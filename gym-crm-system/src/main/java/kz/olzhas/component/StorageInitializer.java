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
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class StorageInitializer implements BeanPostProcessor, ResourceLoaderAware {
    private static final Logger LOGGER = Logger.getLogger(StorageInitializer.class.getName());

    private String dataLocation;
    private ResourceLoader resourceLoader;
    private Properties seedData;

    @Value("${gym.storage.data.path}")
    public void setDataLocation(String dataLocation) {
        this.dataLocation = dataLocation;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (!(bean instanceof Map<?, ?>)) {
            return bean;
        }

        switch (beanName) {
            case "traineeStorage" -> initializeTrainees((Map<Long, Trainee>) bean);
            case "trainerStorage" -> initializeTrainers((Map<Long, Trainer>) bean);
            case "trainingStorage" -> initializeTrainings((Map<Long, Training>) bean);
        }

        return bean;
    }

    private Properties loadSeedData() {
        if (seedData != null) {
            return seedData;
        }
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
        seedData = data;
        return seedData;
    }

    private void initializeTrainees(Map<Long, Trainee> storage) {
        Properties data = loadSeedData();
        int traineeCount = Integer.parseInt(data.getProperty("trainees.count", "0"));
        for (int index = 1; index <= traineeCount; index++) {
            String prefix = "trainee." + index + ".";
            String dateOfBirth = data.getProperty(prefix + "dateOfBirth");
            Trainee trainee = new Trainee(
                    Long.parseLong(data.getProperty(prefix + "id")),
                    data.getProperty(prefix + "firstName"),
                    data.getProperty(prefix + "lastName"),
                    data.getProperty(prefix + "username"),
                    data.getProperty(prefix + "password"),
                    Boolean.parseBoolean(data.getProperty(prefix + "active")),
                    dateOfBirth == null || dateOfBirth.isBlank() ? null : LocalDate.parse(dateOfBirth),
                    data.getProperty(prefix + "address"));
            storage.put(trainee.getId(), trainee);
        }
        LOGGER.info("Loaded " + traineeCount + " trainees");
    }

    private void initializeTrainers(Map<Long, Trainer> storage) {
        Properties data = loadSeedData();
        int trainerCount = Integer.parseInt(data.getProperty("trainers.count", "0"));
        for (int index = 1; index <= trainerCount; index++) {
            String prefix = "trainer." + index + ".";
            Trainer trainer = new Trainer(
                    Long.parseLong(data.getProperty(prefix + "id")),
                    data.getProperty(prefix + "firstName"),
                    data.getProperty(prefix + "lastName"),
                    data.getProperty(prefix + "username"),
                    data.getProperty(prefix + "password"),
                    Boolean.parseBoolean(data.getProperty(prefix + "active")),
                    new TrainingType(data.getProperty(prefix + "specialization")));
            storage.put(trainer.getId(), trainer);
        }
        LOGGER.info("Loaded " + trainerCount + " trainers");
    }

    private void initializeTrainings(Map<Long, Training> storage) {
        Properties data = loadSeedData();
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
            storage.put(training.getId(), training);
        }

        LOGGER.info("Loaded " + trainingCount + " trainings");
    }
}
