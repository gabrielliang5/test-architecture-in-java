package core.test.engine.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature; // 必须有这一行
import com.fasterxml.jackson.databind.SerializationFeature;   // 必须有这一行
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;
import java.io.InputStream;

/**
 * 框架核心组件：负责将 JSON 模板转换为强类型的 Record 对象
 */
public class JsonDataFactory {

    // 使用 static block 或者直接在定义时初始化
    private static final ObjectMapper MAPPER = new ObjectMapper()
            // 1. 支持 Java 8 时间类型
            .registerModule(new JavaTimeModule())
            // 2. 更好地支持 Record 的参数名发现
            .registerModule(new ParameterNamesModule())
            // 3. 常见工程实践：遇到未知属性不报错，提高测试兼容性
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // 4. 常见工程实践：写入时间戳为字符串而不是数字
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    /**
     * @param resourcePath 相对 resources 目录的路径
     * @param clazz 目标 Record 类的 Class
     */
    public static <T> T createModel(String resourcePath, Class<T> clazz) {
        try (InputStream is = JsonDataFactory.class.getClassLoader().
                getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("未找到数据文件: " + resourcePath);
            }
            return MAPPER.readValue(is, clazz);
        } catch (IOException e) {
            throw new RuntimeException("数据解析失败 [" + clazz.getSimpleName() + "]: " + e.getMessage(), e);
        }
    }
}