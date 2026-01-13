package core.test.engine.testcase;

import core.test.engine.base.BaseTest;
import core.test.engine.mock.MockExtremeConditions;
import core.test.engine.mock.MockServiceManager;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

//测试Mock，为压测做准备
public class MockExtremeCondionsTest extends BaseTest {

    private MockExtremeConditions mockExtremeCondions;
    @BeforeEach
    public void beforeEach() {
        MockServiceManager.reset();
        mockExtremeCondions = new MockExtremeConditions();

    }

    @Test
    public void setupLatencyJitterTest(){

               mockExtremeCondions.setupLatencyJitter();
        RestAssured.given().baseUri(MockServiceManager.getBaseUrl()).when().get(MockExtremeConditions.PATH_JITTER).then().assertThat().statusCode(200);

    }

    @Test
    public void setupHangingServiceTest(){
        mockExtremeCondions.setupHangingService();
        RestAssured.given().baseUri(MockServiceManager.getBaseUrl()).when().get(MockExtremeConditions.PATH_HANG).then().assertThat().statusCode(200);


    }

    @Test
    public void setupRandomNetworkFaults(){
        mockExtremeCondions.setupRandomNetworkFaults();
        assertThrows(Exception.class, () -> {
            RestAssured.given()
                    .baseUri(MockServiceManager.getBaseUrl())
                    .when()
                    .get(MockExtremeConditions.PATH_FAULTS);
        }, "预期的网络层故障未发生");



    }

    @Test
    public void setupServiceUnavailableTest(){
        mockExtremeCondions.setupServiceUnavailable();
        RestAssured.given().baseUri(MockServiceManager.getBaseUrl()).when().get(MockExtremeConditions.PATH_DOWN).then().assertThat().statusCode(503);

    }

    @Test
    public void setupMemoryLeakSimulationTest(){
        mockExtremeCondions.setupMemoryLeakSimulation();
        RestAssured.given().baseUri(MockServiceManager.getBaseUrl()).when().get(MockExtremeConditions.PATH_LEAK).then().assertThat().statusCode(200);


    }

    @AfterEach
    public void afterEach() {

    }

}


