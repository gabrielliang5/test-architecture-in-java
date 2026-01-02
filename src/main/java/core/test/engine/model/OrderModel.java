package core.test.engine.model;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * 职责：订单数据模型
 * 使用 Record 保证不可变性，非常适合作为 API 的 RequestBody。
 * @JsonIgnoreProperties 保证了当 API 返回多余字段时，测试不会报错，增强了鲁棒性。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderModel(
        @JsonProperty("orderId")
        String orderId,

        @JsonProperty("amount")
        BigDecimal amount,

        @JsonProperty("status")
        String status,

        @JsonProperty("currency")
        String currency,

        @JsonProperty("description")
        String description
) {
    /**
     * 在 Record 内部定义一个静态内部类或方法来快速创建简单的 Model
     * 方便在不需要 JSON 文件时快速构造数据
     */
    public static OrderModel of(String id, BigDecimal amt) {
        return new OrderModel(id, amt, "PENDING", "CAD", "Montreal Job Application Blitz");
    }
}