package com.footballbooking.pages;

import com.footballbooking.config.TestConfig;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final JavascriptExecutor jsExecutor;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getInt("timeout.seconds", 15)));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    public void open(String url) {
        driver.get(url);
        waitForDocumentReady();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected WebElement waitVisible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected WebElement waitClickable(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    protected List<WebElement> waitVisibleAll(By by) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    protected void click(By by) {
        waitClickable(by).click();
    }

    protected void type(By by, String value) {
        WebElement element = waitVisible(by);
        element.clear();
        element.sendKeys(value);
    }

    protected void selectByVisibleText(By by, String text) {
        new Select(waitVisible(by)).selectByVisibleText(text);
    }

    protected void selectByValue(By by, String value) {
        new Select(waitVisible(by)).selectByValue(value);
    }

    protected String text(By by) {
        return waitVisible(by).getText().trim();
    }

    protected boolean isPresent(By by) {
        return !driver.findElements(by).isEmpty();
    }

    protected boolean isVisible(By by) {
        try {
            return waitVisible(by).isDisplayed();
        } catch (TimeoutException ex) {
            return false;
        }
    }

    protected By firstPresent(List<By> locators) {
        return wait.until(d -> locators.stream().filter(this::isPresent).findFirst().orElse(null));
    }

    protected By firstVisible(List<By> locators) {
        return wait.until(d -> locators.stream().filter(this::isVisibleFast).findFirst().orElse(null));
    }

    protected By firstClickable(List<By> locators) {
        return wait.until(d -> locators.stream().filter(this::isClickableFast).findFirst().orElse(null));
    }

    protected void clickWithJs(By by) {
        WebElement element = waitVisible(by);
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    protected void waitForDocumentReady() {
        wait.until(d -> "complete".equals(jsExecutor.executeScript("return document.readyState")));
    }

    public void acceptJsAlertIfPresent() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (TimeoutException ignored) {
        }
    }

    private boolean isVisibleFast(By by) {
        try {
            List<WebElement> elements = driver.findElements(by);
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        } catch (StaleElementReferenceException ex) {
            return false;
        }
    }

    private boolean isClickableFast(By by) {
        try {
            List<WebElement> elements = driver.findElements(by);
            return !elements.isEmpty() && elements.get(0).isDisplayed() && elements.get(0).isEnabled();
        } catch (StaleElementReferenceException ex) {
            return false;
        }
    }
}