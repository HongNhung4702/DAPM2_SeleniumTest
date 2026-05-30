package com.footballbooking.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtils() {
    }

    public static String capture(WebDriver driver, String testName) {
        if (driver == null) {
            return null;
        }
        if (!(driver instanceof TakesScreenshot)) {
            return null;
        }
        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        try {
            Path outputDir = Path.of("target", "screenshots");
            Files.createDirectories(outputDir);
            String safeName = sanitize(testName);
            Path outputPath = outputDir.resolve(safeName + "-" + LocalDateTime.now().format(TS_FORMAT) + ".png");
            Path tempFile = screenshotDriver.getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(tempFile, outputPath, StandardCopyOption.REPLACE_EXISTING);
            return outputPath.toAbsolutePath().toString();
        } catch (IOException e) {
            return null;
        }
    }

    private static String sanitize(String input) {
        if (input == null || input.isBlank()) {
            return "test";
        }
        return input.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
