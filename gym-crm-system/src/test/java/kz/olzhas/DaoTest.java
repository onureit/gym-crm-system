package kz.olzhas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.dao.TrainingDao;
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
        TraineeDao dao = new TraineeDao();
        dao.setStorage(storage);

        Trainee trainee =
                new Trainee(
                        null, "New", "User", "New.User", "abcdefghij", true, null, "Old address");
        dao.create(trainee);
        assertEquals(Long.valueOf(21), trainee.getId());
        assertEquals(2, dao.findAll().size());
        assertSame(trainee, dao.findById(21));

        trainee.setAddress("New address");
        assertTrue(dao.update(trainee));
        assertEquals("New address", dao.findById(21).getAddress());

        assertTrue(dao.delete(21));
        assertNull(dao.findById(21));
        assertFalse(dao.delete(21));
        assertFalse(dao.update(trainee));

        Trainee next = new Trainee();
        dao.create(next);
        assertEquals(Long.valueOf(22), next.getId());
    }

    @Test
    public void trainerDaoCreatesUpdatesAndSelects() {
        TrainerDao dao = new TrainerDao();
        dao.setStorage(new LinkedHashMap<>());

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
        assertSame(trainer, dao.findById(1));

        trainer.setSpecialization(new TrainingType("Pilates"));
        assertTrue(dao.update(trainer));
        assertEquals("Pilates", dao.findById(1).getSpecialization().getTrainingTypeName());
        assertEquals(1, dao.findAll().size());
        assertNull(dao.findById(999));

        Trainer missing = new Trainer();
        missing.setId(999L);
        assertFalse(dao.update(missing));
    }

    @Test
    public void trainingDaoCreatesAndSelects() {
        TrainingDao dao = new TrainingDao();
        dao.setStorage(new LinkedHashMap<>());

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
        assertSame(training, dao.findById(1));
        assertEquals(1, dao.findAll().size());
        assertEquals(Duration.ofMinutes(30), dao.findById(1).getTrainingDuration());
        assertNull(dao.findById(999));
    }
}
