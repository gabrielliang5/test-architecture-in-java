package core.test.engine.api;

import core.test.engine.base.ApiTestEngine;
import core.test.engine.model.OrderModel;
import io.restassured.response.Response;

public class OrderApi extends ApiTestEngine{

    private static final String ORDER_PATH = "/api/v1/orders";

    // 获取订单
    public Response getOrder(String orderId) {
        // 调用父类的 getResponse，把业务逻辑转化为路径参数
        return getResponse(ORDER_PATH + "/" + orderId, null);
    }

    // 创建订单
    public Response createOrder(OrderModel order) {

        return postResponse(ORDER_PATH, order, null);
    }
}
