package com.example.bankcards.util;

public final class SensitiveDataMasker {

    private SensitiveDataMasker() {
    }

    public static String maskPan(String panOrLast4) {
        if (panOrLast4 == null || panOrLast4.isBlank()) {
            return "**** **** **** ****";
        }
        String digits = panOrLast4.replaceAll("\\D", "");
        if (digits.length() < 4) {
            return "**** **** **** ****";
        }
        String last4 = digits.substring(digits.length() - 4);
        return "**** **** **** " + last4;
    }
}
