package kz.olzhas.model;

public class Training {
    private String trainingName;
    private String trainingType;
    private String trainerUsername;
    private String traineeUsername;
    private String trainingDate;
    private int trainingDuration;

    public Training() {
    }

    public Training(String trainingName, String trainingType, String trainerUsername,
                    String traineeUsername, String trainingDate, int trainingDuration) {
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainerUsername = trainerUsername;
        this.traineeUsername = traineeUsername;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}
