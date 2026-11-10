package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/** Trang đăng nhập khách /users/login — locator lấy từ DOM thật 03-11-2026. */
public class CustomerLoginPage extends StorefrontPage {

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

    public CustomerLoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang đăng nhập khách")
    public CustomerLoginPage open() {
        openStorefront("/users/login", EMAIL_INPUT);
        return this;
    }

    @Step("Đăng nhập khách với email {email}")
    public void login(String email, String password) {
        type(EMAIL_INPUT, email);
        type(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
    }

    @Step("Bấm Login khi để trống")
    public void submitEmpty() {
        click(LOGIN_BUTTON);
    }

    @Step("Bấm Register Now")
    public void clickRegisterNow() {
        click(REGISTER_NOW_LINK);
    }

    @Step("Bấm Forgot password?")
    public void clickForgotPassword() {
        click(FORGOT_PASSWORD_LINK);
    }

    public boolean isTitleDisplayed() {
        return isVisible(TITLE);
    }

    public boolean isEmailDisplayed() {
        return isPresentNow(EMAIL_INPUT);
    }

    public boolean isPasswordDisplayed() {
        return isPresentNow(PASSWORD_INPUT);
    }

    public boolean isRememberMeDisplayed() {
        return isPresentNow(REMEMBER_LABEL);
    }

    public boolean isForgotPasswordDisplayed() {
        return isPresentNow(FORGOT_PASSWORD_LINK);
    }

    public boolean isLoginButtonDisplayed() {
        return isPresentNow(LOGIN_BUTTON);
    }

    public boolean isRegisterNowDisplayed() {
        return isPresentNow(REGISTER_NOW_LINK);
    }

    public boolean areHeaderLinksDisplayed() {
        return isPresentNow(HEADER_LOGIN) && isPresentNow(HEADER_REGISTRATION);
    }

    /** Dòng chữ đứng ngay trên liên kết Register Now. */
    public String registerPrompt() {
        return (String) js("const a=[...document.querySelectorAll('a')].find(x=>x.innerText.trim()==='Register Now');"
                + "return a.parentElement.innerText.replace(a.innerText,'').trim();");
    }

    public String emailFieldError() {
        return getText(FIELD_ERROR);
    }

    public List<String> invalidFieldNames() {
        return driver.findElements(INVALID_FIELDS).stream().map(e -> e.getAttribute("name")).toList();
    }

    public String emailValue() {
        return driver.findElement(EMAIL_INPUT).getAttribute("value");
    }

    public String passwordValue() {
        return driver.findElement(PASSWORD_INPUT).getAttribute("value");
    }

    public boolean isStillOnPage() {
        return isOn("/users/login") && isPresentNow(FORM);
    }
}
