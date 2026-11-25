package com.danghoang.pages;

import org.openqa.selenium.By;

/**
 * Danh sách chính của một module (Brand, Category, Color, Product, Digital): mở thẳng bằng địa chỉ, có ô tìm.
 * Test gọi searchFor / findRow, không tự ghép open + search + rowIndexOf.
 */
public abstract class MainListPage extends AdminListPage {

    protected MainListPage(By title) {
        super(title);
    }

    public abstract MainListPage open();

    /** Mở lại danh sách (bỏ bộ lọc cũ) rồi tìm. */
    public void searchFor(String keyword) {
        open();
        search(keyword);
    }

    /** Tìm theo chính tên đó; không có thì -1. */
    public int findRow(String name) {
        return findRow(name, name);
    }

    /** Tìm theo keyword, trả về vị trí dòng có đúng tên name; không có thì -1. */
    public int findRow(String keyword, String name) {
        searchFor(keyword);
        return rowIndexOf(name);
    }
}
