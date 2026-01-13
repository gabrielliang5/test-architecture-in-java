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



        // 截图 ，在 Context 关闭前截取当前页面状态，并直接给 Allure
        try {
            byte[] screenshot = pageThreadLocal.get().screenshot(
                    new Page.ScreenshotOptions().setFullPage(true)
            );
            io.qameta.allure.Allure.addAttachment("测试结束截图",
                    new java.io.ByteArrayInputStream(screenshot));
        } catch (Exception e) {
            logger.error("截图失败: {}", e.getMessage());
        }

        //是否创建目录
        String dir = "target/traces/";
        Path path = Paths.get(dir);
        // --- 目录创建逻辑 ---
        boolean canSave = true;
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                logger.error("Could not create trace directory: {}", e.getMessage());
                canSave = false;
            }
        }

        // --- 执行 Trace 存盘 ---
        try {
            if (canSave && contextThreadLocal.get() != null) {
                Path tracePath = path.resolve(testName + ".zip");//保存在指定目录
                contextThreadLocal.get().tracing().stop(new Tracing.StopOptions().setPath(tracePath));

                // 把 Trace 喂给 Allure
                io.qameta.allure.Allure.addAttachment(
                        "操作轨迹回放 (Trace Viewer)",
                        "application/zip",
                        java.nio.file.Files.newInputStream(tracePath),
                        ".zip"
                );
            }
        } catch (Exception e) {
            logger.error("Trace 保存或挂载失败: {}", e.getMessage());
        } finally {
            // --- 确保资源回收，无论前面是否报错 ---
            if (browserThreadLocal.get() != null)
                browserThreadLocal.get().close();
            if (playwrightThreadLocal.get() != null)
                playwrightThreadLocal.get().close();

            playwrightThreadLocal.remove();
            browserThreadLocal.remove();
            contextThreadLocal.remove();
            pageThreadLocal.remove();
        }
    }
}
