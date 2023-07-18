package supervision.spot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.string.StringKit;
import security.dao.Admin;
import security.dao.Role;

/**
 * 消息
 */
public class Message {
    // 数据库表名
    public static String DATABASE_TABLE_NAME = "svp_message";

    // 是否已读
    public static enum Read {
        UN_READ, // 未读
        READ // 已读
    }

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public Message(final Connection connection) {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加消息
     * 
     * @param fromUuid 来源的uuid（允许为0，当为0时表示系统发送）
     * @param toUuid 目标的uuid（允许为null）
     * @param roleName 角色名称（允许为null）
     * @param title 标题
     * @param content 内容
     * @param remark 备注
     * @return 消息对象
     */
    public com.palestink.server.sdk.msg.Message addMessage(final String fromUuid, final String toUuid, final String roleName, final String title, final String content, final String remark) {
        final com.palestink.server.sdk.msg.Message msg = new com.palestink.server.sdk.msg.Message();
        try {
            // 是否存在管理员
            {
                if (!fromUuid.equalsIgnoreCase("0")) {
                    final AdminInfo obj = new AdminInfo(this.connection);
                    if (!obj.isExistAdminInfo(fromUuid, null)) {
                        msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                        msg.setContent("FROM_ADMIN_NOT_EXIST");
                        msg.setAttach("来源管理员不存在");
                        return msg;
                    }
                }
            }
            // 是否存在管理员
            {
                if (null != toUuid) {
                    final AdminInfo obj = new AdminInfo(this.connection);
                    if (!obj.isExistAdminInfo(toUuid, null)) {
                        msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                        msg.setContent("TO_ADMIN_NOT_EXIST");
                        msg.setAttach("目标管理员不存在");
                        return msg;
                    }
                }
            }
            final ArrayList<String> roleUuidList = new ArrayList<>();
            // 是否存在角色
            {
                if (null != roleName) {
                    final Role obj = new Role(this.connection);
                    final com.palestink.server.sdk.msg.Message resultMsg = obj.getRole(null, roleName, null, null);
                    if (com.palestink.server.sdk.msg.Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                        msg.setContent("ROLE_NOT_EXIST");
                        msg.setAttach("角色不存在");
                        return msg;
                    }
                    for (int i = 0; i < array.length(); i++) {
                        final String uuid = array.getJSONObject(i).getString("uuid");
                        roleUuidList.add(uuid);
                    }
                }
            }
            final ArrayList<String> toUuidList = new ArrayList<>();
            // 是否存在管理员
            {
                for (int i = 0; i < roleUuidList.size(); i++) {
                    final Admin obj = new Admin(this.connection);
                    final com.palestink.server.sdk.msg.Message resultMsg = obj.getAdmin(null, roleUuidList.get(i), null, null, null, null);
                    if (com.palestink.server.sdk.msg.Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                        msg.setContent("ROLE_ACCOUNT_NOT_EXIST");
                        msg.setAttach("角色账户不存在");
                        return msg;
                    }
                    for (int j = 0; j < array.length(); j++) {
                        final String uuid = array.getJSONObject(j).getString("uuid");
                        toUuidList.add(uuid);
                    }
                }
            }
            String uuid = null;
            // 添加消息
            {
                if (null != toUuid) {
                    uuid = StringKit.getUuidStr(true);
                    final long createTimestamp = System.currentTimeMillis();
                    final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp));
                    {
                        PreparedStatement ps = null;
                        try {
                            ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "from_uuid", "to_uuid", "title", "content", "remark", "is_read", "create_timestamp", "create_datetime"));
                            ps.setString(1, uuid);
                            ps.setString(2, fromUuid);
                            ps.setString(3, toUuid);
                            ps.setString(4, title);
                            ps.setString(5, content);
                            ps.setString(6, remark);
                            ps.setString(7, Read.UN_READ.toString());
                            ps.setLong(8, createTimestamp);
                            ps.setString(9, createDatetime);
                            final int res = ps.executeUpdate();
                            if (0 >= res) {
                                msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                                msg.setContent("ADD_MESSAGE_FAIL");
                                msg.setAttach("添加消息失败");
                                return msg;
                            }
                        } finally {
                            if (null != ps) {
                                ps.close();
                            }
                        }
                    }
                }
            }
            final JSONArray uuidArray = new JSONArray();
            // 添加消息
            {
                for (int i = 0; i < toUuidList.size(); i++) {
                    final String uid = StringKit.getUuidStr(true);
                    final long createTimestamp = System.currentTimeMillis();
                    final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp));
                    {
                        PreparedStatement ps = null;
                        try {
                            ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "from_uuid", "to_uuid", "title", "content", "remark", "is_read", "create_timestamp", "create_datetime"));
                            ps.setString(1, uid);
                            ps.setString(2, fromUuid);
                            ps.setString(3, toUuidList.get(i));
                            ps.setString(4, title);
                            ps.setString(5, content);
                            ps.setString(6, remark);
                            ps.setString(7, Read.UN_READ.toString());
                            ps.setLong(8, createTimestamp);
                            ps.setString(9, createDatetime);
                            final int res = ps.executeUpdate();
                            if (0 >= res) {
                                msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                                msg.setContent("ADD_MESSAGE_FAIL");
                                msg.setAttach("添加消息失败");
                                return msg;
                            }
                            uuidArray.put(uid);
                        } finally {
                            if (null != ps) {
                                ps.close();
                            }
                        }
                    }
                }
            }
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.SUCCESS);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("uuid", uuid);
            resultObj.put("uuid_array", uuidArray);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 根据uuid删除消息
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid uuid
     * @return 消息对象
     */
    public com.palestink.server.sdk.msg.Message removeMessageByUuid(final boolean checkExist, final String uuid) {
        final com.palestink.server.sdk.msg.Message msg = new com.palestink.server.sdk.msg.Message();
        try {
            // 是否存在消息
            {
                if (checkExist) {
                    if (!this.isExistMessage(uuid, null, null, null)) {
                        msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                        msg.setContent("MESSAGE_NOT_EXIST");
                        msg.setAttach("消息不存在");
                        return msg;
                    }
                }
            }
            // 删除消息
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
                        msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                        msg.setContent("REMOVE_MESSAGE_FAIL");
                        msg.setAttach("删除消息失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            // 删除关联
            // svp_storage-file
            {
                final StorageFile obj = new StorageFile(this.connection);
                final com.palestink.server.sdk.msg.Message resultMsg = obj.removeStorageFileByAssociateUuid(uuid);
                if (com.palestink.server.sdk.msg.Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
            }
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.SUCCESS);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 根据来源的uuid删除消息<br />
     * 非自身uuid删除无需检查带删除数据是否存在
     * 
     * @param fromUuid 来源的uuid
     * @return 消息对象
     */
    public com.palestink.server.sdk.msg.Message removeMessageByFromUuid(final String fromUuid) {
        final com.palestink.server.sdk.msg.Message msg = new com.palestink.server.sdk.msg.Message();
        if (null == fromUuid) {
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
            msg.setContent("REMOVE_PARAMETER_NOT_ALLOW_NULL");
            msg.setAttach("删除参数不允许为空");
            return msg;
        }
        try {
            // 获取符合条件的管理员
            final ArrayList<String> uuidList = new ArrayList<>();
            {
                final com.palestink.server.sdk.msg.Message resultMsg = this.getMessage(null, fromUuid, null, null, null, null, null, null, null, null, null);
                if (com.palestink.server.sdk.msg.Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                    msg.setContent("MESSAGE_NOT_EXIST");
                    msg.setAttach("消息不存在");
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
                    final com.palestink.server.sdk.msg.Message resultMsg = this.removeMessageByUuid(false, uuid);
                    if (com.palestink.server.sdk.msg.Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                }
            }
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.SUCCESS);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 根据来源的uuid删除消息<br />
     * 非自身uuid删除无需检查带删除数据是否存在
     * 
     * @param toUuid 目标的uuid
     * @return 消息对象
     */
    public com.palestink.server.sdk.msg.Message removeMessageByToUuid(final String toUuid) {
        final com.palestink.server.sdk.msg.Message msg = new com.palestink.server.sdk.msg.Message();
        if (null == toUuid) {
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
            msg.setContent("REMOVE_PARAMETER_NOT_ALLOW_NULL");
            msg.setAttach("删除参数不允许为空");
            return msg;
        }
        try {
            // 获取符合条件的管理员
            final ArrayList<String> uuidList = new ArrayList<>();
            {
                final com.palestink.server.sdk.msg.Message resultMsg = this.getMessage(null, toUuid, null, null, null, null, null, null, null, null, null);
                if (com.palestink.server.sdk.msg.Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                    msg.setContent("MESSAGE_NOT_EXIST");
                    msg.setAttach("消息不存在");
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
                    final com.palestink.server.sdk.msg.Message resultMsg = this.removeMessageByUuid(false, uuid);
                    if (com.palestink.server.sdk.msg.Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                }
            }
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.SUCCESS);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 修改消息
     * 
     * @param uuid 消息的uuid
     * @param read 是否已读
     * @return 消息对象
     */
    public com.palestink.server.sdk.msg.Message modifyMessage(final String uuid, final Read read) {
        final com.palestink.server.sdk.msg.Message msg = new com.palestink.server.sdk.msg.Message();
        try {
            // 是否存在消息
            {
                if (!this.isExistMessage(uuid, null, null, null)) {
                    msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                    msg.setContent("MESSAGE_NOT_EXIST");
                    msg.setAttach("消息不存在");
                    return msg;
                }
            }
            // 修改消息
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "' and `remove_timestamp` is null";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != read) {
                        hm.put("is_read", read.toString());
                        if (Read.READ == read) {
                            final long currentTimestamp = System.currentTimeMillis();
                            final String currentDatetime = this.simpleDateFormat.format(new Date(currentTimestamp));
                            hm.put("read_timestamp", Long.valueOf(currentTimestamp));
                            hm.put("read_datetime", currentDatetime);
                        }
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                        msg.setContent("MODIFY_MESSAGE_FAIL");
                        msg.setAttach("修改消息失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.SUCCESS);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 获取消息
     * 
     * @param uuid uuid（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param toUuid 目标的uuid（允许为null）
     * @param remark 备注（允许为null）
     * @param read 是否已读（允许为null）
     * @param startCreateDatetime 开始创建时间（允许为null）
     * @param endCreateDatetime 结束创建时间（允许为null）
     * @param startReadDatetime 开始已读时间（允许为null）
     * @param endReadDatetime 结束已读时间（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public com.palestink.server.sdk.msg.Message getMessage(final String uuid, final String fromUuid, final String toUuid, final String remark, final Read read, final String startCreateDatetime, final String endCreateDatetime, final String startReadDatetime,
            final String endReadDatetime, final Integer offset, final Integer rows) {
        final com.palestink.server.sdk.msg.Message msg = new com.palestink.server.sdk.msg.Message();
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
            if (null != remark) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "remark");
                obj.put("symbol", "=");
                obj.put("value", remark);
                whereArray.put(obj);
            }
            if (null != read) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "is_read");
                obj.put("symbol", "=");
                obj.put("value", read.toString());
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
            if (null != startReadDatetime) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "read_timestamp");
                obj.put("symbol", ">");
                obj.put("value", this.simpleDateFormat.parse(startReadDatetime).getTime());
                whereArray.put(obj);
            }
            if (null != endReadDatetime) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "read_timestamp");
                obj.put("symbol", "<");
                obj.put("value", this.simpleDateFormat.parse(endReadDatetime).getTime());
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
                            if (columnLabel.equalsIgnoreCase("from_uuid")) {
                                // 获取管理员信息
                                {
                                    final AdminInfo ai = new AdminInfo(this.connection);
                                    final com.palestink.server.sdk.msg.Message aiResultMsg = ai.getAdminInfo((String) rs.getObject(columnLabel), null, null, null, null, null, null, null, null, null);
                                    if (com.palestink.server.sdk.msg.Message.Status.SUCCESS != aiResultMsg.getStatus()) {
                                        return aiResultMsg;
                                    }
                                    final JSONArray aiArray = ((JSONObject) aiResultMsg.getContent()).getJSONArray("array");
                                    if (0 < aiArray.length()) {
                                        obj.put("from_uuid_info", aiArray.getJSONObject(0));
                                    }
                                }
                            }
                            if (columnLabel.equalsIgnoreCase("to_uuid")) {
                                // 获取管理员信息
                                {
                                    final AdminInfo ai = new AdminInfo(this.connection);
                                    final com.palestink.server.sdk.msg.Message aiResultMsg = ai.getAdminInfo((String) rs.getObject(columnLabel), null, null, null, null, null, null, null, null, null);
                                    if (com.palestink.server.sdk.msg.Message.Status.SUCCESS != aiResultMsg.getStatus()) {
                                        return aiResultMsg;
                                    }
                                    final JSONArray aiArray = ((JSONObject) aiResultMsg.getContent()).getJSONArray("array");
                                    if (0 < aiArray.length()) {
                                        obj.put("to_uuid_info", aiArray.getJSONObject(0));
                                    }
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
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.SUCCESS);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(com.palestink.server.sdk.msg.Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 是否存在消息
     * 
     * @param uuid 消息的uuid（允许为null）
     * @param fromUuid 来源的uuid
     * @param toUuid 目标的uuid
     * @param excludeUuid 排除类别的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistMessage(final String uuid, final String fromUuid, final String toUuid, final String excludeUuid) throws Exception {
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