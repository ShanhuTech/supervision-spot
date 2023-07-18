package security;

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
import security.dao.Admin.Status;

@Module(description = "管理员")
public final class Admin extends AbstractModule {

    // JwtKey的Map
    public static final HashMap<String, String> JWT_KEY_MAP = new HashMap<>();

    static {
        Admin.JWT_KEY_MAP.put("LOGIN_TOKEN", "LOGIN_TOKEN"); // 登录Token
        // （若有其他登录后需要放入Token的数据，可通过静态调用添加）
    }

    // 登录失败重试计数（单位:次）
    public static final int LOGIN_FAILED_RETRY_COUNT = 5;

    // 账户冻结时间（单位:分钟）
    public static final int ACCOUNT_FROZEN_TIME = 10;

    private HttpServlet httpServlet;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;
    private Account account;

    public Admin() {
    }

    public Admin(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) throws Exception {
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
            @Parameter(name = "password", text = "密码", type = Parameter.Type.STRING, allowNull = false, format = "^\\S{1,16}$", formatPrompt = "1-16位的非空白字符", remark = "") }, returns = @Returns())
    public final Message addAdmin() {
        final String roleUuid = (String) this.parameter.get("role_uuid");
        final String name = (String) this.parameter.get("name");
        final String password = (String) this.parameter.get("password");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Admin obj = new security.dao.Admin(con);
                final Message resultMsg = obj.addAdmin(roleUuid, name, password);
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
                final security.dao.Admin obj = new security.dao.Admin(con);
                final Message resultMsg = obj.removeAdminByUuid(true, uuid);
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
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^NORMAL|LOCK$", formatPrompt = "常量NORMAL或LOCK", remark = "NORMAL:正常;LOCK:锁定") }, returns = @Returns())
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
        // 至少修改一项字段
        {
            if ((null == roleUuid) && (null == name) && (null == password) && (null == statusStr) && (null == status)) {
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
                final security.dao.Admin obj = new security.dao.Admin(con);
                final Message resultMsg = obj.modifyAdmin(uuid, roleUuid, name, password, null, null, null, status);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改管理员密码（自身）", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "old_password", text = "旧密码", type = Parameter.Type.STRING, allowNull = false, format = "^\\S{1,16}$", formatPrompt = "1-16位的非空白字符", remark = ""),
            @Parameter(name = "new_password", text = "新密码", type = Parameter.Type.STRING, allowNull = false, format = "^\\S{1,16}$", formatPrompt = "1-16位的非空白字符", remark = "") }, returns = @Returns())
    public final Message modifyAdminPasswordOfSelf() {
        final Message msg = new Message();
        String accountUuid = null;
        // 从账户令牌中获取信息
        {
            final Message resultMsg = this.account.getTokenData(Framework.ACCOUNT_TOKEN_UUID);
            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                return resultMsg;
            }
            accountUuid = ((JSONObject) resultMsg.getContent()).getString("data");
        }
        String dbPassword = null;
        // 获取管理员密码
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Admin obj = new security.dao.Admin(con);
                final Message resultMsg = obj.getAdmin(accountUuid, null, null, null, Integer.valueOf(0), Integer.valueOf(1));
                this.messageResultHandler(resultMsg, con, true);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONObject resultObj = (JSONObject) resultMsg.getContent();
                dbPassword = resultObj.getJSONArray("array").getJSONObject(0).getString("password");
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
        // 检查旧密码和数据库存储的密码是否相同
        {
            try {
                if (!dbPassword.equalsIgnoreCase(Md5.encode(((String) this.parameter.get("old_password")).getBytes()))) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("OLD_PASSWORD_ERROR");
                    msg.setAttach("旧密码错误");
                    return msg;
                }
            } catch (final Exception e) {
                msg.setStatus(Message.Status.EXCEPTION);
                msg.setContent(StringKit.getExceptionStackTrace(e));
                return msg;
            }
        }
        // 修改新密码
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Admin obj = new security.dao.Admin(con);
                final Message resultMsg = obj.modifyAdmin(accountUuid, null, null, (String) this.parameter.get("new_password"), null, null, null, null);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取管理员", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 10, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取管理员", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 100, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "管理员的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "role_uuid", text = "账户角色", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "name", text = "账户名称", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z_-]{4,16}$", formatPrompt = "4-16位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^NORMAL|FROZEN|LOCK$", formatPrompt = "常量NORMAL、FROZEN或LOCK$", remark = "NORMAL:正常;FROZEN:冻结;LOCK:锁定"),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "账户的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "role_uuid", type = "string[1,40]", isNecessary = true, description = "角色的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "role_name", type = "string[1,40]", isNecessary = true, description = "角色的名称"),
                    @ReturnResult(parentId = "array_id", id = "", name = "failed_retry_count", type = "int", isNecessary = true, description = "失败重试次数"),
                    @ReturnResult(parentId = "array_id", id = "", name = "frozen_datetime", type = "string[1,20]", isNecessary = false, description = "冻结时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "status", type = "string[1,16]", isNecessary = true, description = "账户的状态") }))
    public final Message getAdmin() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String roleUuid = (String) this.parameter.get("role_uuid");
        final String name = (String) this.parameter.get("name");
        final String statusStr = (String) this.parameter.get("status");
        Status status = null;
        if (null != statusStr) {
            status = Status.valueOf(statusStr);
        }
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        JSONObject resultObj = null;
        // 获取数据
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Admin obj = new security.dao.Admin(con);
                final Message resultMsg = obj.getAdmin(uuid, roleUuid, name, status, offset, rows);
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
        // 数据脱敏
        {
            final JSONArray array = resultObj.getJSONArray("array");
            for (int i = 0; i < array.length(); i++) {
                final JSONObject obj = array.getJSONObject(i);
                obj.remove("permissions");
                obj.remove("password");
                obj.remove("login_token");
            }
            msg.setStatus(Message.Status.SUCCESS);
            msg.setContent(resultObj);
            return msg;
        }
    }

    @Method(description = "管理员登录", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = Integer.MAX_VALUE, unit = Frequency.Unit.DAY) }, methodType = Method.Type.GET, parameters = {
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
        // 获取账户状态
        {
            JSONObject resultObj = null;
            {
                Connection con = null;
                try {
                    con = DruidInstance.getInstance().getTransConnection();
                    final security.dao.Admin obj = new security.dao.Admin(con);
                    final Message resultMsg = obj.getAdmin(null, null, name, null, Integer.valueOf(0), Integer.valueOf(1));
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
                        if (frozenTimestamp.longValue() + (ACCOUNT_FROZEN_TIME * 1000 * 60) < System.currentTimeMillis()) { // 冻结时间已过，解冻账号。
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
                    final int countRes = LOGIN_FAILED_RETRY_COUNT - (failedRetryCount.intValue() + 1);
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
                        msg.setAttach("密码错误（剩余重试次数：" + (LOGIN_FAILED_RETRY_COUNT - failedRetryCount.intValue() - 1) + "）");
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
        resultObj.put("name", name);
        if (null != menus) {
            resultObj.put("menus", menus);
        }
        resultObj.put("token", jwt);
        msg.setStatus(Message.Status.SUCCESS);
        msg.setContent(resultObj);
        return msg;
    }

    @Method(description = "管理员登出", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.GET, parameters = {}, returns = @Returns())
    public final Message adminLogoff() {
        final Message msg = new Message();
        msg.setStatus(Message.Status.SUCCESS);
        return msg;
    }

    /**
     * 比较管理员登录Token
     * 
     * @return 消息对象
     */
    private final Message compareAdminLoginToken() {
        final Message msg = new Message();
        String accountUuid = null;
        String loginToken = null;
        // 从账户令牌中获取信息
        {
            Message resultMsg = this.account.getTokenData(Framework.ACCOUNT_TOKEN_UUID);
            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                return resultMsg;
            }
            accountUuid = ((JSONObject) resultMsg.getContent()).getString("data");
            resultMsg = this.account.getTokenData(Admin.JWT_KEY_MAP.get("LOGIN_TOKEN"));
            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                return resultMsg;
            }
            loginToken = ((JSONObject) resultMsg.getContent()).getString("data");
        }
        JSONObject resultObj = null;
        // 获取账户的数据库登陆令牌是否存在
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final security.dao.Admin obj = new security.dao.Admin(con);
                final Message resultMsg = obj.getAdmin(accountUuid, null, null, null, Integer.valueOf(0), Integer.valueOf(1));
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
            final String dbLoginToken = resultObj.getJSONArray("array").getJSONObject(0).getString("login_token");
            if (!dbLoginToken.equalsIgnoreCase(loginToken)) {
                msg.setStatus(Message.Status.ERROR);
                msg.setContent("LOGIN_TOKEN_REFRESHED");
                msg.setAttach("登陆令牌已刷新");
                return msg;
            }
        }
        msg.setStatus(Message.Status.SUCCESS);
        return msg;
    }

    @Method(description = "刷新管理员令牌", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 100, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {}, returns = @Returns())
    public final Message refreshAdminToken() {
        // 比较管理员自身的登陆令牌（确保单点登录，如果不需要单点登录可以注销代码）
        {
            final Message aifMsg = this.compareAdminLoginToken();
            if (Message.Status.SUCCESS != aifMsg.getStatus()) {
                return aifMsg;
            }
        }
        // 刷新令牌
        {
            final JSONArray array = new JSONArray();
            Admin.JWT_KEY_MAP.entrySet().stream().forEach((entry) -> {
                final JSONObject obj = new JSONObject();
                obj.put("name", entry.getValue());
                array.put(obj);
            });
            final Message resultObj = this.account.refreshToken(array);
            return resultObj;
        }
    }
}