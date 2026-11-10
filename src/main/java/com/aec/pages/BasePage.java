package com.aec.pages;

import com.aec.config.ConfigReader;
import com.aec.utils.WaitHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePage {

    protected static final By NOTIFY_MESSAGE = By.cssSelector("[data-notify='container']");
    private static final String FOCUS_STYLE_SCRIPT = "const c=getComputedStyle(arguments[0]);"
            + "return [c.outlineStyle,c.outlineColor,c.boxShadow,c.borderColor,c.backgroundColor].join('|');";

    private static final Duration NOTIFY_SHORT_WAIT = Duration.ofSeconds(4);

    protected final Logger log = LogManager.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    private String lastPageNotify = "";

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = WaitHelper.create(driver);
    }

    protected void navigate(String path) {
        String url = ConfigReader.getInstance().getBaseUrl() + path;
        log.info("Mở {}", url);
        driver.get(url);
        WaitHelper.waitForPageReady(driver);
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        log.info("Bấm {}", locator);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String value) {
        log.info("Nhập vào {}", locator);
        WebElement element = waitForVisible(locator);
        element.clear();
        if (value.codePoints().anyMatch(cp -> cp > 0xFFFF)) {
            // ChromeDriver không gõ được ký tự ngoài BMP (emoji) — đặt giá trị và phát sự kiện input như khi gõ
            js("arguments[0].value=arguments[1];arguments[0].dispatchEvent(new Event('input',{bubbles:true}));",
                    element, value);
        } else {
            element.sendKeys(value);
        }
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected boolean isVisible(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean isPresentNow(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    protected void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    /** Chờ trang chuyển tới đúng đường dẫn (không tính tham số). */
    public void waitForPath(String path) {
        try {
            wait.until(d -> path.equals(currentPath()));
        } catch (TimeoutException e) {
            log.warn("Không tới {}, đang ở {}", path, currentPath());
        }
    }

    /**
     * Bấm vào start rồi nhấn Tab count lần, trả về phần tử nhận focus sau mỗi lần:
     * name của ô nhập · name của select gốc nếu là nút bootstrap-select · chữ trên nút.
     */
    protected List<String> tabSequenceFrom(By start, int count) {
        driver.findElement(start).click();
        List<String> sequence = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            new Actions(driver).sendKeys(Keys.TAB).perform();
            sequence.add((String) js("const a=document.activeElement,bs=a.closest('.bootstrap-select');"
                    + "return bs ? bs.querySelector('select').name : (a.name||a.innerText.trim());"));
        }
        return sequence;
    }

    /**
     * Ô nhập nào trông giống hệt nhau khi đang focus và khi không focus.
     * Bỏ focus bằng blur() thay vì bấm ra chỗ khác — chỗ bấm có thể bị header cố định che khi trang đã cuộn.
     */
    protected List<String> fieldsWithoutVisibleFocus(List<By> fields, List<String> names) {
        List<String> invisible = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            WebElement field = driver.findElement(fields.get(i));
            js("arguments[0].scrollIntoView({block:'center'});if(document.activeElement)document.activeElement.blur();", field);
            String blurred = stableStyle(field);
            field.click();
            if (stableStyle(field).equals(blurred)) {
                invisible.add(names.get(i));
            }
        }
        js("if(document.activeElement)document.activeElement.blur();");
        return invisible;
    }

    /** Bấm vào from rồi nhấn Tab một lần tới target — target có đổi kiểu so với lúc chưa focus không. */
    protected boolean changesStyleWhenTabbedTo(By from, By target) {
        WebElement element = driver.findElement(target);
        js("arguments[0].scrollIntoView({block:'center'});if(document.activeElement)document.activeElement.blur();", element);
        String blurred = stableStyle(element);
        driver.findElement(from).click();
        new Actions(driver).sendKeys(Keys.TAB).perform();
        return driver.switchTo().activeElement().equals(element) && !stableStyle(element).equals(blurred);
    }

    /** Kiểu viền sau khi hiệu ứng chuyển màu đã dừng (2 lần đọc liên tiếp giống nhau). */
    private String stableStyle(WebElement element) {
        String[] last = {""};
        return wait.until(d -> {
            String now = (String) js(FOCUS_STYLE_SCRIPT, element);
            boolean stable = now.equals(last[0]);
            last[0] = now;
            return stable ? now : null;
        });
    }

    /** Làm hành động khiến trang tải lại, chờ trang mới tải xong. */
    protected void andWaitForNewPage(Runnable action) {
        markPage();
        action.run();
        wait.until(d -> !isSamePageInstance());
        WaitHelper.waitForPageReady(driver);
        // Thông báo nổi hiện ngay khi trang tải xong và tự tắt sau vài giây — trang nặng (bảng ~600 dòng)
        // dựng xong thì thông báo có thể đã tắt, nên ghi lại ngay lúc này
        lastPageNotify = currentNotifyText();
    }

    /** Chữ của thông báo nổi mới nhất đang có trên trang ("" nếu không có) — đọc một lần bằng JS, không bị stale. */
    private String currentNotifyText() {
        return (String) js("const t=[...document.querySelectorAll(\"[data-notify='container']\")];if(!t.length)return '';"
                + "const e=t[t.length-1];const m=e.querySelector(\"[data-notify='message']\");return (m||e).innerText.trim();");
    }

    protected Object js(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    /** Thông báo bật lỗi của trình duyệt tại ô (rỗng = ô hợp lệ). */
    protected String validationMessage(By locator) {
        return (String) js("return arguments[0].validationMessage;", driver.findElement(locator));
    }

    /** Đánh dấu trang hiện tại — trang tải lại thì dấu mất. */
    public void markPage() {
        js("window.__aecMark = true;");
    }

    public boolean isSamePageInstance() {
        return Boolean.TRUE.equals(js("return window.__aecMark === true;"));
    }

    /** Nội dung thông báo nổi mới nhất (góc dưới trái). */
    public String getNotifyMessage() {
        // Chờ ngắn thông báo đang hiện (kể cả thông báo đến sau qua AJAX); đã tắt thì dùng bản ghi lúc trang vừa tải
        try {
            return new WebDriverWait(driver, NOTIFY_SHORT_WAIT).until(d -> {
                String text = currentNotifyText();
                return text.isEmpty() ? null : text;
            });
        } catch (TimeoutException e) {
            if (!lastPageNotify.isEmpty()) {
                return lastPageNotify;
            }
        }
        return wait.until(d -> {
            String text = currentNotifyText();
            return text.isEmpty() ? null : text;
        });
    }

    /** Có hộp thoại alert của trình duyệt đang mở không (dấu hiệu script bị thực thi). */
    public boolean isAlertOpen() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    /**
     * Trang lỗi máy chủ hoặc trang trắng: Laravel "Server Error", trang "Something went wrong! … Error code: 500",
     * hoặc trang 403 của tường lửa LiteSpeed ("Access to this resource on the server is denied!").
     */
    public boolean isServerErrorPage() {
        String body = driver.findElement(By.tagName("body")).getText();
        return driver.getTitle().contains("Server Error") || body.contains("Server Error")
                || body.contains("Something went wrong!") || body.contains("Access to this resource on the server is denied")
                || body.isBlank();
    }

    /** Trang "Page Not Found!" của website (id không tồn tại). */
    public boolean isPageNotFound() {
        return !driver.findElements(By.xpath("//h1[normalize-space()='Page Not Found!']")).isEmpty();
    }

    public boolean hasNoHorizontalScroll() {
        return Boolean.TRUE.equals(js("return document.documentElement.scrollWidth <= window.innerWidth;"));
    }

    /** Chờ tab mới (mở bằng target=_blank) xuất hiện rồi chuyển sang tab đó. */
    public void switchToNewWindow(String originalHandle) {
        String handle = wait.until(d -> d.getWindowHandles().stream().filter(h -> !h.equals(originalHandle)).findFirst().orElse(null));
        driver.switchTo().window(handle);
        WaitHelper.waitForPageReady(driver);
    }

    /** Chờ jQuery không còn request AJAX nào đang chạy (trang không dùng jQuery thì bỏ qua). */
    public void waitForAjaxIdle() {
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInstance().getTimeout() * 4L)).until(d -> Boolean.TRUE.equals(
                js("return !window.jQuery || window.jQuery.active === 0;")));
    }

    /** Toàn bộ chữ đang hiện trên trang. */
    public String bodyText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String currentPath() {
        return (String) js("return location.pathname;");
    }
}
