package supervision.spot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.string.StringKit;
import security.dao.Admin;
import security.dao.Admin.Status;

/**
 * 管理员信息
 */
public class AdminInfo {
    // 数据库表名
    public static String DATABASE_TABLE_NAME = "svp_admin-info";

    // 角色
    public static enum Role {
        DS_ADMIN, // 督审管理员
        DS_USER, // 督审用户
        DEPARTMENT_LEADER, // 单位领导
        DEPARTMENT_ADMIN, // 单位管理员
        MIN_JING, // 民警
        FU_JING // 辅警
    }

    // 管理
    public static enum Manage {
        ADD, // 添加
        REMOVE // 删除
    }

    private Connection connection;

    public AdminInfo(final Connection connection) {
        this.connection = connection;
    }

    /**
     * 添加管理员信息
     * 
     * @param roleUuid 角色的uuid（允许为null，与角色名称二选一）
     * @param roleName 角色名称（允许为null，与角色的uuid二选一）
     * @param name 用户名
     * @param password 密码
     * @param realName 姓名
     * @param departmentUuid 单位的uuid
     * @return 消息对象
     */
    public Message addAdminInfo(final String roleUuid, final String roleName, final String name, final String password, final String realName, final String departmentUuid) {
        final Message msg = new Message();
        try {
            String adminRoleUuid = null;
            // 是否存在角色
            {
                if (null != roleUuid) {
                    final security.dao.Role role = new security.dao.Role(this.connection);
                    if (!role.isExistRole(roleUuid, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ROLE_NOT_EXIST");
                        msg.setAttach("角色不存在");
                        return msg;
                    }
                    adminRoleUuid = roleUuid;
                }
            }
            String rolePermissions = null;
            {
                if (null != roleName) {
                    final JSONObject obj = this.getSvpAdminRoleByName(roleName);
                    if (null == obj) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADMIN_ROLE_NOT_EXIST");
                        msg.setAttach("账户角色不存在");
                        return msg;
                    }
                    adminRoleUuid = obj.getString("uuid");
                    rolePermissions = obj.getString("permissions");
                }
            }
            {
                if (null == adminRoleUuid) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ADMIN_ROLE_NOT_EXIST");
                    msg.setAttach("账户角色不存在");
                    return msg;
                }
            }
            // 是否存在单位
            {
                if (null != departmentUuid) {
                    final Department obj = new Department(this.connection);
                    if (!obj.isExistDepartment(departmentUuid, null, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("DEPARTMENT_NOT_EXIST");
                        msg.setAttach("单位不存在");
                        return msg;
                    }
                }
            }
            // 添加管理员信息
            String uuid = null;
            {
                final Admin obj = new Admin(this.connection);
                final Message resultMsg = obj.addAdmin(adminRoleUuid, name, password);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                uuid = ((JSONObject) resultMsg.getContent()).getString("uuid");
            }
            Integer personInitScore = null;
            // 获取系统配置
            {
                final SystemConfig obj = new SystemConfig(this.connection);
                final Message resultMsg = obj.getSystemConfig();
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("SYSTEM_CONFIG_NOT_EXIST");
                    msg.setAttach("系统配置不存在");
                    return msg;
                }
                personInitScore = Integer.valueOf(array.getJSONObject(0).getInt("person_init_score"));
            }
            // 添加管理员信息
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "admin_uuid", "real_name", "department_uuid", "problem_count", "score"));
                    ps.setString(1, StringKit.getUuidStr(true));
                    ps.setString(2, uuid);
                    ps.setString(3, realName);
                    ps.setString(4, departmentUuid);
                    ps.setInt(5, 0);
                    ps.setInt(6, personInitScore.intValue());
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_ADMIN_INFO_FAIL");
                        msg.setAttach("添加管理员信息失败");
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
            resultObj.put("permissions", rolePermissions);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 根据uuid删除管理员信息
     * 
     * @param uuid uuid
     * @return 消息对象
     */
    public Message removeAdminInfoByUuid(final String uuid) {
        final Message msg = new Message();
        try {
            // 删除管理员
            {
                final Admin obj = new Admin(this.connection);
                final Message resultMsg = obj.removeAdminByUuid(true, uuid);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
            }
            // 删除管理员信息
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `admin_uuid` = '" + uuid + "'";
                    final HashMap<String, Object> hm = new HashMap<>();
                    hm.put("remove_timestamp", Long.valueOf(System.currentTimeMillis()));
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("REMOVE_ADMIN_INFO_FAIL");
                        msg.setAttach("删除管理员信息失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            // 删除关联（无）
            msg.setStatus(Message.Status.SUCCESS);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 根据单位的uuid删除管理员<br />
     * 非自身uuid删除无需检查带删除数据是否存在
     * 
     * @param departmentUuid 单位的uuid
     * @return 消息对象
     */
    public Message removeAdminInfoByDepartmentUuid(final String departmentUuid) {
        final Message msg = new Message();
        if (null == departmentUuid) {
            msg.setStatus(Message.Status.ERROR);
            msg.setContent("REMOVE_PARAMETER_NOT_ALLOW_NULL");
            msg.setAttach("删除参数不允许为空");
            return msg;
        }
        try {
            // 获取符合条件的管理员
            final ArrayList<String> uuidList = new ArrayList<>();
            {
                final Message resultMsg = this.getAdminInfo(null, null, null, departmentUuid, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ADMIN_INFO_NOT_EXIST");
                    msg.setAttach("管理员信息不存在");
                    return msg;
                }
                for (int i = 0; i < array.length(); i++) {
                    final String uuid = array.getJSONObject(i).getString("uuid");
                    uuidList.add(uuid);
                }
            }
            // 删除符合条件的管理员信息
            {
                final security.dao.Admin obj = new security.dao.Admin(this.connection);
                for (int i = 0; i < uuidList.size(); i++) {
                    final String uuid = uuidList.get(i);
                    final Message resultMsg = obj.removeAdminByUuid(false, uuid);
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
     * 修改管理员（至少修改一项字段）
     * 
     * @param uuid 管理员的uuid
     * @param roleUuid 角色的uuid（允许为null）
     * @param name 账户名称（允许为null）
     * @param password 密码（允许为null）
     * @param failedRetryCount 失败重复计数（允许为null）
     * @param frozenTimestamp 冻结时间戳（允许为null，为null不修改，长度为0则清空）
     * @param loginToken 登录Token（允许为null）
     * @param status 状态（允许为null）
     * @param realName 姓名（允许为null）
     * @param departmentUuid 单位的uuid（允许为null）
     * @param problemCount 问题计数（允许为null）
     * @param score 分值（允许为null）
     * @param manage 管理（允许为null）
     * @param manageDepartmentUuid 管理单位的uuid（允许为null）
     * @return 消息对象
     */
    public final Message modifyAdmin(final String uuid, final String roleUuid, final String name, final String password, final Integer failedRetryCount, final Long frozenTimestamp, final String loginToken, final Status status, final String realName,
            final String departmentUuid, final Integer problemCount, final Integer score, final Manage manage, final String manageDepartmentUuid) {
        final Message msg = new Message();
        try {
            String manageDepartments = "";
            // 是否存在管理员信息
            {
                final Message resultMsg = this.getAdminInfo(uuid, null, null, null, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ADMIN_INFO_NOT_EXIST");
                    msg.setAttach("管理员信息不存在");
                    return msg;
                }
                if (array.getJSONObject(0).has("manage_departments")) {
                    manageDepartments = array.getJSONObject(0).getString("manage_departments");
                    if (null == manageDepartments) {
                        manageDepartments = "";
                    }
                }
            }
            // 是否存在单位
            {
                if ((null != manage) && (null != manageDepartmentUuid)) {
                    final Department obj = new Department(this.connection);
                    if (!obj.isExistDepartment(manageDepartmentUuid, null, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("DEPARTMENT_NOT_EXIST");
                        msg.setAttach("单位不存在");
                        return msg;
                    }
                }
            }
            // 修改管理员
            {
                if ((null != roleUuid) || (null != name) || (null != password) || (null != failedRetryCount) || (null != frozenTimestamp) || (null != loginToken) || (null != status)) {
                    final Admin obj = new Admin(this.connection);
                    final Message resultMsg = obj.modifyAdmin(uuid, roleUuid, name, password, failedRetryCount, frozenTimestamp, loginToken, status);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                }
            }
            // 修改管理员信息
            {
                if ((null != realName) || (null != departmentUuid) || (null != problemCount) || (null != score) || (null != manage) || (null != manageDepartmentUuid)) {
                    PreparedStatement ps = null;
                    try {
                        final String whereSql = "where `admin_uuid` = '" + uuid + "'";
                        final HashMap<String, Object> hm = new HashMap<>();
                        if (null != realName) {
                            hm.put("real_name", realName);
                        }
                        if (null != departmentUuid) {
                            hm.put("department_uuid", departmentUuid);
                        }
                        if (null != problemCount) {
                            hm.put("problem_count", problemCount);
                        }
                        if (null != score) {
                            hm.put("score", score);
                        }
                        if ((null != manage) && (null != manageDepartmentUuid)) {
                            final StringBuilder sb = new StringBuilder();
                            if (Manage.ADD == manage) {
                                final String[] manageDepartmentArray = manageDepartments.split(";");
                                for (int i = 0; i < manageDepartmentArray.length; i++) {
                                    final String manageDepartment = manageDepartmentArray[i];
                                    sb.append(manageDepartment);
                                    sb.append(";");
                                }
                                sb.append(manageDepartmentUuid);
                                sb.append(";");
                            } else if (Manage.REMOVE == manage) {
                                final String[] manageDepartmentArray = manageDepartments.split(";");
                                for (int i = 0; i < manageDepartmentArray.length; i++) {
                                    final String manageDepartment = manageDepartmentArray[i];
                                    if (!manageDepartment.equalsIgnoreCase(manageDepartmentUuid)) {
                                        sb.append(manageDepartment);
                                        sb.append(";");
                                    }
                                }
                            }
                            if (0 < sb.length()) {
                                hm.put("manage_departments", sb.toString());
                            }
                        }
                        final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                        ps = this.connection.prepareStatement(sql);
                        final int res = ps.executeUpdate();
                        if (0 >= res) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("MODIFY_ADMIN_INFO_FAIL");
                            msg.setAttach("修改管理员信息失败");
                            return msg;
                        }
                    } finally {
                        if (null != ps) {
                            ps.close();
                        }
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
     * 获取管理员信息
     * 
     * @param uuid 管理员信息的uuid（允许为null）
     * @param name 用户名称（允许为null）
     * @param status 状态（允许为null）
     * @param departmentUuid 单位的uuid（允许为null）
     * @param problemCountSymbol 问题计数符号（允许为null）
     * @param problemCount 问题计数（包括：>、<、>=、<=或=）（允许为null）
     * @param manageDepartmentUuidLike 管理单位的uuid的Like（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getAdminInfo(final String uuid, final String name, final Status status, final String departmentUuid, final String problemCountSymbol, final Integer problemCount, final String manageDepartmentUuidLike, final String allFromDepartmentUuid,
            final Integer offset, final Integer rows) {
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
            if (null != departmentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "i");
                obj.put("name", "department_uuid");
                obj.put("symbol", "=");
                obj.put("value", departmentUuid);
                whereArray.put(obj);
            }
            if ((null != problemCountSymbol) && (null != problemCount)) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "i");
                obj.put("name", "problem_count");
                obj.put("symbol", problemCountSymbol);
                obj.put("value", problemCount);
                whereArray.put(obj);
            }
            if (null != manageDepartmentUuidLike) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "i");
                obj.put("name", "manage_departments");
                obj.put("symbol", "like");
                obj.put("value", "%" + manageDepartmentUuidLike + "%");
                whereArray.put(obj);
            }
            if (null != allFromDepartmentUuid) {
                final Department dep = new Department(this.connection);
                final Message resultMsg = dep.getChildDepartment(new String[] { allFromDepartmentUuid });
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("DEP_LV2_UUID_NOT_EXIST");
                    msg.setAttach("部门lv2uuid不存在");
                    return msg;
                }
                final ArrayList<String> uuidList = new ArrayList<>();
                for (int j = 0; j < array.length(); j++) {
                    uuidList.add(array.getJSONObject(j).getString("uuid"));
                }
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "department_uuid");
                obj.put("symbol", "in");
                obj.put("value", uuidList.toArray(new String[uuidList.size()]));
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
                    ps = this.connection.prepareStatement("select count(*) as `count` from `" + Admin.DATABASE_TABLE_NAME + "` u inner join `" + security.dao.Role.DATABASE_TABLE_NAME + "` r on u.role_uuid = r.uuid inner join `" + AdminInfo.DATABASE_TABLE_NAME
                            + "` i on u.uuid = i.admin_uuid " + whereSql);
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
                            "select u.uuid as `uuid`, u.role_uuid as `role_uuid`, r.name as `role_name`, r.permissions as `permissions`, r.menus as `menus`, u.name as `name`, u.password as `password`, u.failed_retry_count as `failed_retry_count`, u.frozen_timestamp as `frozen_timestamp`, u.frozen_datetime as `frozen_datetime`, u.status as `status`, i.real_name as `real_name`, i.department_uuid as `department_uuid`, i.problem_count as `problem_count`, i.score as `score`, i.manage_departments as `manage_departments` from `"
                                    + Admin.DATABASE_TABLE_NAME + "` u inner join `" + security.dao.Role.DATABASE_TABLE_NAME + "` r on u.role_uuid = r.uuid inner join `" + AdminInfo.DATABASE_TABLE_NAME + "` i on u.uuid = i.admin_uuid " + whereSql
                                    + " order by `name` asc " + limitCode);
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
                        obj.put("status", rs.getString("status"));
                        obj.put("real_name", rs.getString("real_name"));
                        obj.put("department_uuid", rs.getString("department_uuid"));
                        obj.put("problem_count", rs.getInt("problem_count"));
                        obj.put("score", rs.getInt("score"));
                        obj.put("manage_departments", rs.getString("manage_departments"));
                        {
                            final Department department = new Department(this.connection);
                            Message resultMsg = department.getDepartmentFullNameByUuid(rs.getString("department_uuid"));
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            final String fullName = ((JSONObject) resultMsg.getContent()).getString("full_name");
                            obj.put("full_name", fullName);
                            // 很多地方都用到了parent_array的数据，千万不要随意修改。
                            resultMsg = department.getDepartmentParentBriefInfoByUuid(rs.getString("department_uuid"));
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            final JSONArray parentArray = ((JSONObject) resultMsg.getContent()).getJSONArray("parent_array");
                            obj.put("parent_array", parentArray);
                            resultMsg = department.getDepartment(rs.getString("department_uuid"), null, null, null, null, null, null, null, null);
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            final JSONArray detailArray = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                            obj.put("department_detail", detailArray);
                        }
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
     * 获取管理员信息
     * 
     * @param uuid 管理员信息的uuid（允许为null）
     * @param name 用户名称（允许为null）
     * @param status 状态（允许为null）
     * @param departmentUuid 单位的uuid（允许为null）
     * @param problemCountSymbol 问题计数符号（允许为null）
     * @param problemCount 问题计数（包括：>、<、>=、<=或=）（允许为null）
     * @param manageDepartmentUuidLike 管理单位的uuid的Like（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getAdminInfoShort(final String uuid, final String name, final Status status, final String departmentUuid, final String problemCountSymbol, final Integer problemCount, final String manageDepartmentUuidLike, final String allFromDepartmentUuid,
            final Integer offset, final Integer rows) {
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
            if (null != departmentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "i");
                obj.put("name", "department_uuid");
                obj.put("symbol", "=");
                obj.put("value", departmentUuid);
                whereArray.put(obj);
            }
            if ((null != problemCountSymbol) && (null != problemCount)) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "i");
                obj.put("name", "problem_count");
                obj.put("symbol", problemCountSymbol);
                obj.put("value", problemCount);
                whereArray.put(obj);
            }
            if (null != manageDepartmentUuidLike) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("alias", "i");
                obj.put("name", "manage_departments");
                obj.put("symbol", "like");
                obj.put("value", "%" + manageDepartmentUuidLike + "%");
                whereArray.put(obj);
            }
            if (null != allFromDepartmentUuid) {
                final Department dep = new Department(this.connection);
                final Message resultMsg = dep.getChildDepartment(new String[] { allFromDepartmentUuid });
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("DEP_LV2_UUID_NOT_EXIST");
                    msg.setAttach("部门lv2uuid不存在");
                    return msg;
                }
                final ArrayList<String> uuidList = new ArrayList<>();
                for (int j = 0; j < array.length(); j++) {
                    uuidList.add(array.getJSONObject(j).getString("uuid"));
                }
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "department_uuid");
                obj.put("symbol", "in");
                obj.put("value", uuidList.toArray(new String[uuidList.size()]));
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
                    ps = this.connection.prepareStatement("select count(*) as `count` from `" + Admin.DATABASE_TABLE_NAME + "` u inner join `" + security.dao.Role.DATABASE_TABLE_NAME + "` r on u.role_uuid = r.uuid inner join `" + AdminInfo.DATABASE_TABLE_NAME
                            + "` i on u.uuid = i.admin_uuid " + whereSql);
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
                            "select u.uuid as `uuid`, u.role_uuid as `role_uuid`, r.name as `role_name`, r.permissions as `permissions`, r.menus as `menus`, u.name as `name`, u.password as `password`, u.failed_retry_count as `failed_retry_count`, u.frozen_timestamp as `frozen_timestamp`, u.frozen_datetime as `frozen_datetime`, u.status as `status`, i.real_name as `real_name`, i.department_uuid as `department_uuid`, i.problem_count as `problem_count`, i.score as `score`, i.manage_departments as `manage_departments` from `"
                                    + Admin.DATABASE_TABLE_NAME + "` u inner join `" + security.dao.Role.DATABASE_TABLE_NAME + "` r on u.role_uuid = r.uuid inner join `" + AdminInfo.DATABASE_TABLE_NAME + "` i on u.uuid = i.admin_uuid " + whereSql
                                    + " order by `name` asc " + limitCode);
                    if ((null != offset) && (null != rows)) {
                        ps.setInt(1, offset.intValue());
                        ps.setInt(2, rows.intValue());
                    }
                    rs = ps.executeQuery();
                    final JSONArray array = new JSONArray();
                    while (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        obj.put("uuid", rs.getString("uuid"));
                        obj.put("name", rs.getString("name"));
                        obj.put("real_name", rs.getString("real_name"));
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
     * 根据角色名称获取督察管理员角色
     * 
     * @param name 角色名称
     * @return 存在返回角色的JSONObject对象，不存在返回null。
     */
    private JSONObject getSvpAdminRoleByName(final String name) throws Exception {
        final security.dao.Role role = new security.dao.Role(this.connection);
        final Message resultMsg = role.getRole(null, name, null, null);
        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            return null;
        }
        return ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
    }

    /**
     * 是否存在管理员信息
     * 
     * @param adminUuid 管理员的uuid（允许为null）
     * @param departmentUuid 单位的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public boolean isExistAdminInfo(final String adminUuid, final String departmentUuid) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            final JSONArray whereArray = new JSONArray();
            if (null != adminUuid) {
                // 是否存在管理员
                {
                    final security.dao.Admin obj = new security.dao.Admin(this.connection);
                    if (!obj.isExistAdmin(adminUuid, null, null)) {
                        return false;
                    }
                }
                {
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("name", "admin_uuid");
                    obj.put("symbol", "=");
                    obj.put("value", adminUuid);
                    whereArray.put(obj);
                }
            }
            if (null != departmentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "department_uuid");
                obj.put("symbol", "=");
                obj.put("value", departmentUuid);
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

    /**
     * 管理员单位归属验证<br />
     * 判断管理员的单位是否等于或在归属单位之下。
     * 
     * @param adminDepartmentUuid 管理员单位的uuid
     * @param belongDepartmentUuid 归属单位的uuid
     * @return 归属返回true，否则返回false。
     */
    public boolean adminDepartmentBelongValidation(final String adminDepartmentUuid, final String belongDepartmentUuid) throws Exception {
        if (adminDepartmentUuid.equalsIgnoreCase(belongDepartmentUuid)) {
            return true;
        }
        // 获取单位
        {
            final Department obj = new Department(this.connection);
            final Message resultMsg = obj.getDepartment(belongDepartmentUuid, null, null, null, null, null, null, null, null);
            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                return false;
            }
            final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            for (int i = 0; i < array.length(); i++) {
                final JSONArray parentArray = array.getJSONObject(i).getJSONArray("parent_array");
                for (int j = 0; j < parentArray.length(); j++) {
                    final String uuid = parentArray.getJSONObject(j).getString("uuid");
                    if (uuid.equalsIgnoreCase(adminDepartmentUuid)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}