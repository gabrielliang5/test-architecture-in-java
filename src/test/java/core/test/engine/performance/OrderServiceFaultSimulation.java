package core.test.engine.performance;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import core.test.engine.mock.MockExtremeConditions;
import core.test.engine.mock.MockServiceManager;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderServiceFaultSimulation extends Simulation {

    Logger logger = LoggerFactory.getLogger(OrderServiceFaultSimulation.class);
    private String baseUrl;
    OrderService service;
    @Override
    public void before() {
        MockServiceManager.start();
        baseUrl = MockServiceManager.getBaseUrl();
        service = new OrderService();
        // 这里记得调用你之前写的 setupRandomNetworkFaults()
        MockExtremeConditions mockExtremeConditions = new MockExtremeConditions();
        mockExtremeConditions.setupRandomNetworkFaults();
        if(baseUrl==null){
            throw new RuntimeException("baseUrl is null");
        }

        logger.info("✅ Mock 容器已通过 Testcontainers 启动: " + baseUrl);

    }

    // 1. 定义协议：我们要压测的是你的本地 Mock 服务
//    HttpProtocolBuilder httpProtocol = http
//            .baseUrl(baseUrl);

    // 2. 定义场景：模拟用户下单
    ScenarioBuilder scn = scenario("Order Fault Test")
            .exec(session -> {
                // 在这里直接调用你的业务代码 (SUT)
                OrderService service = new OrderService();
                int status = service.placeOrder(baseUrl);

                // 记录结果：如果返回 999，说明我们的系统成功捕获了抖动，没崩
                return session.set("status", status);
            })
            // 校验：我们预期会收到大量的 999 (由于 Fault 注入)
            .pause(1);

    {
        // 3. 设置压力模型：10秒内均匀启动 50 个并发用户
        setUp(
                scn.injectOpen(rampUsers(50).during(10))
        );
    }

    // 3. 仿真结束后关闭容器，释放你的云盘/内存资源
    @Override
    public void after() {
        MockServiceManager.stop();
        logger.info("Mock 容器已关闭，资源已释放");
    }
}
