package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

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

    public MediaLibraryModal(WebDriver driver) {
        super(driver);
    }

    public MediaLibraryModal waitUntilOpen() {
        waitForVisible(MODAL);
        wait.until(d -> !d.findElements(FILES).isEmpty());
        return this;
    }

    public List<String> tabs() {
        return texts(TABS);
    }

    public String activeTab() {
        return getText(ACTIVE_TAB);
    }

    public String selectedSort() {
        return new Select(driver.findElement(SORT_SELECT)).getFirstSelectedOption().getText().trim();
    }

    /** Các lựa chọn sắp xếp theo thứ tự trong dropdown. */
    public List<String> sortOptions() {
        return new Select(driver.findElement(SORT_SELECT)).getOptions().stream()
                .map(o -> o.getAttribute("textContent").trim()).toList();
    }

    public boolean isSelectedOnlyChecked() {
        return driver.findElement(SELECTED_ONLY).isSelected();
    }

    public String searchValue() {
        return driver.findElement(SEARCH_INPUT).getAttribute("value");
    }

    public String searchPlaceholder() {
        return driver.findElement(SEARCH_INPUT).getAttribute("placeholder");
    }

    /** Mọi ảnh trong lưới đều có tên tệp và dung lượng. */
    public boolean everyFileHasNameAndSize() {
        List<WebElement> files = driver.findElements(FILES);
        return !files.isEmpty() && files.stream().allMatch(f ->
                !f.findElement(FILE_CARD).getAttribute("title").isBlank()
                        && f.findElement(FILE_SIZE).getText().trim().matches("[\\d.,]+\\s*[KMG]?B"));
    }

    public String selectedCountText() {
        return getText(SELECTED_COUNT);
    }

    public List<String> footerButtons() {
        return List.of(getText(CLEAR_BUTTON), getText(PREV_BUTTON), getText(NEXT_BUTTON), getText(ADD_FILES_BUTTON));
    }

    public boolean hasCloseIcon() {
        return isPresentNow(CLOSE_BUTTON);
    }

    /** Lọc thư viện — ô tìm chỉ lọc sau khi nhấn Enter. */
    @Step("Lọc thư viện theo {keyword}")
    public void search(String keyword) {
        WebElement input = waitForVisible(SEARCH_INPUT);
        input.clear();
        input.sendKeys(keyword, Keys.ENTER);
        wait.until(d -> {
            List<WebElement> files = d.findElements(FILES);
            return !files.isEmpty() && files.stream().allMatch(f ->
                    f.findElement(FILE_CARD).getAttribute("title").toLowerCase().contains(keyword.toLowerCase()));
        });
    }

    @Step("Bấm ảnh thứ {index} trong lưới (đếm từ 0)")
    public void selectFile(int index) {
        // Lưới vẽ lại sau khi lọc / Clear — chờ có đủ ảnh rồi mới bấm
        wait.until(d -> d.findElements(FILES).size() > index);
        driver.findElements(FILES).get(index).findElement(FILE_CARD).click();
        wait.until(d -> "true".equals(d.findElements(FILES).get(index).getAttribute("data-selected")));
    }

    /** Vị trí (từ 0) các ảnh đang được đánh dấu chọn. */
    public List<Integer> selectedIndexes() {
        List<WebElement> files = driver.findElements(FILES);
        return IntStream.range(0, files.size())
                .filter(i -> "true".equals(files.get(i).getAttribute("data-selected"))).boxed().toList();
    }

    public String fileNameAt(int index) {
        return driver.findElements(FILES).get(index).findElement(FILE_CARD).getAttribute("title");
    }

    @Step("Bấm Clear")
    public void clearSelection() {
        click(CLEAR_BUTTON);
        wait.until(d -> d.findElements(SELECTED_FILES).isEmpty());
    }

    @Step("Bấm Add Files")
    public void addFiles() {
        click(ADD_FILES_BUTTON);
        waitUntilClosed();
    }

    @Step("Đóng hộp thư viện bằng {how}")
    public void close(CloseBy how) {
        switch (how) {
            case CLOSE_ICON -> click(CLOSE_BUTTON);
            case ESC -> new Actions(driver).sendKeys(Keys.ESCAPE).perform();
        }
    }

    /** Chờ hộp đóng; hết thời gian chờ mà vẫn mở thì trả false. */
    public boolean waitUntilClosed() {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
        } catch (TimeoutException e) {
            return false;
        }
    }

    private List<String> texts(By locator) {
        return driver.findElements(locator).stream().map(e -> e.getText().trim()).toList();
    }
}
