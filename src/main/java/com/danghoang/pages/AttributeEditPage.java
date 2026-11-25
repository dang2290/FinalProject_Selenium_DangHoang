package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Trang Attribute Information /admin/attributes/edit/{id}?lang=en|vn — locator lấy từ DOM thật 04-11-2026. */
public class AttributeEditPage extends TranslatableFormPage {

    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Attribute Information']");
    private static final By TRANSLATABLE_ICON = By.xpath("//form[.//input[@id='name']]//label/i[contains(@class,'la-language')]");
    private static final By NAME_INPUT = AdminLocators.NAME_INPUT;
    private static final By SAVE_BUTTON = By.xpath("//form[.//input[@id='name']]//button[@type='submit']");

    public AttributeEditPage() {
        super(PAGE_TITLE, NAME_INPUT, "/admin/attributes/edit/");
    }

    @Step("Mở trang sửa thuộc tính theo đường dẫn {url}")
    public AttributeEditPage openUrl(String url) {
        WebUI.openURL(url);
        return waitUntilLoaded();
    }

    public AttributeEditPage waitUntilLoaded() {
        waitForForm();
        return this;
    }

    public boolean isOnEditPage() {
        return currentPath().startsWith("/admin/attributes/edit/");
    }

    /** Chú thích của biểu tượng ngôn ngữ cạnh nhãn Name (hiện khi rê chuột). */
    public String translatableTooltip() {
        return WebUI.getTooltipOnHover(WebUI.getWebElement(TRANSLATABLE_ICON), "biểu tượng ngôn ngữ");
    }

    public String saveButtonText() {
        return WebUI.getElementTextNow(SAVE_BUTTON);
    }

    @Step("Đổi Name thành [{name}]")
    public void enterName(String name) {
        WebUI.replaceText(NAME_INPUT, name);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở trang sửa thuộc tính")
    public void clickSave() {
        markPage();
        WebUI.clickElementNow(SAVE_BUTTON);
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitUnlessError(this::clickSave, this::waitUntilLoaded);
    }
}
