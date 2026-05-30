package com.footballbooking.tests;

import com.footballbooking.data.TestStadiumData;
import com.footballbooking.pages.BookingPage;
import com.footballbooking.pages.StadiumPage;
import com.footballbooking.utils.DebugUtils;
import com.footballbooking.utils.RandomDataUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;

@Epic("Hệ thống đặt sân bóng đá trực tuyến")
@Feature("Tìm kiếm và đặt sân")
@DisplayName("Kiểm thử chức năng tìm kiếm và đặt sân")
public class BookingTest extends BaseTest {

    private static final By BOOKING_DATE = By.id("bookingDate");
    private static final By START_TIME = By.id("startTime");
    private static final By END_TIME = By.id("endTime");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit'].btn-success");

    private static final By STADIUM_NAME_SELECT = By.cssSelector(
            "select#stadiumFilter, select[name='stadiumFilter'], " +
                    "select#stadiumName, select#stadiumId, " +
                    "select[name='stadiumName'], select[name='stadiumId']"
    );

    private static final By SEARCH_BUTTON = By.xpath(
            "//button[contains(normalize-space(),'Tìm Kiếm Nhanh') " +
                    "or contains(normalize-space(),'Tìm kiếm nhanh') " +
                    "or contains(normalize-space(),'Tìm kiếm') " +
                    "or contains(normalize-space(),'Tìm Kiếm')]"
    );

    // Kiểm tra sau khi đăng nhập, người dùng mở được trang danh sách sân.
    @Test
    @Story("Bổ sung - Mở danh sách sân sau đăng nhập")
    @DisplayName("Bổ sung - Mở danh sách sân sau khi đăng nhập")
    @Description("Kiểm tra sau khi đăng nhập, người dùng có thể truy cập trang danh sách sân bóng.")
    void openStadiumListAfterLogin() {
        loginAsUser();
        StadiumPage stadiumPage = new StadiumPage(driver, baseUrl).open();
        Assertions.assertTrue(stadiumPage.isAtStadiumListPage(), DebugUtils.describe(driver));
    }

    // TC_011: Kiểm tra tìm kiếm sân theo khu vực.
    @Test
    @Story("TC_011 - Tìm kiếm sân theo khu vực")
    @DisplayName("TC_011 - Tìm kiếm sân theo khu vực")
    @Description("Kiểm tra người dùng có thể lọc danh sách sân bóng theo khu vực đã chọn.")
    void filterByArea() {
        loginAsUser();
        StadiumPage stadiumPage = new StadiumPage(driver, baseUrl).open();
        stadiumPage.filterByArea(TestStadiumData.AREA_QUY_NHON);

        List<String> titles = stadiumPage.getVisibleStadiumTitles();
        Assertions.assertFalse(titles.isEmpty(), "Không có sân hiển thị sau khi lọc khu vực.");
    }

    // TC_012: Kiểm tra tìm kiếm sân theo tên sân.
    @Test
    @Story("TC_012 - Tìm kiếm sân theo tên sân")
    @DisplayName("TC_012 - Tìm kiếm sân theo tên sân")
    @Description("Kiểm tra người dùng có thể lọc danh sách sân theo tên sân sau khi chọn khu vực.")
    void filterByStadiumName() {
        loginAsUser();

        StadiumPage stadiumPage = new StadiumPage(driver, baseUrl).open();
        int beforeSearchCount = stadiumPage.getVisibleCardCount();

        Assertions.assertTrue(
                beforeSearchCount > 0,
                "Danh sách sân ban đầu đang trống. " + DebugUtils.describe(driver)
        );

        selectAreaForStadiumNameFilter(TestStadiumData.AREA_QUY_NHON);

        boolean selected = selectFirstAvailableStadiumName();

        Assertions.assertTrue(
                selected,
                "Không tìm thấy dữ liệu tên sân sau khi đã chọn khu vực. " + DebugUtils.describe(driver)
        );

        clickSearchButton();

        Assertions.assertTrue(
                stadiumPage.getVisibleCardCount() > 0,
                "Không có sân hiển thị sau khi lọc theo tên sân. " + DebugUtils.describe(driver)
        );
    }

    // TC_013: Kiểm tra tìm kiếm sân theo loại sân.
    @Test
    @Story("TC_013 - Tìm kiếm sân theo loại sân")
    @DisplayName("TC_013 - Tìm kiếm sân theo loại sân")
    @Description("Kiểm tra người dùng có thể lọc danh sách sân theo loại sân.")
    void filterByFieldType() {
        loginAsUser();
        StadiumPage stadiumPage = new StadiumPage(driver, baseUrl).open();
        stadiumPage.filterByFieldType(TestStadiumData.FIELD_TYPE_SAN_5);

        Assertions.assertTrue(
                stadiumPage.getVisibleCardCount() > 0,
                "Không có sân hiển thị sau khi lọc loại sân."
        );
    }

    // TC_014: Kiểm tra tìm kiếm sân theo nhiều tiêu chí kết hợp.
    @Test
    @Story("TC_014 - Tìm kiếm sân theo nhiều tiêu chí")
    @DisplayName("TC_014 - Tìm kiếm sân theo nhiều tiêu chí")
    @Description("Kiểm tra người dùng có thể tìm kiếm sân bằng cách kết hợp nhiều tiêu chí lọc.")
    void filterByMultipleCriteria() {
        loginAsUser();
        StadiumPage stadiumPage = new StadiumPage(driver, baseUrl).open();
        stadiumPage.filterByMultiple(TestStadiumData.AREA_QUY_NHON, TestStadiumData.FIELD_TYPE_SAN_5);

        Assertions.assertTrue(
                stadiumPage.getVisibleCardCount() > 0,
                "Không có sân hiển thị sau khi lọc nhiều tiêu chí."
        );
    }

    // TC_015: Kiểm tra tìm kiếm khi không nhập hoặc không chọn tiêu chí lọc.
    @Test
    @Story("TC_015 - Tìm kiếm khi không nhập tiêu chí")
    @DisplayName("TC_015 - Tìm kiếm khi không nhập tiêu chí")
    @Description("Kiểm tra khi người dùng không chọn tiêu chí lọc, hệ thống vẫn hiển thị danh sách sân phù hợp.")
    void filterWithoutCriteriaShowsAllStadiums() {
        loginAsUser();

        StadiumPage stadiumPage = new StadiumPage(driver, baseUrl).open();
        clickSearchButton();

        Assertions.assertTrue(
                stadiumPage.getVisibleCardCount() > 0,
                "Không có sân hiển thị khi tìm kiếm không chọn tiêu chí. " + DebugUtils.describe(driver)
        );
    }

    // Kiểm tra mở trang chi tiết sân từ danh sách sân.
    @Test
    @Story("Bổ sung - Mở chi tiết sân")
    @DisplayName("Bổ sung - Mở trang chi tiết sân")
    @Description("Kiểm tra người dùng có thể mở trang chi tiết sân từ danh sách sân.")
    void openStadiumDetail() {
        loginAsUser();
        new StadiumPage(driver, baseUrl).open().openFirstStadiumDetail();
        Assertions.assertTrue(driver.getCurrentUrl().contains("/book"), DebugUtils.describe(driver));
    }

    // TC_016: Kiểm tra đặt sân thành công với ngày và khung giờ hợp lệ.
    @Test
    @Story("TC_016 - Đặt sân thành công")
    @DisplayName("TC_016 - Đặt sân thành công")
    @Description("Kiểm tra người dùng đặt sân thành công khi chọn ngày và khung giờ hợp lệ.")
    void createBookingSuccess() {
        loginAsUser();
        new StadiumPage(driver, baseUrl).open().openFirstStadiumDetail();

        BookingPage bookingPage = new BookingPage(driver);
        BookingSlot slot = prepareFirstAvailableSlot(bookingPage, 5, 14);

        Assertions.assertNotNull(
                slot,
                "Không tìm thấy ngày nào có khung giờ trống để test createBookingSuccess."
        );

        Assertions.assertEquals(
                slot.date(),
                bookingPage.getDateValue(),
                "Input bookingDate không giữ đúng giá trị đã set trước khi submit."
        );

        bookingPage.submitAndWaitForResult();

        String feedback = bookingPage.readFeedbackMessage().toLowerCase();
        Assertions.assertTrue(
                bookingPage.isRedirectedToStadiumList()
                        || feedback.contains("thành công")
                        || feedback.contains("đặt sân"),
                "Kết quả đặt sân chưa đúng kỳ vọng. slot=" + slot
                        + ", feedback='" + feedback + "' | " + DebugUtils.describe(driver)
        );
    }

    // TC_017: Kiểm tra đặt sân thất bại khi khung giờ đã được đặt hoặc bị giao diện chặn trùng giờ.
    @Test
    @Story("TC_017 - Đặt sân thất bại khi trùng khung giờ")
    @DisplayName("TC_017 - Đặt sân thất bại khi khung giờ đã được đặt")
    @Description("Kiểm tra hệ thống không cho đặt sân khi khung giờ đã có booking trước đó.")
    void createBookingFailByOverlap() {
        loginAsUser();
        new StadiumPage(driver, baseUrl).open().openFirstStadiumDetail();

        String detailUrl = driver.getCurrentUrl();
        BookingPage firstBookingPage = new BookingPage(driver);

        BookingSlot slot = prepareFirstAvailableSlot(firstBookingPage, 6, 14);
        Assertions.assertNotNull(slot, "Không tìm thấy khung giờ để test overlap.");

        firstBookingPage.submitAndWaitForResult();

        String firstFeedback = firstBookingPage.readFeedbackMessage().toLowerCase();
        Assertions.assertTrue(
                firstBookingPage.isRedirectedToStadiumList()
                        || firstFeedback.contains("thành công")
                        || firstFeedback.contains("đặt sân"),
                "Lần đặt sân đầu tiên không thành công. slot=" + slot
                        + ", feedback='" + firstFeedback + "' | " + DebugUtils.describe(driver)
        );

        driver.get(detailUrl);
        BookingPage secondBookingPage = new BookingPage(driver);

        secondBookingPage.setDateValue(slot.date());
        waitUntilStartOptionsLoaded();

        boolean startStillAvailable = secondBookingPage.getStartTimeValues().contains(slot.startTime());

        if (!startStillAvailable) {
            Assertions.assertTrue(
                    true,
                    "Khung giờ bắt đầu đã biến mất khỏi UI sau lần đặt đầu, overlap đã được ngăn ở giao diện."
            );
            return;
        }

        secondBookingPage.selectStartTime(slot.startTime());
        waitUntilEndOptionsLoaded();

        boolean endStillAvailable = secondBookingPage.getEndTimeValues().contains(slot.endTime());

        if (!endStillAvailable) {
            Assertions.assertTrue(
                    true,
                    "Khung giờ kết thúc đã biến mất khỏi UI sau lần đặt đầu, overlap đã được ngăn ở giao diện."
            );
            return;
        }

        selectEndTime(slot.endTime());
        secondBookingPage.submitAndWaitForResult();

        Assertions.assertFalse(
                secondBookingPage.isHttp400Page(),
                "Không mong đợi HTTP 400 khi test overlap. slot=" + slot + " | " + DebugUtils.describe(driver)
        );

        String feedback = secondBookingPage.readFeedbackMessage().toLowerCase();

        Assertions.assertTrue(
                feedback.contains("trùng")
                        || feedback.contains("đã được đặt")
                        || feedback.contains("không khả dụng")
                        || secondBookingPage.isBookingFormDisplayed(),
                "Feedback overlap không đúng kỳ vọng: slot=" + slot
                        + ", feedback='" + feedback + "' | " + DebugUtils.describe(driver)
        );
    }

    // TC_018: Kiểm tra không cho gửi đơn đặt sân khi chưa chọn ngày đặt.
    @Test
    @Story("TC_018 - Đặt sân thất bại khi chưa chọn ngày")
    @DisplayName("TC_018 - Đặt sân thất bại khi chưa chọn ngày đặt")
    @Description("Kiểm tra hệ thống không cho gửi đơn đặt sân khi người dùng chưa chọn ngày đặt.")
    void createBookingFailWhenDateEmpty() {
        loginAsUser();
        new StadiumPage(driver, baseUrl).open().openFirstStadiumDetail();

        clearInputValue(BOOKING_DATE);
        clickSubmitButton();

        Assertions.assertTrue(
                isInvalidHtml5Input(BOOKING_DATE) || driver.getCurrentUrl().contains("/book"),
                "Hệ thống không validate khi chưa chọn ngày đặt sân. " + DebugUtils.describe(driver)
        );
    }

    // TC_019: Kiểm tra không cho gửi đơn đặt sân khi chưa chọn giờ bắt đầu.
    @Test
    @Story("TC_019 - Đặt sân thất bại khi chưa chọn giờ bắt đầu")
    @DisplayName("TC_019 - Đặt sân thất bại khi chưa chọn giờ bắt đầu")
    @Description("Kiểm tra hệ thống không cho gửi đơn đặt sân khi người dùng chưa chọn giờ bắt đầu.")
    void createBookingFailWhenStartTimeEmpty() {
        loginAsUser();
        new StadiumPage(driver, baseUrl).open().openFirstStadiumDetail();

        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.setDateValue(RandomDataUtil.dateAfterDays(7));
        clickSubmitButton();

        Assertions.assertTrue(
                isInvalidHtml5Input(START_TIME) || driver.getCurrentUrl().contains("/book"),
                "Hệ thống không validate khi chưa chọn giờ bắt đầu. " + DebugUtils.describe(driver)
        );
    }

    // TC_020: Kiểm tra không cho gửi đơn đặt sân khi chưa chọn giờ kết thúc.
    @Test
    @Story("TC_020 - Đặt sân thất bại khi chưa chọn giờ kết thúc")
    @DisplayName("TC_020 - Đặt sân thất bại khi chưa chọn giờ kết thúc")
    @Description("Kiểm tra hệ thống không cho gửi đơn đặt sân khi người dùng chưa chọn giờ kết thúc.")
    void createBookingFailWhenEndTimeEmpty() {
        loginAsUser();
        new StadiumPage(driver, baseUrl).open().openFirstStadiumDetail();

        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.setDateValue(RandomDataUtil.dateAfterDays(8));
        waitUntilStartOptionsLoaded();

        List<String> starts = bookingPage.getStartTimeValues();
        Assertions.assertFalse(starts.isEmpty(), "Không có giờ bắt đầu để test thiếu giờ kết thúc.");

        bookingPage.selectStartTime(starts.get(0));
        clickSubmitButton();

        Assertions.assertTrue(
                isInvalidHtml5Input(END_TIME) || driver.getCurrentUrl().contains("/book"),
                "Hệ thống không validate khi chưa chọn giờ kết thúc. " + DebugUtils.describe(driver)
        );
    }

    // TC_021: Kiểm tra nút quay lại từ trang chi tiết sân về danh sách sân.
    @Test
    @Story("TC_021 - Quay lại từ trang chi tiết sân")
    @DisplayName("TC_021 - Quay lại từ trang chi tiết sân")
    @Description("Kiểm tra người dùng có thể quay lại danh sách sân từ trang chi tiết sân.")
    void backFromStadiumDetailToStadiumList() {
        loginAsUser();

        StadiumPage stadiumPage = new StadiumPage(driver, baseUrl).open();
        stadiumPage.openFirstStadiumDetail();

        BookingPage bookingPage = new BookingPage(driver);
        StadiumPage resultPage = bookingPage.backToStadiumList(baseUrl);

        Assertions.assertTrue(
                resultPage.isAtStadiumListPage(),
                "Không quay lại được trang danh sách sân. " + DebugUtils.describe(driver)
        );
    }

    // Kiểm tra UI không cho chọn giờ kết thúc nhỏ hơn hoặc bằng giờ bắt đầu.
    @Test
    @Story("Bổ sung - Kiểm tra ràng buộc giờ kết thúc")
    @DisplayName("Bổ sung - Không cho chọn giờ kết thúc không hợp lệ")
    @Description("Kiểm tra giao diện không cho chọn giờ kết thúc nhỏ hơn hoặc bằng giờ bắt đầu.")
    void invalidEndTimeShouldBePreventedByUi() {
        loginAsUser();
        new StadiumPage(driver, baseUrl).open().openFirstStadiumDetail();

        BookingPage bookingPage = new BookingPage(driver);
        BookingSlot slot = prepareFirstAvailableSlot(bookingPage, 5, 14);

        Assertions.assertNotNull(slot, "Không tìm thấy khung giờ hợp lệ để test UI end time.");

        bookingPage.selectStartTime(slot.startTime());
        waitUntilEndOptionsLoaded();

        List<String> endOptions = bookingPage.getEndTimeValues();

        Assertions.assertFalse(endOptions.isEmpty(), "Dropdown giờ kết thúc không có lựa chọn.");
        Assertions.assertFalse(endOptions.contains(slot.startTime()), "Giờ kết thúc không được trùng giờ bắt đầu.");
        Assertions.assertTrue(
                endOptions.stream().allMatch(end -> end.compareTo(slot.startTime()) > 0),
                "UI vẫn đang cho chọn giờ kết thúc không hợp lệ. start="
                        + slot.startTime() + ", endOptions=" + endOptions
        );
    }

    // Hàm hỗ trợ tìm ngày và khung giờ trống đầu tiên để đặt sân.
    private BookingSlot prepareFirstAvailableSlot(BookingPage bookingPage, int fromDay, int toDay) {
        for (int i = fromDay; i <= toDay; i++) {
            String candidateDate = RandomDataUtil.dateAfterDays(i);
            bookingPage.setDateValue(candidateDate);

            waitUntilStartOptionsLoaded();

            List<String> starts = bookingPage.getStartTimeValues();
            if (starts.isEmpty()) {
                continue;
            }

            for (String start : starts) {
                bookingPage.selectStartTime(start);
                waitUntilEndOptionsLoaded();

                List<String> ends = bookingPage.getEndTimeValues();
                if (!ends.isEmpty()) {
                    String end = ends.get(0);
                    selectEndTime(end);
                    return new BookingSlot(candidateDate, start, end);
                }
            }
        }
        return null;
    }

    // Hàm hỗ trợ chờ dropdown giờ bắt đầu có dữ liệu.
    private void waitUntilStartOptionsLoaded() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> !new Select(driver.findElement(START_TIME)).getOptions().stream()
                        .map(option -> option.getAttribute("value"))
                        .filter(v -> v != null && !v.isBlank())
                        .toList()
                        .isEmpty());
    }

    // Hàm hỗ trợ chờ dropdown giờ kết thúc có dữ liệu.
    private void waitUntilEndOptionsLoaded() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> !new Select(driver.findElement(END_TIME)).getOptions().stream()
                        .map(option -> option.getAttribute("value"))
                        .filter(v -> v != null && !v.isBlank())
                        .toList()
                        .isEmpty());
    }

    // Hàm hỗ trợ chọn giờ kết thúc theo value.
    private void selectEndTime(String value) {
        new Select(driver.findElement(END_TIME)).selectByValue(value);
    }

    // Hàm hỗ trợ chọn khu vực để hệ thống bật dropdown tên sân.
    private void selectAreaForStadiumNameFilter(String area) {
        WebElement areaSelectElement = driver.findElement(By.id("areaFilter"));
        Select areaSelect = new Select(areaSelectElement);
        areaSelect.selectByVisibleText(area);

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    WebElement stadiumSelect = d.findElement(STADIUM_NAME_SELECT);
                    Select stadiumDropdown = new Select(stadiumSelect);

                    boolean enabled = stadiumSelect.isEnabled();
                    boolean hasData = stadiumDropdown.getOptions().stream()
                            .map(option -> option.getAttribute("value"))
                            .anyMatch(value -> value != null && !value.isBlank());

                    return enabled && hasData;
                });
    }

    // Hàm hỗ trợ chọn tên sân đầu tiên trong dropdown tên sân nếu có.
    private boolean selectFirstAvailableStadiumName() {
        List<WebElement> selects = driver.findElements(STADIUM_NAME_SELECT);

        if (selects.isEmpty()) {
            return false;
        }

        Select select = new Select(selects.get(0));
        List<WebElement> options = select.getOptions();

        for (WebElement option : options) {
            String value = option.getAttribute("value");
            if (value != null && !value.isBlank()) {
                select.selectByValue(value);
                return true;
            }
        }

        return false;
    }

    // Hàm hỗ trợ click nút tìm kiếm nhanh.
    private void clickSearchButton() {
        WebElement searchButton = driver.findElement(SEARCH_BUTTON);
        searchButton.click();
    }

    // Hàm hỗ trợ click nút gửi đơn đặt sân.
    private void clickSubmitButton() {
        WebElement button = driver.findElement(SUBMIT_BUTTON);
        button.click();
    }

    // Hàm hỗ trợ xóa giá trị trong input bằng JavaScript.
    private void clearInputValue(By locator) {
        WebElement input = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '';" +
                        "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                input
        );
    }

    // Hàm hỗ trợ kiểm tra validate HTML5 của input/select.
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

    // Kiểu dữ liệu lưu ngày đặt, giờ bắt đầu và giờ kết thúc đã chọn.
    private record BookingSlot(String date, String startTime, String endTime) {
    }
}