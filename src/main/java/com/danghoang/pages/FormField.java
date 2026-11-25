package com.danghoang.pages;

import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Stream;

/**
 * Các ô của form quản trị. Test gọi theo tên nghiệp vụ; thuộc tính name trong HTML chỉ khai ở đây —
 * giao diện đổi name thì sửa một dòng.
 */
public enum FormField {

    NAME("name"),
    SLUG("slug"),
    META_TITLE("meta_title"),
    META_DESCRIPTION("meta_description"),
    PARENT_CATEGORY("parent_id"),
    ORDER_LEVEL("order_level"),
    CATEGORY_TYPE("digital"),
    FILTERING_ATTRIBUTES("filtering_attributes[]"),
    CATEGORY("category_id"),
    BRAND("brand_id"),
    UNIT("unit"),
    WEIGHT("weight"),
    MIN_QTY("min_qty"),
    THUMBNAIL("thumbnail_img"),
    VIDEO_PROVIDER("video_provider"),
    VIDEO_LINK("video_link"),
    VARIATION_ATTRIBUTES("choice_attributes[]"),
    UNIT_PRICE("unit_price"),
    PURCHASE_PRICE("purchase_price"),
    DATE_RANGE("date_range"),
    DISCOUNT("discount"),
    DISCOUNT_TYPE("discount_type"),
    QUANTITY("current_stock"),
    SKU("sku"),
    TAX("tax[]"),
    TAX_TYPE("tax_type[]"),
    LOW_STOCK_QUANTITY("low_stock_quantity"),
    STOCK_VISIBILITY("stock_visibility_state"),
    CASH_ON_DELIVERY("cash_on_delivery"),
    FEATURED("featured"),
    TODAYS_DEAL("todays_deal"),
    FLASH_DISCOUNT("flash_discount"),
    SHIPPING_DAYS("est_shipping_days");

    private final String htmlName;

    FormField(String htmlName) {
        this.htmlName = htmlName;
    }

    By locator() {
        return By.name(htmlName);
    }

    /** Tên hiện trong bước Allure và trong chuỗi thứ tự focus khi nhấn Tab. */
    @Override
    public String toString() {
        return htmlName;
    }

    /** Thứ tự focus mong đợi, cùng dạng với kết quả tabSequenceFromName(). */
    public static List<String> names(FormField... fields) {
        return Stream.of(fields).map(FormField::toString).toList();
    }
}
