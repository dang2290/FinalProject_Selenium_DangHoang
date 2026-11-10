package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/** Trang /admin sau khi đăng nhập — locator lấy từ DOM thật 03-11-2026. */
public class DashboardPage extends BasePage {

    private static final By SIDEBAR = By.cssSelector(".aiz-sidebar");
    private static final By DASHBOARD_MENU_ACTIVE = By.xpath(
            "//a[contains(@class,'aiz-side-nav-link') and contains(@class,'active')][normalize-space()='Dashboard']");
    private static final By ACCOUNT_TOGGLE = By.xpath("(//div[contains(@class,'aiz-topbar')]//*[@data-toggle='dropdown'])[last()]");
    private static final By ACCOUNT_NAME = By.cssSelector(".aiz-topbar .d-block.fw-500");
    private static final By ACCOUNT_MENU_ITEMS = By.cssSelector(".aiz-topbar .dropdown-menu.show a");
    private static final By LOGOUT_ITEM = By.xpath("//div[contains(@class,'aiz-topbar')]//div[contains(@class,'dropdown-menu')]//a[normalize-space()='Logout']");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    @Step("Chờ trang Dashboard tải xong")
    public DashboardPage waitUntilLoaded() {
        waitForUrlContains("/admin");
        waitForVisible(SIDEBAR);
        return this;
    }

    @Step("Mở trang Dashboard")
    public DashboardPage open() {
        navigate("/admin");
        return waitUntilLoaded();
    }

    public boolean isDashboardMenuActive() {
        return isVisible(DASHBOARD_MENU_ACTIVE);
    }

    public boolean isSidebarDisplayedNow() {
        return isPresentNow(SIDEBAR);
    }

    public String accountName() {
        return getText(ACCOUNT_NAME);
    }

    public String accountRole() {
        return (String) js("return arguments[0].nextElementSibling.innerText.trim();", driver.findElement(ACCOUNT_NAME));
    }

    @Step("Mở menu tài khoản góc trên phải")
    public List<String> openAccountMenu() {
        click(ACCOUNT_TOGGLE);
        wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_MENU_ITEMS));
        return driver.findElements(ACCOUNT_MENU_ITEMS).stream().map(e -> e.getText().trim()).toList();
    }

    @Step("Đăng xuất bằng menu tài khoản")
    public void logout() {
        openAccountMenu();
        click(LOGOUT_ITEM);
        waitForUrlContains("/login");
    }
}
