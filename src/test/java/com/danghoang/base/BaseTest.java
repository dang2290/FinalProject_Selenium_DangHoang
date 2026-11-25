package com.danghoang.base;

import com.danghoang.config.ConfigReader;
import com.danghoang.drivers.DriverFactory;
import com.danghoang.drivers.DriverManager;
import com.danghoang.listeners.TestListener;
import com.danghoang.pages.BrowserWindow;
import com.danghoang.pages.DashboardPage;
import com.danghoang.pages.LoginPage;
import com.danghoang.utils.LogUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Allure.ThrowableRunnable;
import io.qameta.allure.Allure.ThrowableRunnableVoid;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

/**
 * Lớp cha của mọi test: mở driver cho thread → chạy test → dọn dữ liệu → đóng driver.
 * Log kết quả, ảnh cuối, nhật ký: {@link TestListener} lo.
 * Driver nằm ở DriverManager (theo thread), không ở field: TestNG dùng chung 1 instance class cho nhiều thread.
 *
 * <p><b>Cách đặt tên step (test chỉ dùng các hàm dưới, không gọi Allure.step):</b>
 * <ul>
 *   <li>{@code arrange}: tiền đề. {@code act}: hành động đang kiểm. {@code verify}: kết quả mong đợi.</li>
 *   <li>Không tự gõ tiền tố. Tên lặp ở nhiều test thì lấy từ {@link StepNames}.</li>
 *   <li>Viết theo nghiệp vụ, nối thao tác bằng "rồi", nút lưu ghi "bấm Save". Chi tiết click / nhập đã có WebUI ghi.</li>
 * </ul>
 */
@Listeners(TestListener.class)
public abstract class BaseTest {

    private static final String ARRANGE = "Arrange: ";
    private static final String ACT = "Act: ";
    private static final String ASSERT = "Assert: ";
    private static final String ACT_AND_ASSERT = "Act + Assert: ";
    private static final String CLEANUP = "Dọn dữ liệu: ";

    protected final ConfigReader config = ConfigReader.getInstance();

    /** Thao tác mức trình duyệt (điều hướng, tab, cửa sổ). */
    protected BrowserWindow browser() {
        return new BrowserWindow();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverManager.setDriver(DriverFactory.createDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            cleanUpTestData();
        } finally {
            closeDriver();
        }
    }

    private static void closeDriver() {
        try {
            DriverManager.quit();
        } catch (WebDriverException e) {
            // ChromeDriver đã chết trước khi đóng: để lỗi văng ra thì TestNG bỏ qua các test sau
            LogUtils.warn("Không đóng được trình duyệt, bỏ qua: {}", e.getClass().getSimpleName());
        }
    }

    /** Dọn dữ liệu test đã tạo: chạy sau khi TestListener chụp ảnh cuối, trước khi đóng trình duyệt. */
    protected void cleanUpTestData() {
    }

    // ---------- Step của test ----------

    protected static void arrange(String what, ThrowableRunnableVoid body) {
        Allure.step(ARRANGE + what, body);
    }

    protected static <T> T arrange(String what, ThrowableRunnable<T> body) {
        return Allure.step(ARRANGE + what, body);
    }

    protected static void act(String what, ThrowableRunnableVoid body) {
        Allure.step(ACT + what, body);
    }

    protected static <T> T act(String what, ThrowableRunnable<T> body) {
        return Allure.step(ACT + what, body);
    }

    protected static void verify(String what, ThrowableRunnableVoid body) {
        Allure.step(ASSERT + what, body);
    }

    protected static <T> T verify(String what, ThrowableRunnable<T> body) {
        return Allure.step(ASSERT + what, body);
    }

    /** Hành động và kiểm tra không tách được (VD mỗi vòng lặp bấm rồi kiểm ngay). */
    protected static void actAndVerify(String what, ThrowableRunnableVoid body) {
        Allure.step(ACT_AND_ASSERT + what, body);
    }

    protected static <T> T actAndVerify(String what, ThrowableRunnable<T> body) {
        return Allure.step(ACT_AND_ASSERT + what, body);
    }

    protected static void cleanUp(String what, ThrowableRunnableVoid body) {
        Allure.step(CLEANUP + what, body);
    }

    // ---------- Dùng chung ----------

    /** Gắn TC ID (truy vết RTM) và tag cho test: hiện ở Allure, log và bảng tổng hợp. */
    protected void tc(String testId, String... tags) {
        Allure.label("testId", testId);
        for (String tag : tags) {
            Allure.label("tag", tag);
        }
        ThreadContext.put(TestListener.TEST_ID, testId);
        LogUtils.info("TC ID = {}", testId);
    }

    /** Tiền đề dùng chung: đăng nhập admin bằng tài khoản trong .env / biến môi trường. */
    protected DashboardPage loginAsAdmin() {
        return arrange("Đăng nhập tài khoản admin", () -> {
            return new LoginPage().open().login(config.getUsername(), config.getPassword()).waitUntilReadyToWork();
        });
    }
}
