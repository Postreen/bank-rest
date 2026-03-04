package com.example.bankcards.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class LogAuthUtil {

    private LogAuthUtil() {
    }

    public static String principal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "anonymous";
        return String.valueOf(auth.getName());
    }
}
