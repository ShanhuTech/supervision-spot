package supervision.spot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
import supervision.spot.dao.Problem.Fact;
import supervision.spot.dao.Problem.Read;
import supervision.spot.dao.Problem.Status;

/**
 * 问题单位类型
 */
public final class ProblemDepartmentType {
    // 数据库表名
    public static final String DATABASE_TABLE_NAME = "svp_problem-department-type";

    // 体系类型
    public static enum SystemType {
        ZHENG_ZHI, // 政治监督
        JING_WU, // 警务监督
        ZHI_FA, // 执法监督
        JI_JIAN, // 纪检监督
        MIN_YI // 民意监督
    }

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public ProblemDepartmentType(final Connection connection) throws Exception {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加问题单位类型
     * 
     * @param parentUuid 父级问题单位类型的uuid
     * @param name 名称
     * @param description 描述
     * @param order 排序编号
     * @return 消息对象
     */
    public final Message addProblemDepartmentType(final String parentUuid, final String name, final String description, final Integer order) {
        final Message msg = new Message();
        try {
            int level = 1;
            String orderGroup = String.format("%03d", order);
            // 如果parentUuid不是顶级问题单位类型检查parentUuid是否存在
            {
                if (!"0".equalsIgnoreCase(parentUuid)) {
                    final Message resultMsg = this.getProblemDepartmentType(parentUuid, null, null,null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PARENT_PROBLEM_DEPARTMENT_TYPE_NOT_EXIST");
                        msg.setAttach("父级问题单位类型不存在");
                        return msg;
                    }
                    level = array.getJSONObject(0).getInt("level") + 1;
                    orderGroup = array.getJSONObject(0).getString("order_group") + orderGroup;
                }
            }
            final String uuid = StringKit.getUuidStr(true);
            final long createTimestamp = System.currentTimeMillis();
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp));
            // 添加问题单位类型
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "parent_uuid", "name", "description", "level", "order", "order_group", "create_timestamp", "create_datetime"));
                    ps.setString(1, uuid);
                    ps.setString(2, parentUuid);
                    ps.setString(3, name);
                    ps.setString(4, description);
                    ps.setInt(5, level);
                    ps.setInt(6, order.intValue());
                    ps.setString(7, orderGroup);
                    ps.setLong(8, createTimestamp);
                    ps.setString(9, createDatetime);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_PROBLEM_DEPARTMENT_TYPE_FAIL");
                        msg.setAttach("添加问题单位类型失败");
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
     * 根据uuid删除问题单位类型
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid 问题等级的uuid
     * @return 消息对象
     */
    public final Message removeProblemDepartmentTypeByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在问题等级
            {
                if (checkExist) {
                    if (!this.isExistProblemDepartmentType(uuid, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_DEPARTMENT_TYPE_NOT_EXIST");
                        msg.setAttach("问题单位类型不存在");
                        return msg;
                    }
                }
            }
            final StringBuilder uuidSql = new StringBuilder();
            final String split = "' or";
            // 组成删除问题单位类型的SQL语句
            {
                final ArrayList<String> resultList = new ArrayList<>();
                // 添加当前要删除问题单位类型的uuid
                resultList.add(uuid);
                // 添加当前问题单位类型下所有子问题单位类型的uuid
                this.getChildrenProblemDepartmentType(resultList, uuid);
                // 转换所有子问题单位类型数据成SQL语句
                final Iterator<String> resultIter = resultList.iterator();
                while (resultIter.hasNext()) {
                    final String _uuid = resultIter.next();
                    uuidSql.append(" `uuid` = '" + _uuid);
                    uuidSql.append(split);
                }
            }
            // 删除问题单位类型
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where" + uuidSql.toString().substring(0, uuidSql.length() - (split.length() - 1/* 保留`字符 */));
                    final HashMap<String, Object> hm = new HashMap<>();
                    hm.put("remove_timestamp", Long.valueOf(System.currentTimeMillis()));
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("REMOVE_PROBLEM_DEPARTMENT_TYPE_FAIL");
                        msg.setAttach("删除问题单位类型失败");
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
     * 修改问题单位类型（至少修改一项字段）
     * 
     * @param uuid 角色的uuid
     * @param parentUuid 父级问题单位类型的uuid（允许为null）
     * @param name 问题单位类型名称（允许为null）
     * @param description 描述（允许为空，为null不修改，长度为0则清空）
     * @param order 排序编号（允许为null）
     * @return 消息对象
     */
    public final Message modifyProblemDepartmentType(final String uuid, final String parentUuid, final String name, final String description, final Integer order) {
        final Message msg = new Message();
        try {
            // 是否存在问题单位类型
            {
                if (!this.isExistProblemDepartmentType(uuid, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("PROBLEM_DEPARTMENT_TYPE_NOT_EXIST");
                    msg.setAttach("问题单位类型不存在");
                    return msg;
                }
            }
            int level = 1;
            String orderGroup = String.format("%03d", order);
            // 如果parentUuid不是顶级问题单位类型检查parentUuid是否存在
            {
                if (null != parentUuid) {
                    if (!"0".equalsIgnoreCase(parentUuid)) {
                        final Message resultMsg = this.getProblemDepartmentType(parentUuid, null, null,null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        if (0 >= array.length()) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("PARENT_PROBLEM_DEPARTMENT_TYPE_NOT_EXIST");
                            msg.setAttach("父级问题单位类型不存在");
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
                    if (!this.isExistProblemDepartmentType(parentUuid, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PARENT_PROBLEM_DEPARTMENT_TYPE_NOT_EXIST");
                        msg.setAttach("父级问题单位类型不存在");
                        return msg;
                    }
                }
            }
            // 修改问题单位类型
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "'";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != parentUuid) {
                        hm.put("parent_uuid", parentUuid);
                        hm.put("level", Integer.valueOf(level)); // 改变了父级问题单位类型的uuid才会改变级别
                    }
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
                    if (null != order) {
                        hm.put("order", order);
                        hm.put("order_group", orderGroup);
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_PROBLEM_DEPARTMENT_TYPE_FAIL");
                        msg.setAttach("修改问题单位类型失败");
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
     * 获取问题单位类型
     * 
     * @param uuid 问题单位类型的uuid（允许为null）
     * @param parentUuid 父级问题单位类型的uuid（允许为null）
     * @param name 问题单位类型名称（允许为null）
     * @param orderGroup 排序分组（允许为null）
     * @param systemType 体系类型（允许为null）
     * @return 消息对象
     */
    public final Message getProblemDepartmentType(final String uuid, final String parentUuid, final String name, final SystemType systemType) {
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
                obj.put("name", "name");
                obj.put("symbol", "=");
                obj.put("value", name);
                whereArray.put(obj);
            }
            if (null != systemType) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "system_type");
                obj.put("symbol", "=");
                obj.put("value", systemType.toString());
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
                        obj.put("description", rs.getString("description"));
                        obj.put("level", rs.getInt("level"));
                        obj.put("order", rs.getInt("order"));
                        obj.put("order_group", rs.getString("order_group"));
                        {
                            final Message resultMsg = this.getProblemDepartmentTypeByOrderGroup(rs.getString("order_group"));
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            obj.put("child", ((JSONObject) resultMsg.getContent()).getJSONArray("array"));
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
     * 根据名称模糊查询获取问题单位类型
     * 
     * @param name 问题单位类型名称（允许为null）
     * @return 消息对象
     */
    public final Message getProblemDepartmentTypeByNameLike(final String name) {
        final Message msg = new Message();
        try {
            final JSONArray whereArray = new JSONArray();
            if (null != name) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "name");
                obj.put("symbol", "like");
                obj.put("value", "%" + name + "%");
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
                        obj.put("description", rs.getString("description"));
                        obj.put("level", rs.getInt("level"));
                        obj.put("order", rs.getInt("order"));
                        obj.put("order_group", rs.getString("order_group"));
                        {
                            final Message resultMsg = this.getProblemDepartmentTypeByOrderGroup(rs.getString("order_group"));
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            obj.put("child", ((JSONObject) resultMsg.getContent()).getJSONArray("array"));
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
     * 获取问题单位类型
     * 
     * @param orderGroup 排序分组
     * @return 消息对象
     */
    public final Message getProblemDepartmentTypeByOrderGroup(final String orderGroup) {
        final Message msg = new Message();
        try {
            final JSONArray whereArray = new JSONArray();
            {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "order_group");
                obj.put("symbol", "like");
                obj.put("value", orderGroup + "%");
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
     * 获取问题单位类型数量
     * 
     * @param uuid 问题单位类型的uuid（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param toUuid 目标的uuid（允许为null）
     * @param read 是否已读（允许为null）
     * @param fact 是否属实（允许为null）
     * @param feedback 是否反馈（允许为null）
     * @param startCreateDatetime 开始创建时间（允许为null）
     * @param endCreateDatetime 结束创建时间（允许为null）
     * @return 消息对象
     */
    public final Message getProblemDepartmentTypeCount(final String uuid, final String fromUuid, final String toUuid, final Read read, final Fact fact, final Status status, final String startCreateDatetime, final String endCreateDatetime) {
        final Message msg = new Message();
        try {
            final JSONArray whereArray = new JSONArray();
            if (null != uuid) {
                final Message resultMsg = this.getProblemDepartmentType(uuid, null, null,null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("PROBLEM_DEPARTMENT_TYPE_NOT_EXIST");
                    msg.setAttach("问题单位类型不存在");
                    return msg;
                }
                final String orderGroup = array.getJSONObject(0).getString("order_group");
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "order_group");
                obj.put("symbol", "like");
                obj.put("value", orderGroup + "%");
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
                        obj.put("level", rs.getInt("level"));
                        obj.put("order", rs.getInt("order"));
                        obj.put("order_group", rs.getString("order_group"));
                        {
                            final Problem problemObj = new Problem(this.connection);
                            final Message resultMsg = problemObj.getProblemExact(null, null, Problem.Type.DEPARTMENT, fromUuid, toUuid, rs.getString("uuid"), null, read, fact, status, null, null, null, null, null, null, null, null, null, null, null,
                                    startCreateDatetime, endCreateDatetime, null, null, null, null);
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
     * 是否存在问题单位类型
     * 
     * @param uuid 问题单位类型的uuid（允许为null）
     * @param parentUuid 父级问题单位类型的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistProblemDepartmentType(final String uuid, final String parentUuid) throws Exception {
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
     * 获取子问题单位类型（父找子）
     * 
     * @param resultList 结果列表
     * @param uuid uuid
     */
    private void getChildrenProblemDepartmentType(final ArrayList<String> resultList, final String uuid) {
        final Message resultMsg = this.getProblemDepartmentType(null, uuid, null,null);
        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            return;
        }
        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
        for (int i = 0; i < array.length(); i++) {
            final JSONObject jo = array.getJSONObject(i);
            resultList.add(jo.getString("uuid"));
            this.getChildrenProblemDepartmentType(resultList, jo.getString("uuid"));
        }
    }
}