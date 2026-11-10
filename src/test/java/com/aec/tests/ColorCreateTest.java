package com.aec.tests;

import com.aec.pages.ColorEditPage;
import com.aec.pages.ColorListPage;
import com.aec.utils.TestDataGenerator;
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
 * Form Add New Color: tạo hợp lệ, validation Name / Color Code, bấm đúp, script/SQL, emoji, bàn phím.
 * TC ở docs/testcases/colors/web/test_cases_colors_web.md.
 */
@Feature("Colors")
@Story("Tạo màu")
public class ColorCreateTest extends ColorTestBase {

    @Test(groups = {"smoke", "colors", "auth", "criticalPath"}, description = "Tạo màu hợp lệ, nằm dòng 1")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra nhập Name, Color Code hợp lệ rồi Save thì báo Color has been inserted successfully, vẫn ở /admin/colors, hai ô trở về rỗng và màu mới nằm ở đầu bảng.")
    public void testCreateValidColor() {
        tc("AEC_COLOR_TC_004", "smoke", "colors", "auth", "criticalPath");
        String name = "Col_" + newToken("TC004");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Nhập Name, Color Code rồi bấm Save", () -> list.createColor(name, newCode()));

        Allure.step("Assert: Tạo thành công, form rỗng, màu mới ở đầu bảng", () -> {
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save");
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/colors", "Vẫn ở danh sách");
            Assert.assertEquals(list.nameValue(), "", "Ô Name phải rỗng");
            Assert.assertEquals(list.codeValue(), "", "Ô Color Code phải rỗng");
            int row = list.rowIndexOf(name);
            Assert.assertTrue(row >= 0 && row < TOP_ROWS, "Màu mới phải ở đầu bảng, thực tế dòng " + (row + 1));
        });
    }

    @Test(groups = {"regression", "colors", "auth", "validation"}, description = "Không tạo được khi Name trống")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra để trống Name, nhập Color Code rồi Save thì trình duyệt chặn: không gửi form, trang không nạp lại, con trỏ ở ô Name. Không đếm số dòng như TC — các test song song tạo màu cùng lúc.")
    public void testCreateWithEmptyNameBlocked() {
        tc("AEC_COLOR_TC_013", "regression", "colors", "auth", "validation");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Để trống Name, nhập Color Code, bấm Save", () -> {
            list.enterCode(newCode());
            list.markPage();
            list.clickSave();
        });

        Allure.step("Assert: Trình duyệt chặn ở ô Name", () -> {
            Assert.assertFalse(list.nameValidationMessage().isEmpty(), "Phải có bong bóng nhắc nhập");
            Assert.assertTrue(list.isSamePageInstance(), "Không được gửi form");
            Assert.assertTrue(list.isNameFocused(), "Con trỏ phải nằm trong ô Name");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "validation"}, description = "Không tạo được khi Color Code trống")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra nhập Name, để trống Color Code rồi Save thì trình duyệt chặn: không gửi form, con trỏ ở ô Color Code, không có màu mang Name.")
    public void testCreateWithEmptyCodeBlocked() {
        tc("AEC_COLOR_TC_014", "regression", "colors", "auth", "validation");
        String name = "Col_" + newToken("TC014");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Nhập Name, để trống Color Code, bấm Save", () -> {
            list.enterName(name);
            list.markPage();
            list.clickSave();
        });

        Allure.step("Assert: Trình duyệt chặn ở ô Color Code, không có màu mới", () -> {
            Assert.assertFalse(list.codeValidationMessage().isEmpty(), "Phải có bong bóng nhắc nhập");
            Assert.assertTrue(list.isSamePageInstance(), "Không được gửi form");
            Assert.assertTrue(list.isCodeFocused(), "Con trỏ phải nằm trong ô Color Code");
            Assert.assertEquals(findRow(list, name, name), -1, "Không được có màu mang Name vừa nhập");
        });
    }

    @DataProvider(name = "validNameLengths", parallel = true)
    public Object[][] validNameLengths() {
        return new Object[][]{{"a", 1}, {"b", 29}, {"c", 30}};
    }

    @Test(dataProvider = "validNameLengths", groups = {"regression", "colors", "auth", "validation", "boundary"},
            description = "Name có độ dài hợp lệ được lưu nguyên vẹn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name 1, 29, 30 ký tự tạo thành công và trang sửa hiện đúng từng ký tự. Biến thể a (tên 1 ký tự Q) không chứa được mã truy vết — tìm lại màu qua Color Code ở các dòng đầu, dọn theo id.")
    public void testValidNameLengths(String variant, int length) {
        tc("AEC_COLOR_TC_015", "regression", "colors", "auth", "validation", "boundary");
        String token = newToken("TC015" + variant);
        String name = length == 1 ? "Q" : TestDataGenerator.padTo(token, 'x', length);
        String code = newCode();
        Allure.parameter("Biến thể", variant + " — " + length + " ký tự");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tạo màu với Name " + length + " ký tự", () -> list.createColor(name, code));
        Allure.step("Assert: Tạo thành công", () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save"));

        Allure.step("Assert: Trang sửa hiện đúng Name", () -> {
            ColorEditPage edit = length == 1
                    ? new ColorEditPage(driver()).openById(idOfNewColor(list, name, code))
                    : openEditOf(list, name);
            Assert.assertEquals(edit.nameValue(), name, "Ô Name");
        });
    }

    @DataProvider(name = "tooLongNames", parallel = true)
    public Object[][] tooLongNames() {
        return new Object[][]{{"a", 31}, {"b", 255}};
    }

    @Test(dataProvider = "tooLongNames", groups = {"regression", "colors", "auth", "validation", "boundary", "knownIssue"},
            description = "Name dài hơn 30 ký tự phải bị chặn kèm thông báo, không được cắt im lặng")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name 31 và 255 ký tự thì phải báo lỗi vượt quá 30 ký tự và không tạo màu. Đang lệch: hệ thống báo thành công và lưu 30 ký tự đầu (AMB-COLOR-01).")
    public void testTooLongNameRejected(String variant, int length) {
        tc("AEC_COLOR_TC_016", "regression", "colors", "auth", "validation", "boundary", "knownIssue");
        String token = newToken("TC016" + variant);
        String name = TestDataGenerator.padTo(token, 'x', length);
        Allure.parameter("Biến thể", variant + " — " + length + " ký tự");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Nhập Name " + length + " ký tự, Color Code, bấm Save", () -> {
            list.enterName(name);
            list.enterCode(newCode());
            list.saveAndWait();
        });

        Allure.step("Assert: Báo lỗi, không có màu mới", () -> {
            Assert.assertFalse(list.errorMessages().isEmpty(), "Phải có khối lỗi về độ dài Name");
            Assert.assertTrue(findRow(list, token, name) < 0 && list.rowIndexesContaining(token).isEmpty(),
                    "Không được có màu mới, thực tế " + list.names());
        });
    }

    @Test(groups = {"regression", "colors", "auth", "validation"}, description = "Name chỉ có khoảng trắng bị chặn khi tạo")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name chỉ có 3 dấu cách, Color Code hợp lệ thì trang nạp lại ở /admin/colors, khối đỏ trong thẻ Add New Color ghi The name field is required., không có thông báo xanh, ô Color Code giữ mã đã nhập.")
    public void testWhitespaceNameRejected() {
        tc("AEC_COLOR_TC_017", "regression", "colors", "auth", "validation");
        String code = newCode();
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Nhập Name toàn khoảng trắng, Color Code, bấm Save", () -> {
            list.enterName("   ");
            list.enterCode(code);
            list.saveAndWait();
        });

        Allure.step("Assert: Khối đỏ báo Name bắt buộc, Color Code giữ nguyên", () -> {
            Assert.assertEquals(list.currentUrl(), config.getBaseUrl() + "/admin/colors", "Vẫn ở danh sách");
            Assert.assertEquals(list.errorMessages(), List.of(NAME_REQUIRED), "Khối lỗi");
            Assert.assertEquals(list.codeValue(), code, "Ô Color Code giữ mã đã nhập");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "validation"}, description = "Name có khoảng trắng đầu và cuối được bỏ khi lưu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name có 3 dấu cách đầu và cuối tạo thành công; danh sách và trang sửa hiện tên đã bỏ khoảng trắng.")
    public void testNameTrimmed() {
        tc("AEC_COLOR_TC_018", "regression", "colors", "auth", "validation");
        String name = "Trim_" + newToken("TC018");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tạo màu với Name có khoảng trắng đầu và cuối", () -> list.createColor("   " + name + "   ", newCode()));
        Allure.step("Assert: Tạo thành công", () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save"));

        Allure.step("Assert: Danh sách và trang sửa hiện tên đã bỏ khoảng trắng", () -> {
            Assert.assertTrue(findRow(list, name, name) >= 0, "Danh sách phải có " + name + ", thực tế " + list.names());
            Assert.assertEquals(list.editRow(list.rowIndexOf(name)).nameValue(), name, "Ô Name");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "validation"}, description = "Color Code có khoảng trắng đầu và cuối được bỏ khi lưu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Color Code có 2 dấu cách đầu và cuối tạo thành công, trang sửa hiện mã đã bỏ khoảng trắng.")
    public void testCodeTrimmed() {
        tc("AEC_COLOR_TC_019", "regression", "colors", "auth", "validation");
        String name = "Col_" + newToken("TC019");
        String code = newCode();
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tạo màu với Color Code có khoảng trắng đầu và cuối", () -> list.createColor(name, "  " + code + "  "));
        Allure.step("Assert: Tạo thành công", () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save"));

        Allure.step("Assert: Trang sửa hiện mã đã bỏ khoảng trắng", () ->
                Assert.assertEquals(openEditOf(list, name).codeValue(), code, "Ô Color Code"));
    }

    @Test(groups = {"regression", "colors", "auth", "validation"}, description = "Dấu cách giữa Name bị xoá khi tạo")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name có dấu cách ở giữa tạo thành công và danh sách hiện tên đã xoá mọi dấu cách (giả định Q đã chốt).")
    public void testInnerSpacesRemoved() {
        tc("AEC_COLOR_TC_020", "regression", "colors", "auth", "validation");
        String token = newToken("TC020");
        String typed = "Co l " + token.replace("_", " _");
        String expected = typed.replace(" ", "");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tạo màu với Name " + typed, () -> list.createColor(typed, newCode()));
        Allure.step("Assert: Tạo thành công", () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save"));

        Allure.step("Assert: Danh sách hiện " + expected, () ->
                Assert.assertTrue(findRow(list, token, expected) >= 0, "Danh sách phải có " + expected + ", thực tế " + list.names()));
    }

    @Test(groups = {"regression", "colors", "auth"}, description = "Tạo được hai màu trùng tên, khác mã")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tạo hai màu cùng Name, khác Color Code thì cả hai đều thành công và tìm Name ra đúng 2 dòng.")
    public void testDuplicateNameAllowed() {
        tc("AEC_COLOR_TC_021", "regression", "colors", "auth");
        String name = "Dup_" + newToken("TC021");
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());

        Allure.step("Act: Tạo thêm màu cùng Name, mã khác", () -> list.createColor(name, newCode()));
        Allure.step("Assert: Tạo thành công, không báo trùng", () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save"));

        Allure.step("Assert: Tìm Name ra đúng 2 dòng", () -> {
            list.search(name);
            Assert.assertEquals(list.names(), List.of(name, name), "Kết quả tìm");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "validation", "localization"}, description = "Name tiếng Việt có dấu được lưu đúng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name tiếng Việt có dấu tạo thành công; danh sách và trang sửa hiện đúng, không thành ? hay ô vuông.")
    public void testVietnameseName() {
        tc("AEC_COLOR_TC_022", "regression", "colors", "auth", "validation", "localization");
        String token = newToken("TC022");
        String name = "MàuĐỏ_" + token;
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tạo màu với Name " + name, () -> list.createColor(name, newCode()));
        Allure.step("Assert: Tạo thành công", () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save"));

        Allure.step("Assert: Danh sách và trang sửa hiện đúng tên", () -> {
            int row = findRow(list, token, name);
            Assert.assertTrue(row >= 0, "Danh sách phải có " + name + ", thực tế " + list.names());
            Assert.assertEquals(list.editRow(row).nameValue(), name, "Ô Name");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "validation"}, description = "Color Code chỉ có khoảng trắng bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Color Code chỉ có 3 dấu cách thì khối đỏ ghi The code field is required., không có thông báo xanh, ô Name giữ tên, không có màu mới.")
    public void testWhitespaceCodeRejected() {
        tc("AEC_COLOR_TC_023", "regression", "colors", "auth", "validation");
        String name = "Col_" + newToken("TC023");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Nhập Name, Color Code toàn khoảng trắng, bấm Save", () -> {
            list.enterName(name);
            list.enterCode("   ");
            list.saveAndWait();
        });

        Allure.step("Assert: Khối đỏ báo Color Code bắt buộc, Name giữ nguyên, không có màu mới", () -> {
            Assert.assertEquals(list.errorMessages(), List.of(CODE_REQUIRED), "Khối lỗi");
            Assert.assertEquals(list.nameValue(), name, "Ô Name giữ tên đã nhập");
            Assert.assertEquals(findRow(list, name, name), -1, "Không được có màu mới");
        });
    }

    @DataProvider(name = "takenCodes", parallel = true)
    public Object[][] takenCodes() {
        return new Object[][]{{"a", false}, {"b", true}};
    }

    @Test(dataProvider = "takenCodes", groups = {"regression", "colors", "auth", "validation"},
            description = "Mã màu đã dùng bị từ chối, kể cả khác hoa thường")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra tạo màu mới với Color Code trùng mã của màu test có sẵn (cùng kiểu chữ / khác hoa thường) thì khối đỏ ghi The code has already been taken., hai ô giữ dữ liệu, không có màu mới.")
    public void testTakenCodeRejected(String variant, boolean upperCase) {
        tc("AEC_COLOR_TC_024", "regression", "colors", "auth", "validation");
        String token = newToken("TC024" + variant);
        String existingCode = newCode();
        String typedCode = upperCase ? existingCode.toUpperCase() : existingCode;
        String newName = "ColX_" + token;
        Allure.parameter("Biến thể", variant + " — " + typedCode);
        ColorListPage list = openColorsAsAdmin();
        createColor(list, "Col_" + token, existingCode);

        Allure.step("Act: Nhập Name mới, Color Code " + typedCode + ", bấm Save", () -> {
            list.enterName(newName);
            list.enterCode(typedCode);
            list.saveAndWait();
        });

        Allure.step("Assert: Khối đỏ báo mã đã dùng, hai ô giữ dữ liệu, không có màu mới", () -> {
            Assert.assertEquals(list.errorMessages(), List.of(CODE_TAKEN), "Khối lỗi");
            Assert.assertEquals(list.nameValue(), newName, "Ô Name");
            Assert.assertEquals(list.codeValue(), typedCode, "Ô Color Code");
            Assert.assertEquals(findRow(list, newName, newName), -1, "Không được có màu mới");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "validation", "boundary", "knownIssue"},
            description = "Color Code dài đúng 255 ký tự được lưu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Color Code 255 ký tự tạo thành công và trang sửa hiện đủ 255 ký tự. Đang lệch: mã bị cắt còn 10 ký tự mà vẫn báo thành công (run_1793589946).")
    public void testCode255Saved() {
        tc("AEC_COLOR_TC_025", "regression", "colors", "auth", "validation", "boundary", "knownIssue");
        String name = "Col_" + newToken("TC025");
        String code = TestDataGenerator.padTo("#" + randomHex(9), 'a', 255);
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tạo màu với Color Code 255 ký tự", () -> list.createColor(name, code));
        Allure.step("Assert: Tạo thành công", () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save"));

        Allure.step("Assert: Trang sửa hiện đủ 255 ký tự", () ->
                Assert.assertEquals(openEditOf(list, name).codeValue(), code, "Ô Color Code"));
    }

    @Test(groups = {"regression", "colors", "auth", "validation", "boundary"}, description = "Color Code dài hơn 255 ký tự bị từ chối")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Color Code 256 ký tự thì khối đỏ ghi The code may not be greater than 255 characters., không có màu mới.")
    public void testCode256Rejected() {
        tc("AEC_COLOR_TC_026", "regression", "colors", "auth", "validation", "boundary");
        String name = "Col_" + newToken("TC026");
        String code = TestDataGenerator.padTo("#" + randomHex(9), 'a', 256);
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Nhập Name, Color Code 256 ký tự, bấm Save", () -> {
            list.enterName(name);
            list.enterCode(code);
            list.saveAndWait();
        });

        Allure.step("Assert: Khối đỏ báo mã quá dài, không có màu mới", () -> {
            Assert.assertEquals(list.errorMessages(), List.of(CODE_TOO_LONG), "Khối lỗi");
            Assert.assertEquals(findRow(list, name, name), -1, "Không được có màu mới");
        });
    }

    @DataProvider(name = "validHexCodes", parallel = true)
    public Object[][] validHexCodes() {
        return new Object[][]{{"a", "short"}, {"b", "upper"}};
    }

    @Test(dataProvider = "validHexCodes", groups = {"regression", "colors", "auth", "validation"},
            description = "Mã màu hex hợp lệ được lưu nguyên văn")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra mã hex 3 ký tự và mã chữ hoa tạo thành công, trang sửa hiện đúng mã đã nhập. Mã 3 ký tự chỉ có 4096 giá trị — trùng mã có sẵn thì đổi mã khác, thử tối đa 5 lần.")
    public void testValidHexCodeSaved(String variant, String kind) {
        tc("AEC_COLOR_TC_027", "regression", "colors", "auth", "validation");
        String name = "Col_" + newToken("TC027" + variant);
        Allure.parameter("Biến thể", variant + " — " + kind);
        ColorListPage list = openColorsAsAdmin();

        String code = Allure.step("Act: Tạo màu với mã " + kind, () -> {
            for (int attempt = 0; attempt < 5; attempt++) {
                String candidate = "short".equals(kind) ? "#" + randomHex(3) : newCode().toUpperCase();
                list.open();
                list.createColor(name, candidate);
                if (!list.errorMessages().contains(CODE_TAKEN)) {
                    return candidate;
                }
            }
            throw new AssertionError("5 mã thử đều đã có người dùng");
        });
        Allure.step("Assert: Tạo thành công", () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save"));

        Allure.step("Assert: Trang sửa hiện đúng mã " + code, () ->
                Assert.assertEquals(openEditOf(list, name).codeValue(), code, "Ô Color Code"));
    }

    @DataProvider(name = "invalidCodes", parallel = true)
    public Object[][] invalidCodes() {
        return new Object[][]{{"a", "red" + randomHex(4)}, {"b", "c28" + randomHex(3)}, {"c", "#GG" + randomHex(4)}};
    }

    @Test(dataProvider = "invalidCodes", groups = {"regression", "colors", "auth", "validation", "knownIssue"},
            description = "Color Code sai định dạng phải bị chặn")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Color Code là tên màu, thiếu #, chữ không phải hex thì phải báo lỗi định dạng và không tạo màu. Đang lệch: hệ thống báo thành công và lưu nguyên văn (AMB-COLOR-03). Thêm hậu tố ngẫu nhiên vào mã để mã không trùng giữa các lần chạy.")
    public void testInvalidCodeRejected(String variant, String code) {
        tc("AEC_COLOR_TC_028", "regression", "colors", "auth", "validation", "knownIssue");
        String name = "Col_" + newToken("TC028" + variant);
        Allure.parameter("Biến thể", variant + " — " + code);
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Nhập Name, Color Code " + code + ", bấm Save", () -> {
            list.enterName(name);
            list.enterCode(code);
            list.saveAndWait();
        });

        Allure.step("Assert: Báo lỗi định dạng, không có màu mới", () -> {
            Assert.assertFalse(list.errorMessages().isEmpty(), "Phải có khối lỗi về định dạng mã màu");
            Assert.assertEquals(findRow(list, name, name), -1, "Không được có màu mới");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "reliability"}, description = "Bấm Save liên tiếp chỉ tạo một màu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra bấm đúp nút Save của form Add New Color thì không ra trang lỗi và chỉ có đúng 1 màu được tạo.")
    public void testDoubleClickSaveCreatesOne() {
        tc("AEC_COLOR_TC_041", "regression", "colors", "auth", "reliability");
        String name = "Dbl_" + newToken("TC041");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Nhập Name, Color Code rồi bấm đúp Save", () -> {
            list.enterName(name);
            list.enterCode(newCode());
            list.doubleClickSave();
        });

        Allure.step("Assert: Không trang lỗi, đúng 1 dòng mang Name", () -> {
            Assert.assertFalse(list.isServerErrorPage(), "Không được ra trang lỗi");
            list.open();
            list.search(name);
            Assert.assertEquals(list.rowIndexesContaining(name).size(), 1, "Không được tạo trùng màu");
        });
    }

    @DataProvider(name = "injectionNames", parallel = true)
    public Object[][] injectionNames() {
        return new Object[][]{{"a", "<script>alert(1)</script>", "T44a"}, {"b", "<b>B</b>", "T44b"}, {"c", "'OR'1'='1--", "44c"}};
    }

    @Test(dataProvider = "injectionNames", groups = {"regression", "colors", "auth", "security"},
            description = "Mã script và chuỗi SQL ở Name được lưu như chữ, không chạy")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name chứa script, thẻ <b>, chuỗi SQL tạo thành công; danh sách và trang sửa không bật hộp thoại, hiện nguyên văn dạng chữ. Biến thể a dài 25 ký tự không chứa được mã truy vết — tìm lại theo Color Code, dọn theo id.")
    public void testInjectionNameNotExecuted(String variant, String payload, String testName) {
        tc("AEC_COLOR_TC_044", "regression", "colors", "auth", "security");
        boolean traceable = !"a".equals(variant);
        String token = traceable ? newToken(testName) : "";
        String name = payload + token;
        String code = newCode();
        Allure.parameter("Biến thể", variant + " — " + name);
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tạo màu với Name chứa mã", () -> list.createColor(name, code));
        Allure.step("Assert: Tạo thành công, không trang lỗi", () -> {
            Assert.assertFalse(list.isServerErrorPage(), "Không được ra trang lỗi");
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save");
        });

        Allure.step("Assert: Danh sách — không hộp thoại, tên nguyên văn", () -> {
            Assert.assertFalse(list.isAlertOpen(), "Không được bật hộp thoại ở danh sách");
            Assert.assertTrue(list.rowIndexOf(name) >= 0, "Danh sách phải hiện nguyên văn " + name + ", thực tế " + list.names());
        });
        Allure.step("Assert: Trang sửa — không hộp thoại, tên nguyên văn", () -> {
            ColorEditPage edit;
            if (traceable) {
                // Tìm theo mã truy vết, không theo cả tên — chuỗi có thẻ HTML dễ bị tường lửa chặn ở ô tìm
                int row = findRow(list, token, name);
                Assert.assertTrue(row >= 0, "Phải tìm thấy màu test " + name);
                edit = list.editRow(row);
            } else {
                edit = new ColorEditPage(driver()).openById(idOfNewColor(list, name, code));
            }
            Assert.assertFalse(edit.isAlertOpen(), "Không được bật hộp thoại ở trang sửa");
            Assert.assertEquals(edit.nameValue(), name, "Ô Name");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "localization", "knownIssue"}, description = "Emoji trong Name hiển thị và lưu đúng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name có emoji tạo thành công, danh sách và trang sửa hiện đúng emoji. Đang lệch: emoji bị thay bằng ? (AMB-COLOR-07).")
    public void testEmojiName() {
        tc("AEC_COLOR_TC_047", "regression", "colors", "auth", "localization", "knownIssue");
        String token = newToken("TC047");
        String name = "Màu🎨" + token;
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Tạo màu với Name có emoji", () -> list.createColor(name, newCode()));
        Allure.step("Assert: Tạo thành công", () ->
                Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi Save"));

        Allure.step("Assert: Danh sách hiện đúng emoji", () -> {
            list.search(token);
            Assert.assertEquals(list.names(), List.of(name), "Tên ở danh sách");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "accessibility"}, description = "Tạo màu bằng phím Enter")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể a: nhập Name, Color Code rồi nhấn Enter ở ô Color Code thì màu được tạo và nằm ở đầu bảng.")
    public void testKeyboardCreate() {
        tc("AEC_COLOR_TC_048", "regression", "colors", "auth", "accessibility");
        Allure.parameter("Biến thể", "a — Enter");
        String name = "Kb_" + newToken("TC048a");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Act: Nhập Name, Color Code rồi nhấn Enter", () -> list.createByEnterInCode(name, newCode()));

        Allure.step("Assert: Tạo thành công, màu mới ở đầu bảng", () -> {
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Thông báo sau khi nhấn Enter");
            int row = list.rowIndexOf(name);
            Assert.assertTrue(row >= 0 && row < TOP_ROWS, "Màu mới phải ở đầu bảng, thực tế dòng " + (row + 1));
        });
    }

    @Test(groups = {"regression", "colors", "auth", "accessibility"}, description = "Chuyển focus Name → Color Code → Save bằng Tab")
    @Severity(SeverityLevel.MINOR)
    @Description("Biến thể b: bấm vào ô Name rồi nhấn Tab 2 lần thì focus lần lượt sang ô Color Code, nút Save; ô và nút khi được focus có viền nhìn thấy được.")
    public void testKeyboardTabOrder() {
        tc("AEC_COLOR_TC_048", "regression", "colors", "auth", "accessibility");
        Allure.parameter("Biến thể", "b — Tab");
        ColorListPage list = openColorsAsAdmin();

        Allure.step("Assert: Tab từ ô Name tới Color Code rồi Save", () ->
                Assert.assertEquals(list.tabSequenceFromName(2), List.of("code", "Save"), "Phần tử nhận focus sau mỗi lần Tab"));
        Allure.step("Assert: Ô Name, Color Code và nút Save có viền khi được focus", () -> {
            Assert.assertEquals(list.fieldsWithoutVisibleFocus(), List.of(), "Ô không đổi gì khi được focus");
            Assert.assertTrue(list.saveShowsFocusWhenTabbed(), "Nút Save phải đổi kiểu khi được focus bằng Tab");
        });
    }
}
