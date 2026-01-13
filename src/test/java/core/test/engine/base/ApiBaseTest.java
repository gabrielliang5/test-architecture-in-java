package core.test.engine.base;

import core.test.engine.mock.MockServiceManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ApiBaseTest extends BaseTest {
    protected static final io.qameta.allure.restassured.AllureRestAssured ALLURE_FILTER =
            new io.qameta.allure.restassured.AllureRestAssured();

    Logger logger = LoggerFactory.getLogger(ApiBaseTest.class);

    @BeforeEach
    public void beforeEach() {
        MockServiceManager.reset();
        logger.info("Mock reset");

    }

    //  只负责（Allure）和日志，不设任何 ContentType 限制
    protected RequestSpecification baseRequest() {
        return RestAssured.given()
                .filter(ALLURE_FILTER)//挂载报告
                .log().ifValidationFails();// 失败时自动打印控制台
    }

    protected <T> T asModel(Response response, Class<T> clazz) {

        return response.as(clazz);
    }
}
