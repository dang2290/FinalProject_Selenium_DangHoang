package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/** Trang Brand Information /admin/brands/edit/{id}?lang=en|vn — locator lấy từ DOM thật 03-11-2026. */
public class BrandEditPage extends BasePage {

    private static final String EDIT_FORM = "form[action*='/admin/brands/'] ";

    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Brand Information']");
    private static final By LANGUAGE_TABS = By.cssSelector("a.nav-link[href*='/admin/brands/edit/']");
    private static final By ACTIVE_TAB = By.cssSelector("a.nav-link.active[href*='/admin/brands/edit/']");
    private static final By FORM_LABELS = By.cssSelector(EDIT_FORM + "label");
    private static final By TRANSLATABLE_ICON = By.cssSelector(EDIT_FORM + "label i.la-language");
    private static final By NAME_INPUT = By.id("name");
    private static final By META_TITLE_INPUT = By.cssSelector(EDIT_FORM + "input[name='meta_title']");
    private static final By META_DESCRIPTION_INPUT = By.cssSelector(EDIT_FORM + "textarea[name='meta_description']");
    private static final By SLUG_INPUT = By.id("slug");
    private static final By LOGO_PREVIEW_ITEMS = By.cssSelector(EDIT_FORM + ".file-preview-item");
    private static final By SAVE_BUTTON = By.cssSelector(EDIT_FORM + "button[type='submit']");
    private static final By NOT_FOUND_HEADING = By.xpath("//h1[normalize-space()='Page Not Found!']");
    private static final By NOT_FOUND_TEXT = By.xpath("//p[contains(normalize-space(),'has not been found on our server')]");

    public BrandEditPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở form sửa brand theo đường dẫn {url}")
    public BrandEditPage openUrl(String url) {
        driver.get(url);
        return waitUntilLoaded();
    }

    @Step("Mở đường dẫn sửa {path}")
    public void openPath(String path) {
        navigate(path);
    }

    public BrandEditPage waitUntilLoaded() {
        waitForVisible(PAGE_TITLE);
        return this;
    }

    public boolean isFormDisplayed() {
        return isPresentNow(PAGE_TITLE) && isPresentNow(NAME_INPUT);
    }

    public boolean isNotFoundPage() {
        return isPresentNow(NOT_FOUND_HEADING) && isPresentNow(NOT_FOUND_TEXT);
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

    public List<String> formLabels() {
        return driver.findElements(FORM_LABELS).stream().map(e -> e.getText().trim()).toList();
    }

    /** Chú thích của biểu tượng ngôn ngữ cạnh nhãn Name (hiện khi rê chuột). */
    public String translatableTooltip() {
        WebElement icon = driver.findElement(TRANSLATABLE_ICON);
        new Actions(driver).moveToElement(icon).perform();
        return icon.getAttribute("title");
    }

    public String nameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String metaTitleValue() {
        return driver.findElement(META_TITLE_INPUT).getAttribute("value");
    }

    public String metaDescriptionValue() {
        return driver.findElement(META_DESCRIPTION_INPUT).getAttribute("value");
    }

    public String slugValue() {
        return driver.findElement(SLUG_INPUT).getAttribute("value");
    }

    public boolean isSlugEditable() {
        WebElement slug = driver.findElement(SLUG_INPUT);
        return slug.isEnabled() && slug.getAttribute("readonly") == null;
    }

    public int logoPreviewCount() {
        return driver.findElements(LOGO_PREVIEW_ITEMS).size();
    }

    public String logoPreviewTitle() {
        return driver.findElement(LOGO_PREVIEW_ITEMS).getAttribute("title");
    }

    public boolean isSaveDisplayed() {
        return isPresentNow(SAVE_BUTTON);
    }

    @Step("Đổi Name thành {name}")
    public void enterName(String name) {
        clearAndType(NAME_INPUT, name);
    }

    @Step("Xoá hết ô Name")
    public void clearName() {
        clearAndType(NAME_INPUT, "");
    }

    @Step("Nhập Meta Title: {metaTitle}")
    public void enterMetaTitle(String metaTitle) {
        clearAndType(META_TITLE_INPUT, metaTitle);
    }

    @Step("Đặt Slug: {slug}")
    public void enterSlug(String slug) {
        clearAndType(SLUG_INPUT, slug);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở form sửa")
    public void clickSave() {
        driver.findElement(SAVE_BUTTON).click();
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitForNewPage(this::clickSave);
    }

    public String nameValidationMessage() {
        return validationMessage(NAME_INPUT);
    }

    /** Ô đã có sẵn giá trị: chọn hết rồi gõ đè (clear() không phát sự kiện bàn phím). */
    private void clearAndType(By locator, String value) {
        WebElement field = waitForVisible(locator);
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        if (!value.isEmpty()) {
            type(locator, value);
        }
    }
}
