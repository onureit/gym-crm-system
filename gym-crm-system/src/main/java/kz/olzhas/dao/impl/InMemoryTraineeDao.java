package kz.olzhas.dao.impl;

import kz.olzhas.dao.AbstractInMemoryDao;
import kz.olzhas.dao.TraineeDao;
import kz.olzhas.exceptions.TraineeNotFoundException;
import kz.olzhas.model.Trainee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InMemoryTraineeDao extends AbstractInMemoryDao<Trainee> implements TraineeDao {
    public InMemoryTraineeDao() {
        super("Trainee", Trainee::getId, Trainee::setId);
    }

    @Override
    @Autowired
    public void setStorage(@Qualifier("traineeStorage") Map<Long, Trainee> storage) {
        super.setStorage(storage);
    }

    @Override
    public boolean update(Trainee trainee) {
        return updateEntity(trainee, TraineeNotFoundException::new);
    }

    @Override
    public void delete(long id) {
        deleteEntity(id, TraineeNotFoundException::new);
    }
}
