package com.danghoang.tests;

import com.danghoang.pages.BrandPage;
import com.danghoang.pages.CategoryListPage;
import com.danghoang.pages.DigitalProductFormPage;
import com.danghoang.pages.DigitalProductListPage;
import com.danghoang.pages.ProductCreatePage;
import com.danghoang.pages.ProductCreatePage.SaveButton;
import com.danghoang.pages.ProductEditPage;
import com.danghoang.pages.ProductListPage;
import com.danghoang.utils.TestDataGenerator;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiền đề chung của TC Product (docs/testcases/product/web/parts/):
 * PC-1 đăng nhập · PC-2 tạo sản phẩm test (Save &amp; Unpublish) · PC-3 dọn · PC-5 tạo sản phẩm số test.
 * PC-4: chỉ đụng sản phẩm có tên chứa mã truy vết của test; sản phẩm có thể bị lưu tên rỗng thì ghi id để dọn.
 */
abstract class ProductTestBase extends TrackedDataTestBase {

    protected static final String INSERTED = "Product has been inserted successfully";
    protected static final String UPDATED = "Product has been updated successfully";
    protected static final String DELETED = "Product has been deleted successfully";
    protected static final String DUPLICATED = "Product has been duplicated successfully";
    protected static final String DIGITAL_INSERTED = "Digital Product has been inserted successfully";
    protected static final String DIGITAL_UPDATED = "Digital Product has been updated successfully";
    protected static final String NAME_REQUIRED = "Product name is required";
    /** Chạy song song 5 luồng — sản phẩm vừa tạo chỉ chắc nằm trong vài dòng đầu. */
    protected static final int TOP_ROWS = 5;
    /** Các khối của form Add New Product, đúng thứ tự trên trang. */
    protected static final List<String> PRODUCT_FORM_SECTIONS = List.of("Product Information", "Product Images", "Product Videos",
            "Product Variation", "Product price + stock", "Product Description", "PDF Specification", "SEO Meta Tags",
            "Shipping Configuration", "Low Stock Quantity Warning", "Stock Visibility State", "Cash on Delivery", "Featured",
            "Todays Deal", "Flash Deal", "Estimate Shipping Time", "Vat & TAX");

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
        return arrange("Mở All products", () -> new ProductListPage().open());
    }

    protected ProductCreatePage openCreateForm() {
        return arrange("Mở Add New Product", () -> new ProductCreatePage().open());
    }

    /** PC-2: tạo sản phẩm với dữ liệu tối thiểu, lưu bằng nút đã chọn. */
    protected ProductListPage createProduct(String name, SaveButton button) {
        return arrange("Tạo sản phẩm test " + name, () -> {
            ProductListPage created = new ProductCreatePage().open().createProduct(name, button);
            Assert.assertEquals(created.getNotifyMessage(), INSERTED, "Tạo sản phẩm test phải thành công");
            return created;
        });
    }

    protected ProductListPage createProduct(String name) {
        return createProduct(name, SaveButton.UNPUBLISH);
    }

    /** PC-5: tạo sản phẩm số với dữ liệu tối thiểu. */
    protected DigitalProductListPage createDigital(String name) {
        return arrange("Tạo sản phẩm số test " + name, () -> {
            DigitalProductListPage created = new DigitalProductFormPage().openCreate().createProduct(name, "10");
            Assert.assertEquals(created.getNotifyMessage(), DIGITAL_INSERTED, "Tạo sản phẩm số test phải thành công");
            return created;
        });
    }

    protected ProductEditPage openEditOf(ProductListPage list, String keyword, String name) {
        return arrange("Mở trang sửa của " + name, () -> {
            int row = list.findRow(keyword, name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy sản phẩm test " + name);
            return list.editRow(row);
        });
    }

    protected DigitalProductFormPage openDigitalEditOf(String keyword, String name) {
        return arrange("Mở trang sửa sản phẩm số " + name, () -> {
            DigitalProductListPage list = new DigitalProductListPage();
            int row = list.findRow(keyword, name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy sản phẩm số test " + name);
            return list.editRow(row);
        });
    }

    @Override
    protected void cleanUpTestData() {
        List<String> physical = takeTokens();
        List<String> digital = takeAll(digitalTokens);
        List<String> ids = takeAll(productIds);
        List<String> dIds = takeAll(digitalIds);
        List<String> categories = takeAll(categoryTokens);
        List<String> brands = takeAll(brandTokens);
        dismissAlertIfOpen();
        for (String id : ids) {
            String token = TestDataGenerator.token("Del");
            physical.add(token);
            runCleanup("đổi tên sản phẩm id " + id + " thành " + token, () -> renameProduct(id, token));
        }
        for (String id : dIds) {
            String token = TestDataGenerator.token("Del");
            digital.add(token);
            runCleanup("đổi tên sản phẩm số id " + id + " thành " + token, () -> renameDigital(id, token));
        }
        for (String token : digital) {
            runCleanup("xoá sản phẩm số chứa " + token,
                    () -> new DigitalProductListPage().deleteAllContaining(token));
        }
        for (String token : physical) {
            runCleanup("xoá sản phẩm chứa " + token,
                    () -> new ProductListPage().deleteAllContaining(token));
        }
        // Xoá sau sản phẩm: không để sản phẩm trỏ vào danh mục / brand đã xoá
        for (String token : categories) {
            runCleanup("xoá danh mục chứa " + token,
                    () -> new CategoryListPage().deleteAllContaining(token));
        }
        for (String token : brands) {
            runCleanup("xoá brand chứa " + token,
                    () -> new BrandPage().deleteAllContaining(token));
        }
    }

    private void renameProduct(String id, String token) {
        browser().openPath("/admin/products/admin/" + id + "/edit?lang=en");
        ProductEditPage edit = new ProductEditPage();
        if (edit.isPageNotFound() || !edit.isTitleDisplayed()) {
            return;
        }
        edit.saveWithName(token);
    }

    private void renameDigital(String id, String token) {
        browser().openPath("/admin/digitalproducts/edit/" + id + "?lang=en");
        DigitalProductFormPage edit = new DigitalProductFormPage();
        if (edit.isPageNotFound() || !edit.isEditTitleDisplayed()) {
            return;
        }
        edit.saveWithName(token);
    }
}
