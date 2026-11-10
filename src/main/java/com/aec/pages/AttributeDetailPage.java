package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Trang Attribute Detail /admin/attributes/{id}: bảng giá trị (#, Value, Action) bên trái, form Add New Attribute Value bên phải.
 * Locator lấy từ DOM thật 04-11-2026.
 */
public class AttributeDetailPage extends AdminListPage {

    private static final int COL_VALUE = 2;

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='Attribute Detail']");
    private static final By CARD_HEADERS = By.cssSelector(".card-header");
    private static final By VALUE_FORM_LABELS = By.xpath("//form[.//input[@id='value']]//label");
    private static final By ATTRIBUTE_NAME_INPUT = By.xpath("//form[.//input[@id='value']]//input[@name='name']");
    private static final By VALUE_INPUT = By.id("value");
    private static final By SAVE_BUTTON = By.xpath("//form[.//input[@id='value']]//button[@type='submit']");
    private static final By ROW_BUTTONS = By.cssSelector("td a.btn-circle");

    public AttributeDetailPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang Attribute Detail theo đường dẫn {url}")
    public AttributeDetailPage openUrl(String url) {
        driver.get(url);
        return waitUntilLoaded();
    }

    public AttributeDetailPage waitUntilLoaded() {
        waitForVisible(PAGE_TITLE);
        waitForTableReady();
        return this;
    }

    @Override
    protected void waitForListTitle() {
        waitUntilLoaded();
    }

    public boolean isTitleDisplayed() {
        return isPresentNow(PAGE_TITLE);
    }

    /** Tiêu đề hai thẻ: thẻ trái mang tên thuộc tính, thẻ phải "Add New Attribute Value". */
    public List<String> cardHeaders() {
        return texts(CARD_HEADERS);
    }

    public List<String> valueFormLabels() {
        return texts(VALUE_FORM_LABELS);
    }

    public String attributeNameValue() {
        return driver.findElement(ATTRIBUTE_NAME_INPUT).getAttribute("value");
    }

    public boolean isAttributeNameReadonly() {
        return driver.findElement(ATTRIBUTE_NAME_INPUT).getAttribute("readonly") != null;
    }

    public String valuePlaceholder() {
        return driver.findElement(VALUE_INPUT).getAttribute("placeholder");
    }

    public String valueInputText() {
        return driver.findElement(VALUE_INPUT).getAttribute("value");
    }

    public boolean isSaveDisplayed() {
        return isPresentNow(SAVE_BUTTON);
    }

    // ---------- Bảng giá trị ----------

    public List<String> values() {
        return columnTexts(COL_VALUE);
    }

    /** Chú thích (title) các nút ở cột Action của dòng giá trị thứ row (đếm từ 0). */
    public List<String> rowButtonTitles(int row) {
        return rows().get(row).findElements(ROW_BUTTONS).stream().map(e -> e.getAttribute("title")).toList();
    }

    @Step("Bấm nút Edit ở dòng giá trị {row} (đếm từ 0)")
    public AttributeValueEditPage editValue(int row) {
        clickEditOfRow(row);
        return new AttributeValueEditPage(driver).waitUntilLoaded();
    }

    public String editValueUrlOfRow(int row) {
        return rows().get(row).findElement(ROW_EDIT).getAttribute("href");
    }

    /** Nút thứ button (đếm từ 0) của dòng giá trị có bấm trúng được không (không bị phần tử khác che). */
    public boolean isRowButtonClickable(int row, int button) {
        WebElement target = rows().get(row).findElements(ROW_BUTTONS).get(button);
        return Boolean.TRUE.equals(js("const e=arguments[0];e.scrollIntoView({block:'center'});const r=e.getBoundingClientRect();"
                + "const hit=document.elementFromPoint(r.left+r.width/2,r.top+r.height/2);return hit===e||e.contains(hit);", target));
    }

    // ---------- Form Add New Attribute Value ----------

    @Step("Nhập Attribute Value: [{value}]")
    public void enterValue(String value) {
        type(VALUE_INPUT, value);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở form Add New Attribute Value")
    public void clickSave() {
        click(SAVE_BUTTON);
    }

    @Step("Thêm giá trị [{value}]")
    public void addValue(String value) {
        enterValue(value);
        andWaitForNewPage(this::clickSave);
        if (!isServerErrorPage()) {
            waitUntilLoaded();
        }
    }

    public String valueValidationMessage() {
        return validationMessage(VALUE_INPUT);
    }

    public boolean isValueFocused() {
        return driver.switchTo().activeElement().equals(driver.findElement(VALUE_INPUT));
    }

    /** Xoá mọi giá trị đang có qua hộp xác nhận (dọn dữ liệu trước khi xoá thuộc tính). */
    @Step("Xoá mọi giá trị của thuộc tính")
    public void deleteAllValues() {
        for (int guard = 0; guard < 20 && rowCount() > 0; guard++) {
            deleteRow(0);
        }
    }
}
