package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Form sửa có tab ngôn ngữ (English / Vietnamese). */
public abstract class TranslatableFormPage extends AdminFormPage {

    private final By languageTabs;
    private final By activeTab;

    /** tabHref: phần đường dẫn mà mọi tab ngôn ngữ của form này cùng chứa. */
    protected TranslatableFormPage(By title, By nameInput, String tabHref) {
        super(title, nameInput);
        this.languageTabs = By.cssSelector("a.nav-link[href*='" + tabHref + "']");
        this.activeTab = By.cssSelector("a.nav-link.active[href*='" + tabHref + "']");
    }

    public List<String> languageTabs() {
        return WebUI.getAllElementsText(languageTabs);
    }

    public String activeTab() {
        return WebUI.getElementText(activeTab);
    }

    public abstract void enterName(String name);

    public abstract void saveAndWait();

    /** Nhập Name rồi lưu, chờ trang tải lại. */
    public void saveWithName(String name) {
        enterName(name);
        saveAndWait();
    }

    @Step("Chọn tab ngôn ngữ {tab}")
    public void clickTab(String tab) {
        WebElement target = WebUI.getWebElements(languageTabs).stream()
                .filter(e -> e.getText().trim().equals(tab)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Không có tab " + tab));
        andWaitForNewPage(() -> WebUI.clickElement(target, "tab " + tab));
        waitForForm();
    }
}
