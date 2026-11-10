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
 * Trang All Attributes /admin/attributes — bảng (#, Name, Values, Options) bên trái, form Add New Attribute bên phải.
 * Bảng hiện mọi thuộc tính trên một trang (~600 dòng), không có ô tìm kiếm. Locator lấy từ DOM thật 04-11-2026.
 */
public class AttributeListPage extends AdminListPage {

    private static final String ADD_FORM = "//form[contains(@action,'/admin/attributes')][.//input[@id='name']]";

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='All Attributes']");
    private static final By CARD_HEADERS = By.cssSelector(".card-header h5");
    private static final By SIDEBAR_PRODUCTS = By.xpath("//a[contains(@class,'aiz-side-nav-link')][span[normalize-space()='Products']]");
    private static final By SIDEBAR_ATTRIBUTE = By.cssSelector("a.aiz-side-nav-link[href$='/admin/attributes']");
    private static final By ATTRIBUTE_MENU_ACTIVE = By.cssSelector(".aiz-side-nav-link.active[href$='/admin/attributes']");
    private static final By FORM_LABELS = By.xpath(ADD_FORM + "//label");
    private static final By NAME_INPUT = By.id("name");
    private static final By SAVE_BUTTON = By.xpath(ADD_FORM + "//button[@type='submit']");
    private static final By SEARCH_INPUT = By.id("search");
    private static final By ROW_BUTTONS = By.cssSelector("td a.btn-circle");
    private static final By ROW_VALUES = By.cssSelector("a[title='Attribute values']");
    private static final By VALUE_BADGES = By.cssSelector("span.badge");

    private static final int COL_NAME = 2;
    private static final int MAX_CLEANUP_ROUNDS = 10;

    public AttributeListPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang All Attributes")
    public AttributeListPage open() {
        navigate("/admin/attributes");
        return waitUntilLoaded();
    }

    /** Vào All Attributes từ sidebar: mở nhóm Products nếu đang thu gọn rồi bấm Attribute. */
    @Step("Bấm Products → Attribute ở sidebar")
    public AttributeListPage openFromSidebar() {
        if (!isPresentNow(SIDEBAR_ATTRIBUTE)) {
            click(SIDEBAR_PRODUCTS);
        }
        andWaitForNewPage(() -> click(SIDEBAR_ATTRIBUTE));
        return waitUntilLoaded();
    }

    public AttributeListPage waitUntilLoaded() {
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

    public List<String> cardHeaders() {
        return texts(CARD_HEADERS);
    }

    public boolean isAttributeMenuActive() {
        return isPresentNow(ATTRIBUTE_MENU_ACTIVE);
    }

    public boolean hasSearchInput() {
        return isPresentNow(SEARCH_INPUT);
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

    /** Vị trí (từ 0) mọi dòng có tên chứa chuỗi này. */
    public List<Integer> rowIndexesContaining(String text) {
        List<String> names = names();
        return IntStream.range(0, names.size())
                .filter(i -> names.get(i).toLowerCase().contains(text.toLowerCase())).boxed().toList();
    }

    /** Các nhãn giá trị ở cột Values của dòng thứ row (đếm từ 0). */
    public List<String> valuesOf(int row) {
        return rows().get(row).findElements(VALUE_BADGES).stream().map(e -> e.getText().trim()).toList();
    }

    /** Chú thích (title) của 3 nút tròn trên dòng thứ row, theo thứ tự. */
    public List<String> rowButtonTitles(int row) {
        return rows().get(row).findElements(ROW_BUTTONS).stream().map(e -> e.getAttribute("title")).toList();
    }

    /** Rê chuột lên nút thứ button (đếm từ 0) của dòng, trả về chú thích hiện ra. */
    public String hoverRowButton(int row, int button) {
        WebElement target = rows().get(row).findElements(ROW_BUTTONS).get(button);
        new Actions(driver).moveToElement(target).perform();
        return target.getAttribute("title");
    }

    /** Có ít nhất một dòng hiện giá trị dưới dạng nhãn, và mọi nhãn đều có chữ. */
    public boolean valuesShownAsBadges() {
        return Boolean.TRUE.equals(js("const b=[...document.querySelectorAll('table tbody tr td span.badge')];"
                + "return b.length>0 && b.every(x => x.textContent.trim().length>0);"));
    }

    public boolean everyRowHasThreeButtons() {
        return Boolean.TRUE.equals(js("return [...document.querySelectorAll('table tbody tr')]"
                + ".filter(r => r.querySelector('a.confirm-delete')).every(r => r.querySelectorAll('td a.btn-circle').length === 3);"));
    }

    public String detailUrlOfRow(int row) {
        return rows().get(row).findElement(ROW_VALUES).getAttribute("href");
    }

    /** Vị trí (từ 0) của dòng có nút Attribute values trỏ tới đúng url (đọc một lần bằng JS), -1 nếu không có. */
    public int rowIndexOfDetailUrl(String url) {
        return ((Number) js("return [...document.querySelectorAll('table tbody tr')]"
                + ".filter(r => r.querySelector('a.confirm-delete'))"
                + ".findIndex(r => (r.querySelector(\"a[title='Attribute values']\") || {}).href === arguments[0]);", url)).intValue();
    }

    @Step("Bấm nút Attribute values ở dòng {row} (đếm từ 0)")
    public AttributeDetailPage openValuesOfRow(int row) {
        andWaitForNewPage(() -> clickRowButton(rows().get(row).findElement(ROW_VALUES)));
        return new AttributeDetailPage(driver).waitUntilLoaded();
    }

    @Step("Bấm nút Edit ở dòng {row} (đếm từ 0)")
    public AttributeEditPage editRow(int row) {
        clickEditOfRow(row);
        return new AttributeEditPage(driver).waitUntilLoaded();
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
            new AttributeDetailPage(driver).openUrl(detailUrl).deleteAllValues();
            open();
            int row = rowIndexOfDetailUrl(detailUrl);
            if (row >= 0) {
                deleteRow(row);
            }
        }
    }

    // ---------- Form Add New Attribute ----------

    public List<String> formLabels() {
        return texts(FORM_LABELS);
    }

    public String namePlaceholder() {
        return driver.findElement(NAME_INPUT).getAttribute("placeholder");
    }

    public String nameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String saveButtonText() {
        return driver.findElement(SAVE_BUTTON).getText().trim();
    }

    @Step("Nhập Name: {name}")
    public void enterName(String name) {
        type(NAME_INPUT, name);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở form Add New Attribute")
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
        waitUntilLoaded();
    }

    public void createAttribute(String name) {
        enterName(name);
        saveAndWait();
    }

    @Step("Bấm vào ô Name, gõ tên rồi nhấn Enter")
    public void createByEnter(String name) {
        enterName(name);
        andWaitForNewPage(() -> driver.findElement(NAME_INPUT).sendKeys(Keys.ENTER));
        waitUntilLoaded();
    }

    public String nameValidationMessage() {
        return validationMessage(NAME_INPUT);
    }

    public boolean isNameFocused() {
        return driver.switchTo().activeElement().equals(driver.findElement(NAME_INPUT));
    }

    @Step("Nhấn Tab {count} lần từ ô Name")
    public List<String> tabSequenceFromName(int count) {
        return tabSequenceFrom(NAME_INPUT, count);
    }

    @Step("Đối chiếu viền của ô Name khi đang focus")
    public List<String> fieldsWithoutVisibleFocus() {
        return fieldsWithoutVisibleFocus(List.of(NAME_INPUT), List.of("Name"));
    }

    /** Từ ô Name nhấn Tab tới nút Save — nút có đổi kiểu (viền, bóng, màu) so với lúc chưa focus không. */
    public boolean saveShowsFocusWhenTabbed() {
        return changesStyleWhenTabbedTo(NAME_INPUT, SAVE_BUTTON);
    }
}
