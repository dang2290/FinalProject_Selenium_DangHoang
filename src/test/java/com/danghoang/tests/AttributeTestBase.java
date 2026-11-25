package com.danghoang.tests;

import com.danghoang.pages.AttributeDetailPage;
import com.danghoang.pages.AttributeEditPage;
import com.danghoang.pages.AttributeListPage;
import com.danghoang.pages.CategoryListPage;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiền đề chung của TC Attribute (docs/testcases/attribute/web/test_cases_attribute_web.md):
 * PC-1 đăng nhập · PC-2 tạo thuộc tính test · PC-3 dọn: giá trị trước, thuộc tính sau, rồi danh mục test.
 * PC-4: bảng không có ô tìm — chỉ đụng dòng có tên chứa mã truy vết của test.
 */
abstract class AttributeTestBase extends TrackedDataTestBase {

    protected static final String INSERTED = "Attribute has been inserted successfully";
    protected static final String UPDATED = "Attribute has been updated successfully";
    protected static final String DELETED = "Attribute has been deleted successfully";
    protected static final String VALUE_INSERTED = "Attribute value has been inserted successfully";
    protected static final String VALUE_UPDATED = "Attribute value has been updated successfully";
    protected static final String VALUE_DELETED = "Attribute value has been deleted successfully";
    /** Chạy song song 5 luồng — thuộc tính vừa tạo nằm trong vài dòng đầu, không chắc đúng dòng 1. */
    protected static final int TOP_ROWS = 5;

    private final ThreadLocal<List<String>> categoryTokens = ThreadLocal.withInitial(ArrayList::new);

    /** Test có tạo danh mục chứa token — xoá cả danh mục khi dọn. */
    protected void alsoCleanCategories(String token) {
        categoryTokens.get().add(token);
    }

    /** PC-1: đăng nhập admin rồi mở All Attributes. */
    protected AttributeListPage openAttributesAsAdmin() {
        loginAsAdmin();
        return arrange("Mở trang All Attributes", () -> new AttributeListPage().open());
    }

    /** PC-2: tạo thuộc tính bằng form Add New Attribute. */
    protected void createAttribute(AttributeListPage list, String name) {
        arrange("Tạo thuộc tính test " + name, () -> {
            list.open();
            list.createAttribute(name);
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Tạo thuộc tính test phải thành công");
        });
    }

    protected AttributeEditPage openEditOf(AttributeListPage list, String name) {
        return arrange("Mở trang sửa của " + name, () -> {
            int row = list.findRow(name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy thuộc tính test " + name);
            return list.editRow(row);
        });
    }

    protected AttributeDetailPage openDetailOf(AttributeListPage list, String name) {
        return arrange("Mở trang Attribute Detail của " + name, () -> {
            int row = list.findRow(name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy thuộc tính test " + name);
            return list.openValuesOfRow(row);
        });
    }

    @Override
    protected void cleanUpTestData() {
        List<String> attributes = takeTokens();
        List<String> categories = takeAll(categoryTokens);
        dismissAlertIfOpen();
        for (String token : attributes) {
            runCleanup("xoá thuộc tính chứa " + token,
                    () -> new AttributeListPage().deleteAllContaining(token));
        }
        for (String token : categories) {
            runCleanup("xoá danh mục chứa " + token,
                    () -> new CategoryListPage().deleteAllContaining(token));
        }
    }
}
