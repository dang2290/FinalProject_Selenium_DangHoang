package com.aec.tests;

import com.aec.pages.ColorEditPage;
import com.aec.pages.ColorListPage;
import com.aec.pages.LoginPage;
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

import java.util.List;
import java.util.stream.IntStream;

/** Màn All Colors: giao diện, lối vào, tìm kiếm, phân trang, công tắc lọc màu, phân quyền, bố cục. TC ở docs/testcases/colors/web/test_cases_colors_web.md. */
@Feature("Colors")
@Story("Danh sách & tìm kiếm màu")
public class ColorListTest extends ColorTestBase {

    @Test(groups = {"smoke", "colors", "auth", "ui"}, description = "Màn All Colors đủ thành phần, nhãn nguyên văn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra vào Products → Colors thì mở /admin/colors với tiêu đề All Colors, sidebar tô sáng Colors; ô tìm có gợi ý đúng; bảng 3 cột #, Name, Options, cột Name chỉ có chữ; mỗi dòng có nút Edit, Delete; thẻ Add New Color có 2 ô rỗng và nút Save; thẻ Color filter activation có công tắc.")
    public void testAllColorsElements() {
        tc("AEC_COLOR_TC_001", "smoke", "colors", "auth", "ui");
        loginAsAdmin();
        ColorListPage list = new ColorListPage(driver());

        Allure.step("Act: Bấm Products → Colors ở sidebar", list::openFromSidebar);

        Allure.step("Assert: Bảng kiểm màn All Colors", () -> {
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/colors", "Đường dẫn");
            Assert.assertTrue(list.isTitleDisplayed(), "Tiêu đề All Colors");
            Assert.assertTrue(list.isColorsMenuActive(), "Mục Colors ở sidebar phải được tô sáng");
            Assert.assertEquals(list.cardHeaders(), List.of("Colors", "Add New Color", "Color filter activation"), "Các thẻ");
            Assert.assertEquals(list.searchPlaceholder(), "Type color name & Enter", "Gợi ý ô tìm");
            Assert.assertEquals(list.tableHeaders(), List.of("#", "Name", "Options"), "Cột của bảng");
            Assert.assertTrue(list.nameColumnHasOnlyText(), "Cột Name chỉ có chữ, không có ô mẫu màu");
            Assert.assertTrue(list.everyRowHasEditAndDelete(), "Mỗi dòng phải có nút Edit và Delete");
            Assert.assertEquals(list.rowButtonTitles(0), List.of("Edit", "Delete"), "Chú thích 2 nút");
            Assert.assertEquals(list.formLabels(), List.of("Name", "Color Code"), "Nhãn form Add New Color");
            Assert.assertEquals(list.namePlaceholder(), "Name", "Gợi ý ô Name");
            Assert.assertEquals(list.codePlaceholder(), "Color Code", "Gợi ý ô Color Code");
            Assert.assertEquals(list.nameValue(), "", "Ô Name phải rỗng");
            Assert.assertEquals(list.codeValue(), "", "Ô Color Code phải rỗng");
            Assert.assertEquals(list.saveButtonText(), "Save", "Nút Save");
            Assert.assertTrue(list.hasFilterSwitch(), "Thẻ Color filter activation phải có công tắc");
        });
    }

    @DataProvider(name = "entryPoints", parallel = true)
    public Object[][] entryPoints() {
        return new Object[][]{{"a", "sidebar"}, {"b", "url"}, {"c", "edit"}};
    }

    @Test(dataProvider = "entryPoints", groups = {"smoke", "colors", "auth"}, description = "Mở danh sách màu bằng mọi lối vào")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra từ Dashboard bấm Products → Colors, gõ thẳng địa chỉ /admin/colors, và từ trang sửa màu test bấm Colors ở sidebar đều về /admin/colors với tiêu đề All Colors, sidebar tô sáng Colors.")
    public void testOpenColorsFromEveryEntry(String variant, String entry) {
        tc("AEC_COLOR_TC_003", "smoke", "colors", "auth");
        Allure.parameter("Biến thể", variant + " — " + entry);
        loginAsAdmin();
        ColorListPage list = new ColorListPage(driver());
        if ("edit".equals(entry)) {
            String name = "Col_" + newToken("TC003c");
            createColor(list, name, newCode());
            openEditOf(list, name);
        }

        Allure.step("Act: Mở danh sách màu theo lối vào " + entry, () -> {
            switch (entry) {
                case "sidebar" -> list.openFromSidebar();
                case "url" -> list.open();
                default -> new ColorEditPage(driver()).clickColorsInSidebar();
            }
        });

        Allure.step("Assert: Ở All Colors, sidebar tô sáng Colors", () -> {
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/colors", "Đường dẫn");
            Assert.assertTrue(list.isTitleDisplayed(), "Tiêu đề All Colors");
            Assert.assertTrue(list.isColorsMenuActive(), "Mục Colors ở sidebar phải được tô sáng");
        });
    }

    @Test(groups = {"smoke", "colors", "auth"}, description = "Tìm màu theo tên đầy đủ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra gõ tên đầy đủ của màu test vào ô tìm rồi Enter thì địa chỉ có ?search=<tên>, bảng có đúng 1 dòng là màu test, ô tìm giữ từ khoá.")
    public void testSearchByFullName() {
        tc("AEC_COLOR_TC_005", "smoke", "colors", "auth");
        String name = "Col_" + newToken("TC005");
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());

        Allure.step("Act: Tìm " + name, () -> {
            list.open();
            list.search(name);
        });

        Allure.step("Assert: Đúng 1 dòng là màu test, ô tìm giữ từ khoá", () -> {
            Assert.assertTrue(list.currentUrl().contains("search=" + name), "Địa chỉ phải có từ khoá: " + list.currentUrl());
            Assert.assertEquals(list.names(), List.of(name), "Kết quả tìm");
            Assert.assertEquals(list.searchValue(), name, "Ô tìm giữ từ khoá");
        });
    }

    @Test(groups = {"regression", "colors", "auth"}, description = "Phân trang 10 dòng, số thứ tự liên tục qua các trang")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra trang 1 có 10 dòng # 1–10 và thanh phân trang; trang 2 (?page=2) có # 11–20; trang cuối có số # liên tục, dòng cuối bằng (số trang − 1) × 10 + số dòng của trang cuối. Không so tổng cố định 299 — dữ liệu dùng chung thay đổi.")
    public void testPaginationContinuousNumbers() {
        tc("AEC_COLOR_TC_008", "regression", "colors", "auth");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Assert: Trang 1 có 10 dòng, # từ 1 đến 10, có phân trang", () -> {
            Assert.assertEquals(list.rowNumbers(), IntStream.rangeClosed(1, 10).boxed().toList(), "Cột # trang 1");
            Assert.assertTrue(list.hasPagination(), "Phải có thanh phân trang");
        });

        Allure.step("Act: Bấm trang 2", () -> list.clickPage("2"));
        Allure.step("Assert: Trang 2 có # từ 11 đến 20", () -> {
            Assert.assertTrue(list.currentUrl().contains("page=2"), "Địa chỉ phải có page=2: " + list.currentUrl());
            Assert.assertEquals(list.rowNumbers(), IntStream.rangeClosed(11, 20).boxed().toList(), "Cột # trang 2");
        });

        int last = list.lastPageNumber();
        Allure.step("Act: Bấm số trang cuối " + last, () -> list.clickPage(String.valueOf(last)));
        Allure.step("Assert: Số # trang cuối liên tục, không nhảy số", () -> {
            List<Integer> numbers = list.rowNumbers();
            int first = (last - 1) * 10 + 1;
            Assert.assertEquals(numbers, IntStream.range(first, first + numbers.size()).boxed().toList(), "Cột # trang cuối");
            Allure.parameter("Tổng số màu", numbers.get(numbers.size() - 1));
        });
    }

    @DataProvider(name = "rowButtons", parallel = true)
    public Object[][] rowButtons() {
        return new Object[][]{{"a", 0, "Edit"}, {"b", 1, "Delete"}};
    }

    @Test(dataProvider = "rowButtons", groups = {"regression", "colors", "auth"}, description = "Hai nút ở cột Options")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra rê chuột lên nút thứ nhất hiện Edit, bấm thì mở Color Information của màu test; nút thứ hai hiện Delete, bấm thì mở hộp Delete Confirmation mà địa chỉ không đổi (bấm Cancel để đóng).")
    public void testOptionsButtons(String variant, int button, String title) {
        tc("AEC_COLOR_TC_009", "regression", "colors", "auth");
        Allure.parameter("Biến thể", variant + " — " + title);
        String name = "Col_" + newToken("TC009" + variant);
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());
        int row = findRow(list, name, name);
        String url = list.currentUrl();

        Allure.step("Assert: Rê chuột lên nút hiện " + title, () ->
                Assert.assertEquals(list.hoverRowButton(row, button), title, "Chú thích nút"));

        if (button == 0) {
            ColorEditPage edit = Allure.step("Act: Bấm nút Edit", () -> list.editRow(row));
            Allure.step("Assert: Mở trang Color Information của màu test", () -> {
                Assert.assertTrue(edit.isTitleDisplayed(), "Tiêu đề Color Information");
                Assert.assertEquals(edit.nameValue(), name, "Ô Name");
            });
        } else {
            Allure.step("Act: Bấm nút Delete", () -> list.openDeleteOfRow(row));
            Allure.step("Assert: Hộp Delete Confirmation mở, địa chỉ không đổi", () -> {
                Assert.assertEquals(list.deleteModalTitle(), "Delete Confirmation", "Tiêu đề hộp");
                Assert.assertEquals(list.currentUrl(), url, "Thanh địa chỉ không được đổi");
            });
            Allure.step("Act: Bấm Cancel đóng hộp", () -> list.closeDeleteModal(ColorListPage.CloseBy.CANCEL));
        }
    }

    @DataProvider(name = "partialKeywords", parallel = true)
    public Object[][] partialKeywords() {
        return new Object[][]{{"a", "part"}, {"b", "lower"}};
    }

    @Test(dataProvider = "partialKeywords", groups = {"regression", "colors", "auth"}, description = "Tìm theo một phần tên, không phân biệt hoa thường")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tìm bằng một phần tên (mã truy vết, bỏ tiền tố) và bằng tên viết thường toàn bộ đều ra dòng màu test.")
    public void testSearchPartialCaseInsensitive(String variant, String kind) {
        tc("AEC_COLOR_TC_010", "regression", "colors", "auth");
        String token = newToken("TC010" + variant);
        String name = "Col_" + token;
        String keyword = "part".equals(kind) ? token : name.toLowerCase();
        Allure.parameter("Biến thể", variant + " — " + keyword);
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());

        Allure.step("Act: Tìm " + keyword, () -> {
            list.open();
            list.search(keyword);
        });

        Allure.step("Assert: Bảng có dòng màu test", () ->
                Assert.assertTrue(list.rowIndexOf(name) >= 0, "Kết quả phải có " + name + ", thực tế " + list.names()));
    }

    @Test(groups = {"regression", "colors", "auth"}, description = "Kết quả tìm nhiều trang giữ từ khoá khi sang trang")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tìm blue (dữ liệu có sẵn, chỉ đọc) thì có phân trang, mọi dòng chứa blue; bấm trang 2 thì địa chỉ có search=blue và page=2, ô tìm vẫn là blue, mọi dòng vẫn chứa blue.")
    public void testSearchKeepsKeywordAcrossPages() {
        tc("AEC_COLOR_TC_011", "regression", "colors", "auth");
        String keyword = "blue";
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tìm " + keyword, () -> list.search(keyword));
        Allure.step("Assert: Có phân trang, mọi dòng chứa " + keyword, () -> {
            Assert.assertTrue(list.hasPagination(), "Phải có thanh phân trang");
            Assert.assertEquals(list.rowIndexesContaining(keyword).size(), list.rowCount(), "Mọi dòng phải chứa từ khoá: " + list.names());
        });

        Allure.step("Act: Bấm trang 2", () -> list.clickPage("2"));
        Allure.step("Assert: Trang 2 vẫn giữ từ khoá", () -> {
            Assert.assertTrue(list.currentUrl().contains("search=" + keyword), "Địa chỉ phải có search: " + list.currentUrl());
            Assert.assertTrue(list.currentUrl().contains("page=2"), "Địa chỉ phải có page=2: " + list.currentUrl());
            Assert.assertEquals(list.searchValue(), keyword, "Ô tìm");
            Assert.assertTrue(list.rowCount() > 0, "Trang 2 phải có dòng");
            Assert.assertEquals(list.rowIndexesContaining(keyword).size(), list.rowCount(), "Mọi dòng phải chứa từ khoá: " + list.names());
        });
    }

    @Test(groups = {"regression", "colors", "auth"}, description = "Tìm không có kết quả")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tìm từ khoá không tồn tại thì bảng hiện Nothing found và không có thanh phân trang.")
    public void testSearchNoResult() {
        tc("AEC_COLOR_TC_012", "regression", "colors", "auth");
        String keyword = "zzqKhongCo" + System.currentTimeMillis() / 1000;
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tìm " + keyword, () -> list.search(keyword));

        Allure.step("Assert: Nothing found, không có phân trang", () -> {
            Assert.assertTrue(list.isNothingFoundShown(), "Phải hiện Nothing found");
            Assert.assertFalse(list.hasPagination(), "Không được có thanh phân trang");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "ui"}, description = "Công tắc Color filter activation hiện ở trạng thái đang đặt")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra thẻ Color filter activation có một công tắc và ghi lại trạng thái hiện tại (lúc khảo sát: tắt). Không bấm công tắc — cấu hình toàn hệ thống (PC-4); trạng thái khác thì chỉ ghi lại, không coi là lỗi.")
    public void testFilterActivationSwitchShown() {
        tc("AEC_COLOR_TC_039", "regression", "colors", "auth", "ui");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Assert: Thẻ Color filter activation có công tắc", () -> {
            Assert.assertTrue(list.cardHeaders().contains("Color filter activation"), "Tiêu đề thẻ");
            Assert.assertTrue(list.hasFilterSwitch(), "Phải có công tắc");
            Allure.parameter("Trạng thái công tắc", list.isFilterSwitchOn() ? "bật" : "tắt");
        });
    }

    @DataProvider(name = "protectedColorUrls", parallel = true)
    public Object[][] protectedColorUrls() {
        return new Object[][]{{"a", "/admin/colors"}, {"b", "/admin/colors/edit/1?lang=en"}};
    }

    @Test(dataProvider = "protectedColorUrls", groups = {"regression", "colors", "security"},
            description = "Chưa đăng nhập không vào được các trang Colors")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra chưa đăng nhập mở danh sách màu và trang sửa màu đều bị đưa về /login. Biến thể b dùng id 1 thay cho id màu test — chưa đăng nhập thì không tạo được màu, và trang chuyển về /login trước khi đọc id.")
    public void testColorPagesRequireLogin(String variant, String path) {
        tc("AEC_COLOR_TC_043", "regression", "colors", "security");
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

    @Test(dataProvider = "injectionKeywords", groups = {"regression", "colors", "auth", "security"},
            description = "Mã script và chuỗi SQL ở ô tìm được xử lý như chữ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tìm bằng chuỗi script và chuỗi SQL thì không bật hộp thoại, không ra trang lỗi, bảng hiện Nothing found, ô tìm giữ nguyên chuỗi.")
    public void testInjectionSearchTreatedAsText(String variant, String payload) {
        tc("AEC_COLOR_TC_045", "regression", "colors", "auth", "security");
        String keyword = payload + System.currentTimeMillis() / 1000;
        Allure.parameter("Biến thể", variant + " — " + keyword);
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tìm " + keyword, () -> list.search(keyword));

        Allure.step("Assert: Không hộp thoại, không trang lỗi, Nothing found, ô tìm giữ chuỗi", () -> {
            Assert.assertFalse(list.isAlertOpen(), "Không được bật hộp thoại");
            Assert.assertFalse(list.isServerErrorPage(), "Không được ra trang lỗi");
            Assert.assertTrue(list.isNothingFoundShown(), "Phải hiện Nothing found");
            Assert.assertEquals(list.searchValue(), keyword, "Ô tìm giữ nguyên chuỗi");
        });
    }

    @DataProvider(name = "windowSizes", parallel = true)
    public Object[][] windowSizes() {
        return new Object[][]{{"a", 1920, 1080}, {"b", 1366, 768}};
    }

    @Test(dataProvider = "windowSizes", groups = {"regression", "colors", "auth", "responsive"},
            description = "Trang All Colors dùng được ở màn rộng và màn hẹp")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ở 1920×1080: bảng và các thẻ cạnh nhau, đủ 3 cột, không cuộn ngang; ở 1366×768: đọc được tên màu, 2 nút thao tác, ô Name, Color Code, nút Save. Cả hai: bấm Delete dòng 1 mở hộp xác nhận rồi Cancel. Phần chữ đè nhau kiểm tay (Partial).")
    public void testAllColorsResponsive(String variant, int width, int height) {
        tc("AEC_COLOR_TC_049", "regression", "colors", "auth", "responsive");
        Allure.parameter("Biến thể", variant + " — " + width + "×" + height);
        loginAsAdmin();
        ColorListPage list = new ColorListPage(driver());

        Allure.step("Act: Đặt cửa sổ " + width + "×" + height + " rồi mở All Colors", () -> {
            driver().manage().window().setSize(new Dimension(width, height));
            list.open();
        });

        Allure.step("Assert: Bố cục dùng được", () -> {
            Assert.assertEquals(list.cardHeaders(), List.of("Colors", "Add New Color", "Color filter activation"), "Bảng và các thẻ");
            Assert.assertTrue(list.names().stream().noneMatch(String::isBlank), "Tên màu phải đọc được");
            Assert.assertTrue(list.everyRowHasEditAndDelete(), "Mỗi dòng phải có 2 nút thao tác");
            Assert.assertEquals(list.formLabels(), List.of("Name", "Color Code"), "Ô của form");
            Assert.assertEquals(list.saveButtonText(), "Save", "Nút Save của form");
            if (width >= 1920) {
                Assert.assertEquals(list.tableHeaders(), List.of("#", "Name", "Options"), "Đủ 3 cột");
                Assert.assertTrue(list.hasNoHorizontalScroll(), "Không được có thanh cuộn ngang toàn trang");
            }
        });

        Allure.step("Act: Bấm Delete ở dòng 1", () -> list.openDeleteOfRow(0));
        Allure.step("Assert: Hộp xác nhận mở ra", () ->
                Assert.assertEquals(list.deleteModalTitle(), "Delete Confirmation", "Tiêu đề hộp"));
        Allure.step("Act: Bấm Cancel", () -> list.closeDeleteModal(ColorListPage.CloseBy.CANCEL));
    }
}
