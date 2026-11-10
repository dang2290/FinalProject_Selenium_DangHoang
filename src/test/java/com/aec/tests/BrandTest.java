package com.aec.tests;

import com.aec.pages.BrandEditPage;
import com.aec.pages.BrandPage;
import com.aec.pages.MediaLibraryModal;
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

/** Vòng đời brand: tạo · sửa · xoá · liên kết Product. TC ở docs/testcases/brand/web/test_cases_brand_web.md. */
@Feature("Manage Brand")
@Story("Tạo · sửa · xoá brand")
public class BrandTest extends BrandTestBase {

    @Test(groups = {"smoke", "brand", "auth"}, description = "Tạo brand chỉ với Name rồi tìm lại được trong danh sách")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tạo brand chỉ nhập Name thì hiện thông báo thành công, form trống lại, tìm lại được đúng 1 dòng với logo là ảnh giữ chỗ.")
    public void testCreateBrandAndFindInList() {
        tc("AEC_BRAND_TC_006", "smoke", "brand", "auth");
        String name = "AutoBrand_" + newToken("TC006");
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name rồi bấm Save", () -> brandPage.createBrand(name));

        Allure.step("Assert: Thông báo tạo thành công, vẫn ở All Brands, form trống lại", () -> {
            Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Thông báo sau khi tạo brand");
            Assert.assertTrue(brandPage.isTitleDisplayed(), "Phải vẫn ở trang All Brands");
            Assert.assertEquals(brandPage.nameValue(), "", "Ô Name phải trống lại sau khi lưu");
        });

        Allure.step("Act: Tìm brand vừa tạo", () -> brandPage.search(name));

        Allure.step("Assert: Đúng 1 dòng mang tên brand, logo là ảnh giữ chỗ", () -> {
            Assert.assertEquals(brandPage.getResultNames(), List.of(name), "Kết quả tìm kiếm");
            Assert.assertTrue(brandPage.logoSrcOfFirstRow().endsWith("placeholder.jpg"), "Cột Logo phải là ảnh giữ chỗ");
        });
    }

    @Test(groups = {"smoke", "brand", "auth"}, description = "Tạo brand với đủ mọi trường rồi đối chiếu ở danh sách và form sửa")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tạo brand có Name, logo chọn từ thư viện (brandimg.png), Meta Title, Meta description thì danh sách hiện đúng logo và form sửa giữ đúng mọi giá trị.")
    public void testCreateBrandWithAllFields() {
        tc("AEC_BRAND_TC_007", "smoke", "brand", "auth");
        String name = "AutoBrand_" + newToken("TC007");
        String metaTitle = "Meta AutoBrand TC007";
        String metaDescription = "Mô tả thương hiệu tự động TC007";
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name", () -> brandPage.enterNewBrandName(name));

        MediaLibraryModal library = Allure.step("Act: Bấm Browse ở ô Logo, lọc brandimg, chọn ảnh đầu", () -> {
            MediaLibraryModal modal = brandPage.openLogoLibrary();
            modal.search("brandimg");
            modal.selectFile(0);
            return modal;
        });

        Allure.step("Assert: Ảnh được đánh dấu, chân hộp 1 File selected", () -> {
            Assert.assertEquals(library.selectedIndexes(), List.of(0), "Ảnh được đánh dấu");
            Assert.assertEquals(library.selectedCountText(), "1 File selected", "Chân hộp thư viện");
        });

        Allure.step("Act: Bấm Add Files", library::addFiles);

        Allure.step("Assert: Ô Logo báo 1 File selected và có ô xem trước", () -> {
            Assert.assertEquals(brandPage.logoAmountText(), "1 File selected", "Ô Logo");
            Assert.assertEquals(brandPage.logoPreviewCount(), 1, "Ô xem trước ảnh");
        });

        Allure.step("Act: Nhập Meta Title, Meta description rồi bấm Save", () -> {
            brandPage.enterMetaTitle(metaTitle);
            brandPage.enterMetaDescription(metaDescription);
            brandPage.saveAndWait();
        });

        Allure.step("Assert: Thông báo tạo thành công", () ->
                Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Thông báo sau khi tạo brand"));

        Allure.step("Act: Tìm brand vừa tạo", () -> brandPage.search(name));

        Allure.step("Assert: Đúng 1 dòng, cột Logo là ảnh vừa chọn", () -> {
            Assert.assertEquals(brandPage.getResultNames(), List.of(name), "Kết quả tìm kiếm");
            Assert.assertFalse(brandPage.logoSrcOfFirstRow().endsWith("placeholder.jpg"), "Cột Logo không được là ảnh giữ chỗ");
        });

        BrandEditPage editPage = Allure.step("Act: Bấm nút bút chì", brandPage::editFirstResult);

        Allure.step("Assert: Form sửa giữ đúng Name, logo, Meta Title, Meta description", () -> {
            Assert.assertEquals(editPage.nameValue(), name, "Name");
            Assert.assertEquals(editPage.logoPreviewCount(), 1, "Logo phải có 1 ảnh");
            Assert.assertEquals(editPage.logoPreviewTitle(), "brandimg.png", "Tên tệp logo");
            Assert.assertEquals(editPage.metaTitleValue(), metaTitle, "Meta Title");
            Assert.assertEquals(editPage.metaDescriptionValue(), metaDescription, "Meta description");
        });
    }

    @Test(groups = {"smoke", "brand", "auth"}, description = "Sửa Name ở tab English rồi danh sách hiện tên mới")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra đổi Name ở form sửa (tab English) thì hiện thông báo cập nhật thành công, tìm theo tên mới ra 1 dòng, tìm theo tên cũ không còn dòng nào.")
    public void testRenameBrand() {
        tc("AEC_BRAND_TC_008", "smoke", "brand", "auth");
        String token = newToken("TC008");
        String oldName = "AutoBrand_" + token;
        String newName = "AutoBrandRenamed_" + token;
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, oldName);
        BrandEditPage editPage = openEditOf(brandPage, oldName);

        Allure.step("Act: Đổi Name thành tên mới rồi bấm Save", () -> {
            editPage.enterName(newName);
            editPage.saveAndWait();
        });

        Allure.step("Assert: Thông báo cập nhật thành công", () ->
                Assert.assertEquals(editPage.getNotifyMessage(), UPDATED, "Thông báo sau khi sửa brand"));

        Allure.step("Assert: Tìm tên mới ra 1 dòng, tìm tên cũ không còn", () -> {
            brandPage.open();
            brandPage.search(newName);
            Assert.assertEquals(brandPage.getResultNames(), List.of(newName), "Tìm theo tên mới");
            brandPage.search(oldName);
            Assert.assertEquals(brandPage.rowCount(), 0, "Tìm theo tên cũ không được còn dòng nào");
        });
    }

    @Test(groups = {"smoke", "brand", "auth"}, description = "Xoá brand test qua hộp xác nhận rồi không còn tìm thấy")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bấm thùng rác hiện hộp Delete Confirmation đúng nội dung, bấm Delete thì báo xoá thành công và tìm lại không còn dòng nào.")
    public void testDeleteBrandViaConfirmation() {
        tc("AEC_BRAND_TC_009", "smoke", "brand", "auth");
        String name = "AutoBrand_" + newToken("TC009");
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);

        Allure.step("Act: Tìm brand test, bấm nút thùng rác", () -> {
            brandPage.search(name);
            brandPage.openDeleteOfFirstResult();
        });

        Allure.step("Assert: Hộp xác nhận đúng tiêu đề, câu hỏi và nút", () -> {
            Assert.assertEquals(brandPage.deleteModalTitle(), "Delete Confirmation", "Tiêu đề hộp xác nhận");
            Assert.assertEquals(brandPage.deleteModalText(), "Are you sure to delete this?", "Câu hỏi xác nhận");
            Assert.assertEquals(brandPage.deleteModalButtons(), List.of("Cancel", "Delete"), "Nút trong hộp xác nhận");
        });

        Allure.step("Act: Bấm Delete trong hộp xác nhận", brandPage::confirmDelete);

        Allure.step("Assert: Thông báo xoá thành công", () ->
                Assert.assertEquals(brandPage.getNotifyMessage(), DELETED, "Thông báo sau khi xoá brand"));

        Allure.step("Assert: Tìm lại không còn brand test", () -> {
            brandPage.search(name);
            Assert.assertEquals(brandPage.rowCount(), 0, "Brand đã xoá vẫn còn trong danh sách");
        });
    }

    @DataProvider(name = "deleteModalCloseWays", parallel = true)
    public Object[][] deleteModalCloseWays() {
        return new Object[][]{{"a", BrandPage.CloseBy.CANCEL}, {"b", BrandPage.CloseBy.CLOSE_ICON}, {"c", BrandPage.CloseBy.ESC}};
    }

    @Test(dataProvider = "deleteModalCloseWays", groups = {"regression", "brand", "auth", "knownIssue"},
            description = "Đóng hộp xác nhận xoá mà không xoá brand")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra đóng hộp Delete Confirmation bằng Cancel, nút × hoặc phím ESC thì hộp đóng và brand vẫn còn. Biến thể c đang lệch: hộp không nhận focus nên ESC không đóng (run_1793105365).")
    public void testCloseDeleteConfirmationKeepsBrand(String variant, BrandPage.CloseBy closeBy) {
        tc("AEC_BRAND_TC_041", "regression", "brand", "auth", "knownIssue");
        Allure.parameter("Biến thể", variant + " — " + closeBy);
        String name = "AutoDel_" + newToken("TC041" + variant);
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);

        Allure.step("Act: Tìm brand test, bấm nút thùng rác", () -> {
            brandPage.search(name);
            brandPage.openDeleteOfFirstResult();
        });

        Allure.step("Act: Đóng hộp xác nhận bằng " + closeBy, () -> brandPage.closeDeleteModal(closeBy));

        Allure.step("Assert: Hộp đóng, vẫn ở All Brands", () -> {
            Assert.assertTrue(brandPage.waitDeleteModalClosed(), "Hộp Delete Confirmation phải đóng");
            Assert.assertTrue(brandPage.isTitleDisplayed(), "Phải vẫn ở trang All Brands");
        });

        Allure.step("Assert: Brand test vẫn còn", () -> {
            brandPage.search(name);
            Assert.assertEquals(brandPage.getResultNames(), List.of(name), "Brand test phải còn");
        });
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Dropdown Brand của Product cập nhật theo tạo, đổi tên, xoá brand")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra dropdown Brand ở Add New Product có brand vừa tạo, đổi theo tên mới, và mất đi khi brand bị xoá. Không bấm Save ở form sản phẩm.")
    public void testProductBrandDropdownFollowsBrandChanges() {
        tc("AEC_BRAND_TC_043", "regression", "brand", "auth");
        String token = newToken("TC043");
        String name = "AutoLink_" + token;
        String renamed = name + "_RENAMED";
        BrandPage brandPage = openBrandsAsAdmin();
        ProductCreatePage productPage = new ProductCreatePage(driver());
        createBrand(brandPage, name);

        Allure.step("Assert: (a) Sau khi tạo — dropdown có đúng 1 lựa chọn tên brand", () ->
                Assert.assertEquals(productPage.open().brandOptionsContaining(token), List.of(name), "Dropdown sau khi tạo"));

        Allure.step("Act: Đổi tên brand thành " + renamed, () -> {
            BrandEditPage editPage = openEditOf(brandPage, name);
            editPage.enterName(renamed);
            editPage.saveAndWait();
        });

        Allure.step("Assert: (b) Sau khi đổi tên — chỉ có tên mới", () ->
                Assert.assertEquals(productPage.open().brandOptionsContaining(token), List.of(renamed), "Dropdown sau khi đổi tên"));

        Allure.step("Act: Xoá brand qua hộp xác nhận", () -> {
            brandPage.open();
            brandPage.search(renamed);
            brandPage.deleteFirstResult();
        });

        Allure.step("Assert: (c) Sau khi xoá — không còn lựa chọn nào", () ->
                Assert.assertEquals(productPage.open().brandOptionsContaining(token), List.of(), "Dropdown sau khi xoá"));
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Bấm Save hai lần liên tiếp chỉ tạo một brand")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bấm đúp thật nhanh nút Save của form Add New Brand thì chỉ có đúng 1 brand được tạo.")
    public void testDoubleClickSaveCreatesOneBrand() {
        tc("AEC_BRAND_TC_044", "regression", "brand", "auth");
        String name = "AutoDouble_" + newToken("TC044");
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name rồi bấm đúp nút Save", () -> {
            brandPage.enterNewBrandName(name);
            brandPage.doubleClickSave();
        });

        Allure.step("Assert: Tìm theo Name có đúng 1 dòng", () -> {
            brandPage.open();
            brandPage.search(name);
            Assert.assertEquals(brandPage.getResultNames(), List.of(name), "Không được tạo trùng brand");
        });
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Hai tab cùng sửa một brand không làm lỗi trang")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra mở form sửa cùng một brand ở 2 tab, tab 1 lưu tên X rồi tab 2 lưu tên Y thì cả hai lưu thành công, danh sách còn đúng 1 dòng tên Y.")
    public void testConcurrentEditLastSaveWins() {
        tc("AEC_BRAND_TC_045", "regression", "brand", "auth");
        String token = newToken("TC045");
        String name = "AutoConc_" + token;
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);
        String editUrl = openEditOf(brandPage, name).currentUrl();
        String firstTab = driver().getWindowHandle();

        String secondTab = Allure.step("Arrange: Mở form sửa cùng brand ở tab thứ 2", () -> {
            driver().switchTo().newWindow(WindowType.TAB);
            new BrandEditPage(driver()).openUrl(editUrl);
            return driver().getWindowHandle();
        });

        Allure.step("Act: Tab 1 đổi Name thành X rồi Save", () -> {
            driver().switchTo().window(firstTab);
            BrandEditPage editPage = new BrandEditPage(driver());
            editPage.enterName(name + "_X");
            editPage.saveAndWait();
            Assert.assertEquals(editPage.getNotifyMessage(), UPDATED, "Tab 1 phải lưu thành công");
        });

        Allure.step("Act: Tab 2 đổi Name thành Y rồi Save", () -> {
            driver().switchTo().window(secondTab);
            BrandEditPage editPage = new BrandEditPage(driver());
            editPage.enterName(name + "_Y");
            editPage.saveAndWait();
            Assert.assertFalse(editPage.isServerErrorPage(), "Tab 2 không được ra trang lỗi");
            Assert.assertEquals(editPage.getNotifyMessage(), UPDATED, "Tab 2 phải lưu thành công");
        });

        Allure.step("Assert: Danh sách còn đúng 1 dòng mang tên Y", () -> {
            brandPage.open();
            brandPage.search(token);
            Assert.assertEquals(brandPage.getResultNames(), List.of(name + "_Y"), "Lần lưu sau phải thắng");
        });
    }
}
