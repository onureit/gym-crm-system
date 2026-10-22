package kz.olzhas.dao;

import kz.olzhas.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDao {
    Training create(Training training);

    Optional<Training> findById(long id);

    List<Training> findAll();
}
