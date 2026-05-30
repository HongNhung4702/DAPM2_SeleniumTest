package com.footballbooking.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class AdminStadiumPage extends BasePage {
    private final String baseUrl;

    private static final By TABLE_ROWS = By.cssSelector("table tbody tr");
    private static final By ADD_BUTTON = By.cssSelector("a[href$='/admin/stadiums/add']");
    private static final By SAVE_FORM = By.cssSelector("form[action$='/admin/stadiums/save']");
    private static final By NAME = By.id("name");
    private static final By ADDRESS = By.id("address");
    private static final By PRICE = By.id("pricePerHour");
    private static final By FIELD_TYPE = By.id("fieldType");
    private static final By DESCRIPTION = By.id("description");
    private static final By SUBMIT = By.cssSelector("button[type='submit']");
    private static final By EDIT_BUTTONS = By.cssSelector("a[href*='/admin/stadiums/edit/']");
    private static final By DELETE_BUTTONS = By.cssSelector("button[onclick^='confirmDelete']");
    private static final By DELETE_MODAL = By.id("deleteModal");
    private static final By DELETE_SUBMIT = By.cssSelector("#deleteForm button[type='submit']");
    private static final By PAGE_TITLE = By.cssSelector("h2");
    private static final By ALERT = By.cssSelector(".alert, .toast, .text-success, .text-danger");

    public AdminStadiumPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    public AdminStadiumPage open() {
        open(baseUrl + "/admin/stadiums");
        waitVisible(PAGE_TITLE);
        return this;
    }

    public int getStadiumRowCount() {
        return driver.findElements(TABLE_ROWS).size();
    }

    public AdminStadiumPage clickAddNew() {
        click(ADD_BUTTON);
        waitVisible(SAVE_FORM);
        return this;
    }

    public AdminStadiumPage fillStadiumForm(String name, String address, String price, String description) {
        type(NAME, name);
        type(ADDRESS, address);
        type(PRICE, price);
        selectFirstValidFieldType();
        type(DESCRIPTION, description);
        return this;
    }

    public AdminStadiumPage fillStadiumFormForEdit(String name, String address, String price, String description) {
        type(NAME, name);
        type(ADDRESS, address);
        type(PRICE, price);
        selectDifferentFieldTypeIfPossible();
        type(DESCRIPTION, description);
        return this;
    }

    private void selectFirstValidFieldType() {
        Select select = new Select(waitVisible(FIELD_TYPE));
        List<WebElement> options = select.getOptions();

        for (WebElement option : options) {
            String value = option.getAttribute("value");
            if (value != null && !value.isBlank() && option.isEnabled()) {
                select.selectByValue(value);
                return;
            }
        }

        throw new IllegalStateException("Không tìm thấy field type hợp lệ để chọn.");
    }

    private void selectDifferentFieldTypeIfPossible() {
        Select select = new Select(waitVisible(FIELD_TYPE));
        String currentValue = select.getFirstSelectedOption().getAttribute("value");
        List<WebElement> options = select.getOptions();

        for (WebElement option : options) {
            String value = option.getAttribute("value");
            if (value != null && !value.isBlank() && option.isEnabled() && !value.equals(currentValue)) {
                select.selectByValue(value);
                return;
            }
        }
    }

    public AdminStadiumPage submitForm() {
        try {
            WebElement submitButton = waitVisible(SUBMIT);
            jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", submitButton);
            waitClickable(SUBMIT).click();
        } catch (Exception ex) {
            WebElement fallbackButton = waitVisible(SUBMIT);
            jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", fallbackButton);
            jsExecutor.executeScript("arguments[0].click();", fallbackButton);
        }

        wait.until(d -> currentUrl().contains("/admin/stadiums") || isPresent(ALERT));
        return this;
    }

    public AdminStadiumPage editFirstStadium() {
        List<WebElement> buttons = waitVisibleAll(EDIT_BUTTONS);
        buttons.get(0).click();
        waitVisible(SAVE_FORM);
        return this;
    }

    public AdminStadiumPage deleteFirstStadiumByModal() {
        List<WebElement> buttons = waitVisibleAll(DELETE_BUTTONS);
        buttons.get(0).click();
        waitVisible(DELETE_MODAL);
        click(DELETE_SUBMIT);
        wait.until(d -> currentUrl().contains("/admin/stadiums") || isPresent(ALERT));
        return this;
    }

    public String getFeedbackMessage() {
        if (!isPresent(ALERT)) {
            return "";
        }
        return text(ALERT);
    }
}