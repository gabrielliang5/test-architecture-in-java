package core.test.engine.mock;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;





/**
 * 💡核心组件：Mock 服务管理器
 * 职责：在 Docker (Codespaces) 中启动虚拟网元，并提供桩（Stub）配置接口。
 */
public class MockServiceManager {

    private final static Logger logger = LoggerFactory.getLogger(MockServiceManager.class);

    // 1. 定义 WireMock 容器，使用官方镜像
    // 在 Codespaces 中，Testcontainers 会自动寻找 Docker Daemon 运行该容器
    private static final GenericContainer<?> WIREMOCK_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("wiremock/wiremock:3.3.1"))
                    .withExposedPorts(8080); // WireMock 默认内部端口

    /**
     * 启动 Mock 服务器
     */
    public static void start() {
        if (!WIREMOCK_CONTAINER.isRunning()) {
            logger.info("正在云端启动 Mock 容器...");
            WIREMOCK_CONTAINER.start();

            // 2. 核心逻辑：配置 WireMock 客户端
            // 因为 Codespaces 映射到宿主机的端口是随机的，必须动态获取
            WireMock.configureFor(
                    WIREMOCK_CONTAINER.getHost(),
                    WIREMOCK_CONTAINER.getMappedPort(8080)
            );

            logger.info(" Mock Server 已就绪: " + getBaseUrl());
        }
    }

    /**
     * 获取 Mock 服务器的 Base URL (供测试脚本调用)
     */
    public static String getBaseUrl() {
        return "http://" + WIREMOCK_CONTAINER.getHost() + ":" + WIREMOCK_CONTAINER.getMappedPort(8080);
    }

    /**
     * 停止 Mock 服务器 (通常在所有测试结束时调用)
     */
    public static void stop() {
        if (WIREMOCK_CONTAINER.isRunning()) {
            WIREMOCK_CONTAINER.stop();
            logger.info("Mock 容器已销毁");
        }
    }

    //
    public static void reset() {
        WireMock.reset();
        logger.info("Mock 已重置，确保测试独立性");
    }

    /**
     * 💡 故障注入 (Fault Injection) 示例：模拟服务器 500 错误
     * 这体现了 20 年经验中对“鲁棒性”测试的关注
     */
    public static void stubInternalError(String url) {
        WireMock.stubFor(WireMock.any(WireMock.urlEqualTo(url))
                .willReturn(WireMock.aResponse()
                        .withStatus(500)
                        .withBody("{\"error\": \"Internal Server Error\"}")));
    }
}