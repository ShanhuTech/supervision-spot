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
 * 问责
 */
public class Accountable {
    // 数据库表名
    public static String DATABASE_TABLE_NAME = "svp_accountable";

    // 类型
    public static enum Type {
        PERSON, // 个人
        DEPARTMENT // 单位
    }

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public Accountable(final Connection connection) {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加问责
     * 
     * @param type 类型
     * @param fromUuid 来源的uuid
     * @param toUuid 目标的uuid
     * @param typeUuid 类型的uuid
     * @param content 内容
     * @param files 文件二维数组，格式：{{"base64文件内容", "PNG"}, ...}
     * @param createTimestamp 创建时间戳
     * @return 消息对象
     */
    public Message addAccountable(final String problemNumber, final Type type, final String fromUuid, final String toUuid, final String typeUuid, final String content, final String fromDepartmentUuid, final String toDepartmentUuid, Long createTimestamp) {
        final Message msg = new Message();
        try {
            // 是否存在问题编号
            {
                if (null != problemNumber) {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, Long.valueOf(problemNumber), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_NUMBER_NOT_EXIST");
                        msg.setAttach("问题编号不存在");
                        return msg;
                    }
                }
            }
            // 是否存在管理员
            {
                final AdminInfo obj = new AdminInfo(this.connection);
                if (!obj.isExistAdminInfo(fromUuid, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("FROM_ADMIN_NOT_EXIST");
                    msg.setAttach("来源管理员不存在");
                    return msg;
                }
            }
            // 是否存在部门
            {
                final Department obj = new Department(this.connection);
                if (!obj.isExistDepartment(fromDepartmentUuid, null, null, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("FROM_DEPARTMENT_UUID_NOT_EXIST");
                    msg.setAttach("来源部门的uuid");
                    return msg;
                }
            }
            // 是否存在部门
            {
                final Department obj = new Department(this.connection);
                if (!obj.isExistDepartment(toDepartmentUuid, null, null, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("TO_DEPARTMENT_UUID_NOT_EXIST");
                    msg.setAttach("目标部门的uuid");
                    return msg;
                }
            }
            // 是否存在问责单位类型
            {
                final AccountableType obj = new AccountableType(this.connection);
                final Message resultMsg = obj.getAccountableType(typeUuid, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("PROBLEM_TYPE_NOT_EXIST");
                    msg.setAttach("问责类型不存在");
                    return msg;
                }
            }
            if (Type.PERSON == type) {
                String personDepartmentUuid = null;
                // 是否存在管理员信息
                {
                    final AdminInfo obj = new AdminInfo(this.connection);
                    final Message resultMsg = obj.getAdminInfo(toUuid, null, null, null, null, null, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("TO_ADMIN_NOT_EXIST");
                        msg.setAttach("目标管理员不存在");
                        return msg;
                    }
                    personDepartmentUuid = array.getJSONObject(0).getString("department_uuid");
                }
            } else if (Type.DEPARTMENT == type) {
                // 是否存在单位
                {
                    final Department obj = new Department(this.connection);
                    if (!obj.isExistDepartment(toUuid, null, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("TO_DEPARTMENT_NOT_EXIST");
                        msg.setAttach("目标单位不存在");
                        return msg;
                    }
                }
            }
            // 添加问责
            final String uuid = StringKit.getUuidStr(true);
            createTimestamp = (null != createTimestamp) ? createTimestamp : Long.valueOf(System.currentTimeMillis());
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp.longValue()));
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(
                            DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "problem_number", "type", "from_uuid", "to_uuid", "type_uuid", "content", "from_department_uuid", "to_department_uuid", "create_timestamp", "create_datetime"));
                    ps.setString(1, uuid);
                    if (null == problemNumber) {
                        ps.setNull(2, Types.BIGINT);
                    } else {
                        ps.setLong(2, Long.valueOf(problemNumber));
                    }
                    ps.setString(3, type.toString());
                    ps.setString(4, fromUuid);
                    ps.setString(5, toUuid);
                    ps.setString(6, typeUuid);
                    ps.setString(7, content);
                    ps.setString(8, fromDepartmentUuid);
                    ps.setString(9, toDepartmentUuid);
                    ps.setLong(10, createTimestamp.longValue());
                    ps.setString(11, createDatetime);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_ACCOUNTABLE_FAIL");
                        msg.setAttach("添加问责失败");
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
     * 根据uuid删除问责
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid uuid
     * @return 消息对象
     */
    public Message removeAccountableByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在问责
            {
                if (checkExist) {
                    if (!this.isExistAccountable(uuid, null, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_NOT_EXIST");
                        msg.setAttach("问责不存在");
                        return msg;
                    }
                }
            }
            // 删除问责
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
                        msg.setContent("REMOVE_PROBLEM_FAIL");
                        msg.setAttach("删除问责失败");
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
     * 根据uuid删除问责
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid uuid
     * @return 消息对象
     */
    public Message deleteAccountableByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在问责
            {
                if (checkExist) {
                    if (!this.isExistAccountable(uuid, null, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_NOT_EXIST");
                        msg.setAttach("问责不存在");
                        return msg;
                    }
                }
            }
            // 删除问责
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "'";
                    final String sql = "delete from `" + DATABASE_TABLE_NAME + "` " + whereSql;
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("DELETE_PROBLEM_FAIL");
                        msg.setAttach("删除问责失败");
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
     * 修改问责（至少修改一项字段）
     * 
     * @param uuid 问责的uuid
     * @param problemNumber 问题的编号（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param toUuid 目标的uuid（允许为null）
     * @param typeUuid 类型的uuid（允许为null）
     * @param content 内容（允许为null）
     * @param read 是否已读（允许为null）
     * @param fact 是否属实（允许为null）
     * @param status 状态（允许为null）
     * @param isUnfeedback24hNotify 是否未反馈24小时通知（允许为null）
     * @return 消息对象
     */
    public Message modifyAccountable(final String uuid, Type type, final String problemNumber, final String fromUuid, final String toUuid, final String typeUuid, final String content, final String toDepartmentUuid) {
        final Message msg = new Message();
        try {
            // Type type = null;
            // 是否存在问责
            if (null == type) {
                final Message resultMsg = this.getAccountable(uuid, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ACCOUNTABLE_NOT_EXIST");
                    msg.setAttach("问责不存在");
                    return msg;
                }
                type = Type.valueOf(array.getJSONObject(0).getString("type"));
            }
            // 是否存在问题编号
            {
                if ((null != problemNumber) && (!problemNumber.equalsIgnoreCase("null"))) {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, Long.valueOf(problemNumber), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_NUMBER_NOT_EXIST");
                        msg.setAttach("问题编号不存在");
                        return msg;
                    }
                }
            }
            // 是否存在管理员
            {
                if (null != fromUuid) {
                    final AdminInfo obj = new AdminInfo(this.connection);
                    if (!obj.isExistAdminInfo(fromUuid, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("FROM_ADMIN_NOT_EXIST");
                        msg.setAttach("来源管理员不存在");
                        return msg;
                    }
                }
            }
            if (Type.PERSON == type) {
                // 是否存在管理员
                {
                    if (null != toUuid) {
                        final AdminInfo obj = new AdminInfo(this.connection);
                        if (!obj.isExistAdminInfo(toUuid, null)) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("TO_ADMIN_NOT_EXIST");
                            msg.setAttach("目标管理员不存在");
                            return msg;
                        }
                    }
                }
                // 是否存在问责个人类型
                {
                    final AccountableType obj = new AccountableType(this.connection);
                    if (!obj.isExistAccountableType(typeUuid, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ACCOUNTABLE_TYPE_NOT_EXIST");
                        msg.setAttach("问责类型不存在");
                        return msg;
                    }
                }
            } else if (Type.DEPARTMENT == type) {
                // 是否存在单位
                {
                    if (null != fromUuid) {
                        final Department obj = new Department(this.connection);
                        if (!obj.isExistDepartment(toUuid, null, null, null, null)) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("TO_DEPARTMENT_NOT_EXIST");
                            msg.setAttach("目标单位不存在");
                            return msg;
                        }
                    }
                }
                // 是否存在问责单位类型
                {
                    final AccountableType obj = new AccountableType(this.connection);
                    if (!obj.isExistAccountableType(typeUuid, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ACCOUNTABLE_TYPE_NOT_EXIST");
                        msg.setAttach("问责类型不存在");
                        return msg;
                    }
                }
            }
            // 是否存在部门
            if (null != toDepartmentUuid) {
                final Department obj = new Department(this.connection);
                if (!obj.isExistDepartment(toDepartmentUuid, null, null, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("TO_DEPARTMENT_UUID_NOT_EXIST");
                    msg.setAttach("目标部门的uuid");
                    return msg;
                }
            }
            // 修改问责
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "' and `remove_timestamp` is null";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != type) {
                        hm.put("type", type.toString());
                    }
                    if (null != problemNumber) {
                        if (problemNumber.equalsIgnoreCase("null")) {
                            hm.put("problem_number", null);
                        } else {
                            hm.put("problem_number", problemNumber);
                        }
                    }
                    if (null != fromUuid) {
                        hm.put("from_uuid", fromUuid);
                    }
                    if (null != toUuid) {
                        hm.put("to_uuid", toUuid);
                    }
                    if (null != typeUuid) {
                        hm.put("type_uuid", typeUuid);
                    }
                    if (null != content) {
                        hm.put("content", content);
                    }
                    if (null != toDepartmentUuid) {
                        hm.put("to_department_uuid", toDepartmentUuid);
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_PROBLEM_FAIL");
                        msg.setAttach("修改问责失败");
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
     * 获取问责
     * 
     * @param uuid
     * @param number
     * @param type
     * @param fromUuid
     * @param toUuid
     * @param typeUuid
     * @param typeUuidArray
     * @param read
     * @param fact
     * @param status
     * @param responsibleUuidLike
     * @param problemType
     * @param fromDepartmentUuid
     * @param toDepartmentUuid
     * @param orFromUuid
     * @param orToUuid
     * @param orResponsibleUuidLike
     * @param orFromDepartmentUuid
     * @param orToDepartmentUuid
     * @param responsibleUuidHas
     * @param conditionGroup
     * @param startCreateDatetime
     * @param endCreateDatetime
     * @param startReadDatetime
     * @param endReadDatetime
     * @param offset
     * @param rows
     * @return 消息对象
     */
    public Message getAccountable(final String uuid, final Long problemNumber, final Type type, final String fromUuid, final String toUuid, final String typeUuid, final String[] typeUuidArray, final String fromDepartmentUuid, final String toDepartmentUuid,
            final String orFromUuid, final String orToUuid, final String orFromDepartmentUuid, final String orToDepartmentUuid, final String startCreateDatetime, final String endCreateDatetime, final Integer offset, final Integer rows) {
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
            if (null != problemNumber) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "number");
                obj.put("symbol", "=");
                obj.put("value", problemNumber.longValue());
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
            if (null != fromUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "from_uuid");
                obj.put("symbol", "=");
                obj.put("value", fromUuid);
                whereArray.put(obj);
            }
            if (null != toUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "to_uuid");
                obj.put("symbol", "=");
                obj.put("value", toUuid);
                whereArray.put(obj);
            }
            if (null != typeUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "type_uuid");
                obj.put("symbol", "=");
                obj.put("value", typeUuid);
                whereArray.put(obj);
            }
            if (null != typeUuidArray) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "type_uuid");
                obj.put("symbol", "in");
                obj.put("value", typeUuidArray);
                whereArray.put(obj);
            }
            if (null != fromDepartmentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "from_department_uuid");
                obj.put("symbol", "=");
                obj.put("value", fromDepartmentUuid);
                whereArray.put(obj);
            }
            if (null != toDepartmentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "to_department_uuid");
                obj.put("symbol", "=");
                obj.put("value", toDepartmentUuid);
                whereArray.put(obj);
            }
            if (null != orFromUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "from_uuid");
                obj.put("symbol", "=");
                obj.put("value", orFromUuid);
                whereArray.put(obj);
            }
            if (null != orToUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "to_uuid");
                obj.put("symbol", "=");
                obj.put("value", orToUuid);
                whereArray.put(obj);
            }
            if (null != orFromDepartmentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "from_department_uuid");
                obj.put("symbol", "=");
                obj.put("value", orFromDepartmentUuid);
                whereArray.put(obj);
            }
            if (null != orToDepartmentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "to_department_uuid");
                obj.put("symbol", "=");
                obj.put("value", orToDepartmentUuid);
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
                            if (columnLabel.equalsIgnoreCase("uuid")) {
                                // 获取问责相关存储文件
                                {
                                    final StorageFile sf = new StorageFile(this.connection);
                                    final Message resultMsg = sf.getStorageFile(null, (String) rs.getObject(columnLabel), null, null, null);
                                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                        return resultMsg;
                                    }
                                    final JSONArray sfArray = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                                    if (0 < sfArray.length()) {
                                        obj.put("storage_file", sfArray);
                                    }
                                }
                            }
                        }
                        if (obj.has("from_uuid")) {
                            // 获取from_uuid个人的关联信息
                            final AdminInfo ai = new AdminInfo(this.connection);
                            final Message resultMsg = ai.getAdminInfo(obj.getString("from_uuid"), null, null, null, null, null, null, null, null, null);
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                final JSONObject aiObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                obj.put("from_real_name", aiObj.getString("real_name"));
                                obj.put("from_account_name", aiObj.getString("name"));
                            }
                        }
                        if (obj.has("type") && obj.has("to_uuid")) {
                            if (Type.PERSON == Type.valueOf(obj.getString("type"))) {
                                // 获取to_uuid个人的关联信息
                                final AdminInfo ai = new AdminInfo(this.connection);
                                final Message resultMsg = ai.getAdminInfo(obj.getString("to_uuid"), null, null, null, null, null, null, null, null, null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                    final JSONObject aiObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                    obj.put("to_real_name", aiObj.getString("real_name"));
                                    obj.put("to_account_name", aiObj.getString("name"));
                                }
                            } else if (Type.DEPARTMENT == Type.valueOf(obj.getString("type"))) {
                                String parentUuid = null;
                                // 获取to_uuid单位的名称
                                {
                                    final Department dep = new Department(this.connection);
                                    final Message resultMsg = dep.getDepartment(obj.getString("to_uuid"), null, null, null, null, null, null, null, null);
                                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                        return resultMsg;
                                    }
                                    if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                        final JSONObject depObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                        obj.put("department_name", depObj.getString("name"));
                                        if (depObj.has("parent_uuid") && (null != depObj.getString("parent_uuid")) && (1/* 排除parent_uuid为0的情况 */ < depObj.getString("parent_uuid").length())) {
                                            parentUuid = depObj.getString("parent_uuid");
                                        }
                                    }
                                }
                                // 获取to_uuid单位父级的名称
                                {
                                    final Department dep = new Department(this.connection);
                                    final Message resultMsg = dep.getDepartment(parentUuid, null, null, null, null, null, null, null, null);
                                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                        return resultMsg;
                                    }
                                    if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                        final JSONObject depObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                        obj.put("department_parent_name", depObj.getString("name"));
                                    }
                                }
                            }
                        }
                        if (obj.has("type") && obj.has("type_uuid")) {
                            // 获取type_uuid的关联信息
                            final AccountableType ppt = new AccountableType(this.connection);
                            final Message resultMsg = ppt.getAccountableType(obj.getString("type_uuid"), null);
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                final JSONObject pptObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                obj.put("accountable_type_name", pptObj.getString("name"));
                            }
                        }
                        if (obj.has("from_department_uuid")) {
                            {
                                final Department dep = new Department(this.connection);
                                final Message resultMsg = dep.getDepartment(obj.getString("from_department_uuid"), null, null, null, null, null, null, null, null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                    final JSONObject depObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                    obj.put("from_department_name", depObj.getString("name"));
                                }
                            }
                        }
                        if (obj.has("to_department_uuid")) {
                            {
                                final Department dep = new Department(this.connection);
                                final Message resultMsg = dep.getDepartment(obj.getString("to_department_uuid"), null, null, null, null, null, null, null, null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                    final JSONObject depObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                    obj.put("to_department_name", depObj.getString("name"));
                                }
                            }
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

    public Message getAccountableStatistics() {
        final Message msg = new Message();
        try {
            final JSONObject resultObj = new JSONObject();
            final JSONArray array = new JSONArray();
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("SELECT t.name AS accountable_type, COUNT(a.uuid) AS accountable_count FROM `" + AccountableType.DATABASE_TABLE_NAME + "` t LEFT JOIN `" + Accountable.DATABASE_TABLE_NAME
                            + "` a ON a.type_uuid = t.uuid AND a.remove_timestamp IS NULL WHERE t.remove_timestamp IS NULL GROUP BY t.uuid");
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
     * 是否存在问责
     * 
     * @param uuid 问题的uuid（允许为null）
     * @param fromUuid 来源的uuid
     * @param toUuid 目标的uuid
     * @param typeUuid 类型的uuid
     * @param excludeUuid 排除类别的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistAccountable(final String uuid, final String fromUuid, final String toUuid, final String typeUuid, final String excludeUuid) throws Exception {
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
            if (null != fromUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "from_uuid");
                obj.put("symbol", "=");
                obj.put("value", fromUuid);
                whereArray.put(obj);
            }
            if (null != toUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "to_uuid");
                obj.put("symbol", "=");
                obj.put("value", toUuid);
                whereArray.put(obj);
            }
            if (null != typeUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "type_uuid");
                obj.put("symbol", "=");
                obj.put("value", typeUuid);
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