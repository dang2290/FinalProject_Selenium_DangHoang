package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.util.List;

/**
 * Trang All Colors /admin/colors — bảng (#, Name, Options) 10 dòng mỗi trang bên trái; thẻ Add New Color và
 * thẻ Color filter activation bên phải. Locator lấy từ DOM thật 04-11-2026.
 * ⛔ Công tắc Color filter activation là cấu hình toàn hệ thống — page này chỉ đọc trạng thái, không có hàm bấm.
 */
public class ColorListPage extends MainListPage {

    /** Nút trên dòng, đúng thứ tự trái → phải trên trang. */
    public enum RowButton { EDIT, DELETE }

    private static final String ADD_CARD = "//div[contains(@class,'card')][.//h5[normalize-space()='Add New Color']]";
    private static final String ADD_FORM = "//form[contains(@action,'/admin/colors/store')]";

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='All Colors']");
    private static final By CARD_HEADERS = AdminLocators.CARD_HEADERS;
    private static final By SIDEBAR_COLORS = AdminLocators.SIDEBAR_COLORS;
    private static final By COLORS_MENU_ACTIVE = AdminLocators.COLORS_MENU_ACTIVE;
    private static final By FORM_LABELS = By.xpath(ADD_FORM + "//label");
    private static final By NAME_INPUT = AdminLocators.NAME_INPUT;
    private static final By CODE_INPUT = By.id("code");
    private static final By SAVE_BUTTON = By.xpath(ADD_FORM + "//button[@type='submit']");
    private static final By ERROR_MESSAGES = By.xpath(ADD_CARD + "//div[contains(@class,'alert-danger')]//li");
    private static final By FILTER_SWITCH = By.cssSelector("input[type='checkbox'][onchange*='color_filter_activation']");

    public ColorListPage() {
        super(PAGE_TITLE);
    }

    @Step("Mở trang All Colors")
    public ColorListPage open() {
        navigate("/admin/colors");
        return waitUntilLoaded();
    }

    @Step("Mở trang danh sách màu số {page}")
    public ColorListPage openPage(int page) {
        navigate("/admin/colors?page=" + page);
        return waitUntilLoaded();
    }

    /** Vào All Colors từ sidebar: mở nhóm Products nếu đang thu gọn rồi bấm Colors. */
    @Step("Bấm Products → Colors ở sidebar")
    public ColorListPage openFromSidebar() {
        clickInProductsMenu(SIDEBAR_COLORS);
        return waitUntilLoaded();
    }

    public ColorListPage waitUntilLoaded() {
        waitForListTitle();
        return this;
    }

    public boolean isColorsMenuActive() {
        return WebUI.isElementDisplayedNow(COLORS_MENU_ACTIVE);
    }

    public List<String> cardHeaders() {
        return WebUI.getAllElementsText(CARD_HEADERS);
    }

    // ---------- Bảng ----------

    /** Cột Name chỉ có chữ — không ô nào chứa ô mẫu màu (phần tử có nền tô màu) hay ảnh. */
    public boolean nameColumnHasOnlyText() {
        return Boolean.TRUE.equals(WebUI.executeJS("return " + JS_DATA_ROWS
                + ".every(r => r.cells[1] && r.cells[1].querySelectorAll('*').length === 0);"));
    }

    /** Rê chuột lên nút của dòng, trả về chú thích hiện ra. */
    public String hoverRowButton(int row, RowButton button) {
        return WebUI.getTooltipOnHover(rowButton(row, button.ordinal()), "nút " + button + ", dòng " + row);
    }

    @Step("Bấm nút Edit ở dòng {row} (đếm từ 0)")
    public ColorEditPage editRow(int row) {
        clickEditOfRow(row);
        return new ColorEditPage().waitUntilLoaded();
    }

    @Step("Xoá mọi màu có tên chứa {token}")
    public void deleteAllContaining(String token) {
        deleteMatchingRows(token, this::open);
    }

    // ---------- Form Add New Color ----------

    public List<String> formLabels() {
        return WebUI.getAllElementsText(FORM_LABELS);
    }

    public String namePlaceholder() {
        return WebUI.getElementAttribute(NAME_INPUT, "placeholder");
    }

    public String codePlaceholder() {
        return WebUI.getElementAttribute(CODE_INPUT, "placeholder");
    }

    public String nameValue() {
        return WebUI.getElementValue(NAME_INPUT);
    }

    public String codeValue() {
        return WebUI.getElementValue(CODE_INPUT);
    }

    public String saveButtonText() {
        return WebUI.getElementTextNow(SAVE_BUTTON);
    }

    @Step("Nhập Name: [{name}]")
    public void enterName(String name) {
        WebUI.setText(NAME_INPUT, name);
    }

    @Step("Nhập Color Code: [{code}]")
    public void enterCode(String code) {
        WebUI.setText(CODE_INPUT, code);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở form Add New Color")
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
        andWaitUnlessError(() -> WebUI.doubleClickElement(SAVE_BUTTON), this::waitUntilLoaded);
    }

    public void createColor(String name, String code) {
        enterName(name);
        enterCode(code);
        saveAndWait();
    }

    @Step("Nhập Name, Color Code rồi nhấn Enter ở ô Color Code")
    public void createByEnterInCode(String name, String code) {
        enterName(name);
        enterCode(code);
        andWaitForNewPage(() -> WebUI.pressKey(CODE_INPUT, Keys.ENTER));
        waitUntilLoaded();
    }

    /** Các dòng lỗi trong khối đỏ của thẻ Add New Color (rỗng nếu không có khối lỗi). */
    public List<String> errorMessages() {
        return WebUI.getAllElementsText(ERROR_MESSAGES);
    }

    public String nameValidationMessage() {
        return WebUI.getValidationMessage(NAME_INPUT);
    }

    public String codeValidationMessage() {
        return WebUI.getValidationMessage(CODE_INPUT);
    }

    public boolean isNameFocused() {
        return WebUI.isElementFocused(NAME_INPUT);
    }

    public boolean isCodeFocused() {
        return WebUI.isElementFocused(CODE_INPUT);
    }

    @Step("Nhấn Tab {count} lần từ ô Name")
    public List<String> tabSequenceFromName(int count) {
        return WebUI.tabSequenceFrom(NAME_INPUT, count);
    }

    @Step("Đối chiếu viền của ô Name, Color Code khi đang focus")
    public List<String> fieldsWithoutVisibleFocus() {
        return WebUI.fieldsWithoutVisibleFocus(List.of(NAME_INPUT, CODE_INPUT), List.of("Name", "Color Code"));
    }

    /** Từ ô Color Code nhấn Tab tới nút Save — nút có đổi kiểu so với lúc chưa focus không. */
    public boolean saveShowsFocusWhenTabbed() {
        return WebUI.changesStyleWhenTabbedTo(CODE_INPUT, SAVE_BUTTON);
    }

    // ---------- Thẻ Color filter activation (chỉ đọc) ----------

    public boolean hasFilterSwitch() {
        return WebUI.checkElementExist(FILTER_SWITCH);
    }

    public boolean isFilterSwitchOn() {
        return WebUI.isElementSelected(FILTER_SWITCH);
    }
}
