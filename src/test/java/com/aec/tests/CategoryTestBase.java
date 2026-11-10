package com.aec.tests;

import com.aec.base.BaseTest;
import com.aec.pages.CategoryFormPage;
import com.aec.pages.CategoryListPage;
import com.aec.utils.TestDataGenerator;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Tiền đề chung của TC Category (docs/testcases/category/web/test_cases_category_web.md):
 * PC-1 đăng nhập · PC-2 tạo danh mục test · PC-3 dọn danh mục test, cấp sâu nhất trước (cháu → con → cha).
 * PC-4: chỉ tìm và đụng danh mục chứa mã truy vết của test — không bao giờ chạm 4541–4543 (kẹt vòng lặp cha–con).
 */
abstract class CategoryTestBase extends BaseTest {

    protected static final String INSERTED = "Category has been inserted successfully";
    protected static final String UPDATED = "Category has been updated successfully";
    protected static final String DELETED = "Category has been deleted successfully";
    protected static final String FEATURED_UPDATED = "Featured categories updated successfully";
    protected static final String NO_PARENT = "No Parent";
    protected static final String EMPTY_CELL = "—";

    private static final Logger LOG = LogManager.getLogger(CategoryTestBase.class);
    private final ThreadLocal<List<String>> tokens = ThreadLocal.withInitial(ArrayList::new);

    /** Mã truy vết cho dữ liệu của test — danh mục chứa mã này sẽ bị xoá khi test kết thúc. */
    protected String newToken(String testName) {
        String token = TestDataGenerator.token(testName);
        tokens.get().add(token);
        return token;
    }

    /** PC-1: đăng nhập admin rồi mở All categories. */
    protected CategoryListPage openCategoriesAsAdmin() {
        loginAsAdmin();
        return Allure.step("Arrange: Mở trang All categories", () -> new CategoryListPage(driver()).open());
    }

    /** PC-2: tạo danh mục bằng form Add New category; extra điền thêm các ô khác trước khi Save. */
    protected void createCategory(String name, Consumer<CategoryFormPage> extra) {
        Allure.step("Arrange: Tạo danh mục test " + name, () -> {
            CategoryFormPage form = new CategoryFormPage(driver()).openCreate();
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

    /** Tìm danh mục theo tên đầy đủ, trả về vị trí dòng mang đúng tên đó (-1 nếu không có). */
    protected int findRow(CategoryListPage list, String name) {
        list.open();
        list.search(name);
        return list.rowIndexOf(name);
    }

    /** Tìm danh mục test rồi mở form sửa của nó. */
    protected CategoryFormPage openEditOf(CategoryListPage list, String name) {
        return Allure.step("Arrange: Mở form sửa của " + name, () -> {
            int row = findRow(list, name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy danh mục test " + name);
            return list.editRow(row);
        });
    }

    @Override
    protected void cleanUpTestData() {
        List<String> created = new ArrayList<>(tokens.get());
        tokens.remove();
        for (String token : created) {
            try {
                Allure.step("Dọn dữ liệu: xoá danh mục chứa " + token, () -> deleteCategoriesContaining(token));
            } catch (RuntimeException e) {
                LOG.warn("Không dọn được danh mục chứa {} — cần xoá tay", token, e);
            }
        }
    }

    private void deleteCategoriesContaining(String token) {
        dismissAlertIfOpen();
        new CategoryListPage(driver()).deleteAllContaining(token);
    }

    private void dismissAlertIfOpen() {
        try {
            driver().switchTo().alert().dismiss();
        } catch (NoAlertPresentException e) {
            LOG.debug("Không có hộp thoại cần đóng");
        }
    }
}
