package kz.olzhas.util;

import kz.olzhas.dao.UserDao;

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

    private final UserDao userDao;

    public UserRegistry(UserDao userDao) {
        this.userDao = userDao;
    }

    public String generateUsername(String firstName, String lastName) {
        String base = Objects.requireNonNull(firstName, "firstName")
                + "." + Objects.requireNonNull(lastName, "lastName");
        String candidate = base;
        int suffix = 1;
        while (userDao.existsByUsername(candidate)) {
            candidate = base + suffix++;
        }

        return candidate;
    }

    public String generatePassword() {
        return RANDOM.ints(PASSWORD_LENGTH, 0, PASSWORD_ALPHABET.length())
                .mapToObj(index -> String.valueOf(PASSWORD_ALPHABET.charAt(index)))
                .collect(Collectors.joining());
    }
}
