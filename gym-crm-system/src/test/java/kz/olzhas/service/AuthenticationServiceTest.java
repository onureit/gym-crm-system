package kz.olzhas.service;

import kz.olzhas.TestTypes;
import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.exceptions.AuthenticationException;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class AuthenticationServiceTest {
    @Mock private TraineeDao traineeDao;
    @Mock private TrainerDao trainerDao;
    private AuthenticationService service;

    @Before
    public void setUp() {
        service = new AuthenticationService(traineeDao, trainerDao);
    }

    @Test
    public void matchTraineeChecksThePassword() {
        when(traineeDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainee()));

        assertTrue(service.matchTrainee("John.Smith", "secret12345"));
        assertFalse(service.matchTrainee("John.Smith", "wrong"));
    }

    @Test
    public void matchTraineeIsFalseForUnknownUser() {
        when(traineeDao.findByUsername("Nobody")).thenReturn(Optional.empty());

        assertFalse(service.matchTrainee("Nobody", "secret12345"));
    }

    @Test
    public void matchTrainerChecksThePassword() {
        when(trainerDao.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainer()));

        assertTrue(service.matchTrainer("Jane.Doe", "secret12345"));
        assertFalse(service.matchTrainer("Jane.Doe", "wrong"));
    }

    @Test
    public void authenticateTraineeReturnsTheProfile() {
        Trainee trainee = trainee();
        when(traineeDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainee));

        assertSame(trainee, service.authenticateTrainee("John.Smith", "secret12345"));
    }

    @Test
    public void authenticateTraineeFailsOnWrongPasswordOrUnknownUser() {
        when(traineeDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainee()));
        when(traineeDao.findByUsername("Nobody")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> service.authenticateTrainee("John.Smith", "wrong"));
        assertThrows(AuthenticationException.class,
                () -> service.authenticateTrainee("Nobody", "secret12345"));
    }

    @Test
    public void authenticateTrainerFailsOnWrongPassword() {
        Trainer trainer = trainer();
        when(trainerDao.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainer));

        assertSame(trainer, service.authenticateTrainer("Jane.Doe", "secret12345"));
        assertThrows(AuthenticationException.class,
                () -> service.authenticateTrainer("Jane.Doe", "wrong"));
    }

    private Trainee trainee() {
        return new Trainee(1L, "John", "Smith", "John.Smith", "secret12345", true, null, null);
    }

    private Trainer trainer() {
        return new Trainer(2L, "Jane", "Doe", "Jane.Doe", "secret12345", true, TestTypes.YOGA);
    }
}
