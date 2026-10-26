package kz.olzhas.service;

import kz.olzhas.TestTypes;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.dao.TrainingDao;
import kz.olzhas.dao.TrainingTypeDao;
import kz.olzhas.exceptions.AuthenticationException;
import kz.olzhas.exceptions.TrainerNotFoundException;
import kz.olzhas.exceptions.ValidationException;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TrainingServiceTest {
    private static final String TRAINEE = "John.Smith";
    private static final String TRAINER = "Jane.Doe";
    private static final String PASSWORD = "secret12345";
    private static final LocalDate DATE = LocalDate.of(2026, 10, 12);

    @Mock private TrainingDao trainingDao;
    @Mock private TrainerDao trainerDao;
    @Mock private TrainingTypeDao trainingTypeDao;
    @Mock private AuthenticationService authenticationService;
    private TrainingService service;
    private Trainee trainee;
    private Trainer trainer;

    @Before
    public void setUp() {
        service = new TrainingService(trainingDao, trainerDao, trainingTypeDao, authenticationService);
        trainee = new Trainee(1L, "John", "Smith", TRAINEE, PASSWORD, true, null, null);
        trainer = new Trainer(2L, "Jane", "Doe", TRAINER, PASSWORD, true, TestTypes.YOGA);
    }

    @Test
    public void createSavesTheTraining() {
        when(authenticationService.authenticateTrainee(TRAINEE, PASSWORD)).thenReturn(trainee);
        when(trainerDao.findByUsername(TRAINER)).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("YOGA")).thenReturn(Optional.of(TestTypes.YOGA));
        when(trainingDao.create(any(Training.class))).thenAnswer(call -> call.getArgument(0));

        Training created = service.create(TRAINEE, PASSWORD, TRAINER, "Morning yoga", "YOGA", DATE, 60);

        assertSame(trainee, created.getTrainee());
        assertSame(trainer, created.getTrainer());
        assertEquals("Morning yoga", created.getTrainingName());
        assertSame(TestTypes.YOGA, created.getTrainingType());
        assertEquals(DATE, created.getTrainingDate());
        assertEquals(Integer.valueOf(60), created.getTrainingDuration());
    }

    @Test
    public void createRejectsInvalidFields() {
        when(authenticationService.authenticateTrainee(TRAINEE, PASSWORD)).thenReturn(trainee);

        assertThrows(ValidationException.class,
                () -> service.create(TRAINEE, PASSWORD, TRAINER, " ", "YOGA", DATE, 60));
        assertThrows(ValidationException.class,
                () -> service.create(TRAINEE, PASSWORD, TRAINER, "Yoga", "YOGA", null, 60));
        assertThrows(ValidationException.class,
                () -> service.create(TRAINEE, PASSWORD, TRAINER, "Yoga", "YOGA", DATE, 0));
        assertThrows(ValidationException.class,
                () -> service.create(TRAINEE, PASSWORD, null, "Yoga", "YOGA", DATE, 60));
        verifyNoInteractions(trainingDao, trainerDao, trainingTypeDao);
    }

    @Test
    public void createFailsForUnknownTrainer() {
        when(authenticationService.authenticateTrainee(TRAINEE, PASSWORD)).thenReturn(trainee);
        when(trainerDao.findByUsername("Nobody")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> service.create(TRAINEE, PASSWORD, "Nobody", "Yoga", "YOGA", DATE, 60));
        verifyNoInteractions(trainingDao);
    }

    @Test
    public void createFailsForUnknownTrainingType() {
        when(authenticationService.authenticateTrainee(TRAINEE, PASSWORD)).thenReturn(trainee);
        when(trainerDao.findByUsername(TRAINER)).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("JUDO")).thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> service.create(TRAINEE, PASSWORD, TRAINER, "Judo", "JUDO", DATE, 60));
        verifyNoInteractions(trainingDao);
    }

    @Test
    public void createRequiresAuthentication() {
        when(authenticationService.authenticateTrainee(TRAINEE, "wrong"))
                .thenThrow(new AuthenticationException("Invalid username or password."));

        assertThrows(AuthenticationException.class,
                () -> service.create(TRAINEE, "wrong", TRAINER, "Yoga", "YOGA", DATE, 60));
        verifyNoInteractions(trainingDao, trainerDao, trainingTypeDao);
    }

    @Test
    public void getTraineeTrainingsPassesTheCriteria() {
        when(authenticationService.authenticateTrainee(TRAINEE, PASSWORD)).thenReturn(trainee);
        List<Training> trainings = List.of(new Training());
        when(trainingDao.findTraineeTrainings(TRAINEE, DATE, DATE, "Jane", "YOGA"))
                .thenReturn(trainings);

        assertSame(trainings,
                service.getTraineeTrainings(TRAINEE, PASSWORD, DATE, DATE, "Jane", "YOGA"));
    }

    @Test
    public void getTrainerTrainingsPassesTheCriteria() {
        when(authenticationService.authenticateTrainer(TRAINER, PASSWORD)).thenReturn(trainer);
        when(trainingDao.findTrainerTrainings(TRAINER, null, DATE, "John")).thenReturn(List.of());

        assertTrue(service.getTrainerTrainings(TRAINER, PASSWORD, null, DATE, "John").isEmpty());
    }

    @Test
    public void getTrainerTrainingsRequiresAuthentication() {
        when(authenticationService.authenticateTrainer(TRAINER, "wrong"))
                .thenThrow(new AuthenticationException("Invalid username or password."));

        assertThrows(AuthenticationException.class,
                () -> service.getTrainerTrainings(TRAINER, "wrong", null, null, null));
        verifyNoInteractions(trainingDao);
    }
}
