package kz.olzhas.model;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void idCanBeAssignedOnlyOnce() {
        Trainee trainee = draft();

        trainee.assignId(5L);

        assertEquals(Long.valueOf(5), trainee.getId());
        assertThrows(IllegalStateException.class, () -> trainee.assignId(6L));
    }

    @Test
    public void idMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> draft().assignId(0L));
    }

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
        Trainer trainer = new Trainer("Alex", "Smith", true, TrainingType.YOGA);

        trainer.updateProfile("Alex", "Brown", TrainingType.AEROBICA);

        assertEquals("Brown", trainer.getLastName());
        assertEquals(TrainingType.AEROBICA, trainer.getSpecialization());
    }

    @Test
    public void activationCanBeToggled() {
        Trainee trainee = draft();

        trainee.deactivate();
        assertFalse(trainee.isActive());
        trainee.activate();
        assertTrue(trainee.isActive());
    }

    private Trainee draft() {
        return new Trainee("Alex", "Smith", true, null, null);
    }
}
