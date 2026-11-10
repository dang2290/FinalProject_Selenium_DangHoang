package com.aec.tests;

import com.aec.pages.AttributeDetailPage;
import com.aec.pages.AttributeEditPage;
import com.aec.pages.AttributeListPage;
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
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Màn All Attributes: giao diện, lối vào, danh sách một trang, cột Values, nút trên dòng, phân quyền, bàn phím, bố cục, hiệu năng.
 * TC ở docs/testcases/attribute/web/test_cases_attribute_web.md.
 */
@Feature("Attribute")
@Story("Danh sách thuộc tính")
public class AttributeListTest extends AttributeTestBase {

    private static final Duration MAX_LOAD = Duration.ofSeconds(3);

    @Test(groups = {"smoke", "attribute", "auth", "ui"}, description = "Màn All Attributes hiển thị đủ thành phần với nhãn nguyên văn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra vào All Attributes từ sidebar Products → Attribute thì đúng URL, tiêu đề, mục Attribute tô sáng, thẻ Attributes và Add New Attribute, 4 cột đúng thứ tự, giá trị hiện dạng nhãn, mỗi dòng 3 nút, form có Name rỗng và nút Save.")
    public void testAllAttributesPageElements() {
        tc("AEC_ATTR_TC_001", "smoke", "attribute", "auth", "ui");
        loginAsAdmin();
        AttributeListPage list = new AttributeListPage(driver());

        Allure.step("Act: Ở sidebar bấm Products → Attribute", list::openFromSidebar);

        Allure.step("Assert: Bảng kiểm màn All Attributes", () -> {
            SoftAssert soft = new SoftAssert();
            soft.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/attributes", "Thanh địa chỉ");
            soft.assertTrue(list.isTitleDisplayed(), "(1) Tiêu đề All Attributes");
            soft.assertTrue(list.isAttributeMenuActive(), "(1) Mục Attribute ở sidebar được tô sáng");
            soft.assertEquals(list.cardHeaders(), List.of("Attributes", "Add New Attribute"), "(2) Thẻ bảng và thẻ form");
            soft.assertEquals(list.tableHeaders(), List.of("#", "Name", "Values", "Options"), "(3) Hàng tiêu đề bảng");
            soft.assertTrue(list.valuesShownAsBadges(), "(4) Giá trị hiện dạng nhãn");
            soft.assertTrue(list.everyRowHasThreeButtons(), "(5) Mỗi dòng có 3 nút tròn");
            soft.assertEquals(list.formLabels(), List.of("Name"), "(6) Nhãn form");
            soft.assertEquals(list.nameValue(), "", "(6) Ô Name rỗng");
            soft.assertEquals(list.namePlaceholder(), "Name", "(6) Gợi ý ô Name");
            soft.assertEquals(list.saveButtonText(), "Save", "(6) Nút Save");
            soft.assertAll();
        });
    }

    @DataProvider(name = "entryWays", parallel = true)
    public Object[][] entryWays() {
        return new Object[][]{{"a", "sidebar"}, {"b", "url"}, {"c", "editPage"}, {"d", "detailPage"}};
    }

    @Test(dataProvider = "entryWays", groups = {"smoke", "attribute", "auth"}, description = "Mở danh sách Attribute bằng mọi lối vào")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra vào All Attributes từ sidebar, gõ URL, từ trang sửa thuộc tính và từ trang Attribute Detail bấm Attribute ở sidebar đều ra cùng màn: bảng, form Add New Attribute, mục Attribute tô sáng, URL /admin/attributes.")
    public void testOpenAttributeListFromEveryEntry(String variant, String entry) {
        tc("AEC_ATTR_TC_004", "smoke", "attribute", "auth");
        Allure.parameter("Biến thể", variant + " — " + entry);
        loginAsAdmin();
        AttributeListPage list = new AttributeListPage(driver());
        if (entry.endsWith("Page")) {
            String name = "AutoAttr_" + newToken("TC004" + variant);
            createAttribute(list, name);
            Allure.step("Arrange: Mở trang " + entry + " của thuộc tính test", () -> {
                int row = findRow(list, name);
                if ("editPage".equals(entry)) {
                    list.editRow(row);
                } else {
                    list.openValuesOfRow(row);
                }
            });
        }

        Allure.step("Act: Vào trang theo lối " + entry, () -> {
            if ("url".equals(entry)) {
                list.open();
            } else {
                list.openFromSidebar();
            }
        });

        Allure.step("Assert: Màn All Attributes, mục Attribute tô sáng, đúng URL", () -> {
            Assert.assertTrue(list.isTitleDisplayed(), "Tiêu đề All Attributes");
            Assert.assertEquals(list.cardHeaders(), List.of("Attributes", "Add New Attribute"), "Bảng và form Add New Attribute");
            Assert.assertTrue(list.isAttributeMenuActive(), "Mục Attribute ở sidebar phải được tô sáng");
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/attributes", "Thanh địa chỉ");
        });
    }

    @Test(groups = {"regression", "attribute", "auth"}, description = "Mọi thuộc tính hiển thị trên một trang, cột # đánh số liên tục")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra bảng hiện mọi thuộc tính trên một trang: cột # từ 1 tới tổng số dòng, liên tục; không có thanh phân trang và ô tìm kiếm.")
    public void testAllAttributesOnOnePage() {
        tc("AEC_ATTR_TC_009", "regression", "attribute", "auth");
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Assert: Cột # từ 1 tới tổng số dòng, không phân trang, không ô tìm", () -> {
            List<Integer> numbers = list.rowNumbers();
            Allure.parameter("Số dòng", numbers.size());
            Assert.assertEquals(numbers, IntStream.rangeClosed(1, numbers.size()).boxed().toList(), "Cột # phải liên tục từ 1");
            Assert.assertFalse(list.hasPagination(), "Không được có thanh phân trang");
            Assert.assertFalse(list.hasSearchInput(), "Không được có ô tìm kiếm");
        });
    }

    @DataProvider(name = "valueColumns", parallel = true)
    public Object[][] valueColumns() {
        return new Object[][]{{"a", false}, {"b", true}};
    }

    @Test(dataProvider = "valueColumns", groups = {"regression", "attribute", "auth"}, description = "Cột Values hiển thị nhãn giá trị hoặc để trống")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra thuộc tính chưa có giá trị thì ô Values trống; có 2 giá trị thì hiện 2 nhãn riêng đúng thứ tự đã thêm.")
    public void testValuesColumn(String variant, boolean withValues) {
        tc("AEC_ATTR_TC_010", "regression", "attribute", "auth");
        Allure.parameter("Biến thể", variant + (withValues ? " — 2 giá trị" : " — không giá trị"));
        String token = newToken("TC010" + variant);
        String name = "AutoAttr_" + token;
        List<String> values = withValues ? List.of("AutoVal_A_" + token, "AutoVal_B_" + token) : List.of();
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        if (withValues) {
            Allure.step("Arrange: Thêm 2 giá trị", () -> {
                AttributeDetailPage detail = openDetailOf(list, name);
                values.forEach(detail::addValue);
            });
        }

        Allure.step("Assert: Cột Values ở dòng thuộc tính test", () ->
                Assert.assertEquals(list.valuesOf(findRow(list, name)), values, "Nhãn ở cột Values"));
    }

    @DataProvider(name = "rowButtons", parallel = true)
    public Object[][] rowButtons() {
        return new Object[][]{{"a", 0, "Attribute values"}, {"b", 1, "Edit"}, {"c", 2, "Delete"}};
    }

    @Test(dataProvider = "rowButtons", groups = {"regression", "attribute", "auth"}, description = "Ba nút thao tác trên dòng mở đúng màn hình")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra 3 nút tròn trên dòng: Attribute values mở Attribute Detail, Edit mở Attribute Information, Delete mở hộp xác nhận mà không đổi URL (bấm Cancel để đóng).")
    public void testRowButtons(String variant, int button, String title) {
        tc("AEC_ATTR_TC_011", "regression", "attribute", "auth");
        Allure.parameter("Biến thể", variant + " — " + title);
        String name = "AutoAttr_" + newToken("TC011" + variant);
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        int row = findRow(list, name);

        Allure.step("Assert: Rê chuột lên nút thứ " + (button + 1) + " hiện " + title, () ->
                Assert.assertEquals(list.hoverRowButton(row, button), title, "Chú thích nút"));

        Allure.step("Act + Assert: Bấm nút " + title, () -> {
            String before = list.currentUrl();
            switch (button) {
                case 0 -> {
                    AttributeDetailPage detail = list.openValuesOfRow(row);
                    Assert.assertTrue(detail.currentUrl().matches(".*/admin/attributes/\\d+$"), "Thanh địa chỉ: " + detail.currentUrl());
                    Assert.assertTrue(detail.isTitleDisplayed(), "Phải mở Attribute Detail");
                }
                case 1 -> {
                    AttributeEditPage edit = list.editRow(row);
                    Assert.assertTrue(edit.currentUrl().matches(".*/admin/attributes/edit/\\d+\\?lang=en$"), "Thanh địa chỉ: " + edit.currentUrl());
                    Assert.assertTrue(edit.isTitleDisplayed(), "Phải mở Attribute Information");
                }
                default -> {
                    list.openDeleteOfRow(row);
                    Assert.assertTrue(list.isDeleteModalShown(), "Phải hiện hộp Delete Confirmation");
                    Assert.assertEquals(list.currentUrl(), before, "Thanh địa chỉ không được đổi");
                    list.closeDeleteModal(AttributeListPage.CloseBy.CANCEL);
                    Assert.assertTrue(list.waitDeleteModalClosed(), "Cancel phải đóng hộp");
                }
            }
        });
    }

    @DataProvider(name = "protectedAttributeUrls", parallel = true)
    public Object[][] protectedAttributeUrls() {
        return new Object[][]{{"a", "/admin/attributes"}, {"b", "/admin/attributes/edit/1?lang=en"}, {"c", "/admin/attributes/1"}};
    }

    @Test(dataProvider = "protectedAttributeUrls", groups = {"regression", "attribute", "security"},
            description = "Chưa đăng nhập không vào được các trang Attribute")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra chưa đăng nhập mở danh sách, trang sửa, trang Attribute Detail đều bị đưa về /login. Biến thể b, c dùng id 1 thay cho id thuộc tính test — chưa đăng nhập thì không tạo được thuộc tính, và trang chuyển về /login trước khi đọc id.")
    public void testAttributePagesRequireLogin(String variant, String path) {
        tc("AEC_ATTR_TC_035", "regression", "attribute", "security");
        Allure.parameter("Biến thể", variant + " — " + path);
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Act: Mở " + path + " khi chưa đăng nhập", () -> driver().get(config.getBaseUrl() + path));

        Allure.step("Assert: Về trang đăng nhập", () -> {
            loginPage.waitForPath("/login");
            Assert.assertEquals(loginPage.currentPath(), "/login", "Phải chuyển về /login");
            Assert.assertTrue(loginPage.isHeadingDisplayed(), "Phải thấy form đăng nhập");
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "accessibility"}, description = "Tạo thuộc tính bằng phím Enter")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể a: bấm vào ô Name, gõ tên rồi nhấn Enter thì thuộc tính được tạo và nằm ở những dòng đầu bảng.")
    public void testKeyboardCreate() {
        tc("AEC_ATTR_TC_040", "regression", "attribute", "auth", "accessibility");
        Allure.parameter("Biến thể", "a — Enter");
        String name = "AutoAttrKb_" + newToken("TC040a");
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Gõ Name rồi nhấn Enter", () -> list.createByEnter(name));

        Allure.step("Assert: Tạo thành công, thuộc tính mới ở đầu bảng", () -> {
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi nhấn Enter");
            int row = list.rowIndexOf(name);
            Assert.assertTrue(row >= 0 && row < TOP_ROWS, "Thuộc tính mới phải ở đầu bảng, thực tế dòng " + (row + 1));
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "accessibility"}, description = "Chuyển focus từ ô Name sang nút Save bằng Tab")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể b: bấm vào ô Name rồi nhấn Tab thì focus sang nút Save; ô Name và nút Save khi được focus có viền nhìn thấy được.")
    public void testKeyboardTabToSave() {
        tc("AEC_ATTR_TC_040", "regression", "attribute", "auth", "accessibility");
        Allure.parameter("Biến thể", "b — Tab");
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Assert: Tab từ ô Name tới Save", () ->
                Assert.assertEquals(list.tabSequenceFromName(1), List.of("Save"), "Phần tử nhận focus sau Tab"));
        Allure.step("Assert: Ô Name và nút Save có viền khi được focus", () -> {
            Assert.assertEquals(list.fieldsWithoutVisibleFocus(), List.of(), "Ô không đổi gì khi được focus");
            Assert.assertTrue(list.saveShowsFocusWhenTabbed(), "Nút Save phải đổi kiểu khi được focus bằng Tab");
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "accessibility"}, description = "Đóng hộp xác nhận xoá thuộc tính bằng phím Esc")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể c: mở hộp Delete Confirmation của thuộc tính test rồi nhấn Esc thì hộp đóng, thuộc tính còn nguyên.")
    public void testEscClosesDeleteConfirmation() {
        tc("AEC_ATTR_TC_040", "regression", "attribute", "auth", "accessibility");
        Allure.parameter("Biến thể", "c — Esc");
        String name = "AutoAttr_" + newToken("TC040c");
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);

        Allure.step("Act: Mở hộp xoá của thuộc tính test, nhấn Esc", () -> {
            list.openDeleteOfRow(findRow(list, name));
            list.closeDeleteModal(AttributeListPage.CloseBy.ESC);
        });

        Allure.step("Assert: Hộp đóng, thuộc tính còn nguyên", () -> {
            Assert.assertTrue(list.waitDeleteModalClosed(), "Hộp Delete Confirmation phải đóng");
            Assert.assertTrue(findRow(list, name) >= 0, "Thuộc tính test phải còn");
        });
    }

    @DataProvider(name = "windowSizes", parallel = true)
    public Object[][] windowSizes() {
        return new Object[][]{{"a", 1920, 1080}, {"b", 1366, 768}};
    }

    @Test(dataProvider = "windowSizes", groups = {"regression", "attribute", "auth", "responsive"},
            description = "Trang All Attributes dùng được ở màn rộng và màn hẹp")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ở 1920×1080: bảng và form cạnh nhau, đủ 4 cột, không cuộn ngang; ở 1366×768: đọc được tên, 3 nút thao tác và form Name + Save. Phần chữ đè nhau kiểm tay (Partial).")
    public void testAllAttributesResponsive(String variant, int width, int height) {
        tc("AEC_ATTR_TC_041", "regression", "attribute", "auth", "responsive");
        Allure.parameter("Biến thể", variant + " — " + width + "×" + height);
        loginAsAdmin();
        AttributeListPage list = new AttributeListPage(driver());

        Allure.step("Act: Đặt cửa sổ " + width + "×" + height + " rồi mở All Attributes", () -> {
            driver().manage().window().setSize(new Dimension(width, height));
            list.open();
        });

        Allure.step("Assert: Bố cục dùng được", () -> {
            Assert.assertEquals(list.cardHeaders(), List.of("Attributes", "Add New Attribute"), "Bảng và form");
            Assert.assertTrue(list.names().stream().limit(15).noneMatch(String::isBlank), "Tên thuộc tính phải đọc được");
            Assert.assertTrue(list.everyRowHasThreeButtons(), "Mỗi dòng phải có 3 nút thao tác");
            Assert.assertEquals(list.saveButtonText(), "Save", "Nút Save của form");
            if (width >= 1920) {
                Assert.assertEquals(list.tableHeaders(), List.of("#", "Name", "Values", "Options"), "Đủ 4 cột");
                Assert.assertTrue(list.hasNoHorizontalScroll(), "Không được có thanh cuộn ngang toàn trang");
            }
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "performance"}, description = "Danh sách khoảng 600 thuộc tính tải trong thời gian chấp nhận được")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra mở All Attributes (~600 dòng trên một trang) thì bảng hiện đủ trong ≤ 3 giây. Đo từ trình duyệt tự động, chỉ tham khảo; phần cuộn mượt kiểm tay (Partial).")
    public void testListLoadTime() {
        tc("AEC_ATTR_TC_042", "regression", "attribute", "performance", "auth");
        loginAsAdmin();
        AttributeListPage list = new AttributeListPage(driver());

        Duration load = Allure.step("Act: Mở danh sách, bấm giờ tới khi bảng hiện đủ", () -> {
            long start = System.nanoTime();
            list.open();
            return Duration.ofNanos(System.nanoTime() - start);
        });

        Allure.step("Assert: Bảng hiện đủ trong ≤ 3 giây", () -> {
            Allure.parameter("Thời gian (ms)", load.toMillis());
            Assert.assertTrue(list.rowCount() > 0, "Bảng phải có dữ liệu");
            Assert.assertTrue(load.compareTo(MAX_LOAD) <= 0, "Mở danh sách mất " + load.toMillis() + " ms");
        });
    }
}
