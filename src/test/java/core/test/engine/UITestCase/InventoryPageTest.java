package core.test.engine.UITestCase;

import core.test.engine.base.UIBaseTest;
import core.test.engine.page.InventoryPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class InventoryPageTest extends UIBaseTest {

    InventoryPage inventoryPage;

    @BeforeEach
    public void beforeEach() {

        inventoryPage  = new InventoryPage(page);

    }

    @Test
    @DisplayName("电商流测试：添加商品至购物车并验证")
    void shouldAddItemToCart() {


        inventoryPage.navigateTo("https://www.example.com/inventory");

        // 这里的步骤会出现在报告中
        inventoryPage.addFirstItemToCart("Backpack");

        // Web-First Assertion (Day 6 知识点)
        assertThat(page.locator(".shopping_cart_badge")).hasText("1");
    }
}
