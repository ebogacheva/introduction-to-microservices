package com.epam.bogacheva.song_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DurationValidator implements ConstraintValidator<ValidDuration, String> {

    private static final String SEPARATOR = ":";
    private static final int PARTS_COUNT = 2;
    private static final int PART_LENGTH = 2;
    private static final int MAX_SECONDS = 59;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String[] parts = value.split(SEPARATOR, -1);
        if (parts.length != PARTS_COUNT || !isTwoDigits(parts[0]) || !isTwoDigits(parts[1])) {
            return false;
        }
        int seconds = Integer.parseInt(parts[1]);
        return seconds <= MAX_SECONDS;
    }

    private boolean isTwoDigits(String part) {
        return part.length() == PART_LENGTH && ValidationUtils.isDigitsOnly(part);
    }
}
