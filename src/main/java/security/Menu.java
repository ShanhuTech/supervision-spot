package security;

import java.sql.Connection;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.palestink.server.sdk.module.AbstractModule;
import com.palestink.server.sdk.module.annotation.Frequency;
import com.palestink.server.sdk.module.annotation.Method;
import com.palestink.server.sdk.module.annotation.Module;
import com.palestink.server.sdk.module.annotation.Parameter;
import com.palestink.server.sdk.module.annotation.ReturnResult;
import com.palestink.server.sdk.module.annotation.Returns;
import com.palestink.server.sdk.msg.Message;
import env.db.DruidInstance;

@Module(description = "菜单")
public final class Menu extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;

    public Menu() {
    }

    public Menu(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) throws Exception {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
    }

    @Method(description = "添加菜单", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "parent_uuid", text = "父级菜单", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,32}$", formatPrompt = "1-32位的数字或大小写字母", remark = "顶级菜单的uuid为0"),
            @Parameter(name = "name", text = "菜单名称", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z_-]{4,32}$", formatPrompt = "4-32位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "text", text = "显示文本", type = Parameter.Type.STRING, allowNull = false, format = "^.{2,32}$", formatPrompt = "2-32位的任意字符", remark = ""),
            @Parameter(name = "description", text = "描述", type = Parameter.Type.STRING, allowNull = true, format = "^.{2,64}$", formatPrompt = "2-64位的任意字符", remark = ""),
            @Parameter(name = "link", text = "链接", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,256}$", formatPrompt = "1-256位的任意字符", remark = ""),
            @Parameter(name = "order", text = "排序编号", type = Parameter.Type.INTEGER, allowNull = false, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = "") }, returns = @Returns())
    public final Message addMenu() {
        final String parentUuid = (String) this.parameter.get("parent_uuid");
        final String name = (String) this.parameter.get("name");
        final String text = (String) this.parameter.get("text");
        final String description = (String) this.parameter.get("description");
        final String link = (String) this.parameter.get("link");
        final Integer order = (Integer) this.parameter.get("order");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Menu obj = new security.dao.Menu(con);
                final Message resultMsg = obj.addMenu(parentUuid, name, text, description, link, order);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除菜单", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除菜单", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public final Message removeMenu() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Menu obj = new security.dao.Menu(con);
                final Message resultMsg = obj.removeMenuByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改菜单", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改菜单", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "parent_uuid", text = "父级菜单", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,32}$", formatPrompt = "1-32位的数字或大小写字母", remark = "顶级菜单的uuid为0"),
            @Parameter(name = "name", text = "菜单名称", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z_-]{4,32}$", formatPrompt = "4-32位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "text", text = "显示文本", type = Parameter.Type.STRING, allowNull = true, format = "^.{2,32}$", formatPrompt = "2-32位的任意字符", remark = ""),
            @Parameter(name = "description", text = "描述", type = Parameter.Type.STRING, allowNull = true, format = "^.{2,64}$", formatPrompt = "2-64位的任意字符", remark = ""),
            @Parameter(name = "description_null", text = "描述置空", type = Parameter.Type.STRING, allowNull = true, format = "^null$", formatPrompt = "常量null", remark = "用于清空操作"),
            @Parameter(name = "link", text = "链接", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,256}$", formatPrompt = "1-256位的任意字符", remark = ""),
            @Parameter(name = "link_null", text = "链接置空", type = Parameter.Type.STRING, allowNull = true, format = "^null$", formatPrompt = "常量null", remark = "用于清空操作"),
            @Parameter(name = "order", text = "排序编号", type = Parameter.Type.INTEGER, allowNull = true, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = "") }, returns = @Returns())
    public final Message modifyMenu() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String parentUuid = (String) this.parameter.get("parent_uuid");
        final String name = (String) this.parameter.get("name");
        final String text = (String) this.parameter.get("text");
        final String description = (String) this.parameter.get("description");
        final String descriptionNull = (String) this.parameter.get("description_null");
        final String link = (String) this.parameter.get("link");
        final String linkNull = (String) this.parameter.get("link_null");
        final Integer order = (Integer) this.parameter.get("order");
        // 至少修改一项字段
        {
            if ((null == parentUuid) && (null == name) && (null == text) && (null == description) && (null == descriptionNull) && (null == link) && (null == linkNull) && (null == order)) {
                msg.setStatus(Message.Status.ERROR);
                msg.setContent("NO_DATA_MODIFIED");
                msg.setAttach("没有修改数据");
                return msg;
            }
        }
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Menu obj = new security.dao.Menu(con);
                final Message resultMsg = obj.modifyMenu(uuid, parentUuid, name, text, (null != descriptionNull) ? "" : description, (null != linkNull) ? "" : link, order);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取菜单", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 10, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {}, returns = @Returns(results = {
            @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"), @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "菜单的uuid"),
            @ReturnResult(parentId = "array_id", id = "", name = "parent_uuid", type = "string[1,40]", isNecessary = true, description = "父级菜单的uuid"),
            @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[4,32]", isNecessary = true, description = "菜单名称"),
            @ReturnResult(parentId = "array_id", id = "", name = "text", type = "string[2,32]", isNecessary = true, description = "显示文本"),
            @ReturnResult(parentId = "array_id", id = "", name = "description", type = "string[2,64]", isNecessary = false, description = "描述"),
            @ReturnResult(parentId = "array_id", id = "", name = "link", type = "string[1,256]", isNecessary = false, description = "链接"), @ReturnResult(parentId = "array_id", id = "", name = "level", type = "int", isNecessary = true, description = "级别"),
            @ReturnResult(parentId = "array_id", id = "", name = "order", type = "int", isNecessary = true, description = "排序编号"),
            @ReturnResult(parentId = "array_id", id = "", name = "order_group", type = "string[1,60]", isNecessary = true, description = "排序编号组") }))
    public final Message getMenu() {
        Connection con = null;
        try {
            con = DruidInstance.getInstance().getTransConnection();
            final security.dao.Menu obj = new security.dao.Menu(con);
            final Message resultMsg = obj.getMenu(null, null, null);
            this.messageResultHandler(resultMsg, con, true);
            return resultMsg;
        } catch (final Exception e) {
            return this.catchHandler(con, e);
        } finally {
            this.finallyHandler(con);
        }
    }
}