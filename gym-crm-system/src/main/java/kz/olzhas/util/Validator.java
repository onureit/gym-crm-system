package kz.olzhas.util;

import kz.olzhas.exceptions.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Validator.class);

    private Validator() {
    }

    public static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw fail(field + " is required.");
        }

        return value.trim();
    }

    public static <T> T requireNotNull(T value, String field) {
        if (value == null) {
            throw fail(field + " is required.");
        }

        return value;
    }

    public static int requirePositive(Integer value, String field) {
        if (value == null || value <= 0) {
            throw fail(field + " must be a positive number.");
        }

        return value;
    }

    private static ValidationException fail(String message) {
        LOGGER.warn("Validation failed: {}", message);

        return new ValidationException(message);
    }
}
