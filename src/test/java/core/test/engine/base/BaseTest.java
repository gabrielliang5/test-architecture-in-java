package core.test.engine.base;

import core.test.engine.mock.MockServiceManager;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.utility.DockerImageName;


public abstract class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @BeforeAll
    protected static void beforeAll() {

        MockServiceManager.start();
        logger.info("Mock 启动");
    }

    @BeforeEach
    protected void beforeEach() {
        MockServiceManager.reset();
        logger.info("Mock 重置");
    }

    @AfterAll
    protected static void afterAll() {
        MockServiceManager.stop();
        logger.info("Mock 关闭");
    }

    protected <T> T asModel(Response response, Class<T> clazz) {
        return response.as(clazz);
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