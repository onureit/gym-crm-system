package kz.olzhas.model;

public class Trainer extends User {

    private TrainingType specialization;

    public Trainer() {
        super();
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

    public TrainingType getSpecialization() {
        return specialization;
    }

    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }
}
