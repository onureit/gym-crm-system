package kz.olzhas.service;

import kz.olzhas.dao.TrainerDao;
import kz.olzhas.exceptions.TrainerNotFoundException;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.TrainingType;
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
public class TrainerServiceTest {
    @Mock private TrainerDao dao;
    @Mock private UserRegistry registry;
    private TrainerService service;

    @Before
    public void setUp() {
        service = new TrainerService();
        service.setTrainerDao(dao);
        service.setUserRegistry(registry);
    }

    @Test
    public void createAssignsGeneratedCredentialsBeforePersisting() {
        Trainer draft = draft();
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
        Trainer existing = profile(42L);
        when(registry.generateUsername("Alex", "Smith")).thenReturn("Alex.Smith1");
        when(registry.generatePassword()).thenReturn("Abcdef1234");

        assertThrows(IllegalStateException.class, () -> service.create(existing));
        verifyNoInteractions(dao);
    }

    @Test
    public void createPropagatesStorageFailure() {
        Trainer draft = draft();
        IllegalStateException failure = new IllegalStateException("Storage unavailable");
        when(registry.generateUsername("Alex", "Smith")).thenReturn("Alex.Smith");
        when(registry.generatePassword()).thenReturn("Abcdef1234");
        when(dao.create(draft)).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.create(draft)));
    }

    @Test
    public void updateChangesEditableFieldsOfTheStoredProfileOnly() {
        Trainer stored = profile(42L);
        when(dao.findById(42L)).thenReturn(Optional.of(stored));
        when(dao.update(stored)).thenReturn(true);

        assertTrue(service.update(new Trainer(42L, "Alicia", "Brown", "Injected", "Injected", false, TrainingType.AEROBICA)));

        assertEquals("Alicia", stored.getFirstName());
        assertEquals("Brown", stored.getLastName());
        assertEquals(TrainingType.AEROBICA, stored.getSpecialization());
        assertEquals("Alex.Smith", stored.getUsername());
        assertEquals("Abcdef1234", stored.getPassword());
        assertTrue(stored.isActive());
        verify(dao).update(stored);
        verifyNoInteractions(registry);
    }

    @Test
    public void updateOfMissingProfileDoesNotWriteToTheDao() {
        when(dao.findById(42L)).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> service.update(profile(42L)));
        verify(dao, never()).update(any());
        verifyNoInteractions(registry);
    }

    @Test
    public void findByIdReturnsTheDaoProfile() {
        Trainer stored = profile(42L);
        when(dao.findById(42L)).thenReturn(Optional.of(stored));

        assertSame(stored, service.findById(42L));
        verify(dao).findById(42L);
    }

    @Test
    public void findByIdThrowsADescriptiveExceptionForMissingProfile() {
        when(dao.findById(42L)).thenReturn(Optional.empty());

        TrainerNotFoundException failure =
                assertThrows(TrainerNotFoundException.class, () -> service.findById(42L));
        assertEquals("Trainer with ID 42 does not exist.", failure.getMessage());
    }

    @Test
    public void findAllReturnsTheDaoProfilesIncludingAnEmptyResult() {
        List<Trainer> profiles = List.of(profile(42L));
        when(dao.findAll()).thenReturn(profiles, List.of());

        assertSame(profiles, service.findAll());
        assertTrue(service.findAll().isEmpty());
    }

    private Trainer draft() {
        return new Trainer("Alex", "Smith", true, TrainingType.YOGA);
    }

    private Trainer profile(Long id) {
        return new Trainer(id, "Alex", "Smith", "Alex.Smith", "Abcdef1234", true,
                TrainingType.YOGA);
    }
}
