package com.aec.tests;

import com.aec.pages.ProductCreatePage;
import com.aec.pages.ProductCreatePage.SaveButton;
import com.aec.pages.ProductEditPage;
import com.aec.pages.ProductListPage;
import com.aec.utils.TestDataGenerator;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.function.Consumer;

/** Form Add New Product: giá, giảm giá, SKU, thuế, Flash Deal, Shipping, biến thể. TC ở docs/testcases/product/web/parts/part_02_web_tao_gia_bien_the.md. */
@Feature("Product")
@Story("Giá & biến thể")
public class ProductPriceVariantTest extends ProductTestBase {

    private static final String DISCOUNT_LT_10 = "The discount must be less than 10.";

    /** Mở form, nhập dữ liệu PC-2 cho name, chỉnh thêm, bấm Save &amp; Unpublish và chờ trang tải lại. */
    private ProductCreatePage submit(String name, String what, Consumer<ProductCreatePage> customize) {
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();
        Allure.step("Act: Nhập dữ liệu, " + what + ", bấm Save & Unpublish", () -> {
            form.fillRequired(name);
            customize.accept(form);
            form.saveAndWait(SaveButton.UNPUBLISH);
        });
        return form;
    }

    private void assertInserted(ProductCreatePage form) {
        Allure.step("Assert: Thêm thành công", () -> {
            Assert.assertEquals(form.errorMessages(), List.of(), "Không được có khối lỗi");
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
        });
    }

    private void assertRejected(ProductCreatePage form, String message) {
        Allure.step("Assert: Ở lại form, khối đỏ ghi " + message, () -> {
            Assert.assertEquals(form.currentPath(), "/admin/products/create", "Phải ở lại form");
            Assert.assertTrue(form.errorMessages().contains(message), "Khối lỗi: " + form.errorMessages());
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "boundary"}, description = "Unit price âm bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Unit price -1 thì trình duyệt chặn ở ô Unit price, không gửi form.")
    public void testNegativePriceBlocked() {
        tc("AEC_PROD_TC_036", "regression", "product", "auth", "validation", "boundary");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Unit price -1, bấm Save & Unpublish", () -> {
            form.fillRequired("AutoProd_" + newToken("TC036"));
            form.enterUnitPrice("-1");
            form.markPage();
            form.clickSave(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Trình duyệt chặn ở ô Unit price", () -> {
            Assert.assertFalse(form.validationMessageOf("unit_price").isEmpty(), "Phải có bong bóng nhắc");
            Assert.assertTrue(form.isSamePageInstance(), "Không được gửi form");
        });
    }

    @DataProvider(name = "acceptedPrices", parallel = true)
    public Object[][] acceptedPrices() {
        return new Object[][]{
                {"AEC_PROD_TC_037", "Unit price 0.01", "0.01", "0", "Flat"},
                {"AEC_PROD_TC_039", "Discount Flat 9.99", "10", "9.99", "Flat"},
                {"AEC_PROD_TC_042", "Discount Percent 9", "10", "9", "Percent"}};
    }

    @Test(dataProvider = "acceptedPrices", groups = {"regression", "product", "auth", "boundary"},
            description = "Giá và giảm giá sát biên lưu được")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Unit price 0.01 (TC_037), Discount Flat 9.99 với giá 10 (TC_039), Discount Percent 9 với giá 10 (TC_042) đều thêm thành công.")
    public void testAcceptedPrices(String testId, String what, String price, String discount, String type) {
        tc(testId, "regression", "product", "auth", "boundary");
        Allure.parameter("Trường hợp", what);
        String name = "AutoProd_" + newToken("TC" + testId.substring(testId.length() - 3));

        ProductCreatePage form = submit(name, what, f -> {
            f.enterUnitPrice(price);
            f.enterDiscount(discount);
            if (!"Flat".equals(type)) {
                f.chooseDiscountType(type);
            }
        });
        assertInserted(form);
    }

    @DataProvider(name = "flatDiscountTooHigh", parallel = true)
    public Object[][] flatDiscountTooHigh() {
        return new Object[][]{{"a", "10"}, {"b", "20"}};
    }

    @Test(dataProvider = "flatDiscountTooHigh", groups = {"regression", "product", "auth", "validation", "boundary"},
            description = "Discount Flat không nhỏ hơn giá bị từ chối")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra giá 10, Discount Flat bằng giá (10) hoặc lớn hơn (20) thì ở lại form, khối đỏ ghi The discount must be less than 10.")
    public void testFlatDiscountNotLessThanPrice(String variant, String discount) {
        tc("AEC_PROD_TC_038", "regression", "product", "auth", "validation", "boundary");
        Allure.parameter("Biến thể", variant + " — " + discount);
        ProductCreatePage form = submit("AutoProd_" + newToken("TC038" + variant), "Discount Flat " + discount,
                f -> f.enterDiscount(discount));
        assertRejected(form, DISCOUNT_LT_10);
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Giá 0 hiện không tạo được")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Unit price 0, Discount 0 thì ở lại form, khối đỏ ghi The discount must be less than 0. (AMB-PROD-05 — chờ PO quyết có cho giá 0 không).")
    public void testZeroPriceRejected() {
        tc("AEC_PROD_TC_040", "regression", "product", "auth");
        ProductCreatePage form = submit("AutoProd_" + newToken("TC040"), "Unit price 0", f -> f.enterUnitPrice("0"));
        assertRejected(form, "The discount must be less than 0.");
    }

    @DataProvider(name = "percentDiscounts", parallel = true)
    public Object[][] percentDiscounts() {
        return new Object[][]{{"a", "15"}, {"b", "150"}};
    }

    @Test(dataProvider = "percentDiscounts", groups = {"regression", "product", "auth", "validation", "knownIssue"},
            description = "Discount Percent phải so với 100%, không so với giá")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra giá 10, Discount Percent 15 phải thêm thành công; Percent 150 phải báo lỗi không vượt 100%. Đang lệch: cả hai đều báo The discount must be less than 10. (AMB-PROD-04).")
    public void testPercentDiscountComparedTo100(String variant, String discount) {
        tc("AEC_PROD_TC_041", "regression", "product", "auth", "validation", "knownIssue");
        Allure.parameter("Biến thể", variant + " — " + discount + "%");
        ProductCreatePage form = submit("AutoProd_" + newToken("TC041" + variant), "Discount Percent " + discount, f -> {
            f.enterDiscount(discount);
            f.chooseDiscountType("Percent");
        });

        if ("a".equals(variant)) {
            assertInserted(form);
        } else {
            Allure.step("Assert: Báo lỗi giảm giá không vượt 100%", () ->
                    Assert.assertTrue(form.errorMessages().stream().anyMatch(m -> m.contains("100")), "Khối lỗi: " + form.errorMessages()));
        }
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Khoảng ngày giảm giá lưu đúng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Discount 1 với Discount Date Range 02-11-2026 00:00:00 to 10-11-2026 23:59:00 thêm thành công và trang sửa hiện đúng khoảng ngày.")
    public void testDiscountDateRangeSaved() {
        tc("AEC_PROD_TC_043", "regression", "product", "auth");
        String name = "AutoProd_" + newToken("TC043");
        String range = "02-11-2026 00:00:00 to 10-11-2026 23:59:00";
        ProductCreatePage form = submit(name, "Discount 1, chọn khoảng ngày", f -> {
            f.enterDiscount("1");
            f.enterDateRange(range);
        });
        assertInserted(form);

        Allure.step("Assert: Trang sửa hiện đúng khoảng ngày", () ->
                Assert.assertEquals(openEditOf(new ProductListPage(driver()), name, name).fieldValue("date_range"), range, "Discount Date Range"));
    }

    @Test(groups = {"regression", "product", "auth", "validation", "knownIssue"}, description = "Ngày sai định dạng phải báo lỗi, không ra trang lỗi")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Discount Date Range là chữ thường thì báo lỗi ngày, ở lại form, không ra trang Something went wrong!. Đang lệch: ra trang lỗi (AMB-PROD-12).")
    public void testInvalidDateRangeRejected() {
        tc("AEC_PROD_TC_044", "regression", "product", "auth", "validation", "knownIssue");
        ProductCreatePage form = submit("AutoProd_" + newToken("TC044"), "ngày sai định dạng", f -> {
            f.enterDiscount("1");
            f.enterDateRange("khong phai ngay");
        });

        Allure.step("Assert: Không trang lỗi, báo lỗi ngày, ở lại form", () -> {
            Assert.assertFalse(form.isServerErrorPage(), "Không được ra trang Something went wrong!");
            Assert.assertEquals(form.currentPath(), "/admin/products/create", "Phải ở lại form");
            Assert.assertFalse(form.errorMessages().isEmpty(), "Phải có khối lỗi về ngày");
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "knownIssue"}, description = "SKU trùng phải bị từ chối")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tạo sản phẩm thứ hai cùng SKU với sản phẩm thứ nhất thì báo lỗi SKU đã tồn tại, không tạo. Đang lệch: cả hai đều lưu được (AMB-PROD-11).")
    public void testDuplicateSkuRejected() {
        tc("AEC_PROD_TC_045", "regression", "product", "auth", "validation", "knownIssue");
        String token = newToken("TC045");
        String sku = "SKU-" + token;
        String second = "AutoProdB_" + token;
        loginAsAdmin();

        Allure.step("Arrange: Tạo sản phẩm thứ nhất với SKU " + sku, () -> {
            ProductCreatePage form = new ProductCreatePage(driver()).open();
            form.fillRequired("AutoProdA_" + token);
            form.enterField("sku", sku);
            form.saveAndWait(SaveButton.UNPUBLISH);
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Sản phẩm thứ nhất phải tạo được");
        });

        ProductCreatePage form = openCreateForm();
        Allure.step("Act: Tạo sản phẩm thứ hai cùng SKU", () -> {
            form.fillRequired(second);
            form.enterField("sku", sku);
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Báo lỗi SKU, không có sản phẩm thứ hai", () -> {
            Assert.assertFalse(form.errorMessages().isEmpty(), "Phải có khối lỗi SKU đã tồn tại");
            Assert.assertEquals(findRow(new ProductListPage(driver()), second, second), -1, "Không được có sản phẩm thứ hai");
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "boundary", "knownIssue"}, description = "SKU dài hơn 255 ký tự phải bị chặn")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra SKU 256 ký tự thì báo lỗi SKU quá dài, không tạo sản phẩm. Đang lệch: báo thành công và lưu 255 ký tự (AMB-PROD-08). SKU bắt đầu bằng mã truy vết để không trùng giữa các lần chạy.")
    public void testSkuTooLongRejected() {
        tc("AEC_PROD_TC_046", "regression", "product", "auth", "validation", "boundary", "knownIssue");
        String token = newToken("TC046");
        ProductCreatePage form = submit("AutoProd_" + token, "SKU 256 ký tự",
                f -> f.enterField("sku", TestDataGenerator.padTo("SKU" + token, 'S', 256)));

        Allure.step("Assert: Báo lỗi, không tạo sản phẩm", () -> {
            Assert.assertFalse(form.errorMessages().isEmpty(), "Phải có khối lỗi SKU quá dài");
            ProductListPage list = new ProductListPage(driver()).open();
            list.search(token);
            Assert.assertTrue(list.rowIndexesContaining(token).isEmpty(), "Không được có sản phẩm mới");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Tax khác 0 lưu đúng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Tax 5 kiểu Percent thêm thành công và trang sửa hiện Tax 5, kiểu Percent.")
    public void testTaxSaved() {
        tc("AEC_PROD_TC_047", "regression", "product", "auth");
        String name = "AutoProd_" + newToken("TC047");
        ProductCreatePage form = submit(name, "Tax 5 Percent", f -> {
            f.enterField("tax[]", "5");
            f.chooseTaxType("Percent");
        });
        assertInserted(form);

        Allure.step("Assert: Trang sửa hiện Tax 5, Percent", () -> {
            ProductEditPage edit = openEditOf(new ProductListPage(driver()), name, name);
            Assert.assertEquals(Double.parseDouble(edit.fieldValue("tax[]")), 5.0, "Tax");
            Assert.assertEquals(edit.selectedOptionText("tax_type[]"), "Percent", "Kiểu Tax");
        });
    }

    @Test(groups = {"regression", "product", "auth", "ui"}, description = "Khối Flash Deal hiển thị đúng")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra khối Flash Deal có ô Add To Flash với ít nhất một đợt để chọn, ô Discount, ô Discount Type (Flat, Percent). Không lưu (Q14).")
    public void testFlashDealBlock() {
        tc("AEC_PROD_TC_048", "regression", "product", "auth", "ui");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Assert: Khối Flash Deal đủ ô", () -> {
            Assert.assertTrue(form.flashDealCount() > 0, "Ô Add To Flash phải có đợt để chọn");
            Assert.assertTrue(form.hasField("flash_discount"), "Phải có ô Discount");
            Assert.assertTrue(form.flashDiscountTypes().containsAll(List.of("Flat", "Percent")), "Discount Type: " + form.flashDiscountTypes());
        });
    }

    @Test(groups = {"regression", "product", "auth", "ui"}, description = "Khối Shipping Configuration chỉ là dòng chữ")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra khối Shipping Configuration ghi Product wise shipping cost is disable., có liên kết Shipping Configuration, không có ô nhập.")
    public void testShippingConfigurationBlock() {
        tc("AEC_PROD_TC_075", "regression", "product", "auth", "ui");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Assert: Khối chỉ có dòng chữ và liên kết", () -> {
            Assert.assertTrue(form.shippingConfigurationText().contains("Product wise shipping cost is disable."), "Nội dung: " + form.shippingConfigurationText());
            Assert.assertTrue(form.shippingConfigurationHasLink(), "Phải có liên kết Shipping Configuration");
            Assert.assertFalse(form.shippingConfigurationHasInputs(), "Không được có ô nhập");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Ô màu khoá tới khi bật công tắc Colors")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ô chọn màu không mở được khi công tắc Colors tắt; bật công tắc thì ô mở ra và liệt kê màu kèm ô mẫu.")
    public void testColorSelectLockedUntilSwitchOn() {
        tc("AEC_PROD_TC_049", "regression", "product", "auth");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Assert: Ô chọn màu bị khoá", () ->
                Assert.assertFalse(form.tryOpenColorSelect(), "Ô chọn màu không được mở ra"));
        Allure.step("Act: Bật công tắc Colors", form::enableColors);
        Allure.step("Assert: Ô mở ra, mọi màu có ô mẫu", () ->
                Assert.assertTrue(form.colorListShowsSwatches(), "Danh sách màu phải có ô mẫu ở mọi mục"));
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Chọn thuộc tính sinh ô giá trị")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra chọn thuộc tính Metal ở Product Variation thì hiện 1 dòng mang tên Metal với ô chọn nhiều giá trị. Không lưu.")
    public void testAttributeAddsValueRow() {
        tc("AEC_PROD_TC_050", "regression", "product", "auth");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Chọn thuộc tính Metal", () -> form.chooseAttribute("Metal"));

        Allure.step("Assert: 1 dòng Metal với ô chọn nhiều giá trị", () -> {
            Assert.assertEquals(form.attributeChoiceTitles(), List.of("Metal"), "Dòng thuộc tính");
            Assert.assertTrue(form.choiceValueSelectIsMultiple(), "Ô giá trị phải cho chọn nhiều");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Sinh bảng tổ hợp biến thể")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bật Colors chọn 2 màu, chọn Metal với 2 giá trị khác tên thì bảng tổ hợp có cột Variant, Variant Price, SKU, Quantity, Photo, đủ 4 dòng, Quantity mặc định 10, ô Quantity và SKU chung bị ẩn. Không lưu.")
    public void testVariantCombinationTable() {
        tc("AEC_PROD_TC_051", "regression", "product", "auth");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Chọn 2 màu và 2 giá trị Metal", () -> {
            form.enableColors();
            form.chooseFirstColors(2);
            form.chooseAttribute("Metal");
            form.chooseFirstDistinctValues(2);
            form.waitForVariants(4);
        });

        Allure.step("Assert: Bảng tổ hợp 4 dòng, ô chung bị ẩn", () -> {
            Assert.assertEquals(form.variantHeaders(), List.of("Variant", "Variant Price", "SKU", "Quantity", "Photo"), "Cột bảng tổ hợp");
            Assert.assertEquals(form.variantNames().size(), 4, "Số dòng tổ hợp");
            Assert.assertEquals(form.variantQuantities(), List.of("10", "10", "10", "10"), "Quantity mặc định");
            Assert.assertFalse(form.isFieldGroupVisible("current_stock"), "Ô Quantity chung phải ẩn");
            Assert.assertFalse(form.isFieldGroupVisible("sku"), "Ô SKU chung phải ẩn");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Lưu sản phẩm có nhiều tổ hợp")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra sản phẩm 2 màu × 2 giá trị Metal khác tên, giá 10–13 lưu thành công; trang sửa có đủ 4 dòng tổ hợp, không trùng, giá đúng. Ảnh từng tổ hợp kiểm tay (Partial).")
    public void testSaveProductWithVariants() {
        tc("AEC_PROD_TC_052", "regression", "product", "auth");
        String name = "AutoProd_" + newToken("TC052");
        List<String> prices = List.of("10", "11", "12", "13");
        ProductCreatePage form = submit(name, "2 màu × 2 giá trị Metal, giá từng dòng", f -> {
            f.enableColors();
            f.chooseFirstColors(2);
            f.chooseAttribute("Metal");
            Allure.parameter("Giá trị Metal", String.join(", ", f.chooseFirstDistinctValues(2)));
            f.waitForVariants(4);
            f.enterVariantPrices(prices);
        });
        assertInserted(form);

        Allure.step("Assert: Trang sửa có 4 tổ hợp khác nhau, giá đúng", () -> {
            ProductEditPage edit = openEditOf(new ProductListPage(driver()), name, name);
            List<String> variants = edit.variantNames();
            Assert.assertEquals(variants.size(), 4, "Số dòng tổ hợp: " + variants);
            Assert.assertEquals(variants.stream().distinct().count(), 4L, "Không được có dòng trùng: " + variants);
            Assert.assertEquals(edit.variantPrices().stream().map(Double::parseDouble).toList(),
                    prices.stream().map(Double::parseDouble).toList(), "Giá từng dòng");
        });
    }
}
