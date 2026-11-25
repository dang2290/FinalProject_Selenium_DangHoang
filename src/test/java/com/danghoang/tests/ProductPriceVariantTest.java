package com.danghoang.tests;

import com.danghoang.base.StepNames;
import com.danghoang.pages.FormField;
import com.danghoang.utils.EnvReferenceData;
import com.danghoang.pages.ProductCreatePage;
import com.danghoang.pages.ProductCreatePage.SaveButton;
import com.danghoang.pages.ProductEditPage;
import com.danghoang.pages.ProductListPage;
import com.danghoang.utils.TestDataGenerator;
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

    private static final String ATTRIBUTE = EnvReferenceData.get("variantAttribute");

    private static final String DISCOUNT_LT_10 = "The discount must be less than 10.";

    /** Mở form, nhập dữ liệu PC-2 cho name, chỉnh thêm, bấm Save &amp; Unpublish và chờ trang tải lại. */
    private ProductCreatePage submit(String name, String what, Consumer<ProductCreatePage> customize) {
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();
        act("Nhập dữ liệu, " + what + ", bấm Save & Unpublish", () -> {
            form.fillRequired(name);
            customize.accept(form);
            form.saveAndWait(SaveButton.UNPUBLISH);
        });
        return form;
    }

    private void assertInserted(ProductCreatePage form) {
        verify(StepNames.CREATED, () -> {
            Assert.assertEquals(form.errorMessages(), List.of(), "Không được có khối lỗi");
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
        });
    }

    private void assertRejected(ProductCreatePage form, String message) {
        verify("Ở lại form, khối đỏ ghi " + message, () -> {
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

        act("Unit price -1 rồi bấm Save & Unpublish", () -> {
            form.fillRequired("AutoProd_" + newToken("TC036"));
            form.enterUnitPrice("-1");
            form.clickSave(SaveButton.UNPUBLISH);
        });

        verify("Trình duyệt chặn ở ô Unit price", () -> {
            Assert.assertFalse(form.validationMessageOf(FormField.UNIT_PRICE).isEmpty(), "Phải có bong bóng nhắc");
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
                f.pricing().chooseDiscountType(type);
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
            f.pricing().chooseDiscountType("Percent");
        });

        if ("a".equals(variant)) {
            assertInserted(form);
        } else {
            verify("Báo lỗi giảm giá không vượt 100%", () ->
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
            f.pricing().enterDateRange(range);
        });
        assertInserted(form);

        verify("Trang sửa hiện đúng khoảng ngày", () ->
                Assert.assertEquals(openEditOf(new ProductListPage(), name, name).fieldValue(FormField.DATE_RANGE), range, "Discount Date Range"));
    }

    @Test(groups = {"regression", "product", "auth", "validation", "knownIssue"}, description = "Ngày sai định dạng phải báo lỗi, không ra trang lỗi")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Discount Date Range là chữ thường thì báo lỗi ngày, ở lại form, không ra trang Something went wrong!. Đang lệch: ra trang lỗi (AMB-PROD-12).")
    public void testInvalidDateRangeRejected() {
        tc("AEC_PROD_TC_044", "regression", "product", "auth", "validation", "knownIssue");
        ProductCreatePage form = submit("AutoProd_" + newToken("TC044"), "ngày sai định dạng", f -> {
            f.enterDiscount("1");
            f.pricing().enterDateRange("khong phai ngay");
        });

        verify("Không trang lỗi, báo lỗi ngày, ở lại form", () -> {
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

        arrange("Tạo sản phẩm thứ nhất với SKU " + sku, () -> {
            ProductCreatePage form = new ProductCreatePage().open();
            form.fillRequired("AutoProdA_" + token);
            form.enterField(FormField.SKU, sku);
            form.saveAndWait(SaveButton.UNPUBLISH);
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Sản phẩm thứ nhất phải tạo được");
        });

        ProductCreatePage form = openCreateForm();
        act("Tạo sản phẩm thứ hai cùng SKU", () -> {
            form.fillRequired(second);
            form.enterField(FormField.SKU, sku);
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        verify("Báo lỗi SKU, không có sản phẩm thứ hai", () -> {
            Assert.assertFalse(form.errorMessages().isEmpty(), "Phải có khối lỗi SKU đã tồn tại");
            Assert.assertEquals(new ProductListPage().findRow(second), -1, "Không được có sản phẩm thứ hai");
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "boundary", "knownIssue"}, description = "SKU dài hơn 255 ký tự phải bị chặn")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra SKU 256 ký tự thì báo lỗi SKU quá dài, không tạo sản phẩm. Đang lệch: báo thành công và lưu 255 ký tự (AMB-PROD-08). SKU bắt đầu bằng mã truy vết để không trùng giữa các lần chạy.")
    public void testSkuTooLongRejected() {
        tc("AEC_PROD_TC_046", "regression", "product", "auth", "validation", "boundary", "knownIssue");
        String token = newToken("TC046");
        ProductCreatePage form = submit("AutoProd_" + token, "SKU 256 ký tự",
                f -> f.enterField(FormField.SKU, TestDataGenerator.padTo("SKU" + token, 'S', 256)));

        verify(StepNames.REJECTED_NO_PRODUCT, () -> {
            Assert.assertFalse(form.errorMessages().isEmpty(), "Phải có khối lỗi SKU quá dài");
            ProductListPage list = new ProductListPage();
            list.searchFor(token);
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
            f.enterField(FormField.TAX, "5");
            f.pricing().chooseTaxType("Percent");
        });
        assertInserted(form);

        verify("Trang sửa hiện Tax 5, Percent", () -> {
            ProductEditPage edit = openEditOf(new ProductListPage(), name, name);
            Assert.assertEquals(Double.parseDouble(edit.fieldValue(FormField.TAX)), 5.0, "Tax");
            Assert.assertEquals(edit.selectedOptionText(FormField.TAX_TYPE), "Percent", "Kiểu Tax");
        });
    }

    @Test(groups = {"regression", "product", "auth", "ui"}, description = "Khối Flash Deal hiển thị đúng")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra khối Flash Deal có ô Add To Flash với ít nhất một đợt để chọn, ô Discount, ô Discount Type (Flat, Percent). Không lưu (Q14).")
    public void testFlashDealBlock() {
        tc("AEC_PROD_TC_048", "regression", "product", "auth", "ui");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        verify("Khối Flash Deal đủ ô", () -> {
            Assert.assertTrue(form.pricing().flashDealCount() > 0, "Ô Add To Flash phải có đợt để chọn");
            Assert.assertTrue(form.hasField(FormField.FLASH_DISCOUNT), "Phải có ô Discount");
            Assert.assertTrue(form.pricing().flashDiscountTypes().containsAll(List.of("Flat", "Percent")), "Discount Type: " + form.pricing().flashDiscountTypes());
        });
    }

    @Test(groups = {"regression", "product", "auth", "ui"}, description = "Khối Shipping Configuration chỉ là dòng chữ")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra khối Shipping Configuration ghi Product wise shipping cost is disable., có liên kết Shipping Configuration, không có ô nhập.")
    public void testShippingConfigurationBlock() {
        tc("AEC_PROD_TC_075", "regression", "product", "auth", "ui");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        verify("Khối chỉ có dòng chữ và liên kết", () -> {
            Assert.assertTrue(form.shipping().shippingConfigurationText().contains("Product wise shipping cost is disable."), "Nội dung: " + form.shipping().shippingConfigurationText());
            Assert.assertTrue(form.shipping().shippingConfigurationHasLink(), "Phải có liên kết Shipping Configuration");
            Assert.assertFalse(form.shipping().shippingConfigurationHasInputs(), "Không được có ô nhập");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Ô màu khoá tới khi bật công tắc Colors")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ô chọn màu không mở được khi công tắc Colors tắt; bật công tắc thì ô mở ra và liệt kê màu kèm ô mẫu.")
    public void testColorSelectLockedUntilSwitchOn() {
        tc("AEC_PROD_TC_049", "regression", "product", "auth");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        verify("Ô chọn màu bị khoá", () -> {
            Assert.assertTrue(form.variation().isColorSelectDisabled(), "Ô chọn màu phải ở trạng thái disabled khi công tắc Colors tắt");
            Assert.assertFalse(form.variation().tryOpenColorSelect(), "Ô chọn màu không được mở ra");
        });
        act("Bật công tắc Colors", form.variation()::enableColors);
        verify("Ô mở ra, mọi màu có ô mẫu", () -> {
            Assert.assertFalse(form.variation().isColorSelectDisabled(), "Bật công tắc thì ô chọn màu phải hết disabled");
            Assert.assertTrue(form.variation().colorListShowsSwatches(), "Danh sách màu phải có ô mẫu ở mọi mục");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Chọn thuộc tính sinh ô giá trị")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra chọn thuộc tính Metal ở Product Variation thì hiện 1 dòng mang tên Metal với ô chọn nhiều giá trị. Không lưu.")
    public void testAttributeAddsValueRow() {
        tc("AEC_PROD_TC_050", "regression", "product", "auth");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        act("Chọn thuộc tính " + ATTRIBUTE, () -> form.variation().chooseAttribute(ATTRIBUTE));

        verify("1 dòng " + ATTRIBUTE + " với ô chọn nhiều giá trị", () -> {
            Assert.assertEquals(form.variation().attributeChoiceTitles(), List.of(ATTRIBUTE), "Dòng thuộc tính");
            Assert.assertTrue(form.variation().choiceValueSelectIsMultiple(), "Ô giá trị phải cho chọn nhiều");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Sinh bảng tổ hợp biến thể")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bật Colors chọn 2 màu, chọn Metal với 2 giá trị khác tên thì bảng tổ hợp có cột Variant, Variant Price, SKU, Quantity, Photo, đủ 4 dòng, Quantity mặc định 10, ô Quantity và SKU chung bị ẩn. Không lưu.")
    public void testVariantCombinationTable() {
        tc("AEC_PROD_TC_051", "regression", "product", "auth");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        act("Chọn 2 màu và 2 giá trị " + ATTRIBUTE, () -> {
            form.variation().enableColors();
            form.variation().chooseFirstColors(2);
            form.variation().chooseAttribute(ATTRIBUTE);
            form.variation().chooseFirstDistinctValues(2);
            form.variation().waitForVariants(4);
        });

        verify("Bảng tổ hợp 4 dòng, ô chung bị ẩn", () -> {
            Assert.assertEquals(form.variation().variantHeaders(), List.of("Variant", "Variant Price", "SKU", "Quantity", "Photo"), "Cột bảng tổ hợp");
            Assert.assertEquals(form.variation().variantNames().size(), 4, "Số dòng tổ hợp");
            Assert.assertEquals(form.variation().variantQuantities(), List.of("10", "10", "10", "10"), "Quantity mặc định");
            Assert.assertFalse(form.isFieldGroupVisible(FormField.QUANTITY), "Ô Quantity chung phải ẩn");
            Assert.assertFalse(form.isFieldGroupVisible(FormField.SKU), "Ô SKU chung phải ẩn");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Lưu sản phẩm có nhiều tổ hợp")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra sản phẩm 2 màu × 2 giá trị Metal khác tên, giá 10–13 lưu thành công; trang sửa có đủ 4 dòng tổ hợp, không trùng, giá đúng. Ảnh từng tổ hợp kiểm tay (Partial).")
    public void testSaveProductWithVariants() {
        tc("AEC_PROD_TC_052", "regression", "product", "auth");
        String name = "AutoProd_" + newToken("TC052");
        List<String> prices = List.of("10", "11", "12", "13");
        ProductCreatePage form = submit(name, "2 màu × 2 giá trị " + ATTRIBUTE + ", giá từng dòng", f -> {
            f.variation().enableColors();
            f.variation().chooseFirstColors(2);
            f.variation().chooseAttribute(ATTRIBUTE);
            Allure.parameter("Giá trị " + ATTRIBUTE, String.join(", ", f.variation().chooseFirstDistinctValues(2)));
            f.variation().waitForVariants(4);
            f.variation().enterVariantPrices(prices);
        });
        assertInserted(form);

        verify("Trang sửa có 4 tổ hợp khác nhau, giá đúng", () -> {
            ProductEditPage edit = openEditOf(new ProductListPage(), name, name);
            List<String> variants = edit.variantNames();
            Assert.assertEquals(variants.size(), 4, "Số dòng tổ hợp: " + variants);
            Assert.assertEquals(variants.stream().distinct().count(), 4L, "Không được có dòng trùng: " + variants);
            Assert.assertEquals(edit.variantPrices().stream().map(Double::parseDouble).toList(),
                    prices.stream().map(Double::parseDouble).toList(), "Giá từng dòng");
        });
    }
}
