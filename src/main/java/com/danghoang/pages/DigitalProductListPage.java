package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Danh sách Digital Products /admin/digitalproducts — 10 dòng/trang, cột #, Name, Added By, Photo, Base Price,
 * Todays Deal, Published, Featured, Options (Edit, Delete, Download). Locator lấy từ DOM thật 04-11-2026.
 */
public class DigitalProductListPage extends MainListPage {

    private static final By CARD_TITLE = By.xpath("//div[contains(@class,'card-header')]//h5[normalize-space()='Digital Products']");
    private static final By ADD_NEW_BUTTON = By.xpath("//a[contains(@href,'/admin/digitalproducts/create')][normalize-space()='Add New Digital Product']");

    private static final int COL_BASE_PRICE = 5;

    public DigitalProductListPage() {
        super(CARD_TITLE);
    }

    @Step("Mở Digital Products")
    public DigitalProductListPage open() {
        navigate("/admin/digitalproducts");
        return waitUntilLoaded();
    }

    public DigitalProductListPage waitUntilLoaded() {
        waitForListTitle();
        return this;
    }

    public boolean hasAddNewButton() {
        return WebUI.isElementDisplayedNow(ADD_NEW_BUTTON);
    }

    public String basePriceOfRow(int row) {
        return cellText(rows().get(row), COL_BASE_PRICE);
    }

    @Step("Bấm nút Edit ở dòng {row} (đếm từ 0)")
    public DigitalProductFormPage editRow(int row) {
        clickEditOfRow(row);
        return new DigitalProductFormPage().waitUntilEditLoaded();
    }

    /** Tìm theo token rồi xoá lần lượt mọi dòng có tên chứa token (qua hộp xác nhận). */
    @Step("Xoá mọi sản phẩm số có tên chứa {token}")
    public void deleteAllContaining(String token) {
        deleteMatchingRows(token, this::open);
    }
}
