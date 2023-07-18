package supervision.spot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.string.StringKit;

/**
 * 问责类型
 */
public final class AccountableType {
    // 数据库表名
    public static final String DATABASE_TABLE_NAME = "svp_accountable-type";
    private Connection connection;

    public AccountableType(final Connection connection) throws Exception {
        this.connection = connection;
    }

    /**
     * 添加问责类型
     * 
     * @param name 名称
     * @param order 排序编号
     * @return 消息对象
     */
    public final Message addAccountableType(final String name, final Integer order) {
        final Message msg = new Message();
        try {
            final String uuid = StringKit.getUuidStr(true);
            // 添加问责类型
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "name", "order"));
                    ps.setString(1, uuid);
                    ps.setString(2, name);
                    ps.setInt(3, order.intValue());
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_ACCOUNTABLE_TYPE_FAIL");
                        msg.setAttach("添加问责类型失败");
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
     * 根据uuid删除问责类型
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid 问责类型的uuid
     * @return 消息对象
     */
    public final Message removeAccountableTypeByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在问责类型
            {
                if (checkExist) {
                    if (!this.isExistAccountableType(uuid, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ACCOUNTABLE_TYPE_NOT_EXIST");
                        msg.setAttach("问责类型不存在");
                        return msg;
                    }
                }
            }
            // 查找关联
            // svp_department
            {
                final Accountable obj = new Accountable(this.connection);
                if (obj.isExistAccountable(null, null, uuid, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ASSOCIATED_DATA_ACCOUNTABLE_IS_EXIST");
                    msg.setAttach("存在关联数据问责");
                    return msg;
                }
            }
            // 删除问责类型
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
                        msg.setContent("REMOVE_ACCOUNTABLE_TYPE_FAIL");
                        msg.setAttach("删除问责类型失败");
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
     * 修改问责类型（至少修改一项字段）
     * 
     * @param uuid 角色的uuid
     * @param name 问责类型名称（允许为null）
     * @param order 排序编号（允许为null）
     * @return 消息对象
     */
    public final Message modifyAccountableType(final String uuid, final String name, final Integer order) {
        final Message msg = new Message();
        try {
            // 是否存在问责类型
            {
                if (!this.isExistAccountableType(uuid, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ACCOUNTABLE_TYPE_NOT_EXIST");
                    msg.setAttach("问责类型不存在");
                    return msg;
                }
            }
            // 修改问责类型
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "'";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != name) {
                        hm.put("name", name);
                    }
                    if (null != order) {
                        hm.put("order", order);
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_ACCOUNTABLE_TYPE_FAIL");
                        msg.setAttach("修改问责类型失败");
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
     * 获取问责类型
     * 
     * @param uuid 问责类型的uuid（允许为null）
     * @param name 问责类型名称（允许为null）
     * @return 消息对象
     */
    public final Message getAccountableType(final String uuid, final String name) {
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
            final String whereSql = DatabaseKit.composeWhereSql(whereArray);
            final JSONArray array = new JSONArray();
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "`" + whereSql + " order by `order` asc");
                    rs = ps.executeQuery();
                    // 根据列名获取所有返回数据
                    final ResultSetMetaData rsmd = rs.getMetaData();
                    final ArrayList<String> columnLabelList = new ArrayList<>();
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        columnLabelList.add(rsmd.getColumnLabel(i));
                    }
                    while (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        for (int i = 0; i < columnLabelList.size(); i++) {
                            final String columnLabel = columnLabelList.get(i);
                            obj.put(columnLabel, rs.getObject(columnLabel));
                        }
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
     * 是否存在问责类型
     * 
     * @param uuid 类别的uuid（允许为null）
     * @param name 名称（允许为null）
     * @param excludeUuid 排除类别的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistAccountableType(final String uuid, final String name, final String excludeUuid) throws Exception {
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