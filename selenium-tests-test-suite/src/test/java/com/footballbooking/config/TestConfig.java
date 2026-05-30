package com.footballbooking.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("Cannot find config.properties in test resources.");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private TestConfig() {
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }
}
