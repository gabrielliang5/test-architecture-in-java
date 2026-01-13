package core.test.engine.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import core.test.engine.base.BasePage;
import io.qameta.allure.Step;

public class InventoryPage extends BasePage {

    // 使用 Locator 预定义元素
    private final Locator productTitle;
    private final Locator addToCartBtn;
    private final Locator cartBadge;

    public InventoryPage(Page page) {
        super(page); // 传递给父类
        this.productTitle = page.locator(".inventory_item_name");
        this.addToCartBtn = page.locator("button[id='add-to-cart']");
        this.cartBadge = page.locator(".shopping_cart_badge");
    }

    @Step("业务,将第一个商品 [{productName}] 加入购物车")
    public void addFirstItemToCart(String productName) {
        // 这里展示了 Playwright 的高级过滤定位
        productTitle.filter(new Locator.FilterOptions().setHasText(productName)).first().click();
        addToCartBtn.first().click();
    }

    @Step("验证,检查购物车角标是否显示数量：{expectedCount}")
    public String getCartCount() {
        return cartBadge.textContent();
    }
}
