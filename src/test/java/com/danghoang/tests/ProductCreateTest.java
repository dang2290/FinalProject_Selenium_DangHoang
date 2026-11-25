package com.danghoang.tests;

import com.danghoang.base.StepNames;
import com.danghoang.pages.FormField;
import com.danghoang.utils.EnvReferenceData;
import com.danghoang.pages.MediaLibraryModal;
import com.danghoang.pages.ProductCreatePage;
import com.danghoang.pages.ProductCreatePage.SaveButton;
import com.danghoang.pages.ProductEditPage;
import com.danghoang.pages.ProductListPage;
import com.danghoang.pages.ProductListPage.Listing;
import com.danghoang.pages.ProductListPage.RowSwitch;
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

/** Form Add New Product: giao diện, tạo, validation Name / Unit / Min Qty, Category, Brand, tệp, bấm đúp, script/SQL, emoji, bàn phím. TC ở docs/testcases/product/web/parts/. */
@Feature("Product")
@Story("Tạo sản phẩm")
public class ProductCreateTest extends ProductTestBase {

    private static final String CATEGORY = EnvReferenceData.get("productCategory");
    private static final String BRAND = EnvReferenceData.get("productBrand");

    @Test(groups = {"smoke", "product", "auth", "ui"}, description = "Form Add New Product đủ khối, đúng mặc định")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra form Add New Product có tiêu đề, đủ 17 khối theo thứ tự, giá trị mặc định (Weight 0.00, Min Qty 1, giá/discount/quantity/tax 0, Discount Flat, Low Stock 1), Show Stock Quantity và Cash on Delivery bật, Featured / Todays Deal tắt, Video Provider Youtube, 2 nút Save & Unpublish / Save & Publish.")
    public void testCreateFormDefaults() {
        tc("AEC_PROD_TC_002", "smoke", "product", "auth", "ui");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        verify("Tiêu đề và các khối", () -> {
            Assert.assertTrue(form.isTitleDisplayed(), "Tiêu đề Add New Product");
            Assert.assertEquals(form.sectionTitles(), PRODUCT_FORM_SECTIONS, "Các khối của form");
        });
        verify("Giá trị mặc định", () -> {
            Assert.assertEquals(form.fieldValue(FormField.WEIGHT), "0.00", "Weight");
            Assert.assertEquals(form.fieldValue(FormField.MIN_QTY), "1", "Minimum Purchase Qty");
            Assert.assertEquals(form.fieldValue(FormField.UNIT_PRICE), "0", "Unit price");
            Assert.assertEquals(form.fieldValue(FormField.DISCOUNT), "0", "Discount");
            Assert.assertEquals(form.fieldValue(FormField.QUANTITY), "0", "Quantity");
            Assert.assertEquals(form.fieldValue(FormField.TAX), "0", "Tax");
            Assert.assertEquals(form.selectedOptionText(FormField.DISCOUNT_TYPE), "Flat", "Kiểu Discount");
            Assert.assertEquals(form.fieldValue(FormField.LOW_STOCK_QUANTITY), "1", "Low Stock Quantity");
            Assert.assertEquals(form.checkedRadio(FormField.STOCK_VISIBILITY), "quantity", "Show Stock Quantity phải bật");
            Assert.assertTrue(form.isChecked(FormField.CASH_ON_DELIVERY), "Cash on Delivery phải bật");
            Assert.assertFalse(form.isChecked(FormField.FEATURED), "Featured phải tắt");
            Assert.assertFalse(form.isChecked(FormField.TODAYS_DEAL), "Todays Deal phải tắt");
            Assert.assertEquals(form.selectedOptionText(FormField.VIDEO_PROVIDER), "Youtube", "Video Provider");
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

        ProductListPage list = act("Nhập dữ liệu rồi bấm Save & Unpublish",
                () -> form.createProduct(name, SaveButton.UNPUBLISH));

        verify("Thêm thành công, về In House Products", () -> {
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
            Assert.assertEquals(list.currentPath(), Listing.IN_HOUSE.path(), "Đường dẫn sau khi lưu");
        });
        verify("Dòng sản phẩm đúng giá, kho, Published tắt", () -> {
            int row = list.waitUntilLoaded().rowIndexOf(name);
            Assert.assertTrue(row >= 0 && row < TOP_ROWS, "Sản phẩm mới phải ở đầu bảng, thực tế dòng " + (row + 1));
            Assert.assertEquals(list.basePriceOfRow(row), "$10.00", "Base Price");
            Assert.assertEquals(list.totalStockOfRow(row), "5", "Total Stock");
            Assert.assertFalse(list.isSwitchOn(row, RowSwitch.PUBLISHED), "Published phải tắt");
        });
        verify("Meta Title bằng Name", () ->
                Assert.assertEquals(openEditOf(list, name, name).fieldValue(FormField.META_TITLE), name, "Meta Title"));
    }

    @Test(groups = {"smoke", "product", "auth", "criticalPath"}, description = "Tạo sản phẩm bằng Save & Publish")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra nhập dữ liệu như PC-2 rồi Save & Publish thì báo thêm thành công và dòng sản phẩm có công tắc Published bật.")
    public void testCreateSavePublish() {
        tc("AEC_PROD_TC_008", "smoke", "product", "auth", "criticalPath");
        String name = "AutoProd_" + newToken("TC008");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        ProductListPage list = act("Nhập dữ liệu rồi bấm Save & Publish", () -> form.createProduct(name, SaveButton.PUBLISH));

        verify("Thêm thành công, Published bật", () -> {
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
            list.waitUntilLoaded();
            int row = list.rowIndexOf(name);
            Assert.assertTrue(row >= 0, "Phải có dòng sản phẩm mới");
            Assert.assertTrue(list.isSwitchOn(row, RowSwitch.PUBLISHED), "Published phải bật");
        });
    }

    @DataProvider(name = "missingRequired", parallel = true)
    public Object[][] missingRequired() {
        return new Object[][]{{"AEC_PROD_TC_022", FormField.NAME}, {"AEC_PROD_TC_023", FormField.UNIT}};
    }

    @Test(dataProvider = "missingRequired", groups = {"regression", "product", "auth", "validation"},
            description = "Không tạo được khi thiếu Product Name / Unit")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra để trống Product Name (TC_022) hoặc Unit (TC_023) rồi Save & Unpublish thì trình duyệt chặn: không gửi form, con trỏ về đúng ô, có bong bóng nhắc nhập.")
    public void testMissingRequiredBlocked(String testId, FormField field) {
        tc(testId, "regression", "product", "auth", "validation");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        act("Bỏ trống " + field + ", bấm Save & Unpublish", () -> {
            if (field == FormField.NAME) {
                form.enterUnit("pc");
            } else {
                form.enterName("AutoProd_" + newToken("TC023"));
            }
            form.clickSave(SaveButton.UNPUBLISH);
        });

        verify("Trình duyệt chặn ở ô " + field, () -> {
            Assert.assertFalse(form.validationMessageOf(field).isEmpty(), "Phải có bong bóng nhắc nhập");
            Assert.assertTrue(form.isSamePageInstance(), "Không được gửi form");
            Assert.assertEquals(form.focusedFieldName(), field.toString(), "Con trỏ phải ở ô " + field);
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation"}, description = "Name chỉ có khoảng trắng bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name chỉ có 3 dấu cách, dữ liệu còn lại như PC-2, Save & Unpublish thì ở lại form và khối đỏ ghi Product name is required.")
    public void testWhitespaceNameRejected() {
        tc("AEC_PROD_TC_024", "regression", "product", "auth", "validation");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        act("Name toàn khoảng trắng rồi bấm Save & Unpublish", () -> {
            form.fillRequired("   ");
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        verify("Ở lại form, khối đỏ báo Name bắt buộc", () -> {
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

        ProductListPage list = act("Tạo sản phẩm với Name 255 ký tự", () -> form.createProduct(name, SaveButton.UNPUBLISH));
        verify(StepNames.CREATED, () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu"));

        verify("Trang sửa hiện đủ 255 ký tự", () -> {
            list.searchFor(token);
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

        act("Nhập dữ liệu " + kind + ", bấm Save & Unpublish", () -> {
            if ("name256".equals(kind)) {
                form.fillRequired(TestDataGenerator.padTo("P256_" + token + "_", 'x', 256));
            } else {
                form.fillRequired("AutoProd_" + token);
                form.enterUnit("   ");
            }
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        verify("Ở lại form, khối đỏ báo lỗi", () -> {
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

        act("Unit 21 ký tự rồi bấm Save & Unpublish", () -> {
            form.fillRequired("AutoProd_" + token);
            form.enterUnit("U".repeat(21));
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        verify(StepNames.REJECTED_NO_PRODUCT, () -> {
            Assert.assertFalse(form.errorMessages().isEmpty(), "Phải có khối lỗi về độ dài Unit");
            ProductListPage list = new ProductListPage();
            list.searchFor(token);
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

        verify("Nhãn ô Unit có dấu *", () ->
                Assert.assertEquals(form.labelOf(FormField.UNIT), "Unit *", "Nhãn ô Unit"));
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Bỏ trống Tags vẫn lưu được")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra nhập dữ liệu như PC-2 nhưng không nhập Tags thì vẫn báo thêm thành công dù nhãn ghi Tags * (AMB-PROD-09).")
    public void testEmptyTagsAllowed() {
        tc("AEC_PROD_TC_030", "regression", "product", "auth");
        String name = "AutoProd_" + newToken("TC030");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        act("Nhập dữ liệu, không nhập Tags rồi bấm Save & Unpublish", () -> {
            form.enterName(name);
            form.enterUnit("pc");
            form.enterUnitPrice("10");
            form.enterQuantity("5");
            form.saveAndWait(SaveButton.UNPUBLISH);
        });

        verify(StepNames.CREATED, () ->
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

        ProductListPage list = act("Chọn Category, Brand, nhập dữ liệu rồi bấm Save & Unpublish", () -> {
            form.chooseCategory(CATEGORY);
            form.chooseBrand(BRAND);
            return form.createProduct(name, SaveButton.UNPUBLISH);
        });
        verify(StepNames.CREATED, () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu"));

        verify("Trang sửa hiện đúng Category, Brand", () -> {
            ProductEditPage edit = openEditOf(list, name, name);
            Assert.assertEquals(edit.selectedOptionText(FormField.CATEGORY).replaceFirst("^-+\\s*", ""), CATEGORY, "Category");
            Assert.assertEquals(edit.selectedOptionText(FormField.BRAND), BRAND, "Brand");
        });
    }

    @Test(groups = {"regression", "product", "auth", "validation", "boundary"}, description = "Minimum Purchase Qty bằng 0 bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Minimum Purchase Qty 0, dữ liệu còn lại như PC-2, thì trình duyệt chặn ở ô Minimum Purchase Qty, không gửi form.")
    public void testMinQtyZeroBlocked() {
        tc("AEC_PROD_TC_032", "regression", "product", "auth", "validation", "boundary");
        loginAsAdmin();
        ProductCreatePage form = openCreateForm();

        act("Minimum Purchase Qty 0 rồi bấm Save & Unpublish", () -> {
            form.fillRequired("AutoProd_" + newToken("TC032"));
            form.enterField(FormField.MIN_QTY, "0");
            form.clickSave(SaveButton.UNPUBLISH);
        });

        verify("Trình duyệt chặn ở ô Minimum Purchase Qty", () -> {
            Assert.assertFalse(form.validationMessageOf(FormField.MIN_QTY).isEmpty(), "Phải có bong bóng nhắc");
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

        ProductListPage list = act("Tạo sản phẩm tên tiếng Việt", () -> form.createProduct(name, SaveButton.UNPUBLISH));

        verify("Thêm thành công, tên đúng như đã nhập", () -> {
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
            Assert.assertTrue(list.findRow(token, name) >= 0, "Danh sách phải có " + name);
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

        ProductListPage list = act("Tạo lần nữa cùng Name", () -> {
            ProductListPage created = new ProductCreatePage().open().createProduct(name, SaveButton.UNPUBLISH);
            Assert.assertEquals(created.getNotifyMessage(), INSERTED, "Lần 2 không được báo trùng");
            return created;
        });

        verify("Hai Slug khác nhau", () -> {
            list.searchFor(name);
            Assert.assertEquals(list.rowIndexesContaining(name).size(), 2, "Phải có 2 sản phẩm cùng tên");
            String first = list.editRow(0).fieldValue(FormField.SLUG);
            list.searchFor(name);
            String second = list.editRow(1).fieldValue(FormField.SLUG);
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
        String[] chosenFile = {""};

        act("Nhập dữ liệu, gắn " + kind + ", bấm Save & Unpublish", () -> {
            form.fillRequired(name);
            if ("thumbnail".equals(kind)) {
                MediaLibraryModal library = form.openThumbnailLibrary();
                library.selectFile(0);
                chosenFile[0] = library.fileNameAt(0);
                library.addFiles();
                Allure.parameter("Ảnh đã chọn", chosenFile[0]);
                Assert.assertEquals(form.thumbnailPreviewCount(), 1, "Khung xem trước phải có 1 ảnh");
                Assert.assertEquals(form.thumbnailPreviewTitle(), chosenFile[0], "Khung xem trước phải là ảnh vừa chọn");
            } else {
                form.enterField(FormField.VIDEO_LINK, videoLink);
            }
            form.saveAndWait(SaveButton.UNPUBLISH);
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
        });

        verify("Trang sửa hiện đúng " + kind, () -> {
            ProductEditPage edit = openEditOf(new ProductListPage(), name, name);
            if ("thumbnail".equals(kind)) {
                Assert.assertEquals(edit.thumbnailPreviewCount(), 1, "Ô Thumbnail Image phải có 1 ảnh");
                Assert.assertEquals(edit.thumbnailPreviewTitle(), chosenFile[0], "Ô Thumbnail Image phải là đúng ảnh đã chọn");
            } else {
                Assert.assertEquals(edit.selectedOptionText(FormField.VIDEO_PROVIDER), "Youtube", "Video Provider");
                Assert.assertEquals(edit.fieldValue(FormField.VIDEO_LINK), videoLink, "Video Link");
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

        act("Nhập dữ liệu, nhấp đúp Save & Unpublish", () -> {
            form.fillRequired(name);
            form.doubleClickSave(SaveButton.UNPUBLISH);
        });

        verify("Không trang lỗi, đúng 1 dòng", () -> {
            Assert.assertFalse(form.isServerErrorPage(), "Không được ra trang lỗi");
            ProductListPage list = new ProductListPage();
            list.searchFor(name);
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

        ProductListPage list = act("Tạo sản phẩm với Name chứa mã", () -> form.createProduct(name, SaveButton.UNPUBLISH));
        verify("Thêm thành công, không trang lỗi", () -> {
            Assert.assertFalse(list.isServerErrorPage(), "Không được ra trang lỗi");
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
        });

        verify(StepNames.LIST_NAME_AS_TEXT, () -> {
            int row = list.findRow(token, name);
            Assert.assertFalse(list.isAlertOpen(), "Không được bật hộp thoại ở danh sách");
            Assert.assertTrue(row >= 0, "Danh sách phải hiện nguyên văn " + name + ", thực tế " + list.names());
        });
        verify(StepNames.EDIT_NAME_AS_TEXT, () -> {
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

        ProductListPage list = act("Tạo sản phẩm tên có emoji", () -> form.createProduct(name, SaveButton.UNPUBLISH));

        verify(StepNames.LIST_SHOWS_EMOJI, () -> {
            list.searchFor(token);
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

        List<String> sequence = act("Từ ô Product Name nhấn Tab 4 lần", () -> form.tabSequenceFromName(4));

        verify("Focus đi lần lượt Category → Brand → Unit → Weight, có viền khi focus", () -> {
            Allure.parameter("Thứ tự focus", String.join(" → ", sequence));
            Assert.assertEquals(sequence, FormField.names(FormField.CATEGORY, FormField.BRAND, FormField.UNIT, FormField.WEIGHT), "Thứ tự focus sau ô Product Name");
            Assert.assertEquals(form.fieldsWithoutVisibleFocus(), List.of(), "Ô không đổi gì khi được focus");
        });
    }
}
