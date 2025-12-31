package core.test.engine.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.filter.Filter;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class ApiTestEngine {


    protected final String baseUrl;


    protected final int timeout;


    protected final String env;


    protected final String contentType;


    protected final String accept;

    private final Map<String, String> defaultHeaders;
    private final Header DEFAULTHEADER;


    private final Logger logger = LoggerFactory.getLogger(ApiTestEngine.class);
    private static final Filter ALLURE_FILTER = new AllureRestAssured();


    public ApiTestEngine() {
        // 读取配置文件，如果没找到就用默认值
        Properties prop = new Properties();

        InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties");

        if (in == null) {
            throw new RuntimeException("测试配置文件 application.properties 未找到");
        }

        try (in) {
            prop.load(in);
        } catch (IOException e) {
            logger.error("读取测试配置文件失败", e);
            throw new RuntimeException(e);
        }

        baseUrl = prop.getProperty("base.url", "http://localhost:8080");
        logger.info("baseUrl={}", baseUrl);
        timeout = Integer.parseInt(prop.getProperty("timeout", "5000"));
        env = prop.getProperty("env", "dev");
        contentType = prop.getProperty("header.Content-Type", "application/json");
        accept = prop.getProperty("header.Accept", "application/json");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Accept", accept);
        this.defaultHeaders = Map.copyOf(headers);
        this.DEFAULTHEADER = new Header("Content-Type", contentType);

        //超时配置
        RestAssured.config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", timeout)
                .setParam("http.socket.timeout", timeout));

        logger.info("RestAssured 全局超时已设置为: {}ms", timeout);
    }

    protected Response getResponse(String path,Header header) {
        header = header==null?DEFAULTHEADER:header;
        path = path==null?"":path;
        return RestAssured.given().filter(ALLURE_FILTER). // 👈 这一行是灵魂：自动抓取数据到报告
                header(header).
                log().ifValidationFails(). // 只在失败时打印控制台日志，保持整洁
                when().get(baseUrl + path).then().extract().response();
    }


}
