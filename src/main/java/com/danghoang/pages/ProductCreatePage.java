package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Form Add New Product (/admin/products/create) và Add New Digital Product (/admin/digitalproducts/create).
 * Các module khác chỉ đọc dropdown Brand / Category / Colors. Module Product nhập form và bấm Save &amp; Unpublish /
 * Save &amp; Publish — luôn với Name chứa mã truy vết để dọn được. Locator lấy từ DOM thật 04-11-2026.
 */
public class ProductCreatePage extends AdminFormPage {

    /** JS: chữ lựa chọn đang chọn của thẻ có name = arguments[0] ("" nếu chưa chọn). */
    private static final String JS_SELECTED_OPTION_TEXT = "const s=document.querySelector('[name=\"'+arguments[0]+'\"]');"
            + "return s.selectedOptions.length ? s.selectedOptions[0].textContent.trim() : '';";
    /** JS: value của nút radio đang chọn trong nhóm name = arguments[0] ("" nếu chưa chọn). */
    private static final String JS_CHECKED_RADIO_VALUE = "const r=document.querySelector('input[name=\"'+arguments[0]+'\"]:checked'); return r ? r.value : '';";
    /** JS: label đầu tiên trong form-group chứa ô name = arguments[0], đã gộp khoảng trắng. */
    private static final String JS_LABEL_OF_FIELD = "const g=document.querySelector('[name=\"'+arguments[0]+'\"]').closest('.form-group');"
            + "const l=g && g.querySelector('label'); return l ? l.textContent.replace(/\\s+/g,' ').trim() : '';";
    /** JS: form-group chứa ô name = arguments[0] có đang hiện không. */
    private static final String JS_FIELD_GROUP_VISIBLE = "const g=document.querySelector('[name=\"'+arguments[0]+'\"]').closest('.form-group');"
            + "return !!(g && g.offsetParent);";
    private static final By CATEGORY_SELECT = By.id("category_id");
    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Add New Product']");
    private static final By NAME_INPUT = FormField.NAME.locator();
    private static final By UNIT_INPUT = FormField.UNIT.locator();
    private static final By WEIGHT_INPUT = FormField.WEIGHT.locator();
    private static final By MIN_QTY_INPUT = FormField.MIN_QTY.locator();
    private static final By BRAND_SELECT = FormField.BRAND.locator();
    private static final By THUMBNAIL_BROWSE = By.xpath("//input[@name='thumbnail_img']/preceding-sibling::div[contains(@class,'input-group-prepend')]/div");
    private static final By DESCRIPTION_EDITOR = By.xpath("//textarea[@name='description']/following-sibling::div[contains(@class,'note-editor')]//div[contains(@class,'note-editable')]");

    private final BootstrapSelect brand = new BootstrapSelect("brand_id");
    private final BootstrapSelect category = new BootstrapSelect("category_id");
    private final ProductVariationSection variation = new ProductVariationSection();
    private final ProductPricingSection pricing = new ProductPricingSection();
    private final ProductShippingSection shipping = new ProductShippingSection();

    public ProductCreatePage() {
        super(PAGE_TITLE, NAME_INPUT);
    }

    /** Khối Product Variation: màu, thuộc tính, bảng tổ hợp. */
    public ProductVariationSection variation() {
        return variation;
    }

    /** Khối giá: giảm giá, thuế, Flash Deal. */
    public ProductPricingSection pricing() {
        return pricing;
    }

    /** Khối Shipping Configuration. */
    public ProductShippingSection shipping() {
        return shipping;
    }

    @Step("Mở trang Add New Product")
    public ProductCreatePage open() {
        navigate("/admin/products/create");
        WebUI.waitUntil("form sản phẩm có Category và Brand", () -> WebUI.checkElementExist(CATEGORY_SELECT) && WebUI.checkElementExist(BRAND_SELECT));
        return this;
    }

    @Step("Mở trang Add New Digital Product")
    public ProductCreatePage openDigital() {
        navigate("/admin/digitalproducts/create");
        WebUI.waitUntil("form sản phẩm số có Category", () -> WebUI.checkElementExist(CATEGORY_SELECT));
        return this;
    }

    /** Tên các brand trong dropdown Brand, không tính dòng "Select Brand". */
    public List<String> brandOptions() {
        return brand.optionTexts().stream().filter(name -> !"Select Brand".equals(name)).toList();
    }

    /** Lựa chọn trong dropdown Brand có chứa từ khoá — như khi gõ vào ô tìm của dropdown. */
    @Step("Lọc dropdown Brand theo {keyword}")
    public List<String> brandOptionsContaining(String keyword) {
        return brandOptions().stream().filter(name -> name.contains(keyword)).toList();
    }

    /** Lựa chọn trong dropdown Category có chứa từ khoá (đã bỏ tiền tố cấp "-- "). */
    @Step("Lọc dropdown Category theo {keyword}")
    public List<String> categoryOptionsContaining(String keyword) {
        return category.optionTexts().stream().filter(name -> name.contains(keyword)).toList();
    }

    // ======================= Form Add New Product — nhập liệu, lưu =======================

    public String fieldValue(FormField field) {
        return WebUI.getElementValue(field.locator());
    }

    public boolean isChecked(FormField field) {
        return WebUI.isElementSelected(field.locator());
    }

    /** Chữ của lựa chọn đang chọn ở thẻ select gốc. */
    public String selectedOptionText(FormField field) {
        return (String) WebUI.executeJS(JS_SELECTED_OPTION_TEXT, field.toString());
    }

    public String checkedRadio(FormField field) {
        return (String) WebUI.executeJS(JS_CHECKED_RADIO_VALUE, field.toString());
    }

    /** Nhãn của ô (label đầu tiên trong form-group chứa ô). */
    public String labelOf(FormField field) {
        return (String) WebUI.executeJS(JS_LABEL_OF_FIELD, field.toString());
    }

    /** Nhập vào ô (xoá giá trị cũ trước). */
    @Step("Nhập {field}: [{value}]")
    public void enterField(FormField field, String value) {
        WebUI.scrollAndSetText(field.locator(), value);
    }

    public void enterName(String name) {
        enterField(FormField.NAME, name);
    }

    public void enterUnit(String unit) {
        enterField(FormField.UNIT, unit);
    }

    public void enterUnitPrice(String price) {
        enterField(FormField.UNIT_PRICE, price);
    }

    public void enterDiscount(String discount) {
        enterField(FormField.DISCOUNT, discount);
    }

    public void enterQuantity(String quantity) {
        enterField(FormField.QUANTITY, quantity);
    }

    /** Ô Description là trình soạn thảo summernote: gõ vào vùng soạn thảo, textarea gốc tự cập nhật. */
    @Step("Nhập Description: [{text}]")
    public void enterDescription(String text) {
        WebUI.clickAndType(DESCRIPTION_EDITOR, text);
    }

    @Step("Chọn Category {name}")
    public void chooseCategory(String name) {
        category.choose(name);
    }

    @Step("Chọn Brand {name}")
    public void chooseBrand(String name) {
        brand.choose(name);
    }

    /** PC-2: Name, Unit pc, Tags auto, Unit price 10, Discount 0, Quantity 5 — Category để mặc định. */
    @Step("Nhập dữ liệu tối thiểu cho sản phẩm {name}")
    public void fillRequired(String name) {
        enterName(name);
        enterUnit("pc");
        addTag("auto");
        enterUnitPrice("10");
        enterDiscount("0");
        enterQuantity("5");
    }

    /**
     * Bấm nút lưu — không chờ trang mới (dùng khi trình duyệt có thể chặn form).
     * Ô Name và Unit price gọi AJAX update_sku() ở mỗi lần nhả phím: gõ chuỗi dài sinh hàng trăm request nền,
     * gửi form khi chúng còn chạy thì phiên bị ghi đè, mất khối lỗi / thông báo. Chờ hết request trước khi bấm.
     */
    @Step("Bấm {button}")
    public void clickSave(SaveButton button) {
        markPage();
        waitForAjaxIdle();
        WebUI.scrollAndClickElement(button.locator);
    }

    @Step("Bấm {button} và chờ trang tải lại")
    public void saveAndWait(SaveButton button) {
        andWaitForNewPage(() -> clickSave(button));
    }

    @Step("Bấm đúp {button}")
    public void doubleClickSave(SaveButton button) {
        waitForAjaxIdle();
        WebElement target = WebUI.getWebElement(button.locator);
        WebUI.scrollToElementAtCenter(target);
        andWaitForNewPage(() -> WebUI.doubleClickElement(target, "nút " + button));
    }

    public String validationMessageOf(FormField field) {
        return WebUI.getValidationMessage(field.locator());
    }

    /** name của ô đang có focus ("" nếu không phải ô nhập). */
    public String focusedFieldName() {
        return WebUI.getFocusedProperty("name");
    }

    @Step("Nhấn Tab {count} lần từ ô Product Name")
    public List<String> tabSequenceFromName(int count) {
        return WebUI.tabSequenceFrom(NAME_INPUT, count);
    }

    @Step("Đối chiếu viền của các ô đầu form khi đang focus")
    public List<String> fieldsWithoutVisibleFocus() {
        return WebUI.fieldsWithoutVisibleFocus(List.of(NAME_INPUT, UNIT_INPUT, WEIGHT_INPUT, MIN_QTY_INPUT),
                List.of("Product Name", "Unit", "Weight", "Minimum Purchase Qty"));
    }

    public boolean hasField(FormField field) {
        return WebUI.checkElementExist(field.locator());
    }

    @Step("Bấm Browse ở Thumbnail Image")
    public MediaLibraryModal openThumbnailLibrary() {
        WebUI.scrollAndClickElement(THUMBNAIL_BROWSE);
        return new MediaLibraryModal().waitUntilOpen();
    }

    /** Số ảnh đang hiện trong khung xem trước của Thumbnail Image. */
    public int thumbnailPreviewCount() {
        return WebUI.getElementCount(AdminLocators.THUMBNAIL_PREVIEW);
    }

    /** Tên tệp của ảnh Thumbnail đang xem trước (thuộc tính title của ô xem trước). */
    public String thumbnailPreviewTitle() {
        return WebUI.getElementAttribute(AdminLocators.THUMBNAIL_PREVIEW, "title");
    }

    /** Ô của form-group chứa ô name có đang hiện không (Quantity, SKU chung bị ẩn khi có tổ hợp). */
    public boolean isFieldGroupVisible(FormField field) {
        return Boolean.TRUE.equals(WebUI.executeJS(JS_FIELD_GROUP_VISIBLE, field.toString()));
    }

    /** Hai nút lưu cuối form. */
    public enum SaveButton {
        UNPUBLISH(By.cssSelector("button[name='button'][value='unpublish']")),
        PUBLISH(By.cssSelector("button[name='button'][value='publish']"));

        private final By locator;

        SaveButton(By locator) {
            this.locator = locator;
        }
    }

    public List<String> saveButtonTexts() {
        return List.of(SaveButton.UNPUBLISH, SaveButton.PUBLISH).stream()
                .map(b -> WebUI.getElementTextNow(b.locator)).toList();
    }

    /** Tạo sản phẩm hợp lệ: lưu xong hệ thống chuyển về danh sách. Dữ liệu sai thì dùng fillRequired + saveAndWait. */
    public ProductListPage createProduct(String name, SaveButton button) {
        fillRequired(name);
        saveAndWait(button);
        return arrivedAt(new ProductListPage());
    }
}
