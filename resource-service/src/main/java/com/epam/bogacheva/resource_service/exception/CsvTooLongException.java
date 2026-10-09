package com.epam.bogacheva.resource_service.exception;

public class CsvTooLongException extends BadRequestException {

    public CsvTooLongException(int length, int maxLength) {
        super("CSV string is too long: received %d characters, maximum allowed is %d".formatted(length, maxLength));
    }
}
