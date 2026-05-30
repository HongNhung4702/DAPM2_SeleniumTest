package com.footballbooking.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class StadiumPage extends BasePage {
    private final String baseUrl;

    private static final By AREA_FILTER = By.id("areaFilter");
    private static final By STADIUM_FILTER = By.id("stadiumFilter");
    private static final By FIELD_TYPE_FILTER = By.id("fieldTypeFilter");
    private static final By QUICK_SEARCH_BUTTON = By.xpath("//button[contains(normalize-space(),'Tìm Kiếm Nhanh')]");
    private static final By STADIUM_LIST = By.id("stadiumList");
    private static final By STADIUM_CARDS = By.cssSelector("#stadiumList .card");
    private static final By STADIUM_CARD_TITLE = By.cssSelector("#stadiumList .card-title");
    private static final By DETAIL_BUTTONS = By.cssSelector("#stadiumList a.btn.btn-success");

    private static final List<By> LOGOUT_LOCATORS = List.of(
            By.cssSelector("a[href$='/logout']"),
            By.cssSelector("a[href*='/logout']"),
            By.xpath("//a[contains(normalize-space(),'Đăng xuất')]"),
            By.xpath("//a[contains(normalize-space(),'Logout')]")
    );

    private static final By LOGIN_USERNAME_INPUT = By.cssSelector("input#username,input[name='username']");

    public StadiumPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    public StadiumPage open() {
        open(baseUrl + "/stadiums");
        waitUntilLoaded();
        return this;
    }

    public StadiumPage waitUntilLoaded() {
        waitVisible(AREA_FILTER);
        waitVisible(STADIUM_LIST);
        return this;
    }

    public boolean isAtStadiumListPage() {
        return currentUrl().contains("/stadiums") && isPresent(STADIUM_LIST);
    }

    public StadiumPage filterByArea(String area) {
        selectByVisibleText(AREA_FILTER, area);
        click(QUICK_SEARCH_BUTTON);
        waitVisible(STADIUM_LIST);
        return this;
    }

    public StadiumPage filterByFieldType(String fieldType) {
        selectByVisibleText(FIELD_TYPE_FILTER, fieldType);
        click(QUICK_SEARCH_BUTTON);
        waitVisible(STADIUM_LIST);
        return this;
    }

    public StadiumPage filterByMultiple(String area, String fieldType) {
        selectByVisibleText(AREA_FILTER, area);
        selectByVisibleText(FIELD_TYPE_FILTER, fieldType);
        click(QUICK_SEARCH_BUTTON);
        waitVisible(STADIUM_LIST);
        return this;
    }

    public List<String> getVisibleStadiumTitles() {
        return driver.findElements(STADIUM_CARD_TITLE).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public int getVisibleCardCount() {
        return (int) driver.findElements(STADIUM_CARDS).stream().filter(WebElement::isDisplayed).count();
    }

    public List<String> getAreaOptions() {
        return new Select(waitVisible(AREA_FILTER)).getOptions().stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isBlank() && !"Khu vực".equalsIgnoreCase(text))
                .collect(Collectors.toList());
    }

    public List<String> getFieldTypeOptions() {
        return new Select(waitVisible(FIELD_TYPE_FILTER)).getOptions().stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isBlank() && !"Chọn loại sân".equalsIgnoreCase(text))
                .collect(Collectors.toList());
    }

    public StadiumPage openFirstStadiumDetail() {
        List<WebElement> buttons = waitVisibleAll(DETAIL_BUTTONS);
        WebElement firstVisible = buttons.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy nút Chi tiết hiển thị."));
        firstVisible.click();
        return this;
    }

    public StadiumPage logoutByConfirm() {
        click(firstClickable(LOGOUT_LOCATORS));
        acceptJsAlertIfPresent();
        wait.until(d -> currentUrl().contains("/login") || isPresent(LOGIN_USERNAME_INPUT));
        return this;
    }

    public boolean isStadiumFilterEnabled() {
        return waitVisible(STADIUM_FILTER).isEnabled();
    }
}