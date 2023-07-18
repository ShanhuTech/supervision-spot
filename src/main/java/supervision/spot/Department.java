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
import security.Account;
import supervision.spot.dao.Problem.Fact;
import supervision.spot.dao.Problem.Read;

@Module(description = "单位")
public class Department extends AbstractModule {
    private HttpServlet httpServlet;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;
    @SuppressWarnings("unused")
    private Account account;

    public Department() {
    }

    public Department(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
        this.account = new Account(this.httpServlet, this.httpServletRequest, this.httpServletResponse, this.parameter);
    }

    @Method(description = "添加单位", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "parent_uuid", text = "父级单位的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "name", text = "名称", type = Parameter.Type.STRING, allowNull = false, format = "^.{1,64}$", formatPrompt = "1-64位的任意字符", remark = ""),
            @Parameter(name = "order", text = "排序编号", type = Parameter.Type.INTEGER, allowNull = false, format = "^[1-9]\\d*|0$", formatPrompt = "正整数", remark = "") }, returns = @Returns())
    public Message addDepartment() {
        final String parentUuid = (String) this.parameter.get("parent_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final String name = (String) this.parameter.get("name");
        final Integer order = (Integer) this.parameter.get("order");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Department obj = new supervision.spot.dao.Department(con);
                final Message resultMsg = obj.addDepartment(parentUuid, typeUuid, name, order);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "删除单位", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待删除单位", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = "") }, returns = @Returns())
    public Message removeDepartment() {
        final String uuid = (String) this.parameter.get("uuid");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Department obj = new supervision.spot.dao.Department(con);
                final Message resultMsg = obj.removeDepartmentByUuid(true, uuid);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改单位", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改单位", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "parent_uuid", text = "父级单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "name", text = "名称", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,64}$", formatPrompt = "1-64位的任意字符", remark = ""),
            @Parameter(name = "order", text = "排序编号", type = Parameter.Type.INTEGER, allowNull = true, format = "^[1-9]\\d*|0$", formatPrompt = "正整数", remark = "") }, returns = @Returns())
    public Message modifyDepartment() {
        final Message msg = new Message();
        final String uuid = (String) this.parameter.get("uuid");
        final String parentUuid = (String) this.parameter.get("parent_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final String name = (String) this.parameter.get("name");
        final Integer order = (Integer) this.parameter.get("order");
        // 至少修改一项字段
        {
            if ((null == parentUuid) && (null == typeUuid) && (null == name) && (null == order)) {
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
                final supervision.spot.dao.Department obj = new supervision.spot.dao.Department(con);
                final Message resultMsg = obj.modifyDepartment(uuid, parentUuid, typeUuid, name, null, order);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "修改单位问题类型", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 10, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "待修改单位", type = Parameter.Type.STRING, allowNull = false, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "problem_types", text = "问题类型", type = Parameter.Type.STRING, allowNull = true, format = "^([0-9a-zA-Z]{2,32};)+$", formatPrompt = "以分号分割的2-32位的数字或大小写字母集合", remark = "") }, returns = @Returns())
    public final Message modifyProblemType() {
        final String uuid = (String) this.parameter.get("uuid");
        final String problemTypes = (String) this.parameter.get("problem_types");
        // 修改角色
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Department obj = new supervision.spot.dao.Department(con);
                final Message resultMsg = obj.modifyDepartment(uuid, null, null, null, (null == problemTypes) ? "" : problemTypes, null);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取单位", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取单位", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = Integer.MAX_VALUE, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "parent_uuid", text = "父级单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "level", text = "级别", type = Parameter.Type.INTEGER, allowNull = true, format = "^[1-9]\\d*$", formatPrompt = "大于0的正整数", remark = ""),
            @Parameter(name = "name", text = "名称", type = Parameter.Type.STRING, allowNull = true, format = "^.{1,64}$", formatPrompt = "1-64位任意字符", remark = ""),
            @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = true/* 需要获取全部一级单位，所以允许为空 */, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
            @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = true/* 需要获取全部一级单位，所以允许为空 */, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "单位的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "parent_uuid", type = "string[1,40]", isNecessary = true, description = "父级单位的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = false, description = "类型的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "type_name", type = "string[1,16]", isNecessary = false, description = "类型名称"),
                    @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[1,64]", isNecessary = true, description = "名称"),
                    @ReturnResult(parentId = "array_id", id = "", name = "level", type = "int", isNecessary = true, description = "级别"), @ReturnResult(parentId = "array_id", id = "", name = "order", type = "int", isNecessary = true, description = "排序编号"),
                    @ReturnResult(parentId = "array_id", id = "", name = "order_group", type = "string[1,60]", isNecessary = true, description = "排序编号组"),
                    @ReturnResult(parentId = "array_id", id = "", name = "problem_types", type = "list", isNecessary = false, description = "问题类型列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
                    @ReturnResult(parentId = "array_id", id = "", name = "full_name", type = "string[1,]", isNecessary = true, description = "组合的全称"),
                    @ReturnResult(parentId = "array_id", id = "parent_array_id", name = "parent_array", type = "array", isNecessary = false, description = "父级数组列表"),
                    @ReturnResult(parentId = "parent_array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "单位的uuid"),
                    @ReturnResult(parentId = "parent_array_id", id = "", name = "parent_uuid", type = "string[1,40]", isNecessary = true, description = "父级单位的uuid"),
                    @ReturnResult(parentId = "parent_array_id", id = "", name = "name", type = "string[1,64]", isNecessary = true, description = "名称"),
                    @ReturnResult(parentId = "parent_array_id", id = "", name = "level", type = "int", isNecessary = true, description = "级别") }))
    public Message getDepartment() {
        final String uuid = (String) this.parameter.get("uuid");
        final String parentUuid = (String) this.parameter.get("parent_uuid");
        final String typeUuid = (String) this.parameter.get("type_uuid");
        final Integer level = (Integer) this.parameter.get("level");
        final String name = (String) this.parameter.get("name");
        final Integer offset = (Integer) this.parameter.get("offset");
        final Integer rows = (Integer) this.parameter.get("rows");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Department obj = new supervision.spot.dao.Department(con);
                final Message resultMsg = obj.getDepartment(uuid, parentUuid, typeUuid, level, name, null, null, offset, rows);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    @Method(description = "获取单位工作量", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "admin_uuid", type = "string[1,40]", isNecessary = true, description = "管理员的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "problem_count", type = "int", isNecessary = true, description = "问题的数量"),
                    @ReturnResult(parentId = "array_id", id = "", name = "department_uuid", type = "string[1,40]", isNecessary = true, description = "部门的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "department_name", type = "string[1,65]", isNecessary = true, description = "部门名称") }))
    public Message getDepartmentWorkload() {
        final String uuid = (String) this.parameter.get("uuid");
        final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
        final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Department obj = new supervision.spot.dao.Department(con);
                final Message resultMsg = obj.getDepartmentWorkload(uuid, startCreateDatetime, endCreateDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取单位工作量", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取单位工作量", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "level", text = "级别", type = Parameter.Type.INTEGER, allowNull = true, format = "^[1-9]\\d*$", formatPrompt = "大于0的正整数", remark = ""),
            @Parameter(name = "type_uuids", text = "类型的uuid的集合", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}(;[0-9a-zA-Z]{1,40})*$", formatPrompt = "以分号分割的1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "admin_uuid", type = "string[1,40]", isNecessary = true, description = "管理员的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "problem_count", type = "int", isNecessary = true, description = "问题的数量"),
                    @ReturnResult(parentId = "array_id", id = "", name = "department_uuid", type = "string[1,40]", isNecessary = true, description = "部门的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "department_name", type = "string[1,65]", isNecessary = true, description = "部门名称") }))
    public Message getDepartmentWorkloadByLT() {
        final String uuid = (String) this.parameter.get("uuid");
        final Integer level = (Integer) this.parameter.get("level");
        final String typeUuids = (String) this.parameter.get("type_uuids");
        String[] typeUuidArray = null;
        if (null != typeUuids) {
            typeUuidArray = typeUuids.split(";");
        }
        final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
        final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Department obj = new supervision.spot.dao.Department(con);
                final Message resultMsg = obj.getDepartmentWorkload(uuid, level, typeUuidArray, startCreateDatetime, endCreateDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取单位工作量", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取单位工作量", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") 
    }, returns = @Returns(results = {
            @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"), @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[1,40]", isNecessary = true, description = "部门名称"),
            @ReturnResult(parentId = "array_id", id = "", name = "count", type = "int", isNecessary = true, description = "问题的数量") }))
    public Message getDepartmentWorkloadFromCache() {
        final String startCreateDatetime = (String) this.parameter.get("start_create_datetime");
        final String endCreateDatetime = (String) this.parameter.get("end_create_datetime");
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Department obj = new supervision.spot.dao.Department(con);
                final Message resultMsg = obj.getDepartmentWorkloadFromCache(startCreateDatetime, endCreateDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取单位问题数量", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    @Method(description = "获取单位问题数量", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "level", text = "级别", type = Parameter.Type.INTEGER, allowNull = true, format = "^[1-9]\\d*$", formatPrompt = "大于0的正整数", remark = ""),
            @Parameter(name = "order_group", text = "排序编号组", type = Parameter.Type.STRING, allowNull = true, format = "^\\d{1,60}$", formatPrompt = "1-60位的数字", remark = ""),
            @Parameter(name = "type_uuid", text = "问题类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
            @Parameter(name = "read", text = "是否已读", type = Parameter.Type.STRING, allowNull = true, format = "^UN_READ|READ$", formatPrompt = "常量UN_READ或READ", remark = "UN_READ（未读）、READ（已读）"),
            @Parameter(name = "fact", text = "是否属实", type = Parameter.Type.STRING, allowNull = true, format = "^UN_CONFIR|NOT_FACT|FACT$", formatPrompt = "常量UN_CONFIR、NOT_FACT或FACT", remark = "UN_CONFIR（未确认）、NOT_FACT（失实）、FACT（属实）"),
            @Parameter(name = "status", text = "状态", type = Parameter.Type.STRING, allowNull = true, format = "^UN_FEEDBACK|FEEDBACK|FILED$", formatPrompt = "常量UN_FEEDBACK、FEEDBACK或FILED", remark = "UN_FEEDBACK（未反馈）、FEEDBACK（已反馈）、FILED（已归档）"),
            @Parameter(name = "start_create_datetime", text = "开始创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33"),
            @Parameter(name = "end_create_datetime", text = "结束创建时间", type = Parameter.Type.STRING, allowNull = true, format = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", formatPrompt = "yyyy-MM-dd HH:mm:ss时间格式", remark = "如：1994-06-17 09:40:33") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
                    @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "部门的uuid"),
                    @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[1,64]", isNecessary = true, description = "部门名称"),
                    @ReturnResult(parentId = "array_id", id = "", name = "problem_count", type = "int", isNecessary = true, description = "问题数量") }))
    public Message getDepartmentProblemCount() {
        final String uuid = (String) this.parameter.get("uuid");
        final Integer level = (Integer) this.parameter.get("level");
        final String orderGroup = (String) this.parameter.get("order_group");
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
        {
            Connection con = null;
            try {
                con = DruidInstance.getInstance().getTransConnection();
                final supervision.spot.dao.Department obj = new supervision.spot.dao.Department(con);
                final Message resultMsg = obj.getDepartmentProblemCount(uuid, level, orderGroup, typeUuid, read, fact, status, startCreateDatetime, endCreateDatetime);
                this.messageResultHandler(resultMsg, con, true);
                return resultMsg;
            } catch (final Exception e) {
                return this.catchHandler(con, e);
            } finally {
                this.finallyHandler(con);
            }
        }
    }

    // @Method(description = "获取单位（自身）", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 10, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
    // @Parameter(name = "uuid", text = "单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
    // @Parameter(name = "parent_uuid", text = "父级单位的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
    // @Parameter(name = "level_uuid", text = "等级的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
    // @Parameter(name = "type_uuid", text = "类型的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
    // @Parameter(name = "attribute_uuid", text = "属性的uuid", type = Parameter.Type.STRING, allowNull = true, format = "^[0-9a-zA-Z]{1,40}$", formatPrompt = "1-40位的数字或大小写字母", remark = ""),
    // @Parameter(name = "level", text = "级别", type = Parameter.Type.INTEGER, allowNull = true, format = "^[1-9]\\d*$", formatPrompt = "大于0的正整数", remark = ""),
    // @Parameter(name = "has_camera", text = "是否有摄像头", type = Parameter.Type.INTEGER, allowNull = true, format = "^0|1$", formatPrompt = "常量0或1", remark = "0（否）、1（是）"),
    // @Parameter(name = "offset", text = "查询的偏移", type = Parameter.Type.INTEGER, allowNull = true/* 需要获取全部一级单位，所以允许为空 */, format = "^[0-9]{1,10}$", formatPrompt = "1-10位的数字", remark = "从0开始"),
    // @Parameter(name = "rows", text = "查询的行数", type = Parameter.Type.INTEGER, allowNull = true/* 需要获取全部一级单位，所以允许为空 */, format = "^[0-9]{1,3}$", formatPrompt = "1-3位的数字", remark = "从1开始") }, returns = @Returns(results = {
    // @ReturnResult(parentId = "", id = "", name = "count", type = "int", isNecessary = true, description = "数量（没有找到数据count小于等于0）"),
    // @ReturnResult(parentId = "", id = "array_id", name = "array", type = "array", isNecessary = true, description = "列表"),
    // @ReturnResult(parentId = "array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "单位的uuid"),
    // @ReturnResult(parentId = "array_id", id = "", name = "parent_uuid", type = "string[1,40]", isNecessary = true, description = "父级单位的uuid"),
    // @ReturnResult(parentId = "array_id", id = "", name = "level_uuid", type = "string[1,40]", isNecessary = true, description = "等级的uuid"),
    // @ReturnResult(parentId = "array_id", id = "", name = "type_uuid", type = "string[1,40]", isNecessary = false, description = "类型的uuid"),
    // @ReturnResult(parentId = "array_id", id = "", name = "type_name", type = "string[1,16]", isNecessary = false, description = "类型名称"),
    // @ReturnResult(parentId = "array_id", id = "", name = "attribute_uuid", type = "string[1,40]", isNecessary = false, description = "属性的uuid"),
    // @ReturnResult(parentId = "array_id", id = "", name = "attribute_name", type = "string[1,16]", isNecessary = false, description = "属性名称"),
    // @ReturnResult(parentId = "array_id", id = "", name = "name", type = "string[1,64]", isNecessary = true, description = "名称"),
    // @ReturnResult(parentId = "array_id", id = "", name = "lng", type = "string[1,20]", isNecessary = false, description = "经度"),
    // @ReturnResult(parentId = "array_id", id = "", name = "lat", type = "string[1,20]", isNecessary = false, description = "纬度"),
    // @ReturnResult(parentId = "array_id", id = "", name = "level", type = "int", isNecessary = true, description = "级别"),
    // @ReturnResult(parentId = "array_id", id = "", name = "camera_quantity", type = "int", isNecessary = true, description = "摄像头数量"),
    // @ReturnResult(parentId = "array_id", id = "", name = "order", type = "int", isNecessary = true, description = "排序编号"),
    // @ReturnResult(parentId = "array_id", id = "", name = "order_group", type = "string[1,60]", isNecessary = true, description = "排序编号组"),
    // @ReturnResult(parentId = "array_id", id = "", name = "create_datetime", type = "string[1,30]", isNecessary = true, description = "创建时间"),
    // @ReturnResult(parentId = "array_id", id = "", name = "level_name", type = "string[1,16]", isNecessary = true, description = "等级名称"),
    // @ReturnResult(parentId = "array_id", id = "", name = "full_name", type = "string[1,]", isNecessary = true, description = "组合的全称"),
    // @ReturnResult(parentId = "array_id", id = "parent_array_id", name = "parent_array", type = "array", isNecessary = false, description = "父级数组列表"),
    // @ReturnResult(parentId = "parent_array_id", id = "", name = "uuid", type = "string[1,40]", isNecessary = true, description = "单位的uuid"),
    // @ReturnResult(parentId = "parent_array_id", id = "", name = "parent_uuid", type = "string[1,40]", isNecessary = true, description = "父级单位的uuid"),
    // @ReturnResult(parentId = "parent_array_id", id = "", name = "name", type = "string[1,64]", isNecessary = true, description = "名称"),
    // @ReturnResult(parentId = "parent_array_id", id = "", name = "level", type = "int", isNecessary = true, description = "级别") }))
    // public Message getDepartmentOfSelf() {
    // String accountDepartmentUuid = null;
    // // 从账户令牌中获取信息
    // {
    // final Message resultMsg = this.account.getTokenData(Admin.JWT_KEY_MAP.get("DEPARTMENT_UUID"));
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // accountDepartmentUuid = ((JSONObject) resultMsg.getContent()).getString("data");
    // }
    // final String uuid = (String) this.parameter.get("uuid");
    // final String parentUuid = (String) this.parameter.get("parent_uuid");
    // final String levelUuid = (String) this.parameter.get("level_uuid");
    // final String typeUuid = (String) this.parameter.get("type_uuid");
    // final String attributeUuid = (String) this.parameter.get("attribute_uuid");
    // final Integer level = (Integer) this.parameter.get("level");
    // final Integer hasCamera = (Integer) this.parameter.get("has_camera");
    // final Integer offset = (Integer) this.parameter.get("offset");
    // final Integer rows = (Integer) this.parameter.get("rows");
    // {
    // Connection con = null;
    // try {
    // con = DruidInstance.getInstance().getTransConnection();
    // final supervision.dao.Department obj = new supervision.dao.Department(con);
    // final Message resultMsg = obj.getDepartment(uuid, parentUuid, levelUuid, typeUuid, attributeUuid, level, hasCamera, accountDepartmentUuid, offset, rows);
    // this.messageResultHandler(resultMsg, con, true);
    // return resultMsg;
    // } catch (final Exception e) {
    // return this.catchHandler(con, e);
    // } finally {
    // this.finallyHandler(con);
    // }
    // }
    // }
}