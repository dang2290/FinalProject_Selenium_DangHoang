package com.aec.tests;

import com.aec.pages.BrandPage;
import com.aec.pages.CategoryFormPage;
import com.aec.pages.ProductCreatePage;
import com.aec.pages.ProductCreatePage.SaveButton;
import com.aec.pages.ProductEditPage;
import com.aec.pages.ProductListPage;
import com.aec.pages.ProductListPage.RowSwitch;
import com.aec.pages.StorefrontListingPage;
import com.aec.pages.StorefrontProductPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Luồng liên module: đăng nhập → tạo Brand → tạo Category → tạo sản phẩm dùng Brand, Category vừa tạo (TC_076),
 * và đối chiếu chi tiết sản phẩm ở Admin với trang sản phẩm ngoài cửa hàng (TC_077).
 * Dọn theo PC-6: sản phẩm → danh mục → brand. TC ở docs/testcases/product/web/parts/part_03_web_sua_digital_bulk.md.
 */
@Feature("Product")
@Story("Luồng liên module Brand → Category → Product → cửa hàng")
public class ProductCrossModuleTest extends ProductTestBase {

    private static final String BRAND_INSERTED = "Brand has been inserted successfully";
    private static final String CATEGORY_INSERTED = "Category has been inserted successfully";

    /** Dữ liệu một lượt luồng: brand, danh mục, sản phẩm cùng mang một mã truy vết. */
    private record FlowData(String token, String brand, String category, String product, String description) {
        static FlowData of(String token) {
            return new FlowData(token, "AutoBrand_" + token, "AutoCat_" + token, "AutoProd_" + token, "Mô tả " + token);
        }
    }

    /** Bước 2–4 của TC_076: tạo Brand, Category (Physical), rồi sản phẩm chọn đúng hai mục đó và bấm Save &amp; Publish. */
    private void createBrandCategoryProduct(FlowData data) {
        Allure.step("Act: Tạo brand " + data.brand(), () -> {
            BrandPage brands = new BrandPage(driver()).open();
            brands.createBrand(data.brand());
            Assert.assertEquals(brands.getNotifyMessage(), BRAND_INSERTED, "Thông báo sau khi tạo brand");
        });
        Allure.step("Act: Tạo danh mục " + data.category() + " (Physical)", () -> {
            CategoryFormPage category = new CategoryFormPage(driver()).openCreate();
            category.enterName(data.category());
            Assert.assertEquals(category.typeShown(), "Physical", "Type mặc định của danh mục");
            category.saveAndWait();
            Assert.assertEquals(category.getNotifyMessage(), CATEGORY_INSERTED, "Thông báo sau khi tạo danh mục");
        });
        Allure.step("Act: Tạo sản phẩm dùng Brand, Category vừa tạo, bấm Save & Publish", () -> {
            ProductCreatePage form = new ProductCreatePage(driver()).open();
            form.chooseCategory(data.category());
            form.chooseBrand(data.brand());
            form.enterName(data.product());
            form.enterUnit("pc");
            form.enterField("min_qty", "2");
            form.addTag("auto");
            form.enterUnitPrice("100");
            form.enterDiscount("10");
            form.enterQuantity("20");
            form.enterDescription(data.description());
            form.saveAndWait(SaveButton.PUBLISH);
            Assert.assertEquals(form.errorMessages(), java.util.List.of(), "Không được có khối lỗi");
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi tạo sản phẩm");
        });
    }

    @Test(groups = {"regression", "product", "auth", "e2e", "crossModule"},
            description = "Luồng tạo trọn vẹn: đăng nhập → tạo Brand → tạo Category → tạo sản phẩm dùng Brand, Category vừa tạo")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra đăng nhập admin, tạo brand test, tạo danh mục test (Physical), tạo sản phẩm chọn đúng brand và danh mục đó (giá 100, giảm 10 Flat, kho 20, Min Qty 2) bằng Save & Publish; All products có đúng 1 dòng Base Price $100.00, Total Stock 20, Published bật; trang sửa hiện đúng Category, Brand đã chọn. Dọn: sản phẩm → danh mục → brand.")
    public void testBrandCategoryProductFlow() {
        tc("AEC_PROD_TC_076", "regression", "product", "auth", "e2e", "crossModule");
        FlowData data = FlowData.of(newToken("TC076"));
        alsoCleanCategoryAndBrand(data.token());
        loginAsAdmin();

        createBrandCategoryProduct(data);

        ProductListPage list = new ProductListPage(driver());
        Allure.step("Assert: All products có đúng 1 dòng, giá, kho, Published đúng", () -> {
            list.open();
            list.search(data.product());
            Assert.assertEquals(list.names(), java.util.List.of(data.product()), "Kết quả tìm");
            Assert.assertEquals(list.basePriceOfRow(0), "$100.00", "Base Price");
            Assert.assertEquals(list.totalStockOfRow(0), "20", "Total Stock");
            Assert.assertTrue(list.isSwitchOn(0, RowSwitch.PUBLISHED), "Published phải bật");
        });
        Allure.step("Assert: Trang sửa hiện đúng Category, Brand vừa tạo", () -> {
            ProductEditPage edit = list.editRow(0);
            Assert.assertEquals(edit.selectedOptionText("category_id").replaceFirst("^-+\\s*", ""), data.category(), "Category");
            Assert.assertEquals(edit.selectedOptionText("brand_id"), data.brand(), "Brand");
        });
    }

    @Test(groups = {"regression", "product", "auth", "e2e", "crossModule"},
            description = "Chi tiết sản phẩm ở Admin khớp với trang sản phẩm ngoài cửa hàng")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra sản phẩm vừa tạo qua luồng Brand → Category → Product (Save & Publish): bấm View mở /product/<slug>; tiêu đề tab bằng Meta Title, tên, brand, giá gốc gạch ngang $100.00 /pc, giá giảm $90.00 /pc, (20 available), ô số lượng mặc định 2 (= Min Qty, chưa khảo sát), Description khớp với trang sửa; trang brand và trang danh mục ngoài cửa hàng liệt kê sản phẩm. Trang sản phẩm không hiện Category, Tags — đối chiếu Category qua trang danh mục. Mọi chỗ lệch được gom báo cùng lúc.")
    public void testAdminDetailMatchesStorefront() {
        tc("AEC_PROD_TC_077", "regression", "product", "auth", "e2e", "crossModule");
        FlowData data = FlowData.of(newToken("TC077"));
        alsoCleanCategoryAndBrand(data.token());
        loginAsAdmin();
        createBrandCategoryProduct(data);

        ProductListPage list = new ProductListPage(driver());
        String[] admin = Allure.step("Arrange: Mở trang sửa, ghi lại Name, Meta Title, Brand", () -> {
            list.open();
            list.search(data.product());
            ProductEditPage edit = list.editRow(list.rowIndexOf(data.product()));
            return new String[]{edit.nameValue(), edit.fieldValue("meta_title"), edit.selectedOptionText("brand_id")};
        });

        String adminTab = driver().getWindowHandle();
        StorefrontProductPage storefront = Allure.step("Act: Ở All products bấm View", () -> {
            list.open();
            list.search(data.product());
            list.clickRowButtonAt(list.rowIndexOf(data.product()), 0);
            list.switchToNewWindow(adminTab);
            return new StorefrontProductPage(driver()).waitUntilLoaded();
        });

        Allure.step("Assert: Khối thông tin sản phẩm khớp với Admin", () -> {
            SoftAssert soft = new SoftAssert();
            soft.assertTrue(storefront.currentPath().startsWith("/product/"), "Đường dẫn: " + storefront.currentUrl());
            soft.assertEquals(storefront.browserTitle(), admin[1], "Tiêu đề tab = Meta Title");
            soft.assertEquals(storefront.productName(), admin[0], "Tên sản phẩm");
            soft.assertEquals(storefront.brandName(), admin[2], "Brand (chú thích logo)");
            soft.assertTrue(storefront.isOriginalPriceStruckThrough(), "Giá gốc phải gạch ngang khi có giảm giá");
            soft.assertEquals(storefront.rowValue("Price:"), "$100.00 /pc", "Price");
            soft.assertEquals(storefront.rowValue("Discount Price:"), "$90.00 /pc", "Discount Price");
            soft.assertEquals(storefront.availableQuantity(), "20", "Số lượng còn (available)");
            soft.assertEquals(storefront.quantityValue(), "2", "Ô số lượng mặc định = Min Qty");
            soft.assertTrue(storefront.descriptionText().contains(data.description()), "Tab Description phải có: " + data.description());
            soft.assertAll();
        });

        Allure.step("Assert: Trang brand ngoài cửa hàng liệt kê sản phẩm", () -> {
            StorefrontListingPage brandPage = storefront.clickBrand();
            Assert.assertTrue(brandPage.listsProduct(data.product()), "Trang brand phải có " + data.product());
        });
        Allure.step("Assert: Trang danh mục ngoài cửa hàng liệt kê sản phẩm", () -> {
            StorefrontListingPage categoryPage = new StorefrontListingPage(driver()).openAllCategories().clickCategory(data.category());
            Assert.assertTrue(categoryPage.listsProduct(data.product()), "Trang danh mục phải có " + data.product());
        });

        driver().close();
        driver().switchTo().window(adminTab);
    }
}
