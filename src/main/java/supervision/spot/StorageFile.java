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
import supervision.spot.dao.StorageFile.AssociateType;
import supervision.spot.dao.StorageFile.Suffix;

@Module(description = "存储文件")
public class StorageFile extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;
    // private Account account;

    public StorageFile() {
    }

    public StorageFile(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
        // this.account = new Account(this.httpServlet, this.httpServletRequest, this.httpServletResponse, this.parameter);
    }

    @Method(description = "添加存储文件", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "associate_type", text = "关联类型", type = Parameter.Type.STRING, allowNull = false, format = "^PROBLEM|FEEDBACK$", formatPrompt = "常量PROBLEM或FEEDBACK", remark = "PROBLEM:问题；FEEDBACK：反馈"),
            @Parameter(name = "associate_uuid", text = "关联的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "suffix", text = "后缀", type = Parameter.Type.STRING, allowNull = false, format = "^JPG|JPEG|PNG|TXT|WPS|PDF|DOC|DOCX|XLS|XLSX|PPT|PPTX|MP4|AVI|RAR|ZIP$", formatPrompt = "常量JPG、JPEG、PNG、TXT、WPS、PDF、DOC、DOCX、XLS、XLSX、PPT、PPTX、MP4、AVI、RAR或ZIP", remark = "允许上传的文件类型"),
            @Parameter(name = "file_url", text = "文件地址", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,256}$", formatPrompt = "1-256位的任意字符", remark = ""),
            @Parameter(name = "file_base64", text = "文件内容", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,5242880}$", formatPrompt = "1-5242880位的任意字符（即，5MB内的文件）", remark = "base64.length * 0.75 = 文件大小（单位：字节）。如果限制文件大小为2MB，那么base64长度限制为：(2 * 1024 * 1024) / 0.75 = 2796202.67") }, returns = @Returns())
    public Message addStorageFile() {
        final Message msg = new Message();
        final String associateTypeStr = (String) this.parameter.get("associate_type");
        final AssociateType associateType = AssociateType.valueOf(associateTypeStr);
        final String associateUuid = (String) this.parameter.get("associate_uuid");
        final String suffixStr = (String) this.parameter.get("suffix");
        final Suffix suffix = Suffix.valueOf(suffixStr);
        final String fileUrl = (String) this.parameter.get("file_url");
        String fileBase64 = (String) this.parameter.get("file_base64");
        if (null != fileBase64) {
            fileBase64 = fileBase64.contains(",") ? fileBase64.substring(fileBase64.indexOf(",") + 1)/* 去除base64类型头 */ : fileBase64; // 文件内容
        }
        // 至少有一项字段
        {
            if ((null == fileUrl) && (null == fileBase64)) {
                msg.setStatus(Message.Status.ERROR);
                msg.setContent("FILE_NOT_ALLOW_NULL");
                msg.setAttach("文件不允许为空");
                return msg;
            }
        }
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.StorageFile obj = new supervision.spot.dao.StorageFile(con);
                final Message resultMsg = obj.addStorageFile(associateType, associateUuid, suffix, fileUrl, fileBase64);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "批量添加存储文件", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "associate_type", text = "关联类型", type = Parameter.Type.STRING, allowNull = false, format = "^PROBLEM|FEEDBACK$", formatPrompt = "常量PROBLEM或FEEDBACK", remark = "PROBLEM:问题；FEEDBACK：反馈"),
            @Parameter(name = "associate_uuid", text = "关联的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "file_content", text = "文件内容", type = Parameter.Type.STRING, allowNull = false, format = "^.{1,52428800}$", formatPrompt = "1-52428800位的任意字符（即，10个5MB内的文件）", remark = "base64.length * 0.75 = 文件大小（单位：字节）。如果限制文件大小为2MB，那么base64长度限制为：(2 * 1024 * 1024) / 0.75 = 2796202.67。每个base64文件内容用|分割，需要与后缀顺序、数量匹配。"),
            @Parameter(name = "file_suffix", text = "文件后缀", type = Parameter.Type.STRING, allowNull = false, format = "^.{1,50}$", formatPrompt = "1-10位的任意字符（即，10个后缀）", remark = "每个后缀用|分割，需要与文件内容顺序、数量匹配。后缀常量JPG, JPEG, PNG, TXT, WPS, PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, MP4, AVI, RAR或ZIP") }, returns = @Returns())
    public Message addStorageFileBatch() {
        final Message msg = new Message();
        final String associateTypeStr = (String) this.parameter.get("associate_type");
        final AssociateType associateType = AssociateType.valueOf(associateTypeStr);
        final String associateUuid = (String) this.parameter.get("associate_uuid");
        final String fileContent = (String) this.parameter.get("file_content");
        final String fileSuffix = (String) this.parameter.get("file_suffix");
        String[][] files = null;
        // 检查文件内容与后缀
        {
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
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.StorageFile obj = new supervision.spot.dao.StorageFile(con);
                final Message resultMsg = obj.addStorageFile(associateType, associateUuid, files);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除存储文件", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除存储文件", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public Message removeStorageFile() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.StorageFile obj = new supervision.spot.dao.StorageFile(con);
                final Message resultMsg = obj.removeStorageFileByUuid(uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "修改存储文件", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    // @Parameter(name = "uuid", text = "待修改存储文件", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
    // @Parameter(name = "associate_uuid", text = "关联的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
    // @Parameter(name = "suffix", text = "后缀", type = Parameter.Type.STRING, allowNull = false, format = "^JPG|JPEG|PNG|TXT|WPS|PDF|DOC|DOCX|XLS|XLSX|PPT|PPTX|MP4|AVI|RAR|ZIP$", formatPrompt = "常量JPG、JPEG、PNG、TXT、WPS、PDF、DOC、DOCX、XLS、XLSX、PPT、PPTX、MP4、AVI、RAR或ZIP", remark = "允许上传的文件类型"),
    // @Parameter(name = "file_url", text = "文件地址", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,256}$", formatPrompt = "1-256位的任意字符", remark = ""),
    // @Parameter(name = "file_base64", text = "文件内容", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,5242880}$", formatPrompt = "1-5242880位的任意字符（即，5MB内的图片）", remark = "base64.length * 0.75 = 文件大小（单位：字节）。如果限制文件大小为2MB，那么base64长度限制为：(2 * 1024 * 1024) / 0.75 = 2796202.67") }, returns = @Returns())
    // public Message modifyStorageFile() {
    // final Message msg = new Message();
    // final String uuid = (String) this.parameter.get("uuid");
    // final String associateUuid = (String) this.parameter.get("associate_uuid");
    // final String suffixStr = (String) this.parameter.get("suffix");
    // final Suffix suffix = Suffix.valueOf(suffixStr);
    // final String fileUrl = (String) this.parameter.get("file_url");
    // final String fileBase64 = (String) this.parameter.get("file_base64");
    // // 至少有一项字段
    // {
    // if ((null == fileUrl) && (null == fileBase64)) {
    // msg.setStatus(Message.Status.ERROR);
    // msg.setContent("FILE_NOT_ALLOW_NULL");
    // msg.setAttach("文件不允许为空");
    // return msg;
    // }
    // }
    // {
    // Connection con = null;
    // try {
    // con = DruidInstance.getInstance().getTransConnection();
    // final supervision.spot.dao.StorageFile obj = new supervision.spot.dao.StorageFile(con);
    // final Message resultMsg = obj.modifyStorageFile(uuid, null, associateUuid, suffix, fileUrl, fileBase64);
    // this.messageResultHandler(resultMsg, con, true);
    // return resultMsg;
    // } catch (final Exception e) {
    // return this.catchHandler(con, e);
    // } finally {
    // this.finallyHandler(con);
    // }
    // }
    // }

    @Method(description = "获取存储文件", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 10, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "存储文件的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "associate_uuid", text = "关联的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "存储文件的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "associate_uuid", type = "string[1,40]", isNecessary = true, description = "关联的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "suffix", type = "string[1,16]", isNecessary = false, description = "后缀"),
                    @ReturnResult(parentId = "array_id", id = "", name = "url", type = "string[1,256]", isNecessary = false, description = "地址"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间") }))
    public Message getStorageFile() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String associateUuid = (String) this.parameter.get("associate_uuid");
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        // 至少有一项字段
        {
            if ((null == uuid) && (null == associateUuid)) {
                msg.setStatus(Message.Status.ERROR);
                msg.setContent("UUID_ASSOCIATE_UUID_NOT_ALLOW_NULL");
                msg.setAttach("uuid或关联的uuid不允许为空");
                return msg;
            }
        }
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.StorageFile obj = new supervision.spot.dao.StorageFile(con);
                final Message resultMsg = obj.getStorageFile(uuid, associateUuid, null, offset, rows);
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