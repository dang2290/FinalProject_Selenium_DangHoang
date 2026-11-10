package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Trang Edit Product /admin/products/admin/{id}/edit?lang=en|vn — tab English / Vietnamese, ô Slug, một nút Update Product,
 * form gửi tới /admin/products/update/{id}. Locator lấy từ DOM thật 04-11-2026.
 */
public class ProductEditPage extends BasePage {

    private static final String EDIT_FORM = "//form[contains(@action,'/admin/products/update/')]";

    private static final By PAGE_TITLE = By.xpath("//h1[normalize-space()='Edit Product']");
    private static final By LANGUAGE_TABS = By.cssSelector("a.nav-link[href*='/edit?lang=']");
    private static final By ACTIVE_TAB = By.cssSelector("a.nav-link.active[href*='/edit?lang=']");
    private static final By UPDATE_BUTTON = By.xpath(EDIT_FORM + "//button[@type='submit']");
    private static final By ERROR_MESSAGES = By.cssSelector("div.alert-danger li");
    private static final By VARIANT_NAMES = By.cssSelector("#sku_combination tr.variant label");
    private static final By THUMBNAIL_PREVIEW = By.xpath("//input[@name='thumbnail_img']/ancestor::div[contains(@class,'input-group')]"
            + "/following-sibling::div[contains(@class,'file-preview')]/div");

    public ProductEditPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang sửa sản phẩm id {id} tab {lang}")
    public ProductEditPage openById(String id, String lang) {
        navigate("/admin/products/admin/" + id + "/edit?lang=" + lang);
        return waitUntilLoaded();
    }

    public ProductEditPage waitUntilLoaded() {
        waitForVisible(PAGE_TITLE);
        return this;
    }

    public boolean isTitleDisplayed() {
        return isPresentNow(PAGE_TITLE);
    }

    public boolean isOnEditPage() {
        return currentPath().matches("/admin/products/admin/\\d+/edit");
    }

    public String productId() {
        return currentPath().replaceAll(".*/admin/(\\d+)/edit.*", "$1");
    }

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

    public String fieldValue(String name) {
        return driver.findElement(By.xpath(EDIT_FORM + "//*[@name='" + name + "']")).getAttribute("value");
    }

    public String nameValue() {
        return fieldValue("name");
    }

    /** Chữ của lựa chọn đang chọn ở thẻ select gốc (Category, Brand, Discount type…). */
    public String selectedOptionText(String name) {
        return (String) js("const s=document.querySelector('select[name=\"'+arguments[0]+'\"]');"
                + "return s && s.selectedOptions.length ? s.selectedOptions[0].textContent.trim() : '';", name);
    }

    public int updateButtonCount() {
        return driver.findElements(UPDATE_BUTTON).size();
    }

    public String updateButtonText() {
        return driver.findElement(UPDATE_BUTTON).getText().trim();
    }

    @Step("Đổi {name} thành [{value}]")
    public void enterField(String name, String value) {
        By locator = By.xpath(EDIT_FORM + "//input[@name='" + name + "']");
        WebElement field = driver.findElement(locator);
        js("arguments[0].scrollIntoView({block:'center'});", field);
        field.clear();
        if (!value.isEmpty()) {
            type(locator, value);
        }
    }

    public void enterName(String name) {
        enterField("name", name);
    }

    /** Bấm Update Product — không chờ (dùng khi phiên có thể đã hết). */
    @Step("Bấm Update Product")
    public void clickUpdate() {
        // Như form tạo: gõ Name / Unit price gọi AJAX update_sku() — chờ hết request nền rồi mới gửi form
        waitForAjaxIdle();
        WebElement button = driver.findElement(UPDATE_BUTTON);
        js("arguments[0].scrollIntoView({block:'center'});", button);
        button.click();
    }

    @Step("Bấm Update Product và chờ trang tải lại")
    public void updateAndWait() {
        andWaitForNewPage(this::clickUpdate);
        if (!isServerErrorPage()) {
            waitUntilLoaded();
        }
    }

    public List<String> errorMessages() {
        return driver.findElements(ERROR_MESSAGES).stream().map(e -> e.getText().trim()).toList();
    }

    /** Bảng tổ hợp được dựng bằng AJAX sau khi trang tải — chờ có dòng rồi mới đọc. */
    public List<String> variantNames() {
        wait.until(d -> !d.findElements(VARIANT_NAMES).isEmpty());
        return driver.findElements(VARIANT_NAMES).stream().map(e -> e.getAttribute("textContent").trim()).toList();
    }

    @SuppressWarnings("unchecked")
    public List<String> variantPrices() {
        return (List<String>) js("return [...document.querySelectorAll('#sku_combination tr.variant input[name^=\"price_\"]')].map(i => i.value);");
    }

    public int thumbnailPreviewCount() {
        return driver.findElements(THUMBNAIL_PREVIEW).size();
    }

    /** Trang 419 PAGE EXPIRED — gửi form khi phiên đã hết. */
    public boolean isPageExpired() {
        return driver.getTitle().contains("Page Expired") || driver.getPageSource().contains("419");
    }
}
