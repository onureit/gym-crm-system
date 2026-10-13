package kz.olzhas.service;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TraineeService {
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public Trainee create(Trainee trainee) {
        String baseUsername = trainee.getFirstName() + "." + trainee.getLastName();
        String username = baseUsername;
        int suffix = 1;
        while (usernameExists(username)) {
            username = baseUsername + suffix++;
        }
        trainee.setUsername(username);
        trainee.setPassword(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        return traineeDao.create(trainee);
    }

    public boolean update(Trainee trainee) {
        return traineeDao.update(trainee);
    }

    public boolean delete(long id) {
        return traineeDao.delete(id);
    }

    public Trainee findById(long id) {
        return traineeDao.findById(id);
    }

    public List<Trainee> findAll() {
        return traineeDao.findAll();
    }

    private boolean usernameExists(String username) {
        for (Trainee trainee : traineeDao.findAll()) {
            if (username.equals(trainee.getUsername())) {
                return true;
            }
        }
        for (Trainer trainer : trainerDao.findAll()) {
            if (username.equals(trainer.getUsername())) {
                return true;
            }
        }
        return false;
    }
}
