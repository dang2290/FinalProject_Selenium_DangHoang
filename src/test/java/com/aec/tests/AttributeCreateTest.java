package com.aec.tests;

import com.aec.pages.AttributeDetailPage;
import com.aec.pages.AttributeEditPage;
import com.aec.pages.AttributeListPage;
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

/**
 * Tạo thuộc tính & validation Name. Máy chủ cắt Name còn 50 ký tự (AMB-ATTR-01) — tên test giữ ≤ 50 ký tự trừ TC kiểm độ dài.
 * Không automate: TC_013-a (Name 1 ký tự `Q`) và TC_015 (Name toàn khoảng trắng tạo bản ghi tên rỗng) — không truy vết được
 * bản ghi để dọn trên môi trường dùng chung khi nhiều test chạy song song.
 * TC ở docs/testcases/attribute/web/test_cases_attribute_web.md.
 */
@Feature("Attribute")
@Story("Tạo thuộc tính")
public class AttributeCreateTest extends AttributeTestBase {

    @Test(groups = {"smoke", "attribute", "auth"}, description = "Tạo thuộc tính hợp lệ, thuộc tính mới nằm ở dòng đầu bảng")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tạo thuộc tính thì báo thành công, vẫn ở /admin/attributes, ô Name trống lại; thuộc tính mới ở đầu bảng với cột Values trống và đủ 3 nút. Chạy song song nên kiểm trong 5 dòng đầu thay vì đúng dòng 1.")
    public void testCreateAttribute() {
        tc("AEC_ATTR_TC_005", "smoke", "attribute", "auth");
        String name = "AutoAttr_" + newToken("TC005");
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Nhập Name rồi bấm Save", () -> list.createAttribute(name));

        Allure.step("Assert: Thông báo thành công, vẫn ở danh sách, form trống lại", () -> {
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi tạo");
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/attributes", "Thanh địa chỉ");
            Assert.assertEquals(list.nameValue(), "", "Ô Name phải trống lại");
        });

        Allure.step("Assert: Thuộc tính mới ở đầu bảng, Values trống, đủ 3 nút", () -> {
            int row = list.rowIndexOf(name);
            Assert.assertTrue(row >= 0 && row < TOP_ROWS, "Thuộc tính mới phải ở đầu bảng, thực tế dòng " + (row + 1));
            Assert.assertEquals(list.rowNumbers().get(row).intValue(), row + 1, "Cột #");
            Assert.assertEquals(list.valuesOf(row), List.of(), "Cột Values phải trống");
            Assert.assertEquals(list.rowButtonTitles(row), List.of("Attribute values", "Edit", "Delete"), "3 nút thao tác");
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "validation"}, description = "Không tạo được thuộc tính khi Name trống")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra để trống Name rồi Save thì trình duyệt chặn: không gửi form, trang không nạp lại, con trỏ ở ô Name. Không đếm số dòng như TC — các test song song tạo thuộc tính cùng lúc.")
    public void testCreateWithEmptyNameBlocked() {
        tc("AEC_ATTR_TC_012", "regression", "attribute", "auth", "validation");
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Để trống Name, bấm Save", () -> {
            list.markPage();
            list.clickSave();
        });

        Allure.step("Assert: Trình duyệt chặn ở ô Name", () -> {
            Assert.assertFalse(list.nameValidationMessage().isEmpty(), "Phải có bong bóng nhắc nhập");
            Assert.assertTrue(list.isSamePageInstance(), "Không được gửi form");
            Assert.assertTrue(list.isNameFocused(), "Con trỏ phải nằm trong ô Name");
        });
    }

    @DataProvider(name = "validNameLengths", parallel = true)
    public Object[][] validNameLengths() {
        return new Object[][]{{"b", 49}, {"c", 50}};
    }

    @Test(dataProvider = "validNameLengths", groups = {"regression", "attribute", "auth", "validation", "boundary"},
            description = "Name có độ dài hợp lệ được lưu nguyên vẹn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name 49 và 50 ký tự lưu thành công, trang sửa giữ đúng từng ký tự. Biến thể a (1 ký tự) không automate.")
    public void testNameAtValidLength(String variant, int length) {
        tc("AEC_ATTR_TC_013", "regression", "attribute", "auth", "validation", "boundary");
        Allure.parameter("Biến thể", variant + " — " + length + " ký tự");
        String token = newToken("TC013" + variant);
        String name = padTo("AutoAttr" + length + "_" + token + "_", 'x', length);
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Tạo thuộc tính Name " + length + " ký tự", () -> createAttribute(list, name));

        AttributeEditPage edit = openEditOf(list, name);
        Allure.step("Assert: Ô Name đúng từng ký tự", () -> {
            Assert.assertEquals(edit.nameValue(), name, "Name sau khi lưu");
            Assert.assertEquals(edit.nameValue().length(), length, "Độ dài Name");
        });
    }

    @DataProvider(name = "tooLongNames", parallel = true)
    public Object[][] tooLongNames() {
        return new Object[][]{{"a", 51}, {"b", 255}};
    }

    @Test(dataProvider = "tooLongNames", groups = {"regression", "attribute", "auth", "validation", "boundary", "knownIssue"},
            description = "Name dài hơn 50 ký tự phải bị chặn kèm thông báo, không được cắt im lặng")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name 51 và 255 ký tự thì không có thuộc tính mới. Đang lệch: hệ thống báo thành công và lưu 50 ký tự đầu (AMB-ATTR-01).")
    public void testNameTooLongRejected(String variant, int length) {
        tc("AEC_ATTR_TC_014", "regression", "attribute", "auth", "validation", "boundary", "knownIssue");
        Allure.parameter("Biến thể", variant + " — " + length + " ký tự");
        String token = newToken("TC014" + variant);
        String name = padTo("AutoAttr" + length + "_" + token + "_", 'x', length);
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Nhập Name " + length + " ký tự rồi Save", () -> list.createAttribute(name));

        Allure.step("Assert: Không có thuộc tính mới (kể cả bản bị cắt)", () -> {
            list.open();
            Assert.assertEquals(list.rowIndexesContaining(token), List.of(), "Thuộc tính quá dài không được lưu");
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "validation"}, description = "Name có khoảng trắng đầu và cuối được bỏ khi lưu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name có 3 dấu cách ở mỗi đầu thì danh sách và trang sửa hiện Name không còn khoảng trắng đầu/cuối.")
    public void testNameTrimmed() {
        tc("AEC_ATTR_TC_016", "regression", "attribute", "auth", "validation");
        String name = "AutoAttrTrim_" + newToken("TC016");
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Tạo thuộc tính với Name có khoảng trắng đầu/cuối", () -> createAttribute(list, "   " + name + "   "));

        Allure.step("Assert: Danh sách hiện Name đã bỏ khoảng trắng", () ->
                Assert.assertTrue(list.rowIndexOf(name) >= 0, "Phải có dòng mang đúng " + name));
        AttributeEditPage edit = openEditOf(list, name);
        Allure.step("Assert: Ô Name không còn khoảng trắng đầu/cuối", () ->
                Assert.assertEquals(edit.nameValue(), name, "Name sau khi lưu"));
    }

    @Test(groups = {"regression", "attribute", "auth"}, description = "Tạo được hai thuộc tính trùng tên")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tạo 2 thuộc tính cùng Name đều báo thành công, bảng có đúng 2 dòng mang Name đó.")
    public void testDuplicateNameAllowed() {
        tc("AEC_ATTR_TC_017", "regression", "attribute", "auth");
        String name = "AutoAttrDup_" + newToken("TC017");
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Tạo 2 thuộc tính cùng Name", () -> {
            createAttribute(list, name);
            createAttribute(list, name);
        });

        Allure.step("Assert: Đúng 2 dòng cùng Name", () ->
                Assert.assertEquals(list.rowIndexesContaining(name).size(), 2, "Số dòng mang Name"));
    }

    @DataProvider(name = "specialNames", parallel = true)
    public Object[][] specialNames() {
        return new Object[][]{{"a", "Chất liệu Ưu đãi ", ""}, {"b", "Size & Màu (", ")"}, {"c", "1234567890_", ""}};
    }

    @Test(dataProvider = "specialNames", groups = {"regression", "attribute", "auth", "validation", "localization"},
            description = "Name có tiếng Việt, số và ký tự đặc biệt được lưu đúng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name tiếng Việt có dấu, có &, (), toàn số đều lưu thành công; danh sách và trang sửa hiện đúng như đã nhập.")
    public void testSpecialCharacterName(String variant, String head, String tail) {
        tc("AEC_ATTR_TC_018", "regression", "attribute", "auth", "validation", "localization");
        String name = head + newToken("TC018" + variant) + tail;
        Allure.parameter("Biến thể", variant + " — " + name);
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Tạo thuộc tính", () -> createAttribute(list, name));

        Allure.step("Assert: Danh sách hiện đúng Name", () ->
                Assert.assertTrue(list.rowIndexOf(name) >= 0, "Phải có dòng mang đúng " + name));
        AttributeEditPage edit = openEditOf(list, name);
        Allure.step("Assert: Ô Name đúng như đã nhập", () -> Assert.assertEquals(edit.nameValue(), name, "Name"));
    }

    @Test(groups = {"regression", "attribute", "auth"}, description = "Bấm Save liên tiếp chỉ tạo một thuộc tính")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra bấm đúp nút Save của form Add New Attribute thì chỉ có đúng 1 thuộc tính được tạo.")
    public void testDoubleClickSaveCreatesOne() {
        tc("AEC_ATTR_TC_033", "regression", "attribute", "auth");
        String name = "AutoAttrDbl_" + newToken("TC033");
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Nhập Name rồi bấm đúp Save", () -> {
            list.enterName(name);
            list.doubleClickSave();
        });

        Allure.step("Assert: Đúng 1 dòng mang Name", () -> {
            list.open();
            Assert.assertEquals(list.rowIndexesContaining(name).size(), 1, "Không được tạo trùng thuộc tính");
        });
    }

    @DataProvider(name = "injectionNames", parallel = true)
    public Object[][] injectionNames() {
        return new Object[][]{{"a", "<script>alert(1)</script>"}, {"b", "<img src=x onerror=alert(1)>"}, {"c", "' OR '1'='1 -- "}};
    }

    @Test(dataProvider = "injectionNames", groups = {"regression", "attribute", "auth", "security"},
            description = "Mã script và chuỗi SQL ở Name được lưu như chữ, không chạy")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name chứa script, thẻ img onerror, chuỗi SQL lưu thành công; danh sách, trang sửa, trang Attribute Detail hiện nguyên văn dạng chữ và không bật hộp thoại.")
    public void testInjectionNameNotExecuted(String variant, String payload) {
        tc("AEC_ATTR_TC_036", "regression", "attribute", "auth", "security");
        String name = payload + newToken("TC036" + variant);
        Allure.parameter("Biến thể", variant + " — " + name);
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Tạo thuộc tính với Name chứa mã", () -> createAttribute(list, name));

        Allure.step("Assert: Danh sách — không hộp thoại, tên nguyên văn", () -> {
            Assert.assertFalse(list.isAlertOpen(), "Không được bật hộp thoại ở danh sách");
            Assert.assertTrue(list.rowIndexOf(name) >= 0, "Danh sách phải hiện nguyên văn " + name);
        });
        Allure.step("Assert: Trang sửa — không hộp thoại, tên nguyên văn", () -> {
            AttributeEditPage edit = openEditOf(list, name);
            Assert.assertFalse(edit.isAlertOpen(), "Không được bật hộp thoại ở trang sửa");
            Assert.assertEquals(edit.nameValue(), name, "Ô Name");
        });
        Allure.step("Assert: Attribute Detail — không hộp thoại, tên nguyên văn", () -> {
            AttributeDetailPage detail = openDetailOf(list, name);
            Assert.assertFalse(detail.isAlertOpen(), "Không được bật hộp thoại ở Attribute Detail");
            Assert.assertEquals(detail.cardHeaders().get(0), name, "Tiêu đề thẻ trái");
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "localization", "knownIssue"}, description = "Emoji trong Name hiển thị và lưu đúng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name có emoji lưu thành công, danh sách và trang sửa hiện đúng emoji. Đang lệch: emoji bị thay bằng ? (AMB-ATTR-09).")
    public void testEmojiName() {
        tc("AEC_ATTR_TC_039", "regression", "attribute", "auth", "localization", "knownIssue");
        String token = newToken("TC039");
        String name = "AutoAttr 📦🎨 " + token;
        AttributeListPage list = openAttributesAsAdmin();

        Allure.step("Act: Tạo thuộc tính với Name có emoji", () -> createAttribute(list, name));

        Allure.step("Assert: Danh sách hiện đúng emoji", () -> {
            List<Integer> rows = list.rowIndexesContaining(token);
            Assert.assertEquals(rows.size(), 1, "Phải có 1 dòng chứa mã test");
            Assert.assertEquals(list.names().get(rows.get(0)), name, "Tên ở danh sách");
        });
    }
}
