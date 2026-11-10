package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Danh sách sản phẩm: All products /admin/products/all · In House /admin/products/admin · Seller /admin/products/seller.
 * Bảng 15 dòng/trang (ô chọn, Name, Added By*, Info, Total Stock, Todays Deal, Published, Featured, Options) — trang
 * In House không có cột Added By nên cột được tra theo tiêu đề. Thanh điều khiển: Bulk Action, All Sellers, Sort by,
 * ô tìm "Type &amp; Enter". Locator lấy từ DOM thật 04-11-2026.
 */
public class ProductListPage extends AdminListPage {

    /** Ba trang danh sách dùng chung bố cục. */
    public enum Listing {
        ALL("/admin/products/all"), IN_HOUSE("/admin/products/admin"), SELLER("/admin/products/seller");

        private final String path;

        Listing(String path) {
            this.path = path;
        }

        public String path() {
            return path;
        }
    }

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='All products']");
    private static final By CARD_TITLE = By.cssSelector("#sort_products .card-header h5");
    private static final By ADD_NEW_BUTTON = By.xpath("//a[contains(@href,'/admin/products/create')][normalize-space()='Add New Product']");
    private static final By BULK_ACTION = By.xpath("//button[contains(@class,'dropdown-toggle')][normalize-space()='Bulk Action']");
    private static final By DELETE_SELECTION = By.xpath("//a[contains(@class,'dropdown-item')][normalize-space()='Delete selection']");
    private static final By ROW_BUTTONS = By.cssSelector("td a.btn-circle");
    private static final By ROW_CHECKBOX = By.cssSelector("input.check-one");
    private static final By SIDEBAR_PRODUCTS = By.xpath("//a[contains(@class,'aiz-side-nav-link')][span[normalize-space()='Products']]");

    private final BootstrapSelect sellers = new BootstrapSelect(driver, "user_id");
    private final BootstrapSelect sort = new BootstrapSelect(driver, "type");

    public ProductListPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở All products")
    public ProductListPage open() {
        return open(Listing.ALL);
    }

    @Step("Mở danh sách {listing}")
    public ProductListPage open(Listing listing) {
        navigate(listing.path());
        return waitUntilLoaded();
    }

    @Step("Mở All products trang {page}")
    public ProductListPage openPage(int page) {
        navigate(Listing.ALL.path() + "?page=" + page);
        return waitUntilLoaded();
    }

    /** Bấm mục con của nhóm Products ở sidebar (mở nhóm nếu đang thu gọn) — chờ trang mới tải xong. */
    @Step("Bấm Products → mục {path} ở sidebar")
    public void clickSidebar(String path) {
        By item = By.cssSelector("a.aiz-side-nav-link[href$='" + path + "']");
        if (!isPresentNow(item)) {
            click(SIDEBAR_PRODUCTS);
        }
        andWaitForNewPage(() -> click(item));
    }

    public ProductListPage waitUntilLoaded() {
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

    public String cardTitle() {
        return getText(CARD_TITLE);
    }

    public boolean hasAddNewButton() {
        return isPresentNow(ADD_NEW_BUTTON);
    }

    public boolean hasBulkAction() {
        return isPresentNow(BULK_ACTION);
    }

    public String sellerFilterText() {
        return sellers.shownText();
    }

    public String sortText() {
        return sort.shownText();
    }

    /** Tiêu đề mọi cột (kể cả cột footable đang giấu), theo thứ tự. */
    @SuppressWarnings("unchecked")
    public List<String> allHeaders() {
        return (List<String>) js("return [...document.querySelectorAll('table thead th')].map(t => t.textContent.trim());");
    }

    /** Vị trí cột (đếm từ 1) theo tiêu đề. */
    private int columnOf(String header) {
        int index = allHeaders().indexOf(header);
        if (index < 0) {
            throw new IllegalStateException("Bảng không có cột " + header);
        }
        return index + 1;
    }

    public List<String> names() {
        return columnTexts(columnOf("Name"));
    }

    public int rowIndexOf(String name) {
        List<String> names = names();
        return IntStream.range(0, names.size()).filter(i -> names.get(i).equals(name)).findFirst().orElse(-1);
    }

    public List<Integer> rowIndexesContaining(String text) {
        List<String> names = names();
        return IntStream.range(0, names.size())
                .filter(i -> names.get(i).toLowerCase().contains(text.toLowerCase())).boxed().toList();
    }

    /** Ô Info của dòng — chữ gộp "Num of Sale: … Base Price: … Rating: …". */
    public String infoOfRow(int row) {
        return cellText(rows().get(row), columnOf("Info")).replaceAll("\\s+", " ");
    }

    /** Giá trị Base Price ở ô Info (VD "$10.00"). */
    public String basePriceOfRow(int row) {
        return infoOfRow(row).replaceAll(".*Base Price:\\s*(\\S+).*", "$1");
    }

    /** Giá Base Price đã đổi thành số, theo thứ tự dòng. */
    public List<Double> basePrices() {
        return IntStream.range(0, rowCount()).mapToObj(this::basePriceOfRow)
                .map(p -> Double.parseDouble(p.replaceAll("[^0-9.]", ""))).toList();
    }

    /** Num of Sale ở ô Info, theo thứ tự dòng. */
    public List<Integer> numOfSales() {
        return IntStream.range(0, rowCount()).mapToObj(this::infoOfRow)
                .map(s -> Integer.parseInt(s.replaceAll(".*Num of Sale:\\s*(\\d+).*", "$1"))).toList();
    }

    public String totalStockOfRow(int row) {
        return cellText(rows().get(row), columnOf("Total Stock"));
    }

    /** Ô Info có đủ 3 mục Num of Sale, Base Price, Rating ở mọi dòng. */
    public boolean everyInfoHasSaleBasePriceRating() {
        return IntStream.range(0, rowCount()).mapToObj(this::infoOfRow)
                .allMatch(s -> s.contains("Num of Sale:") && s.contains("Base Price:") && s.contains("Rating:"));
    }

    /** Mọi dòng có 4 nút tròn và 3 công tắc. */
    public boolean everyRowHasFourButtonsAndThreeSwitches() {
        return Boolean.TRUE.equals(js("return [...document.querySelectorAll('table tbody tr')]"
                + ".filter(r => r.querySelector('a.confirm-delete'))"
                + ".every(r => r.querySelectorAll('td a.btn-circle').length === 4 && r.querySelectorAll('label.aiz-switch input').length === 3);"));
    }

    public List<String> rowButtonTitles(int row) {
        return rows().get(row).findElements(ROW_BUTTONS).stream().map(e -> e.getAttribute("title")).toList();
    }

    public String hoverRowButton(int row, int button) {
        WebElement target = rows().get(row).findElements(ROW_BUTTONS).get(button);
        js("arguments[0].scrollIntoView({block:'center'});", target);
        if (target.isDisplayed()) {
            new Actions(driver).moveToElement(target).perform();
        }
        return target.getAttribute("title");
    }

    @Step("Bấm nút thứ {button} (đếm từ 0) ở dòng {row}")
    public void clickRowButtonAt(int row, int button) {
        clickRowButton(rows().get(row).findElements(ROW_BUTTONS).get(button));
    }

    public String viewUrlOfRow(int row) {
        return rows().get(row).findElement(By.cssSelector("a[title='View']")).getAttribute("href");
    }

    public String idOfRow(int row) {
        return rows().get(row).findElement(ROW_EDIT).getAttribute("href").replaceAll(".*/admin/(\\d+)/edit.*", "$1");
    }

    @Step("Bấm nút Edit ở dòng {row} (đếm từ 0)")
    public ProductEditPage editRow(int row) {
        clickEditOfRow(row);
        return new ProductEditPage(driver).waitUntilLoaded();
    }

    @Step("Bấm nút Duplicate ở dòng {row} (đếm từ 0)")
    public void duplicateRow(int row) {
        andWaitForNewPage(() -> clickRowButton(rows().get(row).findElement(By.cssSelector("a[title='Duplicate']"))));
        if (!isServerErrorPage()) {
            waitUntilLoaded();
        }
    }

    // ---------- Công tắc trên dòng ----------

    /** Ba công tắc trên dòng: Todays Deal, Published, Featured. */
    public enum RowSwitch {
        TODAYS_DEAL("update_todays_deal"), PUBLISHED("update_published"), FEATURED("update_featured");

        private final String handler;

        RowSwitch(String handler) {
            this.handler = handler;
        }
    }

    private WebElement switchInput(int row, RowSwitch rowSwitch) {
        return rows().get(row).findElement(By.cssSelector("input[onchange^='" + rowSwitch.handler + "']"));
    }

    public boolean isSwitchOn(int row, RowSwitch rowSwitch) {
        return switchInput(row, rowSwitch).isSelected();
    }

    /** Bấm công tắc — lưu bằng AJAX, trang không tải lại. Chờ trạng thái ô đổi. */
    @Step("Bấm công tắc {rowSwitch} ở dòng {row}")
    public void toggleSwitch(int row, RowSwitch rowSwitch) {
        boolean before = isSwitchOn(row, rowSwitch);
        WebElement slider = switchInput(row, rowSwitch).findElement(By.xpath("following-sibling::span"));
        clickRowButton(slider);
        wait.until(d -> isSwitchOn(row, rowSwitch) != before);
    }

    // ---------- Lọc, sắp xếp ----------

    @Step("Lọc theo người bán {seller}")
    public void filterBySeller(String seller) {
        andWaitForNewPage(() -> sellers.choose(seller));
        waitUntilLoaded();
    }

    @Step("Sắp xếp theo {option}")
    public void sortBy(String option) {
        andWaitForNewPage(() -> sort.choose(option));
        waitUntilLoaded();
    }

    // ---------- Xoá hàng loạt ----------

    @Step("Tick ô chọn ở các dòng {rowIndexes}")
    public void tickRows(List<Integer> rowIndexes) {
        for (int row : rowIndexes) {
            WebElement box = rows().get(row).findElement(ROW_CHECKBOX);
            if (!box.isSelected()) {
                js("arguments[0].click();", box);
            }
        }
    }

    /** id của các dòng đang được tick — kiểm trước khi xoá hàng loạt. */
    @SuppressWarnings("unchecked")
    public List<String> tickedIds() {
        return (List<String>) js("return [...document.querySelectorAll('input.check-one:checked')].map(c => c.value);");
    }

    @Step("Bấm Bulk Action → Delete selection")
    public void clickDeleteSelection() {
        markPage();
        click(BULK_ACTION);
        click(DELETE_SELECTION);
    }

    /** Sau Delete selection: hộp xác nhận hiện ra, hoặc trang tự tải lại (đã xoá ngay). */
    public boolean waitConfirmOrReload() {
        wait.until(d -> isDeleteModalShown() || !isSamePageInstance());
        return isDeleteModalShown();
    }

    // ---------- Dọn dữ liệu ----------

    /** Tìm theo token ở All products rồi xoá lần lượt mọi dòng có tên chứa token (qua hộp xác nhận). */
    @Step("Xoá mọi sản phẩm có tên chứa {token}")
    public void deleteAllContaining(String token) {
        for (int round = 0; round < 10; round++) {
            open();
            search(token);
            List<Integer> matches = rowIndexesContaining(token);
            if (matches.isEmpty()) {
                return;
            }
            deleteRow(matches.get(0));
        }
    }
}
