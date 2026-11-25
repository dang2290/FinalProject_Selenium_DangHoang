package com.danghoang.pages;

import com.danghoang.keywords.WebUI;
import com.danghoang.utils.LogUtils;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.net.URI;
import java.util.List;

/** Trang /admin sau khi đăng nhập — locator lấy từ DOM thật 03-11-2026. */
public class DashboardPage extends BasePage {

    /** JS: chữ của phần tử đứng ngay sau (vai trò nằm dưới tên tài khoản). */
    private static final String JS_NEXT_SIBLING_TEXT = "return arguments[0].nextElementSibling.innerText.trim();";
    private static final String HOME_PATH = "/home";
    private static final By SIDEBAR = By.cssSelector(".aiz-sidebar");
    private static final By DASHBOARD_MENU_ACTIVE = By.xpath(
            "//a[contains(@class,'aiz-side-nav-link') and contains(@class,'active')][normalize-space()='Dashboard']");
    private static final By ACCOUNT_TOGGLE = By.xpath("(//div[contains(@class,'aiz-topbar')]//*[@data-toggle='dropdown'])[last()]");
    private static final By ACCOUNT_NAME = By.cssSelector(".aiz-topbar .d-block.fw-500");
    private static final By ACCOUNT_MENU_ITEMS = By.cssSelector(".aiz-topbar .dropdown-menu.show a");
    private static final By LOGOUT_ITEM = By.xpath("//div[contains(@class,'aiz-topbar')]//div[contains(@class,'dropdown-menu')]//a[normalize-space()='Logout']");

    @Step("Chờ trang Dashboard tải xong")
    public DashboardPage waitUntilLoaded() {
        WebUI.waitForCurrentURLContains("/admin");
        WebUI.waitForElementVisible(SIDEBAR);
        return this;
    }

    /**
     * Đăng nhập để làm việc tiếp: web đôi khi chuyển về /home dù tài khoản admin đã đăng nhập đúng
     * (23-11-2026, ColorCreateTest TC_022) — khi đó mở thẳng /admin. Test kiểm luồng đăng nhập dùng {@link #waitUntilLoaded()}.
     */
    @Step("Chờ vào trang quản trị sau khi đăng nhập")
    public DashboardPage waitUntilReadyToWork() {
        // Đọc URL qua driver chứ không qua JS: trang có thể còn đang chuyển
        WebUI.waitUntil("địa chỉ /admin hoặc /home", () -> urlPath().startsWith("/admin") || HOME_PATH.equals(urlPath()));
        if (HOME_PATH.equals(urlPath())) {
            LogUtils.warn("Đăng nhập xong web chuyển về {}, mở lại /admin", HOME_PATH);
            Allure.step("Cảnh báo: đăng nhập xong web chuyển về " + HOME_PATH + ", mở lại /admin");
            navigate("/admin");
        }
        return waitUntilLoaded();
    }

    private static String urlPath() {
        return URI.create(WebUI.getCurrentURL()).getPath();
    }

    @Step("Mở trang Dashboard")
    public DashboardPage open() {
        navigate("/admin");
        return waitUntilLoaded();
    }

    public boolean isDashboardMenuActive() {
        return WebUI.isElementVisible(DASHBOARD_MENU_ACTIVE);
    }

    public boolean isSidebarDisplayedNow() {
        return WebUI.isElementDisplayedNow(SIDEBAR);
    }

    public String accountName() {
        return WebUI.getElementText(ACCOUNT_NAME);
    }

    public String accountRole() {
        return (String) WebUI.executeJS(JS_NEXT_SIBLING_TEXT, WebUI.getWebElement(ACCOUNT_NAME));
    }

    @Step("Mở menu tài khoản góc trên phải")
    public List<String> openAccountMenu() {
        WebUI.clickElement(ACCOUNT_TOGGLE);
        WebUI.waitForElementVisible(ACCOUNT_MENU_ITEMS);
        return WebUI.getAllElementsText(ACCOUNT_MENU_ITEMS);
    }

    @Step("Đăng xuất bằng menu tài khoản")
    public LoginPage logout() {
        openAccountMenu();
        WebUI.clickElement(LOGOUT_ITEM);
        WebUI.waitForCurrentURLContains("/login");
        return new LoginPage();
    }

    /** Đăng xuất ở một tab khác rồi quay lại tab đang làm việc (giả lập hết phiên). */
    public void logoutInNewTab(String workingTab) {
        WebUI.openNewTab();
        open().logout();
        WebUI.closeCurrentTab(workingTab);
    }

    // ---------- Menu Products: Dashboard là điểm nối sang các trang ----------

    public BrandPage goToBrands() {
        return new BrandPage().openFromSidebar();
    }

    public CategoryListPage goToCategories() {
        return new CategoryListPage().openFromSidebar();
    }

    public AttributeListPage goToAttributes() {
        return new AttributeListPage().openFromSidebar();
    }

    public ColorListPage goToColors() {
        return new ColorListPage().openFromSidebar();
    }

    public ProductListPage goToAllProducts() {
        ProductListPage products = new ProductListPage();
        products.clickSidebar(ProductListPage.Listing.ALL.path());
        return products.waitUntilLoaded();
    }

    public DigitalProductListPage goToDigitalProducts() {
        new ProductListPage().clickSidebar("/admin/digitalproducts");
        return new DigitalProductListPage().waitUntilLoaded();
    }

    public ProductBulkUploadPage goToBulkImport() {
        new ProductListPage().clickSidebar("/admin/product-bulk-upload/index");
        return new ProductBulkUploadPage().waitUntilLoaded();
    }
}
