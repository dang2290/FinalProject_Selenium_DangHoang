package com.aec.tests;

import com.aec.base.BaseTest;
import com.aec.pages.CustomerLoginPage;
import com.aec.pages.ForgotPasswordPage;
import com.aec.pages.RegistrationPage;
import com.aec.utils.TestDataGenerator;
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

import java.util.List;

/** TC ở docs/testcases/login/web/test_cases_login_web.md — nhóm đăng nhập khách và điều hướng giữa các trang tài khoản. */
@Feature("Đăng nhập · Tài khoản")
@Story("Đăng nhập khách hàng")
public class CustomerLoginTest extends BaseTest {

    @Test(groups = {"smoke", "login"}, description = "Màn đăng nhập khách đủ thành phần")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra /users/login hiện tiêu đề, 2 ô, Remember Me, Forgot password?, nút Login, liên kết Register Now và 2 liên kết ở header.")
    public void testCustomerLoginPageElements() {
        tc("AEC_LOGIN_TC_003", "smoke", "login");
        CustomerLoginPage page = new CustomerLoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập khách, đóng popup nếu có", () -> {
            page.open();
        });

        Allure.step("Assert: Đối chiếu bảng kiểm thành phần", () -> {
            SoftAssert soft = new SoftAssert();
            soft.assertTrue(page.isTitleDisplayed(), "1. Tiêu đề 'Login to your account.'");
            soft.assertTrue(page.isEmailDisplayed() && page.isPasswordDisplayed(), "2. Ô Email, Password");
            soft.assertTrue(page.isRememberMeDisplayed(), "2. Ô tick Remember Me");
            soft.assertTrue(page.isForgotPasswordDisplayed(), "2. Liên kết Forgot password?");
            soft.assertTrue(page.isLoginButtonDisplayed(), "3. Nút Login");
            soft.assertTrue(page.isRegisterNowDisplayed(), "4. Liên kết Register Now");
            soft.assertTrue(page.areHeaderLinksDisplayed(), "5. Header có Login, Registration");
            soft.assertAll();
        });
    }

    @Test(groups = {"regression", "login", "validation", "knownIssue"}, description = "Đăng nhập khách để trống báo đúng lỗi")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra bấm Login khi để trống thì 2 ô viền đỏ và báo email bắt buộc, không nhắc số điện thoại. Đang lệch: thông báo nhắc 'phone' (AMB-LOGIN-02).")
    public void testCustomerLoginEmptyFields() {
        tc("AEC_LOGIN_TC_021", "regression", "login", "validation", "knownIssue");
        CustomerLoginPage page = new CustomerLoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập khách", () -> {
            page.open();
        });

        Allure.step("Act: Để trống 2 ô, bấm Login", page::submitEmpty);

        Allure.step("Assert: 2 ô viền đỏ, lỗi email không nhắc số điện thoại", () -> {
            Assert.assertTrue(page.isStillOnPage(), "Phải ở lại /users/login");
            Assert.assertEquals(page.invalidFieldNames(), List.of("email", "password"), "Hai ô phải viền đỏ");
            String error = page.emailFieldError();
            Assert.assertTrue(error.toLowerCase().contains("email"), "Lỗi phải nói về email: " + error);
            Assert.assertFalse(error.toLowerCase().contains("phone"), "Lỗi không được nhắc số điện thoại: " + error);
        });
    }

    @Test(groups = {"regression", "login"}, description = "Đăng nhập khách sai thông tin bị từ chối")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra email chưa đăng ký ở form khách thì ở lại /users/login, thông báo 'Invalid login credentials', các ô trống.")
    public void testCustomerLoginInvalidCredentials() {
        tc("AEC_LOGIN_TC_022", "regression", "login");
        CustomerLoginPage page = new CustomerLoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập khách", () -> {
            page.open();
        });

        Allure.step("Act: Đăng nhập bằng email chưa đăng ký", () ->
                page.login(TestDataGenerator.email("login_TC022"), TestDataGenerator.password("TC022")));

        Allure.step("Assert: Bị từ chối, các ô trống", () -> {
            Assert.assertEquals(page.getNotifyMessage(), "Invalid login credentials", "Thông báo sai thông tin");
            Assert.assertTrue(page.isStillOnPage(), "Phải ở lại /users/login");
            Assert.assertEquals(page.emailValue(), "", "Ô Email phải trống");
            Assert.assertEquals(page.passwordValue(), "", "Ô Password phải trống");
        });
    }

    @DataProvider(name = "accountLinks", parallel = true)
    public Object[][] accountLinks() {
        return new Object[][]{
                {"a", "/users/login → Register Now", "/users/registration"},
                {"b", "/users/registration → Log In", "/users/login"},
                {"c", "/users/login → Forgot password?", "/password/reset"},
                {"d", "/password/reset → Back to Login", "/users/login"}
        };
    }

    @Test(dataProvider = "accountLinks", groups = {"regression", "login"},
            description = "Liên kết giữa các trang tài khoản đúng đích")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra 4 liên kết Register Now, Log In, Forgot password?, Back to Login mở đúng trang đích.")
    public void testAccountPageLinks(String variant, String route, String expectedPath) {
        tc("AEC_LOGIN_TC_024", "regression", "login");
        Allure.parameter("Biến thể", variant + " — " + route);

        Allure.step("Act: Mở trang xuất phát và bấm liên kết " + route, () -> {
            switch (variant) {
                case "a" -> new CustomerLoginPage(driver()).open().clickRegisterNow();
                case "b" -> new RegistrationPage(driver()).open().clickLogIn();
                case "c" -> new CustomerLoginPage(driver()).open().clickForgotPassword();
                default -> new ForgotPasswordPage(driver()).open().clickBackToLogin();
            }
        });

        Allure.step("Assert: Mở đúng " + expectedPath, () -> {
            new CustomerLoginPage(driver()).waitForPath(expectedPath);
            Assert.assertEquals(new CustomerLoginPage(driver()).currentPath(), expectedPath, "Trang đích của liên kết");
        });
    }

    @Test(groups = {"regression", "login", "knownIssue"}, description = "Câu mời đăng ký viết đúng chính tả")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra dòng phía trên Register Now ghi 'Don't have an account?'. Đang lệch: thiếu dấu nháy (AMB-LOGIN-13).")
    public void testCustomerLoginRegisterPromptSpelling() {
        tc("AEC_LOGIN_TC_040", "regression", "login", "knownIssue");
        CustomerLoginPage page = new CustomerLoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập khách", () -> {
            page.open();
        });

        Allure.step("Assert: Dòng mời đăng ký đúng chính tả", () ->
                Assert.assertEquals(page.registerPrompt(), "Don't have an account?", "Câu phía trên Register Now"));
    }

    @Test(dataProvider = "windowSizes", dataProviderClass = AdminLoginTest.class,
            groups = {"regression", "login", "responsive"}, description = "Form đăng nhập khách dùng được ở màn rộng và hẹp")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ở 1920×1080, 1366×768 và bề ngang 800, form /users/login đủ ô, nút và không có thanh cuộn ngang.")
    public void testCustomerLoginResponsive(String variant, int width, int height) {
        tc("AEC_LOGIN_TC_038", "regression", "login", "responsive");
        Allure.parameter("Biến thể", variant + " — " + width + "×" + height + " — trang khách");
        CustomerLoginPage page = new CustomerLoginPage(driver());

        Allure.step("Arrange: Đặt cửa sổ " + width + "×" + height, () ->
                driver().manage().window().setSize(new Dimension(width, height)));

        Allure.step("Act: Mở trang đăng nhập khách", () -> {
            page.open();
        });

        Allure.step("Assert: Đủ thành phần, không cuộn ngang", () -> {
            Assert.assertTrue(page.isLoginButtonDisplayed() && page.isRegisterNowDisplayed(), "Nút Login, Register Now phải hiện");
            Assert.assertTrue(page.hasNoHorizontalScroll(), "Không được có thanh cuộn ngang toàn trang");
        });
    }
}
