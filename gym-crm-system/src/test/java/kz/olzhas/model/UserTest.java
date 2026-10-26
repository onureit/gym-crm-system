package kz.olzhas.model;

import kz.olzhas.TestTypes;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void credentialsCanBeAssignedOnlyOnce() {
        Trainee trainee = draft();

        trainee.assignCredentials("Alex.Smith", "Abcdef1234");

        assertEquals("Alex.Smith", trainee.getUsername());
        assertEquals("Abcdef1234", trainee.getPassword());
        assertThrows(IllegalStateException.class,
                () -> trainee.assignCredentials("Other", "Other12345"));
    }

    @Test
    public void traineeUpdateProfileKeepsIdentityAndCredentials() {
        Trainee trainee = new Trainee(5L, "Alex", "Smith", "Alex.Smith", "Abcdef1234", true, null, "Old");

        trainee.updateProfile("Alicia", "Brown", LocalDate.of(2001, 2, 3), "New");

        assertEquals("Alicia", trainee.getFirstName());
        assertEquals("Brown", trainee.getLastName());
        assertEquals(LocalDate.of(2001, 2, 3), trainee.getDateOfBirth());
        assertEquals("New", trainee.getAddress());
        assertEquals(Long.valueOf(5), trainee.getId());
        assertEquals("Alex.Smith", trainee.getUsername());
        assertEquals("Abcdef1234", trainee.getPassword());
    }

    @Test
    public void trainerUpdateProfileChangesSpecialization() {
        Trainer trainer = new Trainer("Alex", "Smith", true, TestTypes.YOGA);

        trainer.updateProfile("Alex", "Brown", TestTypes.AEROBICA);

        assertEquals("Brown", trainer.getLastName());
        assertEquals(TestTypes.AEROBICA, trainer.getSpecialization());
    }

    @Test
    public void toggleActiveFlipsTheFlagEveryTime() {
        Trainee trainee = draft();

        trainee.toggleActive();
        assertFalse(trainee.isActive());
        trainee.toggleActive();
        assertTrue(trainee.isActive());
    }

    @Test
    public void changePasswordReplacesThePassword() {
        Trainee trainee = new Trainee(5L, "Alex", "Smith", "Alex.Smith", "Abcdef1234", true, null, null);

        trainee.changePassword("NewPass5678");

        assertEquals("NewPass5678", trainee.getPassword());
        assertThrows(NullPointerException.class, () -> trainee.changePassword(null));
    }

    @Test
    public void replaceTrainersKeepsBothSidesInSync() {
        Trainee trainee = draft();
        Trainer first = new Trainer("Jane", "Doe", true, TestTypes.YOGA);
        Trainer second = new Trainer("John", "Roe", true, TestTypes.LIFTING);
        trainee.addTrainer(first);

        trainee.replaceTrainers(java.util.Set.of(second));

        assertEquals(java.util.Set.of(second), trainee.getTrainers());
        assertTrue(first.getTrainees().isEmpty());
        assertTrue(second.getTrainees().contains(trainee));
    }

    private Trainee draft() {
        return new Trainee("Alex", "Smith", true, null, null);
    }
}
