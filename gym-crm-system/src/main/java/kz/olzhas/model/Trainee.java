package kz.olzhas.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
@Table(name = "trainee")
public class Trainee{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "date_of_birth", nullable = true)
    private LocalDate dateOfBirth;
    
    @Column (name = "address", nullable = true)
    private String address;

    @OneToOne
    @JoinColumn (name = "user_id")
    private User user;

    public Trainee() {
    }
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
