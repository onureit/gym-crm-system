package kz.olzhas.dao.impl;

import kz.olzhas.dao.AbstractInMemoryDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.exceptions.TrainerNotFoundException;
import kz.olzhas.model.Trainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InMemoryTrainerDao extends AbstractInMemoryDao<Trainer> implements TrainerDao {
    public InMemoryTrainerDao() {
        super("Trainer", Trainer::getId, Trainer::assignId);
    }

    @Override
    @Autowired
    public void setStorage(@Qualifier("trainerStorage") Map<Long, Trainer> storage) {
        super.setStorage(storage);
    }

    @Override
    public boolean update(Trainer trainer) {
        return updateEntity(trainer, TrainerNotFoundException::new);
    }
}
