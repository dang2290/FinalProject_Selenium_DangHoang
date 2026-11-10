package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Trang All Colors /admin/colors — bảng (#, Name, Options) 10 dòng mỗi trang bên trái; thẻ Add New Color và
 * thẻ Color filter activation bên phải. Locator lấy từ DOM thật 04-11-2026.
 * ⛔ Công tắc Color filter activation là cấu hình toàn hệ thống — page này chỉ đọc trạng thái, không có hàm bấm.
 */
public class ColorListPage extends AdminListPage {

    private static final String ADD_CARD = "//div[contains(@class,'card')][.//h5[normalize-space()='Add New Color']]";
    private static final String ADD_FORM = "//form[contains(@action,'/admin/colors/store')]";

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='All Colors']");
    private static final By CARD_HEADERS = By.cssSelector(".card-header");
    private static final By SIDEBAR_PRODUCTS = By.xpath("//a[contains(@class,'aiz-side-nav-link')][span[normalize-space()='Products']]");
    private static final By SIDEBAR_COLORS = By.cssSelector("a.aiz-side-nav-link[href$='/admin/colors']");
    private static final By COLORS_MENU_ACTIVE = By.cssSelector(".aiz-side-nav-link.active[href$='/admin/colors']");
    private static final By FORM_LABELS = By.xpath(ADD_FORM + "//label");
    private static final By NAME_INPUT = By.id("name");
    private static final By CODE_INPUT = By.id("code");
    private static final By SAVE_BUTTON = By.xpath(ADD_FORM + "//button[@type='submit']");
    private static final By ERROR_MESSAGES = By.xpath(ADD_CARD + "//div[contains(@class,'alert-danger')]//li");
    private static final By FILTER_SWITCH = By.cssSelector("input[type='checkbox'][onchange*='color_filter_activation']");
    private static final By ROW_BUTTONS = By.cssSelector("td a.btn-circle");

    private static final int COL_NAME = 2;
    private static final int MAX_CLEANUP_ROUNDS = 10;

    public ColorListPage(WebDriver driver) {
        super(driver);
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
        if (!isPresentNow(SIDEBAR_COLORS)) {
            click(SIDEBAR_PRODUCTS);
        }
        andWaitForNewPage(() -> click(SIDEBAR_COLORS));
        return waitUntilLoaded();
    }

    public ColorListPage waitUntilLoaded() {
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

    public boolean isColorsMenuActive() {
        return isPresentNow(COLORS_MENU_ACTIVE);
    }

    public List<String> cardHeaders() {
        return texts(CARD_HEADERS);
    }

    // ---------- Bảng ----------

    public List<String> names() {
        return columnTexts(COL_NAME);
    }

    /** Vị trí (từ 0) của dòng mang đúng tên này, -1 nếu không có. */
    public int rowIndexOf(String name) {
        List<String> names = names();
        return IntStream.range(0, names.size()).filter(i -> names.get(i).equals(name)).findFirst().orElse(-1);
    }

    /** Vị trí (từ 0) mọi dòng có tên chứa chuỗi này (không phân biệt hoa thường). */
    public List<Integer> rowIndexesContaining(String text) {
        List<String> names = names();
        return IntStream.range(0, names.size())
                .filter(i -> names.get(i).toLowerCase().contains(text.toLowerCase())).boxed().toList();
    }

    /** Cột Name chỉ có chữ — không ô nào chứa ô mẫu màu (phần tử có nền tô màu) hay ảnh. */
    public boolean nameColumnHasOnlyText() {
        return Boolean.TRUE.equals(js("return [...document.querySelectorAll('table tbody tr')]"
                + ".filter(r => r.querySelector('a.confirm-delete'))"
                + ".every(r => r.cells[1] && r.cells[1].querySelectorAll('*').length === 0);"));
    }

    /** Chú thích (title) các nút tròn trên dòng thứ row (đếm từ 0), theo thứ tự. */
    public List<String> rowButtonTitles(int row) {
        return rows().get(row).findElements(ROW_BUTTONS).stream().map(e -> e.getAttribute("title")).toList();
    }

    /** Rê chuột lên nút thứ button (đếm từ 0) của dòng, trả về chú thích hiện ra. */
    public String hoverRowButton(int row, int button) {
        WebElement target = rows().get(row).findElements(ROW_BUTTONS).get(button);
        new Actions(driver).moveToElement(target).perform();
        return target.getAttribute("title");
    }

    /** Id màu ở dòng thứ row (đếm từ 0) — lấy từ đường dẫn nút Edit. */
    public String idOfRow(int row) {
        return rows().get(row).findElement(ROW_EDIT).getAttribute("href").replaceAll(".*/edit/(\\d+).*", "$1");
    }

    @Step("Bấm nút Edit ở dòng {row} (đếm từ 0)")
    public ColorEditPage editRow(int row) {
        clickEditOfRow(row);
        return new ColorEditPage(driver).waitUntilLoaded();
    }

    /** Dọn dữ liệu test: tìm theo token rồi xoá lần lượt mọi dòng có tên chứa token. Chỉ đụng dòng chứa token. */
    @Step("Xoá mọi màu có tên chứa {token}")
    public void deleteAllContaining(String token) {
        for (int round = 0; round < MAX_CLEANUP_ROUNDS; round++) {
            open();
            search(token);
            List<Integer> matches = rowIndexesContaining(token);
            if (matches.isEmpty()) {
                return;
            }
            deleteRow(matches.get(0));
        }
    }

    // ---------- Form Add New Color ----------

    public List<String> formLabels() {
        return texts(FORM_LABELS);
    }

    public String namePlaceholder() {
        return driver.findElement(NAME_INPUT).getAttribute("placeholder");
    }

    public String codePlaceholder() {
        return driver.findElement(CODE_INPUT).getAttribute("placeholder");
    }

    public String nameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String codeValue() {
        return driver.findElement(CODE_INPUT).getAttribute("value");
    }

    public String saveButtonText() {
        return driver.findElement(SAVE_BUTTON).getText().trim();
    }

    @Step("Nhập Name: [{name}]")
    public void enterName(String name) {
        type(NAME_INPUT, name);
    }

    @Step("Nhập Color Code: [{code}]")
    public void enterCode(String code) {
        type(CODE_INPUT, code);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở form Add New Color")
    public void clickSave() {
        click(SAVE_BUTTON);
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitForNewPage(this::clickSave);
        if (!isServerErrorPage()) {
            waitUntilLoaded();
        }
    }

    @Step("Bấm đúp nút Save")
    public void doubleClickSave() {
        andWaitForNewPage(() -> new Actions(driver).doubleClick(waitForVisible(SAVE_BUTTON)).perform());
        if (!isServerErrorPage()) {
            waitUntilLoaded();
        }
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
        andWaitForNewPage(() -> driver.findElement(CODE_INPUT).sendKeys(Keys.ENTER));
        waitUntilLoaded();
    }

    /** Các dòng lỗi trong khối đỏ của thẻ Add New Color (rỗng nếu không có khối lỗi). */
    public List<String> errorMessages() {
        return texts(ERROR_MESSAGES);
    }

    public String nameValidationMessage() {
        return validationMessage(NAME_INPUT);
    }

    public String codeValidationMessage() {
        return validationMessage(CODE_INPUT);
    }

    public boolean isNameFocused() {
        return driver.switchTo().activeElement().equals(driver.findElement(NAME_INPUT));
    }

    public boolean isCodeFocused() {
        return driver.switchTo().activeElement().equals(driver.findElement(CODE_INPUT));
    }

    @Step("Nhấn Tab {count} lần từ ô Name")
    public List<String> tabSequenceFromName(int count) {
        return tabSequenceFrom(NAME_INPUT, count);
    }

    @Step("Đối chiếu viền của ô Name, Color Code khi đang focus")
    public List<String> fieldsWithoutVisibleFocus() {
        return fieldsWithoutVisibleFocus(List.of(NAME_INPUT, CODE_INPUT), List.of("Name", "Color Code"));
    }

    /** Từ ô Color Code nhấn Tab tới nút Save — nút có đổi kiểu so với lúc chưa focus không. */
    public boolean saveShowsFocusWhenTabbed() {
        return changesStyleWhenTabbedTo(CODE_INPUT, SAVE_BUTTON);
    }

    // ---------- Thẻ Color filter activation (chỉ đọc) ----------

    public boolean hasFilterSwitch() {
        return !driver.findElements(FILTER_SWITCH).isEmpty();
    }

    public boolean isFilterSwitchOn() {
        return driver.findElement(FILTER_SWITCH).isSelected();
    }
}
