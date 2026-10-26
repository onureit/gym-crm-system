package kz.olzhas.dao;

import kz.olzhas.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    Trainer create(Trainer trainer);

    Trainer update(Trainer trainer);

    Optional<Trainer> findByUsername(String username);

    List<Trainer> findNotAssignedToTrainee(String traineeUsername);
}
