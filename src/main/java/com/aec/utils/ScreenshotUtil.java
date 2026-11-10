package com.aec.utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ScreenshotUtil {

    private static final Path SCREENSHOT_DIR = Path.of("reports", "screenshots");

    private ScreenshotUtil() {}

    /** Đính ảnh vào Allure và lưu bản sao vào reports/screenshots/. */
    public static void captureAndAttach(WebDriver driver, String attachmentName, String fileName) {
        if (driver == null) {
            return;
        }
        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(attachmentName, "image/png", new ByteArrayInputStream(png), "png");
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            Files.write(SCREENSHOT_DIR.resolve(fileName + ".png"), png);
        } catch (IOException e) {
            throw new IllegalStateException("Không lưu được ảnh " + fileName, e);
        }
    }
}
