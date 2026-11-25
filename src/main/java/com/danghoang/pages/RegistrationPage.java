package com.danghoang.pages;

import com.danghoang.keywords.WebUI;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
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

    @Step("Mở trang đăng ký")
    public RegistrationPage open() {
        openStorefront("/users/registration", FULL_NAME_INPUT);
        return this;
    }

    // Không dùng @Step ở method nhận mật khẩu: Allure ghi mọi tham số vào JSON, kể cả khi @Param(mode = MASKED)
    public void fillForm(String fullName, String email, String password, String confirmPassword) {
        Allure.step("Điền form đăng ký: tên " + fullName + ", email " + email, () -> {
            WebUI.setText(FULL_NAME_INPUT, fullName);
            WebUI.setText(EMAIL_INPUT, email);
            WebUI.setText(PASSWORD_INPUT, password);
            WebUI.setText(CONFIRM_PASSWORD_INPUT, confirmPassword);
        });
    }

    @Step("Tick ô đồng ý điều khoản")
    public void acceptTerms() {
        WebElement checkbox = WebUI.getWebElement(TERMS_CHECKBOX);
        if (!checkbox.isSelected()) {
            WebUI.clickElementByJS(checkbox, "ô đồng ý điều khoản");
        }
    }

    @Step("Bấm Create Account")
    public void submit() {
        markPage();
        WebUI.clickElement(CREATE_ACCOUNT_BUTTON);
    }

    @Step("Bấm Log In")
    public CustomerLoginPage clickLogIn() {
        WebUI.clickElement(LOG_IN_LINK);
        return new CustomerLoginPage();
    }

    public boolean isTitleDisplayed() {
        return WebUI.isElementVisible(TITLE);
    }

    public List<String> fieldPlaceholdersInOrder() {
        return WebUI.getAllElementsAttribute(VISIBLE_INPUTS, "placeholder");
    }

    public boolean isTermsTextDisplayed() {
        return WebUI.isElementDisplayedNow(TERMS_TEXT);
    }

    public boolean isTermsChecked() {
        return WebUI.isElementSelected(TERMS_CHECKBOX);
    }

    public String termsValidationMessage() {
        return WebUI.getValidationMessage(TERMS_CHECKBOX);
    }

    public boolean isCreateAccountDisplayed() {
        return WebUI.isElementDisplayedNow(CREATE_ACCOUNT_BUTTON);
    }

    public boolean isLogInPromptDisplayed() {
        return WebUI.isElementDisplayedNow(ALREADY_HAVE_ACCOUNT) && WebUI.isElementDisplayedNow(LOG_IN_LINK);
    }

    public boolean hasPhoneField() {
        return WebUI.checkElementExist(PHONE_INPUT);
    }

    public String emailValue() {
        return WebUI.getElementValue(EMAIL_INPUT);
    }
}
