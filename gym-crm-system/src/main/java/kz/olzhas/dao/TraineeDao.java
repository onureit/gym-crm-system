package kz.olzhas.dao;

import kz.olzhas.model.Trainee;

import java.util.Optional;

public interface TraineeDao {
    Trainee create(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Trainee trainee);

    Optional<Trainee> findByUsername(String username);
}
