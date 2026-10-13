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
public class TrainerService {
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

    public Trainer create(Trainer trainer) {
        String baseUsername = trainer.getFirstName() + "." + trainer.getLastName();
        String username = baseUsername;
        int suffix = 1;
        while (usernameExists(username)) {
            username = baseUsername + suffix++;
        }
        trainer.setUsername(username);
        trainer.setPassword(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        return trainerDao.create(trainer);
    }

    public boolean update(Trainer trainer) {
        return trainerDao.update(trainer);
    }

    public Trainer findById(long id) {
        return trainerDao.findById(id);
    }

    public List<Trainer> findAll() {
        return trainerDao.findAll();
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
