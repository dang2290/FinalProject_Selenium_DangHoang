package com.danghoang.base;

import com.danghoang.config.ConfigReader;
import com.danghoang.drivers.DriverFactory;
import com.danghoang.pages.BrowserWindow;
import com.danghoang.pages.DashboardPage;
import com.danghoang.pages.LoginPage;
import com.danghoang.utils.ScreenshotUtil;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Vòng đời mỗi test: mở driver riêng cho thread → chạy test → đính ảnh cuối → đóng driver.
 * Không giữ WebDriver ở field: TestNG parallel="methods" dùng chung 1 instance class cho nhiều thread.
 */
public abstract class BaseTest {

    protected final ConfigReader config = ConfigReader.getInstance();

    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }

    /** Thao tác mức trình duyệt (điều hướng, tab, cửa sổ) — thân test dùng cái này thay vì gọi driver trực tiếp. */
    protected BrowserWindow browser() {
        return new BrowserWindow(driver());
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            String name = result.isSuccess() ? "trang_thai_cuoi_cua_test" : "trang_thai_khi_that_bai";
            String file = result.getMethod().getMethodName() + "_" + System.currentTimeMillis();
            ScreenshotUtil.captureAndAttach(driver(), name, file);
        } finally {
            try {
                cleanUpTestData();
            } finally {
                DriverFactory.quitDriver();
            }
        }
    }

    /** Dọn dữ liệu test đã tạo — chạy sau khi chụp ảnh cuối, trước khi đóng trình duyệt. */
    protected void cleanUpTestData() {
    }

    /** Gắn TC ID (truy vết RTM) và tag cho test trong Allure. */
    protected void tc(String testId, String... tags) {
        Allure.label("testId", testId);
        for (String tag : tags) {
            Allure.label("tag", tag);
        }
    }

    /** Tiền đề dùng chung: đăng nhập admin bằng tài khoản trong .env / biến môi trường. */
    protected DashboardPage loginAsAdmin() {
        return Allure.step("Arrange: Đăng nhập tài khoản admin", () -> {
            LoginPage loginPage = new LoginPage(driver()).open();
            loginPage.login(config.getUsername(), config.getPassword());
            return new DashboardPage(driver()).waitUntilLoaded();
        });
    }
}
