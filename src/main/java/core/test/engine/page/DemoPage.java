package core.test.engine.page;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import core.test.engine.base.BasePage;
import io.qameta.allure.Link;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.shaded.org.bouncycastle.crypto.digests.ParallelHash;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//这个文件就是用来练习的，没有具体的页面的对应，需求和元素都来自于Genemi
public class DemoPage extends BasePage {

    Page page;

    public DemoPage(Page page) {
        super(page);
        this.page = page;
    }

    /**
     *页面元素结构：
     *
     * 搜索框：<input id="search-input" placeholder="输入商品名称...">
     *
     * 分类下拉框：<select name="category"><option value="1">电子产品</option></select>
     *
     * 搜索按钮：<button type="submit" class="btn-primary">查询</button>
     *
     * 表格数据：搜索结果会加载进一个 <table>。
     *
     * 异步加载状态：点击查询后，会出现一个 <div class="loading-overlay">正在加载...</div>，加载完后该元素消失。
     *
     * 操作按钮：表格第一行商品有个“编辑”链接：<a href="/edit/101">编辑</a>。
     *
     * 你的任务：编写一个 Test 方法完成以下流程
     * 进入页面。
     *
     * 在搜索框输入 MacBook。
     *
     * 在分类下拉框选择 电子产品。
     *
     * 点击“查询”按钮。
     *
     * 关键点： 必须确保加载遮罩层（loading-overlay）消失后，再进行下一步操作。
     *
     * 点击第一行结果的“编辑”链接。
     *
     * 验证： 获取并打印编辑页面的标题（假设标题 class 是 .page-title）。
     */
    public void execises1(){

        /*
        * 在搜索框输入 MacBook。
         *
         * 在分类下拉框选择 电子产品。
         *
         * 点击“查询”按钮。
         *
         * 关键点： 必须确保加载遮罩层（loading-overlay）消失后，再进行下一步操作。
         *
         * 点击第一行结果的“编辑”链接。
         *
         * 验证： 获取并打印编辑页面的标题（假设标题 class 是 .page-title）。
         * */
        //1
        page.locator("#search-input").fill("MacBook");
        //2
        page.selectOption("select[name='category']", "1"); // 通过 value 选
        // 或者通过文字：
        page.selectOption("select[name='category']", new SelectOption().setLabel("电子产品"));
        //3
        page.locator("button.btn-primary").click();
        //4,异步加载Table,这不知道这么判断消失,这行代码会阻塞，直到这个 div 在页面上看不见为止。
        page.locator("div.loading-overlay").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        //先找第一行 tr，再在这一行里找写着“编辑”的链接。,用内容找a链接
        page.locator("table tr").nth(0).locator("a:has-text('编辑')").click();

        assertThat(page.locator(".page-title")).hasText("");



    }
    /*
    * <tr class="user-row">
  <td>101</td>
  <td>Montreal_Dev</td>
  <td>Active</td>
  <td>
    <button class="edit-btn">Edit</button>
    <button class="del-btn">Delete</button>
  </td>
</tr>
*
* 找到所有 class 为 user-row 的行。
从中筛选（filter）出包含文本 "Montreal_Dev" 的那一行。
在这一行内部，找到类名为 del-btn 的按钮并点击。
点击后会弹出一个浏览器原生的 Confirm 对话框，请写出**接受（Accept）**对话框的代码。（提示：Playwright 处理 Dialog 需要提前监听 page.onDialog）。
    * */
    public void execises2(){
        page.locator("table tr").filter(new Locator.FilterOptions().setHasText("Montreal_Dev"));

        page.onDialog(dialog -> {
            System.out.println("弹窗内容是: " + dialog.message()); // 顺便打印一下
            dialog.accept();
        });
        page.locator("table tr").getByRole(AriaRole.BUTTON).locator(".del-btn").click();


    }

    /*
    场景：任务管理系统（Task Management System）
    页面结构：
    Table ID: #task-table
    Columns:
    ID (td)
    任务名称 (td)
    负责人 (td)
    优先级 (td，内含 span 标签，内容为 "High", "Medium", "Low")
    操作列 (td，含 "Edit", "Delete", "Run" 三个按钮)
    任务要求：
    访问 https://test-system.com/tasks。
    在表格中找到 负责人为 "Dev_Team" 且 优先级为 "High" 的所有行中的 第一行。
    获取这一行的 任务 ID 并保存到变量。
    点击该行末尾的 "Run" 按钮（注意：点击后会弹出一个浏览器原生 Confirm 对话框，请点击“确定”）。
    点击完确定后，页面顶部会出现一个提示框 div.toast-success，请等待它消失后再进行最后一步。
    点击页面右上角的 "Logout" 链接（使用 getByRole 定位）。
    * */
    public void execises3(){

        page.navigate("https://test-system.com/tasks");
        List<Locator> list =  page.locator("#task-table").locator("tr").all();

        String id  = "";
//        Locator actual = null;
        for(Locator locator : list){

            if(locator.locator("td").nth(1).innerText().equals("Dev_Team")&&locator.locator("td").nth(2).innerText().equals("High")){

                id = locator.locator("td").nth(0).innerText();

                page.onDialog(dialog -> {
                    dialog.accept();
                });
                locator.locator("td").getByRole(AriaRole.BUTTON,new Locator.GetByRoleOptions().setName("run")).click();


                break;
            }

        }

        page.locator("div.toast-success").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Logout")).click();




    }

    /*
    * 就用  Filter  ，不写循环。 题目： 在一个表格中，找到包含 "101" 且包含 "Urgent" 的那一行，点击里面的 "Edit" 按钮
    * */

    public void execises4(){

        page.locator("table tr").filter(new Locator.FilterOptions().setHasText("101")).filter(new Locator.FilterOptions().setHasText("Urgent")).getByRole(AriaRole.BUTTON,new Locator.GetByRoleOptions().setName("Edit")).click();
    }


   /*
   * 页面情况：

    Table ID: #member-list

    Row A: ID:101, Name:Alex, Status:Active, Action:【Delete按钮】

    Row B: ID:102, Name:Alex (Legacy), Status:Inactive, Action:【Delete按钮】

    注意：这两个人的名字都包含 "Alex"。

    任务要求： 你必须删除那个 状态为 "Inactive" 的 Alex。

    要求1：使用你刚才学会的 Filter 模式（不写循环）。

    要求2：不能只过滤 "Alex"，因为会匹配到两行。
   * */
    public void execises5(){

        Locator locator =page.locator("#member-list").filter(
                new Locator.FilterOptions().setHasText("Inactive")).nth(0);

        String id = locator.innerText();
        System.out.println(id);
        locator.getByRole(AriaRole.BUTTON,new Locator.GetByRoleOptions().setName("Delete按钮")).click();
    }
   /*
   *<div id="user-management">
  <table role="grid" aria-label="User List">
    <thead>
      <tr>
        <th>Name</th>
        <th>Email</th>
        <th>Role</th>
        <th>Status</th>
        <th>Actions</th>
      </tr>
    </thead>
    <tbody>
      <tr class="user-row">
        <td>Jean Tremblay</td>
        <td>jean@example.com</td>
        <td>Admin</td>
        <td><span class="badge">Active</span></td>
        <td>
          <button class="btn-edit">Edit</button>
          <button class="btn-delete">Delete</button>
        </td>
      </tr>
      <tr class="user-row">
        <td>Marie Curie</td>
        <td>marie@example.com</td>
        <td>User</td>
        <td><span class="badge">Inactive</span></td> <td>
          <button class="btn-edit">Edit</button>
          <button class="btn-delete">Delete</button>
        </td>
      </tr>
      <tr class="user-row">
        <td>Jean Pierre</td>
        <td>jean@example.com</td> <td>User</td>
        <td><span class="badge">Inactive</span></td>
        <td>
          <button class="btn-edit">Edit</button>
          <button class="btn-delete">Delete</button>
        </td>
      </tr>
    </tbody>
  </table>
</div>
*
* 找到所有 Email 为 jean@example.com 的行。

进一步筛选，在这些行中，找到 Status 为 Active 的那一行（也就是 Row 1）。

在定位到的这一行中，点击 "Delete" 按钮。
   * */

    public void execises6(){
        //user-management
        /*
        当有id
        page.locator("#user-management").getByRole(AriaRole.ROW).filter().........

        当有aria-label="Active Users"
        page.getByRole(AriaRole.GRID, new Page.GetByRoleOptions().setName("Active Users"))来定位
        * */

        page.locator("table tr").filter(new Locator.FilterOptions().setHasText("jean@example.com")).filter(new Locator.FilterOptions().setHasText("Active")).getByRole(AriaRole.BUTTON,new Locator.GetByRoleOptions().setName("Delete")).click();


        page.getByRole(AriaRole.PROGRESSBAR, new Page.GetByRoleOptions().setName("xxxxxx")).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));

        /*
        * <select id="user-role">
                <option value="1">Admin</option>
                <option value="2">Editor</option>
                <option value="3">Guest</option>
          </select>
        *
        * */
        page.locator("#user-role").selectOption("Editor");
        page.locator("#user-role").selectOption("2");

        /*
        * Find the transaction row where the Transaction ID is TX-995.

          Click the 'Approve' button inside that specific row.

          Handle the 'Processing' overlay correctly.

          Verify that the status of that same row now displays 'Approved'.
        * */

        List<Locator> rows = page.locator("#TX-995").all();
        Locator result = null;
        for(Locator locator:rows){
           Locator button = locator.nth(3);
           if(button.innerText().equals("Approve")){
               button.click();
               result = locator;
               break;
           }

        }

//        page.locator("#TX-995").getByRole(AriaRole.BUTTON,new Locator.GetByRoleOptions().setName("Approve")).click();
        page.getByRole(AriaRole.PROGRESSBAR,new Page.GetByRoleOptions().setName("Processing")).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));

        assertThat(result.nth(0)).containsText("Approved");


        /*
        * 定位容器： 页面上有一个 ID 为 user-grid 的表格。

        锁定行： 在这个表格中，找到**“邮箱 (Email)”**这一列文本为 jean.pierre@mtl.com 的那一行。

        执行操作： 点击该行中**“角色 (Role)”**这一列的下拉框（select 标签），并将其选项改为 Admin。

        验证结果： 验证该下拉框现在的所选值确实是 Admin。
        * */

        Locator selected = page.locator("#user-grid").filter(new Locator.FilterOptions().setHasText("jean.pierre@mtl.com")).getByRole(AriaRole.COMBOBOX);
        selected.selectOption("Admin");

// 2. 验证：检查该元素当前的 value
// 资深工程师更倾向于用断言直接检查元素状态
        assertThat(selected).hasValue("Admin");

        page.locator("#Upload").click();


            assertThat(page.locator("text=Upload Complete")).equals("Upload Complete");
            page.getByRole(AriaRole.GRID).waitFor(new Locator.WaitForOptions());

    }

    public void execises7() throws URISyntaxException {

        //新页面，独立url
        // 捕捉那个独立出来的 Page 对象
        Page pdfPage = page.waitForPopup(() -> {
            page.getByText("打印账单").click();
        });

        //文件下载
        // 1. 开启监听
        Download download = pdfPage.waitForDownload(() -> {
            // 2. 触发下载的那个点击动作
            pdfPage.getByText("确认下载").click();
        });

        // 最轻量级断言  ：检查建议的文件名是否正确
        assertEquals("data_export.csv", download.suggestedFilename());
         //  检查下载是否成功完成（这会阻塞直到下载结束，但不持久化）
        assertNull(download.failure());//download.failure() 是一个方法，它的逻辑如下：如果下载成功： 这个方法会返回 null。

         // 3. 此时文件已经下载到临时目录了，你可以处理它，Playwright 会把下载的文件放在临时文件夹中，测试结束后自动删除。如果你需要保留文件（比如为了之后的断言），必须调用 .saveAs()。
        System.out.println(download.url()); // 查看下载链接
        download.saveAs(Paths.get("downloads/my_bill.pdf")); // 将它持久化到你指定的目录



       //上传一个文件
        URL resource = getClass().getClassLoader().getResource("attachments/my_resume.pdf");
        Path path = null;
        if (resource != null) {
            path = Paths.get(resource.toURI());
        }
        page.setInputFiles("#resume-upload", path);

        //   属性校验 验证 input 内部状态 (虽然是 fakepath，但文件名必须对)
        String displayedValue = page.locator("#resume-upload").inputValue();
        assertTrue(displayedValue.contains("my_resume.pdf"),
                "断言失败：Input 框内未显示正确的文件名");

        //上传多个文件
        String[] fileNames = {
                "attachments/my_resume.pdf",
                "attachments/my_resume1.pdf",
                "attachments/my_resume2.pdf"
        };

        Path[] paths = new Path[fileNames.length];

// 2. 循环处理路径
        for (int i = 0; i < fileNames.length; i++) {
            URL res = getClass().getClassLoader().getResource(fileNames[i]);

            // 关键点：检查每个资源是否存在
            if (res == null) {
                throw new RuntimeException("错误：在 Classpath 中找不到文件: " + fileNames[i]);
            }

            paths[i] = Paths.get(resource.toURI());
        }

      // 3. 执行上传
        page.setInputFiles("#resume-upload", paths);

        // alert 断言. 设置 Dialog 监听器
        page.onDialog(dialog -> {
            // 【核心断言】：验证弹窗里的文字是不是“上传成功！”
            assertEquals("上传成功！", dialog.message());

            //  点击弹窗上的“确定”按钮，让流程继续
            dialog.accept();

            System.out.println("弹窗断言通过，已点击确定。");
        });

        //页面断言，验证特定的文件名是否都在页面上
        String pageContent = page.textContent("body");
        assertTrue(pageContent.contains("my_resume.pdf"));
        assertTrue(pageContent.contains("my_resume1.pdf"));
        assertTrue(pageContent.contains("my_resume2.pdf"));


        assertThat(page.locator("")).not().hasText("body");




    }


}
