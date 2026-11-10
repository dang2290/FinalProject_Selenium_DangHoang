package com.aec.tests;

import com.aec.pages.MediaLibraryModal;
import com.aec.pages.ProductCreatePage;
import com.aec.pages.ProductCreatePage.SaveButton;
import com.aec.pages.ProductEditPage;
import com.aec.pages.ProductListPage;
import com.aec.pages.ProductListPage.Listing;
import com.aec.pages.ProductListPage.RowSwitch;
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

/** Form Add New Product: giao diện, tạo, validation Name / Unit / Min Qty, Category, Brand, tệp, bấm đúp, script/SQL, emoji, bàn phím. TC ở docs/testcases/product/web/parts/. */
@Feature("Product")
@Story("Tạo sản phẩm")
public class ProductCreateTest extends ProductTestBase {

    private static final List<String> SECTIONS = List.of("Product Information", "Product Images", "Product Videos",
            "Product Variation", "Product price + stock", "Product Description", "PDF Specification", "SEO Meta Tags",
            "Shipping Configuration", "Low Stock Quantity Warning", "Stock Visibility State", "Cash on Delivery", "Featured",
            "Todays Deal", "Flash Deal", "Estimate Shipping Time", "Vat & TAX");

    @Test(groups = {"smoke", "product", "auth", "ui"}, description = "Form Add New Product đủ khối, đúng mặc định")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra form Add New Product có tiêu đề, đủ 17 khối theo thứ tự, giá trị mặc định (Weight 0.00, Min Qty 1, giá/discount/quantity/tax 0, Discount Flat, Low Stock 1), Show Stock Quantity và Cash on Delivery bật, Featured / Todays Deal tắt, Video Provider Youtube, 2 nút Save & Unpublish / Save & Publish.")
    public void testCreateFormDefaults() {
        tc("AEC_PROD_TC_002", "smoke", "product", "auth", "ui");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Assert: Tiêu đề và các khối", () -> {
            Assert.assertTrue(form.isTitleDisplayed(), "Tiêu đề Add New Product");
            Assert.assertEquals(form.sectionTitles(), SECTIONS, "Các khối của form");
        });
        Allure.step("Assert: Giá trị mặc định", () -> {
            Assert.assertEquals(form.fieldValue("weight"), "0.00", "Weight");
            Assert.assertEquals(form.fieldValue("min_qty"), "1", "Minimum Purchase Qty");
            Assert.assertEquals(form.fieldValue("unit_price"), "0", "Unit price");
            Assert.assertEquals(form.fieldValue("discount"), "0", "Discount");
            Assert.assertEquals(form.fieldValue("current_stock"), "0", "Quantity");
            Assert.assertEquals(form.fieldValue("tax[]"), "0", "Tax");
            Assert.assertEquals(form.selectedOptionText("discount_type"), "Flat", "Kiểu Discount");
            Assert.assertEquals(form.fieldValue("low_stock_quantity"), "1", "Low Stock Quantity");
            Assert.assertEquals(form.checkedRadio("stock_visibility_state"), "quantity", "Show Stock Quantity phải bật");
            Assert.assertTrue(form.isChecked("cash_on_delivery"), "Cash on Delivery phải bật");
            Assert.assertFalse(form.isChecked("featured"), "Featured phải tắt");
            Assert.assertFalse(form.isChecked("todays_deal"), "Todays Deal phải tắt");
            Assert.assertEquals(form.selectedOptionText("video_provider"), "Youtube", "Video Provider");
            Assert.assertEquals(form.saveButtonTexts(), List.of("Save & Unpublish", "Save & Publish"), "Nút lưu cuối form");
        });
    }

    @Test(groups = {"smoke", "product", "auth", "criticalPath"}, description = "Tạo sản phẩm bằng Save & Unpublish")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra nhập Name, Unit, Tags, Unit price 10, Quantity 5 rồi Save & Unpublish thì báo thêm thành công, về /admin/products/admin; dòng sản phẩm có Base Price $10.00, Total Stock 5, Published tắt; Meta Title bằng Name.")
    public void testCreateSaveUnpublish() {
        tc("AEC_PROD_TC_007", "smoke", "product", "auth", "criticalPath");
        String name = "AutoProd_" + newToken("TC007");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Act: Nhập dữ liệu, bấm Save & Unpublish", () -> {
            form.fillRequired(name);
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Thêm thành công, về In House Products", () -> {
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
            Assert.assertEquals(form.currentPath(), Listing.IN_HOUSE.path(), "Đường dẫn sau khi lưu");
        });
        Allure.step("Assert: Dòng sản phẩm đúng giá, kho, Published tắt", () -> {
            int row = list.waitUntilLoaded().rowIndexOf(name);
            Assert.assertTrue(row >= 0 && row < TOP_ROWS, "Sản phẩm mới phải ở đầu bảng, thực tế dòng " + (row + 1));
            Assert.assertEquals(list.basePriceOfRow(row), "$10.00", "Base Price");
            Assert.assertEquals(list.totalStockOfRow(row), "5", "Total Stock");
            Assert.assertFalse(list.isSwitchOn(row, RowSwitch.PUBLISHED), "Published phải tắt");
        });
        Allure.step("Assert: Meta Title bằng Name", () ->
                Assert.assertEquals(openEditOf(list, name, name).fieldValue("meta_title"), name, "Meta Title"));
    }

    @Test(groups = {"smoke", "product", "auth", "criticalPath"}, description = "Tạo sản phẩm bằng Save & Publish")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra nhập dữ liệu như PC-2 rồi Save & Publish thì báo thêm thành công và dòng sản phẩm có công tắc Published bật.")
    public void testCreateSavePublish() {
        tc("AEC_PROD_TC_008", "smoke", "product", "auth", "criticalPath");
        String name = "AutoProd_" + newToken("TC008");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Nhập dữ liệu, bấm Save & Publish", () -> {
            form.fillRequired(name);
            form.saveAndWait(SaveButton.PUBLISH);
        });

        Allure.step("Assert: Thêm thành công, Published bật", () -> {
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
            ProductListPage list = new ProductListPage(driver()).waitUntilLoaded();
            int row = list.rowIndexOf(name);
            Assert.assertTrue(row >= 0, "Phải có dòng sản phẩm mới");
            Assert.assertTrue(list.isSwitchOn(row, RowSwitch.PUBLISHED), "Published phải bật");
        });
    }

    @DataProvider(name = "missingRequired", parallel = true)
    public Object[][] missingRequired() {
        return new Object[][]{{"AEC_PROD_TC_022", "name"}, {"AEC_PROD_TC_023", "unit"}};
    }

    @Test(dataProvider = "missingRequired", groups = {"regression", "product", "auth", "validation"},
            description = "Không tạo được khi thiếu Product Name / Unit")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra để trống Product Name (TC_022) hoặc Unit (TC_023) rồi Save & Unpublish thì trình duyệt chặn: không gửi form, con trỏ về đúng ô, có bong bóng nhắc nhập.")
    public void testMissingRequiredBlocked(String testId, String field) {
        tc(testId, "regression", "product", "auth", "validation");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Bỏ trống " + field + ", bấm Save & Unpublish", () -> {
            if ("name".equals(field)) {
                form.enterUnit("pc");
            } else {
                form.enterName("AutoProd_" + newToken("TC023"));
            }
            form.markPage();
            form.clickSave(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Trình duyệt chặn ở ô " + field, () -> {
            Assert.assertFalse(form.validationMessageOf(field).isEmpty(), "Phải có bong bóng nhắc nhập");
            Assert.assertTrue(form.isSamePageInstance(), "Không được gửi form");
            Assert.assertEquals(form.focusedFieldName(), field, "Con trỏ phải ở ô " + field);
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation"}, description = "Name chỉ có khoảng trắng bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name chỉ có 3 dấu cách, dữ liệu còn lại như PC-2, Save & Unpublish thì ở lại form và khối đỏ ghi Product name is required.")
    public void testWhitespaceNameRejected() {
        tc("AEC_PROD_TC_024", "regression", "product", "auth", "validation");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Name toàn khoảng trắng, bấm Save & Unpublish", () -> {
            form.fillRequired("   ");
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Ở lại form, khối đỏ báo Name bắt buộc", () -> {
            Assert.assertEquals(form.currentPath(), "/admin/products/create", "Phải ở lại form");
            Assert.assertTrue(form.errorMessages().contains(NAME_REQUIRED), "Khối lỗi: " + form.errorMessages());
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "boundary", "knownIssue"}, description = "Name dài đúng 255 ký tự lưu được")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name 255 ký tự tạo thành công và trang sửa hiện đủ 255 ký tự. Đang lệch: Name bị cắt còn 200 ký tự, không báo (run_1793607938).")
    public void testName255Saved() {
        tc("AEC_PROD_TC_025", "regression", "product", "auth", "validation", "boundary", "knownIssue");
        String token = newToken("TC025");
        String name = TestDataGenerator.padTo("P255_" + token + "_", 'x', 255);
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Tạo sản phẩm với Name 255 ký tự", () -> {
            form.fillRequired(name);
            form.saveAndWait(SaveButton.UNPUBLISH);
        });
        Allure.step("Assert: Thêm thành công", () ->
                Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu"));

        Allure.step("Assert: Trang sửa hiện đủ 255 ký tự", () -> {
            ProductListPage list = new ProductListPage(driver());
            list.open();
            list.search(token);
            Assert.assertFalse(list.rowIndexesContaining(token).isEmpty(), "Phải tìm thấy sản phẩm test");
            Assert.assertEquals(list.editRow(list.rowIndexesContaining(token).get(0)).nameValue(), name, "Ô Name");
        });
    }

    @DataProvider(name = "rejectedInputs", parallel = true)
    public Object[][] rejectedInputs() {
        return new Object[][]{
                {"AEC_PROD_TC_026", "name256", "The name may not be greater than 255 characters."},
                {"AEC_PROD_TC_027", "unitBlank", "Unit field is required"}};
    }

    @Test(dataProvider = "rejectedInputs", groups = {"regression", "product", "auth", "validation"},
            description = "Name 256 ký tự / Unit toàn khoảng trắng bị từ chối")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name 256 ký tự (TC_026) và Unit chỉ có khoảng trắng (TC_027) thì ở lại form, khối đỏ ghi đúng câu lỗi.")
    public void testServerSideRejection(String testId, String kind, String message) {
        tc(testId, "regression", "product", "auth", "validation");
        Allure.parameter("Trường hợp", kind);
        String token = newToken("TC" + testId.substring(testId.length() - 3));
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Nhập dữ liệu " + kind + ", bấm Save & Unpublish", () -> {
            if ("name256".equals(kind)) {
                form.fillRequired(TestDataGenerator.padTo("P256_" + token + "_", 'x', 256));
            } else {
                form.fillRequired("AutoProd_" + token);
                form.enterUnit("   ");
            }
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Ở lại form, khối đỏ báo lỗi", () -> {
            Assert.assertEquals(form.currentPath(), "/admin/products/create", "Phải ở lại form");
            Assert.assertTrue(form.errorMessages().contains(message), "Khối lỗi: " + form.errorMessages());
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "boundary", "knownIssue"},
            description = "Unit dài hơn 20 ký tự phải bị chặn, không được cắt im lặng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Unit 21 ký tự thì phải báo lỗi và không tạo sản phẩm. Đang lệch: báo thành công và lưu 20 ký tự đầu (AMB-PROD-08).")
    public void testUnitTooLongRejected() {
        tc("AEC_PROD_TC_028", "regression", "product", "auth", "validation", "boundary", "knownIssue");
        String token = newToken("TC028");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Unit 21 ký tự, bấm Save & Unpublish", () -> {
            form.fillRequired("AutoProd_" + token);
            form.enterUnit("U".repeat(21));
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Báo lỗi, không tạo sản phẩm", () -> {
            Assert.assertFalse(form.errorMessages().isEmpty(), "Phải có khối lỗi về độ dài Unit");
            ProductListPage list = new ProductListPage(driver()).open();
            list.search(token);
            Assert.assertTrue(list.rowIndexesContaining(token).isEmpty(), "Không được có sản phẩm mới");
        });
    }

    @Test(groups = {"regression", "product", "auth", "ui", "knownIssue"}, description = "Nhãn Unit phải có dấu bắt buộc")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra nhãn ô Unit là 'Unit *' giống các ô bắt buộc khác. Đang lệch: nhãn là 'Unit', không có * (AMB-PROD-09).")
    public void testUnitLabelMarkedRequired() {
        tc("AEC_PROD_TC_029", "regression", "product", "auth", "ui", "knownIssue");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Assert: Nhãn ô Unit có dấu *", () ->
                Assert.assertEquals(form.labelOf("unit"), "Unit *", "Nhãn ô Unit"));
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Bỏ trống Tags vẫn lưu được")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra nhập dữ liệu như PC-2 nhưng không nhập Tags thì vẫn báo thêm thành công dù nhãn ghi Tags * (AMB-PROD-09).")
    public void testEmptyTagsAllowed() {
        tc("AEC_PROD_TC_030", "regression", "product", "auth");
        String name = "AutoProd_" + newToken("TC030");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Nhập dữ liệu, không nhập Tags, bấm Save & Unpublish", () -> {
            form.enterName(name);
            form.enterUnit("pc");
            form.enterUnitPrice("10");
            form.enterQuantity("5");
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Thêm thành công", () ->
                Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu"));
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Chọn Category, Brand và lưu đúng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra chọn Category Sport shoes, Brand MiuMiu rồi lưu thì trang sửa hiện đúng hai lựa chọn.")
    public void testCategoryBrandSaved() {
        tc("AEC_PROD_TC_031", "regression", "product", "auth");
        String name = "AutoProd_" + newToken("TC031");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Chọn Category, Brand, nhập dữ liệu, bấm Save & Unpublish", () -> {
            form.chooseCategory("Sport shoes");
            form.chooseBrand("MiuMiu");
            form.fillRequired(name);
            form.saveAndWait(SaveButton.UNPUBLISH);
        });
        Allure.step("Assert: Thêm thành công", () ->
                Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu"));

        Allure.step("Assert: Trang sửa hiện đúng Category, Brand", () -> {
            ProductEditPage edit = openEditOf(new ProductListPage(driver()), name, name);
            Assert.assertEquals(edit.selectedOptionText("category_id").replaceFirst("^-+\\s*", ""), "Sport shoes", "Category");
            Assert.assertEquals(edit.selectedOptionText("brand_id"), "MiuMiu", "Brand");
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "boundary"}, description = "Minimum Purchase Qty bằng 0 bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Minimum Purchase Qty 0, dữ liệu còn lại như PC-2, thì trình duyệt chặn ở ô Minimum Purchase Qty, không gửi form.")
    public void testMinQtyZeroBlocked() {
        tc("AEC_PROD_TC_032", "regression", "product", "auth", "validation", "boundary");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Minimum Purchase Qty 0, bấm Save & Unpublish", () -> {
            form.fillRequired("AutoProd_" + newToken("TC032"));
            form.enterField("min_qty", "0");
            form.markPage();
            form.clickSave(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Trình duyệt chặn ở ô Minimum Purchase Qty", () -> {
            Assert.assertFalse(form.validationMessageOf("min_qty").isEmpty(), "Phải có bong bóng nhắc");
            Assert.assertTrue(form.isSamePageInstance(), "Không được gửi form");
        });
    }

    @Test(groups = {"regression", "product", "auth", "localization"}, description = "Tên tiếng Việt lưu đúng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tên tiếng Việt có dấu, có dấu cách tạo thành công và danh sách hiện đúng như đã nhập.")
    public void testVietnameseName() {
        tc("AEC_PROD_TC_033", "regression", "product", "auth", "localization");
        String token = newToken("TC033");
        String name = "Sản phẩm thử Ưu đãi " + token;
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Tạo sản phẩm tên tiếng Việt", () -> {
            form.fillRequired(name);
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Thêm thành công, tên đúng như đã nhập", () -> {
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
            Assert.assertTrue(findRow(new ProductListPage(driver()), token, name) >= 0, "Danh sách phải có " + name);
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Hai sản phẩm trùng tên có slug khác nhau")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tạo hai sản phẩm cùng Name thì cả hai đều thành công và ô Slug ở trang sửa của hai sản phẩm khác nhau.")
    public void testDuplicateNameDifferentSlug() {
        tc("AEC_PROD_TC_034", "regression", "product", "auth");
        String name = "AutoProdDup_" + newToken("TC034");
        loginAsAdmin();
        createProduct(name);

        Allure.step("Act: Tạo lần nữa cùng Name", () -> {
            ProductCreatePage form = new ProductCreatePage(driver()).open();
            form.fillRequired(name);
            form.saveAndWait(SaveButton.UNPUBLISH);
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Lần 2 không được báo trùng");
        });

        Allure.step("Assert: Hai Slug khác nhau", () -> {
            ProductListPage list = new ProductListPage(driver()).open();
            list.search(name);
            Assert.assertEquals(list.rowIndexesContaining(name).size(), 2, "Phải có 2 sản phẩm cùng tên");
            String first = list.editRow(0).fieldValue("slug");
            list.open();
            list.search(name);
            String second = list.editRow(1).fieldValue("slug");
            Assert.assertNotEquals(first, second, "Slug hai sản phẩm phải khác nhau");
        });
    }

    @DataProvider(name = "mediaVariants", parallel = true)
    public Object[][] mediaVariants() {
        return new Object[][]{{"a", "thumbnail"}, {"b", "video"}};
    }

    @Test(dataProvider = "mediaVariants", groups = {"regression", "product", "auth"}, description = "Gắn ảnh, video vào sản phẩm")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra chọn Thumbnail Image từ thư viện (a) và nhập Video Link Youtube (b) rồi lưu thì trang sửa hiện ảnh đã chọn / đúng provider và link. Biến thể c (PDF) không automate — thư viện chưa có PDF.")
    public void testAttachMedia(String variant, String kind) {
        tc("AEC_PROD_TC_035", "regression", "product", "auth");
        Allure.parameter("Biến thể", variant + " — " + kind);
        String name = "AutoProd_" + newToken("TC035" + variant);
        String videoLink = "https://www.youtube.com/watch?v=aqz-KE-bpKQ";
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Nhập dữ liệu, gắn " + kind + ", bấm Save & Unpublish", () -> {
            form.fillRequired(name);
            if ("thumbnail".equals(kind)) {
                MediaLibraryModal library = form.openThumbnailLibrary();
                library.selectFile(0);
                library.addFiles();
                Assert.assertEquals(form.thumbnailPreviewCount(), 1, "Khung xem trước phải có 1 ảnh");
            } else {
                form.enterField("video_link", videoLink);
            }
            form.saveAndWait(SaveButton.UNPUBLISH);
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
        });

        Allure.step("Assert: Trang sửa hiện đúng " + kind, () -> {
            ProductEditPage edit = openEditOf(new ProductListPage(driver()), name, name);
            if ("thumbnail".equals(kind)) {
                Assert.assertEquals(edit.thumbnailPreviewCount(), 1, "Ô Thumbnail Image phải hiện ảnh đã chọn");
            } else {
                Assert.assertEquals(edit.selectedOptionText("video_provider"), "Youtube", "Video Provider");
                Assert.assertEquals(edit.fieldValue("video_link"), videoLink, "Video Link");
            }
        });
    }

    @Test(groups = {"regression", "product", "auth", "reliability"}, description = "Bấm lưu liên tiếp chỉ tạo một sản phẩm")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra nhấp đúp Save & Unpublish thì không ra trang lỗi và tìm Name ra đúng 1 dòng.")
    public void testDoubleClickSaveCreatesOne() {
        tc("AEC_PROD_TC_066", "regression", "product", "auth", "reliability");
        String name = "AutoProdDbl_" + newToken("TC066");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Nhập dữ liệu, nhấp đúp Save & Unpublish", () -> {
            form.fillRequired(name);
            form.doubleClickSave(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Không trang lỗi, đúng 1 dòng", () -> {
            Assert.assertFalse(form.isServerErrorPage(), "Không được ra trang lỗi");
            ProductListPage list = new ProductListPage(driver()).open();
            list.search(name);
            Assert.assertEquals(list.rowIndexesContaining(name).size(), 1, "Không được tạo trùng sản phẩm");
        });
    }

    @DataProvider(name = "injectionNames", parallel = true)
    public Object[][] injectionNames() {
        return new Object[][]{{"a", "<script>alert(1)</script>"}, {"b", "<b>Bold</b>"}, {"c", "' OR '1'='1 -- "}};
    }

    @Test(dataProvider = "injectionNames", groups = {"regression", "product", "auth", "security"},
            description = "Mã script, chuỗi SQL ở Name lưu như chữ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name chứa script, thẻ <b>, chuỗi SQL tạo thành công, không trang lỗi; danh sách và trang sửa không bật hộp thoại, hiện nguyên văn.")
    public void testInjectionNameNotExecuted(String variant, String payload) {
        tc("AEC_PROD_TC_069", "regression", "product", "auth", "security");
        String token = newToken("TC069" + variant);
        String name = payload + token;
        Allure.parameter("Biến thể", variant + " — " + name);
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Tạo sản phẩm với Name chứa mã", () -> {
            form.fillRequired(name);
            form.saveAndWait(SaveButton.UNPUBLISH);
        });
        Allure.step("Assert: Thêm thành công, không trang lỗi", () -> {
            Assert.assertFalse(form.isServerErrorPage(), "Không được ra trang lỗi");
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
        });

        ProductListPage list = new ProductListPage(driver());
        Allure.step("Assert: Danh sách — không hộp thoại, tên nguyên văn", () -> {
            int row = findRow(list, token, name);
            Assert.assertFalse(list.isAlertOpen(), "Không được bật hộp thoại ở danh sách");
            Assert.assertTrue(row >= 0, "Danh sách phải hiện nguyên văn " + name + ", thực tế " + list.names());
        });
        Allure.step("Assert: Trang sửa — không hộp thoại, tên nguyên văn", () -> {
            ProductEditPage edit = list.editRow(list.rowIndexOf(name));
            Assert.assertFalse(edit.isAlertOpen(), "Không được bật hộp thoại ở trang sửa");
            Assert.assertEquals(edit.nameValue(), name, "Ô Name");
        });
    }

    @Test(groups = {"regression", "product", "auth", "localization", "knownIssue"}, description = "Emoji trong Name lưu đúng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name có emoji tạo thành công và danh sách hiện đúng emoji. Đang lệch: emoji thành ? (AMB-PROD-10).")
    public void testEmojiName() {
        tc("AEC_PROD_TC_071", "regression", "product", "auth", "localization", "knownIssue");
        String token = newToken("TC071");
        String name = "AutoProd 🎨 " + token;
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        Allure.step("Act: Tạo sản phẩm tên có emoji", () -> {
            form.fillRequired(name);
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        Allure.step("Assert: Danh sách hiện đúng emoji", () -> {
            ProductListPage list = new ProductListPage(driver()).open();
            list.search(token);
            Assert.assertEquals(list.names(), List.of(name), "Tên ở danh sách");
        });
    }

    @Test(groups = {"regression", "product", "auth", "accessibility"}, description = "Tab đi qua các ô đầu form, ô đang focus có viền")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể b: từ ô Product Name nhấn Tab 4 lần thì focus đi lần lượt sang 4 ô khác nhau; các ô Product Name, Unit, Weight, Minimum Purchase Qty có viền khi được focus. Thứ tự ghi vào report.")
    public void testKeyboardTabOrder() {
        tc("AEC_PROD_TC_072", "regression", "product", "auth", "accessibility");
        Allure.parameter("Biến thể", "b — Tab");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        List<String> sequence = Allure.step("Act: Từ ô Product Name nhấn Tab 4 lần", () -> form.tabSequenceFromName(4));

        Allure.step("Assert: Focus đi lần lượt qua 4 ô khác nhau, có viền khi focus", () -> {
            Allure.parameter("Thứ tự focus", String.join(" → ", sequence));
            Assert.assertEquals(sequence.stream().distinct().count(), 4L, "Mỗi lần Tab phải sang ô khác: " + sequence);
            Assert.assertTrue(sequence.stream().noneMatch(String::isBlank), "Phần tử nhận focus phải là ô/nút có tên: " + sequence);
            Assert.assertEquals(form.fieldsWithoutVisibleFocus(), List.of(), "Ô không đổi gì khi được focus");
        });
    }
}
