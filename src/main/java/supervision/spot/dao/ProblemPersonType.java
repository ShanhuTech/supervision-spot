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

import supervision.spot.dao.Problem.Fact;
import supervision.spot.dao.Problem.Read;
import supervision.spot.dao.Problem.Status;

/**
 * 问题个人类型
 */
public final class ProblemPersonType {
    // 数据库表名
    public static final String DATABASE_TABLE_NAME = "svp_problem-person-type";

    private Connection connection;

    public ProblemPersonType(final Connection connection) throws Exception {
        this.connection = connection;
    }

    /**
     * 添加问题个人类型
     * 
     * @param name 名称
     * @param score 分值
     * @param order 排序编号
     * @return 消息对象
     */
    public final Message addProblemPersonType(final String name, final Integer score, final Integer order) {
        final Message msg = new Message();
        try {
            final String uuid = StringKit.getUuidStr(true);
            // 添加问题个人类型
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "name", "score", "order"));
                    ps.setString(1, uuid);
                    ps.setString(2, name);
                    ps.setInt(3, score.intValue());
                    ps.setInt(4, order.intValue());
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_PROBLEM_PERSON_TYPE_FAIL");
                        msg.setAttach("添加问题个人类型失败");
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
     * 根据uuid删除问题个人类型
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid 问题个人类型的uuid
     * @return 消息对象
     */
    public final Message removeProblemPersonTypeByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在问题个人类型
            {
                if (checkExist) {
                    if (!this.isExistProblemPersonType(uuid, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_PERSON_TYPE_NOT_EXIST");
                        msg.setAttach("问题个人类型不存在");
                        return msg;
                    }
                }
            }
            // 查找关联
            // svp_problem
            {
                final Problem obj = new Problem(this.connection);
                if (obj.isExistProblem(null, null, null, uuid, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ASSOCIATED_DATA_PROBLEM_IS_EXIST");
                    msg.setAttach("存在关联数据问题");
                    return msg;
                }
            }
            // 删除问题个人类型
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
                        msg.setContent("REMOVE_PROBLEM_PERSON_TYPE_FAIL");
                        msg.setAttach("删除问题个人类型失败");
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
     * 修改问题个人类型（至少修改一项字段）
     * 
     * @param uuid 角色的uuid
     * @param name 问题个人类型名称（允许为null）
     * @param score 分值（允许为null）
     * @param order 排序编号（允许为null）
     * @return 消息对象
     */
    public final Message modifyProblemPersonType(final String uuid, final String name, final Integer score, final Integer order) {
        final Message msg = new Message();
        try {
            // 是否存在问题个人类型
            {
                if (!this.isExistProblemPersonType(uuid, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("PROBLEM_PERSON_TYPE_NOT_EXIST");
                    msg.setAttach("问题个人类型不存在");
                    return msg;
                }
            }
            // 修改问题个人类型
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "'";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != name) {
                        hm.put("name", name);
                    }
                    if (null != score) {
                        hm.put("score", score);
                    }
                    if (null != order) {
                        hm.put("order", order);
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_PROBLEM_PERSON_TYPE_FAIL");
                        msg.setAttach("修改问题个人类型失败");
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
     * 获取问题个人类型
     * 
     * @param uuid 问题个人类型的uuid（允许为null）
     * @param name 问题个人类型名称（允许为null）
     * @return 消息对象
     */
    public final Message getProblemPersonType(final String uuid, final String name) {
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
     * 获取问题个人类型数量
     * 
     * @param uuid 问题单位类型的uuid（允许为null）
     * @param read 是否已读（允许为null）
     * @param fact 是否属实（允许为null）
     * @param feedback 是否反馈（允许为null）
     * @param startCreateDatetime 开始创建时间（允许为null）
     * @param endCreateDatetime 结束创建时间（允许为null）
     * @return 消息对象
     */
    public final Message getProblemPersonTypeCount(final String uuid, final Read read, final Fact fact, final Status status, final String startCreateDatetime, final String endCreateDatetime) {
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
                        obj.put("name", rs.getString("name"));
                        {
                            final Problem problemObj = new Problem(this.connection);
                            final Message resultMsg = problemObj.getProblemExact(null, null, Problem.Type.PERSON, null, null, rs.getString("uuid"), null, read, fact, null, null, null, null, null, null, null, null, null, null, null, null, startCreateDatetime,
                                    endCreateDatetime, null, null, null, null);
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            obj.put("problem_count", ((JSONObject) resultMsg.getContent()).getJSONArray("array").length());
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
     * 是否存在问题个人类型
     * 
     * @param uuid 类别的uuid（允许为null）
     * @param name 名称（允许为null）
     * @param excludeUuid 排除类别的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistProblemPersonType(final String uuid, final String name, final String excludeUuid) throws Exception {
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