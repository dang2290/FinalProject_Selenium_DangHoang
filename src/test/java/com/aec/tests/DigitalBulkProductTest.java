package com.aec.tests;

import com.aec.pages.DigitalProductFormPage;
import com.aec.pages.DigitalProductListPage;
import com.aec.pages.ProductBulkUploadPage;
import com.aec.pages.ProductListPage;
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

/**
 * Digital Products (danh sách, tạo, sửa, validation) và Product Bulk Upload.
 * Không automate TC_064 (Bulk Export tải tệp .xlsx về máy). TC ở docs/testcases/product/web/parts/.
 */
@Feature("Product")
@Story("Digital & Bulk")
public class DigitalBulkProductTest extends ProductTestBase {

    @Test(groups = {"smoke", "product", "auth", "ui"}, description = "Danh sách Digital Products đủ thành phần")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Products → Digital Products mở /admin/digitalproducts với tiêu đề Digital Products, nút Add New Digital Product, ô tìm; 9 cột đúng thứ tự; 10 dòng/trang có phân trang; nút Edit, Delete, Download.")
    public void testDigitalListElements() {
        tc("AEC_PROD_TC_004", "smoke", "product", "auth", "ui");
        loginAsAdmin();
        DigitalProductListPage list = new DigitalProductListPage(driver());

        Allure.step("Act: Bấm Products → Digital Products ở sidebar", () -> {
            new ProductListPage(driver()).clickSidebar("/admin/digitalproducts");
            list.waitUntilLoaded();
        });

        Allure.step("Assert: Bảng kiểm Digital Products", () -> {
            Assert.assertEquals(list.currentPath(), "/admin/digitalproducts", "Đường dẫn");
            Assert.assertTrue(list.isTitleDisplayed(), "Tiêu đề Digital Products");
            Assert.assertTrue(list.hasAddNewButton(), "Nút Add New Digital Product");
            Assert.assertFalse(list.searchPlaceholder().isBlank(), "Phải có ô tìm");
            Assert.assertEquals(list.allHeaders(),
                    List.of("#", "Name", "Added By", "Photo", "Base Price", "Todays Deal", "Published", "Featured", "Options"), "Cột của bảng");
            Assert.assertEquals(list.rowCount(), 10, "Số dòng mỗi trang");
            Assert.assertTrue(list.hasPagination(), "Phải có thanh phân trang");
            Assert.assertEquals(list.rowButtonTitles(0), List.of("Edit", "Delete", "Download"), "Nút Options");
        });
    }

    @Test(groups = {"smoke", "product", "auth", "ui"}, description = "Form Add New Digital Product đủ khối")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra form có khối General, Images, Meta Tags, Price, Product Information; các ô Product Name, Category, Product File, Tags, Main Images, Thumbnail Image (290x300), Unit price, Purchase price, Tax, Discount Date Range, Discount; không có Quantity, Variation, Shipping; nút Save Product.")
    public void testDigitalCreateFormElements() {
        tc("AEC_PROD_TC_005", "smoke", "product", "auth", "ui");
        loginAsAdmin();
        DigitalProductFormPage form = Allure.step("Act: Mở Add New Digital Product", () -> new DigitalProductFormPage(driver()).openCreate());

        Allure.step("Assert: Bảng kiểm form", () -> {
            Assert.assertTrue(form.sectionTitles().containsAll(List.of("General", "Images", "Meta Tags", "Price", "Product Information")),
                    "Các khối: " + form.sectionTitles());
            Assert.assertTrue(form.fieldLabels().containsAll(List.of("Product Name", "Category", "Product File", "Tags", "Main Images",
                    "Thumbnail Image (290x300)", "Unit price", "Purchase price", "Tax", "Discount Date Range", "Discount")), "Các ô: " + form.fieldLabels());
            Assert.assertFalse(form.hasField("current_stock"), "Không được có Quantity");
            Assert.assertFalse(form.hasField("choice_attributes[]"), "Không được có Variation");
            Assert.assertFalse(form.hasField("est_shipping_days"), "Không được có Shipping");
            Assert.assertEquals(form.submitText(), "Save Product", "Nút lưu");
        });
    }

    @Test(groups = {"smoke", "product", "auth"}, description = "Tạo sản phẩm số")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra nhập Name, Tags, Unit price 10, Purchase price 5, không chọn tệp rồi Save Product thì báo Digital Product has been inserted successfully, về /admin/digitalproducts, sản phẩm ở đầu bảng với Base Price 10.00 (giả định đã chốt).")
    public void testCreateDigitalProduct() {
        tc("AEC_PROD_TC_011", "smoke", "product", "auth");
        String name = "AutoDig_" + newDigitalToken("TC011");
        loginAsAdmin();
        DigitalProductFormPage form = new DigitalProductFormPage(driver()).openCreate();

        Allure.step("Act: Nhập dữ liệu, bấm Save Product", () -> {
            form.fillRequired(name, "10");
            form.submitAndWait();
        });

        Allure.step("Assert: Thêm thành công, sản phẩm ở đầu bảng, Base Price 10.00", () -> {
            Assert.assertEquals(form.getNotifyMessage(), DIGITAL_INSERTED, "Thông báo sau khi lưu");
            Assert.assertEquals(form.currentPath(), "/admin/digitalproducts", "Đường dẫn sau khi lưu");
            DigitalProductListPage list = new DigitalProductListPage(driver()).waitUntilLoaded();
            int row = list.rowIndexOf(name);
            Assert.assertTrue(row >= 0 && row < TOP_ROWS, "Sản phẩm mới phải ở đầu bảng, thực tế dòng " + (row + 1));
            Assert.assertEquals(list.basePriceOfRow(row), "10.00", "Base Price");
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "knownIssue"}, description = "Name số toàn khoảng trắng phải báo lỗi")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name sản phẩm số chỉ có khoảng trắng thì báo Name bắt buộc, không ra trang Something went wrong!. Đang lệch: ra trang lỗi (AMB-PROD-14).")
    public void testDigitalWhitespaceNameRejected() {
        tc("AEC_PROD_TC_058", "regression", "product", "auth", "validation", "knownIssue");
        loginAsAdmin();
        DigitalProductFormPage form = new DigitalProductFormPage(driver()).openCreate();

        Allure.step("Act: Name toàn khoảng trắng, bấm Save Product", () -> {
            form.fillRequired("   ", "10");
            form.submitAndWait();
        });

        Allure.step("Assert: Báo Name bắt buộc, không trang lỗi", () -> {
            Assert.assertFalse(form.isServerErrorPage(), "Không được ra trang Something went wrong!");
            Assert.assertFalse(form.errorMessages().isEmpty(), "Phải có thông báo Name bắt buộc");
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "knownIssue"}, description = "Discount số lớn hơn giá phải bị từ chối")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra sản phẩm số giá 10, Discount Flat 20 thì báo discount phải nhỏ hơn giá, không tạo. Đang lệch: lưu được (AMB-PROD-13).")
    public void testDigitalDiscountTooHighRejected() {
        tc("AEC_PROD_TC_059", "regression", "product", "auth", "validation", "knownIssue");
        String name = "AutoDig_" + newDigitalToken("TC059");
        loginAsAdmin();
        DigitalProductFormPage form = new DigitalProductFormPage(driver()).openCreate();

        Allure.step("Act: Giá 10, Discount 20, bấm Save Product", () -> {
            form.fillRequired(name, "10");
            form.enterField("discount", "20");
            form.submitAndWait();
        });

        Allure.step("Assert: Báo lỗi, không tạo sản phẩm", () -> {
            Assert.assertFalse(form.errorMessages().isEmpty(), "Phải có khối lỗi về discount");
            DigitalProductListPage list = new DigitalProductListPage(driver()).open();
            list.search(name);
            Assert.assertEquals(list.rowIndexOf(name), -1, "Không được có sản phẩm số mới");
        });
    }

    @DataProvider(name = "digitalEditTabs", parallel = true)
    public Object[][] digitalEditTabs() {
        return new Object[][]{{"a", "English"}, {"b", "Vietnamese"}};
    }

    @Test(dataProvider = "digitalEditTabs", groups = {"regression", "product", "auth"}, description = "Sửa sản phẩm số 2 ngôn ngữ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ở Edit Digital Product, đổi Name ở tab English / Vietnamese rồi Update Product thì báo Digital Product has been updated successfully, ở lại trang, ô Name hiện tên mới.")
    public void testEditDigitalByTab(String variant, String tab) {
        tc("AEC_PROD_TC_060", "regression", "product", "auth");
        Allure.parameter("Biến thể", variant + " — " + tab);
        String token = newDigitalToken("TC060" + variant);
        String name = "AutoDig_" + token;
        String newName = "English".equals(tab) ? "AutoDigEd_" + token : "Số thử " + token;
        loginAsAdmin();
        createDigital(name);
        DigitalProductFormPage edit = openDigitalEditOf(name, name);

        Allure.step("Act: Tab " + tab + ", đổi Name, bấm Update Product", () -> {
            if (!"English".equals(tab)) {
                edit.clickTab(tab);
            }
            edit.enterName(newName);
            edit.submitAndWait();
        });

        Allure.step("Assert: Cập nhật thành công, ở lại trang, tên mới", () -> {
            Assert.assertEquals(edit.getNotifyMessage(), DIGITAL_UPDATED, "Thông báo sau khi cập nhật");
            Assert.assertTrue(edit.isOnEditPage(), "Phải ở lại trang sửa: " + edit.currentUrl());
            Assert.assertEquals(edit.nameValue(), newName, "Ô Name");
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "knownIssue"}, description = "Sửa Name số thành khoảng trắng phải bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra thay Name sản phẩm số bằng khoảng trắng rồi Update Product thì báo Name bắt buộc và giữ tên cũ. Đang lệch: báo thành công và lưu tên rỗng (AMB-PROD-14) — dọn theo id.")
    public void testEditDigitalWhitespaceNameRejected() {
        tc("AEC_PROD_TC_061", "regression", "product", "auth", "validation", "knownIssue");
        String name = "AutoDig_" + newDigitalToken("TC061");
        loginAsAdmin();
        createDigital(name);
        DigitalProductListPage list = new DigitalProductListPage(driver()).open();
        list.search(name);
        String id = list.idOfRow(list.rowIndexOf(name));
        trackDigitalId(id);
        DigitalProductFormPage edit = list.editRow(list.rowIndexOf(name));

        Allure.step("Act: Thay Name bằng khoảng trắng, bấm Update Product", () -> {
            edit.enterName("   ");
            edit.submitAndWait();
        });

        Allure.step("Assert: Báo Name bắt buộc, giữ tên cũ", () -> {
            Assert.assertFalse(edit.errorMessages().isEmpty(), "Phải có thông báo Name bắt buộc");
            Assert.assertEquals(edit.openEdit(id, "en").nameValue(), name, "Ô Name");
        });
    }

    @Test(groups = {"regression", "product", "auth", "ui"}, description = "Trang Product Bulk Upload đủ thành phần")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Products → Bulk Import mở /admin/product-bulk-upload/index với tiêu đề Product Bulk Upload, Step 1, Step 2, nút Download CSV, Download Category, Download Brand, khối Upload Product File, ô chọn tệp, nút Upload CSV.")
    public void testBulkUploadElements() {
        tc("AEC_PROD_TC_062", "regression", "product", "auth", "ui");
        loginAsAdmin();
        ProductBulkUploadPage bulk = new ProductBulkUploadPage(driver());

        Allure.step("Act: Bấm Products → Bulk Import ở sidebar", () -> {
            new ProductListPage(driver()).clickSidebar("/admin/product-bulk-upload/index");
            bulk.waitUntilLoaded();
        });

        Allure.step("Assert: Bảng kiểm trang Bulk Upload", () -> {
            Assert.assertEquals(bulk.currentPath(), "/admin/product-bulk-upload/index", "Đường dẫn");
            Assert.assertEquals(bulk.sectionTitles(), List.of("Product Bulk Upload", "Upload Product File"), "Tiêu đề các khối");
            Assert.assertEquals(bulk.stepLabels(), List.of("Step 1:", "Step 2:"), "Các bước");
            Assert.assertEquals(bulk.buttonTexts(), List.of("Download CSV", "Download Category", "Download Brand", "Upload CSV"), "Các nút");
            Assert.assertTrue(bulk.hasFileInput(), "Phải có ô chọn tệp");
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation"}, description = "Upload CSV không chọn tệp bị chặn")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra bấm Upload CSV khi chưa chọn tệp thì không gửi, trình duyệt nhắc chọn tệp. Tải tệp thật không thực hiện (Q14).")
    public void testUploadWithoutFileBlocked() {
        tc("AEC_PROD_TC_063", "regression", "product", "auth", "validation");
        loginAsAdmin();
        ProductBulkUploadPage bulk = Allure.step("Arrange: Mở Product Bulk Upload", () -> new ProductBulkUploadPage(driver()).open());

        Allure.step("Act: Bấm Upload CSV khi chưa chọn tệp", bulk::clickUpload);

        Allure.step("Assert: Không gửi, trình duyệt nhắc chọn tệp", () -> {
            Assert.assertFalse(bulk.fileValidationMessage().isEmpty(), "Phải có bong bóng nhắc chọn tệp");
            Assert.assertTrue(bulk.isSamePageInstance(), "Không được gửi form");
        });
    }
}
