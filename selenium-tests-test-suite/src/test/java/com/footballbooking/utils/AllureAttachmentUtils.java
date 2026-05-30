package com.footballbooking.utils;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class AllureAttachmentUtils {

    // Đính kèm ảnh chụp màn hình vào Allure Report khi test bị lỗi.
    @Attachment(value = "Screenshot khi test fail", type = "image/png")
    public static byte[] attachScreenshot(WebDriver driver) {
        if (driver == null) {
            return new byte[0];
        }

        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception ex) {
            return new byte[0];
        }
    }

    // Đính kèm URL hiện tại vào Allure Report để dễ xác định lỗi xảy ra ở trang nào.
    @Attachment(value = "Current URL", type = "text/plain")
    public static String attachCurrentUrl(WebDriver driver) {
        if (driver == null) {
            return "Driver is null";
        }

        try {
            return driver.getCurrentUrl();
        } catch (Exception ex) {
            return "Cannot get current URL: " + ex.getMessage();
        }
    }

    // Đính kèm tiêu đề trang hiện tại vào Allure Report.
    @Attachment(value = "Page Title", type = "text/plain")
    public static String attachPageTitle(WebDriver driver) {
        if (driver == null) {
            return "Driver is null";
        }

        try {
            return driver.getTitle();
        } catch (Exception ex) {
            return "Cannot get page title: " + ex.getMessage();
        }
    }
}