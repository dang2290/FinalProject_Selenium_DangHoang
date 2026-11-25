package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Trang quên mật khẩu /password/reset — locator lấy từ DOM thật 03-11-2026. */
public class ForgotPasswordPage extends StorefrontPage {

    private static final By EMAIL_INPUT = By.cssSelector("form[action$='/password/email'] input[name='email']");
    private static final By SEND_BUTTON = By.cssSelector("form[action$='/password/email'] button[type='submit']");
    private static final By TITLE = By.xpath("//*[normalize-space()='Forgot password?' and not(self::a)]");
    private static final By SUBTITLE = By.xpath("//*[normalize-space()='Enter your email address to recover your password.']");
    private static final By BACK_TO_LOGIN_LINK = By.xpath("//a[normalize-space()='Back to Login']");

    @Step("Mở trang quên mật khẩu")
    public ForgotPasswordPage open() {
        openStorefront("/password/reset", EMAIL_INPUT);
        return this;
    }

    /** Dùng khi đến trang này bằng một liên kết, không mở thẳng địa chỉ. */
    @Step("Chờ trang quên mật khẩu tải xong")
    public ForgotPasswordPage waitUntilLoaded() {
        WebUI.waitForElementVisible(EMAIL_INPUT);
        closeAdPopupIfShown();
        return this;
    }

    @Step("Gửi yêu cầu đặt lại mật khẩu cho email '{email}'")
    public void requestReset(String email) {
        WebUI.setText(EMAIL_INPUT, email);
        WebUI.clickElement(SEND_BUTTON);
    }

    @Step("Bấm Send Password Reset Link khi để trống")
    public void submitEmpty() {
        WebUI.clickElement(SEND_BUTTON);
    }

    /** Link trỏ về đăng nhập khách, kể cả khi đến từ trang quản trị (lỗi đã biết AMB-LOGIN-01). */
    @Step("Bấm Back to Login")
    public CustomerLoginPage clickBackToLogin() {
        WebUI.clickElement(BACK_TO_LOGIN_LINK);
        return new CustomerLoginPage();
    }

    public boolean isTitleDisplayed() {
        return WebUI.isElementVisible(TITLE);
    }

    public boolean isSubtitleDisplayed() {
        return WebUI.isElementDisplayedNow(SUBTITLE);
    }

    public boolean isEmailDisplayed() {
        return WebUI.isElementDisplayedNow(EMAIL_INPUT);
    }

    public boolean isSendButtonDisplayed() {
        return WebUI.isElementDisplayedNow(SEND_BUTTON) && WebUI.getElementText(SEND_BUTTON).equals("Send Password Reset Link");
    }

    public boolean isBackToLoginDisplayed() {
        return WebUI.isElementDisplayedNow(BACK_TO_LOGIN_LINK);
    }
}
