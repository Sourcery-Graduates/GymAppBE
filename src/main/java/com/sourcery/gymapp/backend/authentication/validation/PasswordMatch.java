package com.sourcery.gymapp.backend.authentication.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { PasswordMatchValidator.class })
public @interface PasswordMatch {
    String message() default "Password and confirm password do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
