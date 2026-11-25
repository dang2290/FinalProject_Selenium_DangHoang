package com.danghoang.tests;

import com.danghoang.pages.CategoryFormPage;
import com.danghoang.pages.CategoryListPage;
import org.testng.Assert;

import java.util.function.Consumer;

/**
 * Tiền đề chung của TC Category (docs/testcases/category/web/test_cases_category_web.md):
 * PC-1 đăng nhập · PC-2 tạo danh mục test · PC-3 dọn danh mục test, cấp sâu nhất trước (cháu → con → cha).
 * PC-4: chỉ tìm và đụng danh mục chứa mã truy vết của test — không bao giờ chạm 4541–4543 (kẹt vòng lặp cha–con).
 */
abstract class CategoryTestBase extends TrackedDataTestBase {

    protected static final String INSERTED = "Category has been inserted successfully";
    protected static final String UPDATED = "Category has been updated successfully";
    protected static final String DELETED = "Category has been deleted successfully";
    protected static final String FEATURED_UPDATED = "Featured categories updated successfully";
    protected static final String NO_PARENT = "No Parent";
    protected static final String EMPTY_CELL = "—";

    /** PC-1: đăng nhập admin rồi mở All categories. */
    protected CategoryListPage openCategoriesAsAdmin() {
        loginAsAdmin();
        return arrange("Mở trang All categories", () -> new CategoryListPage().open());
    }

    /** PC-2: tạo danh mục bằng form Add New category; extra điền thêm các ô khác trước khi Save. */
    protected void createCategory(String name, Consumer<CategoryFormPage> extra) {
        arrange("Tạo danh mục test " + name, () -> {
            CategoryFormPage form = new CategoryFormPage().openCreate();
            form.enterName(name);
            extra.accept(form);
            form.saveAndWait();
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Tạo danh mục test phải thành công");
        });
    }

    protected void createCategory(String name) {
        createCategory(name, form -> { });
    }

    protected void createChildCategory(String name, String parentName) {
        createCategory(name, form -> form.chooseParent(parentName));
    }

    /** Tìm danh mục test rồi mở form sửa của nó. */
    protected CategoryFormPage openEditOf(CategoryListPage list, String name) {
        return arrange("Mở form sửa của " + name, () -> {
            int row = list.findRow(name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy danh mục test " + name);
            return list.editRow(row);
        });
    }

    @Override
    protected void cleanUpTestData() {
        for (String token : takeTokens()) {
            runCleanup("xoá danh mục chứa " + token, () -> deleteCategoriesContaining(token));
        }
    }

    private void deleteCategoriesContaining(String token) {
        dismissAlertIfOpen();
        new CategoryListPage().deleteAllContaining(token);
    }
}
