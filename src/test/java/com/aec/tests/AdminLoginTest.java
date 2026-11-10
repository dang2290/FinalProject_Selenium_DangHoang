package com.aec.tests;

import com.aec.base.BaseTest;
import com.aec.pages.LoginPage;
import com.aec.utils.JsonDataReader;
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
import java.util.Map;

/** TC ở docs/testcases/login/web/test_cases_login_web.md — nhóm đăng nhập quản trị, không cần tài khoản thật. */
@Feature("Đăng nhập · Tài khoản")
@Story("Đăng nhập quản trị")
public class AdminLoginTest extends BaseTest {

    private static final String INVALID_CREDENTIALS = "Invalid login credentials";

    @Test(groups = {"smoke", "login"}, description = "Màn đăng nhập quản trị đủ thành phần, nhãn nguyên văn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra trang /login hiện đủ logo, tiêu đề, 2 ô có chữ gợi ý, Remember Me, Forgot password ?, nút Login và không có CAPTCHA.")
    public void testAdminLoginPageElements() {
        tc("AEC_LOGIN_TC_001", "smoke", "login");
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
        });

        Allure.step("Assert: Đối chiếu bảng kiểm thành phần", () -> {
            SoftAssert soft = new SoftAssert();
            soft.assertTrue(loginPage.isLogoDisplayed(), "1. Phải có logo");
            soft.assertTrue(loginPage.isHeadingDisplayed(), "1. Tiêu đề phải là 'Welcome to Active eCommerce CMS'");
            soft.assertTrue(loginPage.isSubtitleDisplayed(), "1. Phải có dòng 'Login to your account.'");
            soft.assertEquals(loginPage.emailPlaceholder(), "Email", "2. Chữ gợi ý ô Email");
            soft.assertEquals(loginPage.passwordPlaceholder(), "Password", "2. Chữ gợi ý ô Password");
            soft.assertFalse(loginPage.hasFieldLabels(), "2. Hai ô không có nhãn riêng");
            soft.assertTrue(loginPage.isRememberMeDisplayed(), "3. Phải có ô tick 'Remember Me'");
            soft.assertTrue(loginPage.isForgotPasswordDisplayed(), "3. Phải có liên kết 'Forgot password ?'");
            soft.assertTrue(loginPage.isLoginButtonDisplayed(), "4. Phải có nút 'Login'");
            soft.assertFalse(loginPage.hasCaptcha(), "5. Không được có CAPTCHA");
            soft.assertAll();
        });
    }

    @Test(groups = {"smoke", "login"}, description = "Trạng thái mặc định của form quản trị")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra mở /login là gõ được ngay vào ô Email, Remember Me không tick, ô Password trống.")
    public void testAdminLoginDefaultState() {
        tc("AEC_LOGIN_TC_002", "smoke", "login");
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
        });

        Allure.step("Act: Gõ chữ 'a' ngay, không bấm chuột", () -> loginPage.typeWithoutClicking("a"));

        Allure.step("Assert: Chữ vào ô Email, Remember Me không tick, Password trống", () -> {
            SoftAssert soft = new SoftAssert();
            soft.assertEquals(loginPage.emailValue(), "a", "Con trỏ phải nằm sẵn ở ô Email");
            soft.assertFalse(loginPage.isRememberMeChecked(), "Remember Me phải không tick");
            soft.assertEquals(loginPage.passwordValue(), "", "Ô Password phải trống");
            soft.assertAll();
        });
    }

    @DataProvider(name = "invalidEmails", parallel = true)
    public Object[][] invalidEmails() {
        return JsonDataReader.readList("testdata/login_email_invalid.json").stream()
                .map(row -> new Object[]{row})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalidEmails", groups = {"regression", "login", "validation"},
            description = "Email trống hoặc sai định dạng bị chặn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bấm Login khi ô Email trống hoặc sai định dạng thì trình duyệt nhắc ở ô Email, form không gửi, trang không tải lại.")
    public void testAdminLoginInvalidEmailBlocked(Map<String, String> data) {
        tc("AEC_LOGIN_TC_008", "regression", "login", "validation");
        Allure.parameter("Biến thể", data.get("variant") + " — " + data.get("case"));
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
            loginPage.markPage();
        });

        Allure.step("Act: Nhập Email '" + data.get("email") + "', Password giả, bấm Login", () -> {
            if (!data.get("email").isEmpty()) {
                loginPage.enterEmail(data.get("email"));
            }
            loginPage.enterPassword(TestDataGenerator.password("TC008"));
            loginPage.submit();
        });

        Allure.step("Assert: Trình duyệt nhắc ở ô Email, form không gửi", () -> {
            Assert.assertFalse(loginPage.emailValidationMessage().isEmpty(), "Trình duyệt phải nhắc ở ô Email");
            Assert.assertTrue(loginPage.isSamePageInstance(), "Trang không được tải lại");
            Assert.assertEquals(loginPage.focusedFieldId(), "email", "Con trỏ phải về ô Email");
        });
    }

    @Test(groups = {"regression", "login", "validation"}, description = "Password trống bị chặn (biến thể a)")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Password trống thì trình duyệt nhắc ở ô Password, form không gửi.")
    public void testAdminLoginEmptyPasswordBlocked() {
        tc("AEC_LOGIN_TC_009", "regression", "login", "validation");
        Allure.parameter("Biến thể", "a — Password trống");
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
            loginPage.markPage();
        });

        Allure.step("Act: Nhập Email test, để trống Password, bấm Login", () -> {
            loginPage.enterEmail(TestDataGenerator.email("login_TC009a"));
            loginPage.submit();
        });

        Allure.step("Assert: Trình duyệt nhắc ở ô Password, form không gửi", () -> {
            Assert.assertFalse(loginPage.passwordValidationMessage().isEmpty(), "Trình duyệt phải nhắc ở ô Password");
            Assert.assertTrue(loginPage.isSamePageInstance(), "Trang không được tải lại");
        });
    }

    @Test(groups = {"regression", "login", "validation"}, description = "Password chỉ có khoảng trắng bị từ chối (biến thể b)")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Password chỉ có khoảng trắng thì máy chủ báo 'The password field is required.' dưới ô Password và giữ email đã nhập.")
    public void testAdminLoginBlankPasswordRejected() {
        tc("AEC_LOGIN_TC_009", "regression", "login", "validation");
        Allure.parameter("Biến thể", "b — Password 3 dấu cách");
        LoginPage loginPage = new LoginPage(driver());
        String email = TestDataGenerator.email("login_TC009b");

        Allure.step("Arrange: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
        });

        Allure.step("Act: Nhập Email test, Password 3 dấu cách, bấm Login", () -> loginPage.login(email, "   "));

        Allure.step("Assert: Chữ đỏ dưới Password, ô Email giữ giá trị", () -> {
            Assert.assertEquals(loginPage.getErrorMessage(), "The password field is required.", "Thông báo dưới ô Password");
            Assert.assertTrue(loginPage.isStillOnLoginPage(), "Phải ở lại /login");
            Assert.assertEquals(loginPage.emailValue(), email, "Ô Email phải giữ email đã nhập");
        });
    }

    @Test(groups = {"regression", "login"}, description = "Sai thông tin đăng nhập bị từ chối (biến thể a)")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra email chưa đăng ký thì ở lại /login, thông báo 'Invalid login credentials', hai ô bị xoá trắng. Biến thể b (email admin) không automate để tránh khoá tài khoản dùng chung.")
    public void testAdminLoginUnknownEmailRejected() {
        tc("AEC_LOGIN_TC_010", "regression", "login");
        Allure.parameter("Biến thể", "a — Email chưa đăng ký");
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
        });

        Allure.step("Act: Đăng nhập bằng email chưa đăng ký", () ->
                loginPage.login(TestDataGenerator.email("login_TC010"), TestDataGenerator.password("TC010")));

        Allure.step("Assert: Thông báo lỗi, hai ô trống", () -> {
            Assert.assertEquals(loginPage.getErrorMessage(), INVALID_CREDENTIALS, "Thông báo sai thông tin");
            Assert.assertTrue(loginPage.isStillOnLoginPage(), "Phải ở lại /login");
            Assert.assertEquals(loginPage.emailValue(), "", "Ô Email phải trống");
            Assert.assertEquals(loginPage.passwordValue(), "", "Ô Password phải trống");
        });
    }

    @Test(groups = {"regression", "login", "security"}, description = "Mật khẩu được che khi gõ")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ô Password che ký tự khi gõ và không có nút hiện mật khẩu.")
    public void testAdminPasswordMasked() {
        tc("AEC_LOGIN_TC_034", "regression", "login", "security");
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
        });

        Allure.step("Act: Gõ 'Abc12345' vào ô Password", () -> loginPage.enterPassword("Abc12345"));

        Allure.step("Assert: Ô được che, không có nút hiện mật khẩu", () -> {
            Assert.assertEquals(loginPage.passwordInputType(), "password", "Ô Password phải che ký tự");
            Assert.assertFalse(loginPage.hasPasswordRevealToggle(), "Không được có nút hiện mật khẩu");
        });
    }

    @DataProvider(name = "sqlInjection", parallel = true)
    public Object[][] sqlInjection() {
        return new Object[][]{
                {"a", "auto'OR'1'='1_TC035_" + System.currentTimeMillis() + "@auto.test", TestDataGenerator.password("TC035a")},
                {"b", TestDataGenerator.email("login_TC035b"), "' OR '1'='1' -- "}
        };
    }

    @Test(dataProvider = "sqlInjection", groups = {"regression", "login", "security"},
            description = "Chuỗi SQL ở Email, Password không đăng nhập được")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra chuỗi SQL ở Email hoặc Password chỉ nhận 'Invalid login credentials', không trang lỗi, không vào Dashboard.")
    public void testAdminLoginSqlInjectionRejected(String variant, String email, String password) {
        tc("AEC_LOGIN_TC_035", "regression", "login", "security");
        Allure.parameter("Biến thể", variant);
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
        });

        Allure.step("Act: Đăng nhập với chuỗi SQL", () -> loginPage.login(email, password));

        Allure.step("Assert: Bị từ chối, không trang lỗi", () -> {
            Assert.assertEquals(loginPage.getErrorMessage(), INVALID_CREDENTIALS, "Phải bị từ chối như sai thông tin");
            Assert.assertTrue(loginPage.isStillOnLoginPage(), "Phải ở lại /login, không vào Dashboard");
        });
    }

    @Test(groups = {"regression", "login"}, description = "Bấm Login liên tiếp không gây lỗi")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra nhấp đúp nút Login với thông tin sai thì không có trang lỗi, chỉ một thông báo 'Invalid login credentials'.")
    public void testAdminLoginDoubleClick() {
        tc("AEC_LOGIN_TC_036", "regression", "login");
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Mở trang, nhập thông tin sai", () -> {
            loginPage.open();
            loginPage.enterEmail(TestDataGenerator.email("login_TC036"));
            loginPage.enterPassword(TestDataGenerator.password("TC036"));
        });

        Allure.step("Act: Nhấp đúp nút Login", loginPage::doubleClickLogin);

        Allure.step("Assert: Một thông báo lỗi, không trang lỗi", () -> {
            Assert.assertEquals(loginPage.getErrorMessage(), INVALID_CREDENTIALS, "Thông báo sai thông tin");
            Assert.assertTrue(loginPage.isStillOnLoginPage(), "Phải ở lại /login, không trang lỗi");
        });
    }

    @Test(groups = {"regression", "login", "accessibility", "knownIssue"},
            description = "Đăng nhập quản trị bằng bàn phím — thứ tự Tab và viền khi được chọn (biến thể a)")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra Tab đi lần lượt Email → Password → Remember Me → Forgot password ? → Login và phần tử đang chọn có dấu hiệu nhìn thấy được. Đang lệch: 3 phần tử cuối không có dấu hiệu.")
    public void testAdminLoginKeyboardTabOrder() {
        tc("AEC_LOGIN_TC_037", "regression", "login", "accessibility", "knownIssue");
        Allure.parameter("Biến thể", "a — Tab");
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
        });

        List<String> sequence = Allure.step("Act: Nhấn Tab 4 lần từ ô Email", () -> loginPage.tabSequenceFromEmail(4));

        Allure.step("Assert: Thứ tự Tab đúng", () ->
                Assert.assertEquals(sequence, List.of("password", "remember", "Forgot password ?", "Login"),
                        "Thứ tự focus sau mỗi lần Tab"));

        Allure.step("Assert: Phần tử đang chọn có dấu hiệu nhìn thấy được", () ->
                Assert.assertEquals(loginPage.elementsWithoutVisibleFocus(), List.of(),
                        "Các phần tử này không đổi gì khi được chọn bằng Tab"));
    }

    @Test(groups = {"regression", "login", "accessibility"}, description = "Nhấn Enter trong ô Password gửi form (biến thể b)")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra nhấn Enter trong ô Password thì form gửi đi và hiện 'Invalid login credentials' với dữ liệu sai.")
    public void testAdminLoginEnterSubmits() {
        tc("AEC_LOGIN_TC_037", "regression", "login", "accessibility");
        Allure.parameter("Biến thể", "b — Enter");
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Mở trang, nhập thông tin sai", () -> {
            loginPage.open();
            loginPage.enterEmail(TestDataGenerator.email("login_TC037b"));
            loginPage.enterPassword(TestDataGenerator.password("TC037b"));
        });

        Allure.step("Act: Nhấn Enter trong ô Password", loginPage::pressEnterInPassword);

        Allure.step("Assert: Form được gửi", () ->
                Assert.assertEquals(loginPage.getErrorMessage(), INVALID_CREDENTIALS, "Enter phải gửi form"));
    }

    @DataProvider(name = "windowSizes", parallel = true)
    public static Object[][] windowSizes() {
        return new Object[][]{{"a", 1920, 1080}, {"b", 1366, 768}, {"c", 800, 900}};
    }

    @Test(dataProvider = "windowSizes", groups = {"regression", "login", "responsive"},
            description = "Form đăng nhập quản trị dùng được ở màn rộng và hẹp")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ở 1920×1080, 1366×768 và bề ngang 800, form /login đủ ô, nút, liên kết và không có thanh cuộn ngang. Trang khách kiểm ở CustomerLoginTest.")
    public void testAdminLoginResponsive(String variant, int width, int height) {
        tc("AEC_LOGIN_TC_038", "regression", "login", "responsive");
        Allure.parameter("Biến thể", variant + " — " + width + "×" + height);
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Arrange: Đặt cửa sổ " + width + "×" + height, () ->
                driver().manage().window().setSize(new Dimension(width, height)));

        Allure.step("Act: Mở trang đăng nhập quản trị", () -> {
            loginPage.open();
        });

        Allure.step("Assert: Đủ thành phần, không cuộn ngang", () -> {
            Assert.assertTrue(loginPage.isLoginButtonDisplayed() && loginPage.isForgotPasswordDisplayed(),
                    "Nút Login và liên kết Forgot password ? phải hiện");
            Assert.assertTrue(loginPage.hasNoHorizontalScroll(), "Không được có thanh cuộn ngang toàn trang");
        });
    }
}
