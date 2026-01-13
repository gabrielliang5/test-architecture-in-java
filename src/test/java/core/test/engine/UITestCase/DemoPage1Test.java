package core.test.engine.UITestCase;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import core.test.engine.base.UIBaseTest;
import core.test.engine.page.DemoPage1;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DemoPage1Test extends UIBaseTest {

    DemoPage1 demoPage1;

    @BeforeEach
    public void setUp(){
        demoPage1 = new DemoPage1(page);
    }

    @Test
    public void inputTaskTest(){

        demoPage1.open();

        demoPage1.inputText("test");
        demoPage1.pressEnter();
        demoPage1.inputText("test");
        demoPage1.pressEnter();
        demoPage1.inputText("test");
        demoPage1.pressEnter();
        String content = demoPage1.getCountNumber();
        Assertions.assertEquals(3,Integer.parseInt(content.trim().substring(0,1)));

    }

    @Test
    public void inputCheckBoxTest(){

        demoPage1.open();

        demoPage1.inputText("test1");
        demoPage1.pressEnter();
        demoPage1.inputText("test2");
        demoPage1.pressEnter();
        demoPage1.inputText("test3");
        demoPage1.pressEnter();
        Locator item = demoPage1.getItemByText("test2");
        demoPage1.click(item.getByRole(AriaRole.CHECKBOX));
        assertThat(item).hasClass("completed");


    }

    @Test
    public void inputButtonTest(){

        demoPage1.open();

        demoPage1.inputText("test1");
        demoPage1.pressEnter();
        demoPage1.inputText("test2");
        demoPage1.pressEnter();
        demoPage1.inputText("test3");
        demoPage1.pressEnter();
        Locator item = demoPage1.getItemByText("test2");
        item.hover();
        demoPage1.click(item.getByRole(AriaRole.BUTTON));

        Assertions.assertEquals(2,Integer.parseInt(demoPage1.getCountNumber().trim().substring(0,1)));

        assertThat(demoPage1.getItemByText("test2")).isHidden();


    }
}
