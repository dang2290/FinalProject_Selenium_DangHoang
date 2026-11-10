package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/**
 * Form Category Information — tạo (/admin/categories/create) và sửa (/admin/categories/edit/{id}?lang=en|vn).
 * Form sửa có thêm 2 tab ngôn ngữ và ô Slug. Locator lấy từ DOM thật 03-11-2026.
 */
public class CategoryFormPage extends BasePage {

    private static final String FORM = "//form[.//input[@name='name']]";

    private static final By PAGE_TITLE = By.xpath("//*[self::h5 or self::h6][normalize-space()='Category Information']");
    private static final By FORM_LABELS = By.xpath(FORM + "//label");
    private static final By TRANSLATABLE_LABELS = By.xpath(FORM + "//label[i[contains(@class,'la-language')]]");
    private static final By TRANSLATABLE_ICON = By.xpath(FORM + "//label/i[contains(@class,'la-language')]");
    private static final By LANGUAGE_TABS = By.cssSelector("a.nav-link[href*='/admin/categories/edit/']");
    private static final By ACTIVE_TAB = By.cssSelector("a.nav-link.active[href*='/admin/categories/edit/']");
    private static final By NAME_INPUT = By.id("name");
    private static final By ORDER_LEVEL_INPUT = By.id("order_level");
    private static final By ORDER_LEVEL_HINT = By.xpath(FORM + "//small[normalize-space()='Higher number has high priority']");
    private static final By META_TITLE_INPUT = By.xpath(FORM + "//input[@name='meta_title']");
    private static final By META_DESCRIPTION_INPUT = By.xpath(FORM + "//textarea[@name='meta_description']");
    private static final By SLUG_INPUT = By.id("slug");
    private static final By SAVE_BUTTON = By.xpath(FORM + "//button[@type='submit']");

    private final BootstrapSelect parent = new BootstrapSelect(driver, "parent_id");
    private final BootstrapSelect type = new BootstrapSelect(driver, "digital");
    private final BootstrapSelect attributes = new BootstrapSelect(driver, "filtering_attributes[]");

    /** Ô ảnh của form. */
    public enum ImageField {
        BANNER("banner"), ICON("icon");

        private final String inputName;

        ImageField(String inputName) {
            this.inputName = inputName;
        }

        private String uploader() {
            return "//div[@data-toggle='aizuploader'][input[@name='" + inputName + "']]";
        }
    }

    public CategoryFormPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở form tạo danh mục")
    public CategoryFormPage openCreate() {
        navigate("/admin/categories/create");
        return waitUntilLoaded();
    }

    @Step("Mở form sửa theo đường dẫn {url}")
    public CategoryFormPage openUrl(String url) {
        driver.get(url);
        return waitUntilLoaded();
    }

    public CategoryFormPage waitUntilLoaded() {
        waitForVisible(PAGE_TITLE);
        waitForVisible(NAME_INPUT);
        return this;
    }

    public boolean isOnCreatePage() {
        return "/admin/categories/create".equals(currentPath());
    }

    public boolean isOnEditPage() {
        return currentPath().startsWith("/admin/categories/edit/");
    }

    public boolean isTitleDisplayed() {
        return isPresentNow(PAGE_TITLE);
    }

    public List<String> labels() {
        return driver.findElements(FORM_LABELS).stream().map(e -> e.getText().trim()).toList();
    }

    /** Nhãn có biểu tượng ngôn ngữ (trường dịch được). */
    public List<String> translatableLabels() {
        return driver.findElements(TRANSLATABLE_LABELS).stream().map(e -> e.getText().trim()).toList();
    }

    /** Chú thích của biểu tượng ngôn ngữ cạnh nhãn Name (hiện khi rê chuột). */
    public String translatableTooltip() {
        WebElement icon = driver.findElement(TRANSLATABLE_ICON);
        new Actions(driver).moveToElement(icon).perform();
        return icon.getAttribute("title");
    }

    // ---------- Tab ngôn ngữ (form sửa) ----------

    public List<String> languageTabs() {
        return driver.findElements(LANGUAGE_TABS).stream().map(e -> e.getText().trim()).toList();
    }

    public String activeTab() {
        return getText(ACTIVE_TAB);
    }

    @Step("Chọn tab ngôn ngữ {tab}")
    public void clickTab(String tab) {
        WebElement target = driver.findElements(LANGUAGE_TABS).stream()
                .filter(e -> e.getText().trim().equals(tab)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Không có tab " + tab));
        andWaitForNewPage(target::click);
        waitUntilLoaded();
    }

    // ---------- Đọc giá trị ----------

    public String nameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String orderLevelValue() {
        return driver.findElement(ORDER_LEVEL_INPUT).getAttribute("value");
    }

    public String orderLevelPlaceholder() {
        return driver.findElement(ORDER_LEVEL_INPUT).getAttribute("placeholder");
    }

    public boolean isOrderLevelHintShown() {
        return isPresentNow(ORDER_LEVEL_HINT);
    }

    public String metaTitleValue() {
        return driver.findElement(META_TITLE_INPUT).getAttribute("value");
    }

    public String metaDescriptionValue() {
        return driver.findElement(META_DESCRIPTION_INPUT).getAttribute("value");
    }

    public String slugValue() {
        return driver.findElement(SLUG_INPUT).getAttribute("value");
    }

    public boolean hasSlugField() {
        return isPresentNow(SLUG_INPUT);
    }

    public String parentShown() {
        return parent.shownText();
    }

    public String typeShown() {
        return type.shownText();
    }

    public List<String> typeOptions() {
        return type.optionTexts();
    }

    public String attributesShown() {
        return attributes.shownText();
    }

    public List<String> selectedAttributes() {
        return attributes.selectedTexts();
    }

    public String browseText(ImageField field) {
        return driver.findElement(By.xpath(field.uploader() + "//div[contains(@class,'input-group-text')]")).getText().trim();
    }

    public String fileAmountText(ImageField field) {
        return driver.findElement(By.xpath(field.uploader() + "//div[contains(@class,'file-amount')]")).getText().trim();
    }

    public int previewCount(ImageField field) {
        return driver.findElements(previewItems(field)).size();
    }

    private By previewItems(ImageField field) {
        return By.xpath(field.uploader() + "/following-sibling::div[contains(@class,'file-preview')][1]"
                + "//div[contains(@class,'file-preview-item')]");
    }

    public boolean isSaveDisplayed() {
        return isPresentNow(SAVE_BUTTON);
    }

    public String nameValidationMessage() {
        return validationMessage(NAME_INPUT);
    }

    public String orderLevelValidationMessage() {
        return validationMessage(ORDER_LEVEL_INPUT);
    }

    public boolean isNameFocused() {
        return driver.switchTo().activeElement().equals(driver.findElement(NAME_INPUT));
    }

    /** Trang 419 PAGE EXPIRED — gửi form khi phiên đã hết. */
    public boolean isPageExpired() {
        return driver.getTitle().contains("Page Expired") || driver.getPageSource().contains("419");
    }

    // ---------- Nhập liệu ----------

    @Step("Nhập Name: {name}")
    public void enterName(String name) {
        clearAndType(NAME_INPUT, name);
    }

    @Step("Xoá hết ô Name")
    public void clearName() {
        clearAndType(NAME_INPUT, "");
    }

    @Step("Nhập Ordering Number: {value}")
    public void enterOrderLevel(String value) {
        clearAndType(ORDER_LEVEL_INPUT, value);
    }

    @Step("Nhập Meta Title: {metaTitle}")
    public void enterMetaTitle(String metaTitle) {
        clearAndType(META_TITLE_INPUT, metaTitle);
    }

    @Step("Nhập Meta description")
    public void enterMetaDescription(String metaDescription) {
        clearAndType(META_DESCRIPTION_INPUT, metaDescription);
    }

    @Step("Đặt Slug: {slug}")
    public void enterSlug(String slug) {
        clearAndType(SLUG_INPUT, slug);
    }

    @Step("Chọn Parent Category: {name}")
    public void chooseParent(String name) {
        parent.choose(name);
    }

    /** Gõ từ khoá vào ô tìm của Parent Category, trả về các mục còn hiện (giữ tiền tố cấp "-- "). */
    public List<String> searchParent(String keyword) {
        return parent.search(keyword);
    }

    public boolean isParentNoResultsShown() {
        return parent.isNoResultsShown();
    }

    public void closeParentList() {
        parent.close();
    }

    @Step("Chọn Type: {typeName}")
    public void chooseType(String typeName) {
        type.choose(typeName);
    }

    /** Gõ từ khoá vào ô tìm của Filtering Attributes, trả về các mục còn hiện. */
    public List<String> searchAttributes(String keyword) {
        List<String> found = attributes.search(keyword);
        attributes.close();
        return found;
    }

    @Step("Chọn thuộc tính {name} ở Filtering Attributes")
    public void chooseAttribute(String name) {
        attributes.choose(name);
        attributes.close();
    }

    @Step("Bấm thuộc tính thứ {index} trong Filtering Attributes")
    public void toggleAttribute(int index) {
        attributes.toggleItem(index);
    }

    public boolean isAttributesSearchShown() {
        attributes.open();
        return attributes.hasSearchBox();
    }

    public void closeAttributesList() {
        attributes.close();
    }

    @Step("Mở thư viện tệp của ô {field}")
    public MediaLibraryModal openLibrary(ImageField field) {
        click(By.xpath(field.uploader() + "//div[contains(@class,'input-group-text')]"));
        return new MediaLibraryModal(driver).waitUntilOpen();
    }

    @Step("Bấm nút xoá (×) trên ảnh xem trước của ô {field}")
    public void removePreview(ImageField field) {
        WebElement item = driver.findElement(previewItems(field));
        item.findElement(By.cssSelector(".remove-attachment")).click();
        wait.until(d -> previewCount(field) == 0);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save")
    public void clickSave() {
        driver.findElement(SAVE_BUTTON).click();
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitForNewPage(this::clickSave);
    }

    @Step("Bấm đúp nút Save")
    public void doubleClickSave() {
        andWaitForNewPage(() -> new Actions(driver).doubleClick(waitForVisible(SAVE_BUTTON)).perform());
    }

    @Step("Nhấn Tab {count} lần từ ô Name")
    public List<String> tabSequenceFromName(int count) {
        return tabSequenceFrom(NAME_INPUT, count);
    }

    /** Ô nhập chữ nào trông giống hệt nhau khi đang focus và khi không focus. */
    @Step("Đối chiếu viền của các ô nhập chữ khi đang focus")
    public List<String> fieldsWithoutVisibleFocus() {
        return fieldsWithoutVisibleFocus(List.of(NAME_INPUT, ORDER_LEVEL_INPUT, META_TITLE_INPUT, META_DESCRIPTION_INPUT),
                List.of("Name", "Ordering Number", "Meta Title", "Meta description"));
    }

    /** Ô đã có sẵn giá trị: chọn hết rồi gõ đè (clear() không phát sự kiện bàn phím). */
    private void clearAndType(By locator, String value) {
        WebElement field = waitForVisible(locator);
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        if (!value.isEmpty()) {
            type(locator, value);
        }
    }
}
