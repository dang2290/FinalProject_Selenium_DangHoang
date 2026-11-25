package com.danghoang.reporting;

import com.danghoang.drivers.DriverManager;
import com.danghoang.utils.LogUtils;
import com.danghoang.utils.ScreenshotUtil;
import io.qameta.allure.Allure;

/** Mọi thứ đính vào Allure (chữ, ảnh) đi qua đây. */
public final class AllureManager {

    private AllureManager() {}

    public static void saveTextLog(String name, String content) {
        Allure.addAttachment(name, "text/plain", content, "txt");
    }

    /** Chụp hỏng (trình duyệt đã chết, đang có alert) thì chỉ cảnh báo, không che lỗi thật của test. */
    public static void saveScreenshot(String name, String fileName) {
        try {
            ScreenshotUtil.captureAndAttach(DriverManager.getDriver(), name, fileName);
        } catch (RuntimeException e) {
            LogUtils.warn("Không chụp được ảnh {}: {}", name, e.getClass().getSimpleName());
        }
    }
}
