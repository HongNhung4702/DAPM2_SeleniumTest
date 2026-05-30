package com.footballbooking.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.function.Function;

public final class WaitUtils {
    private WaitUtils() {
    }

    public static <T> T waitUntil(WebDriver driver, Duration timeout, Function<WebDriver, T> condition) {
        return new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(Duration.ofMillis(250))
                .ignoring(RuntimeException.class)
                .until(condition);
    }
}
