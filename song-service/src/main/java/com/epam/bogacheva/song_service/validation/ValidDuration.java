package com.epam.bogacheva.song_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DurationValidator.class)
public @interface ValidDuration {

    String message() default "Duration must be in mm:ss format with leading zeros";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
