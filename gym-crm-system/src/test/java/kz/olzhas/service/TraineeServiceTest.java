package kz.olzhas.service;

import kz.olzhas.TestTypes;
import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.exceptions.AuthenticationException;
import kz.olzhas.exceptions.TrainerNotFoundException;
import kz.olzhas.exceptions.ValidationException;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.util.UserRegistry;

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
public class TraineeServiceTest {
    private static final String USERNAME = "John.Smith";
    private static final String PASSWORD = "secret12345";

    @Mock private TraineeDao traineeDao;
    @Mock private TrainerDao trainerDao;
    @Mock private AuthenticationService authenticationService;
    @Mock private UserRegistry userRegistry;
    private TraineeService service;
    private Trainee trainee;

    @Before
    public void setUp() {
        service = new TraineeService(traineeDao, trainerDao, authenticationService, userRegistry);
        trainee = new Trainee(1L, "John", "Smith", USERNAME, PASSWORD, true,
                LocalDate.of(1995, 4, 15), "Old street");
    }

    @Test
    public void createGeneratesCredentialsAndSaves() {
        when(userRegistry.generateUsername("John", "Smith")).thenReturn(USERNAME);
        when(userRegistry.generatePassword()).thenReturn(PASSWORD);
        when(traineeDao.create(any(Trainee.class))).thenAnswer(call -> call.getArgument(0));

        Trainee created = service.create(" John ", "Smith", LocalDate.of(1995, 4, 15), "Street");

        assertEquals("John", created.getFirstName());
        assertEquals(USERNAME, created.getUsername());
        assertEquals(PASSWORD, created.getPassword());
        assertTrue(created.isActive());
        verify(traineeDao).create(created);
    }

    @Test
    public void createRejectsMissingNames() {
        assertThrows(ValidationException.class, () -> service.create(null, "Smith", null, null));
        assertThrows(ValidationException.class, () -> service.create("John", " ", null, null));
        verifyNoInteractions(traineeDao, userRegistry);
    }

    @Test
    public void matchCredentialsDelegatesToAuthentication() {
        when(authenticationService.matchTrainee(USERNAME, PASSWORD)).thenReturn(true);

        assertTrue(service.matchCredentials(USERNAME, PASSWORD));
    }

    @Test
    public void findByUsernameReturnsTheAuthenticatedProfile() {
        authenticated();

        assertSame(trainee, service.findByUsername(USERNAME, PASSWORD));
    }

    @Test
    public void operationsFailWhenAuthenticationFails() {
        when(authenticationService.authenticateTrainee(USERNAME, "wrong"))
                .thenThrow(new AuthenticationException("Invalid username or password."));

        assertThrows(AuthenticationException.class,
                () -> service.changePassword(USERNAME, "wrong", "newPass123"));
        assertThrows(AuthenticationException.class, () -> service.delete(USERNAME, "wrong"));
        verifyNoInteractions(traineeDao);
    }

    @Test
    public void changePasswordUpdatesThePassword() {
        authenticated();

        service.changePassword(USERNAME, PASSWORD, "newPass123");

        assertEquals("newPass123", trainee.getPassword());
        verify(traineeDao).update(trainee);
    }

    @Test
    public void changePasswordRejectsBlankPassword() {
        authenticated();

        assertThrows(ValidationException.class,
                () -> service.changePassword(USERNAME, PASSWORD, " "));
        verifyNoInteractions(traineeDao);
    }

    @Test
    public void updateChangesTheProfile() {
        authenticated();
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee updated = service.update(USERNAME, PASSWORD, "Johnny", "Smithson",
                LocalDate.of(1990, 1, 1), "New street");

        assertEquals("Johnny", updated.getFirstName());
        assertEquals("Smithson", updated.getLastName());
        assertEquals("New street", updated.getAddress());
        assertEquals(USERNAME, updated.getUsername());
    }

    @Test
    public void updateRejectsMissingNames() {
        authenticated();

        assertThrows(ValidationException.class,
                () -> service.update(USERNAME, PASSWORD, "", "Smith", null, null));
        verifyNoInteractions(traineeDao);
    }

    @Test
    public void toggleActiveIsNotIdempotent() {
        authenticated();

        assertFalse(service.toggleActive(USERNAME, PASSWORD));
        assertTrue(service.toggleActive(USERNAME, PASSWORD));
        verify(traineeDao, times(2)).update(trainee);
    }

    @Test
    public void deleteRemovesTheTrainee() {
        authenticated();

        service.delete(USERNAME, PASSWORD);

        verify(traineeDao).delete(trainee);
    }

    @Test
    public void getUnassignedTrainersUsesTheDao() {
        authenticated();
        List<Trainer> trainers = List.of(trainer("Jane.Doe"));
        when(trainerDao.findNotAssignedToTrainee(USERNAME)).thenReturn(trainers);

        assertSame(trainers, service.getUnassignedTrainers(USERNAME, PASSWORD));
    }

    @Test
    public void updateTrainersReplacesTheList() {
        authenticated();
        Trainer oldTrainer = trainer("Old.Trainer");
        Trainer newTrainer = trainer("Jane.Doe");
        trainee.addTrainer(oldTrainer);
        when(trainerDao.findByUsername("Jane.Doe")).thenReturn(Optional.of(newTrainer));

        List<Trainer> result = service.updateTrainers(USERNAME, PASSWORD, List.of("Jane.Doe"));

        assertEquals(List.of(newTrainer), result);
        assertTrue(oldTrainer.getTrainees().isEmpty());
        verify(traineeDao).update(trainee);
    }

    @Test
    public void updateTrainersFailsForUnknownTrainer() {
        authenticated();
        when(trainerDao.findByUsername("Nobody")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> service.updateTrainers(USERNAME, PASSWORD, List.of("Nobody")));
        verifyNoInteractions(traineeDao);
    }

    private void authenticated() {
        when(authenticationService.authenticateTrainee(USERNAME, PASSWORD)).thenReturn(trainee);
    }

    private Trainer trainer(String username) {
        return new Trainer(null, "First", "Last", username, PASSWORD, true, TestTypes.YOGA);
    }
}
