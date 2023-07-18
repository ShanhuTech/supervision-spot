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

@Module(description = "评论")
public final class Comment extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;

    public Comment() {
    }

    public Comment(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) throws Exception {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
    }

    @Method(description = "添加评论", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
        @Parameter(name = "problem_uuid", text = "问题的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
        @Parameter(name = "author_uuid", text = "作者的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
        @Parameter(name = "author_name", text = "作者名称", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,16}$", formatPrompt = "1-16位任意字符", remark = ""),
        @Parameter(name = "author_real_name", text = "作者真实姓名", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,16}$", formatPrompt = "1-16位任意字符", remark = ""),
        @Parameter(name = "title", text = "标题", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,512}$", formatPrompt = "1-512位任意字符", remark = ""),
        @Parameter(name = "content", text = "内容", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,512}$", formatPrompt = "1-512位任意字符", remark = ""),
        @Parameter(name = "remark", text = "备注", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,512}$", formatPrompt = "1-512位任意字符", remark = ""),
        @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,16}$", formatPrompt = "1-16位任意字符", remark = ""),
        @Parameter(name = "detail", text = "详情", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,1024}$", formatPrompt = "1-1024位任意字符", remark = "") }, returns = @Returns())
    public final Message addComment() {
        final String problemUuid = (String) this.parameter.get("problem_uuid");
        final String authorUuid = (String) this.parameter.get("author_uuid");
        final String authorName = (String) this.parameter.get("author_name");
        final String authorRealName = (String) this.parameter.get("author_real_name");
        final String title = (String) this.parameter.get("title");
        final String content = (String) this.parameter.get("content");
        final String remark = (String) this.parameter.get("remark");
        final String status = (String) this.parameter.get("status");
        final String detail = (String) this.parameter.get("detail");
        // 添加评论
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Comment obj = new supervision.spot.dao.Comment(con);
                final Message resultMsg = obj.addComment(problemUuid, authorUuid, authorName, authorRealName, title, content, remark, status, detail);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除评论", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
        @Parameter(name = "uuid", text = "待删除评论", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public final Message removeComment() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Comment obj = new supervision.spot.dao.Comment(con);
                final Message resultMsg = obj.removeCommentByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改评论", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
        @Parameter(name = "uuid", text = "待修改评论", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
        @Parameter(name = "author_uuid", text = "作者的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
        @Parameter(name = "author_name", text = "作者名称", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,16}$", formatPrompt = "1-16位任意字符", remark = ""),
        @Parameter(name = "author_real_name", text = "作者真实姓名", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,16}$", formatPrompt = "1-16位任意字符", remark = ""),
        @Parameter(name = "title", text = "标题", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,512}$", formatPrompt = "1-512位任意字符", remark = ""),
        @Parameter(name = "content", text = "内容", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,512}$", formatPrompt = "1-512位任意字符", remark = ""),
        @Parameter(name = "remark", text = "备注", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,512}$", formatPrompt = "1-512位任意字符", remark = ""),
        @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,16}$", formatPrompt = "1-16位任意字符", remark = ""),
        @Parameter(name = "detail", text = "详情", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,1024}$", formatPrompt = "1-1024位任意字符", remark = "") }, returns = @Returns())
    public final Message modifyComment() {
        final String uuid = (String) this.parameter.get("uuid");
        final String authorUuid = (String) this.parameter.get("author_uuid");
        final String authorName = (String) this.parameter.get("author_name");
        final String authorRealName = (String) this.parameter.get("author_real_name");
        final String title = (String) this.parameter.get("title");
        final String content = (String) this.parameter.get("content");
        final String remark = (String) this.parameter.get("remark");
        final String status = (String) this.parameter.get("status");
        final String detail = (String) this.parameter.get("detail");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Comment obj = new supervision.spot.dao.Comment(con);
                final Message resultMsg = obj.modifyComment(uuid, authorUuid, authorName, authorRealName, title, content, remark, status, detail);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取评论", anonymousAccess = true, frequencys = {
        @Frequency(source = Frequency.Source.IP, count = Integer.MAX_VALUE, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "problem_uuid", text = "问题的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "author_uuid", text = "作者的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "问题发起人"),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = false, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
                @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "评论的uuid"),
                @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[1,16]", isNecessary = false, description = "名称"),
                @ReturnResult(parentId = "array_id", id = "", name = "order", type = "int", isNecessary = true, description = "排序编号") }))
    public final Message getComment() {
        final String uuid = (String) this.parameter.get("uuid");
        final String problemUuid = (String) this.parameter.get("problem_uuid");
        final String authorUuid = (String) this.parameter.get("author_uuid");
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Comment obj = new supervision.spot.dao.Comment(con);
                final Message resultMsg = obj.getComment(uuid, problemUuid, authorUuid, offset, rows);
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