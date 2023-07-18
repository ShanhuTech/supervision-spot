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
import env.db.DruidInstance;
import supervision.spot.dao.Message.Read;

@Module(description = "消息")
public class Message extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;
    // private Account account;

    public Message() {
    }

    public Message(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
        // this.account = new Account(this.httpServlet, this.httpServletRequest, this.httpServletResponse, this.parameter);
    }

    @Method(description = "添加消息", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{2,40}$"/* 注意：这里必须是大于2的参数，因为from为0时，标记为系统消息 */, formatPrompt = "1-40位的数字或大小写字母", remark = "消息发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "消息接收人"),
            @Parameter(name = "role_name", text = "角色名称", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z_-]{4,32}$", formatPrompt = "4-32位的数字、大小写字母、下划线或横线", remark = ""),
            @Parameter(name = "title", text = "标题", type = Parameter.Type.STRING, allowNull = false, format = "^.{1,64}$", formatPrompt = "1-64位的任意字符", remark = ""),
            @Parameter(name = "content", text = "内容", type = Parameter.Type.STRING, allowNull = false, format = "^.{1,1024}$", formatPrompt = "1-1024位的任意字符", remark = ""),
            @Parameter(name = "remark", text = "备注", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,1024}$", formatPrompt = "1-1024位的任意字符", remark = "") }, returns = @Returns())
    public com.palestink.server.sdk.msg.Message addMessage() {
        final com.palestink.server.sdk.msg.Message msg = new com.palestink.server.sdk.msg.Message();
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String roleName = (String) this.parameter.get("role_name");
        final String title = (String) this.parameter.get("title");
        final String content = (String) this.parameter.get("content");
        final String remark = (String) this.parameter.get("remark");
        // 至少填写一项字段
        {
            if ((null == toUuid) && (null == roleName)) {
                msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                msg.setContent("TO_UUID_AND_ROLE_UUID_NOT_BE_NULL_AT_LEAST");
                msg.setAttach("to_uuid和role_uuid至少一个不为空");
                return msg;
            }
        }
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Message obj = new supervision.spot.dao.Message(con);
                final com.palestink.server.sdk.msg.Message resultMsg = obj.addMessage(fromUuid, toUuid, roleName, title, content, remark);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除消息", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除消息", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public com.palestink.server.sdk.msg.Message removeMessage() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Message obj = new supervision.spot.dao.Message(con);
                final com.palestink.server.sdk.msg.Message resultMsg = obj.removeMessageByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改消息", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改消息", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "read", text = "是否已读", type = Parameter.Type.STRING, allowNull = true, format = "^UN_READ|READ$", formatPrompt = "常量UN_READ或READ", remark = "UN_READ（未读）、READ（已读）") }, returns = @Returns())
    public com.palestink.server.sdk.msg.Message modifyMessage() {
        final com.palestink.server.sdk.msg.Message msg = new com.palestink.server.sdk.msg.Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String readStr = (String) this.parameter.get("read");
        Read read = null;
        if (null != readStr) {
            read = Read.valueOf(readStr);
        }
        // 至少修改一项字段
        {
            if ((null == readStr) && (null == read)) {
                msg.setStatus(com.palestink.server.sdk.msg.Message.Status.ERROR);
                msg.setContent("NO_DATA_MODIFIED");
                msg.setAttach("没有修改数据");
                return msg;
            }
        }
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Message obj = new supervision.spot.dao.Message(con);
                final com.palestink.server.sdk.msg.Message resultMsg = obj.modifyMessage(uuid, read);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取消息", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "消息的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "from_uuid", text = "来源的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "消息发起人"),
            @Parameter(name = "to_uuid", text = "目标的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "消息接收人"),
            @Parameter(name = "remark", text = "备注", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,1024}$", formatPrompt = "1-1024位的任意字符", remark = ""),
            @Parameter(name = "read", text = "是否已读", type = Parameter.Type.STRING, allowNull = true, format = "^UN_READ|READ$", formatPrompt = "常量UN_READ或READ", remark = "UN_READ（未读）、READ（已读）"),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "start_read_datetime", text = "开始已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_read_datetime", text = "结束已读时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "消息的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "from_uuid", type = "string[1,40]", isNecessary = true, description = "来源的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "to_uuid", type = "string[1,40]", isNecessary = true, description = "目标的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "title", type = "string[1,64]", isNecessary = true, description = "标题"),
                    @ReturnResult(parentId = "array_id", id = "", name = "content", type = "string[1,512]", isNecessary = true, description = "内容"),
                    @ReturnResult(parentId = "array_id", id = "", name = "remark", type = "string[1,1024]", isNecessary = true, description = "备注"),
                    @ReturnResult(parentId = "array_id", id = "", name = "is_read", type = "string[1,8]", isNecessary = true, description = "是否已读（UN_READ：未读；READ：已读）"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "read_datetime", type = "string[1,30]", isNecessary = true, description = "已读时间") }))
    public com.palestink.server.sdk.msg.Message getMessage() {
        final String uuid = (String) this.parameter.get("uuid");
        final String fromUuid = (String) this.parameter.get("from_uuid");
        final String toUuid = (String) this.parameter.get("to_uuid");
        final String remark = (String) this.parameter.get("remark");
        final String readStr = (String) this.parameter.get("read");
        Read read = null;
        if (null != readStr) {
            read = Read.valueOf(readStr);
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
                final supervision.spot.dao.Message obj = new supervision.spot.dao.Message(con);
                final com.palestink.server.sdk.msg.Message resultMsg = obj.getMessage(uuid, fromUuid, toUuid, remark, read, startCreateDatetime, endCreateDatetime, startReadDatetime, endReadDatetime, offset, rows);
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