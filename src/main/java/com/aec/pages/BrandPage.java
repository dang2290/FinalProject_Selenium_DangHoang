package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/** Trang /admin/brands — danh sách + thẻ Add New Brand. Locator lấy từ DOM thật 03-11-2026 (khớp docs/requirements/brand). */
public class BrandPage extends AdminListPage {

    private static final String ADD_FORM = "form[action$='/admin/brands']:not(#sort_brands) ";

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='All Brands']");
    private static final By CARD_HEADERS = By.cssSelector(".card-header h5");
    private static final By SIDEBAR_PRODUCTS = By.xpath("//a[contains(@class,'aiz-side-nav-link')][span[normalize-space()='Products']]");
    private static final By SIDEBAR_BRAND = By.cssSelector("a.aiz-side-nav-link[href$='/admin/brands']");
    private static final By BRAND_MENU_ACTIVE = By.cssSelector(".aiz-side-nav-link.active[href$='/admin/brands']");

    private static final By FORM_LABELS = By.cssSelector(ADD_FORM + "label");
    private static final By NAME_INPUT = By.cssSelector(ADD_FORM + "input[name='name']");
    private static final By META_TITLE_INPUT = By.cssSelector(ADD_FORM + "input[name='meta_title']");
    private static final By META_DESCRIPTION_INPUT = By.cssSelector(ADD_FORM + "textarea[name='meta_description']");
    private static final By LOGO_BROWSE = By.cssSelector(ADD_FORM + "[data-toggle='aizuploader'] .input-group-text");
    private static final By LOGO_AMOUNT = By.cssSelector(ADD_FORM + ".file-amount");
    private static final By LOGO_PREVIEW_ITEMS = By.cssSelector(ADD_FORM + ".file-preview-item");
    private static final By SAVE_BUTTON = By.cssSelector(ADD_FORM + "button[type='submit']");

    private static final By CELL_LOGO = By.cssSelector("td img");

    public BrandPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang All Brands")
    public BrandPage open() {
        navigate("/admin/brands");
        return waitUntilLoaded();
    }

    @Step("Mở trang danh sách số {page}")
    public BrandPage openPage(int page) {
        navigate("/admin/brands?page=" + page);
        return waitUntilLoaded();
    }

    /** Vào All Brands từ sidebar: mở nhóm Products nếu đang thu gọn rồi bấm Brand. */
    @Step("Bấm Products → Brand ở sidebar")
    public BrandPage openFromSidebar() {
        if (!isPresentNow(SIDEBAR_BRAND)) {
            click(SIDEBAR_PRODUCTS);
        }
        andWaitForNewPage(() -> click(SIDEBAR_BRAND));
        return waitUntilLoaded();
    }

    /** Sidebar có nhóm Products chứa mục Brand. */
    public boolean isBrandInProductsMenu() {
        if (!isPresentNow(SIDEBAR_BRAND)) {
            click(SIDEBAR_PRODUCTS);
        }
        return isVisible(SIDEBAR_BRAND);
    }

    public BrandPage waitUntilLoaded() {
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

    public boolean isBrandMenuActive() {
        return isPresentNow(BRAND_MENU_ACTIVE);
    }

    // ---------- Bảng ----------

    public List<String> getResultNames() {
        return columnTexts(2);
    }

    public String logoSrcOfFirstRow() {
        return rows().get(0).findElement(CELL_LOGO).getAttribute("src");
    }

    public String logoAltOfFirstRow() {
        return rows().get(0).findElement(CELL_LOGO).getAttribute("alt");
    }

    public boolean isLogoOfFirstRowLoaded() {
        return Boolean.TRUE.equals(js("const i=arguments[0];return i.complete && i.naturalWidth>0;",
                rows().get(0).findElement(CELL_LOGO)));
    }

    /** Dọn dữ liệu: tìm theo token rồi xoá lần lượt mọi brand có tên chứa token (qua hộp xác nhận). Chỉ đụng dòng chứa token. */
    @Step("Xoá mọi brand có tên chứa {token}")
    public void deleteAllContaining(String token) {
        for (int round = 0; round < 10; round++) {
            open();
            search(token);
            List<String> names = getResultNames();
            int row = java.util.stream.IntStream.range(0, names.size())
                    .filter(i -> names.get(i).toLowerCase().contains(token.toLowerCase())).findFirst().orElse(-1);
            if (row < 0) {
                return;
            }
            deleteRow(row);
        }
    }

    @Step("Bấm nút bút chì ở dòng đầu")
    public BrandEditPage editFirstResult() {
        clickEditOfRow(0);
        return new BrandEditPage(driver).waitUntilLoaded();
    }

    // ---------- Thẻ Add New Brand ----------

    public List<String> formLabels() {
        return texts(FORM_LABELS);
    }

    public String namePlaceholder() {
        return driver.findElement(NAME_INPUT).getAttribute("placeholder");
    }

    public String metaTitlePlaceholder() {
        return driver.findElement(META_TITLE_INPUT).getAttribute("placeholder");
    }

    public String nameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String metaTitleValue() {
        return driver.findElement(META_TITLE_INPUT).getAttribute("value");
    }

    public String metaDescriptionValue() {
        return driver.findElement(META_DESCRIPTION_INPUT).getAttribute("value");
    }

    public String logoBrowseText() {
        return driver.findElement(LOGO_BROWSE).getText().trim();
    }

    public String logoAmountText() {
        return driver.findElement(LOGO_AMOUNT).getText().trim();
    }

    public int logoPreviewCount() {
        return driver.findElements(LOGO_PREVIEW_ITEMS).size();
    }

    public boolean isSaveButtonEnabled() {
        WebElement save = driver.findElement(SAVE_BUTTON);
        return save.isDisplayed() && save.isEnabled();
    }

    public String saveButtonText() {
        return driver.findElement(SAVE_BUTTON).getText().trim();
    }

    @Step("Nhập tên brand mới: {name}")
    public void enterNewBrandName(String name) {
        type(NAME_INPUT, name);
    }

    @Step("Nhập Meta Title: {metaTitle}")
    public void enterMetaTitle(String metaTitle) {
        type(META_TITLE_INPUT, metaTitle);
    }

    @Step("Nhập Meta description")
    public void enterMetaDescription(String metaDescription) {
        type(META_DESCRIPTION_INPUT, metaDescription);
    }

    @Step("Xoá hết ô Name")
    public void clearName() {
        WebElement name = waitForVisible(NAME_INPUT);
        name.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở thẻ Add New Brand")
    public void clickSave() {
        click(SAVE_BUTTON);
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitForNewPage(this::clickSave);
    }

    @Step("Bấm đúp nút Save")
    public void doubleClickSave() {
        andWaitForNewPage(() -> new Actions(driver).doubleClick(waitForVisible(SAVE_BUTTON)).perform());
    }

    public void createBrand(String name) {
        enterNewBrandName(name);
        saveAndWait();
    }

    public String nameValidationMessage() {
        return validationMessage(NAME_INPUT);
    }

    /** Trang 419 PAGE EXPIRED — gửi form khi phiên đã hết. */
    public boolean isPageExpired() {
        return driver.getTitle().contains("Page Expired") || driver.getPageSource().contains("419");
    }

    public boolean isNameFocused() {
        return driver.switchTo().activeElement().equals(driver.findElement(NAME_INPUT));
    }

    @Step("Mở thư viện tệp của ô Logo")
    public MediaLibraryModal openLogoLibrary() {
        click(LOGO_BROWSE);
        return new MediaLibraryModal(driver).waitUntilOpen();
    }

    /** Bấm vào ô Name rồi nhấn Tab, trả về tên phần tử nhận focus sau mỗi lần Tab. */
    @Step("Nhấn Tab {count} lần từ ô Name")
    public List<String> tabSequenceFromName(int count) {
        return tabSequenceFrom(NAME_INPUT, count);
    }

    /** Ô nhập nào trông giống hệt nhau khi đang focus và khi không focus. */
    @Step("Đối chiếu viền của Name, Meta Title, Meta description khi đang focus")
    public List<String> fieldsWithoutVisibleFocus() {
        return fieldsWithoutVisibleFocus(List.of(NAME_INPUT, META_TITLE_INPUT, META_DESCRIPTION_INPUT),
                List.of("Name", "Meta Title", "Meta description"));
    }

    @Step("Focus nút Save rồi nhấn Enter")
    public void pressEnterOnSave() {
        WebElement save = driver.findElement(SAVE_BUTTON);
        js("arguments[0].focus();", save);
        andWaitForNewPage(() -> save.sendKeys(Keys.ENTER));
    }
}
