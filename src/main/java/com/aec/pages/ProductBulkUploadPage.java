package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/** Trang Product Bulk Upload /admin/product-bulk-upload/index — chỉ đọc giao diện, không tải tệp. Locator lấy từ DOM thật 04-11-2026. */
public class ProductBulkUploadPage extends BasePage {

    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Product Bulk Upload']");
    private static final By SECTION_TITLES = By.cssSelector(".card-header h5");
    private static final By STEP_LABELS = By.xpath("//strong[starts-with(normalize-space(),'Step')]");
    private static final By BUTTONS = By.cssSelector(".aiz-main-content button");
    private static final By FILE_INPUT = By.name("bulk_file");
    private static final By UPLOAD_BUTTON = By.xpath("//form[contains(@action,'/bulk-product-upload')]//button[@type='submit']");

    public ProductBulkUploadPage(WebDriver driver) {
        super(driver);
    }

    public ProductBulkUploadPage waitUntilLoaded() {
        waitForVisible(PAGE_TITLE);
        return this;
    }

    @Step("Mở Product Bulk Upload")
    public ProductBulkUploadPage open() {
        navigate("/admin/product-bulk-upload/index");
        return waitUntilLoaded();
    }

    public List<String> sectionTitles() {
        return driver.findElements(SECTION_TITLES).stream().map(e -> e.getText().trim()).toList();
    }

    public List<String> stepLabels() {
        return driver.findElements(STEP_LABELS).stream().map(e -> e.getText().trim()).toList();
    }

    public List<String> buttonTexts() {
        return driver.findElements(BUTTONS).stream().map(e -> e.getText().trim()).filter(t -> !t.isEmpty()).toList();
    }

    public boolean hasFileInput() {
        return !driver.findElements(FILE_INPUT).isEmpty();
    }

    @Step("Bấm Upload CSV khi chưa chọn tệp")
    public void clickUpload() {
        markPage();
        click(UPLOAD_BUTTON);
    }

    public String fileValidationMessage() {
        return validationMessage(FILE_INPUT);
    }
}
