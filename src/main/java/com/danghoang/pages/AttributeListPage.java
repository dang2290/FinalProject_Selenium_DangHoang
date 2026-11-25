package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.util.List;

/**
 * Trang All Attributes /admin/attributes — bảng (#, Name, Values, Options) bên trái, form Add New Attribute bên phải.
 * Bảng hiện mọi thuộc tính trên một trang (~600 dòng), không có ô tìm kiếm. Locator lấy từ DOM thật 04-11-2026.
 */
public class AttributeListPage extends AdminListPage {

    /** Nút trên dòng, đúng thứ tự trái → phải trên trang. */
    public enum RowButton { VALUES, EDIT, DELETE }

    /** JS: có ít nhất một nhãn giá trị và mọi nhãn đều có chữ. */
    private static final String JS_VALUES_ARE_BADGES = "const b=[...document.querySelectorAll('table tbody tr td span.badge')];"
            + "return b.length>0 && b.every(x => x.textContent.trim().length>0);";
    private static final String ADD_FORM = "//form[contains(@action,'/admin/attributes')][.//input[@id='name']]";

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='All Attributes']");
    private static final By CARD_HEADERS = AdminLocators.CARD_HEADER_TITLES;
    private static final By SIDEBAR_ATTRIBUTE = By.cssSelector("a.aiz-side-nav-link[href$='/admin/attributes']");
    private static final By ATTRIBUTE_MENU_ACTIVE = By.cssSelector(".aiz-side-nav-link.active[href$='/admin/attributes']");
    private static final By FORM_LABELS = By.xpath(ADD_FORM + "//label");
    private static final By NAME_INPUT = AdminLocators.NAME_INPUT;
    private static final By SAVE_BUTTON = By.xpath(ADD_FORM + "//button[@type='submit']");
    private static final By SEARCH_INPUT = AdminLocators.SEARCH_INPUT;
    private static final By ROW_VALUES = By.cssSelector("a[title='Attribute values']");
    private static final By VALUE_BADGES = By.cssSelector("span.badge");

    private static final int MAX_CLEANUP_ROUNDS = 10;

    public AttributeListPage() {
        super(PAGE_TITLE);
    }

    @Step("Mở trang All Attributes")
    public AttributeListPage open() {
        navigate("/admin/attributes");
        return waitUntilLoaded();
    }

    /** Trang này không phân trang theo ô tìm: mở lại rồi dò theo tên; không có thì -1. */
    public int findRow(String name) {
        open();
        return rowIndexOf(name);
    }

    /** Vào All Attributes từ sidebar: mở nhóm Products nếu đang thu gọn rồi bấm Attribute. */
    @Step("Bấm Products → Attribute ở sidebar")
    public AttributeListPage openFromSidebar() {
        clickInProductsMenu(SIDEBAR_ATTRIBUTE);
        return waitUntilLoaded();
    }

    public AttributeListPage waitUntilLoaded() {
        waitForListTitle();
        return this;
    }

    public List<String> cardHeaders() {
        return WebUI.getAllElementsText(CARD_HEADERS);
    }

    public boolean isAttributeMenuActive() {
        return WebUI.isElementDisplayedNow(ATTRIBUTE_MENU_ACTIVE);
    }

    public boolean hasSearchInput() {
        return WebUI.isElementDisplayedNow(SEARCH_INPUT);
    }

    // ---------- Bảng ----------

    /** Các nhãn giá trị ở cột Values của dòng thứ row (đếm từ 0). */
    public List<String> valuesOf(int row) {
        return WebUI.getWebElements(rows().get(row), VALUE_BADGES).stream().map(e -> e.getText().trim()).toList();
    }

    /** Rê chuột lên nút của dòng, trả về chú thích hiện ra. */
    public String hoverRowButton(int row, RowButton button) {
        return WebUI.getTooltipOnHover(rowButton(row, button.ordinal()), "nút " + button + ", dòng " + row);
    }

    /** Có ít nhất một dòng hiện giá trị dưới dạng nhãn, và mọi nhãn đều có chữ. */
    public boolean valuesShownAsBadges() {
        return Boolean.TRUE.equals(WebUI.executeJS(JS_VALUES_ARE_BADGES));
    }

    public boolean everyRowHasThreeButtons() {
        return Boolean.TRUE.equals(WebUI.executeJS("return " + JS_DATA_ROWS
                + ".every(r => r.querySelectorAll('td a.btn-circle').length === 3);"));
    }

    public String detailUrlOfRow(int row) {
        return WebUI.getWebElement(rows().get(row), ROW_VALUES).getAttribute("href");
    }

    /** Vị trí (từ 0) của dòng có nút Attribute values trỏ tới đúng url (đọc một lần bằng JS), -1 nếu không có. */
    public int rowIndexOfDetailUrl(String url) {
        return ((Number) WebUI.executeJS("return " + JS_DATA_ROWS
                + ".findIndex(r => (r.querySelector(\"a[title='Attribute values']\") || {}).href === arguments[0]);", url)).intValue();
    }

    @Step("Bấm nút Attribute values ở dòng {row} (đếm từ 0)")
    public AttributeDetailPage openValuesOfRow(int row) {
        andWaitForNewPage(() -> clickRowButton(WebUI.getWebElement(rows().get(row), ROW_VALUES), "Attribute values, dòng " + row));
        return new AttributeDetailPage().waitUntilLoaded();
    }

    @Step("Bấm nút Edit ở dòng {row} (đếm từ 0)")
    public AttributeEditPage editRow(int row) {
        clickEditOfRow(row);
        return new AttributeEditPage().waitUntilLoaded();
    }

    /**
     * Dọn dữ liệu test: với mỗi thuộc tính có tên chứa token — xoá hết giá trị ở trang Attribute Detail trước,
     * rồi xoá thuộc tính (xoá thuộc tính còn giá trị sẽ để lại giá trị mồ côi — AMB-ATTR-06). Chỉ đụng dòng chứa token.
     */
    @Step("Xoá mọi thuộc tính chứa {token} (giá trị trước, thuộc tính sau)")
    public void deleteAllContaining(String token) {
        for (int round = 0; round < MAX_CLEANUP_ROUNDS; round++) {
            open();
            List<Integer> matches = rowIndexesContaining(token);
            if (matches.isEmpty()) {
                return;
            }
            String detailUrl = detailUrlOfRow(matches.get(0));
            new AttributeDetailPage().openUrl(detailUrl).deleteAllValues();
            open();
            int row = rowIndexOfDetailUrl(detailUrl);
            if (row >= 0) {
                deleteRow(row);
            }
        }
    }

    // ---------- Form Add New Attribute ----------

    public List<String> formLabels() {
        return WebUI.getAllElementsText(FORM_LABELS);
    }

    public String namePlaceholder() {
        return WebUI.getElementAttribute(NAME_INPUT, "placeholder");
    }

    public String nameValue() {
        return WebUI.getElementValue(NAME_INPUT);
    }

    public String saveButtonText() {
        return WebUI.getElementTextNow(SAVE_BUTTON);
    }

    @Step("Nhập Name: {name}")
    public void enterName(String name) {
        WebUI.setText(NAME_INPUT, name);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở form Add New Attribute")
    public void clickSave() {
        markPage();
        WebUI.clickElement(SAVE_BUTTON);
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitUnlessError(this::clickSave, this::waitUntilLoaded);
    }

    @Step("Bấm đúp nút Save")
    public void doubleClickSave() {
        andWaitForNewPage(() -> WebUI.doubleClickElement(SAVE_BUTTON));
        waitUntilLoaded();
    }

    public void createAttribute(String name) {
        enterName(name);
        saveAndWait();
    }

    @Step("Bấm vào ô Name, gõ tên rồi nhấn Enter")
    public void createByEnter(String name) {
        enterName(name);
        andWaitForNewPage(() -> WebUI.pressKey(NAME_INPUT, Keys.ENTER));
        waitUntilLoaded();
    }

    public String nameValidationMessage() {
        return WebUI.getValidationMessage(NAME_INPUT);
    }

    public boolean isNameFocused() {
        return WebUI.isElementFocused(NAME_INPUT);
    }

    @Step("Nhấn Tab {count} lần từ ô Name")
    public List<String> tabSequenceFromName(int count) {
        return WebUI.tabSequenceFrom(NAME_INPUT, count);
    }

    @Step("Đối chiếu viền của ô Name khi đang focus")
    public List<String> fieldsWithoutVisibleFocus() {
        return WebUI.fieldsWithoutVisibleFocus(List.of(NAME_INPUT), List.of("Name"));
    }

    /** Từ ô Name nhấn Tab tới nút Save — nút có đổi kiểu (viền, bóng, màu) so với lúc chưa focus không. */
    public boolean saveShowsFocusWhenTabbed() {
        return WebUI.changesStyleWhenTabbedTo(NAME_INPUT, SAVE_BUTTON);
    }
}
