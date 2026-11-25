package com.danghoang.tests;

import com.danghoang.base.StepNames;
import com.danghoang.pages.ColorEditPage;
import com.danghoang.pages.ColorListPage;
import com.danghoang.pages.ProductCreatePage;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Xoá màu, liên kết Add New Product, vòng đời trọn vẹn.
 * Không automate TC_037: cần lưu một sản phẩm thật (Save &amp; Unpublish) trên môi trường dùng chung rồi xoá tay ở All products.
 * TC ở docs/testcases/colors/web/test_cases_colors_web.md.
 */
@Feature("Colors")
@Story("Xoá & liên kết màu")
public class ColorLifecycleTest extends ColorTestBase {

    @Test(groups = {"smoke", "colors", "auth", "criticalPath"}, description = "Xoá màu qua hộp xác nhận")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra bấm thùng rác ở dòng màu test thì hộp Delete Confirmation hiện câu hỏi, nút Cancel / Delete, địa chỉ không đổi; bấm Delete thì báo Color has been deleted successfully, về /admin/colors; tìm lại ra Nothing found.")
    public void testDeleteColor() {
        tc("AEC_COLOR_TC_007", "smoke", "colors", "auth", "criticalPath");
        String name = "Col_" + newToken("TC007");
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());
        int row = list.findRow(name);
        String url = list.currentUrl();

        act("Bấm nút thùng rác ở dòng màu test", () -> list.openDeleteOfRow(row));
        verify("Hộp xác nhận đúng nội dung, địa chỉ không đổi", () -> {
            Assert.assertEquals(list.deleteModalTitle(), "Delete Confirmation", "Tiêu đề hộp");
            Assert.assertEquals(list.deleteModalText(), "Are you sure to delete this?", "Câu hỏi xác nhận");
            Assert.assertEquals(list.deleteModalButtons(), List.of("Cancel", "Delete"), "Nút trong hộp");
            Assert.assertEquals(list.currentUrl(), url, "Thanh địa chỉ không được đổi");
        });

        act(StepNames.CLICK_DELETE, list::confirmDelete);
        verify("Xoá thành công, về /admin/colors", () -> {
            Assert.assertEquals(list.getNotifyMessage(), DELETED, "Thông báo sau khi xoá");
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/colors", "Thanh địa chỉ");
        });

        verify("Tìm lại ra Nothing found", () -> {
            list.search(name);
            Assert.assertTrue(list.isNothingFoundShown(), "Phải hiện Nothing found, thực tế " + list.names());
        });
    }

    @Test(groups = {"regression", "colors", "auth"}, description = "Huỷ xoá thì màu còn nguyên")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra mở hộp Delete Confirmation của màu test rồi Cancel thì hộp đóng, không có thông báo, địa chỉ vẫn giữ từ khoá tìm, màu test còn nguyên.")
    public void testCancelDeleteKeepsColor() {
        tc("AEC_COLOR_TC_035", "regression", "colors", "auth");
        String name = "Col_" + newToken("TC035");
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());
        int row = list.findRow(name);

        act(StepNames.CLICK_DELETE, () -> list.openDeleteOfRow(row));
        verify("Hộp Delete Confirmation mở", () ->
                Assert.assertEquals(list.deleteModalTitle(), "Delete Confirmation", "Tiêu đề hộp"));

        act(StepNames.CLICK_CANCEL, () -> list.closeDeleteModal(ColorListPage.CloseBy.CANCEL));
        verify("Hộp đóng, địa chỉ giữ từ khoá, màu test còn", () -> {
            Assert.assertTrue(list.waitDeleteModalClosed(), "Hộp phải đóng");
            Assert.assertTrue(list.currentUrl().contains("search=" + name), "Địa chỉ phải giữ từ khoá: " + list.currentUrl());
            Assert.assertTrue(list.rowIndexOf(name) >= 0, "Màu test phải còn");
        });
    }

    @Test(groups = {"regression", "colors", "auth"}, description = "Xoá từ kết quả tìm thì về danh sách đầy đủ")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra xoá màu test từ kết quả tìm thì báo xoá thành công, địa chỉ về /admin/colors, ô tìm rỗng, bảng hiện trang 1 đầy đủ — từ khoá không được giữ (AMB-COLOR-12).")
    public void testDeleteFromSearchReturnsFullList() {
        tc("AEC_COLOR_TC_036", "regression", "colors", "auth");
        String name = "Col_" + newToken("TC036");
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());
        int row = list.findRow(name);

        act("Xoá màu test qua hộp xác nhận", () -> list.deleteRow(row));

        verify("Về danh sách đầy đủ, ô tìm rỗng", () -> {
            Assert.assertEquals(list.getNotifyMessage(), DELETED, "Thông báo sau khi xoá");
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/colors", "Thanh địa chỉ");
            Assert.assertEquals(list.searchValue(), "", "Ô tìm phải rỗng");
            Assert.assertEquals(list.rowCount(), 10, "Bảng hiện trang 1 đầy đủ 10 dòng");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "integration"}, description = "Màu mới xuất hiện ở ô Colors của Add New Product")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ở Add New Product, ô chọn màu bị khoá khi công tắc Colors tắt; bật công tắc thì mở khoá; gõ tên màu test thì danh sách có màu test kèm ô mẫu tô đúng mã. Không lưu sản phẩm. Phần màu tô nhìn bằng mắt kiểm tay (Partial).")
    public void testNewColorAvailableInProductForm() {
        tc("AEC_COLOR_TC_038", "regression", "colors", "auth", "integration");
        String name = "Col_" + newToken("TC038");
        String code = newCode();
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, code);
        ProductCreatePage product = arrange("Mở Add New Product", () -> new ProductCreatePage().open());

        verify("Ô chọn màu bị khoá, không mở được", () -> {
            Assert.assertTrue(product.variation().isColorSelectDisabled(), "Ô chọn màu phải bị khoá");
            Assert.assertFalse(product.variation().tryOpenColorSelect(), "Ô chọn màu không được mở ra");
        });

        act("Bật công tắc Colors", product.variation()::enableColors);
        verify("Ô chọn màu mở khoá", () ->
                Assert.assertFalse(product.variation().isColorSelectDisabled(), "Ô chọn màu phải mở khoá"));

        List<String> found = act("Mở ô chọn màu, gõ tên màu test", () -> product.variation().searchColors(name));
        verify("Liệt kê màu test kèm ô mẫu đúng mã", () -> {
            Assert.assertEquals(found, List.of(name), "Kết quả trong ô chọn màu");
            Assert.assertTrue(product.variation().colorSwatchStyleOf(name).toLowerCase().contains(code.toLowerCase()),
                    "Ô mẫu phải tô mã " + code + ", thực tế style=" + product.variation().colorSwatchStyleOf(name));
        });
    }

    @Test(groups = {"regression", "colors", "auth", "e2e"}, description = "Vòng đời màu: tạo, tìm, sửa, xoá")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra tạo màu (đầu bảng), tìm ra đúng 1 dòng, sửa Name và mã (báo cập nhật), tìm tên mới ra đúng 1 dòng, xoá qua hộp xác nhận (báo xoá), tìm lại ra Nothing found.")
    public void testColorLifecycle() {
        tc("AEC_COLOR_TC_040", "regression", "colors", "auth", "e2e");
        String token = newToken("TC040");
        String name = "E2E_" + token;
        String newName = "ColZ_" + token;
        ColorListPage list = openColorsAsAdmin();

        act("Tạo màu " + name, () -> list.createColor(name, newCode()));
        verify("Tạo thành công, màu ở đầu bảng", () -> {
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi tạo");
            int row = list.rowIndexOf(name);
            Assert.assertTrue(row >= 0 && row < TOP_ROWS, "Màu mới phải ở đầu bảng, thực tế dòng " + (row + 1));
        });

        act("Tìm tên màu", () -> list.search(name));
        verify(StepNames.ONE_ROW, () -> Assert.assertEquals(list.names(), List.of(name), "Kết quả tìm"));

        ColorEditPage edit = act("Sửa Name và mã", () -> {
            ColorEditPage page = list.editRow(0);
            page.enterName(newName);
            page.enterCode(newCode());
            page.saveAndWait();
            return page;
        });
        verify("Cập nhật thành công", () ->
                Assert.assertEquals(edit.getNotifyMessage(), UPDATED, "Thông báo sau khi sửa"));

        act("Tìm tên mới", () -> list.searchFor(newName));
        verify(StepNames.ONE_ROW, () -> Assert.assertEquals(list.names(), List.of(newName), "Kết quả tìm"));

        act("Xoá màu qua hộp xác nhận", () -> list.deleteRow(0));
        verify("Xoá thành công", () ->
                Assert.assertEquals(list.getNotifyMessage(), DELETED, "Thông báo sau khi xoá"));

        act("Tìm lại tên mới", () -> list.search(newName));
        verify("Nothing found", () ->
                Assert.assertTrue(list.isNothingFoundShown(), "Phải hiện Nothing found, thực tế " + list.names()));
    }

    @Test(groups = {"regression", "colors", "auth", "security", "knownIssue"}, description = "Tên màu có thẻ HTML phải hiển thị như chữ ở form sản phẩm")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra màu tên <b>Bold</b>… ở ô chọn màu của Add New Product phải hiện nguyên văn, không in đậm. Đang lệch: tên được hiểu thành HTML, chữ Bold in đậm (AMB-COLOR-05). Chỉ dùng thẻ vô hại <b>. Không lưu sản phẩm.")
    public void testHtmlColorNameShownAsTextInProductForm() {
        tc("AEC_COLOR_TC_046", "regression", "colors", "auth", "security", "knownIssue");
        String token = newToken("T46");
        String name = "<b>Bold</b>" + token;
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());
        ProductCreatePage product = arrange("Mở Add New Product, bật công tắc Colors", () -> {
            ProductCreatePage page = new ProductCreatePage().open();
            page.variation().enableColors();
            return page;
        });

        List<String> found = act("Gõ " + token + " vào ô chọn màu", () -> product.variation().searchColors(token));

        verify("Tên hiện nguyên văn, không in đậm", () -> {
            Assert.assertEquals(found, List.of(name), "Chữ của mục màu test (phải thấy cả thẻ)");
            Assert.assertFalse(product.variation().colorItemRendersHtml(token), "Tên không được hiểu thành HTML");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "accessibility"}, description = "Đóng hộp xác nhận xoá màu bằng phím Esc")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể c: mở hộp Delete Confirmation của màu test rồi nhấn Esc thì hộp đóng, màu test còn nguyên.")
    public void testEscClosesDeleteConfirmation() {
        tc("AEC_COLOR_TC_048", "regression", "colors", "auth", "accessibility");
        Allure.parameter("Biến thể", "c — Esc");
        String name = "Col_" + newToken("TC048c");
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());

        act("Mở hộp xoá của màu test, nhấn Esc", () -> {
            list.openDeleteOfRow(list.findRow(name));
            list.closeDeleteModal(ColorListPage.CloseBy.ESC);
        });

        verify("Hộp đóng, màu test còn nguyên", () -> {
            Assert.assertTrue(list.waitDeleteModalClosed(), "Hộp Delete Confirmation phải đóng");
            Assert.assertTrue(list.findRow(name) >= 0, "Màu test phải còn");
        });
    }
}
