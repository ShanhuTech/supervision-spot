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

@Module(description = "角色")
public final class Role extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;

    public Role() {
    }

    public Role(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) throws Exception {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
    }

    @Method(description = "添加角色", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "name", text = "角色名称", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z_-]{4,32}$", formatPrompt = "4-32位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "description", text = "描述", type = Parameter.Type.STRING, allowNull = true, format = "^.{2,64}$", formatPrompt = "2-64位的任意字符", remark = ""),
            @Parameter(name = "order", text = "排序编号", type = Parameter.Type.INTEGER, allowNull = false, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = "") }, returns = @Returns())
    public final Message addRole() {
        final String name = (String) this.parameter.get("name");
        final String description = (String) this.parameter.get("description");
        final Integer order = (Integer) this.parameter.get("order");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Role role = new security.dao.Role(con);
                final Message resultMsg = role.addRole(name, description, order);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除角色", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除角色", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public final Message removeRole() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Role role = new security.dao.Role(con);
                final Message resultMsg = role.removeRoleByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "修改角色信息", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "修改角色信息", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = Integer.MAX_VALUE, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改角色", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "name", text = "角色名称", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z_-]{4,32}$", formatPrompt = "4-32位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "description", text = "描述", type = Parameter.Type.STRING, allowNull = true, format = "^.{2,64}$", formatPrompt = "2-64位的任意字符", remark = ""),
            @Parameter(name = "description_null", text = "描述置空", type = Parameter.Type.STRING, allowNull = true, format = "^null$", formatPrompt = "常量null", remark = "用于清空操作"),
            @Parameter(name = "order", text = "排序编号", type = Parameter.Type.INTEGER, allowNull = true, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = "") }, returns = @Returns())
    public final Message modifyRoleInfo() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String name = (String) this.parameter.get("name");
        final String description = (String) this.parameter.get("description");
        final String descriptionNull = (String) this.parameter.get("description_null");
        final Integer order = (Integer) this.parameter.get("order");
        // 至少修改一项字段
        {
            if ((null == name) && (null == description) && (null == descriptionNull) && (null == order)) {
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
                final security.dao.Role role = new security.dao.Role(con);
                final Message resultMsg = role.modifyRole(uuid, name, (null != descriptionNull) ? "" : description, null, null, order);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改角色权限", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改角色", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "permissions", text = "角色权限", type = Parameter.Type.STRING, allowNull = false, format = "^.{1,}$", formatPrompt = "大于1位的任意字符", remark = "") }, returns = @Returns())
    public final Message modifyRolePermission() {
        final String uuid = (String) this.parameter.get("uuid");
        final String permissions = (String) this.parameter.get("permissions");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Role role = new security.dao.Role(con);
                final Message resultMsg = role.modifyRole(uuid, null, null, permissions, null, null);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改角色菜单", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改角色", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "menus", text = "角色菜单", type = Parameter.Type.STRING, allowNull = false, format = "^([0-9a-zA-Z]{2,32};)+$", formatPrompt = "以分号分割的2-32位的数字或大小写字母集合", remark = "") }, returns = @Returns())
    public final Message modifyRoleMenu() {
        final String uuid = (String) this.parameter.get("uuid");
        final String menus = (String) this.parameter.get("menus");
        // 修改角色
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Role role = new security.dao.Role(con);
                final Message resultMsg = role.modifyRole(uuid, null, null, null, menus, null);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取角色", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 10, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {}, returns = @Returns(results = {
            @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"), @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
            @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[4,32]", isNecessary = true, description = "角色名称"),
            @ReturnResult(parentId = "array_id", id = "", name = "description", type = "string[2,64]", isNecessary = false, description = "描述"),
            @ReturnResult(parentId = "array_id", id = "", name = "permission", type = "string[1,]", isNecessary = false, description = "角色权限"),
            @ReturnResult(parentId = "array_id", id = "", name = "menus", type = "list", isNecessary = false, description = "菜单列表（详情参见getMenu方法）"),
            @ReturnResult(parentId = "array_id", id = "", name = "order", type = "int", isNecessary = true, description = "排序编号") }))
    public final Message getRole() {
        Connection con = null;
        try {
            con = DruidInstance.getInstance().getTransConnection();
            final security.dao.Role role = new security.dao.Role(con);
            final Message resultMsg = role.getRole(null, null, null, null);
            this.messageResultHandler(resultMsg, con, true);
            return resultMsg;
        } catch (final Exception e) {
            return this.catchHandler(con, e);
        } finally {
            this.finallyHandler(con);
        }
    }
}