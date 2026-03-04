package com.example.bankcards.dto.card.admin.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LuhnValidator implements ConstraintValidator<ValidPan, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !value.matches("\\d{16}")) {
            return false;
        }

        return isValidLuhn(value);
    }

    private boolean isValidLuhn(String pan) {
        int sum = 0;
        boolean alternate = false;

        for (int i = pan.length() - 1; i >= 0; i--) {
            int n = pan.charAt(i) - '0';

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }

            sum += n;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }
}
