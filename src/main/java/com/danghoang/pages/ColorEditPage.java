package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Trang Color Information /admin/colors/edit/{id}?lang=en — không có tab ngôn ngữ. Locator lấy từ DOM thật 04-11-2026. */
public class ColorEditPage extends AdminFormPage {

    private static final String EDIT_FORM = "//form[contains(@action,'/admin/colors/update/')]";

    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Color Information']");
    private static final By LANGUAGE_TABS = AdminLocators.LANGUAGE_LINKS;
    private static final By COLORS_MENU_ACTIVE = AdminLocators.COLORS_MENU_ACTIVE;
    private static final By SIDEBAR_COLORS = AdminLocators.SIDEBAR_COLORS;
    private static final By NAME_INPUT = By.xpath(EDIT_FORM + "//input[@id='name']");
    private static final By CODE_INPUT = By.xpath(EDIT_FORM + "//input[@id='code']");
    private static final By SAVE_BUTTON = By.xpath(EDIT_FORM + "//button[@type='submit']");

    public ColorEditPage() {
        super(PAGE_TITLE, NAME_INPUT);
    }

    @Step("Mở trang sửa màu id {id}")
    public ColorEditPage openById(String id) {
        navigate("/admin/colors/edit/" + id + "?lang=en");
        return waitUntilLoaded();
    }

    public ColorEditPage waitUntilLoaded() {
        waitForForm();
        return this;
    }

    public boolean isColorsMenuActive() {
        return WebUI.isElementDisplayedNow(COLORS_MENU_ACTIVE);
    }

    public boolean hasLanguageTabs() {
        return WebUI.isElementDisplayedNow(LANGUAGE_TABS);
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
        andWaitForNewPage(() -> WebUI.clickElement(SIDEBAR_COLORS));
        return new ColorListPage().waitUntilLoaded();
    }

    public String codeValue() {
        return WebUI.getElementValue(CODE_INPUT);
    }

    public String saveButtonText() {
        return WebUI.getElementTextNow(SAVE_BUTTON);
    }

    @Step("Đổi Name thành [{name}]")
    public void enterName(String name) {
        WebUI.replaceText(NAME_INPUT, name);
    }

    @Step("Đổi Color Code thành [{code}]")
    public void enterCode(String code) {
        WebUI.replaceText(CODE_INPUT, code);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở trang sửa màu")
    public void clickSave() {
        markPage();
        WebUI.clickElementNow(SAVE_BUTTON);
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitUnlessError(this::clickSave, this::waitUntilLoaded);
    }

    /** Nhập Name rồi lưu, chờ trang tải lại. */
    public void saveWithName(String name) {
        enterName(name);
        saveAndWait();
    }
}
