package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Khối Product Variation của form Add New Product: màu, thuộc tính, bảng tổ hợp biến thể. */
public class ProductVariationSection extends BasePage {

    /** JS: các mục đang hiện của danh sách màu (bỏ dòng "No results"), gán vào biến li. */
    private static final String JS_SHOWN_COLOR_ITEMS = "const li=[...document.querySelectorAll(\"div.bootstrap-select:has(select[name='colors[]']) ul.inner li:not(.no-results)\")]";
    /** JS: style của ô mẫu màu ở mục có chữ đúng bằng arguments[0]. */
    private static final String JS_SWATCH_STYLE_OF = JS_SHOWN_COLOR_ITEMS
            + ".find(l => l.textContent.trim() === arguments[0]); const s=li && li.querySelector('span.size-15px');"
            + "return s ? s.getAttribute('style') : '';";
    /** JS: mục chứa arguments[0] có thẻ định dạng (b, strong, i, u) trong phần chữ — tên bị hiểu thành HTML. */
    private static final String JS_ITEM_RENDERS_HTML = JS_SHOWN_COLOR_ITEMS
            + ".find(l => l.textContent.includes(arguments[0])); if(!li) return false;"
            + "return li.querySelector('span.text b, span.text strong, span.text i, span.text u') !== null;";
    /** JS: danh sách màu có mục và mục nào cũng có ô mẫu màu. */
    private static final String JS_ALL_ITEMS_HAVE_SWATCH = "const li=[...document.querySelectorAll(\"div.bootstrap-select:has(select[name='colors[]']) ul.inner li\")];"
            + "return li.length > 0 && li.every(l => l.querySelector('span.size-15px'));";
    /** JS: chữ các lựa chọn của thẻ select chỉ xuất hiện một lần (bỏ tên trùng và rỗng). */
    private static final String JS_UNIQUE_OPTION_TEXTS = "const t=[...arguments[0].options].map(o => o.textContent.trim());"
            + "return t.filter(x => x && t.indexOf(x) === t.lastIndexOf(x));";
    private static final By COLOR_SELECT = By.name("colors[]");
    private static final By COLORS_SWITCH_INPUT = By.name("colors_active");
    private static final By COLORS_SWITCH = By.xpath("//input[@name='colors_active']/parent::label[contains(@class,'aiz-switch')]");
    private static final By COLOR_TOGGLE = By.xpath("//div[contains(@class,'bootstrap-select')][select[@name='colors[]']]/button");
    private static final By CHOICE_VALUE_SELECT = By.cssSelector("#customer_choice_options select[name^='choice_options_']");
    private static final By VARIANT_ROWS = By.cssSelector("#sku_combination tr.variant");
    private static final By VARIANT_PRICE_INPUTS = By.cssSelector("#sku_combination tr.variant input[name^='price_']");

    private final BootstrapSelect colors = new BootstrapSelect("colors[]");
    private final BootstrapSelect variationAttributes = new BootstrapSelect(FormField.VARIATION_ATTRIBUTES.toString());

    /** Ô chọn màu đang bị khoá (select gốc disabled) — mặc định khi công tắc Colors tắt. */
    public boolean isColorSelectDisabled() {
        return !WebUI.isElementEnabled(COLOR_SELECT);
    }

    @Step("Bật công tắc Colors")
    public void enableColors() {
        if (!WebUI.isElementSelected(COLORS_SWITCH_INPUT)) {
            // Công tắc ở giữa form — cuộn vào giữa màn hình để thanh trên cố định không che chỗ bấm
            WebUI.scrollToElementAtCenter(COLORS_SWITCH);
            WebUI.clickElement(COLORS_SWITCH);
        }
        WebUI.waitUntil("ô chọn màu mở khoá", () -> WebUI.isElementEnabled(COLOR_SELECT));
    }

    /** Thử mở ô chọn màu — trả về danh sách có mở ra không. */
    public boolean tryOpenColorSelect() {
        WebUI.clickElementByJS(COLOR_TOGGLE);
        return colors.isOpen();
    }

    /** Gõ từ khoá vào ô tìm của ô chọn màu, trả về chữ các mục còn hiện. */
    @Step("Tìm {keyword} trong ô chọn màu")
    public List<String> searchColors(String keyword) {
        return colors.search(keyword);
    }

    /** Thuộc tính style của ô mẫu màu ở mục đang hiện có chữ đúng bằng name ("" nếu không có mục). */
    public String colorSwatchStyleOf(String name) {
        return (String) WebUI.executeJS(JS_SWATCH_STYLE_OF, name);
    }

    /** Mục đang hiện chứa từ khoá có phần tử định dạng (b, strong, i…) bên trong phần chữ — tức tên bị hiểu thành HTML. */
    public boolean colorItemRendersHtml(String keyword) {
        return Boolean.TRUE.equals(WebUI.executeJS(JS_ITEM_RENDERS_HTML, keyword));
    }

    /** Danh sách màu (sau khi bật công tắc) có ô mẫu màu ở mọi mục. */
    public boolean colorListShowsSwatches() {
        colors.open();
        return Boolean.TRUE.equals(WebUI.executeJS(JS_ALL_ITEMS_HAVE_SWATCH));
    }

    @Step("Chọn {count} màu đầu danh sách")
    public void chooseFirstColors(int count) {
        for (int i = 0; i < count; i++) {
            colors.toggleItem(i);
        }
        colors.close();
    }

    /** Gõ từ khoá vào ô chọn thuộc tính của khối Product Variation, trả về các mục còn hiện. */
    @Step("Tìm {keyword} trong ô chọn thuộc tính của Product Variation")
    public List<String> searchVariationAttributes(String keyword) {
        List<String> found = variationAttributes.search(keyword);
        variationAttributes.close();
        return found;
    }

    @Step("Chọn thuộc tính {name} ở Product Variation")
    public void chooseAttribute(String name) {
        variationAttributes.choose(name);
        variationAttributes.close();
        WebUI.waitUntil("ô giá trị thuộc tính xuất hiện", () -> WebUI.checkElementExist(CHOICE_VALUE_SELECT));
    }

    /** Tên các dòng thuộc tính đã sinh (ô Choice Title chỉ đọc). */
    public List<String> attributeChoiceTitles() {
        return WebUI.getAllValuesByJS("input[name=\"choice[]\"]");
    }

    public boolean choiceValueSelectIsMultiple() {
        return WebUI.getElementAttribute(CHOICE_VALUE_SELECT, "multiple") != null;
    }

    /**
     * Chọn count giá trị đầu danh sách mà **tên chỉ xuất hiện một lần** trong thuộc tính vừa chọn (thuộc tính dùng chung
     * có giá trị trùng tên như "Test", "Test" — chọn một cái sẽ kéo theo cả hai). Đọc tên từ thẻ select gốc bằng JS,
     * chọn từng tên qua ô tìm của danh sách. Trả về tên đã chọn.
     */
    @Step("Chọn {count} giá trị khác tên nhau của thuộc tính")
    @SuppressWarnings("unchecked")
    public List<String> chooseFirstDistinctValues(int count) {
        WebElement select = WebUI.getWebElement(CHOICE_VALUE_SELECT);
        List<String> unique = (List<String>) WebUI.executeJS(JS_UNIQUE_OPTION_TEXTS, select);
        if (unique.size() < count) {
            throw new IllegalStateException("Thuộc tính chỉ có " + unique.size() + " giá trị không trùng tên: " + unique);
        }
        BootstrapSelect values = new BootstrapSelect(select.getAttribute("name"));
        List<String> chosen = unique.subList(0, count);
        chosen.forEach(values::choose);
        values.close();
        return chosen;
    }

    /** Chờ bảng tổ hợp có đúng count dòng. */
    public void waitForVariants(int count) {
        WebUI.waitUntil("bảng tổ hợp có " + count + " dòng", () -> WebUI.getElementCount(VARIANT_ROWS) == count);
    }

    public List<String> variantHeaders() {
        return WebUI.getAllTextsByJS("#sku_combination thead td, #sku_combination thead th");
    }

    public List<String> variantNames() {
        return WebUI.getAllTextsByJS("#sku_combination tr.variant label");
    }

    public List<String> variantQuantities() {
        return WebUI.getAllValuesByJS("#sku_combination tr.variant input[name^=\"qty_\"]");
    }

    /** Nhập giá từng dòng tổ hợp theo thứ tự. Ô có thể bị footable giấu ở màn hẹp. */
    @Step("Nhập giá các dòng tổ hợp {prices}")
    public void enterVariantPrices(List<String> prices) {
        List<WebElement> inputs = WebUI.getWebElements(VARIANT_PRICE_INPUTS);
        for (int i = 0; i < prices.size(); i++) {
            WebUI.setTextEvenIfHidden(inputs.get(i), prices.get(i), "giá tổ hợp dòng " + i);
        }
    }
}
