package com.danghoang.pages;

import com.danghoang.utils.WaitHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

/**
 * Thao tác ở mức trình duyệt — điều hướng, cửa sổ, tab, hộp thoại — để thân test không gọi WebDriver trực tiếp.
 */
public class BrowserWindow extends BasePage {

    public BrowserWindow(WebDriver driver) {
        super(driver);
    }

    @Step("Mở đường dẫn {path}")
    public void openPath(String path) {
        navigate(path);
    }

    @Step("Mở {url}")
    public void openUrl(String url) {
        log.info("Mở {}", url);
        driver.get(url);
        WaitHelper.waitForPageReady(driver);
    }

    /** Bấm Back rồi chờ thanh địa chỉ đổi và trang hiện xong — trang lấy từ bộ nhớ đệm cũng tính là đã hiện. */
    @Step("Bấm Back của trình duyệt")
    public void back() {
        String before = driver.getCurrentUrl();
        driver.navigate().back();
        try {
            wait.until(d -> !before.equals(d.getCurrentUrl()));
        } catch (TimeoutException e) {
            log.warn("Bấm Back nhưng thanh địa chỉ không đổi: {}", before);
        }
        WaitHelper.waitForPageReady(driver);
    }

    @Step("Tải lại trang")
    public void refresh() {
        andWaitForNewPage(() -> driver.navigate().refresh());
    }

    @Step("Đặt cửa sổ trình duyệt {width}×{height}")
    public void resize(int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
    }

    public String currentHandle() {
        return driver.getWindowHandle();
    }

    /** Mở tab trống mới, chuyển sang đó; trả về handle của tab mới. */
    @Step("Mở tab mới")
    public String openNewTab() {
        driver.switchTo().newWindow(WindowType.TAB);
        return driver.getWindowHandle();
    }

    @Step("Chuyển sang tab khác")
    public void switchTo(String handle) {
        driver.switchTo().window(handle);
    }

    @Step("Đóng tab hiện tại, quay về tab trước")
    public void closeTabAndSwitchTo(String handle) {
        driver.close();
        driver.switchTo().window(handle);
    }

    /** Đóng hộp thoại alert đang mở (nếu có) — dùng khi dọn dữ liệu. */
    public void dismissAlertIfOpen() {
        try {
            driver.switchTo().alert().dismiss();
        } catch (NoAlertPresentException e) {
            log.debug("Không có hộp thoại cần đóng");
        }
    }
}
