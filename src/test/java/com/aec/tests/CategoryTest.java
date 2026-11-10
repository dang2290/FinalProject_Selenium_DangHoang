package com.aec.tests;

import com.aec.pages.CategoryFormPage;
import com.aec.pages.CategoryListPage;
import com.aec.pages.DashboardPage;
import com.aec.pages.ProductCreatePage;
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

/** Vòng đời danh mục: tạo · sửa · xoá · cây cha–con · liên kết Product · phiên. TC ở docs/testcases/category/web/test_cases_category_web.md. */
@Feature("Manage Category")
@Story("Tạo · sửa · xoá danh mục")
public class CategoryTest extends CategoryTestBase {

    @Test(groups = {"smoke", "category", "auth"}, description = "Tạo danh mục gốc chỉ với Name rồi tìm lại được trong danh sách")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tạo danh mục chỉ nhập Name thì báo thành công, về All categories, tìm lại được đúng 1 dòng: Parent —, Order Level 0, Level 0, Banner —, Icon —, Featured tắt.")
    public void testCreateRootCategory() {
        tc("AEC_CAT_TC_006", "smoke", "category", "auth");
        String name = "AutoCat_" + newToken("TC006");
        loginAsAdmin();
        CategoryFormPage form = new CategoryFormPage(driver());
        CategoryListPage list = new CategoryListPage(driver());

        Allure.step("Act: Mở form tạo, nhập Name, bấm Save", () -> {
            form.openCreate();
            form.enterName(name);
            form.saveAndWait();
        });

        Allure.step("Assert: Thông báo thành công, về All categories", () -> {
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Thông báo sau khi tạo");
            Assert.assertEquals(form.currentPath(), "/admin/categories", "Phải về trang All categories");
        });

        int row = Allure.step("Act: Tìm danh mục vừa tạo", () -> {
            list.search(name);
            return list.rowIndexOf(name);
        });

        Allure.step("Assert: Đúng 1 dòng với giá trị mặc định", () -> {
            Assert.assertEquals(list.rowCount(), 1, "Số dòng tìm được");
            Assert.assertEquals(row, 0, "Dòng mang tên danh mục test");
            Assert.assertEquals(list.parentOf(row), EMPTY_CELL, "Parent Category");
            Assert.assertEquals(list.orderLevelOf(row), "0", "Order Level");
            Assert.assertEquals(list.levelOf(row), "0", "Level");
            Assert.assertEquals(list.bannerOf(row), EMPTY_CELL, "Banner");
            Assert.assertEquals(list.iconOf(row), EMPTY_CELL, "Icon");
            Assert.assertFalse(list.isFeaturedOn(row), "Featured phải đang tắt");
        });
    }

    @Test(groups = {"smoke", "category", "auth"}, description = "Sửa Name danh mục và thấy tên mới trong danh sách")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra đổi Name ở form sửa thì báo cập nhật thành công, vẫn ở trang sửa với Name mới; danh sách tìm tên mới ra 1 dòng, tên cũ không còn.")
    public void testRenameCategory() {
        tc("AEC_CAT_TC_007", "smoke", "category", "auth");
        String token = newToken("TC007");
        String oldName = "AutoCat_" + token;
        String newName = "AutoCatEd_" + token;
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(oldName);
        CategoryFormPage form = openEditOf(list, oldName);

        Allure.step("Act: Đổi Name rồi bấm Save", () -> {
            form.enterName(newName);
            form.saveAndWait();
        });

        Allure.step("Assert: Thông báo cập nhật, vẫn ở trang sửa với Name mới", () -> {
            Assert.assertEquals(form.getNotifyMessage(), UPDATED, "Thông báo sau khi sửa");
            Assert.assertTrue(form.isOnEditPage(), "Phải vẫn ở trang sửa: " + form.currentUrl());
            Assert.assertEquals(form.nameValue(), newName, "Ô Name sau khi lưu");
        });

        Allure.step("Assert: Tìm tên mới ra 1 dòng, tên cũ không còn", () -> {
            Assert.assertTrue(findRow(list, newName) >= 0, "Phải tìm thấy tên mới");
            Assert.assertEquals(list.rowCount(), 1, "Số dòng mang tên mới");
            Assert.assertEquals(findRow(list, oldName), -1, "Không được còn dòng mang tên cũ");
        });
    }

    @Test(groups = {"smoke", "category", "auth"}, description = "Xoá danh mục test qua hộp xác nhận")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra nút thùng rác có chú thích Delete, hộp xác nhận đúng nội dung, bấm Delete thì báo xoá thành công, về All categories và không còn danh mục test.")
    public void testDeleteCategoryViaConfirmation() {
        tc("AEC_CAT_TC_008", "smoke", "category", "auth");
        String name = "AutoCat_" + newToken("TC008");
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);

        Allure.step("Act: Tìm danh mục test", () -> findRow(list, name));
        Allure.step("Assert: Rê chuột lên thùng rác hiện Delete", () ->
                Assert.assertEquals(list.deleteTooltipOfFirstRow(), "Delete", "Chú thích nút thùng rác"));

        Allure.step("Act: Bấm nút thùng rác", () -> list.openDeleteOfRow(list.rowIndexOf(name)));
        Allure.step("Assert: Hộp xác nhận đúng nội dung", () -> {
            Assert.assertEquals(list.deleteModalTitle(), "Delete Confirmation", "Tiêu đề hộp");
            Assert.assertEquals(list.deleteModalText(), "Are you sure to delete this?", "Câu hỏi xác nhận");
            Assert.assertEquals(list.deleteModalButtons(), List.of("Cancel", "Delete"), "Nút trong hộp");
        });

        Allure.step("Act: Bấm Delete", list::confirmDelete);
        Allure.step("Assert: Thông báo xoá thành công, về All categories", () -> {
            Assert.assertEquals(list.getNotifyMessage(), DELETED, "Thông báo sau khi xoá");
            Assert.assertEquals(list.currentPath(), "/admin/categories", "Phải về trang All categories");
        });

        Allure.step("Assert: Không còn danh mục test", () ->
                Assert.assertEquals(findRow(list, name), -1, "Danh mục đã xoá vẫn còn"));
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Cột Parent Category và Level hiển thị đúng theo cây cha–con")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tạo cây gốc → con → cháu qua ô Parent Category thì danh sách hiện Parent và Level lần lượt: — / 0, tên gốc / 1, tên con / 2.")
    public void testParentAndLevelColumns() {
        tc("AEC_CAT_TC_012", "regression", "category", "auth");
        String token = newToken("TC012");
        String root = "AutoCatP_" + token;
        String child = "AutoCatC_" + token;
        String grandchild = "AutoCatG_" + token;
        CategoryListPage list = openCategoriesAsAdmin();

        Allure.step("Act: Tạo gốc → con → cháu", () -> {
            createCategory(root);
            createChildCategory(child, root);
            createChildCategory(grandchild, child);
        });

        Allure.step("Assert: (a) Gốc — Parent —, Level 0", () -> assertParentAndLevel(list, root, EMPTY_CELL, "0"));
        Allure.step("Assert: (b) Con — Parent là gốc, Level 1", () -> assertParentAndLevel(list, child, root, "1"));
        Allure.step("Assert: (c) Cháu — Parent là con, Level 2", () -> assertParentAndLevel(list, grandchild, child, "2"));
    }

    private void assertParentAndLevel(CategoryListPage list, String name, String parent, String level) {
        int row = findRow(list, name);
        Assert.assertTrue(row >= 0, "Phải tìm thấy " + name);
        Assert.assertEquals(list.parentOf(row), parent, "Parent Category của " + name);
        Assert.assertEquals(list.levelOf(row), level, "Level của " + name);
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Huỷ xoá trong hộp xác nhận thì danh mục còn nguyên")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bấm thùng rác rồi Cancel thì hộp đóng, không có thông báo, danh mục test vẫn còn.")
    public void testCancelDeleteKeepsCategory() {
        tc("AEC_CAT_TC_017", "regression", "category", "auth");
        String name = "AutoCat_" + newToken("TC017");
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);

        Allure.step("Act: Tìm danh mục test, bấm thùng rác", () -> list.openDeleteOfRow(findRow(list, name)));
        Allure.step("Assert: Hộp xác nhận hiện câu hỏi", () ->
                Assert.assertEquals(list.deleteModalText(), "Are you sure to delete this?", "Câu hỏi xác nhận"));

        Allure.step("Act: Bấm Cancel", () -> list.closeDeleteModal(CategoryListPage.CloseBy.CANCEL));
        Allure.step("Assert: Hộp đóng, dòng danh mục test vẫn hiện", () -> {
            Assert.assertTrue(list.waitDeleteModalClosed(), "Hộp xác nhận phải đóng");
            Assert.assertTrue(list.rowIndexOf(name) >= 0, "Dòng danh mục test vẫn phải hiện");
        });

        Allure.step("Assert: Tìm lại vẫn còn", () -> Assert.assertTrue(findRow(list, name) >= 0, "Danh mục test phải còn"));
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Xoá danh mục cha đang có danh mục con không để lại con mồ côi")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra xoá danh mục cha khi đang có con thì hệ thống chặn xoá hoặc xoá kèm con — không được để con còn lại mà cha đã mất. Expected chưa có bằng chứng (@NeedsVerify).")
    public void testDeleteParentLeavesNoOrphan() {
        tc("AEC_CAT_TC_018", "regression", "category", "auth");
        String token = newToken("TC018");
        String parent = "AutoCatP_" + token;
        String child = "AutoCatC_" + token;
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(parent);
        createChildCategory(child, parent);

        Allure.step("Act: Tìm danh mục cha, bấm thùng rác → Delete", () -> list.deleteRow(findRow(list, parent)));

        Allure.step("Assert: Con không bị mồ côi", () -> {
            boolean parentExists = findRow(list, parent) >= 0;
            int childRow = findRow(list, child);
            if (childRow >= 0) {
                Assert.assertTrue(parentExists, "Con còn lại nhưng cha đã bị xoá");
                Assert.assertEquals(list.parentOf(childRow), parent, "Parent Category của con");
            }
        });
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Đổi tên và xoá danh mục được phản ánh ở form Add New Product")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra đổi tên danh mục thì ô Category của Add New Product có tên mới, không còn tên cũ; xoá danh mục thì không còn tên mới. Không bấm Save ở form sản phẩm.")
    public void testProductCategoryFollowsRenameAndDelete() {
        tc("AEC_CAT_TC_040", "regression", "category", "auth");
        String token = newToken("TC040");
        String oldName = "AutoCat_" + token;
        String newName = "AutoCatRen_" + token;
        CategoryListPage list = openCategoriesAsAdmin();
        ProductCreatePage productPage = new ProductCreatePage(driver());
        createCategory(oldName);

        Allure.step("Act: Đổi Name rồi Save", () -> {
            CategoryFormPage form = openEditOf(list, oldName);
            form.enterName(newName);
            form.saveAndWait();
        });

        Allure.step("Assert: Add New Product có tên mới, không còn tên cũ", () ->
                Assert.assertEquals(productPage.open().categoryOptionsContaining(token), List.of(newName), "Ô Category sau khi đổi tên"));

        Allure.step("Act: Xoá danh mục qua thùng rác → Delete", () -> list.deleteRow(findRow(list, newName)));

        Allure.step("Assert: Add New Product không còn tên mới", () ->
                Assert.assertEquals(productPage.open().categoryOptionsContaining(token), List.of(), "Ô Category sau khi xoá"));
    }

    @DataProvider(name = "injectionNames", parallel = true)
    public Object[][] injectionNames() {
        return new Object[][]{
                {"a", "<script>alert(1)</script>"},
                {"b", "<img src=x onerror=alert(1)>"},
                {"c", "' OR '1'='1 -- "}
        };
    }

    @Test(dataProvider = "injectionNames", groups = {"regression", "category", "auth", "security"},
            description = "Mã script và chuỗi SQL ở Name được lưu như chữ thường, không chạy")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name chứa script, thẻ img onerror, chuỗi SQL: lưu thành công hoặc bị từ chối, không trang lỗi; nếu đã lưu thì danh sách, form sửa, ô Category của Add New Product hiện nguyên văn dạng chữ, không bật hộp thoại.")
    public void testInjectionNameNotExecuted(String variant, String payload) {
        tc("AEC_CAT_TC_041", "regression", "category", "auth", "security");
        String token = newToken("TC041" + variant);
        String name = payload + token;
        Allure.parameter("Biến thể", variant + " — " + name);
        loginAsAdmin();
        CategoryFormPage form = new CategoryFormPage(driver());
        CategoryListPage list = new CategoryListPage(driver());

        Allure.step("Act: Tạo danh mục với Name chứa mã", () -> {
            form.openCreate();
            form.enterName(name);
            form.saveAndWait();
        });

        boolean saved = Allure.step("Assert: Không bật hộp thoại, không trang lỗi", () -> {
            Assert.assertFalse(form.isAlertOpen(), "Không được bật hộp thoại sau khi lưu");
            Assert.assertFalse(form.isServerErrorPage(), "Không được ra trang lỗi");
            return INSERTED.equals(form.getNotifyMessage());
        });
        if (!saved) {
            return;
        }

        Allure.step("Assert: Danh sách hiện nguyên văn, không bật hộp thoại", () -> {
            list.open();
            list.search(token);
            Assert.assertFalse(list.isAlertOpen(), "Không được bật hộp thoại ở danh sách");
            Assert.assertEquals(list.names(), List.of(name), "Tên ở danh sách");
        });

        Allure.step("Assert: Form sửa đúng nguyên văn", () -> {
            CategoryFormPage edit = list.editRow(0);
            Assert.assertFalse(edit.isAlertOpen(), "Không được bật hộp thoại ở form sửa");
            Assert.assertEquals(edit.nameValue(), name, "Ô Name ở form sửa");
        });

        Allure.step("Assert: Ô Category của Add New Product hiện nguyên văn", () -> {
            ProductCreatePage productPage = new ProductCreatePage(driver()).open();
            Assert.assertFalse(productPage.isAlertOpen(), "Không được bật hộp thoại ở form sản phẩm");
            Assert.assertEquals(productPage.categoryOptionsContaining(token), List.of(name), "Lựa chọn trong ô Category");
        });
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Bấm Save liên tiếp chỉ tạo một danh mục")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra bấm đúp nút Save của form tạo thì chỉ có đúng 1 danh mục được tạo.")
    public void testDoubleClickSaveCreatesOneCategory() {
        tc("AEC_CAT_TC_043", "regression", "category", "auth");
        String name = "AutoCatDbl_" + newToken("TC043");
        loginAsAdmin();
        CategoryFormPage form = new CategoryFormPage(driver());
        CategoryListPage list = new CategoryListPage(driver());

        Allure.step("Act: Nhập Name rồi bấm đúp Save", () -> {
            form.openCreate();
            form.enterName(name);
            form.doubleClickSave();
        });

        Allure.step("Assert: Đúng 1 dòng", () -> {
            list.open();
            list.search(name);
            Assert.assertEquals(list.names(), List.of(name), "Không được tạo trùng danh mục");
        });
    }

    @DataProvider(name = "expiredSessionForms", parallel = true)
    public Object[][] expiredSessionForms() {
        return new Object[][]{{"a", "create"}, {"b", "edit"}};
    }

    @Test(dataProvider = "expiredSessionForms", groups = {"regression", "category", "auth", "security"},
            description = "Hết phiên đăng nhập khi đang điền form thì không lưu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tab 1 đang điền form tạo/sửa danh mục, tab 2 đăng xuất, tab 1 bấm Save thì ra 419 PAGE EXPIRED hoặc về /login; đăng nhập lại thấy dữ liệu không đổi.")
    public void testExpiredSessionDoesNotSave(String variant, String formType) {
        tc("AEC_CAT_TC_044", "regression", "category", "auth", "security");
        Allure.parameter("Biến thể", variant + " — form " + formType);
        String token = newToken("TC044" + variant);
        String originalName = "AutoCat_" + token;
        String typedName = ("create".equals(formType) ? "AutoCatSess_" : "AutoCatSessEd_") + token;
        CategoryListPage list = openCategoriesAsAdmin();
        CategoryFormPage form = Allure.step("Arrange: Tab 1 mở form " + formType + ", nhập Name", () -> {
            CategoryFormPage page;
            if ("create".equals(formType)) {
                page = new CategoryFormPage(driver()).openCreate();
            } else {
                createCategory(originalName);
                page = openEditOf(list, originalName);
            }
            page.enterName(typedName);
            return page;
        });
        String formTab = driver().getWindowHandle();

        Allure.step("Act: Tab 2 đăng xuất", () -> {
            driver().switchTo().newWindow(WindowType.TAB);
            new DashboardPage(driver()).open().logout();
            driver().close();
            driver().switchTo().window(formTab);
        });

        Allure.step("Act: Tab 1 bấm Save", form::clickSave);

        Allure.step("Assert: 419 PAGE EXPIRED hoặc về /login, không báo thành công", () -> {
            form.waitForPath("/login");
            Assert.assertTrue(form.isPageExpired() || "/login".equals(form.currentPath()),
                    "Gửi form sau khi hết phiên phải bị chặn: " + form.currentUrl());
        });

        Allure.step("Assert: Đăng nhập lại — dữ liệu không đổi", () -> {
            loginAsAdmin();
            Assert.assertEquals(findRow(list, typedName), -1, "Không được có danh mục mang tên vừa nhập");
            if (!"create".equals(formType)) {
                Assert.assertTrue(findRow(list, originalName) >= 0, "Danh mục test phải giữ tên cũ");
            }
        });
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Hai tab cùng sửa một danh mục thì bản lưu sau được giữ, không lỗi")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra mở form sửa cùng một danh mục ở 2 tab, tab 1 lưu tên 1 rồi tab 2 lưu tên 2 thì cả hai báo cập nhật thành công, danh sách còn 1 dòng mang tên 2.")
    public void testConcurrentEditLastSaveWins() {
        tc("AEC_CAT_TC_045", "regression", "category", "auth");
        String token = newToken("TC045");
        String name = "AutoCat_" + token;
        String firstName = "AutoCatT1_" + token;
        String secondName = "AutoCatT2_" + token;
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);
        String editUrl = openEditOf(list, name).currentUrl();
        String firstTab = driver().getWindowHandle();

        String secondTab = Allure.step("Arrange: Mở form sửa cùng danh mục ở tab thứ 2", () -> {
            driver().switchTo().newWindow(WindowType.TAB);
            new CategoryFormPage(driver()).openUrl(editUrl);
            return driver().getWindowHandle();
        });

        Allure.step("Act: Tab 1 đổi Name thành giá trị 1 rồi Save", () -> {
            driver().switchTo().window(firstTab);
            CategoryFormPage form = new CategoryFormPage(driver());
            form.enterName(firstName);
            form.saveAndWait();
            Assert.assertEquals(form.getNotifyMessage(), UPDATED, "Tab 1 phải lưu thành công");
        });

        Allure.step("Act: Tab 2 đổi Name thành giá trị 2 rồi Save", () -> {
            driver().switchTo().window(secondTab);
            CategoryFormPage form = new CategoryFormPage(driver());
            form.enterName(secondName);
            form.saveAndWait();
            Assert.assertFalse(form.isServerErrorPage(), "Tab 2 không được ra trang lỗi");
            Assert.assertEquals(form.getNotifyMessage(), UPDATED, "Tab 2 phải lưu thành công");
        });

        Allure.step("Assert: Danh sách còn 1 dòng mang giá trị 2", () -> {
            list.open();
            list.search(token);
            Assert.assertEquals(list.names(), List.of(secondName), "Lần lưu sau phải thắng");
        });
    }
}
