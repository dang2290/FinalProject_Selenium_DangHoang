package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Trang Attribute Value Information /admin/edit-attribute-value/{id} — không có tab ngôn ngữ. Locator lấy từ DOM thật 04-11-2026. */
public class AttributeValueEditPage extends BasePage {

    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Attribute Value Information']");
    private static final By VALUE_INPUT = AdminLocators.VALUE_INPUT;
    private static final By SAVE_BUTTON = AdminLocators.VALUE_SAVE_BUTTON;
    private static final By LANGUAGE_TABS = AdminLocators.LANGUAGE_LINKS;

    public AttributeValueEditPage waitUntilLoaded() {
        WebUI.waitForElementVisible(PAGE_TITLE);
        return this;
    }

    public boolean isTitleDisplayed() {
        return WebUI.isElementDisplayedNow(PAGE_TITLE);
    }

    public boolean hasLanguageTabs() {
        return WebUI.isElementDisplayedNow(LANGUAGE_TABS);
    }

    public boolean isSaveDisplayed() {
        return WebUI.isElementDisplayedNow(SAVE_BUTTON);
    }

    public String valueText() {
        return WebUI.getElementValue(VALUE_INPUT);
    }

    @Step("Đổi giá trị thành [{value}] rồi Save")
    public void changeValue(String value) {
        WebUI.replaceText(VALUE_INPUT, value);
        andWaitForNewPage(() -> WebUI.clickElementNow(SAVE_BUTTON));
        waitUntilLoaded();
    }
}
