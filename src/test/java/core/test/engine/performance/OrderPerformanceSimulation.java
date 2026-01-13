package core.test.engine.performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class OrderPerformanceSimulation extends Simulation {

    //  定义 HTTP 协议配置
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://example.com")
            .acceptHeader("application/json")//只接受json
            .contentTypeHeader("application/json");

    //  定义动态数据源 (Feeder) - 确保 ID 不重复
    Iterator<Map<String, Object>> feeder =
            Stream.generate(() -> {
                // Map.of 显式声明类型参数 <String, Object>,别名orderId
                return Map.<String, Object>of("orderId", UUID.randomUUID().toString());
            }).iterator();

    //  定义业务场景
    ScenarioBuilder scn = scenario("Post Order Stress Test")
            .feed(feeder) // 注入动态数据
            .exec(http("api order post")
                    .post("/api/v1/orders")
                    .body(StringBody("{\"id\": \"${orderId}\", \"amount\": 100}"))
                    .check(status().is(202))); // 断言接口返回 202

    // 4. 定义负载策略 (Load Injection)
    {
        setUp(
                scn.injectOpen(
                        atOnceUsers(10),            // 启动瞬时冲入 10 个用户
                        rampUsers(500).during(60)   //  Ramp up ,在 60 秒内均匀增加到 500 人
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile3().lt(200), // P95 < 200ms
                        global().failedRequests().count().is(0L)       // 错误数为 0
                );
    }
}