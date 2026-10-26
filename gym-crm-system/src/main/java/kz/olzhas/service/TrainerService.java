package kz.olzhas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kz.olzhas.dao.TrainerDao;
import kz.olzhas.dao.TrainingTypeDao;
import kz.olzhas.exceptions.ValidationException;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.TrainingType;
import kz.olzhas.util.UserRegistry;
import kz.olzhas.util.Validator;

@Service
@Transactional
public class TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final AuthenticationService authenticationService;
    private final UserRegistry userRegistry;

    public TrainerService(TrainerDao trainerDao, TrainingTypeDao trainingTypeDao,
            AuthenticationService authenticationService, UserRegistry userRegistry) {
        this.trainerDao = trainerDao;
        this.trainingTypeDao = trainingTypeDao;
        this.authenticationService = authenticationService;
        this.userRegistry = userRegistry;
    }

    public Trainer create(String firstName, String lastName, String specialization) {
        String first = Validator.requireText(firstName, "First name");
        String last = Validator.requireText(lastName, "Last name");
        TrainingType type = findType(Validator.requireText(specialization, "Specialization"));

        Trainer trainer = new Trainer(first, last, true, type);
        trainer.assignCredentials(
                userRegistry.generateUsername(first, last), userRegistry.generatePassword());
        Trainer created = trainerDao.create(trainer);
        LOGGER.info("Created trainer with username: {}", created.getUsername());

        return created;
    }

    @Transactional(readOnly = true)
    public boolean matchCredentials(String username, String password) {
        return authenticationService.matchTrainer(username, password);
    }

    @Transactional(readOnly = true)
    public Trainer findByUsername(String username, String password) {
        return authenticationService.authenticateTrainer(username, password);
    }

    public void changePassword(String username, String password, String newPassword) {
        Trainer trainer = authenticationService.authenticateTrainer(username, password);
        trainer.changePassword(Validator.requireText(newPassword, "New password"));
        trainerDao.update(trainer);
        LOGGER.info("Changed password of trainer: {}", username);
    }

    public Trainer update(String username, String password, String firstName, String lastName,
            String specialization) {
        Trainer trainer = authenticationService.authenticateTrainer(username, password);
        trainer.updateProfile(Validator.requireText(firstName, "First name"),
                Validator.requireText(lastName, "Last name"),
                findType(Validator.requireText(specialization, "Specialization")));
        Trainer updated = trainerDao.update(trainer);
        LOGGER.info("Updated trainer: {}", username);

        return updated;
    }

    public boolean toggleActive(String username, String password) {
        Trainer trainer = authenticationService.authenticateTrainer(username, password);
        trainer.toggleActive();
        trainerDao.update(trainer);
        LOGGER.info("Trainer {} is now {}", username, trainer.isActive() ? "active" : "inactive");

        return trainer.isActive();
    }

    private TrainingType findType(String name) {
        return trainingTypeDao.findByName(name).orElseThrow(() -> {
            LOGGER.warn("Unknown specialization: {}", name);

            return new ValidationException("Unknown specialization: " + name);
        });
    }
}
