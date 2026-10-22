package kz.olzhas.util;

import java.security.SecureRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kz.olzhas.dao.TraineeDao;
import kz.olzhas.dao.TrainerDao;
import kz.olzhas.model.User;

@Component
public class UserRegistry {
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    private final SecureRandom random = new SecureRandom();

    @Autowired
    public UserRegistry(TrainerDao trainerDao, TraineeDao traineeDao) {
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
    }

    public String generatePassword() {
        return random.ints(PASSWORD_LENGTH, 0, ALPHABET.length())
                .mapToObj(i -> String.valueOf(ALPHABET.charAt(i)))
                .collect(Collectors.joining());
    }

    public String generateUserName(User user) {
        String baseUserName = user.getFirstName() + "." + user.getLastName();
        String username = baseUserName;
        int suffix = 1;

        while (usernameExists(username)) {
            username = baseUserName + suffix++;
        }

        return username;
    }

    public boolean usernameExists(String username) {
        return traineeDao.findAll().stream().anyMatch(t -> t.getUsername().equals(username)) ||
                trainerDao.findAll().stream().anyMatch(t -> t.getUsername().equals(username));
    }

}
