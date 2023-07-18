package supervision.spot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.palestink.server.sdk.Framework;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.db.DatabaseKit;
import com.palestink.utils.string.StringKit;

import supervision.spot.dao.Problem.Fact;
import supervision.spot.dao.Problem.Read;
import supervision.spot.dao.Problem.Status;

/**
 * 单位
 */
public class Department {
    // 数据库表名
    public static String DATABASE_TABLE_NAME = "svp_department";

    // public static JSONArray DepartmentProblemArray = new JSONArray();

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;

    public Department(final Connection connection) {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 添加单位
     * 
     * @param parentUuid 父级单位的uuid
     * @param typeUuid 类型的uuid（允许为null）
     * @param name 名称
     * @param order 排序编号
     * @return 消息对象
     */
    public Message addDepartment(final String parentUuid, final String typeUuid, final String name, final Integer order) {
        final Message msg = new Message();
        try {
            int level = 1;
            String orderGroup = String.format("%06d", order);
            // 如果parentUuid不是顶级单位检查parentUuid是否存在
            {
                if (!"0".equalsIgnoreCase(parentUuid)) {
                    final Message resultMsg = this.getDepartment(parentUuid, null, null, null, null, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PARENT_DEPARTMENT_NOT_EXIST");
                        msg.setAttach("父级单位不存在");
                        return msg;
                    }
                    level = array.getJSONObject(0).getInt("level") + 1;
                    orderGroup = array.getJSONObject(0).getString("order_group") + orderGroup;
                }
            }
            // 是否存在单位类型
            if (null != typeUuid) {
                final DepartmentType obj = new DepartmentType(this.connection);
                if (!obj.isExistDepartmentType(typeUuid, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("DEPARTMENT_TYPE_NOT_EXIST");
                    msg.setAttach("单位类型不存在");
                    return msg;
                }
            }
            // 添加单位
            final String uuid = StringKit.getUuidStr(true);
            final long createTimestamp = System.currentTimeMillis();
            final String createDatetime = this.simpleDateFormat.format(new Date(createTimestamp));
            {
                PreparedStatement ps = null;
                try {
                    ps = this.connection.prepareStatement(DatabaseKit.composeInsertSql(DATABASE_TABLE_NAME, "uuid", "parent_uuid", "type_uuid", "level", "name", "order", "order_group", "create_timestamp", "create_datetime"));
                    ps.setString(1, uuid);
                    ps.setString(2, parentUuid);
                    ps.setString(3, typeUuid);
                    ps.setInt(4, level);
                    ps.setString(5, name);
                    ps.setInt(6, order.intValue());
                    ps.setString(7, orderGroup);
                    ps.setLong(8, createTimestamp);
                    ps.setString(9, createDatetime);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("ADD_DEPARTMENT_FAIL");
                        msg.setAttach("添加单位失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            msg.setStatus(Message.Status.SUCCESS);
            msg.setContent(uuid);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 根据uuid删除单位
     * 
     * @param checkExist 检查待删除数据是否存在
     * @param uuid uuid
     * @return 消息对象
     */
    public Message removeDepartmentByUuid(final boolean checkExist, final String uuid) {
        final Message msg = new Message();
        try {
            // 是否存在单位
            {
                if (checkExist) {
                    if (!this.isExistDepartment(uuid, null, null, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("DEPARTMENT_NOT_EXIST");
                        msg.setAttach("单位不存在");
                        return msg;
                    }
                }
            }
            // 查找关联
            // svp_department
            {
                if (this.isExistDepartment(null, uuid, null, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ASSOCIATED_DATA_CHILDREN_DEPARTMENT_IS_EXIST");
                    msg.setAttach("存在关联数据子单位");
                    return msg;
                }
            }
            // svp_admin-info
            {
                final AdminInfo obj = new AdminInfo(this.connection);
                if (obj.isExistAdminInfo(null, uuid)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ASSOCIATED_DATA_ADMIN_INFO_IS_EXIST");
                    msg.setAttach("存在关联数据管理员信息");
                    return msg;
                }
            }
            // svp_problem
            {
                final Problem obj = new Problem(this.connection);
                if (obj.isExistProblem(null, null, uuid, null, null)) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ASSOCIATED_DATA_PROBLEM_IS_EXIST");
                    msg.setAttach("存在关联数据问题");
                    return msg;
                }
            }
            // 删除单位
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
                        msg.setContent("REMOVE_DEPARTMENT_FAIL");
                        msg.setAttach("删除单位失败");
                        return msg;
                    }
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
            // 删除关联（无）
            msg.setStatus(Message.Status.SUCCESS);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 根据父级单位的uuid删除单位
     * 
     * @param parentUuid 父级单位的uuid
     * @return 消息对象
     */
    public Message removeDepartmentByParentUuid(final String parentUuid) {
        final Message msg = new Message();
        if (null == parentUuid) {
            msg.setStatus(Message.Status.ERROR);
            msg.setContent("REMOVE_PARAMETER_NOT_ALLOW_NULL");
            msg.setAttach("删除参数不允许为空");
            return msg;
        }
        try {
            // 获取符合条件的单位
            final ArrayList<String> uuidList = new ArrayList<>();
            {
                final Message resultMsg = this.getDepartment(null, parentUuid, null, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("PARENT_DEPARTMENT_NOT_EXIST");
                    msg.setAttach("父级单位不存在");
                    return msg;
                }
                for (int i = 0; i < array.length(); i++) {
                    final String uuid = array.getJSONObject(i).getString("uuid");
                    uuidList.add(uuid);
                }
            }
            // 删除符合条件的单位
            {
                for (int i = 0; i < uuidList.size(); i++) {
                    final String uuid = uuidList.get(i);
                    final Message resultMsg = this.removeDepartmentByUuid(true, uuid);
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
     * 根据类型的uuid删除单位<br />
     * 非自身uuid删除无需检查带删除数据是否存在
     * 
     * @param typeUuid 类型的uuid
     * @return 消息对象
     */
    public Message removeDepartmentByTypeUuid(final String typeUuid) {
        final Message msg = new Message();
        if (null == typeUuid) {
            msg.setStatus(Message.Status.ERROR);
            msg.setContent("REMOVE_PARAMETER_NOT_ALLOW_NULL");
            msg.setAttach("删除参数不允许为空");
            return msg;
        }
        try {
            // 获取符合条件的单位
            final ArrayList<String> uuidList = new ArrayList<>();
            {
                final Message resultMsg = this.getDepartment(null, null, typeUuid, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("DEPARTMENT_TYPE_NOT_EXIST");
                    msg.setAttach("单位类型不存在");
                    return msg;
                }
                for (int i = 0; i < array.length(); i++) {
                    final String uuid = array.getJSONObject(i).getString("uuid");
                    uuidList.add(uuid);
                }
            }
            // 删除符合条件的单位
            {
                for (int i = 0; i < uuidList.size(); i++) {
                    final String uuid = uuidList.get(i);
                    final Message resultMsg = this.removeDepartmentByUuid(false, uuid);
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
     * 获取问题类型的字符串
     * 
     * @param problemTypeArray 数据库中问题类型的JSONArray格式集合
     * @param uuid 从参数获取问题类型uuid
     * @return 如果比较成功，返回字符串；否则返回null。
     */
    private final String getProblemType(final JSONArray problemTypeArray, final String[] problemTypeStrArray) {
        final JSONArray array = new JSONArray();
        for (int i = 0; i < problemTypeArray.length(); i++) {
            final JSONObject problemType = problemTypeArray.getJSONObject(i);
            for (int j = 0; j < problemTypeStrArray.length; j++) {
                if (problemType.getString("uuid").equalsIgnoreCase(problemTypeStrArray[j])) {
                    array.put(problemType);
                }
            }
        }
        if (0 >= array.length()) {
            return null;
        }
        return array.toString();
    }

    /**
     * 修改单位（至少修改一项字段）
     * 
     * @param uuid 单位的uuid
     * @param parentUuid 父级单位的uuid（允许为null）
     * @param typeUuid 类型的uuid（允许为null，为null不修改，长度为0则清空）
     * @param name 名称（允许为null）
     * @param problemTypes 问题类型（允许为null，为null不修改，长度为0则清空）
     * @param order 排序编号（允许为null）
     * @return 消息对象
     */
    public Message modifyDepartment(final String uuid, final String parentUuid, final String typeUuid, final String name, final String problemTypes, final Integer order) {
        final Message msg = new Message();
        try {
            String problemType = null;
            // 获取问题类型
            {
                if ((null != problemTypes) && (0 < problemTypes.length())) {
                    final supervision.spot.dao.ProblemDepartmentType obj = new supervision.spot.dao.ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null,null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MENU_NOT_EXIST");
                        msg.setAttach("菜单不存在");
                        return msg;
                    }
                    final String[] problemTypeList = problemTypes.split(";");
                    problemType = this.getProblemType(array, problemTypeList);
                    if (null == problemType) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("PROBLEM_TYPE_NOT_EXIST");
                        msg.setAttach("问题类型不存在");
                        return msg;
                    }
                }
            }
            int currentLevel = 1;
            // 是否存在单位
            {
                final Message resultMsg = this.getDepartment(uuid, null, null, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("DEPARTMENT_NOT_EXIST");
                    msg.setAttach("单位不存在");
                    return msg;
                }
                currentLevel = array.getJSONObject(0).getInt("level");
            }
            int level = 1;
            String orderGroup = String.format("%06d", order);
            // 如果parentUuid不是顶级单位检查parentUuid是否存在
            {
                if (null != parentUuid) {
                    if (!"0".equalsIgnoreCase(parentUuid)) {
                        final Message resultMsg = this.getDepartment(parentUuid, null, null, null, null, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        if (0 >= array.length()) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("PARENT_DEPARTMENT_NOT_EXIST");
                            msg.setAttach("父级单位不存在");
                            return msg;
                        }
                        // 确保不能跨级别修改
                        if (array.getJSONObject(0).getInt("level") != (currentLevel - 1)) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("DEPARTMENT_LEVEL_NOT_MATCH");
                            msg.setAttach("单位级别不匹配");
                            return msg;
                        }
                        level = array.getJSONObject(0).getInt("level") + 1;
                        orderGroup = array.getJSONObject(0).getString("order_group") + orderGroup;
                    }
                }
            }
            // 是否存在单位类型
            {
                if (null != typeUuid) {
                    final DepartmentType obj = new DepartmentType(this.connection);
                    if (!obj.isExistDepartmentType(typeUuid, null, null)) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("DEPARTMENT_TYPE_NOT_EXIST");
                        msg.setAttach("单位类型不存在");
                        return msg;
                    }
                }
            }
            // 修改单位
            {
                PreparedStatement ps = null;
                try {
                    final String whereSql = "where `uuid` = '" + uuid + "' and `remove_timestamp` is null";
                    final HashMap<String, Object> hm = new HashMap<>();
                    if (null != parentUuid) {
                        hm.put("parent_uuid", parentUuid);
                        hm.put("level", Integer.valueOf(level)); // 改变了父级问题类型的uuid才会改变级别
                    }
                    if (null != typeUuid) {
                        if (0 >= typeUuid.length()) {
                            hm.put("type_uuid", null);
                        } else {
                            hm.put("type_uuid", typeUuid);
                        }
                    }
                    if (null != name) {
                        hm.put("name", name);
                    }
                    if (null != problemTypes) {
                        if (0 >= problemTypes.length()) {
                            hm.put("problem_types", null);
                        } else {
                            hm.put("problem_types", problemType);
                        }
                    }
                    if (null != order) {
                        hm.put("order", order);
                        hm.put("order_group", orderGroup);
                    }
                    final String sql = DatabaseKit.composeUpdateSql(DATABASE_TABLE_NAME, hm, whereSql);
                    ps = this.connection.prepareStatement(sql);
                    final int res = ps.executeUpdate();
                    if (0 >= res) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("MODIFY_DEPARTMENT_FAIL");
                        msg.setAttach("修改单位失败");
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
     * 获取单位
     * 
     * @param uuid uuid（允许为null）
     * @param parentUuid 父级单位的uuid（允许为null）
     * @param typeUuid 类型的uuid（允许为null）
     * @param level 级别（允许为null）
     * @param name 名称（允许为null）
     * @param geUuid order_group asc 排序时大于等于单位的uuid（允许为null）
     * @param orderGroupLike 排序编号组模糊查询（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getDepartment(final String uuid, final String parentUuid, final String typeUuid, final Integer level, final String name, final String geUuid, final String orderGroupLike, final Integer offset, final Integer rows) {
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
            if (null != parentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "parent_uuid");
                obj.put("symbol", "=");
                obj.put("value", parentUuid);
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
            if (null != level) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "level");
                obj.put("symbol", "=");
                obj.put("value", level.intValue());
                whereArray.put(obj);
            }
            if (null != name) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "name");
                obj.put("symbol", "=");
                obj.put("value", name);
                whereArray.put(obj);
            }
            if (null != geUuid) {
                // 是否存在单位
                {
                    final Message resultMsg = this.getDepartment(geUuid, null, null, null, null, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 >= array.length()) {
                        msg.setStatus(Message.Status.ERROR);
                        msg.setContent("DEPARTMENT_NOT_EXIST");
                        msg.setAttach("单位不存在");
                        return msg;
                    }
                    final String orderGroup = array.getJSONObject(0).getString("order_group");
                    final JSONObject obj = new JSONObject();
                    obj.put("condition", "and");
                    obj.put("name", "order_group");
                    obj.put("symbol", ">=");
                    obj.put("value", orderGroup);
                    whereArray.put(obj);
                }
            }
            if (null != orderGroupLike) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "order_group");
                obj.put("symbol", "like");
                obj.put("value", orderGroupLike + "%");
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
                    ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "` " + whereSql + " order by `order_group` asc " + limitCode);
                    if ((null != offset) && (null != rows)) {
                        ps.setInt(1, offset.intValue());
                        ps.setInt(2, rows.intValue());
                    }
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        obj.put("uuid", rs.getString("uuid"));
                        obj.put("parent_uuid", rs.getString("parent_uuid"));
                        // type_uuid可能为空，所以不能用联合查询。
                        {
                            final String _typeUuid = rs.getString("type_uuid");
                            if (null != _typeUuid) {
                                obj.put("type_uuid", _typeUuid);
                                final supervision.spot.dao.DepartmentType departmentType = new supervision.spot.dao.DepartmentType(this.connection);
                                final Message resultMsg = departmentType.getDepartmentType(_typeUuid, null);
                                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                    return resultMsg;
                                }
                                final JSONArray _array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                                if (0 >= _array.length()) {
                                    msg.setStatus(Message.Status.ERROR);
                                    msg.setContent("DEPARTMENT_TYPE_NOT_EXIST");
                                    msg.setAttach("单位类型不存在");
                                    return msg;
                                }
                                obj.put("type_name", _array.getJSONObject(0).getString("name"));
                            }
                        }
                        obj.put("name", rs.getString("name"));
                        obj.put("level", rs.getInt("level"));
                        obj.put("order", rs.getInt("order"));
                        obj.put("order_group", rs.getString("order_group"));
                        final String problemTypes = rs.getString("problem_types");
                        if (null != problemTypes) {
                            obj.put("problem_types", new JSONArray(problemTypes));
                        }
                        obj.put("create_datetime", rs.getString("create_datetime"));
                        {
                            final Message resultMsg = this.getDepartmentFullNameByUuid(rs.getString("uuid"));
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            final String fullName = ((JSONObject) resultMsg.getContent()).getString("full_name");
                            obj.put("full_name", fullName);
                        }
                        {
                            // 只有像单位管理这样“自我关联”时，才需要获取单位的时候附加parent_array的数据。
                            // 很多地方都用到了parent_array的数据，千万不要随意修改。
                            final Message resultMsg = this.getDepartmentParentBriefInfoByUuid(rs.getString("uuid"));
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            final JSONArray parentArray = ((JSONObject) resultMsg.getContent()).getJSONArray("parent_array");
                            obj.put("parent_array", parentArray);
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
     * 获取单位工作量
     * 
     * @param uuid uuid（允许为null）
     * @param startCreateDatetime 开始创建时间（允许为null）
     * @param endCreateDatetime 结束创建时间（允许为null）
     * @return 消息对象
     */
    public Message getDepartmentWorkload(final String uuid, final String startCreateDatetime, final String endCreateDatetime) {
        final Message msg = new Message();
        try {
            // 部门问题的Map
            final HashMap<String, JSONObject> departmentProblemMap = new HashMap<>();
            {
                final Problem obj = new Problem(this.connection);
                final Message resultMsg = obj.getProblemExact(null, null, Problem.Type.DEPARTMENT, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, startCreateDatetime, endCreateDatetime, null, null,
                        null, null);
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
                for (int i = 0; i < array.length(); i++) {
                    final String fromUuid = array.getJSONObject(i).getString("from_uuid");
                    JSONObject adminObj = departmentProblemMap.get(fromUuid);
                    if (null == adminObj) {
                        adminObj = new JSONObject();
                        adminObj.put("admin_uuid", fromUuid);
                        adminObj.put("problem_count", 1);
                    } else {
                        adminObj.put("problem_count", adminObj.getInt("problem_count") + 1);
                    }
                    departmentProblemMap.put(fromUuid, adminObj);
                }
            }
            final JSONArray array = new JSONArray();
            // 遍历部门问题的Map找到对应管理员的信息
            for (final String adminUuid : departmentProblemMap.keySet()) {
                final JSONObject adminObj = departmentProblemMap.get(adminUuid);
                final AdminInfo obj = new AdminInfo(this.connection);
                final Message resultMsg = obj.getAdminInfo(adminUuid, null, null, uuid, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray adminArray = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= adminArray.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("ADMIN_NOT_EXIST");
                    msg.setAttach("管理员不存在");
                    return msg;
                }
                for (int i = 0; i < adminArray.length(); i++) {
                    final String departmentUuid = adminArray.getJSONObject(i).getString("department_uuid");
                    adminObj.put("department_uuid", departmentUuid);
                    {
                        final Message depResultMsg = this.getDepartment(departmentUuid, null, null, null, null, null, null, null, null);
                        if (Message.Status.SUCCESS != depResultMsg.getStatus()) {
                            return depResultMsg;
                        }
                        final JSONArray depArray = ((JSONObject) depResultMsg.getContent()).getJSONArray("array");
                        if (0 >= depArray.length()) {
                            msg.setStatus(Message.Status.ERROR);
                            msg.setContent("DEPARTMENT_NOT_EXIST");
                            msg.setAttach("单位不存在");
                            return msg;
                        }
                        adminObj.put("department_name", depArray.getJSONObject(i).getString("name"));
                    }
                    if (0 >= array.length()) {
                        array.put(adminObj);
                    } else {
                        boolean isExistObj = false;
                        for (int j = 0; j < array.length(); j++) {
                            final JSONObject existObj = array.getJSONObject(j);
                            if (existObj.getString("department_uuid").equalsIgnoreCase(adminObj.getString("department_uuid"))) {
                                isExistObj = true;
                                existObj.put("problem_count", existObj.getInt("problem_count") + adminObj.getInt("problem_count"));
                            }
                        }
                        if (!isExistObj) {
                            array.put(adminObj);
                        }
                    }
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
     * 获取单位工作量
     * 
     * @param uuid uuid（允许为null）
     * @param level 级别（允许为null）
     * @param typeUuidArray 类型的uuid数组（允许为null）
     * @param startCreateDatetime 开始创建时间（允许为null）
     * @param endCreateDatetime 结束创建时间（允许为null）
     * @return 消息对象
     */
    public Message getDepartmentWorkload(final String uuid, final Integer level, final String[] typeUuidArray, final String startCreateDatetime, final String endCreateDatetime) {
        final Message msg = new Message();
        try {
            final ArrayList<String> adminUuidList = new ArrayList<>();
            String orderGroup = null;
            {
                final Message resultMsg = this.getDepartment(uuid, null, null, null, null, null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("DEPARTMENT_NOT_EXIST");
                    msg.setAttach("单位不存在");
                    return msg;
                }
                orderGroup = array.getJSONObject(0).getString("order_group");
            }
            {
                final Message depResultMsg = this.getDepartment(null, uuid, null, level, null, null, orderGroup, null, null);
                if (Message.Status.SUCCESS != depResultMsg.getStatus()) {
                    return depResultMsg;
                }
                // 获取管理员
                {
                    if (null != uuid) {
                        final AdminInfo obj = new AdminInfo(this.connection);
                        final Message resultMsg = obj.getAdminInfo(null, null, null, uuid, null, null, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            adminUuidList.add(array.getJSONObject(i).getString("uuid"));
                        }
                    }
                }
                final JSONArray depArray = ((JSONObject) depResultMsg.getContent()).getJSONArray("array");
                for (int i = 0; i < depArray.length(); i++) {
                    final JSONObject depObj = depArray.getJSONObject(i);
                    // 获取管理员
                    {
                        final AdminInfo obj = new AdminInfo(this.connection);
                        final Message resultMsg = obj.getAdminInfo(null, null, null, depObj.getString("uuid"), null, null, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int j = 0; j < array.length(); j++) {
                            adminUuidList.add(array.getJSONObject(j).getString("uuid"));
                        }
                    }
                }
            }
            int count = 0;
            {
                for (int i = 0; i < adminUuidList.size(); i++) {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, Problem.Type.DEPARTMENT, adminUuidList.get(i), null, null, typeUuidArray, null, null, null, null, null, null, null, null, null, null, null, null, null, null, startCreateDatetime,
                            endCreateDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    count += ((JSONObject) resultMsg.getContent()).getInt("count");
                }
            }
            final JSONObject resultObj = new JSONObject();
            resultObj.put("count", count);
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
     * 获取单位问题数量
     * 
     * @param uuid uuid（允许为null）
     * @param level 级别（允许为null）
     * @param orderGroup 问题单位类型的排序编号组（允许为null）
     * @param typeUuid 问题类型的uuid（允许为null）
     * @param read 是否已读（允许为null）
     * @param fact 是否属实（允许为null）
     * @param feedback 是否反馈（允许为null）
     * @param startCreateDatetime 开始创建时间（允许为null）
     * @param endCreateDatetime 结束创建时间（允许为null）
     * @return 消息对象
     */
    public Message getDepartmentProblemCount(final String uuid, final Integer level, final String orderGroup, final String typeUuid, final Read read, final Fact fact, final Status status, final String startCreateDatetime, final String endCreateDatetime) {
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
            if (null != level) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "level");
                obj.put("symbol", "=");
                obj.put("value", level.intValue());
                whereArray.put(obj);
            }
            String[] typeUuidArray = null;
            final ArrayList<String> typeUuidList = new ArrayList<>();
            if (null != orderGroup) {
                final ProblemDepartmentType pdt = new ProblemDepartmentType(this.connection);
                final Message resultMsg = pdt.getProblemDepartmentTypeByOrderGroup(orderGroup);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    return resultMsg;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                for (int i = 0; i < array.length(); i++) {
                    typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                }
                typeUuidArray = typeUuidList.toArray(new String[typeUuidList.size()]);
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
                    ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "` " + whereSql + " order by `order_group` asc ");
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        obj.put("uuid", rs.getString("uuid"));
                        obj.put("name", rs.getString("name"));
                        {
                            final Problem problemObj = new Problem(this.connection);
                            final Message resultMsg = problemObj.getProblemExact(null, null, Problem.Type.DEPARTMENT, null, obj.getString("uuid"), typeUuid, typeUuidArray, read, fact, status, null, null, null, null, null, null, null, null, null, null, null,
                                    startCreateDatetime, endCreateDatetime, null, null, null, null);
                            if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                                return resultMsg;
                            }
                            obj.put("problem_count", ((JSONObject) resultMsg.getContent()).getJSONArray("array").length());
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
     * 根据uuid获取单位的全部名称（子找父）
     * 
     * @param uuid uuid
     * @return 消息对象
     */
    public Message getDepartmentFullNameByUuid(final String uuid) {
        final Message msg = new Message();
        try {
            final JSONArray whereArray = new JSONArray();
            {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "uuid");
                obj.put("symbol", "=");
                obj.put("value", uuid);
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
            int maxCount = 0;
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("select count(*) as `count` from `" + DATABASE_TABLE_NAME + "`"/* 没有where条件查询全部数据 */);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        maxCount = rs.getInt("count");
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
            for (int i = 0; i < maxCount; i++) {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "` " + whereSql + " order by `order_group` asc");
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        obj.put("name", rs.getString("name"));
                        array.put(obj);
                        if (rs.getString("parent_uuid").equalsIgnoreCase("0")) {
                            break;
                        }
                        for (int j = 0; j < whereArray.length(); j++) {
                            final JSONObject whereObj = whereArray.getJSONObject(j);
                            if (whereObj.getString("name").equalsIgnoreCase("uuid")) {
                                whereObj.put("value", rs.getString("parent_uuid"));
                                break;
                            }
                        }
                        whereSql = DatabaseKit.composeWhereSql(whereArray);
                    } else {
                        Framework.Log.error("Get Parent Department By Uuid Error");
                        break;
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
            final StringBuilder sb = new StringBuilder();
            final String split = " / ";
            for (int i = array.length() - 1; i >= 0; i--) {
                final JSONObject obj = array.getJSONObject(i);
                sb.append(obj.getString("name"));
                sb.append(split);
            }
            resultObj.put("full_name", sb.toString().substring(0, sb.length() - split.length()));
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
     * 根据uuid获取单位的父级简要信息（子找父）
     * 
     * @param uuid uuid
     * @return 消息对象
     */
    public Message getDepartmentParentBriefInfoByUuid(final String uuid) {
        final Message msg = new Message();
        try {
            final JSONArray whereArray = new JSONArray();
            {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "uuid");
                obj.put("symbol", "=");
                obj.put("value", uuid);
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
            int maxCount = 0;
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("select count(*) as `count` from `" + DATABASE_TABLE_NAME + "`"/* 没有where条件查询全部数据 */);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        maxCount = rs.getInt("count");
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
            for (int i = 0; i < maxCount; i++) {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "` " + whereSql + " order by `order_group` asc");
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        final JSONObject obj = new JSONObject();
                        // 根据需求添加父级简要信息
                        obj.put("uuid", rs.getString("uuid"));
                        obj.put("parent_uuid", rs.getString("parent_uuid"));
                        obj.put("name", rs.getString("name"));
                        obj.put("level", rs.getInt("level"));
                        array.put(obj);
                        if (rs.getString("parent_uuid").equalsIgnoreCase("0")) {
                            break;
                        }
                        for (int j = 0; j < whereArray.length(); j++) {
                            final JSONObject whereObj = whereArray.getJSONObject(j);
                            if (whereObj.getString("name").equalsIgnoreCase("uuid")) {
                                whereObj.put("value", rs.getString("parent_uuid"));
                                break;
                            }
                        }
                        whereSql = DatabaseKit.composeWhereSql(whereArray);
                    } else {
                        Framework.Log.error("Get Parent Department By Uuid Error");
                        break;
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
            final JSONArray parentArray = new JSONArray();
            for (int i = array.length() - 1; i > 0/* 这里没有等于号，否则就包含了uuid对应的数据，就不是父级简要信息了 */; i--) {
                final JSONObject obj = array.getJSONObject(i);
                parentArray.put(obj);
            }
            resultObj.put("parent_array", parentArray);
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
     * 是否存在单位
     * 
     * @param uuid 单位的uuid（允许为null）
     * @param parentUuid 父级单位的uuid（允许为null）
     * @param typeUuid 类型的uuid（允许为null）
     * @param name 名称（允许为null）
     * @param excludeUuid 排除类别的uuid（允许为null）
     * @return 存在返回true，不存在返回false。
     */
    public final boolean isExistDepartment(final String uuid, final String parentUuid, final String typeUuid, final String name, final String excludeUuid) throws Exception {
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
            if (null != parentUuid) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "parent_uuid");
                obj.put("symbol", "=");
                obj.put("value", parentUuid);
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
            if (null != name) {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "name");
                obj.put("symbol", "=");
                obj.put("value", name);
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
     * 根据预警的uuid是否存在单位关联
     * 
     * @param warnUuid 预警的uuid
     * @return 消息对象
     */
    public boolean hasDepartmentRelationByWarnUuid(final String warnUuid) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "` where `warn_uuids` like '%" + warnUuid + "%' and `remove_timestamp` is null limit 0, 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        } catch (final Exception e) {
            return false;
        } finally {
            try {
                if (null != rs) {
                    rs.close();
                }
                if (null != ps) {
                    ps.close();
                }
            } catch (final Exception e) {
                return false;
            }
        }
    }

    /**
     * 从缓存中（已经不是缓存了）获取部门工作量数据
     * 
     * @return 消息对象
     */
    public Message getDepartmentWorkloadFromCache(final String startCreateDatetime, final String endCreateDatetime) {
        final Message msg = new Message();
        try {
            final JSONArray whereArray = new JSONArray();
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
            final JSONArray array = new JSONArray();
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    // ps = this.connection.prepareStatement("select * from `" + DATABASE_TABLE_NAME + "` " + whereSql + " order by `order_group` asc " + limitCode);
                    // ps = this.connection.prepareStatement("select p.`from_department_uuid`, d.`name`, count(p.`from_department_uuid`) as `count` from `" + Problem.DATABASE_TABLE_NAME + "` p inner join `" + Department.DATABASE_TABLE_NAME
                    // + "` d on p.to_uuid = d.uuid group by `from_department_uuid`");
                    ps = this.connection.prepareStatement("SELECT p.`from_department_uuid`, d.`name`, COUNT(p.`from_department_uuid`) AS `count` FROM `" + Problem.DATABASE_TABLE_NAME + "` p INNER JOIN `" + DATABASE_TABLE_NAME
                            + "` d ON p.`from_department_uuid` = d.`uuid` "+whereSql+" GROUP BY p.`from_department_uuid`");
                    rs = ps.executeQuery();
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
     * 获取子部门（返回结果包含当前查询uuid）
     * 
     * @param uuidArray 部门的uuid数组
     * @return 消息对象
     */
    public final Message getChildDepartment(final String[] uuidArray) {
        try {
            final JSONArray whereArray = new JSONArray();
            {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "uuid");
                obj.put("symbol", "in");
                obj.put("value", uuidArray);
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
                    ps = this.connection.prepareStatement("with recursive `trees` as (select * from `" + DATABASE_TABLE_NAME + "` " + whereSql + " union all select t.* from trees join `" + DATABASE_TABLE_NAME
                            + "` t on t.`parent_uuid` = `trees`.`uuid`) select * from trees order by `order_group` asc");
                    rs = ps.executeQuery();
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
            return new Message(Message.Status.SUCCESS, resultObj, null);
        } catch (final Exception e) {
            return new Message(Message.Status.EXCEPTION, StringKit.getExceptionStackTrace(e), null);
        }
    }
    
    /**
     * 获取父单位的lv2名称（返回结果包含当前查询uuid）
     * 
     * @param uuidArray 菜单的uuid数组
     * @return 消息对象
     */
    public final String getParentDepLv2Name(final String[] uuidArray) {
        try {
            final JSONArray whereArray = new JSONArray();
            {
                final JSONObject obj = new JSONObject();
                obj.put("condition", "and");
                obj.put("name", "uuid");
                obj.put("symbol", "in");
                obj.put("value", uuidArray);
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
            {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = this.connection.prepareStatement("with recursive `trees` as (select * from `" + DATABASE_TABLE_NAME + "` " + whereSql + " union all select t.* from `"
                            + DATABASE_TABLE_NAME + "` t inner join `trees` on `trees`.`parent_uuid` = t.`uuid`) select * from `trees` order by `order_group` asc;");
                    rs = ps.executeQuery();
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
                        }
                        System.out.println(obj);
                        if (2 == obj.getInt("level")) {
                            return obj.getString("name");
                        }
                    }
                    return null;
                } finally {
                    if (null != rs) {
                        rs.close();
                    }
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
        }  catch (final Exception e) {
            return null;
        }
    }
}