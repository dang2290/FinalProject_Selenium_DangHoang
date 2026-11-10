package com.aec.tests;

import com.aec.pages.ColorEditPage;
import com.aec.pages.ColorListPage;
import com.aec.pages.DashboardPage;
import com.aec.utils.TestDataGenerator;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.openqa.selenium.WindowType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/** Trang Color Information: giao diện, sửa Name / Color Code, validation, hết phiên. TC ở docs/testcases/colors/web/test_cases_colors_web.md. */
@Feature("Colors")
@Story("Sửa màu")
public class ColorEditTest extends ColorTestBase {

    @Test(groups = {"smoke", "colors", "auth", "ui"}, description = "Trang sửa màu đủ thành phần, không có tab ngôn ngữ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra nút Edit mở /admin/colors/edit/<id>?lang=en với tiêu đề Color Information, sidebar tô sáng Colors, ô Name và Color Code đúng dữ liệu màu test, có nút Save và không có tab ngôn ngữ.")
    public void testEditPageElements() {
        tc("AEC_COLOR_TC_002", "smoke", "colors", "auth", "ui");
        String name = "Col_" + newToken("TC002");
        String code = newCode();
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, code);

        ColorEditPage edit = Allure.step("Act: Tìm màu test, bấm nút Edit", () -> openEditOf(list, name));

        Allure.step("Assert: Bảng kiểm trang sửa", () -> {
            Assert.assertTrue(edit.currentUrl().matches(".*/admin/colors/edit/\\d+\\?lang=en$"), "Đường dẫn: " + edit.currentUrl());
            Assert.assertTrue(edit.isTitleDisplayed(), "Tiêu đề Color Information");
            Assert.assertTrue(edit.isColorsMenuActive(), "Mục Colors ở sidebar phải được tô sáng");
            Assert.assertEquals(edit.nameValue(), name, "Ô Name");
            Assert.assertEquals(edit.codeValue(), code, "Ô Color Code");
            Assert.assertEquals(edit.saveButtonText(), "Save", "Nút Save");
            Assert.assertFalse(edit.hasLanguageTabs(), "Không được có tab ngôn ngữ");
        });
    }

    @Test(groups = {"smoke", "colors", "auth", "criticalPath"}, description = "Sửa Name và Color Code, ở lại trang sửa")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra đổi Name và Color Code rồi Save thì báo Color has been updated successfully, vẫn ở trang sửa với giá trị mới; danh sách có tên mới, tìm tên cũ ra Nothing found.")
    public void testEditNameAndCode() {
        tc("AEC_COLOR_TC_006", "smoke", "colors", "auth", "criticalPath");
        String token = newToken("TC006");
        String oldName = "Col_" + token;
        String newName = "ColEd_" + token;
        String newCode = newCode();
        ColorListPage list = openColorsAsAdmin();
        createColor(list, oldName, newCode());
        ColorEditPage edit = openEditOf(list, oldName);

        Allure.step("Act: Đổi Name, Color Code rồi bấm Save", () -> {
            edit.enterName(newName);
            edit.enterCode(newCode);
            edit.saveAndWait();
        });

        Allure.step("Assert: Cập nhật thành công, ở lại trang sửa với giá trị mới", () -> {
            Assert.assertEquals(edit.getNotifyMessage(), UPDATED, "Thông báo sau khi Save");
            Assert.assertTrue(edit.isOnEditPage(), "Phải ở lại trang sửa: " + edit.currentUrl());
            Assert.assertEquals(edit.nameValue(), newName, "Ô Name");
            Assert.assertEquals(edit.codeValue(), newCode, "Ô Color Code");
        });

        Allure.step("Assert: Danh sách có tên mới, không còn tên cũ", () -> {
            Assert.assertTrue(findRow(list, newName, newName) >= 0, "Phải tìm thấy tên mới");
            list.search(oldName);
            Assert.assertTrue(list.isNothingFoundShown(), "Tìm tên cũ phải ra Nothing found, thực tế " + list.names());
        });
    }

    @Test(groups = {"regression", "colors", "auth", "validation"}, description = "Không lưu được khi xoá trống Name ở trang sửa")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra xoá hết Name ở trang sửa rồi Save thì trình duyệt chặn: không gửi form, con trỏ ở ô Name; màu test vẫn giữ tên cũ.")
    public void testEditEmptyNameBlocked() {
        tc("AEC_COLOR_TC_029", "regression", "colors", "auth", "validation");
        String name = "Col_" + newToken("TC029");
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());
        ColorEditPage edit = openEditOf(list, name);

        Allure.step("Act: Xoá hết Name, bấm Save", () -> {
            edit.enterName("");
            edit.markPage();
            edit.clickSave();
        });

        Allure.step("Assert: Trình duyệt chặn ở ô Name", () -> {
            Assert.assertFalse(edit.nameValidationMessage().isEmpty(), "Phải có bong bóng nhắc nhập");
            Assert.assertTrue(edit.isSamePageInstance(), "Không được gửi form");
            Assert.assertTrue(edit.isNameFocused(), "Con trỏ phải nằm trong ô Name");
        });
        Allure.step("Assert: Màu test vẫn giữ tên cũ", () ->
                Assert.assertTrue(findRow(list, name, name) >= 0, "Phải còn màu mang tên cũ"));
    }

    @Test(groups = {"regression", "colors", "auth", "validation", "knownIssue"}, description = "Sửa Name thành chỉ có khoảng trắng phải báo lỗi, giữ tên cũ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra thay Name bằng 3 dấu cách rồi Save thì khối đỏ ghi The name field is required. và Name vẫn là tên cũ. Đang lệch: hệ thống báo cập nhật thành công và lưu tên rỗng (AMB-COLOR-04) — dọn theo id.")
    public void testEditWhitespaceNameRejected() {
        tc("AEC_COLOR_TC_030", "regression", "colors", "auth", "validation", "knownIssue");
        String name = "Col_" + newToken("TC030");
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());
        ColorEditPage edit = openEditOf(list, name);
        String id = edit.colorId();
        trackColorId(id);

        Allure.step("Act: Thay Name bằng khoảng trắng, bấm Save", () -> {
            edit.enterName("   ");
            edit.saveAndWait();
        });

        Allure.step("Assert: Khối đỏ báo Name bắt buộc, không báo cập nhật", () ->
                Assert.assertEquals(edit.errorMessages(), List.of(NAME_REQUIRED), "Khối lỗi"));
        Allure.step("Assert: Mở lại trang sửa — Name vẫn là tên cũ", () ->
                Assert.assertEquals(edit.openById(id).nameValue(), name, "Ô Name"));
    }

    @DataProvider(name = "badEditCodes", parallel = true)
    public Object[][] badEditCodes() {
        return new Object[][]{{"a", "taken"}, {"b", "blank"}};
    }

    @Test(dataProvider = "badEditCodes", groups = {"regression", "colors", "auth", "validation"},
            description = "Sửa Color Code sai bị từ chối, giữ mã cũ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ở trang sửa màu Y, đổi Color Code thành mã của màu X (khối đỏ The code has already been taken.) hoặc toàn khoảng trắng (khối đỏ The code field is required.); không có thông báo xanh, mở lại trang sửa mã vẫn là mã cũ của Y.")
    public void testEditBadCodeRejected(String variant, String kind) {
        tc("AEC_COLOR_TC_031", "regression", "colors", "auth", "validation");
        Allure.parameter("Biến thể", variant + " — " + kind);
        String token = newToken("TC031" + variant);
        String codeX = newCode();
        String codeY = newCode();
        String nameY = "ColY_" + token;
        ColorListPage list = openColorsAsAdmin();
        createColor(list, "ColX_" + token, codeX);
        createColor(list, nameY, codeY);
        ColorEditPage edit = openEditOf(list, nameY);
        String id = edit.colorId();

        Allure.step("Act: Đổi Color Code của màu Y, bấm Save", () -> {
            edit.enterCode("taken".equals(kind) ? codeX : "   ");
            edit.saveAndWait();
        });

        Allure.step("Assert: Khối đỏ báo lỗi mã", () ->
                Assert.assertEquals(edit.errorMessages(), List.of("taken".equals(kind) ? CODE_TAKEN : CODE_REQUIRED), "Khối lỗi"));
        Allure.step("Assert: Mở lại trang sửa — mã vẫn là mã cũ của Y", () ->
                Assert.assertEquals(edit.openById(id).codeValue(), codeY, "Ô Color Code"));
    }

    @Test(groups = {"regression", "colors", "auth"}, description = "Đổi tên, giữ nguyên mã của chính màu đó")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra đổi Name, giữ nguyên Color Code của chính màu đó rồi Save thì báo cập nhật thành công, không báo mã đã dùng, Color Code giữ nguyên.")
    public void testRenameKeepOwnCode() {
        tc("AEC_COLOR_TC_032", "regression", "colors", "auth");
        String token = newToken("TC032");
        String code = newCode();
        ColorListPage list = openColorsAsAdmin();
        createColor(list, "Col_" + token, code);
        ColorEditPage edit = openEditOf(list, "Col_" + token);

        Allure.step("Act: Đổi Name, giữ Color Code, bấm Save", () -> {
            edit.enterName("ColEd_" + token);
            edit.saveAndWait();
        });

        Allure.step("Assert: Cập nhật thành công, mã giữ nguyên", () -> {
            Assert.assertEquals(edit.getNotifyMessage(), UPDATED, "Thông báo sau khi Save");
            Assert.assertEquals(edit.errorMessages(), List.of(), "Không được có khối lỗi");
            Assert.assertEquals(edit.codeValue(), code, "Ô Color Code");
        });
    }

    @Test(groups = {"regression", "colors", "auth", "validation", "boundary", "knownIssue"},
            description = "Sửa Name dài hơn 30 ký tự phải bị chặn, không được cắt im lặng")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra thay Name bằng chuỗi 31 ký tự rồi Save thì phải báo lỗi vượt quá 30 ký tự, Name vẫn là tên cũ. Đang lệch: hệ thống báo thành công và lưu 30 ký tự đầu (AMB-COLOR-01).")
    public void testEditTooLongNameRejected() {
        tc("AEC_COLOR_TC_033", "regression", "colors", "auth", "validation", "boundary", "knownIssue");
        String token = newToken("TC033");
        String name = "Col_" + token;
        ColorListPage list = openColorsAsAdmin();
        createColor(list, name, newCode());
        ColorEditPage edit = openEditOf(list, name);
        String id = edit.colorId();

        Allure.step("Act: Thay Name bằng chuỗi 31 ký tự, bấm Save", () -> {
            edit.enterName(TestDataGenerator.padTo(token, 'x', 31));
            edit.saveAndWait();
        });

        Allure.step("Assert: Báo lỗi, không báo cập nhật", () ->
                Assert.assertFalse(edit.errorMessages().isEmpty(), "Phải có khối lỗi về độ dài Name"));
        Allure.step("Assert: Mở lại trang sửa — Name vẫn là tên cũ", () ->
                Assert.assertEquals(edit.openById(id).nameValue(), name, "Ô Name"));
    }

    @Test(groups = {"regression", "colors", "auth", "validation"}, description = "Dấu cách giữa Name bị xoá khi sửa")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra thay Name bằng chuỗi có dấu cách ở giữa rồi Save thì báo cập nhật thành công và ô Name là chuỗi đã xoá mọi dấu cách (giả định Q đã chốt).")
    public void testEditInnerSpacesRemoved() {
        tc("AEC_COLOR_TC_034", "regression", "colors", "auth", "validation");
        String token = newToken("TC034");
        String typed = "Ed " + token.replace("_", " _");
        ColorListPage list = openColorsAsAdmin();
        createColor(list, "Col_" + token, newCode());
        ColorEditPage edit = openEditOf(list, "Col_" + token);

        Allure.step("Act: Thay Name bằng " + typed + ", bấm Save", () -> {
            edit.enterName(typed);
            edit.saveAndWait();
        });

        Allure.step("Assert: Cập nhật thành công, dấu cách đã bị xoá", () -> {
            Assert.assertEquals(edit.getNotifyMessage(), UPDATED, "Thông báo sau khi Save");
            Assert.assertEquals(edit.nameValue(), typed.replace(" ", ""), "Ô Name");
        });
    }

    @DataProvider(name = "expiredSessionForms", parallel = true)
    public Object[][] expiredSessionForms() {
        return new Object[][]{{"a", "create"}, {"b", "edit"}};
    }

    @Test(dataProvider = "expiredSessionForms", groups = {"regression", "colors", "auth", "security"},
            description = "Hết phiên đăng nhập khi đang nhập thì không lưu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tab 1 đang nhập ở form Add New Color / trang sửa màu, tab 2 đăng xuất, tab 1 bấm Save thì ra 419 PAGE EXPIRED hoặc về /login; đăng nhập lại thấy dữ liệu không đổi.")
    public void testExpiredSessionDoesNotSave(String variant, String formType) {
        tc("AEC_COLOR_TC_042", "regression", "colors", "auth", "security");
        Allure.parameter("Biến thể", variant + " — " + formType);
        String token = newToken("TC042" + variant);
        String originalName = "Col_" + token;
        String typedName = ("create".equals(formType) ? "ColSs_" : "ColEd_") + token;
        ColorListPage list = openColorsAsAdmin();
        ColorEditPage edit = new ColorEditPage(driver());

        Allure.step("Arrange: Tab 1 mở form " + formType + ", nhập dữ liệu", () -> {
            if ("create".equals(formType)) {
                list.open();
                list.enterName(typedName);
                list.enterCode(newCode());
            } else {
                createColor(list, originalName, newCode());
                openEditOf(list, originalName).enterName(typedName);
            }
        });
        String formTab = driver().getWindowHandle();

        Allure.step("Act: Tab 2 đăng xuất", () -> {
            driver().switchTo().newWindow(WindowType.TAB);
            new DashboardPage(driver()).open().logout();
            driver().close();
            driver().switchTo().window(formTab);
        });

        Allure.step("Act: Tab 1 bấm Save", () -> {
            if ("create".equals(formType)) {
                list.clickSave();
            } else {
                edit.clickSave();
            }
        });

        Allure.step("Assert: 419 PAGE EXPIRED hoặc về /login", () -> {
            edit.waitForPath("/login");
            Assert.assertTrue(edit.isPageExpired() || "/login".equals(edit.currentPath()),
                    "Gửi form sau khi hết phiên phải bị chặn: " + edit.currentUrl());
        });

        Allure.step("Assert: Đăng nhập lại — dữ liệu không đổi", () -> {
            loginAsAdmin();
            Assert.assertEquals(findRow(list, typedName, typedName), -1, "Không được có màu mang tên vừa nhập");
            if (!"create".equals(formType)) {
                Assert.assertTrue(findRow(list, originalName, originalName) >= 0, "Màu test phải giữ tên cũ");
            }
        });
    }
}
