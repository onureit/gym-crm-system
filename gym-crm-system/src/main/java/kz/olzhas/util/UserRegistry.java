package kz.olzhas.util;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserRegistry {
    private static final String PASSWORD_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;

    public UserRegistry(TrainerDao trainerDao, TraineeDao traineeDao) {
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
    }

    public String generateUsername(String firstName, String lastName) {
        String base = Objects.requireNonNull(firstName, "firstName")
                + "." + Objects.requireNonNull(lastName, "lastName");
        String candidate = base;
        int suffix = 1;
        while (usernameExists(candidate)) {
            candidate = base + suffix++;
        }
        return candidate;
    }

    public String generatePassword() {
        return RANDOM.ints(PASSWORD_LENGTH, 0, PASSWORD_ALPHABET.length())
                .mapToObj(index -> String.valueOf(PASSWORD_ALPHABET.charAt(index)))
                .collect(Collectors.joining());
    }

    public boolean usernameExists(String username) {
        return traineeDao.findAll().stream().anyMatch(t -> Objects.equals(t.getUsername(), username))
                || trainerDao.findAll().stream().anyMatch(t -> Objects.equals(t.getUsername(), username));
    }
}
