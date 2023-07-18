package supervision.spot;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.palestink.server.sdk.module.AbstractModule;
import com.palestink.server.sdk.module.annotation.Frequency;
import com.palestink.server.sdk.module.annotation.Method;
import com.palestink.server.sdk.module.annotation.Module;
import com.palestink.server.sdk.module.annotation.Parameter;
import com.palestink.server.sdk.module.annotation.Returns;
import com.palestink.server.sdk.msg.Message;
import env.db.DruidInstance;
import supervision.spot.dao.Accountable.Type;

@Module(description = "问题")
public class Accountable extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;

    public Accountable() {
    }

    public Accountable(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
    }

    @Method(description = "添加问责", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "problem_number", text = "问题编号", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,256}$", formatPrompt = "1-256位任意字符", remark = ""),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = false, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问责发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问责接收人或单位"),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "content", text = "内容", type = Parameter.Type.STRING, allowNull = false, format = "^[\\s\\S]{1,5120}$", formatPrompt = "1-5120位的任意内容", remark = ""),
            @Parameter(name = "from_lv2_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "level2单位的uuid"),
            @Parameter(name = "to_lv2_department_uuid", text = "目标部门的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "level2单位的uuid"),
            @Parameter(name = "create_datetime", text = "创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns())
    public Message addAccountable() {
        final Message msg = new Message();
        final String problemNumber = (String) this.parameter.get("problem_number");
        final String typeStr = (String) this.parameter.get("type");
        final Type type = Type.valueOf(typeStr);
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final String content = (String) this.parameter.get("content");
        final String fromDepartmentUuid = (String) this.parameter.get("from_lv2_department_uuid");
        final String toDepartmentUuid = (String) this.parameter.get("to_lv2_department_uuid");
        final String createDatetimeStr = (String) this.parameter.get("create_datetime");
        Long createDatetime = null;
        if (null != createDatetimeStr) {
            try {
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                createDatetime = Long.valueOf(sdf.parse(createDatetimeStr).getTime());
            } catch (final Exception e) {
                msg.setStatus(Message.Status.ERROR);
                msg.setContent("invalid_create_datetime");
                msg.setAttach("非法创建时间");
                return msg;
            }
        }
        // 添加问责
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Accountable obj = new supervision.spot.dao.Accountable(con);
                final Message resultMsg = obj.addAccountable(problemNumber, type, fromUuid, toUuid, typeUuid, content, fromDepartmentUuid, toDepartmentUuid, createDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除问责", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除问责", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public Message removeAccountable() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Accountable obj = new supervision.spot.dao.Accountable(con);
                final Message resultMsg = obj.removeAccountableByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除问责", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除问责", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public Message deleteAccountableByUuid() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Accountable obj = new supervision.spot.dao.Accountable(con);
                final Message resultMsg = obj.deleteAccountableByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改问责", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改问责", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = true, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "problem_number", text = "问题编号", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,256}$", formatPrompt = "1-256位任意字符", remark = ""),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = true, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问责发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问责接收人或单位"),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "content", text = "内容", type = Parameter.Type.STRING, allowNull = true, format = "^[\\s\\S]{1,5120}$", formatPrompt = "1-5120位的任意内容", remark = ""),
            @Parameter(name = "to_lv2_department_uuid", text = "目标部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "level2单位的uuid")    }, returns = @Returns())
    public Message modifyAccountable() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String problemNumber = (String) this.parameter.get("problem_number");
        final String typeStr = (String) this.parameter.get("type");
        Type type = null;
        if (null != typeStr) {
            type = Type.valueOf(typeStr);
        }
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final String content = (String) this.parameter.get("content");
        final String toLv2DepartmentUuid = (String) this.parameter.get("to_lv2_department_uuid");
        // 至少修改一项字段
        {
            if ((null == problemNumber) && (null == typeStr) && (null == type) && (null == fromUuid) && (null == toUuid) && (null == typeUuid) && (null == content)) {
                msg.setStatus(Message.Status.ERROR);
                msg.setContent("NO_DATA_MODIFIED");
                msg.setAttach("没有修改数据");
                return msg;
            }
        }
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Accountable obj = new supervision.spot.dao.Accountable(con);
                final Message resultMsg = obj.modifyAccountable(uuid, type, problemNumber, fromUuid, toUuid, typeUuid, content,toLv2DepartmentUuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取问责", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "问责的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "problem_number", text = "编号", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,256}$", formatPrompt = "1-256位的任意字符", remark = ""),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = true, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问责发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问责接收人或单位"),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "type_uuids", text = "类型的uuid的集合", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {}))
    public Message getAccountable() {
        final String uuid = (String) this.parameter.get("uuid");
        final String typeUuids = (String) this.parameter.get("type_uuids");
        String[] typeUuidArray = null;
        if (null != typeUuids) {
            typeUuidArray = typeUuids.split(";");
        }
        final String problemNumber = (String) this.parameter.get("problem_number");
        final String typeStr = (String) this.parameter.get("type");
        Type type = null;
        if (null != typeStr) {
            type = Type.valueOf(typeStr);
        }
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
        final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Accountable obj = new supervision.spot.dao.Accountable(con);
                final Message resultMsg = obj.getAccountable(uuid, (null != problemNumber) ? Long.valueOf(problemNumber) : null, type, fromUuid, toUuid, typeUuid, typeUuidArray, null, null, null, null, null, null, startCreateDatetime, endCreateDatetime,
                        offset, rows);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取问责统计", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {}, returns = @Returns(results = {}))
    public Message getAccountableStatistics() {
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Accountable obj = new supervision.spot.dao.Accountable(con);
                final Message resultMsg = obj.getAccountableStatistics();
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }
}