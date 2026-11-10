package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Trang Attribute Value Information /admin/edit-attribute-value/{id} — không có tab ngôn ngữ. Locator lấy từ DOM thật 04-11-2026. */
public class AttributeValueEditPage extends BasePage {

    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Attribute Value Information']");
    private static final By VALUE_INPUT = By.id("value");
    private static final By SAVE_BUTTON = By.xpath("//form[.//input[@id='value']]//button[@type='submit']");
    private static final By LANGUAGE_TABS = By.cssSelector("a.nav-link[href*='lang=']");

    public AttributeValueEditPage(WebDriver driver) {
        super(driver);
    }

    public AttributeValueEditPage waitUntilLoaded() {
        waitForVisible(PAGE_TITLE);
        return this;
    }

    public boolean isTitleDisplayed() {
        return isPresentNow(PAGE_TITLE);
    }

    public boolean hasLanguageTabs() {
        return isPresentNow(LANGUAGE_TABS);
    }

    public boolean isSaveDisplayed() {
        return isPresentNow(SAVE_BUTTON);
    }

    public String valueText() {
        return driver.findElement(VALUE_INPUT).getAttribute("value");
    }

    @Step("Đổi giá trị thành [{value}] rồi Save")
    public void changeValue(String value) {
        WebElement field = waitForVisible(VALUE_INPUT);
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        type(VALUE_INPUT, value);
        andWaitForNewPage(() -> driver.findElement(SAVE_BUTTON).click());
        waitUntilLoaded();
    }
}
