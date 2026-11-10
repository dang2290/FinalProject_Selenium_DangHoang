package com.aec.tests;

import com.aec.pages.CategoryFormPage;
import com.aec.pages.CategoryFormPage.ImageField;
import com.aec.pages.CategoryListPage;
import com.aec.pages.LoginPage;
import com.aec.pages.MediaLibraryModal;
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Màn All categories: giao diện, lối vào, phân trang, sắp xếp, tìm kiếm, Featured, phân quyền, bố cục, bàn phím, hiệu năng.
 * TC_009, 010, 011-a, 049 chỉ đọc dữ liệu dùng chung. TC ở docs/testcases/category/web/test_cases_category_web.md.
 */
@Feature("Manage Category")
@Story("Danh sách & tìm kiếm danh mục")
public class CategoryListTest extends CategoryTestBase {

    private static final int PAGE_SIZE = 15;
    private static final List<String> ALL_COLUMNS = List.of("#", "Name", "Parent Category", "Order Level", "Level",
            "Banner", "Icon", "Featured", "Commission", "Options");
    private static final Duration MAX_RESPONSE = Duration.ofSeconds(3);

    @Test(groups = {"smoke", "category", "auth", "ui"}, description = "Màn All categories hiển thị đủ thành phần với nhãn nguyên văn ở màn rộng")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra vào All categories từ sidebar Products → Category thì đúng URL, có tiêu đề, nút Add New category, thẻ Categories, ô tìm rỗng, 10 cột đúng thứ tự, mỗi dòng có công tắc Featured, nút sửa, xoá và có thanh phân trang.")
    public void testAllCategoriesPageElements() {
        tc("AEC_CAT_TC_001", "smoke", "category", "auth", "ui");
        loginAsAdmin();
        CategoryListPage list = new CategoryListPage(driver());

        Allure.step("Act: Ở sidebar bấm Products → Category", list::openFromSidebar);

        Allure.step("Assert: Bảng kiểm màn All categories", () -> {
            SoftAssert soft = new SoftAssert();
            soft.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/categories", "Thanh địa chỉ");
            soft.assertTrue(list.isTitleDisplayed(), "(1) Tiêu đề All categories");
            soft.assertTrue(list.isAddNewButtonDisplayed(), "(1) Nút Add New category");
            soft.assertEquals(list.cardHeaders(), List.of("Categories"), "(1) Tiêu đề thẻ bảng");
            soft.assertTrue(list.isCategoryMenuActive(), "(1) Mục Category ở sidebar được tô sáng");
            soft.assertEquals(list.searchValue(), "", "(2) Ô tìm kiếm rỗng");
            soft.assertEquals(list.searchPlaceholder(), "Type name & Enter", "(2) Gợi ý ô tìm kiếm");
            soft.assertEquals(list.tableHeaders(), ALL_COLUMNS, "(3) Hàng tiêu đề bảng");
            soft.assertTrue(list.everyRowHasFeaturedEditDelete(), "(4)(5) Mỗi dòng có công tắc Featured, bút chì, thùng rác");
            soft.assertTrue(list.hasPagination(), "(6) Thanh phân trang");
            soft.assertAll();
        });
    }

    @DataProvider(name = "entryWays", parallel = true)
    public Object[][] entryWays() {
        return new Object[][]{{"a", "sidebar"}, {"b", "url"}, {"c", "createForm"}};
    }

    @Test(dataProvider = "entryWays", groups = {"smoke", "category", "auth"}, description = "Mở danh sách Category bằng mọi lối vào")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra vào All categories từ sidebar, gõ thẳng URL, và từ form Add New category bấm Category ở sidebar đều ra cùng màn: bảng danh sách, mục Category tô sáng, URL /admin/categories.")
    public void testOpenCategoryListFromEveryEntry(String variant, String entry) {
        tc("AEC_CAT_TC_004", "smoke", "category", "auth");
        Allure.parameter("Biến thể", variant + " — " + entry);
        loginAsAdmin();
        CategoryListPage list = new CategoryListPage(driver());

        Allure.step("Act: Vào trang theo lối " + entry, () -> {
            switch (entry) {
                case "sidebar" -> list.openFromSidebar();
                case "url" -> list.open();
                default -> {
                    new CategoryFormPage(driver()).openCreate();
                    list.openFromSidebar();
                }
            }
        });

        Allure.step("Assert: Màn All categories, mục Category tô sáng, đúng URL", () -> {
            Assert.assertTrue(list.isTitleDisplayed(), "Tiêu đề All categories");
            Assert.assertTrue(list.rowCount() > 0, "Phải có bảng danh sách");
            Assert.assertTrue(list.isCategoryMenuActive(), "Mục Category ở sidebar phải được tô sáng");
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/categories", "Thanh địa chỉ");
        });
    }

    @Test(groups = {"smoke", "category", "auth"}, description = "Nút Add New category mở đúng form tạo")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Biến thể a: bấm Add New category thì mở /admin/categories/create với ô Name rỗng.")
    public void testAddNewButtonOpensCreateForm() {
        tc("AEC_CAT_TC_005", "smoke", "category", "auth");
        Allure.parameter("Biến thể", "a — Add New category");
        CategoryListPage list = openCategoriesAsAdmin();

        CategoryFormPage form = Allure.step("Act: Bấm Add New category", list::clickAddNew);

        Allure.step("Assert: Form tạo, Name rỗng", () -> {
            Assert.assertEquals(form.currentUrl(), config.getBaseUrl() + "/admin/categories/create", "Thanh địa chỉ");
            Assert.assertEquals(form.nameValue(), "", "Ô Name");
        });
    }

    @Test(groups = {"smoke", "category", "auth"}, description = "Nút Edit mở đúng form sửa của danh mục")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Biến thể b: nút bút chì có chú thích Edit, bấm vào mở /admin/categories/edit/<id>?lang=en với Name đúng tên danh mục test.")
    public void testEditButtonOpensEditForm() {
        tc("AEC_CAT_TC_005", "smoke", "category", "auth");
        Allure.parameter("Biến thể", "b — nút Edit");
        String name = "AutoCat_" + newToken("TC005b");
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);

        Allure.step("Act: Tìm danh mục test", () -> findRow(list, name));
        Allure.step("Assert: Rê chuột lên bút chì hiện Edit", () ->
                Assert.assertEquals(list.editTooltipOfFirstRow(), "Edit", "Chú thích nút bút chì"));

        CategoryFormPage form = Allure.step("Act: Bấm nút bút chì", () -> list.editRow(list.rowIndexOf(name)));
        Allure.step("Assert: Form sửa đúng danh mục", () -> {
            Assert.assertTrue(form.currentUrl().matches(".*/admin/categories/edit/\\d+\\?lang=en$"), "Thanh địa chỉ: " + form.currentUrl());
            Assert.assertEquals(form.nameValue(), name, "Ô Name");
        });
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Danh sách chia trang 15 dòng và chuyển trang được")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra trang 1 có 15 dòng; bấm 2 sang trang 2 (URL page=2, số 2 được tô, dữ liệu khác trang 1); ‹ về trang 1; › sang trang 2; trang cuối có 1–15 dòng và nút › không bấm được.")
    public void testPaginationNavigation() {
        tc("AEC_CAT_TC_009", "regression", "category", "auth");
        CategoryListPage list = openCategoriesAsAdmin();

        Allure.step("Assert: Trang 1 có 15 dòng", () -> Assert.assertEquals(list.rowCount(), PAGE_SIZE, "Số dòng trang 1"));

        Allure.step("Act: Bấm 2", () -> list.clickPage("2"));
        Allure.step("Assert: Sang trang 2", () -> {
            Assert.assertTrue(list.currentUrl().contains("page=2"), "Thanh địa chỉ: " + list.currentUrl());
            Assert.assertEquals(list.activePage(), "2", "Số trang được tô");
            Assert.assertEquals(list.rowNumbers().get(0).intValue(), PAGE_SIZE + 1, "Dữ liệu trang 2 khác trang 1");
        });

        Allure.step("Act: Bấm ‹", () -> list.clickPage("‹"));
        Allure.step("Assert: Về trang 1", () -> Assert.assertEquals(list.activePage(), "1", "Số trang được tô"));

        Allure.step("Act: Bấm ›", () -> list.clickPage("›"));
        Allure.step("Assert: Sang trang 2", () -> Assert.assertEquals(list.activePage(), "2", "Số trang được tô"));

        Allure.step("Act: Bấm số trang cuối cùng", () -> list.clickPage(String.valueOf(list.lastPageNumber())));
        Allure.step("Assert: Trang cuối có 1–15 dòng, nút › không bấm được", () -> {
            int rows = list.rowCount();
            Assert.assertTrue(rows >= 1 && rows <= PAGE_SIZE, "Trang cuối có " + rows + " dòng");
            Assert.assertTrue(list.isNextDisabled(), "Nút › phải không bấm được ở trang cuối");
        });
    }

    @DataProvider(name = "numberedPages", parallel = true)
    public Object[][] numberedPages() {
        return new Object[][]{{"a", 1}, {"b", 2}, {"c", 0}};
    }

    @Test(dataProvider = "numberedPages", groups = {"regression", "category", "auth"}, description = "Cột # đánh số liên tục qua các trang")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra cột # ở trang N chạy liên tục từ 15 × (N − 1) + 1, tăng từng 1 (trang 1: 1–15, trang 2: 16–30, trang cuối nối tiếp).")
    public void testRowNumbersContinueAcrossPages(String variant, int page) {
        tc("AEC_CAT_TC_010", "regression", "category", "auth");
        Allure.parameter("Biến thể", variant + " — " + (page == 0 ? "trang cuối" : "trang " + page));
        CategoryListPage list = openCategoriesAsAdmin();

        int target = Allure.step("Act: Mở trang theo biến thể", () -> {
            int p = page == 0 ? list.lastPageNumber() : page;
            list.openPage(p);
            return p;
        });

        Allure.step("Assert: Cột # nối tiếp từ " + "15 × (trang − 1) + 1", () -> {
            int first = (target - 1) * PAGE_SIZE + 1;
            List<Integer> expected = IntStream.range(first, first + list.rowCount()).boxed().toList();
            Assert.assertEquals(list.rowNumbers(), expected, "Cột # trang " + target);
            if (page != 0) {
                Assert.assertEquals(list.rowCount(), PAGE_SIZE, "Trang đầy phải có 15 dòng");
            }
        });
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Danh sách sắp theo Order Level giảm dần")
    @Severity(SeverityLevel.NORMAL)
    @Description("Biến thể a: Order Level 15 dòng trang 1 và dòng đầu trang 2 không tăng dần từ trên xuống.")
    public void testListSortedByOrderLevelDescending() {
        tc("AEC_CAT_TC_011", "regression", "category", "auth");
        Allure.parameter("Biến thể", "a — chỉ quan sát");
        CategoryListPage list = openCategoriesAsAdmin();

        List<Long> levels = Allure.step("Act: Đọc Order Level trang 1 và dòng đầu trang 2", () -> {
            List<Long> values = new ArrayList<>(list.orderLevels());
            values.add(list.openPage(2).orderLevels().get(0));
            return values;
        });

        Allure.step("Assert: Mỗi giá trị ≥ giá trị ngay dưới", () ->
                IntStream.range(1, levels.size()).forEach(i -> Assert.assertTrue(levels.get(i - 1) >= levels.get(i),
                        "Order Level dòng " + i + " < dòng " + (i + 1) + ": " + levels)));
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Danh mục ưu tiên cao lên đầu danh sách")
    @Severity(SeverityLevel.NORMAL)
    @Description("Biến thể b: tạo danh mục Ordering Number 999999999 thì nó nằm ở trang 1, chỉ dưới các danh mục có Order Level cao hơn. TC ghi dòng 1 — dữ liệu dùng chung đang có danh mục 2147483647 nên kiểm vị trí tương đối.")
    public void testHighOrderLevelGoesTop() {
        tc("AEC_CAT_TC_011", "regression", "category", "auth");
        Allure.parameter("Biến thể", "b — Order Level 999999999");
        String name = "AutoCatTop_" + newToken("TC011b");
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name, form -> form.enterOrderLevel("999999999"));

        Allure.step("Act: Mở lại trang 1 (không tìm kiếm)", list::open);

        Allure.step("Assert: Danh mục test ở trang 1, mọi dòng trên nó có Order Level ≥ 999999999", () -> {
            int row = list.rowIndexOf(name);
            Assert.assertTrue(row >= 0, "Danh mục test phải ở trang 1");
            Assert.assertEquals(list.orderLevelOf(row), "999999999", "Order Level");
            List<Long> above = list.orderLevels().subList(0, row);
            Assert.assertTrue(above.stream().allMatch(v -> v >= 999_999_999L), "Dòng phía trên có Order Level thấp hơn: " + above);
        });
    }

    @DataProvider(name = "imageCells", parallel = true)
    public Object[][] imageCells() {
        return new Object[][]{{"a", false}, {"b", true}};
    }

    @Test(dataProvider = "imageCells", groups = {"regression", "category", "auth"}, description = "Cột Banner và Icon hiện ảnh thu nhỏ hoặc dấu gạch")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra danh mục chỉ có Name thì cột Banner, Icon là —; danh mục có Banner và Icon (ảnh brandimg trong thư viện) thì hai cột hiện ảnh thu nhỏ tải được.")
    public void testBannerAndIconColumns(String variant, boolean withImages) {
        tc("AEC_CAT_TC_013", "regression", "category", "auth");
        Allure.parameter("Biến thể", variant + (withImages ? " — có ảnh" : " — chỉ Name"));
        String name = "AutoCat_" + newToken("TC013" + variant);
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name, form -> {
            if (withImages) {
                for (ImageField field : ImageField.values()) {
                    MediaLibraryModal library = form.openLibrary(field);
                    library.search("brandimg");
                    library.selectFile(0);
                    library.addFiles();
                }
            }
        });

        int row = Allure.step("Act: Tìm danh mục", () -> findRow(list, name));

        Allure.step("Assert: Cột Banner, Icon", () -> {
            if (withImages) {
                Assert.assertTrue(list.hasLoadedImage(row, true), "Banner phải là ảnh thu nhỏ tải được");
                Assert.assertTrue(list.hasLoadedImage(row, false), "Icon phải là ảnh thu nhỏ tải được");
            } else {
                Assert.assertEquals(list.bannerOf(row), EMPTY_CELL, "Banner");
                Assert.assertEquals(list.iconOf(row), EMPTY_CELL, "Icon");
            }
        });
    }

    @DataProvider(name = "searchKeywords", parallel = true)
    public Object[][] searchKeywords() {
        return new Object[][]{{"a", "full"}, {"b", "middle"}, {"c", "upper"}, {"d", "lower"}, {"e", "tail"}, {"f", "fullNoPaging"}};
    }

    @Test(dataProvider = "searchKeywords", groups = {"regression", "category", "auth"}, description = "Tìm kiếm khớp một phần, không phân biệt hoa thường")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tìm bằng tên đầy đủ, đoạn giữa, chữ hoa, chữ thường, đoạn cuối đều ra danh mục test và ô tìm giữ từ khoá; kết quả ≤ 15 dòng thì không có thanh phân trang.")
    public void testSearchPartialCaseInsensitive(String variant, String style) {
        tc("AEC_CAT_TC_014", "regression", "category", "auth");
        String token = newToken("TC014" + variant);
        String name = "AutoCatSrch_" + token;
        String keyword = switch (style) {
            case "middle" -> name.substring(7, name.lastIndexOf('_') - 4);
            case "upper" -> name.toUpperCase();
            case "lower" -> name.toLowerCase();
            case "tail" -> token;
            default -> name;
        };
        Allure.parameter("Biến thể", variant + " — " + keyword);
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);

        Allure.step("Act: Tìm " + keyword, () -> {
            list.open();
            list.search(keyword);
        });

        Allure.step("Assert: Có danh mục test, ô tìm giữ từ khoá", () -> {
            Assert.assertTrue(list.names().contains(name), "Kết quả phải có danh mục test: " + list.names());
            Assert.assertEquals(list.searchValue(), keyword, "Ô tìm kiếm");
            if ("fullNoPaging".equals(style)) {
                Assert.assertFalse(list.hasPagination(), "Kết quả ≤ 15 dòng thì không có thanh phân trang");
            }
        });
    }

    @DataProvider(name = "emptyishKeywords", parallel = true)
    public Object[][] emptyishKeywords() {
        return new Object[][]{{"a", "zzqxnotexist987"}, {"b", ""}, {"c", "   "}};
    }

    @Test(dataProvider = "emptyishKeywords", groups = {"regression", "category", "auth"}, description = "Tìm kiếm không có kết quả, ô rỗng và ô chỉ có khoảng trắng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tìm từ khoá không khớp thì bảng chỉ còn Nothing found, không phân trang, giữ từ khoá; ô rỗng hoặc toàn khoảng trắng thì hiện lại toàn bộ danh sách từ trang 1 có phân trang, không lỗi.")
    public void testSearchNoMatchOrBlank(String variant, String keyword) {
        tc("AEC_CAT_TC_015", "regression", "category", "auth");
        Allure.parameter("Biến thể", variant + " — [" + keyword + "]");
        CategoryListPage list = openCategoriesAsAdmin();

        Allure.step("Act: Tìm [" + keyword + "]", () -> list.search(keyword));

        Allure.step("Assert: Kết quả theo biến thể", () -> {
            Assert.assertFalse(list.isServerErrorPage(), "Không được có trang lỗi");
            if ("a".equals(variant)) {
                Assert.assertEquals(list.rowCount(), 0, "Không được có dòng dữ liệu");
                Assert.assertTrue(list.isNothingFoundShown(), "Phải có dòng Nothing found");
                Assert.assertFalse(list.hasPagination(), "Không được có thanh phân trang");
                Assert.assertEquals(list.searchValue(), keyword, "Ô tìm kiếm phải giữ từ khoá");
            } else {
                Assert.assertEquals(list.rowCount(), PAGE_SIZE, "Phải hiện lại trang 1 đầy đủ");
                Assert.assertEquals(list.rowNumbers().get(0).intValue(), 1, "Bắt đầu từ trang 1");
                Assert.assertTrue(list.hasPagination(), "Phải có thanh phân trang");
            }
        });
    }

    @DataProvider(name = "featuredSwitches", parallel = true)
    public Object[][] featuredSwitches() {
        return new Object[][]{{"a", true}, {"b", false}};
    }

    @Test(dataProvider = "featuredSwitches", groups = {"regression", "category", "auth"}, description = "Bật và tắt Featured ngay trên bảng, trạng thái giữ sau khi tải lại")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bấm công tắc Featured thì đổi ngay, không hộp xác nhận, báo Featured categories updated successfully; tải lại trang vẫn giữ trạng thái (a: bật · b: bật rồi tắt).")
    public void testToggleFeatured(String variant, boolean turnOn) {
        tc("AEC_CAT_TC_016", "regression", "category", "auth");
        Allure.parameter("Biến thể", variant + (turnOn ? " — tắt → bật" : " — bật → tắt"));
        String name = "AutoCat_" + newToken("TC016" + variant);
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);
        Allure.step("Arrange: Tìm danh mục test" + (turnOn ? "" : ", bật Featured trước"), () -> {
            int row = findRow(list, name);
            if (!turnOn) {
                list.toggleFeatured(row);
                Assert.assertEquals(list.getNotifyMessage(), FEATURED_UPDATED, "Bật Featured phải thành công");
                list.search(name);
            }
        });

        Allure.step("Act: Bấm công tắc Featured", () -> list.toggleFeatured(list.rowIndexOf(name)));

        Allure.step("Assert: Công tắc đổi ngay, không hộp xác nhận, có thông báo", () -> {
            Assert.assertFalse(list.isDeleteModalShown(), "Không được có hộp xác nhận");
            Assert.assertEquals(list.isFeaturedOn(list.rowIndexOf(name)), turnOn, "Trạng thái công tắc");
            Assert.assertEquals(list.getNotifyMessage(), FEATURED_UPDATED, "Thông báo sau khi đổi Featured");
        });

        Allure.step("Act: Tải lại trang", () -> {
            driver().navigate().refresh();
            list.waitUntilLoaded();
        });

        Allure.step("Assert: Trạng thái giữ sau khi tải lại", () ->
                Assert.assertEquals(list.isFeaturedOn(list.rowIndexOf(name)), turnOn, "Công tắc sau khi tải lại"));
    }

    @DataProvider(name = "protectedCategoryUrls", parallel = true)
    public Object[][] protectedCategoryUrls() {
        return new Object[][]{{"a", "/admin/categories"}, {"b", "/admin/categories/create"}};
    }

    @Test(dataProvider = "protectedCategoryUrls", groups = {"regression", "category", "security"},
            description = "Chưa đăng nhập không vào được trang Category")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra chưa đăng nhập mở danh sách danh mục hoặc form tạo danh mục thì bị đưa về trang đăng nhập.")
    public void testCategoryPagesRequireLogin(String variant, String path) {
        tc("AEC_CAT_TC_020", "regression", "category", "security");
        Allure.parameter("Biến thể", variant + " — " + path);
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Act: Mở " + path + " khi chưa đăng nhập", () -> driver().get(config.getBaseUrl() + path));

        Allure.step("Assert: Về trang đăng nhập", () -> {
            loginPage.waitForPath("/login");
            Assert.assertEquals(loginPage.currentPath(), "/login", "Phải chuyển về /login");
            Assert.assertTrue(loginPage.isHeadingDisplayed(), "Phải thấy form đăng nhập");
        });
    }

    @Test(groups = {"regression", "category", "auth", "security"}, description = "Admin hiện tại làm được đủ thao tác trên danh mục")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Biến thể c: đăng nhập admin thì sidebar có Products → Category; danh sách có nút Add New category, công tắc Featured, nút bút chì và thùng rác ở mọi dòng.")
    public void testAdminHasFullCategoryPermission() {
        tc("AEC_CAT_TC_020", "regression", "category", "auth", "security");
        Allure.parameter("Biến thể", "c — admin hiện tại");
        loginAsAdmin();
        CategoryListPage list = new CategoryListPage(driver());

        Allure.step("Assert: Sidebar Products có mục Category", () ->
                Assert.assertTrue(list.isCategoryInProductsMenu(), "Sidebar Products phải có mục Category"));

        Allure.step("Act: Bấm Category", list::openFromSidebar);

        Allure.step("Assert: Nút Add New category, công tắc Featured, bút chì, thùng rác", () -> {
            Assert.assertTrue(list.isAddNewButtonDisplayed(), "Phải có nút Add New category");
            Assert.assertTrue(list.rowCount() > 0, "Danh sách phải có dòng");
            Assert.assertTrue(list.everyRowHasFeaturedEditDelete(), "Mỗi dòng phải có công tắc Featured, bút chì, thùng rác");
        });
    }

    @DataProvider(name = "injectionKeywords", parallel = true)
    public Object[][] injectionKeywords() {
        return new Object[][]{
                {"a", "<script>alert(1)</script>zzq"},
                {"b", "\"><img src=x onerror=alert(1)>zzq"},
                {"c", "' OR '1'='1 zzq"}
        };
    }

    @Test(dataProvider = "injectionKeywords", groups = {"regression", "category", "auth", "security"},
            description = "Mã script và ký tự đại diện SQL ở ô tìm kiếm không gây lỗi")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tìm bằng mã script, thẻ img onerror, chuỗi SQL (kèm hậu tố zzq<ts>) thì không bật hộp thoại, không trang lỗi, ô tìm hiện nguyên văn và không trả về toàn bộ danh sách.")
    public void testSearchInjectionKeyword(String variant, String head) {
        tc("AEC_CAT_TC_042", "regression", "category", "auth", "security");
        String keyword = head + System.currentTimeMillis() / 1000;
        Allure.parameter("Biến thể", variant + " — " + keyword);
        CategoryListPage list = openCategoriesAsAdmin();

        Allure.step("Act: Tìm " + keyword, () -> list.search(keyword));

        Allure.step("Assert: Không hộp thoại, không lỗi, giữ từ khoá, không có kết quả", () -> {
            Assert.assertFalse(list.isAlertOpen(), "Không được bật hộp thoại");
            Assert.assertFalse(list.isServerErrorPage(), "Không được có trang lỗi");
            Assert.assertEquals(list.searchValue(), keyword, "Ô tìm kiếm phải hiện nguyên văn");
            Assert.assertTrue(list.isNothingFoundShown(), "Phải là Nothing found — chuỗi được hiểu là chữ");
            Assert.assertFalse(list.hasPagination(), "Không được trả về toàn bộ danh sách");
        });
    }

    @DataProvider(name = "responsiveVariants", parallel = true)
    public Object[][] responsiveVariants() {
        return new Object[][]{{"a", 1920, 1080}, {"b", 1366, 768}, {"c", 1366, 768}, {"d", 1366, 768}};
    }

    @Test(dataProvider = "responsiveVariants", groups = {"regression", "category", "auth", "responsive"},
            description = "Bảng danh sách dùng được ở màn rộng và màn hẹp")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra (a) 1920: đủ 10 cột, không cuộn ngang; (b) 1366: chỉ Name, Options, mỗi dòng có dấu +; (c) bấm + mở hàng chi tiết đủ 8 mục, công tắc Featured trong đó bấm được; (d) bấm − thu lại.")
    public void testListResponsive(String variant, int width, int height) {
        tc("AEC_CAT_TC_046", "regression", "category", "auth", "responsive");
        Allure.parameter("Biến thể", variant + " — " + width + "×" + height);
        String name = "AutoCat_" + newToken("TC046" + variant);
        loginAsAdmin();
        createCategory(name);
        CategoryListPage list = new CategoryListPage(driver());

        Allure.step("Act: Đặt cửa sổ " + width + "×" + height + ", tìm danh mục test", () -> {
            driver().manage().window().setSize(new Dimension(width, height));
            findRow(list, name);
        });

        switch (variant) {
            case "a" -> Allure.step("Assert: Đủ 10 cột, không cuộn ngang", () -> {
                Assert.assertEquals(list.tableHeaders(), ALL_COLUMNS, "Cột đang hiện");
                Assert.assertTrue(list.hasNoHorizontalScroll(), "Không được có thanh cuộn ngang");
            });
            case "b" -> Allure.step("Assert: Chỉ Name, Options; mỗi dòng có dấu +", () -> {
                Assert.assertEquals(list.tableHeaders(), List.of("Name", "Options"), "Cột đang hiện");
                Assert.assertTrue(list.everyRowHasExpandToggle(), "Mỗi dòng phải có dấu +");
            });
            case "c" -> {
                Allure.step("Act: Bấm +", () -> list.clickExpandToggle(0));
                Allure.step("Assert: Hàng chi tiết đủ 8 mục", () -> Assert.assertEquals(list.detailHeaders(),
                        List.of("#", "Parent Category", "Order Level", "Level", "Banner", "Icon", "Featured", "Commission"), "Mục trong hàng chi tiết"));
                Allure.step("Act: Bật Featured trong hàng chi tiết", list::toggleDetailFeatured);
                Allure.step("Assert: Có thông báo cập nhật Featured", () ->
                        Assert.assertEquals(list.getNotifyMessage(), FEATURED_UPDATED, "Thông báo sau khi đổi Featured"));
                Allure.step("Act: Tắt lại Featured", list::toggleDetailFeatured);
            }
            default -> {
                Allure.step("Act: Bấm + rồi bấm −", () -> {
                    list.clickExpandToggle(0);
                    list.clickExpandToggle(0);
                });
                Allure.step("Assert: Hàng chi tiết thu lại", () ->
                        Assert.assertFalse(list.isDetailRowShown(), "Hàng chi tiết phải thu lại"));
            }
        }
    }

    @Test(groups = {"regression", "category", "auth", "accessibility"}, description = "Tìm kiếm danh mục bằng bàn phím")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể a: bấm vào ô tìm kiếm, gõ tên danh mục test, nhấn Enter thì danh sách được lọc theo từ khoá.")
    public void testKeyboardSearch() {
        tc("AEC_CAT_TC_047", "regression", "category", "auth", "accessibility");
        Allure.parameter("Biến thể", "a — tìm bằng Enter");
        String name = "AutoCat_" + newToken("TC047a");
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);

        Allure.step("Act: Gõ tên vào ô tìm kiếm, nhấn Enter", () -> {
            list.open();
            list.search(name);
        });

        Allure.step("Assert: Danh sách được lọc", () -> Assert.assertEquals(list.names(), List.of(name), "Kết quả tìm kiếm"));
    }

    @Test(groups = {"regression", "category", "auth", "accessibility"}, description = "Di chuyển trong form tạo danh mục bằng phím Tab")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể b: từ ô Name nhấn Tab liên tục thì focus đi Parent Category → Ordering Number → Type → Meta Title → Meta description → Filtering Attributes → Save (ô Banner, Icon không nhận focus); ô nhập đang focus có viền thấy được.")
    public void testKeyboardTabOrderOnCreateForm() {
        tc("AEC_CAT_TC_047", "regression", "category", "auth", "accessibility");
        Allure.parameter("Biến thể", "b — thứ tự Tab");
        loginAsAdmin();
        CategoryFormPage form = new CategoryFormPage(driver()).openCreate();

        List<String> sequence = Allure.step("Act: Bấm vào ô Name, nhấn Tab 7 lần", () -> form.tabSequenceFromName(7));
        Allure.step("Assert: Thứ tự focus từ trên xuống", () -> Assert.assertEquals(sequence,
                List.of("parent_id", "order_level", "digital", "meta_title", "meta_description", "filtering_attributes[]", "Save"),
                "Thứ tự focus"));

        Allure.step("Assert: Ô nhập đang focus có viền nhìn thấy được", () ->
                Assert.assertEquals(form.fieldsWithoutVisibleFocus(), List.of(), "Các ô không đổi gì khi được focus"));
    }

    @Test(groups = {"regression", "category", "auth", "accessibility"}, description = "Đóng hộp xác nhận xoá bằng phím Esc")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể c: mở hộp Delete Confirmation của danh mục test rồi nhấn Esc thì hộp đóng, danh mục còn nguyên.")
    public void testEscClosesDeleteConfirmation() {
        tc("AEC_CAT_TC_047", "regression", "category", "auth", "accessibility");
        Allure.parameter("Biến thể", "c — Esc");
        String name = "AutoCat_" + newToken("TC047c");
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);

        Allure.step("Act: Mở hộp xoá của danh mục test, nhấn Esc", () -> {
            list.openDeleteOfRow(findRow(list, name));
            list.closeDeleteModal(CategoryListPage.CloseBy.ESC);
        });

        Allure.step("Assert: Hộp đóng, danh mục còn nguyên", () -> {
            Assert.assertTrue(list.waitDeleteModalClosed(), "Hộp Delete Confirmation phải đóng");
            Assert.assertTrue(findRow(list, name) >= 0, "Danh mục test phải còn");
        });
    }

    @Test(groups = {"regression", "category", "auth", "performance"}, description = "Danh sách khoảng 2 800 danh mục tải và chuyển trang trong thời gian chấp nhận được")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra mở danh sách, bấm trang cuối, tìm từ khoá không khớp — mỗi thao tác hiện kết quả trong ≤ 3 giây. Thời gian đo bằng trình duyệt tự động, chỉ mang tính tham khảo.")
    public void testListResponseTime() {
        tc("AEC_CAT_TC_049", "regression", "category", "auth", "performance");
        loginAsAdmin();
        CategoryListPage list = new CategoryListPage(driver());

        Duration open = Allure.step("Act: Mở danh sách, bấm giờ", () -> timed(list::open));
        Duration lastPage = Allure.step("Act: Bấm trang cuối, bấm giờ", () -> timed(() -> list.clickPage(String.valueOf(list.lastPageNumber()))));
        Duration search = Allure.step("Act: Tìm zzqxnotexist987, bấm giờ", () -> timed(() -> list.search("zzqxnotexist987")));

        Allure.step("Assert: Mỗi thao tác ≤ 3 giây", () -> {
            Allure.parameter("Thời gian (ms)", "mở " + open.toMillis() + " · trang cuối " + lastPage.toMillis() + " · tìm " + search.toMillis());
            Assert.assertTrue(open.compareTo(MAX_RESPONSE) <= 0, "Mở danh sách mất " + open.toMillis() + " ms");
            Assert.assertTrue(lastPage.compareTo(MAX_RESPONSE) <= 0, "Sang trang cuối mất " + lastPage.toMillis() + " ms");
            Assert.assertTrue(search.compareTo(MAX_RESPONSE) <= 0, "Tìm kiếm mất " + search.toMillis() + " ms");
        });
    }

    private static Duration timed(Runnable action) {
        long start = System.nanoTime();
        action.run();
        return Duration.ofNanos(System.nanoTime() - start);
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Cột Commission hiển thị dạng phần trăm")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra cột Commission 15 dòng trang 1 đều dạng <số> %, không trống; danh mục test mới tạo có Commission 0 %.")
    public void testCommissionColumnFormat() {
        tc("AEC_CAT_TC_050", "regression", "category", "auth");
        String name = "AutoCat_" + newToken("TC050");
        CategoryListPage list = openCategoriesAsAdmin();

        Allure.step("Assert: Commission trang 1 dạng <số> %", () -> {
            List<String> values = list.commissions();
            Assert.assertEquals(values.size(), PAGE_SIZE, "Số ô Commission");
            Assert.assertTrue(values.stream().allMatch(v -> v.matches("-?\\d+(\\.\\d+)? %")), "Ô Commission: " + values);
        });

        createCategory(name);
        Allure.step("Assert: Danh mục test có Commission 0 %", () ->
                Assert.assertEquals(list.commissionOf(findRow(list, name)), "0 %", "Commission của danh mục test"));
    }
}
