package supervision.spot;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.palestink.server.sdk.module.AbstractModule;
import com.palestink.server.sdk.module.annotation.Frequency;
import com.palestink.server.sdk.module.annotation.Method;
import com.palestink.server.sdk.module.annotation.Module;
import com.palestink.server.sdk.module.annotation.Parameter;
import com.palestink.server.sdk.module.annotation.ReturnResult;
import com.palestink.server.sdk.module.annotation.Returns;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.encrypt.Base64;

import env.db.DruidInstance;
import supervision.spot.dao.Problem.Fact;
import supervision.spot.dao.Problem.ProblemType;
import supervision.spot.dao.Problem.Read;
import supervision.spot.dao.Problem.StatisticsUnit;
import supervision.spot.dao.Problem.Type;

@Module(description = "问题")
public class Problem extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;
    // private Account account;

    public Problem() {
    }

    public Problem(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
        // this.account = new Account(this.httpServlet, this.httpServletRequest, this.httpServletResponse, this.parameter);
    }

    @Method(description = "添加问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = false, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位"),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "content", text = "内容", type = Parameter.Type.STRING, allowNull = false, format = "^[\\s\\S]{1,5120}$", formatPrompt = "1-5120位的任意内容", remark = ""),
            @Parameter(name = "file_content", text = "文件内容", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,52428800}$", formatPrompt = "1-52428800位的任意字符（即，10个5MB内的文件）", remark = "base64.length * 0.75 = 文件大小（单位：字节）。如果限制文件大小为2MB，那么base64长度限制为：(2 * 1024 * 1024) / 0.75 = 2796202.67。每个base64文件内容用|分割，需要与后缀顺序、数量匹配。"),
            @Parameter(name = "file_suffix", text = "文件后缀", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,50}$", formatPrompt = "1-10位的任意字符（即，10个后缀）", remark = "每个后缀用|分割，需要与文件内容顺序、数量匹配。后缀常量JPG, JPEG, PNG, TXT, WPS, PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, MP4, AVI, RAR或ZIP"),
            @Parameter(name = "responsible_uuids", text = "负责人的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母集合", remark = ""),
            @Parameter(name = "problem_type", text = "问题类型", type = Parameter.Type.STRING, allowNull = false, format = "^SELF|CHECK$", formatPrompt = "常量SELF或CHECK", remark = "SELF（自查）、CHECK（检查）"),
            @Parameter(name = "from_lv2_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "level2单位的uuid"),
            @Parameter(name = "to_lv2_department_uuid", text = "目标部门的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "level2单位的uuid"),
            @Parameter(name = "create_datetime", text = "创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns())
    public Message addProblem() {
        final Message msg = new Message();
        final String typeStr = (String) this.parameter.get("type");
        final Type type = Type.valueOf(typeStr);
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final String content = (String) this.parameter.get("content");
        final String fileContent = (String) this.parameter.get("file_content");
        final String fileSuffix = (String) this.parameter.get("file_suffix");
        final String responsibleUuids = (String) this.parameter.get("responsible_uuids");
        final String problemTypeStr = (String) this.parameter.get("problem_type");
        final ProblemType problemType = ProblemType.valueOf(problemTypeStr);
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
        String[][] files = null;
        // 检查文件内容与后缀
        {
            if ((null != fileContent) && (null != fileSuffix)) {
                final String[] fileContentArray = fileContent.split("\\|");
                final String[] fileSuffixArray = fileSuffix.split("\\|");
                if (fileContentArray.length != fileSuffixArray.length) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("FILE_CONTENT_SUFFIX_DIFFERENT_COUNT");
                    msg.setAttach("文件内容与后缀数量不统一");
                    return msg;
                }
                files = new String[fileContentArray.length][2];
                for (int i = 0; i < fileContentArray.length; i++) {
                    final String fc = fileContentArray[i];
                    final String fs = fileSuffixArray[i];
                    try {
                        supervision.spot.dao.StorageFile.Suffix.valueOf(fs);
                    } catch (final Exception e) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("FILE_SUFFIX_INVALID");
                        msg.setAttach("文件内后缀不合法");
                        return msg;
                    }
                    files[i][0] = fc.contains(",") ? fc.substring(fc.indexOf(",") + 1)/* 去除base64类型头 */ : fc; // 文件内容
                    files[i][1] = fs; // 文件后缀
                }
            }
        }
        // 添加问题
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.addProblem(type, fromUuid, toUuid, typeUuid, content, files, (null != responsibleUuids) ? responsibleUuids.split(";") : null, problemType, fromDepartmentUuid, toDepartmentUuid, createDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "添加问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "uuid", type = Parameter.Type.STRING, allowNull = false, format = "^.{1,40}$", formatPrompt = "1-40位的任意字符", remark = "问题的uuid"),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = false, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位"),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "content", text = "内容", type = Parameter.Type.STRING, allowNull = false, format = "^[\\s\\S]{1,5120}$", formatPrompt = "1-5120位的任意内容", remark = ""),
            @Parameter(name = "file_url", text = "文件url", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,20480}$", formatPrompt = "1-20480位的任意字符", remark = "每个文件url用|分割，需要与后缀顺序、数量匹配。"),
            @Parameter(name = "file_suffix", text = "文件后缀", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,50}$", formatPrompt = "1-10位的任意字符（即，10个后缀）", remark = "每个后缀用|分割，需要与文件内容顺序、数量匹配。后缀常量JPG, JPEG, PNG, TXT, WPS, PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, MP4, AVI, RAR或ZIP"),
            @Parameter(name = "responsible_uuids", text = "负责人的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母集合", remark = ""),
            @Parameter(name = "problem_type", text = "问题类型", type = Parameter.Type.STRING, allowNull = false, format = "^SELF|CHECK|VIDEO$", formatPrompt = "常量SELF或CHECK、VIDEO", remark = "SELF（自查）、CHECK（检查）、VIDEO（视频）"),
            @Parameter(name = "from_lv2_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "level2单位的uuid"),
            @Parameter(name = "to_lv2_department_uuid", text = "目标部门的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "level2单位的uuid"),
            @Parameter(name = "create_datetime", text = "创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns())
    public Message addProblemWithUuid() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String typeStr = (String) this.parameter.get("type");
        final Type type = Type.valueOf(typeStr);
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final String content = (String) this.parameter.get("content");
        final String fileUrl = (String) this.parameter.get("file_url");
        final String fileSuffix = (String) this.parameter.get("file_suffix");
        final String responsibleUuids = (String) this.parameter.get("responsible_uuids");
        final String problemTypeStr = (String) this.parameter.get("problem_type");
        final ProblemType problemType = ProblemType.valueOf(problemTypeStr);
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
        String[][] files = null;
        // 检查文件内容与后缀
        {
            if ((null != fileUrl) && (null != fileSuffix)) {
                final String[] fileUrlArray = fileUrl.split("\\|");
                final String[] fileSuffixArray = fileSuffix.split("\\|");
                if (fileUrlArray.length != fileSuffixArray.length) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("FILE_URL_SUFFIX_DIFFERENT_COUNT");
                    msg.setAttach("文件url与后缀数量不统一");
                    return msg;
                }
                files = new String[fileUrlArray.length][2];
                for (int i = 0; i < fileUrlArray.length; i++) {
                    final String fc = fileUrlArray[i];
                    final String fs = fileSuffixArray[i];
                    try {
                        supervision.spot.dao.StorageFile.Suffix.valueOf(fs);
                    } catch (final Exception e) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("FILE_SUFFIX_INVALID");
                        msg.setAttach("文件内后缀不合法");
                        return msg;
                    }
                    files[i][0] = fc.contains(",") ? fc.substring(fc.indexOf(",") + 1)/* 去除base64类型头 */ : fc; // 文件内容
                    files[i][1] = fs; // 文件后缀
                }
            }
        }
        // 添加问题
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.addProblem(uuid, type, fromUuid, toUuid, typeUuid, content, files, (null != responsibleUuids) ? responsibleUuids.split(";") : null, problemType, fromDepartmentUuid, toDepartmentUuid, createDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除问题", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public Message removeProblem() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.removeProblemByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除问题", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public Message deleteProblemByUuid() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.deleteProblemByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改问题", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = true, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位"),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "content", text = "内容", type = Parameter.Type.STRING, allowNull = true, format = "^[\\s\\S]{1,5120}$", formatPrompt = "1-5120位的任意内容", remark = ""),
            @Parameter(name = "read", text = "是否已读", type = Parameter.Type.STRING, allowNull = true, format = "^UN_READ|READ$", formatPrompt = "常量UN_READ或READ", remark = "UN_READ（未读）、READ（已读）"),
            @Parameter(name = "fact", text = "是否属实", type = Parameter.Type.STRING, allowNull = true, format = "^UN_CONFIR|NOT_FACT|FACT$", formatPrompt = "常量UN_CONFIR、NOT_FACT或FACT", remark = "UN_CONFIR（未确认）、NOT_FACT（失实）、FACT（属实）"),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^UN_FEEDBACK|FEEDBACK|FILED$", formatPrompt = "常量UN_FEEDBACK、FEEDBACK或FILED", remark = "UN_FEEDBACK（未反馈）、FEEDBACK（已反馈）、FILED（已归档）"),
            @Parameter(name = "responsible_uuids", text = "负责人的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母集合", remark = "如果要删除全部责任人，传递clear参数") }, returns = @Returns())
    public Message modifyProblem() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String typeStr = (String) this.parameter.get("type");
        Type type = null;
        if (null != typeStr) {
            type = Type.valueOf(typeStr);
        }
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final String content = (String) this.parameter.get("content");
        final String readStr = (String) this.parameter.get("read");
        Read read = null;
        if (null != readStr) {
            read = Read.valueOf(readStr);
        }
        final String factStr = (String) this.parameter.get("fact");
        Fact fact = null;
        if (null != factStr) {
            fact = Fact.valueOf(factStr);
        }
        final String statusStr = (String) this.parameter.get("status");
        supervision.spot.dao.Problem.Status status = null;
        if (null != statusStr) {
            status = supervision.spot.dao.Problem.Status.valueOf(statusStr);
        }
        final String responsibleUuids = (String) this.parameter.get("responsible_uuids");
        // 至少修改一项字段
        {
            if ((null == typeStr) && (null == type) && (null == fromUuid) && (null == toUuid) && (null == typeUuid) && (null == content) && (null == readStr) && (null == read) && (null == factStr) && (null == fact) && (null == statusStr) && (null == status)
                    && (null == responsibleUuids)) {
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
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.modifyProblem(uuid, fromUuid, toUuid, typeUuid, content, read, fact, status, (null != responsibleUuids) ? responsibleUuids.split(";") : null, null);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "问题的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "number", text = "编号", type = Parameter.Type.LONG, allowNull = true, format = "^[1-9]\\d*$", formatPrompt = "正整数", remark = ""),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = true, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位"),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "type_uuids", text = "类型的uuid的集合", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "read", text = "是否已读", type = Parameter.Type.STRING, allowNull = true, format = "^UN_READ|READ$", formatPrompt = "常量UN_READ或READ", remark = "UN_READ（未读）、READ（已读）"),
            @Parameter(name = "fact", text = "是否属实", type = Parameter.Type.STRING, allowNull = true, format = "^UN_CONFIR|NOT_FACT|FACT$", formatPrompt = "常量UN_CONFIR、NOT_FACT或FACT", remark = "UN_CONFIR（未确认）、NOT_FACT（失实）、FACT（属实）"),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^UN_FEEDBACK|FEEDBACK|FILED$", formatPrompt = "常量UN_FEEDBACK、FEEDBACK或FILED", remark = "UN_FEEDBACK（未反馈）、FEEDBACK（已反馈）、FILED（已归档）"),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "start_read_datetime", text = "开始已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_read_datetime", text = "结束已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type", type = "string[1,16]", isNecessary = true, description = "类型（PERSON：个人；DEPARTMENT：单位）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = true, description = "类型的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_fact", type = "string[1,16]", isNecessary = true, description = "是否属实（UN_CONFIR：未确认；NOT_FACT：失实；FACT：属实）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_feedback", type = "string[1,16]", isNecessary = true, description = "是否反馈（UN_FEEDBACK：未反馈；FEEDBACK：已反馈）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间"),
                    @ReturnResult(parentId = "array_id", id = "storage_file", name = "storage_file", type = "array", isNecessary = false, description = "问题存储文件"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    public Message getProblem() {
        final String uuid = (String) this.parameter.get("uuid");
        final String typeUuids = (String) this.parameter.get("type_uuids");
        String[] typeUuidArray = null;
        if (null != typeUuids) {
            typeUuidArray = typeUuids.split(";");
        }
        final Long number = (Long) this.parameter.get("number");
        final String typeStr = (String) this.parameter.get("type");
        Type type = null;
        if (null != typeStr) {
            type = Type.valueOf(typeStr);
        }
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final String readStr = (String) this.parameter.get("read");
        Read read = null;
        if (null != readStr) {
            read = Read.valueOf(readStr);
        }
        final String factStr = (String) this.parameter.get("fact");
        Fact fact = null;
        if (null != factStr) {
            fact = Fact.valueOf(factStr);
        }
        final String statusStr = (String) this.parameter.get("status");
        supervision.spot.dao.Problem.Status status = null;
        if (null != statusStr) {
            status = supervision.spot.dao.Problem.Status.valueOf(statusStr);
        }
        final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
        final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
        final String startReadDatetime = (String) this.parameter.get("start_read_datetime");
        final String endReadDatetime = (String) this.parameter.get("end_read_datetime");
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.getProblemExact(uuid, number, type, fromUuid, toUuid, typeUuid, typeUuidArray, read, fact, status, null, null, null, null, null, null, null, null, null, null, null, startCreateDatetime, endCreateDatetime,
                        startReadDatetime, endReadDatetime, offset, rows);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "根据FromUuid和ToUuid获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "问题的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "number", text = "编号", type = Parameter.Type.LONG, allowNull = true, format = "^[1-9]\\d*$", formatPrompt = "正整数", remark = ""),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位"),
            @Parameter(name = "type_uuids", text = "类型的uuid的集合", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^UN_FEEDBACK|FEEDBACK|FILED$", formatPrompt = "常量UN_FEEDBACK、FEEDBACK或FILED", remark = "UN_FEEDBACK（未反馈）、FEEDBACK（已反馈）、FILED（已归档）"),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type", type = "string[1,16]", isNecessary = true, description = "类型（PERSON：个人；DEPARTMENT：单位）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = true, description = "类型的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_fact", type = "string[1,16]", isNecessary = true, description = "是否属实（UN_CONFIR：未确认；NOT_FACT：失实；FACT：属实）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_feedback", type = "string[1,16]", isNecessary = true, description = "是否反馈（UN_FEEDBACK：未反馈；FEEDBACK：已反馈）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间"),
                    @ReturnResult(parentId = "array_id", id = "storage_file", name = "storage_file", type = "array", isNecessary = false, description = "问题存储文件"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    public Message getProblemByFromUuidOrToUuid() {
        final String uuid = (String) this.parameter.get("uuid");
        final Long number = (Long) this.parameter.get("number");
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String typeUuids = (String) this.parameter.get("type_uuids");
        String[] typeUuidArray = null;
        if (null != typeUuids) {
            typeUuidArray = typeUuids.split(";");
        }
        final String statusStr = (String) this.parameter.get("status");
        supervision.spot.dao.Problem.Status status = null;
        if (null != statusStr) {
            status = supervision.spot.dao.Problem.Status.valueOf(statusStr);
        }
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.getProblemByFromUuidOrToUuid(uuid, number, fromUuid, toUuid, typeUuidArray, status, offset, rows);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取问题", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "statistics_unit", text = "统计单位", type = Parameter.Type.STRING, allowNull = false, format = "^DAY|WEEK|MONTH$", formatPrompt = "常量DAY、WEEK或MONTH", remark = "DAY（天）、WEEK（周）、MONTH（月）"),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = true, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位"),
            @Parameter(name = "type_name_like", text = "类型名称的模糊查询", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,128}$", formatPrompt = "1-128位的任意字符", remark = ""),
            @Parameter(name = "type_uuids", text = "类型的uuid的集合", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "read", text = "是否已读", type = Parameter.Type.STRING, allowNull = true, format = "^UN_READ|READ$", formatPrompt = "常量UN_READ或READ", remark = "UN_READ（未读）、READ（已读）"),
            @Parameter(name = "fact", text = "是否属实", type = Parameter.Type.STRING, allowNull = true, format = "^UN_CONFIR|NOT_FACT|FACT$", formatPrompt = "常量UN_CONFIR、NOT_FACT或FACT", remark = "UN_CONFIR（未确认）、NOT_FACT（失实）、FACT（属实）"),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^UN_FEEDBACK|FEEDBACK|FILED$", formatPrompt = "常量UN_FEEDBACK、FEEDBACK或FILED", remark = "UN_FEEDBACK（未反馈）、FEEDBACK（已反馈）、FILED（已归档）"),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "start_read_datetime", text = "开始已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_read_datetime", text = "结束已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "对象", type = "{\"datetime\": \"...\", \"count\": 33}", isNecessary = true, description = "当统计单位为WEEK时，datetime为当前年的第N周（从周一开始计算）") }))
    public Message getProblemCountStatistics() {
        final String statisticsUnitStr = (String) this.parameter.get("statistics_unit");
        StatisticsUnit statisticsUnit = null;
        if (null != statisticsUnitStr) {
            statisticsUnit = StatisticsUnit.valueOf(statisticsUnitStr);
        }
        final String typeUuids = (String) this.parameter.get("type_uuids");
        String[] typeUuidArray = null;
        final String typeStr = (String) this.parameter.get("type");
        Type type = null;
        if (null != typeStr) {
            type = Type.valueOf(typeStr);
        }
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String typeNameLike = (String) this.parameter.get("type_name_like");
        if (null != typeUuids) {
            typeUuidArray = typeUuids.split(";");
        }
        final String readStr = (String) this.parameter.get("read");
        Read read = null;
        if (null != readStr) {
            read = Read.valueOf(readStr);
        }
        final String factStr = (String) this.parameter.get("fact");
        Fact fact = null;
        if (null != factStr) {
            fact = Fact.valueOf(factStr);
        }
        final String statusStr = (String) this.parameter.get("status");
        supervision.spot.dao.Problem.Status status = null;
        if (null != statusStr) {
            status = supervision.spot.dao.Problem.Status.valueOf(statusStr);
        }
        final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
        final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
        final String startReadDatetime = (String) this.parameter.get("start_read_datetime");
        final String endReadDatetime = (String) this.parameter.get("end_read_datetime");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.getProblemCountStatistics(statisticsUnit, type, fromUuid, toUuid, typeNameLike, typeUuidArray, read, fact, status, startCreateDatetime, endCreateDatetime, startReadDatetime, endReadDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    // @Method(description = "获取问题", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    // @Parameter(name = "admin_uuid", text = "管理员的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
    // @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
    // @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
    // @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
    // @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
    // @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题的uuid"),
    // @ReturnResult(parentId = "array_id", id = "", name = "type", type = "string[1,16]", isNecessary = true, description = "类型（PERSON：个人；DEPARTMENT：单位）"),
    // @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
    // @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
    // @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = true, description = "类型的uuid"),
    // @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
    // @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
    // @ReturnResult(parentId = "array_id", id = "", name = "is_fact", type = "string[1,16]", isNecessary = true, description = "是否属实（UN_CONFIR：未确认；NOT_FACT：失实；FACT：属实）"),
    // @ReturnResult(parentId = "array_id", id = "", name = "is_feedback", type = "string[1,16]", isNecessary = true, description = "是否反馈（UN_FEEDBACK：未反馈；FEEDBACK：已反馈）"),
    // @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
    // @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间"),
    // @ReturnResult(parentId = "array_id", id = "storage_file", name = "storage_file", type = "array", isNecessary = false, description = "问题存储文件"),
    // @ReturnResult(parentId = "storage_file", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
    // @ReturnResult(parentId = "storage_file", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
    // @ReturnResult(parentId = "storage_file", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
    // @ReturnResult(parentId = "storage_file", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
    // @ReturnResult(parentId = "storage_file", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    // public Message getProblemByAuthor() {
    // final String adminUuid = (String) this.parameter.get("admin_uuid");
    // final Integer offset = (Integer) this.parameter.get("offset");
    // final Integer rows = (Integer) this.parameter.get("rows");
    // {
    // Connection con = null;
    // try {
    // con = DruidInstance.getInstance().getTransConnection();
    // final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
    // final Message resultMsg = obj.getProblemByAuthor(adminUuid, offset, rows);
    // this.messageResultHandler(resultMsg, con, true);
    // return resultMsg;
    // } catch (final Exception e) {
    // return this.catchHandler(con, e);
    // } finally {
    // this.finallyHandler(con);
    // }
    // }
    // }

    // @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取问题", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "问题的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "department_uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type", type = "string[1,16]", isNecessary = true, description = "类型（PERSON：个人；DEPARTMENT：单位）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = true, description = "类型的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_fact", type = "string[1,16]", isNecessary = true, description = "是否属实（UN_CONFIR：未确认；NOT_FACT：失实；FACT：属实）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_feedback", type = "string[1,16]", isNecessary = true, description = "是否反馈（UN_FEEDBACK：未反馈；FEEDBACK：已反馈）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间"),
                    @ReturnResult(parentId = "array_id", id = "storage_file", name = "storage_file", type = "array", isNecessary = false, description = "问题存储文件"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    public Message getProblemByLeader() {
        final String uuid = (String) this.parameter.get("uuid");
        final String departmentUuid = (String) this.parameter.get("department_uuid");
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.getProblemByLeader(uuid, departmentUuid, offset, rows);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取问题", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "问题的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "number", text = "编号", type = Parameter.Type.LONG, allowNull = true, format = "^[1-9]\\d*$", formatPrompt = "正整数", remark = ""),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = true, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位"),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "type_uuids", text = "类型的uuid的集合", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "read", text = "是否已读", type = Parameter.Type.STRING, allowNull = true, format = "^UN_READ|READ$", formatPrompt = "常量UN_READ或READ", remark = "UN_READ（未读）、READ（已读）"),
            @Parameter(name = "fact", text = "是否属实", type = Parameter.Type.STRING, allowNull = true, format = "^UN_CONFIR|NOT_FACT|FACT$", formatPrompt = "常量UN_CONFIR、NOT_FACT或FACT", remark = "UN_CONFIR（未确认）、NOT_FACT（失实）、FACT（属实）"),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^UN_FEEDBACK|FEEDBACK|FILED$", formatPrompt = "常量UN_FEEDBACK、FEEDBACK或FILED", remark = "UN_FEEDBACK（未反馈）、FEEDBACK（已反馈）、FILED（已归档）"),
            @Parameter(name = "responsible_uuid_like", text = "负责人的uuid模糊查询", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "problem_type", text = "问题类型", type = Parameter.Type.STRING, allowNull = true, format = "^SELF|CHECK$", formatPrompt = "常量SELF或CHECK", remark = "SELF（自查）、CHECK（检查）"),
            @Parameter(name = "from_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起单位"),
            @Parameter(name = "to_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收单位"),
            @Parameter(name = "or_from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人（or关系）"),
            @Parameter(name = "or_to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位（or关系）"),
            @Parameter(name = "or_responsible_uuid_like", text = "负责人的uuid模糊查询", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "（or关系）"),
            @Parameter(name = "or_from_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起单位（or关系）"),
            @Parameter(name = "or_to_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收单位（or关系）"),
            @Parameter(name = "responsible_uuid_has", text = "负责人的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^true|false$", formatPrompt = "常量true和false", remark = "如果传递true则返回有责任人的数据，传递false则返回无责任人的数据"),
            @Parameter(name = "or_group", text = "or组", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,}$", formatPrompt = "大于1位的任意字符", remark = "如果数据库关系如：and (from_uuid = 123 or responsible_uuid_like like 234) and 这样的数据，传递参数为  from_uuid:=:123;responsible_uuid_like:like:234"),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "start_read_datetime", text = "开始已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_read_datetime", text = "结束已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type", type = "string[1,16]", isNecessary = true, description = "类型（PERSON：个人；DEPARTMENT：单位）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = true, description = "类型的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_fact", type = "string[1,16]", isNecessary = true, description = "是否属实（UN_CONFIR：未确认；NOT_FACT：失实；FACT：属实）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_feedback", type = "string[1,16]", isNecessary = true, description = "是否反馈（UN_FEEDBACK：未反馈；FEEDBACK：已反馈）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间"),
                    @ReturnResult(parentId = "array_id", id = "storage_file", name = "storage_file", type = "array", isNecessary = false, description = "问题存储文件"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    public Message getProblemExact() {
        final String uuid = (String) this.parameter.get("uuid");
        final String typeUuids = (String) this.parameter.get("type_uuids");
        String[] typeUuidArray = null;
        if (null != typeUuids) {
            typeUuidArray = typeUuids.split(";");
        }
        final Long number = (Long) this.parameter.get("number");
        final String typeStr = (String) this.parameter.get("type");
        Type type = null;
        if (null != typeStr) {
            type = Type.valueOf(typeStr);
        }
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final String readStr = (String) this.parameter.get("read");
        Read read = null;
        if (null != readStr) {
            read = Read.valueOf(readStr);
        }
        final String factStr = (String) this.parameter.get("fact");
        Fact fact = null;
        if (null != factStr) {
            fact = Fact.valueOf(factStr);
        }
        final String statusStr = (String) this.parameter.get("status");
        supervision.spot.dao.Problem.Status status = null;
        if (null != statusStr) {
            status = supervision.spot.dao.Problem.Status.valueOf(statusStr);
        }
        final String responsibleUuidLike = (String) this.parameter.get("responsible_uuid_like");
        final String problemTypeStr = (String) this.parameter.get("problem_type");
        ProblemType problemType = null;
        if (null != problemTypeStr) {
            problemType = ProblemType.valueOf(problemTypeStr);
        }
        final String fromDepartmentUuid = (String) this.parameter.get("from_department_uuid");
        final String toDepartmentUuid = (String) this.parameter.get("to_department_uuid");
        final String orFromUuid = (String) this.parameter.get("or_from_uuid");
        final String orToUuid = (String) this.parameter.get("or_to_uuid");
        final String orResponsibleUuidLike = (String) this.parameter.get("or_responsible_uuid_like");
        final String orFromDepartmentUuid = (String) this.parameter.get("or_from_department_uuid");
        final String orToDepartmentUuid = (String) this.parameter.get("or_to_department_uuid");
        final String responsibleUuidHas = (String) this.parameter.get("responsible_uuid_has");
        JSONArray conditionArray = null;
        final String orGroup = (String) this.parameter.get("or_group");
        if (null != orGroup) {
            try {
                conditionArray = new JSONArray();
                final String[] item = orGroup.split(";");
                for (int i = 0; i < item.length; i++) {
                    final String p = item[i];
                    final String name = p.split(":")[0];
                    final String symbol = p.split(":")[1];
                    final String value = p.split(":")[2];
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "or");
                    obj.put("name", name);
                    obj.put("symbol", symbol);
                    obj.put("value", value);
                    conditionArray.put(obj);
                }

            } catch (final Exception e) {
                return new Message(Message.Status.ERROR, "OR_GROUP_FORMAT_ERROR", null);
            }
        }
        final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
        final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
        final String startReadDatetime = (String) this.parameter.get("start_read_datetime");
        final String endReadDatetime = (String) this.parameter.get("end_read_datetime");
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.getProblemExact(uuid, number, type, fromUuid, toUuid, typeUuid, typeUuidArray, read, fact, status, responsibleUuidLike, problemType, fromDepartmentUuid, toDepartmentUuid, orFromUuid, orToUuid,
                        orResponsibleUuidLike, orFromDepartmentUuid, orToDepartmentUuid, responsibleUuidHas, conditionArray, startCreateDatetime, endCreateDatetime, startReadDatetime, endReadDatetime, offset, rows);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取问题", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "问题的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "number", text = "编号", type = Parameter.Type.LONG, allowNull = true, format = "^[1-9]\\d*$", formatPrompt = "正整数", remark = ""),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = true, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位"),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "type_uuids", text = "类型的uuid的集合", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "read", text = "是否已读", type = Parameter.Type.STRING, allowNull = true, format = "^UN_READ|READ$", formatPrompt = "常量UN_READ或READ", remark = "UN_READ（未读）、READ（已读）"),
            @Parameter(name = "fact", text = "是否属实", type = Parameter.Type.STRING, allowNull = true, format = "^UN_CONFIR|NOT_FACT|FACT$", formatPrompt = "常量UN_CONFIR、NOT_FACT或FACT", remark = "UN_CONFIR（未确认）、NOT_FACT（失实）、FACT（属实）"),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^UN_FEEDBACK|FEEDBACK|FILED$", formatPrompt = "常量UN_FEEDBACK、FEEDBACK或FILED", remark = "UN_FEEDBACK（未反馈）、FEEDBACK（已反馈）、FILED（已归档）"),
            @Parameter(name = "responsible_uuid_like", text = "负责人的uuid模糊查询", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "problem_type", text = "问题类型", type = Parameter.Type.STRING, allowNull = true, format = "^SELF|CHECK$", formatPrompt = "常量SELF或CHECK", remark = "SELF（自查）、CHECK（检查）"),
            @Parameter(name = "from_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起单位"),
            @Parameter(name = "to_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收单位"),
            @Parameter(name = "or_from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人（or关系）"),
            @Parameter(name = "or_to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位（or关系）"),
            @Parameter(name = "or_responsible_uuid_like", text = "负责人的uuid模糊查询", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "（or关系）"),
            @Parameter(name = "or_from_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起单位（or关系）"),
            @Parameter(name = "or_to_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收单位（or关系）"),
            @Parameter(name = "responsible_uuid_has", text = "负责人的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^true|false$", formatPrompt = "常量true和false", remark = "如果传递true则返回有责任人的数据，传递false则返回无责任人的数据"),
            @Parameter(name = "or_group", text = "or组", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,}$", formatPrompt = "大于1位的任意字符", remark = "如果数据库关系如：and (from_uuid = 123 or responsible_uuid_like like 234) and 这样的数据，传递参数为  from_uuid:=:123;responsible_uuid_like:like:234"),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "start_read_datetime", text = "开始已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_read_datetime", text = "结束已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type", type = "string[1,16]", isNecessary = true, description = "类型（PERSON：个人；DEPARTMENT：单位）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = true, description = "类型的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_fact", type = "string[1,16]", isNecessary = true, description = "是否属实（UN_CONFIR：未确认；NOT_FACT：失实；FACT：属实）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_feedback", type = "string[1,16]", isNecessary = true, description = "是否反馈（UN_FEEDBACK：未反馈；FEEDBACK：已反馈）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间"),
                    @ReturnResult(parentId = "array_id", id = "storage_file", name = "storage_file", type = "array", isNecessary = false, description = "问题存储文件"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    public Message getProblemExactExport() {
        final Message msg = this.getProblemExact();
        if (Message.Status.SUCCESS != msg.getStatus()) {
            return msg;
        }
        final JSONObject obj = (JSONObject) msg.getContent();
        final JSONArray array = obj.getJSONArray("array");
        final StringBuilder sb = new StringBuilder();
        sb.append("序号,问题编号,问题创建单位,问题创建人,问题类型,问题类别,检查时间,被检查单位,问题责任人,问题描述,问题状态,是否反馈,反馈负责人,反馈时间,属实情况,反馈描述");
        for (int i = 0; i < array.length(); i++) {
            final JSONObject jo = array.getJSONObject(i);
            try {
                final String index = String.valueOf(i + 1); // 序号
                final String problemUuid = jo.getString("uuid"); // 问题的uuid
                final String number = String.valueOf(jo.getInt("number")); // 问题编号
                final String fromDepartmentName = jo.getString("from_department_name"); // 问题创建单位
                final String fromRealName = jo.getString("from_real_name"); // 问题创建人
                final String problemDepartmentTypeName = jo.getString("problem_department_type_name"); // 问题类型
                String problemType = jo.getString("problem_type"); // 问题类别
                if (problemType.equalsIgnoreCase("check")) {
                    problemType = "检查问题";
                } else {
                    problemType = "自查问题";
                }
                final String createDatetime = jo.getString("create_datetime"); // 检查时间
                final String toDepartmentName = jo.getString("to_department_name"); // 被检查单位
                String responsibleRealName = "";
                if (jo.has("responsible_real_name")) {
                    responsibleRealName = jo.getJSONArray("responsible_real_name").toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", ""); // 问题责任人
                }
                final String content = jo.getString("content"); // 问题描述
                String status = jo.getString("status"); // 问题状态
                String isFeedback = ""; // 是否反馈
                if (status.equalsIgnoreCase("UN_FEEDBACK")) {
                    status = "未反馈";
                    isFeedback = "否";
                } else if (status.equalsIgnoreCase("FEEDBACK")) {
                    status = "已反馈";
                    isFeedback = "是";
                } else {
                    status = "已归档";
                    isFeedback = "是";
                }
                JSONArray feedbackArray = null;
                // 获取反馈
                {
                    Connection con = null;
                    try {
                        con = DruidInstance.getInstance().getTransConnection();
                        final supervision.spot.dao.Feedback fb = new supervision.spot.dao.Feedback(con);
                        final Message resultMsg = fb.getFeedbackOnly(null, problemUuid, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        feedbackArray = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        this.messageResultHandler(resultMsg, con, true);
                    } catch (final Exception e) {
                        return this.catchHandler(con, e);
                    } finally {
                        this.finallyHandler(con);
                    }
                }
                String feedbackRealName = "";
                if ((null != feedbackArray) && (0 < feedbackArray.length())) {
                    feedbackRealName = feedbackArray.getJSONObject(0).getJSONArray("from_admin_array").getJSONObject(0).getString("real_name"); // 反馈负责人
                }
                String feedBackCreateDatetime = "";
                if ((null != feedbackArray) && (0 < feedbackArray.length())) {
                    feedBackCreateDatetime = feedbackArray.getJSONObject(0).getString("create_datetime"); // 反馈时间
                }
                String isFact = jo.getString("is_fact"); // 属实情况
                if (isFact.equalsIgnoreCase("UN_CONFIR")) {
                    isFact = "未确认";
                } else if (isFact.equalsIgnoreCase("NOT_FACT")) {
                    isFact = "失实";
                } else {
                    isFact = "属实";
                }
                String feedBackContent = "";
                if ((null != feedbackArray) && (0 < feedbackArray.length())) {
                    feedBackContent = feedbackArray.getJSONObject(0).getString("content"); // 反馈描述
                }
                final String res = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", index.replaceAll(",", "，"), number.replaceAll(",", "，"), fromDepartmentName.replaceAll(",", "，"), fromRealName.replaceAll(",", "，"),
                        problemType.replaceAll(",", "，"), problemDepartmentTypeName.replaceAll(",", "，"), createDatetime.replaceAll(",", "，"), toDepartmentName.replaceAll(",", "，"), responsibleRealName.replaceAll(",", "，"), content.replaceAll("\\s", "").replaceAll(",", "，"),
                        status.replaceAll(",", "，"), isFeedback.replaceAll(",", "，"), feedbackRealName.replaceAll(",", "，"), feedBackCreateDatetime.replaceAll(",", "，"), isFact.replaceAll(",", "，"), feedBackContent.replaceAll("\\s", "").replaceAll(",", "，"));
                sb.append(System.lineSeparator());
                sb.append(res);
            } catch (final Exception e) {
                continue;
            }
        }
        // System.out.println(Base64.encode(sb.toString().getBytes()));
        return new Message(Message.Status.SUCCESS, Base64.encode(sb.toString().getBytes()), null);
    }

    // @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取问题", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type", type = "string[1,16]", isNecessary = true, description = "类型（PERSON：个人；DEPARTMENT：单位）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = true, description = "类型的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_fact", type = "string[1,16]", isNecessary = true, description = "是否属实（UN_CONFIR：未确认；NOT_FACT：失实；FACT：属实）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_feedback", type = "string[1,16]", isNecessary = true, description = "是否反馈（UN_FEEDBACK：未反馈；FEEDBACK：已反馈）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间"),
                    @ReturnResult(parentId = "array_id", id = "storage_file", name = "storage_file", type = "array", isNecessary = false, description = "问题存储文件"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    public Message getProblemByPolice() {
        {
            final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
            final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.getProblemByPolice(startCreateDatetime, endCreateDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取问题", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type", type = "string[1,16]", isNecessary = true, description = "类型（PERSON：个人；DEPARTMENT：单位）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = true, description = "类型的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_fact", type = "string[1,16]", isNecessary = true, description = "是否属实（UN_CONFIR：未确认；NOT_FACT：失实；FACT：属实）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_feedback", type = "string[1,16]", isNecessary = true, description = "是否反馈（UN_FEEDBACK：未反馈；FEEDBACK：已反馈）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间"),
                    @ReturnResult(parentId = "array_id", id = "storage_file", name = "storage_file", type = "array", isNecessary = false, description = "问题存储文件"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    public Message getProblemByPoliceFast() {
        {
            final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
            final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.getProblemByPoliceFast(startCreateDatetime, endCreateDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取问题", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type", type = "string[1,16]", isNecessary = true, description = "类型（PERSON：个人；DEPARTMENT：单位）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = true, description = "类型的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_fact", type = "string[1,16]", isNecessary = true, description = "是否属实（UN_CONFIR：未确认；NOT_FACT：失实；FACT：属实）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_feedback", type = "string[1,16]", isNecessary = true, description = "是否反馈（UN_FEEDBACK：未反馈；FEEDBACK：已反馈）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间"),
                    @ReturnResult(parentId = "array_id", id = "storage_file", name = "storage_file", type = "array", isNecessary = false, description = "问题存储文件"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    public Message getProblemByLv2() {
        {
            final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
            final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.getProblemByLv2(startCreateDatetime, endCreateDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取问题", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type", type = "string[1,16]", isNecessary = true, description = "类型（PERSON：个人；DEPARTMENT：单位）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = true, description = "类型的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_fact", type = "string[1,16]", isNecessary = true, description = "是否属实（UN_CONFIR：未确认；NOT_FACT：失实；FACT：属实）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_feedback", type = "string[1,16]", isNecessary = true, description = "是否反馈（UN_FEEDBACK：未反馈；FEEDBACK：已反馈）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间"),
                    @ReturnResult(parentId = "array_id", id = "storage_file", name = "storage_file", type = "array", isNecessary = false, description = "问题存储文件"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
                    @ReturnResult(parentId = "storage_file", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    public Message getProblemCountInfo() {
        {
            final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
            final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.getProblemCountInfo(startCreateDatetime, endCreateDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取问题", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取问题", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "merge_type", text = "合并分类", type = Parameter.Type.INTEGER, allowNull = false, format = "^1|2|3|4$", formatPrompt = "常量1、2、3、4", remark = "1：调用getProblemByFromUuidOrToUuid；2：调用getProblemByLeader；3：调用getProblem；4：调用getProblemExact。注意：传递参数详见各接口所需。"),
            @Parameter(name = "uuid", text = "问题的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "number", text = "编号", type = Parameter.Type.LONG, allowNull = true, format = "^[1-9]\\d*$", formatPrompt = "正整数", remark = ""),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题接收人或单位"),
            @Parameter(name = "type_uuids", text = "类型的uuid的集合", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^UN_FEEDBACK|FEEDBACK|FILED$", formatPrompt = "常量UN_FEEDBACK、FEEDBACK或FILED", remark = "UN_FEEDBACK（未反馈）、FEEDBACK（已反馈）、FILED（已归档）"),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = true, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = true, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始"),
            @Parameter(name = "department_uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = true, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "read", text = "是否已读", type = Parameter.Type.STRING, allowNull = true, format = "^UN_READ|READ$", formatPrompt = "常量UN_READ或READ", remark = "UN_READ（未读）、READ（已读）"),
            @Parameter(name = "fact", text = "是否属实", type = Parameter.Type.STRING, allowNull = true, format = "^UN_CONFIR|NOT_FACT|FACT$", formatPrompt = "常量UN_CONFIR、NOT_FACT或FACT", remark = "UN_CONFIR（未确认）、NOT_FACT（失实）、FACT（属实）"),
            @Parameter(name = "type", text = "类型", type = Parameter.Type.STRING, allowNull = true, format = "^PERSON|DEPARTMENT$", formatPrompt = "常量PERSON或DEPARTMENT", remark = "PERSON（个人）、DEPARTMENT（单位）"),
            @Parameter(name = "responsible_uuid_like", text = "负责人的uuid模糊查询", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "problem_type", text = "问题类型", type = Parameter.Type.STRING, allowNull = true, format = "^SELF|CHECK$", formatPrompt = "常量SELF或CHECK", remark = "SELF（自查）、CHECK（检查）"),
            @Parameter(name = "from_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "to_department_uuid", text = "来源部门的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "start_read_datetime", text = "开始已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_read_datetime", text = "结束已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "返回数据详见各接口") }))
    public Message getProblemMerge() {
        final Message msg = new Message();
        final Integer mergeType = (Integer) this.parameter.get("merge_type");
        if (1 == mergeType.intValue()) {
            return this.getProblemByFromUuidOrToUuid();
        } else if (2 == mergeType.intValue()) {
            return this.getProblemByLeader();
        } else if (3 == mergeType.intValue()) {
            return this.getProblem();
        } else if (4 == mergeType.intValue()) {
            return this.getProblemExact();
        }
        msg.setStatus(Message.Status.ERROR);
        msg.setContent("INVALID_MERGE_TYPE");
        msg.setAttach("非法合并分类");
        return msg;
    }
}