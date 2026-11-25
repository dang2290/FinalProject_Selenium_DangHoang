package com.danghoang.pages;

import org.openqa.selenium.By;

/**
 * Locator của thành phần admin xuất hiện giống hệt ở nhiều trang — khai một nơi, chỉ dùng trong package pages.
 * Trùng chuỗi nhưng khác ý nghĩa (VD form đăng nhập admin và form đăng nhập khách) thì KHÔNG đưa vào đây.
 */
final class AdminLocators {

    static final By NAME_INPUT = By.id("name");
    static final By SLUG_INPUT = By.id("slug");
    static final By VALUE_INPUT = By.id("value");
    static final By VALUE_SAVE_BUTTON = By.xpath("//form[.//input[@id='value']]//button[@type='submit']");
    static final By SEARCH_INPUT = By.id("search");
    static final By CARD_HEADERS = By.cssSelector(".card-header");
    static final By CARD_HEADER_TITLES = By.cssSelector(".card-header h5");
    static final By LANGUAGE_LINKS = By.cssSelector("a.nav-link[href*='lang=']");
    static final By SIDEBAR_COLORS = By.cssSelector("a.aiz-side-nav-link[href$='/admin/colors']");
    static final By COLORS_MENU_ACTIVE = By.cssSelector(".aiz-side-nav-link.active[href$='/admin/colors']");
    static final By TAG_INPUT = By.cssSelector("tags.tagify .tagify__input");
    static final By TAGS = By.cssSelector("tags.tagify tag");
    static final By THUMBNAIL_PREVIEW = By.xpath("//input[@name='thumbnail_img']/ancestor::div[contains(@class,'input-group')]"
            + "/following-sibling::div[contains(@class,'file-preview')]/div");

    private AdminLocators() {}
}
