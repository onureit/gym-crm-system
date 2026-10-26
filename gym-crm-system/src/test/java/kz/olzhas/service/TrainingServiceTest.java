package kz.olzhas.service;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.dao.TrainingDao;
import kz.olzhas.exceptions.TrainingNotFoundException;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;
import kz.olzhas.model.TrainingType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TrainingServiceTest {
    @Mock private TrainingDao trainingDao;
    @Mock private TraineeDao traineeDao;
    @Mock private TrainerDao trainerDao;
    private TrainingService service;

    @Before
    public void setUp() {
        service = new TrainingService();
        service.setTrainingDao(trainingDao);
        service.setTraineeDao(traineeDao);
        service.setTrainerDao(trainerDao);
    }

    @Test
    public void createChecksBothParticipantsBeforePersisting() {
        Training draft = training(null, 12L, 34L);
        Training stored = training(56L, 12L, 34L);
        when(traineeDao.findById(12L)).thenReturn(Optional.of(new Trainee("Alex", "Smith", true, null, null)));
        when(trainerDao.findById(34L)).thenReturn(Optional.of(new Trainer("Alex", "Smith", true, null)));
        when(trainingDao.create(draft)).thenReturn(stored);

        assertSame(stored, service.create(draft));

        InOrder order = inOrder(traineeDao, trainerDao, trainingDao);
        order.verify(traineeDao).findById(12L);
        order.verify(trainerDao).findById(34L);
        order.verify(trainingDao).create(draft);
        verifyNoMoreInteractions(traineeDao, trainerDao, trainingDao);
    }

    @Test
    public void missingTraineePreventsStorageAndTrainerLookup() {
        Training draft = training(null, 12L, 34L);
        when(traineeDao.findById(12L)).thenReturn(Optional.empty());

        IllegalArgumentException failure =
                assertThrows(IllegalArgumentException.class, () -> service.create(draft));
        assertEquals("Trainee with ID 12 does not exist.", failure.getMessage());
        verifyNoInteractions(trainerDao, trainingDao);
    }

    @Test
    public void missingTrainerPreventsStorage() {
        Training draft = training(null, 12L, 34L);
        when(traineeDao.findById(12L)).thenReturn(Optional.of(new Trainee("Alex", "Smith", true, null, null)));
        when(trainerDao.findById(34L)).thenReturn(Optional.empty());

        IllegalArgumentException failure =
                assertThrows(IllegalArgumentException.class, () -> service.create(draft));
        assertEquals("Trainer with ID 34 does not exist.", failure.getMessage());
        verifyNoInteractions(trainingDao);
    }

    @Test
    public void nullParticipantIdsSkipParticipantLookups() {
        Training draft = training(null, null, null);
        when(trainingDao.create(draft)).thenReturn(draft);

        assertSame(draft, service.create(draft));
        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    public void participantLookupFailurePreventsStorage() {
        Training draft = training(null, 12L, 34L);
        IllegalStateException failure = new IllegalStateException("Lookup failed");
        when(traineeDao.findById(12L)).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.create(draft)));
        verifyNoInteractions(trainerDao, trainingDao);
    }

    @Test
    public void createPropagatesStorageFailure() {
        Training draft = training(null, null, null);
        IllegalStateException failure = new IllegalStateException("Storage unavailable");
        when(trainingDao.create(draft)).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.create(draft)));
    }

    @Test
    public void findByIdReturnsTheDaoTraining() {
        Training stored = training(56L, 12L, 34L);
        when(trainingDao.findById(56L)).thenReturn(Optional.of(stored));

        assertSame(stored, service.findById(56L));
        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    public void findByIdThrowsADescriptiveExceptionForMissingTraining() {
        when(trainingDao.findById(56L)).thenReturn(Optional.empty());

        TrainingNotFoundException failure =
                assertThrows(TrainingNotFoundException.class, () -> service.findById(56L));
        assertEquals("Training with ID 56 does not exist.", failure.getMessage());
    }

    @Test
    public void findAllReturnsTheDaoTrainingsIncludingAnEmptyResult() {
        List<Training> trainings = List.of(training(56L, 12L, 34L));
        when(trainingDao.findAll()).thenReturn(trainings, List.of());

        assertSame(trainings, service.findAll());
        assertTrue(service.findAll().isEmpty());
        verifyNoInteractions(traineeDao, trainerDao);
    }

    private Training training(Long id, Long traineeId, Long trainerId) {
        return new Training(id, traineeId, trainerId, "Test session", TrainingType.YOGA,
                LocalDate.of(2026, 1, 2), Duration.ofMinutes(45));
    }
}
