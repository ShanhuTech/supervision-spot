package security.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.string.StringKit;

/**
 * 菜单
 */
public final class Menu {
    // 数据库表名
    public static final String DATABASE_TABLE_NAME = "account-security_menu";

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public Menu(final Connection connection) throws Exception {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加菜单
     * 
     * @param parentUuid 父级菜单的uuid
     * @param name 菜单名称
     * @param text 显示文本
     * @param description 描述
     * @param link 链接
     * @param order 排序编号
     * @return 消息对象
     */
    public final Message addMenu(final String parentUuid, final String name, final String text, final String description, final String link, final Integer order) {
        final Message msg = new Message();
        try {
            int level = 1;
            String orderGroup = String.format("%03d", order);
            // 如果parentUuid不是顶级菜单检查parentUuid是否存在
            {
                if (!"0".equalsIgnoreCase(parentUuid)) {
                    final Message resultMsg = this.getMenu(parentUuid, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PARENT_MENU_NOT_EXIST");
                        msg.setAttach("父级菜单不存在");
                        return msg;
                    }
                    level = array.getJSONObject(0).getInt("level") + 1;
                    orderGroup = array.getJSONObject(0).getString("order_group") + orderGroup;
                }
            }
            final String uuid = StringKit.getUuidStr(true);
            final long createTimestamp = System.currentTimeMillis();
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp));
            // 添加菜单
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "parent_uuid", "name", "text", "description", "link", "level", "order", "order_group", "create_timestamp", "create_datetime"));
                    ps.setString(1, uuid);
                    ps.setString(2, parentUuid);
                    ps.setString(3, name);
                    ps.setString(4, text);
                    ps.setString(5, description);
                    ps.setString(6, link);
                    ps.setInt(7, level);
                    ps.setInt(8, order.intValue());
                    ps.setString(9, orderGroup);
                    ps.setLong(10, createTimestamp);
                    ps.setString(11, createDatetime);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_MENU_FAIL");
                        msg.setAttach("添加菜单失败");
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
     * 根据uuid删除菜单
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid 问题等级的uuid
     * @return 消息对象
     */
    public final Message removeMenuByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在问题等级
            {
                if (checkExist) {
                    if (!this.isExistMenu(uuid, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MENU_NOT_EXIST");
                        msg.setAttach("菜单不存在");
                        return msg;
                    }
                }
            }
            final StringBuilder uuidSql = new StringBuilder();
            final String split = "' or";
            // 组成删除菜单的SQL语句
            {
                final ArrayList<String> resultList = new ArrayList<>();
                // 添加当前要删除菜单的uuid
                resultList.add(uuid);
                // 添加当前菜单下所有子菜单的uuid
                this.getChildrenMenu(resultList, uuid);
                // 转换所有子菜单数据成SQL语句
                final Iterator<String> resultIter = resultList.iterator();
                while (resultIter.hasNext()) {
                    final String _uuid = resultIter.next();
                    uuidSql.append(" `uuid` = '" + _uuid);
                    uuidSql.append(split);
                }
            }
            // 删除菜单
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where" + uuidSql.toString().substring(0, uuidSql.length() - (split.length() - 1/*保留`字符*/));
                    final HashMap<String, Object> hm = new HashMap<>();
                    hm.put("remove_timestamp", Long.valueOf(System.currentTimeMillis()));
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("REMOVE_MENU_FAIL");
                        msg.setAttach("删除菜单失败");
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
     * 修改菜单（至少修改一项字段）
     * 
     * @param uuid 角色的uuid
     * @param parentUuid 父级菜单的uuid（允许为null）
     * @param name 菜单名称（允许为null）
     * @param text 显示文本（允许为null）
     * @param description 描述（允许为空，为null不修改，长度为0则清空）
     * @param link 链接（允许为null）
     * @param order 排序编号（允许为null）
     * @return 消息对象
     */
    public final Message modifyMenu(final String uuid, final String parentUuid, final String name, final String text, final String description, final String link, final Integer order) {
        final Message msg = new Message();
        try {
            // 是否存在菜单
            {
                if (!this.isExistMenu(uuid, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("MENU_NOT_EXIST");
                    msg.setAttach("菜单不存在");
                    return msg;
                }
            }
            int level = 1;
            String orderGroup = String.format("%03d", order);
            // 如果parentUuid不是顶级菜单检查parentUuid是否存在
            {
                if (null != parentUuid) {
                    if (!"0".equalsIgnoreCase(parentUuid)) {
                        final Message resultMsg = this.getMenu(parentUuid, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        if (0 >= array.length()) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("PARENT_MENU_NOT_EXIST");
                            msg.setAttach("父级菜单不存在");
                            return msg;
                        }
                        level = array.getJSONObject(0).getInt("level") + 1;
                        orderGroup = array.getJSONObject(0).getString("order_group") + orderGroup;
                    }
                }
            }
            // 如果parentUuid不是顶级目录检查parentUuid是否存在
            {
                if ((null != parentUuid) && (!"0".equalsIgnoreCase(parentUuid))) {
                    if (!this.isExistMenu(parentUuid, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PARENT_MENU_NOT_EXIST");
                        msg.setAttach("父级菜单不存在");
                        return msg;
                    }
                }
            }
            // 修改菜单
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "'";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != parentUuid) {
                        hm.put("parent_uuid", parentUuid);
                        hm.put("level", Integer.valueOf(level)); // 改变了父级菜单的uuid才会改变级别
                    }
                    if (null != name) {
                        hm.put("name", name);
                    }
                    if (null != text) {
                        hm.put("text", text);
                    }
                    if (null != description) {
                        if (0 >= description.length()) {
                            hm.put("description", null);
                        } else {
                            hm.put("description", description);
                        }
                    }
                    if (null != link) {
                        if (0 >= link.length()) {
                            hm.put("link", null);
                        } else {
                            hm.put("link", link);
                        }
                    }
                    if (null != order) {
                        hm.put("order", order);
                        hm.put("order_group", orderGroup);
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_MENU_FAIL");
                        msg.setAttach("修改菜单失败");
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
     * 获取菜单
     * 
     * @param uuid 菜单的uuid（允许为null）
     * @param parentUuid 父级菜单的uuid（允许为null）
     * @param name 菜单名称（允许为null）
     * @return 消息对象
     */
    public final Message getMenu(final String uuid, final String parentUuid, final String name) {
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
            if (null != parentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "parent_uuid");
                obj.put("symbol", "=");
                obj.put("value", parentUuid);
                whereArray.put(obj);
            }
            if (null != name) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "company_name");
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
            final String whereSql = DatabaseKit.composeWhereSql(whereArray);
            final JSONArray array = new JSONArray();
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "`" + whereSql + " order by `order_group` asc");
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        obj.put("uuid", rs.getString("uuid"));
                        obj.put("parent_uuid", rs.getString("parent_uuid"));
                        obj.put("name", rs.getString("name"));
                        obj.put("text", rs.getString("text"));
                        obj.put("description", rs.getString("description"));
                        obj.put("link", rs.getString("link"));
                        obj.put("level", rs.getInt("level"));
                        obj.put("order", rs.getInt("order"));
                        obj.put("order_group", rs.getString("order_group"));
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
            msg.setStatus(Message.Status.SUCCESS);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("array", array);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 是否存在菜单
     * 
     * @param uuid 菜单的uuid（允许为null）
     * @param parentUuid 父级菜单的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistMenu(final String uuid, final String parentUuid) throws Exception {
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
            if (null != parentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "parent_uuid");
                obj.put("symbol", "=");
                obj.put("value", parentUuid);
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
     * 获取子菜单（父找子）
     * 
     * @param resultList 结果列表
     * @param uuid uuid
     */
    private void getChildrenMenu(final ArrayList<String> resultList, final String uuid) {
        final Message resultMsg = this.getMenu(null, uuid, null);
        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            return;
        }
        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
        for (int i = 0; i < array.length(); i++) {
            final JSONObject jo = array.getJSONObject(i);
            resultList.add(jo.getString("uuid"));
            this.getChildrenMenu(resultList, jo.getString("uuid"));
        }
    }
}