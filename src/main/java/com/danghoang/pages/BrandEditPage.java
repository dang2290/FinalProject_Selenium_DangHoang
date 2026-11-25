package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Trang Brand Information /admin/brands/edit/{id}?lang=en|vn — locator lấy từ DOM thật 03-11-2026. */
public class BrandEditPage extends TranslatableFormPage {

    private static final String EDIT_FORM = "form[action*='/admin/brands/'] ";

    private static final By PAGE_TITLE = By.xpath("//h5[normalize-space()='Brand Information']");
    private static final By FORM_LABELS = By.cssSelector(EDIT_FORM + "label");
    private static final By TRANSLATABLE_ICON = By.cssSelector(EDIT_FORM + "label i.la-language");
    private static final By NAME_INPUT = AdminLocators.NAME_INPUT;
    private static final By META_TITLE_INPUT = By.cssSelector(EDIT_FORM + "input[name='meta_title']");
    private static final By META_DESCRIPTION_INPUT = By.cssSelector(EDIT_FORM + "textarea[name='meta_description']");
    private static final By SLUG_INPUT = AdminLocators.SLUG_INPUT;
    private static final By LOGO_PREVIEW_ITEMS = By.cssSelector(EDIT_FORM + ".file-preview-item");
    private static final By LOGO_INPUT = By.cssSelector(EDIT_FORM + "input[name='logo']");
    private static final By SAVE_BUTTON = By.cssSelector(EDIT_FORM + "button[type='submit']");
    private static final By NOT_FOUND_TEXT = By.xpath("//p[contains(normalize-space(),'has not been found on our server')]");

    public BrandEditPage() {
        super(PAGE_TITLE, NAME_INPUT, "/admin/brands/edit/");
    }

    @Step("Mở form sửa brand theo đường dẫn {url}")
    public BrandEditPage openUrl(String url) {
        WebUI.openURL(url);
        return waitUntilLoaded();
    }

    @Step("Mở đường dẫn sửa {path}")
    public void openPath(String path) {
        navigate(path);
    }

    public BrandEditPage waitUntilLoaded() {
        waitForForm();
        return this;
    }

    public boolean isFormDisplayed() {
        return WebUI.isElementDisplayedNow(PAGE_TITLE) && WebUI.isElementDisplayedNow(NAME_INPUT);
    }

    public boolean isNotFoundPage() {
        return WebUI.isElementDisplayedNow(NOT_FOUND_HEADING) && WebUI.isElementDisplayedNow(NOT_FOUND_TEXT);
    }

    public List<String> formLabels() {
        return WebUI.getAllElementsText(FORM_LABELS);
    }

    /** Chú thích của biểu tượng ngôn ngữ cạnh nhãn Name (hiện khi rê chuột). */
    public String translatableTooltip() {
        return WebUI.getTooltipOnHover(WebUI.getWebElement(TRANSLATABLE_ICON), "biểu tượng ngôn ngữ");
    }

    public String metaTitleValue() {
        return WebUI.getElementValue(META_TITLE_INPUT);
    }

    public String metaDescriptionValue() {
        return WebUI.getElementValue(META_DESCRIPTION_INPUT);
    }

    public String slugValue() {
        return WebUI.getElementValue(SLUG_INPUT);
    }

    public boolean isSlugEditable() {
        WebElement slug = WebUI.getWebElement(SLUG_INPUT);
        return slug.isEnabled() && slug.getAttribute("readonly") == null;
    }

    /** Ô logo đã có id ảnh thì chờ ảnh xem trước dựng xong rồi mới đếm. */
    public int logoPreviewCount() {
        String logoId = WebUI.checkElementExist(LOGO_INPUT) ? WebUI.getElementValue(LOGO_INPUT) : "";
        return previewCountWhenReady(logoId, LOGO_PREVIEW_ITEMS);
    }

    public String logoPreviewTitle() {
        return WebUI.getElementAttribute(LOGO_PREVIEW_ITEMS, "title");
    }

    public boolean isSaveDisplayed() {
        return WebUI.isElementDisplayedNow(SAVE_BUTTON);
    }

    @Step("Đổi Name thành {name}")
    public void enterName(String name) {
        WebUI.replaceText(NAME_INPUT, name);
    }

    @Step("Xoá hết ô Name")
    public void clearName() {
        WebUI.replaceText(NAME_INPUT, "");
    }

    @Step("Nhập Meta Title: {metaTitle}")
    public void enterMetaTitle(String metaTitle) {
        WebUI.replaceText(META_TITLE_INPUT, metaTitle);
    }

    @Step("Đặt Slug: {slug}")
    public void enterSlug(String slug) {
        WebUI.replaceText(SLUG_INPUT, slug);
    }

    /** Bấm Save — không chờ gì (dùng khi trình duyệt có thể chặn form). */
    @Step("Bấm Save ở form sửa")
    public void clickSave() {
        markPage();
        WebUI.clickElementNow(SAVE_BUTTON);
    }

    @Step("Bấm Save và chờ trang tải lại")
    public void saveAndWait() {
        andWaitForNewPage(this::clickSave);
    }

    /** Đặt Slug rồi lưu, chờ trang tải lại. */
    public void saveWithSlug(String slug) {
        enterSlug(slug);
        saveAndWait();
    }
}
