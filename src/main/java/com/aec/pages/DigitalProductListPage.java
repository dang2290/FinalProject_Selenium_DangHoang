package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Danh sách Digital Products /admin/digitalproducts — 10 dòng/trang, cột #, Name, Added By, Photo, Base Price,
 * Todays Deal, Published, Featured, Options (Edit, Delete, Download). Locator lấy từ DOM thật 04-11-2026.
 */
public class DigitalProductListPage extends AdminListPage {

    private static final By CARD_TITLE = By.xpath("//div[contains(@class,'card-header')]//h5[normalize-space()='Digital Products']");
    private static final By ADD_NEW_BUTTON = By.xpath("//a[contains(@href,'/admin/digitalproducts/create')][normalize-space()='Add New Digital Product']");
    private static final By ROW_BUTTONS = By.cssSelector("td a.btn-circle");

    private static final int COL_NAME = 2;
    private static final int COL_BASE_PRICE = 5;

    public DigitalProductListPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở Digital Products")
    public DigitalProductListPage open() {
        navigate("/admin/digitalproducts");
        return waitUntilLoaded();
    }

    public DigitalProductListPage waitUntilLoaded() {
        waitForVisible(CARD_TITLE);
        waitForTableReady();
        return this;
    }

    @Override
    protected void waitForListTitle() {
        waitUntilLoaded();
    }

    public boolean isTitleDisplayed() {
        return isPresentNow(CARD_TITLE);
    }

    public boolean hasAddNewButton() {
        return isPresentNow(ADD_NEW_BUTTON);
    }

    /** Tiêu đề mọi cột (kể cả cột footable đang giấu). */
    @SuppressWarnings("unchecked")
    public List<String> allHeaders() {
        return (List<String>) js("return [...document.querySelectorAll('table thead th')].map(t => t.textContent.trim());");
    }

    public List<String> names() {
        return columnTexts(COL_NAME);
    }

    public int rowIndexOf(String name) {
        List<String> names = names();
        return IntStream.range(0, names.size()).filter(i -> names.get(i).equals(name)).findFirst().orElse(-1);
    }

    public List<Integer> rowIndexesContaining(String text) {
        List<String> names = names();
        return IntStream.range(0, names.size())
                .filter(i -> names.get(i).toLowerCase().contains(text.toLowerCase())).boxed().toList();
    }

    public String basePriceOfRow(int row) {
        return cellText(rows().get(row), COL_BASE_PRICE);
    }

    public List<String> rowButtonTitles(int row) {
        return rows().get(row).findElements(ROW_BUTTONS).stream().map(e -> e.getAttribute("title")).toList();
    }

    public String idOfRow(int row) {
        return rows().get(row).findElement(ROW_EDIT).getAttribute("href").replaceAll(".*/edit/(\\d+).*", "$1");
    }

    @Step("Bấm nút Edit ở dòng {row} (đếm từ 0)")
    public DigitalProductFormPage editRow(int row) {
        clickEditOfRow(row);
        return new DigitalProductFormPage(driver).waitUntilEditLoaded();
    }

    /** Tìm theo token rồi xoá lần lượt mọi dòng có tên chứa token (qua hộp xác nhận). */
    @Step("Xoá mọi sản phẩm số có tên chứa {token}")
    public void deleteAllContaining(String token) {
        for (int round = 0; round < 10; round++) {
            open();
            search(token);
            List<Integer> matches = rowIndexesContaining(token);
            if (matches.isEmpty()) {
                return;
            }
            deleteRow(matches.get(0));
        }
    }
}
