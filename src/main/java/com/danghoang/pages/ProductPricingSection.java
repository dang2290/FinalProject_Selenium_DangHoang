package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;

import java.util.List;

/** Khối giá của form Add New Product: kiểu giảm giá, khoảng ngày, thuế, Flash Deal. */
public class ProductPricingSection extends BasePage {

    /** JS: đặt chuỗi vào ô Discount Date Range rồi phát sự kiện change như khi bấm Apply của daterangepicker. */
    private static final String JS_SET_DATE_RANGE = "const i=document.querySelector('input[name=\"date_range\"]'); i.value=arguments[0];"
            + "i.dispatchEvent(new Event('change',{bubbles:true}));";
    /** JS: số lựa chọn có value ở ô Flash Deal (bỏ dòng "Choose Flash Title"). */
    private static final String JS_FLASH_DEAL_COUNT = "return [...document.querySelector('select[name=\"flash_deal_id\"]').options].filter(o => o.value).length;";

    @Step("Chọn kiểu Discount {type}")
    public void chooseDiscountType(String type) {
        new BootstrapSelect("discount_type").choose(type);
    }

    @Step("Chọn kiểu Tax {type}")
    public void chooseTaxType(String type) {
        new BootstrapSelect("tax_type[]").choose(type);
    }

    /** Ô Discount Date Range dùng daterangepicker — đặt chuỗi vào ô như khi chọn xong và bấm Apply. */
    @Step("Đặt Discount Date Range: [{range}]")
    public void enterDateRange(String range) {
        WebUI.executeJS(JS_SET_DATE_RANGE, range);
    }

    /** Số đợt Flash Deal có thể chọn (không tính dòng "Choose Flash Title"). */
    public int flashDealCount() {
        return ((Number) WebUI.executeJS(JS_FLASH_DEAL_COUNT)).intValue();
    }

    public List<String> flashDiscountTypes() {
        return WebUI.getAllTextsByJS("select[name=\"flash_discount_type\"] option");
    }
}
