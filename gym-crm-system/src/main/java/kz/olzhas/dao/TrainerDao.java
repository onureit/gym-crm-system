package kz.olzhas.dao;

import kz.olzhas.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    Trainer create(Trainer trainer);

    boolean update(Trainer trainer);

    Optional<Trainer> findById(long id);

    List<Trainer> findAll();
}
