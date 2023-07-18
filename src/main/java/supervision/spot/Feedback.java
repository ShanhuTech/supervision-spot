package supervision.spot;

import java.sql.Connection;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.palestink.server.sdk.module.AbstractModule;
import com.palestink.server.sdk.module.annotation.Frequency;
import com.palestink.server.sdk.module.annotation.Method;
import com.palestink.server.sdk.module.annotation.Module;
import com.palestink.server.sdk.module.annotation.Parameter;
import com.palestink.server.sdk.module.annotation.ReturnResult;
import com.palestink.server.sdk.module.annotation.Returns;
import com.palestink.server.sdk.msg.Message;
import env.db.DruidInstance;

@Module(description = "反馈")
public final class Feedback extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;

    public Feedback() {
    }

    public Feedback(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) throws Exception {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
    }

    @Method(description = "添加反馈", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "problem_uuid", text = "问题的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "反馈发起人"),
            @Parameter(name = "content", text = "内容", type = Parameter.Type.STRING, allowNull = false, format = "^[\\s\\S]{1,512}$", formatPrompt = "1-512位的任意内容", remark = ""),
            @Parameter(name = "file_content", text = "文件内容", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,52428800}$", formatPrompt = "1-52428800位的任意字符（即，10个5MB内的文件）", remark = "base64.length * 0.75 = 文件大小（单位：字节）。如果限制文件大小为2MB，那么base64长度限制为：(2 * 1024 * 1024) / 0.75 = 2796202.67。每个base64文件内容用|分割，需要与后缀顺序、数量匹配。"),
            @Parameter(name = "file_suffix", text = "文件后缀", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,50}$", formatPrompt = "1-10位的任意字符（即，10个后缀）", remark = "每个后缀用|分割，需要与文件内容顺序、数量匹配。后缀常量JPG, JPEG, PNG, TXT, WPS, PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, MP4, AVI, RAR或ZIP") }, returns = @Returns())
    public final Message addFeedback() {
        final Message msg = new Message();
        final String problemUuid = (String) this.parameter.get("problem_uuid");
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String content = (String) this.parameter.get("content");
        final String fileContent = (String) this.parameter.get("file_content");
        final String fileSuffix = (String) this.parameter.get("file_suffix");
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
        // 添加反馈
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Feedback obj = new supervision.spot.dao.Feedback(con);
                final Message resultMsg = obj.addFeedback(problemUuid, fromUuid, content, files);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除反馈", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除反馈", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public final Message removeFeedback() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Feedback obj = new supervision.spot.dao.Feedback(con);
                final Message resultMsg = obj.removeFeedbackByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除反馈", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除反馈", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public final Message deleteFeedback() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Feedback obj = new supervision.spot.dao.Feedback(con);
                final Message resultMsg = obj.deleteFeedbackByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改反馈", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改反馈", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "content", text = "内容", type = Parameter.Type.STRING, allowNull = false, format = "^[\\s\\S]{1,512}$", formatPrompt = "1-512位的任意内容", remark = "") }, returns = @Returns())
    public final Message modifyFeedback() {
        final String uuid = (String) this.parameter.get("uuid");
        final String content = (String) this.parameter.get("content");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Feedback obj = new supervision.spot.dao.Feedback(con);
                final Message resultMsg = obj.modifyFeedback(uuid, content);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取反馈", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取反馈", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = Integer.MAX_VALUE, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "problem_uuid", text = "问题的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "反馈的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[1,16]", isNecessary = false, description = "名称"),
                    @ReturnResult(parentId = "array_id", id = "", name = "order", type = "int", isNecessary = true, description = "排序编号") }))
    public final Message getFeedback() {
        final String uuid = (String) this.parameter.get("uuid");
        final String problemUuid = (String) this.parameter.get("problem_uuid");
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Feedback obj = new supervision.spot.dao.Feedback(con);
                final Message resultMsg = obj.getFeedback(uuid, problemUuid, fromUuid, offset, rows);
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