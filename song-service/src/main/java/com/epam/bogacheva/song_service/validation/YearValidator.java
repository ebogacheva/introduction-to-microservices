package com.epam.bogacheva.song_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YearValidator implements ConstraintValidator<ValidYear, String> {

    private static final int YEAR_LENGTH = 4;
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2099;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.length() != YEAR_LENGTH || !ValidationUtils.isDigitsOnly(value)) {
            return false;
        }
        int year = Integer.parseInt(value);
        return year >= MIN_YEAR && year <= MAX_YEAR;
    }
}
