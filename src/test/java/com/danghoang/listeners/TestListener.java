package com.danghoang.listeners;

import com.danghoang.config.ConfigReader;
import com.danghoang.keywords.WebUI;
import com.danghoang.reporting.AllureLogListener;
import com.danghoang.reporting.AllureManager;
import com.danghoang.reporting.TestLogCollector;
import com.danghoang.utils.LogUtils;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.ThreadContext;
import org.testng.IConfigurationListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Nơi DUY NHẤT theo dõi vòng đời test. Được gắn bằng {@code @Listeners} ở BaseTest nên chạy testng.xml hay -Dtest đều có.
 * <ol>
 *   <li>Bắt đầu suite: ghi môi trường và nhóm lỗi cho Allure.</li>
 *   <li>Mỗi test: log bắt đầu / kết quả, đính ảnh cuối và nhật ký thao tác vào report.</li>
 *   <li>Lỗi setUp / tearDown, test bị SKIP: vẫn được ghi nhận.</li>
 *   <li>Kết thúc suite: ghi bảng tổng hợp reports/test-summary.md.</li>
 * </ol>
 */
public class TestListener implements ISuiteListener, ITestListener, IInvokedMethodListener, IConfigurationListener {

    /** Trạng thái đầy đủ của một test: TestNG chỉ có PASS / FAIL / SKIP. */
    enum Outcome {
        PASS("Đạt"),
        PASS_KNOWN_ISSUE("Đạt dù gắn knownIssue: kiểm xem lỗi đã được sửa chưa"),
        FAIL("Sai kỳ vọng: lỗi cần xem"),
        FAIL_KNOWN_ISSUE("Sai kỳ vọng: lỗi đã biết"),
        BROKEN("Test không chạy hết: timeout, không thấy element, lỗi môi trường"),
        SKIP("Không chạy");

        private final String meaning;

        Outcome(String meaning) {
            this.meaning = meaning;
        }
    }

    private record Finished(Outcome outcome, String testId, String test, String reason) {}

    public static final String TEST_ID = "tc";
    private static final String TEST_NAME = "test";
    private static final String RECORDED = "recorded";
    private static final int MAX_REASON_CHARS = 160;

    private final Queue<Finished> finished = new ConcurrentLinkedQueue<>();
    private final Queue<String> configFailures = new ConcurrentLinkedQueue<>();
    private final Map<String, AtomicInteger> variants = new ConcurrentHashMap<>();

    // ---------- Suite ----------

    @Override
    public void onStart(ISuite suite) {
        TestLogCollector.install();
        writeReportSetup();
        LogUtils.status("SUITE START {}", suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        Map<Outcome, Long> totals = finished.stream()
                .collect(Collectors.groupingBy(Finished::outcome, TreeMap::new, Collectors.counting()));
        writeSummary(totals);
        LogUtils.status("SUITE DONE total={} {} configFailures={}", finished.size(), totals, configFailures.size());
    }

    // ---------- Vòng đời một test ----------

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isConfigurationMethod() && method.getTestMethod().isBeforeMethodConfiguration()) {
            ThreadContext.clearMap();
            TestLogCollector.startTest();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        ThreadContext.put(TEST_NAME, nameOf(result));
        LogUtils.status("START {}", nameOf(result));
    }

    /** Chạy ngay sau thân test, TRƯỚC khi dọn dữ liệu và đóng trình duyệt: lúc duy nhất chụp đúng trạng thái cuối. */
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isTestMethod()) {
            record(result, outcomeOf(result));
            attachScreenshot(result);
            AllureManager.saveTextLog("nhat_ky_thao_tac", TestLogCollector.currentLog());
        } else if (method.getTestMethod().isAfterMethodConfiguration()) {
            ThreadContext.clearMap();
            TestLogCollector.endTest();
            WebUI.clearContext();
        }
    }

    /** SKIP do setUp hỏng thì thân test không chạy, afterInvocation không được gọi. */
    @Override
    public void onTestSkipped(ITestResult result) {
        if (result.getAttribute(RECORDED) == null) {
            record(result, Outcome.SKIP);
        }
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        String failure = nameOf(result) + ": " + reasonOf(result.getThrowable());
        configFailures.add(failure);
        LogUtils.statusError("CONFIG FAILURE {}", failure);
    }

    // ---------- Chi tiết ----------

    private Outcome outcomeOf(ITestResult result) {
        boolean known = List.of(result.getMethod().getGroups()).contains(AllureLogListener.KNOWN_ISSUE);
        Throwable error = result.getThrowable();
        if (result.isSuccess()) {
            return known ? Outcome.PASS_KNOWN_ISSUE : Outcome.PASS;
        }
        if (error instanceof SkipException || result.getStatus() == ITestResult.SKIP) {
            return Outcome.SKIP;
        }
        if (error instanceof AssertionError) {
            return known ? Outcome.FAIL_KNOWN_ISSUE : Outcome.FAIL;
        }
        return Outcome.BROKEN;
    }

    private void record(ITestResult result, Outcome outcome) {
        result.setAttribute(RECORDED, true);
        String testId = ThreadContext.get(TEST_ID);
        Finished done = new Finished(outcome, testId == null ? "-" : testId, nameOf(result),
                result.isSuccess() ? "" : reasonOf(result.getThrowable()));
        finished.add(done);
        switch (outcome) {
            case PASS, PASS_KNOWN_ISSUE -> LogUtils.status("{} | {} | {}", outcome, done.testId(), done.test());
            case FAIL_KNOWN_ISSUE, SKIP -> LogUtils.statusWarn("{} | {} | {} | {}", outcome, done.testId(), done.test(), done.reason());
            case FAIL, BROKEN -> LogUtils.statusError("{} | {} | {} | {}", outcome, done.testId(), done.test(), done.reason());
        }
    }

    private void attachScreenshot(ITestResult result) {
        String name = result.isSuccess() ? "trang_thai_cuoi_cua_test" : "trang_thai_khi_that_bai";
        AllureManager.saveScreenshot(name, result.getMethod().getMethodName() + "_" + System.currentTimeMillis());
    }

    /** Class.method, biến thể DataProvider có thêm [mã] để log song song không lẫn nhau. */
    private String nameOf(ITestResult result) {
        Object cached = result.getAttribute(TEST_NAME);
        if (cached != null) {
            return cached.toString();
        }
        String name = result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
        if (result.getMethod().isTest() && result.getParameters().length > 0) {
            String first = String.valueOf(result.getParameters()[0]);
            // Chỉ lấy mã biến thể ngắn (a, b, c…), không đưa dữ liệu test (có thể là mật khẩu) vào tên
            String variant = first.matches("[a-z]")
                    ? first : String.valueOf(variants.computeIfAbsent(name, key -> new AtomicInteger()).incrementAndGet());
            name += "[" + variant + "]";
        }
        result.setAttribute(TEST_NAME, name);
        return name;
    }

    private static String reasonOf(Throwable error) {
        if (error == null) {
            return "";
        }
        String first = String.valueOf(error.getMessage()).lines().findFirst().orElse("");
        String shortened = first.length() > MAX_REASON_CHARS ? first.substring(0, MAX_REASON_CHARS) + "…" : first;
        return error.getClass().getSimpleName() + ": " + shortened;
    }

    // ---------- File cho report ----------

    private void writeReportSetup() {
        Path results = Path.of(System.getProperty("allure.results.directory", "reports/allure-results"));
        ConfigReader config = ConfigReader.getInstance();
        try (InputStream categories = getClass().getResourceAsStream("/allure/categories.json")) {
            Files.createDirectories(results);
            if (categories != null) {
                Files.copy(categories, results.resolve("categories.json"), StandardCopyOption.REPLACE_EXISTING);
            }
            Files.write(results.resolve("environment.properties"), List.of(
                    "Base.URL=" + config.getBaseUrl(),
                    "Browser=" + config.getBrowser(),
                    "Headless=" + config.isHeadless(),
                    "Timeout.seconds=" + config.getTimeout(),
                    "Java=" + System.getProperty("java.version"),
                    "OS=" + System.getProperty("os.name")));
        } catch (IOException e) {
            LogUtils.statusWarn("Không ghi được cấu hình report", e);
        }
    }

    private void writeSummary(Map<Outcome, Long> totals) {
        StringBuilder text = new StringBuilder("# Kết quả chạy test, ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append("\n\n")
                .append("Tổng: ").append(finished.size()).append("\n\n| Trạng thái | Số test | Nghĩa |\n|---|---|---|\n");
        for (Outcome outcome : Outcome.values()) {
            text.append("| ").append(outcome).append(" | ").append(totals.getOrDefault(outcome, 0L))
                    .append(" | ").append(outcome.meaning).append(" |\n");
        }
        text.append("\n## Test không đạt\n\n| Trạng thái | TC ID | Test | Lý do |\n|---|---|---|---|\n");
        finished.stream().filter(done -> !done.reason().isEmpty() || done.outcome() == Outcome.SKIP)
                .sorted((a, b) -> a.outcome().compareTo(b.outcome()))
                .forEach(done -> text.append("| ").append(done.outcome()).append(" | ").append(done.testId()).append(" | ")
                        .append(done.test()).append(" | ").append(done.reason().replace("|", "\\|")).append(" |\n"));
        if (!configFailures.isEmpty()) {
            text.append("\n## Lỗi setUp / tearDown\n\n");
            configFailures.forEach(failure -> text.append("- ").append(failure).append('\n'));
        }
        try {
            Files.createDirectories(Path.of("reports"));
            Files.writeString(Path.of("reports", "test-summary.md"), text);
        } catch (IOException e) {
            LogUtils.statusWarn("Không ghi được reports/test-summary.md", e);
        }
    }
}
