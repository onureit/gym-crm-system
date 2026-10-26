package kz.olzhas.service;

import kz.olzhas.TestTypes;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.dao.TrainingTypeDao;
import kz.olzhas.exceptions.AuthenticationException;
import kz.olzhas.exceptions.ValidationException;
import kz.olzhas.model.Trainer;
import kz.olzhas.util.UserRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TrainerServiceTest {
    private static final String USERNAME = "Jane.Doe";
    private static final String PASSWORD = "secret12345";

    @Mock private TrainerDao trainerDao;
    @Mock private TrainingTypeDao trainingTypeDao;
    @Mock private AuthenticationService authenticationService;
    @Mock private UserRegistry userRegistry;
    private TrainerService service;
    private Trainer trainer;

    @Before
    public void setUp() {
        service = new TrainerService(trainerDao, trainingTypeDao, authenticationService, userRegistry);
        trainer = new Trainer(1L, "Jane", "Doe", USERNAME, PASSWORD, true, TestTypes.YOGA);
    }

    @Test
    public void createGeneratesCredentialsAndSaves() {
        when(trainingTypeDao.findByName("YOGA")).thenReturn(Optional.of(TestTypes.YOGA));
        when(userRegistry.generateUsername("Jane", "Doe")).thenReturn(USERNAME);
        when(userRegistry.generatePassword()).thenReturn(PASSWORD);
        when(trainerDao.create(any(Trainer.class))).thenAnswer(call -> call.getArgument(0));

        Trainer created = service.create(" Jane ", "Doe", "YOGA");

        assertEquals("Jane", created.getFirstName());
        assertEquals(USERNAME, created.getUsername());
        assertEquals(PASSWORD, created.getPassword());
        assertTrue(created.isActive());
        assertSame(TestTypes.YOGA, created.getSpecialization());
        verify(trainerDao).create(created);
    }

    @Test
    public void createRejectsMissingFields() {
        assertThrows(ValidationException.class, () -> service.create(null, "Doe", "YOGA"));
        assertThrows(ValidationException.class, () -> service.create("Jane", "", "YOGA"));
        assertThrows(ValidationException.class, () -> service.create("Jane", "Doe", " "));
        verifyNoInteractions(trainerDao, trainingTypeDao, userRegistry);
    }

    @Test
    public void createRejectsUnknownSpecialization() {
        when(trainingTypeDao.findByName("JUDO")).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> service.create("Jane", "Doe", "JUDO"));
        verifyNoInteractions(trainerDao, userRegistry);
    }

    @Test
    public void matchCredentialsDelegatesToAuthentication() {
        when(authenticationService.matchTrainer(USERNAME, "wrong")).thenReturn(false);

        assertFalse(service.matchCredentials(USERNAME, "wrong"));
    }

    @Test
    public void findByUsernameReturnsTheAuthenticatedProfile() {
        authenticated();

        assertSame(trainer, service.findByUsername(USERNAME, PASSWORD));
    }

    @Test
    public void operationsFailWhenAuthenticationFails() {
        when(authenticationService.authenticateTrainer(USERNAME, "wrong"))
                .thenThrow(new AuthenticationException("Invalid username or password."));

        assertThrows(AuthenticationException.class,
                () -> service.toggleActive(USERNAME, "wrong"));
        verifyNoInteractions(trainerDao);
    }

    @Test
    public void changePasswordUpdatesThePassword() {
        authenticated();

        service.changePassword(USERNAME, PASSWORD, "newPass123");

        assertEquals("newPass123", trainer.getPassword());
        verify(trainerDao).update(trainer);
    }

    @Test
    public void updateChangesTheProfile() {
        authenticated();
        when(trainingTypeDao.findByName("AEROBICA")).thenReturn(Optional.of(TestTypes.AEROBICA));
        when(trainerDao.update(trainer)).thenReturn(trainer);

        Trainer updated = service.update(USERNAME, PASSWORD, "Janet", "Dow", "AEROBICA");

        assertEquals("Janet", updated.getFirstName());
        assertEquals("Dow", updated.getLastName());
        assertSame(TestTypes.AEROBICA, updated.getSpecialization());
        assertEquals(USERNAME, updated.getUsername());
    }

    @Test
    public void updateRejectsMissingNames() {
        authenticated();

        assertThrows(ValidationException.class,
                () -> service.update(USERNAME, PASSWORD, "Jane", null, "YOGA"));
        verifyNoInteractions(trainerDao);
    }

    @Test
    public void toggleActiveIsNotIdempotent() {
        authenticated();

        assertFalse(service.toggleActive(USERNAME, PASSWORD));
        assertTrue(service.toggleActive(USERNAME, PASSWORD));
        verify(trainerDao, times(2)).update(trainer);
    }

    private void authenticated() {
        when(authenticationService.authenticateTrainer(USERNAME, PASSWORD)).thenReturn(trainer);
    }
}
