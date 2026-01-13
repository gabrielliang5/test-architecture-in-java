package core.test.engine.testcase;

import com.github.tomakehurst.wiremock.client.WireMock;
import core.test.engine.base.BaseTest;
import core.test.engine.base.BaseTest1;
import core.test.engine.util.UUIDUtil;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

//Mock隔离，多线程测试，
public class IsolateMockTest extends BaseTest1 {

    @Test
    void testIsolationWithThreadLocal() {
        // 1. 设桩 (从 ThreadLocal 悄悄拿 ID)
        String currentId = UUIDUtil.getUUID().toString();

        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/data"))
                .withHeader("X-Test-ID", WireMock.equalTo(currentId))
                .willReturn(WireMock.okJson("{\"status\": \"isolated\"}")));

        // 2. 发起请求 (同样从 ThreadLocal 悄悄拿 ID)
        RestAssured.given()
                .header("X-Test-ID", currentId) // 这里的 ID 保证和上面是同一个
                .when()
                .get("/api/data")
                .then()
                .statusCode(200);
    }
}
