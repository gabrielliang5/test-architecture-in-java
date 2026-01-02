package core.test.engine.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // 转化为运行时异常，因为在测试中如果序列化失败，本身就是环境或代码Bug，应该直接中断
            throw new RuntimeException("JSON 序列化失败: " + obj.getClass().getName(), e);
        }
    }
}