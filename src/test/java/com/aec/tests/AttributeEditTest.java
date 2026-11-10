package com.aec.tests;

import com.aec.pages.AttributeEditPage;
import com.aec.pages.AttributeListPage;
import com.aec.pages.DashboardPage;
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

/** Trang Attribute Information: giao diện, đổi tên, validation, đa ngôn ngữ, hết phiên. TC ở docs/testcases/attribute/web/test_cases_attribute_web.md. */
@Feature("Attribute")
@Story("Sửa thuộc tính")
public class AttributeEditTest extends AttributeTestBase {

    @Test(groups = {"smoke", "attribute", "auth", "ui"}, description = "Trang sửa thuộc tính hiển thị đủ thành phần, tab English được chọn mặc định")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra nút Edit mở /admin/attributes/edit/<id>?lang=en với tiêu đề Attribute Information, 2 tab English (đang chọn) / Vietnamese, ô Name đúng tên, nút Save, biểu tượng cạnh Name có chú thích Translatable.")
    public void testEditPageElements() {
        tc("AEC_ATTR_TC_002", "smoke", "attribute", "auth", "ui");
        String name = "AutoAttr_" + newToken("TC002");
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);

        AttributeEditPage edit = Allure.step("Act: Bấm nút Edit ở dòng thuộc tính test", () -> openEditOf(list, name));

        Allure.step("Assert: Bảng kiểm trang sửa", () -> {
            Assert.assertTrue(edit.currentUrl().matches(".*/admin/attributes/edit/\\d+\\?lang=en$"), "Thanh địa chỉ: " + edit.currentUrl());
            Assert.assertTrue(edit.isTitleDisplayed(), "Tiêu đề Attribute Information");
            Assert.assertEquals(edit.languageTabs(), List.of("English", "Vietnamese"), "Hai tab");
            Assert.assertEquals(edit.activeTab(), "English", "Tab đang chọn");
            Assert.assertEquals(edit.nameValue(), name, "Ô Name");
            Assert.assertEquals(edit.saveButtonText(), "Save", "Nút Save");
        });

        Allure.step("Assert: Rê chuột lên biểu tượng cạnh Name hiện Translatable", () ->
                Assert.assertEquals(edit.translatableTooltip(), "Translatable", "Chú thích biểu tượng ngôn ngữ"));
    }

    @Test(groups = {"smoke", "attribute", "auth"}, description = "Sửa Name thuộc tính, ở lại trang sửa và danh sách hiện tên mới")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra đổi Name rồi Save thì báo cập nhật thành công, vẫn ở trang sửa với Name mới; danh sách có đúng 1 dòng tên mới, không còn tên cũ.")
    public void testRenameAttribute() {
        tc("AEC_ATTR_TC_006", "smoke", "attribute", "auth");
        String token = newToken("TC006");
        String oldName = "AutoAttr_" + token;
        String newName = "AutoAttrEd_" + token;
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, oldName);
        AttributeEditPage edit = openEditOf(list, oldName);

        Allure.step("Act: Đổi Name rồi Save", () -> {
            edit.enterName(newName);
            edit.saveAndWait();
        });

        Allure.step("Assert: Thông báo cập nhật, vẫn ở trang sửa với Name mới", () -> {
            Assert.assertEquals(edit.getNotifyMessage(), UPDATED, "Thông báo sau khi sửa");
            Assert.assertTrue(edit.isOnEditPage(), "Phải vẫn ở trang sửa: " + edit.currentUrl());
            Assert.assertEquals(edit.nameValue(), newName, "Ô Name sau khi lưu");
        });

        Allure.step("Assert: Danh sách có tên mới, không còn tên cũ", () -> {
            list.open();
            Assert.assertEquals(list.rowIndexesContaining(token).size(), 1, "Số dòng mang mã test");
            Assert.assertTrue(list.rowIndexOf(newName) >= 0, "Phải có dòng mang tên mới");
            Assert.assertEquals(list.rowIndexOf(oldName), -1, "Không được còn dòng mang tên cũ");
        });
    }

    @Test(groups = {"regression", "attribute", "auth", "validation"}, description = "Không lưu được khi xoá trống Name ở trang sửa")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra xoá hết Name rồi Save thì trình duyệt chặn, trang không nạp lại, con trỏ ở ô Name; thuộc tính giữ tên cũ.")
    public void testEditWithEmptyNameBlocked() {
        tc("AEC_ATTR_TC_019", "regression", "attribute", "auth", "validation");
        String name = "AutoAttr_" + newToken("TC019");
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeEditPage edit = openEditOf(list, name);

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

        Allure.step("Assert: Thuộc tính giữ Name cũ", () ->
                Assert.assertTrue(findRow(list, name) >= 0, "Thuộc tính test phải giữ tên cũ"));
    }

    @Test(groups = {"regression", "attribute", "auth", "validation", "knownIssue"}, description = "Sửa Name thành chỉ có khoảng trắng phải báo lỗi, giữ tên cũ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra thay Name bằng 3 dấu cách rồi Save thì không cập nhật, mở lại trang sửa vẫn là tên cũ. Đang lệch: hệ thống lưu tên rỗng (AMB-ATTR-02) — test đặt lại tên cũ trước khi dọn dữ liệu.")
    public void testEditWithBlankNameKeepsOldName() {
        tc("AEC_ATTR_TC_020", "regression", "attribute", "auth", "validation", "knownIssue");
        String name = "AutoAttr_" + newToken("TC020");
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        String editUrl = openEditOf(list, name).currentUrl();
        AttributeEditPage edit = new AttributeEditPage(driver());

        try {
            Allure.step("Act: Thay Name bằng 3 dấu cách rồi Save", () -> {
                edit.enterName("   ");
                edit.saveAndWait();
            });

            Allure.step("Assert: Mở lại trang sửa, Name vẫn là tên cũ", () ->
                    Assert.assertEquals(edit.openUrl(editUrl).nameValue(), name, "Name sau khi lưu chuỗi trắng"));
        } finally {
            Allure.step("Dọn dữ liệu: đặt lại tên cũ nếu tên đã bị lưu rỗng", () -> {
                if (!name.equals(edit.openUrl(editUrl).nameValue())) {
                    edit.enterName(name);
                    edit.saveAndWait();
                }
            });
        }
    }

    @Test(groups = {"regression", "attribute", "auth", "localization"}, description = "Name tiếng Việt lưu riêng, tên tiếng Anh và danh sách giữ nguyên")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ở tab Vietnamese (?lang=vn, ô Name có sẵn tên tiếng Anh) đổi Name rồi Save thì vẫn ở tab Vietnamese với tên mới; tab English và danh sách vẫn là tên tiếng Anh.")
    public void testVietnameseNameKeptSeparately() {
        tc("AEC_ATTR_TC_021", "regression", "attribute", "auth", "localization");
        String token = newToken("TC021");
        String name = "AutoAttr_" + token;
        String vietnameseName = "Thuộc tính thử " + token;
        AttributeListPage list = openAttributesAsAdmin();
        createAttribute(list, name);
        AttributeEditPage edit = openEditOf(list, name);

        Allure.step("Act: Bấm tab Vietnamese", () -> edit.clickTab("Vietnamese"));
        Allure.step("Assert: ?lang=vn, ô Name có sẵn tên tiếng Anh", () -> {
            Assert.assertTrue(edit.currentUrl().endsWith("lang=vn"), "Thanh địa chỉ: " + edit.currentUrl());
            Assert.assertEquals(edit.nameValue(), name, "Ô Name ở tab Vietnamese");
        });

        Allure.step("Act: Đổi Name tiếng Việt rồi Save", () -> {
            edit.enterName(vietnameseName);
            edit.saveAndWait();
        });
        Allure.step("Assert: Cập nhật thành công, vẫn ở tab Vietnamese với tên tiếng Việt", () -> {
            Assert.assertEquals(edit.getNotifyMessage(), UPDATED, "Thông báo sau khi sửa");
            Assert.assertEquals(edit.activeTab(), "Vietnamese", "Tab đang chọn");
            Assert.assertEquals(edit.nameValue(), vietnameseName, "Ô Name ở tab Vietnamese");
        });

        Allure.step("Assert: Tab English vẫn là tên tiếng Anh", () -> {
            edit.clickTab("English");
            Assert.assertEquals(edit.nameValue(), name, "Ô Name ở tab English");
        });
        Allure.step("Assert: Danh sách vẫn hiện tên tiếng Anh", () ->
                Assert.assertTrue(findRow(list, name) >= 0, "Danh sách phải hiện tên tiếng Anh"));
    }

    @DataProvider(name = "expiredSessionForms", parallel = true)
    public Object[][] expiredSessionForms() {
        return new Object[][]{{"a", "create"}, {"b", "edit"}};
    }

    @Test(dataProvider = "expiredSessionForms", groups = {"regression", "attribute", "auth", "security"},
            description = "Hết phiên đăng nhập khi đang nhập thì không lưu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tab 1 đang nhập ở form Add New Attribute / trang sửa, tab 2 đăng xuất, tab 1 bấm Save thì ra 419 PAGE EXPIRED hoặc về /login; đăng nhập lại thấy dữ liệu không đổi.")
    public void testExpiredSessionDoesNotSave(String variant, String formType) {
        tc("AEC_ATTR_TC_034", "regression", "attribute", "auth", "security");
        Allure.parameter("Biến thể", variant + " — " + formType);
        String token = newToken("TC034" + variant);
        String originalName = "AutoAttr_" + token;
        String typedName = ("create".equals(formType) ? "AutoAttrSess_" : "AutoAttrSessEd_") + token;
        AttributeListPage list = openAttributesAsAdmin();
        AttributeEditPage edit = new AttributeEditPage(driver());

        Allure.step("Arrange: Tab 1 mở form " + formType + ", nhập Name", () -> {
            if ("create".equals(formType)) {
                list.open();
                list.enterName(typedName);
            } else {
                createAttribute(list, originalName);
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
            Assert.assertEquals(findRow(list, typedName), -1, "Không được có thuộc tính mang tên vừa nhập");
            if (!"create".equals(formType)) {
                Assert.assertTrue(list.rowIndexOf(originalName) >= 0, "Thuộc tính test phải giữ tên cũ");
            }
        });
    }
}
