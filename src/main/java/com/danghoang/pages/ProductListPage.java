package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Danh sách sản phẩm: All products /admin/products/all · In House /admin/products/admin · Seller /admin/products/seller.
 * Bảng 15 dòng/trang (ô chọn, Name, Added By*, Info, Total Stock, Todays Deal, Published, Featured, Options) — trang
 * In House không có cột Added By nên cột được tra theo tiêu đề. Thanh điều khiển: Bulk Action, All Sellers, Sort by,
 * ô tìm "Type &amp; Enter". Locator lấy từ DOM thật 04-11-2026.
 */
public class ProductListPage extends MainListPage {

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

    /** Nút trên dòng, đúng thứ tự trái → phải trên trang. */
    public enum RowButton { VIEW, EDIT, DUPLICATE, DELETE }

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='All products']");
    private static final By CARD_TITLE = By.cssSelector("#sort_products .card-header h5");
    private static final By ADD_NEW_BUTTON = By.xpath("//a[contains(@href,'/admin/products/create')][normalize-space()='Add New Product']");
    private static final By BULK_ACTION = By.xpath("//button[contains(@class,'dropdown-toggle')][normalize-space()='Bulk Action']");
    private static final By DELETE_SELECTION = By.xpath("//a[contains(@class,'dropdown-item')][normalize-space()='Delete selection']");
    private static final By ROW_DUPLICATE = By.cssSelector("a[title='Duplicate']");
    private static final By SWITCH_SLIDER = By.xpath("following-sibling::span");
    private static final By ROW_CHECKBOX = By.cssSelector("input.check-one");

    private final BootstrapSelect sellers = new BootstrapSelect("user_id");
    private final BootstrapSelect sort = new BootstrapSelect("type");

    public ProductListPage() {
        super(PAGE_TITLE);
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
        clickInProductsMenu(sidebarItem(path));
    }

    private By sidebarItem(String path) {
        return By.cssSelector("a.aiz-side-nav-link[href$='" + path + "']");
    }

    public ProductListPage waitUntilLoaded() {
        waitForListTitle();
        return this;
    }

    public String cardTitle() {
        return WebUI.getElementText(CARD_TITLE);
    }

    public boolean hasAddNewButton() {
        return WebUI.isElementDisplayedNow(ADD_NEW_BUTTON);
    }

    public boolean hasBulkAction() {
        return WebUI.isElementDisplayedNow(BULK_ACTION);
    }

    public String sellerFilterText() {
        return sellers.shownText();
    }

    public String sortText() {
        return sort.shownText();
    }

    /** Vị trí cột (đếm từ 1) theo tiêu đề. */
    private int columnOf(String header) {
        int index = allHeaders().indexOf(header);
        if (index < 0) {
            throw new IllegalStateException("Bảng không có cột " + header);
        }
        return index + 1;
    }

    /** Trang In House không có cột Added By — tra cột Name theo tiêu đề. */
    @Override
    protected int nameColumn() {
        return columnOf("Name");
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
        return Boolean.TRUE.equals(WebUI.executeJS("return " + JS_DATA_ROWS
                + ".every(r => r.querySelectorAll('td a.btn-circle').length === 4 && r.querySelectorAll('label.aiz-switch input').length === 3);"));
    }

    public String hoverRowButton(int row, RowButton button) {
        WebElement target = rowButton(row, button.ordinal());
        WebUI.scrollToElementAtCenter(target);
        if (target.isDisplayed()) {
            WebUI.hoverElement(target, "nút " + button + ", dòng " + row);
        }
        return target.getAttribute("title");
    }

    @Step("Bấm nút {button} ở dòng {row}")
    public void clickRowButtonAt(int row, RowButton button) {
        clickRowButton(rowButton(row, button.ordinal()), button + ", dòng " + row);
    }

    public String duplicateUrlOfRow(int row) {
        return WebUI.getWebElement(rows().get(row), ROW_DUPLICATE).getAttribute("href");
    }

    @Override
    public String idOfRow(int row) {
        return WebUI.getWebElement(rows().get(row), ROW_EDIT).getAttribute("href").replaceAll(".*/admin/(\\d+)/edit.*", "$1");
    }

    @Step("Bấm nút Edit ở dòng {row} (đếm từ 0)")
    public ProductEditPage editRow(int row) {
        clickEditOfRow(row);
        return new ProductEditPage().waitUntilLoaded();
    }

    @Step("Bấm nút Duplicate ở dòng {row} (đếm từ 0)")
    public void duplicateRow(int row) {
        andWaitUnlessError(() -> clickRowButton(WebUI.getWebElement(rows().get(row), ROW_DUPLICATE), "Duplicate, dòng " + row), this::waitUntilLoaded);
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
        return WebUI.getWebElement(rows().get(row), By.cssSelector("input[onchange^='" + rowSwitch.handler + "']"));
    }

    public boolean isSwitchOn(int row, RowSwitch rowSwitch) {
        return switchInput(row, rowSwitch).isSelected();
    }

    /** Bấm công tắc — lưu bằng AJAX, trang không tải lại. Chờ trạng thái ô đổi. */
    @Step("Bấm công tắc {rowSwitch} ở dòng {row}")
    public void toggleSwitch(int row, RowSwitch rowSwitch) {
        boolean before = isSwitchOn(row, rowSwitch);
        WebElement slider = WebUI.getWebElement(switchInput(row, rowSwitch), SWITCH_SLIDER);
        markPage();
        ignoreShownNotifies();
        clickRowButton(slider, "công tắc " + rowSwitch + ", dòng " + row);
        WebUI.waitUntil("công tắc " + rowSwitch + " đổi trạng thái", () -> isSwitchOn(row, rowSwitch) != before);
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
            WebElement box = WebUI.getWebElement(rows().get(row), ROW_CHECKBOX);
            if (!box.isSelected()) {
                WebUI.clickElementByJS(box, "ô chọn dòng " + row);
            }
        }
    }

    /** id của các dòng đang được tick — kiểm trước khi xoá hàng loạt. */
    public List<String> tickedIds() {
        return WebUI.getAllValuesByJS("input.check-one:checked");
    }

    @Step("Bấm Bulk Action → Delete selection")
    public void clickDeleteSelection() {
        markPage();
        WebUI.clickElement(BULK_ACTION);
        WebUI.clickElement(DELETE_SELECTION);
    }

    /** Sau Delete selection: hộp xác nhận hiện ra, hoặc trang tự tải lại (đã xoá ngay). */
    public boolean waitConfirmOrReload() {
        WebUI.waitUntil("hộp xác nhận hoặc trang tải lại", () -> isDeleteModalShown() || !isSamePageInstance());
        return isDeleteModalShown();
    }

    // ---------- Dọn dữ liệu ----------

    /** Tìm theo token ở All products rồi xoá lần lượt mọi dòng có tên chứa token (qua hộp xác nhận). */
    @Step("Xoá mọi sản phẩm có tên chứa {token}")
    public void deleteAllContaining(String token) {
        deleteMatchingRows(token, this::open);
    }

    /** Bấm View: mở tab mới, chuyển sang trang sản phẩm ngoài cửa hàng. */
    public StorefrontProductPage viewRow(int row, String adminTab) {
        clickRowButtonAt(row, RowButton.VIEW);
        switchToNewWindow(adminTab);
        return new StorefrontProductPage();
    }
}
