package kz.olzhas.model;

import javax.annotation.processing.Generated;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table (name = "trainer")
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (name = "specialization_id")
    private TrainingType trainingType;

    @OneToOne
    @JoinColumn (name = "user_id", nullable = false, unique = true)
    private User user;

    public Trainer(String firstName, String lastName, boolean active, TrainingType specialization) {
        super(firstName, lastName, active);
        this.trainingType = specialization;
    }

    public Trainer(
            Long id,
            String firstName,
            String lastName,
            String username,
            String password,
            boolean active,
            TrainingType specialization) {

        super(id, firstName, lastName, username, password, active);
        this.specialization = specialization;
    }

    public void updateProfile(String firstName, String lastName, TrainingType specialization) {
        rename(firstName, lastName);
        this.specialization = specialization;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }
}
