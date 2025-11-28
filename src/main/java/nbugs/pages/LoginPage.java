package nbugs.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    public static final String WELCOME_NONAME_TEXT = "Welcome, noname!";

     private SelenideElement button = $("button");

    @Override
    public String url() {
        return "/login";
    }

    public LoginPage login(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        button.click();
        return this;
    }
}
