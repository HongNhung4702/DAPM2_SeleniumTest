package com.footballbooking.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = TestConfig.get("browser", "chrome").trim().toLowerCase();
        boolean headless = TestConfig.getBoolean("headless", false);
        int timeoutSeconds = TestConfig.getInt("timeout.seconds", 15);

        WebDriver driver;

        switch (browser) {
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                }
                edgeOptions.addArguments("--start-maximized");
                driver = new EdgeDriver(edgeOptions);
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-infobars");
                chromeOptions.addArguments("--remote-allow-origins=*");
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeoutSeconds + 15L));
        driver.manage().window().maximize();
        return driver;
    }
}