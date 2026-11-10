package com.aec.tests;

import com.aec.pages.AttributeDetailPage;
import com.aec.pages.AttributeEditPage;
import com.aec.pages.AttributeListPage;
import com.aec.pages.CategoryFormPage;
import com.aec.pages.CategoryListPage;
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

/**
 * Xoá thuộc tính, liên kết Category · Product, vòng đời trọn vẹn.
 * Không automate TC_029: xoá thuộc tính còn giá trị cố ý để lại giá trị mồ côi (AMB-ATTR-06) — mỗi lần chạy sẽ sinh thêm
 * bản ghi không dọn được qua giao diện trên môi trường dùng chung.
 * TC ở docs/testcases/attribute/web/test_cases_attribute_web.md.
 */
@Feature("Attribute")
@Story("Xoá & liên kết thuộc tính")
public class AttributeLifecycleTest extends AttributeTestBase {

    @Test(groups = {"smoke", "attribute", "auth"}, description = "Xoá thuộc tính chưa có giá trị qua hộp xác nhận")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra nút thứ ba có chú thích Delete, mở hộp Delete Confirmation đúng nội dung mà không đổi URL; bấm Delete thì báo xoá thành công, ở /admin/attributes, không còn dòng thuộc tính; mở lại trang Attribute Detail cũ thì báo không tìm thấy.")
    public void testDeleteAttribute() {
        tc("AEC_ATTR_TC_008", "smoke", "attribute", "auth");
        String name = "AutoAttr_" + newToken("TC008");
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        int row = findRow(list, name);
        String detailUrl = list.detailUrlOfRow(row);

        Allure.step("Assert: Rê chuột lên nút thứ ba hiện Delete", () ->
                Assert.assertEquals(list.hoverRowButton(row, 2), "Delete", "Chú thích nút"));

        Allure.step("Act: Bấm nút Delete", () -> list.openDeleteOfRow(row));
        Allure.step("Assert: Hộp xác nhận đúng nội dung, URL không đổi", () -> {
            Assert.assertEquals(list.deleteModalTitle(), "Delete Confirmation", "Tiêu đề hộp");
            Assert.assertEquals(list.deleteModalText(), "Are you sure to delete this?", "Câu hỏi xác nhận");
            Assert.assertEquals(list.deleteModalButtons(), List.of("Cancel", "Delete"), "Nút trong hộp");
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/attributes", "Thanh địa chỉ không được đổi");
        });

        Allure.step("Act: Bấm Delete", list::confirmDelete);
        Allure.step("Assert: Xoá thành công, không còn dòng thuộc tính", () -> {
            Assert.assertEquals(list.getNotifyMessage(), DELETED, "Thông báo sau khi xoá");
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/attributes", "Thanh địa chỉ");
            Assert.assertEquals(list.rowIndexOf(name), -1, "Không được còn dòng mang tên thuộc tính");
        });

        Allure.step("Assert: Trang Attribute Detail cũ báo không tìm thấy", () -> {
            driver().get(detailUrl);
            Assert.assertTrue(list.isPageNotFound(), "Phải hiện Page Not Found!");
        });
    }

    @Test(groups = {"regression", "attribute", "auth"}, description = "Huỷ xoá thuộc tính thì thuộc tính còn nguyên")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bấm Delete ở dòng thuộc tính rồi Cancel thì hộp đóng, vẫn ở danh sách, thuộc tính còn nguyên.")
    public void testCancelDeleteKeepsAttribute() {
        tc("AEC_ATTR_TC_028", "regression", "attribute", "auth");
        String name = "AutoAttr_" + newToken("TC028");
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);

        Allure.step("Act: Bấm nút Delete ở dòng thuộc tính test", () -> list.openDeleteOfRow(findRow(list, name)));
        Allure.step("Assert: Hộp xác nhận hiện câu hỏi", () ->
                Assert.assertEquals(list.deleteModalText(), "Are you sure to delete this?", "Câu hỏi xác nhận"));

        Allure.step("Act: Bấm Cancel", () -> list.closeDeleteModal(AttributeListPage.CloseBy.CANCEL));
        Allure.step("Assert: Hộp đóng, vẫn ở danh sách, thuộc tính còn", () -> {
            Assert.assertTrue(list.waitDeleteModalClosed(), "Hộp phải đóng");
            Assert.assertTrue(list.isTitleDisplayed(), "Phải vẫn ở trang danh sách");
            Assert.assertTrue(findRow(list, name) >= 0, "Thuộc tính test phải còn");
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "integration"}, description = "Xoá thuộc tính đang được chọn ở Filtering Attributes của danh mục không làm hỏng danh mục")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra danh mục test chọn thuộc tính test ở Filtering Attributes; xoá thuộc tính (không bị chặn) thì trang sửa danh mục vẫn mở bình thường và Filtering Attributes về Nothing selected. Expected theo hành vi quan sát 30-10-2026 — AMB-ATTR-07 chưa chốt.")
    public void testDeleteAttributeUsedByCategory() {
        tc("AEC_ATTR_TC_030", "regression", "attribute", "auth", "integration");
        String token = newToken("TC030");
        alsoCleanCategories(token);
        String attribute = "AutoAttrUse_" + token;
        String category = "AutoCatAttr_" + token;
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, attribute);

        Allure.step("Arrange: Tạo danh mục test chọn thuộc tính test ở Filtering Attributes", () -> {
            CategoryFormPage form = new CategoryFormPage(driver()).openCreate();
            form.enterName(category);
            form.chooseAttribute(attribute);
            form.saveAndWait();
        });

        Allure.step("Act: Xoá thuộc tính test qua hộp xác nhận", () -> {
            list.open();
            list.deleteRow(list.rowIndexOf(attribute));
        });
        Allure.step("Assert: Xoá thành công, không còn dòng thuộc tính", () -> {
            Assert.assertEquals(list.getNotifyMessage(), DELETED, "Thông báo sau khi xoá");
            Assert.assertEquals(list.rowIndexOf(attribute), -1, "Không được còn dòng thuộc tính");
        });

        Allure.step("Assert: Trang sửa danh mục mở bình thường, Filtering Attributes về Nothing selected", () -> {
            CategoryListPage categories = new CategoryListPage(driver()).open();
            categories.search(category);
            int row = categories.rowIndexOf(category);
            Assert.assertTrue(row >= 0, "Phải tìm thấy danh mục test");
            CategoryFormPage edit = categories.editRow(row);
            Assert.assertFalse(edit.isServerErrorPage(), "Không được ra trang lỗi");
            Assert.assertTrue(edit.isTitleDisplayed(), "Tiêu đề Category Information");
            Assert.assertEquals(edit.attributesShown(), "Nothing selected", "Ô Filtering Attributes");
        });
    }

    @DataProvider(name = "linkedForms", parallel = true)
    public Object[][] linkedForms() {
        return new Object[][]{{"a", "category"}, {"b", "product"}};
    }

    @Test(dataProvider = "linkedForms", groups = {"regression", "attribute", "auth", "integration"},
            description = "Thuộc tính mới xuất hiện ở Category và Product")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra gõ tên thuộc tính test vào ô Filtering Attributes của Add New category, và ô chọn thuộc tính của khối Product Variation ở Add New Product, đều thấy thuộc tính. Không bấm Save ở cả hai form.")
    public void testNewAttributeAvailableInForms(String variant, String form) {
        tc("AEC_ATTR_TC_031", "regression", "attribute", "auth", "integration");
        Allure.parameter("Biến thể", variant + " — " + form);
        String name = "AutoAttrLink_" + newToken("TC031" + variant);
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);

        List<String> found = Allure.step("Act: Mở form " + form + ", gõ tên thuộc tính vào ô chọn", () ->
                "category".equals(form)
                        ? new CategoryFormPage(driver()).openCreate().searchAttributes(name)
                        : new ProductCreatePage(driver()).open().searchVariationAttributes(name));

        Allure.step("Assert: Ô chọn liệt kê thuộc tính test", () ->
                Assert.assertEquals(found, List.of(name), "Kết quả trong ô chọn"));
    }

    @Test(groups = {"regression", "attribute", "auth", "e2e"}, description = "Vòng đời thuộc tính: tạo, thêm giá trị, đổi tên, xoá giá trị, xoá thuộc tính")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra trọn vòng đời: tạo thuộc tính, thêm 2 giá trị (cột Values hiện 2 nhãn), đổi tên (vẫn đủ 2 nhãn), xoá lần lượt 2 giá trị (Nothing found), xoá thuộc tính (không còn dòng).")
    public void testAttributeLifecycle() {
        tc("AEC_ATTR_TC_032", "regression", "attribute", "auth", "e2e");
        String token = newToken("TC032");
        String name = "AutoAttrE2E_" + token;
        String renamed = "AutoAttrE2EEd_" + token;
        List<String> values = List.of("AutoVal_S_" + token, "AutoVal_M_" + token);
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: (1) Tạo thuộc tính", () -> createAttribute(list, name));

        Allure.step("Act: (2) Thêm 2 giá trị", () -> {
            AttributeDetailPage detail = openDetailOf(list, name);
            for (String value : values) {
                detail.addValue(value);
                Assert.assertEquals(detail.getNotifyMessage(), VALUE_INSERTED, "Thêm giá trị " + value);
            }
        });
        Allure.step("Assert: (3) Cột Values hiện 2 nhãn", () ->
                Assert.assertEquals(list.valuesOf(findRow(list, name)), values, "Cột Values"));

        Allure.step("Act: (4) Đổi tên", () -> {
            AttributeEditPage edit = openEditOf(list, name);
            edit.enterName(renamed);
            edit.saveAndWait();
            Assert.assertEquals(edit.getNotifyMessage(), UPDATED, "Thông báo đổi tên");
        });
        Allure.step("Assert: (5) Dòng mang tên mới, vẫn đủ 2 nhãn", () ->
                Assert.assertEquals(list.valuesOf(findRow(list, renamed)), values, "Cột Values sau khi đổi tên"));

        Allure.step("Act: (6) Xoá lần lượt 2 giá trị", () -> {
            AttributeDetailPage detail = openDetailOf(list, renamed);
            for (int i = 0; i < values.size(); i++) {
                detail.deleteRow(0);
                Assert.assertEquals(detail.getNotifyMessage(), VALUE_DELETED, "Thông báo xoá giá trị");
            }
            Assert.assertTrue(detail.isNothingFoundShown(), "Bảng giá trị phải Nothing found");
        });

        Allure.step("Act: (7) Xoá thuộc tính", () -> list.deleteRow(findRow(list, renamed)));
        Allure.step("Assert: (7) Xoá thành công, không còn dòng", () -> {
            Assert.assertEquals(list.getNotifyMessage(), DELETED, "Thông báo xoá thuộc tính");
            Assert.assertEquals(list.rowIndexOf(renamed), -1, "Không được còn dòng mang tên mới");
        });
    }
}
