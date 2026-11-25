package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Trang sản phẩm ngoài cửa hàng /product/{slug}. Khối thông tin (div.text-left chứa h1): tên, logo brand (link /brand/…,
 * alt = tên brand), Sold by, Price (gạch ngang khi có giảm giá) / Discount Price kèm "/đơn vị", ô số lượng,
 * "(n available)", Total Price; tab Description bên dưới. Trang **không** hiện Category, Tags. Locator lấy từ DOM thật 10-11-2026.
 */
public class StorefrontProductPage extends StorefrontPage {

    /** JS: khai báo findRow(nhãn) — dòng thông tin dưới tên sản phẩm có nhãn đúng bằng nhãn. Ghép trước phần đọc. */
    private static final String JS_FIND_INFO_ROW = "const h=document.querySelector('h1').closest('.text-left');"
            + "const findRow=t=>[...h.querySelectorAll('.row')].find(x => { const l=x.querySelector('.opacity-50');"
            + " return l && l.textContent.trim()===t; });";
    /** JS: chữ cột giá trị của dòng có nhãn arguments[0], đã gộp khoảng trắng. */
    private static final String JS_INFO_ROW_VALUE = JS_FIND_INFO_ROW
            + "const r=findRow(arguments[0]);const v=r && r.querySelector('.col-sm-10'); return v ? v.textContent.replace(/\\s+/g,' ').trim() : '';";
    /** JS: giá gốc (dòng Price:) có bị gạch ngang (del). */
    private static final String JS_PRICE_STRUCK = JS_FIND_INFO_ROW + "const r=findRow('Price:'); return !!(r && r.querySelector('del'));";
    private static final By PRODUCT_NAME = By.cssSelector("h1.fs-20");
    private static final By BRAND_LINK = By.xpath("//h1/ancestor::div[contains(@class,'text-left')][1]//a[contains(@href,'/brand/')]");
    private static final By BRAND_LOGO = By.xpath("//h1/ancestor::div[contains(@class,'text-left')][1]//a[contains(@href,'/brand/')]/img");
    private static final By QUANTITY_INPUT = By.cssSelector("#option-choice-form input[name='quantity']");
    private static final By AVAILABLE_QUANTITY = By.id("available-quantity");
    private static final By DESCRIPTION_TAB = By.cssSelector("a[href='#tab_default_1']");
    private static final By DESCRIPTION = By.cssSelector("#tab_default_1 .aiz-editor-data, #tab_default_1 .mw-100");

    /** Trang vừa mở ở tab mới (nút View) — chờ tên sản phẩm hiện, đóng popup quảng cáo nếu có. */
    public StorefrontProductPage waitUntilLoaded() {
        WebUI.waitForElementVisible(PRODUCT_NAME);
        closeAdPopupIfShown();
        return this;
    }

    public String productName() {
        return WebUI.getElementText(PRODUCT_NAME);
    }

    /** Tên brand — lấy từ chú thích (alt) của logo brand trên trang sản phẩm ("" nếu sản phẩm không có brand). */
    public String brandName() {
        return !WebUI.checkElementExist(BRAND_LOGO) ? "" : WebUI.getElementAttribute(BRAND_LOGO, "alt").trim();
    }

    @Step("Bấm logo brand trên trang sản phẩm")
    public StorefrontListingPage clickBrand() {
        andWaitForNewPage(() -> WebUI.clickElement(BRAND_LINK));
        return new StorefrontListingPage().waitUntilLoaded();
    }

    /** Chữ ở cột giá trị của dòng có nhãn label (VD "Price:", "Discount Price:"), đã gộp khoảng trắng; "" nếu không có dòng. */
    public String rowValue(String label) {
        return (String) WebUI.executeJS(JS_INFO_ROW_VALUE, label);
    }

    /** Giá gốc có bị gạch ngang (del) — dấu hiệu đang có giảm giá. */
    public boolean isOriginalPriceStruckThrough() {
        return Boolean.TRUE.equals(WebUI.executeJS(JS_PRICE_STRUCK));
    }

    public String availableQuantity() {
        return WebUI.getElementTextNow(AVAILABLE_QUANTITY);
    }

    /** Giá trị mặc định của ô số lượng mua. */
    public String quantityValue() {
        return WebUI.getElementValue(QUANTITY_INPUT);
    }

    @Step("Mở tab Description")
    public String descriptionText() {
        WebUI.scrollToElementAtCenter(DESCRIPTION_TAB);
        WebUI.clickElement(DESCRIPTION_TAB);
        return WebUI.getElementAttribute(DESCRIPTION, "textContent").replaceAll("\\s+", " ").trim();
    }

    public String browserTitle() {
        return WebUI.getPageTitle().trim();
    }
}
