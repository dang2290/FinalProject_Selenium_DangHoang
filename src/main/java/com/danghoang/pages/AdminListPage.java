package com.danghoang.pages;

import com.danghoang.config.ConfigReader;
import com.danghoang.keywords.WebUI;
import com.danghoang.utils.LogUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Phần chung của trang danh sách quản trị (Brand, Category…): ô tìm kiếm #search, bảng footable,
 * thanh phân trang Laravel, nút Edit/Delete mỗi dòng, hộp #delete-modal. Locator kiểm trên DOM thật 03-11-2026.
 */
public abstract class AdminListPage extends BasePage {

    /** JS: các dòng dữ liệu của bảng (dòng có nút xoá), bỏ dòng "Nothing found". Ghép sau "return ". */
    protected static final String JS_DATA_ROWS = "[...document.querySelectorAll('table tbody tr')].filter(r => r.querySelector('a.confirm-delete'))";
    private static final By SEARCH_INPUT = AdminLocators.SEARCH_INPUT;
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
    protected static final By ROW_BUTTONS = By.cssSelector("td a.btn-circle");
    private static final By SIDEBAR_PRODUCTS = By.xpath("//a[contains(@class,'aiz-side-nav-link')][span[normalize-space()='Products']]");
    private static final By PAGE_ITEMS = By.cssSelector(".pagination .page-link");
    private static final int DEFAULT_NAME_COLUMN = 2;
    private static final int MAX_CLEANUP_ROUNDS = 10;

    /** Cách đóng hộp xác nhận xoá mà không xoá. */
    public enum CloseBy { CANCEL, CLOSE_ICON, ESC }

    private final By title;

    /** title: phần tử tiêu đề riêng của trang danh sách. */
    protected AdminListPage(By title) {
        super();
        this.title = title;
    }

    /** Chờ tiêu đề trang danh sách hiện và bảng dựng xong. */
    protected void waitForListTitle() {
        WebUI.waitForElementVisible(title);
        waitForTableReady();
    }

    public boolean isTitleDisplayed() {
        return WebUI.isElementDisplayedNow(title);
    }

    /**
     * Footable ẩn bảng và hiện vòng xoay div.footable-loader trong lúc dựng (vendors.js: $el.hide().after($loader)),
     * dựng xong mới hiện lại bảng — đọc dòng trước lúc đó sẽ ra 0 dòng.
     */
    protected void waitForTableReady() {
        // Bảng một trang ~600 dòng (Attribute) dựng chậm hơn hẳn trang 15 dòng — cho gấp 3 thời gian chờ thường
        WebUI.waitUntil("bảng dựng xong", Duration.ofSeconds(ConfigReader.getInstance().getTimeout() * 3L),
                () -> !WebUI.checkElementExist(TABLE_LOADER) && WebUI.isElementDisplayedNow(LIST_TABLE));
    }

    // ---------- Tìm kiếm ----------

    @Step("Tìm: {keyword}")
    public void search(String keyword) {
        andWaitForNewPage(() -> {
            WebUI.setText(SEARCH_INPUT, keyword);
            WebUI.pressKey(SEARCH_INPUT, Keys.ENTER);
        });
        if (isServerErrorPage()) {
            LogUtils.warn("Tìm '{}' ra trang lỗi máy chủ", keyword);
            return;
        }
        waitForListTitle();
    }

    public String searchPlaceholder() {
        return WebUI.getElementAttribute(SEARCH_INPUT, "placeholder");
    }

    public String searchValue() {
        return WebUI.getElementValue(SEARCH_INPUT);
    }

    // ---------- Bảng ----------

    protected List<WebElement> rows() {
        return WebUI.getWebElements(ROWS);
    }

    public int rowCount() {
        return rows().size();
    }

    /** Chữ của ô cột thứ column (đếm từ 1) — đọc textContent để vẫn đúng khi footable ẩn cột ở màn hẹp. */
    protected String cellText(WebElement row, int column) {
        return WebUI.getWebElement(row, By.xpath("./td[" + column + "]")).getAttribute("textContent").trim();
    }

    /** Chữ cả một cột (đếm từ 1) — đọc một lần bằng JS vì bảng có thể tới hàng trăm dòng. */
    @SuppressWarnings("unchecked")
    protected List<String> columnTexts(int column) {
        return (List<String>) WebUI.executeJS("return " + JS_DATA_ROWS
                + ".map(r => (r.cells[arguments[0] - 1] || {textContent: ''}).textContent.trim());", column);
    }

    public List<Integer> rowNumbers() {
        return columnTexts(1).stream().map(Integer::parseInt).toList();
    }

    /** Tiêu đề các cột đang hiện. */
    public List<String> tableHeaders() {
        return WebUI.getWebElements(TABLE_HEADERS).stream().filter(WebElement::isDisplayed)
                .map(e -> e.getText().trim()).toList();
    }

    /** Mỗi dòng đều có nút Edit (bút chì) và Delete (thùng rác). */
    public boolean everyRowHasEditAndDelete() {
        return rows().stream().allMatch(r -> WebUI.getWebElement(r, ROW_EDIT).isDisplayed() && WebUI.getWebElement(r, ROW_DELETE).isDisplayed());
    }

    /** Chú thích hiện khi rê chuột lên nút bút chì (thuộc tính title). */
    public String editTooltipOfFirstRow() {
        return WebUI.getTooltipOnHover(WebUI.getWebElement(rows().get(0), ROW_EDIT), "nút Edit dòng đầu");
    }

    /** Chú thích hiện khi rê chuột lên nút thùng rác (thuộc tính title). */
    public String deleteTooltipOfFirstRow() {
        return WebUI.getTooltipOnHover(WebUI.getWebElement(rows().get(0), ROW_DELETE), "nút Delete dòng đầu");
    }

    public String editUrlOfFirstRow() {
        return WebUI.getWebElement(rows().get(0), ROW_EDIT).getAttribute("href");
    }

    /** Bấm nút bút chì ở dòng thứ index (đếm từ 0) và chờ trang sửa tải. */
    protected void clickEditOfRow(int index) {
        andWaitForNewPage(() -> clickRowButton(WebUI.getWebElement(rows().get(index), ROW_EDIT), "Edit, dòng " + index));
    }

    /**
     * Bấm nút trên dòng. Giá trị rất dài làm vỡ bố cục (AMB-ATTR-11): footable giấu cột Options, hoặc thẻ bên phải
     * đè lên cột Action — nút không bấm được bằng chuột. Khi đó gọi WebUI.clickElement() của chính nút đó, trình xử lý của trang
     * vẫn chạy như khi bấm. Việc kiểm nút có bấm trúng được hay không nằm ở TC_038, không ở đây.
     */
    protected void clickRowButton(WebElement button, String what) {
        WebUI.clickElementOrByJS(button, what);
    }

    public boolean isNothingFoundShown() {
        return WebUI.isElementDisplayedNow(NOTHING_FOUND);
    }

    // ---------- Tra dòng theo tên, nút trên dòng ----------

    /** Cột (đếm từ 1) chứa tên bản ghi. */
    protected int nameColumn() {
        return DEFAULT_NAME_COLUMN;
    }

    /** Tên ở mọi dòng của trang đang xem, theo thứ tự. */
    public List<String> names() {
        return columnTexts(nameColumn());
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

    /** Tiêu đề mọi cột (kể cả cột footable đang giấu), theo thứ tự. */
    public List<String> allHeaders() {
        return WebUI.getAllTextsByJS("table thead th");
    }

    /** Chú thích (title) các nút tròn trên dòng thứ row (đếm từ 0), theo thứ tự. */
    public List<String> rowButtonTitles(int row) {
        return WebUI.getWebElements(rows().get(row), ROW_BUTTONS).stream().map(e -> e.getAttribute("title")).toList();
    }

    /** Nút tròn thứ position của dòng — trang con đặt tên vị trí bằng enum RowButton của nó. */
    protected WebElement rowButton(int row, int position) {
        return WebUI.getWebElements(rows().get(row), ROW_BUTTONS).get(position);
    }

    /** Id bản ghi ở dòng thứ row (đếm từ 0) — lấy từ đường dẫn nút Edit. */
    public String idOfRow(int row) {
        return WebUI.getWebElement(rows().get(row), ROW_EDIT).getAttribute("href").replaceAll(".*/edit/(\\d+).*", "$1");
    }

    // ---------- Nhóm Products ở sidebar ----------

    /** Bấm mục con của nhóm Products (mở nhóm nếu đang thu gọn), chờ trang mới. */
    protected void clickInProductsMenu(By item) {
        expandProductsMenu(item);
        andWaitForNewPage(() -> WebUI.clickElement(item));
    }

    protected boolean isInProductsMenu(By item) {
        expandProductsMenu(item);
        return WebUI.isElementVisible(item);
    }

    private void expandProductsMenu(By item) {
        if (!WebUI.isElementDisplayedNow(item)) {
            WebUI.clickElement(SIDEBAR_PRODUCTS);
        }
    }

    // ---------- Phân trang ----------

    public boolean hasPagination() {
        return WebUI.isElementDisplayedNow(PAGINATION);
    }

    public String activePage() {
        return !WebUI.checkElementExist(ACTIVE_PAGE) ? "" : WebUI.getElementTextNow(ACTIVE_PAGE);
    }

    /** Số trang lớn nhất đang hiện trên thanh phân trang (kể cả trang đang xem). */
    public int lastPageNumber() {
        List<String> labels = new ArrayList<>(WebUI.getAllElementsText(PAGE_LINKS));
        labels.add(activePage());
        return labels.stream().filter(t -> t.matches("\\d+")).mapToInt(Integer::parseInt).max().orElse(1);
    }

    public boolean isNextDisabled() {
        return WebUI.isElementDisplayedNow(NEXT_DISABLED);
    }

    @Step("Bấm nút phân trang {label}")
    public void clickPage(String label) {
        WebElement target = WebUI.getWebElements(PAGE_ITEMS).stream()
                .filter(e -> e.getText().trim().equals(label)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Không có nút phân trang " + label));
        if ("a".equals(target.getTagName())) {
            andWaitForNewPage(() -> WebUI.clickElement(target, "trang " + label));
            waitForListTitle();
        } else {
            WebUI.clickElement(target, "trang " + label);
        }
    }

    // ---------- Xoá ----------

    @Step("Bấm nút thùng rác ở dòng {index} (đếm từ 0)")
    public void openDeleteOfRow(int index) {
        clickRowButton(WebUI.getWebElement(rows().get(index), ROW_DELETE), "Delete, dòng " + index);
        WebUI.waitForElementVisible(DELETE_MODAL);
        WebUI.waitForElementClickable(CONFIRM_DELETE_BUTTON);
    }

    public void openDeleteOfFirstResult() {
        openDeleteOfRow(0);
    }

    public boolean isDeleteModalShown() {
        return WebUI.isElementDisplayedNow(DELETE_MODAL);
    }

    public String deleteModalTitle() {
        return WebUI.getElementText(DELETE_MODAL_TITLE);
    }

    public String deleteModalText() {
        return WebUI.getElementText(DELETE_MODAL_TEXT);
    }

    public List<String> deleteModalButtons() {
        return List.of(WebUI.getElementText(DELETE_MODAL_CANCEL), WebUI.getElementText(CONFIRM_DELETE_BUTTON));
    }

    @Step("Đóng hộp xác nhận xoá bằng {how}")
    public void closeDeleteModal(CloseBy how) {
        switch (how) {
            case CANCEL -> WebUI.clickElement(DELETE_MODAL_CANCEL);
            case CLOSE_ICON -> WebUI.clickElement(DELETE_MODAL_CLOSE);
            case ESC -> WebUI.pressKey(Keys.ESCAPE);
        }
    }

    /** Chờ hộp xác nhận đóng; hết thời gian chờ mà vẫn mở thì trả false. */
    public boolean waitDeleteModalClosed() {
        return WebUI.waitUntilElementGone(DELETE_MODAL);
    }

    @Step("Bấm Delete trong hộp xác nhận")
    public void confirmDelete() {
        andWaitForNewPage(() -> WebUI.clickElement(CONFIRM_DELETE_BUTTON));
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

    /** Dọn dữ liệu test: tìm theo token rồi xoá lần lượt dòng có tên chứa token. Chỉ đụng dòng chứa token. */
    protected void deleteMatchingRows(String token, Runnable openList) {
        for (int round = 0; round < MAX_CLEANUP_ROUNDS; round++) {
            openList.run();
            search(token);
            List<Integer> matches = rowIndexesContaining(token);
            if (matches.isEmpty()) {
                return;
            }
            deleteRow(matches.get(0));
        }
    }
}
