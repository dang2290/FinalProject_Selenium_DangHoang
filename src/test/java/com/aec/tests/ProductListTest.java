package com.aec.tests;

import com.aec.pages.DigitalProductListPage;
import com.aec.pages.LoginPage;
import com.aec.pages.ProductCreatePage;
import com.aec.pages.ProductListPage;
import com.aec.pages.ProductListPage.Listing;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Danh sách sản phẩm: giao diện, lối vào, phân trang, tìm, lọc, sắp xếp, nút trên dòng, phân quyền, bố cục. TC ở docs/testcases/product/web/parts/. */
@Feature("Product")
@Story("Danh sách sản phẩm")
public class ProductListTest extends ProductTestBase {

    private static final List<String> ALL_HEADERS =
            List.of("", "Name", "Added By", "Info", "Total Stock", "Todays Deal", "Published", "Featured", "Options");

    @Test(groups = {"smoke", "product", "auth", "ui"}, description = "Màn All products đủ thành phần, nhãn nguyên văn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Products → All products mở /admin/products/all với tiêu đề All products, thẻ All Product, nút Add New Product; thanh điều khiển Bulk Action, All Sellers, Sort by, ô tìm Type & Enter; 9 cột đúng thứ tự; cột Info có Num of Sale, Base Price, Rating; mỗi dòng 4 nút tròn và 3 công tắc.")
    public void testAllProductsElements() {
        tc("AEC_PROD_TC_001", "smoke", "product", "auth", "ui");
        loginAsAdmin();
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Act: Bấm Products → All products ở sidebar", () -> {
            list.clickSidebar(Listing.ALL.path());
            list.waitUntilLoaded();
        });

        Allure.step("Assert: Bảng kiểm All products", () -> {
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + Listing.ALL.path(), "Đường dẫn");
            Assert.assertTrue(list.isTitleDisplayed(), "Tiêu đề All products");
            Assert.assertEquals(list.cardTitle(), "All Product", "Tiêu đề thẻ");
            Assert.assertTrue(list.hasAddNewButton(), "Nút Add New Product");
            Assert.assertTrue(list.hasBulkAction(), "Nút Bulk Action");
            Assert.assertEquals(list.sellerFilterText(), "All Sellers", "Ô lọc người bán");
            Assert.assertEquals(list.sortText(), "Sort by", "Ô sắp xếp");
            Assert.assertEquals(list.searchPlaceholder(), "Type & Enter", "Gợi ý ô tìm");
            Assert.assertEquals(list.allHeaders(), ALL_HEADERS, "Cột của bảng");
            Assert.assertTrue(list.everyInfoHasSaleBasePriceRating(), "Cột Info phải có Num of Sale, Base Price, Rating");
            Assert.assertTrue(list.everyRowHasFourButtonsAndThreeSwitches(), "Mỗi dòng phải có 4 nút tròn và 3 công tắc");
        });
    }

    @DataProvider(name = "sidebarEntries", parallel = true)
    public Object[][] sidebarEntries() {
        return new Object[][]{{"a", "/admin/products/create"}, {"b", "/admin/products/all"}, {"c", "/admin/products/admin"},
                {"d", "/admin/products/seller"}, {"e", "/admin/digitalproducts"}};
    }

    @Test(dataProvider = "sidebarEntries", groups = {"smoke", "product", "auth"}, description = "Mở các màn Product từ sidebar")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra các mục Add New Product, All products, In House Products, Seller Products, Digital Products ở sidebar mở đúng đường dẫn và màn hình: form tạo; All products có cột Added By; In House không có cột Added By; Seller có tiêu đề All products; Digital Products.")
    public void testOpenProductScreensFromSidebar(String variant, String path) {
        tc("AEC_PROD_TC_006", "smoke", "product", "auth");
        Allure.parameter("Biến thể", variant + " — " + path);
        loginAsAdmin();
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Act: Bấm mục " + path + " ở sidebar", () -> list.clickSidebar(path));

        Allure.step("Assert: Đúng màn hình", () -> {
            Assert.assertEquals(list.currentPath(), path, "Đường dẫn");
            switch (variant) {
                case "a" -> Assert.assertTrue(new ProductCreatePage(driver()).isTitleDisplayed(), "Tiêu đề Add New Product");
                case "b" -> Assert.assertTrue(list.waitUntilLoaded().allHeaders().contains("Added By"), "Phải có cột Added By");
                case "c" -> Assert.assertFalse(list.waitUntilLoaded().allHeaders().contains("Added By"), "Không được có cột Added By");
                case "d" -> Assert.assertTrue(list.waitUntilLoaded().isTitleDisplayed(), "Tiêu đề All products");
                default -> Assert.assertTrue(new DigitalProductListPage(driver()).waitUntilLoaded().isTitleDisplayed(), "Tiêu đề Digital Products");
            }
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Phân trang 15 dòng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra All products trang 1 có 15 dòng và thanh phân trang; bấm trang 2 thì địa chỉ có page=2, 15 dòng khác trang 1.")
    public void testPagination15Rows() {
        tc("AEC_PROD_TC_012", "regression", "product", "auth");
        ProductListPage list = openProductsAsAdmin();
        List<String> firstPage = list.names();

        Allure.step("Assert: Trang 1 có 15 dòng và thanh phân trang", () -> {
            Assert.assertEquals(firstPage.size(), 15, "Số dòng trang 1");
            Assert.assertTrue(list.hasPagination(), "Phải có thanh phân trang");
        });

        Allure.step("Act: Bấm trang 2", () -> list.clickPage("2"));
        Allure.step("Assert: Trang 2 có 15 dòng khác trang 1", () -> {
            Assert.assertTrue(list.currentUrl().contains("page=2"), "Địa chỉ phải có page=2: " + list.currentUrl());
            Assert.assertEquals(list.rowCount(), 15, "Số dòng trang 2");
            Assert.assertNotEquals(list.names(), firstPage, "Trang 2 phải khác trang 1");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Seller Products hiện đúng cấu trúc")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra /admin/products/seller có đủ cột như All products; lúc chưa có sản phẩm người bán bảng hiện Nothing found (giả định đã chốt).")
    public void testSellerProductsStructure() {
        tc("AEC_PROD_TC_013", "regression", "product", "auth");
        loginAsAdmin();
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Act: Mở Seller Products", () -> list.open(Listing.SELLER));

        Allure.step("Assert: Đủ cột, bảng trống hiện Nothing found", () -> {
            Assert.assertEquals(list.allHeaders(), ALL_HEADERS, "Cột của bảng");
            Assert.assertTrue(list.isNothingFoundShown(), "Bảng phải hiện Nothing found, thực tế " + list.rowCount() + " dòng");
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "All products hiện cả sản phẩm số")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra sản phẩm số test vừa tạo ở Add New Digital Product được tìm thấy ở All products (giả định đã chốt).")
    public void testAllProductsShowsDigital() {
        tc("AEC_PROD_TC_014", "regression", "product", "auth");
        String name = "AutoDig_" + newDigitalToken("TC014");
        loginAsAdmin();
        createDigital(name);
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Act: Tìm tên sản phẩm số ở All products", () -> {
            list.open();
            list.search(name);
        });

        Allure.step("Assert: Có 1 dòng mang tên sản phẩm số test", () ->
                Assert.assertEquals(list.names(), List.of(name), "Kết quả tìm"));
    }

    @DataProvider(name = "searchKeywords", parallel = true)
    public Object[][] searchKeywords() {
        return new Object[][]{{"a", "full"}, {"b", "part"}, {"c", "lower"}, {"d", "none"}};
    }

    @Test(dataProvider = "searchKeywords", groups = {"regression", "product", "auth"}, description = "Tìm sản phẩm theo tên")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tìm bằng tên đầy đủ, một phần tên, tên viết thường đều ra dòng sản phẩm test và ô tìm giữ từ khoá; từ khoá không tồn tại ra Nothing found.")
    public void testSearchProduct(String variant, String kind) {
        tc("AEC_PROD_TC_015", "regression", "product", "auth");
        String token = newToken("TC015" + variant);
        String name = "AutoProd_" + token;
        String keyword = switch (kind) {
            case "full" -> name;
            case "part" -> token;
            case "lower" -> name.toLowerCase();
            default -> "zzqKhongCo" + System.currentTimeMillis() / 1000;
        };
        Allure.parameter("Biến thể", variant + " — " + keyword);
        loginAsAdmin();
        if (!"none".equals(kind)) {
            createProduct(name);
        }
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Act: Tìm " + keyword, () -> {
            list.open();
            list.search(keyword);
        });

        Allure.step("Assert: Kết quả tìm", () -> {
            if ("none".equals(kind)) {
                Assert.assertTrue(list.isNothingFoundShown(), "Phải hiện Nothing found");
            } else {
                Assert.assertTrue(list.rowIndexOf(name) >= 0, "Kết quả phải có " + name + ", thực tế " + list.names());
                Assert.assertEquals(list.searchValue(), keyword, "Ô tìm giữ từ khoá");
            }
        });
    }

    @Test(groups = {"regression", "product", "auth"}, description = "Lọc theo người bán không có sản phẩm")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra chọn người bán Cam Tu ở ô All Sellers thì bảng hiện Nothing found và ô lọc giữ tên đã chọn.")
    public void testFilterSellerWithoutProducts() {
        tc("AEC_PROD_TC_016", "regression", "product", "auth");
        ProductListPage list = openProductsAsAdmin();

        Allure.step("Act: Chọn người bán Cam Tu", () -> list.filterBySeller("Cam Tu"));

        Allure.step("Assert: Nothing found, ô lọc giữ Cam Tu", () -> {
            Assert.assertTrue(list.isNothingFoundShown(), "Phải hiện Nothing found");
            Assert.assertEquals(list.sellerFilterText(), "Cam Tu", "Ô lọc người bán");
        });
    }

    @DataProvider(name = "sortOptions", parallel = true)
    public Object[][] sortOptions() {
        return new Object[][]{{"a", "Base Price (Low > High)"}, {"b", "Base Price (High > Low)"}, {"c", "Num of Sale (High > Low)"}};
    }

    @Test(dataProvider = "sortOptions", groups = {"regression", "product", "auth", "knownIssue"}, description = "Sắp xếp phải đổi thứ tự bảng")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra chọn kiểu ở ô Sort by thì các dòng xếp đúng theo Base Price / Num of Sale ở cột Info. Đang lệch: thứ tự không đổi với mọi lựa chọn (AMB-PROD-01).")
    public void testSortChangesOrder(String variant, String option) {
        tc("AEC_PROD_TC_017", "regression", "product", "auth", "knownIssue");
        Allure.parameter("Biến thể", variant + " — " + option);
        ProductListPage list = openProductsAsAdmin();

        Allure.step("Act: Chọn " + option + " ở Sort by", () -> list.sortBy(option));

        Allure.step("Assert: Các dòng xếp đúng tiêu chí", () -> {
            if (option.startsWith("Base Price")) {
                List<Double> prices = list.basePrices();
                List<Double> expected = new ArrayList<>(prices);
                expected.sort(option.contains("Low > High") ? Comparator.naturalOrder() : Comparator.reverseOrder());
                Assert.assertEquals(prices, expected, "Base Price theo thứ tự dòng");
            } else {
                List<Integer> sales = list.numOfSales();
                List<Integer> expected = new ArrayList<>(sales);
                expected.sort(Comparator.reverseOrder());
                Assert.assertEquals(sales, expected, "Num of Sale theo thứ tự dòng");
            }
        });
    }

    @DataProvider(name = "rowButtons", parallel = true)
    public Object[][] rowButtons() {
        return new Object[][]{{"a", 0, "View"}, {"b", 1, "Edit"}, {"c", 2, "Duplicate"}, {"d", 3, "Delete"}};
    }

    @Test(dataProvider = "rowButtons", groups = {"regression", "product", "auth"}, description = "Bốn nút ở cột Options")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra chú thích 4 nút View, Edit, Duplicate, Delete; View mở trang sản phẩm /product/<slug> ở tab mới; Edit mở Edit Product; Duplicate không bấm; Delete mở hộp xác nhận rồi Cancel.")
    public void testOptionsButtons(String variant, int button, String title) {
        tc("AEC_PROD_TC_018", "regression", "product", "auth");
        Allure.parameter("Biến thể", variant + " — " + title);
        String token = newToken("TC018" + variant);
        String name = "AutoProd_" + token;
        loginAsAdmin();
        createProduct(name);
        ProductListPage list = new ProductListPage(driver());
        int row = findRow(list, token, name);

        Allure.step("Assert: Rê chuột lên nút hiện " + title, () ->
                Assert.assertEquals(list.hoverRowButton(row, button), title, "Chú thích nút"));

        switch (variant) {
            case "a" -> {
                String original = driver().getWindowHandle();
                Allure.step("Act: Bấm nút View", () -> {
                    list.clickRowButtonAt(row, button);
                    list.switchToNewWindow(original);
                });
                Allure.step("Assert: Mở trang sản phẩm ngoài cửa hàng", () ->
                        Assert.assertTrue(list.currentPath().startsWith("/product/"), "Đường dẫn: " + list.currentUrl()));
                driver().close();
                driver().switchTo().window(original);
            }
            case "b" -> Allure.step("Act + Assert: Bấm Edit mở Edit Product", () ->
                    Assert.assertTrue(list.editRow(row).isTitleDisplayed(), "Tiêu đề Edit Product"));
            case "d" -> {
                Allure.step("Act: Bấm Delete", () -> list.openDeleteOfRow(row));
                Allure.step("Assert: Hộp xác nhận mở", () ->
                        Assert.assertEquals(list.deleteModalTitle(), "Delete Confirmation", "Tiêu đề hộp"));
                Allure.step("Act: Bấm Cancel", () -> list.closeDeleteModal(ProductListPage.CloseBy.CANCEL));
            }
            default -> Allure.step("Assert: Nút Duplicate không bấm (PC-4)", () ->
                    Assert.assertEquals(list.rowButtonTitles(row).get(button), "Duplicate", "Nút thứ 3"));
        }
    }

    @DataProvider(name = "protectedProductUrls", parallel = true)
    public Object[][] protectedProductUrls() {
        return new Object[][]{{"a", "/admin/products/all"}, {"b", "/admin/products/create"}};
    }

    @Test(dataProvider = "protectedProductUrls", groups = {"regression", "product", "security"},
            description = "Chưa đăng nhập không vào được trang Product")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra chưa đăng nhập mở danh sách sản phẩm và form tạo đều bị đưa về /login, không thấy danh sách hay form.")
    public void testProductPagesRequireLogin(String variant, String path) {
        tc("AEC_PROD_TC_068", "regression", "product", "security");
        Allure.parameter("Biến thể", variant + " — " + path);
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Act: Mở " + path + " khi chưa đăng nhập", () -> driver().get(config.getBaseUrl() + path));

        Allure.step("Assert: Về trang đăng nhập", () -> {
            loginPage.waitForPath("/login");
            Assert.assertEquals(loginPage.currentPath(), "/login", "Phải chuyển về /login");
            Assert.assertTrue(loginPage.isHeadingDisplayed(), "Phải thấy form đăng nhập");
        });
    }

    @DataProvider(name = "injectionKeywords", parallel = true)
    public Object[][] injectionKeywords() {
        return new Object[][]{{"a", "<script>alert(1)</script>zzq"}, {"b", "' OR '1'='1 zzq"}};
    }

    @Test(dataProvider = "injectionKeywords", groups = {"regression", "product", "auth", "security"},
            description = "Mã script, chuỗi SQL ở ô tìm xử lý như chữ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tìm bằng chuỗi script và chuỗi SQL thì không bật hộp thoại, không ra trang lỗi, bảng hiện Nothing found, ô tìm giữ nguyên chuỗi.")
    public void testInjectionSearchTreatedAsText(String variant, String payload) {
        tc("AEC_PROD_TC_070", "regression", "product", "auth", "security");
        String keyword = payload + System.currentTimeMillis() / 1000;
        Allure.parameter("Biến thể", variant + " — " + keyword);
        ProductListPage list = openProductsAsAdmin();

        Allure.step("Act: Tìm " + keyword, () -> list.search(keyword));

        Allure.step("Assert: Không hộp thoại, không trang lỗi, Nothing found, ô tìm giữ chuỗi", () -> {
            Assert.assertFalse(list.isAlertOpen(), "Không được bật hộp thoại");
            Assert.assertFalse(list.isServerErrorPage(), "Không được ra trang lỗi");
            Assert.assertTrue(list.isNothingFoundShown(), "Phải hiện Nothing found");
            Assert.assertEquals(list.searchValue(), keyword, "Ô tìm giữ nguyên chuỗi");
        });
    }

    @Test(groups = {"regression", "product", "auth", "accessibility"}, description = "Nhấn Enter ở ô tìm chạy tìm kiếm")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể a: gõ từ khoá vào ô Type & Enter rồi nhấn Enter thì trang tải lại với ?search=<từ khoá>.")
    public void testKeyboardSearch() {
        tc("AEC_PROD_TC_072", "regression", "product", "auth", "accessibility");
        Allure.parameter("Biến thể", "a — Enter ở ô tìm");
        String keyword = "zzqKb" + System.currentTimeMillis() / 1000;
        ProductListPage list = openProductsAsAdmin();

        Allure.step("Act: Gõ từ khoá, nhấn Enter", () -> list.search(keyword));

        Allure.step("Assert: Tìm kiếm đã chạy", () ->
                Assert.assertTrue(list.currentUrl().contains("search=" + keyword), "Địa chỉ phải có từ khoá: " + list.currentUrl()));
    }

    @DataProvider(name = "windowSizes", parallel = true)
    public Object[][] windowSizes() {
        return new Object[][]{{"a", 1920, 1080}, {"b", 1366, 768}};
    }

    @Test(dataProvider = "windowSizes", groups = {"regression", "product", "auth", "responsive"},
            description = "Màn Product dùng được ở màn rộng và hẹp")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ở 1920×1080 All products hiện đủ cột, không cuộn ngang; ở 1366×768 đọc được tên, nút Options có chú thích; form tạo mở đủ các khối. Phần chữ đè nhau kiểm tay (Partial).")
    public void testProductScreensResponsive(String variant, int width, int height) {
        tc("AEC_PROD_TC_073", "regression", "product", "auth", "responsive");
        Allure.parameter("Biến thể", variant + " — " + width + "×" + height);
        loginAsAdmin();
        ProductListPage list = new ProductListPage(driver());

        Allure.step("Act: Đặt cửa sổ " + width + "×" + height + " rồi mở All products", () -> {
            driver().manage().window().setSize(new Dimension(width, height));
            list.open();
        });

        Allure.step("Assert: Danh sách dùng được", () -> {
            Assert.assertTrue(list.names().stream().noneMatch(String::isBlank), "Tên sản phẩm phải đọc được");
            Assert.assertEquals(list.rowButtonTitles(0), List.of("View", "Edit", "Duplicate", "Delete"), "Nút Options dòng 1");
            if (width >= 1920) {
                Assert.assertTrue(list.tableHeaders().containsAll(List.of("Name", "Info", "Options")), "Đủ cột chính: " + list.tableHeaders());
                Assert.assertTrue(list.hasNoHorizontalScroll(), "Không được có thanh cuộn ngang toàn trang");
            }
        });

        Allure.step("Assert: Form tạo mở đủ các khối", () -> {
            ProductCreatePage form = new ProductCreatePage(driver()).open();
            Assert.assertTrue(form.sectionTitles().size() >= 17, "Số khối của form: " + form.sectionTitles());
        });
    }
}
