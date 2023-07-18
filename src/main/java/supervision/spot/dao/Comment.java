package supervision.spot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.string.StringKit;

/**
 * 评论
 */
public class Comment {
    // 数据库表名
    public static String DATABASE_TABLE_NAME = "svp_comment";
    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public Comment(final Connection connection) {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加评论
     * 
     * @param problemUuid 问题的uuid
     * @param fromUuid 来源的uuid
     * @param content 内容
     * @param files 文件二维数组，格式：{{"base64文件内容", "PNG"}, ...}
     * @return 消息对象
     */
    public Message addComment(final String problemUuid, final String authorUuid, final String authorName, final String authorRealName, final String title, final String content, final String remark,
        final String status, final String detail) {
        final Message msg = new Message();
        try {
            // 是否存在问题
            {
                final Problem obj = new Problem(this.connection);
                if (!obj.isExistProblem(problemUuid, null, null, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("PROBLEM_NOT_EXIST");
                    msg.setAttach("问题不存在");
                    return msg;
                }
            }
            // 是否存在管理员
            {
                final AdminInfo obj = new AdminInfo(this.connection);
                if (!obj.isExistAdminInfo(authorUuid, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("AUTHOR_NOT_EXIST");
                    msg.setAttach("来源管理员不存在");
                    return msg;
                }
            }
            // 添加评论
            final String uuid = StringKit.getUuidStr(true);
            final long createTimestamp = System.currentTimeMillis();
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp));
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "problem_uuid", "author_uuid", "author_name", "author_real_name", "title", "content",
                        "remark", "status", "detail", "create_timestamp", "create_datetime", "last_update_datetime"));
                    ps.setString(1, uuid);
                    ps.setString(2, problemUuid);
                    ps.setString(3, authorUuid);
                    if (null != authorName) {
                        ps.setString(4, authorName);
                    } else {
                        ps.setNull(4, Types.VARCHAR);
                    }
                    if (null != authorRealName) {
                        ps.setString(5, authorRealName);
                    } else {
                        ps.setNull(5, Types.VARCHAR);
                    }
                    if (null != title) {
                        ps.setString(6, title);
                    } else {
                        ps.setNull(6, Types.VARCHAR);
                    }
                    if (null != content) {
                        ps.setString(7, content);
                    } else {
                        ps.setNull(7, Types.VARCHAR);
                    }
                    if (null != remark) {
                        ps.setString(8, remark);
                    } else {
                        ps.setNull(8, Types.VARCHAR);
                    }
                    if (null != status) {
                        ps.setString(9, status);
                    } else {
                        ps.setNull(9, Types.VARCHAR);
                    }
                    if (null != detail) {
                        ps.setString(10, detail);
                    } else {
                        ps.setNull(10, Types.VARCHAR);
                    }
                    ps.setLong(11, createTimestamp);
                    ps.setString(12, createDatetime);
                    ps.setString(13, createDatetime);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_COMMENT_FAIL");
                        msg.setAttach("添加评论失败");
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
     * 根据uuid删除评论
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid uuid
     * @return 消息对象
     */
    public Message removeCommentByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在评论
            {
                if (checkExist) {
                    if (!this.isExistComment(uuid, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("COMMENT_NOT_EXIST");
                        msg.setAttach("评论不存在");
                        return msg;
                    }
                }
            }
            // 删除评论
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
                        msg.setContent("REMOVE_COMMENT_FAIL");
                        msg.setAttach("删除评论失败");
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
     * 根据问题的uuid删除评论<br />
     * 非自身uuid删除无需检查带删除数据是否存在
     * 
     * @param problemUuid 问题的uuid
     * @return 消息对象
     */
    public Message removeCommentByProblemUuid(final String problemUuid) {
        final Message msg = new Message();
        if (null == problemUuid) {
            msg.setStatus(Message.Status.ERROR);
            msg.setContent("REMOVE_PARAMETER_NOT_ALLOW_NULL");
            msg.setAttach("删除参数不允许为空");
            return msg;
        }
        try {
            // 获取符合条件的管理员
            final ArrayList<String> uuidList = new ArrayList<>();
            {
                final Message resultMsg = this.getComment(null, problemUuid, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("COMMENT_NOT_EXIST");
                    msg.setAttach("评论不存在");
                    return msg;
                }
                for (int i = 0; i < array.length(); i++) {
                    final String uuid = array.getJSONObject(i).getString("uuid");
                    uuidList.add(uuid);
                }
            }
            // 删除符合条件的消息
            {
                for (int i = 0; i < uuidList.size(); i++) {
                    final String uuid = uuidList.get(i);
                    final Message resultMsg = this.removeCommentByUuid(false, uuid);
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
     * 根据作者的uuid删除评论<br />
     * 非自身uuid删除无需检查带删除数据是否存在
     * 
     * @param fromUuid 来源的uuid
     * @return 消息对象
     */
    public Message removeCommentByAuthorUuid(final String authorUuid) {
        final Message msg = new Message();
        if (null == authorUuid) {
            msg.setStatus(Message.Status.ERROR);
            msg.setContent("REMOVE_PARAMETER_NOT_ALLOW_NULL");
            msg.setAttach("删除参数不允许为空");
            return msg;
        }
        try {
            // 获取符合条件的管理员
            final ArrayList<String> uuidList = new ArrayList<>();
            {
                final Message resultMsg = this.getComment(null, null, authorUuid, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("COMMENT_NOT_EXIST");
                    msg.setAttach("评论不存在");
                    return msg;
                }
                for (int i = 0; i < array.length(); i++) {
                    final String uuid = array.getJSONObject(i).getString("uuid");
                    uuidList.add(uuid);
                }
            }
            // 删除符合条件的消息
            {
                for (int i = 0; i < uuidList.size(); i++) {
                    final String uuid = uuidList.get(i);
                    final Message resultMsg = this.removeCommentByUuid(false, uuid);
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
     * 修改评论
     * 
     * @param uuid 评论的uuid
     * @param content 内容
     * @return 消息对象
     */
    public Message modifyComment(final String uuid, final String authorUuid, final String authorName, final String authorRealName, final String title, final String content, final String remark,
        final String status, final String detail) {
        final Message msg = new Message();
        try {
            // 是否存在评论
            {
                if (!this.isExistComment(uuid, null, null, null)) {
                    msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                    msg.setContent("COMMENT_NOT_EXIST");
                    msg.setAttach("评论不存在");
                    return msg;
                }
            }
            // 修改评论
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "' and `remove_timestamp` is null";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != authorUuid) {
                        hm.put("author_uuid", authorUuid);
                    }
                    if (null != authorName) {
                        hm.put("author_name", authorName);
                    }
                    if (null != authorRealName) {
                        hm.put("author_real_name", authorRealName);
                    }
                    if (null != title) {
                        hm.put("title", title);
                    }
                    if (null != content) {
                        hm.put("content", content);
                    }
                    if (null != remark) {
                        hm.put("remark", remark);
                    }
                    if (null != status) {
                        hm.put("status", status);
                    }
                    if (null != detail) {
                        hm.put("detail", detail);
                    }
                    final long lastUpdateTimestamp = System.currentTimeMillis();
                    final String lastUpdateDatetime = this.simpleDateFormat.format(new Date(lastUpdateTimestamp));
                    hm.put("last_update_datetime", lastUpdateDatetime);
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_COMMENT_FAIL");
                        msg.setAttach("修改评论失败");
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
     * 获取评论
     * 
     * @param uuid uuid（允许为null）
     * @param problemUuid 问题的uuid（允许为null）
     * @param authorUuid 来源的uuid（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getComment(final String uuid, final String problemUuid, final String authorUuid, final Integer offset, final Integer rows) {
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
            if (null != problemUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "problem_uuid");
                obj.put("symbol", "=");
                obj.put("value", problemUuid);
                whereArray.put(obj);
            }
            if (null != authorUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "author_uuid");
                obj.put("symbol", "=");
                obj.put("value", authorUuid);
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

    /**
     * 是否存在评论
     * 
     * @param uuid 评论的uuid（允许为null）
     * @param problemUuid 问题的uuid
     * @param authorUuid 来源的uuid
     * @param excludeUuid 排除类别的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistComment(final String uuid, final String problemUuid, final String authorUuid, final String excludeUuid) throws Exception {
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
            if (null != problemUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "problem_uuid");
                obj.put("symbol", "=");
                obj.put("value", problemUuid);
                whereArray.put(obj);
            }
            if (null != authorUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "author_uuid");
                obj.put("symbol", "=");
                obj.put("value", authorUuid);
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