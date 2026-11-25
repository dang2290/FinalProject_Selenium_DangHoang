package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Trang /admin/brands — danh sách + thẻ Add New Brand. Locator lấy từ DOM thật 03-11-2026 (khớp docs/requirements/brand). */
public class BrandPage extends MainListPage {

    private static final String ADD_FORM = "form[action$='/admin/brands']:not(#sort_brands) ";

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='All Brands']");
    private static final By CARD_HEADERS = AdminLocators.CARD_HEADER_TITLES;
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

    public BrandPage() {
        super(PAGE_TITLE);
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
        clickInProductsMenu(SIDEBAR_BRAND);
        return waitUntilLoaded();
    }

    /** Sidebar có nhóm Products chứa mục Brand. */
    public boolean isBrandInProductsMenu() {
        return isInProductsMenu(SIDEBAR_BRAND);
    }

    public BrandPage waitUntilLoaded() {
        waitForListTitle();
        return this;
    }

    public List<String> cardHeaders() {
        return WebUI.getAllElementsText(CARD_HEADERS);
    }

    public boolean isBrandMenuActive() {
        return WebUI.isElementDisplayedNow(BRAND_MENU_ACTIVE);
    }

    // ---------- Bảng ----------

    public String logoSrcOfFirstRow() {
        return WebUI.getWebElement(rows().get(0), CELL_LOGO).getAttribute("src");
    }

    public String logoAltOfFirstRow() {
        return WebUI.getWebElement(rows().get(0), CELL_LOGO).getAttribute("alt");
    }

    public boolean isLogoOfFirstRowLoaded() {
        return WebUI.isImageLoaded(WebUI.getWebElement(rows().get(0), CELL_LOGO));
    }

    @Step("Xoá mọi brand có tên chứa {token}")
    public void deleteAllContaining(String token) {
        deleteMatchingRows(token, this::open);
    }

    @Step("Bấm nút bút chì ở dòng đầu")
    public BrandEditPage editFirstResult() {
        clickEditOfRow(0);
        return new BrandEditPage().waitUntilLoaded();
    }

    // ---------- Thẻ Add New Brand ----------

    public List<String> formLabels() {
        return WebUI.getAllElementsText(FORM_LABELS);
    }

    public String namePlaceholder() {
        return WebUI.getElementAttribute(NAME_INPUT, "placeholder");
    }

    public String metaTitlePlaceholder() {
        return WebUI.getElementAttribute(META_TITLE_INPUT, "placeholder");
    }

    public String nameValue() {
        return WebUI.getElementValue(NAME_INPUT);
    }

    public String metaTitleValue() {
        return WebUI.getElementValue(META_TITLE_INPUT);
    }

    public String metaDescriptionValue() {
        return WebUI.getElementValue(META_DESCRIPTION_INPUT);
    }

    public String logoBrowseText() {
        return WebUI.getElementTextNow(LOGO_BROWSE);
    }

    public String logoAmountText() {
        return WebUI.getElementTextNow(LOGO_AMOUNT);
    }

    public int logoPreviewCount() {
        return WebUI.getElementCount(LOGO_PREVIEW_ITEMS);
    }

    public boolean isSaveButtonEnabled() {
        WebElement save = WebUI.getWebElement(SAVE_BUTTON);
        return save.isDisplayed() && save.isEnabled();
    }

    public String saveButtonText() {
        return WebUI.getElementTextNow(SAVE_BUTTON);
    }

    @Step("Nhập tên brand mới: {name}")
    public void enterNewBrandName(String name) {
        WebUI.setText(NAME_INPUT, name);
    }

    @Step("Nhập Meta Title: {metaTitle}")
    public void enterMetaTitle(String metaTitle) {
        WebUI.setText(META_TITLE_INPUT, metaTitle);
    }

    @Step("Nhập Meta description")
    public void enterMetaDescription(String metaDescription) {
        WebUI.setText(META_DESCRIPTION_INPUT, metaDescription);
    }

    @Step("Xoá hết ô Name")
    public void clearName() {
        WebUI.replaceText(NAME_INPUT, "");
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở thẻ Add New Brand")
    public void clickSave() {
        markPage();
        WebUI.clickElement(SAVE_BUTTON);
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitForNewPage(this::clickSave);
    }

    @Step("Bấm đúp nút Save")
    public void doubleClickSave() {
        andWaitForNewPage(() -> WebUI.doubleClickElement(SAVE_BUTTON));
    }

    public void createBrand(String name) {
        enterNewBrandName(name);
        saveAndWait();
    }

    public String nameValidationMessage() {
        return WebUI.getValidationMessage(NAME_INPUT);
    }

    public boolean isNameFocused() {
        return WebUI.isElementFocused(NAME_INPUT);
    }

    @Step("Mở thư viện tệp của ô Logo")
    public MediaLibraryModal openLogoLibrary() {
        WebUI.clickElement(LOGO_BROWSE);
        return new MediaLibraryModal().waitUntilOpen();
    }

    /** Bấm vào ô Name rồi nhấn Tab, trả về tên phần tử nhận focus sau mỗi lần Tab. */
    @Step("Nhấn Tab {count} lần từ ô Name")
    public List<String> tabSequenceFromName(int count) {
        return WebUI.tabSequenceFrom(NAME_INPUT, count);
    }

    /** Ô nhập nào trông giống hệt nhau khi đang focus và khi không focus. */
    @Step("Đối chiếu viền của Name, Meta Title, Meta description khi đang focus")
    public List<String> fieldsWithoutVisibleFocus() {
        return WebUI.fieldsWithoutVisibleFocus(List.of(NAME_INPUT, META_TITLE_INPUT, META_DESCRIPTION_INPUT),
                List.of("Name", "Meta Title", "Meta description"));
    }

    @Step("Focus nút Save rồi nhấn Enter")
    public void pressEnterOnSave() {
        WebElement save = WebUI.getWebElement(SAVE_BUTTON);
        WebUI.focusElement(save);
        andWaitForNewPage(() -> WebUI.pressKey(save, Keys.ENTER, "nút Save"));
    }
}
