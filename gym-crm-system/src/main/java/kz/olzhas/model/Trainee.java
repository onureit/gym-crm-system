package kz.olzhas.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "trainee")
public class Trainee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address")
    private String address;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    private Set<Trainer> trainers = new HashSet<>();

    protected Trainee() {
    }

    public Trainee(
            String firstName,
            String lastName,
            boolean active,
            LocalDate dateOfBirth,
            String address) {
        this.user = new User(firstName, lastName, active);
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
        this.id = id;
        this.user = new User(null, firstName, lastName, username, password, active);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public void updateProfile(
            String firstName, String lastName, LocalDate dateOfBirth, String address) {
        user.rename(firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public void assignCredentials(String username, String password) {
        user.assignCredentials(username, password);
    }

    public void changePassword(String newPassword) {
        user.changePassword(newPassword);
    }

    public void toggleActive() {
        user.toggleActive();
    }

    public void addTrainer(Trainer trainer) {
        trainers.add(trainer);
        trainer.getTrainees().add(this);
    }

    public void removeTrainer(Trainer trainer) {
        trainers.remove(trainer);
        trainer.getTrainees().remove(this);
    }

    public void replaceTrainers(Set<Trainer> newTrainers) {
        for (Trainer trainer : new HashSet<>(trainers)) {
            removeTrainer(trainer);
        }
        for (Trainer trainer : newTrainers) {
            addTrainer(trainer);
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getPassword() {
        return user.getPassword();
    }

    public boolean isActive() {
        return user.isActive();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public Set<Trainer> getTrainers() {
        return trainers;
    }
}
