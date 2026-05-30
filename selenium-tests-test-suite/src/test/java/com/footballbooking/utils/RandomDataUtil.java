package com.footballbooking.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class RandomDataUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private RandomDataUtil() {
    }

    public static String randomUsername(String prefix) {
        return prefix + "_" + shortId();
    }

    public static String randomEmail(String prefix) {
        return prefix + "_" + shortId() + "@mail.com";
    }

    public static String randomStadiumName() {
        return "Selenium Stadium " + shortId();
    }

    public static String dateAfterDays(int days) {
        return LocalDate.now().plusDays(days).format(DATE_FORMATTER);
    }

    private static String shortId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}