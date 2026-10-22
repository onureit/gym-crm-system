package kz.olzhas.dao.impl;

import kz.olzhas.dao.AbstractInMemoryDao;
import kz.olzhas.dao.TrainingDao;
import kz.olzhas.model.Training;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InMemoryTrainingDao extends AbstractInMemoryDao<Training> implements TrainingDao {
    public InMemoryTrainingDao() {
        super("Training", Training::getId, Training::setId);
    }

    @Override
    @Autowired
    public void setStorage(@Qualifier("trainingStorage") Map<Long, Training> storage) {
        super.setStorage(storage);
    }
}
