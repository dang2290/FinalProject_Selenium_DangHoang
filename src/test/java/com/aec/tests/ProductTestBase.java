package com.aec.tests;

import com.aec.base.BaseTest;
import com.aec.pages.BrandPage;
import com.aec.pages.CategoryListPage;
import com.aec.pages.DigitalProductFormPage;
import com.aec.pages.DigitalProductListPage;
import com.aec.pages.ProductCreatePage;
import com.aec.pages.ProductCreatePage.SaveButton;
import com.aec.pages.ProductEditPage;
import com.aec.pages.ProductListPage;
import com.aec.utils.TestDataGenerator;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiền đề chung của TC Product (docs/testcases/product/web/parts/):
 * PC-1 đăng nhập · PC-2 tạo sản phẩm test (Save &amp; Unpublish) · PC-3 dọn · PC-5 tạo sản phẩm số test.
 * PC-4: chỉ đụng sản phẩm có tên chứa mã truy vết của test; sản phẩm có thể bị lưu tên rỗng thì ghi id để dọn.
 */
abstract class ProductTestBase extends BaseTest {

    protected static final String INSERTED = "Product has been inserted successfully";
    protected static final String UPDATED = "Product has been updated successfully";
    protected static final String DELETED = "Product has been deleted successfully";
    protected static final String DUPLICATED = "Product has been duplicated successfully";
    protected static final String DIGITAL_INSERTED = "Digital Product has been inserted successfully";
    protected static final String DIGITAL_UPDATED = "Digital Product has been updated successfully";
    protected static final String NAME_REQUIRED = "Product name is required";
    /** Chạy song song 5 luồng — sản phẩm vừa tạo chỉ chắc nằm trong vài dòng đầu. */
    protected static final int TOP_ROWS = 5;

    private static final Logger LOG = LogManager.getLogger(ProductTestBase.class);
    private final ThreadLocal<List<String>> tokens = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<String>> digitalTokens = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<String>> productIds = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<String>> digitalIds = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<String>> categoryTokens = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<String>> brandTokens = ThreadLocal.withInitial(ArrayList::new);

    /** Luồng liên module (PC-6): test có tạo danh mục, brand chứa token — dọn sau sản phẩm, theo thứ tự danh mục → brand. */
    protected void alsoCleanCategoryAndBrand(String token) {
        categoryTokens.get().add(token);
        brandTokens.get().add(token);
    }

    /** Mã truy vết cho sản phẩm thường — sản phẩm có tên chứa mã này bị xoá khi test kết thúc. */
    protected String newToken(String testName) {
        String token = TestDataGenerator.token(testName);
        tokens.get().add(token);
        return token;
    }

    /** Mã truy vết cho sản phẩm số — dọn ở danh sách Digital Products. */
    protected String newDigitalToken(String testName) {
        String token = TestDataGenerator.token(testName);
        digitalTokens.get().add(token);
        return token;
    }

    /** Sản phẩm có thể bị lưu tên rỗng — khi dọn đổi tên về mã truy vết rồi xoá. */
    protected void trackProductId(String id) {
        productIds.get().add(id);
    }

    protected void trackDigitalId(String id) {
        digitalIds.get().add(id);
    }

    protected ProductListPage openProductsAsAdmin() {
        loginAsAdmin();
        return Allure.step("Arrange: Mở All products", () -> new ProductListPage(driver()).open());
    }

    protected ProductCreatePage openCreateForm() {
        return Allure.step("Arrange: Mở Add New Product", () -> new ProductCreatePage(driver()).open());
    }

    /** PC-2: tạo sản phẩm với dữ liệu tối thiểu, lưu bằng nút đã chọn. */
    protected void createProduct(String name, SaveButton button) {
        Allure.step("Arrange: Tạo sản phẩm test " + name, () -> {
            ProductCreatePage form = new ProductCreatePage(driver()).open();
            form.fillRequired(name);
            form.saveAndWait(button);
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Tạo sản phẩm test phải thành công");
        });
    }

    protected void createProduct(String name) {
        createProduct(name, SaveButton.UNPUBLISH);
    }

    /** PC-5: tạo sản phẩm số với dữ liệu tối thiểu. */
    protected void createDigital(String name) {
        Allure.step("Arrange: Tạo sản phẩm số test " + name, () -> {
            DigitalProductFormPage form = new DigitalProductFormPage(driver()).openCreate();
            form.fillRequired(name, "10");
            form.submitAndWait();
            Assert.assertEquals(form.getNotifyMessage(), DIGITAL_INSERTED, "Tạo sản phẩm số test phải thành công");
        });
    }

    /** Mở All products, tìm theo từ khoá, trả về vị trí dòng mang đúng tên (-1 nếu không có). */
    protected int findRow(ProductListPage list, String keyword, String name) {
        list.open();
        list.search(keyword);
        return list.rowIndexOf(name);
    }

    protected ProductEditPage openEditOf(ProductListPage list, String keyword, String name) {
        return Allure.step("Arrange: Mở trang sửa của " + name, () -> {
            int row = findRow(list, keyword, name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy sản phẩm test " + name);
            return list.editRow(row);
        });
    }

    protected DigitalProductFormPage openDigitalEditOf(String keyword, String name) {
        return Allure.step("Arrange: Mở trang sửa sản phẩm số " + name, () -> {
            DigitalProductListPage list = new DigitalProductListPage(driver()).open();
            list.search(keyword);
            int row = list.rowIndexOf(name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy sản phẩm số test " + name);
            return list.editRow(row);
        });
    }

    @Override
    protected void cleanUpTestData() {
        List<String> physical = new ArrayList<>(tokens.get());
        List<String> digital = new ArrayList<>(digitalTokens.get());
        List<String> ids = new ArrayList<>(productIds.get());
        List<String> dIds = new ArrayList<>(digitalIds.get());
        List<String> categories = new ArrayList<>(categoryTokens.get());
        List<String> brands = new ArrayList<>(brandTokens.get());
        categoryTokens.remove();
        brandTokens.remove();
        tokens.remove();
        digitalTokens.remove();
        productIds.remove();
        digitalIds.remove();
        dismissAlertIfOpen();
        for (String id : ids) {
            String token = TestDataGenerator.token("Del");
            physical.add(token);
            runCleanup("Dọn dữ liệu: đổi tên sản phẩm id " + id + " thành " + token, () -> renameProduct(id, token));
        }
        for (String id : dIds) {
            String token = TestDataGenerator.token("Del");
            digital.add(token);
            runCleanup("Dọn dữ liệu: đổi tên sản phẩm số id " + id + " thành " + token, () -> renameDigital(id, token));
        }
        for (String token : digital) {
            runCleanup("Dọn dữ liệu: xoá sản phẩm số chứa " + token,
                    () -> new DigitalProductListPage(driver()).deleteAllContaining(token));
        }
        for (String token : physical) {
            runCleanup("Dọn dữ liệu: xoá sản phẩm chứa " + token,
                    () -> new ProductListPage(driver()).deleteAllContaining(token));
        }
        // Xoá sau sản phẩm: không để sản phẩm trỏ vào danh mục / brand đã xoá
        for (String token : categories) {
            runCleanup("Dọn dữ liệu: xoá danh mục chứa " + token,
                    () -> new CategoryListPage(driver()).deleteAllContaining(token));
        }
        for (String token : brands) {
            runCleanup("Dọn dữ liệu: xoá brand chứa " + token,
                    () -> new BrandPage(driver()).deleteAllContaining(token));
        }
    }

    private void renameProduct(String id, String token) {
        driver().get(config.getBaseUrl() + "/admin/products/admin/" + id + "/edit?lang=en");
        ProductEditPage edit = new ProductEditPage(driver());
        if (edit.isPageNotFound() || !edit.isTitleDisplayed()) {
            return;
        }
        edit.enterName(token);
        edit.updateAndWait();
    }

    private void renameDigital(String id, String token) {
        driver().get(config.getBaseUrl() + "/admin/digitalproducts/edit/" + id + "?lang=en");
        DigitalProductFormPage edit = new DigitalProductFormPage(driver());
        if (edit.isPageNotFound() || !edit.isEditTitleDisplayed()) {
            return;
        }
        edit.enterName(token);
        edit.submitAndWait();
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
