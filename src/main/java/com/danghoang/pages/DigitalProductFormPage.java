package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

/**
 * Form Add New Digital Product /admin/digitalproducts/create (nút Save Product) và Edit Digital Product
 * /admin/digitalproducts/edit/{id}?lang=en|vn (tab ngôn ngữ, nút Update Product). Locator lấy từ DOM thật 04-11-2026.
 */
public class DigitalProductFormPage extends TranslatableFormPage {

    /** JS: chữ mọi label trong form, gộp khoảng trắng, bỏ trùng và bỏ rỗng. */
    private static final String JS_UNIQUE_LABELS = "return [...new Set([...arguments[0].querySelectorAll('label')]"
            + ".map(l => l.textContent.replace(/\\s+/g,' ').trim()).filter(t => t))];";
    private static final By CREATE_TITLE = By.xpath("//h5[normalize-space()='Add New Digital Product']");
    private static final By EDIT_TITLE = By.xpath("//h5[normalize-space()='Edit Digital Product']");
    private static final By FORM = By.xpath("//form[.//input[@name='name']][.//button[@type='submit']]");
    private static final By NAME_INPUT = By.xpath("//form//input[@name='name']");
    private static final By SUBMIT = By.xpath("//form[.//input[@name='name']]//button[@type='submit']");

    public DigitalProductFormPage() {
        super(EDIT_TITLE, NAME_INPUT, "lang=");
    }

    @Step("Mở Add New Digital Product")
    public DigitalProductFormPage openCreate() {
        navigate("/admin/digitalproducts/create");
        WebUI.waitForElementVisible(CREATE_TITLE);
        return this;
    }

    @Step("Mở Edit Digital Product id {id} tab {lang}")
    public DigitalProductFormPage openEdit(String id, String lang) {
        navigate("/admin/digitalproducts/edit/" + id + "?lang=" + lang);
        return waitUntilEditLoaded();
    }

    public DigitalProductFormPage waitUntilEditLoaded() {
        waitForForm();
        return this;
    }

    /** Form này có hai tiêu đề (tạo / sửa) — tiêu đề của lớp cha là tiêu đề trang sửa. */
    public boolean isEditTitleDisplayed() {
        return isTitleDisplayed();
    }

    public boolean isOnEditPage() {
        return currentPath().startsWith("/admin/digitalproducts/edit/");
    }

    /** Nhãn mọi ô của form (bỏ trùng, theo thứ tự). */
    @SuppressWarnings("unchecked")
    public List<String> fieldLabels() {
        return (List<String>) WebUI.executeJS(JS_UNIQUE_LABELS, WebUI.getWebElement(FORM));
    }

    public boolean hasField(FormField field) {
        return WebUI.checkElementExist(field.locator());
    }

    public String submitText() {
        return WebUI.getElementTextNow(SUBMIT);
    }

    @Step("Nhập {field}: [{value}]")
    public void enterField(FormField field, String value) {
        WebUI.scrollAndSetText(inputInForm(field), value);
    }

    private By inputInForm(FormField field) {
        return By.xpath("//form//input[@name='" + field + "']");
    }

    public void enterName(String name) {
        enterField(FormField.NAME, name);
    }

    /** PC-5: Name, Tags auto, Unit price, Purchase price 5, Discount 0 — Category để mặc định, không chọn tệp. */
    @Step("Nhập dữ liệu tối thiểu cho sản phẩm số {name}")
    public void fillRequired(String name, String unitPrice) {
        enterName(name);
        addTag("auto");
        enterField(FormField.UNIT_PRICE, unitPrice);
        enterField(FormField.PURCHASE_PRICE, "5");
        enterField(FormField.DISCOUNT, "0");
    }

    @Step("Bấm {buttonText}")
    public void clickSubmit(String buttonText) {
        markPage();
        WebUI.scrollAndClickElement(SUBMIT);
    }

    @Step("Bấm nút lưu và chờ trang tải lại")
    public void saveAndWait() {
        String text = submitText();
        andWaitForNewPage(() -> clickSubmit(text));
    }

    /** Tạo sản phẩm số hợp lệ: lưu xong hệ thống chuyển về Digital Products. */
    public DigitalProductListPage createProduct(String name, String unitPrice) {
        fillRequired(name, unitPrice);
        saveAndWait();
        return arrivedAt(new DigitalProductListPage());
    }
}
