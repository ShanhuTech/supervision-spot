package security.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.Framework;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.string.StringKit;

/**
 * 角色
 */
public final class Role {
    // 数据库表名
    public static final String DATABASE_TABLE_NAME = "account-security_role";

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public Role(final Connection connection) throws Exception {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加角色
     * 
     * @param name 角色名称
     * @param description 描述
     * @param order 排序编号
     * @return 消息对象
     */
    public final Message addRole(final String name, final String description, final Integer order) {
        final Message msg = new Message();
        try {
            // 是否存在角色
            {
                if (this.isExistRole(null, name, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ROLE_NAME_EXIST");
                    msg.setAttach("角色名称已存在");
                    return msg;
                }
            }
            final String uuid = StringKit.getUuidStr(true);
            final long createTimestamp = System.currentTimeMillis();
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp));
            // 添加企业
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "name", "description", "permissions", "menus", "order", "create_timestamp", "create_datetime"));
                    ps.setString(1, uuid);
                    ps.setString(2, name);
                    ps.setString(3, description);
                    ps.setNull(4, Types.VARCHAR);
                    ps.setNull(5, Types.VARCHAR);
                    ps.setInt(6, order.intValue());
                    ps.setLong(7, createTimestamp);
                    ps.setString(8, createDatetime);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_ROLE_FAIL");
                        msg.setAttach("添加角色失败");
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
     * 根据uuid删除角色
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid uuid
     * @return 消息对象
     */
    public final Message removeRoleByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在角色
            {
                if (checkExist) {
                    if (!this.isExistRole(uuid, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ROLE_NOT_EXIST");
                        msg.setAttach("角色不存在");
                        return msg;
                    }
                }
            }
            // 删除角色
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
                        msg.setContent("REMOVE_ROLE_FAIL");
                        msg.setAttach("删除角色失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            // 删除关联
            // account-security_admin
            {
                final security.dao.Admin obj = new security.dao.Admin(this.connection);
                final Message resultMsg = obj.removeAdminByRoleUuid(uuid);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
            }
            // account-security_user
            {
                final security.dao.User obj = new security.dao.User(this.connection);
                final Message resultMsg = obj.removeUserByRoleUuid(uuid);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
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
     * 获取角色菜单的字符串
     * 
     * @param menuArray 数据库中菜单的JSONArray格式集合
     * @param uuid 从参数获取菜单uuid
     * @return 如果比较成功，返回字符串；否则返回null。
     */
    private final String getRoleMenu(final JSONArray menuArray, final String[] menuStrArray) {
        final JSONArray roleMenuArray = new JSONArray();
        for (int i = 0; i < menuArray.length(); i++) {
            final JSONObject menu = menuArray.getJSONObject(i);
            for (int j = 0; j < menuStrArray.length; j++) {
                if (menu.getString("uuid").equalsIgnoreCase(menuStrArray[j])) {
                    roleMenuArray.put(menu);
                }
            }
        }
        if (0 >= roleMenuArray.length()) {
            return null;
        }
        return roleMenuArray.toString();
    }

    /**
     * 修改角色（至少修改一项字段）
     * 
     * @param uuid 角色的uuid
     * @param name 角色名称（允许为null）
     * @param description 描述（允许为空，为null不修改，长度为0则清空）
     * @param permissions 权限集合（分号分割）（允许为null，为null不修改，长度为0则清空）
     * @param menus 菜单（允许为null，为null不修改，长度为0则清空）
     * @param order 排序编号（允许为null）
     * @return 消息对象
     */
    public final Message modifyRole(final String uuid, final String name, final String description, final String permissions, final String menus, final Integer order) {
        final Message msg = new Message();
        try {
            // 是否存在角色
            {
                if (!this.isExistRole(uuid, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ROLE_NOT_EXIST");
                    msg.setAttach("角色不存在");
                    return msg;
                }
                if (null != name) {
                    if (this.isExistRole(null, name, uuid)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ROLE_NAME_EXIST");
                        msg.setAttach("角色名称已存在");
                        return msg;
                    }
                }
            }
            String roleMenu = null;
            // 获取角色菜单
            {
                if ((null != menus) && (0 < menus.length())) {
                    final security.dao.Menu obj = new security.dao.Menu(this.connection);
                    final Message resultMsg = obj.getMenu(null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MENU_NOT_EXIST");
                        msg.setAttach("菜单不存在");
                        return msg;
                    }
                    final String[] menuList = menus.split(";");
                    roleMenu = this.getRoleMenu(array, menuList);
                    if (null == roleMenu) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ROLE_MENU_NOT_EXIST");
                        msg.setAttach("角色菜单不存在");
                        return msg;
                    }
                }
            }
            // 修改角色
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "'";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != name) {
                        hm.put("name", name);
                    }
                    if (null != description) {
                        if (0 >= description.length()) {
                            hm.put("description", null);
                        } else {
                            hm.put("description", description);
                        }
                    }
                    if (null != permissions) {
                        if (0 >= permissions.length()) {
                            hm.put("permissions", null);
                        } else {
                            hm.put("permissions", permissions);
                        }
                    }
                    if (null != menus) {
                        if (0 >= menus.length()) {
                            hm.put("menus", null);
                        } else {
                            hm.put("menus", roleMenu);
                        }
                    }
                    if (null != order) {
                        hm.put("order", order);
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_ROLE_FAIL");
                        msg.setAttach("修改角色失败");
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
     * 获取角色
     * 
     * @param uuid 角色的uuid（允许为null）
     * @param name 角色名称（允许为null）
     * @return 消息对象
     */
    public Message getRole(final String uuid, final String name, final Integer offset, final Integer rows) {
        final Message msg = new Message();
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
            {
                // 排除逻辑删除
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
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
                    ps = this.connection.prepareStatement("select count(*) as `count` from `" + DATABASE_TABLE_NAME + "` " + whereSql);
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
            final JSONArray array = new JSONArray();
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    String limitCode = "";
                    if ((null != offset) && (null != rows)) {
                        limitCode = "limit ?, ?";
                    }
                    ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "` " + whereSql + " order by `order` asc " + limitCode);
                    if ((null != offset) && (null != rows)) {
                        ps.setInt(1, offset.intValue());
                        ps.setInt(2, rows.intValue());
                    }
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        obj.put("uuid", rs.getString("uuid"));
                        obj.put("name", rs.getString("name"));
                        obj.put("description", rs.getString("description"));
                        if ((null != rs.getString("permissions")) && (rs.getString("permissions").equalsIgnoreCase("*"))) {
                            obj.put("permissions", this.getModulePermission());
                        } else {
                            obj.put("permissions", rs.getString("permissions"));
                        }
                        final String menu = rs.getString("menus");
                        if (null != menu) {
                            obj.put("menus", new JSONArray(menu));
                        }
                        obj.put("order", rs.getInt("order"));
                        array.put(obj);
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
            resultObj.put("array", array);
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
     * 是否存在角色
     * 
     * @param uuid 角色的uuid（允许为null）
     * @param name 角色名称（允许为null）
     * @param excludeUuid 排除角色的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistRole(final String uuid, final String name, final String excludeUuid) throws Exception {
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

    /**
     * 获取模块和权限的字符串
     * 
     * @return 模块和权限的字符串
     */
    private final String getModulePermission() {
        final StringBuilder sb = new StringBuilder();
        final Iterator<Entry<String, ArrayList<String>>> iter = Framework.MODULE_MAP.entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<String, ArrayList<String>> module = iter.next();
            final Iterator<String> servletIter = module.getValue().iterator();
            while (servletIter.hasNext()) {
                final String s = servletIter.next();
                sb.append(s + ";");
            }
        }
        return sb.toString();
    }
}