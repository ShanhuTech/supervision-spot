package supervision.spot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.string.StringKit;
import security.dao.Admin;

/**
 * 问题
 */
public class Problem {
    // 数据库表名
    public static String DATABASE_TABLE_NAME = "svp_problem";

    // 类型
    public static enum Type {
        PERSON, // 个人
        DEPARTMENT // 单位
    }

    // 是否已读
    public static enum Read {
        UN_READ, // 未读
        READ // 已读
    }

    // 是否属实
    public static enum Fact {
        UN_CONFIR, // 未确认
        NOT_FACT, // 失实
        FACT // 属实
    }

    // 状态
    public static enum Status {
        UN_FEEDBACK, // 未反馈
        FEEDBACK, // 已反馈
        FILED, // 已归档
    }

    // 统计单位
    public static enum StatisticsUnit {
        DAY, // 日
        WEEK, // 周
        MONTH // 月
    }

    // 问题类型
    public static enum ProblemType {
        SELF, // 自查问题
        CHECK, // 检查问题
        VIDEO // 视频问题
    }

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public Problem(final Connection connection) {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加问题
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
    public Message addProblem(final Type type, final String fromUuid, final String toUuid, final String typeUuid, final String content, final String[][] files, final String[] responsibleUuidArray,
        final ProblemType problemType, final String fromDepartmentUuid, final String toDepartmentUuid, Long createTimestamp) {
        final Message msg = new Message();
        try {
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
            String responsibleUuids = null;
            // 是否存在问题负责人
            {
                final AdminInfo obj = new AdminInfo(this.connection);
                if (null != responsibleUuidArray) {
                    for (int i = 0; i < responsibleUuidArray.length; i++) {
                        final String responsibleUuid = responsibleUuidArray[i];
                        if (!obj.isExistAdminInfo(responsibleUuid, null)) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("RESPONSIBLE_NOT_EXIST");
                            msg.setAttach("负责人不存在");
                            return msg;
                        }
                        if (null == responsibleUuids) {
                            responsibleUuids = "";
                        }
                        responsibleUuids += responsibleUuid + ";";
                    }
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
            // String problemDepartmentTypeName = null;
            // 是否存在问题单位类型
            {
                final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                final Message resultMsg = obj.getProblemDepartmentType(typeUuid, null, null, null);
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
                // problemDepartmentTypeName = array.getJSONObject(0).getString("name");
            }
            if (Type.PERSON == type) {
                // // 2023-03-10：去嗲了问题个人类型，但这里的个人问题分值扣分机制未来可能会用，但目前单位问题类型没有设置分值字段。
                // // 所以，如果要实现个人分值扣分，需要增加问题单位类型分值字段。这里保留代码。
                // String problemPersonTypeName = null;
                // Integer problemPersonTypeScore = null;
                // // 是否存在问题个人类型（提前判断）
                // {
                // final ProblemPersonType obj = new ProblemPersonType(this.connection);
                // final Message resultMsg = obj.getProblemPersonType(typeUuid, null);
                // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                // return resultMsg;
                // }
                // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                // if (0 >= array.length()) {
                // msg.setStatus(Message.Status.ERROR);
                // msg.setContent("PROBLEM_PERSON_TYPE_NOT_EXIST");
                // msg.setAttach("问题个人类型不存在");
                // return msg;
                // }
                // problemPersonTypeName = array.getJSONObject(0).getString("name");
                // problemPersonTypeScore = Integer.valueOf(array.getJSONObject(0).getInt("score"));
                // }
                String name = null;
                String fullName = null;
                String personDepartmentUuid = null;
                Integer problemCount = null;
                // Integer adminScore = null;
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
                    name = array.getJSONObject(0).getString("name");
                    fullName = array.getJSONObject(0).getString("full_name");
                    personDepartmentUuid = array.getJSONObject(0).getString("department_uuid");
                    problemCount = Integer.valueOf(array.getJSONObject(0).getInt("problem_count"));
                    // adminScore = Integer.valueOf(array.getJSONObject(0).getInt("score"));
                }
                // // 通知本人
                // {
                // final supervision.spot.dao.Message message = new supervision.spot.dao.Message(this.connection);
                // final Message resultMsg = message.addMessage("0", toUuid, "督察通报", String.format("警号：%s 姓名：%s 触发了%s问题，特此通知。", name, fullName, problemDepartmentTypeName), null);
                // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                // return resultMsg;
                // }
                // }
                // 2023-03-10：暂不扣分，代码保留。
                // // 本人扣分
                // {
                // final AdminInfo obj = new AdminInfo(this.connection);
                // final Message resultMsg = obj.modifyAdmin(toUuid, null, null, null, null, null, null, null, null, null, null, Integer.valueOf(adminScore.intValue() - problemPersonTypeScore.intValue()), null, null);
                // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                // return resultMsg;
                // }
                // }
                Integer personProblemTriggerReportCount = null;
                // 获取系统配置
                {
                    final SystemConfig obj = new SystemConfig(this.connection);
                    final Message resultMsg = obj.getSystemConfig();
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("SYSTEM_CONFIG_NOT_EXIST");
                        msg.setAttach("系统配置不存在");
                        return msg;
                    }
                    personProblemTriggerReportCount = Integer.valueOf(array.getJSONObject(0).getInt("person_problem_trigger_report_count"));
                }
                // 用户的问题计数是否满足触发条件
                if ((problemCount.intValue() + 1) >= personProblemTriggerReportCount.intValue()) {
                    final ArrayList<String> managerList = new ArrayList<>();
                    // 根据管理员单位的uuid获取领导的列表
                    {
                        final AdminInfo obj = new AdminInfo(this.connection);
                        final Message resultMsg = obj.getAdminInfo(null, null, null, null, null, null, personDepartmentUuid, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject admin = array.getJSONObject(i);
                            managerList.add(admin.getString("uuid"));
                        }
                    }
                    // 如果存在一个或多个领导，才能触发通知。
                    for (int i = 0; i < managerList.size(); i++) {
                        final supervision.spot.dao.Message message = new supervision.spot.dao.Message(this.connection);
                        final Message resultMsg = message.addMessage("0", managerList.get(i), null, "督察通报",
                            String.format("警号：%s 姓名：%s 因个人问题触发累计超过%d次，特此通知相关领导。", name, fullName, personProblemTriggerReportCount), toUuid/* 备注记录问题人的uuid，便于统计该用户是否向领导发送消息的最后时间 */);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                    }
                    // 用户问题计数清零
                    {
                        final AdminInfo obj = new AdminInfo(this.connection);
                        final Message resultMsg = obj.modifyAdmin(toUuid, null, null, null, null, null, null, null, null, null, Integer.valueOf(0), null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                    }
                } else {
                    // 用户问题计数加1
                    {
                        final AdminInfo obj = new AdminInfo(this.connection);
                        final Message resultMsg = obj.modifyAdmin(toUuid, null, null, null, null, null, null, null, null, null, Integer.valueOf(problemCount.intValue() + 1), null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                    }
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
            // 添加问题
            final String uuid = StringKit.getUuidStr(true);
            createTimestamp = (null != createTimestamp) ? createTimestamp : Long.valueOf(System.currentTimeMillis());
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp.longValue()));
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "number", "type", "from_uuid", "to_uuid", "type_uuid", "content", "is_read", "is_fact",
                        "status", "responsible_uuid", "problem_type", "from_department_uuid", "to_department_uuid", "create_timestamp", "create_datetime"));
                    ps.setString(1, uuid);
                    ps.setLong(2, this.getMaxNumber() + 1);
                    ps.setString(3, type.toString());
                    ps.setString(4, fromUuid);
                    ps.setString(5, toUuid);
                    ps.setString(6, typeUuid);
                    ps.setString(7, content);
                    ps.setString(8, Read.UN_READ.toString());
                    ps.setString(9, Fact.UN_CONFIR.toString());
                    ps.setString(10, Status.UN_FEEDBACK.toString());
                    ps.setString(11, responsibleUuids);
                    ps.setString(12, problemType.toString());
                    ps.setString(13, fromDepartmentUuid);
                    ps.setString(14, toDepartmentUuid);
                    ps.setLong(15, createTimestamp.longValue());
                    ps.setString(16, createDatetime);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_PROBLEM_FAIL");
                        msg.setAttach("添加问题失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            // 添加存储文件
            {
                if ((null != files) && (0 < files.length)) {
                    for (int i = 0; i < files.length; i++) {
                        final String[] file = files[i];
                        final String fileContent = file[0];
                        final String fileSuffix = file[1];
                        final StorageFile obj = new StorageFile(this.connection);
                        final Message resultMsg = obj.addStorageFile(StorageFile.AssociateType.PROBLEM, uuid, StorageFile.Suffix.valueOf(fileSuffix), null, fileContent);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
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
     * 添加问题
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
    public Message addProblem(final String uuid, final Type type, final String fromUuid, final String toUuid, final String typeUuid, final String content, final String[][] files,
        final String[] responsibleUuidArray, final ProblemType problemType, final String fromDepartmentUuid, final String toDepartmentUuid, Long createTimestamp) {
        final Message msg = new Message();
        try {
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
            String responsibleUuids = null;
            // 是否存在问题负责人
            {
                final AdminInfo obj = new AdminInfo(this.connection);
                if (null != responsibleUuidArray) {
                    for (int i = 0; i < responsibleUuidArray.length; i++) {
                        final String responsibleUuid = responsibleUuidArray[i];
                        if (!obj.isExistAdminInfo(responsibleUuid, null)) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("RESPONSIBLE_NOT_EXIST");
                            msg.setAttach("负责人不存在");
                            return msg;
                        }
                        if (null == responsibleUuids) {
                            responsibleUuids = "";
                        }
                        responsibleUuids += responsibleUuid + ";";
                    }
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
            // String problemDepartmentTypeName = null;
            // 是否存在问题单位类型
            {
                final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                final Message resultMsg = obj.getProblemDepartmentType(typeUuid, null, null, null);
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
                // problemDepartmentTypeName = array.getJSONObject(0).getString("name");
            }
            if (Type.PERSON == type) {
                // // 2023-03-10：去嗲了问题个人类型，但这里的个人问题分值扣分机制未来可能会用，但目前单位问题类型没有设置分值字段。
                // // 所以，如果要实现个人分值扣分，需要增加问题单位类型分值字段。这里保留代码。
                // String problemPersonTypeName = null;
                // Integer problemPersonTypeScore = null;
                // // 是否存在问题个人类型（提前判断）
                // {
                // final ProblemPersonType obj = new ProblemPersonType(this.connection);
                // final Message resultMsg = obj.getProblemPersonType(typeUuid, null);
                // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                // return resultMsg;
                // }
                // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                // if (0 >= array.length()) {
                // msg.setStatus(Message.Status.ERROR);
                // msg.setContent("PROBLEM_PERSON_TYPE_NOT_EXIST");
                // msg.setAttach("问题个人类型不存在");
                // return msg;
                // }
                // problemPersonTypeName = array.getJSONObject(0).getString("name");
                // problemPersonTypeScore = Integer.valueOf(array.getJSONObject(0).getInt("score"));
                // }
                String name = null;
                String fullName = null;
                String personDepartmentUuid = null;
                Integer problemCount = null;
                // Integer adminScore = null;
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
                    name = array.getJSONObject(0).getString("name");
                    fullName = array.getJSONObject(0).getString("full_name");
                    personDepartmentUuid = array.getJSONObject(0).getString("department_uuid");
                    problemCount = Integer.valueOf(array.getJSONObject(0).getInt("problem_count"));
                    // adminScore = Integer.valueOf(array.getJSONObject(0).getInt("score"));
                }
                // // 通知本人
                // {
                // final supervision.spot.dao.Message message = new supervision.spot.dao.Message(this.connection);
                // final Message resultMsg = message.addMessage("0", toUuid, "督察通报", String.format("警号：%s 姓名：%s 触发了%s问题，特此通知。", name, fullName, problemDepartmentTypeName), null);
                // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                // return resultMsg;
                // }
                // }
                // 2023-03-10：暂不扣分，代码保留。
                // // 本人扣分
                // {
                // final AdminInfo obj = new AdminInfo(this.connection);
                // final Message resultMsg = obj.modifyAdmin(toUuid, null, null, null, null, null, null, null, null, null, null, Integer.valueOf(adminScore.intValue() - problemPersonTypeScore.intValue()), null, null);
                // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                // return resultMsg;
                // }
                // }
                Integer personProblemTriggerReportCount = null;
                // 获取系统配置
                {
                    final SystemConfig obj = new SystemConfig(this.connection);
                    final Message resultMsg = obj.getSystemConfig();
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("SYSTEM_CONFIG_NOT_EXIST");
                        msg.setAttach("系统配置不存在");
                        return msg;
                    }
                    personProblemTriggerReportCount = Integer.valueOf(array.getJSONObject(0).getInt("person_problem_trigger_report_count"));
                }
                // 用户的问题计数是否满足触发条件
                if ((problemCount.intValue() + 1) >= personProblemTriggerReportCount.intValue()) {
                    final ArrayList<String> managerList = new ArrayList<>();
                    // 根据管理员单位的uuid获取领导的列表
                    {
                        final AdminInfo obj = new AdminInfo(this.connection);
                        final Message resultMsg = obj.getAdminInfo(null, null, null, null, null, null, personDepartmentUuid, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject admin = array.getJSONObject(i);
                            managerList.add(admin.getString("uuid"));
                        }
                    }
                    // 如果存在一个或多个领导，才能触发通知。
                    for (int i = 0; i < managerList.size(); i++) {
                        final supervision.spot.dao.Message message = new supervision.spot.dao.Message(this.connection);
                        final Message resultMsg = message.addMessage("0", managerList.get(i), null, "督察通报",
                            String.format("警号：%s 姓名：%s 因个人问题触发累计超过%d次，特此通知相关领导。", name, fullName, personProblemTriggerReportCount), toUuid/* 备注记录问题人的uuid，便于统计该用户是否向领导发送消息的最后时间 */);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                    }
                    // 用户问题计数清零
                    {
                        final AdminInfo obj = new AdminInfo(this.connection);
                        final Message resultMsg = obj.modifyAdmin(toUuid, null, null, null, null, null, null, null, null, null, Integer.valueOf(0), null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                    }
                } else {
                    // 用户问题计数加1
                    {
                        final AdminInfo obj = new AdminInfo(this.connection);
                        final Message resultMsg = obj.modifyAdmin(toUuid, null, null, null, null, null, null, null, null, null, Integer.valueOf(problemCount.intValue() + 1), null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                    }
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
            // 添加问题
            createTimestamp = (null != createTimestamp) ? createTimestamp : Long.valueOf(System.currentTimeMillis());
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp.longValue()));
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "number", "type", "from_uuid", "to_uuid", "type_uuid", "content", "is_read", "is_fact",
                        "status", "responsible_uuid", "problem_type", "from_department_uuid", "to_department_uuid", "create_timestamp", "create_datetime"));
                    ps.setString(1, uuid);
                    ps.setLong(2, this.getMaxNumber() + 1);
                    ps.setString(3, type.toString());
                    ps.setString(4, fromUuid);
                    ps.setString(5, toUuid);
                    ps.setString(6, typeUuid);
                    ps.setString(7, content);
                    ps.setString(8, Read.UN_READ.toString());
                    ps.setString(9, Fact.UN_CONFIR.toString());
                    ps.setString(10, Status.UN_FEEDBACK.toString());
                    ps.setString(11, responsibleUuids);
                    ps.setString(12, problemType.toString());
                    ps.setString(13, fromDepartmentUuid);
                    ps.setString(14, toDepartmentUuid);
                    ps.setLong(15, createTimestamp.longValue());
                    ps.setString(16, createDatetime);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_PROBLEM_FAIL");
                        msg.setAttach("添加问题失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            // 添加存储文件
            {
                if ((null != files) && (0 < files.length)) {
                    for (int i = 0; i < files.length; i++) {
                        final String[] file = files[i];
                        // final String fileContent = file[0];
                        final String fileUrl = file[0];
                        final String fileSuffix = file[1];
                        final StorageFile obj = new StorageFile(this.connection);
                        final Message resultMsg = obj.addStorageFile(StorageFile.AssociateType.PROBLEM, uuid, StorageFile.Suffix.valueOf(fileSuffix), fileUrl, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
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
     * 根据uuid删除问题
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid uuid
     * @return 消息对象
     */
    public Message removeProblemByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在问题
            {
                if (checkExist) {
                    if (!this.isExistProblem(uuid, null, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_NOT_EXIST");
                        msg.setAttach("问题不存在");
                        return msg;
                    }
                }
            }
            // 删除问题
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
                        msg.setAttach("删除问题失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            // 删除关联
            // svp_feedback
            {
                final supervision.spot.dao.Feedback obj = new supervision.spot.dao.Feedback(this.connection);
                if (obj.isExistFeedback(null, uuid, null, null)) {
                    final Message resultMsg = obj.removeFeedbackByProblemUuid(uuid);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                }
            }
            // svp_comment
            {
                final supervision.spot.dao.Comment obj = new supervision.spot.dao.Comment(this.connection);
                if (obj.isExistComment(null, uuid, null, null)) {
                    final Message resultMsg = obj.removeCommentByProblemUuid(uuid);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                }
            }
            // svp_storage-file
            {
                final StorageFile obj = new StorageFile(this.connection);
                if (obj.isExistStorageFile(null, uuid, null, null)) {
                    final Message resultMsg = obj.removeStorageFileByAssociateUuid(uuid);
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
     * 根据uuid删除问题
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid uuid
     * @return 消息对象
     */
    public Message deleteProblemByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在问题
            {
                if (checkExist) {
                    if (!this.isExistProblem(uuid, null, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_NOT_EXIST");
                        msg.setAttach("问题不存在");
                        return msg;
                    }
                }
            }
            // 删除问题
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
                        msg.setAttach("删除问题失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            // 删除关联
            // svp_feedback
            {
                final supervision.spot.dao.Feedback obj = new supervision.spot.dao.Feedback(this.connection);
                if (obj.isExistFeedback(null, uuid, null, null)) {
                    final Message resultMsg = obj.removeFeedbackByProblemUuid(uuid);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                }
            }
            // svp_storage-file
            {
                final StorageFile obj = new StorageFile(this.connection);
                if (obj.isExistStorageFile(null, uuid, null, null)) {
                    final Message resultMsg = obj.removeStorageFileByAssociateUuid(uuid);
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
     * 修改问题（至少修改一项字段）
     * 
     * @param uuid 问题的uuid
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
    public Message modifyProblem(final String uuid, final String fromUuid, final String toUuid, final String typeUuid, final String content, final Read read, final Fact fact, final Status status,
        final String[] responsibleUuidArray, final String isUnfeedback24hNotify) {
        final Message msg = new Message();
        try {
            Type type = null;
            // 是否存在问题
            {
                final Message resultMsg = this.getProblem(uuid, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("PROBLEM_NOT_EXIST");
                    msg.setAttach("问题不存在");
                    return msg;
                }
                type = Type.valueOf(array.getJSONObject(0).getString("type"));
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
                // 是否存在问题个人类型
                {
                    final ProblemPersonType obj = new ProblemPersonType(this.connection);
                    if (!obj.isExistProblemPersonType(typeUuid, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_PERSON_TYPE_NOT_EXIST");
                        msg.setAttach("问题个人类型不存在");
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
                // 是否存在问题单位类型
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    if (!obj.isExistProblemDepartmentType(typeUuid, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_DEPARTMENT_TYPE_NOT_EXIST");
                        msg.setAttach("问题单位类型不存在");
                        return msg;
                    }
                }
            }
            // 修改问题
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "' and `remove_timestamp` is null";
                    final HashMap<String, Object> hm = new HashMap<>();
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
                    if (null != read) {
                        hm.put("is_read", read.toString());
                        if (Read.READ == read) {
                            final long currentTimestamp = System.currentTimeMillis();
                            final String currentDatetime = this.simpleDateFormat.format(new Date(currentTimestamp));
                            hm.put("read_timestamp", Long.valueOf(currentTimestamp));
                            hm.put("read_datetime", currentDatetime);
                        }
                    }
                    if (null != fact) {
                        hm.put("is_fact", fact.toString());
                    }
                    if (null != status) {
                        hm.put("status", status.toString());
                    }
                    if (null != isUnfeedback24hNotify) {
                        hm.put("is_unfeedback_24h_notify", isUnfeedback24hNotify);
                    }
                    if (null != responsibleUuidArray) {
                        if (responsibleUuidArray[0].equalsIgnoreCase("clear")) {
                            hm.put("responsible_uuid", null);
                        } else {
                            String responsibleUuids = null;
                            // 是否存在问题负责人
                            {
                                final AdminInfo obj = new AdminInfo(this.connection);
                                if (null != responsibleUuidArray) {
                                    for (int i = 0; i < responsibleUuidArray.length; i++) {
                                        final String responsibleUuid = responsibleUuidArray[i];
                                        if (!obj.isExistAdminInfo(responsibleUuid, null)) {
                                            msg.setStatus(Message.Status.ERROR);
                                            msg.setContent("RESPONSIBLE_NOT_EXIST");
                                            msg.setAttach("负责人不存在");
                                            return msg;
                                        }
                                        if (null == responsibleUuids) {
                                            responsibleUuids = "";
                                        }
                                        responsibleUuids += responsibleUuid + ";";
                                    }
                                }
                            }
                            hm.put("responsible_uuid", responsibleUuids);
                        }
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_PROBLEM_FAIL");
                        msg.setAttach("修改问题失败");
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
     * 获取问题
     * 
     * @param uuid uuid（允许为null）
     * @param number 编号（null）
     * @param type 类型（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param toUuid 目标的uuid（允许为null）
     * @param typeUuid 类型的uuid（允许为null）
     * @param typeUuidArray 类型的uuid数组（允许为null）
     * @param read 是否已读（允许为null）
     * @param fact 是否属实（允许为null）
     * @param status 状态（允许为null）
     * @param startCreateDatetime 开始创建时间（允许为null）
     * @param endCreateDatetime 结束创建时间（允许为null）
     * @param startReadDatetime 开始已读时间（允许为null）
     * @param endReadDatetime 结束已读时间（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    private Message getProblem(final String uuid, final Long number, final Type type, final String fromUuid, final String toUuid, final String typeUuid, final String[] typeUuidArray, final Read read,
        final Fact fact, final Status status, final String startCreateDatetime, final String endCreateDatetime, final String startReadDatetime, final String endReadDatetime, final Integer offset,
        final Integer rows) {
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
            if (null != number) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "number");
                obj.put("symbol", "=");
                obj.put("value", number.longValue());
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
                // String str = "(";
                // for (int i = 0; i < typeUuidArray.length; i++) {
                // final String uid = typeUuidArray[i];
                // str += "'" + uid + "',";
                // }
                // str = str.substring(0, str.length() - 1);
                // str += ")";
                // final JSONObject obj = new JSONObject();
                // obj.put("condition", "and");
                // obj.put("name", "type_uuid");
                // obj.put("symbol", "in");
                // obj.put("value", str);
                // whereArray.put(obj);
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "type_uuid");
                obj.put("symbol", "in");
                obj.put("value", typeUuidArray);
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
            if (null != fact) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "is_fact");
                obj.put("symbol", "=");
                obj.put("value", fact.toString());
                whereArray.put(obj);
            }
            if (null != status) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "status");
                obj.put("symbol", "=");
                obj.put("value", status.toString());
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
                            if (columnLabel.equalsIgnoreCase("uuid")) {
                                // 获取问题相关存储文件
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
                                    if (null != parentUuid) {
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
                        }
                        if (obj.has("type") && obj.has("type_uuid")) {
                            // 获取type_uuid的关联信息
                            if (Type.PERSON == Type.valueOf(obj.getString("type"))) {
                                final ProblemPersonType ppt = new ProblemPersonType(this.connection);
                                final Message resultMsg = ppt.getProblemPersonType(obj.getString("type_uuid"), null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                    final JSONObject pptObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                    obj.put("problem_person_type_name", pptObj.getString("name"));
                                }
                            } else if (Type.DEPARTMENT == Type.valueOf(obj.getString("type"))) {
                                String parentUuid = null;
                                // 获取该问题单位类型的名称
                                {
                                    final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                    final Message resultMsg = pdt.getProblemDepartmentType(obj.getString("type_uuid"), null, null, null);
                                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                        return resultMsg;
                                    }
                                    if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                        final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                        obj.put("problem_department_type_name", pdtObj.getString("name"));
                                        if (pdtObj.has("parent_uuid") && (null != pdtObj.getString("parent_uuid")) && (0 < pdtObj.getString("parent_uuid").length())) {
                                            parentUuid = pdtObj.getString("parent_uuid");
                                        }
                                    }
                                }
                                // 获取该问题单位类型的父级名称
                                {
                                    if (null != parentUuid) {
                                        final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                        final Message resultMsg = pdt.getProblemDepartmentType(parentUuid, null, null, null);
                                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                            return resultMsg;
                                        }
                                        if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                            final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                            obj.put("problem_department_type_parent_name", pdtObj.getString("name"));
                                        }
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
     * 获取问题数量
     * 
     * @param statisticsUnit 统计单位
     * @param fromUuid 来源的uuid（允许为null）
     * @param toUuid 目标的uuid（允许为null）
     * @param typeNameLike 类型名称的模糊查询（允许为null）
     * @param typeUuidArray 类型的uuid数组（允许为null）
     * @param read 是否已读（允许为null）
     * @param fact 是否属实（允许为null）
     * @param status 状态（允许为null）
     * @param startCreateDatetime 开始创建时间（允许为null）
     * @param endCreateDatetime 结束创建时间（允许为null）
     * @param startReadDatetime 开始已读时间（允许为null）
     * @param endReadDatetime 结束已读时间（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getProblemCountStatistics(final StatisticsUnit statisticsUnit, final Type type, final String fromUuid, final String toUuid, final String typeNameLike, final String[] typeUuidArray,
        final Read read, final Fact fact, final Status status, final String startCreateDatetime, final String endCreateDatetime, final String startReadDatetime, final String endReadDatetime) {
        final Message msg = new Message();
        try {
            final JSONArray whereArray = new JSONArray();
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
            if (null != typeNameLike) {
                final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                final Message resultMsg = pdt.getProblemDepartmentTypeByNameLike(typeNameLike);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final ArrayList<String> typeUuidList = new ArrayList<>();
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                for (int i = 0; i < array.length(); i++) {
                    final JSONObject jo = array.getJSONObject(i);
                    typeUuidList.add(jo.getString("uuid"));
                }
                {
                    // String str = "(";
                    // for (int i = 0; i < typeUuidList.size(); i++) {
                    // final String uuid = typeUuidList.get(i);
                    // str += "'" + uuid + "',";
                    // }
                    // str = str.substring(0, str.length() - 1);
                    // str += ")";
                    // final JSONObject obj = new JSONObject();
                    // obj.put("condition", "and");
                    // obj.put("name", "type_uuid");
                    // obj.put("symbol", "in");
                    // obj.put("value", str);
                    // whereArray.put(obj);
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("name", "type_uuid");
                    obj.put("symbol", "in");
                    obj.put("value", typeUuidList.toArray(new String[typeUuidList.size()]));
                    whereArray.put(obj);
                }
            }
            if (null != typeUuidArray) {
                // String str = "(";
                // for (int i = 0; i < typeUuidArray.length; i++) {
                // final String uuid = typeUuidArray[i];
                // str += "'" + uuid + "',";
                // }
                // str = str.substring(0, str.length() - 1);
                // str += ")";
                // final JSONObject obj = new JSONObject();
                // obj.put("condition", "and");
                // obj.put("name", "type_uuid");
                // obj.put("symbol", "in");
                // obj.put("value", str);
                // whereArray.put(obj);
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "type_uuid");
                obj.put("symbol", "in");
                obj.put("value", typeUuidArray);
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
            if (null != fact) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "is_fact");
                obj.put("symbol", "=");
                obj.put("value", fact.toString());
                whereArray.put(obj);
            }
            if (null != status) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "status");
                obj.put("symbol", "=");
                obj.put("value", status.toString());
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
            final JSONArray array = new JSONArray();
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("select `create_timestamp`, `create_datetime` from `" + DATABASE_TABLE_NAME + "` " + whereSql + " order by `create_timestamp` asc");
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        final long createTimestamp = rs.getLong("create_timestamp");
                        final String createDatetime = rs.getString("create_datetime");
                        if (StatisticsUnit.DAY == statisticsUnit) {
                            if (0 < array.length()) {
                                boolean isFind = false;
                                for (int i = 0; i < array.length(); i++) {
                                    final JSONObject obj = array.getJSONObject(i);
                                    final String key = createDatetime.substring(0, createDatetime.indexOf(" "));
                                    if (obj.getString("datetime").equalsIgnoreCase(key)) {
                                        obj.put("count", obj.getInt("count") + 1);
                                        isFind = true;
                                        break;
                                    }
                                }
                                if (!isFind) {
                                    final JSONObject obj = new JSONObject();
                                    obj.put("datetime", createDatetime.substring(0, createDatetime.indexOf(" ")));
                                    obj.put("count", 1);
                                    array.put(obj);
                                }
                            } else {
                                final JSONObject obj = new JSONObject();
                                obj.put("datetime", createDatetime.substring(0, createDatetime.indexOf(" ")));
                                obj.put("count", 1);
                                array.put(obj);
                            }
                        } else if (StatisticsUnit.WEEK == statisticsUnit) {
                            final Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(createTimestamp);
                            cal.setFirstDayOfWeek(Calendar.MONDAY);
                            if (0 < array.length()) {
                                boolean isFind = false;
                                for (int i = 0; i < array.length(); i++) {
                                    final JSONObject obj = array.getJSONObject(i);
                                    final String key = String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
                                    if (obj.getString("datetime").equalsIgnoreCase(key)) {
                                        obj.put("count", obj.getInt("count") + 1);
                                        isFind = true;
                                        break;
                                    }
                                }
                                if (!isFind) {
                                    final JSONObject obj = new JSONObject();
                                    obj.put("datetime", String.valueOf(cal.get(Calendar.WEEK_OF_YEAR)));
                                    obj.put("count", 1);
                                    array.put(obj);
                                }
                            } else {
                                final JSONObject obj = new JSONObject();
                                obj.put("datetime", String.valueOf(cal.get(Calendar.WEEK_OF_YEAR)));
                                obj.put("count", 1);
                                array.put(obj);
                            }
                        } else if (StatisticsUnit.MONTH == statisticsUnit) {
                            if (0 < array.length()) {
                                boolean isFind = false;
                                for (int i = 0; i < array.length(); i++) {
                                    final JSONObject obj = array.getJSONObject(i);
                                    final String key = createDatetime.substring(0, 7);
                                    if (obj.getString("datetime").equalsIgnoreCase(key)) {
                                        obj.put("count", obj.getInt("count") + 1);
                                        isFind = true;
                                        break;
                                    }
                                }
                                if (!isFind) {
                                    final JSONObject obj = new JSONObject();
                                    obj.put("datetime", createDatetime.substring(0, 7));
                                    obj.put("count", 1);
                                    array.put(obj);
                                }
                            } else {
                                final JSONObject obj = new JSONObject();
                                obj.put("datetime", createDatetime.substring(0, 7));
                                obj.put("count", 1);
                                array.put(obj);
                            }
                        }
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
     * 获取问题
     * 
     * @param fromUuid 来源的uuid
     * @param toUuid 目标的uuid
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    @SuppressWarnings("unused")
    private Message getProblemByFromUuidOrToUuid(final String fromUuid, final String toUuid, final Integer offset, final Integer rows) {
        final Message msg = new Message();
        try {
            final JSONArray whereArray = new JSONArray();
            if (null != fromUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "from_uuid");
                obj.put("symbol", "=");
                obj.put("value", fromUuid);
                whereArray.put(obj);
            }
            if (null != toUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "to_uuid");
                obj.put("symbol", "=");
                obj.put("value", toUuid);
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
                                // 获取问题相关存储文件
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
                            if (Type.PERSON == Type.valueOf(obj.getString("type"))) {
                                final ProblemPersonType ppt = new ProblemPersonType(this.connection);
                                final Message resultMsg = ppt.getProblemPersonType(obj.getString("type_uuid"), null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                    final JSONObject pptObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                    obj.put("problem_person_type_name", pptObj.getString("name"));
                                }
                            } else if (Type.DEPARTMENT == Type.valueOf(obj.getString("type"))) {
                                String parentUuid = null;
                                // 获取该问题单位类型的名称
                                {
                                    final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                    final Message resultMsg = pdt.getProblemDepartmentType(obj.getString("type_uuid"), null, null, null);
                                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                        return resultMsg;
                                    }
                                    if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                        final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                        obj.put("problem_department_type_name", pdtObj.getString("name"));
                                        if (pdtObj.has("parent_uuid") && (null != pdtObj.getString("parent_uuid")) && (0 < pdtObj.getString("parent_uuid").length())) {
                                            parentUuid = pdtObj.getString("parent_uuid");
                                        }
                                    }
                                }
                                // 获取该问题单位类型的父级名称
                                {
                                    if (null != parentUuid) {
                                        final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                        final Message resultMsg = pdt.getProblemDepartmentType(parentUuid, null, null, null);
                                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                            return resultMsg;
                                        }
                                        if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                            final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                            obj.put("problem_department_type_parent_name", pdtObj.getString("name"));
                                        }
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
     * 获取问题
     * 
     * @param fromUuid 来源的uuid
     * @param toUuid 目标的uuid
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getProblemByFromUuidOrToUuid(final String uuid, final Long number, final String fromUuid, final String toUuid, final String[] typeUuidArray, final Status status, final Integer offset,
        final Integer rows) {
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
            if (null != number) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "number");
                obj.put("symbol", "=");
                obj.put("value", number.longValue());
                whereArray.put(obj);
            }
            if (null != fromUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "from_uuid");
                obj.put("symbol", "=");
                obj.put("value", fromUuid);
                whereArray.put(obj);
            }
            if (null != toUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "to_uuid");
                obj.put("symbol", "=");
                obj.put("value", toUuid);
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
            if (null != status) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "status");
                obj.put("symbol", "=");
                obj.put("value", status.toString());
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
                                // 获取问题相关存储文件
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
                            if (Type.PERSON == Type.valueOf(obj.getString("type"))) {
                                final ProblemPersonType ppt = new ProblemPersonType(this.connection);
                                final Message resultMsg = ppt.getProblemPersonType(obj.getString("type_uuid"), null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                    final JSONObject pptObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                    obj.put("problem_person_type_name", pptObj.getString("name"));
                                }
                            } else if (Type.DEPARTMENT == Type.valueOf(obj.getString("type"))) {
                                String parentUuid = null;
                                // 获取该问题单位类型的名称
                                {
                                    final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                    final Message resultMsg = pdt.getProblemDepartmentType(obj.getString("type_uuid"), null, null, null);
                                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                        return resultMsg;
                                    }
                                    if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                        final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                        obj.put("problem_department_type_name", pdtObj.getString("name"));
                                        if (pdtObj.has("parent_uuid") && (null != pdtObj.getString("parent_uuid")) && (0 < pdtObj.getString("parent_uuid").length())) {
                                            parentUuid = pdtObj.getString("parent_uuid");
                                        }
                                    }
                                }
                                // 获取该问题单位类型的父级名称
                                {
                                    if (null != parentUuid) {
                                        final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                        final Message resultMsg = pdt.getProblemDepartmentType(parentUuid, null, null, null);
                                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                            return resultMsg;
                                        }
                                        if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                            final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                            obj.put("problem_department_type_parent_name", pdtObj.getString("name"));
                                        }
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
     * 获取子部门（父找子）
     * 
     * @param resultList 结果列表
     * @param uuid uuid
     */
    private void getChildrenDepartment(final ArrayList<String> resultList, final String uuid) {
        final Department dep = new Department(this.connection);
        final Message resultMsg = dep.getDepartment(null, uuid, null, null, null, null, null, null, null);
        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            return;
        }
        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
        for (int i = 0; i < array.length(); i++) {
            final JSONObject jo = array.getJSONObject(i);
            resultList.add(jo.getString("uuid"));
            this.getChildrenDepartment(resultList, jo.getString("uuid"));
        }
    }

    /**
     * 获取问题
     * 
     * @param departmentUuidArray 部门的uuid数组
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    @SuppressWarnings("unused")
    private Message getProblemByDepartment(final String[] departmentUuidArray, final Integer offset, final Integer rows) {
        final Message msg = new Message();
        try {
            final ArrayList<String> departmentUuidList = new ArrayList<>();
            // 将部门的uuid加入部门的uuid列表
            for (int i = 0; i < departmentUuidArray.length; i++) {
                final String uuid = departmentUuidArray[i];
                departmentUuidList.add(uuid);
                this.getChildrenDepartment(departmentUuidList, uuid);
            }
            final ArrayList<String> fromUuidList = new ArrayList<>();
            for (int i = 0; i < departmentUuidList.size(); i++) {
                final AdminInfo ai = new AdminInfo(this.connection);
                final Message resultMsg = ai.getAdminInfo(null, null, null, departmentUuidList.get(i), null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                for (int j = 0; j < array.length(); j++) {
                    final JSONObject jo = array.getJSONObject(j);
                    fromUuidList.add(jo.getString("uuid"));
                }
            }
            final JSONArray whereArray = new JSONArray();
            {
                // String str = "(";
                // for (int i = 0; i < fromUuidList.size(); i++) {
                // final String uuid = fromUuidList.get(i);
                // str += "'" + uuid + "',";
                // }
                // str = str.substring(0, str.length() - 1);
                // str += ")";
                // final JSONObject obj = new JSONObject();
                // obj.put("condition", "or");
                // obj.put("name", "from_uuid");
                // obj.put("symbol", "in");
                // obj.put("value", str);
                // whereArray.put(obj);
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "from_uuid");
                obj.put("symbol", "in");
                obj.put("value", fromUuidList.toArray(new String[fromUuidList.size()]));
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
            String whereSql = DatabaseKit.composeWhereSql(whereArray);
            whereSql = whereSql.replaceAll("'\\(", "\\(");
            whereSql = whereSql.replaceAll("\\)'", "\\)");
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
                                // 获取问题相关存储文件
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
                            if (Type.PERSON == Type.valueOf(obj.getString("type"))) {
                                final ProblemPersonType ppt = new ProblemPersonType(this.connection);
                                final Message resultMsg = ppt.getProblemPersonType(obj.getString("type_uuid"), null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                    final JSONObject pptObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                    obj.put("problem_person_type_name", pptObj.getString("name"));
                                }
                            } else if (Type.DEPARTMENT == Type.valueOf(obj.getString("type"))) {
                                String parentUuid = null;
                                // 获取该问题单位类型的名称
                                {
                                    final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                    final Message resultMsg = pdt.getProblemDepartmentType(obj.getString("type_uuid"), null, null, null);
                                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                        return resultMsg;
                                    }
                                    if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                        final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                        obj.put("problem_department_type_name", pdtObj.getString("name"));
                                        if (pdtObj.has("parent_uuid") && (null != pdtObj.getString("parent_uuid")) && (0 < pdtObj.getString("parent_uuid").length())) {
                                            parentUuid = pdtObj.getString("parent_uuid");
                                        }
                                    }
                                }
                                // 获取该问题单位类型的父级名称
                                {
                                    if (null != parentUuid) {
                                        final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                        final Message resultMsg = pdt.getProblemDepartmentType(parentUuid, null, null, null);
                                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                            return resultMsg;
                                        }
                                        if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                            final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                            obj.put("problem_department_type_parent_name", pdtObj.getString("name"));
                                        }
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
            msg.setStatus(Message.Status.SUCCESS);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }
    // /**
    // * 获取问题
    // *
    // * @param adminUuid 管理员的uuid
    // * @param offset 查询的偏移（允许为null）
    // * @param rows 查询的行数（允许为null）
    // * @return 消息对象
    // */
    // public Message getProblemByAuthor(final String adminUuid, final Integer offset, final Integer rows) {
    // final Message msg = new Message();
    // try {
    // String adminDepartmentUuid = null;
    // // 获取管理员部门的uuid
    // {
    // final AdminInfo ai = new AdminInfo(this.connection);
    // final Message resultMsg = ai.getAdminInfo(adminUuid, null, null, null, null, null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
    // if (0 >= array.length()) {
    // msg.setStatus(Message.Status.ERROR);
    // msg.setContent("ADMIN_NOT_EXIST");
    // msg.setAttach("管理员不存在");
    // return msg;
    // }
    // adminDepartmentUuid = array.getJSONObject(0).getString("department_uuid");
    // }
    // String lv2DepUuid = null;
    // // 获取管理员所在Lv2单位的uuid
    // {
    // final Department dep = new Department(this.connection);
    // final Message resultMsg = dep.getDepartment(adminDepartmentUuid, null, null, null, null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
    // if (0 >= array.length()) {
    // msg.setStatus(Message.Status.ERROR);
    // msg.setContent("ADMIN_DEPARTMENT_NOT_EXIST");
    // msg.setAttach("管理员单位不存在");
    // return msg;
    // }
    // if (2 >= array.getJSONObject(0).getInt("level")) {
    // lv2DepUuid = adminDepartmentUuid;
    // }
    // }
    // // 获取管理员所在Lv2单位的uuid
    // {
    // if (null == lv2DepUuid) {
    // final Department dep = new Department(this.connection);
    // final Message resultMsg = dep.getDepartmentParentBriefInfoByUuid(adminDepartmentUuid);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("parent_array");
    // if (0 >= array.length()) {
    // msg.setStatus(Message.Status.ERROR);
    // msg.setContent("ADMIN_DEPARTMENT_NOT_EXIST");
    // msg.setAttach("管理员单位不存在");
    // return msg;
    // }
    // for (int j = 0; j < array.length(); j++) {
    // final JSONObject jo = array.getJSONObject(j);
    // if (2 == jo.getInt("level")) {
    // lv2DepUuid = jo.getString("uuid");
    // break;
    // }
    // }
    // }
    // }
    // if (null == lv2DepUuid) {
    // msg.setStatus(Message.Status.ERROR);
    // msg.setContent("LV2_DEPARTMENT_NOT_FOUND");
    // msg.setAttach("二级单位未找到");
    // return msg;
    // }
    // final String[] departmentUuidArray = new String[] { lv2DepUuid };
    // final ArrayList<String> departmentUuidList = new ArrayList<>();
    // // 将部门的uuid加入部门的uuid列表
    // for (int i = 0; i < departmentUuidArray.length; i++) {
    // final String uuid = departmentUuidArray[i];
    // departmentUuidList.add(uuid);
    // this.getChildrenDepartment(departmentUuidList, uuid);
    // }
    // final ArrayList<String> fromUuidList = new ArrayList<>();
    // for (int i = 0; i < departmentUuidList.size(); i++) {
    // final AdminInfo ai = new AdminInfo(this.connection);
    // final Message resultMsg = ai.getAdminInfo(null, null, null, departmentUuidList.get(i), null, null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
    // for (int j = 0; j < array.length(); j++) {
    // final JSONObject jo = array.getJSONObject(j);
    // fromUuidList.add(jo.getString("uuid"));
    // }
    // }
    // final JSONArray whereArray = new JSONArray();
    // {
    // String str = "(";
    // str += "'" + adminUuid + "',";
    // for (int i = 0; i < fromUuidList.size(); i++) {
    // final String uuid = fromUuidList.get(i);
    // str += "'" + uuid + "',";
    // }
    // str = str.substring(0, str.length() - 1);
    // str += ")";
    // final JSONObject obj = new JSONObject();
    // obj.put("condition", "or");
    // obj.put("name", "to_uuid");
    // obj.put("symbol", "in");
    // obj.put("value", str);
    // whereArray.put(obj);
    // }
    // // {
    // // final String str = "('" + adminUuid + "')";
    // // final JSONObject obj = new JSONObject();
    // // obj.put("condition", "and");
    // // obj.put("name", "from_uuid");
    // // obj.put("symbol", "in");
    // // obj.put("value", str);
    // // whereArray.put(obj);
    // // }
    // {
    // // 排除逻辑删除
    // final JSONObject obj = new JSONObject();
    // obj.put("condition", "and");
    // obj.put("name", "remove_timestamp");
    // obj.put("symbol", "is");
    // obj.put("value", JSONObject.NULL);
    // whereArray.put(obj);
    // }
    // final JSONObject resultObj = new JSONObject();
    // String whereSql = DatabaseKit.composeWhereSql(whereArray);
    // whereSql = whereSql.replaceAll("'\\(", "\\(");
    // whereSql = whereSql.replaceAll("\\)'", "\\)");
    // // System.out.println(whereSql);
    // {
    // PreparedStatement ps = null;
    // ResultSet rs = null;
    // try {
    // ps = this.connection.prepareStatement("select count(*) as `count` from `" + DATABASE_TABLE_NAME + "` " + whereSql);
    // rs = ps.executeQuery();
    // if (rs.next()) {
    // resultObj.put("count", rs.getInt("count"));
    // } else {
    // resultObj.put("count", 0);
    // }
    // } finally {
    // if (null != rs) {
    // rs.close();
    // }
    // if (null != ps) {
    // ps.close();
    // }
    // }
    // }
    // final JSONArray array = new JSONArray();
    // {
    // PreparedStatement ps = null;
    // ResultSet rs = null;
    // try {
    // String limitCode = "";
    // if ((null != offset) && (null != rows)) {
    // limitCode = "limit ?, ?";
    // }
    // ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "` " + whereSql + " order by `create_timestamp` desc " + limitCode);
    // if ((null != offset) && (null != rows)) {
    // ps.setInt(1, offset.intValue());
    // ps.setInt(2, rows.intValue());
    // }
    // rs = ps.executeQuery();
    // // 根据列名获取所有返回数据
    // final ResultSetMetaData rsmd = rs.getMetaData();
    // final ArrayList<String> columnLabelList = new ArrayList<>();
    // for (int i = 1; i <= rsmd.getColumnCount(); i++) {
    // columnLabelList.add(rsmd.getColumnLabel(i));
    // }
    // while (rs.next()) {
    // final JSONObject obj = new JSONObject();
    // for (int i = 0; i < columnLabelList.size(); i++) {
    // final String columnLabel = columnLabelList.get(i);
    // obj.put(columnLabel, rs.getObject(columnLabel));
    // if (columnLabel.equalsIgnoreCase("uuid")) {
    // // 获取问题相关存储文件
    // {
    // final StorageFile sf = new StorageFile(this.connection);
    // final Message resultMsg = sf.getStorageFile(null, (String) rs.getObject(columnLabel), null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONArray sfArray = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
    // if (0 < sfArray.length()) {
    // obj.put("storage_file", sfArray);
    // }
    // }
    // }
    // }
    // if (obj.has("from_uuid")) {
    // // 获取from_uuid个人的关联信息
    // final AdminInfo ai = new AdminInfo(this.connection);
    // final Message resultMsg = ai.getAdminInfo(obj.getString("from_uuid"), null, null, null, null, null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONObject aiObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
    // obj.put("from_real_name", aiObj.getString("real_name"));
    // obj.put("from_account_name", aiObj.getString("name"));
    // }
    // if (obj.has("type") && obj.has("to_uuid")) {
    // if (Type.PERSON == Type.valueOf(obj.getString("type"))) {
    // // 获取to_uuid个人的关联信息
    // final AdminInfo ai = new AdminInfo(this.connection);
    // final Message resultMsg = ai.getAdminInfo(obj.getString("to_uuid"), null, null, null, null, null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONObject aiObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
    // obj.put("to_real_name", aiObj.getString("real_name"));
    // obj.put("to_account_name", aiObj.getString("name"));
    // } else if (Type.DEPARTMENT == Type.valueOf(obj.getString("type"))) {
    // String parentUuid = null;
    // // 获取to_uuid单位的名称
    // {
    // final Department dep = new Department(this.connection);
    // final Message resultMsg = dep.getDepartment(obj.getString("to_uuid"), null, null, null, null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONObject depObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
    // obj.put("department_name", depObj.getString("name"));
    // if (depObj.has("parent_uuid") && (null != depObj.getString("parent_uuid")) && (1/* 排除parent_uuid为0的情况 */ < depObj.getString("parent_uuid").length())) {
    // parentUuid = depObj.getString("parent_uuid");
    // }
    // }
    // // 获取to_uuid单位父级的名称
    // {
    // final Department dep = new Department(this.connection);
    // final Message resultMsg = dep.getDepartment(parentUuid, null, null, null, null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONObject depObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
    // obj.put("department_parent_name", depObj.getString("name"));
    // }
    // }
    // }
    // if (obj.has("type") && obj.has("type_uuid")) {
    // // 获取type_uuid的关联信息
    // if (Type.PERSON == Type.valueOf(obj.getString("type"))) {
    // final ProblemPersonType ppt = new ProblemPersonType(this.connection);
    // final Message resultMsg = ppt.getProblemPersonType(obj.getString("type_uuid"), null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONObject pptObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
    // obj.put("problem_person_type_name", pptObj.getString("name"));
    // } else if (Type.DEPARTMENT == Type.valueOf(obj.getString("type"))) {
    // String parentUuid = null;
    // // 获取该问题单位类型的名称
    // {
    // final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
    // final Message resultMsg = pdt.getProblemDepartmentType(obj.getString("type_uuid"), null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
    // obj.put("problem_department_type_name", pdtObj.getString("name"));
    // if (pdtObj.has("parent_uuid") && (null != pdtObj.getString("parent_uuid")) && (0 < pdtObj.getString("parent_uuid").length())) {
    // parentUuid = pdtObj.getString("parent_uuid");
    // }
    // }
    // // 获取该问题单位类型的父级名称
    // {
    // if (null != parentUuid) {
    // final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
    // final Message resultMsg = pdt.getProblemDepartmentType(parentUuid, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
    // obj.put("problem_department_type_parent_name", pdtObj.getString("name"));
    // }
    // }
    // }
    // }
    // array.put(obj);
    // }
    // } finally {
    // if (null != rs) {
    // rs.close();
    // }
    // if (null != ps) {
    // ps.close();
    // }
    // }
    // }
    // resultObj.put("array", array);
    // msg.setStatus(Message.Status.SUCCESS);
    // msg.setContent(resultObj);
    // return msg;
    // } catch (final Exception e) {
    // msg.setStatus(Message.Status.EXCEPTION);
    // msg.setContent(StringKit.getExceptionStackTrace(e));
    // return msg;
    // }
    // }

    /**
     * 获取问题
     * 
     * @param adminUuid 管理员的uuid
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getProblemByLeader(final String uuid, final String lv2DepUuid, final Integer offset, final Integer rows) {
        final Message msg = new Message();
        try {
            final ArrayList<String> departmentUuidList = new ArrayList<>();
            departmentUuidList.add(lv2DepUuid);
            this.getChildrenDepartment(departmentUuidList, lv2DepUuid);
            final ArrayList<String> fromUuidList = new ArrayList<>();
            for (int i = 0; i < departmentUuidList.size(); i++) {
                final AdminInfo ai = new AdminInfo(this.connection);
                final Message resultMsg = ai.getAdminInfo(null, null, null, departmentUuidList.get(i), null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                for (int j = 0; j < array.length(); j++) {
                    final JSONObject jo = array.getJSONObject(j);
                    fromUuidList.add(jo.getString("uuid"));
                }
            }
            final JSONArray whereArray = new JSONArray();
            {
                if (null != uuid) {
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("name", "uuid");
                    obj.put("symbol", "=");
                    obj.put("value", uuid);
                    whereArray.put(obj);
                }
            }
            // 单位下所有人发起的问题
            {
                // String str = "(";
                // for (int i = 0; i < fromUuidList.size(); i++) {
                // final String fromUuid = fromUuidList.get(i);
                // str += "'" + fromUuid + "',";
                // }
                // str = str.substring(0, str.length() - 1);
                // str += ")";
                // final JSONObject obj = new JSONObject();
                // obj.put("condition", "or");
                // obj.put("name", "from_uuid");
                // obj.put("symbol", "in");
                // obj.put("value", str);
                // whereArray.put(obj);
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "from_uuid");
                obj.put("symbol", "in");
                obj.put("value", fromUuidList.toArray(new String[fromUuidList.size()]));
                whereArray.put(obj);
            }
            // 给单位的问题
            {
                // final JSONObject obj = new JSONObject();
                // obj.put("condition", "or");
                // obj.put("name", "to_uuid");
                // obj.put("symbol", "in");
                // obj.put("value", String.format("('%s')", lv2DepUuid));
                // whereArray.put(obj);
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "to_uuid");
                obj.put("symbol", "in");
                obj.put("value", new String[] { lv2DepUuid });
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
            String whereSql = DatabaseKit.composeWhereSql(whereArray);
            whereSql = whereSql.replaceAll("'\\(", "\\(");
            whereSql = whereSql.replaceAll("\\)'", "\\)");
            // System.out.println(whereSql);
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
                                // 获取问题相关存储文件
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
                                    if (null != parentUuid) {
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
                        }
                        if (obj.has("type") && obj.has("type_uuid")) {
                            // 获取type_uuid的关联信息
                            if (Type.PERSON == Type.valueOf(obj.getString("type"))) {
                                final ProblemPersonType ppt = new ProblemPersonType(this.connection);
                                final Message resultMsg = ppt.getProblemPersonType(obj.getString("type_uuid"), null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                    final JSONObject pptObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                    obj.put("problem_person_type_name", pptObj.getString("name"));
                                }
                            } else if (Type.DEPARTMENT == Type.valueOf(obj.getString("type"))) {
                                String parentUuid = null;
                                // 获取该问题单位类型的名称
                                {
                                    final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                    final Message resultMsg = pdt.getProblemDepartmentType(obj.getString("type_uuid"), null, null, null);
                                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                        return resultMsg;
                                    }
                                    if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                        final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                        obj.put("problem_department_type_name", pdtObj.getString("name"));
                                        if (pdtObj.has("parent_uuid") && (null != pdtObj.getString("parent_uuid")) && (0 < pdtObj.getString("parent_uuid").length())) {
                                            parentUuid = pdtObj.getString("parent_uuid");
                                        }
                                    }
                                }
                                // 获取该问题单位类型的父级名称
                                {
                                    if (null != parentUuid) {
                                        final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                        final Message resultMsg = pdt.getProblemDepartmentType(parentUuid, null, null, null);
                                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                            return resultMsg;
                                        }
                                        if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                            final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                            obj.put("problem_department_type_parent_name", pdtObj.getString("name"));
                                        }
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
     * 是否存在问题
     * 
     * @param uuid 问题的uuid（允许为null）
     * @param fromUuid 来源的uuid
     * @param toUuid 目标的uuid
     * @param typeUuid 类型的uuid
     * @param excludeUuid 排除类别的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistProblem(final String uuid, final String fromUuid, final String toUuid, final String typeUuid, final String excludeUuid) throws Exception {
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

    /**
     * 获取最大编号
     * 
     * @return 最大编号
     */
    private final long getMaxNumber() throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = this.connection.prepareStatement("select max(`number`) as `max_number` from `" + DATABASE_TABLE_NAME + "`");
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("max_number");
            }
            return 0L;
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
     * 获取问题
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
    public Message getProblemExact(final String uuid, final Long number, final Type type, final String fromUuid, final String toUuid, final String typeUuid, final String[] typeUuidArray, final Read read,
        final Fact fact, final Status status, final String responsibleUuidLike, final ProblemType problemType, final String fromDepartmentUuid, final String toDepartmentUuid, final String orFromUuid,
        final String orToUuid, final String orResponsibleUuidLike, final String orFromDepartmentUuid, final String orToDepartmentUuid, final String responsibleUuidHas, final JSONArray conditionArray,
        final String startCreateDatetime, final String endCreateDatetime, final String startReadDatetime, final String endReadDatetime, final Integer offset, final Integer rows) {
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
            if (null != number) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "number");
                obj.put("symbol", "=");
                obj.put("value", number.longValue());
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
                // String str = "(";
                // for (int i = 0; i < typeUuidArray.length; i++) {
                // final String uid = typeUuidArray[i];
                // str += "'" + uid + "',";
                // }
                // str = str.substring(0, str.length() - 1);
                // str += ")";
                // final JSONObject obj = new JSONObject();
                // obj.put("condition", "and");
                // obj.put("name", "type_uuid");
                // obj.put("symbol", "in");
                // obj.put("value", str);
                // whereArray.put(obj);
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "type_uuid");
                obj.put("symbol", "in");
                obj.put("value", typeUuidArray);
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
            if (null != fact) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "is_fact");
                obj.put("symbol", "=");
                obj.put("value", fact.toString());
                whereArray.put(obj);
            }
            if (null != status) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "status");
                obj.put("symbol", "=");
                obj.put("value", status.toString());
                whereArray.put(obj);
            }
            if (null != responsibleUuidLike) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "responsible_uuid");
                obj.put("symbol", "like");
                obj.put("value", "%" + responsibleUuidLike + "%");
                whereArray.put(obj);
            }
            if (null != problemType) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "problem_type");
                obj.put("symbol", "=");
                obj.put("value", problemType.toString());
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
            if (null != orResponsibleUuidLike) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "or");
                obj.put("name", "responsible_uuid");
                obj.put("symbol", "like");
                obj.put("value", "%" + orResponsibleUuidLike + "%");
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
            if (null != responsibleUuidHas) {
                if (responsibleUuidHas.equalsIgnoreCase("true")) {
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("name", "responsible_uuid");
                    obj.put("symbol", "is not");
                    obj.put("value", JSONObject.NULL);
                    whereArray.put(obj);
                } else {
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("name", "responsible_uuid");
                    obj.put("symbol", "is");
                    obj.put("value", JSONObject.NULL);
                    whereArray.put(obj);
                }
            }
            if (null != conditionArray) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("type", "group");
                obj.put("value", conditionArray);
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
            // String orGroupSql = null;
            // if (null != conditionGroup) {
            // final String[] item = conditionGroup.split(";");
            // orGroupSql = "(";
            // for (int i = 0; i < item.length; i++) {
            // final String column = item[i].split(":")[0];
            // final String value = item[i].split(":")[1];
            // orGroupSql += "`" + column + "` = '" + value + "' or ";
            // }
            // orGroupSql = orGroupSql.substring(0, orGroupSql.length() - 4);
            // orGroupSql += ")";
            // }
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
                                // 获取问题相关存储文件
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
                            if (Type.PERSON == Type.valueOf(obj.getString("type"))) {
                                final ProblemPersonType ppt = new ProblemPersonType(this.connection);
                                final Message resultMsg = ppt.getProblemPersonType(obj.getString("type_uuid"), null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                    final JSONObject pptObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                    obj.put("problem_person_type_name", pptObj.getString("name"));
                                }
                            } else if (Type.DEPARTMENT == Type.valueOf(obj.getString("type"))) {
                                String parentUuid = null;
                                // 获取该问题单位类型的名称
                                {
                                    final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                    final Message resultMsg = pdt.getProblemDepartmentType(obj.getString("type_uuid"), null, null, null);
                                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                        return resultMsg;
                                    }
                                    if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                        final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                        obj.put("problem_department_type_name", pdtObj.getString("name"));
                                        if (pdtObj.has("parent_uuid") && (null != pdtObj.getString("parent_uuid")) && (0 < pdtObj.getString("parent_uuid").length())) {
                                            parentUuid = pdtObj.getString("parent_uuid");
                                        }
                                    }
                                }
                                // 获取该问题单位类型的父级名称
                                {
                                    if (null != parentUuid) {
                                        final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                                        final Message resultMsg = pdt.getProblemDepartmentType(parentUuid, null, null, null);
                                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                            return resultMsg;
                                        }
                                        if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                            final JSONObject pdtObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                            obj.put("problem_department_type_parent_name", pdtObj.getString("name"));
                                        }
                                    }
                                }
                            }
                        }
                        if (obj.has("responsible_uuid")) {
                            final String[] responsibleUuidArray = obj.getString("responsible_uuid").split(";");
                            final JSONArray uuidArray = new JSONArray();
                            final JSONArray realNameArray = new JSONArray();
                            final JSONArray accountNameArray = new JSONArray();
                            for (int i = 0; i < responsibleUuidArray.length; i++) {
                                final String responsibleUuid = responsibleUuidArray[i];
                                // 获取responsible_uuid个人的关联信息
                                final AdminInfo ai = new AdminInfo(this.connection);
                                final Message resultMsg = ai.getAdminInfo(responsibleUuid, null, null, null, null, null, null, null, null, null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                if (0 < ((JSONObject) resultMsg.getContent()).getJSONArray("array").length()) {
                                    final JSONObject aiObj = ((JSONObject) resultMsg.getContent()).getJSONArray("array").getJSONObject(0);
                                    uuidArray.put(responsibleUuid);
                                    realNameArray.put(aiObj.getString("real_name"));
                                    accountNameArray.put(aiObj.getString("name"));
                                }
                            }
                            obj.put("responsible_uuid", uuidArray);
                            obj.put("responsible_real_name", realNameArray);
                            obj.put("responsible_account_name", accountNameArray);
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

    /**
     * 获取警察问题
     * 
     * @return 消息对象
     */
    public Message getProblemByPolice(final String startCreateDatetime, final String endCreateDatetime) {
        final Message msg = new Message();
        try {
            JSONArray adminArray = null;
            {
                final AdminInfo ai = new AdminInfo(this.connection);
                final Message resultMsg = ai.getAdminInfo(null, null, null, null, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                adminArray = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            }
            JSONArray problemArray = null;
            {
                final Message resultMsg = this.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, startCreateDatetime,
                    endCreateDatetime, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                problemArray = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            }
            final ArrayList<JSONObject> minjingList = new ArrayList<>();
            final ArrayList<JSONObject> fujingList = new ArrayList<>();
            for (int i = 0; i < adminArray.length(); i++) {
                final JSONObject admin = adminArray.getJSONObject(i);
                for (int j = 0; j < problemArray.length(); j++) {
                    final JSONObject problem = problemArray.getJSONObject(j);
                    if (problem.has("responsible_uuid") && problem.getJSONArray("responsible_uuid").toString().contains(admin.getString("uuid"))) {
                        if (!admin.has("count_problem")) {
                            admin.put("count_problem", 0);
                        }
                        admin.put("count_problem", admin.getInt("count_problem") + 1);
                    }
                }
                if (admin.has("count_problem")) {
                    if (admin.getString("name").contains("-")) {
                        fujingList.add(admin);
                    } else {
                        minjingList.add(admin);
                    }
                }
            }
            Collections.sort(minjingList, new Comparator<JSONObject>() {
                @Override
                public int compare(final JSONObject obj1, final JSONObject obj2) {
                    return Integer.valueOf(obj2.getInt("count_problem")).compareTo(Integer.valueOf(obj1.getInt("count_problem")));
                }
            });
            Collections.sort(fujingList, new Comparator<JSONObject>() {
                @Override
                public int compare(final JSONObject obj1, final JSONObject obj2) {
                    return Integer.valueOf(obj2.getInt("count_problem")).compareTo(Integer.valueOf(obj1.getInt("count_problem")));
                }
            });
            final JSONArray array = new JSONArray();
            {
                final JSONObject pObj = new JSONObject();
                final JSONArray dataArr = new JSONArray(minjingList);
                pObj.put("name", "minjing");
                pObj.put("list", dataArr);
                array.put(pObj);
            }
            {
                final JSONObject pObj = new JSONObject();
                final JSONArray dataArr = new JSONArray(fujingList);
                pObj.put("name", "fumijing");
                pObj.put("list", dataArr);
                array.put(pObj);
            }
            final JSONObject resultObj = new JSONObject();
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
     * 获取警察问题
     * 
     * @return 消息对象
     */
    public Message getProblemByPoliceFast(final String startCreateDatetime, final String endCreateDatetime) {
        final Message msg = new Message();
        try {
            final JSONArray array = new JSONArray();
            // 辅警top10
            {
                final JSONArray whereArray = new JSONArray();
                {
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("alias", "a");
                    obj.put("name", "name");
                    obj.put("symbol", "like");
                    obj.put("value", "%-%");
                    whereArray.put(obj);
                }
                if (null != startCreateDatetime) {
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("alias", "p");
                    obj.put("name", "create_timestamp");
                    obj.put("symbol", ">");
                    obj.put("value", this.simpleDateFormat.parse(startCreateDatetime).getTime());
                    whereArray.put(obj);
                }
                if (null != endCreateDatetime) {
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("alias", "p");
                    obj.put("name", "create_timestamp");
                    obj.put("symbol", "<");
                    obj.put("value", this.simpleDateFormat.parse(endCreateDatetime).getTime());
                    whereArray.put(obj);
                }
                final String whereSql = DatabaseKit.composeWhereSql(whereArray);
                final JSONArray arr = new JSONArray();
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("SELECT a.uuid, a.name, i.real_name, d.uuid as `dep_uuid`, d.name as `dep_name`, COUNT(*) AS problem_count FROM `" + Admin.DATABASE_TABLE_NAME
                        + "` a INNER JOIN `" + Problem.DATABASE_TABLE_NAME + "` p ON FIND_IN_SET(a.uuid, REPLACE(p.responsible_uuid, ';', ',')) > 0 INNER JOIN `" + AdminInfo.DATABASE_TABLE_NAME
                        + "` i ON a.uuid = i.admin_uuid inner join `" + Department.DATABASE_TABLE_NAME + "` d on i.department_uuid = d.uuid " + whereSql
                        + " GROUP BY a.uuid, a.name, i.real_name, d.uuid, d.name order by `problem_count` desc limit 0, 10");
                    rs = ps.executeQuery();
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        obj.put("uuid", rs.getString("uuid"));
                        obj.put("name", rs.getString("name"));
                        obj.put("real_name", rs.getString("real_name"));
                        obj.put("problem_count", rs.getInt("problem_count"));
                        obj.put("dep_uuid", rs.getString("dep_uuid"));
                        obj.put("dep_name", rs.getString("dep_name"));
                        String lv2Name = null;
                        {
                            Department dep = new Department(this.connection);
                            lv2Name = dep.getParentDepLv2Name(new String[] { rs.getString("dep_uuid") });
                        }
                        if (null != lv2Name) {
                            obj.put("dep_name", lv2Name);
                        }
                        arr.put(obj);
                    }
                } finally {
                    if (null != rs) {
                        rs.close();
                    }
                    if (null != ps) {
                        ps.close();
                    }
                }
                {
                    final JSONObject pObj = new JSONObject();
                    pObj.put("name", "fujing");
                    pObj.put("list", arr);
                    array.put(pObj);
                }
            }
            // 民警top10
            {
                final JSONArray whereArray = new JSONArray();
                {
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("alias", "a");
                    obj.put("name", "name");
                    obj.put("symbol", "not like");
                    obj.put("value", "%-%");
                    whereArray.put(obj);
                }
                if (null != startCreateDatetime) {
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("alias", "p");
                    obj.put("name", "create_timestamp");
                    obj.put("symbol", ">");
                    obj.put("value", this.simpleDateFormat.parse(startCreateDatetime).getTime());
                    whereArray.put(obj);
                }
                if (null != endCreateDatetime) {
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("alias", "p");
                    obj.put("name", "create_timestamp");
                    obj.put("symbol", "<");
                    obj.put("value", this.simpleDateFormat.parse(endCreateDatetime).getTime());
                    whereArray.put(obj);
                }
                final String whereSql = DatabaseKit.composeWhereSql(whereArray);
                final JSONArray arr = new JSONArray();
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("SELECT a.uuid, a.name, i.real_name, d.uuid as `dep_uuid`, d.name as `dep_name`, COUNT(*) AS problem_count FROM `" + Admin.DATABASE_TABLE_NAME
                        + "` a INNER JOIN `" + Problem.DATABASE_TABLE_NAME + "` p ON FIND_IN_SET(a.uuid, REPLACE(p.responsible_uuid, ';', ',')) > 0 INNER JOIN `" + AdminInfo.DATABASE_TABLE_NAME
                        + "` i ON a.uuid = i.admin_uuid inner join `" + Department.DATABASE_TABLE_NAME + "` d on i.department_uuid = d.uuid " + whereSql
                        + " GROUP BY a.uuid, a.name, i.real_name, d.uuid, d.name order by `problem_count` desc limit 0, 10");
                    rs = ps.executeQuery();
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        obj.put("uuid", rs.getString("uuid"));
                        obj.put("name", rs.getString("name"));
                        obj.put("real_name", rs.getString("real_name"));
                        obj.put("problem_count", rs.getInt("problem_count"));
                        obj.put("dep_uuid", rs.getString("dep_uuid"));
                        obj.put("dep_name", rs.getString("dep_name"));
                        String lv2Name = null;
                        {
                            Department dep = new Department(this.connection);
                            lv2Name = dep.getParentDepLv2Name(new String[] { rs.getString("dep_uuid") });
                        }
                        if (null != lv2Name) {
                            obj.put("dep_name", lv2Name);
                        }
                        arr.put(obj);
                    }
                } finally {
                    if (null != rs) {
                        rs.close();
                    }
                    if (null != ps) {
                        ps.close();
                    }
                }
                {
                    final JSONObject pObj = new JSONObject();
                    pObj.put("name", "minjing");
                    pObj.put("list", arr);
                    array.put(pObj);
                }
            }
            final JSONObject resultObj = new JSONObject();
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
     * 各业务警种检查发现的问题
     * 
     * @return 消息对象
     */
    public Message getProblemByLv2(final String startCreateDatetime, final String endCreateDatetime) {
        final Message msg = new Message();
        try {
            JSONArray pcsArray = new JSONArray();
            JSONArray zsdwArray = new JSONArray();
            {
                final Department ai = new Department(this.connection);
                final Message resultMsg = ai.getDepartment(null, null, null, Integer.valueOf(2), null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                for (int i = 0; i < array.length(); i++) {
                    final JSONObject jo = array.getJSONObject(i);
                    if (jo.getString("name").contains("派出所")) {
                        pcsArray.put(jo);
                    } else {
                        zsdwArray.put(jo);
                    }
                }
            }
            // 派出所
            {
                for (int i = 0; i < pcsArray.length(); i++) {
                    final JSONObject jo = pcsArray.getJSONObject(i);
                    {
                        final Message resultMsg = this.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, jo.getString("uuid"), null, null, null, null, null, null,
                            null, startCreateDatetime, endCreateDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final int count = ((JSONObject) resultMsg.getContent()).getInt("count");
                        jo.put("created_problem_count", count);// 派出所被创建问题数量
                    }
                    {
                        final Message resultMsg = this.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, ProblemType.SELF, null, jo.getString("uuid"), null, null, null, null,
                            null, null, null, startCreateDatetime, endCreateDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final int count = ((JSONObject) resultMsg.getContent()).getInt("count");
                        jo.put("self_problem_count", count);// 派出所自查问题数量
                    }
                }
            }
            // 直属单位
            {
                for (int i = 0; i < zsdwArray.length(); i++) {
                    final JSONObject jo = zsdwArray.getJSONObject(i);
                    {
                        final Message resultMsg = this.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, ProblemType.CHECK, jo.getString("uuid"), null, null, null, null, null,
                            null, null, null, startCreateDatetime, endCreateDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final int count = ((JSONObject) resultMsg.getContent()).getInt("count");
                        jo.put("check_problem_count", count);// 直属单位自查问题数量
                    }
                }
            }
            final JSONArray array = new JSONArray();
            {
                final JSONObject pObj = new JSONObject();
                pObj.put("name", "paichusuo");
                pObj.put("list", pcsArray);
                array.put(pObj);
            }
            {
                final JSONObject pObj = new JSONObject();
                pObj.put("name", "zhishudanwei");
                pObj.put("list", zsdwArray);
                array.put(pObj);
            }
            final JSONObject resultObj = new JSONObject();
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
     * 问题数量信息
     * 
     * @return 消息对象
     */
    public Message getProblemCountInfo(final String startCreateDatetime, final String endCreateDatetime) {
        final Message msg = new Message();
        try {
            final JSONObject resultObj = new JSONObject();
            // 全部问题数量
            {
                final Message resultMsg = this.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, startCreateDatetime,
                    endCreateDatetime, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final int count = ((JSONObject) resultMsg.getContent()).getInt("count");
                resultObj.put("all_problem_count", count);
            }
            // 各单位自查问题数量
            {
                final Message resultMsg = this.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, ProblemType.SELF, null, null, null, null, null, null, null, null, null,
                    startCreateDatetime, endCreateDatetime, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final int count = ((JSONObject) resultMsg.getContent()).getInt("count");
                resultObj.put("self_problem_count", count);
            }
            // 分局检查数量
            {
                resultObj.put("check_problem_count", resultObj.getInt("all_problem_count") - resultObj.getInt("self_problem_count"));
            }
            // 全部未整改数量
            {
                final Message resultMsg = this.getProblemExact(null, null, null, null, null, null, null, null, null, Status.UN_FEEDBACK, null, null, null, null, null, null, null, null, null, null, null,
                    startCreateDatetime, endCreateDatetime, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final int count = ((JSONObject) resultMsg.getContent()).getInt("count");
                resultObj.put("all_unfeedback_count", count);
            }
            // 全部已整改数量
            {
                resultObj.put("all_feedback_count", resultObj.getInt("all_problem_count") - resultObj.getInt("all_unfeedback_count"));
            }
            // 各单位自查问题未整改数量
            {
                // 各单位自查未整改数量
                final Message resultMsg = this.getProblemExact(null, null, null, null, null, null, null, null, null, Status.UN_FEEDBACK, null, ProblemType.SELF, null, null, null, null, null, null, null, null,
                    null, startCreateDatetime, endCreateDatetime, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final int count = ((JSONObject) resultMsg.getContent()).getInt("count");
                resultObj.put("self_unfeedback_count", count); // 各单位自查未整改数量
                resultObj.put("check_unfeedback_count", resultObj.getInt("all_unfeedback_count") - count); // 分局检查未整改问题数量
                resultObj.put("self_feedback_count", resultObj.getInt("self_problem_count") - resultObj.getInt("self_unfeedback_count")); // 各单位自查已整改数量
                resultObj.put("check_feedback_count", resultObj.getInt("check_problem_count") - resultObj.getInt("check_unfeedback_count")); // 分局检查已整改问题数量
            }
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