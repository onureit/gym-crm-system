package kz.olzhas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import kz.olzhas.component.InMemoryStorage;
import kz.olzhas.config.AppConfig;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;
import kz.olzhas.model.TrainingType;
import kz.olzhas.service.GymService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Duration;
import java.time.LocalDate;

public class AppTest {
    private AnnotationConfigApplicationContext context;
    private GymService gym;

    @Before
    public void openContext() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        gym = context.getBean(GymService.class);
    }

    @After
    public void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    private Trainee trainee(String first, String last) {
        return new Trainee(
                null, first, last, null, null, true, LocalDate.of(2000, 1, 1), "Address");
    }

    private Trainer trainer(String first, String last) {
        return new Trainer(null, first, last, null, null, true, new TrainingType("Yoga"));
    }

    private Training training(long traineeId, long trainerId) {
        return new Training(
                null,
                traineeId,
                trainerId,
                "Yoga session",
                new TrainingType("Yoga"),
                LocalDate.of(2026, 10, 12),
                Duration.ofMinutes(45));
    }

    @Test
    public void contextLoadsAllNamespacesAndPreparedData() {
        InMemoryStorage storage = context.getBean(InMemoryStorage.class);
        assertSame(context.getBean("traineeStorage"), storage.getTrainees());
        assertSame(context.getBean("trainerStorage"), storage.getTrainers());
        assertSame(context.getBean("trainingStorage"), storage.getTrainings());
        assertEquals(1, gym.listTrainees().size());
        assertEquals("John.Smith", gym.selectTrainee(1).getUsername());
        assertEquals("Jane.Doe", gym.selectTrainer(1).getUsername());
        assertEquals(Duration.ofMinutes(60), gym.selectTraining(1).getTrainingDuration());
    }

    @Test
    public void traineeCrudPreservesCredentials() {
        Trainee created = gym.createTrainee(trainee("Alice", "Brown"));
        assertEquals(Long.valueOf(2), created.getId());
        assertEquals("Alice.Brown", created.getUsername());
        assertEquals(10, created.getPassword().length());
        assertTrue(created.getPassword().matches("[A-Za-z0-9]{10}"));
        created.setAddress("New address");
        created.setFirstName("Alicia");
        String password = created.getPassword();
        assertTrue(gym.updateTrainee(created));
        Trainee stored = gym.selectTrainee(created.getId());
        assertEquals("New address", stored.getAddress());
        assertEquals("Alicia", stored.getFirstName());
        assertEquals("Alice.Brown", stored.getUsername());
        assertEquals(password, stored.getPassword());
        assertTrue(gym.deleteTrainee(created.getId()));
        assertNull(gym.selectTrainee(created.getId()));
        assertFalse(gym.deleteTrainee(created.getId()));
    }

    @Test
    public void trainerCreateUpdateAndSelect() {
        Trainer created = gym.createTrainer(trainer("Alex", "Green"));
        created.setSpecialization(new TrainingType("Pilates"));
        created.setActive(false);
        String username = created.getUsername();
        String password = created.getPassword();
        assertTrue(gym.updateTrainer(created));
        Trainer stored = gym.selectTrainer(created.getId());
        assertEquals("Pilates", stored.getSpecialization().getTrainingTypeName());
        assertFalse(stored.isActive());
        assertEquals(username, stored.getUsername());
        assertEquals(password, stored.getPassword());
    }

    @Test
    public void usernamesAreUniqueAcrossBothProfileNamespacesAndSeedData() {
        Trainee first = gym.createTrainee(trainee("John", "Smith"));
        Trainer second = gym.createTrainer(trainer("John", "Smith"));
        Trainee third = gym.createTrainee(trainee("John", "Smith"));
        assertEquals("John.Smith1", first.getUsername());
        assertEquals("John.Smith2", second.getUsername());
        assertEquals("John.Smith3", third.getUsername());
    }

    @Test
    public void usernameSuffixAlsoAvoidsDifferentNamesProducingSameCandidate() {
        gym.createTrainee(trainee("John", "Smith1"));
        assertEquals("John.Smith2", gym.createTrainer(trainer("John", "Smith")).getUsername());
    }

    @Test
    public void trainingCreateAndSelect() {
        Trainee created = gym.createTrainee(trainee("New", "Trainee"));
        Training session = gym.createTraining(training(created.getId(), 1));
        assertEquals(Long.valueOf(2), session.getId());
        assertEquals(created.getId(), gym.selectTraining(session.getId()).getTraineeId());
        assertTrue(gym.deleteTrainee(created.getId()));
        assertNotNull(gym.selectTraining(session.getId()));
        assertNotNull(gym.selectTraining(1));
    }

    @Test
    public void missingRecordsHavePredictableResults() {
        assertNull(gym.selectTrainee(999));
        assertNull(gym.selectTrainer(999));
        assertNull(gym.selectTraining(999));
        Trainee missing = trainee("Missing", "User");
        missing.setId(999L);
        assertFalse(gym.updateTrainee(missing));
        Trainer absent = trainer("Missing", "Trainer");
        absent.setId(999L);
        assertFalse(gym.updateTrainer(absent));
    }

    @Test
    public void contextStorageIsNotStatic() {
        gym.createTrainee(trainee("Isolated", "Profile"));
        try (AnnotationConfigApplicationContext other =
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            assertEquals(1, other.getBean(GymService.class).listTrainees().size());
            assertNotSame(context.getBean("traineeStorage"), other.getBean("traineeStorage"));
        }
    }
}
