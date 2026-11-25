package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Trang Attribute Detail /admin/attributes/{id}: bảng giá trị (#, Value, Action) bên trái, form Add New Attribute Value bên phải.
 * Locator lấy từ DOM thật 04-11-2026.
 */
public class AttributeDetailPage extends AdminListPage {

    /** JS: cuộn phần tử ra giữa, điểm giữa của nó có trúng chính nó không (không bị phần tử khác che). */
    private static final String JS_NOT_COVERED = "const e=arguments[0];e.scrollIntoView({block:'center'});const r=e.getBoundingClientRect();"
            + "const hit=document.elementFromPoint(r.left+r.width/2,r.top+r.height/2);return hit===e||e.contains(hit);";
    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='Attribute Detail']");
    private static final By CARD_HEADERS = AdminLocators.CARD_HEADERS;
    private static final By VALUE_FORM_LABELS = By.xpath("//form[.//input[@id='value']]//label");
    private static final By ATTRIBUTE_NAME_INPUT = By.xpath("//form[.//input[@id='value']]//input[@name='name']");
    private static final By VALUE_INPUT = AdminLocators.VALUE_INPUT;
    private static final By SAVE_BUTTON = AdminLocators.VALUE_SAVE_BUTTON;

    public AttributeDetailPage() {
        super(PAGE_TITLE);
    }

    @Step("Mở trang Attribute Detail theo đường dẫn {url}")
    public AttributeDetailPage openUrl(String url) {
        WebUI.openURL(url);
        return waitUntilLoaded();
    }

    public AttributeDetailPage waitUntilLoaded() {
        waitForListTitle();
        return this;
    }

    /** Tiêu đề hai thẻ: thẻ trái mang tên thuộc tính, thẻ phải "Add New Attribute Value". */
    public List<String> cardHeaders() {
        return WebUI.getAllElementsText(CARD_HEADERS);
    }

    public List<String> valueFormLabels() {
        return WebUI.getAllElementsText(VALUE_FORM_LABELS);
    }

    public String attributeNameValue() {
        return WebUI.getElementValue(ATTRIBUTE_NAME_INPUT);
    }

    public boolean isAttributeNameReadonly() {
        return WebUI.getElementAttribute(ATTRIBUTE_NAME_INPUT, "readonly") != null;
    }

    public String valuePlaceholder() {
        return WebUI.getElementAttribute(VALUE_INPUT, "placeholder");
    }

    public String valueInputText() {
        return WebUI.getElementValue(VALUE_INPUT);
    }

    public boolean isSaveDisplayed() {
        return WebUI.isElementDisplayedNow(SAVE_BUTTON);
    }

    // ---------- Bảng giá trị ----------

    /** Cột Value là cột "tên" của bảng này. */
    public List<String> values() {
        return names();
    }

    @Step("Bấm nút Edit ở dòng giá trị {row} (đếm từ 0)")
    public AttributeValueEditPage editValue(int row) {
        clickEditOfRow(row);
        return new AttributeValueEditPage().waitUntilLoaded();
    }

    public String editValueUrlOfRow(int row) {
        return WebUI.getWebElement(rows().get(row), ROW_EDIT).getAttribute("href");
    }

    /** Nút thứ button (đếm từ 0) của dòng giá trị có bấm trúng được không (không bị phần tử khác che). */
    public boolean isRowButtonClickable(int row, int button) {
        WebElement target = rowButton(row, button);
        return Boolean.TRUE.equals(WebUI.executeJS(JS_NOT_COVERED, target));
    }

    // ---------- Form Add New Attribute Value ----------

    @Step("Nhập Attribute Value: [{value}]")
    public void enterValue(String value) {
        WebUI.setText(VALUE_INPUT, value);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở form Add New Attribute Value")
    public void clickSave() {
        markPage();
        WebUI.clickElement(SAVE_BUTTON);
    }

    @Step("Thêm giá trị [{value}]")
    public void addValue(String value) {
        enterValue(value);
        andWaitUnlessError(this::clickSave, this::waitUntilLoaded);
    }

    public String valueValidationMessage() {
        return WebUI.getValidationMessage(VALUE_INPUT);
    }

    public boolean isValueFocused() {
        return WebUI.isElementFocused(VALUE_INPUT);
    }

    /** Xoá mọi giá trị đang có qua hộp xác nhận (dọn dữ liệu trước khi xoá thuộc tính). */
    @Step("Xoá mọi giá trị của thuộc tính")
    public void deleteAllValues() {
        for (int guard = 0; guard < 20 && rowCount() > 0; guard++) {
            deleteRow(0);
        }
    }

    /** Mở trang sửa giá trị theo đường dẫn của nút Edit (không bấm nút). */
    public AttributeValueEditPage openEditValueByUrl(int row) {
        String url = editValueUrlOfRow(row);
        WebUI.openURL(url);
        WebUI.waitForPageLoaded();
        return new AttributeValueEditPage().waitUntilLoaded();
    }
}
