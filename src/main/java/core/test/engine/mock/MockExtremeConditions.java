package core.test.engine.mock;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MockExtremeConditions {

    public static final String PATH_JITTER = "/api/jitter";
    public static final String PATH_HANG = "/api/hang";
    public static final String PATH_FAULTS = "/api/faults";
    public static final String PATH_DOWN = "/api/down";
    public static final String PATH_LEAK = "/api/leak";

    //模拟网络不稳定或数据库变慢。这会直接导致 Gatling 报告中的 Std Deviation 变大。
    public void setupLatencyJitter() {
        stubFor(get(urlEqualTo(PATH_JITTER))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"status\":\"jittery\"}")
                        // 设平均值 500ms，标准差 0.2
                        // 这会让 P95 明显偏离 Mean
                        .withLogNormalRandomDelay(500, 0.2)));
    }

    //模拟下游接口长时间不返回。在 Gatling 中你会看到 Concurrent Users 不断堆积，直到达到你应用的线程上限。
    public void setupHangingService() {
        stubFor(get(urlEqualTo(PATH_HANG))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(30000) // 延迟 30 秒
                        .withBody("{\"status\":\"finally_done\"}")));
    }
    //模拟极端硬件或网络层错误（如连接重置）。在 Gatling 中会产生大量的 KO (Red Bars)。
    public void setupRandomNetworkFaults() {
        stubFor(get(urlEqualTo(PATH_FAULTS))
                .willReturn(aResponse()
                        // 随机返回：无效数据、垃圾字节或直接关闭连接
                        .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));
    }


    //模拟下游服务彻底宕机。用于测试你代码的异常处理能力。
    public void setupServiceUnavailable() {
        stubFor(get(urlEqualTo(PATH_DOWN))
                .willReturn(aResponse()
                        .withStatus(503) // 返回 Service Unavailable
                        .withHeader("Retry-After", "60")
                        .withBody("{\"error\":\"Backend Overloaded\"}")));
    }

    //模拟典型的内存泄漏或连接泄漏过程——起初很快，随着压测进行越来越慢。
    public void setupMemoryLeakSimulation() {
        // 第一次调用很快
        stubFor(get(urlEqualTo(PATH_LEAK)).inScenario("Leak")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(200).withFixedDelay(100))
                .willSetStateTo("SlightlySlow"));

        // 后续调用越来越慢
        stubFor(get(urlEqualTo("/api/leak")).inScenario("Leak")
                .whenScenarioStateIs("SlightlySlow")
                .willReturn(aResponse().withStatus(200).withFixedDelay(2000)));
    }
}
