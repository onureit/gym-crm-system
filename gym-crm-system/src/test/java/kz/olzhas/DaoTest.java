package kz.olzhas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.impl.InMemoryTraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.dao.impl.InMemoryTrainerDao;
import kz.olzhas.dao.TrainingDao;
import kz.olzhas.dao.impl.InMemoryTrainingDao;
import kz.olzhas.exceptions.TraineeNotFoundException;
import kz.olzhas.exceptions.TrainerNotFoundException;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;
import kz.olzhas.model.TrainingType;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class DaoTest {

    @Test
    public void traineeDaoCreatesUpdatesSelectsAndDeletes() {
        Map<Long, Trainee> storage = new LinkedHashMap<>();
        storage.put(
                20L, new Trainee(20L, "Seed", "User", "Seed.User", "0123456789", true, null, null));
        InMemoryTraineeDao implementation = new InMemoryTraineeDao();
        implementation.setStorage(storage);
        TraineeDao dao = implementation;

        Trainee trainee =
                new Trainee(
                        null, "New", "User", "New.User", "abcdefghij", true, null, "Old address");
        dao.create(trainee);
        assertEquals(Long.valueOf(21), trainee.getId());
        assertEquals(2, dao.findAll().size());
        assertSame(trainee, dao.findById(21).orElseThrow());

        trainee.setAddress("New address");
        assertTrue(dao.update(trainee));
        assertEquals("New address", dao.findById(21).orElseThrow().getAddress());

        dao.delete(21);
        assertTrue(dao.findById(21).isEmpty());
        assertThrows(TraineeNotFoundException.class, () -> dao.delete(21));
        assertThrows(TraineeNotFoundException.class, () -> dao.update(trainee));

        Trainee next = new Trainee();
        dao.create(next);
        assertEquals(Long.valueOf(22), next.getId());
    }

    @Test
    public void trainerDaoCreatesUpdatesAndSelects() {
        InMemoryTrainerDao implementation = new InMemoryTrainerDao();
        implementation.setStorage(new LinkedHashMap<>());
        TrainerDao dao = implementation;

        Trainer trainer =
                new Trainer(
                        null,
                        "Alex",
                        "Smith",
                        "Alex.Smith",
                        "0123456789",
                        true,
                        new TrainingType("Yoga"));
        dao.create(trainer);
        assertEquals(Long.valueOf(1), trainer.getId());
        assertSame(trainer, dao.findById(1).orElseThrow());

        trainer.setSpecialization(new TrainingType("Pilates"));
        assertTrue(dao.update(trainer));
        assertEquals("Pilates", dao.findById(1).orElseThrow().getSpecialization().getTrainingTypeName());
        assertEquals(1, dao.findAll().size());
        assertTrue(dao.findById(999).isEmpty());

        Trainer missing = new Trainer();
        missing.setId(999L);
        assertThrows(TrainerNotFoundException.class, () -> dao.update(missing));
    }

    @Test
    public void trainingDaoCreatesAndSelects() {
        InMemoryTrainingDao implementation = new InMemoryTrainingDao();
        implementation.setStorage(new LinkedHashMap<>());
        TrainingDao dao = implementation;

        Training training =
                new Training(
                        null,
                        1L,
                        1L,
                        "Session",
                        new TrainingType("Yoga"),
                        LocalDate.of(2026, 10, 12),
                        Duration.ofMinutes(30));
        dao.create(training);
        assertEquals(Long.valueOf(1), training.getId());
        assertSame(training, dao.findById(1).orElseThrow());
        assertEquals(1, dao.findAll().size());
        assertEquals(Duration.ofMinutes(30), dao.findById(1).orElseThrow().getTrainingDuration());
        assertTrue(dao.findById(999).isEmpty());
    }

    private static void assertThrows(Class<? extends RuntimeException> expected, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            assertTrue("Unexpected exception: " + exception, expected.isInstance(exception));
            return;
        }
        org.junit.Assert.fail("Expected " + expected.getSimpleName());
    }
}
