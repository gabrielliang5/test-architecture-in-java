package core.test.engine.page;

import com.microsoft.playwright.Page;
import core.test.engine.base.BasePage;
import core.test.engine.model.UserCredentials;

public class LoginPage extends BasePage {
    // 1. 定义定位器 (Locators),
    private final String usernameInput = "#user-name";
    private final String passwordInput = "#password";
    private final String loginButton = "#login-button";

    public LoginPage(Page page) {
        super(page);
    }

    public void open(String url){
        page.navigate(url);
    }

    // 2. 业务逻辑封装
    public void login(UserCredentials credentials) {
        page.fill(usernameInput, credentials.username());
        page.fill(passwordInput, credentials.password());
        page.click(loginButton);
    }
}