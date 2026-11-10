package com.aec.utils;

import com.aec.config.ConfigReader;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Điều kiện chờ bổ sung ngoài ExpectedConditions — không có hard sleep. */
public final class WaitHelper {

    private WaitHelper() {}

    /** Bỏ qua stale element khi chờ: lưới/bảng vẽ lại giữa hai lần đọc thì đọc lại ở lần chờ kế tiếp. */
    public static WebDriverWait create(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInstance().getTimeout()));
        wait.ignoring(StaleElementReferenceException.class);
        return wait;
    }

    public static void waitForPageReady(WebDriver driver) {
        create(driver).until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
    }
}
