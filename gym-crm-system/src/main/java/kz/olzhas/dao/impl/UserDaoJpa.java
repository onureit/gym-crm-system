package kz.olzhas.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import kz.olzhas.dao.UserDao;

@Repository
public class UserDaoJpa implements UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean existsByUsername(String username) {
        String query = "SELECT COUNT(u) FROM User u WHERE u.username = :username";
        Long count = entityManager.createQuery(query, Long.class)
                .setParameter("username", username)
                .getSingleResult();

        return count > 0;
    }
}
