package core.test.engine.base;

import com.microsoft.playwright.Page;
import core.test.engine.mock.MockServiceManager;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.utility.DockerImageName;


public abstract class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    protected static final boolean MOCK_ENABLED = false;
    @BeforeAll
    protected static void beforeAll() {

        if (MOCK_ENABLED) {
            MockServiceManager.start();
            logger.info("Mock 启动");
        } else {
            logger.info("Mock 已禁用");
        }
    }

//    @BeforeEach
//    protected void beforeEach() {
//        MockServiceManager.reset();
//        page = PlaywrightFactory.init("Firefox",true);
//        logger.info("Mock 重置");
//    }

//    @AfterEach
//    protected void afterEach(TestInfo testInfo) {
//
//        PlaywrightFactory.close(testInfo.getDisplayName());
////        // 1. 抓取截图 (UI 层的证据)
////        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
////        Allure.addAttachment("页面最终状态截图", new ByteArrayInputStream(screenshot));
//
//        // 2. 抓取前端控制台日志 (帮助排查 JS 报错)
//        // 这个通常需要监听 page.onConsoleMessage，简单起见我们先做截图
//    }

    @AfterAll
    protected static void afterAll() {
        if (MOCK_ENABLED) {
            MockServiceManager.stop();
            logger.info("Mock 关闭");
        }
    }


//    // 定义为 static，确保所有测试类共享同一个容器实例
//    protected static final GenericContainer<?> WIREMOCK_CONTAINER;
//
//    static {
//        log.info("正在初始化云端测试引擎...");
//        WIREMOCK_CONTAINER = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock:latest"))
//                .withExposedPorts(8080);
//
//        // 启动容器
//        WIREMOCK_CONTAINER.start();
//
//        // 获取映射后的真实端口
//        String host = WIREMOCK_CONTAINER.getHost();
//        Integer port = WIREMOCK_CONTAINER.getMappedPort(8080);
//
//        log.info("Docker 容器已就绪,WireMock 管理地址: http://{}:{}/__admin", host, port);
//
//        // 将地址设为系统变量，方便 RestAssured 全局调用
//        System.setProperty("test.mock.url", "http://" + host + ":" + port);
//    }
}