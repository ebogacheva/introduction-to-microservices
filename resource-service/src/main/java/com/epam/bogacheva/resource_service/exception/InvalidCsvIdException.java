package com.epam.bogacheva.resource_service.exception;

public class InvalidCsvIdException extends BadRequestException {

    public InvalidCsvIdException(String value) {
        super("Invalid ID format: '%s'. Only positive integers are allowed".formatted(value));
    }
}
