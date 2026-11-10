package com.aec.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Trang đăng ký khách /users/registration — locator lấy từ DOM thật 03-11-2026. */
public class RegistrationPage extends StorefrontPage {

    private static final String FORM = "form[action$='/register'] ";
    private static final By FULL_NAME_INPUT = By.cssSelector(FORM + "input[name='name']");
    private static final By EMAIL_INPUT = By.cssSelector(FORM + "input[name='email']");
    private static final By PASSWORD_INPUT = By.cssSelector(FORM + "input[name='password']");
    private static final By CONFIRM_PASSWORD_INPUT = By.cssSelector(FORM + "input[name='password_confirmation']");
    private static final By TERMS_CHECKBOX = By.cssSelector(FORM + "input[type='checkbox']");
    private static final By TERMS_TEXT = By.xpath("//*[normalize-space()='By signing up you agree to our terms and conditions.']");
    private static final By CREATE_ACCOUNT_BUTTON = By.cssSelector(FORM + "button[type='submit']");
    private static final By TITLE = By.xpath("//*[normalize-space()='Create an account.']");
    private static final By ALREADY_HAVE_ACCOUNT = By.xpath("//*[normalize-space()='Already have an account?']");
    private static final By LOG_IN_LINK = By.xpath("//a[normalize-space()='Log In' and contains(@href,'/users/login')]");
    private static final By PHONE_INPUT = By.cssSelector(FORM + "input[name='phone'], " + FORM + "input[type='tel']");
    private static final By VISIBLE_INPUTS = By.cssSelector(FORM + "input:not([type='hidden']):not([type='checkbox'])");

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    @Step("Mở trang đăng ký")
    public RegistrationPage open() {
        openStorefront("/users/registration", FULL_NAME_INPUT);
        return this;
    }

    @Step("Điền form đăng ký: tên {fullName}, email {email}")
    public void fillForm(String fullName, String email, String password, String confirmPassword) {
        type(FULL_NAME_INPUT, fullName);
        type(EMAIL_INPUT, email);
        type(PASSWORD_INPUT, password);
        type(CONFIRM_PASSWORD_INPUT, confirmPassword);
    }

    @Step("Tick ô đồng ý điều khoản")
    public void acceptTerms() {
        WebElement checkbox = driver.findElement(TERMS_CHECKBOX);
        if (!checkbox.isSelected()) {
            js("arguments[0].click();", checkbox);
        }
    }

    @Step("Bấm Create Account")
    public void submit() {
        click(CREATE_ACCOUNT_BUTTON);
    }

    @Step("Bấm Log In")
    public void clickLogIn() {
        click(LOG_IN_LINK);
    }

    public boolean isTitleDisplayed() {
        return isVisible(TITLE);
    }

    public List<String> fieldPlaceholdersInOrder() {
        return driver.findElements(VISIBLE_INPUTS).stream().map(e -> e.getAttribute("placeholder")).toList();
    }

    public boolean isTermsTextDisplayed() {
        return isPresentNow(TERMS_TEXT);
    }

    public boolean isTermsChecked() {
        return driver.findElement(TERMS_CHECKBOX).isSelected();
    }

    public String termsValidationMessage() {
        return validationMessage(TERMS_CHECKBOX);
    }

    public boolean isCreateAccountDisplayed() {
        return isPresentNow(CREATE_ACCOUNT_BUTTON);
    }

    public boolean isLogInPromptDisplayed() {
        return isPresentNow(ALREADY_HAVE_ACCOUNT) && isPresentNow(LOG_IN_LINK);
    }

    public boolean hasPhoneField() {
        return !driver.findElements(PHONE_INPUT).isEmpty();
    }

    public String emailValue() {
        return driver.findElement(EMAIL_INPUT).getAttribute("value");
    }
}
