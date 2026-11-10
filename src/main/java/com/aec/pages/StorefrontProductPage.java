package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Trang sản phẩm ngoài cửa hàng /product/{slug}. Khối thông tin (div.text-left chứa h1): tên, logo brand (link /brand/…,
 * alt = tên brand), Sold by, Price (gạch ngang khi có giảm giá) / Discount Price kèm "/đơn vị", ô số lượng,
 * "(n available)", Total Price; tab Description bên dưới. Trang **không** hiện Category, Tags. Locator lấy từ DOM thật 10-11-2026.
 */
public class StorefrontProductPage extends StorefrontPage {

    private static final By PRODUCT_NAME = By.cssSelector("h1.fs-20");
    private static final By BRAND_LINK = By.xpath("//h1/ancestor::div[contains(@class,'text-left')][1]//a[contains(@href,'/brand/')]");
    private static final By BRAND_LOGO = By.xpath("//h1/ancestor::div[contains(@class,'text-left')][1]//a[contains(@href,'/brand/')]/img");
    private static final By QUANTITY_INPUT = By.cssSelector("#option-choice-form input[name='quantity']");
    private static final By AVAILABLE_QUANTITY = By.id("available-quantity");
    private static final By DESCRIPTION_TAB = By.cssSelector("a[href='#tab_default_1']");
    private static final By DESCRIPTION = By.cssSelector("#tab_default_1 .aiz-editor-data, #tab_default_1 .mw-100");

    public StorefrontProductPage(WebDriver driver) {
        super(driver);
    }

    /** Trang vừa mở ở tab mới (nút View) — chờ tên sản phẩm hiện, đóng popup quảng cáo nếu có. */
    public StorefrontProductPage waitUntilLoaded() {
        waitForVisible(PRODUCT_NAME);
        closeAdPopupIfShown();
        return this;
    }

    public String productName() {
        return getText(PRODUCT_NAME);
    }

    /** Tên brand — lấy từ chú thích (alt) của logo brand trên trang sản phẩm ("" nếu sản phẩm không có brand). */
    public String brandName() {
        return driver.findElements(BRAND_LOGO).isEmpty() ? "" : driver.findElement(BRAND_LOGO).getAttribute("alt").trim();
    }

    @Step("Bấm logo brand trên trang sản phẩm")
    public StorefrontListingPage clickBrand() {
        andWaitForNewPage(() -> click(BRAND_LINK));
        return new StorefrontListingPage(driver).waitUntilLoaded();
    }

    /** Chữ ở cột giá trị của dòng có nhãn label (VD "Price:", "Discount Price:"), đã gộp khoảng trắng; "" nếu không có dòng. */
    public String rowValue(String label) {
        return (String) js("const h=document.querySelector('h1').closest('.text-left');"
                + "const r=[...h.querySelectorAll('.row')].find(x => { const l=x.querySelector('.opacity-50');"
                + " return l && l.textContent.trim()===arguments[0]; });"
                + "const v=r && r.querySelector('.col-sm-10'); return v ? v.textContent.replace(/\\s+/g,' ').trim() : '';", label);
    }

    /** Giá gốc có bị gạch ngang (del) — dấu hiệu đang có giảm giá. */
    public boolean isOriginalPriceStruckThrough() {
        return Boolean.TRUE.equals(js("const h=document.querySelector('h1').closest('.text-left');"
                + "const r=[...h.querySelectorAll('.row')].find(x => { const l=x.querySelector('.opacity-50');"
                + " return l && l.textContent.trim()==='Price:'; }); return !!(r && r.querySelector('del'));"));
    }

    public String availableQuantity() {
        return driver.findElement(AVAILABLE_QUANTITY).getText().trim();
    }

    /** Giá trị mặc định của ô số lượng mua. */
    public String quantityValue() {
        return driver.findElement(QUANTITY_INPUT).getAttribute("value");
    }

    @Step("Mở tab Description")
    public String descriptionText() {
        js("arguments[0].scrollIntoView({block:'center'});", driver.findElement(DESCRIPTION_TAB));
        click(DESCRIPTION_TAB);
        return driver.findElement(DESCRIPTION).getAttribute("textContent").replaceAll("\\s+", " ").trim();
    }

    public String browserTitle() {
        return driver.getTitle().trim();
    }
}
