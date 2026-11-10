package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Trang liệt kê sản phẩm ngoài cửa hàng: trang brand /brand/{slug}, trang danh mục /category/{slug}, và trang
 * All categories /categories (danh sách link /category/…). Locator lấy từ DOM thật 10-11-2026.
 */
public class StorefrontListingPage extends StorefrontPage {

    private static final By PAGE_BODY = By.cssSelector("section, .container");
    private static final By ALL_CATEGORIES_TITLE = By.xpath("//h1[normalize-space()='All categories']");

    public StorefrontListingPage(WebDriver driver) {
        super(driver);
    }

    public StorefrontListingPage waitUntilLoaded() {
        waitForVisible(PAGE_BODY);
        closeAdPopupIfShown();
        return this;
    }

    @Step("Mở All categories ngoài cửa hàng")
    public StorefrontListingPage openAllCategories() {
        openStorefront("/categories", ALL_CATEGORIES_TITLE);
        return this;
    }

    /**
     * Trang có hàng nghìn link /category/ (cả trong menu đang ẩn) — tìm link **đang hiện** có đúng tên bằng một lần gọi JS
     * thay vì đọc từng phần tử.
     */
    @Step("Bấm danh mục {name} ở All categories")
    public StorefrontListingPage clickCategory(String name) {
        WebElement link = (WebElement) js("return [...document.querySelectorAll(\"a[href*='/category/']\")]"
                + ".find(a => a.offsetParent && a.textContent.trim() === arguments[0]) || null;", name);
        if (link == null) {
            throw new IllegalStateException("All categories không có danh mục " + name);
        }
        js("arguments[0].scrollIntoView({block:'center'});", link);
        andWaitForNewPage(link::click);
        return waitUntilLoaded();
    }

    /** Chữ của mọi link sản phẩm đang hiện trên trang (thẻ sản phẩm). */
    @SuppressWarnings("unchecked")
    public List<String> productNames() {
        return (List<String>) js("return [...new Set([...document.querySelectorAll(\"a[href*='/product/']\")]"
                + ".map(a => a.textContent.replace(/\\s+/g,' ').trim()).filter(t => t))];");
    }

    public boolean listsProduct(String name) {
        return productNames().contains(name);
    }
}
