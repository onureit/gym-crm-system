package kz.olzhas.dao.impl;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import kz.olzhas.dao.TrainerDao;
import kz.olzhas.model.Trainer;

@Repository
public class TrainerDaoJpa implements TrainerDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainer create(Trainer trainer) {
        entityManager.persist(trainer);

        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        return entityManager.merge(trainer);
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        String query = "SELECT t FROM Trainer t JOIN FETCH t.user u WHERE u.username = :username";

        return entityManager.createQuery(query, Trainer.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Trainer> findNotAssignedToTrainee(String traineeUsername) {
        String query = "SELECT t FROM Trainer t JOIN FETCH t.user "
                + "WHERE t.id NOT IN ("
                + "SELECT tr.id FROM Trainee te JOIN te.trainers tr JOIN te.user u "
                + "WHERE u.username = :username)";

        return entityManager.createQuery(query, Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }
}
