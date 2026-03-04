package com.example.bankcards.dto.card.admin.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LuhnValidator.class)
public @interface ValidPan {

    String message() default "Invalid PAN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
