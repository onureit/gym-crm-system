package kz.olzhas.util;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.TrainingType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class UserRegistryTest {
    @Mock private TraineeDao traineeDao;
    @Mock private TrainerDao trainerDao;
    private UserRegistry registry;

    @Before
    public void setUp() {
        registry = new UserRegistry(trainerDao, traineeDao);
    }

    @Test
    public void usernameIsFirstNameDotLastName() {
        assertEquals("Alex.Smith", registry.generateUsername("Alex", "Smith"));
    }

    @Test
    public void usernameSkipsCollisionsInBothDaos() {
        when(traineeDao.findAll()).thenReturn(List.of(trainee("Alex.Smith")));
        when(trainerDao.findAll()).thenReturn(List.of(trainer("Alex.Smith1")));

        assertEquals("Alex.Smith2", registry.generateUsername("Alex", "Smith"));
    }

    @Test
    public void usernameRequiresBothNames() {
        assertThrows(NullPointerException.class, () -> registry.generateUsername(null, "Smith"));
        assertThrows(NullPointerException.class, () -> registry.generateUsername("Alex", null));
        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    public void passwordHasTenAlphanumericCharacters() {
        assertTrue(registry.generatePassword().matches("[A-Za-z0-9]{10}"));
    }

    private Trainee trainee(String username) {
        return new Trainee(1L, "Alex", "Smith", username, "Pass123456", true, null, null);
    }

    private Trainer trainer(String username) {
        return new Trainer(2L, "Alex", "Smith", username, "Pass123456", true, TrainingType.YOGA);
    }
}
