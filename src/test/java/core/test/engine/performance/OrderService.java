package core.test.engine.performance;



import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

//这个类是模拟业务的http请求
public class OrderService {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3)) // 建立连接超时
            .build();

    public int placeOrder(String mockUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mockUrl + "/faults")) // 撞击网元故障接口
                    .timeout(Duration.ofSeconds(5))      // 等待响应超时
                    .GET()
                    .build();

            // 发送请求
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode();
        } catch (Exception e) {
            // 这就是网络抖动发生时，业务系统的“自保”行为
            // 在压测中，我们要看系统在这里处理异常的速度
            return 999; // 我们定义 999 为“系统捕获到的网络抖动错误”
        }
    }
}