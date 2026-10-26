package kz.olzhas.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "trainer")
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "specialization", nullable = false)
    private TrainingType specialization;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany(mappedBy = "trainers")
    private Set<Trainee> trainees = new HashSet<>();

    protected Trainer() {
    }

    public Trainer(String firstName, String lastName, boolean active, TrainingType specialization) {
        this.user = new User(firstName, lastName, active);
        this.specialization = specialization;
    }

    public Trainer(
            Long id,
            String firstName,
            String lastName,
            String username,
            String password,
            boolean active,
            TrainingType specialization) {
        this.id = id;
        this.user = new User(null, firstName, lastName, username, password, active);
        this.specialization = specialization;
    }

    public void updateProfile(String firstName, String lastName, TrainingType specialization) {
        user.rename(firstName, lastName);
        this.specialization = specialization;
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

    public TrainingType getSpecialization() {
        return specialization;
    }

    public Set<Trainee> getTrainees() {
        return trainees;
    }
}
