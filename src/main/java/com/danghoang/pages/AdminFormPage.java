package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.util.List;

/**
 * Phần chung của form tạo / sửa trong trang quản trị: tiêu đề, ô Name, khối lỗi đỏ, ô tag.
 * Form có tab ngôn ngữ thì kế thừa {@link TranslatableFormPage}.
 */
public abstract class AdminFormPage extends BasePage {

    private static final By ERROR_MESSAGES = By.cssSelector("div.alert-danger li");

    private final By title;
    private final By nameInput;

    /** title: tiêu đề riêng của form · nameInput: ô Name của form đó. */
    protected AdminFormPage(By title, By nameInput) {
        super();
        this.title = title;
        this.nameInput = nameInput;
    }

    /** Chờ form sẵn sàng. */
    protected void waitForForm() {
        WebUI.waitForElementVisible(title);
    }

    public boolean isTitleDisplayed() {
        return WebUI.isElementDisplayedNow(title);
    }

    public String nameValue() {
        return WebUI.getElementValue(nameInput);
    }

    public String nameValidationMessage() {
        return WebUI.getValidationMessage(nameInput);
    }

    public boolean isNameFocused() {
        return WebUI.isElementFocused(nameInput);
    }

    /** Tiêu đề các khối (thẻ) của form, theo thứ tự — bỏ tiêu đề mẫu trong thanh công cụ của ô soạn thảo. */
    public List<String> sectionTitles() {
        return WebUI.getAllTextsByJS(".card > .card-header > h5");
    }

    /** Các dòng lỗi trong khối đỏ của form (rỗng nếu không có). */
    public List<String> errorMessages() {
        return WebUI.getAllElementsText(ERROR_MESSAGES);
    }

    @Step("Thêm tag {tag}")
    public void addTag(String tag) {
        WebUI.clickAndType(AdminLocators.TAG_INPUT, tag, Keys.ENTER);
        WebUI.waitUntil("tag xuất hiện", () -> WebUI.checkElementExist(AdminLocators.TAGS));
    }
}
