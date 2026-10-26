package kz.olzhas.dao.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import kz.olzhas.dao.TrainingDao;
import kz.olzhas.model.Training;

@Repository
public class TrainingDaoJpa implements TrainingDao {
    private static final String BASE_QUERY = "SELECT t FROM Training t "
            + "JOIN FETCH t.trainee te JOIN FETCH te.user teu "
            + "JOIN FETCH t.trainer tr JOIN FETCH tr.user tru "
            + "JOIN FETCH t.trainingType tt ";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Training create(Training training) {
        entityManager.persist(training);

        return training;
    }

    @Override
    public List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate,
            LocalDate toDate, String trainerName, String trainingType) {
        StringBuilder query = new StringBuilder(BASE_QUERY + "WHERE teu.username = :username");
        Map<String, Object> params = new HashMap<>();
        params.put("username", traineeUsername);
        addDateFilters(query, params, fromDate, toDate);
        if (trainerName != null && !trainerName.isBlank()) {
            query.append(" AND tru.firstName = :trainerName");
            params.put("trainerName", trainerName);
        }
        if (trainingType != null && !trainingType.isBlank()) {
            query.append(" AND tt.name = :trainingType");
            params.put("trainingType", trainingType);
        }

        return run(query, params);
    }

    @Override
    public List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate,
            LocalDate toDate, String traineeName) {
        StringBuilder query = new StringBuilder(BASE_QUERY + "WHERE tru.username = :username");
        Map<String, Object> params = new HashMap<>();
        params.put("username", trainerUsername);
        addDateFilters(query, params, fromDate, toDate);
        if (traineeName != null && !traineeName.isBlank()) {
            query.append(" AND teu.firstName = :traineeName");
            params.put("traineeName", traineeName);
        }

        return run(query, params);
    }

    private void addDateFilters(StringBuilder query, Map<String, Object> params,
            LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null) {
            query.append(" AND t.trainingDate >= :fromDate");
            params.put("fromDate", fromDate);
        }
        if (toDate != null) {
            query.append(" AND t.trainingDate <= :toDate");
            params.put("toDate", toDate);
        }
    }

    private List<Training> run(StringBuilder query, Map<String, Object> params) {
        query.append(" ORDER BY t.trainingDate");
        TypedQuery<Training> typedQuery = entityManager.createQuery(query.toString(), Training.class);
        params.forEach(typedQuery::setParameter);

        return typedQuery.getResultList();
    }
}
