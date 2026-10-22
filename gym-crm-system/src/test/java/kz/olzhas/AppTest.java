package kz.olzhas;

import kz.olzhas.config.AppConfig;
import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.dao.TrainingDao;
import kz.olzhas.dao.impl.InMemoryTraineeDao;
import kz.olzhas.dao.impl.InMemoryTrainerDao;
import kz.olzhas.dao.impl.InMemoryTrainingDao;
import kz.olzhas.service.GymService;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.time.Duration;
import java.util.Map;

import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void contextWiresServicesAndSeedsTheDaoMaps() {
        try (AnnotationConfigApplicationContext context = context("classpath:test-seed-data.properties")) {
            context.refresh();
            GymService gym = context.getBean(GymService.class);
            assertTrue(context.getBean(TraineeDao.class) instanceof InMemoryTraineeDao);
            assertTrue(context.getBean(TrainerDao.class) instanceof InMemoryTrainerDao);
            assertTrue(context.getBean(TrainingDao.class) instanceof InMemoryTrainingDao);
            assertEquals(1, gym.listTrainees().size());
            assertEquals(1, gym.listTrainers().size());
            assertEquals(1, gym.listTrainings().size());
            assertEquals("Test.Member", gym.selectTrainee(41).getUsername());
            assertEquals("Test.Coach", gym.selectTrainer(71).getUsername());
            assertEquals(Duration.ofMinutes(35), gym.selectTraining(91).getTrainingDuration());
            assertSame(context.getBean("traineeStorage", Map.class).get(41L), gym.selectTrainee(41));
            assertSame(context.getBean("trainerStorage", Map.class).get(71L), gym.selectTrainer(71));
            assertSame(context.getBean("trainingStorage", Map.class).get(91L), gym.selectTraining(91));
        }
    }

    @Test
    public void missingSeedFilePreventsStartup() {
        try (AnnotationConfigApplicationContext context = context("classpath:missing-test-seed.properties")) {
            BeansException exception = assertThrows(BeansException.class, context::refresh);
            Throwable rootCause = exception;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            assertTrue(rootCause instanceof java.io.IOException);
        }
    }

    private AnnotationConfigApplicationContext context(String location) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(
                new MapPropertySource("test", Map.of("gym.storage.data.path", location)));
        context.register(AppConfig.class);
        return context;
    }
}
