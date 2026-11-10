package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Form Add New Digital Product /admin/digitalproducts/create (nút Save Product) và Edit Digital Product
 * /admin/digitalproducts/edit/{id}?lang=en|vn (tab ngôn ngữ, nút Update Product). Locator lấy từ DOM thật 04-11-2026.
 */
public class DigitalProductFormPage extends BasePage {

    private static final By CREATE_TITLE = By.xpath("//h5[normalize-space()='Add New Digital Product']");
    private static final By EDIT_TITLE = By.xpath("//h5[normalize-space()='Edit Digital Product']");
    private static final By FORM = By.xpath("//form[.//input[@name='name']][.//button[@type='submit']]");
    private static final By NAME_INPUT = By.xpath("//form//input[@name='name']");
    private static final By SUBMIT = By.xpath("//form[.//input[@name='name']]//button[@type='submit']");
    private static final By TAG_INPUT = By.cssSelector("tags.tagify .tagify__input");
    private static final By LANGUAGE_TABS = By.cssSelector("a.nav-link[href*='lang=']");
    private static final By ACTIVE_TAB = By.cssSelector("a.nav-link.active[href*='lang=']");

    public DigitalProductFormPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở Add New Digital Product")
    public DigitalProductFormPage openCreate() {
        navigate("/admin/digitalproducts/create");
        waitForVisible(CREATE_TITLE);
        return this;
    }

    @Step("Mở Edit Digital Product id {id} tab {lang}")
    public DigitalProductFormPage openEdit(String id, String lang) {
        navigate("/admin/digitalproducts/edit/" + id + "?lang=" + lang);
        return waitUntilEditLoaded();
    }

    public DigitalProductFormPage waitUntilEditLoaded() {
        waitForVisible(EDIT_TITLE);
        return this;
    }

    public boolean isCreateTitleDisplayed() {
        return isPresentNow(CREATE_TITLE);
    }

    public boolean isEditTitleDisplayed() {
        return isPresentNow(EDIT_TITLE);
    }

    public boolean isOnEditPage() {
        return currentPath().startsWith("/admin/digitalproducts/edit/");
    }

    @SuppressWarnings("unchecked")
    public List<String> sectionTitles() {
        return (List<String>) js("return [...document.querySelectorAll('.card > .card-header > h5')].map(h => h.textContent.trim());");
    }

    /** Nhãn mọi ô của form (bỏ trùng, theo thứ tự). */
    @SuppressWarnings("unchecked")
    public List<String> fieldLabels() {
        return (List<String>) js("return [...new Set([...arguments[0].querySelectorAll('label')]"
                + ".map(l => l.textContent.replace(/\\s+/g,' ').trim()).filter(t => t))];", driver.findElement(FORM));
    }

    public boolean hasField(String name) {
        return !driver.findElements(By.name(name)).isEmpty();
    }

    public String submitText() {
        return driver.findElement(SUBMIT).getText().trim();
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
        waitUntilEditLoaded();
    }

    public String nameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    @Step("Nhập {name}: [{value}]")
    public void enterField(String name, String value) {
        By locator = By.xpath("//form//input[@name='" + name + "']");
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

    @Step("Thêm tag {tag}")
    public void addTag(String tag) {
        WebElement input = driver.findElement(TAG_INPUT);
        js("arguments[0].scrollIntoView({block:'center'});", input);
        input.click();
        input.sendKeys(tag, Keys.ENTER);
        wait.until(d -> !d.findElements(By.cssSelector("tags.tagify tag")).isEmpty());
    }

    /** PC-5: Name, Tags auto, Unit price, Purchase price 5, Discount 0 — Category để mặc định, không chọn tệp. */
    @Step("Nhập dữ liệu tối thiểu cho sản phẩm số {name}")
    public void fillRequired(String name, String unitPrice) {
        enterName(name);
        addTag("auto");
        enterField("unit_price", unitPrice);
        enterField("purchase_price", "5");
        enterField("discount", "0");
    }

    @Step("Bấm {buttonText}")
    public void clickSubmit(String buttonText) {
        WebElement button = driver.findElement(SUBMIT);
        js("arguments[0].scrollIntoView({block:'center'});", button);
        button.click();
    }

    @Step("Bấm nút lưu và chờ trang tải lại")
    public void submitAndWait() {
        String text = submitText();
        andWaitForNewPage(() -> clickSubmit(text));
    }

    public List<String> errorMessages() {
        return driver.findElements(By.cssSelector("div.alert-danger li")).stream().map(e -> e.getText().trim()).toList();
    }
}
