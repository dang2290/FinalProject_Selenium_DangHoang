package com.aec.tests;

import com.aec.pages.BrandEditPage;
import com.aec.pages.BrandPage;
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

/** Màn Brand Information: tab ngôn ngữ, Name, Meta Title, Slug, Not Found. TC ở docs/testcases/brand/web/test_cases_brand_web.md. */
@Feature("Manage Brand")
@Story("Sửa brand")
public class BrandEditTest extends BrandTestBase {

    private static final String EDIT_URL_PATTERN = ".*/admin/brands/edit/\\d+\\?lang=en$";

    /** Slug tự sinh: tên brand bỏ ký tự đặc biệt + "-" + 5 ký tự chữ số. */
    private static String generatedSlugPattern(String name) {
        return "^" + name.replaceAll("[^A-Za-z0-9]", "") + "-[A-Za-z0-9]{5}$";
    }

    @Test(groups = {"smoke", "brand", "auth"}, description = "Màn Brand Information hiển thị đủ thành phần, tab English được chọn mặc định")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra form sửa có tiêu đề Brand Information, 2 tab English (đang chọn) / Vietnamese, đủ nhãn theo thứ tự, Name đúng tên brand, Slug tự sinh sửa được, biểu tượng cạnh Name có chú thích Translatable.")
    public void testBrandInformationPageElements() {
        tc("AEC_BRAND_TC_002", "smoke", "brand", "auth");
        String name = "AutoBrand_" + newToken("TC002");
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);

        BrandEditPage editPage = Allure.step("Act: Tìm brand test, bấm nút bút chì", () -> openEditOf(brandPage, name));

        Allure.step("Assert: Bảng kiểm thành phần màn Brand Information", () -> {
            Assert.assertEquals(editPage.languageTabs(), List.of("English", "Vietnamese"), "(REQ-22) Hai tab ngôn ngữ");
            Assert.assertEquals(editPage.activeTab(), "English", "(REQ-22) Tab đang chọn");
            Assert.assertEquals(editPage.formLabels(), List.of("Name", "Logo (120x80)", "Meta Title", "Meta description", "Slug"),
                    "(REQ-24) Nhãn từ trên xuống");
            Assert.assertTrue(editPage.isSaveDisplayed(), "(REQ-24) Phải có nút Save");
            Assert.assertEquals(editPage.nameValue(), name, "(REQ-22) Ô Name");
            Assert.assertTrue(editPage.slugValue().matches(generatedSlugPattern(name)),
                    "(REQ-25) Slug phải dạng <tên brand>-<5 ký tự>, thực tế: " + editPage.slugValue());
            Assert.assertTrue(editPage.isSlugEditable(), "(REQ-25) Ô Slug phải sửa được");
        });

        Allure.step("Assert: Rê chuột lên biểu tượng cạnh Name hiện Translatable", () ->
                Assert.assertEquals(editPage.translatableTooltip(), "Translatable", "Chú thích biểu tượng ngôn ngữ"));
    }

    @Test(groups = {"smoke", "brand", "auth"}, description = "Bấm nút Edit ở dòng brand mở đúng form sửa của brand đó")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra nút bút chì có chú thích Edit, bấm vào mở /admin/brands/edit/<id>?lang=en với tab English đang chọn và Name đúng tên brand test.")
    public void testEditButtonOpensBrandForm() {
        tc("AEC_BRAND_TC_004", "smoke", "brand", "auth");
        String name = "AutoBrand_" + newToken("TC004");
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);

        Allure.step("Act: Tìm brand test", () -> {
            brandPage.open();
            brandPage.search(name);
        });

        Allure.step("Assert: Rê chuột lên nút bút chì hiện Edit", () ->
                Assert.assertEquals(brandPage.editTooltipOfFirstRow(), "Edit", "Chú thích nút bút chì"));

        BrandEditPage editPage = Allure.step("Act: Bấm nút bút chì", brandPage::editFirstResult);

        Allure.step("Assert: Mở đúng form sửa của brand test", () -> {
            Assert.assertTrue(editPage.currentUrl().matches(EDIT_URL_PATTERN), "Đường dẫn form sửa: " + editPage.currentUrl());
            Assert.assertEquals(editPage.activeTab(), "English", "Tab đang chọn");
            Assert.assertEquals(editPage.nameValue(), name, "Ô Name");
        });
    }

    @DataProvider(name = "languageTabs", parallel = true)
    public Object[][] languageTabs() {
        return new Object[][]{{"a", "English"}, {"b", "Vietnamese"}};
    }

    @Test(dataProvider = "languageTabs", groups = {"regression", "brand", "auth", "validation"},
            description = "Không lưu được chỉnh sửa khi Name để trống")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ở tab English và Vietnamese, xoá hết Name rồi Save thì trình duyệt chặn, trang không tải lại và brand vẫn mang tên cũ.")
    public void testEditWithEmptyNameBlocked(String variant, String tab) {
        tc("AEC_BRAND_TC_022", "regression", "brand", "auth", "validation");
        Allure.parameter("Biến thể", variant + " — tab " + tab);
        String name = "AutoBrand_" + newToken("TC022" + variant);
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);
        BrandEditPage editPage = openEditOf(brandPage, name);

        Allure.step("Act: Chọn tab " + tab + ", xoá hết Name, bấm Save", () -> {
            if (!tab.equals(editPage.activeTab())) {
                editPage.clickTab(tab);
            }
            editPage.clearName();
            editPage.markPage();
            editPage.clickSave();
        });

        Allure.step("Assert: Trình duyệt chặn, trang không tải lại", () -> {
            Assert.assertFalse(editPage.nameValidationMessage().isEmpty(), "Phải có cảnh báo dưới ô Name");
            Assert.assertTrue(editPage.isSamePageInstance(), "Trang không được tải lại");
        });

        Allure.step("Assert: Brand vẫn mang tên cũ", () -> {
            brandPage.open();
            brandPage.search(name);
            Assert.assertEquals(brandPage.getResultNames(), List.of(name), "Tên brand không được đổi");
        });
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Sửa Name ở tab Vietnamese không làm đổi Name tiếng Anh")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra lưu Name tiếng Việt ở tab Vietnamese (?lang=vn) thì tab Vietnamese hiện tên mới, tab English và danh sách vẫn là tên gốc.")
    public void testVietnameseNameDoesNotChangeEnglish() {
        tc("AEC_BRAND_TC_037", "regression", "brand", "auth");
        String token = newToken("TC037");
        String name = "AutoLang_" + token;
        String vietnameseName = "Thương hiệu " + token;
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);
        BrandEditPage editPage = openEditOf(brandPage, name);

        Allure.step("Act: Bấm tab Vietnamese", () -> editPage.clickTab("Vietnamese"));

        Allure.step("Assert: Đường dẫn ?lang=vn, tab Vietnamese được chọn", () -> {
            Assert.assertTrue(editPage.currentUrl().endsWith("lang=vn"), "Đường dẫn: " + editPage.currentUrl());
            Assert.assertEquals(editPage.activeTab(), "Vietnamese", "Tab đang chọn");
        });

        Allure.step("Act: Nhập Name tiếng Việt rồi Save", () -> {
            editPage.enterName(vietnameseName);
            editPage.saveAndWait();
            Assert.assertEquals(editPage.getNotifyMessage(), UPDATED, "Lưu tab Vietnamese phải thành công");
        });

        Allure.step("Assert: Tab Vietnamese hiện tên tiếng Việt", () -> {
            editPage.clickTab("Vietnamese");
            Assert.assertEquals(editPage.nameValue(), vietnameseName, "Name ở tab Vietnamese");
        });

        Allure.step("Assert: Tab English vẫn là tên gốc", () -> {
            editPage.clickTab("English");
            Assert.assertEquals(editPage.nameValue(), name, "Name ở tab English");
        });

        Allure.step("Assert: Danh sách vẫn hiện tên gốc", () -> {
            brandPage.open();
            brandPage.search(name);
            Assert.assertEquals(brandPage.getResultNames(), List.of(name), "Tên ở danh sách");
        });
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Meta Title sửa ở một ngôn ngữ dùng chung cho cả hai ngôn ngữ")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra lưu Meta Title ở tab Vietnamese thì tab English cũng hiện đúng Meta Title đó.")
    public void testMetaTitleSharedAcrossLanguages() {
        tc("AEC_BRAND_TC_038", "regression", "brand", "auth");
        String name = "AutoLang_" + newToken("TC038");
        String metaTitle = "Meta VN TC038";
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);
        BrandEditPage editPage = openEditOf(brandPage, name);

        Allure.step("Act: Tab Vietnamese — nhập Meta Title rồi Save", () -> {
            editPage.clickTab("Vietnamese");
            editPage.enterMetaTitle(metaTitle);
            editPage.saveAndWait();
            Assert.assertEquals(editPage.getNotifyMessage(), UPDATED, "Lưu phải thành công");
        });

        Allure.step("Assert: Tab English hiện cùng Meta Title", () -> {
            editPage.clickTab("English");
            Assert.assertEquals(editPage.metaTitleValue(), metaTitle, "Meta Title ở tab English");
        });
    }

    @DataProvider(name = "slugValues", parallel = true)
    public Object[][] slugValues() {
        return new Object[][]{{"a", true}, {"b", false}};
    }

    @Test(dataProvider = "slugValues", groups = {"regression", "brand", "auth"},
            description = "Slug tự đặt được lưu, Slug để trống được sinh lại")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra đặt Slug riêng rồi Save thì Slug được giữ nguyên; xoá trống Slug rồi Save thì hệ thống sinh lại dạng <tên brand>-<5 ký tự>.")
    public void testCustomAndEmptySlug(String variant, boolean customSlug) {
        tc("AEC_BRAND_TC_039", "regression", "brand", "auth");
        String token = newToken("TC039" + variant);
        String name = "AutoSlug_" + token;
        String slug = customSlug ? "auto-slug-" + token.toLowerCase().replace('_', '-') : "";
        Allure.parameter("Biến thể", variant + " — " + (customSlug ? slug : "để trống"));
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);
        BrandEditPage editPage = openEditOf(brandPage, name);

        Allure.step("Act: Đặt Slug rồi Save", () -> {
            editPage.enterSlug(slug);
            editPage.saveAndWait();
            Assert.assertEquals(editPage.getNotifyMessage(), UPDATED, "Lưu phải thành công");
        });

        BrandEditPage reopened = openEditOf(brandPage, name);

        Allure.step("Assert: Slug sau khi mở lại form sửa", () -> {
            if (customSlug) {
                Assert.assertEquals(reopened.slugValue(), slug, "Slug tự đặt phải được giữ");
            } else {
                Assert.assertTrue(reopened.slugValue().matches(generatedSlugPattern(name)),
                        "Slug trống phải được sinh lại, thực tế: " + reopened.slugValue());
            }
        });
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Không cho hai brand dùng chung một Slug")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra đặt cho brand B đúng Slug của brand A thì hệ thống báo trùng hoặc tự đổi — Slug của B sau khi lưu phải khác A.")
    public void testDuplicateSlugRejected() {
        tc("AEC_BRAND_TC_040", "regression", "brand", "auth");
        String token = newToken("TC040");
        String brandA = "AutoSlugA_" + token;
        String brandB = "AutoSlugB_" + token;
        String slug = "dup-slug-" + token.toLowerCase().replace('_', '-');
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, brandA);
        createBrand(brandPage, brandB);

        Allure.step("Act: Đặt Slug cho brand A rồi Save", () -> {
            BrandEditPage editA = openEditOf(brandPage, brandA);
            editA.enterSlug(slug);
            editA.saveAndWait();
            Assert.assertEquals(editA.getNotifyMessage(), UPDATED, "Lưu brand A phải thành công");
        });

        Allure.step("Act: Đặt cùng Slug cho brand B rồi Save", () -> {
            BrandEditPage editB = openEditOf(brandPage, brandB);
            editB.enterSlug(slug);
            editB.saveAndWait();
            Assert.assertFalse(editB.isServerErrorPage(), "Không được ra trang lỗi máy chủ");
        });

        Allure.step("Assert: Slug của brand B khác Slug của brand A", () -> {
            Assert.assertEquals(openEditOf(brandPage, brandA).slugValue(), slug, "Slug brand A");
            Assert.assertNotEquals(openEditOf(brandPage, brandB).slugValue(), slug, "Brand B không được dùng chung Slug với A");
        });
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Mở form sửa của brand không tồn tại hiện trang không tìm thấy")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra mở /admin/brands/edit/999999999 thì hiện trang Page Not Found!, không có form sửa.")
    public void testEditMissingIdShowsNotFound() {
        tc("AEC_BRAND_TC_042", "regression", "brand", "auth");
        Allure.parameter("Biến thể", "a — id 999999999");
        loginAsAdmin();
        BrandEditPage editPage = new BrandEditPage(driver());

        Allure.step("Act: Mở /admin/brands/edit/999999999?lang=en", () ->
                editPage.openPath("/admin/brands/edit/999999999?lang=en"));

        Allure.step("Assert: Trang Page Not Found!, không có form sửa", () -> {
            Assert.assertTrue(editPage.isNotFoundPage(), "Phải hiện Page Not Found!");
            Assert.assertFalse(editPage.isFormDisplayed(), "Không được hiện form sửa");
        });
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Mở lại form sửa của brand đã xoá hiện trang không tìm thấy")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ghi lại đường dẫn sửa của brand test, xoá brand đó rồi mở lại đường dẫn thì hiện trang Page Not Found!.")
    public void testEditDeletedBrandShowsNotFound() {
        tc("AEC_BRAND_TC_042", "regression", "brand", "auth");
        Allure.parameter("Biến thể", "b — brand đã xoá");
        String name = "AutoGone_" + newToken("TC042b");
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);
        String editUrl = openEditOf(brandPage, name).currentUrl();

        Allure.step("Arrange: Xoá brand test", () -> {
            brandPage.open();
            brandPage.search(name);
            brandPage.deleteFirstResult();
        });

        BrandEditPage editPage = new BrandEditPage(driver());
        Allure.step("Act: Mở lại đường dẫn sửa đã ghi", () -> driver().get(editUrl));

        Allure.step("Assert: Trang Page Not Found!, không có form sửa", () -> {
            Assert.assertTrue(editPage.isNotFoundPage(), "Phải hiện Page Not Found!");
            Assert.assertFalse(editPage.isFormDisplayed(), "Không được hiện form sửa");
        });
    }
}
