package supervision.spot.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.Framework;
import com.palestink.server.sdk.module.AbstractPreExecute;
import com.palestink.server.sdk.module.annotation.PreExecute;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.io.IoKit;
import com.palestink.utils.net.HttpKit;
import com.palestink.utils.net.HttpKit.HttpRequestResult;
import com.palestink.utils.net.HttpKit.HttpRequestType;
import com.palestink.utils.net.HttpKit.HttpResponseType;
import com.palestink.utils.net.HttpKit.ProxyType;
import com.palestink.utils.string.StringKit;
import com.palestink.utils.system.SystemKit;
import env.db.DruidInstance;
import supervision.spot.dao.Feedback;
import supervision.spot.dao.Problem;

class ForTest {
    public ForTest() {
        {
            try {
                // 特殊处理：生成大屏json数据
                // final String ip = "192.168.10.150";
                String ip = null;
                if (SystemKit.isWindows()) {
                    ip = "192.168.10.150";
                } else {
                    ip = "127.0.0.1";
                }
                final String jsonFilePath = IoKit.regulatePath(System.getProperty("user.dir")) + "/webapp/data.json";
                String token = null;
                // 登录
                {
                    final JSONObject obj = new JSONObject();
                    final JSONObject user = new JSONObject();
                    user.put("username", "superadmin");
                    user.put("password", "aaduchaaa!");
                    user.put("source_type", "Admin");
                    obj.put("user", user);
                    final HashMap<String, String> httpHeader = new HashMap<>();
                    httpHeader.put("Content-Type", "application/json");
                    final HashMap<HttpRequestResult, Object> resultMap = HttpKit.getHttpRequestResult(HttpRequestType.POST, HttpResponseType.STRING, "http://" + ip + "/api/auth/validate_username_password", ProxyType.NONE, null, null, null, null, httpHeader, null, null, null, null,
                            obj.toString(), null, null, null);
                    final int code = ((Integer) resultMap.get(HttpRequestResult.CODE)).intValue();
                    if (200 == code) {
                        final String entity = (String) resultMap.get(HttpRequestResult.ENTITY);
                        final JSONObject obj2 = new JSONObject(entity);
                        token = obj2.getJSONObject("data").getString("token");
                    }
                }
                final JSONArray dataArray = new JSONArray();
                // 获取数据
                {
                    final HashMap<String, String> httpHeader = new HashMap<>();
                    httpHeader.put("Content-Type", "application/json");
                    httpHeader.put("Authorization", "Bearer " + token);
                    final HashMap<HttpRequestResult, Object> resultMap = HttpKit.getHttpRequestResult(HttpRequestType.GET, HttpResponseType.STRING, "http://" + ip + "/api/location_events?page_size=100", ProxyType.NONE, null, null, null, null, httpHeader, null, null, null, null, null,
                            null, null, null);
                    final int code = ((Integer) resultMap.get(HttpRequestResult.CODE)).intValue();
                    if (200 == code) {
                        final String entity = (String) resultMap.get(HttpRequestResult.ENTITY);
                        final JSONObject obj2 = new JSONObject(entity);
                        final JSONArray array = obj2.getJSONArray("data");
                        for (int i = array.length() - 1; i >= 0; i--) {
                            final JSONObject jo = array.getJSONObject(i);
                            String place = null;
                            final JSONArray arr = jo.getJSONObject("location").getJSONArray("path");
                            for (int j = 0; j < arr.length(); j++) {
                                final JSONObject jo1 = arr.getJSONObject(j);
                                // if (5 == jo1.getInt("id")) {
                                if (3 == j) {
                                    place = jo1.getString("name");
                                }
                            }
                            final String datetime = this.getUTC(jo.getString("created_at"));
                            final String event = jo.getJSONObject("event").getString("name");
                            final JSONObject dataObj = new JSONObject();
                            dataObj.put("place", place);
                            dataObj.put("datetime", datetime);
                            dataObj.put("event", event);
                            dataArray.put(dataObj);
                        }
                    }
                }
                // 写入文件
                IoKit.simpleWriteFile(jsonFilePath, dataArray.toString().getBytes());
            } catch (final Exception e) {
                Framework.Log.error(StringKit.getExceptionStackTrace(e));
            }
        }
    }

    private String getUTC(String utcTimeStr) {
        final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        final int TIME_ZONE = 8;
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);

        try {
            date = sdf.parse(utcTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + TIME_ZONE);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf2.format(new Date(calendar.getTimeInMillis()));
    }
}

// class ForCacheData {
// public ForCacheData() {
// Connection con = null;
// try {
// con = DruidInstance.getInstance().getTransConnection();
// con.setAutoCommit(true); // 不需要事务
// final JSONArray depArray = new JSONArray();
// // 获取数据
// {
// final Department dep = new Department(con);
// final Message resultMsg = dep.getDepartment(null, null, null, Integer.valueOf(2), null, null, null, null, null);
// if (Message.Status.SUCCESS != resultMsg.getStatus()) {
// return;
// }
// final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
// for (int i = 0; i < array.length(); i++) {
// final JSONObject jo = array.getJSONObject(i);
// depArray.put(jo);
// }
// }
// final JSONArray targetArray = new JSONArray();
// // 获取数据
// {
// final Department dep = new Department(con);
// for (int i = 0; i < depArray.length(); i++) {
// final Message resultMsg = dep.getDepartmentWorkload(depArray.getJSONObject(i).getString("uuid"), null, null, null, null);
// if (Message.Status.SUCCESS != resultMsg.getStatus()) {
// return;
// }
// final String name = depArray.getJSONObject(i).getString("name");
// final int count = ((JSONObject) resultMsg.getContent()).getInt("count");
// final JSONObject obj = new JSONObject();
// obj.put("name", name);
// obj.put("count", count);
// targetArray.put(obj);
// }
// }
// if (0 < targetArray.length()) {
// Department.DepartmentProblemArray.clear();
// for (int i = 0; i < targetArray.length(); i++) {
// Department.DepartmentProblemArray.put(targetArray.getJSONObject(i));
// }
// }
// } catch (final Exception e) {
// Framework.Log.error(StringKit.getExceptionStackTrace(e));
// } finally {
// try {
// if (null != con) {
// con.close();
// }
// } catch (final Exception e) {
// Framework.Log.error(StringKit.getExceptionStackTrace(e));
// }
// }
// }
// }

/**
 * 摄像头监控线程
 */
final class MonitorThread extends Thread {
    // 监控循环休眠时间（默认值：30秒钟）
    private static final int MONITOR_LOOP_SLEEP_TIME = 1000 * 60 * 1;

    // 检查问题归档过期时间（默认值：5分钟）
    private static final int CHECK_PROBLEM_FILED_EXPIRE_TIME = 1000 * 60 * 5;

    // 检查问题未反馈通知过期时间（默认值：5分钟）
    private static final int CHECK_PROBLEM_UNFEEDBACK_NOTIFY_EXPIRE_TIME = 1000 * 60 * 5;

    // 最新检查问题归档过期检查时间
    private long lastCheckProblemFiledExpireCheckTime = 0;

    // 最新检查问题未反馈通知过期检查时间
    private long lastCheckProblemUnfeedbackNotifyExpireCheckTime = 0;

    /**
     * 检查问题归档
     */
    private void checkProblemFiled() {
        Connection con = null;
        try {
            con = DruidInstance.getInstance().getTransConnection();
            con.setAutoCommit(true); // 不需要事务
            final Calendar cal = Calendar.getInstance();
            // 获取问题归档时间
            {
                final supervision.spot.dao.SystemConfig obj = new supervision.spot.dao.SystemConfig(con);
                final Message resultMsg = obj.getSystemConfig();
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    Framework.Log.error((String) resultMsg.getContent());
                    return;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                if (0 >= array.length()) {
                    Framework.Log.error("获取问题归档时间出错");
                    return;
                }
                final int problemFiledDays = array.getJSONObject(0).getInt("problem_filed_days");
                cal.add(Calendar.DAY_OF_YEAR, problemFiledDays * -1);
            }
            // 修改已反馈问题中，反馈时间超过问题归档时间的状态为已归档。
            {
                PreparedStatement ps = null;
                try {
                    ps = con.prepareStatement("update `" + Problem.DATABASE_TABLE_NAME + "` inner join `" + Feedback.DATABASE_TABLE_NAME + "` on `" + Problem.DATABASE_TABLE_NAME + "`.uuid = `" + Feedback.DATABASE_TABLE_NAME + "`.problem_uuid set `"
                            + Problem.DATABASE_TABLE_NAME + "`.status = ? where `" + Feedback.DATABASE_TABLE_NAME + "`.last_update_timestamp < ?");
                    ps.setString(1, Problem.Status.FILED.toString());
                    ps.setLong(2, cal.getTimeInMillis());
                    ps.executeUpdate();
                } finally {
                    if (null != ps) {
                        ps.close();
                    }
                }
            }
        } catch (final Exception e) {
            Framework.Log.error(StringKit.getExceptionStackTrace(e));
            return;
        } finally {
            try {
                if (null != con) {
                    con.close();
                }
            } catch (final Exception e) {
                Framework.Log.error(StringKit.getExceptionStackTrace(e));
            }
        }
    }

    /**
     * 检查问题未反馈通知
     */
    private void checkProblemUnfeedbackNotify() {
        Connection con = null;
        try {
            con = DruidInstance.getInstance().getTransConnection();
            final JSONArray problemArray = new JSONArray();
            // 获取未反馈通知问题
            {
                final Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR, -24);
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, Problem.Status.UN_FEEDBACK, null, Problem.ProblemType.CHECK, null, null, null, null, null, null, null, null, null, null,
                        sdf.format(new Date(cal.getTimeInMillis())), null, null, null, null);
                if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                    Framework.Log.error((String) resultMsg.getContent());
                    return;
                }
                final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                for (int i = 0; i < array.length(); i++) {
                    final JSONObject p = array.getJSONObject(i);
                    if (!p.has("is_unfeedback_24h_notify")) {
                        problemArray.put(p);
                    } else {
                        if (p.getString("is_unfeedback_24h_notify").equalsIgnoreCase("0")) {
                            problemArray.put(p);
                        }
                    }
                }
            }
            final HashMap<String, JSONObject> hm = new HashMap<>();
            // 获取部门负责人
            {
                for (int i = 0; i < problemArray.length(); i++) {
                    final JSONObject problem = problemArray.getJSONObject(i);
                    if (problem.has("from_department_uuid")) {
                        final supervision.spot.dao.AdminInfo obj = new supervision.spot.dao.AdminInfo(con);
                        final Message resultMsg = obj.getAdminInfo(null, null, null, null, null, null, problem.getString("from_department_uuid"), null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            Framework.Log.error((String) resultMsg.getContent());
                            return;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int j = 0; j < array.length(); j++) {
                            final JSONObject admin = array.getJSONObject(j);
                            admin.put("problem_detail", problem);
                            hm.put(admin.getString("uuid"), admin);
                        }
                    }
                    if (problem.has("to_department_uuid")) {
                        final supervision.spot.dao.AdminInfo obj = new supervision.spot.dao.AdminInfo(con);
                        final Message resultMsg = obj.getAdminInfo(null, null, null, null, null, null, problem.getString("to_department_uuid"), null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            Framework.Log.error((String) resultMsg.getContent());
                            return;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int j = 0; j < array.length(); j++) {
                            final JSONObject admin = array.getJSONObject(j);
                            admin.put("problem_detail", problem);
                            hm.put(admin.getString("uuid"), admin);
                        }
                    }
                }
            }
            // 消息通知
            {
                for (Map.Entry<String, JSONObject> entry : hm.entrySet()) {
                    try {
                        final JSONObject admin = entry.getValue();
                        final supervision.spot.dao.Message obj = new supervision.spot.dao.Message(con);
                        final String toDepartmentName = admin.getJSONObject("problem_detail").getString("to_department_name");
                        final String createDatetime = admin.getJSONObject("problem_detail").getString("create_datetime").substring(0, 10);
                        final String realName = admin.getString("real_name");
                        final String name = admin.getString("name");
                        final String problemDepartmentTypeName = admin.getJSONObject("problem_detail").getString("problem_department_type_name");
                        // 法制支队，于“2023-04-01”被“顾玉树(270526)”发现“内务卫生”的问题，未在24小时内反馈，特此通知相关领导及负责人。
                        final String content = String.format("%s，于“%s”被“%s(%s)”发现“%s”的问题，未在24小时内反馈，特此通知相关领导及负责人。", toDepartmentName, createDatetime, realName, name, problemDepartmentTypeName);
                        final Message resultMsg = obj.addMessage("0", admin.getString("uuid"), null, "未反馈问题通告", content, admin.getJSONObject("problem_detail").getString("uuid"));
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            Framework.Log.error((String) resultMsg.getContent());
                            return;
                        }
                    } catch (final Exception e) {
                    }
                }
            }
            // 修改问题是否未反馈24小时通知的状态
            {
                for (int i = 0; i < problemArray.length(); i++) {
                    final JSONObject problem = problemArray.getJSONObject(i);
                    final supervision.spot.dao.Problem obj = new supervision.spot.dao.Problem(con);
                    final Message resultMsg = obj.modifyProblem(problem.getString("uuid"), null, null, null, null, null, null, null, null, "1");
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        Framework.Log.error((String) resultMsg.getContent());
                        return;
                    }
                }
            }
            con.commit();
        } catch (final Exception e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Framework.Log.error(StringKit.getExceptionStackTrace(e));
            return;
        } finally {
            try {
                if (null != con) {
                    con.close();
                }
            } catch (final Exception e) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                Framework.Log.error(StringKit.getExceptionStackTrace(e));
            }
        }
    }

    @Override
    public void run() {
        for (;;) {
            try {
                if ((this.lastCheckProblemFiledExpireCheckTime + CHECK_PROBLEM_FILED_EXPIRE_TIME) < System.currentTimeMillis()) {
                    // 检查时间已过期，更新为当前时间。
                    this.lastCheckProblemFiledExpireCheckTime = System.currentTimeMillis();
                    this.checkProblemFiled(); // 检查问题归档
                }
                if ((this.lastCheckProblemUnfeedbackNotifyExpireCheckTime + CHECK_PROBLEM_UNFEEDBACK_NOTIFY_EXPIRE_TIME) < System.currentTimeMillis()) {
                    // 检查时间已过期，更新为当前时间。
                    this.lastCheckProblemUnfeedbackNotifyExpireCheckTime = System.currentTimeMillis();
                    this.checkProblemUnfeedbackNotify(); // 检查问题未反馈通知
                }
                {
                    // 特殊处理：生成大屏json数据
                    new ForTest();
                }
                // {
                // new ForCacheData();
                // }
                Thread.sleep(MONITOR_LOOP_SLEEP_TIME);
            } catch (final InterruptedException e) {
                Framework.Log.error(StringKit.getExceptionStackTrace(e));
                break;
            } catch (final Exception e) {
                Framework.Log.error(StringKit.getExceptionStackTrace(e));
            }
        }
        Framework.Log.info("Monitor Thread Finish");
    }
}

@PreExecute(order = 5) /* 从1开始 */
public final class ProblemFiledPreExecute extends AbstractPreExecute {
    public ProblemFiledPreExecute() {
    }

    @Override
    public final void run() throws Exception {
        // 开启监控线程
        final MonitorThread mt = new MonitorThread();
        mt.start();
        Framework.Log.info("Problem Filed Pre-Execute Complete");
    }
}