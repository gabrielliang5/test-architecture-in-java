package core.test.engine.testcase;

import core.test.engine.api.OrderApi;
import core.test.engine.base.BaseTest;
import core.test.engine.data.OrderDataProvider;
import core.test.engine.model.OrderModel;
import core.test.engine.util.AssertUtils;
import core.test.engine.util.JsonUtil;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.*;

public class OrderApiTest extends BaseTest {

    private  OrderApi orderApi;

    @BeforeEach
    void setup(){
        orderApi = new OrderApi();
    }

    @Test
    @DisplayName("正常创建订单 - 验证 Record 序列化与数据独立性")
    void shouldCreateOrderSuccessfully() {

        record OrderResonse(Long id,
                BigDecimal amount,
                String status,
                String currency,
                String description){

        };


        // 1. 准备数据 (Record)
        OrderModel requestBody = OrderDataProvider.getDynamicOrder();
        OrderResonse or = new OrderResonse(110000L,requestBody.amount(),"OK",requestBody.currency(),requestBody.description());

        //序列化
        String requestJson = JsonUtil.toJson(requestBody);
        String responseJson = JsonUtil.toJson(or);

        // 模拟

        stubFor(post(urlEqualTo("/api/v1/orders"))
                .withRequestBody(equalToJson(requestJson))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        // 这里可以直接构造一个期望的返回 JSON 字符串，或者用另一个 Record
                        .withBody(responseJson)));

        // 3. 执行请求
        Response response = orderApi.createOrder(requestBody);

        // 4. 断言 (使用你的 AssertUtils)
        AssertUtils.assertStatusCode(response, 201);

        // 如果你想验证返回的 Model
        OrderResonse actualResponse = response.as(OrderResonse.class);
        Assertions.assertEquals(110000L,actualResponse.id());
    }

    @Test
    @DisplayName("极端情况模拟 - 模拟服务器 500 错误 (Fault Injection)")
    void shouldHandleServerInternalError() {
        OrderModel requestBody = OrderDataProvider.getDynamicOrder();

        // 模拟服务器崩溃
//        stubFor(post(urlEqualTo("/api/v1/orders"))
//                .willReturn(serverError()));


        record  OrderResponseError(String status,String description){

        }

        OrderResponseError orderResponseError = new OrderResponseError("Error","initial error");

        stubFor(post(urlEqualTo("/api/v1/orders"))
                .willReturn(aResponse().withStatus(500).
                        withHeader("Content-Type", "application/json").
                        withBody(JsonUtil.toJson(orderResponseError))));

        Response response = orderApi.createOrder(requestBody);
        OrderResponseError actualResponse  = response.as(OrderResponseError.class);
        AssertUtils.assertStatusCode(response, 500);
        AssertUtils.assertJsonValue(response,"status","Error");

    }
}
