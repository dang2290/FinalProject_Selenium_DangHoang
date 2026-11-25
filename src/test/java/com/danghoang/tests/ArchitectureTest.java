package com.danghoang.tests;

import com.danghoang.base.BaseTest;
import com.danghoang.base.ClassReferences;
import com.danghoang.pages.BasePage;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Gác ranh giới POM: Test → Page → WebUI → Selenium. Không mở trình duyệt, không cần mạng.
 * Chạy riêng: {@code mvnw.cmd test -Dtest=ArchitectureTest}
 */
@Feature("Kiến trúc framework")
@Story("Ranh giới Page Object Model")
public class ArchitectureTest extends BaseTest {

    private static final String SELENIUM = "org.openqa.selenium.";
    private static final String DRIVERS = "com.danghoang.drivers.";
    private static final String WEB_UI = "com.danghoang.keywords.WebUI";
    private static final String WEB_ELEMENT = SELENIUM + "WebElement";

    /** Page chỉ được khai locator, phím, và nhận element của bảng để đọc. */
    private static final Set<String> PAGE_MAY_USE = Set.of(SELENIUM + "By", SELENIUM + "Keys", WEB_ELEMENT);
    private static final Set<String> PAGE_MAY_READ = Set.of(
            "equals", "getAttribute", "getTagName", "getText", "isDisplayed", "isEnabled", "isSelected");
    private static final Pattern CLICK_IN_JS = Pattern.compile("\\.(click|submit|requestSubmit)\\(");

    /** Test này chỉ đọc file .class nên không mở trình duyệt. */
    @Override
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
    }

    @Test(groups = {"regression", "architecture"}, description = "Page không thao tác Selenium trực tiếp, mọi thao tác đi qua WebUI")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bytecode của mọi Page: không lấy driver, không tìm / bấm / gõ trên element, không bấm bằng JavaScript tự viết. Thao tác phải qua WebUI để có chờ, step và log.")
    public void testPagesActOnlyThroughWebUI() {
        tc("ARCH_POM_001", "regression", "architecture");
        Map<String, ClassReferences> pages = arrange("Đọc bytecode của package pages", () -> readClasses(BasePage.class));

        List<String> violations = act("Tìm chỗ Page đi tắt qua WebUI", () -> pages.entrySet().stream()
                .flatMap(page -> pageViolations(page.getValue()).map(found -> page.getKey() + " → " + found))
                .toList());

        verify("Không Page nào đi tắt", () -> {
            Assert.assertTrue(pages.containsKey("BasePage.class"), "Phải đọc được package pages");
            Assert.assertEquals(violations, List.of(), "Page phải gọi WebUI thay cho: " + violations);
        });
    }

    @Test(groups = {"regression", "architecture"}, description = "Test không dùng Selenium, WebUI hay driver, chỉ gọi Page")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bytecode của mọi test: không dùng class Selenium, WebUI hoặc DriverManager. Thao tác giao diện phải nằm trong Page.")
    public void testTestsUseOnlyPages() {
        tc("ARCH_POM_002", "regression", "architecture");
        Map<String, ClassReferences> tests = arrange("Đọc bytecode của package tests", () -> readClasses(ArchitectureTest.class));

        List<String> violations = act("Tìm chỗ test đi tắt qua Page", () -> tests.entrySet().stream()
                .flatMap(test -> test.getValue().classes().stream()
                        .filter(used -> used.startsWith(SELENIUM) || used.startsWith(DRIVERS) || used.equals(WEB_UI))
                        .map(used -> test.getKey() + " → " + used))
                .toList());

        verify("Không test nào đi tắt", () -> {
            Assert.assertTrue(tests.containsKey("ArchitectureTest.class"), "Phải đọc được package tests");
            Assert.assertEquals(violations, List.of(), "Test phải gọi Page thay cho: " + violations);
        });
    }

    private static Stream<String> pageViolations(ClassReferences page) {
        Stream<String> classes = page.classes().stream()
                .filter(used -> used.startsWith(DRIVERS) || (used.startsWith(SELENIUM) && !isAllowedInPage(used)));
        Stream<String> methods = page.methods().stream().filter(ArchitectureTest::isDirectElementCall);
        Stream<String> scripts = page.strings().stream()
                .filter(text -> CLICK_IN_JS.matcher(text).find())
                .map(text -> "JavaScript: " + text);
        return Stream.of(classes, methods, scripts).flatMap(found -> found);
    }

    private static boolean isAllowedInPage(String className) {
        return PAGE_MAY_USE.contains(className) || className.endsWith("Exception");
    }

    private static boolean isDirectElementCall(String call) {
        String owner = call.substring(0, call.indexOf('#'));
        String method = call.substring(call.indexOf('#') + 1);
        if (owner.equals(WEB_ELEMENT)) {
            return !PAGE_MAY_READ.contains(method);
        }
        return owner.startsWith(SELENIUM) && method.startsWith("findElement");
    }

    /** Mọi file .class cùng package với class mốc, kể cả class con. */
    private static Map<String, ClassReferences> readClasses(Class<?> anchor) throws IOException, URISyntaxException {
        Path folder = Path.of(anchor.getResource(anchor.getSimpleName() + ".class").toURI()).getParent();
        Map<String, ClassReferences> found = new TreeMap<>();
        try (Stream<Path> files = Files.walk(folder)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".class")).toList()) {
                found.put(folder.relativize(file).toString(), ClassReferences.of(file));
            }
        }
        return found;
    }
}
