package core.test.engine.page;

import com.microsoft.playwright.Page;
import core.test.engine.base.BasePage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ResumePage extends BasePage {
    private final Page page;

    // 1. 把定位符定死在这里，以后改 ID 只要改这里
    private final String UPLOAD_SELECTOR = "#resume-upload";
    private final String SUCCESS_MSG_SELECTOR = "body";

    public ResumePage(Page page) {
        super(page);
        this.page = page;
    }

    // 2. 动作：上传逻辑（用你坚持的 ClassLoader 写法）
    public void upload(String fileName) {
        try {
            java.net.URL resource = getClass().getClassLoader().getResource(fileName);
            java.nio.file.Path path = java.nio.file.Paths.get(resource.toURI());
            page.setInputFiles(UPLOAD_SELECTOR, path);
        } catch (Exception e) {
            throw new RuntimeException("文件路径解析出错", e);
        }
    }

    // 3. 验证：判断文件是否在页面显示（用我欠你的 assertThat 写法）
    public void shouldSeeFile(String fileName) {
        assertThat(page.locator(SUCCESS_MSG_SELECTOR)).containsText(fileName);
    }
}