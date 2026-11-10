package com.aec.tests;

import com.aec.base.BaseTest;
import com.aec.pages.ForgotPasswordPage;
import com.aec.pages.LoginPage;
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

/** TC ở docs/testcases/login/web/test_cases_login_web.md — nhóm quên mật khẩu. Không gửi link tới email có thật. */
@Feature("Đăng nhập · Tài khoản")
@Story("Quên mật khẩu")
public class ForgotPasswordTest extends BaseTest {

    @Test(groups = {"smoke", "login"}, description = "Màn quên mật khẩu đủ thành phần")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra /password/reset có tiêu đề, dòng hướng dẫn, ô Email, nút Send Password Reset Link và liên kết Back to Login.")
    public void testForgotPasswordPageElements() {
        tc("AEC_LOGIN_TC_005", "smoke", "login");
        ForgotPasswordPage page = new ForgotPasswordPage(driver());

        Allure.step("Arrange: Mở trang quên mật khẩu, đóng popup nếu có", () -> {
            page.open();
        });

        Allure.step("Assert: Đối chiếu bảng kiểm thành phần", () -> {
            SoftAssert soft = new SoftAssert();
            soft.assertTrue(page.isTitleDisplayed(), "1. Tiêu đề 'Forgot password?'");
            soft.assertTrue(page.isSubtitleDisplayed(), "2. Dòng hướng dẫn nguyên văn");
            soft.assertTrue(page.isEmailDisplayed(), "3. Ô Email");
            soft.assertTrue(page.isSendButtonDisplayed(), "3. Nút 'Send Password Reset Link'");
            soft.assertTrue(page.isBackToLoginDisplayed(), "4. Liên kết 'Back to Login'");
            soft.assertAll();
        });
    }

    @Test(groups = {"regression", "login", "knownIssue"}, description = "Quên mật khẩu từ trang quản trị quay lại đúng trang quản trị")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra từ /login bấm Forgot password ? rồi Back to Login thì quay về /login. Đang lệch: về /users/login của khách (AMB-LOGIN-01).")
    public void testForgotPasswordFromAdminReturnsToAdminLogin() {
        tc("AEC_LOGIN_TC_029", "regression", "login", "knownIssue");
        LoginPage loginPage = new LoginPage(driver());
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
        });

        Allure.step("Act: Bấm Forgot password ?", () -> {
            loginPage.clickForgotPassword();
            forgotPage.waitUntilLoaded();
        });

        Allure.step("Act: Bấm Back to Login", forgotPage::clickBackToLogin);

        Allure.step("Assert: Về trang đăng nhập quản trị /login", () -> {
            forgotPage.waitForPath("/login");
            Assert.assertEquals(forgotPage.currentPath(), "/login", "Back to Login phải về trang đăng nhập quản trị");
        });
    }

    @Test(groups = {"regression", "login", "validation", "knownIssue"}, description = "Gửi khi để trống báo đúng lỗi")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra bấm Send Password Reset Link khi để trống thì báo email bắt buộc, không nhắc số điện thoại. Đang lệch (AMB-LOGIN-03).")
    public void testForgotPasswordEmptyEmail() {
        tc("AEC_LOGIN_TC_030", "regression", "login", "validation", "knownIssue");
        ForgotPasswordPage page = new ForgotPasswordPage(driver());

        Allure.step("Arrange: Mở trang quên mật khẩu", () -> {
            page.open();
        });

        Allure.step("Act: Để trống Email, bấm Send Password Reset Link", page::submitEmpty);

        Allure.step("Assert: Báo email bắt buộc, không nhắc số điện thoại", () -> {
            String message = page.getNotifyMessage();
            Assert.assertTrue(message.toLowerCase().contains("email"), "Phải báo về email: " + message);
            Assert.assertFalse(message.toLowerCase().contains("phone"), "Không được nhắc số điện thoại: " + message);
        });
    }

    @Test(groups = {"regression", "login", "security", "knownIssue"}, description = "Quên mật khẩu không cho biết email nào có tài khoản")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra gửi email chưa đăng ký không nhận thông báo kiểu 'không có tài khoản'. Đang lệch: 'No account exists with this email' (AMB-LOGIN-04). Bước 2 cần tài khoản khách test — chưa automate.")
    public void testForgotPasswordDoesNotRevealAccounts() {
        tc("AEC_LOGIN_TC_033", "regression", "login", "security", "knownIssue");
        ForgotPasswordPage page = new ForgotPasswordPage(driver());

        Allure.step("Arrange: Mở trang quên mật khẩu", () -> {
            page.open();
        });

        Allure.step("Act: Gửi email chưa đăng ký", () -> page.requestReset(TestDataGenerator.email("login_TC033")));

        Allure.step("Assert: Thông báo không cho biết email chưa có tài khoản", () -> {
            String message = page.getNotifyMessage();
            Assert.assertFalse(message.toLowerCase().contains("no account exists"),
                    "Thông báo đang lộ email chưa có tài khoản: " + message);
        });
    }

    @Test(groups = {"regression", "login", "usability", "knownIssue"}, description = "Thông báo lỗi ở trang cửa hàng nhìn thấy được")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra khi khung cookie còn hiện, thông báo lỗi ở góc dưới trái không bị khung cookie che. Đang lệch (AMB-LOGIN-12).")
    public void testForgotPasswordNotifyNotCoveredByCookieAlert() {
        tc("AEC_LOGIN_TC_039", "regression", "login", "usability", "knownIssue");
        ForgotPasswordPage page = new ForgotPasswordPage(driver());

        Allure.step("Arrange: Mở trang quên mật khẩu, để khung cookie nguyên", () -> {
            page.open();
            Assert.assertTrue(page.isCookieAlertShown(), "Tiền đề: khung cookie phải đang hiện");
        });

        Allure.step("Act: Để trống Email, bấm Send Password Reset Link", page::submitEmpty);

        Allure.step("Assert: Thông báo không bị khung cookie che", () ->
                Assert.assertFalse(page.isNotifyCoveredByCookieAlert(), "Thông báo lỗi đang bị khung cookie che"));
    }
}
