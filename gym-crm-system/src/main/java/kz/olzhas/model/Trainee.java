package kz.olzhas.model;

import java.time.LocalDate;

public final class Trainee extends User {

    private LocalDate dateOfBirth;
    private String address;

    public Trainee(
            String firstName,
            String lastName,
            boolean active,
            LocalDate dateOfBirth,
            String address) {

        super(firstName, lastName, active);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public Trainee(
            Long id,
            String firstName,
            String lastName,
            String username,
            String password,
            boolean active,
            LocalDate dateOfBirth,
            String address) {

        super(id, firstName, lastName, username, password, active);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public void updateProfile(
            String firstName, String lastName, LocalDate dateOfBirth, String address) {
        rename(firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }
}
