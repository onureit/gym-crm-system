package kz.olzhas.dao.impl;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.model.Trainee;

@Repository
public class TraineeDaoJpa implements TraineeDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainee create(Trainee trainee) {
        entityManager.persist(trainee);

        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        return entityManager.merge(trainee);
    }

    @Override
    public void delete(Trainee trainee) {
        Trainee managed = entityManager.contains(trainee) ? trainee : entityManager.merge(trainee);
        entityManager.remove(managed);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        String query = "SELECT t FROM Trainee t JOIN FETCH t.user u WHERE u.username = :username";

        return entityManager.createQuery(query, Trainee.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
}
