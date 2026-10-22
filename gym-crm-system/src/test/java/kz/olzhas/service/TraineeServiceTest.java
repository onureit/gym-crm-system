package kz.olzhas.service;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.exceptions.TraineeNotFoundException;
import kz.olzhas.model.Trainee;
import kz.olzhas.util.UserRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TraineeServiceTest {
    @Mock private TraineeDao dao;
    @Mock private UserRegistry registry;
    private TraineeService service;

    @Before
    public void setUp() {
        service = new TraineeService();
        service.setTraineeDao(dao);
        service.setUserRegistry(registry);
    }

    @Test
    public void createAssignsGeneratedCredentialsBeforePersisting() {
        Trainee draft = draft();
        when(registry.generateUsername("Alex", "Smith")).thenReturn("Alex.Smith");
        when(registry.generatePassword()).thenReturn("Abcdef1234");
        when(dao.create(draft)).thenReturn(draft);

        assertSame(draft, service.create(draft));

        assertEquals("Alex.Smith", draft.getUsername());
        assertEquals("Abcdef1234", draft.getPassword());
        InOrder order = inOrder(registry, dao);
        order.verify(registry).generateUsername("Alex", "Smith");
        order.verify(registry).generatePassword();
        order.verify(dao).create(draft);
    }

    @Test
    public void createRejectsAProfileWithCredentials() {
        Trainee existing = profile(42L);
        when(registry.generateUsername("Alex", "Smith")).thenReturn("Alex.Smith1");
        when(registry.generatePassword()).thenReturn("Abcdef1234");

        assertThrows(IllegalStateException.class, () -> service.create(existing));
        verifyNoInteractions(dao);
    }

    @Test
    public void createPropagatesStorageFailure() {
        Trainee draft = draft();
        IllegalStateException failure = new IllegalStateException("Storage unavailable");
        when(registry.generateUsername("Alex", "Smith")).thenReturn("Alex.Smith");
        when(registry.generatePassword()).thenReturn("Abcdef1234");
        when(dao.create(draft)).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.create(draft)));
    }

    @Test
    public void updateChangesEditableFieldsOfTheStoredProfileOnly() {
        Trainee stored = profile(42L);
        when(dao.findById(42L)).thenReturn(Optional.of(stored));
        when(dao.update(stored)).thenReturn(true);

        assertTrue(service.update(new Trainee(42L, "Alicia", "Brown", "Injected", "Injected", false, null, "New address")));

        assertEquals("Alicia", stored.getFirstName());
        assertEquals("Brown", stored.getLastName());
        assertEquals("New address", stored.getAddress());
        assertEquals("Alex.Smith", stored.getUsername());
        assertEquals("Abcdef1234", stored.getPassword());
        assertTrue(stored.isActive());
        verify(dao).update(stored);
        verifyNoInteractions(registry);
    }

    @Test
    public void updateOfMissingProfileDoesNotWriteToTheDao() {
        when(dao.findById(42L)).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> service.update(profile(42L)));
        verify(dao, never()).update(any());
        verifyNoInteractions(registry);
    }

    @Test
    public void findByIdReturnsTheDaoProfile() {
        Trainee stored = profile(42L);
        when(dao.findById(42L)).thenReturn(Optional.of(stored));

        assertSame(stored, service.findById(42L));
        verify(dao).findById(42L);
    }

    @Test
    public void findByIdThrowsADescriptiveExceptionForMissingProfile() {
        when(dao.findById(42L)).thenReturn(Optional.empty());

        TraineeNotFoundException failure =
                assertThrows(TraineeNotFoundException.class, () -> service.findById(42L));
        assertEquals("Trainee with ID 42 does not exist.", failure.getMessage());
    }

    @Test
    public void findAllReturnsTheDaoProfilesIncludingAnEmptyResult() {
        List<Trainee> profiles = List.of(profile(42L));
        when(dao.findAll()).thenReturn(profiles, List.of());

        assertSame(profiles, service.findAll());
        assertTrue(service.findAll().isEmpty());
    }

    @Test
    public void deleteDelegatesToTheDao() {
        service.delete(42L);

        verify(dao).delete(42L);
        verifyNoMoreInteractions(dao);
        verifyNoInteractions(registry);
    }

    @Test
    public void deletePropagatesMissingProfile() {
        TraineeNotFoundException failure = new TraineeNotFoundException("Missing profile");
        doThrow(failure).when(dao).delete(42L);

        assertSame(failure, assertThrows(TraineeNotFoundException.class, () -> service.delete(42L)));
        verifyNoInteractions(registry);
    }

    private Trainee draft() {
        return new Trainee("Alex", "Smith", true, null, "Test address");
    }

    private Trainee profile(Long id) {
        return new Trainee(id, "Alex", "Smith", "Alex.Smith", "Abcdef1234", true,
                null, "Test address");
    }
}
