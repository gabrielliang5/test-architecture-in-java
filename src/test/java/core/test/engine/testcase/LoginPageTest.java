package core.test.engine.testcase;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import core.test.engine.base.BaseTest;
import core.test.engine.base.PlaywrightFactory;
import core.test.engine.model.UserCredentials;
import core.test.engine.page.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPageTest extends BaseTest {
    Page page;
    LoginPage loginPage;

    @BeforeEach
    void setup() {
        page = PlaywrightFactory.init("chromium", false);
        loginPage = new LoginPage(page);
    }

    @Test
    void shouldLoginSuccessfully() {
        loginPage.open("https://www.saucedemo.com");
        loginPage.login( new UserCredentials("standard_user", "secret_sauce"));

        // 断言逻辑
        assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        // 处理
        PlaywrightFactory.close(testInfo.getDisplayName());
    }
}