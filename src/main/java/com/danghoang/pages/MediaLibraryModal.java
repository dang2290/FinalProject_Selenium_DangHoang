package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.IntStream;

/** Hộp thư viện tệp (#aizUploaderModal) mở từ nút Browse của ô Logo. Hộp bị gỡ khỏi DOM khi đóng. */
public class MediaLibraryModal extends BasePage {

    private static final By MODAL = By.id("aizUploaderModal");
    private static final By TABS = By.cssSelector("#aizUploaderModal .nav-tabs a.nav-link");
    private static final By ACTIVE_TAB = By.cssSelector("#aizUploaderModal .nav-tabs a.nav-link.active");
    private static final By SORT_SELECT = By.cssSelector("#aizUploaderModal select[name='aiz-uploader-sort']");
    private static final By SELECTED_ONLY = By.id("aiz-show-selected");
    private static final By SEARCH_INPUT = By.cssSelector("#aizUploaderModal input[name='aiz-uploader-search']");
    private static final By FILES = By.cssSelector("#aizUploaderModal .aiz-file-box-wrap");
    private static final By FILE_CARD = By.cssSelector(".aiz-uploader-select");
    private static final By FILE_SIZE = By.cssSelector(".card-body p");
    private static final By SELECTED_FILES = By.cssSelector("#aizUploaderModal .aiz-file-box-wrap[data-selected='true']");
    private static final By SELECTED_COUNT = By.cssSelector("#aizUploaderModal .aiz-uploader-selected");
    private static final By CLEAR_BUTTON = By.cssSelector("#aizUploaderModal .aiz-uploader-selected-clear");
    private static final By PREV_BUTTON = By.id("uploader_prev_btn");
    private static final By NEXT_BUTTON = By.id("uploader_next_btn");
    private static final By ADD_FILES_BUTTON = By.cssSelector("#aizUploaderModal [data-toggle='aizUploaderAddSelected']");
    private static final By CLOSE_BUTTON = By.cssSelector("#aizUploaderModal .modal-header .close");

    /** Cách đóng hộp thư viện. */
    public enum CloseBy { CLOSE_ICON, ESC }

    public MediaLibraryModal waitUntilOpen() {
        WebUI.waitForElementVisible(MODAL);
        WebUI.waitUntil("lưới tệp có ảnh", () -> WebUI.checkElementExist(FILES));
        return this;
    }

    public List<String> tabs() {
        return WebUI.getAllElementsText(TABS);
    }

    public String activeTab() {
        return WebUI.getElementText(ACTIVE_TAB);
    }

    public String selectedSort() {
        return WebUI.getSelectedOptionText(SORT_SELECT);
    }

    /** Các lựa chọn sắp xếp theo thứ tự trong dropdown. */
    public List<String> sortOptions() {
        return WebUI.getAllOptionsText(SORT_SELECT);
    }

    public boolean isSelectedOnlyChecked() {
        return WebUI.isElementSelected(SELECTED_ONLY);
    }

    public String searchValue() {
        return WebUI.getElementValue(SEARCH_INPUT);
    }

    public String searchPlaceholder() {
        return WebUI.getElementAttribute(SEARCH_INPUT, "placeholder");
    }

    /** Mọi ảnh trong lưới đều có tên tệp và dung lượng. */
    public boolean everyFileHasNameAndSize() {
        List<WebElement> files = WebUI.getWebElements(FILES);
        return !files.isEmpty() && files.stream().allMatch(f ->
                !WebUI.getWebElement(f, FILE_CARD).getAttribute("title").isBlank()
                        && WebUI.getWebElement(f, FILE_SIZE).getText().trim().matches("[\\d.,]+\\s*[KMG]?B"));
    }

    public String selectedCountText() {
        return WebUI.getElementText(SELECTED_COUNT);
    }

    public List<String> footerButtons() {
        return List.of(WebUI.getElementText(CLEAR_BUTTON), WebUI.getElementText(PREV_BUTTON), WebUI.getElementText(NEXT_BUTTON), WebUI.getElementText(ADD_FILES_BUTTON));
    }

    public boolean hasCloseIcon() {
        return WebUI.isElementDisplayedNow(CLOSE_BUTTON);
    }

    /** Lọc thư viện — ô tìm chỉ lọc sau khi nhấn Enter. */
    @Step("Lọc thư viện theo {keyword}")
    public void search(String keyword) {
        WebUI.setTextAndKey(SEARCH_INPUT, keyword, Keys.ENTER);
        WebUI.waitUntil("thư viện lọc theo " + keyword, () -> {
            List<WebElement> files = WebUI.getWebElements(FILES);
            return !files.isEmpty() && files.stream().allMatch(f ->
                    WebUI.getWebElement(f, FILE_CARD).getAttribute("title").toLowerCase().contains(keyword.toLowerCase()));
        });
    }

    @Step("Bấm ảnh thứ {index} trong lưới (đếm từ 0)")
    public void selectFile(int index) {
        // Lưới vẽ lại sau khi lọc / Clear — chờ có đủ ảnh rồi mới bấm
        WebUI.waitUntil("lưới có ảnh thứ " + index, () -> WebUI.getElementCount(FILES) > index);
        WebUI.clickElement(WebUI.getWebElement(WebUI.getWebElements(FILES).get(index), FILE_CARD), "ảnh thứ " + index);
        WebUI.waitUntil("ảnh được đánh dấu chọn", () -> "true".equals(WebUI.getWebElements(FILES).get(index).getAttribute("data-selected")));
    }

    /** Vị trí (từ 0) các ảnh đang được đánh dấu chọn. */
    public List<Integer> selectedIndexes() {
        List<WebElement> files = WebUI.getWebElements(FILES);
        return IntStream.range(0, files.size())
                .filter(i -> "true".equals(files.get(i).getAttribute("data-selected"))).boxed().toList();
    }

    public String fileNameAt(int index) {
        return WebUI.getWebElement(WebUI.getWebElements(FILES).get(index), FILE_CARD).getAttribute("title");
    }

    @Step("Bấm Clear")
    public void clearSelection() {
        WebUI.clickElement(CLEAR_BUTTON);
        WebUI.waitUntil("bỏ chọn hết ảnh", () -> !WebUI.checkElementExist(SELECTED_FILES));
    }

    @Step("Bấm Add Files")
    public void addFiles() {
        WebUI.clickElement(ADD_FILES_BUTTON);
        waitUntilClosed();
    }

    @Step("Đóng hộp thư viện bằng {how}")
    public void close(CloseBy how) {
        switch (how) {
            case CLOSE_ICON -> WebUI.clickElement(CLOSE_BUTTON);
            case ESC -> WebUI.pressKey(Keys.ESCAPE);
        }
    }

    /** Chờ hộp đóng; hết thời gian chờ mà vẫn mở thì trả false. */
    public boolean waitUntilClosed() {
        return WebUI.waitUntilElementGone(MODAL);
    }
}
