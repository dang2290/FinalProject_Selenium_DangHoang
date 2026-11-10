package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/** Phần chung của các trang cửa hàng: popup quảng cáo, khung cookie, thông báo nổi. */
public abstract class StorefrontPage extends BasePage {

    private static final By AD_POPUP = By.cssSelector(".website-popup");
    private static final By AD_POPUP_CLOSE = By.cssSelector(".website-popup button[data-key='website-popup']");
    private static final By COOKIE_ALERT = By.cssSelector(".aiz-cookie-alert.show");
    private static final Duration POPUP_APPEAR_TIMEOUT = Duration.ofSeconds(4);

    protected StorefrontPage(WebDriver driver) {
        super(driver);
    }

    /** Popup có thể hiện sau khi trang tải xong — chờ ngắn, hiện thì đóng bằng nút X. */
    @Step("Đóng popup quảng cáo nếu hiện")
    public void closeAdPopupIfShown() {
        try {
            new WebDriverWait(driver, POPUP_APPEAR_TIMEOUT)
                    .until(ExpectedConditions.visibilityOfElementLocated(AD_POPUP_CLOSE)).click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(AD_POPUP));
        } catch (TimeoutException e) {
            log.info("Không có popup quảng cáo");
        }
    }

    protected void openStorefront(String path, By readyLocator) {
        navigate(path);
        waitForVisible(readyLocator);
        closeAdPopupIfShown();
    }

    public boolean isCookieAlertShown() {
        return isPresentNow(COOKIE_ALERT);
    }

    /** Thông báo nổi mới nhất có bị khung cookie che phần nào không. */
    public boolean isNotifyCoveredByCookieAlert() {
        getNotifyMessage();
        List<WebElement> cookie = driver.findElements(COOKIE_ALERT);
        if (cookie.isEmpty()) {
            return false;
        }
        List<WebElement> toasts = driver.findElements(NOTIFY_MESSAGE);
        @SuppressWarnings("unchecked")
        Map<String, Number> overlap = (Map<String, Number>) js(
                "const a=arguments[0].getBoundingClientRect(),b=arguments[1].getBoundingClientRect();"
                        + "return {w:Math.max(0,Math.min(a.right,b.right)-Math.max(a.left,b.left)),"
                        + "h:Math.max(0,Math.min(a.bottom,b.bottom)-Math.max(a.top,b.top))};",
                toasts.get(toasts.size() - 1), cookie.get(0));
        return overlap.get("w").doubleValue() > 0 && overlap.get("h").doubleValue() > 0;
    }

    public boolean isOn(String path) {
        return currentPath().equals(path);
    }
}
