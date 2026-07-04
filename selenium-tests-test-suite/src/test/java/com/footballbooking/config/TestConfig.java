package com.footballbooking.config;

public final class TestConfig {

    private TestConfig() {
    }

    public static String getEnvironment() {
        return EnvironmentManager.getEnvironment();
    }

    public static String get(String key) {
        return ConfigReader.get(key);
    }

    public static String get(String key, String defaultValue) {
        return ConfigReader.get(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return ConfigReader.getInt(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return ConfigReader.getBoolean(key, defaultValue);
    }
}
