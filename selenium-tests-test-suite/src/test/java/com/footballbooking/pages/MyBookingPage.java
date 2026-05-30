package com.footballbooking.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class MyBookingPage extends BasePage {
    private final String baseUrl;

    private static final By BOOKING_TABLE = By.cssSelector("table.table");
    private static final By BOOKING_ROWS = By.cssSelector("table tbody tr");
    private static final By CANCEL_BUTTONS = By.cssSelector(".cancel-btn");
    private static final By CANCEL_MODAL = By.id("cancelModal");
    private static final By CONFIRM_CANCEL_BUTTON = By.cssSelector("#cancelForm button[type='submit']");
    private static final By PAGE_TITLE = By.cssSelector("h2");
    private static final By ALERT = By.cssSelector(".alert, .toast, .text-success, .text-danger");

    public MyBookingPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    public MyBookingPage open() {
        open(baseUrl + "/my-bookings");
        waitVisible(PAGE_TITLE);
        return this;
    }

    public boolean hasBookingTable() {
        return isPresent(BOOKING_TABLE);
    }

    public int getBookingRowCount() {
        List<WebElement> rows = driver.findElements(BOOKING_ROWS);
        return rows.size();
    }

    public boolean hasCancelableBooking() {
        return !driver.findElements(CANCEL_BUTTONS).isEmpty();
    }

    public MyBookingPage cancelFirstBookingViaModal() {
        List<WebElement> buttons = waitVisibleAll(CANCEL_BUTTONS);
        buttons.get(0).click();
        waitVisible(CANCEL_MODAL);
        click(CONFIRM_CANCEL_BUTTON);
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/my-bookings"),
                ExpectedConditions.invisibilityOfElementLocated(CANCEL_MODAL),
                ExpectedConditions.visibilityOfElementLocated(ALERT)
        ));
        return this;
    }

    public String getFeedbackMessage() {
        List<WebElement> alerts = driver.findElements(ALERT);
        if (alerts.isEmpty()) {
            return "";
        }
        return alerts.get(0).getText().trim();
    }
}
