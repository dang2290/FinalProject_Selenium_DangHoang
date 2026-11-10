package com.aec.tests;

import com.aec.pages.DashboardPage;
import com.aec.pages.ProductCreatePage;
import com.aec.pages.ProductCreatePage.SaveButton;
import com.aec.pages.ProductEditPage;
import com.aec.pages.ProductListPage;
import com.aec.pages.ProductListPage.RowSwitch;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.openqa.selenium.WindowType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/** Trang Edit Product, công tắc trên dòng, Duplicate, xoá, xoá hàng loạt, luồng trọn vẹn, hết phiên. TC ở docs/testcases/product/web/parts/. */
@Feature("Product")
@Story("Sửa, xoá & trạng thái sản phẩm")
public class ProductEditTest extends ProductTestBase {

    @Test(groups = {"smoke", "product", "auth", "ui"}, description = "Trang Edit Product đủ thành phần")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra nút Edit mở /admin/products/admin/<id>/edit?lang=en với tiêu đề Edit Product, tab English / Vietnamese, ô Product Name đúng tên, ô Slug có giá trị, một nút Update Product.")
    public void testEditPageElements() {
        tc("AEC_PROD_TC_003", "smoke", "product", "auth", "ui");
        String name = "AutoProd_" + newToken("TC003");
        loginAsAdmin();
        createProduct(name);

        ProductEditPage edit = Allure.step("Act: Tìm sản phẩm test, bấm Edit", () -> openEditOf(new ProductListPage(driver()), name, name));

        Allure.step("Assert: Bảng kiểm trang sửa", () -> {
            Assert.assertTrue(edit.currentUrl().matches(".*/admin/products/admin/\\d+/edit\\?lang=en$"), "Đường dẫn: " + edit.currentUrl());
            Assert.assertTrue(edit.isTitleDisplayed(), "Tiêu đề Edit Product");
            Assert.assertEquals(edit.languageTabs(), List.of("English", "Vietnamese"), "Tab ngôn ngữ");
            Assert.assertEquals(edit.nameValue(), name, "Ô Product Name");
            Assert.assertFalse(edit.fieldValue("slug").isBlank(), "Ô Slug phải có giá trị");
            Assert.assertEquals(edit.updateButtonCount(), 1, "Số nút Update Product");
            Assert.assertEquals(edit.updateButtonText(), "Update Product", "Chữ trên nút");
        });
    }

    @Test(groups = {"smoke", "product", "auth", "criticalPath"}, description = "Sửa sản phẩm ở tab English")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra đổi Name và Unit price 20 rồi Update Product thì báo cập nhật thành công, vẫn ở trang sửa với giá trị mới; tìm tên mới ra 1 dòng Base Price $20.00.")
    public void testEditNameAndPrice() {
        tc("AEC_PROD_TC_009", "smoke", "product", "auth", "criticalPath");
        String token = newToken("TC009");
        String newName = "AutoProdEd_" + token;
        loginAsAdmin();
        createProduct("AutoProd_" + token);
        ProductListPage list = new ProductListPage(driver());
        ProductEditPage edit = openEditOf(list, "AutoProd_" + token, "AutoProd_" + token);

        Allure.step("Act: Đổi Name, Unit price 20, bấm Update Product", () -> {
            edit.enterName(newName);
            edit.enterField("unit_price", "20");
            edit.updateAndWait();
        });

        Allure.step("Assert: Cập nhật thành công, ở lại trang sửa với giá trị mới", () -> {
            Assert.assertEquals(edit.getNotifyMessage(), UPDATED, "Thông báo sau khi cập nhật");
            Assert.assertTrue(edit.isOnEditPage(), "Phải ở lại trang sửa: " + edit.currentUrl());
            Assert.assertEquals(edit.nameValue(), newName, "Ô Name");
            Assert.assertEquals(Double.parseDouble(edit.fieldValue("unit_price")), 20.0, "Ô Unit price");
        });
        Allure.step("Assert: Danh sách có tên mới, Base Price $20.00", () -> {
            int row = findRow(list, newName, newName);
            Assert.assertTrue(row >= 0, "Phải tìm thấy tên mới");
            Assert.assertEquals(list.rowIndexesContaining(newName).size(), 1, "Đúng 1 dòng");
            Assert.assertEquals(list.basePriceOfRow(row), "$20.00", "Base Price");
        });
    }

    @Test(groups = {"smoke", "product", "auth", "criticalPath"}, description = "Huỷ và xác nhận xoá sản phẩm")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra hộp Delete Confirmation hiện câu hỏi, nút Cancel / Delete; a: Cancel thì hộp đóng, sản phẩm còn; b: mở lại, Delete thì báo xoá thành công và tìm lại ra Nothing found.")
    public void testCancelThenDeleteProduct() {
        tc("AEC_PROD_TC_010", "smoke", "product", "auth", "criticalPath");
        String name = "AutoProd_" + newToken("TC010");
        loginAsAdmin();
        createProduct(name);
        ProductListPage list = new ProductListPage(driver());
        int row = findRow(list, name, name);

        Allure.step("Act: Bấm nút Delete", () -> list.openDeleteOfRow(row));
        Allure.step("Assert: Hộp xác nhận đúng nội dung", () -> {
            Assert.assertEquals(list.deleteModalTitle(), "Delete Confirmation", "Tiêu đề hộp");
            Assert.assertEquals(list.deleteModalText(), "Are you sure to delete this?", "Câu hỏi xác nhận");
            Assert.assertEquals(list.deleteModalButtons(), List.of("Cancel", "Delete"), "Nút trong hộp");
        });

        Allure.step("Act (a): Bấm Cancel", () -> list.closeDeleteModal(ProductListPage.CloseBy.CANCEL));
        Allure.step("Assert (a): Hộp đóng, sản phẩm còn", () -> {
            Assert.assertTrue(list.waitDeleteModalClosed(), "Hộp phải đóng");
            Assert.assertTrue(list.rowIndexOf(name) >= 0, "Sản phẩm phải còn");
        });

        Allure.step("Act (b): Mở lại hộp, bấm Delete", () -> list.deleteRow(list.rowIndexOf(name)));
        Allure.step("Assert (b): Xoá thành công, tìm lại ra Nothing found", () -> {
            Assert.assertEquals(list.getNotifyMessage(), DELETED, "Thông báo sau khi xoá");
            list.search(name);
            Assert.assertTrue(list.isNothingFoundShown(), "Phải hiện Nothing found, thực tế " + list.names());
        });
    }

    @DataProvider(name = "rowSwitches", parallel = true)
    public Object[][] rowSwitches() {
        return new Object[][]{
                {"AEC_PROD_TC_019", RowSwitch.TODAYS_DEAL, "Todays Deal updated successfully"},
                {"AEC_PROD_TC_020", RowSwitch.PUBLISHED, "Published products updated successfully"},
                {"AEC_PROD_TC_021", RowSwitch.FEATURED, "Featured products updated successfully"}};
    }

    @Test(dataProvider = "rowSwitches", groups = {"regression", "product", "auth"}, description = "Công tắc trên dòng lưu ngay")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bấm công tắc Todays Deal (TC_019), Published (TC_020), Featured (TC_021) của sản phẩm test thì báo cập nhật thành công mà trang không tải lại, tải lại trang công tắc vẫn bật. TC_020 bấm thêm lần nữa thì tắt và giữ tắt sau khi tải lại.")
    public void testRowSwitchSavedImmediately(String testId, RowSwitch rowSwitch, String message) {
        tc(testId, "regression", "product", "auth");
        Allure.parameter("Công tắc", rowSwitch.name());
        String token = newToken("TC" + testId.substring(testId.length() - 3));
        String name = "AutoProd_" + token;
        loginAsAdmin();
        createProduct(name);
        ProductListPage list = new ProductListPage(driver());
        int row = findRow(list, name, name);

        Allure.step("Act: Bấm công tắc " + rowSwitch, () -> {
            list.markPage();
            list.toggleSwitch(row, rowSwitch);
        });
        Allure.step("Assert: Báo cập nhật, trang không tải lại, công tắc bật", () -> {
            Assert.assertEquals(list.getNotifyMessage(), message, "Thông báo");
            Assert.assertTrue(list.isSamePageInstance(), "Trang không được tải lại");
            Assert.assertTrue(list.isSwitchOn(row, rowSwitch), "Công tắc phải bật");
        });
        Allure.step("Assert: Tải lại trang — công tắc vẫn bật", () ->
                Assert.assertTrue(list.isSwitchOn(findRow(list, name, name), rowSwitch), "Công tắc phải giữ bật"));

        if (rowSwitch == RowSwitch.PUBLISHED) {
            int again = list.rowIndexOf(name);
            Allure.step("Act: Bấm công tắc lần nữa", () -> list.toggleSwitch(again, rowSwitch));
            Allure.step("Assert: Báo cập nhật, công tắc tắt, tải lại vẫn tắt", () -> {
                Assert.assertEquals(list.getNotifyMessage(), message, "Thông báo lần 2");
                Assert.assertFalse(list.isSwitchOn(findRow(list, name, name), rowSwitch), "Công tắc phải giữ tắt");
            });
        }
    }

    @Test(groups = {"regression", "product", "auth", "validation"}, description = "Sửa Name thành khoảng trắng bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra thay Name bằng 3 dấu cách rồi Update Product thì khối đỏ ghi Product name is required và mở lại trang sửa Name vẫn là tên cũ. Ghi id để dọn nếu tên lỡ bị lưu rỗng.")
    public void testEditWhitespaceNameRejected() {
        tc("AEC_PROD_TC_053", "regression", "product", "auth", "validation");
        String name = "AutoProd_" + newToken("TC053");
        loginAsAdmin();
        createProduct(name);
        ProductEditPage edit = openEditOf(new ProductListPage(driver()), name, name);
        String id = edit.productId();
        trackProductId(id);

        Allure.step("Act: Thay Name bằng khoảng trắng, bấm Update Product", () -> {
            edit.enterName("   ");
            edit.updateAndWait();
        });

        Allure.step("Assert: Khối đỏ báo Name bắt buộc", () ->
                Assert.assertTrue(edit.errorMessages().contains(NAME_REQUIRED), "Khối lỗi: " + edit.errorMessages()));
        Allure.step("Assert: Mở lại — Name vẫn là tên cũ", () ->
                Assert.assertEquals(edit.openById(id, "en").nameValue(), name, "Ô Name"));
    }

    @Test(groups = {"regression", "product", "auth", "validation"}, description = "Sửa Discount lớn hơn giá bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ở trang sửa nhập Discount 25 (giá 10) rồi Update Product thì khối đỏ ghi The discount must be less than 10. và Discount không được lưu.")
    public void testEditDiscountTooHighRejected() {
        tc("AEC_PROD_TC_054", "regression", "product", "auth", "validation");
        String name = "AutoProd_" + newToken("TC054");
        loginAsAdmin();
        createProduct(name);
        ProductEditPage edit = openEditOf(new ProductListPage(driver()), name, name);
        String id = edit.productId();

        Allure.step("Act: Discount 25, bấm Update Product", () -> {
            edit.enterField("discount", "25");
            edit.updateAndWait();
        });

        Allure.step("Assert: Khối đỏ báo lỗi, không lưu", () -> {
            Assert.assertTrue(edit.errorMessages().contains("The discount must be less than 10."), "Khối lỗi: " + edit.errorMessages());
            Assert.assertEquals(Double.parseDouble(edit.openById(id, "en").fieldValue("discount")), 0.0, "Discount không được lưu");
        });
    }

    @Test(groups = {"regression", "product", "auth", "localization", "knownIssue"}, description = "Lưu tên tiếng Việt ở tab Vietnamese")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ở tab Vietnamese đổi Name rồi Update Product thì báo cập nhật, tab Vietnamese hiện tên mới, tab English giữ tên cũ. Đang lệch: ra trang Something went wrong!, không lưu (AMB-PROD-06).")
    public void testVietnameseTabName() {
        tc("AEC_PROD_TC_055", "regression", "product", "auth", "localization", "knownIssue");
        String token = newToken("TC055");
        String name = "AutoProd_" + token;
        String vietnamese = "Sản phẩm thử " + token;
        loginAsAdmin();
        createProduct(name);
        ProductEditPage edit = openEditOf(new ProductListPage(driver()), name, name);

        Allure.step("Act: Tab Vietnamese, đổi Name, bấm Update Product", () -> {
            edit.clickTab("Vietnamese");
            edit.enterName(vietnamese);
            edit.updateAndWait();
        });

        Allure.step("Assert: Cập nhật thành công ở tab Vietnamese", () -> {
            Assert.assertFalse(edit.isServerErrorPage(), "Không được ra trang Something went wrong!");
            Assert.assertEquals(edit.getNotifyMessage(), UPDATED, "Thông báo sau khi cập nhật");
            Assert.assertEquals(edit.activeTab(), "Vietnamese", "Tab đang chọn");
            Assert.assertEquals(edit.nameValue(), vietnamese, "Ô Name tab Vietnamese");
        });
        Allure.step("Assert: Tab English giữ tên cũ", () -> {
            edit.clickTab("English");
            Assert.assertEquals(edit.nameValue(), name, "Ô Name tab English");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Duplicate tạo bản sao giữ nguyên trạng thái")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra sản phẩm test đã bật Published, bấm Duplicate thì không có hộp hỏi lại, báo Product has been duplicated successfully; có 2 dòng cùng tên, đều Published bật; Slug bản sao khác bản gốc (AMB-PROD-15).")
    public void testDuplicateKeepsState() {
        tc("AEC_PROD_TC_056", "regression", "product", "auth");
        String token = newToken("TC056");
        String name = "AutoProd_" + token;
        loginAsAdmin();
        createProduct(name, SaveButton.PUBLISH);
        ProductListPage list = new ProductListPage(driver());
        int row = findRow(list, token, name);

        Allure.step("Act: Bấm Duplicate", () -> list.duplicateRow(row));

        Allure.step("Assert: Không hộp hỏi lại, báo nhân bản thành công", () -> {
            Assert.assertFalse(list.isDeleteModalShown(), "Không được có hộp hỏi lại");
            Assert.assertEquals(list.getNotifyMessage(), DUPLICATED, "Thông báo sau khi Duplicate");
        });
        Allure.step("Assert: 2 dòng cùng tên, đều Published bật, Slug khác nhau", () -> {
            list.open();
            list.search(token);
            Assert.assertEquals(list.names(), List.of(name, name), "Bản gốc và bản sao");
            Assert.assertTrue(list.isSwitchOn(0, RowSwitch.PUBLISHED) && list.isSwitchOn(1, RowSwitch.PUBLISHED), "Cả hai phải Published bật");
            String first = list.editRow(0).fieldValue("slug");
            list.open();
            list.search(token);
            String second = list.editRow(1).fieldValue("slug");
            Assert.assertNotEquals(first, second, "Slug bản sao phải khác bản gốc");
        });
    }

    @Test(groups = {"regression", "product", "auth", "knownIssue"}, description = "Delete selection phải hỏi lại trước khi xoá")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tìm hậu tố chung chỉ ra 2 sản phẩm test, tick đúng 2 dòng đó, Bulk Action → Delete selection thì phải hiện hộp xác nhận trước khi xoá. Đang lệch: xoá ngay, không hỏi lại (AMB-PROD-07). ⛔ PC-4: dừng nếu kết quả tìm có dòng không phải dữ liệu test.")
    public void testBulkDeleteAsksConfirmation() {
        tc("AEC_PROD_TC_057", "regression", "product", "auth", "knownIssue");
        String token = newToken("TC057");
        loginAsAdmin();
        createProduct("AutoProdA_" + token);
        createProduct("AutoProdB_" + token);
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Arrange: Tìm hậu tố chung, tick 2 dòng test", () -> {
            list.open();
            list.search(token);
            List<String> names = list.names();
            Assert.assertEquals(names.size(), 2, "Kết quả tìm phải chỉ có 2 dòng test: " + names);
            Assert.assertTrue(names.stream().allMatch(n -> n.contains(token)), "Mọi dòng phải là dữ liệu test: " + names);
            list.tickRows(List.of(0, 1));
            Assert.assertEquals(list.tickedIds(), List.of(list.idOfRow(0), list.idOfRow(1)), "Chỉ được tick 2 dòng test");
        });

        Allure.step("Act: Bulk Action → Delete selection", list::clickDeleteSelection);

        Allure.step("Assert: Hiện hộp xác nhận trước khi xoá", () ->
                Assert.assertTrue(list.waitConfirmOrReload(), "Phải hỏi lại trước khi xoá — thực tế trang tải lại ngay"));
    }

    @Test(groups = {"regression", "product", "auth", "e2e"}, description = "Vòng đời sản phẩm")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra tạo sản phẩm (Save & Unpublish), tìm ra 1 dòng, sửa Name ở tab English (báo cập nhật), tìm tên mới ra 1 dòng, xoá qua hộp xác nhận (báo xoá), tìm lại ra Nothing found.")
    public void testProductLifecycle() {
        tc("AEC_PROD_TC_065", "regression", "product", "auth", "e2e");
        String token = newToken("TC065");
        String name = "AutoProdE2E_" + token;
        String newName = "AutoProdZ_" + token;
        loginAsAdmin();
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Act: Tạo sản phẩm " + name, () -> {
            ProductCreatePage form = new ProductCreatePage(driver()).open();
            form.fillRequired(name);
            form.saveAndWait(SaveButton.UNPUBLISH);
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi tạo");
        });
        Allure.step("Act + Assert: Tìm tên ra đúng 1 dòng", () -> {
            list.open();
            list.search(name);
            Assert.assertEquals(list.names(), List.of(name), "Kết quả tìm");
        });

        Allure.step("Act + Assert: Sửa Name ở tab English", () -> {
            ProductEditPage edit = list.editRow(0);
            edit.enterName(newName);
            edit.updateAndWait();
            Assert.assertEquals(edit.getNotifyMessage(), UPDATED, "Thông báo sau khi sửa");
        });
        Allure.step("Act + Assert: Tìm tên mới ra đúng 1 dòng", () -> {
            list.open();
            list.search(newName);
            Assert.assertEquals(list.names(), List.of(newName), "Kết quả tìm");
        });

        Allure.step("Act + Assert: Xoá qua hộp xác nhận", () -> {
            list.deleteRow(0);
            Assert.assertEquals(list.getNotifyMessage(), DELETED, "Thông báo sau khi xoá");
        });
        Allure.step("Act + Assert: Tìm lại tên mới ra Nothing found", () -> {
            list.search(newName);
            Assert.assertTrue(list.isNothingFoundShown(), "Phải hiện Nothing found, thực tế " + list.names());
        });
    }

    @DataProvider(name = "expiredSessionForms", parallel = true)
    public Object[][] expiredSessionForms() {
        return new Object[][]{{"a", "create"}, {"b", "edit"}};
    }

    @Test(dataProvider = "expiredSessionForms", groups = {"regression", "product", "auth", "security"},
            description = "Hết phiên khi đang nhập thì không lưu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tab 1 đang nhập ở form tạo / trang sửa, tab 2 đăng xuất, tab 1 bấm lưu thì ra 419 PAGE EXPIRED hoặc về /login; đăng nhập lại: a không có sản phẩm mới, b giữ tên cũ.")
    public void testExpiredSessionDoesNotSave(String variant, String formType) {
        tc("AEC_PROD_TC_067", "regression", "product", "auth", "security");
        Allure.parameter("Biến thể", variant + " — " + formType);
        String token = newToken("TC067" + variant);
        String originalName = "AutoProd_" + token;
        String typedName = ("create".equals(formType) ? "AutoProdSs_" : "AutoProdEd_") + token;
        loginAsAdmin();
        ProductCreatePage form = new ProductCreatePage(driver());
        ProductEditPage edit = new ProductEditPage(driver());
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Arrange: Tab 1 mở " + formType + ", nhập dữ liệu", () -> {
            if ("create".equals(formType)) {
                form.open();
                form.fillRequired(typedName);
            } else {
                createProduct(originalName);
                openEditOf(list, originalName, originalName).enterName(typedName);
            }
        });
        String formTab = driver().getWindowHandle();

        Allure.step("Act: Tab 2 đăng xuất", () -> {
            driver().switchTo().newWindow(WindowType.TAB);
            new DashboardPage(driver()).open().logout();
            driver().close();
            driver().switchTo().window(formTab);
        });

        Allure.step("Act: Tab 1 bấm lưu", () -> {
            if ("create".equals(formType)) {
                form.clickSave(SaveButton.UNPUBLISH);
            } else {
                edit.clickUpdate();
            }
        });

        Allure.step("Assert: 419 PAGE EXPIRED hoặc về /login", () -> {
            edit.waitForPath("/login");
            Assert.assertTrue(edit.isPageExpired() || "/login".equals(edit.currentPath()),
                    "Gửi form sau khi hết phiên phải bị chặn: " + edit.currentUrl());
        });

        Allure.step("Assert: Đăng nhập lại — dữ liệu không đổi", () -> {
            loginAsAdmin();
            Assert.assertEquals(findRow(list, typedName, typedName), -1, "Không được có sản phẩm mang tên vừa nhập");
            if (!"create".equals(formType)) {
                Assert.assertTrue(findRow(list, originalName, originalName) >= 0, "Sản phẩm test phải giữ tên cũ");
            }
        });
    }

    @Test(groups = {"regression", "product", "auth", "accessibility", "knownIssue"}, description = "Đóng hộp xoá sản phẩm bằng phím Esc")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể c: mở hộp xoá của sản phẩm test rồi nhấn Esc thì hộp đóng, sản phẩm còn. Đang lệch: Esc không đóng hộp — cùng hành vi ở Brand, Category, Attribute, Colors.")
    public void testEscClosesDeleteConfirmation() {
        tc("AEC_PROD_TC_072", "regression", "product", "auth", "accessibility", "knownIssue");
        Allure.parameter("Biến thể", "c — Esc");
        String name = "AutoProd_" + newToken("TC072c");
        loginAsAdmin();
        createProduct(name);
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Act: Mở hộp xoá của sản phẩm test, nhấn Esc", () -> {
            list.openDeleteOfRow(findRow(list, name, name));
            list.closeDeleteModal(ProductListPage.CloseBy.ESC);
        });

        Allure.step("Assert: Hộp đóng, sản phẩm còn", () -> {
            Assert.assertTrue(list.waitDeleteModalClosed(), "Hộp Delete Confirmation phải đóng");
            Assert.assertTrue(findRow(list, name, name) >= 0, "Sản phẩm test phải còn");
        });
    }

    @Test(groups = {"regression", "product", "auth", "e2e"}, description = "Sản phẩm đã publish hiện ngoài cửa hàng")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra sản phẩm tạo bằng Save & Publish, bấm View ở All products thì mở /product/<slug> ở tab mới, trang hiện tên và giá $10.00. Phần bố cục trang cửa hàng kiểm tay (Partial).")
    public void testPublishedProductOnStorefront() {
        tc("AEC_PROD_TC_074", "regression", "product", "auth", "e2e");
        String name = "AutoProd_" + newToken("TC074");
        loginAsAdmin();
        createProduct(name, SaveButton.PUBLISH);
        ProductListPage list = new ProductListPage(driver());
        int row = findRow(list, name, name);
        String original = driver().getWindowHandle();

        Allure.step("Act: Bấm View", () -> {
            list.clickRowButtonAt(row, 0);
            list.switchToNewWindow(original);
        });

        Allure.step("Assert: Trang sản phẩm hiện tên và giá", () -> {
            Assert.assertTrue(list.currentPath().startsWith("/product/"), "Đường dẫn: " + list.currentUrl());
            String page = list.bodyText();
            Assert.assertTrue(page.contains(name), "Trang phải hiện tên sản phẩm");
            Assert.assertTrue(page.contains("$10.00"), "Trang phải hiện giá $10.00");
        });
        driver().close();
        driver().switchTo().window(original);
    }
}
