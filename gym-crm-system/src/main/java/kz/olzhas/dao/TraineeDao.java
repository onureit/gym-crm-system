package kz.olzhas.dao;

import kz.olzhas.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {
    Trainee create(Trainee trainee);

    boolean update(Trainee trainee);

    void delete(long id);

    Optional<Trainee> findById(long id);

    List<Trainee> findAll();
}
