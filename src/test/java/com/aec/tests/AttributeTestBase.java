package com.aec.tests;

import com.aec.base.BaseTest;
import com.aec.pages.AttributeDetailPage;
import com.aec.pages.AttributeEditPage;
import com.aec.pages.AttributeListPage;
import com.aec.pages.CategoryListPage;
import com.aec.utils.TestDataGenerator;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiền đề chung của TC Attribute (docs/testcases/attribute/web/test_cases_attribute_web.md):
 * PC-1 đăng nhập · PC-2 tạo thuộc tính test · PC-3 dọn: giá trị trước, thuộc tính sau, rồi danh mục test.
 * PC-4: bảng không có ô tìm — chỉ đụng dòng có tên chứa mã truy vết của test.
 */
abstract class AttributeTestBase extends BaseTest {

    protected static final String INSERTED = "Attribute has been inserted successfully";
    protected static final String UPDATED = "Attribute has been updated successfully";
    protected static final String DELETED = "Attribute has been deleted successfully";
    protected static final String VALUE_INSERTED = "Attribute value has been inserted successfully";
    protected static final String VALUE_UPDATED = "Attribute value has been updated successfully";
    protected static final String VALUE_DELETED = "Attribute value has been deleted successfully";
    /** Chạy song song 5 luồng — thuộc tính vừa tạo nằm trong vài dòng đầu, không chắc đúng dòng 1. */
    protected static final int TOP_ROWS = 5;

    private static final Logger LOG = LogManager.getLogger(AttributeTestBase.class);
    private final ThreadLocal<List<String>> tokens = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<String>> categoryTokens = ThreadLocal.withInitial(ArrayList::new);

    /** Mã truy vết cho dữ liệu của test — thuộc tính (và giá trị của nó) chứa mã này sẽ bị xoá khi test kết thúc. */
    protected String newToken(String testName) {
        String token = TestDataGenerator.token(testName);
        tokens.get().add(token);
        return token;
    }

    /** Test có tạo danh mục chứa token — xoá cả danh mục khi dọn. */
    protected void alsoCleanCategories(String token) {
        categoryTokens.get().add(token);
    }

    /** PC-1: đăng nhập admin rồi mở All Attributes. */
    protected AttributeListPage openAttributesAsAdmin() {
        loginAsAdmin();
        return Allure.step("Arrange: Mở trang All Attributes", () -> new AttributeListPage(driver()).open());
    }

    /** PC-2: tạo thuộc tính bằng form Add New Attribute. */
    protected void createAttribute(AttributeListPage list, String name) {
        Allure.step("Arrange: Tạo thuộc tính test " + name, () -> {
            list.open();
            list.createAttribute(name);
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Tạo thuộc tính test phải thành công");
        });
    }

    /** Mở danh sách, trả về vị trí dòng mang đúng tên (-1 nếu không có). */
    protected int findRow(AttributeListPage list, String name) {
        list.open();
        return list.rowIndexOf(name);
    }

    protected AttributeEditPage openEditOf(AttributeListPage list, String name) {
        return Allure.step("Arrange: Mở trang sửa của " + name, () -> {
            int row = findRow(list, name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy thuộc tính test " + name);
            return list.editRow(row);
        });
    }

    protected AttributeDetailPage openDetailOf(AttributeListPage list, String name) {
        return Allure.step("Arrange: Mở trang Attribute Detail của " + name, () -> {
            int row = findRow(list, name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy thuộc tính test " + name);
            return list.openValuesOfRow(row);
        });
    }

    @Override
    protected void cleanUpTestData() {
        List<String> attributes = new ArrayList<>(tokens.get());
        List<String> categories = new ArrayList<>(categoryTokens.get());
        tokens.remove();
        categoryTokens.remove();
        dismissAlertIfOpen();
        for (String token : attributes) {
            runCleanup("Dọn dữ liệu: xoá thuộc tính chứa " + token,
                    () -> new AttributeListPage(driver()).deleteAllContaining(token));
        }
        for (String token : categories) {
            runCleanup("Dọn dữ liệu: xoá danh mục chứa " + token,
                    () -> new CategoryListPage(driver()).deleteAllContaining(token));
        }
    }

    private void runCleanup(String title, Runnable action) {
        try {
            Allure.step(title, action::run);
        } catch (RuntimeException e) {
            LOG.warn("{} — không dọn được, cần xoá tay", title, e);
        }
    }

    private void dismissAlertIfOpen() {
        try {
            driver().switchTo().alert().dismiss();
        } catch (NoAlertPresentException e) {
            LOG.debug("Không có hộp thoại cần đóng");
        }
    }
}
