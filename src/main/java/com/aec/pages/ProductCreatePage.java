package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/**
 * Form Add New Product (/admin/products/create) và Add New Digital Product (/admin/digitalproducts/create).
 * Các module khác chỉ đọc dropdown Brand / Category / Colors. Module Product nhập form và bấm Save &amp; Unpublish /
 * Save &amp; Publish — luôn với Name chứa mã truy vết để dọn được. Locator lấy từ DOM thật 04-11-2026.
 */
public class ProductCreatePage extends BasePage {

    private static final By CATEGORY_SELECT = By.id("category_id");
    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Add New Product']");
    private static final By TAG_INPUT = By.cssSelector("tags.tagify .tagify__input");
    private static final By ERROR_MESSAGES = By.cssSelector("div.alert-danger li");
    private static final By THUMBNAIL_BROWSE = By.xpath("//input[@name='thumbnail_img']/preceding-sibling::div[contains(@class,'input-group-prepend')]/div");
    private static final By THUMBNAIL_PREVIEW = By.xpath("//input[@name='thumbnail_img']/ancestor::div[contains(@class,'input-group')]/following-sibling::div[contains(@class,'file-preview')]/div");
    private static final By CHOICE_VALUE_SELECT = By.cssSelector("#customer_choice_options select[name^='choice_options_']");
    private static final By VARIANT_ROWS = By.cssSelector("#sku_combination tr.variant");
    private static final By DESCRIPTION_EDITOR = By.xpath("//textarea[@name='description']/following-sibling::div[contains(@class,'note-editor')]//div[contains(@class,'note-editable')]");
    private static final By COLOR_SELECT = By.name("colors[]");
    private static final By COLORS_SWITCH_INPUT = By.name("colors_active");
    private static final By COLORS_SWITCH = By.xpath("//input[@name='colors_active']/parent::label[contains(@class,'aiz-switch')]");
    private static final By COLOR_TOGGLE = By.xpath("//div[contains(@class,'bootstrap-select')][select[@name='colors[]']]/button");

    private final BootstrapSelect brand = new BootstrapSelect(driver, "brand_id");
    private final BootstrapSelect category = new BootstrapSelect(driver, "category_id");
    private final BootstrapSelect variationAttributes = new BootstrapSelect(driver, "choice_attributes[]");
    private final BootstrapSelect colors = new BootstrapSelect(driver, "colors[]");

    public ProductCreatePage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang Add New Product")
    public ProductCreatePage open() {
        navigate("/admin/products/create");
        wait.until(d -> !d.findElements(CATEGORY_SELECT).isEmpty() && !d.findElements(By.name("brand_id")).isEmpty());
        return this;
    }

    @Step("Mở trang Add New Digital Product")
    public ProductCreatePage openDigital() {
        navigate("/admin/digitalproducts/create");
        wait.until(d -> !d.findElements(CATEGORY_SELECT).isEmpty());
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

    /** Gõ từ khoá vào ô chọn thuộc tính của khối Product Variation, trả về các mục còn hiện. */
    @Step("Tìm {keyword} trong ô chọn thuộc tính của Product Variation")
    public List<String> searchVariationAttributes(String keyword) {
        List<String> found = variationAttributes.search(keyword);
        variationAttributes.close();
        return found;
    }

    // ---------- Dòng Colors: công tắc colors_active + ô chọn nhiều colors[] ----------

    /** Ô chọn màu đang bị khoá (select gốc disabled) — mặc định khi công tắc Colors tắt. */
    public boolean isColorSelectDisabled() {
        return !driver.findElement(COLOR_SELECT).isEnabled();
    }

    @Step("Bật công tắc Colors")
    public void enableColors() {
        if (!driver.findElement(COLORS_SWITCH_INPUT).isSelected()) {
            // Công tắc ở giữa form — cuộn vào giữa màn hình để thanh trên cố định không che chỗ bấm
            js("arguments[0].scrollIntoView({block:'center'});", driver.findElement(COLORS_SWITCH));
            click(COLORS_SWITCH);
        }
        wait.until(d -> d.findElement(COLOR_SELECT).isEnabled());
    }

    /** Thử mở ô chọn màu — trả về danh sách có mở ra không. */
    public boolean tryOpenColorSelect() {
        js("arguments[0].click();", driver.findElement(COLOR_TOGGLE));
        return colors.isOpen();
    }

    /** Gõ từ khoá vào ô tìm của ô chọn màu, trả về chữ các mục còn hiện. */
    @Step("Tìm {keyword} trong ô chọn màu")
    public List<String> searchColors(String keyword) {
        return colors.search(keyword);
    }

    /** Thuộc tính style của ô mẫu màu ở mục đang hiện có chữ đúng bằng name ("" nếu không có mục). */
    public String colorSwatchStyleOf(String name) {
        return (String) js("const li=[...document.querySelectorAll(\"div.bootstrap-select:has(select[name='colors[]']) ul.inner li:not(.no-results)\")]"
                + ".find(l => l.textContent.trim() === arguments[0]); const s=li && li.querySelector('span.size-15px');"
                + "return s ? s.getAttribute('style') : '';", name);
    }

    /** Mục đang hiện chứa từ khoá có phần tử định dạng (b, strong, i…) bên trong phần chữ — tức tên bị hiểu thành HTML. */
    public boolean colorItemRendersHtml(String keyword) {
        return Boolean.TRUE.equals(js("const li=[...document.querySelectorAll(\"div.bootstrap-select:has(select[name='colors[]']) ul.inner li:not(.no-results)\")]"
                + ".find(l => l.textContent.includes(arguments[0])); if(!li) return false;"
                + "return li.querySelector('span.text b, span.text strong, span.text i, span.text u') !== null;", keyword));
    }

    /** Lựa chọn trong dropdown Category có chứa từ khoá (đã bỏ tiền tố cấp "-- "). */
    @Step("Lọc dropdown Category theo {keyword}")
    public List<String> categoryOptionsContaining(String keyword) {
        return category.optionTexts().stream().filter(name -> name.contains(keyword)).toList();
    }

    // ======================= Form Add New Product — nhập liệu, lưu =======================

    public boolean isTitleDisplayed() {
        return isPresentNow(PAGE_TITLE);
    }

    /** Tiêu đề các khối (thẻ) của form, theo thứ tự — bỏ tiêu đề mẫu trong thanh công cụ của ô Description. */
    @SuppressWarnings("unchecked")
    public List<String> sectionTitles() {
        return (List<String>) js("return [...document.querySelectorAll('.card > .card-header > h5')].map(h => h.textContent.trim());");
    }

    public String fieldValue(String name) {
        return driver.findElement(By.name(name)).getAttribute("value");
    }

    public boolean isChecked(String name) {
        return driver.findElement(By.name(name)).isSelected();
    }

    /** Chữ của lựa chọn đang chọn ở thẻ select gốc. */
    public String selectedOptionText(String name) {
        return (String) js("const s=document.querySelector('[name=\"'+arguments[0]+'\"]');"
                + "return s.selectedOptions.length ? s.selectedOptions[0].textContent.trim() : '';", name);
    }

    public String checkedRadio(String name) {
        return (String) js("const r=document.querySelector('input[name=\"'+arguments[0]+'\"]:checked'); return r ? r.value : '';", name);
    }

    /** Nhãn của ô (label đầu tiên trong form-group chứa ô). */
    public String labelOf(String name) {
        return (String) js("const g=document.querySelector('[name=\"'+arguments[0]+'\"]').closest('.form-group');"
                + "const l=g && g.querySelector('label'); return l ? l.textContent.replace(/\\s+/g,' ').trim() : '';", name);
    }

    /** Nhập vào ô theo name (xoá giá trị cũ trước). */
    @Step("Nhập {name}: [{value}]")
    public void enterField(String name, String value) {
        WebElement field = driver.findElement(By.name(name));
        js("arguments[0].scrollIntoView({block:'center'});", field);
        field.clear();
        if (!value.isEmpty()) {
            type(By.name(name), value);
        }
    }

    public void enterName(String name) {
        enterField("name", name);
    }

    public void enterUnit(String unit) {
        enterField("unit", unit);
    }

    public void enterUnitPrice(String price) {
        enterField("unit_price", price);
    }

    public void enterDiscount(String discount) {
        enterField("discount", discount);
    }

    public void enterQuantity(String quantity) {
        enterField("current_stock", quantity);
    }

    /** Ô Description là trình soạn thảo summernote — gõ vào vùng soạn thảo (.note-editable), textarea gốc tự cập nhật. */
    @Step("Nhập Description: [{text}]")
    public void enterDescription(String text) {
        WebElement editor = driver.findElement(DESCRIPTION_EDITOR);
        js("arguments[0].scrollIntoView({block:'center'});", editor);
        editor.click();
        editor.sendKeys(text);
    }

    /** Ô Discount Date Range dùng daterangepicker — đặt chuỗi vào ô như khi chọn xong và bấm Apply. */
    @Step("Đặt Discount Date Range: [{range}]")
    public void enterDateRange(String range) {
        js("const i=document.querySelector('input[name=\"date_range\"]'); i.value=arguments[0];"
                + "i.dispatchEvent(new Event('change',{bubbles:true}));", range);
    }

    @Step("Thêm tag {tag}")
    public void addTag(String tag) {
        WebElement input = driver.findElement(TAG_INPUT);
        js("arguments[0].scrollIntoView({block:'center'});", input);
        input.click();
        input.sendKeys(tag, org.openqa.selenium.Keys.ENTER);
        wait.until(d -> !d.findElements(By.cssSelector("tags.tagify tag")).isEmpty());
    }

    @Step("Chọn Category {name}")
    public void chooseCategory(String name) {
        category.choose(name);
    }

    @Step("Chọn Brand {name}")
    public void chooseBrand(String name) {
        brand.choose(name);
    }

    @Step("Chọn kiểu Discount {type}")
    public void chooseDiscountType(String type) {
        new BootstrapSelect(driver, "discount_type").choose(type);
    }

    @Step("Chọn kiểu Tax {type}")
    public void chooseTaxType(String type) {
        new BootstrapSelect(driver, "tax_type[]").choose(type);
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
        waitForAjaxIdle();
        WebElement target = driver.findElement(button.locator);
        js("arguments[0].scrollIntoView({block:'center'});", target);
        target.click();
    }

    @Step("Bấm {button} và chờ trang tải lại")
    public void saveAndWait(SaveButton button) {
        andWaitForNewPage(() -> clickSave(button));
    }

    @Step("Bấm đúp {button}")
    public void doubleClickSave(SaveButton button) {
        waitForAjaxIdle();
        WebElement target = driver.findElement(button.locator);
        js("arguments[0].scrollIntoView({block:'center'});", target);
        andWaitForNewPage(() -> new Actions(driver).doubleClick(target).perform());
    }

    /** Các dòng lỗi trong khối đỏ đầu form (rỗng nếu không có). */
    public List<String> errorMessages() {
        return driver.findElements(ERROR_MESSAGES).stream().map(e -> e.getText().trim()).toList();
    }

    public String validationMessageOf(String name) {
        return validationMessage(By.name(name));
    }

    /** name của ô đang có focus ("" nếu không phải ô nhập). */
    public String focusedFieldName() {
        return (String) js("return document.activeElement ? (document.activeElement.name || '') : '';");
    }

    @Step("Nhấn Tab {count} lần từ ô Product Name")
    public List<String> tabSequenceFromName(int count) {
        return tabSequenceFrom(By.name("name"), count);
    }

    @Step("Đối chiếu viền của các ô đầu form khi đang focus")
    public List<String> fieldsWithoutVisibleFocus() {
        return fieldsWithoutVisibleFocus(List.of(By.name("name"), By.name("unit"), By.name("weight"), By.name("min_qty")),
                List.of("Product Name", "Unit", "Weight", "Minimum Purchase Qty"));
    }

    // ---------- Khối phụ ----------

    public String shippingConfigurationText() {
        return (String) js("const c=[...document.querySelectorAll('.card')].find(c => ((c.querySelector('.card-header h5')||{}).textContent||'').trim()==='Shipping Configuration');"
                + "return c ? c.querySelector('.card-body').textContent.replace(/\\s+/g,' ').trim() : '';");
    }

    public boolean shippingConfigurationHasLink() {
        return isPresentNow(By.xpath("//div[contains(@class,'card')][.//h5[normalize-space()='Shipping Configuration']]"
                + "//a[contains(@href,'/admin/shipping_configuration')]"));
    }

    public boolean shippingConfigurationHasInputs() {
        return !driver.findElements(By.xpath("//div[contains(@class,'card')][.//h5[normalize-space()='Shipping Configuration']]"
                + "//div[contains(@class,'card-body')]//input")).isEmpty();
    }

    /** Số đợt Flash Deal có thể chọn (không tính dòng "Choose Flash Title"). */
    public int flashDealCount() {
        return ((Number) js("return [...document.querySelector('select[name=\"flash_deal_id\"]').options].filter(o => o.value).length;")).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<String> flashDiscountTypes() {
        return (List<String>) js("return [...document.querySelector('select[name=\"flash_discount_type\"]').options].map(o => o.textContent.trim());");
    }

    public boolean hasField(String name) {
        return !driver.findElements(By.name(name)).isEmpty();
    }

    @Step("Bấm Browse ở Thumbnail Image")
    public MediaLibraryModal openThumbnailLibrary() {
        WebElement browse = driver.findElement(THUMBNAIL_BROWSE);
        js("arguments[0].scrollIntoView({block:'center'});", browse);
        browse.click();
        return new MediaLibraryModal(driver).waitUntilOpen();
    }

    /** Số ảnh đang hiện trong khung xem trước của Thumbnail Image. */
    public int thumbnailPreviewCount() {
        return driver.findElements(THUMBNAIL_PREVIEW).size();
    }

    @Step("Chọn Video Provider {provider}")
    public void chooseVideoProvider(String provider) {
        new BootstrapSelect(driver, "video_provider").choose(provider);
    }

    // ---------- Biến thể ----------

    /** Danh sách màu (sau khi bật công tắc) có ô mẫu màu ở mọi mục. */
    public boolean colorListShowsSwatches() {
        colors.open();
        return Boolean.TRUE.equals(js("const li=[...document.querySelectorAll(\"div.bootstrap-select:has(select[name='colors[]']) ul.inner li\")];"
                + "return li.length > 0 && li.every(l => l.querySelector('span.size-15px'));"));
    }

    @Step("Chọn {count} màu đầu danh sách")
    public void chooseFirstColors(int count) {
        for (int i = 0; i < count; i++) {
            colors.toggleItem(i);
        }
        colors.close();
    }

    @Step("Chọn thuộc tính {name} ở Product Variation")
    public void chooseAttribute(String name) {
        variationAttributes.choose(name);
        variationAttributes.close();
        wait.until(d -> !d.findElements(CHOICE_VALUE_SELECT).isEmpty());
    }

    /** Tên các dòng thuộc tính đã sinh (ô Choice Title chỉ đọc). */
    @SuppressWarnings("unchecked")
    public List<String> attributeChoiceTitles() {
        return (List<String>) js("return [...document.querySelectorAll('input[name=\"choice[]\"]')].map(i => i.value);");
    }

    public boolean choiceValueSelectIsMultiple() {
        return driver.findElement(CHOICE_VALUE_SELECT).getAttribute("multiple") != null;
    }

    /**
     * Chọn count giá trị đầu danh sách mà **tên chỉ xuất hiện một lần** trong thuộc tính vừa chọn (thuộc tính dùng chung
     * có giá trị trùng tên như "Test", "Test" — chọn một cái sẽ kéo theo cả hai). Đọc tên từ thẻ select gốc bằng JS,
     * chọn từng tên qua ô tìm của danh sách. Trả về tên đã chọn.
     */
    @Step("Chọn {count} giá trị khác tên nhau của thuộc tính")
    @SuppressWarnings("unchecked")
    public List<String> chooseFirstDistinctValues(int count) {
        WebElement select = driver.findElement(CHOICE_VALUE_SELECT);
        List<String> unique = (List<String>) js("const t=[...arguments[0].options].map(o => o.textContent.trim());"
                + "return t.filter(x => x && t.indexOf(x) === t.lastIndexOf(x));", select);
        if (unique.size() < count) {
            throw new IllegalStateException("Thuộc tính chỉ có " + unique.size() + " giá trị không trùng tên: " + unique);
        }
        BootstrapSelect values = new BootstrapSelect(driver, select.getAttribute("name"));
        List<String> chosen = unique.subList(0, count);
        chosen.forEach(values::choose);
        values.close();
        return chosen;
    }

    /** Chờ bảng tổ hợp có đúng count dòng. */
    public void waitForVariants(int count) {
        wait.until(d -> d.findElements(VARIANT_ROWS).size() == count);
    }

    @SuppressWarnings("unchecked")
    public List<String> variantHeaders() {
        return (List<String>) js("return [...document.querySelectorAll('#sku_combination thead td, #sku_combination thead th')].map(t => t.textContent.trim());");
    }

    @SuppressWarnings("unchecked")
    public List<String> variantNames() {
        return (List<String>) js("return [...document.querySelectorAll('#sku_combination tr.variant label')].map(l => l.textContent.trim());");
    }

    @SuppressWarnings("unchecked")
    public List<String> variantQuantities() {
        return (List<String>) js("return [...document.querySelectorAll('#sku_combination tr.variant input[name^=\"qty_\"]')].map(i => i.value);");
    }

    /** Nhập giá từng dòng tổ hợp theo thứ tự. Ô có thể bị footable giấu ở màn hẹp — đặt giá trị như khi gõ. */
    @Step("Nhập giá các dòng tổ hợp {prices}")
    public void enterVariantPrices(List<String> prices) {
        List<WebElement> inputs = driver.findElements(By.cssSelector("#sku_combination tr.variant input[name^='price_']"));
        for (int i = 0; i < prices.size(); i++) {
            WebElement input = inputs.get(i);
            if (input.isDisplayed()) {
                input.clear();
                input.sendKeys(prices.get(i));
            } else {
                js("arguments[0].value=arguments[1];arguments[0].dispatchEvent(new Event('input',{bubbles:true}));", input, prices.get(i));
            }
        }
    }

    /** Ô của form-group chứa ô name có đang hiện không (Quantity, SKU chung bị ẩn khi có tổ hợp). */
    public boolean isFieldGroupVisible(String name) {
        return Boolean.TRUE.equals(js("const g=document.querySelector('[name=\"'+arguments[0]+'\"]').closest('.form-group');"
                + "return !!(g && g.offsetParent);", name));
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
                .map(b -> driver.findElement(b.locator).getText().trim()).toList();
    }
}
