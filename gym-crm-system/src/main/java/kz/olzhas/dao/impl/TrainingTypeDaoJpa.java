package kz.olzhas.dao.impl;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import kz.olzhas.dao.TrainingTypeDao;
import kz.olzhas.model.TrainingType;

@Repository
public class TrainingTypeDaoJpa implements TrainingTypeDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<TrainingType> findByName(String name) {
        String query = "SELECT t FROM TrainingType t WHERE LOWER(t.name) = LOWER(:name)";

        return entityManager.createQuery(query, TrainingType.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }
}
