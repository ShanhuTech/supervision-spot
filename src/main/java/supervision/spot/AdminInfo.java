package supervision.spot;

import java.sql.Connection;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.Framework;
import com.palestink.server.sdk.module.AbstractModule;
import com.palestink.server.sdk.module.annotation.Frequency;
import com.palestink.server.sdk.module.annotation.Method;
import com.palestink.server.sdk.module.annotation.Module;
import com.palestink.server.sdk.module.annotation.Parameter;
import com.palestink.server.sdk.module.annotation.ReturnResult;
import com.palestink.server.sdk.module.annotation.Returns;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.encrypt.Md5;
import com.palestink.utils.string.StringKit;
import env.db.DruidInstance;
import security.Account;
import security.Admin;
import security.dao.Admin.Status;

@Module(description = "管理员信息")
public class AdminInfo extends AbstractModule {
    static {
        Admin.JWT_KEY_MAP.put("DEPARTMENT_UUID", "DEPARTMENT_UUID"); // 单位的uuid
    }

    private HttpServlet httpServlet;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;
    private Account account;

    public AdminInfo() {
    }

    public AdminInfo(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
        this.account = new Account(this.httpServlet, this.httpServletRequest, this.httpServletResponse, this.parameter);
    }

    @Method(description = "添加管理员", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "role_uuid", text = "账户角色", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "name", text = "账户名称", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z_-]{4,16}$", formatPrompt = "4-16位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "password", text = "密码", type = Parameter.Type.STRING, allowNull = false, format = "^\\S{1,16}$", formatPrompt = "1-16位的非空白字符", remark = ""),
            @Parameter(name = "real_name", text = "姓名", type = Parameter.Type.STRING, allowNull = false, format = "^.{1,16}$", formatPrompt = "1-16位的任意字符", remark = ""),
            @Parameter(name = "department_uuid", text = "父级单位的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public final Message addAdmin() {
        final String roleUuid = (String) this.parameter.get("role_uuid");
        final String name = (String) this.parameter.get("name");
        final String password = (String) this.parameter.get("password");
        final String realName = (String) this.parameter.get("real_name");
        final String departmentUuid = (String) this.parameter.get("department_uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.AdminInfo obj = new supervision.spot.dao.AdminInfo(con);
                final Message resultMsg = obj.addAdminInfo(roleUuid, null, name, password, realName, departmentUuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除管理员", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除账户", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public final Message removeAdmin() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.AdminInfo obj = new supervision.spot.dao.AdminInfo(con);
                final Message resultMsg = obj.removeAdminInfoByUuid(uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改管理员", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改账户", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "role_uuid", text = "账户角色", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "name", text = "账户名称", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z_-]{4,16}$", formatPrompt = "4-16位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "password", text = "密码", type = Parameter.Type.STRING, allowNull = true, format = "^\\S{1,16}$", formatPrompt = "1-16位的非空白字符", remark = ""),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^NORMAL|LOCK$", formatPrompt = "常量NORMAL或LOCK", remark = "NORMAL:正常;LOCK:锁定"),
            @Parameter(name = "real_name", text = "姓名", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,16}$", formatPrompt = "1-16位的任意字符", remark = ""),
            @Parameter(name = "department_uuid", text = "父级单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "manage_department_uuid", text = "管理部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "manage_department_type", text = "管理部门类别", type = Parameter.Type.STRING, allowNull = true, format = "^ADD|REMOVE$", formatPrompt = "常量ADD或REMOVE", remark = "ADD:添加;REMOVE$:删除"),
            @Parameter(name = "score", text = "分值", type = Parameter.Type.INTEGER, allowNull = true, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = "") }, returns = @Returns())
    public final Message modifyAdmin() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String roleUuid = (String) this.parameter.get("role_uuid");
        final String name = (String) this.parameter.get("name");
        final String password = (String) this.parameter.get("password");
        final String statusStr = (String) this.parameter.get("status");
        Status status = null;
        if (null != statusStr) {
            status = Status.valueOf(statusStr);
        }
        final String realName = (String) this.parameter.get("real_name");
        final String departmentUuid = (String) this.parameter.get("department_uuid");
        final String manageDepartmentUuid = (String) this.parameter.get("manage_department_uuid");
        final String manageDepartmentTypeStr = (String) this.parameter.get("manage_department_type");
        supervision.spot.dao.AdminInfo.Manage manage = null;
        if (null != manageDepartmentTypeStr) {
            manage = supervision.spot.dao.AdminInfo.Manage.valueOf(manageDepartmentTypeStr);
        }
        final Integer score = (Integer) this.parameter.get("score");
        // 至少修改一项字段
        {
            if ((null == roleUuid) && (null == name) && (null == password) && (null == statusStr) && (null == status) && (null == realName) && (null == departmentUuid) && (null == manageDepartmentUuid) && (null == manage) && (null == score)) {
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
                final supervision.spot.dao.AdminInfo obj = new supervision.spot.dao.AdminInfo(con);
                final Message resultMsg = obj.modifyAdmin(uuid, roleUuid, name, password, null, null, null, status, realName, departmentUuid, null, score, manage, manageDepartmentUuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取管理员", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 10, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "管理员的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "name", text = "账户名称", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z_-]{4,16}$", formatPrompt = "4-16位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^NORMAL|LOCK$", formatPrompt = "常量NORMAL或LOCK$", remark = "NORMAL:正常;LOCK:锁定"),
            @Parameter(name = "department_uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "problem_count_symbol", text = "问题类型符号", type = Parameter.Type.STRING, allowNull = true, format = "^[><=]{1,3}$", formatPrompt = "常量>、<、>=、<=或=", remark = ""),
            @Parameter(name = "problem_count", text = "问题类型", type = Parameter.Type.INTEGER, allowNull = true, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从0开始"),
            @Parameter(name = "manage_department_uuid_like", text = "管理单位的uuid的Like", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "all_from_department_uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            // @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始")
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "管理员信息账户列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "账户的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "role_uuid", type = "string[1,40]", isNecessary = true, description = "角色的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "role_name", type = "string[1,40]", isNecessary = true, description = "角色的名称"),
                    @ReturnResult(parentId = "array_id", id = "", name = "failed_retry_count", type = "int", isNecessary = true, description = "失败重试次数"),
                    @ReturnResult(parentId = "array_id", id = "", name = "frozen_datetime", type = "string[1,20]", isNecessary = false, description = "冻结时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "status", type = "string[1,16]", isNecessary = true, description = "账户的状态"),
                    @ReturnResult(parentId = "array_id", id = "", name = "real_name", type = "string[1,16]", isNecessary = true, description = "姓名"),
                    @ReturnResult(parentId = "array_id", id = "", name = "department_uuid", type = "string[1,40]", isNecessary = true, description = "单位的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "problem_count", type = "int", isNecessary = true, description = "问题计数"),
                    @ReturnResult(parentId = "array_id", id = "", name = "score", type = "int", isNecessary = true, description = "分值"),
                    @ReturnResult(parentId = "array_id", id = "", name = "manage_departments", type = "string[1,]", isNecessary = false, description = "管理单位（单位的uuid集合，分号分割）") }))
    public Message getAdmin() {
        final String uuid = (String) this.parameter.get("uuid");
        final String name = (String) this.parameter.get("name");
        final String statusStr = (String) this.parameter.get("status");
        Status status = null;
        if (null != statusStr) {
            status = Status.valueOf(statusStr);
        }
        final String departmentUuid = (String) this.parameter.get("department_uuid");
        final String problemCountSymbol = (String) this.parameter.get("problem_count_symbol");
        final Integer problemCount = (Integer) this.parameter.get("problem_count");
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        final String manageDepartmentUuidLike = (String) this.parameter.get("manage_department_uuid_like");
        final String allFromDepartmentUuid = (String) this.parameter.get("all_from_department_uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.AdminInfo obj = new supervision.spot.dao.AdminInfo(con);
                final Message resultMsg = obj.getAdminInfo(uuid, name, status, departmentUuid, problemCountSymbol, problemCount, manageDepartmentUuidLike, allFromDepartmentUuid, offset, rows);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }
    @Method(description = "获取管理员", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 10, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "管理员的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "name", text = "账户名称", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z_-]{4,16}$", formatPrompt = "4-16位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^NORMAL|LOCK$", formatPrompt = "常量NORMAL或LOCK$", remark = "NORMAL:正常;LOCK:锁定"),
            @Parameter(name = "department_uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "problem_count_symbol", text = "问题类型符号", type = Parameter.Type.STRING, allowNull = true, format = "^[><=]{1,3}$", formatPrompt = "常量>、<、>=、<=或=", remark = ""),
            @Parameter(name = "problem_count", text = "问题类型", type = Parameter.Type.INTEGER, allowNull = true, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从0开始"),
            @Parameter(name = "manage_department_uuid_like", text = "管理单位的uuid的Like", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "all_from_department_uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            // @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始")
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "管理员信息账户列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "账户的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "role_uuid", type = "string[1,40]", isNecessary = true, description = "角色的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "role_name", type = "string[1,40]", isNecessary = true, description = "角色的名称"),
                    @ReturnResult(parentId = "array_id", id = "", name = "failed_retry_count", type = "int", isNecessary = true, description = "失败重试次数"),
                    @ReturnResult(parentId = "array_id", id = "", name = "frozen_datetime", type = "string[1,20]", isNecessary = false, description = "冻结时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "status", type = "string[1,16]", isNecessary = true, description = "账户的状态"),
                    @ReturnResult(parentId = "array_id", id = "", name = "real_name", type = "string[1,16]", isNecessary = true, description = "姓名"),
                    @ReturnResult(parentId = "array_id", id = "", name = "department_uuid", type = "string[1,40]", isNecessary = true, description = "单位的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "problem_count", type = "int", isNecessary = true, description = "问题计数"),
                    @ReturnResult(parentId = "array_id", id = "", name = "score", type = "int", isNecessary = true, description = "分值"),
                    @ReturnResult(parentId = "array_id", id = "", name = "manage_departments", type = "string[1,]", isNecessary = false, description = "管理单位（单位的uuid集合，分号分割）") }))
    public Message getAdminShort() {
        final String uuid = (String) this.parameter.get("uuid");
        final String name = (String) this.parameter.get("name");
        final String statusStr = (String) this.parameter.get("status");
        Status status = null;
        if (null != statusStr) {
            status = Status.valueOf(statusStr);
        }
        final String departmentUuid = (String) this.parameter.get("department_uuid");
        final String problemCountSymbol = (String) this.parameter.get("problem_count_symbol");
        final Integer problemCount = (Integer) this.parameter.get("problem_count");
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        final String manageDepartmentUuidLike = (String) this.parameter.get("manage_department_uuid_like");
        final String allFromDepartmentUuid = (String) this.parameter.get("all_from_department_uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.AdminInfo obj = new supervision.spot.dao.AdminInfo(con);
                final Message resultMsg = obj.getAdminInfoShort(uuid, name, status, departmentUuid, problemCountSymbol, problemCount, manageDepartmentUuidLike, allFromDepartmentUuid, offset, rows);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "管理员登录", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = Integer.MAX_VALUE, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.GET, parameters = {
            @Parameter(name = "name", text = "账户名称", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z_-]{4,16}$", formatPrompt = "4-16位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "password", text = "密码", type = Parameter.Type.STRING, allowNull = false, format = "^\\S{1,16}$", formatPrompt = "1-16位的非空白字符", remark = "") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "uuid", type = "string[1,64]", isNecessary = true, description = "账户的uuid"),
                    @ReturnResult(parentId = "", id = "", name = "name", type = "string[4,16]", isNecessary = true, description = "账户的名称"),
                    @ReturnResult(parentId = "", id = "", name = "menu", type = "array", isNecessary = false, description = "账户的菜单（详情参见getMenu）"),
                    @ReturnResult(parentId = "", id = "", name = "expires", type = "string[1,20]", isNecessary = true, description = "token过期时间"),
                    @ReturnResult(parentId = "", id = "", name = "token", type = "string[1,]", isNecessary = true, description = "token") }))
    public final Message adminLogon() {
        final Message msg = new Message();
        // 获取账户信息
        final String name = (String) this.parameter.get("name");
        String uuid = null;
        String password = null;
        String permissions = null;
        String menus = null;
        Integer failedRetryCount = null;
        Long frozenTimestamp = null;
        Status status = null;
        String departmentUuid = null;
        // 获取账户状态
        {
            JSONObject resultObj = null;
            {
                Connection con = null;
                try {
                    con = DruidInstance.getInstance().getTransConnection();
                    final supervision.spot.dao.AdminInfo obj = new supervision.spot.dao.AdminInfo(con);
                    final Message resultMsg = obj.getAdminInfo(null, name, null, null, null, null, null, null, Integer.valueOf(0), Integer.valueOf(1));
                    this.messageResultHandler(resultMsg, con, true);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    resultObj = (JSONObject) resultMsg.getContent();
                } catch (final Exception e) {
                    return this.catchHandler(con, e);
                } finally {
                    this.finallyHandler(con);
                }
            }
            {
                try {
                    final JSONArray array = resultObj.getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PASSWORD_WRONG");
                        msg.setAttach("密码错误"); // 这里应该返回“没有找到账户”才对，但考虑到安全因素，所以返回密码错误（Password Wrong，用以区分密码错误）。
                        return msg;
                    }
                    final JSONObject obj = array.getJSONObject(0);
                    uuid = obj.getString("uuid");
                    password = obj.getString("password");
                    if (obj.has("permissions")) {
                        permissions = obj.getString("permissions");
                    }
                    if (obj.has("menus")) {
                        menus = obj.getString("menus");
                    }
                    failedRetryCount = Integer.valueOf(obj.getInt("failed_retry_count"));
                    if (obj.has("frozen_timestamp")) {
                        frozenTimestamp = Long.valueOf(obj.getLong("frozen_timestamp"));
                    }
                    final String statusStr = obj.getString("status");
                    status = Status.valueOf(statusStr);
                    departmentUuid = obj.getString("department_uuid");
                } catch (final Exception e) {
                    msg.setStatus(Message.Status.EXCEPTION);
                    msg.setContent(StringKit.getExceptionStackTrace(e));
                    return msg;
                }
            }
        }
        // 登录“检索”成功，判断账户状态。
        {
            STATUS: {
                if (Status.NORMAL == status) { // 账户正常
                    break STATUS;
                } else if (Status.FROZEN == status) { // 账号冻结
                    if (null != frozenTimestamp) { // 判断冻结时间是否已过
                        if (frozenTimestamp.longValue() + (security.Admin.ACCOUNT_FROZEN_TIME * 1000 * 60) < System.currentTimeMillis()) { // 冻结时间已过，解冻账号。
                            Connection con = null;
                            try {
                                con = DruidInstance.getInstance().getTransConnection();
                                final security.dao.Admin obj = new security.dao.Admin(con);
                                final Message resultMsg = obj.modifyAdmin(uuid, null, null, null, Integer.valueOf(0), Long.valueOf(0L)/* 置null */, null, Status.NORMAL);
                                this.messageResultHandler(resultMsg, con, true);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    msg.setStatus(Message.Status.ERROR);
                                    msg.setContent("UNFROZEN_ACCOUNT_FAIL");
                                    msg.setAttach("解冻账户失败");
                                    return msg;
                                }
                            } catch (final Exception e) {
                                return this.catchHandler(con, e);
                            } finally {
                                this.finallyHandler(con);
                            }
                            break STATUS;
                        }
                    }
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ACCOUNT_HAS_BEEN_FROZEN");
                    msg.setAttach("账户已冻结");
                    return msg;
                } else if (Status.LOCK == status) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ACCOUNT_HAS_BEEN_LOCKED");
                    msg.setAttach("账户已锁定");
                    return msg;
                } else {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ACCOUNT_STATUS_EXCEPTION");
                    msg.setAttach("账户状态异常");
                    return msg;
                }
            }
        }
        // 判断密码是否匹配
        {
            String pwd = null;
            try {
                pwd = Md5.encode(((String) this.parameter.get("password")).getBytes());
            } catch (final Exception e) {
                msg.setStatus(Message.Status.EXCEPTION);
                msg.setContent(StringKit.getExceptionStackTrace(e));
                return msg;
            }
            if (!password.equalsIgnoreCase(pwd)) { // 如果密码不匹配，判断是否超过最大重试计数限制。
                Connection con = null;
                try {
                    con = DruidInstance.getInstance().getTransConnection();
                    final security.dao.Admin obj = new security.dao.Admin(con);
                    final int countRes = security.Admin.LOGIN_FAILED_RETRY_COUNT - (failedRetryCount.intValue() + 1);
                    if (0 < countRes) { // 如果没有超过限制计数，那么增加失败重试计数。并且给予密码错误的提示信息。
                        final Message resultMsg = obj.modifyAdmin(uuid, null, null, null, Integer.valueOf(failedRetryCount.intValue() + 1), null, null, null);
                        this.messageResultHandler(resultMsg, con, true);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("INCREASE_FAILED_RETRY_COUNT_FAIL");
                            msg.setAttach("增加失败重试计数失败");
                            return msg;
                        }
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PASSWORD_ERROR"); // 账户存在但密码错误，返回密码错误（Password Error，用以区分账户不存在）。
                        msg.setAttach("密码错误（剩余重试次数：" + (security.Admin.LOGIN_FAILED_RETRY_COUNT - failedRetryCount.intValue() - 1) + "）");
                        return msg;
                    }
                    // 如果已经超过规定值，执行以下操作：冻结账号；失败重试计数归零；返回账户冻结信息。
                    final Message resultMsg = obj.modifyAdmin(uuid, null, null, null, Integer.valueOf(0), Long.valueOf(System.currentTimeMillis()), null, Status.FROZEN);
                    this.messageResultHandler(resultMsg, con, true);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("FROZEN_ACCOUNT_FAIL"); // 冻结账户失败
                        msg.setAttach("冻结账户失败");
                        return msg;
                    }
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ACCOUNT_FROZEN");
                    msg.setAttach("密码重试次数太多账户被冻结");
                    return msg;
                } catch (final Exception e) {
                    return this.catchHandler(con, e);
                } finally {
                    this.finallyHandler(con);
                }
            }
        }
        final String loginToken = StringKit.getUuidStr(true);
        // 用户名密码匹配，正常登录。需要重置账户数据，包括：登陆令牌、失败重试次数、冻结时间、账户状态。
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Admin obj = new security.dao.Admin(con);
                final Message resultMsg = obj.modifyAdmin(uuid, null, null, null, Integer.valueOf(0), Long.valueOf(0L)/* 置null */, loginToken, Status.NORMAL);
                this.messageResultHandler(resultMsg, con, true);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("MODIFY_ACCOUNT_DATA_FAIL");
                    msg.setAttach("修改账户数据失败");
                    return msg;
                }
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
        String jwt = null;
        // 生成Jwt
        {
            final JSONArray array = new JSONArray();
            final JSONObject obj = new JSONObject();
            obj.put(Admin.JWT_KEY_MAP.get("LOGIN_TOKEN"), loginToken);
            obj.put(Admin.JWT_KEY_MAP.get("DEPARTMENT_UUID"), departmentUuid);
            array.put(obj);
            try {
                jwt = this.account.generateToken(uuid, permissions, array);
            } catch (final Exception e) {
                msg.setStatus(Message.Status.EXCEPTION);
                msg.setContent(StringKit.getExceptionStackTrace(e));
                return msg;
            }
        }
        final JSONObject resultObj = new JSONObject();
        resultObj.put("uuid", uuid);
        resultObj.put("name", name);
        if (null != menus) {
            resultObj.put("menus", menus);
        }
        resultObj.put("token", jwt);
        // 获取管理员信息
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.AdminInfo obj = new supervision.spot.dao.AdminInfo(con);
                final Message resultMsg = obj.getAdminInfo(uuid, null, null, null, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                resultObj.put("admin_info", ((JSONObject) resultMsg.getContent()).getJSONArray("array"));
                this.messageResultHandler(resultMsg, con, true);
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
        // 添加登入登出信息
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.LogonLogoffInfo obj = new supervision.spot.dao.LogonLogoffInfo(con);
                final Message resultMsg = obj.addLogonLogoffInfo(uuid, supervision.spot.dao.LogonLogoffInfo.Type.LOGIN, this.httpServletRequest.getRemoteAddr());
                this.messageResultHandler(resultMsg, con, true);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ADD_LOGON_LOGOFF_INFO_FAIL");
                    msg.setAttach("添加修改账户数据失败");
                    return msg;
                }
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
        msg.setStatus(Message.Status.SUCCESS);
        msg.setContent(resultObj);
        return msg;
    }

    @Method(description = "管理员登出", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.GET, parameters = {}, returns = @Returns())
    public final Message adminLogoff() {
        String accountUuid = null;
        // 从账户令牌中获取信息
        {
            final Message resultMsg = this.account.getTokenData(Framework.ACCOUNT_TOKEN_UUID);
            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                return resultMsg;
            }
            accountUuid = ((JSONObject) resultMsg.getContent()).getString("data");
        }
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.LogonLogoffInfo obj = new supervision.spot.dao.LogonLogoffInfo(con);
                final Message resultMsg = obj.addLogonLogoffInfo(accountUuid, supervision.spot.dao.LogonLogoffInfo.Type.LOGOFF, this.httpServletRequest.getRemoteAddr());
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }
}