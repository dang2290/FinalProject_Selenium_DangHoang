package com.aec.tests;

import com.aec.base.BaseTest;
import com.aec.pages.RegistrationPage;
import com.aec.utils.TestDataGenerator;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

/**
 * TC ở docs/testcases/login/web/test_cases_login_web.md — nhóm đăng ký.
 * Môi trường dùng chung: chỉ automate các TC chắc chắn KHÔNG tạo tài khoản (004, 025, 026).
 */
@Feature("Đăng nhập · Tài khoản")
@Story("Đăng ký khách hàng")
public class RegistrationTest extends BaseTest {

    @Test(groups = {"smoke", "login"}, description = "Màn đăng ký đủ thành phần")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra /users/registration có tiêu đề, 4 ô đúng thứ tự, ô tick điều khoản chưa tick, nút Create Account, dòng Log In và không có ô số điện thoại.")
    public void testRegistrationPageElements() {
        tc("AEC_LOGIN_TC_004", "smoke", "login");
        RegistrationPage page = new RegistrationPage(driver());

        Allure.step("Arrange: Mở trang đăng ký, đóng popup nếu có", () -> {
            page.open();
        });

        Allure.step("Assert: Đối chiếu bảng kiểm thành phần", () -> {
            SoftAssert soft = new SoftAssert();
            soft.assertTrue(page.isTitleDisplayed(), "1. Tiêu đề 'Create an account.'");
            soft.assertEquals(page.fieldPlaceholdersInOrder(), List.of("Full Name", "Email", "Password", "Confirm Password"),
                    "2. Bốn ô theo thứ tự");
            soft.assertTrue(page.isTermsTextDisplayed(), "3. Ô tick điều khoản có chữ nguyên văn");
            soft.assertFalse(page.isTermsChecked(), "3. Ô điều khoản chưa tick");
            soft.assertTrue(page.isCreateAccountDisplayed(), "4. Nút Create Account");
            soft.assertTrue(page.isLogInPromptDisplayed(), "4. Dòng 'Already have an account?' + Log In");
            soft.assertFalse(page.hasPhoneField(), "5. Không có ô số điện thoại");
            soft.assertAll();
        });
    }

    @Test(groups = {"regression", "login", "validation"}, description = "Chưa tick điều khoản thì không gửi")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra điền đủ 4 ô nhưng không tick điều khoản thì trình duyệt nhắc ở ô tick, form không gửi, không tạo tài khoản.")
    public void testRegistrationRequiresTerms() {
        tc("AEC_LOGIN_TC_025", "regression", "login", "validation");
        RegistrationPage page = new RegistrationPage(driver());
        String password = TestDataGenerator.password("TC025");

        Allure.step("Arrange: Mở trang đăng ký", () -> {
            page.open();
            page.markPage();
        });

        Allure.step("Act: Điền 4 ô, không tick điều khoản, bấm Create Account", () -> {
            page.fillForm("Auto TC025", TestDataGenerator.email("login_TC025"), password, password);
            page.submit();
        });

        Allure.step("Assert: Trình duyệt nhắc ở ô tick, form không gửi", () -> {
            Assert.assertFalse(page.termsValidationMessage().isEmpty(), "Trình duyệt phải nhắc ở ô tick điều khoản");
            Assert.assertTrue(page.isSamePageInstance(), "Form không được gửi");
        });
    }

    @Test(groups = {"regression", "login", "validation"}, description = "Email đã có tài khoản không đăng ký lại được")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra đăng ký bằng email của tài khoản admin đã tồn tại thì báo 'Email or Phone already exists.' và không tạo tài khoản.")
    public void testRegistrationExistingEmailRejected() {
        tc("AEC_LOGIN_TC_026", "regression", "login", "validation");
        RegistrationPage page = new RegistrationPage(driver());
        String password = TestDataGenerator.password("TC026");

        Allure.step("Arrange: Mở trang đăng ký", () -> {
            page.open();
        });

        Allure.step("Act: Đăng ký bằng email admin đã tồn tại", () -> {
            page.fillForm("Auto TC026", config.getUsername(), password, password);
            page.acceptTerms();
            page.submit();
        });

        Allure.step("Assert: Báo email đã tồn tại, ở lại trang đăng ký", () -> {
            Assert.assertEquals(page.getNotifyMessage(), "Email or Phone already exists.", "Thông báo email trùng");
            Assert.assertTrue(page.isOn("/users/registration"), "Phải ở lại /users/registration");
        });
    }
}
