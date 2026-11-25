package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

/** Thao tác mức trình duyệt cho test (tab, cửa sổ, điều hướng). Step và log do WebUI ghi. */
public class BrowserWindow extends BasePage {

    public void openPath(String path) {
        navigate(path);
    }

    public void openUrl(String url) {
        WebUI.openURL(url);
        WebUI.waitForPageLoaded();
    }

    public void back() {
        WebUI.navigateBack();
    }

    public void refresh() {
        andWaitForNewPage(WebUI::refreshPage);
    }

    public void resize(int width, int height) {
        WebUI.setWindowSize(width, height);
    }

    public String currentHandle() {
        return WebUI.getCurrentWindowHandle();
    }

    /** Mở tab trống mới, chuyển sang đó; trả về handle của tab mới. */
    public String openNewTab() {
        return WebUI.openNewTab();
    }

    public void switchTo(String handle) {
        WebUI.switchToWindow(handle);
    }

    public void closeTabAndSwitchTo(String handle) {
        WebUI.closeCurrentTab(handle);
    }

    /** Đóng hộp thoại alert đang mở (nếu có), dùng khi dọn dữ liệu. */
    public void dismissAlertIfOpen() {
        WebUI.dismissAlertIfPresent();
    }
}
