package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import org.openqa.selenium.By;

/** Khối Shipping Configuration của form Add New Product (chỉ đọc). */
public class ProductShippingSection extends BasePage {

    /** JS: chữ thân khối Shipping Configuration, đã gộp khoảng trắng ("" nếu không có khối). */
    private static final String JS_SHIPPING_TEXT = "const c=[...document.querySelectorAll('.card')].find(c => ((c.querySelector('.card-header h5')||{}).textContent||'').trim()==='Shipping Configuration');"
            + "return c ? c.querySelector('.card-body').textContent.replace(/\\s+/g,' ').trim() : '';";
    private static final String SHIPPING_CARD = "//div[contains(@class,'card')][.//h5[normalize-space()='Shipping Configuration']]";
    private static final By SHIPPING_LINK = By.xpath(SHIPPING_CARD + "//a[contains(@href,'/admin/shipping_configuration')]");
    private static final By SHIPPING_INPUTS = By.xpath(SHIPPING_CARD + "//div[contains(@class,'card-body')]//input");

    public String shippingConfigurationText() {
        return (String) WebUI.executeJS(JS_SHIPPING_TEXT);
    }

    public boolean shippingConfigurationHasLink() {
        return WebUI.isElementDisplayedNow(SHIPPING_LINK);
    }

    public boolean shippingConfigurationHasInputs() {
        return WebUI.checkElementExist(SHIPPING_INPUTS);
    }
}
