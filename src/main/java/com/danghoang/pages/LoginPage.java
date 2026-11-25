package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/** Trang đăng nhập quản trị /login — locator lấy từ DOM thật 03-11-2026. */
public class LoginPage extends BasePage {

    /** JS: cạnh ô (cùng khối cha) có nút / biểu tượng nào đang hiện không — dấu hiệu nút xem mật khẩu. */
    private static final String JS_HAS_VISIBLE_NEIGHBOUR = "return [...arguments[0].parentElement.querySelectorAll('button,i,a,span')]"
            + ".some(e=>e.offsetParent!==null);";
    /** JS: tên dễ đọc của phần tử đang focus (id, name hoặc chữ). */
    private static final String JS_FOCUSED_NAME = "const a=document.activeElement;return a.id||a.name||a.innerText.trim();";
    private static final By FORM = By.cssSelector("form[action$='/login']");
    private static final By EMAIL_INPUT = By.id("email");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By REMEMBER_CHECKBOX = By.id("remember");
    private static final By FORGOT_PASSWORD_LINK = By.xpath("//a[normalize-space()='Forgot password ?']");
    private static final By LOGIN_BUTTON = By.cssSelector("form[action$='/login'] button[type='submit']");
    private static final By HEADING = By.xpath("//h1[normalize-space()='Welcome to Active eCommerce CMS']");
    private static final By SUBTITLE = By.xpath("//*[normalize-space()='Login to your account.']");
    private static final By LOGO = By.cssSelector("form img, .card img, img[src*='logo']");
    private static final By FIELD_ERROR = By.cssSelector(".invalid-feedback");
    private static final By FIELD_LABELS = By.cssSelector("label[for='email'], label[for='password']");
    private static final By REMEMBER_LABEL = By.xpath("//label[normalize-space()='Remember Me']");
    private static final By CAPTCHA = By.cssSelector(".g-recaptcha, iframe[src*='recaptcha']");

    private static final String STYLE_SCRIPT = "const c=getComputedStyle(arguments[0]);"
            + "return [c.outlineStyle,c.outlineWidth,c.outlineColor,c.boxShadow,c.backgroundColor,c.color,c.borderColor,c.textDecorationLine].join('|');";

    @Step("Mở trang đăng nhập quản trị")
    public LoginPage open() {
        navigate("/login");
        WebUI.waitForElementVisible(EMAIL_INPUT);
        return this;
    }

    @Step("Nhập email: {email}")
    public void enterEmail(String email) {
        WebUI.setText(EMAIL_INPUT, email);
    }

    // Không dùng @Step ở method nhận mật khẩu: Allure ghi mọi tham số vào JSON, kể cả khi @Param(mode = MASKED)
    public void enterPassword(String password) {
        Allure.step("Nhập mật khẩu", () -> WebUI.setText(PASSWORD_INPUT, password));
    }

    @Step("Bấm nút Login")
    public void submit() {
        markPage();
        WebUI.clickElement(LOGIN_BUTTON);
    }

    @Step("Nhấp đúp nút Login")
    public void doubleClickLogin() {
        WebUI.doubleClickClickableElement(LOGIN_BUTTON);
    }

    @Step("Nhấn Enter trong ô Password")
    public void pressEnterInPassword() {
        WebUI.pressKey(PASSWORD_INPUT, Keys.ENTER);
    }

    @Step("Bấm liên kết Forgot password ?")
    public ForgotPasswordPage clickForgotPassword() {
        WebUI.clickElement(FORGOT_PASSWORD_LINK);
        return new ForgotPasswordPage();
    }

    /**
     * Trả về Dashboard để nối: {@code new LoginPage().open().login(email, password).waitUntilLoaded()}.
     * Ca đăng nhập sai thì bỏ qua giá trị trả về, kiểm tiếp trên LoginPage.
     */
    public DashboardPage login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        submit();
        return new DashboardPage();
    }

    public boolean isHeadingDisplayed() {
        return WebUI.isElementVisible(HEADING);
    }

    public boolean isSubtitleDisplayed() {
        return WebUI.isElementDisplayedNow(SUBTITLE);
    }

    public boolean isLogoDisplayed() {
        return WebUI.isElementDisplayedNow(LOGO);
    }

    public String emailPlaceholder() {
        return WebUI.getElementAttribute(EMAIL_INPUT, "placeholder");
    }

    public String passwordPlaceholder() {
        return WebUI.getElementAttribute(PASSWORD_INPUT, "placeholder");
    }

    public boolean hasFieldLabels() {
        return WebUI.checkElementExist(FIELD_LABELS);
    }

    public boolean isRememberMeDisplayed() {
        return WebUI.isElementDisplayedNow(REMEMBER_LABEL);
    }

    public boolean isRememberMeChecked() {
        return WebUI.isElementSelected(REMEMBER_CHECKBOX);
    }

    public boolean isForgotPasswordDisplayed() {
        return WebUI.isElementDisplayedNow(FORGOT_PASSWORD_LINK);
    }

    public boolean isLoginButtonDisplayed() {
        return WebUI.isElementDisplayedNow(LOGIN_BUTTON);
    }

    public boolean hasCaptcha() {
        return WebUI.checkElementExist(CAPTCHA);
    }

    @Step("Gõ '{text}' ngay khi mở trang, không bấm chuột")
    public void typeWithoutClicking(String text) {
        WebUI.typeAtFocus(text);
    }

    public String emailValue() {
        return WebUI.getElementValue(EMAIL_INPUT);
    }

    public String passwordValue() {
        return WebUI.getElementValue(PASSWORD_INPUT);
    }

    public String passwordInputType() {
        return WebUI.getElementAttribute(PASSWORD_INPUT, "type");
    }

    public boolean hasPasswordRevealToggle() {
        return (Boolean) WebUI.executeJS(JS_HAS_VISIBLE_NEIGHBOUR, WebUI.getWebElement(PASSWORD_INPUT));
    }

    public String emailValidationMessage() {
        return WebUI.getValidationMessage(EMAIL_INPUT);
    }

    public String passwordValidationMessage() {
        return WebUI.getValidationMessage(PASSWORD_INPUT);
    }

    public String focusedFieldId() {
        return WebUI.getFocusedProperty("id");
    }

    public boolean isStillOnLoginPage() {
        return currentPath().equals("/login") && WebUI.isElementDisplayedNow(FORM);
    }

    /** Nhấn Tab từ ô Email, trả về tên phần tử nhận focus sau mỗi lần Tab. */
    public List<String> tabSequenceFromEmail(int count) {
        return WebUI.tabSequenceFrom(EMAIL_INPUT, count, JS_FOCUSED_NAME);
    }

    /** Phần tử nào trông giống hệt nhau khi được chọn bằng Tab và khi không được chọn. */
    @Step("Đối chiếu kiểu hiển thị khi được chọn bằng Tab của Remember Me, Forgot password ?, Login")
    public List<String> elementsWithoutVisibleFocus() {
        List<WebElement> targets = List.of(WebUI.getWebElement(REMEMBER_CHECKBOX),
                WebUI.getWebElement(FORGOT_PASSWORD_LINK), WebUI.getWebElement(LOGIN_BUTTON));
        List<String> names = List.of("Remember Me", "Forgot password ?", "Login");
        List<String> blurred = new ArrayList<>();
        for (WebElement target : targets) {
            blurred.add((String) WebUI.executeJS(STYLE_SCRIPT, target));
        }
        WebUI.clickElementNow(PASSWORD_INPUT);
        List<String> invisible = new ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            WebUI.pressKey(Keys.TAB);
            WebElement active = WebUI.getActiveElement();
            if (active.equals(targets.get(i)) && WebUI.executeJS(STYLE_SCRIPT, active).equals(blurred.get(i))) {
                invisible.add(names.get(i));
            }
        }
        return invisible;
    }

    /** Lỗi sau khi gửi form: thông báo nổi hoặc chữ đỏ dưới ô nhập. */
    public String getErrorMessage() {
        WebUI.waitForAnyVisible(NOTIFY_MESSAGE, FIELD_ERROR);
        return !WebUI.checkElementExist(FIELD_ERROR) ? getNotifyMessage() : WebUI.getElementText(FIELD_ERROR);
    }
}
