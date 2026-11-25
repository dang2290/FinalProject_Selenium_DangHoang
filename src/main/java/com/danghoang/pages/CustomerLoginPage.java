package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

/** Trang đăng nhập khách /users/login — locator lấy từ DOM thật 03-11-2026. */
public class CustomerLoginPage extends StorefrontPage {

    /** JS: chữ trong khối cha của liên kết Register Now, trừ chữ của chính liên kết. */
    private static final String JS_REGISTER_PROMPT = "const a=[...document.querySelectorAll('a')].find(x=>x.innerText.trim()==='Register Now');"
            + "return a.parentElement.innerText.replace(a.innerText,'').trim();";
    private static final By FORM = By.cssSelector("form[action$='/login']");
    private static final By EMAIL_INPUT = By.cssSelector("form[action$='/login'] input[name='email']");
    private static final By PASSWORD_INPUT = By.cssSelector("form[action$='/login'] input[name='password']");
    private static final By REMEMBER_LABEL = By.xpath("//form[contains(@action,'/login')]//*[normalize-space()='Remember Me']");
    private static final By LOGIN_BUTTON = By.cssSelector("form[action$='/login'] button[type='submit']");
    private static final By TITLE = By.xpath("//*[normalize-space()='Login to your account.']");
    private static final By FORGOT_PASSWORD_LINK = By.xpath("//form[contains(@action,'/login')]//a[normalize-space()='Forgot password?']");
    private static final By REGISTER_NOW_LINK = By.xpath("//a[normalize-space()='Register Now']");
    private static final By HEADER_LOGIN = By.xpath("//a[normalize-space()='Login' and contains(@href,'/users/login')]");
    private static final By HEADER_REGISTRATION = By.xpath("//a[normalize-space()='Registration' and contains(@href,'/users/registration')]");
    private static final By FIELD_ERROR = By.cssSelector("form[action$='/login'] .invalid-feedback");
    private static final By INVALID_FIELDS = By.cssSelector("form[action$='/login'] .is-invalid");

    @Step("Mở trang đăng nhập khách")
    public CustomerLoginPage open() {
        openStorefront("/users/login", EMAIL_INPUT);
        return this;
    }

    // Không dùng @Step ở method nhận mật khẩu: Allure ghi mọi tham số vào JSON, kể cả khi @Param(mode = MASKED)
    public void login(String email, String password) {
        Allure.step("Đăng nhập khách với email " + email, () -> {
            WebUI.setText(EMAIL_INPUT, email);
            WebUI.setText(PASSWORD_INPUT, password);
            WebUI.clickElement(LOGIN_BUTTON);
        });
    }

    @Step("Bấm Login khi để trống")
    public void submitEmpty() {
        WebUI.clickElement(LOGIN_BUTTON);
    }

    @Step("Bấm Register Now")
    public RegistrationPage clickRegisterNow() {
        WebUI.clickElement(REGISTER_NOW_LINK);
        return new RegistrationPage();
    }

    @Step("Bấm Forgot password?")
    public ForgotPasswordPage clickForgotPassword() {
        WebUI.clickElement(FORGOT_PASSWORD_LINK);
        return new ForgotPasswordPage();
    }

    public boolean isTitleDisplayed() {
        return WebUI.isElementVisible(TITLE);
    }

    public boolean isEmailDisplayed() {
        return WebUI.isElementDisplayedNow(EMAIL_INPUT);
    }

    public boolean isPasswordDisplayed() {
        return WebUI.isElementDisplayedNow(PASSWORD_INPUT);
    }

    public boolean isRememberMeDisplayed() {
        return WebUI.isElementDisplayedNow(REMEMBER_LABEL);
    }

    public boolean isForgotPasswordDisplayed() {
        return WebUI.isElementDisplayedNow(FORGOT_PASSWORD_LINK);
    }

    public boolean isLoginButtonDisplayed() {
        return WebUI.isElementDisplayedNow(LOGIN_BUTTON);
    }

    public boolean isRegisterNowDisplayed() {
        return WebUI.isElementDisplayedNow(REGISTER_NOW_LINK);
    }

    public boolean areHeaderLinksDisplayed() {
        return WebUI.isElementDisplayedNow(HEADER_LOGIN) && WebUI.isElementDisplayedNow(HEADER_REGISTRATION);
    }

    /** Dòng chữ đứng ngay trên liên kết Register Now. */
    public String registerPrompt() {
        return (String) WebUI.executeJS(JS_REGISTER_PROMPT);
    }

    public String emailFieldError() {
        return WebUI.getElementText(FIELD_ERROR);
    }

    public List<String> invalidFieldNames() {
        return WebUI.getAllElementsAttribute(INVALID_FIELDS, "name");
    }

    public String emailValue() {
        return WebUI.getElementValue(EMAIL_INPUT);
    }

    public String passwordValue() {
        return WebUI.getElementValue(PASSWORD_INPUT);
    }

    public boolean isStillOnPage() {
        return isOn("/users/login") && WebUI.isElementDisplayedNow(FORM);
    }
}
