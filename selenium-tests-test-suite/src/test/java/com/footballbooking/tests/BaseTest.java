package com.footballbooking.tests;

import com.footballbooking.config.DriverFactory;
import com.footballbooking.config.TestConfig;
import com.footballbooking.data.TestAccounts;
import com.footballbooking.pages.LoginPage;
import com.footballbooking.pages.LoginPage.LoginState;
import com.footballbooking.pages.StadiumPage;
import com.footballbooking.utils.AllureAttachmentUtils;
import com.footballbooking.utils.DebugUtils;
import com.footballbooking.utils.ScreenshotUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

public abstract class BaseTest {
    protected WebDriver driver;
    protected String baseUrl;
    protected String userUsername;
    protected String userPassword;
    protected String adminUsername;
    protected String adminPassword;

    @RegisterExtension
    TestWatcher screenshotOnFailure = new TestWatcher() {
        @Override
        public void testFailed(org.junit.jupiter.api.extension.ExtensionContext context, Throwable cause) {
            if (driver != null) {
                try {
                    // Chụp ảnh lỗi lưu vào thư mục screenshot cũ của project.
                    ScreenshotUtils.capture(driver, context.getDisplayName());

                    // Đính kèm thông tin lỗi vào Allure Report.
                    AllureAttachmentUtils.attachScreenshot(driver);
                    AllureAttachmentUtils.attachCurrentUrl(driver);
                    AllureAttachmentUtils.attachPageTitle(driver);

                    System.err.println("Test failed diagnostics: " + DebugUtils.describe(driver));
                } catch (Exception ignored) {
                }
            }
        }
    };

    @BeforeEach
    void setup() {
        baseUrl = TestConfig.get("base.url");
        userUsername = TestAccounts.userUsername();
        userPassword = TestAccounts.userPassword();
        adminUsername = TestAccounts.adminUsername();
        adminPassword = TestAccounts.adminPassword();
        driver = DriverFactory.createDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void loginAsUser() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.openLoginPage().login(userUsername, userPassword);

        LoginState state = loginPage.waitForStableLoginState();
        if (state != LoginState.USER_HOME || !new StadiumPage(driver, baseUrl).isAtStadiumListPage()) {
            failWithDiagnostics("Không thể login user ổn định.");
        }
    }

    protected void loginAsAdmin() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.openLoginPage().login(adminUsername, adminPassword);

        LoginState state = loginPage.waitForStableLoginState();
        if (state != LoginState.ADMIN_HOME || !loginPage.isAtAdminArea()) {
            failWithDiagnostics("Không thể login admin ổn định.");
        }
    }

    protected void failWithDiagnostics(String reason) {
        String screenshotPath = ScreenshotUtils.capture(driver, "assertion-failure");

        // Đính kèm thêm bằng chứng vào Allure khi fail bằng hàm chủ động.
        AllureAttachmentUtils.attachScreenshot(driver);
        AllureAttachmentUtils.attachCurrentUrl(driver);
        AllureAttachmentUtils.attachPageTitle(driver);

        String diagnostics = DebugUtils.describe(driver);
        Assertions.fail(reason + " | " + diagnostics + " | screenshot=" + Optional.ofNullable(screenshotPath).orElse("n/a"));
    }
}