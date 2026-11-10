package com.aec.tests;

import com.aec.base.BaseTest;
import com.aec.pages.BrandPage;
import com.aec.pages.DashboardPage;
import com.aec.pages.LoginPage;
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

/**
 * TC ở docs/testcases/login/web/test_cases_login_web.md — nhóm phiên đăng nhập.
 * Mọi test group "auth" đăng nhập bằng APP_USERNAME / APP_PASSWORD (.env hoặc biến môi trường).
 */
@Feature("Đăng nhập · Tài khoản")
@Story("Phiên đăng nhập & đăng xuất")
public class SessionTest extends BaseTest {

    @Test(groups = {"smoke", "login", "auth"}, description = "Admin đăng nhập thành công vào Dashboard")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra admin đăng nhập bằng email, mật khẩu hợp lệ thì vào /admin, mục Dashboard được chọn, góc trên phải hiện 'Admin Example'.")
    public void testAdminLoginSuccess() {
        tc("AEC_LOGIN_TC_006", "smoke", "login", "auth");

        DashboardPage dashboard = loginAsAdmin();

        Allure.step("Assert: Vào Dashboard, mục Dashboard được chọn", () -> {
            Assert.assertTrue(dashboard.currentPath().startsWith("/admin"), "Phải vào /admin");
            Assert.assertTrue(dashboard.isDashboardMenuActive(), "Mục Dashboard ở sidebar phải được chọn");
            Assert.assertEquals(dashboard.accountName(), "Admin Example", "Tên tài khoản góc trên phải");
        });
    }

    @Test(groups = {"smoke", "login", "auth"}, description = "Đăng xuất về trang đăng nhập")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra đăng xuất từ menu tài khoản thì về /login; mở lại /admin vẫn ở /login.")
    public void testLogout() {
        tc("AEC_LOGIN_TC_007", "smoke", "login", "auth");
        DashboardPage dashboard = loginAsAdmin();

        Allure.step("Act: Đăng xuất bằng menu tài khoản", dashboard::logout);

        Allure.step("Assert: Về /login; mở lại /admin vẫn ở /login", () -> {
            Assert.assertEquals(dashboard.currentPath(), "/login", "Sau đăng xuất phải về /login");
            driver().get(config.getBaseUrl() + "/admin");
            Assert.assertEquals(dashboard.currentPath(), "/login", "Mở /admin sau đăng xuất phải về /login");
            Assert.assertFalse(dashboard.isSidebarDisplayedNow(), "Không được thấy Dashboard");
        });
    }

    @DataProvider(name = "protectedPages", parallel = true)
    public Object[][] protectedPages() {
        return new Object[][]{{"a", "/admin"}, {"b", "/admin/brands"}, {"c", "/reviews"}};
    }

    @Test(dataProvider = "protectedPages", groups = {"regression", "login", "security"},
            description = "Chưa đăng nhập không vào được trang quản trị")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiểm tra chưa đăng nhập mở /admin, /admin/brands, /reviews đều về /login và không thấy nội dung quản trị.")
    public void testProtectedPagesRequireLogin(String variant, String path) {
        tc("AEC_LOGIN_TC_013", "regression", "login", "security");
        Allure.parameter("Biến thể", variant + " — " + path);
        LoginPage loginPage = new LoginPage(driver());

        Allure.step("Act: Mở " + path + " khi chưa đăng nhập", () -> driver().get(config.getBaseUrl() + path));

        Allure.step("Assert: Về trang đăng nhập", () -> {
            loginPage.waitForPath("/login");
            Assert.assertEquals(loginPage.currentPath(), "/login", "Phải chuyển về /login");
            Assert.assertTrue(loginPage.isHeadingDisplayed(), "Phải thấy form đăng nhập, không thấy nội dung quản trị");
        });
    }

    @DataProvider(name = "authPagesWhenLoggedIn")
    public Object[][] authPagesWhenLoggedIn() {
        return new Object[][]{
                {"AEC_LOGIN_TC_014", "/login", "/admin"},
                {"AEC_LOGIN_TC_015", "/users/login", "/"},
                {"AEC_LOGIN_TC_016", "/users/registration", "/"},
                {"AEC_LOGIN_TC_017", "/password/reset", "/home"}
        };
    }

    @Test(dataProvider = "authPagesWhenLoggedIn", groups = {"regression", "login", "auth"},
            description = "Đang đăng nhập mở trang xác thực thì bị chuyển đi")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra admin đang đăng nhập mở /login, /users/login, /users/registration, /password/reset thì không thấy form, được chuyển tới trang đích. TC_014 đang lệch: /login chuyển sang /home (AMB-LOGIN-10).")
    public void testAuthPagesRedirectWhenLoggedIn(String testId, String path, String expectedPath) {
        tc(testId, "regression", "login", "auth");
        Allure.parameter("Trang", path);
        DashboardPage dashboard = loginAsAdmin();

        Allure.step("Act: Mở " + path + " khi đang đăng nhập", () -> driver().get(config.getBaseUrl() + path));

        Allure.step("Assert: Chuyển tới " + expectedPath, () -> {
            dashboard.waitForPath(expectedPath);
            Assert.assertEquals(dashboard.currentPath(), expectedPath, "Trang đích khi đang đăng nhập mở " + path);
        });
    }

    @Test(groups = {"regression", "login", "auth"}, description = "Menu tài khoản có Profile và Logout")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra góc trên phải hiện 'Admin Example' · 'staff'; mở menu có đúng 2 mục Profile, Logout theo thứ tự.")
    public void testAccountMenu() {
        tc("AEC_LOGIN_TC_018", "regression", "login", "auth");
        DashboardPage dashboard = loginAsAdmin();

        Allure.step("Assert: Tên và vai trò tài khoản", () -> {
            Assert.assertEquals(dashboard.accountName(), "Admin Example", "Tên tài khoản");
            Assert.assertEquals(dashboard.accountRole(), "staff", "Vai trò tài khoản");
        });

        List<String> items = Allure.step("Act: Mở menu tài khoản", dashboard::openAccountMenu);

        Allure.step("Assert: Menu có Profile, Logout", () ->
                Assert.assertEquals(items, List.of("Profile", "Logout"), "Các mục trong menu tài khoản"));
    }

    @Test(groups = {"regression", "login", "auth", "security", "knownIssue"},
            description = "Bấm Back sau đăng xuất không thấy lại trang quản trị")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra sau khi đăng xuất, nút Back không hiện lại Dashboard. Đang lệch: hiện lại từ bộ nhớ đệm (AMB-LOGIN-05).")
    public void testBackAfterLogoutDoesNotShowAdmin() {
        tc("AEC_LOGIN_TC_019", "regression", "login", "auth", "security", "knownIssue");
        DashboardPage dashboard = loginAsAdmin();

        Allure.step("Act: Đăng xuất bằng menu tài khoản", dashboard::logout);

        Allure.step("Act: Bấm Back của trình duyệt", () -> driver().navigate().back());

        Allure.step("Assert: Không hiện lại Dashboard", () ->
                Assert.assertFalse(dashboard.isSidebarDisplayedNow(), "Sau đăng xuất, Back không được hiện lại Dashboard"));
    }

    @Test(groups = {"regression", "login", "auth", "security"}, description = "Hết phiên khi đang mở form thì không lưu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tab 1 đang nhập brand mới, tab 2 đăng xuất, tab 1 bấm Save thì ra 419 PAGE EXPIRED hoặc về /login, không thông báo thành công.")
    public void testExpiredSessionDoesNotSave() {
        tc("AEC_LOGIN_TC_020", "regression", "login", "auth", "security");
        loginAsAdmin();
        BrandPage brandPage = new BrandPage(driver());
        String formTab = driver().getWindowHandle();

        Allure.step("Arrange: Tab 1 mở All Brands, nhập tên brand mới (chưa lưu)", () -> {
            brandPage.open();
            brandPage.enterNewBrandName(TestDataGenerator.name("AutoBrand", "LoginTC020"));
        });

        Allure.step("Act: Tab 2 đăng xuất", () -> {
            driver().switchTo().newWindow(WindowType.TAB);
            new DashboardPage(driver()).open().logout();
            driver().close();
            driver().switchTo().window(formTab);
        });

        Allure.step("Act: Tab 1 bấm Save", brandPage::clickSave);

        Allure.step("Assert: 419 PAGE EXPIRED hoặc về /login, không lưu", () ->
                Assert.assertTrue(brandPage.isPageExpired() || "/login".equals(brandPage.currentPath()),
                        "Gửi form sau khi hết phiên phải bị chặn"));
    }
}
