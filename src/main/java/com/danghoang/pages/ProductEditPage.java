package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

/**
 * Trang Edit Product /admin/products/admin/{id}/edit?lang=en|vn — tab English / Vietnamese, ô Slug, một nút Update Product,
 * form gửi tới /admin/products/update/{id}. Locator lấy từ DOM thật 04-11-2026.
 */
public class ProductEditPage extends TranslatableFormPage {

    /** JS: chữ lựa chọn đang chọn của thẻ select name = arguments[0] ("" nếu không có thẻ hoặc chưa chọn). */
    private static final String JS_SELECTED_OPTION_TEXT = "const s=document.querySelector('select[name=\"'+arguments[0]+'\"]');"
            + "return s && s.selectedOptions.length ? s.selectedOptions[0].textContent.trim() : '';";
    private static final String EDIT_FORM = "//form[contains(@action,'/admin/products/update/')]";

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='Edit Product']");
    private static final By NAME_INPUT = By.xpath(EDIT_FORM + "//*[@name='name']");
    private static final By UPDATE_BUTTON = By.xpath(EDIT_FORM + "//button[@type='submit']");
    private static final By VARIANT_NAMES = By.cssSelector("#sku_combination tr.variant label");

    public ProductEditPage() {
        super(PAGE_TITLE, NAME_INPUT, "/edit?lang=");
    }

    @Step("Mở trang sửa sản phẩm id {id} tab {lang}")
    public ProductEditPage openById(String id, String lang) {
        navigate("/admin/products/admin/" + id + "/edit?lang=" + lang);
        return waitUntilLoaded();
    }

    public ProductEditPage waitUntilLoaded() {
        waitForForm();
        return this;
    }

    public boolean isOnEditPage() {
        return currentPath().matches("/admin/products/admin/\\d+/edit");
    }

    public String productId() {
        return currentPath().replaceAll(".*/admin/(\\d+)/edit.*", "$1");
    }

    public String fieldValue(FormField field) {
        return WebUI.getElementValue(fieldInForm(field));
    }

    private By fieldInForm(FormField field) {
        return By.xpath(EDIT_FORM + "//*[@name='" + field + "']");
    }

    private By inputInForm(FormField field) {
        return By.xpath(EDIT_FORM + "//input[@name='" + field + "']");
    }

    /** Chữ của lựa chọn đang chọn ở thẻ select gốc (Category, Brand, Discount type…). */
    public String selectedOptionText(FormField field) {
        return (String) WebUI.executeJS(JS_SELECTED_OPTION_TEXT, field.toString());
    }

    public int updateButtonCount() {
        return WebUI.getElementCount(UPDATE_BUTTON);
    }

    public String updateButtonText() {
        return WebUI.getElementTextNow(UPDATE_BUTTON);
    }

    @Step("Đổi {field} thành [{value}]")
    public void enterField(FormField field, String value) {
        WebUI.scrollAndSetText(inputInForm(field), value);
    }

    public void enterName(String name) {
        enterField(FormField.NAME, name);
    }

    /** Bấm Update Product — không chờ (dùng khi phiên có thể đã hết). */
    @Step("Bấm Update Product")
    public void clickUpdate() {
        // Như form tạo: gõ Name / Unit price gọi AJAX update_sku() — chờ hết request nền rồi mới gửi form
        waitForAjaxIdle();
        WebUI.scrollAndClickElement(UPDATE_BUTTON);
    }

    @Step("Bấm Update Product và chờ trang tải lại")
    public void saveAndWait() {
        andWaitUnlessError(this::clickUpdate, this::waitUntilLoaded);
    }

    /** Bảng tổ hợp được dựng bằng AJAX sau khi trang tải — chờ có dòng rồi mới đọc. */
    public List<String> variantNames() {
        WebUI.waitUntil("bảng tổ hợp có dòng", () -> WebUI.checkElementExist(VARIANT_NAMES));
        return WebUI.getWebElements(VARIANT_NAMES).stream().map(e -> e.getAttribute("textContent").trim()).toList();
    }

    public List<String> variantPrices() {
        return WebUI.getAllValuesByJS("#sku_combination tr.variant input[name^=\"price_\"]");
    }

    /** Ô Thumbnail đã có id ảnh thì chờ ảnh xem trước dựng xong rồi mới đếm. */
    public int thumbnailPreviewCount() {
        return previewCountWhenReady(fieldValue(FormField.THUMBNAIL), AdminLocators.THUMBNAIL_PREVIEW);
    }

    /** Tên tệp của ảnh Thumbnail đang xem trước — gọi sau thumbnailPreviewCount() để ảnh đã dựng xong. */
    public String thumbnailPreviewTitle() {
        return WebUI.getElementAttribute(AdminLocators.THUMBNAIL_PREVIEW, "title");
    }
}
