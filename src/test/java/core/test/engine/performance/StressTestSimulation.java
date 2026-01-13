package core.test.engine.performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class StressTestSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://example.com").
            acceptHeader("application/json")     //  告诉服务器： 只要 JSON 格式的返回
    .contentTypeHeader("application/json")       //  告诉服务器：我发给你的也是 JSON
    .userAgentHeader("Gatling/PerformanceTest"); //  身份标识：让运维知道这是压测流量而非黑客攻击;

    ScenarioBuilder scn = scenario("Stress Test - Finding the Limit")
            .exec(http("Request Heavy Load ")
                    .post("orders")
                    .body(StringBody("{\"productId\": \"chair\", \"quantity\": 1}"))
                    .check(status().is(201)));//成功条件

    {
        setUp(
                scn.injectOpen(
                        incrementUsersPerSec(50)// 每一级增加的人数。比如第一级 10 人，第二阶就变 60 人。
                                .times(10) // 总共往上加多少次。这里加 10 次，
                                .eachLevelLasting(30)// 每级保持不动的时长（秒）。
                                .separatedByRampsLasting(12) // 两个级之间的“斜坡”时长。即在这个时间内增增加incrementUsers，30秒不动，乘以10次，每两个间隔12秒，共9次，共计30*10+12*9
                                .startingFrom(100)         // 起始压力。测试开始的第一秒，每秒进来的用户数。
                )

        ).protocols(httpProtocol)
                //断言
                .assertions(
            //  全局 P95 响应时间必须 < 200ms
            //   95th percentile
            global().responseTime().percentile3().lt(200),

            // 规则2：成功率必须是 100% (不能有任何报错)
            global().failedRequests().percent().is(0.0)
    );
    }
}
