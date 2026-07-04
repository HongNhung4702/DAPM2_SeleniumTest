package com.footballbooking.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPERTIES = loadProperties();

    private ConfigReader() {
    }

    private static Properties loadProperties() {
        String environment = EnvironmentManager.getEnvironment();
        String configPath = "config/" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(configPath)) {
            if (input == null) {
                throw new IllegalStateException(
                        "Cannot find environment config: " + configPath
                                + ". Supported environments: test, prod. Set -Denv=test or -Denv=prod.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load environment config: " + configPath, e);
        }

        System.out.println("[ConfigReader] Loaded environment '" + environment + "' from " + configPath
                + " | base.url=" + resolveProperty(properties, "base.url", null));

        return properties;
    }

    public static String get(String key) {
        return resolveProperty(PROPERTIES, key, null);
    }

    public static String get(String key, String defaultValue) {
        String value = resolveProperty(PROPERTIES, key, null);
        return value != null ? value : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static String resolveProperty(Properties properties, String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        String propertyValue = properties.getProperty(key);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }
        return defaultValue;
    }
}
