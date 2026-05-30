package com.footballbooking.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class LoginPage extends BasePage {
    private final String baseUrl;

    private static final List<By> USERNAME_INPUTS = List.of(
            By.id("username"),
            By.name("username"),
            By.cssSelector("input[name='username']")
    );

    private static final List<By> PASSWORD_INPUTS = List.of(
            By.id("password"),
            By.name("password"),
            By.cssSelector("input[name='password']")
    );

    private static final List<By> SUBMIT_BUTTONS = List.of(
            By.cssSelector("button[type='submit']"),
            By.cssSelector("form button.btn-primary"),
            By.cssSelector("input[type='submit']")
    );

    private static final By ERROR_ALERT = By.cssSelector(".alert.alert-danger");
    private static final By SUCCESS_ALERT = By.cssSelector(".alert.alert-success");
    private static final By LOGIN_FORM = By.cssSelector("form");
    private static final By LOGIN_PAGE_ANCHOR = By.cssSelector("input#username,input[name='username']");
    private static final By USER_PAGE_ANCHOR = By.cssSelector("#stadiumList,#areaFilter,#fieldTypeFilter");
    private static final By ADMIN_PAGE_ANCHOR = By.cssSelector("a[href*='/admin/bookings'],a[href*='/admin/stadiums'],h2");

    public enum LoginState {
        USER_HOME,
        ADMIN_HOME,
        LOGIN_ERROR,
        UNKNOWN
    }

    public LoginPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    public LoginPage openLoginPage() {
        open(baseUrl + "/login");
        waitVisible(firstVisible(USERNAME_INPUTS));
        return this;
    }

    public LoginPage login(String username, String password) {
        type(firstVisible(USERNAME_INPUTS), username);
        type(firstVisible(PASSWORD_INPUTS), password);
        click(firstClickable(SUBMIT_BUTTONS));
        return this;
    }

    public LoginState waitForStableLoginState() {
        return wait.until(d -> {
            if (isAtUserArea()) {
                return LoginState.USER_HOME;
            }
            if (isAtAdminArea()) {
                return LoginState.ADMIN_HOME;
            }
            if (isAtLoginPage() && hasErrorMessage()) {
                return LoginState.LOGIN_ERROR;
            }
            return null;
        });
    }

    public boolean isAtUserArea() {
        return currentUrl().contains("/stadiums") && isPresent(USER_PAGE_ANCHOR);
    }

    public boolean isAtAdminArea() {
        return currentUrl().contains("/admin") && isPresent(ADMIN_PAGE_ANCHOR);
    }

    public boolean isAtLoginPage() {
        return currentUrl().contains("/login") && isPresent(LOGIN_PAGE_ANCHOR);
    }

    public boolean hasErrorMessage() {
        return isPresent(ERROR_ALERT) || isPresent(By.cssSelector(".text-danger")) || isPresent(By.cssSelector(".invalid-feedback"));
    }

    public String getErrorMessage() {
        List<By> candidates = List.of(
                ERROR_ALERT,
                By.cssSelector(".text-danger"),
                By.cssSelector(".invalid-feedback")
        );

        for (By by : candidates) {
            if (isPresent(by)) {
                try {
                    return text(by);
                } catch (Exception ignored) {
                }
            }
        }
        return "";
    }

    public String getSuccessMessage() {
        if (isPresent(SUCCESS_ALERT)) {
            return text(SUCCESS_ALERT);
        }
        return "";
    }

    public boolean isLoginFormVisible() {
        return isPresent(LOGIN_FORM);
    }
}