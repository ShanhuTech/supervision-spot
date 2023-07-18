package security.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.encrypt.Md5;
import com.palestink.utils.string.StringKit;

/**
 * 用户
 */
public final class User {
    // 数据库表名
    public static final String DATABASE_TABLE_NAME = "account-security_user";

    // 状态
    public static enum Status {
        // 正常
        NORMAL,
        // 冻结
        FROZEN,
        // 锁定
        LOCK
    }

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public User(final Connection connection) throws Exception {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加用户
     * 
     * @param roleUuid 角色的uuid
     * @param name 用户名称
     * @param password 密码
     * @return 消息对象
     */
    public final Message addUser(final String roleUuid, final String name, final String password) {
        final Message msg = new Message();
        try {
            // 是否存在角色
            {
                final Role role = new Role(this.connection);
                if (!role.isExistRole(roleUuid, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ROLE_NOT_EXIST");
                    msg.setAttach("角色不存在");
                    return msg;
                }
            }
            // 是否存在用户
            {
                if (this.isExistUser(null, name, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ADMIN_NAME_EXIST");
                    msg.setAttach("用户名称已存在");
                    return msg;
                }
            }
            final String uuid = StringKit.getUuidStr(true);
            final long createTimestamp = System.currentTimeMillis();
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp));
            // 添加用户
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "role_uuid", "name", "password", "failed_retry_count", "login_token", "status", "create_timestamp", "create_datetime"));
                    ps.setString(1, uuid);
                    ps.setString(2, roleUuid);
                    ps.setString(3, name);
                    ps.setString(4, Md5.encode(password.getBytes()));
                    ps.setInt(5, 0);
                    ps.setString(6, uuid);
                    ps.setString(7, Status.NORMAL.toString());
                    ps.setLong(8, createTimestamp);
                    ps.setString(9, createDatetime);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_ADMIN_FAIL");
                        msg.setAttach("添加用户失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            msg.setStatus(Message.Status.SUCCESS);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("uuid", uuid);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 根据uuid删除用户
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid uuid
     * @return 消息对象
     */
    public final Message removeUserByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在用户
            {
                if (checkExist) {
                    if (!this.isExistUser(uuid, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADMIN_NOT_EXIST");
                        msg.setAttach("用户不存在");
                        return msg;
                    }
                }
            }
            // 删除用户
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "'";
                    final HashMap<String, Object> hm = new HashMap<>();
                    hm.put("remove_timestamp", Long.valueOf(System.currentTimeMillis()));
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("REMOVE_ADMIN_FAIL");
                        msg.setAttach("删除用户失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            msg.setStatus(Message.Status.SUCCESS);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 根据角色的uuid删除用户<br />
     * 非自身uuid删除无需检查带删除数据是否存
     * 
     * @param roleUuid 角色的uuid
     * @return 消息对象
     */
    public final Message removeUserByRoleUuid(final String roleUuid) {
        final Message msg = new Message();
        if (null == roleUuid) {
            msg.setStatus(Message.Status.ERROR);
            msg.setContent("REMOVE_PARAMETER_NOT_ALLOW_NULL");
            msg.setAttach("删除参数不允许为空");
            return msg;
        }
        try {
            // 获取符合条件的用户
            final ArrayList<String> uuidList = new ArrayList<>();
            {
                final Message resultMsg = this.getUser(null, roleUuid, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                for (int i = 0; i < array.length(); i++) {
                    final String uuid = array.getJSONObject(i).getString("uuid");
                    uuidList.add(uuid);
                }
            }
            // 删除符合条件的用户
            {
                for (int i = 0; i < uuidList.size(); i++) {
                    final String uuid = uuidList.get(i);
                    final Message resultMsg = this.removeUserByUuid(false, uuid);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                }
            }
            msg.setStatus(Message.Status.SUCCESS);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 修改用户（至少修改一项字段）
     * 
     * @param uuid 用户的uuid
     * @param roleUuid 角色的uuid（允许为null）
     * @param name 账户名称（允许为null）
     * @param password 密码（允许为null）
     * @param failedRetryCount 失败重复计数（允许为null）
     * @param frozenTimestamp 冻结时间戳（允许为null，为null不修改，长度为0则清空）
     * @param loginToken 登录Token（允许为null）
     * @param status 状态（允许为null）
     * @return 消息对象
     */
    public final Message modifyUser(final String uuid, final String roleUuid, final String name, final String password, final Integer failedRetryCount, final Long frozenTimestamp, final String loginToken, final Status status) {
        final Message msg = new Message();
        try {
            // 是否存在用户
            {
                if (!this.isExistUser(uuid, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ADMIN_NOT_EXIST");
                    msg.setAttach("用户不存在");
                    return msg;
                }
                if (null != name) {
                    if (this.isExistUser(null, name, uuid)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADMIN_NAME_EXIST");
                        msg.setAttach("用户名称已存在");
                        return msg;
                    }
                }
            }
            // 是否存在角色
            {
                if (null != roleUuid) {
                    final Role role = new Role(this.connection);
                    if (!role.isExistRole(roleUuid, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ROLE_NOT_EXIST");
                        msg.setAttach("角色不存在");
                        return msg;
                    }
                }
            }
            // 修改用户
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "'";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != roleUuid) {
                        hm.put("role_uuid", roleUuid);
                    }
                    if (null != name) {
                        hm.put("name", name);
                    }
                    if (null != password) {
                        hm.put("password", Md5.encode(password.getBytes()));
                    }
                    if (null != failedRetryCount) {
                        hm.put("failed_retry_count", failedRetryCount);
                    }
                    if (null != frozenTimestamp) {
                        if (0 < frozenTimestamp.longValue()) {
                            final String frozenDatetime = this.simpleDateFormat.format(new Date(frozenTimestamp.longValue()));
                            hm.put("frozen_timestamp", frozenTimestamp);
                            hm.put("frozen_datetime", frozenDatetime);
                        } else {
                            hm.put("frozen_timestamp", null);
                            hm.put("frozen_datetime", null);
                        }
                    }
                    if (null != loginToken) {
                        hm.put("login_token", loginToken);
                    }
                    if (null != status) {
                        hm.put("status", status.toString());
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_ADMIN_FAIL");
                        msg.setAttach("修改用户失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            msg.setStatus(Message.Status.SUCCESS);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 获取用户
     * 
     * @param uuid 用户的uuid（允许为null）
     * @param roleUuid 角色的uuid（允许为null）
     * @param name 用户名称（允许为null）
     * @param status 状态（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public final Message getUser(final String uuid, final String roleUuid, final String name, final Status status, final Integer offset, final Integer rows) {
        final Message msg = new Message();
        try {
            final JSONArray whereArray = new JSONArray();
            if (null != uuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "u");
                obj.put("name", "uuid");
                obj.put("symbol", "=");
                obj.put("value", uuid);
                whereArray.put(obj);
            }
            if (null != roleUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "u");
                obj.put("name", "role_uuid");
                obj.put("symbol", "=");
                obj.put("value", roleUuid);
                whereArray.put(obj);
            }
            if (null != name) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "u");
                obj.put("name", "name");
                obj.put("symbol", "=");
                obj.put("value", name);
                whereArray.put(obj);
            }
            if (null != status) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "u");
                obj.put("name", "status");
                obj.put("symbol", "=");
                obj.put("value", status.toString());
                whereArray.put(obj);
            }
            {
                // 排除逻辑删除
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "u");
                obj.put("name", "remove_timestamp");
                obj.put("symbol", "is");
                obj.put("value", JSONObject.NULL);
                whereArray.put(obj);
            }
            final JSONObject resultObj = new JSONObject();
            final String whereSql = DatabaseKit.composeWhereSql(whereArray);
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("select count(*) as `count` from `" + DATABASE_TABLE_NAME + "` u " + whereSql);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        resultObj.put("count", rs.getInt("count"));
                    } else {
                        resultObj.put("count", 0);
                    }
                } finally {
                    if (null != rs) {
                        rs.close();
                    }
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    String limitCode = "";
                    if ((null != offset) && (null != rows)) {
                        limitCode = "limit ?, ?";
                    }
                    ps = this.connection.prepareStatement(
                            "select u.uuid as `uuid`, u.role_uuid as `role_uuid`, r.name as `role_name`, r.permissions as `permissions`, r.menus as `menus`, u.name as `name`, u.password as `password`, u.failed_retry_count as `failed_retry_count`, u.frozen_timestamp as `frozen_timestamp`, u.frozen_datetime as `frozen_datetime`, u.login_token as `login_token`, u.status as `status`, u.create_datetime as `create_datetime` from `"
                                    + DATABASE_TABLE_NAME + "` u inner join `" + Role.DATABASE_TABLE_NAME + "` r on u.role_uuid = r.uuid " + whereSql + " order by `name` asc " + limitCode);
                    if ((null != offset) && (null != rows)) {
                        ps.setInt(1, offset.intValue());
                        ps.setInt(2, rows.intValue());
                    }
                    rs = ps.executeQuery();
                    final JSONArray array = new JSONArray();
                    while (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        obj.put("uuid", rs.getString("uuid"));
                        obj.put("role_uuid", rs.getString("role_uuid"));
                        obj.put("role_name", rs.getString("role_name"));
                        obj.put("permissions", rs.getString("permissions"));
                        obj.put("menus", rs.getString("menus"));
                        obj.put("name", rs.getString("name"));
                        obj.put("password", rs.getString("password"));
                        obj.put("failed_retry_count", rs.getInt("failed_retry_count"));
                        obj.put("frozen_timestamp", rs.getLong("frozen_timestamp"));
                        obj.put("frozen_datetime", rs.getString("frozen_datetime"));
                        obj.put("login_token", rs.getString("login_token"));
                        obj.put("status", rs.getString("status"));
                        obj.put("create_datetime", rs.getString("create_datetime"));
                        array.put(obj);
                    }
                    resultObj.put("array", array);
                } finally {
                    if (null != rs) {
                        rs.close();
                    }
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            msg.setStatus(Message.Status.SUCCESS);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 是否存在用户
     * 
     * @param uuid 用户的uuid（允许为null）
     * @param name 账户名称（允许为null）
     * @param excludeUuid 排除用户的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistUser(final String uuid, final String name, final String excludeUuid) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            final JSONArray whereArray = new JSONArray();
            if (null != uuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "uuid");
                obj.put("symbol", "=");
                obj.put("value", uuid);
                whereArray.put(obj);
            }
            if (null != name) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "name");
                obj.put("symbol", "=");
                obj.put("value", name);
                whereArray.put(obj);
            }
            if (null != excludeUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "uuid");
                obj.put("symbol", "<>");
                obj.put("value", excludeUuid);
                whereArray.put(obj);
            }
            {
                // 排除逻辑删除
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "remove_timestamp");
                obj.put("symbol", "is");
                obj.put("value", JSONObject.NULL);
                whereArray.put(obj);
            }
            final String whereSql = DatabaseKit.composeWhereSql(whereArray);
            ps = this.connection.prepareStatement("select `uuid` from `" + DATABASE_TABLE_NAME + "` " + whereSql + " limit 0, 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        } finally {
            if (null != rs) {
                rs.close();
            }
            if (null != ps) {
                ps.close();
            }
        }
    }
}