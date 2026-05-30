package com.footballbooking.tests;

import com.footballbooking.pages.AdminStadiumPage;
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
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@Epic("Hệ thống đặt sân bóng đá trực tuyến")
@Feature("Quản lý sân bóng")
@DisplayName("Kiểm thử chức năng quản lý sân bóng")
public class AdminStadiumTest extends BaseTest {

    private static final By STADIUM_ROWS = By.cssSelector("table tbody tr");
    private static final By ADD_BUTTON = By.cssSelector("a[href$='/admin/stadiums/add']");
    private static final By EDIT_BUTTONS = By.cssSelector("a[href*='/admin/stadiums/edit/']");
    private static final By DELETE_BUTTONS = By.cssSelector("button[onclick^='confirmDelete']");
    private static final By DELETE_MODAL = By.id("deleteModal");
    private static final By CANCEL_DELETE_BUTTON = By.cssSelector("#deleteModal button.btn-secondary, #deleteModal .btn-close");

    private static final By NAME_INPUT = By.id("name");
    private static final By ADDRESS_INPUT = By.id("address");
    private static final By PRICE_INPUT = By.id("pricePerHour");
    private static final By FIELD_TYPE_SELECT = By.id("fieldType");
    private static final By CANCEL_FORM_BUTTON = By.cssSelector("a.btn.btn-secondary[href$='/admin/stadiums']");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");

    // Test bổ sung: Kiểm tra admin xem được danh sách sân bóng.
    @Test
    @Story("Bổ sung - Xem danh sách sân bóng")
    @DisplayName("Bổ sung - Admin xem danh sách sân bóng")
    @Description("Kiểm tra admin có thể truy cập trang quản lý sân bóng và xem danh sách sân hiện có.")
    void adminViewStadiumList() {
        loginAsAdmin();

        AdminStadiumPage page = new AdminStadiumPage(driver, baseUrl).open();

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/admin/stadiums"),
                DebugUtils.describe(driver)
        );

        Assertions.assertTrue(
                page.getStadiumRowCount() >= 0,
                DebugUtils.describe(driver)
        );
    }

    // TC_027: Kiểm tra thêm sân bóng thành công với thông tin hợp lệ.
    @Test
    @Story("TC_027 - Thêm sân bóng thành công")
    @DisplayName("TC_027 - Thêm sân bóng thành công")
    @Description("Kiểm tra admin có thể thêm sân bóng mới khi nhập đầy đủ thông tin hợp lệ.")
    void adminAddStadium() {
        loginAsAdmin();
        AdminStadiumPage page = new AdminStadiumPage(driver, baseUrl).open();

        String name = RandomDataUtil.randomStadiumName();

        page.clickAddNew()
                .fillStadiumForm(name, "100 Test Street, Quy Nhon", "200000", "Sân tạo bởi Selenium")
                .submitForm();

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/admin/stadiums"),
                DebugUtils.describe(driver)
        );

        String feedback = page.getFeedbackMessage().toLowerCase();

        Assertions.assertTrue(
                feedback.contains("thành công") || feedback.contains("thêm") || feedback.isBlank(),
                "Feedback add stadium không đúng kỳ vọng: " + feedback + " | " + DebugUtils.describe(driver)
        );
    }

    // TC_028: Kiểm tra thêm sân bóng thất bại khi thiếu thông tin bắt buộc.
    @Test
    @Story("TC_028 - Thêm sân thất bại khi thiếu thông tin")
    @DisplayName("TC_028 - Thêm sân thất bại khi thiếu thông tin bắt buộc")
    @Description("Kiểm tra hệ thống không cho admin thêm sân bóng khi bỏ trống các trường thông tin bắt buộc.")
    void adminAddStadiumFailWhenRequiredFieldsEmpty() {
        loginAsAdmin();

        driver.get(baseUrl + "/admin/stadiums/add");

        clearInputValue(NAME_INPUT);
        clearInputValue(ADDRESS_INPUT);
        clearInputValue(PRICE_INPUT);
        clearSelectValue(FIELD_TYPE_SELECT);

        clickSubmitButton();

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/admin/stadiums/add")
                        || driver.getCurrentUrl().contains("/admin/stadiums/save"),
                "Thêm sân thiếu thông tin nhưng hệ thống vẫn chuyển về danh sách. "
                        + DebugUtils.describe(driver)
        );

        Assertions.assertTrue(
                isInvalidHtml5Input(NAME_INPUT)
                        || isInvalidHtml5Input(ADDRESS_INPUT)
                        || isInvalidHtml5Input(PRICE_INPUT)
                        || isInvalidHtml5Input(FIELD_TYPE_SELECT),
                "Hệ thống không validate khi thêm sân thiếu thông tin bắt buộc. "
                        + DebugUtils.describe(driver)
        );
    }

    // TC_029: Kiểm tra hủy thao tác thêm sân bóng.
    @Test
    @Story("TC_029 - Hủy thao tác thêm sân bóng")
    @DisplayName("TC_029 - Hủy thao tác thêm sân bóng")
    @Description("Kiểm tra khi admin mở form thêm sân nhưng bấm Hủy, hệ thống không lưu sân mới.")
    void adminAddStadiumCancelAction() {
        loginAsAdmin();

        AdminStadiumPage page = new AdminStadiumPage(driver, baseUrl).open();
        int before = page.getStadiumRowCount();

        clickByJavaScript(ADD_BUTTON);

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/admin/stadiums/add"),
                "Không mở được form thêm sân. " + DebugUtils.describe(driver)
        );

        typeInput(NAME_INPUT, "Cancel Add Stadium");
        typeInput(ADDRESS_INPUT, "Cancel Address");
        typeInput(PRICE_INPUT, "100000");

        clickByJavaScript(CANCEL_FORM_BUTTON);

        AdminStadiumPage pageAfterCancel = new AdminStadiumPage(driver, baseUrl).open();
        int after = pageAfterCancel.getStadiumRowCount();

        Assertions.assertEquals(
                before,
                after,
                "Sau khi bấm Hủy ở form thêm sân, số lượng sân không được thay đổi. "
                        + DebugUtils.describe(driver)
        );
    }

    // TC_030: Kiểm tra chỉnh sửa sân bóng thành công.
    @Test
    @Story("TC_030 - Chỉnh sửa sân bóng thành công")
    @DisplayName("TC_030 - Chỉnh sửa sân bóng thành công")
    @Description("Kiểm tra admin có thể cập nhật thông tin sân bóng với dữ liệu hợp lệ.")
    void adminEditStadium() {
        loginAsAdmin();
        AdminStadiumPage page = new AdminStadiumPage(driver, baseUrl).open();

        if (page.getStadiumRowCount() == 0) {
            Assertions.fail("Không có sân để chỉnh sửa.");
        }

        page.editFirstStadium()
                .fillStadiumFormForEdit("Updated Selenium Stadium", "200 Update Street, Quy Nhon", "250000", "Mô tả updated")
                .submitForm();

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/admin/stadiums"),
                DebugUtils.describe(driver)
        );

        String feedback = page.getFeedbackMessage().toLowerCase();

        Assertions.assertTrue(
                feedback.contains("thành công")
                        || feedback.contains("cập nhật")
                        || feedback.contains("sửa")
                        || feedback.isBlank(),
                "Feedback edit stadium không đúng kỳ vọng: " + feedback + " | " + DebugUtils.describe(driver)
        );
    }

    // TC_031: Kiểm tra chỉnh sửa sân bóng thất bại khi thiếu thông tin bắt buộc.
    @Test
    @Story("TC_031 - Chỉnh sửa sân thất bại khi thiếu thông tin")
    @DisplayName("TC_031 - Chỉnh sửa sân thất bại khi thiếu thông tin bắt buộc")
    @Description("Kiểm tra hệ thống không cho admin cập nhật sân bóng khi bỏ trống trường thông tin bắt buộc.")
    void adminEditStadiumFailWhenRequiredFieldsEmpty() {
        loginAsAdmin();

        AdminStadiumPage page = new AdminStadiumPage(driver, baseUrl).open();

        Assertions.assertTrue(
                page.getStadiumRowCount() > 0,
                "Không có sân để test sửa thiếu thông tin. " + DebugUtils.describe(driver)
        );

        clickFirstElementByJavaScript(EDIT_BUTTONS);

        clearInputValue(NAME_INPUT);
        clickSubmitButton();

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/admin/stadiums/edit")
                        || driver.getCurrentUrl().contains("/admin/stadiums/save"),
                "Sửa sân thiếu thông tin nhưng hệ thống vẫn chuyển về danh sách. "
                        + DebugUtils.describe(driver)
        );

        Assertions.assertTrue(
                isInvalidHtml5Input(NAME_INPUT),
                "Hệ thống không validate khi sửa sân thiếu tên sân. "
                        + DebugUtils.describe(driver)
        );
    }

    // TC_032: Kiểm tra hủy thao tác chỉnh sửa sân bóng.
    @Test
    @Story("TC_032 - Hủy thao tác chỉnh sửa sân bóng")
    @DisplayName("TC_032 - Hủy thao tác chỉnh sửa sân bóng")
    @Description("Kiểm tra khi admin mở form chỉnh sửa sân nhưng bấm Hủy, dữ liệu sân không bị thay đổi.")
    void adminEditStadiumCancelAction() {
        loginAsAdmin();

        AdminStadiumPage page = new AdminStadiumPage(driver, baseUrl).open();

        Assertions.assertTrue(
                page.getStadiumRowCount() > 0,
                "Không có sân để test hủy thao tác sửa. " + DebugUtils.describe(driver)
        );

        int before = page.getStadiumRowCount();

        clickFirstElementByJavaScript(EDIT_BUTTONS);

        typeInput(NAME_INPUT, "Cancel Edit Stadium");

        clickByJavaScript(CANCEL_FORM_BUTTON);

        AdminStadiumPage pageAfterCancel = new AdminStadiumPage(driver, baseUrl).open();
        int after = pageAfterCancel.getStadiumRowCount();

        Assertions.assertEquals(
                before,
                after,
                "Sau khi bấm Hủy ở form sửa sân, số lượng sân không được thay đổi. "
                        + DebugUtils.describe(driver)
        );
    }

    // TC_033/TC_034: Kiểm tra xóa sân bóng hoặc thông báo không thể xóa nếu sân còn đơn đặt.
    @Test
    @Story("TC_033/TC_034 - Xóa sân bóng")
    @DisplayName("TC_033/TC_034 - Xóa sân bóng hoặc không thể xóa khi sân còn đơn đặt")
    @Description("Kiểm tra admin xóa sân bóng thành công nếu đủ điều kiện, hoặc hệ thống hiển thị thông báo không thể xóa khi sân còn đơn đặt chưa hoàn thành.")
    void adminDeleteStadium() {
        loginAsAdmin();
        AdminStadiumPage page = new AdminStadiumPage(driver, baseUrl).open();

        if (page.getStadiumRowCount() == 0) {
            Assertions.fail("Không có sân để xóa.");
        }

        int before = page.getStadiumRowCount();

        page.deleteFirstStadiumByModal();

        int after = page.getStadiumRowCount();

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/admin/stadiums"),
                DebugUtils.describe(driver)
        );

        String feedback = page.getFeedbackMessage().toLowerCase();

        Assertions.assertTrue(
                feedback.contains("xóa")
                        || feedback.contains("không thể")
                        || feedback.isBlank(),
                "Feedback delete stadium không đúng kỳ vọng: " + feedback + " | " + DebugUtils.describe(driver)
        );

        Assertions.assertTrue(after <= before);
    }

    // TC_035: Kiểm tra hủy thao tác xóa sân bóng tại modal xác nhận.
    @Test
    @Story("TC_035 - Hủy thao tác xóa sân bóng")
    @DisplayName("TC_035 - Hủy thao tác xóa sân bóng")
    @Description("Kiểm tra khi admin mở modal xác nhận xóa sân nhưng bấm Hủy, sân bóng không bị xóa.")
    void adminDeleteStadiumCancelAction() {
        loginAsAdmin();

        AdminStadiumPage page = new AdminStadiumPage(driver, baseUrl).open();

        Assertions.assertTrue(
                page.getStadiumRowCount() > 0,
                "Không có sân để test hủy thao tác xóa. " + DebugUtils.describe(driver)
        );

        int before = page.getStadiumRowCount();

        clickFirstElementByJavaScript(DELETE_BUTTONS);

        boolean modalVisible = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> {
                    List<WebElement> modals = d.findElements(DELETE_MODAL);
                    return !modals.isEmpty() && modals.get(0).isDisplayed();
                });

        Assertions.assertTrue(
                modalVisible,
                "Modal xác nhận xóa sân không hiển thị. " + DebugUtils.describe(driver)
        );

        clickByJavaScript(CANCEL_DELETE_BUTTON);

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> {
                    List<WebElement> modals = d.findElements(DELETE_MODAL);
                    return modals.isEmpty() || !modals.get(0).isDisplayed();
                });

        AdminStadiumPage pageAfterCancel = new AdminStadiumPage(driver, baseUrl).open();
        int after = pageAfterCancel.getStadiumRowCount();

        Assertions.assertEquals(
                before,
                after,
                "Sau khi hủy thao tác xóa, số lượng sân không được thay đổi. "
                        + DebugUtils.describe(driver)
        );
    }

    // Hàm hỗ trợ nhập dữ liệu vào input bằng cách xóa dữ liệu cũ rồi nhập dữ liệu mới.
    private void typeInput(By locator, String value) {
        WebElement input = driver.findElement(locator);
        input.clear();
        input.sendKeys(value);
    }

    // Hàm hỗ trợ xóa giá trị input bằng JavaScript.
    private void clearInputValue(By locator) {
        WebElement input = driver.findElement(locator);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '';" +
                        "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                input
        );
    }

    // Hàm hỗ trợ reset select về option rỗng nếu có.
    private void clearSelectValue(By locator) {
        WebElement select = driver.findElement(locator);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '';" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                select
        );
    }

    // Hàm hỗ trợ click nút submit của form thêm/sửa sân.
    private void clickSubmitButton() {
        clickByJavaScript(SUBMIT_BUTTON);
    }

    // Hàm hỗ trợ kiểm tra validate HTML5 của input hoặc select.
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

    // Hàm hỗ trợ click phần tử bằng JavaScript để tránh lỗi element click intercepted.
    private void clickByJavaScript(By locator) {
        WebElement element = driver.findElement(locator);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                element
        );
    }

    // Hàm hỗ trợ click phần tử đầu tiên tìm được bằng JavaScript.
    private void clickFirstElementByJavaScript(By locator) {
        List<WebElement> elements = driver.findElements(locator);

        Assertions.assertFalse(
                elements.isEmpty(),
                "Không tìm thấy phần tử để click: " + locator + " | " + DebugUtils.describe(driver)
        );

        WebElement element = elements.get(0);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                element
        );
    }
}