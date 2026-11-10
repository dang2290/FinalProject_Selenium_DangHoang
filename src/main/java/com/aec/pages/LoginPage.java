package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

/** Trang đăng nhập quản trị /login — locator lấy từ DOM thật 03-11-2026. */
public class LoginPage extends BasePage {

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
    private static final By CAPTCHA = By.cssSelector(".g-recaptcha, iframe[src*='recaptcha']");

    private static final String STYLE_SCRIPT = "const c=getComputedStyle(arguments[0]);"
            + "return [c.outlineStyle,c.outlineWidth,c.outlineColor,c.boxShadow,c.backgroundColor,c.color,c.borderColor,c.textDecorationLine].join('|');";

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang đăng nhập quản trị")
    public LoginPage open() {
        navigate("/login");
        waitForVisible(EMAIL_INPUT);
        return this;
    }

    @Step("Nhập email: {email}")
    public void enterEmail(String email) {
        type(EMAIL_INPUT, email);
    }

    @Step("Nhập mật khẩu")
    public void enterPassword(String password) {
        type(PASSWORD_INPUT, password);
    }

    @Step("Bấm nút Login")
    public void submit() {
        click(LOGIN_BUTTON);
    }

    @Step("Nhấp đúp nút Login")
    public void doubleClickLogin() {
        new Actions(driver).doubleClick(wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON))).perform();
    }

    @Step("Nhấn Enter trong ô Password")
    public void pressEnterInPassword() {
        driver.findElement(PASSWORD_INPUT).sendKeys(Keys.ENTER);
    }

    @Step("Bấm liên kết Forgot password ?")
    public void clickForgotPassword() {
        click(FORGOT_PASSWORD_LINK);
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        submit();
    }

    public boolean isHeadingDisplayed() {
        return isVisible(HEADING);
    }

    public boolean isSubtitleDisplayed() {
        return isPresentNow(SUBTITLE);
    }

    public boolean isLogoDisplayed() {
        return isPresentNow(LOGO);
    }

    public String emailPlaceholder() {
        return driver.findElement(EMAIL_INPUT).getAttribute("placeholder");
    }

    public String passwordPlaceholder() {
        return driver.findElement(PASSWORD_INPUT).getAttribute("placeholder");
    }

    public boolean hasFieldLabels() {
        return !driver.findElements(By.cssSelector("label[for='email'], label[for='password']")).isEmpty();
    }

    public boolean isRememberMeDisplayed() {
        return isPresentNow(By.xpath("//label[normalize-space()='Remember Me']"));
    }

    public boolean isRememberMeChecked() {
        return driver.findElement(REMEMBER_CHECKBOX).isSelected();
    }

    public boolean isForgotPasswordDisplayed() {
        return isPresentNow(FORGOT_PASSWORD_LINK);
    }

    public boolean isLoginButtonDisplayed() {
        return isPresentNow(LOGIN_BUTTON);
    }

    public boolean hasCaptcha() {
        return !driver.findElements(CAPTCHA).isEmpty();
    }

    @Step("Gõ '{text}' ngay khi mở trang, không bấm chuột")
    public void typeWithoutClicking(String text) {
        new Actions(driver).sendKeys(text).perform();
    }

    public String emailValue() {
        return driver.findElement(EMAIL_INPUT).getAttribute("value");
    }

    public String passwordValue() {
        return driver.findElement(PASSWORD_INPUT).getAttribute("value");
    }

    public String passwordInputType() {
        return driver.findElement(PASSWORD_INPUT).getAttribute("type");
    }

    public boolean hasPasswordRevealToggle() {
        return (Boolean) js("return [...arguments[0].parentElement.querySelectorAll('button,i,a,span')]"
                + ".some(e=>e.offsetParent!==null);", driver.findElement(PASSWORD_INPUT));
    }

    public String emailValidationMessage() {
        return validationMessage(EMAIL_INPUT);
    }

    public String passwordValidationMessage() {
        return validationMessage(PASSWORD_INPUT);
    }

    public String focusedFieldId() {
        return (String) js("return document.activeElement.id;");
    }

    public String passwordFieldError() {
        return getText(FIELD_ERROR);
    }

    public boolean isStillOnLoginPage() {
        return currentPath().equals("/login") && isPresentNow(FORM);
    }

    /** Nhấn Tab từ ô Email, trả về tên phần tử nhận focus sau mỗi lần Tab. */
    @Step("Nhấn Tab {count} lần từ ô Email")
    public List<String> tabSequenceFromEmail(int count) {
        driver.findElement(EMAIL_INPUT).click();
        List<String> sequence = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            new Actions(driver).sendKeys(Keys.TAB).perform();
            sequence.add((String) js("const a=document.activeElement;return a.id||a.name||a.innerText.trim();"));
        }
        return sequence;
    }

    /** Phần tử nào trông giống hệt nhau khi được chọn bằng Tab và khi không được chọn. */
    @Step("Đối chiếu kiểu hiển thị khi được chọn bằng Tab của Remember Me, Forgot password ?, Login")
    public List<String> elementsWithoutVisibleFocus() {
        List<WebElement> targets = List.of(driver.findElement(REMEMBER_CHECKBOX),
                driver.findElement(FORGOT_PASSWORD_LINK), driver.findElement(LOGIN_BUTTON));
        List<String> names = List.of("Remember Me", "Forgot password ?", "Login");
        List<String> blurred = new ArrayList<>();
        for (WebElement target : targets) {
            blurred.add((String) js(STYLE_SCRIPT, target));
        }
        driver.findElement(PASSWORD_INPUT).click();
        List<String> invisible = new ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            new Actions(driver).sendKeys(Keys.TAB).perform();
            WebElement active = driver.switchTo().activeElement();
            if (active.equals(targets.get(i)) && js(STYLE_SCRIPT, active).equals(blurred.get(i))) {
                invisible.add(names.get(i));
            }
        }
        return invisible;
    }

    /** Lỗi sau khi gửi form: thông báo nổi hoặc chữ đỏ dưới ô nhập. */
    public String getErrorMessage() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(NOTIFY_MESSAGE),
                ExpectedConditions.visibilityOfElementLocated(FIELD_ERROR)));
        return driver.findElements(FIELD_ERROR).isEmpty() ? getNotifyMessage() : getText(FIELD_ERROR);
    }
}
