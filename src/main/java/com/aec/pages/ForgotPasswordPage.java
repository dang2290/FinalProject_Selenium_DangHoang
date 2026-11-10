package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Trang quên mật khẩu /password/reset — locator lấy từ DOM thật 03-11-2026. */
public class ForgotPasswordPage extends StorefrontPage {

    private static final By EMAIL_INPUT = By.cssSelector("form[action$='/password/email'] input[name='email']");
    private static final By SEND_BUTTON = By.cssSelector("form[action$='/password/email'] button[type='submit']");
    private static final By TITLE = By.xpath("//*[normalize-space()='Forgot password?' and not(self::a)]");
    private static final By SUBTITLE = By.xpath("//*[normalize-space()='Enter your email address to recover your password.']");
    private static final By BACK_TO_LOGIN_LINK = By.xpath("//a[normalize-space()='Back to Login']");

    public ForgotPasswordPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang quên mật khẩu")
    public ForgotPasswordPage open() {
        openStorefront("/password/reset", EMAIL_INPUT);
        return this;
    }

    /** Dùng khi đến trang này bằng một liên kết, không mở thẳng địa chỉ. */
    @Step("Chờ trang quên mật khẩu tải xong")
    public ForgotPasswordPage waitUntilLoaded() {
        waitForVisible(EMAIL_INPUT);
        closeAdPopupIfShown();
        return this;
    }

    @Step("Gửi yêu cầu đặt lại mật khẩu cho email '{email}'")
    public void requestReset(String email) {
        type(EMAIL_INPUT, email);
        click(SEND_BUTTON);
    }

    @Step("Bấm Send Password Reset Link khi để trống")
    public void submitEmpty() {
        click(SEND_BUTTON);
    }

    @Step("Bấm Back to Login")
    public void clickBackToLogin() {
        click(BACK_TO_LOGIN_LINK);
    }

    public boolean isTitleDisplayed() {
        return isVisible(TITLE);
    }

    public boolean isSubtitleDisplayed() {
        return isPresentNow(SUBTITLE);
    }

    public boolean isEmailDisplayed() {
        return isPresentNow(EMAIL_INPUT);
    }

    public boolean isSendButtonDisplayed() {
        return isPresentNow(SEND_BUTTON) && getText(SEND_BUTTON).equals("Send Password Reset Link");
    }

    public boolean isBackToLoginDisplayed() {
        return isPresentNow(BACK_TO_LOGIN_LINK);
    }
}
