package com.footballbooking.config;

public final class EnvironmentManager {

    public static final String ENV_PROPERTY = "env";
    public static final String DEFAULT_ENV = "test";

    private EnvironmentManager() {
    }

    public static String getEnvironment() {
        String env = System.getProperty(ENV_PROPERTY);
        if (env == null || env.isBlank()) {
            env = System.getenv("ENV");
        }
        if (env == null || env.isBlank()) {
            return DEFAULT_ENV;
        }
        return env.trim().toLowerCase();
    }

    public static boolean isTest() {
        return DEFAULT_ENV.equals(getEnvironment());
    }

    public static boolean isProd() {
        return "prod".equals(getEnvironment());
    }
}
