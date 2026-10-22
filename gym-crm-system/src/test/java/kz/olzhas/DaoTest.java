package kz.olzhas;

import kz.olzhas.dao.impl.InMemoryTraineeDao;
import kz.olzhas.dao.impl.InMemoryTrainerDao;
import kz.olzhas.dao.impl.InMemoryTrainingDao;
import kz.olzhas.exceptions.TraineeNotFoundException;
import kz.olzhas.exceptions.TrainerNotFoundException;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;
import kz.olzhas.model.TrainingType;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DaoTest {
    private Map<Long, Trainee> trainees;
    private Map<Long, Trainer> trainers;
    private Map<Long, Training> trainings;
    private InMemoryTraineeDao traineeDao;
    private InMemoryTrainerDao trainerDao;
    private InMemoryTrainingDao trainingDao;

    @Before
    public void setUp() {
        trainees = new LinkedHashMap<>();
        trainers = new LinkedHashMap<>();
        trainings = new LinkedHashMap<>();
        traineeDao = new InMemoryTraineeDao();
        trainerDao = new InMemoryTrainerDao();
        trainingDao = new InMemoryTrainingDao();
        traineeDao.setStorage(trainees);
        trainerDao.setStorage(trainers);
        trainingDao.setStorage(trainings);
    }

    @Test
    public void traineeCreateAssignsAnIdAboveExistingRecords() {
        trainees.put(20L, trainee(20L, "Existing address"));

        Trainee created = traineeDao.create(trainee(null, "New address"));

        assertEquals(Long.valueOf(21), created.getId());
        assertSame(created, trainees.get(21L));
    }

    @Test
    public void createRejectsAProfileThatAlreadyHasAnId() {
        assertThrows(IllegalStateException.class, () -> traineeDao.create(trainee(5L, "Address")));
    }

    @Test
    public void traineeCreateDoesNotReuseDeletedIds() {
        Trainee first = traineeDao.create(trainee(null, "Address"));
        traineeDao.delete(first.getId());

        Trainee next = traineeDao.create(trainee(null, "Address"));

        assertEquals(Long.valueOf(first.getId() + 1), next.getId());
    }

    @Test
    public void traineeUpdateReplacesTheStoredProfile() {
        trainees.put(21L, trainee(21L, "Old address"));
        Trainee changed = trainee(21L, "New address");

        assertTrue(traineeDao.update(changed));

        assertSame(changed, trainees.get(21L));
    }

    @Test
    public void traineeUpdateMissingProfileThrows() {
        assertThrows(TraineeNotFoundException.class,
                () -> traineeDao.update(trainee(999L, "Address")));
    }

    @Test
    public void deleteRemovesTrainee() {
        trainees.put(21L, trainee(21L, "Address"));

        traineeDao.delete(21L);

        assertFalse(trainees.containsKey(21L));
    }

    @Test
    public void deleteMissingTraineeThrows() {
        assertThrows(TraineeNotFoundException.class, () -> traineeDao.delete(999L));
    }

    @Test
    public void traineeFindByIdReturnsStoredProfile() {
        Trainee stored = trainee(21L, "Address");
        trainees.put(21L, stored);

        assertSame(stored, traineeDao.findById(21L).orElseThrow());
    }

    @Test
    public void traineeFindByIdReturnsEmptyForMissingProfile() {
        assertTrue(traineeDao.findById(999L).isEmpty());
    }

    @Test
    public void traineeFindAllReturnsStoredProfiles() {
        Trainee stored = trainee(21L, "Address");
        trainees.put(21L, stored);

        assertEquals(List.of(stored), traineeDao.findAll());
    }

    @Test
    public void trainerCreateAssignsFirstId() {
        Trainer created = trainerDao.create(trainer(null, TrainingType.YOGA));

        assertEquals(Long.valueOf(1), created.getId());
        assertSame(created, trainers.get(1L));
    }

    @Test
    public void trainerUpdateReplacesTheStoredProfile() {
        trainers.put(31L, trainer(31L, TrainingType.YOGA));
        Trainer changed = trainer(31L, TrainingType.AEROBICA);

        assertTrue(trainerDao.update(changed));

        assertSame(changed, trainers.get(31L));
    }

    @Test
    public void trainerUpdateMissingProfileThrows() {
        assertThrows(TrainerNotFoundException.class, () -> trainerDao.update(trainer(999L, TrainingType.YOGA)));
    }

    @Test
    public void trainerFindByIdReturnsStoredProfile() {
        Trainer stored = trainer(31L, TrainingType.YOGA);
        trainers.put(31L, stored);

        assertSame(stored, trainerDao.findById(31L).orElseThrow());
    }

    @Test
    public void trainerFindByIdReturnsEmptyForMissingProfile() {
        assertTrue(trainerDao.findById(999L).isEmpty());
    }

    @Test
    public void trainerFindAllReturnsStoredProfiles() {
        Trainer stored = trainer(31L, TrainingType.YOGA);
        trainers.put(31L, stored);

        assertEquals(List.of(stored), trainerDao.findAll());
    }

    @Test
    public void trainingCreateAssignsFirstId() {
        Training created = trainingDao.create(training(null));

        assertEquals(Long.valueOf(1), created.getId());
        assertSame(created, trainings.get(1L));
    }

    @Test
    public void trainingFindByIdReturnsStoredTraining() {
        Training stored = training(51L);
        trainings.put(51L, stored);

        assertSame(stored, trainingDao.findById(51L).orElseThrow());
    }

    @Test
    public void trainingFindByIdReturnsEmptyForMissingTraining() {
        assertTrue(trainingDao.findById(999L).isEmpty());
    }

    @Test
    public void trainingFindAllReturnsStoredTrainings() {
        Training stored = training(51L);
        trainings.put(51L, stored);

        assertEquals(List.of(stored), trainingDao.findAll());
    }

    private Trainee trainee(Long id, String address) {
        return new Trainee(id, "New", "User", "New.User", "Abcdef1234", true, null, address);
    }

    private Trainer trainer(Long id, TrainingType specialization) {
        return new Trainer(id, "Alex", "Smith", "Alex.Smith", "Abcdef1234", true,
                specialization);
    }

    private Training training(Long id) {
        return new Training(id, 21L, 31L, "Session", TrainingType.YOGA,
                LocalDate.of(2026, 1, 2), Duration.ofMinutes(30));
    }
}
