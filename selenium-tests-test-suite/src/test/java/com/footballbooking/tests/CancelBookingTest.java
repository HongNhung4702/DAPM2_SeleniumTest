package com.footballbooking.tests;

import com.footballbooking.pages.BookingPage;
import com.footballbooking.pages.MyBookingPage;
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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Epic("Hệ thống đặt sân bóng đá trực tuyến")
@Feature("Lịch sử và hủy đặt sân")
@DisplayName("Kiểm thử chức năng lịch sử và hủy đặt sân")
public class CancelBookingTest extends BaseTest {

    private static final By CANCEL_BUTTONS = By.cssSelector(".cancel-btn");
    private static final By CANCEL_MODAL = By.id("cancelModal");
    private static final By CONFIRM_CANCEL_BUTTON = By.cssSelector("#cancelForm button[type='submit']");
    private static final By FEEDBACK_ALERT = By.cssSelector(".alert, .toast, .text-success, .text-danger");

    private static final List<By> CLOSE_CANCEL_MODAL_BUTTONS = List.of(
            By.cssSelector("#cancelModal .btn-close"),
            By.cssSelector("#cancelModal button[aria-label='Close']"),
            By.cssSelector("#cancelModal button[data-bs-dismiss='modal']"),
            By.cssSelector("#cancelModal .modal-header button"),
            By.cssSelector("#cancelModal .modal-footer button.btn-secondary"),
            By.xpath("//div[@id='cancelModal']//button[contains(normalize-space(),'Không')]"),
            By.xpath("//div[@id='cancelModal']//button[contains(normalize-space(),'Đóng')]"),
            By.xpath("//div[@id='cancelModal']//button[contains(normalize-space(),'Hủy')]"),
            By.xpath("//div[@id='cancelModal']//button[contains(normalize-space(),'Cancel')]"),
            By.xpath("//div[@id='cancelModal']//button[contains(normalize-space(),'Close')]")
    );

    // TC_022/TC_023: Kiểm tra người dùng mở được trang lịch sử đặt sân.
    @Test
    @Story("TC_022/TC_023 - Xem lịch sử đặt sân")
    @DisplayName("TC_022/TC_023 - Xem lịch sử đặt sân")
    @Description("Kiểm tra người dùng có thể mở trang lịch sử đặt sân và hệ thống hiển thị trạng thái danh sách hợp lệ.")
    void viewMyBookingsHistory() {
        loginAsUser();

        MyBookingPage myBookingPage = new MyBookingPage(driver, baseUrl).open();

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/my-bookings"),
                DebugUtils.describe(driver)
        );

        Assertions.assertTrue(
                myBookingPage.hasBookingTable() || myBookingPage.getBookingRowCount() == 0,
                "Trang lịch sử đặt sân không hiển thị bảng hoặc trạng thái danh sách hợp lệ. "
                        + DebugUtils.describe(driver)
        );
    }

    // TC_024: Kiểm tra hủy lịch đặt sân thành công khi booking đủ điều kiện hủy.
    @Test
    @Story("TC_024 - Hủy lịch đặt sân thành công")
    @DisplayName("TC_024 - Hủy lịch đặt sân thành công")
    @Description("Kiểm tra người dùng có thể hủy một đơn đặt sân hợp lệ còn trong thời gian cho phép hủy.")
    void cancelBookingSuccess() {
        loginAsUser();
        ensureCancelableBookingExists();

        MyBookingPage myBookingPage = new MyBookingPage(driver, baseUrl).open();

        if (!myBookingPage.hasCancelableBooking()) {
            Assertions.fail("Không tìm thấy booking đủ điều kiện hủy.");
        }

        myBookingPage.cancelFirstBookingViaModal();
        String feedback = myBookingPage.getFeedbackMessage().toLowerCase();

        Assertions.assertTrue(
                feedback.contains("thành công") || feedback.contains("hủy"),
                "Feedback cancel không đúng kỳ vọng: " + feedback + " | " + DebugUtils.describe(driver)
        );
    }

    // TC_025: Kiểm tra hủy thao tác hủy lịch đặt sân, booking vẫn được giữ nguyên.
    @Test
    @Story("TC_025 - Hủy thao tác hủy lịch đặt sân")
    @DisplayName("TC_025 - Hủy thao tác hủy lịch đặt sân")
    @Description("Kiểm tra khi người dùng mở modal hủy đặt sân nhưng đóng modal hoặc không xác nhận, đơn đặt sân vẫn được giữ nguyên.")
    void cancelBookingActionIsDismissed() {
        loginAsUser();
        ensureCancelableBookingExists();

        MyBookingPage myBookingPage = new MyBookingPage(driver, baseUrl).open();

        Assertions.assertTrue(
                myBookingPage.hasCancelableBooking(),
                "Không tìm thấy booking đủ điều kiện để kiểm tra hủy thao tác hủy đặt sân. "
                        + DebugUtils.describe(driver)
        );

        int rowCountBefore = myBookingPage.getBookingRowCount();

        openCancelModal();
        closeCancelModal();

        MyBookingPage pageAfterCancelAction = new MyBookingPage(driver, baseUrl).open();
        int rowCountAfter = pageAfterCancelAction.getBookingRowCount();

        Assertions.assertEquals(
                rowCountBefore,
                rowCountAfter,
                "Sau khi đóng modal hủy, số lượng booking không được thay đổi. "
                        + DebugUtils.describe(driver)
        );

        Assertions.assertTrue(
                pageAfterCancelAction.hasCancelableBooking(),
                "Sau khi hủy thao tác, booking vẫn phải còn ở trạng thái có thể hủy. "
                        + DebugUtils.describe(driver)
        );
    }

    // TC_026: Kiểm tra hủy lịch đặt sân thất bại khi booking đã gần giờ bắt đầu.
    @Test
    @Story("TC_026 - Hủy lịch đặt sân thất bại khi gần giờ bắt đầu")
    @DisplayName("TC_026 - Hủy lịch đặt sân thất bại khi gần giờ bắt đầu")
    @Description("Kiểm tra hệ thống không cho hủy đơn đặt sân khi thời điểm hủy đã quá gần giờ bắt đầu theo quy định.")
    void cancelBookingFailWhenNearStartTime() {
        loginAsUser();

        NearStartBooking nearStartBooking = createNearStartBookingByPost();

        new MyBookingPage(driver, baseUrl).open();

        openCancelModalForNearStartBooking(nearStartBooking);
        confirmCancelModal();

        String feedback = getFeedbackText().toLowerCase();

        Assertions.assertTrue(
                feedback.contains("không thể hủy")
                        || feedback.contains("30 phút")
                        || feedback.contains("ít nhất 30 phút"),
                "Hệ thống chưa hiển thị thông báo không thể hủy booking gần giờ bắt đầu. "
                        + "Feedback='" + feedback + "' | " + DebugUtils.describe(driver)
        );
    }

    // Hàm hỗ trợ đảm bảo có ít nhất một booking đủ điều kiện để hủy.
    private void ensureCancelableBookingExists() {
        MyBookingPage myBookingPage = new MyBookingPage(driver, baseUrl).open();

        if (myBookingPage.hasCancelableBooking()) {
            return;
        }

        new StadiumPage(driver, baseUrl).open().openFirstStadiumDetail();

        new BookingPage(driver)
                .fillBookingForm(RandomDataUtil.dateAfterDays(5), "09:00", "10:00")
                .submitAndWaitForResult();
    }

    // Hàm hỗ trợ tạo booking có giờ bắt đầu gần hiện tại bằng POST trực tiếp từ trình duyệt đang đăng nhập.
    private NearStartBooking createNearStartBookingByPost() {
        new StadiumPage(driver, baseUrl).open().openFirstStadiumDetail();

        String detailUrl = driver.getCurrentUrl();
        String stadiumId = extractStadiumIdFromBookingUrl(detailUrl);

        LocalDateTime start = LocalDateTime.now().plusMinutes(10);
        LocalDateTime end = start.plusMinutes(20);

        String bookingDate = start.toLocalDate().toString();
        String displayBookingDate = start.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String startTime = start.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTime = end.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        String postUrl = baseUrl + "/stadiums/" + stadiumId + "/book";
        String formBody = "bookingDate=" + encode(bookingDate)
                + "&startTime=" + encode(startTime)
                + "&endTime=" + encode(endTime);

        Object result = ((JavascriptExecutor) driver).executeAsyncScript(
                "const callback = arguments[arguments.length - 1];" +
                        "fetch(arguments[0], {" +
                        "  method: 'POST'," +
                        "  headers: {'Content-Type': 'application/x-www-form-urlencoded'}," +
                        "  body: arguments[1]," +
                        "  credentials: 'same-origin'," +
                        "  redirect: 'follow'" +
                        "}).then(response => callback(response.status + '|' + response.url))" +
                        "  .catch(error => callback('ERROR|' + error));",
                postUrl,
                formBody
        );

        String resultText = String.valueOf(result);

        Assertions.assertFalse(
                resultText.startsWith("ERROR"),
                "Không tạo được booking gần giờ bắt đầu bằng POST. result=" + resultText
                        + " | " + DebugUtils.describe(driver)
        );

        return new NearStartBooking(displayBookingDate, startTime);
    }

    // Hàm hỗ trợ mở đúng modal hủy của booking gần giờ bắt đầu vừa tạo.
    private void openCancelModalForNearStartBooking(NearStartBooking nearStartBooking) {
        List<WebElement> cancelButtons = driver.findElements(CANCEL_BUTTONS);

        Assertions.assertFalse(
                cancelButtons.isEmpty(),
                "Không có nút hủy booking để kiểm tra TC_026. " + DebugUtils.describe(driver)
        );

        for (WebElement button : cancelButtons) {
            String bookingDate = button.getAttribute("data-booking-date");
            String bookingTime = button.getAttribute("data-booking-time");

            if (nearStartBooking.displayDate().equals(bookingDate)
                    && nearStartBooking.startTime().equals(bookingTime)) {
                button.click();
                waitUntilCancelModalVisible();
                return;
            }
        }

        Assertions.fail(
                "Không tìm thấy nút hủy đúng booking gần giờ bắt đầu vừa tạo. "
                        + "Expected date=" + nearStartBooking.displayDate()
                        + ", time=" + nearStartBooking.startTime()
                        + " | " + DebugUtils.describe(driver)
        );
    }

    // Hàm hỗ trợ lấy stadiumId từ URL dạng /stadiums/{id}/book.
    private String extractStadiumIdFromBookingUrl(String url) {
        String marker = "/stadiums/";
        int startIndex = url.indexOf(marker);

        Assertions.assertTrue(
                startIndex >= 0,
                "URL chi tiết sân không đúng định dạng để lấy stadiumId: " + url
        );

        int idStart = startIndex + marker.length();
        int idEnd = url.indexOf("/book", idStart);

        Assertions.assertTrue(
                idEnd > idStart,
                "URL chi tiết sân không có phần /book để lấy stadiumId: " + url
        );

        return url.substring(idStart, idEnd);
    }

    // Hàm hỗ trợ mở modal xác nhận hủy booking đầu tiên.
    private void openCancelModal() {
        List<WebElement> cancelButtons = driver.findElements(CANCEL_BUTTONS);

        Assertions.assertFalse(
                cancelButtons.isEmpty(),
                "Không có nút hủy booking để mở modal. " + DebugUtils.describe(driver)
        );

        cancelButtons.get(0).click();
        waitUntilCancelModalVisible();
    }

    // Hàm hỗ trợ chờ modal hủy booking hiển thị.
    private void waitUntilCancelModalVisible() {
        boolean modalVisible = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> {
                    List<WebElement> modals = d.findElements(CANCEL_MODAL);
                    return !modals.isEmpty() && modals.get(0).isDisplayed();
                });

        Assertions.assertTrue(
                modalVisible,
                "Không mở được modal xác nhận hủy booking. " + DebugUtils.describe(driver)
        );
    }

    // Hàm hỗ trợ bấm xác nhận hủy booking trong modal.
    private void confirmCancelModal() {
        WebElement confirmButton = driver.findElement(CONFIRM_CANCEL_BUTTON);
        confirmButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> d.getCurrentUrl().contains("/my-bookings")
                        || !d.findElements(FEEDBACK_ALERT).isEmpty());
    }

    // Hàm hỗ trợ đóng modal xác nhận hủy mà không xác nhận hủy booking.
    private void closeCancelModal() {
        for (By locator : CLOSE_CANCEL_MODAL_BUTTONS) {
            List<WebElement> buttons = driver.findElements(locator);

            for (WebElement button : buttons) {
                if (button.isDisplayed() && button.isEnabled()) {
                    button.click();
                    waitUntilCancelModalClosed();
                    return;
                }
            }
        }

        driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        waitUntilCancelModalClosed();
    }

    // Hàm hỗ trợ chờ modal hủy booking đóng lại.
    private void waitUntilCancelModalClosed() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> {
                    List<WebElement> modals = d.findElements(CANCEL_MODAL);
                    return modals.isEmpty() || !modals.get(0).isDisplayed();
                });
    }

    // Hàm hỗ trợ lấy nội dung thông báo feedback trên giao diện.
    private String getFeedbackText() {
        List<WebElement> alerts = driver.findElements(FEEDBACK_ALERT);

        if (alerts.isEmpty()) {
            return "";
        }

        return alerts.get(0).getText().trim();
    }

    // Hàm hỗ trợ encode dữ liệu form trước khi gửi POST.
    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    // Kiểu dữ liệu lưu ngày hiển thị và giờ bắt đầu của booking gần giờ bắt đầu.
    private record NearStartBooking(String displayDate, String startTime) {
    }
}