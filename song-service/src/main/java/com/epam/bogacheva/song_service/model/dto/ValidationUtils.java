package com.epam.bogacheva.song_service.model.dto;

final class ValidationUtils {

    private ValidationUtils() {
    }

    static boolean isDigitsOnly(String value) {
        return value.chars().allMatch(Character::isDigit);
    }
}