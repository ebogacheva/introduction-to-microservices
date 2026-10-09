package com.epam.bogacheva.song_service.exception;

import com.epam.bogacheva.song_service.model.dto.ErrorDto;
import com.epam.bogacheva.song_service.model.dto.ValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequest(BadRequestException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SongNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(SongNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SongAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleAlreadyExists(SongAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (first, second) -> first));
        ValidationErrorDto body = new ValidationErrorDto(
                "Validation error", details, errorCode(HttpStatus.BAD_REQUEST));
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleNotReadable(HttpMessageNotReadableException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Request body is missing or malformed");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorDto> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        String contentType = Optional.ofNullable(ex.getContentType())
                .map(MediaType::toString)
                .orElse("unknown");
        return buildError(HttpStatus.BAD_REQUEST,
                "Invalid content type: %s. Only application/json is allowed".formatted(contentType));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> handleMissingParameter(MissingServletRequestParameterException ex) {
        return buildError(HttpStatus.BAD_REQUEST,
                "Required parameter '%s' is missing".formatted(ex.getParameterName()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error has occurred");
    }

    private ResponseEntity<ErrorDto> buildError(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorDto(message, errorCode(status)));
    }

    private String errorCode(HttpStatus status) {
        return String.valueOf(status.value());
    }
}
