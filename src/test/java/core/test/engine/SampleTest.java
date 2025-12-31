package core.test.engine;

import core.test.engine.base.ApiTestEngine;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class SampleTest extends ApiTestEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleTest.class);

    @Test
    public void testFrameworkConnection() {
        // 调用你封装好的 getResponse
        // 这里我们随便写一个路径，比如 "/"，主要看它是否能读取 baseUrl 并加上超时配置
        Response response = getResponse("/", null);

        // 打印一下结果，看看 baseUrl 是否正确加载
        LOGGER.info("Status Code: " + response.getStatusCode());

        // 断言结果不为空即可
        assertNotNull(response);
    }
}
