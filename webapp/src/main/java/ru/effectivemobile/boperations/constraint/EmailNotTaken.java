package ru.effectivemobile.boperations.constraint;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Documented
@Inherited
@Constraint(validatedBy = EmailNotTakenValidator.class)
public @interface EmailNotTaken {
    String message() default "Email is already taken";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}