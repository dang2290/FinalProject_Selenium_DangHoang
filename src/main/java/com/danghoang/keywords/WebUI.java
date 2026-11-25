package com.danghoang.keywords;

import com.danghoang.config.ConfigReader;
import com.danghoang.drivers.DriverManager;
import com.danghoang.reporting.AllureManager;
import com.danghoang.utils.LogUtils;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Lớp chung DUY NHẤT gọi Selenium. Page không gọi driver / findElement, chỉ gọi WebUI.
 * <ul>
 *   <li><b>Hành động</b> (mở, bấm, nhập…): tạo một Allure step; AllureLogListener ghi chính step đó ra log,
 *       nên report và log luôn cùng câu chữ.</li>
 *   <li><b>Đọc</b> (text, value, trạng thái): chỉ ghi log để report không rối. Đọc lặp trong lúc chờ ghi ở mức DEBUG.</li>
 *   <li><b>Mật khẩu</b>: luôn thay bằng ******. Không đặt {@code @Step} ở đây: Allure sẽ ghi mọi tham số.</li>
 * </ul>
 * Dùng như trong sườn: gọi static ({@code WebUI.clickElement(BY)}), driver lấy từ DriverManager theo thread đang chạy.
 */
public final class WebUI {

    private static final Pattern SECRET = Pattern.compile("password(?!/)", Pattern.CASE_INSENSITIVE);
    private static final String MASK = "******";
    private static final int MAX_LOGGED_CHARS = 120;
    private static final String SCROLL_TO_CENTER = "arguments[0].scrollIntoView({block:'center'});";
    private static final String SET_VALUE =
            "arguments[0].value=arguments[1];arguments[0].dispatchEvent(new Event('input',{bubbles:true}));";
    private static final String FOCUS_STYLE = "const c=getComputedStyle(arguments[0]);"
            + "return [c.outlineStyle,c.outlineColor,c.boxShadow,c.borderColor,c.backgroundColor].join('|');";
    private static final String BLUR = "if(document.activeElement)document.activeElement.blur();";
    private static final String TEXTS_BY_CSS = "return [...document.querySelectorAll(arguments[0])].map(e => e.textContent.trim());";
    private static final String VALUES_BY_CSS = "return [...document.querySelectorAll(arguments[0])].map(e => e.value);";
    private static final String IMAGE_LOADED = "const i=arguments[0];return i.complete && i.naturalWidth>0;";
    private static final String FOCUSED_PROPERTY = "const a=document.activeElement;return a ? (a[arguments[0]] || '') : '';";
    private static final String CURRENT_PATH = "return location.pathname;";
    private static final String FOCUS = "arguments[0].focus();";

    private static final ThreadLocal<Integer> WAITING = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> ACTIONS = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<WebDriver> PAGE_SCRIPTS_DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<Set<String>> PAGE_SCRIPTS = ThreadLocal.withInitial(HashSet::new);

    private WebUI() {}

    /** Page đăng ký khi được tạo để log hiện tên hằng thay vì chuỗi locator. */
    public static void registerPage(Object page) {
        ElementNames.register(page);
    }

    /**
     * Cho trình duyệt chạy sẵn đoạn JS này ở đầu MỌI trang tải sau đó (tab hiện tại), mỗi trình duyệt cài một lần.
     * Dùng khi cần thấy thứ xuất hiện rồi biến mất trước khi trang tải xong. Chỉ Chrome / Edge có cách này;
     * trình duyệt khác bỏ qua và ghi cảnh báo, Page vẫn chạy theo cách cũ.
     */
    public static void runOnEveryPage(String script) {
        WebDriver driver = getDriver();
        if (driver == null) {
            return;
        }
        if (PAGE_SCRIPTS_DRIVER.get() != driver) {
            PAGE_SCRIPTS_DRIVER.set(driver);
            PAGE_SCRIPTS.get().clear();
        }
        if (!PAGE_SCRIPTS.get().add(script)) {
            return;
        }
        if (driver instanceof HasCdp cdp) {
            cdp.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of("source", script));
        } else {
            LogUtils.warn("{} không cài được JS chạy sẵn trên mọi trang, bỏ qua", driver.getClass().getSimpleName());
        }
    }

    /** Gọi khi test kết thúc. */
    public static void clearContext() {
        ElementNames.clear();
        WAITING.remove();
        ACTIONS.remove();
        PAGE_SCRIPTS_DRIVER.remove();
        PAGE_SCRIPTS.remove();
    }

    /** Số hành động (mở, bấm, nhập…) test đã làm: thứ gì ghi lại trước hành động mới nhất thì đã cũ. */
    public static int getActionCount() {
        return ACTIONS.get();
    }

    private static WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    private static WebDriverWait newWait() {
        WebDriverWait created = new WebDriverWait(getDriver(), defaultTimeout());
        // Bảng / lưới vẽ lại giữa hai lần đọc thì đọc lại ở lần chờ kế tiếp
        created.ignoring(StaleElementReferenceException.class);
        return created;
    }

    private static Duration defaultTimeout() {
        return Duration.ofSeconds(ConfigReader.getInstance().getTimeout());
    }

    // ==================== Điều hướng, cửa sổ ====================

    public static void openURL(String url) {
        action("Mở " + url, () -> getDriver().get(url));
    }

    public static void waitForPageLoaded() {
        newWait().until(d -> "complete".equals(executeJS("return document.readyState")));
    }

    public static void refreshPage() {
        action("Tải lại trang", () -> getDriver().navigate().refresh());
    }

    /**
     * Bấm Back và BẮT BUỘC xác nhận đã rời document hiện tại, không rời được thì lỗi.
     * So cả URL lẫn dấu trên document: Back bị chuyển hướng về đúng URL cũ vẫn là đã điều hướng.
     */
    public static void navigateBack() {
        action("Bấm Back của trình duyệt", () -> {
            String before = getDriver().getCurrentUrl();
            String marker = UUID.randomUUID().toString();
            executeJS("window.__aecBackFrom = arguments[0];", marker);
            getDriver().navigate().back();
            newWait().withMessage(() -> "Bấm Back nhưng trình duyệt không rời trang " + before)
                    .ignoring(JavascriptException.class)
                    .until(d -> !before.equals(d.getCurrentUrl()) || !marker.equals(executeJS("return window.__aecBackFrom;")));
            waitForPageLoaded();
        });
    }

    public static void setWindowSize(int width, int height) {
        action("Đặt cửa sổ trình duyệt " + width + "×" + height,
                () -> getDriver().manage().window().setSize(new Dimension(width, height)));
    }

    public static String getCurrentWindowHandle() {
        return getDriver().getWindowHandle();
    }

    public static String openNewTab() {
        return action("Mở tab mới", () -> {
            getDriver().switchTo().newWindow(WindowType.TAB);
            return getDriver().getWindowHandle();
        });
    }

    public static void switchToWindow(String handle) {
        action("Chuyển sang tab khác", () -> {
            getDriver().switchTo().window(handle);
        });
    }

    public static void closeCurrentTab(String handle) {
        action("Đóng tab hiện tại, quay về tab trước", () -> {
            getDriver().close();
            getDriver().switchTo().window(handle);
        });
    }

    public static void switchToNewWindow(String originalHandle) {
        action("Chuyển sang tab vừa mở", () -> {
            String handle = newWait().until(d -> d.getWindowHandles().stream()
                    .filter(h -> !h.equals(originalHandle)).findFirst().orElse(null));
            getDriver().switchTo().window(handle);
            waitForPageLoaded();
        });
    }

    public static boolean isAlertPresent() {
        try {
            getDriver().switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    public static void dismissAlertIfPresent() {
        try {
            getDriver().switchTo().alert().dismiss();
            LogUtils.info("Đã đóng hộp thoại của trình duyệt");
        } catch (NoAlertPresentException e) {
            LogUtils.debug("Không có hộp thoại cần đóng");
        }
    }

    public static String getCurrentURL() {
        return getDriver().getCurrentUrl();
    }

    public static String getPageTitle() {
        return getDriver().getTitle();
    }

    // ==================== Bấm ====================

    /** Chờ bấm được rồi bấm. */
    public static void clickElement(By by) {
        action("Bấm " + name(by), () -> newWait().until(ExpectedConditions.elementToBeClickable(by)).click());
    }

    /** Bấm ngay, không chờ (khi cần thấy trình duyệt chặn form). */
    public static void clickElementNow(By by) {
        action("Bấm " + name(by), () -> getDriver().findElement(by).click());
    }

    public static void clickElement(WebElement element, String what) {
        action("Bấm [" + what + "]", () -> element.click());
    }

    public static void scrollAndClickElement(By by) {
        action("Cuộn tới và bấm " + name(by), () -> {
            WebElement element = getDriver().findElement(by);
            executeJS(SCROLL_TO_CENTER, element);
            element.click();
        });
    }

    /** Tìm rồi bấm trong lúc chờ: phần tử bị vẽ lại ngay trước khi bấm thì tìm lại. */
    public static void clickWhenFound(String what, Supplier<WebElement> finder) {
        action("Bấm [" + what + "]", () -> {
            newWait().until(d -> {
                finder.get().click();
                return true;
            });
        });
    }

    /** Nút bị giấu / bị che thì bấm bằng JS, và ghi cảnh báo vào cả log lẫn report để không che lỗi giao diện. */
    public static void clickElementOrByJS(WebElement element, String what) {
        action("Bấm [" + what + "]", () -> {
            try {
                element.click();
            } catch (ElementNotInteractableException e) {
                LogUtils.warn("[{}] bị giấu hoặc bị che, chuyển sang bấm qua JS", what);
                Allure.step("Cảnh báo: [" + what + "] bị che, bấm qua JS");
                executeJS("arguments[0].click();", element);
            }
        });
    }

    public static void clickElementByJS(By by) {
        action("Bấm bằng JS " + name(by), () -> {
            executeJS("arguments[0].click();", getDriver().findElement(by));
        });
    }

    public static void clickElementByJS(WebElement element, String what) {
        action("Bấm bằng JS [" + what + "]", () -> {
            executeJS("arguments[0].click();", element);
        });
    }

    /** Chờ ngắn phần tử hiện rồi bấm. */
    public static void clickElement(By by, Duration timeout) {
        WebElement element = new WebDriverWait(getDriver(), timeout).until(ExpectedConditions.visibilityOfElementLocated(by));
        clickElement(element, ElementNames.of(by));
    }

    public static void doubleClickElement(By by) {
        action("Bấm đúp " + name(by), () -> new Actions(getDriver()).doubleClick(waitForElementVisible(by)).perform());
    }

    public static void doubleClickClickableElement(By by) {
        action("Bấm đúp " + name(by),
                () -> new Actions(getDriver()).doubleClick(newWait().until(ExpectedConditions.elementToBeClickable(by))).perform());
    }

    public static void doubleClickElement(WebElement element, String what) {
        action("Bấm đúp [" + what + "]", () -> new Actions(getDriver()).doubleClick(element).perform());
    }

    // ==================== Nhập, bàn phím, chuột ====================

    public static void setText(By by, String value) {
        action("Nhập " + shown(by, value) + " vào " + name(by), () -> clearAndSend(by, value));
    }

    /** Ô đã có giá trị: chọn hết rồi gõ đè (clear() không phát sự kiện bàn phím). Chuỗi rỗng = xoá hết. */
    public static void replaceText(By by, String value) {
        String title = value.isEmpty() ? "Xoá hết " + name(by) : "Gõ đè " + shown(by, value) + " vào " + name(by);
        action(title, () -> {
            waitForElementVisible(by).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
            if (!value.isEmpty()) {
                clearAndSend(by, value);
            }
        });
    }

    /** Ô ở form dài: cuộn vào giữa màn hình rồi nhập. */
    public static void scrollAndSetText(By by, String value) {
        action("Nhập " + shown(by, value) + " vào " + name(by), () -> {
            WebElement field = getDriver().findElement(by);
            executeJS(SCROLL_TO_CENTER, field);
            field.clear();
            if (!value.isEmpty()) {
                clearAndSend(by, value);
            }
        });
    }

    public static void setTextAndKey(By by, String value, Keys key) {
        action("Nhập " + shown(by, value) + " vào " + name(by) + " rồi nhấn " + key.name(), () -> {
            WebElement field = waitForElementVisible(by);
            field.clear();
            field.sendKeys(value, key);
        });
    }

    /** Ô tự dựng (tag, trình soạn thảo): cuộn tới, bấm vào, gõ. */
    public static void clickAndType(By by, CharSequence... keys) {
        action("Gõ " + shown(by, describe(keys)) + " vào " + name(by), () -> {
            WebElement field = getDriver().findElement(by);
            executeJS(SCROLL_TO_CENTER, field);
            field.click();
            field.sendKeys(keys);
        });
    }

    /** Ô có thể bị giấu ở màn hẹp: hiện thì gõ, ẩn thì đặt giá trị như khi gõ. */
    public static void setTextEvenIfHidden(WebElement field, String value, String what) {
        action("Nhập [" + value + "] vào [" + what + "]", () -> {
            if (field.isDisplayed()) {
                field.clear();
                field.sendKeys(value);
            } else {
                executeJS(SET_VALUE, field, value);
            }
        });
    }

    public static void pressKey(By by, Keys key) {
        action("Nhấn " + key.name() + " ở " + name(by), () -> getDriver().findElement(by).sendKeys(key));
    }

    public static void pressKey(WebElement element, Keys key, String what) {
        action("Nhấn " + key.name() + " ở [" + what + "]", () -> element.sendKeys(key));
    }

    /** Nhấn phím ở phần tử đang focus. */
    public static void pressKey(Keys key) {
        action("Nhấn " + key.name(), () -> new Actions(getDriver()).sendKeys(key).perform());
    }

    public static void typeAtFocus(String text) {
        action("Gõ [" + text + "] tại chỗ đang focus", () -> new Actions(getDriver()).sendKeys(text).perform());
    }

    /** Rê chuột lên phần tử, trả về chú thích (thuộc tính title). */
    public static String getTooltipOnHover(WebElement element, String what) {
        return action("Rê chuột lên [" + what + "]", () -> {
            new Actions(getDriver()).moveToElement(element).perform();
            return element.getAttribute("title");
        });
    }

    public static void hoverElement(WebElement element, String what) {
        action("Rê chuột lên [" + what + "]", () -> new Actions(getDriver()).moveToElement(element).perform());
    }

    public static void scrollToElementAtCenter(By by) {
        executeJS(SCROLL_TO_CENTER, getDriver().findElement(by));
    }

    public static void scrollToElementAtCenter(WebElement element) {
        executeJS(SCROLL_TO_CENTER, element);
    }

    // ==================== Đọc ====================

    /** Chờ hiện rồi đọc chữ. */
    public static String getElementText(By by) {
        return read("chữ", by, waitForElementVisible(by).getText().trim());
    }

    /** Đọc chữ ngay, không chờ. */
    public static String getElementTextNow(By by) {
        return read("chữ", by, getDriver().findElement(by).getText().trim());
    }

    public static String getElementRawText(By by) {
        return read("chữ", by, getDriver().findElement(by).getText());
    }

    public static String getElementValue(By by) {
        String value = getDriver().findElement(by).getAttribute("value");
        read("giá trị", by, isSecret(by) ? MASK : value);
        return value;
    }

    public static String getElementAttribute(By by, String attribute) {
        String value = getDriver().findElement(by).getAttribute(attribute);
        read(attribute, by, isSecret(by) && "value".equals(attribute) ? MASK : value);
        return value;
    }

    public static List<String> getAllElementsText(By by) {
        return read("danh sách chữ", by, getDriver().findElements(by).stream().map(e -> e.getText().trim()).toList());
    }

    public static List<String> getAllElementsAttribute(By by, String attribute) {
        return read("danh sách " + attribute, by,
                getDriver().findElements(by).stream().map(e -> e.getAttribute(attribute)).toList());
    }

    public static String getSelectedOptionText(By by) {
        return read("lựa chọn", by, new Select(getDriver().findElement(by)).getFirstSelectedOption().getText().trim());
    }

    public static List<String> getAllOptionsText(By by) {
        return read("các lựa chọn", by, new Select(getDriver().findElement(by)).getOptions().stream()
                .map(o -> o.getAttribute("textContent").trim()).toList());
    }

    public static String getValidationMessage(By by) {
        return read("cảnh báo của trình duyệt", by,
                (String) executeJS("return arguments[0].validationMessage;", getDriver().findElement(by)));
    }

    public static boolean isElementSelected(By by) {
        return read("đang chọn", by, getDriver().findElement(by).isSelected());
    }

    public static boolean isElementEnabled(By by) {
        return read("đang bật", by, getDriver().findElement(by).isEnabled());
    }

    public static boolean isElementFocused(By by) {
        return read("đang focus", by, getDriver().switchTo().activeElement().equals(getDriver().findElement(by)));
    }

    /** Có trong trang (kể cả đang ẩn). */
    public static boolean checkElementExist(By by) {
        return read("có trong trang", by, !getDriver().findElements(by).isEmpty());
    }

    public static int getElementCount(By by) {
        return read("số phần tử", by, getDriver().findElements(by).size());
    }

    /** Đang hiện ngay lúc này, không chờ. */
    public static boolean isElementDisplayedNow(By by) {
        return read("đang hiện", by, getDriver().findElements(by).stream().anyMatch(WebElement::isDisplayed));
    }

    /** Chờ hiện; hết giờ thì trả false. */
    public static boolean isElementVisible(By by) {
        boolean visible;
        try {
            visible = waitForElementVisible(by).isDisplayed();
        } catch (TimeoutException e) {
            visible = false;
        }
        return read("hiện sau khi chờ", by, visible);
    }

    // ==================== Tìm phần tử (cho bảng, lưới nhiều dòng) ====================

    public static WebElement getWebElement(By by) {
        return getDriver().findElement(by);
    }

    public static List<WebElement> getWebElements(By by) {
        return getDriver().findElements(by);
    }

    public static WebElement getWebElement(WebElement parent, By by) {
        return parent.findElement(by);
    }

    public static List<WebElement> getWebElements(WebElement parent, By by) {
        return parent.findElements(by);
    }

    public static WebElement getActiveElement() {
        return getDriver().switchTo().activeElement();
    }

    // ==================== Chờ ====================

    public static WebElement waitForElementVisible(By by) {
        return newWait().until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public static void waitForAnyVisible(By first, By second) {
        newWait().until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(first),
                ExpectedConditions.visibilityOfElementLocated(second)));
    }

    public static void waitForElementClickable(By by) {
        newWait().until(ExpectedConditions.elementToBeClickable(by));
    }

    public static void waitForElementInVisible(By by) {
        newWait().until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    /** Chờ biến mất; hết giờ mà còn thì trả false. */
    public static boolean waitUntilElementGone(By by) {
        boolean gone;
        try {
            gone = newWait().until(ExpectedConditions.invisibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            gone = false;
        }
        return read("đã biến mất", by, gone);
    }

    public static void waitForCurrentURLContains(String fragment) {
        LogUtils.info("Chờ địa chỉ chứa [{}]", fragment);
        newWait().until(ExpectedConditions.urlContains(fragment));
    }

    /** Chờ điều kiện riêng của trang. what được đưa vào log và vào câu lỗi khi hết giờ. */
    public static <T> T waitUntil(String what, Supplier<T> condition) {
        return until(newWait(), what, condition);
    }

    public static <T> T waitUntil(String what, Duration timeout, Supplier<T> condition) {
        return until(new WebDriverWait(getDriver(), timeout), what, condition);
    }

    private static <T> T until(WebDriverWait customWait, String what, Supplier<T> condition) {
        LogUtils.info("Chờ {}", what);
        customWait.withMessage(() -> what);
        WAITING.set(WAITING.get() + 1);
        try {
            return customWait.until(d -> condition.get());
        } finally {
            WAITING.set(WAITING.get() - 1);
        }
    }

    /** Chờ jQuery không còn request AJAX nào đang chạy (trang không dùng jQuery thì bỏ qua). */
    public static void waitForJQueryLoad() {
        waitUntil("các request AJAX chạy xong", defaultTimeout().multipliedBy(4),
                () -> Boolean.TRUE.equals(executeJS("return !window.jQuery || window.jQuery.active === 0;")));
    }

    // ==================== JavaScript, focus bàn phím ====================

    public static Object executeJS(String script, Object... args) {
        return ((JavascriptExecutor) getDriver()).executeScript(script, args);
    }

    // Đọc nhanh bằng JS: một lần gọi cho cả danh sách dài, không bị stale khi bảng vẽ lại giữa chừng

    /** Chữ (đã trim) của mọi phần tử khớp CSS selector, theo thứ tự trên trang. */
    @SuppressWarnings("unchecked")
    public static List<String> getAllTextsByJS(String cssSelector) {
        return (List<String>) executeJS(TEXTS_BY_CSS, cssSelector);
    }

    /** Giá trị (value) của mọi ô khớp CSS selector, theo thứ tự trên trang. */
    @SuppressWarnings("unchecked")
    public static List<String> getAllValuesByJS(String cssSelector) {
        return (List<String>) executeJS(VALUES_BY_CSS, cssSelector);
    }

    /** Ảnh đã tải xong và có nội dung (không phải ảnh vỡ). */
    public static boolean isImageLoaded(WebElement image) {
        return Boolean.TRUE.equals(executeJS(IMAGE_LOADED, image));
    }

    /** Thuộc tính DOM (VD "id", "name") của phần tử đang focus; "" nếu không có. */
    public static String getFocusedProperty(String property) {
        return (String) executeJS(FOCUSED_PROPERTY, property);
    }

    /** Đường dẫn của trang (không có tên miền, không có tham số). */
    public static String getCurrentPath() {
        return (String) executeJS(CURRENT_PATH);
    }

    /** Đặt focus vào phần tử mà không bấm (để nhấn phím ngay tại đó). */
    public static void focusElement(WebElement element) {
        executeJS(FOCUS, element);
    }

    /**
     * Bấm vào start rồi nhấn Tab count lần, trả về phần tử nhận focus sau mỗi lần:
     * name của ô nhập · name của select gốc nếu là nút bootstrap-select · chữ trên nút.
     */
    public static List<String> tabSequenceFrom(By start, int count) {
        return tabSequenceFrom(start, count, "const a=document.activeElement,bs=a.closest('.bootstrap-select');"
                + "return bs ? bs.querySelector('select').name : (a.name||a.innerText.trim());");
    }

    /** Như trên, describeFocused: script mô tả phần tử đang focus. */
    public static List<String> tabSequenceFrom(By start, int count, String describeFocused) {
        return action("Nhấn Tab " + count + " lần từ " + name(start), () -> {
            getDriver().findElement(start).click();
            List<String> sequence = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                new Actions(getDriver()).sendKeys(Keys.TAB).perform();
                sequence.add((String) executeJS(describeFocused));
            }
            return sequence;
        });
    }

    /**
     * Ô nhập nào trông giống hệt nhau khi đang focus và khi không focus.
     * Bỏ focus bằng blur() thay vì bấm ra chỗ khác: chỗ bấm có thể bị header cố định che khi trang đã cuộn.
     */
    public static List<String> fieldsWithoutVisibleFocus(List<By> fields, List<String> labels) {
        return action("So viền khi focus của " + labels, () -> {
            List<String> invisible = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                WebElement field = getDriver().findElement(fields.get(i));
                executeJS(SCROLL_TO_CENTER + BLUR, field);
                String blurred = stableStyle(field);
                field.click();
                if (stableStyle(field).equals(blurred)) {
                    invisible.add(labels.get(i));
                }
            }
            executeJS(BLUR);
            return invisible;
        });
    }

    /** Bấm vào from rồi nhấn Tab một lần tới target: target có đổi kiểu so với lúc chưa focus không. */
    public static boolean changesStyleWhenTabbedTo(By from, By target) {
        return action("Tab từ " + name(from) + " tới " + name(target) + ", so viền", () -> {
            WebElement element = getDriver().findElement(target);
            executeJS(SCROLL_TO_CENTER + BLUR, element);
            String blurred = stableStyle(element);
            getDriver().findElement(from).click();
            new Actions(getDriver()).sendKeys(Keys.TAB).perform();
            return getDriver().switchTo().activeElement().equals(element) && !stableStyle(element).equals(blurred);
        });
    }

    /** Kiểu viền sau khi hiệu ứng chuyển màu đã dừng (2 lần đọc liên tiếp giống nhau). */
    private static String stableStyle(WebElement element) {
        String[] last = {""};
        return newWait().until(d -> {
            String now = (String) executeJS(FOCUS_STYLE, element);
            boolean stable = now.equals(last[0]);
            last[0] = now;
            return stable ? now : null;
        });
    }

    // ==================== Nội bộ ====================

    private static void action(String title, Allure.ThrowableRunnableVoid body) {
        ACTIONS.set(ACTIONS.get() + 1);
        Allure.step(title, () -> {
            body.run();
            screenshotEachStep(title);
        });
    }

    private static <T> T action(String title, Allure.ThrowableRunnable<T> body) {
        ACTIONS.set(ACTIONS.get() + 1);
        return Allure.step(title, () -> {
            T result = body.run();
            screenshotEachStep(title);
            return result;
        });
    }

    /** Công tắc SCREENSHOT_ALL_STEPS: chụp SAU thao tác để thấy kết quả của thao tác. Mặc định tắt cho nhanh. */
    private static void screenshotEachStep(String title) {
        if (ConfigReader.getInstance().isScreenshotAllSteps()) {
            AllureManager.saveScreenshot(title, "step_" + System.nanoTime());
        }
    }

    private static void clearAndSend(By by, String value) {
        WebElement element = waitForElementVisible(by);
        element.clear();
        if (value.codePoints().anyMatch(cp -> cp > 0xFFFF)) {
            // ChromeDriver không gõ được ký tự ngoài BMP (emoji): đặt giá trị và phát sự kiện input như khi gõ
            executeJS(SET_VALUE, element, value);
        } else {
            element.sendKeys(value);
        }
    }

    private static <T> T read(String what, By by, T value) {
        if (WAITING.get() > 0) {
            LogUtils.debug("Đọc {} của {} = {}", what, name(by), value);
        } else {
            LogUtils.info("Đọc {} của {} = {}", what, name(by), shorten(String.valueOf(value)));
        }
        return value;
    }

    private static String name(By by) {
        return "[" + ElementNames.of(by) + "]";
    }

    private static boolean isSecret(By by) {
        return SECRET.matcher(by.toString()).find() || SECRET.matcher(ElementNames.fieldOf(by)).find();
    }

    private static String shown(By by, String value) {
        return "[" + (isSecret(by) ? MASK : shorten(value)) + "]";
    }

    private static String describe(CharSequence... keys) {
        List<String> parts = new ArrayList<>();
        for (CharSequence key : keys) {
            parts.add(key instanceof Keys special ? "<" + special.name() + ">" : key.toString());
        }
        return String.join(" ", parts);
    }

    private static String shorten(String text) {
        String oneLine = text.replaceAll("[\\r\\n]+", " ");
        return oneLine.length() <= MAX_LOGGED_CHARS ? oneLine
                : oneLine.substring(0, MAX_LOGGED_CHARS) + "… (" + oneLine.length() + " ký tự)";
    }
}
