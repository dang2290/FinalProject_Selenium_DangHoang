package com.danghoang.drivers;

import org.openqa.selenium.WebDriver;

/**
 * Nơi DUY NHẤT giữ WebDriver: mỗi thread một driver nên chạy song song không đè nhau.
 * Nhờ vậy WebUI là static và Page không cần nhận driver ở constructor.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {}

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }
}
