package supervision.spot.dao;

import java.io.File;
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
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.encrypt.Base64;
import com.palestink.utils.io.IoKit;
import com.palestink.utils.net.HttpKit;
import com.palestink.utils.string.StringKit;
import env.config.Resource;

/**
 * 存储文件
 */
public class StorageFile {
    // 数据库表名
    public static String DATABASE_TABLE_NAME = "svp_storage-file";

    // 关联类型
    public static enum AssociateType {
        PROBLEM, // 问题
        FEEDBACK // 反馈
    }

    // 后缀
    public static enum Suffix {
        JPG, JPEG, PNG, TXT, WPS, PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, MP4, AVI, RAR, ZIP
    }

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public StorageFile(final Connection connection) {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加存储文件
     * 
     * @param associateType 关联类型
     * @param associateUuid 关联的uuid
     * @param suffix 后缀
     * @param fileUrl 文件地址（与fileBase64二选一）
     * @param fileBase64 文件base64内容（与fileUrl二选一）
     * @return 消息对象
     */
    public Message addStorageFile(final AssociateType associateType, final String associateUuid, final Suffix suffix, final String fileUrl, final String fileBase64) {
        final Message msg = new Message();
        try {
            if (AssociateType.PROBLEM == associateType) {
                // 是否存在问题
                {
                    final Problem obj = new Problem(this.connection);
                    if (!obj.isExistProblem(associateUuid, null, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_NOT_EXIST");
                        msg.setAttach("问题不存在");
                        return msg;
                    }
                }
            } else if (AssociateType.FEEDBACK == associateType) {
                // 是否存在反馈
                {
                    final Feedback obj = new Feedback(this.connection);
                    if (!obj.isExistFeedback(associateUuid, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("FEEDBACK_NOT_EXIST");
                        msg.setAttach("反馈不存在");
                        return msg;
                    }
                }
            }
            // 添加存储文件
            final String uuid = StringKit.getUuidStr(true);
            final String saveFileName = StringKit.getUuidStr(true) + "." + suffix.toString().toLowerCase();
            final long createTimestamp = System.currentTimeMillis();
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp));
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "associate_uuid", "suffix", "url", "create_timestamp", "create_datetime"));
                    ps.setString(1, uuid);
                    ps.setString(2, associateUuid);
                    ps.setString(3, suffix.toString().toLowerCase());
                    ps.setString(4, saveFileName);
                    ps.setLong(5, createTimestamp);
                    ps.setString(6, createDatetime);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_STORAGE_FILE_FAIL");
                        msg.setAttach("添加存储文件失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            if (null != fileUrl) {
                try {
                    HttpKit.downloadFile(fileUrl, Resource.PROBLEM_FILE_OUTPUT_DIR_PATH + saveFileName, null, null, null);
                } catch (final Exception e) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("DOWNLOAD_FILE_FAIL");
                    msg.setAttach("下载文件失败");
                    return msg;
                }
            } else {
                try {
                    final byte[] data = Base64.decode(fileBase64);
                    IoKit.simpleWriteFile(Resource.PROBLEM_FILE_OUTPUT_DIR_PATH + saveFileName, data);
                } catch (final Exception e) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("SAVE_IMAGE_FAIL");
                    msg.setAttach("保存文件失败");
                    return msg;
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
     * 添加问题
     * 
     * @param associateType 关联类型
     * @param associateUuid 关联的uuid
     * @param files 文件二维数组，格式：{{"base64文件内容", "PNG"}, ...}
     * @return 消息对象
     */
    public Message addStorageFile(final AssociateType associateType, final String associateUuid, final String[][] files) {
        final Message msg = new Message();
        try {
            if ((null == files) || (0 >= files.length)) {
                msg.setStatus(Message.Status.ERROR);
                msg.setContent("FILES_NOT_BE_NULL");
                msg.setAttach("文件二维数组不能为空");
                return msg;
            }
            final JSONArray array = new JSONArray();
            if ((null != files) && (0 < files.length)) {
                for (int i = 0; i < files.length; i++) {
                    final String[] file = files[i];
                    final String fileContent = file[0];
                    final String fileSuffix = file[1];
                    final StorageFile obj = new StorageFile(this.connection);
                    final Message resultMsg = obj.addStorageFile(associateType, associateUuid, StorageFile.Suffix.valueOf(fileSuffix), null, fileContent);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    array.put(((JSONObject) resultMsg.getContent()).getString("uuid"));
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
     * 根据uuid删除存储文件<br />
     * 由于删除存储文件时要删除对应的文件，所以这里必须要检查uuid是否存在，这样才能获取url。
     * 
     * @param uuid uuid
     * @return 消息对象
     */
    public Message removeStorageFileByUuid(final String uuid) {
        final Message msg = new Message();
        try {
            @SuppressWarnings("unused")
            String url = null;
            // 是否存在存储文件
            {
                final Message resultMsg = this.getStorageFile(uuid, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("STORAGE_FILE_NOT_EXIST");
                    msg.setAttach("存储文件不存在");
                    return msg;
                }
                url = array.getJSONObject(0).getString("url");
            }
            // 删除存储文件
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
                        msg.setContent("REMOVE_STORAGE_FILE_FAIL");
                        msg.setAttach("删除存储文件失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            // 2023-03-01：不删除物理存储文件。
            // // 删除文件
            // {
            // final File oldFile = new File(Resource.PROBLEM_FILE_OUTPUT_DIR_PATH + url);
            // if (oldFile.exists()) {
            // if (!oldFile.delete()) {
            // final StorageFileDeleteFailRecord obj = new StorageFileDeleteFailRecord(this.connection);
            // final Message resultMsg = obj.addStorageFileDeleteFailRecord(oldFile.getAbsolutePath());
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // }
            // }
            // }
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
     * 根据关联的uuid删除存储文件<br />
     * 非自身uuid删除无需检查带删除数据是否存在
     * 
     * @param associateUuid 关联的uuid
     * @return 消息对象
     */
    public Message removeStorageFileByAssociateUuid(final String associateUuid) {
        final Message msg = new Message();
        if (null == associateUuid) {
            msg.setStatus(Message.Status.ERROR);
            msg.setContent("REMOVE_PARAMETER_NOT_ALLOW_NULL");
            msg.setAttach("删除参数不允许为空");
            return msg;
        }
        try {
            // 获取符合条件的存储文件
            final ArrayList<String> uuidList = new ArrayList<>();
            {
                final Message resultMsg = this.getStorageFile(null, associateUuid, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                // 这里无需判断array是否有内容，允许关联的uuid没有存储文件的情况。
                for (int i = 0; i < array.length(); i++) {
                    final String uuid = array.getJSONObject(i).getString("uuid");
                    uuidList.add(uuid);
                }
            }
            // 删除符合条件的存储文件
            {
                for (int i = 0; i < uuidList.size(); i++) {
                    final String uuid = uuidList.get(i);
                    final Message resultMsg = this.removeStorageFileByUuid(uuid);
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
     * 修改存储文件（至少修改一项字段）
     * 
     * @param uuid 存储文件的uuid
     * @param associateType 关联类型
     * @param associateUuid 关联的uuid
     * @param suffix 后缀（允许为null）
     * @param fileUrl 文件地址（允许为null）
     * @param fileBase64 文件base64内容（允许为null）
     * @return 消息对象
     */
    public Message modifyStorageFile(final String uuid, final AssociateType associateType, final String associateUuid, final Suffix suffix, final String fileUrl, final String fileBase64) {
        final Message msg = new Message();
        try {
            String oldUrl = null;
            // 是否存在存储文件
            {
                final Message resultMsg = this.getStorageFile(uuid, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("STORAGE_FILE_NOT_EXIST");
                    msg.setAttach("存储文件不存在");
                    return msg;
                }
                oldUrl = array.getJSONObject(0).getString("url");
            }
            // 是否存在存储文件
            {
                if (AssociateType.PROBLEM == associateType) {
                    if (null != associateUuid) {
                        final Problem obj = new Problem(this.connection);
                        if (!obj.isExistProblem(associateUuid, null, null, null, null)) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("PROBLEM_NOT_EXIST");
                            msg.setAttach("问题不存在");
                            return msg;
                        }
                    }
                } else if (AssociateType.FEEDBACK == associateType) {
                    if (null != associateUuid) {
                        final Feedback obj = new Feedback(this.connection);
                        if (!obj.isExistFeedback(associateUuid, null, null, null)) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("FEEDBACK_NOT_EXIST");
                            msg.setAttach("反馈不存在");
                            return msg;
                        }
                    }
                }
            }
            // 修改存储文件
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "' and `remove_timestamp` is null";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != associateUuid) {
                        hm.put("associate_uuid", associateUuid);
                    }
                    if (null != suffix) {
                        hm.put("suffix", suffix.toString().toLowerCase());
                    }
                    final String saveFileName = StringKit.getUuidStr(true) + "." + suffix.toString().toLowerCase();
                    if ((null != fileUrl) || (null != fileBase64)) {
                        hm.put("url", saveFileName);
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_STORAGE_FILE_FAIL");
                        msg.setAttach("修改存储文件失败");
                        return msg;
                    }
                    if (null != fileUrl) {
                        try {
                            HttpKit.downloadFile(fileUrl, Resource.PROBLEM_FILE_OUTPUT_DIR_PATH + saveFileName, null, null, null);
                        } catch (final Exception e) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("DOWNLOAD_FILE_FAIL");
                            msg.setAttach("下载文件失败");
                            return msg;
                        }
                        final File oldFile = new File(Resource.PROBLEM_FILE_OUTPUT_DIR_PATH + oldUrl);
                        if (!oldFile.delete()) {
                            final StorageFileDeleteFailRecord obj = new StorageFileDeleteFailRecord(this.connection);
                            final Message resultMsg = obj.addStorageFileDeleteFailRecord(oldFile.getAbsolutePath());
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                        }
                    } else if (null != fileBase64) {
                        try {
                            final byte[] data = Base64.decode(fileBase64);
                            IoKit.simpleWriteFile(Resource.PROBLEM_FILE_OUTPUT_DIR_PATH + saveFileName, data);
                        } catch (final Exception e) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("SAVE_FILE_FAIL");
                            msg.setAttach("保存文件失败");
                            return msg;
                        }
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
     * 获取存储文件
     * 
     * @param uuid uuid（允许为null）
     * @param associateUuid 关联的uuid（允许为null）
     * @param suffix 后缀（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getStorageFile(final String uuid, final String associateUuid, final Suffix suffix, final Integer offset, final Integer rows) {
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
            if (null != associateUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "associate_uuid");
                obj.put("symbol", "=");
                obj.put("value", associateUuid);
                whereArray.put(obj);
            }
            if (null != suffix) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "suffix");
                obj.put("symbol", "=");
                obj.put("value", suffix.toString().toLowerCase());
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
     * 是否存在存储文件
     * 
     * @param uuid 存储文件的uuid（允许为null）
     * @param associateUuid 关联的uuid（允许为null）
     * @param suffix 后缀（允许为null）
     * @param excludeUuid 排除类别的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistStorageFile(final String uuid, final String associateUuid, final Suffix suffix, final String excludeUuid) throws Exception {
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
            if (null != associateUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "associate_uuid");
                obj.put("symbol", "=");
                obj.put("value", associateUuid);
                whereArray.put(obj);
            }
            if (null != suffix) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "suffix");
                obj.put("symbol", "=");
                obj.put("value", suffix.toString().toLowerCase());
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