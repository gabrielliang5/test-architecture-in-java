package core.test.engine.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import core.test.engine.base.BasePage;

public class DemoPage1 extends BasePage {


    final Locator input;
    final Locator count;
    final Locator items;
    public DemoPage1(Page page) {
        super(page);

         input = page.getByPlaceholder("What needs to be done?");
          count =page.getByTestId("todo-count");
          items = page.getByTestId("todo-item");
    }

    public void open(){
        page.navigate("https://demo.playwright.dev/todomvc");
    }

    public void inputText(String text){
        input.fill(text);
    }

    public String getCountNumber(){
        return count.textContent();
    }

    public void pressEnter(){

        input.press("Enter");
    }

    public Locator getItemByText(String text){

        return items.filter(new Locator.FilterOptions().setHasText(text));
    }

    public void click(Locator locator){
        locator.click();
//        locator.getAttribute("class");
    }






}
