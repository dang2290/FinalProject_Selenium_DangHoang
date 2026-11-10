package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/**
 * Ô chọn bootstrap-select (class aiz-selectpicker) bọc ngoài một thẻ select gốc: nút mở danh sách,
 * ô tìm .bs-searchbox, mỗi lựa chọn là li > a > span.text. Danh sách chỉ dựng các mục khớp từ khoá.
 * Mục con trong cây danh mục hiện với tiền tố "-- ", "---- " theo cấp.
 */
public class BootstrapSelect extends BasePage {

    private final By select;
    private final By container;
    private final By toggle;
    private final By searchBox;
    private final By items;
    private final By noResults;
    private final By shownText;

    /** selectName: thuộc tính name của thẻ select gốc. */
    public BootstrapSelect(WebDriver driver, String selectName) {
        super(driver);
        String root = "//div[contains(@class,'bootstrap-select')][select[@name='" + selectName + "']]";
        this.select = By.name(selectName);
        this.container = By.xpath(root);
        this.toggle = By.xpath(root + "/button[contains(@class,'dropdown-toggle')]");
        this.searchBox = By.xpath(root + "//div[contains(@class,'bs-searchbox')]/input");
        this.items = By.xpath(root + "//ul[contains(@class,'inner')]/li[not(contains(@class,'no-results'))]");
        this.noResults = By.xpath(root + "//li[contains(@class,'no-results')]");
        this.shownText = By.xpath(root + "//div[contains(@class,'filter-option-inner-inner')]");
    }

    /** Chữ đang hiện trên ô (lựa chọn hiện tại, hoặc "Nothing selected"). */
    public String shownText() {
        return driver.findElement(shownText).getAttribute("textContent").trim();
    }

    /** Các lựa chọn đang được chọn trong thẻ select gốc. */
    public List<String> selectedTexts() {
        return jsTexts("return [...arguments[0].selectedOptions].map(o=>o.textContent.trim());", driver.findElement(select));
    }

    /** Mọi lựa chọn của thẻ select gốc (đã bỏ tiền tố cấp "-- "). Đọc một lần bằng JS vì danh sách có hàng nghìn mục. */
    public List<String> optionTexts() {
        return jsTexts("return [...arguments[0].options].map(o=>o.textContent.trim());", driver.findElement(select))
                .stream().map(BootstrapSelect::stripLevel).toList();
    }

    @SuppressWarnings("unchecked")
    private List<String> jsTexts(String script, WebElement element) {
        return (List<String>) js(script, element);
    }

    /** Danh sách đang mở: container có đúng class "show" (không nhầm với "show-tick" của ô chọn nhiều). */
    public boolean isOpen() {
        return List.of(driver.findElement(container).getAttribute("class").split("\\s+")).contains("show");
    }

    @Step("Mở danh sách lựa chọn")
    public void open() {
        if (!isOpen()) {
            click(toggle);
            wait.until(d -> isOpen());
        }
    }

    @Step("Đóng danh sách lựa chọn")
    public void close() {
        if (isOpen()) {
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            wait.until(d -> !isOpen());
        }
    }

    /** Có ô tìm trong danh sách (chỉ select có data-live-search). Gọi sau open(). */
    public boolean hasSearchBox() {
        return !driver.findElements(searchBox).isEmpty() && isVisible(searchBox);
    }

    /** Gõ từ khoá vào ô tìm của danh sách, trả về chữ các mục còn hiện (giữ nguyên tiền tố cấp). */
    @Step("Gõ {keyword} vào ô tìm của danh sách")
    public List<String> search(String keyword) {
        open();
        WebElement input = waitForVisible(searchBox);
        input.clear();
        input.sendKeys(keyword);
        wait.until(d -> !d.findElements(noResults).isEmpty() || visibleItems().stream()
                .allMatch(t -> t.toLowerCase().contains(keyword.toLowerCase())));
        return visibleItems();
    }

    public boolean isNoResultsShown() {
        return isPresentNow(noResults);
    }

    private List<String> visibleItems() {
        return jsTexts("return [...arguments[0].querySelectorAll('ul.inner li:not(.no-results)')].map(li=>li.textContent.trim());",
                driver.findElement(container));
    }

    /** Bấm đúng mục có chữ (sau khi bỏ tiền tố cấp) bằng text — có ô tìm thì gõ trước để thu gọn danh sách. */
    @Step("Chọn {text}")
    public void choose(String text) {
        open();
        if (hasSearchBox()) {
            search(text);
        }
        // Danh sách dài (VD hàng trăm thuộc tính) được dựng lại khi lọc — mục vừa tìm có thể bị thay ngay trước khi bấm.
        // Tìm và bấm trong wait.until: gặp StaleElementReference thì tìm lại (WaitHelper bỏ qua lỗi này).
        wait.until(d -> {
            WebElement item = d.findElements(items).stream()
                    .filter(li -> stripLevel(li.getAttribute("textContent")).equals(text)).findFirst()
                    .orElseThrow(() -> new IllegalStateException("Không có lựa chọn " + text));
            item.findElement(By.tagName("a")).click();
            return true;
        });
        wait.until(d -> selectedTexts().stream().map(BootstrapSelect::stripLevel).anyMatch(text::equals));
    }

    /** Ô chọn nhiều: bấm mục thứ index (đếm từ 0) trong danh sách để chọn / bỏ chọn. */
    @Step("Bấm mục thứ {index} trong danh sách")
    public void toggleItem(int index) {
        open();
        int before = selectedTexts().size();
        driver.findElements(items).get(index).findElement(By.tagName("a")).click();
        wait.until(d -> selectedTexts().size() != before);
    }

    /** Chữ mục thứ index (đếm từ 0) trong danh sách đang mở. */
    public String itemText(int index) {
        open();
        return driver.findElements(items).get(index).getAttribute("textContent").trim();
    }

    /** Bỏ tiền tố cấp "-- " / "---- " của mục con. */
    public static String stripLevel(String text) {
        return text.trim().replaceFirst("^-+\\s*", "");
    }
}
