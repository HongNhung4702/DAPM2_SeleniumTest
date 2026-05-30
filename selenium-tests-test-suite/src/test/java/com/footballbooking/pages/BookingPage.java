package com.footballbooking.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class BookingPage extends BasePage {
    private static final By BOOKING_DATE = By.id("bookingDate");
    private static final By START_TIME = By.id("startTime");
    private static final By END_TIME = By.id("endTime");
    private static final By SUBMIT = By.cssSelector("button[type='submit'].btn-success");
    private static final By BACK_BUTTON = By.cssSelector("a[href$='/stadiums']");
    private static final By FORM = By.cssSelector("form.needs-validation");
    private static final By STADIUM_LIST_ANCHOR = By.id("stadiumList");
    private static final By ALERT = By.cssSelector(".alert, .toast, .text-danger, .invalid-feedback");

    public BookingPage(WebDriver driver) {
        super(driver);
    }

    public BookingPage fillBookingForm(String date, String start, String end) {
        setDateValue(date);
        waitUntilStartTimesLoaded();
        selectExistingTimeValue(START_TIME, start);

        waitUntilEndTimesLoaded();
        selectExistingTimeValue(END_TIME, end);
        return this;
    }

    public BookingPage submit() {
        click(SUBMIT);
        return this;
    }

    public BookingPage submitAndWaitForResult() {
        click(SUBMIT);
        wait.until(d -> isBookingFormDisplayed() || isRedirectedToStadiumList() || hasFeedbackMessage() || isHttp400Page());
        return this;
    }

    public StadiumPage backToStadiumList(String baseUrl) {
        click(BACK_BUTTON);
        return new StadiumPage(driver, baseUrl);
    }

    public void setDateValue(String date) {
        WebElement dateInput = waitVisible(BOOKING_DATE);

        // set trực tiếp value chuẩn yyyy-MM-dd để browser submit đúng format
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                dateInput, date
        );

        // xác nhận value thật sự đã vào input
        wait.until(d -> date.equals(dateInput.getAttribute("value")));
    }

    public String getDateValue() {
        return waitVisible(BOOKING_DATE).getAttribute("value");
    }

    private void waitUntilStartTimesLoaded() {
        wait.until(d -> !getSelectValues(START_TIME).isEmpty());
    }

    private void waitUntilEndTimesLoaded() {
        wait.until(d -> !getSelectValues(END_TIME).isEmpty());
    }

    public BookingPage selectStartTime(String start) {
        selectExistingTimeValue(START_TIME, start);
        return this;
    }

    public boolean isStartTimeAvailable(String value) {
        return getSelectValues(START_TIME).contains(value);
    }

    public boolean isEndTimeAvailable(String value) {
        return getSelectValues(END_TIME).contains(value);
    }

    public List<String> getStartTimeValues() {
        return getSelectValues(START_TIME);
    }

    public List<String> getEndTimeValues() {
        return getSelectValues(END_TIME);
    }

    public String[] pickFirstValidTimeRange() {
        waitUntilStartTimesLoaded();

        List<String> starts = getStartTimeValues();
        if (starts.isEmpty()) {
            throw new IllegalStateException("Dropdown startTime không có giá trị hợp lệ.");
        }

        for (String start : starts) {
            selectExistingTimeValue(START_TIME, start);
            waitUntilEndTimesLoaded();
            List<String> ends = getEndTimeValues();
            if (!ends.isEmpty()) {
                return new String[]{start, ends.get(0)};
            }
        }

        throw new IllegalStateException("Không tìm được cặp giờ bắt đầu/kết thúc hợp lệ.");
    }

    private void selectExistingTimeValue(By by, String value) {
        Select select = new Select(waitVisible(by));
        List<String> values = select.getOptions().stream()
                .map(option -> option.getAttribute("value"))
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.toList());

        if (!values.contains(value)) {
            throw new IllegalArgumentException("Dropdown " + by + " không có giá trị '" + value + "'. Available=" + values);
        }

        select.selectByValue(value);
    }

    private List<String> getSelectValues(By by) {
        try {
            Select select = new Select(waitVisible(by));
            return select.getOptions().stream()
                    .map(option -> option.getAttribute("value"))
                    .filter(v -> v != null && !v.isBlank())
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            return List.of();
        }
    }

    public boolean isBookingFormDisplayed() {
        return isPresent(FORM);
    }

    public boolean isRedirectedToStadiumList() {
        return currentUrl().contains("/stadiums") && isPresent(STADIUM_LIST_ANCHOR);
    }

    public boolean hasFeedbackMessage() {
        return !driver.findElements(ALERT).isEmpty();
    }

    public boolean isHttp400Page() {
        String title = driver.getTitle();
        return title != null && title.toLowerCase().contains("http status 400");
    }

    public String readFeedbackMessage() {
        for (int i = 0; i < 3; i++) {
            try {
                List<WebElement> alerts = driver.findElements(ALERT);
                if (alerts.isEmpty()) {
                    return "";
                }
                return alerts.get(0).getText().trim();
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return "";
    }
}