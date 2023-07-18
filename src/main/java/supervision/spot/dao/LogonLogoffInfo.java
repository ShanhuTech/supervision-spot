package supervision.spot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.string.StringKit;
import security.dao.Admin;

/**
 * 登入登出信息
 */
public class LogonLogoffInfo {
    // 数据库表名
    public static String DATABASE_TABLE_NAME = "svp_logon-logoff-info";

    // 类型
    public static enum Type {
        LOGIN, // 登入
        LOGOFF // 登出
    }

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public LogonLogoffInfo(final Connection connection) {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加登入登出信息
     * 
     * @param accountUuid 账户的uuid
     * @param type 类型
     * @param ipAddr ip地址
     * @return 消息对象
     */
    public Message addLogonLogoffInfo(final String accountUuid, final Type type, final String ipAddr) {
        final Message msg = new Message();
        try {
            String name = null;
            // 是否存在管理员
            {
                final Admin obj = new Admin(this.connection);
                final Message resultMsg = obj.getAdmin(accountUuid, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ADMIN_NOT_EXIST");
                    msg.setAttach("管理员不存在");
                    return msg;
                }
                name = array.getJSONObject(0).getString("name");
            }
            // 添加登入登出信息
            final String uuid = StringKit.getUuidStr(true);
            final long createTimestamp = System.currentTimeMillis();
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp));
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "name", "type", "ip_addr", "create_timestamp", "create_datetime"));
                    ps.setString(1, uuid);
                    ps.setString(2, name);
                    ps.setString(3, type.toString());
                    ps.setString(4, ipAddr);
                    ps.setLong(5, createTimestamp);
                    ps.setString(6, createDatetime);
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
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 获取登入登出信息
     * 
     * @param uuid uuid（允许为null）
     * @param name 用户名称（允许为null）
     * @param type 类型（允许为null）
     * @param startCreateDatetime 开始创建时间（允许为null）
     * @param endCreateDatetime 结束创建时间（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getLogonLogoffInfo(final String uuid, final String name, final Type type, final String startCreateDatetime, final String endCreateDatetime, final Integer offset, final Integer rows) {
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
            if (null != type) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "type");
                obj.put("symbol", "=");
                obj.put("value", type.toString());
                whereArray.put(obj);
            }
            if (null != startCreateDatetime) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "create_timestamp");
                obj.put("symbol", ">");
                obj.put("value", this.simpleDateFormat.parse(startCreateDatetime).getTime());
                whereArray.put(obj);
            }
            if (null != endCreateDatetime) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "create_timestamp");
                obj.put("symbol", "<");
                obj.put("value", this.simpleDateFormat.parse(endCreateDatetime).getTime());
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
                    ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "` " + whereSql + " order by `create_timestamp` desc " + limitCode);
                    if ((null != offset) && (null != rows)) {
                        ps.setInt(1, offset.intValue());
                        ps.setInt(2, rows.intValue());
                    }
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
}