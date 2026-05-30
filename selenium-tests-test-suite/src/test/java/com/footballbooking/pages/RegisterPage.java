package com.footballbooking.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class RegisterPage extends BasePage {
    private final String baseUrl;

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By FULL_NAME = By.id("fullName");
    private static final By EMAIL = By.id("email");
    private static final By PHONE = By.id("phone");
    private static final By ADDRESS = By.id("address");
    private static final By SUBMIT = By.cssSelector("button[type='submit']");
    private static final List<By> ERROR_ALERTS = List.of(
            By.cssSelector(".alert.alert-danger"),
            By.cssSelector(".text-danger"),
            By.cssSelector(".invalid-feedback")
    );

    public RegisterPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    public RegisterPage openRegisterPage() {
        open(baseUrl + "/register");
        waitVisible(USERNAME);
        return this;
    }

    public RegisterPage register(String username, String password, String fullName, String email, String phone, String address) {
        type(USERNAME, username);
        type(PASSWORD, password);
        type(FULL_NAME, fullName);
        type(EMAIL, email);
        type(PHONE, phone);
        type(ADDRESS, address);
        click(SUBMIT);
        return this;
    }

    public String getErrorMessage() {
        for (By by : ERROR_ALERTS) {
            if (isPresent(by)) {
                return text(by);
            }
        }
        return "";
    }
}