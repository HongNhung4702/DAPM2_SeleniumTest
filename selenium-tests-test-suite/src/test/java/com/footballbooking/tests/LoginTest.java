package com.footballbooking.tests;

import com.footballbooking.pages.LoginPage;
import com.footballbooking.pages.LoginPage.LoginState;
import com.footballbooking.pages.StadiumPage;
import com.footballbooking.utils.DebugUtils;
import com.footballbooking.utils.ScreenshotUtils;
import com.footballbooking.utils.WaitUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@Epic("Hệ thống đặt sân bóng đá trực tuyến")
@Feature("Đăng nhập và đăng xuất")
@DisplayName("Kiểm thử chức năng đăng nhập và đăng xuất")
public class LoginTest extends BaseTest {
    private static final Duration LOGIN_TIMEOUT = Duration.ofSeconds(15);

    private static final By USERNAME_INPUT = By.cssSelector("input#username,input[name='username']");
    private static final By PASSWORD_INPUT = By.cssSelector("input#password,input[name='password']");

    private static final List<By> LOGOUT_LOCATORS = List.of(
            By.cssSelector("a[href$='/logout']"),
            By.cssSelector("a[href*='/logout']"),
            By.xpath("//a[contains(normalize-space(),'Đăng xuất')]"),
            By.xpath("//a[contains(normalize-space(),'Logout')]")
    );

    // TC_005: Kiểm tra đăng nhập thành công bằng tài khoản người dùng.
    @Test
    @Story("TC_005 - Đăng nhập thành công")
    @DisplayName("TC_005 - Đăng nhập thành công bằng tài khoản người dùng")
    @Description("Kiểm tra người dùng đăng nhập thành công với username và password hợp lệ, sau đó được chuyển đến trang danh sách sân.")
    void loginUserSuccess() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.openLoginPage().login(userUsername, userPassword);

        LoginState state = loginPage.waitForStableLoginState();
        if (state != LoginState.USER_HOME) {
            String screenshotPath = ScreenshotUtils.capture(driver, "login-user-fail");
            Assertions.fail("User login không vào khu user. state=" + state
                    + ", error='" + loginPage.getErrorMessage() + "' | "
                    + DebugUtils.describe(driver)
                    + " | screenshot=" + screenshotPath);
        }

        Assertions.assertTrue(new StadiumPage(driver, baseUrl).isAtStadiumListPage(), DebugUtils.describe(driver));
    }

    // TC_006: Kiểm tra đăng nhập thất bại khi nhập sai username hoặc password.
    @Test
    @Story("TC_006 - Đăng nhập thất bại khi sai thông tin")
    @DisplayName("TC_006 - Đăng nhập thất bại khi sai username hoặc password")
    @Description("Kiểm tra hệ thống không cho đăng nhập khi người dùng nhập sai tên đăng nhập hoặc mật khẩu.")
    void loginUserFail() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.openLoginPage().login("wrong_user", "wrong_password");

        LoginState state = loginPage.waitForStableLoginState();
        Assertions.assertEquals(LoginState.LOGIN_ERROR, state, DebugUtils.describe(driver));

        String error = loginPage.getErrorMessage().toLowerCase();
        Assertions.assertTrue(
                error.contains("không đúng") || error.contains("sai") || error.contains("invalid"),
                "Thông báo lỗi đăng nhập không đúng kỳ vọng. " + DebugUtils.describe(driver)
        );
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), DebugUtils.describe(driver));
    }

    // Kiểm tra đăng nhập thành công bằng tài khoản admin.
    @Test
    @Story("Bổ sung - Đăng nhập admin")
    @DisplayName("Bổ sung - Đăng nhập thành công bằng tài khoản admin")
    @Description("Kiểm tra tài khoản admin đăng nhập thành công và được chuyển đến khu vực quản trị.")
    void loginAdminSuccess() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.openLoginPage().login(adminUsername, adminPassword);

        LoginState state = loginPage.waitForStableLoginState();
        if (state != LoginState.ADMIN_HOME) {
            String screenshotPath = ScreenshotUtils.capture(driver, "login-admin-fail");
            Assertions.fail("Admin login không vào khu admin. state=" + state
                    + ", error='" + loginPage.getErrorMessage() + "' | "
                    + DebugUtils.describe(driver)
                    + " | screenshot=" + screenshotPath);
        }

        Assertions.assertTrue(loginPage.isAtAdminArea(), DebugUtils.describe(driver));
    }

    // TC_007: Kiểm tra đăng nhập thất bại khi bỏ trống mật khẩu.
    @Test
    @Story("TC_007 - Đăng nhập thất bại khi bỏ trống mật khẩu")
    @DisplayName("TC_007 - Đăng nhập thất bại khi bỏ trống mật khẩu")
    @Description("Kiểm tra hệ thống không cho đăng nhập khi người dùng nhập username nhưng bỏ trống password.")
    void loginFailWhenPasswordEmpty() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.openLoginPage().login(userUsername, "");

        assertLoginRejectedAndStillAtLoginPage(
                loginPage,
                PASSWORD_INPUT,
                "Đăng nhập thiếu mật khẩu nhưng hệ thống vẫn cho qua."
        );
    }

    // TC_008: Kiểm tra đăng nhập thất bại khi bỏ trống tên đăng nhập.
    @Test
    @Story("TC_008 - Đăng nhập thất bại khi bỏ trống tên đăng nhập")
    @DisplayName("TC_008 - Đăng nhập thất bại khi bỏ trống tên đăng nhập")
    @Description("Kiểm tra hệ thống không cho đăng nhập khi người dùng bỏ trống username nhưng nhập password.")
    void loginFailWhenUsernameEmpty() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.openLoginPage().login("", userPassword);

        assertLoginRejectedAndStillAtLoginPage(
                loginPage,
                USERNAME_INPUT,
                "Đăng nhập thiếu tên đăng nhập nhưng hệ thống vẫn cho qua."
        );
    }

    // TC_009: Kiểm tra đăng xuất thành công khi người dùng xác nhận OK.
    @Test
    @Story("TC_009 - Đăng xuất thành công")
    @DisplayName("TC_009 - Đăng xuất thành công khi xác nhận OK")
    @Description("Kiểm tra người dùng đăng xuất thành công sau khi bấm Đăng xuất và xác nhận OK ở hộp thoại xác nhận.")
    void logoutSuccessWithConfirm() {
        loginAsUser();

        StadiumPage stadiumPage = new StadiumPage(driver, baseUrl);
        stadiumPage.logoutByConfirm();

        Boolean redirected = WaitUtils.waitUntil(driver, LOGIN_TIMEOUT, d ->
                driver.getCurrentUrl().contains("/login")
                        && !driver.findElements(USERNAME_INPUT).isEmpty() ? true : null
        );

        Assertions.assertTrue(Boolean.TRUE.equals(redirected), DebugUtils.describe(driver));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), DebugUtils.describe(driver));
    }

    // TC_010: Kiểm tra hủy thao tác đăng xuất, người dùng vẫn ở trạng thái đăng nhập.
    @Test
    @Story("TC_010 - Hủy thao tác đăng xuất")
    @DisplayName("TC_010 - Hủy thao tác đăng xuất")
    @Description("Kiểm tra khi người dùng bấm Đăng xuất nhưng chọn Cancel, phiên đăng nhập vẫn được giữ nguyên.")
    void logoutCancelKeepsUserLoggedIn() {
        loginAsUser();

        Assertions.assertTrue(
                new StadiumPage(driver, baseUrl).isAtStadiumListPage(),
                "Trước khi hủy đăng xuất, user phải đang ở trang danh sách sân. " + DebugUtils.describe(driver)
        );

        clickLogoutLink();
        dismissLogoutAlertIfPresent();

        Boolean stillLoggedIn = WaitUtils.waitUntil(driver, LOGIN_TIMEOUT, d ->
                !driver.getCurrentUrl().contains("/login")
                        && new StadiumPage(driver, baseUrl).isAtStadiumListPage() ? true : null
        );

        Assertions.assertTrue(
                Boolean.TRUE.equals(stillLoggedIn),
                "Sau khi bấm Cancel, user phải vẫn ở trạng thái đăng nhập. " + DebugUtils.describe(driver)
        );
    }

    // Hàm hỗ trợ kiểm tra hệ thống không cho đăng nhập khi thiếu dữ liệu bắt buộc.
    private void assertLoginRejectedAndStillAtLoginPage(LoginPage loginPage, By invalidInput, String failMessage) {
        Boolean stillAtLoginPage = WaitUtils.waitUntil(driver, LOGIN_TIMEOUT, d ->
                loginPage.isAtLoginPage() ? true : null
        );

        Assertions.assertTrue(
                Boolean.TRUE.equals(stillAtLoginPage),
                failMessage + " " + DebugUtils.describe(driver)
        );

        boolean hasServerError = loginPage.hasErrorMessage();
        boolean hasBrowserValidation = isInvalidHtml5Input(invalidInput);

        Assertions.assertTrue(
                hasServerError || hasBrowserValidation,
                "Hệ thống không hiển thị lỗi hoặc validate khi đăng nhập thiếu dữ liệu. "
                        + DebugUtils.describe(driver)
        );
    }

    // Hàm hỗ trợ kiểm tra validate HTML5 của input, ví dụ required, invalid value.
    private boolean isInvalidHtml5Input(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            Object valid = ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].checkValidity();", element);

            String validationMessage = element.getAttribute("validationMessage");

            return Boolean.FALSE.equals(valid)
                    || (validationMessage != null && !validationMessage.isBlank());
        } catch (Exception ex) {
            return false;
        }
    }

    // Hàm hỗ trợ tìm và click vào liên kết Đăng xuất trên giao diện.
    private void clickLogoutLink() {
        WebElement logoutLink = WaitUtils.waitUntil(driver, LOGIN_TIMEOUT, d -> {
            for (By locator : LOGOUT_LOCATORS) {
                List<WebElement> elements = d.findElements(locator);
                for (WebElement element : elements) {
                    if (element.isDisplayed() && element.isEnabled()) {
                        return element;
                    }
                }
            }
            return null;
        });

        logoutLink.click();
    }

    // Hàm hỗ trợ bấm Cancel trên hộp thoại xác nhận đăng xuất nếu hộp thoại xuất hiện.
    private void dismissLogoutAlertIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            Alert alert = shortWait.until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
        } catch (TimeoutException ignored) {
            // Nếu ứng dụng không hiển thị alert xác nhận thì bỏ qua.
        }
    }
}