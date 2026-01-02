package core.test.engine.data;




import core.test.engine.model.OrderModel;
import core.test.engine.util.JsonDataFactory;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

public class OrderDataProvider {
    private static final Faker FAKER = new Faker(new Locale("en-CA"));

    /**
     * 2. 静态方法：线程安全产出对象
     */
    public static OrderModel getDynamicOrder() {
        // 逻辑：利用共享的 FAKER 产出独立的对象实例
        return new OrderModel(
                UUID.randomUUID().toString(),               // 唯一 ID
                new BigDecimal(FAKER.commerce().price()),    // 随机价格
                "PENDING",                                  // 初始状态
                "CAD",                                      // 蒙特利尔货币
                "Customer: " + FAKER.name().fullName()      // 随机姓名
        );
    }

    public static OrderModel getTemplateOrder() {
        //JsonDataFactory  测试一个预设好的场景
        return JsonDataFactory.createModel("payloads/order.json", OrderModel.class);
    }
}