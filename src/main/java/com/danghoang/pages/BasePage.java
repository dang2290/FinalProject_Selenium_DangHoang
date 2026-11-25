package com.danghoang.pages;

import com.danghoang.config.ConfigReader;
import com.danghoang.keywords.WebUI;
import com.danghoang.utils.LogUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;

/**
 * Lớp cha của mọi Page: chỉ giữ việc thuộc về TRANG (tải lại, thông báo nổi, trang lỗi).
 * Thao tác element gọi {@code WebUI.xxx(...)}. Page KHÔNG nhận, KHÔNG giữ driver: dùng {@code new LoginPage()}.
 */
public abstract class BasePage {

    protected static final By NOTIFY_MESSAGE = By.cssSelector("[data-notify='container']");
    private static final By BODY = By.tagName("body");
    protected static final By NOT_FOUND_HEADING = By.xpath("//h1[normalize-space()='Page Not Found!']");
    private static final Duration NOTIFY_SHORT_WAIT = Duration.ofSeconds(4);

    /** Trang 419 của Laravel, DOM thật 22-11-2026: title "Page Expired", thân trang có 2 ô "419" và "Page Expired". */
    private static final String EXPIRED_TITLE = "Page Expired";
    private static final By EXPIRED_MESSAGE =
            By.xpath("//div[normalize-space()='419']/following-sibling::div[normalize-space()='Page Expired']");

    /**
     * Chạy ở đầu mọi trang: thông báo nổi nào vừa được gắn vào trang thì chép chữ vào window.__aecNotifies,
     * để khi trang nặng tải xong mà thông báo đã tự tắt vẫn còn bản chép.
     */
    private static final String NOTIFY_RECORDER = "new MutationObserver(()=>document.querySelectorAll(\"[data-notify='container']:not([data-aec-seen])\")"
            + ".forEach(t=>{t.setAttribute('data-aec-seen','');const m=t.querySelector(\"[data-notify='message']\");"
            + "(window.__aecNotifies=window.__aecNotifies||[]).push((m||t).textContent.trim());}))"
            + ".observe(document,{childList:true,subtree:true});";
    /** JS: chữ thông báo nổi mới nhất còn trên trang và chưa bị đánh dấu cũ ("" nếu không có). */
    private static final String JS_LATEST_NOTIFY = "const t=[...document.querySelectorAll(\"[data-notify='container']:not([data-aec-old])\")];if(!t.length)return '';"
            + "const e=t[t.length-1];const m=e.querySelector(\"[data-notify='message']\");return (m||e).innerText.trim();";
    /** JS: thông báo mới nhất trong bản chép của NOTIFY_RECORDER ("" nếu không có). */
    private static final String JS_RECORDED_NOTIFY = "const n=window.__aecNotifies||[];return n.length?n[n.length-1]:'';";
    /** JS: đánh dấu mọi thông báo đang có là cũ. */
    private static final String JS_MARK_NOTIFIES_OLD =
            "document.querySelectorAll(\"[data-notify='container']\").forEach(t => t.setAttribute('data-aec-old', ''));";
    /** JS: đặt dấu lên trang hiện tại / hỏi dấu còn không — trang tải lại thì dấu mất. */
    private static final String JS_MARK_PAGE = "window.__aecMark = true;";
    private static final String JS_PAGE_MARKED = "return window.__aecMark === true;";
    /** JS: trang không rộng hơn cửa sổ (không có thanh cuộn ngang). */
    private static final String JS_NO_HORIZONTAL_SCROLL = "return document.documentElement.scrollWidth <= window.innerWidth;";

    private String lastPageNotify = "";
    private int lastNotifyAction;

    protected BasePage() {
        WebUI.registerPage(this);
        WebUI.runOnEveryPage(NOTIFY_RECORDER);
    }

    /** Tên hiện trong log và report. */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    protected void navigate(String path) {
        WebUI.openURL(ConfigReader.getInstance().getBaseUrl() + path);
        WebUI.waitForPageLoaded();
    }

    /** Chờ trang chuyển tới đúng đường dẫn (không tính tham số). */
    public void waitForPath(String path) {
        try {
            WebUI.waitUntil("địa chỉ là " + path, () -> path.equals(currentPath()));
        } catch (TimeoutException e) {
            LogUtils.warn("Không tới {}, đang ở {}", path, currentPath());
        }
    }

    /** Làm hành động khiến trang tải lại, chờ trang mới tải xong. */
    protected void andWaitForNewPage(Runnable action) {
        markPage();
        action.run();
        WebUI.waitUntil("trang mới thay trang cũ", () -> !isSamePageInstance());
        WebUI.waitForPageLoaded();
        // Thông báo nổi tự tắt sau vài giây, trang nặng dựng xong có thể đã tắt, nên ghi lại ngay lúc này;
        // đã tắt thì lấy bản NOTIFY_RECORDER chép lại lúc nó vừa hiện
        lastPageNotify = currentNotifyText();
        if (lastPageNotify.isEmpty()) {
            lastPageNotify = recordedNotifyText();
            if (!lastPageNotify.isEmpty()) {
                LogUtils.info("Thông báo nổi đã tắt trước khi trang tải xong, dùng bản chép lúc nó hiện: [{}]", lastPageNotify);
            }
        }
        lastNotifyAction = WebUI.getActionCount();
    }

    /** Hành động đã đưa sang trang khác: Page đích nhận luôn thông báo ghi lúc trang tải, test hỏi thẳng Page đích. */
    protected <P extends BasePage> P arrivedAt(P next) {
        BasePage target = next;
        target.lastPageNotify = lastPageNotify;
        target.lastNotifyAction = lastNotifyAction;
        return next;
    }

    /** Như trên; trang mới không phải trang lỗi máy chủ thì chờ nó sẵn sàng. */
    protected void andWaitUnlessError(Runnable action, Runnable ready) {
        andWaitForNewPage(action);
        if (!isServerErrorPage()) {
            ready.run();
        }
    }

    /** Ảnh xem trước dựng bằng AJAX sau khi trang tải: ô đã có id ảnh thì chờ ảnh hiện rồi mới đếm. */
    protected int previewCountWhenReady(String imageId, By items) {
        if (imageId != null && !imageId.isBlank()) {
            try {
                WebUI.waitUntil("ảnh xem trước của id " + imageId, () -> WebUI.checkElementExist(items));
            } catch (TimeoutException e) {
                LogUtils.warn("Ô ảnh có id {} nhưng không dựng được ảnh xem trước", imageId);
            }
        }
        return WebUI.getElementCount(items);
    }

    /**
     * Đánh dấu trang hiện tại: trang tải lại thì dấu mất. Mọi hàm bấm gửi form (clickSave, submit…) tự gọi
     * trước khi bấm, nên test chỉ việc hỏi {@link #isSamePageInstance()} sau đó.
     */
    protected void markPage() {
        WebUI.executeJS(JS_MARK_PAGE);
    }

    public boolean isSamePageInstance() {
        return Boolean.TRUE.equals(WebUI.executeJS(JS_PAGE_MARKED));
    }

    /** Thông báo nổi của hành động vừa xong (góc dưới trái). */
    public String getNotifyMessage() {
        String message = latestNotify();
        LogUtils.info("Thông báo nổi = [{}]", message);
        return message;
    }

    private String latestNotify() {
        // Chờ ngắn thông báo đang hiện (kể cả đến sau qua AJAX); đã tắt thì dùng bản ghi lúc trang vừa tải,
        // nhưng chỉ khi từ lúc ghi chưa có hành động nào khác
        try {
            return WebUI.waitUntil("thông báo nổi", NOTIFY_SHORT_WAIT, this::notifyTextOrNull);
        } catch (TimeoutException e) {
            if (!lastPageNotify.isEmpty() && lastNotifyAction == WebUI.getActionCount()) {
                return lastPageNotify;
            }
        }
        return WebUI.waitUntil("thông báo nổi", this::notifyTextOrNull);
    }

    private String notifyTextOrNull() {
        String text = currentNotifyText();
        return text.isEmpty() ? null : text;
    }

    /** Chữ của thông báo nổi mới nhất đang có trên trang ("" nếu không có): đọc một lần bằng JS, không bị stale. */
    private String currentNotifyText() {
        return (String) WebUI.executeJS(JS_LATEST_NOTIFY);
    }

    /** Thông báo nổi mới nhất trang này từng hiện, theo bản chép của NOTIFY_RECORDER ("" nếu không có). */
    private String recordedNotifyText() {
        return (String) WebUI.executeJS(JS_RECORDED_NOTIFY);
    }

    /** Gọi trước hành động AJAX (trang không tải lại): thông báo đang hiện là của hành động trước, không tính nữa. */
    protected void ignoreShownNotifies() {
        WebUI.executeJS(JS_MARK_NOTIFIES_OLD);
    }

    /**
     * Gửi form khi phiên đã hết có ra trang 419 PAGE EXPIRED không.
     * Chờ tới khi ra 419 hoặc về /login rồi mới kết luận, vì ngay sau khi bấm Save có thể còn ở trang cũ.
     */
    public boolean isPageExpired() {
        try {
            WebUI.waitUntil("trang 419 hoặc trang đăng nhập",
                    () -> showsExpiredPage() || WebUI.getCurrentURL().endsWith("/login"));
        } catch (TimeoutException e) {
            LogUtils.warn("Không ra 419, không về trang đăng nhập, đang ở {}", WebUI.getCurrentURL());
            return false;
        }
        return showsExpiredPage();
    }

    private boolean showsExpiredPage() {
        return EXPIRED_TITLE.equals(WebUI.getPageTitle()) && WebUI.checkElementExist(EXPIRED_MESSAGE);
    }

    /**
     * Trang lỗi máy chủ hoặc trang trắng: Laravel "Server Error", trang "Something went wrong! … Error code: 500",
     * hoặc trang 403 của tường lửa LiteSpeed ("Access to this resource on the server is denied!").
     */
    public boolean isServerErrorPage() {
        String body = WebUI.getElementRawText(BODY);
        return WebUI.getPageTitle().contains("Server Error") || body.contains("Server Error")
                || body.contains("Something went wrong!") || body.contains("Access to this resource on the server is denied")
                || body.isBlank();
    }

    /** Trang "Page Not Found!" của website (id không tồn tại). */
    public boolean isPageNotFound() {
        return WebUI.checkElementExist(NOT_FOUND_HEADING);
    }

    public boolean isAlertOpen() {
        return WebUI.isAlertPresent();
    }

    public boolean hasNoHorizontalScroll() {
        return Boolean.TRUE.equals(WebUI.executeJS(JS_NO_HORIZONTAL_SCROLL));
    }

    /** Chờ tab mới (mở bằng target=_blank) xuất hiện rồi chuyển sang tab đó. */
    public void switchToNewWindow(String originalHandle) {
        WebUI.switchToNewWindow(originalHandle);
    }

    public void waitForAjaxIdle() {
        WebUI.waitForJQueryLoad();
    }

    /** Toàn bộ chữ đang hiện trên trang. */
    public String bodyText() {
        return WebUI.getElementRawText(BODY);
    }

    public String currentUrl() {
        return WebUI.getCurrentURL();
    }

    public String currentPath() {
        return WebUI.getCurrentPath();
    }
}
