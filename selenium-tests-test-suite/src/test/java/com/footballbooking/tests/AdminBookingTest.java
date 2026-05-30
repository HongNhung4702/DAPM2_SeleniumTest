package com.footballbooking.tests;

import com.footballbooking.pages.AdminBookingPage;
import com.footballbooking.pages.BookingPage;
import com.footballbooking.pages.StadiumPage;
import com.footballbooking.utils.DebugUtils;
import com.footballbooking.utils.RandomDataUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@Epic("Hệ thống đặt sân bóng đá trực tuyến")
@Feature("Quản lý đơn đặt sân")
@DisplayName("Kiểm thử chức năng quản lý đơn đặt sân")
public class AdminBookingTest extends BaseTest {

    private static final By REJECT_BUTTONS = By.cssSelector("form[action*='/admin/bookings/reject/'] button");
    private static final By APPROVE_BUTTONS = By.cssSelector("form[action*='/admin/bookings/approve/'] button");

    // TC_036: Kiểm tra admin xem được danh sách đơn đặt sân.
    @Test
    @Story("TC_036 - Xem danh sách đơn đặt sân")
    @DisplayName("TC_036 - Xem danh sách đơn đặt sân")
    @Description("Kiểm tra admin có thể truy cập trang quản lý đặt sân và xem danh sách đơn đặt sân.")
    void adminViewBookingList() {
        loginAsAdmin();

        AdminBookingPage page = new AdminBookingPage(driver, baseUrl).open();

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/admin/bookings"),
                DebugUtils.describe(driver)
        );

        Assertions.assertTrue(
                page.hasBookingTable() || page.getRowCount() == 0,
                DebugUtils.describe(driver)
        );
    }

    // TC_037: Kiểm tra admin lọc danh sách đơn đặt sân theo trạng thái.
    @Test
    @Story("TC_037 - Lọc đơn đặt sân theo trạng thái")
    @DisplayName("TC_037 - Xem danh sách đơn đặt sân theo trạng thái")
    @Description("Kiểm tra admin có thể lọc danh sách đơn đặt sân theo trạng thái, ví dụ trạng thái chờ duyệt.")
    void adminFilterBookingByStatus() {
        loginAsAdmin();

        AdminBookingPage page = new AdminBookingPage(driver, baseUrl).openWithStatus("PENDING");

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("status=PENDING"),
                DebugUtils.describe(driver)
        );

        Assertions.assertTrue(
                page.hasBookingTable() || page.getRowCount() == 0,
                DebugUtils.describe(driver)
        );
    }

    // TC_038: Kiểm tra admin phê duyệt đơn đặt sân thành công.
    @Test
    @Story("TC_038 - Duyệt đơn đặt sân thành công")
    @DisplayName("TC_038 - Duyệt đơn đặt sân thành công")
    @Description("Kiểm tra admin có thể phê duyệt một đơn đặt sân đang ở trạng thái chờ duyệt.")
    void adminApproveBooking() {
        ensurePendingBookingExists();
        loginAsAdmin();

        AdminBookingPage page = new AdminBookingPage(driver, baseUrl).openWithStatus("PENDING");

        if (!page.hasPendingActionButtons()) {
            Assertions.fail("Không có booking PENDING để approve.");
        }

        page.approveFirstPendingBooking();

        String feedback = page.getFeedbackMessage().toLowerCase();

        Assertions.assertTrue(
                feedback.contains("thành công") || feedback.contains("phê duyệt"),
                "Feedback approve không đúng kỳ vọng: " + feedback + " | " + DebugUtils.describe(driver)
        );
    }

    // TC_039: Kiểm tra admin từ chối đơn đặt sân thành công.
    @Test
    @Story("TC_039 - Từ chối đơn đặt sân thành công")
    @DisplayName("TC_039 - Từ chối đơn đặt sân thành công")
    @Description("Kiểm tra admin có thể từ chối một đơn đặt sân đang ở trạng thái chờ duyệt.")
    void adminRejectBooking() {
        ensurePendingBookingExists();
        loginAsAdmin();

        AdminBookingPage page = new AdminBookingPage(driver, baseUrl).openWithStatus("PENDING");

        if (!page.hasPendingActionButtons()) {
            Assertions.fail("Không có booking PENDING để reject.");
        }

        page.rejectFirstPendingBookingByConfirm();

        String feedback = page.getFeedbackMessage().toLowerCase();

        Assertions.assertTrue(
                feedback.contains("thành công") || feedback.contains("từ chối"),
                "Feedback reject không đúng kỳ vọng: " + feedback + " | " + DebugUtils.describe(driver)
        );
    }

    // TC_040: Kiểm tra admin hủy thao tác từ chối đơn đặt sân tại hộp thoại xác nhận.
    @Test
    @Story("TC_040 - Hủy thao tác từ chối đơn đặt sân")
    @DisplayName("TC_040 - Hủy thao tác từ chối đơn đặt sân")
    @Description("Kiểm tra khi admin bấm từ chối nhưng chọn Cancel ở hộp thoại xác nhận, đơn đặt sân vẫn giữ nguyên trạng thái.")
    void adminRejectBookingCancelConfirm() {
        ensurePendingBookingExists();
        loginAsAdmin();

        AdminBookingPage page = new AdminBookingPage(driver, baseUrl).openWithStatus("PENDING");

        Assertions.assertTrue(
                hasRejectButton(),
                "Không có booking PENDING để kiểm tra hủy thao tác từ chối. " + DebugUtils.describe(driver)
        );

        int rejectButtonCountBefore = driver.findElements(REJECT_BUTTONS).size();
        int approveButtonCountBefore = driver.findElements(APPROVE_BUTTONS).size();

        clickFirstRejectButton();
        dismissRejectAlertIfPresent();

        AdminBookingPage pageAfterCancel = new AdminBookingPage(driver, baseUrl).openWithStatus("PENDING");

        int rejectButtonCountAfter = driver.findElements(REJECT_BUTTONS).size();
        int approveButtonCountAfter = driver.findElements(APPROVE_BUTTONS).size();

        Assertions.assertTrue(
                pageAfterCancel.hasPendingActionButtons(),
                "Sau khi bấm Cancel ở hộp thoại từ chối, đơn PENDING vẫn phải còn thao tác xử lý. "
                        + DebugUtils.describe(driver)
        );

        Assertions.assertEquals(
                rejectButtonCountBefore,
                rejectButtonCountAfter,
                "Sau khi hủy thao tác từ chối, số nút từ chối không được thay đổi. "
                        + DebugUtils.describe(driver)
        );

        Assertions.assertEquals(
                approveButtonCountBefore,
                approveButtonCountAfter,
                "Sau khi hủy thao tác từ chối, số nút phê duyệt không được thay đổi. "
                        + DebugUtils.describe(driver)
        );
    }

    // Hàm hỗ trợ tạo một đơn đặt sân ở trạng thái PENDING nếu cần.
    private void ensurePendingBookingExists() {
        loginAsUser();

        new StadiumPage(driver, baseUrl).open().openFirstStadiumDetail();

        new BookingPage(driver)
                .fillBookingForm(RandomDataUtil.dateAfterDays(6), "14:00", "15:00")
                .submitAndWaitForResult();

        driver.get(baseUrl + "/logout");
    }

    // Hàm hỗ trợ kiểm tra có nút từ chối đơn đặt sân hay không.
    private boolean hasRejectButton() {
        return !driver.findElements(REJECT_BUTTONS).isEmpty();
    }

    // Hàm hỗ trợ click nút từ chối đầu tiên trong danh sách đơn PENDING.
    private void clickFirstRejectButton() {
        List<WebElement> rejectButtons = driver.findElements(REJECT_BUTTONS);

        Assertions.assertFalse(
                rejectButtons.isEmpty(),
                "Không tìm thấy nút từ chối đơn đặt sân. " + DebugUtils.describe(driver)
        );

        rejectButtons.get(0).click();
    }

    // Hàm hỗ trợ bấm Cancel trên hộp thoại xác nhận từ chối đơn.
    private void dismissRejectAlertIfPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
        } catch (TimeoutException ignored) {
            // Nếu hệ thống không hiển thị alert xác nhận thì bỏ qua.
        }
    }
}