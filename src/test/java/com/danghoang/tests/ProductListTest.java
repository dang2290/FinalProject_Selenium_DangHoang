package com.danghoang.tests;

import com.danghoang.base.StepNames;
import com.danghoang.dataproviders.DataProviderFactory;
import com.danghoang.pages.DashboardPage;
import com.danghoang.pages.ProductListPage.RowButton;
import com.danghoang.utils.EnvReferenceData;
import com.danghoang.pages.DigitalProductListPage;
import com.danghoang.pages.LoginPage;
import com.danghoang.pages.ProductCreatePage;
import com.danghoang.pages.ProductListPage;
import com.danghoang.pages.ProductListPage.Listing;
import com.danghoang.pages.StorefrontProductPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
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

    private static final String SELLER_WITHOUT_PRODUCTS = EnvReferenceData.get("sellerWithoutProducts");

    private static final List<String> ALL_HEADERS =
            List.of("", "Name", "Added By", "Info", "Total Stock", "Todays Deal", "Published", "Featured", "Options");

    @Test(groups = {"smoke", "product", "auth", "ui"}, description = "Màn All products đủ thành phần, nhãn nguyên văn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Products → All products mở /admin/products/all với tiêu đề All products, thẻ All Product, nút Add New Product; thanh điều khiển Bulk Action, All Sellers, Sort by, ô tìm Type & Enter; 9 cột đúng thứ tự; cột Info có Num of Sale, Base Price, Rating; mỗi dòng 4 nút tròn và 3 công tắc.")
    public void testAllProductsElements() {
        tc("AEC_PROD_TC_001", "smoke", "product", "auth", "ui");
        DashboardPage dashboard = loginAsAdmin();

        ProductListPage list = act("Bấm Products → All products ở sidebar", dashboard::goToAllProducts);

        verify("Bảng kiểm All products", () -> {
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
        ProductListPage list = new ProductListPage();

        act("Bấm mục " + path + " ở sidebar", () -> list.clickSidebar(path));

        verify("Đúng màn hình", () -> {
            Assert.assertEquals(list.currentPath(), path, "Đường dẫn");
            switch (variant) {
                case "a" -> Assert.assertTrue(new ProductCreatePage().isTitleDisplayed(), "Tiêu đề Add New Product");
                case "b" -> Assert.assertTrue(list.waitUntilLoaded().allHeaders().contains("Added By"), "Phải có cột Added By");
                case "c" -> Assert.assertFalse(list.waitUntilLoaded().allHeaders().contains("Added By"), "Không được có cột Added By");
                case "d" -> Assert.assertTrue(list.waitUntilLoaded().isTitleDisplayed(), "Tiêu đề All products");
                default -> Assert.assertTrue(new DigitalProductListPage().waitUntilLoaded().isTitleDisplayed(), "Tiêu đề Digital Products");
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

        verify("Trang 1 có 15 dòng và thanh phân trang", () -> {
            Assert.assertEquals(firstPage.size(), 15, "Số dòng trang 1");
            Assert.assertTrue(list.hasPagination(), "Phải có thanh phân trang");
        });

        act(StepNames.CLICK_PAGE_2, () -> list.clickPage("2"));
        verify("Trang 2 có 15 dòng khác trang 1", () -> {
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
        ProductListPage list = new ProductListPage();

        act("Mở Seller Products", () -> list.open(Listing.SELLER));

        verify("Đủ cột, bảng trống hiện Nothing found", () -> {
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
        ProductListPage list = new ProductListPage();

        act("Tìm tên sản phẩm số ở All products", () -> list.searchFor(name));

        verify("Có 1 dòng mang tên sản phẩm số test", () ->
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
        ProductListPage list = new ProductListPage();

        act("Tìm " + keyword, () -> list.searchFor(keyword));

        verify("Kết quả tìm", () -> {
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

        act("Chọn người bán " + SELLER_WITHOUT_PRODUCTS, () -> list.filterBySeller(SELLER_WITHOUT_PRODUCTS));

        verify("Nothing found, ô lọc giữ " + SELLER_WITHOUT_PRODUCTS, () -> {
            Assert.assertTrue(list.isNothingFoundShown(), "Phải hiện Nothing found");
            Assert.assertEquals(list.sellerFilterText(), SELLER_WITHOUT_PRODUCTS, "Ô lọc người bán");
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

        act("Chọn " + option + " ở Sort by", () -> list.sortBy(option));

        verify("Các dòng xếp đúng tiêu chí", () -> {
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
        return new Object[][]{{"a", RowButton.VIEW, "View"}, {"b", RowButton.EDIT, "Edit"}, {"c", RowButton.DUPLICATE, "Duplicate"}, {"d", RowButton.DELETE, "Delete"}};
    }

    @Test(dataProvider = "rowButtons", groups = {"regression", "product", "auth"}, description = "Bốn nút ở cột Options")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra chú thích 4 nút View, Edit, Duplicate, Delete; View mở trang sản phẩm /product/<slug> ở tab mới; Edit mở Edit Product; Duplicate không bấm; Delete mở hộp xác nhận rồi Cancel.")
    public void testOptionsButtons(String variant, RowButton button, String title) {
        tc("AEC_PROD_TC_018", "regression", "product", "auth");
        Allure.parameter("Biến thể", variant + " — " + title);
        String token = newToken("TC018" + variant);
        String name = "AutoProd_" + token;
        loginAsAdmin();
        ProductListPage list = createProduct(name);
        int row = list.findRow(token, name);

        verify("Rê chuột lên nút hiện " + title, () ->
                Assert.assertEquals(list.hoverRowButton(row, button), title, "Chú thích nút"));

        switch (variant) {
            case "a" -> {
                String original = browser().currentHandle();
                StorefrontProductPage storefront = act("Bấm View", () -> list.viewRow(row, original));
                verify("Mở trang sản phẩm ngoài cửa hàng", () ->
                        Assert.assertTrue(storefront.currentPath().startsWith("/product/"), "Đường dẫn: " + storefront.currentUrl()));
                browser().closeTabAndSwitchTo(original);
            }
            case "b" -> actAndVerify("Bấm Edit mở Edit Product", () ->
                    Assert.assertTrue(list.editRow(row).isTitleDisplayed(), "Tiêu đề Edit Product"));
            case "d" -> {
                act(StepNames.CLICK_DELETE, () -> list.openDeleteOfRow(row));
                verify("Hộp xác nhận mở", () ->
                        Assert.assertEquals(list.deleteModalTitle(), "Delete Confirmation", "Tiêu đề hộp"));
                act(StepNames.CLICK_CANCEL, () -> list.closeDeleteModal(ProductListPage.CloseBy.CANCEL));
            }
            default -> verify("Nút Duplicate trỏ tới trang nhân bản đúng sản phẩm (không bấm — PC-4)", () ->
                    Assert.assertTrue(list.duplicateUrlOfRow(row).endsWith("/admin/products/duplicate/" + list.idOfRow(row) + "?type=All"),
                            "Đường dẫn nút Duplicate: " + list.duplicateUrlOfRow(row)));
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
        LoginPage loginPage = new LoginPage();

        act("Mở " + path + " khi chưa đăng nhập", () -> browser().openPath(path));

        verify(StepNames.BACK_AT_LOGIN, () -> {
            loginPage.waitForPath("/login");
            Assert.assertEquals(loginPage.currentPath(), "/login", "Phải chuyển về /login");
            Assert.assertTrue(loginPage.isHeadingDisplayed(), "Phải thấy form đăng nhập");
        });
    }

    @Test(dataProvider = "injectionKeywords", dataProviderClass = DataProviderFactory.class, groups = {"regression", "product", "auth", "security"},
            description = "Mã script, chuỗi SQL ở ô tìm xử lý như chữ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tìm bằng chuỗi script và chuỗi SQL thì không bật hộp thoại, không ra trang lỗi, bảng hiện Nothing found, ô tìm giữ nguyên chuỗi.")
    public void testInjectionSearchTreatedAsText(String variant, String payload) {
        tc("AEC_PROD_TC_070", "regression", "product", "auth", "security");
        String keyword = payload + System.currentTimeMillis() / 1000;
        Allure.parameter("Biến thể", variant + " — " + keyword);
        ProductListPage list = openProductsAsAdmin();

        act("Tìm " + keyword, () -> list.search(keyword));

        verify("Không hộp thoại, không trang lỗi, Nothing found, ô tìm giữ chuỗi", () -> {
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

        act("Gõ từ khoá, nhấn Enter", () -> list.search(keyword));

        verify("Tìm kiếm đã chạy", () ->
                Assert.assertTrue(list.currentUrl().contains("search=" + keyword), "Địa chỉ phải có từ khoá: " + list.currentUrl()));
    }

    @Test(dataProvider = "windowSizes", dataProviderClass = DataProviderFactory.class, groups = {"regression", "product", "auth", "responsive"},
            description = "Màn Product dùng được ở màn rộng và hẹp")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ở 1920×1080 All products hiện đủ cột, không cuộn ngang; ở 1366×768 đọc được tên, nút Options có chú thích; form tạo mở đủ các khối. Phần chữ đè nhau kiểm tay (Partial).")
    public void testProductScreensResponsive(String variant, int width, int height) {
        tc("AEC_PROD_TC_073", "regression", "product", "auth", "responsive");
        Allure.parameter("Biến thể", variant + " — " + width + "×" + height);
        loginAsAdmin();
        ProductListPage list = new ProductListPage();

        act("Đặt cửa sổ " + width + "×" + height + " rồi mở All products", () -> {
            browser().resize(width, height);
            list.open();
        });

        verify("Danh sách dùng được", () -> {
            Assert.assertTrue(list.names().stream().noneMatch(String::isBlank), "Tên sản phẩm phải đọc được");
            Assert.assertEquals(list.rowButtonTitles(0), List.of("View", "Edit", "Duplicate", "Delete"), "Nút Options dòng 1");
            if (width >= 1920) {
                Assert.assertTrue(list.tableHeaders().containsAll(List.of("Name", "Info", "Options")), "Đủ cột chính: " + list.tableHeaders());
                Assert.assertTrue(list.hasNoHorizontalScroll(), "Không được có thanh cuộn ngang toàn trang");
            }
        });

        verify("Form tạo mở đủ các khối", () -> {
            ProductCreatePage form = new ProductCreatePage().open();
            Assert.assertEquals(form.sectionTitles(), PRODUCT_FORM_SECTIONS, "Các khối của form ở " + width + "×" + height);
        });
    }
}
