package core.test.engine.base;

import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlaywrightFactory {
    // 使用 ThreadLocal 确保并行测试时浏览器上下文互不干扰
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    private static  final Logger logger = LoggerFactory.getLogger(PlaywrightFactory.class);

    public static Page init(String browserType, boolean headless) {
        Playwright playwright = Playwright.create();
        playwrightThreadLocal.set(playwright);

        Browser browser;
        switch (browserType.toLowerCase()) {
            case "firefox" ->
                    browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless));
            case "webkit" ->
                    browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless));
            default -> browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
        }
        browserThreadLocal.set(browser);

        // 创建上下文并开启 Tracing (核心：为 Visual Results 准备数据)
        BrowserContext context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        contextThreadLocal.set(context);

        Page page = context.newPage();
        pageThreadLocal.set(page);
        return page;
    }

    public static Page getPage() {
        return pageThreadLocal.get();
    }

    public static void close(String testName) {


        String dir = "target/traces/";
        Path path = Paths.get(dir);

        // 1. 准备目录：尝试创建，失败则记录日志
        boolean canSave = true;
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                logger.error("Could not create trace directory: {}", e.getMessage());
                canSave = false; // 标记为不可存盘
            }
        }

        // 2. 执行存盘：只有在目录 OK 的情况下才执行
        if (canSave) {
            contextThreadLocal.get().tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get(dir + testName + ".zip")));
        }
        // 停止 Tracing 并保存到本地，


        browserThreadLocal.get().close();
        playwrightThreadLocal.get().close();

        // 清理线程变量
        playwrightThreadLocal.remove();
        browserThreadLocal.remove();
        contextThreadLocal.remove();
        pageThreadLocal.remove();
    }
}
