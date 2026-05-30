package com.footballbooking.data;

import com.footballbooking.config.TestConfig;

public final class TestAccounts {
    private TestAccounts() {
    }

    public static String adminUsername() {
        return TestConfig.get("admin.username");
    }

    public static String adminPassword() {
        return TestConfig.get("admin.password");
    }

    public static String userUsername() {
        return TestConfig.get("user.username");
    }

    public static String userPassword() {
        return TestConfig.get("user.password");
    }

    public static String existingUsername() {
        return TestConfig.get("existing.username");
    }

    public static String existingEmail() {
        return TestConfig.get("existing.email");
    }
}
