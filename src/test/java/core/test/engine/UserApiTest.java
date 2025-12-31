package core.test.engine;

import core.test.engine.base.BaseTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserApiTest extends BaseTest {

    @Test
    void testConnection() {
        // 从 BaseTest 获取动态地址
        String url = System.getProperty("test.mock.url");
        log.info("正在连接云端容器地址: {}", url);

        // 验证请求
        given()
            .baseUri(url)
        .when()
            .get("/__admin/mappings")
        .then()
            .statusCode(200);
            
        log.info("连通性测试通过,Java 代码成功访问了 Docker 容器。");
    }
}