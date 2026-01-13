package core.test.engine.base;

import com.microsoft.playwright.Page;
import core.test.engine.mock.MockServiceManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class UIBaseTest extends BaseTest{

    private final Logger logger = LoggerFactory.getLogger(UIBaseTest.class);

    protected Page page;

    @BeforeEach
    protected void beforeEach() {

        if (MOCK_ENABLED) {
        MockServiceManager.reset();

        logger.info("Mock 重置");
        }

        String browser = System.getProperty("browser", "Chromium");

        page = PlaywrightFactory.init(browser,true);

        logger.debug("page:"+page.toString());
    }

    @AfterEach
    protected void afterEach(TestInfo testInfo) {
        PlaywrightFactory.close(testInfo.getDisplayName());

    }
}
