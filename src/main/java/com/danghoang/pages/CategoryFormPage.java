package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Form Category Information — tạo (/admin/categories/create) và sửa (/admin/categories/edit/{id}?lang=en|vn).
 * Form sửa có thêm 2 tab ngôn ngữ và ô Slug. Locator lấy từ DOM thật 03-11-2026.
 */
public class CategoryFormPage extends TranslatableFormPage {

    private static final String FORM = "//form[.//input[@name='name']]";

    private static final By PAGE_TITLE = By.xpath("//*[self::h5 or self::h6][normalize-space()='Category Information']");
    private static final By FORM_LABELS = By.xpath(FORM + "//label");
    private static final By TRANSLATABLE_LABELS = By.xpath(FORM + "//label[i[contains(@class,'la-language')]]");
    private static final By TRANSLATABLE_ICON = By.xpath(FORM + "//label/i[contains(@class,'la-language')]");
    private static final By NAME_INPUT = AdminLocators.NAME_INPUT;
    private static final By ORDER_LEVEL_INPUT = By.id("order_level");
    private static final By ORDER_LEVEL_HINT = By.xpath(FORM + "//small[normalize-space()='Higher number has high priority']");
    private static final By META_TITLE_INPUT = By.xpath(FORM + "//input[@name='meta_title']");
    private static final By META_DESCRIPTION_INPUT = By.xpath(FORM + "//textarea[@name='meta_description']");
    private static final By SLUG_INPUT = AdminLocators.SLUG_INPUT;
    private static final By SAVE_BUTTON = By.xpath(FORM + "//button[@type='submit']");

    private static final By REMOVE_ATTACHMENT = By.cssSelector(".remove-attachment");

    private final BootstrapSelect parent = new BootstrapSelect("parent_id");
    private final BootstrapSelect type = new BootstrapSelect("digital");
    private final BootstrapSelect attributes = new BootstrapSelect("filtering_attributes[]");

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

    public CategoryFormPage() {
        super(PAGE_TITLE, NAME_INPUT, "/admin/categories/edit/");
    }

    @Step("Mở form tạo danh mục")
    public CategoryFormPage openCreate() {
        navigate("/admin/categories/create");
        return waitUntilLoaded();
    }

    @Step("Mở form sửa theo đường dẫn {url}")
    public CategoryFormPage openUrl(String url) {
        WebUI.openURL(url);
        return waitUntilLoaded();
    }

    @Override
    protected void waitForForm() {
        super.waitForForm();
        WebUI.waitForElementVisible(NAME_INPUT);
    }

    public CategoryFormPage waitUntilLoaded() {
        waitForForm();
        return this;
    }

    public boolean isOnCreatePage() {
        return "/admin/categories/create".equals(currentPath());
    }

    public boolean isOnEditPage() {
        return currentPath().startsWith("/admin/categories/edit/");
    }

    public List<String> labels() {
        return WebUI.getAllElementsText(FORM_LABELS);
    }

    /** Nhãn có biểu tượng ngôn ngữ (trường dịch được). */
    public List<String> translatableLabels() {
        return WebUI.getAllElementsText(TRANSLATABLE_LABELS);
    }

    /** Chú thích của biểu tượng ngôn ngữ cạnh nhãn Name (hiện khi rê chuột). */
    public String translatableTooltip() {
        return WebUI.getTooltipOnHover(WebUI.getWebElement(TRANSLATABLE_ICON), "biểu tượng ngôn ngữ");
    }

    // ---------- Đọc giá trị ----------

    public String orderLevelValue() {
        return WebUI.getElementValue(ORDER_LEVEL_INPUT);
    }

    public String orderLevelPlaceholder() {
        return WebUI.getElementAttribute(ORDER_LEVEL_INPUT, "placeholder");
    }

    public boolean isOrderLevelHintShown() {
        return WebUI.isElementDisplayedNow(ORDER_LEVEL_HINT);
    }

    public String metaTitleValue() {
        return WebUI.getElementValue(META_TITLE_INPUT);
    }

    public String metaDescriptionValue() {
        return WebUI.getElementValue(META_DESCRIPTION_INPUT);
    }

    public String slugValue() {
        return WebUI.getElementValue(SLUG_INPUT);
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
        return WebUI.getElementTextNow(browseButton(field));
    }

    public String fileAmountText(ImageField field) {
        return WebUI.getElementTextNow(fileAmount(field));
    }

    /** Số ảnh xem trước của ô; ô đã có id ảnh thì chờ ảnh dựng xong rồi mới đếm. */
    public int previewCount(ImageField field) {
        return previewCountWhenReady(WebUI.getElementValue(imageInput(field)), previewItems(field));
    }

    private By browseButton(ImageField field) {
        return By.xpath(field.uploader() + "//div[contains(@class,'input-group-text')]");
    }

    private By fileAmount(ImageField field) {
        return By.xpath(field.uploader() + "//div[contains(@class,'file-amount')]");
    }

    private By imageInput(ImageField field) {
        return By.xpath(field.uploader() + "/input[@name='" + field.inputName + "']");
    }

    private By previewItems(ImageField field) {
        return By.xpath(field.uploader() + "/following-sibling::div[contains(@class,'file-preview')][1]"
                + "//div[contains(@class,'file-preview-item')]");
    }

    public boolean isSaveDisplayed() {
        return WebUI.isElementDisplayedNow(SAVE_BUTTON);
    }

    public String orderLevelValidationMessage() {
        return WebUI.getValidationMessage(ORDER_LEVEL_INPUT);
    }

    // ---------- Nhập liệu ----------

    @Step("Nhập Name: {name}")
    public void enterName(String name) {
        WebUI.replaceText(NAME_INPUT, name);
    }

    @Step("Xoá hết ô Name")
    public void clearName() {
        WebUI.replaceText(NAME_INPUT, "");
    }

    @Step("Nhập Ordering Number: {value}")
    public void enterOrderLevel(String value) {
        WebUI.replaceText(ORDER_LEVEL_INPUT, value);
    }

    @Step("Nhập Meta Title: {metaTitle}")
    public void enterMetaTitle(String metaTitle) {
        WebUI.replaceText(META_TITLE_INPUT, metaTitle);
    }

    @Step("Nhập Meta description")
    public void enterMetaDescription(String metaDescription) {
        WebUI.replaceText(META_DESCRIPTION_INPUT, metaDescription);
    }

    @Step("Đặt Slug: {slug}")
    public void enterSlug(String slug) {
        WebUI.replaceText(SLUG_INPUT, slug);
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
        WebUI.clickElement(browseButton(field));
        return new MediaLibraryModal().waitUntilOpen();
    }

    @Step("Bấm nút xoá (×) trên ảnh xem trước của ô {field}")
    public void removePreview(ImageField field) {
        WebElement item = WebUI.getWebElement(previewItems(field));
        WebUI.clickElement(WebUI.getWebElement(item, REMOVE_ATTACHMENT), "× của ảnh " + field);
        WebUI.waitUntil("ảnh xem trước biến mất", () -> !WebUI.checkElementExist(previewItems(field)));
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save")
    public void clickSave() {
        markPage();
        WebUI.clickElementNow(SAVE_BUTTON);
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitForNewPage(this::clickSave);
    }

    @Step("Bấm đúp nút Save")
    public void doubleClickSave() {
        andWaitForNewPage(() -> WebUI.doubleClickElement(SAVE_BUTTON));
    }

    @Step("Nhấn Tab {count} lần từ ô Name")
    public List<String> tabSequenceFromName(int count) {
        return WebUI.tabSequenceFrom(NAME_INPUT, count);
    }

    /** Ô nhập chữ nào trông giống hệt nhau khi đang focus và khi không focus. */
    @Step("Đối chiếu viền của các ô nhập chữ khi đang focus")
    public List<String> fieldsWithoutVisibleFocus() {
        return WebUI.fieldsWithoutVisibleFocus(List.of(NAME_INPUT, ORDER_LEVEL_INPUT, META_TITLE_INPUT, META_DESCRIPTION_INPUT),
                List.of("Name", "Ordering Number", "Meta Title", "Meta description"));
    }

    /** Đặt Slug rồi lưu, chờ trang tải lại. */
    public void saveWithSlug(String slug) {
        enterSlug(slug);
        saveAndWait();
    }

    /** Tạo danh mục hợp lệ: lưu xong hệ thống chuyển về All categories. Dữ liệu sai thì dùng saveWithName. */
    public CategoryListPage createCategory(String name) {
        saveWithName(name);
        return arrivedAt(new CategoryListPage());
    }
}
