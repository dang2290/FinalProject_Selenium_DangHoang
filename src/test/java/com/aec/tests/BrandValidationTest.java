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
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static com.aec.utils.TestDataGenerator.padTo;

/**
 * Required & Validation của form Add New Brand (Name · Meta Title · Meta description · Logo).
 * TC giả định giới hạn 255 ký tự (AMB-BRAND-04); thực tế 03-11-2026 máy chủ cắt Name còn 50 ký tự không báo —
 * mọi tên test (trừ TC kiểm độ dài) giữ ≤ 50 ký tự để mã truy vết không bị cắt.
 * TC ở docs/testcases/brand/web/test_cases_brand_web.md.
 */
@Feature("Manage Brand")
@Story("Validation form brand")
public class BrandValidationTest extends BrandTestBase {

    @DataProvider(name = "emptyNameWays", parallel = true)
    public Object[][] emptyNameWays() {
        return new Object[][]{{"a", false}, {"b", true}};
    }

    @Test(dataProvider = "emptyNameWays", groups = {"regression", "brand", "auth", "validation"},
            description = "Không lưu được brand mới khi Name để trống")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name để trống (không gõ gì / gõ rồi xoá hết), có Meta Title, bấm Save thì trình duyệt chặn: trang không tải lại, con trỏ ở ô Name, Meta Title còn nguyên.")
    public void testCreateWithEmptyNameBlocked(String variant, boolean typeThenClear) {
        tc("AEC_BRAND_TC_021", "regression", "brand", "auth", "validation");
        Allure.parameter("Biến thể", variant + " — " + (typeThenClear ? "gõ abc rồi xoá hết" : "không gõ gì"));
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Chuẩn bị ô Name, nhập Meta Title, bấm Save", () -> {
            if (typeThenClear) {
                brandPage.enterNewBrandName("abc");
                brandPage.clearName();
            }
            brandPage.enterMetaTitle("Meta TC021");
            brandPage.markPage();
            brandPage.clickSave();
        });

        Allure.step("Assert: Trình duyệt chặn ở ô Name, form giữ nguyên", () -> {
            Assert.assertFalse(brandPage.nameValidationMessage().isEmpty(), "Phải có cảnh báo dưới ô Name");
            Assert.assertTrue(brandPage.isSamePageInstance(), "Trang không được tải lại");
            Assert.assertTrue(brandPage.isNameFocused(), "Con trỏ phải nằm trong ô Name");
            Assert.assertEquals(brandPage.metaTitleValue(), "Meta TC021", "Meta Title phải còn nguyên");
        });
    }

    @DataProvider(name = "validNameLengths", parallel = true)
    public Object[][] validNameLengths() {
        return new Object[][]{{"b", 254}, {"c", 255}};
    }

    @Test(dataProvider = "validNameLengths", groups = {"regression", "brand", "auth", "boundary"},
            description = "Name ở độ dài biên hợp lệ lưu thành công")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name dài 254 và 255 ký tự lưu thành công, form sửa giữ đủ chuỗi không bị cắt. Biến thể a (1 ký tự `Q`) không automate: tên 1 ký tự không truy vết được để dọn trên môi trường dùng chung.")
    public void testNameAtValidBoundary(String variant, int length) {
        tc("AEC_BRAND_TC_024", "regression", "brand", "auth", "boundary");
        Allure.parameter("Biến thể", variant + " — " + length + " ký tự");
        String token = newToken("TC024" + variant);
        String name = padTo("AutoBVA" + length + "_" + token + "_", 'x', length);
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name " + length + " ký tự rồi Save", () -> brandPage.createBrand(name));

        Allure.step("Assert: Lưu thành công", () ->
                Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu"));

        BrandEditPage editPage = openEditOf(brandPage, token);

        Allure.step("Assert: Form sửa giữ đủ " + length + " ký tự", () ->
                Assert.assertEquals(editPage.nameValue(), name, "Name không được bị cắt"));
    }

    @DataProvider(name = "tooLongNames", parallel = true)
    public Object[][] tooLongNames() {
        return new Object[][]{{"a", 256}, {"b", 1000}};
    }

    @Test(dataProvider = "tooLongNames", groups = {"regression", "brand", "auth", "boundary"},
            description = "Name vượt độ dài tối đa bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name dài 256 và 1000 ký tự thì không có brand nào được tạo, không ra trang lỗi máy chủ. Câu thông báo lỗi chưa có evidence (@NeedsVerify).")
    public void testNameTooLongRejected(String variant, int length) {
        tc("AEC_BRAND_TC_025", "regression", "brand", "auth", "boundary");
        Allure.parameter("Biến thể", variant + " — " + length + " ký tự");
        String token = newToken("TC025" + variant);
        String name = padTo("AutoBVA" + length + "_" + token + "_", 'x', length);
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name " + length + " ký tự rồi Save", () -> brandPage.createBrand(name));

        Allure.step("Assert: Không ra trang lỗi máy chủ, không báo tạo thành công", () -> {
            Assert.assertFalse(brandPage.isServerErrorPage(), "Không được ra trang lỗi máy chủ");
            Assert.assertNotEquals(brandPage.getNotifyMessage(), INSERTED, "Không được báo tạo thành công");
        });

        Allure.step("Assert: Không có brand nào được tạo", () -> {
            brandPage.open();
            brandPage.search(token);
            Assert.assertEquals(brandPage.rowCount(), 0, "Brand quá dài không được lưu");
        });
    }

    @DataProvider(name = "unicodeNames", parallel = true)
    public Object[][] unicodeNames() {
        return new Object[][]{
                {"a", "Thương hiệu Ánh Dương "},
                {"b", "Brand & Co \"Quote\" 'S' "},
                {"c", "Brand 😀🚀 "},
                {"d", "品牌テスト "}
        };
    }

    @Test(dataProvider = "unicodeNames", groups = {"regression", "brand", "auth"},
            description = "Name có ký tự đặc biệt và Unicode lưu, hiển thị nguyên văn")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name tiếng Việt, ký tự đặc biệt, emoji, chữ Trung/Nhật lưu thành công, danh sách và form sửa hiện đúng nguyên văn.")
    public void testUnicodeNameKeptVerbatim(String variant, String head) {
        tc("AEC_BRAND_TC_026", "regression", "brand", "auth");
        String token = newToken("TC026" + variant);
        String name = head + token;
        Allure.parameter("Biến thể", variant + " — " + name);
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name rồi Save", () -> brandPage.createBrand(name));

        Allure.step("Assert: Lưu thành công", () ->
                Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu"));

        Allure.step("Assert: Danh sách hiện đúng nguyên văn", () -> {
            brandPage.search(token);
            Assert.assertEquals(brandPage.getResultNames(), List.of(name), "Tên ở danh sách");
        });

        BrandEditPage editPage = Allure.step("Act: Mở form sửa", brandPage::editFirstResult);

        Allure.step("Assert: Form sửa đúng nguyên văn", () ->
                Assert.assertEquals(editPage.nameValue(), name, "Ô Name ở form sửa"));
    }

    @DataProvider(name = "injectionNames", parallel = true)
    public Object[][] injectionNames() {
        return new Object[][]{
                {"a", "<script>alert(1)</script> "},
                {"b", "<img src=x onerror=alert(1)>"},
                {"c", "' OR 1=1-- "}
        };
    }

    @Test(dataProvider = "injectionNames", groups = {"regression", "brand", "auth", "security"},
            description = "Name chứa mã script hoặc SQL được lưu và hiển thị dạng chữ, không thực thi")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name chứa script, thẻ img onerror, SQL được lưu nguyên văn; ở danh sách, form sửa, dropdown Brand của Add New Product đều hiện dạng chữ và không bật hộp thoại nào. Không bấm Save ở form sản phẩm.")
    public void testInjectionNameNotExecuted(String variant, String head) {
        tc("AEC_BRAND_TC_027", "regression", "brand", "auth", "security");
        String token = newToken("TC027" + variant);
        String name = head + token;
        Allure.parameter("Biến thể", variant + " — " + name);
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name rồi Save", () -> brandPage.createBrand(name));

        Allure.step("Assert: Sau khi lưu — không có hộp thoại", () ->
                Assert.assertFalse(brandPage.isAlertOpen(), "Không được bật hộp thoại sau khi lưu"));

        Allure.step("Assert: Danh sách hiện đúng chuỗi dạng chữ", () -> {
            brandPage.search(token);
            Assert.assertFalse(brandPage.isAlertOpen(), "Không được bật hộp thoại ở danh sách");
            Assert.assertEquals(brandPage.getResultNames(), List.of(name), "Tên ở danh sách");
        });

        Allure.step("Assert: Form sửa đúng nguyên văn", () -> {
            BrandEditPage editPage = brandPage.editFirstResult();
            Assert.assertFalse(editPage.isAlertOpen(), "Không được bật hộp thoại ở form sửa");
            Assert.assertEquals(editPage.nameValue(), name, "Ô Name ở form sửa");
        });

        Allure.step("Assert: Dropdown Brand của Add New Product hiện đúng chuỗi", () -> {
            ProductCreatePage productPage = new ProductCreatePage(driver()).open();
            Assert.assertFalse(productPage.isAlertOpen(), "Không được bật hộp thoại ở form sản phẩm");
            Assert.assertEquals(productPage.brandOptionsContaining(token), List.of(name), "Lựa chọn trong dropdown Brand");
        });
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Khoảng trắng đầu và cuối Name được cắt bỏ khi lưu")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra Name có 3 dấu cách ở mỗi đầu thì sau khi lưu, form sửa hiện Name không còn khoảng trắng thừa.")
    public void testNameTrimmed() {
        tc("AEC_BRAND_TC_028", "regression", "brand", "auth");
        String name = "AutoTrim_" + newToken("TC028");
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name có khoảng trắng đầu/cuối rồi Save", () -> brandPage.createBrand("   " + name + "   "));

        Allure.step("Assert: Lưu thành công, tìm ra đúng 1 dòng", () -> {
            Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
            brandPage.search(name);
            Assert.assertEquals(brandPage.rowCount(), 1, "Số dòng tìm được");
        });

        BrandEditPage editPage = Allure.step("Act: Mở form sửa", brandPage::editFirstResult);

        Allure.step("Assert: Name không còn khoảng trắng thừa", () ->
                Assert.assertEquals(editPage.nameValue(), name, "Name sau khi lưu"));
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Cho phép tạo hai brand trùng tên")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tạo 2 brand cùng một Name thì cả hai lần đều lưu thành công, tìm theo Name ra đúng 2 dòng.")
    public void testDuplicateNameAllowed() {
        tc("AEC_BRAND_TC_029", "regression", "brand", "auth");
        String name = "AutoDup_" + newToken("TC029");
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Tạo brand lần 1", () -> brandPage.createBrand(name));
        Allure.step("Act: Tạo brand lần 2 cùng Name", () -> {
            brandPage.createBrand(name);
            Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Lần 2 phải lưu thành công, không báo trùng");
        });

        Allure.step("Assert: Tìm theo Name ra đúng 2 dòng", () -> {
            brandPage.search(name);
            Assert.assertEquals(brandPage.getResultNames(), List.of(name, name), "Phải có 2 dòng cùng tên");
        });
    }

    @DataProvider(name = "metaTitles", parallel = true)
    public Object[][] metaTitles() {
        return new Object[][]{
                {"a", padTo("MetaTitle255_", 'm', 255)},
                {"b", "Thương hiệu \"Ánh Dương\" & Co"},
                {"c", "<b>Meta đậm</b>"}
        };
    }

    @Test(dataProvider = "metaTitles", groups = {"regression", "brand", "auth", "boundary"},
            description = "Meta Title hợp lệ lưu và hiển thị nguyên văn ở form sửa")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Meta Title 255 ký tự, có tiếng Việt/ký tự đặc biệt, có thẻ HTML đều lưu thành công và form sửa hiện đúng nguyên văn.")
    public void testValidMetaTitleKeptVerbatim(String variant, String metaTitle) {
        tc("AEC_BRAND_TC_030", "regression", "brand", "auth", "boundary");
        Allure.parameter("Biến thể", variant);
        String name = "AutoMeta_" + newToken("TC030" + variant);
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name, Meta Title rồi Save", () -> {
            brandPage.enterNewBrandName(name);
            brandPage.enterMetaTitle(metaTitle);
            brandPage.saveAndWait();
        });

        Allure.step("Assert: Lưu thành công", () ->
                Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu"));

        BrandEditPage editPage = openEditOf(brandPage, name);

        Allure.step("Assert: Meta Title đúng nguyên văn", () ->
                Assert.assertEquals(editPage.metaTitleValue(), metaTitle, "Meta Title ở form sửa"));
    }

    @Test(groups = {"regression", "brand", "auth", "boundary"}, description = "Meta Title vượt độ dài tối đa bị chặn")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Meta Title 256 ký tự thì không có brand nào được tạo, không ra trang lỗi máy chủ. Câu thông báo lỗi chưa có evidence (@NeedsVerify).")
    public void testMetaTitleTooLongRejected() {
        tc("AEC_BRAND_TC_031", "regression", "brand", "auth", "boundary");
        String name = "AutoMeta_" + newToken("TC031");
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name, Meta Title 256 ký tự rồi Save", () -> {
            brandPage.enterNewBrandName(name);
            brandPage.enterMetaTitle(padTo("MetaTitle256_", 'm', 256));
            brandPage.saveAndWait();
        });

        Allure.step("Assert: Không ra trang lỗi máy chủ, không báo tạo thành công", () -> {
            Assert.assertFalse(brandPage.isServerErrorPage(), "Không được ra trang lỗi máy chủ");
            Assert.assertNotEquals(brandPage.getNotifyMessage(), INSERTED, "Không được báo tạo thành công");
        });

        Allure.step("Assert: Không có brand nào được tạo", () -> {
            brandPage.open();
            brandPage.search(name);
            Assert.assertEquals(brandPage.rowCount(), 0, "Brand có Meta Title quá dài không được lưu");
        });
    }

    @DataProvider(name = "metaDescriptions", parallel = true)
    public Object[][] metaDescriptions() {
        return new Object[][]{
                {"a", "Dòng 1\nDòng 2\nDòng 3"},
                {"b", padTo("Desc2000_", 'd', 2000)},
                {"c", "<h1>Mô tả</h1><script>alert(1)</script>"}
        };
    }

    @Test(dataProvider = "metaDescriptions", groups = {"regression", "brand", "auth", "security"},
            description = "Meta description nhiều dòng, dài, có thẻ HTML lưu đúng và không thực thi")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Meta description 3 dòng, 2000 ký tự, có thẻ HTML/script lưu thành công, không bật hộp thoại, form sửa giữ nguyên văn kể cả xuống dòng.")
    public void testMetaDescriptionKeptVerbatim(String variant, String metaDescription) {
        tc("AEC_BRAND_TC_032", "regression", "brand", "auth", "security");
        Allure.parameter("Biến thể", variant);
        String name = "AutoDesc_" + newToken("TC032" + variant);
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Nhập Name, Meta description rồi Save", () -> {
            brandPage.enterNewBrandName(name);
            brandPage.enterMetaDescription(metaDescription);
            brandPage.saveAndWait();
        });

        Allure.step("Assert: Lưu thành công, không bật hộp thoại", () -> {
            Assert.assertFalse(brandPage.isAlertOpen(), "Không được bật hộp thoại");
            Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Thông báo sau khi lưu");
        });

        BrandEditPage editPage = openEditOf(brandPage, name);

        Allure.step("Assert: Meta description đúng nguyên văn", () -> {
            Assert.assertFalse(editPage.isAlertOpen(), "Không được bật hộp thoại ở form sửa");
            Assert.assertEquals(editPage.metaDescriptionValue(), metaDescription, "Meta description ở form sửa");
        });
    }

    @Test(groups = {"regression", "brand", "auth", "ui"}, description = "Hộp thư viện Logo hiển thị đủ điều khiển")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra bấm Browse ở ô Logo mở hộp thư viện có 2 tab, sắp xếp mặc định Sort by newest với đủ 4 lựa chọn, Selected Only chưa chọn, ô tìm rỗng, ảnh có tên và dung lượng, chân hộp đủ nút.")
    public void testLogoLibraryControls() {
        tc("AEC_BRAND_TC_033", "regression", "brand", "auth", "ui");
        BrandPage brandPage = openBrandsAsAdmin();

        MediaLibraryModal library = Allure.step("Act: Bấm Browse ở ô Logo", brandPage::openLogoLibrary);

        Allure.step("Assert: Bảng kiểm hộp thư viện", () -> {
            Assert.assertEquals(library.tabs(), List.of("Select File", "Upload New"), "Hai tab");
            Assert.assertEquals(library.activeTab(), "Select File", "Tab đang chọn");
            Assert.assertEquals(library.selectedSort(), "Sort by newest", "Sắp xếp mặc định");
            Assert.assertFalse(library.isSelectedOnlyChecked(), "Selected Only chưa được chọn");
            Assert.assertEquals(library.searchValue(), "", "Ô tìm phải rỗng");
            Assert.assertEquals(library.searchPlaceholder(), "Search your files", "Chữ gợi ý ô tìm");
            Assert.assertTrue(library.everyFileHasNameAndSize(), "Mỗi ảnh phải có tên tệp và dung lượng");
            Assert.assertEquals(library.selectedCountText(), "0 File selected", "Chân hộp");
            Assert.assertEquals(library.footerButtons(), List.of("Clear", "Prev", "Next", "Add Files"), "Nút ở chân hộp");
            Assert.assertTrue(library.hasCloseIcon(), "Phải có nút đóng ×");
        });

        Allure.step("Assert: Dropdown sắp xếp đủ 4 lựa chọn theo thứ tự", () ->
                Assert.assertEquals(library.sortOptions(),
                        List.of("Sort by newest", "Sort by oldest", "Sort by smallest", "Sort by largest"), "Lựa chọn sắp xếp"));
    }

    @DataProvider(name = "libraryCloseWays", parallel = true)
    public Object[][] libraryCloseWays() {
        return new Object[][]{{"a", MediaLibraryModal.CloseBy.CLOSE_ICON}, {"b", MediaLibraryModal.CloseBy.ESC}};
    }

    @Test(dataProvider = "libraryCloseWays", groups = {"regression", "brand", "auth", "knownIssue"},
            description = "Đóng thư viện Logo khi chưa chọn thì ô Logo vẫn trống")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra mở thư viện Logo rồi đóng bằng nút × hoặc phím ESC mà chưa chọn ảnh thì hộp đóng, ô Logo vẫn Choose File. Biến thể b đang lệch: hộp không nhận focus nên ESC không đóng (run_1793105365).")
    public void testCloseLogoLibraryWithoutSelection(String variant, MediaLibraryModal.CloseBy closeBy) {
        tc("AEC_BRAND_TC_034", "regression", "brand", "auth", "knownIssue");
        Allure.parameter("Biến thể", variant + " — " + closeBy);
        BrandPage brandPage = openBrandsAsAdmin();

        MediaLibraryModal library = Allure.step("Act: Bấm Browse ở ô Logo", brandPage::openLogoLibrary);
        Allure.step("Act: Đóng hộp bằng " + closeBy, () -> library.close(closeBy));

        Allure.step("Assert: Hộp đóng, ô Logo vẫn trống", () -> {
            Assert.assertTrue(library.waitUntilClosed(), "Hộp thư viện phải đóng");
            Assert.assertEquals(brandPage.logoAmountText(), "Choose File", "Ô Logo");
            Assert.assertEquals(brandPage.logoPreviewCount(), 0, "Không được có ảnh thu nhỏ");
        });
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Logo chỉ chọn được 1 ảnh và có thể bỏ chọn")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra trong thư viện Logo: chọn ảnh thứ hai thì bỏ ảnh thứ nhất (luôn 1 File selected), Clear bỏ hết, chọn lại rồi Add Files thì ô Logo báo 1 File selected và có ô xem trước.")
    public void testLogoSingleSelection() {
        tc("AEC_BRAND_TC_035", "regression", "brand", "auth");
        BrandPage brandPage = openBrandsAsAdmin();

        MediaLibraryModal library = Allure.step("Arrange: Mở thư viện, lọc brandimg", () -> {
            MediaLibraryModal modal = brandPage.openLogoLibrary();
            modal.search("brandimg");
            return modal;
        });

        Allure.step("Act: Bấm ảnh thứ nhất", () -> library.selectFile(0));
        Allure.step("Assert: Ảnh thứ nhất được đánh dấu, 1 File selected", () -> {
            Assert.assertEquals(library.selectedIndexes(), List.of(0), "Ảnh được đánh dấu");
            Assert.assertEquals(library.selectedCountText(), "1 File selected", "Chân hộp");
        });

        Allure.step("Act: Bấm ảnh thứ hai", () -> library.selectFile(1));
        Allure.step("Assert: Chỉ ảnh thứ hai được đánh dấu, vẫn 1 File selected", () -> {
            Assert.assertEquals(library.selectedIndexes(), List.of(1), "Ảnh được đánh dấu");
            Assert.assertEquals(library.selectedCountText(), "1 File selected", "Chân hộp");
        });

        Allure.step("Act: Bấm Clear", library::clearSelection);
        Allure.step("Assert: Không ảnh nào được đánh dấu, 0 File selected", () -> {
            Assert.assertEquals(library.selectedIndexes(), List.of(), "Ảnh được đánh dấu");
            Assert.assertEquals(library.selectedCountText(), "0 File selected", "Chân hộp");
        });

        Allure.step("Act: Bấm ảnh thứ nhất rồi Add Files", () -> {
            library.selectFile(0);
            library.addFiles();
        });
        Allure.step("Assert: Hộp đóng, ô Logo báo 1 File selected và có ô xem trước", () -> {
            Assert.assertEquals(brandPage.logoAmountText(), "1 File selected", "Ô Logo");
            Assert.assertEquals(brandPage.logoPreviewCount(), 1, "Ô xem trước ảnh");
        });
    }
}
