package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

/** Trang Product Bulk Upload /admin/product-bulk-upload/index — chỉ đọc giao diện, không tải tệp. Locator lấy từ DOM thật 04-11-2026. */
public class ProductBulkUploadPage extends BasePage {

    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Product Bulk Upload']");
    private static final By SECTION_TITLES = AdminLocators.CARD_HEADER_TITLES;
    private static final By STEP_LABELS = By.xpath("//strong[starts-with(normalize-space(),'Step')]");
    private static final By BUTTONS = By.cssSelector(".aiz-main-content button");
    private static final By FILE_INPUT = By.name("bulk_file");
    private static final By UPLOAD_BUTTON = By.xpath("//form[contains(@action,'/bulk-product-upload')]//button[@type='submit']");

    public ProductBulkUploadPage waitUntilLoaded() {
        WebUI.waitForElementVisible(PAGE_TITLE);
        return this;
    }

    @Step("Mở Product Bulk Upload")
    public ProductBulkUploadPage open() {
        navigate("/admin/product-bulk-upload/index");
        return waitUntilLoaded();
    }

    public List<String> sectionTitles() {
        return WebUI.getAllElementsText(SECTION_TITLES);
    }

    public List<String> stepLabels() {
        return WebUI.getAllElementsText(STEP_LABELS);
    }

    public List<String> buttonTexts() {
        return WebUI.getAllElementsText(BUTTONS).stream().filter(t -> !t.isEmpty()).toList();
    }

    public boolean hasFileInput() {
        return WebUI.checkElementExist(FILE_INPUT);
    }

    @Step("Bấm Upload CSV khi chưa chọn tệp")
    public void clickUpload() {
        markPage();
        WebUI.clickElement(UPLOAD_BUTTON);
    }

    public String fileValidationMessage() {
        return WebUI.getValidationMessage(FILE_INPUT);
    }
}
