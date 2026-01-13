package core.test.engine.base;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public abstract class BasePage {
    protected final Page page;

    protected BasePage(Page page) {

        this.page = page;

    }

    @Step("浏览器导航至：{url}")
    public void navigateTo(String url) {
        page.navigate(url);
    }

    @Step("当前页面截图")
    public byte[] takeScreenshot() {
        return page.screenshot();
    }


}