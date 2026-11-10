package com.aec.tests;

import com.aec.pages.BrandEditPage;
import com.aec.pages.BrandPage;
import com.aec.pages.DashboardPage;
import com.aec.pages.LoginPage;
import com.aec.pages.ProductCreatePage;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Màn All Brands: giao diện, phân trang, tìm kiếm, phân quyền, bảo mật, bố cục, bàn phím.
 * TC_010, 011, 013, 014, 015 đọc dữ liệu dùng chung (không tạo gì). TC ở docs/testcases/brand/web/test_cases_brand_web.md.
 */
@Feature("Manage Brand")
@Story("Danh sách & tìm kiếm brand")
public class BrandListTest extends BrandTestBase {

    private static final int PAGE_SIZE = 15;

    @Test(groups = {"smoke", "brand", "auth", "ui"}, description = "Màn All Brands hiển thị đủ thành phần với nhãn nguyên văn và trạng thái mặc định")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra vào All Brands từ sidebar Products → Brand thì đủ tiêu đề, cột bảng, ô tìm kiếm, thẻ Add New Brand với nhãn, chữ gợi ý, trạng thái rỗng mặc định, và mỗi dòng có nút sửa/xoá.")
    public void testAllBrandsPageElements() {
        tc("AEC_BRAND_TC_001", "smoke", "brand", "auth", "ui");
        loginAsAdmin();
        BrandPage brandPage = new BrandPage(driver());

        Allure.step("Act: Ở sidebar bấm Products → Brand", brandPage::openFromSidebar);

        Allure.step("Assert: Bảng kiểm thành phần màn All Brands", () -> {
            SoftAssert soft = new SoftAssert();
            soft.assertTrue(brandPage.isTitleDisplayed(), "(1) Tiêu đề trang All Brands");
            soft.assertEquals(brandPage.cardHeaders(), List.of("Brands", "Add New Brand"), "(1)(4) Tiêu đề hai thẻ");
            soft.assertTrue(brandPage.isBrandMenuActive(), "(1) Mục Brand ở sidebar phải được tô sáng");
            soft.assertEquals(brandPage.tableHeaders(), List.of("#", "Name", "Logo", "Options"), "(2) Hàng tiêu đề bảng");
            soft.assertEquals(brandPage.searchValue(), "", "(3) Ô tìm kiếm phải rỗng");
            soft.assertEquals(brandPage.searchPlaceholder(), "Type name & Enter", "(3) Chữ gợi ý ô tìm kiếm");
            soft.assertEquals(brandPage.formLabels(), List.of("Name", "Logo (120x80)", "Meta Title", "Meta description"),
                    "(4) Nhãn thẻ Add New Brand");
            soft.assertEquals(brandPage.saveButtonText(), "Save", "(4) Nút Save");
            soft.assertEquals(brandPage.nameValue(), "", "(5) Ô Name rỗng");
            soft.assertEquals(brandPage.namePlaceholder(), "Name", "(5) Gợi ý ô Name");
            soft.assertEquals(brandPage.logoBrowseText(), "Browse", "(5) Nút Browse ở ô Logo");
            soft.assertEquals(brandPage.logoAmountText(), "Choose File", "(5) Chữ ở ô Logo");
            soft.assertEquals(brandPage.metaTitleValue(), "", "(5) Ô Meta Title rỗng");
            soft.assertEquals(brandPage.metaTitlePlaceholder(), "Meta Title", "(5) Gợi ý ô Meta Title");
            soft.assertEquals(brandPage.metaDescriptionValue(), "", "(5) Ô Meta description rỗng");
            soft.assertTrue(brandPage.everyRowHasEditAndDelete(), "(6) Mỗi dòng phải có nút bút chì và thùng rác");
            soft.assertAll();
        });
    }

    @DataProvider(name = "entryWays", parallel = true)
    public Object[][] entryWays() {
        return new Object[][]{{"a", "sidebar"}, {"b", "url"}, {"c", "editPage"}};
    }

    @Test(dataProvider = "entryWays", groups = {"smoke", "brand", "auth"}, description = "Mở danh sách Brand bằng mọi lối vào")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra vào All Brands từ sidebar, gõ thẳng URL, và từ trang sửa một brand bấm Brand ở sidebar đều ra cùng màn: bảng, thẻ Add New Brand, mục Brand tô sáng, URL /admin/brands.")
    public void testOpenBrandListFromEveryEntry(String variant, String entry) {
        tc("AEC_BRAND_TC_003", "smoke", "brand", "auth");
        Allure.parameter("Biến thể", variant + " — " + entry);
        loginAsAdmin();
        BrandPage brandPage = new BrandPage(driver());

        Allure.step("Act: Vào trang theo lối " + entry, () -> {
            switch (entry) {
                case "sidebar" -> brandPage.openFromSidebar();
                case "url" -> brandPage.open();
                default -> {
                    String editUrl = brandPage.open().editUrlOfFirstRow();
                    new BrandEditPage(driver()).openUrl(editUrl);
                    brandPage.openFromSidebar();
                }
            }
        });

        Allure.step("Assert: Màn All Brands, mục Brand tô sáng, đúng URL", () -> {
            Assert.assertTrue(brandPage.isTitleDisplayed(), "Tiêu đề All Brands");
            Assert.assertEquals(brandPage.cardHeaders(), List.of("Brands", "Add New Brand"), "Bảng danh sách và thẻ Add New Brand");
            Assert.assertTrue(brandPage.isBrandMenuActive(), "Mục Brand ở sidebar phải được tô sáng");
            Assert.assertEquals(brandPage.currentUrl(), config.getBaseUrl() + "/admin/brands", "Thanh địa chỉ");
        });
    }

    @Test(groups = {"smoke", "brand", "auth", "ui"}, description = "Brand chưa có logo hiển thị ảnh giữ chỗ ở cột Logo")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra brand tạo không chọn logo thì cột Logo hiện ảnh giữ chỗ placeholder.jpg (alt Brand) tải được, không phải ảnh lỗi.")
    public void testBrandWithoutLogoShowsPlaceholder() {
        tc("AEC_BRAND_TC_005", "smoke", "brand", "auth", "ui");
        String name = "AutoBrand_" + newToken("TC005");
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);

        Allure.step("Act: Tìm brand test", () -> brandPage.search(name));

        Allure.step("Assert: Cột Logo là ảnh giữ chỗ tải được", () -> {
            Assert.assertTrue(brandPage.logoSrcOfFirstRow().endsWith("placeholder.jpg"), "Đường dẫn ảnh: " + brandPage.logoSrcOfFirstRow());
            Assert.assertEquals(brandPage.logoAltOfFirstRow(), "Brand", "Văn bản thay thế của ảnh");
            Assert.assertTrue(brandPage.isLogoOfFirstRowLoaded(), "Ảnh giữ chỗ phải tải được, không phải ảnh lỗi");
        });
    }

    @DataProvider(name = "fullPages", parallel = true)
    public Object[][] fullPages() {
        return new Object[][]{{"a", 1}, {"b", 2}};
    }

    @Test(dataProvider = "fullPages", groups = {"regression", "brand", "auth"}, description = "Mỗi trang danh sách hiển thị tối đa 15 brand")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra trang 1 và trang 2 của danh sách (dữ liệu dùng chung có trên 30 brand) đều có đúng 15 dòng.")
    public void testPageShowsFifteenRows(String variant, int page) {
        tc("AEC_BRAND_TC_010", "regression", "brand", "auth");
        Allure.parameter("Biến thể", variant + " — trang " + page);
        loginAsAdmin();
        BrandPage brandPage = new BrandPage(driver());

        Allure.step("Act: Mở trang " + page, () -> brandPage.openPage(page));

        Allure.step("Assert: Đúng 15 dòng", () -> Assert.assertEquals(brandPage.rowCount(), PAGE_SIZE, "Số dòng trên trang " + page));
    }

    @DataProvider(name = "numberedPages", parallel = true)
    public Object[][] numberedPages() {
        return new Object[][]{{"a", 1}, {"b", 2}, {"c", 3}};
    }

    @Test(dataProvider = "numberedPages", groups = {"regression", "brand", "auth"}, description = "Cột # đánh số liên tục qua các trang")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra cột # ở trang N chạy từ (N − 1) × 15 + 1 tới N × 15, tăng dần từng 1.")
    public void testRowNumbersContinueAcrossPages(String variant, int page) {
        tc("AEC_BRAND_TC_011", "regression", "brand", "auth");
        Allure.parameter("Biến thể", variant + " — trang " + page);
        loginAsAdmin();
        BrandPage brandPage = new BrandPage(driver());

        Allure.step("Act: Mở trang " + page, () -> brandPage.openPage(page));

        Allure.step("Assert: Cột # từ " + ((page - 1) * PAGE_SIZE + 1) + " tới " + page * PAGE_SIZE, () -> {
            List<Integer> expected = IntStream.rangeClosed((page - 1) * PAGE_SIZE + 1, page * PAGE_SIZE).boxed().toList();
            Assert.assertEquals(brandPage.rowNumbers(), expected, "Cột # trên trang " + page);
        });
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Danh sách sắp xếp theo Name tăng dần, không phân biệt hoa thường")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tạo 3 brand theo thứ tự c → a → b (khác nhau chữ hoa/thường) thì danh sách lọc theo mã test xếp a, b, c — theo chữ cái, không theo thứ tự tạo.")
    public void testListSortedByNameCaseInsensitive() {
        tc("AEC_BRAND_TC_012", "regression", "brand", "auth");
        String token = newToken("TC012");
        String nameA = "ZZSort_" + token + "_a";
        String nameB = "zzsort_" + token + "_b";
        String nameC = "ZzSort_" + token + "_c";
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Tạo lần lượt 3 brand theo thứ tự c → a → b", () -> {
            for (String name : List.of(nameC, nameA, nameB)) {
                brandPage.createBrand(name);
            }
        });

        Allure.step("Act: Tìm theo mã test", () -> brandPage.search(token));

        Allure.step("Assert: Thứ tự a → b → c", () ->
                Assert.assertEquals(brandPage.getResultNames(), List.of(nameA, nameB, nameC), "Thứ tự trong danh sách"));
    }

    @DataProvider(name = "pageNavigations", parallel = true)
    public Object[][] pageNavigations() {
        return new Object[][]{
                {"a", 1, "2", "2"},
                {"b", 1, "›", "2"},
                {"c", 2, "‹", "1"},
                {"d", 1, "1", "1"}
        };
    }

    @Test(dataProvider = "pageNavigations", groups = {"regression", "brand", "auth"}, description = "Điều hướng giữa các trang bằng thanh phân trang")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra bấm số trang, ›, ‹ trên thanh phân trang thì bảng sang trang đích, URL có ?page=<đích>, số trang đích được tô. Biến thể d: bấm số của trang đang xem thì vẫn ở trang đó (trang 1 không mang ?page trong URL).")
    public void testPaginationNavigation(String variant, int startPage, String button, String targetPage) {
        tc("AEC_BRAND_TC_013", "regression", "brand", "auth");
        Allure.parameter("Biến thể", variant + " — trang " + startPage + " bấm " + button);
        loginAsAdmin();
        BrandPage brandPage = new BrandPage(driver());

        Allure.step("Arrange: Đứng ở trang " + startPage, () -> {
            if (startPage == 1) {
                brandPage.open();
            } else {
                brandPage.openPage(startPage);
            }
        });

        Allure.step("Act: Bấm nút " + button, () -> brandPage.clickPage(button));

        Allure.step("Assert: Sang trang " + targetPage, () -> {
            Assert.assertEquals(brandPage.activePage(), targetPage, "Số trang được tô");
            int target = Integer.parseInt(targetPage);
            List<Integer> numbers = brandPage.rowNumbers();
            Assert.assertEquals(numbers.get(0).intValue(), (target - 1) * PAGE_SIZE + 1, "Dòng đầu là dữ liệu của trang đích");
            if (startPage != target) {
                Assert.assertTrue(brandPage.currentUrl().contains("page=" + targetPage), "Thanh địa chỉ: " + brandPage.currentUrl());
            }
        });
    }

    @Test(groups = {"regression", "brand", "auth", "knownIssue"}, description = "Trang cuối của thanh phân trang có dữ liệu")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bấm số trang lớn nhất trên thanh phân trang thì có 1–15 dòng. Đang lệch: trang cuối không có dòng nào (AMB-BRAND-01).")
    public void testLastPageHasRows() {
        tc("AEC_BRAND_TC_014", "regression", "brand", "auth", "knownIssue");
        loginAsAdmin();
        BrandPage brandPage = new BrandPage(driver());

        int lastPage = Allure.step("Act: Mở danh sách, bấm số trang lớn nhất", () -> {
            int page = brandPage.open().lastPageNumber();
            brandPage.clickPage(String.valueOf(page));
            return page;
        });

        Allure.step("Assert: Trang " + lastPage + " có từ 1 đến 15 dòng", () -> {
            int rows = brandPage.rowCount();
            Assert.assertTrue(rows >= 1 && rows <= PAGE_SIZE, "Trang cuối có " + rows + " dòng");
        });
    }

    @Test(groups = {"regression", "brand", "auth", "knownIssue"}, description = "Mọi brand trong dropdown Brand của Product đều có trong danh sách quản trị")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra số # của dòng cuối cùng có dữ liệu ở danh sách bằng số brand trong dropdown Brand của Add New Product. Đang lệch: danh sách thiếu brand so với dropdown (AMB-BRAND-01). Không bấm Save ở form sản phẩm.")
    public void testListCountMatchesProductDropdown() {
        tc("AEC_BRAND_TC_015", "regression", "brand", "auth", "knownIssue");
        loginAsAdmin();
        BrandPage brandPage = new BrandPage(driver());

        int lastNumber = Allure.step("Act: Lấy số # ở dòng cuối cùng có dữ liệu của danh sách", () -> {
            for (int page = brandPage.open().lastPageNumber(); page >= 1; page--) {
                List<Integer> numbers = brandPage.openPage(page).rowNumbers();
                if (!numbers.isEmpty()) {
                    return numbers.get(numbers.size() - 1);
                }
            }
            return 0;
        });

        int dropdownCount = Allure.step("Act: Đếm brand trong dropdown Brand của Add New Product", () ->
                new ProductCreatePage(driver()).open().brandOptions().size());

        Allure.step("Assert: Hai số bằng nhau", () ->
                Assert.assertEquals(lastNumber, dropdownCount, "Số brand ở danh sách quản trị so với dropdown Brand"));
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Tìm bằng Enter, giữ từ khóa và ẩn phân trang khi kết quả ít")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra gõ đủ tên brand test vào ô tìm kiếm rồi Enter thì URL có ?search=, ô tìm giữ từ khoá, bảng có đúng 1 dòng và không có thanh phân trang.")
    public void testSearchByEnterKeepsKeyword() {
        tc("AEC_BRAND_TC_016", "regression", "brand", "auth");
        String name = "AutoSearch_" + newToken("TC016");
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);

        Allure.step("Act: Gõ tên brand test vào ô tìm kiếm, nhấn Enter", () -> brandPage.search(name));

        Allure.step("Assert: URL có ?search=, giữ từ khoá, 1 dòng, không phân trang", () -> {
            Assert.assertTrue(brandPage.currentUrl().contains("search=" + name), "Thanh địa chỉ: " + brandPage.currentUrl());
            Assert.assertEquals(brandPage.searchValue(), name, "Ô tìm kiếm phải giữ từ khoá");
            Assert.assertEquals(brandPage.getResultNames(), List.of(name), "Kết quả tìm kiếm");
            Assert.assertFalse(brandPage.hasPagination(), "(REQ-12) Không được có thanh phân trang");
        });
    }

    @DataProvider(name = "partialKeywords", parallel = true)
    public Object[][] partialKeywords() {
        return new Object[][]{{"a", "head"}, {"b", "middle"}, {"c", "tail"}, {"d", "short"}};
    }

    @Test(dataProvider = "partialKeywords", groups = {"regression", "brand", "auth"}, description = "Tìm theo một phần tên vẫn ra kết quả")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tìm bằng phần đầu, phần giữa, phần cuối, một đoạn ngắn của tên brand test đều ra brand đó, mọi dòng kết quả đều chứa từ khoá.")
    public void testSearchByPartialName(String variant, String part) {
        tc("AEC_BRAND_TC_017", "regression", "brand", "auth");
        String token = newToken("TC017" + variant);
        String name = "AutoSearch_" + token;
        String hex = token.substring(token.lastIndexOf('_') + 1);
        String keyword = switch (part) {
            case "head" -> name.substring(0, name.lastIndexOf('_'));
            case "middle" -> token.substring(0, token.lastIndexOf('_'));
            case "tail" -> name.substring(name.length() - 9);
            default -> "_" + hex;
        };
        Allure.parameter("Biến thể", variant + " — " + part + ": " + keyword);
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);

        Allure.step("Act: Tìm theo " + keyword, () -> brandPage.search(keyword));

        Allure.step("Assert: Có brand test, mọi dòng chứa từ khoá", () -> {
            List<String> names = brandPage.getResultNames();
            Assert.assertTrue(names.contains(name), "Kết quả phải có brand test: " + names);
            Assert.assertTrue(names.stream().allMatch(n -> n.toLowerCase().contains(keyword.toLowerCase())),
                    "Mọi dòng phải chứa từ khoá: " + names);
        });
    }

    @DataProvider(name = "caseKeywords", parallel = true)
    public Object[][] caseKeywords() {
        return new Object[][]{{"a", "lower"}, {"b", "upper"}, {"c", "mixed"}};
    }

    @Test(dataProvider = "caseKeywords", groups = {"regression", "brand", "auth"}, description = "Tìm kiếm không phân biệt chữ hoa, chữ thường")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tìm tên brand test viết toàn chữ thường, toàn chữ hoa, hoặc trộn hoa/thường đều ra brand test.")
    public void testSearchIgnoresCase(String variant, String style) {
        tc("AEC_BRAND_TC_018", "regression", "brand", "auth");
        String name = "AutoSearch_" + newToken("TC018" + variant);
        String keyword = switch (style) {
            case "lower" -> name.toLowerCase();
            case "upper" -> name.toUpperCase();
            default -> mixCase(name);
        };
        Allure.parameter("Biến thể", variant + " — " + keyword);
        BrandPage brandPage = openBrandsAsAdmin();
        createBrand(brandPage, name);

        Allure.step("Act: Tìm theo " + keyword, () -> brandPage.search(keyword));

        Allure.step("Assert: Có brand test trong kết quả", () ->
                Assert.assertTrue(brandPage.getResultNames().contains(name), "Kết quả: " + brandPage.getResultNames()));
    }

    private static String mixCase(String text) {
        StringBuilder mixed = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            mixed.append(i % 2 == 0 ? Character.toLowerCase(c) : Character.toUpperCase(c));
        }
        return mixed.toString();
    }

    @Test(groups = {"regression", "brand", "auth"}, description = "Tìm từ khóa không khớp brand nào")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tìm zzqxnotexist987 thì bảng chỉ còn dòng Nothing found, không có thanh phân trang, ô tìm giữ từ khoá, không có trang lỗi.")
    public void testSearchWithNoMatch() {
        tc("AEC_BRAND_TC_019", "regression", "brand", "auth");
        String keyword = "zzqxnotexist987";
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Tìm " + keyword, () -> brandPage.search(keyword));

        Allure.step("Assert: Bảng rỗng có dòng Nothing found, không phân trang, giữ từ khoá", () -> {
            Assert.assertEquals(brandPage.rowCount(), 0, "Không được có dòng dữ liệu");
            Assert.assertTrue(brandPage.isNothingFoundShown(), "Phải có dòng Nothing found");
            Assert.assertFalse(brandPage.hasPagination(), "Không được có thanh phân trang");
            Assert.assertEquals(brandPage.searchValue(), keyword, "Ô tìm kiếm phải giữ từ khoá");
            Assert.assertFalse(brandPage.isServerErrorPage(), "Không được có trang lỗi");
        });
    }

    @DataProvider(name = "unusualKeywords", parallel = true)
    public Object[][] unusualKeywords() {
        return new Object[][]{
                {"a", "Thương hiệu Ánh Dương"},
                {"b", "%_%"},
                {"c", "Brand's \"Co\""},
                {"d", "<script>alert(1)</script>"},
                {"e", "' OR 1=1--"},
                {"f", "😀🚀"}
        };
    }

    @Test(dataProvider = "unusualKeywords", groups = {"regression", "brand", "auth", "security"},
            description = "Ô tìm kiếm chịu được dữ liệu bất thường")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tìm bằng tiếng Việt có dấu, ký tự đại diện, dấu nháy, mã script, SQL, emoji thì trang vẫn hiển thị bình thường, không trang lỗi, không hộp thoại, ô tìm giữ đúng chuỗi đã gõ.")
    public void testSearchWithUnusualInput(String variant, String keyword) {
        tc("AEC_BRAND_TC_020", "regression", "brand", "auth", "security");
        Allure.parameter("Biến thể", variant + " — " + keyword);
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Tìm " + keyword, () -> brandPage.search(keyword));

        Allure.step("Assert: Trang hiển thị bình thường, giữ chuỗi đã gõ", () -> {
            Assert.assertFalse(brandPage.isAlertOpen(), "Không được bật hộp thoại");
            Assert.assertFalse(brandPage.isServerErrorPage(), "Không được ra trang lỗi");
            Assert.assertTrue(brandPage.isTitleDisplayed(), "Phải vẫn là trang All Brands");
            Assert.assertEquals(brandPage.searchValue(), keyword, "Ô tìm kiếm phải giữ đúng chuỗi");
        });
    }

    @DataProvider(name = "protectedBrandUrls", parallel = true)
    public Object[][] protectedBrandUrls() {
        return new Object[][]{{"a", "/admin/brands"}, {"b", "/admin/brands/edit/999999999?lang=en"}};
    }

    @Test(dataProvider = "protectedBrandUrls", groups = {"regression", "brand", "security", "smoke"},
            description = "Chưa đăng nhập thì không vào được trang Brand")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra chưa đăng nhập mở danh sách brand hoặc form sửa brand thì bị đưa về trang đăng nhập.")
    public void testBrandPagesRequireLogin(String variant, String path) {
        tc("AEC_BRAND_TC_046", "regression", "brand", "security", "smoke");
        Allure.parameter("Biến thể", variant + " — " + path);
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Act: Mở " + path + " khi chưa đăng nhập", () -> driver().get(config.getBaseUrl() + path));

        Allure.step("Assert: Về trang đăng nhập", () -> {
            loginPage.waitForPath("/login");
            Assert.assertEquals(loginPage.currentPath(), "/login", "Phải chuyển về /login");
            Assert.assertTrue(loginPage.isHeadingDisplayed(), "Phải thấy form đăng nhập");
        });
    }

    @Test(groups = {"regression", "brand", "auth", "security", "smoke"},
            description = "Đăng xuất rồi Back và tải lại không vào được trang Brand")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra đang ở trang Brand, đăng xuất, bấm Back rồi tải lại trang thì bị đưa về trang đăng nhập.")
    public void testBrandPageAfterLogoutBackAndReload() {
        tc("AEC_BRAND_TC_046", "regression", "brand", "auth", "security", "smoke");
        Allure.parameter("Biến thể", "c — đăng xuất → Back → tải lại");
        openBrandsAsAdmin();
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Act: Đăng xuất", () -> new DashboardPage(driver()).logout());
        Allure.step("Act: Bấm Back rồi tải lại trang", () -> {
            driver().navigate().back();
            driver().navigate().refresh();
        });

        Allure.step("Assert: Về trang đăng nhập", () -> {
            loginPage.waitForPath("/login");
            Assert.assertEquals(loginPage.currentPath(), "/login", "Phải chuyển về /login");
            Assert.assertTrue(loginPage.isHeadingDisplayed(), "Phải thấy form đăng nhập");
        });
    }

    @Test(groups = {"regression", "brand", "auth", "security"}, description = "Role hiện tại có đủ quyền xem, tạo, sửa, xóa brand")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tài khoản admin hiện có (role staff) thấy mục Brand trong Products, thẻ Add New Brand có nút Save bấm được, mỗi dòng có nút sửa và xoá. Role khác chưa có tài khoản (AMB-BRAND-02).")
    public void testCurrentRoleHasFullBrandPermission() {
        tc("AEC_BRAND_TC_047", "regression", "brand", "auth", "security");
        loginAsAdmin();
        BrandPage brandPage = new BrandPage(driver());

        Allure.step("Assert: (1) Sidebar Products có mục Brand", () ->
                Assert.assertTrue(brandPage.isBrandInProductsMenu(), "Sidebar Products phải có mục Brand"));

        Allure.step("Act: Bấm Brand", brandPage::openFromSidebar);

        Allure.step("Assert: (2)(3)(4) Có thẻ Add New Brand, nút sửa và xoá mỗi dòng", () -> {
            Assert.assertTrue(brandPage.cardHeaders().contains("Add New Brand"), "Phải có thẻ Add New Brand");
            Assert.assertTrue(brandPage.isSaveButtonEnabled(), "Nút Save phải bấm được");
            Assert.assertTrue(brandPage.rowCount() > 0, "Danh sách phải có dòng");
            Assert.assertTrue(brandPage.everyRowHasEditAndDelete(), "Mỗi dòng phải có nút bút chì và thùng rác");
        });
    }

    @DataProvider(name = "windowSizes", parallel = true)
    public Object[][] windowSizes() {
        return new Object[][]{{"a", 1920, 1080}, {"b", 1366, 768}, {"c", 768, 1024}};
    }

    @Test(dataProvider = "windowSizes", groups = {"regression", "brand", "auth", "responsive"},
            description = "Màn All Brands hiển thị đúng ở các kích thước màn hình đã chốt")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra ở 1920×1080, 1366×768, 768×1024: không có thanh cuộn ngang, bảng đủ 4 cột, đọc được tên brand và nút sửa/xoá, thẻ Add New Brand đủ 4 ô và nút Save.")
    public void testAllBrandsResponsive(String variant, int width, int height) {
        tc("AEC_BRAND_TC_049", "regression", "brand", "auth", "responsive");
        Allure.parameter("Biến thể", variant + " — " + width + "×" + height);
        loginAsAdmin();
        BrandPage brandPage = new BrandPage(driver());

        Allure.step("Act: Đặt cửa sổ " + width + "×" + height + " rồi mở All Brands", () -> {
            driver().manage().window().setSize(new Dimension(width, height));
            brandPage.open();
        });

        Allure.step("Assert: Bố cục dùng được, không cuộn ngang", () -> {
            Assert.assertTrue(brandPage.hasNoHorizontalScroll(), "Không được có thanh cuộn ngang toàn trang");
            Assert.assertEquals(brandPage.tableHeaders(), List.of("#", "Name", "Logo", "Options"), "Bảng phải đủ 4 cột");
            Assert.assertTrue(brandPage.getResultNames().stream().noneMatch(String::isBlank), "Tên brand phải đọc được");
            Assert.assertTrue(brandPage.everyRowHasEditAndDelete(), "Nút sửa/xoá phải hiện");
            Assert.assertEquals(brandPage.formLabels(), List.of("Name", "Logo (120x80)", "Meta Title", "Meta description"),
                    "Thẻ Add New Brand đủ 4 ô");
            Assert.assertTrue(brandPage.isSaveButtonEnabled(), "Nút Save phải hiện và bấm được");
        });
    }

    @Test(groups = {"regression", "brand", "auth", "accessibility"}, description = "Thao tác màn Brand bằng bàn phím")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra tìm kiếm bằng Enter, Tab từ ô Name đi lần lượt Meta Title → Meta description → Save (ô Logo không nhận focus), ô đang focus có viền thấy được, focus Save rồi Enter thì tạo brand.")
    public void testBrandPageKeyboard() {
        tc("AEC_BRAND_TC_050", "regression", "brand", "auth", "accessibility");
        String name = "AutoKey_" + newToken("TC050");
        BrandPage brandPage = openBrandsAsAdmin();

        Allure.step("Act: Gõ AutoBrand vào ô tìm kiếm, nhấn Enter", () -> brandPage.search("AutoBrand"));
        Allure.step("Assert: Danh sách được lọc", () ->
                Assert.assertTrue(brandPage.getResultNames().stream().allMatch(n -> n.toLowerCase().contains("autobrand")),
                        "Mọi dòng phải chứa AutoBrand"));

        List<String> sequence = Allure.step("Act: Bấm vào ô Name, nhấn Tab 3 lần", () -> brandPage.tabSequenceFromName(3));
        Allure.step("Assert: Focus đi Meta Title → Meta description → Save", () ->
                Assert.assertEquals(sequence, List.of("meta_title", "meta_description", "Save"), "Thứ tự focus"));

        Allure.step("Assert: Ô nhập đang focus có viền nhìn thấy được", () ->
                Assert.assertEquals(brandPage.fieldsWithoutVisibleFocus(), List.of(), "Các ô không đổi gì khi được focus"));

        Allure.step("Act: Nhập Name, focus Save rồi nhấn Enter", () -> {
            brandPage.enterNewBrandName(name);
            brandPage.pressEnterOnSave();
        });

        Allure.step("Assert: Brand được tạo", () ->
                Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Thông báo sau khi nhấn Enter ở Save"));
    }
}
