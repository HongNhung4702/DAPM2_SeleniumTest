package com.footballbooking.utils;

import org.openqa.selenium.WebDriver;

public final class DebugUtils {
    private DebugUtils() {
    }

    public static String describe(WebDriver driver) {
        if (driver == null) {
            return "driver=null";
        }
        String url = safe(driver::getCurrentUrl);
        String title = safe(driver::getTitle);
        String pageSourceSnippet = safe(() -> {
            String src = driver.getPageSource();
            if (src == null) {
                return "";
            }
            return src.length() > 500 ? src.substring(0, 500) + "...(truncated)" : src;
        });
        return "currentUrl=" + url + " | title=" + title + " | pageSourceSnippet=" + pageSourceSnippet;
    }

    private static String safe(SupplierEx<String> supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            return "n/a(" + ex.getClass().getSimpleName() + ")";
        }
    }

    @FunctionalInterface
    private interface SupplierEx<T> {
        T get();
    }
}
