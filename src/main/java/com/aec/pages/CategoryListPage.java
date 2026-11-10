package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Trang All categories /admin/categories — locator lấy từ DOM thật 03-11-2026 (khớp docs/requirements/category).
 * Cột: 1 # · 2 Name · 3 Parent Category · 4 Order Level · 5 Level · 6 Banner · 7 Icon · 8 Featured · 9 Commission · 10 Options.
 * Màn hẹp (≤ 1366): footable chỉ hiện Name, Options; dấu + ở ô Name mở hàng chi tiết.
 */
public class CategoryListPage extends AdminListPage {

    private static final int COL_NAME = 2;
    private static final int COL_PARENT = 3;
    private static final int COL_ORDER_LEVEL = 4;
    private static final int COL_LEVEL = 5;
    private static final int COL_BANNER = 6;
    private static final int COL_ICON = 7;
    private static final int COL_COMMISSION = 9;
    private static final int MAX_CLEANUP_ROUNDS = 15;

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='All categories']");
    private static final By CARD_HEADERS = By.cssSelector(".card-header h5, .card-header h6");
    private static final By ADD_NEW_BUTTON = By.xpath("//a[contains(@href,'/admin/categories/create')][normalize-space()='Add New category']");
    private static final By SIDEBAR_PRODUCTS = By.xpath("//a[contains(@class,'aiz-side-nav-link')][span[normalize-space()='Products']]");
    private static final By SIDEBAR_CATEGORY = By.cssSelector("a.aiz-side-nav-link[href$='/admin/categories']");
    private static final By CATEGORY_MENU_ACTIVE = By.cssSelector(".aiz-side-nav-link.active[href$='/admin/categories']");

    private static final By ROW_FEATURED_INPUT = By.cssSelector("input[onchange^='update_featured']");
    private static final By ROW_FEATURED_SWITCH = By.cssSelector("label.aiz-switch");
    private static final By EXPAND_TOGGLE = By.cssSelector(".footable-toggle");
    private static final By DETAIL_ROW = By.cssSelector("table tbody tr.footable-detail-row");
    private static final By DETAIL_HEADERS = By.cssSelector("table tbody tr.footable-detail-row th");
    private static final By DETAIL_FEATURED_SWITCH = By.cssSelector("table tbody tr.footable-detail-row label.aiz-switch");
    private static final By DETAIL_FEATURED_INPUT = By.cssSelector("table tbody tr.footable-detail-row input[onchange^='update_featured']");

    public CategoryListPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang All categories")
    public CategoryListPage open() {
        navigate("/admin/categories");
        return waitUntilLoaded();
    }

    @Step("Mở trang danh sách số {page}")
    public CategoryListPage openPage(int page) {
        navigate("/admin/categories?page=" + page);
        return waitUntilLoaded();
    }

    /** Vào All categories từ sidebar: mở nhóm Products nếu đang thu gọn rồi bấm Category. */
    @Step("Bấm Products → Category ở sidebar")
    public CategoryListPage openFromSidebar() {
        if (!isPresentNow(SIDEBAR_CATEGORY)) {
            click(SIDEBAR_PRODUCTS);
        }
        andWaitForNewPage(() -> click(SIDEBAR_CATEGORY));
        return waitUntilLoaded();
    }

    /** Sidebar có nhóm Products chứa mục Category. */
    public boolean isCategoryInProductsMenu() {
        if (!isPresentNow(SIDEBAR_CATEGORY)) {
            click(SIDEBAR_PRODUCTS);
        }
        return isVisible(SIDEBAR_CATEGORY);
    }

    public CategoryListPage waitUntilLoaded() {
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

    public boolean isAddNewButtonDisplayed() {
        return isPresentNow(ADD_NEW_BUTTON);
    }

    public boolean isCategoryMenuActive() {
        return isPresentNow(CATEGORY_MENU_ACTIVE);
    }

    @Step("Bấm Add New category")
    public CategoryFormPage clickAddNew() {
        andWaitForNewPage(() -> click(ADD_NEW_BUTTON));
        return new CategoryFormPage(driver).waitUntilLoaded();
    }

    // ---------- Đọc bảng ----------

    public List<String> names() {
        return columnTexts(COL_NAME);
    }

    public List<Long> orderLevels() {
        return columnTexts(COL_ORDER_LEVEL).stream().map(Long::parseLong).toList();
    }

    public List<String> commissions() {
        return columnTexts(COL_COMMISSION);
    }

    /** Vị trí (từ 0) của dòng mang đúng tên này, -1 nếu không có. */
    public int rowIndexOf(String name) {
        List<String> names = names();
        return IntStream.range(0, names.size()).filter(i -> names.get(i).equals(name)).findFirst().orElse(-1);
    }

    public String parentOf(int row) {
        return cellText(rows().get(row), COL_PARENT);
    }

    public String orderLevelOf(int row) {
        return cellText(rows().get(row), COL_ORDER_LEVEL);
    }

    public String levelOf(int row) {
        return cellText(rows().get(row), COL_LEVEL);
    }

    public List<Integer> levels() {
        return columnTexts(COL_LEVEL).stream().map(Integer::parseInt).toList();
    }

    public String bannerOf(int row) {
        return cellText(rows().get(row), COL_BANNER);
    }

    public String iconOf(int row) {
        return cellText(rows().get(row), COL_ICON);
    }

    public String commissionOf(int row) {
        return cellText(rows().get(row), COL_COMMISSION);
    }

    /** Ô Banner / Icon có ảnh thu nhỏ tải được (không phải "—", không phải ảnh lỗi). */
    public boolean hasLoadedImage(int row, boolean banner) {
        WebElement cell = rows().get(row).findElement(By.xpath("./td[" + (banner ? COL_BANNER : COL_ICON) + "]"));
        List<WebElement> images = cell.findElements(By.tagName("img"));
        return !images.isEmpty() && Boolean.TRUE.equals(
                js("const i=arguments[0];return i.complete && i.naturalWidth>0;", images.get(0)));
    }

    public boolean isFeaturedOn(int row) {
        return rows().get(row).findElement(ROW_FEATURED_INPUT).isSelected();
    }

    /** Mỗi dòng có công tắc Featured, nút bút chì và thùng rác. */
    public boolean everyRowHasFeaturedEditDelete() {
        return everyRowHasEditAndDelete()
                && rows().stream().allMatch(r -> !r.findElements(ROW_FEATURED_INPUT).isEmpty());
    }

    @Step("Bấm công tắc Featured ở dòng {row} (đếm từ 0)")
    public void toggleFeatured(int row) {
        boolean before = isFeaturedOn(row);
        rows().get(row).findElement(ROW_FEATURED_SWITCH).click();
        wait.until(d -> isFeaturedOn(row) != before);
    }

    @Step("Bấm nút bút chì ở dòng {row} (đếm từ 0)")
    public CategoryFormPage editRow(int row) {
        clickEditOfRow(row);
        return new CategoryFormPage(driver).waitUntilLoaded();
    }

    /**
     * Dọn dữ liệu test: xoá mọi danh mục có tên chứa token, mỗi vòng xoá dòng có Level lớn nhất (cháu → con → cha).
     * Chỉ đụng dòng chứa token — không bao giờ chạm danh mục khác.
     */
    @Step("Xoá mọi danh mục chứa {token}")
    public void deleteAllContaining(String token) {
        for (int round = 0; round < MAX_CLEANUP_ROUNDS; round++) {
            open();
            search(token);
            List<String> names = names();
            List<Integer> levels = levels();
            int target = IntStream.range(0, names.size())
                    .filter(i -> names.get(i).toLowerCase().contains(token.toLowerCase()))
                    .boxed().max((a, b) -> Integer.compare(levels.get(a), levels.get(b))).orElse(-1);
            if (target < 0) {
                return;
            }
            deleteRow(target);
        }
    }

    // ---------- Màn hẹp: hàng chi tiết footable ----------

    public boolean everyRowHasExpandToggle() {
        return rows().stream().allMatch(r -> !r.findElements(EXPAND_TOGGLE).isEmpty());
    }

    @Step("Bấm dấu +/− ở dòng {row} (đếm từ 0)")
    public void clickExpandToggle(int row) {
        boolean shownBefore = isDetailRowShown();
        rows().get(row).findElement(EXPAND_TOGGLE).click();
        wait.until(d -> isDetailRowShown() != shownBefore);
    }

    public boolean isDetailRowShown() {
        return isPresentNow(DETAIL_ROW);
    }

    public List<String> detailHeaders() {
        return texts(DETAIL_HEADERS);
    }

    public boolean isDetailFeaturedOn() {
        return driver.findElement(DETAIL_FEATURED_INPUT).isSelected();
    }

    @Step("Bấm công tắc Featured trong hàng chi tiết")
    public void toggleDetailFeatured() {
        boolean before = isDetailFeaturedOn();
        click(DETAIL_FEATURED_SWITCH);
        wait.until(d -> isDetailFeaturedOn() != before);
    }
}
