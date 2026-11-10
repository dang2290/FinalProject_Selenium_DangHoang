package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/** Trang Attribute Information /admin/attributes/edit/{id}?lang=en|vn — locator lấy từ DOM thật 04-11-2026. */
public class AttributeEditPage extends BasePage {

    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Attribute Information']");
    private static final By LANGUAGE_TABS = By.cssSelector("a.nav-link[href*='/admin/attributes/edit/']");
    private static final By ACTIVE_TAB = By.cssSelector("a.nav-link.active[href*='/admin/attributes/edit/']");
    private static final By TRANSLATABLE_ICON = By.xpath("//form[.//input[@id='name']]//label/i[contains(@class,'la-language')]");
    private static final By NAME_INPUT = By.id("name");
    private static final By SAVE_BUTTON = By.xpath("//form[.//input[@id='name']]//button[@type='submit']");

    public AttributeEditPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang sửa thuộc tính theo đường dẫn {url}")
    public AttributeEditPage openUrl(String url) {
        driver.get(url);
        return waitUntilLoaded();
    }

    public AttributeEditPage waitUntilLoaded() {
        waitForVisible(PAGE_TITLE);
        return this;
    }

    public boolean isTitleDisplayed() {
        return isPresentNow(PAGE_TITLE);
    }

    public boolean isOnEditPage() {
        return currentPath().startsWith("/admin/attributes/edit/");
    }

    public List<String> languageTabs() {
        return driver.findElements(LANGUAGE_TABS).stream().map(e -> e.getText().trim()).toList();
    }

    public String activeTab() {
        return getText(ACTIVE_TAB);
    }

    @Step("Chọn tab ngôn ngữ {tab}")
    public void clickTab(String tab) {
        WebElement target = driver.findElements(LANGUAGE_TABS).stream()
                .filter(e -> e.getText().trim().equals(tab)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Không có tab " + tab));
        andWaitForNewPage(target::click);
        waitUntilLoaded();
    }

    /** Chú thích của biểu tượng ngôn ngữ cạnh nhãn Name (hiện khi rê chuột). */
    public String translatableTooltip() {
        WebElement icon = driver.findElement(TRANSLATABLE_ICON);
        new Actions(driver).moveToElement(icon).perform();
        return icon.getAttribute("title");
    }

    public String nameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String saveButtonText() {
        return driver.findElement(SAVE_BUTTON).getText().trim();
    }

    /** Ô đã có sẵn giá trị: chọn hết rồi gõ đè. Chuỗi rỗng = xoá hết. */
    @Step("Đổi Name thành [{name}]")
    public void enterName(String name) {
        WebElement field = waitForVisible(NAME_INPUT);
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        if (!name.isEmpty()) {
            type(NAME_INPUT, name);
        }
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở trang sửa thuộc tính")
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
