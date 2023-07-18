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
import supervision.spot.dao.Problem.Fact;
import supervision.spot.dao.Problem.Read;

@Module(description = "问题个人类型")
public final class ProblemPersonType extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;

    public ProblemPersonType() {
    }

    public ProblemPersonType(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) throws Exception {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
    }

    @Method(description = "添加问题个人类型", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "name", text = "名称", type = Parameter.Type.STRING, allowNull = false, format = "^.{1,16}$", formatPrompt = "1-16位的任意字符", remark = ""),
            @Parameter(name = "score", text = "分值", type = Parameter.Type.INTEGER, allowNull = false, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = ""),
            @Parameter(name = "order", text = "排序编号", type = Parameter.Type.INTEGER, allowNull = false, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = "") }, returns = @Returns())
    public final Message addProblemPersonType() {
        final String name = (String) this.parameter.get("name");
        final Integer score = (Integer) this.parameter.get("score");
        final Integer order = (Integer) this.parameter.get("order");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.ProblemPersonType obj = new supervision.spot.dao.ProblemPersonType(con);
                final Message resultMsg = obj.addProblemPersonType(name, score, order);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除问题个人类型", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除问题个人类型", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public final Message removeProblemPersonType() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.ProblemPersonType obj = new supervision.spot.dao.ProblemPersonType(con);
                final Message resultMsg = obj.removeProblemPersonTypeByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改问题个人类型", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改问题个人类型", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "name", text = "名称", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,16}$", formatPrompt = "1-16位的任意字符", remark = ""),
            @Parameter(name = "score", text = "分值", type = Parameter.Type.INTEGER, allowNull = true, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = ""),
            @Parameter(name = "order", text = "排序编号", type = Parameter.Type.INTEGER, allowNull = true, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = "") }, returns = @Returns())
    public final Message modifyProblemPersonType() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String name = (String) this.parameter.get("name");
        final Integer score = (Integer) this.parameter.get("score");
        final Integer order = (Integer) this.parameter.get("order");
        // 至少修改一项字段
        {
            if ((null == name) && (null == score) && (null == order)) {
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
                final supervision.spot.dao.ProblemPersonType obj = new supervision.spot.dao.ProblemPersonType(con);
                final Message resultMsg = obj.modifyProblemPersonType(uuid, name, score, order);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取问题个人类型", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 10, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {}, returns = @Returns(results = {
            @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
            @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问题个人类型的uuid"),
            @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[1,16]", isNecessary = false, description = "名称"), @ReturnResult(parentId = "array_id", id = "", name = "score", type = "int", isNecessary = true, description = "分值"),
            @ReturnResult(parentId = "array_id", id = "", name = "order", type = "int", isNecessary = true, description = "排序编号") }))
    public final Message getProblemPersonType() {
        Connection con = null;
        try {
            con = DruidInstance.getInstance().getTransConnection();
            final supervision.spot.dao.ProblemPersonType obj = new supervision.spot.dao.ProblemPersonType(con);
            final Message resultMsg = obj.getProblemPersonType(null, null);
            this.messageResultHandler(resultMsg, con, true);
            return resultMsg;
        } catch (final Exception e) {
            return this.catchHandler(con, e);
        } finally {
            this.finallyHandler(con);
        }
    }

    @Method(description = "获取问题个人类型数量", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "read", text = "是否已读", type = Parameter.Type.STRING, allowNull = true, format = "^UN_READ|READ$", formatPrompt = "常量UN_READ或READ", remark = "UN_READ（未读）、READ（已读）"),
            @Parameter(name = "fact", text = "是否属实", type = Parameter.Type.STRING, allowNull = true, format = "^UN_CONFIR|NOT_FACT|FACT$", formatPrompt = "常量UN_CONFIR、NOT_FACT或FACT", remark = "UN_CONFIR（未确认）、NOT_FACT（失实）、FACT（属实）"),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^UN_FEEDBACK|FEEDBACK|FILED$", formatPrompt = "常量UN_FEEDBACK、FEEDBACK或FILED", remark = "UN_FEEDBACK（未反馈）、FEEDBACK（已反馈）、FILED（已归档）"),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "部门的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[1,64]", isNecessary = true, description = "部门名称"),
                    @ReturnResult(parentId = "array_id", id = "", name = "problem_count", type = "int", isNecessary = true, description = "问题数量") }))
    public Message getProblemPersonTypeCount() {
        final String uuid = (String) this.parameter.get("uuid");
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
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.ProblemPersonType obj = new supervision.spot.dao.ProblemPersonType(con);
                final Message resultMsg = obj.getProblemPersonTypeCount(uuid, read, fact, status, startCreateDatetime, endCreateDatetime);
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