package core.test.engine.util;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class AssertUtils {

    private Logger logger = LoggerFactory.getLogger(AssertUtils.class);

    private AssertUtils(){

    }

    /** 断言状态码 */
    public static void assertStatusCode(Response response, int expectedStatus) {
        Objects.requireNonNull(response, "response must not be null");
        assertEquals(expectedStatus, response.getStatusCode(),
                "状态码不匹配, 实际: " + response.getStatusCode());
    }

    /** 断言 JSON 某个字段值 ,Json对象里的内容可以是多种对象*/
    public static void assertJsonValue(Response response, String jsonPath, Object expected) {
        Objects.requireNonNull(response, "response must not be null");
        Object actual = response.jsonPath().get(jsonPath);
        assertEquals(expected, actual,
                jsonPath + " 值不匹配, 实际: " + actual);
    }

    /** 断言 JSON 字段存在 */
    public static void assertJsonFieldExists(Response response, String jsonPath) {
        Objects.requireNonNull(response, "response must not be null");
        assertNotNull(response.jsonPath().get(jsonPath), jsonPath + " 不存在");
    }

    /** 断言响应体包含某字符串 */
    public static void assertBodyContains(Response response, String content) {
        Objects.requireNonNull(response, "response must not be null");
        assertTrue(response.getBody().asString().contains(content),
                "响应体不包含预期内容: " + content);
    }

    /** 断言响应时间小于指定毫秒 */
    public static void assertResponseTimeLessThan(Response response, long maxMillis) {
        Objects.requireNonNull(response, "response must not be null");
        assertTrue(response.time() <= maxMillis,
                "响应时间超过预期: " + response.time() + "ms, 最大允许: " + maxMillis + "ms");
    }

//    public static void  assertNullOrNot(Object obj,String name){
//        assertNotNull(obj,name);
//    }
}