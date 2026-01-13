package core.test.engine.mock;

import com.github.tomakehurst.wiremock.http.Fault;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MockLoginService {

    public  void loginFaultMock(){

        stubFor(post(urlEqualTo("/api/v1/login")).willReturn(aResponse().withStatus(401).withHeader("Content-Type", "application/json").withBody("{\"error\",\"invalible user\"}")));
    }
    //延时模拟
    public  void delayMock(){

        stubFor(post(urlEqualTo("/api/v1/login")).willReturn(aResponse().withFixedDelay(10000).withStatus(500).withHeader("Content-Type", "application/json").withBody("{\"error\":\"System timeout\"}")));
    }
    //底层故障模拟
    public  void systemFaultMock(){

        stubFor(post(urlEqualTo("/api/v1/login")).willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));
    }

}
