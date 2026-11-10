package com.aec.pages;

import com.aec.config.ConfigReader;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Phần chung của trang danh sách quản trị (Brand, Category…): ô tìm kiếm #search, bảng footable,
 * thanh phân trang Laravel, nút Edit/Delete mỗi dòng, hộp #delete-modal. Locator kiểm trên DOM thật 03-11-2026.
 */
public abstract class AdminListPage extends BasePage {

    private static final By SEARCH_INPUT = By.id("search");
    private static final By LIST_TABLE = By.cssSelector("table.aiz-table");
    private static final By TABLE_LOADER = By.cssSelector(".footable-loader");
    private static final By ROWS = By.xpath("//table/tbody/tr[.//a[contains(@class,'confirm-delete')]]");
    private static final By TABLE_HEADERS = By.cssSelector("table thead th");
    private static final By NOTHING_FOUND = By.cssSelector("table tbody tr.footable-empty");
    private static final By PAGINATION = By.cssSelector(".pagination");
    private static final By ACTIVE_PAGE = By.cssSelector(".pagination .page-item.active");
    private static final By PAGE_LINKS = By.cssSelector(".pagination a.page-link");
    private static final By NEXT_DISABLED = By.xpath("//ul[contains(@class,'pagination')]/li[contains(@class,'disabled')][normalize-space()='›']");

    private static final By DELETE_MODAL = By.id("delete-modal");
    private static final By DELETE_MODAL_TITLE = By.cssSelector("#delete-modal .modal-title");
    private static final By DELETE_MODAL_TEXT = By.cssSelector("#delete-modal .modal-body p");
    private static final By DELETE_MODAL_CANCEL = By.cssSelector("#delete-modal .modal-body [data-dismiss='modal']");
    private static final By DELETE_MODAL_CLOSE = By.cssSelector("#delete-modal .close");
    private static final By CONFIRM_DELETE_BUTTON = By.id("delete-link");

    protected static final By ROW_EDIT = By.cssSelector("a[title='Edit']");
    protected static final By ROW_DELETE = By.cssSelector("a.confirm-delete");

    /** Cách đóng hộp xác nhận xoá mà không xoá. */
    public enum CloseBy { CANCEL, CLOSE_ICON, ESC }

    protected AdminListPage(WebDriver driver) {
        super(driver);
    }

    /** Chờ tiêu đề trang danh sách hiện. */
    protected abstract void waitForListTitle();

    /**
     * Footable ẩn bảng và hiện vòng xoay div.footable-loader trong lúc dựng (vendors.js: $el.hide().after($loader)),
     * dựng xong mới hiện lại bảng — đọc dòng trước lúc đó sẽ ra 0 dòng.
     */
    protected void waitForTableReady() {
        // Bảng một trang ~600 dòng (Attribute) dựng chậm hơn hẳn trang 15 dòng — cho gấp 3 thời gian chờ thường
        WebDriverWait tableWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInstance().getTimeout() * 3L));
        tableWait.until(d -> d.findElements(TABLE_LOADER).isEmpty()
                && d.findElements(LIST_TABLE).stream().anyMatch(WebElement::isDisplayed));
    }

    // ---------- Tìm kiếm ----------

    @Step("Tìm: {keyword}")
    public void search(String keyword) {
        andWaitForNewPage(() -> {
            type(SEARCH_INPUT, keyword);
            driver.findElement(SEARCH_INPUT).sendKeys(Keys.ENTER);
        });
        if (isServerErrorPage()) {
            log.warn("Tìm '{}' ra trang lỗi máy chủ", keyword);
            return;
        }
        waitForListTitle();
    }

    public String searchPlaceholder() {
        return driver.findElement(SEARCH_INPUT).getAttribute("placeholder");
    }

    public String searchValue() {
        return driver.findElement(SEARCH_INPUT).getAttribute("value");
    }

    // ---------- Bảng ----------

    protected List<WebElement> rows() {
        return driver.findElements(ROWS);
    }

    public int rowCount() {
        return rows().size();
    }

    /** Chữ của ô cột thứ column (đếm từ 1) — đọc textContent để vẫn đúng khi footable ẩn cột ở màn hẹp. */
    protected String cellText(WebElement row, int column) {
        return row.findElement(By.xpath("./td[" + column + "]")).getAttribute("textContent").trim();
    }

    /** Chữ cả một cột (đếm từ 1) — đọc một lần bằng JS vì bảng có thể tới hàng trăm dòng. */
    @SuppressWarnings("unchecked")
    protected List<String> columnTexts(int column) {
        return (List<String>) js("return [...document.querySelectorAll('table tbody tr')]"
                + ".filter(r => r.querySelector('a.confirm-delete'))"
                + ".map(r => (r.cells[arguments[0] - 1] || {textContent: ''}).textContent.trim());", column);
    }

    public List<Integer> rowNumbers() {
        return columnTexts(1).stream().map(Integer::parseInt).toList();
    }

    /** Tiêu đề các cột đang hiện. */
    public List<String> tableHeaders() {
        return driver.findElements(TABLE_HEADERS).stream().filter(WebElement::isDisplayed)
                .map(e -> e.getText().trim()).toList();
    }

    /** Mỗi dòng đều có nút Edit (bút chì) và Delete (thùng rác). */
    public boolean everyRowHasEditAndDelete() {
        return rows().stream().allMatch(r -> r.findElement(ROW_EDIT).isDisplayed() && r.findElement(ROW_DELETE).isDisplayed());
    }

    /** Chú thích hiện khi rê chuột lên nút bút chì (thuộc tính title). */
    public String editTooltipOfFirstRow() {
        WebElement edit = rows().get(0).findElement(ROW_EDIT);
        new Actions(driver).moveToElement(edit).perform();
        return edit.getAttribute("title");
    }

    /** Chú thích hiện khi rê chuột lên nút thùng rác (thuộc tính title). */
    public String deleteTooltipOfFirstRow() {
        WebElement delete = rows().get(0).findElement(ROW_DELETE);
        new Actions(driver).moveToElement(delete).perform();
        return delete.getAttribute("title");
    }

    public String editUrlOfFirstRow() {
        return rows().get(0).findElement(ROW_EDIT).getAttribute("href");
    }

    /** Bấm nút bút chì ở dòng thứ index (đếm từ 0) và chờ trang sửa tải. */
    protected void clickEditOfRow(int index) {
        andWaitForNewPage(() -> clickRowButton(rows().get(index).findElement(ROW_EDIT)));
    }

    /**
     * Bấm nút trên dòng. Giá trị rất dài làm vỡ bố cục (AMB-ATTR-11): footable giấu cột Options, hoặc thẻ bên phải
     * đè lên cột Action — nút không bấm được bằng chuột. Khi đó gọi click() của chính nút đó, trình xử lý của trang
     * vẫn chạy như khi bấm. Việc kiểm nút có bấm trúng được hay không nằm ở TC_038, không ở đây.
     */
    protected void clickRowButton(WebElement button) {
        try {
            button.click();
        } catch (ElementNotInteractableException e) {
            log.warn("Nút {} trên dòng bị giấu hoặc bị che — bấm qua JS", button.getAttribute("title"));
            js("arguments[0].click();", button);
        }
    }

    public boolean isNothingFoundShown() {
        return isPresentNow(NOTHING_FOUND);
    }

    // ---------- Phân trang ----------

    public boolean hasPagination() {
        return isPresentNow(PAGINATION);
    }

    public String activePage() {
        return driver.findElements(ACTIVE_PAGE).isEmpty() ? "" : driver.findElement(ACTIVE_PAGE).getText().trim();
    }

    /** Số trang lớn nhất đang hiện trên thanh phân trang (kể cả trang đang xem). */
    public int lastPageNumber() {
        List<String> labels = new ArrayList<>(driver.findElements(PAGE_LINKS).stream().map(e -> e.getText().trim()).toList());
        labels.add(activePage());
        return labels.stream().filter(t -> t.matches("\\d+")).mapToInt(Integer::parseInt).max().orElse(1);
    }

    public boolean isNextDisabled() {
        return isPresentNow(NEXT_DISABLED);
    }

    @Step("Bấm nút phân trang {label}")
    public void clickPage(String label) {
        WebElement target = driver.findElements(By.cssSelector(".pagination .page-link")).stream()
                .filter(e -> e.getText().trim().equals(label)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Không có nút phân trang " + label));
        if ("a".equals(target.getTagName())) {
            andWaitForNewPage(target::click);
            waitForListTitle();
        } else {
            target.click();
        }
    }

    // ---------- Xoá ----------

    @Step("Bấm nút thùng rác ở dòng {index} (đếm từ 0)")
    public void openDeleteOfRow(int index) {
        clickRowButton(rows().get(index).findElement(ROW_DELETE));
        waitForVisible(DELETE_MODAL);
        wait.until(ExpectedConditions.elementToBeClickable(CONFIRM_DELETE_BUTTON));
    }

    public void openDeleteOfFirstResult() {
        openDeleteOfRow(0);
    }

    public boolean isDeleteModalShown() {
        return isPresentNow(DELETE_MODAL);
    }

    public String deleteModalTitle() {
        return getText(DELETE_MODAL_TITLE);
    }

    public String deleteModalText() {
        return getText(DELETE_MODAL_TEXT);
    }

    public List<String> deleteModalButtons() {
        return List.of(getText(DELETE_MODAL_CANCEL), getText(CONFIRM_DELETE_BUTTON));
    }

    @Step("Đóng hộp xác nhận xoá bằng {how}")
    public void closeDeleteModal(CloseBy how) {
        switch (how) {
            case CANCEL -> click(DELETE_MODAL_CANCEL);
            case CLOSE_ICON -> click(DELETE_MODAL_CLOSE);
            case ESC -> new Actions(driver).sendKeys(Keys.ESCAPE).perform();
        }
    }

    /** Chờ hộp xác nhận đóng; hết thời gian chờ mà vẫn mở thì trả false. */
    public boolean waitDeleteModalClosed() {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(DELETE_MODAL));
        } catch (TimeoutException e) {
            return false;
        }
    }

    @Step("Bấm Delete trong hộp xác nhận")
    public void confirmDelete() {
        andWaitForNewPage(() -> click(CONFIRM_DELETE_BUTTON));
        waitForListTitle();
    }

    @Step("Xoá dòng {index} (đếm từ 0) qua hộp xác nhận")
    public void deleteRow(int index) {
        openDeleteOfRow(index);
        confirmDelete();
    }

    public void deleteFirstResult() {
        deleteRow(0);
    }

    protected List<String> texts(By locator) {
        return driver.findElements(locator).stream().map(e -> e.getText().trim()).toList();
    }
}
