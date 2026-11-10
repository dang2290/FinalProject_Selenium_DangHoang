package com.aec.tests;

import com.aec.pages.AttributeDetailPage;
import com.aec.pages.AttributeListPage;
import com.aec.pages.AttributeValueEditPage;
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

/** Trang Attribute Detail và giá trị thuộc tính: thêm, sửa, xoá, validation, bảo mật, bố cục. TC ở docs/testcases/attribute/web/test_cases_attribute_web.md. */
@Feature("Attribute")
@Story("Giá trị thuộc tính")
public class AttributeValueTest extends AttributeTestBase {

    @Test(groups = {"smoke", "attribute", "auth", "ui"}, description = "Trang Attribute Detail của thuộc tính chưa có giá trị hiển thị đủ thành phần")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra nút Attribute values mở /admin/attributes/<id> với tiêu đề Attribute Detail, thẻ trái mang tên thuộc tính, bảng #/Value/Action đang Nothing found; thẻ phải Add New Attribute Value có ô Attribute Name sẵn tên và không sửa được, ô Attribute Value rỗng, nút Save.")
    public void testDetailPageElements() {
        tc("AEC_ATTR_TC_003", "smoke", "attribute", "auth", "ui");
        String name = "AutoAttr_" + newToken("TC003");
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);

        AttributeDetailPage detail = Allure.step("Act: Bấm nút Attribute values", () -> openDetailOf(list, name));

        Allure.step("Assert: Bảng kiểm trang Attribute Detail", () -> {
            Assert.assertTrue(detail.currentUrl().matches(".*/admin/attributes/\\d+$"), "Thanh địa chỉ: " + detail.currentUrl());
            Assert.assertTrue(detail.isTitleDisplayed(), "(1) Tiêu đề Attribute Detail");
            Assert.assertEquals(detail.cardHeaders(), List.of(name, "Add New Attribute Value"), "(2)(3) Tiêu đề hai thẻ");
            Assert.assertEquals(detail.tableHeaders(), List.of("#", "Value", "Action"), "(2) Cột bảng");
            Assert.assertTrue(detail.isNothingFoundShown(), "(2) Bảng phải hiện Nothing found");
            Assert.assertEquals(detail.valueFormLabels(), List.of("Attribute Name", "Attribute Value"), "(3) Nhãn form");
            Assert.assertEquals(detail.attributeNameValue(), name, "(3) Ô Attribute Name");
            Assert.assertTrue(detail.isAttributeNameReadonly(), "(3) Ô Attribute Name không được sửa");
            Assert.assertEquals(detail.valueInputText(), "", "(3) Ô Attribute Value rỗng");
            Assert.assertEquals(detail.valuePlaceholder(), "Name", "(3) Gợi ý ô Attribute Value");
            Assert.assertTrue(detail.isSaveDisplayed(), "(3) Nút Save");
        });
    }

    @Test(groups = {"smoke", "attribute", "auth"}, description = "Thêm giá trị cho thuộc tính, giá trị hiện ở trang chi tiết và cột Values")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra thêm giá trị thì báo thành công, vẫn ở Attribute Detail, bảng có 1 dòng với 2 nút Edit/Delete; danh sách hiện nhãn giá trị ở cột Values.")
    public void testAddValue() {
        tc("AEC_ATTR_TC_007", "smoke", "attribute", "auth");
        String token = newToken("TC007");
        String name = "AutoAttr_" + token;
        String value = "AutoVal_Red_" + token;
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeDetailPage detail = openDetailOf(list, name);

        Allure.step("Act: Nhập giá trị rồi Save", () -> detail.addValue(value));

        Allure.step("Assert: Thông báo thành công, vẫn ở Attribute Detail, đúng 1 dòng giá trị", () -> {
            Assert.assertEquals(detail.getNotifyMessage(), VALUE_INSERTED, "Thông báo sau khi thêm giá trị");
            Assert.assertTrue(detail.isTitleDisplayed(), "Phải vẫn ở trang Attribute Detail");
            Assert.assertEquals(detail.values(), List.of(value), "Bảng giá trị");
            Assert.assertEquals(detail.rowNumbers(), List.of(1), "Cột #");
            Assert.assertEquals(detail.rowButtonTitles(0), List.of("Edit", "Delete"), "Nút ở cột Action");
        });

        Allure.step("Assert: Cột Values ở danh sách hiện nhãn giá trị", () ->
                Assert.assertEquals(list.valuesOf(findRow(list, name)), List.of(value), "Cột Values"));
    }

    @Test(groups = {"regression", "attribute", "auth", "validation"}, description = "Không thêm được giá trị khi Attribute Value trống")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra để trống Attribute Value rồi Save thì trình duyệt chặn, con trỏ ở ô Attribute Value, bảng vẫn Nothing found.")
    public void testAddEmptyValueBlocked() {
        tc("AEC_ATTR_TC_022", "regression", "attribute", "auth", "validation");
        String name = "AutoAttr_" + newToken("TC022");
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeDetailPage detail = openDetailOf(list, name);

        Allure.step("Act: Để trống Attribute Value, bấm Save", () -> {
            detail.markPage();
            detail.clickSave();
        });

        Allure.step("Assert: Trình duyệt chặn, bảng vẫn Nothing found", () -> {
            Assert.assertFalse(detail.valueValidationMessage().isEmpty(), "Phải có bong bóng nhắc nhập");
            Assert.assertTrue(detail.isSamePageInstance(), "Không được gửi form");
            Assert.assertTrue(detail.isValueFocused(), "Con trỏ phải nằm trong ô Attribute Value");
            Assert.assertTrue(detail.isNothingFoundShown(), "Bảng phải vẫn Nothing found");
        });
    }

    @Test(groups = {"regression", "attribute", "auth"}, description = "Thêm được hai giá trị trùng nhau trong cùng thuộc tính")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra thêm cùng một giá trị 2 lần đều báo thành công, bảng có 2 dòng cùng giá trị.")
    public void testDuplicateValueAllowed() {
        tc("AEC_ATTR_TC_023", "regression", "attribute", "auth");
        String token = newToken("TC023");
        String name = "AutoAttr_" + token;
        String value = "AutoVal_Dup_" + token;
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeDetailPage detail = openDetailOf(list, name);

        Allure.step("Act: Thêm cùng giá trị 2 lần", () -> {
            detail.addValue(value);
            detail.addValue(value);
        });

        Allure.step("Assert: Lần 2 báo thành công, bảng có 2 dòng cùng giá trị", () -> {
            Assert.assertEquals(detail.getNotifyMessage(), VALUE_INSERTED, "Thông báo lần 2");
            Assert.assertEquals(detail.values(), List.of(value, value), "Bảng giá trị");
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "validation", "knownIssue"}, description = "Giá trị chỉ có khoảng trắng phải bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra giá trị 3 dấu cách thì không được thêm: bảng vẫn Nothing found, cột Values vẫn trống. Đang lệch: hệ thống thêm một giá trị rỗng (AMB-ATTR-04).")
    public void testBlankValueRejected() {
        tc("AEC_ATTR_TC_024", "regression", "attribute", "auth", "validation", "knownIssue");
        String name = "AutoAttr_" + newToken("TC024");
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeDetailPage detail = openDetailOf(list, name);

        Allure.step("Act: Nhập 3 dấu cách rồi Save", () -> detail.addValue("   "));

        Allure.step("Assert: Bảng vẫn Nothing found, cột Values vẫn trống", () -> {
            Assert.assertFalse(detail.isServerErrorPage(), "Không được ra trang lỗi");
            Assert.assertTrue(detail.isNothingFoundShown(),
                    "Bảng giá trị phải vẫn Nothing found, thực tế có " + detail.rowCount() + " dòng: " + detail.values());
            Assert.assertEquals(list.valuesOf(findRow(list, name)), List.of(), "Cột Values");
        });
    }

    @DataProvider(name = "longValues", parallel = true)
    public Object[][] longValues() {
        return new Object[][]{{"a", 51}, {"b", 256}};
    }

    @Test(dataProvider = "longValues", groups = {"regression", "attribute", "auth", "boundary"}, description = "Giá trị dài được lưu nguyên vẹn")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra giá trị 51 và 256 ký tự thêm thành công, trang sửa giá trị hiện đúng từng ký tự, không bị cắt.")
    public void testLongValueKept(String variant, int length) {
        tc("AEC_ATTR_TC_025", "regression", "attribute", "auth", "boundary");
        Allure.parameter("Biến thể", variant + " — " + length + " ký tự");
        String token = newToken("TC025" + variant);
        String name = "AutoAttr_" + token;
        String value = padTo("AutoVal" + length + "_" + token + "_", 'x', length);
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeDetailPage detail = openDetailOf(list, name);

        Allure.step("Act: Thêm giá trị " + length + " ký tự", () -> detail.addValue(value));
        Allure.step("Assert: Thêm thành công", () ->
                Assert.assertEquals(detail.getNotifyMessage(), VALUE_INSERTED, "Thông báo sau khi thêm giá trị"));

        AttributeValueEditPage valueEdit = Allure.step("Act: Mở trang sửa giá trị (theo đường dẫn nút Edit)", () ->
                {
                    String url = detail.editValueUrlOfRow(0);
                    driver().get(url);
                    return new AttributeValueEditPage(driver()).waitUntilLoaded();
                });
        Allure.step("Assert: Ô Attribute Value đúng từng ký tự", () ->
                Assert.assertEquals(valueEdit.valueText(), value, "Giá trị sau khi lưu"));
    }

    @Test(groups = {"regression", "attribute", "auth"}, description = "Sửa giá trị thuộc tính")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra nút Edit (chú thích Edit) ở dòng giá trị mở /admin/edit-attribute-value/<id> — tiêu đề Attribute Value Information, ô có sẵn giá trị, không có tab ngôn ngữ; đổi giá trị rồi Save thì báo cập nhật, vẫn ở trang sửa; Attribute Detail hiện giá trị mới.")
    public void testEditValue() {
        tc("AEC_ATTR_TC_026", "regression", "attribute", "auth");
        String token = newToken("TC026");
        String name = "AutoAttr_" + token;
        String oldValue = "AutoVal_Red_" + token;
        String newValue = "AutoVal_Blue_" + token;
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeDetailPage detail = openDetailOf(list, name);
        Allure.step("Arrange: Thêm giá trị " + oldValue, () -> detail.addValue(oldValue));
        String detailUrl = detail.currentUrl();

        Allure.step("Assert: Rê chuột lên nút thứ nhất hiện Edit", () ->
                Assert.assertEquals(detail.editTooltipOfFirstRow(), "Edit", "Chú thích nút"));

        AttributeValueEditPage valueEdit = Allure.step("Act: Bấm nút Edit", () -> detail.editValue(0));
        Allure.step("Assert: Trang Attribute Value Information", () -> {
            Assert.assertTrue(valueEdit.currentUrl().matches(".*/admin/edit-attribute-value/\\d+$"), "Thanh địa chỉ: " + valueEdit.currentUrl());
            Assert.assertTrue(valueEdit.isTitleDisplayed(), "Tiêu đề Attribute Value Information");
            Assert.assertEquals(valueEdit.valueText(), oldValue, "Ô Attribute Value");
            Assert.assertTrue(valueEdit.isSaveDisplayed(), "Nút Save");
            Assert.assertFalse(valueEdit.hasLanguageTabs(), "Không được có tab ngôn ngữ");
        });

        Allure.step("Act: Đổi giá trị rồi Save", () -> valueEdit.changeValue(newValue));
        Allure.step("Assert: Cập nhật thành công, vẫn ở trang sửa giá trị với giá trị mới", () -> {
            Assert.assertEquals(valueEdit.getNotifyMessage(), VALUE_UPDATED, "Thông báo sau khi sửa");
            Assert.assertTrue(valueEdit.isTitleDisplayed(), "Phải vẫn ở trang sửa giá trị");
            Assert.assertEquals(valueEdit.valueText(), newValue, "Ô Attribute Value");
        });

        Allure.step("Assert: Attribute Detail hiện giá trị mới", () ->
                Assert.assertEquals(detail.openUrl(detailUrl).values(), List.of(newValue), "Bảng giá trị"));
    }

    @Test(groups = {"regression", "attribute", "auth"}, description = "Huỷ và xác nhận xoá giá trị qua hộp xác nhận")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra (a) bấm Delete ở dòng giá trị rồi Cancel thì hộp đóng, giá trị còn; (b) mở lại và bấm Delete thì báo xoá thành công, vẫn ở Attribute Detail, bảng Nothing found.")
    public void testDeleteValue() {
        tc("AEC_ATTR_TC_027", "regression", "attribute", "auth");
        Allure.parameter("Biến thể", "a Cancel → b Delete");
        String token = newToken("TC027");
        String name = "AutoAttr_" + token;
        String value = "AutoVal_Del_" + token;
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeDetailPage detail = openDetailOf(list, name);
        Allure.step("Arrange: Thêm giá trị " + value, () -> detail.addValue(value));

        Allure.step("Act: Bấm nút Delete ở dòng giá trị", () -> detail.openDeleteOfRow(0));
        Allure.step("Assert: Hộp xác nhận đúng nội dung", () -> {
            Assert.assertEquals(detail.deleteModalText(), "Are you sure to delete this?", "Câu hỏi xác nhận");
            Assert.assertEquals(detail.deleteModalButtons(), List.of("Cancel", "Delete"), "Nút trong hộp");
        });

        Allure.step("Act: (a) Bấm Cancel", () -> detail.closeDeleteModal(AttributeDetailPage.CloseBy.CANCEL));
        Allure.step("Assert: (a) Hộp đóng, giá trị còn", () -> {
            Assert.assertTrue(detail.waitDeleteModalClosed(), "Hộp phải đóng");
            Assert.assertEquals(detail.values(), List.of(value), "Bảng giá trị");
        });

        Allure.step("Act: (b) Mở lại hộp và bấm Delete", () -> detail.deleteRow(0));
        Allure.step("Assert: (b) Xoá thành công, vẫn ở Attribute Detail, bảng Nothing found", () -> {
            Assert.assertEquals(detail.getNotifyMessage(), VALUE_DELETED, "Thông báo sau khi xoá giá trị");
            Assert.assertTrue(detail.isTitleDisplayed(), "Phải vẫn ở trang Attribute Detail");
            Assert.assertTrue(detail.isNothingFoundShown(), "Bảng phải Nothing found");
        });
    }

    @DataProvider(name = "injectionValues", parallel = true)
    public Object[][] injectionValues() {
        return new Object[][]{{"a", "<script>alert(1)</script>"}, {"b", "<img src=x onerror=alert(1)>"}, {"c", "' OR '1'='1 -- "}};
    }

    @Test(dataProvider = "injectionValues", groups = {"regression", "attribute", "auth", "security"},
            description = "Mã script và chuỗi SQL ở giá trị được lưu như chữ, không chạy")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra giá trị chứa script, thẻ img onerror, chuỗi SQL thêm thành công; bảng giá trị và cột Values ở danh sách hiện nguyên văn dạng chữ, không bật hộp thoại.")
    public void testInjectionValueNotExecuted(String variant, String payload) {
        tc("AEC_ATTR_TC_037", "regression", "attribute", "auth", "security");
        String token = newToken("TC037" + variant);
        String name = "AutoAttr_" + token;
        String value = payload + "V37" + variant;
        Allure.parameter("Biến thể", variant + " — " + value);
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeDetailPage detail = openDetailOf(list, name);

        Allure.step("Act: Thêm giá trị chứa mã", () -> detail.addValue(value));

        Allure.step("Assert: Bảng giá trị — không hộp thoại, giá trị nguyên văn", () -> {
            Assert.assertFalse(detail.isAlertOpen(), "Không được bật hộp thoại ở Attribute Detail");
            Assert.assertEquals(detail.getNotifyMessage(), VALUE_INSERTED, "Thông báo sau khi thêm giá trị");
            Assert.assertEquals(detail.values(), List.of(value), "Bảng giá trị");
        });
        Allure.step("Assert: Danh sách — không hộp thoại, nhãn nguyên văn", () -> {
            int row = findRow(list, name);
            Assert.assertFalse(list.isAlertOpen(), "Không được bật hộp thoại ở danh sách");
            Assert.assertEquals(list.valuesOf(row), List.of(value), "Cột Values");
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "ui", "knownIssue"}, description = "Giá trị dài không làm vỡ bố cục, nút thao tác luôn bấm được")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra thuộc tính có 1 giá trị ngắn và 1 giá trị 256 ký tự: ở Attribute Detail mọi nút Edit/Delete bấm trúng được, hộp xoá của giá trị ngắn mở bình thường; danh sách không có thanh cuộn ngang. Đang lệch: cột Action tràn xuống dưới thẻ bên phải, danh sách cuộn ngang (AMB-ATTR-11).")
    public void testLongValueLayout() {
        tc("AEC_ATTR_TC_038", "regression", "attribute", "auth", "ui", "knownIssue");
        String token = newToken("TC038");
        String name = "AutoAttr_" + token;
        String shortValue = "AutoVal_Short_" + token;
        String longValue = padTo("AutoVal256_" + token + "_", 'x', 256);
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeDetailPage detail = openDetailOf(list, name);
        Allure.step("Arrange: Thêm 1 giá trị ngắn và 1 giá trị 256 ký tự", () -> {
            detail.addValue(shortValue);
            detail.addValue(longValue);
        });

        Allure.step("Assert: Mọi nút ở cột Action bấm trúng được", () -> {
            for (int row = 0; row < detail.rowCount(); row++) {
                for (int button = 0; button < 2; button++) {
                    Assert.assertTrue(detail.isRowButtonClickable(row, button),
                            "Nút " + (button + 1) + " ở dòng giá trị " + (row + 1) + " bị che, bấm không trúng");
                }
            }
        });

        Allure.step("Act + Assert: Hộp xoá của giá trị ngắn mở bình thường, bấm Cancel", () -> {
            detail.openDeleteOfRow(detail.values().indexOf(shortValue));
            Assert.assertTrue(detail.isDeleteModalShown(), "Hộp Delete Confirmation phải mở");
            detail.closeDeleteModal(AttributeDetailPage.CloseBy.CANCEL);
            Assert.assertTrue(detail.waitDeleteModalClosed(), "Cancel phải đóng hộp");
        });

        Allure.step("Assert: Danh sách không có thanh cuộn ngang", () -> {
            list.open();
            Assert.assertTrue(list.hasNoHorizontalScroll(), "Danh sách không được cuộn ngang");
        });
    }
}
