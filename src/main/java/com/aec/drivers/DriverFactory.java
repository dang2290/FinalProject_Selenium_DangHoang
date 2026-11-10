package com.aec.drivers;

import com.aec.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/** Mỗi thread một WebDriver riêng — bắt buộc vì suite luôn chạy song song. */
public final class DriverFactory {

    private static final Logger LOG = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    /**
     * Chạy song song nhiều cửa sổ headed: cửa sổ bị che bị Chrome coi là ẩn và hoãn vẽ/đếm giờ —
     * bảng footable kẹt ở vòng xoay "đang tải". Tắt cơ chế đó để mọi cửa sổ chạy như đang hiển thị.
     */
    private static final String[] NO_BACKGROUND_THROTTLING = {
            "--disable-backgrounding-occluded-windows", "--disable-renderer-backgrounding", "--disable-background-timer-throttling"};
    /**
     * Windows đặt Scale 150% thì màn 1920×1080 chỉ chứa cửa sổ 1280 px CSS — Chrome tự thu cửa sổ lại,
     * footable chuyển bảng sang chế độ màn hẹp. Ép tỉ lệ 1 để 1920 px cửa sổ = 1920 px CSS.
     */
    private static final String DEVICE_SCALE_1 = "--force-device-scale-factor=1";

    private DriverFactory() {}

    public static WebDriver initDriver() {
        ConfigReader config = ConfigReader.getInstance();
        boolean headless = config.isHeadless();

        WebDriver driver = switch (config.getBrowser()) {
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=" + WIDTH + "," + HEIGHT);
                options.addArguments(NO_BACKGROUND_THROTTLING);
                options.addArguments(DEVICE_SCALE_1);
                yield new EdgeDriver(options);
            }
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                yield new FirefoxDriver(options);
            }
            default -> {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=" + WIDTH + "," + HEIGHT, "--disable-gpu", "--no-sandbox",
                        "--disable-dev-shm-usage", "--lang=en-US");
                options.addArguments(NO_BACKGROUND_THROTTLING);
                options.addArguments(DEVICE_SCALE_1);
                yield new ChromeDriver(options);
            }
        };

        driver.manage().window().setSize(new Dimension(WIDTH, HEIGHT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getTimeout() * 4L));
        DRIVER.set(driver);
        LOG.info("Khởi tạo {} (headless={})", config.getBrowser(), headless);
        return driver;
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (WebDriverException e) {
                // ChromeDriver đã chết trước khi đóng (Connection reset) — lỗi ở đây làm TestNG bỏ qua các test sau
                LOG.warn("Không đóng được trình duyệt, bỏ qua: {}", e.getMessage().lines().findFirst().orElse(""));
            } finally {
                DRIVER.remove();
            }
        }
    }
}
