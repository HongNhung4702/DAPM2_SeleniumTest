package com.footballbooking.tests;

import com.footballbooking.data.TestAccounts;
import com.footballbooking.pages.LoginPage;
import com.footballbooking.pages.RegisterPage;
import com.footballbooking.utils.DebugUtils;
import com.footballbooking.utils.RandomDataUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

@Epic("Hệ thống đặt sân bóng đá trực tuyến")
@Feature("Đăng ký tài khoản")
@DisplayName("Kiểm thử chức năng đăng ký tài khoản")
public class RegisterTest extends BaseTest {

    private static final By USERNAME_INPUT = By.id("username");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By FULL_NAME_INPUT = By.id("fullName");
    private static final By EMAIL_INPUT = By.id("email");
    private static final By PHONE_INPUT = By.id("phone");
    private static final By ADDRESS_INPUT = By.id("address");

    private static final By ERROR_MESSAGE = By.cssSelector(".invalid-feedback");

    // TC_001: Kiểm tra đăng ký tài khoản thành công với thông tin hợp lệ.
    @Test
    @Story("TC_001 - Đăng ký tài khoản thành công")
    @DisplayName("TC_001 - Đăng ký tài khoản thành công")
    @Description("Kiểm tra người dùng đăng ký tài khoản thành công khi nhập đầy đủ thông tin hợp lệ và username/email chưa tồn tại.")
    void registerSuccess() {
        String username = RandomDataUtil.randomUsername("reguser");
        String email = RandomDataUtil.randomEmail("regmail");

        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.openRegisterPage()
                .register(username, "Password1", "Test Register", email, "0912345678", "1 Nguyen Hue, Quy Nhon");

        LoginPage loginPage = new LoginPage(driver, baseUrl);
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), DebugUtils.describe(driver));
        Assertions.assertTrue(
                loginPage.getSuccessMessage().contains("Đăng ký tài khoản thành công"),
                "Không thấy thông báo đăng ký thành công. " + DebugUtils.describe(driver)
        );
    }

    // TC_002: Kiểm tra đăng ký thất bại khi tên đăng nhập đã tồn tại.
    @Test
    @Story("TC_002 - Đăng ký thất bại khi username đã tồn tại")
    @DisplayName("TC_002 - Đăng ký thất bại khi tên đăng nhập đã tồn tại")
    @Description("Kiểm tra hệ thống không cho đăng ký khi người dùng nhập tên đăng nhập đã tồn tại trong hệ thống.")
    void registerFailDuplicateUsername() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.openRegisterPage()
                .register(
                        TestAccounts.existingUsername(),
                        "Password1",
                        "Dup User",
                        RandomDataUtil.randomEmail("dupname"),
                        "0912345678",
                        "2 Nguyen Hue, Quy Nhon"
                );

        Assertions.assertTrue(
                registerPage.getErrorMessage().contains("Tên đăng nhập đã tồn tại"),
                "Không thấy thông báo username đã tồn tại. " + DebugUtils.describe(driver)
        );
    }

    // TC_003: Kiểm tra đăng ký thất bại khi email đã được sử dụng.
    @Test
    @Story("TC_003 - Đăng ký thất bại khi email đã tồn tại")
    @DisplayName("TC_003 - Đăng ký thất bại khi email đã được sử dụng")
    @Description("Kiểm tra hệ thống không cho đăng ký khi người dùng nhập email đã được sử dụng bởi tài khoản khác.")
    void registerFailDuplicateEmail() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.openRegisterPage()
                .register(
                        RandomDataUtil.randomUsername("dupemail"),
                        "Password1",
                        "Dup Email",
                        TestAccounts.existingEmail(),
                        "0912345679",
                        "3 Nguyen Hue, Quy Nhon"
                );

        Assertions.assertTrue(
                registerPage.getErrorMessage().contains("Email đã được sử dụng"),
                "Không thấy thông báo email đã được sử dụng. " + DebugUtils.describe(driver)
        );
    }

    // TC_004: Kiểm tra đăng ký thất bại khi bỏ trống các trường thông tin bắt buộc.
    @Test
    @Story("TC_004 - Đăng ký thất bại khi bỏ trống thông tin bắt buộc")
    @DisplayName("TC_004 - Đăng ký thất bại khi bỏ trống thông tin bắt buộc")
    @Description("Kiểm tra hệ thống không cho đăng ký khi người dùng bỏ trống một hoặc nhiều trường bắt buộc trong form đăng ký.")
    void registerFailWhenRequiredFieldsEmpty() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.openRegisterPage()
                .register("", "", "", "", "", "");

        boolean stillAtRegisterPage = driver.getCurrentUrl().contains("/register");
        boolean hasServerError = hasVisibleServerError();
        boolean hasBrowserValidation = hasInvalidRequiredInput();

        Assertions.assertTrue(
                stillAtRegisterPage,
                "Đăng ký thiếu thông tin nhưng hệ thống vẫn chuyển trang. " + DebugUtils.describe(driver)
        );

        Assertions.assertTrue(
                hasServerError || hasBrowserValidation,
                "Hệ thống không hiển thị lỗi hoặc validate khi đăng ký thiếu thông tin bắt buộc. "
                        + DebugUtils.describe(driver)
        );
    }

    // Hàm hỗ trợ kiểm tra có thông báo lỗi server hiển thị trên giao diện hay không.
    private boolean hasVisibleServerError() {
        List<WebElement> errors = driver.findElements(ERROR_MESSAGE);

        for (WebElement error : errors) {
            if (error.isDisplayed() && !error.getText().isBlank()) {
                return true;
            }
        }

        return false;
    }

    // Hàm hỗ trợ kiểm tra có ít nhất một ô nhập liệu bắt buộc đang không hợp lệ.
    private boolean hasInvalidRequiredInput() {
        return isInvalidHtml5Input(USERNAME_INPUT)
                || isInvalidHtml5Input(PASSWORD_INPUT)
                || isInvalidHtml5Input(FULL_NAME_INPUT)
                || isInvalidHtml5Input(EMAIL_INPUT)
                || isInvalidHtml5Input(PHONE_INPUT)
                || isInvalidHtml5Input(ADDRESS_INPUT);
    }

    // Hàm hỗ trợ kiểm tra validate HTML5 của input, ví dụ required hoặc nhập sai định dạng.
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
}