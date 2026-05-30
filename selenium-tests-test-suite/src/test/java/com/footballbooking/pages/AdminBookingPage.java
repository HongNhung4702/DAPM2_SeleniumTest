package com.footballbooking.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class AdminBookingPage extends BasePage {
    private final String baseUrl;

    private static final By BOOKING_TABLE = By.cssSelector("table.table");
    private static final By ROWS = By.cssSelector("table tbody tr");
    private static final By APPROVE_BUTTONS = By.cssSelector("form[action*='/admin/bookings/approve/'] button");
    private static final By REJECT_BUTTONS = By.cssSelector("form[action*='/admin/bookings/reject/'] button");
    private static final By PAGE_TITLE = By.cssSelector("h2");
    private static final By ALERT = By.cssSelector(".alert, .toast, .text-success, .text-danger");

    public AdminBookingPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    public AdminBookingPage open() {
        open(baseUrl + "/admin/bookings");
        waitVisible(PAGE_TITLE);
        return this;
    }

    public AdminBookingPage openWithStatus(String status) {
        open(baseUrl + "/admin/bookings?status=" + status);
        waitVisible(PAGE_TITLE);
        return this;
    }

    public boolean hasBookingTable() {
        return isPresent(BOOKING_TABLE);
    }

    public int getRowCount() {
        return driver.findElements(ROWS).size();
    }

    public boolean hasPendingActionButtons() {
        return !driver.findElements(APPROVE_BUTTONS).isEmpty() || !driver.findElements(REJECT_BUTTONS).isEmpty();
    }

    public AdminBookingPage approveFirstPendingBooking() {
        List<WebElement> buttons = waitVisibleAll(APPROVE_BUTTONS);
        buttons.get(0).click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(ALERT),
                ExpectedConditions.urlContains("/admin/bookings")
        ));
        return this;
    }

    public AdminBookingPage rejectFirstPendingBookingByConfirm() {
        List<WebElement> buttons = waitVisibleAll(REJECT_BUTTONS);
        buttons.get(0).click();
        acceptJsAlertIfPresent();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(ALERT),
                ExpectedConditions.urlContains("/admin/bookings")
        ));
        return this;
    }

    public List<WebElement> allRows() {
        return driver.findElements(ROWS);
    }

    public String getFeedbackMessage() {
        List<WebElement> alerts = driver.findElements(ALERT);
        if (alerts.isEmpty()) {
            return "";
        }
        return alerts.get(0).getText().trim();
    }
}
