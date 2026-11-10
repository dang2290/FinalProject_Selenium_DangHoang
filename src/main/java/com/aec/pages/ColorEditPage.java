package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Trang Color Information /admin/colors/edit/{id}?lang=en — không có tab ngôn ngữ. Locator lấy từ DOM thật 04-11-2026. */
public class ColorEditPage extends BasePage {

    private static final String EDIT_FORM = "//form[contains(@action,'/admin/colors/update/')]";

    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Color Information']");
    private static final By LANGUAGE_TABS = By.cssSelector("a.nav-link[href*='lang=']");
    private static final By COLORS_MENU_ACTIVE = By.cssSelector(".aiz-side-nav-link.active[href$='/admin/colors']");
    private static final By SIDEBAR_COLORS = By.cssSelector("a.aiz-side-nav-link[href$='/admin/colors']");
    private static final By NAME_INPUT = By.xpath(EDIT_FORM + "//input[@id='name']");
    private static final By CODE_INPUT = By.xpath(EDIT_FORM + "//input[@id='code']");
    private static final By SAVE_BUTTON = By.xpath(EDIT_FORM + "//button[@type='submit']");
    private static final By ERROR_MESSAGES = By.cssSelector("div.alert-danger li");

    public ColorEditPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang sửa màu id {id}")
    public ColorEditPage openById(String id) {
        navigate("/admin/colors/edit/" + id + "?lang=en");
        return waitUntilLoaded();
    }

    public ColorEditPage waitUntilLoaded() {
        waitForVisible(PAGE_TITLE);
        return this;
    }

    public boolean isTitleDisplayed() {
        return isPresentNow(PAGE_TITLE);
    }

    public boolean isColorsMenuActive() {
        return isPresentNow(COLORS_MENU_ACTIVE);
    }

    public boolean hasLanguageTabs() {
        return isPresentNow(LANGUAGE_TABS);
    }

    public boolean isOnEditPage() {
        return currentPath().startsWith("/admin/colors/edit/");
    }

    /** Id màu đang sửa, lấy từ đường dẫn. */
    public String colorId() {
        return currentPath().replaceAll(".*/edit/(\\d+).*", "$1");
    }

    @Step("Bấm Colors ở sidebar")
    public ColorListPage clickColorsInSidebar() {
        andWaitForNewPage(() -> click(SIDEBAR_COLORS));
        return new ColorListPage(driver).waitUntilLoaded();
    }

    public String nameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String codeValue() {
        return driver.findElement(CODE_INPUT).getAttribute("value");
    }

    public String saveButtonText() {
        return driver.findElement(SAVE_BUTTON).getText().trim();
    }

    @Step("Đổi Name thành [{name}]")
    public void enterName(String name) {
        replaceText(NAME_INPUT, name);
    }

    @Step("Đổi Color Code thành [{code}]")
    public void enterCode(String code) {
        replaceText(CODE_INPUT, code);
    }

    /** Ô đã có sẵn giá trị: chọn hết rồi gõ đè. Chuỗi rỗng = xoá hết. */
    private void replaceText(By locator, String value) {
        WebElement field = waitForVisible(locator);
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        if (!value.isEmpty()) {
            type(locator, value);
        }
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở trang sửa màu")
    public void clickSave() {
        driver.findElement(SAVE_BUTTON).click();
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitForNewPage(this::clickSave);
        if (!isServerErrorPage()) {
            waitUntilLoaded();
        }
    }

    public List<String> errorMessages() {
        return driver.findElements(ERROR_MESSAGES).stream().map(e -> e.getText().trim()).toList();
    }

    public String nameValidationMessage() {
        return validationMessage(NAME_INPUT);
    }

    public boolean isNameFocused() {
        return driver.switchTo().activeElement().equals(driver.findElement(NAME_INPUT));
    }

    /** Trang 419 PAGE EXPIRED — gửi form khi phiên đã hết. */
    public boolean isPageExpired() {
        return driver.getTitle().contains("Page Expired") || driver.getPageSource().contains("419");
    }
}
