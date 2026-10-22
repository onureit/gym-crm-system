package kz.olzhas.model;


public final class Trainer extends User {

    private TrainingType specialization;

    public Trainer(String firstName, String lastName, boolean active, TrainingType specialization) {
        super(firstName, lastName, active);
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
