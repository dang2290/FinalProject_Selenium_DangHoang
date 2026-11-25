package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Ô chọn bootstrap-select (class aiz-selectpicker) bọc ngoài một thẻ select gốc: nút mở danh sách,
 * ô tìm .bs-searchbox, mỗi lựa chọn là li > a > span.text. Danh sách chỉ dựng các mục khớp từ khoá.
 * Mục con trong cây danh mục hiện với tiền tố "-- ", "---- " theo cấp.
 */
public class BootstrapSelect extends BasePage {

    /** JS: chữ các lựa chọn đang chọn của thẻ select gốc. */
    private static final String JS_SELECTED_TEXTS = "return [...arguments[0].selectedOptions].map(o=>o.textContent.trim());";
    /** JS: chữ mọi lựa chọn của thẻ select gốc. */
    private static final String JS_OPTION_TEXTS = "return [...arguments[0].options].map(o=>o.textContent.trim());";
    private static final By ITEM_LINK = By.tagName("a");

    private final String name;
    private final By select;
    private final By container;
    private final By toggle;
    private final By searchBox;
    private final By items;
    private final By noResults;
    private final By shownText;

    /** selectName: thuộc tính name của thẻ select gốc. */
    public BootstrapSelect(String selectName) {
        String root = "//div[contains(@class,'bootstrap-select')][select[@name='" + selectName + "']]";
        this.name = selectName;
        this.select = By.name(selectName);
        this.container = By.xpath(root);
        this.toggle = By.xpath(root + "/button[contains(@class,'dropdown-toggle')]");
        this.searchBox = By.xpath(root + "//div[contains(@class,'bs-searchbox')]/input");
        this.items = By.xpath(root + "//ul[contains(@class,'inner')]/li[not(contains(@class,'no-results'))]");
        this.noResults = By.xpath(root + "//li[contains(@class,'no-results')]");
        this.shownText = By.xpath(root + "//div[contains(@class,'filter-option-inner-inner')]");
    }

    @Override
    public String toString() {
        return "Dropdown(" + name + ")";
    }

    /** Chữ đang hiện trên ô (lựa chọn hiện tại, hoặc "Nothing selected"). */
    public String shownText() {
        return WebUI.getElementAttribute(shownText, "textContent").trim();
    }

    /** Các lựa chọn đang được chọn trong thẻ select gốc. */
    public List<String> selectedTexts() {
        return jsTexts(JS_SELECTED_TEXTS, WebUI.getWebElement(select));
    }

    /** Mọi lựa chọn của thẻ select gốc (đã bỏ tiền tố cấp "-- "). Đọc một lần bằng JS vì danh sách có hàng nghìn mục. */
    public List<String> optionTexts() {
        return jsTexts(JS_OPTION_TEXTS, WebUI.getWebElement(select))
                .stream().map(BootstrapSelect::stripLevel).toList();
    }

    @SuppressWarnings("unchecked")
    private List<String> jsTexts(String script, WebElement element) {
        return (List<String>) WebUI.executeJS(script, element);
    }

    /** Danh sách đang mở: container có đúng class "show" (không nhầm với "show-tick" của ô chọn nhiều). */
    public boolean isOpen() {
        return List.of(WebUI.getElementAttribute(container, "class").split("\\s+")).contains("show");
    }

    @Step("Mở danh sách lựa chọn")
    public void open() {
        if (!isOpen()) {
            WebUI.clickElement(toggle);
            WebUI.waitUntil("danh sách mở", this::isOpen);
        }
    }

    @Step("Đóng danh sách lựa chọn")
    public void close() {
        if (isOpen()) {
            WebUI.pressKey(Keys.ESCAPE);
            WebUI.waitUntil("danh sách đóng", () -> !isOpen());
        }
    }

    /** Có ô tìm trong danh sách (chỉ select có data-live-search). Gọi sau open(). */
    public boolean hasSearchBox() {
        return WebUI.checkElementExist(searchBox) && WebUI.isElementVisible(searchBox);
    }

    /** Gõ từ khoá vào ô tìm của danh sách, trả về chữ các mục còn hiện (giữ nguyên tiền tố cấp). */
    @Step("Gõ {keyword} vào ô tìm của danh sách")
    public List<String> search(String keyword) {
        open();
        WebUI.setText(searchBox, keyword);
        WebUI.waitUntil("danh sách lọc theo " + keyword, () -> WebUI.checkElementExist(noResults) || visibleItems().stream()
                .allMatch(t -> t.toLowerCase().contains(keyword.toLowerCase())));
        return visibleItems();
    }

    public boolean isNoResultsShown() {
        return WebUI.isElementDisplayedNow(noResults);
    }

    private List<String> visibleItems() {
        return jsTexts("return [...arguments[0].querySelectorAll('ul.inner li:not(.no-results)')].map(li=>li.textContent.trim());",
                WebUI.getWebElement(container));
    }

    /** Bấm đúng mục có chữ (sau khi bỏ tiền tố cấp) bằng text — có ô tìm thì gõ trước để thu gọn danh sách. */
    @Step("Chọn {text}")
    public void choose(String text) {
        open();
        if (hasSearchBox()) {
            search(text);
        }
        // Danh sách dài (VD hàng trăm thuộc tính) được dựng lại khi lọc — mục vừa tìm có thể bị thay ngay trước khi bấm.
        // Tìm và bấm trong lúc chờ: gặp StaleElementReference thì WebUI tìm lại.
        WebUI.clickWhenFound(text, () -> {
            WebElement item = WebUI.getWebElements(items).stream()
                    .filter(li -> stripLevel(li.getAttribute("textContent")).equals(text)).findFirst()
                    .orElseThrow(() -> new IllegalStateException("Không có lựa chọn " + text));
            return WebUI.getWebElement(item, ITEM_LINK);
        });
        WebUI.waitUntil(text + " được chọn", () -> selectedTexts().stream().map(BootstrapSelect::stripLevel).anyMatch(text::equals));
    }

    /** Ô chọn nhiều: bấm mục thứ index (đếm từ 0) trong danh sách để chọn / bỏ chọn. */
    @Step("Bấm mục thứ {index} trong danh sách")
    public void toggleItem(int index) {
        open();
        int before = selectedTexts().size();
        WebUI.clickElement(WebUI.getWebElement(WebUI.getWebElements(items).get(index), ITEM_LINK), "mục thứ " + index);
        WebUI.waitUntil("số mục được chọn thay đổi", () -> selectedTexts().size() != before);
    }

    /** Bỏ tiền tố cấp "-- " / "---- " của mục con. */
    public static String stripLevel(String text) {
        return text.trim().replaceFirst("^-+\\s*", "");
    }
}
