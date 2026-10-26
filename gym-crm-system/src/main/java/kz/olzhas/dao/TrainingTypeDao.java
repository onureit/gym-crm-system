package kz.olzhas.dao;

import kz.olzhas.model.TrainingType;

import java.util.Optional;

public interface TrainingTypeDao {
    Optional<TrainingType> findByName(String name);
}
