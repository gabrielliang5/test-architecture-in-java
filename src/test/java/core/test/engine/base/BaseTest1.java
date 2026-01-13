package core.test.engine.base;

import core.test.engine.mock.MockServiceManager;
import core.test.engine.util.UUIDUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
* 多线程下的mock，通过UUID，对请求和响应进行对应
* */
public class BaseTest1 {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTest1.class);

    protected static final boolean MOCK_ENABLED = false;
    @BeforeAll
    protected static void beforeAll() {

        if (MOCK_ENABLED) {
            MockServiceManager.start();
            logger.info("Mock 启动");
        } else {
            logger.info("Mock 已禁用");
        }
    }

    @BeforeEach
    public void beforeEach() {
        UUIDUtil.generateUUID();
    }

    @AfterEach
    public void afterEach() {
        UUIDUtil.clearUUID();
    }


    @AfterAll
    public static void afterAll() {

        if (MOCK_ENABLED) {
            MockServiceManager.stop();
        }
    }
}
