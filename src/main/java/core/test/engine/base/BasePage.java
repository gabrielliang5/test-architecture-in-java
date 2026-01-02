package core.test.engine.base;

import com.microsoft.playwright.Page;

public abstract class BasePage {
    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    // 封装通用的 UI 操作，体现架构的复用性
    public void waitForNetworkIdle() {
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }
}