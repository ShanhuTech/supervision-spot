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

@Module(description = "问责类型")
public final class AccountableType extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;

    public AccountableType() {
    }

    public AccountableType(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) throws Exception {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
    }

    @Method(description = "添加问责类型", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "name", text = "名称", type = Parameter.Type.STRING, allowNull = false, format = "^.{1,16}$", formatPrompt = "1-16位的任意字符", remark = ""),
            @Parameter(name = "order", text = "排序编号", type = Parameter.Type.INTEGER, allowNull = false, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = "") }, returns = @Returns())
    public final Message addAccountableType() {
        final String name = (String) this.parameter.get("name");
        final Integer order = (Integer) this.parameter.get("order");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.AccountableType obj = new supervision.spot.dao.AccountableType(con);
                final Message resultMsg = obj.addAccountableType(name, order);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除问责类型", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除问责类型", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public final Message removeAccountableType() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.AccountableType obj = new supervision.spot.dao.AccountableType(con);
                final Message resultMsg = obj.removeAccountableTypeByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改问责类型", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改问责类型", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "name", text = "名称", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,16}$", formatPrompt = "1-16位的任意字符", remark = ""),
            @Parameter(name = "order", text = "排序编号", type = Parameter.Type.INTEGER, allowNull = true, format = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])$", formatPrompt = "1-255之间的正整数", remark = "") }, returns = @Returns())
    public final Message modifyAccountableType() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String name = (String) this.parameter.get("name");
        final Integer order = (Integer) this.parameter.get("order");
        // 至少修改一项字段
        {
            if ((null == name) && (null == order)) {
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
                final supervision.spot.dao.AccountableType obj = new supervision.spot.dao.AccountableType(con);
                final Message resultMsg = obj.modifyAccountableType(uuid, name, order);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取问责类型", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 10, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {}, returns = @Returns(results = {
            @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
            @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "问责类型的uuid"),
            @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[1,16]", isNecessary = false, description = "名称"), @ReturnResult(parentId = "array_id", id = "", name = "order", type = "int", isNecessary = true, description = "排序编号") }))
    public final Message getAccountableType() {
        Connection con = null;
        try {
            con = DruidInstance.getInstance().getTransConnection();
            final supervision.spot.dao.AccountableType obj = new supervision.spot.dao.AccountableType(con);
            final Message resultMsg = obj.getAccountableType(null, null);
            this.messageResultHandler(resultMsg, con, true);
            return resultMsg;
        } catch (final Exception e) {
            return this.catchHandler(con, e);
        } finally {
            this.finallyHandler(con);
        }
    }
}