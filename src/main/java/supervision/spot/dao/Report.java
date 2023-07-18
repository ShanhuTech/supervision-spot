package supervision.spot.dao;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.json.JSONArray;
import org.json.JSONObject;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.encrypt.Md5;
import com.palestink.utils.io.IoKit;
import com.palestink.utils.string.StringKit;
import env.config.Resource;
import supervision.spot.dao.Problem.ProblemType;

class WuWeiYiTi {
    private String index;
    private String name;
    private String count;
    private String detail;

    public WuWeiYiTi() {

    }

    public WuWeiYiTi(String index, String name, String count, String detail) {
        this.index = index;
        this.name = name;
        this.count = count;
        this.detail = detail;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}

class GaoFaWenTi extends WuWeiYiTi {
    public GaoFaWenTi() {

    }

    public GaoFaWenTi(String index, String name, String count, String detail) {
        super(index, name, count, detail);
    }
}

class DanWeiFanKui {
    private String index;
    private String name;
    private String problemCount;
    private String feedbackCount;
    private String feedbackPercent;

    public DanWeiFanKui() {
    }

    public DanWeiFanKui(String index, String name, String problemCount, String feedbackCount, String feedbackPercent) {
        this.index = index;
        this.name = name;
        this.problemCount = problemCount;
        this.feedbackCount = feedbackCount;
        this.feedbackPercent = feedbackPercent;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProblemCount() {
        return problemCount;
    }

    public void setProblemCount(String problemCount) {
        this.problemCount = problemCount;
    }

    public String getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(String feedbackCount) {
        this.feedbackCount = feedbackCount;
    }

    public String getFeedbackPercent() {
        return feedbackPercent;
    }

    public void setFeedbackPercent(String feedbackPercent) {
        this.feedbackPercent = feedbackPercent;
    }
}

class DanWeiZiCha extends WuWeiYiTi {
    private String tongbi;
    private String huanbi;

    public DanWeiZiCha() {

    }

    public DanWeiZiCha(String index, String name, String count, String detail) {
        super(index, name, count, detail);
    }

    public String getTongbi() {
        return tongbi;
    }

    public void setTongbi(String tongbi) {
        this.tongbi = tongbi;
    }

    public String getHuanbi() {
        return huanbi;
    }

    public void setHuanbi(String huanbi) {
        this.huanbi = huanbi;
    }
}

class DanWeiJianCha extends WuWeiYiTi {
    private String tongbi;
    private String huanbi;

    public DanWeiJianCha() {

    }

    public DanWeiJianCha(String index, String name, String count, String detail) {
        super(index, name, count, detail);
    }

    public String getTongbi() {
        return tongbi;
    }

    public void setTongbi(String tongbi) {
        this.tongbi = tongbi;
    }

    public String getHuanbi() {
        return huanbi;
    }

    public void setHuanbi(String huanbi) {
        this.huanbi = huanbi;
    }
}

class GaoFaRenYuan {
    private String index;
    private String name;
    private String department;
    private String count;
    private String detail;

    public GaoFaRenYuan() {

    }

    public GaoFaRenYuan(String index, String name, String department, String count, String detail) {
        this.index = index;
        this.name = name;
        this.department = department;
        this.count = count;
        this.detail = detail;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}

/**
 * 报告
 */
public class Report {
    private static final String[] ChineseIndex = new String[] { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十", "二十一", "二十二", "二十三", "二十四", "二十五", "二十六", "二十七", "二十八", "二十九", "三十", "三十一", "三十二",
            "三十三", "三十四", "三十五", "三十六", "三十七", "三十八", "三十九", "四十", "四十一", "四十二", "四十三", "四十四", "四十五", "四十六", "四十七", "四十八", "四十九", "五十", "五十一", "五十二", "五十三", "五十四", "五十五", "五十六", "五十七", "五十八", "五十九", "六十", "六十一", "六十二", "六十三", "六十四", "六十五", "六十六", "六十七", "六十八",
            "六十九", "七十", "七十一", "七十二", "七十三", "七十四", "七十五", "七十六", "七十七", "七十八", "七十九", "八十", "八十一", "八十二", "八十三", "八十四", "八十五", "八十六", "八十七", "八十八", "八十九", "九十", "九十一", "九十二", "九十三", "九十四", "九十五", "九十六", "九十七", "九十八", "九十九" };

    private Connection connection;
    private SimpleDateFormat simpleDateFormat;
    private final HashMap<String, String> reportTemplateFileNameMap;

    public Report(final Connection connection) throws Exception {
        this.connection = connection;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        {
            final File file = new File(Resource.REPORT_TEMPLATE_DIR_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        this.reportTemplateFileNameMap = new HashMap<>();
        {
            final ArrayList<String> list = IoKit.getDirectoryFileName(Resource.REPORT_TEMPLATE_DIR_PATH, null);
            for (int i = 0; i < list.size(); i++) {
                final String fileName = list.get(i);
                this.reportTemplateFileNameMap.put(Md5.encode(fileName.getBytes()), fileName);
            }
        }
    }

    /**
     * 获取五位一体大监督体系工作报告
     * 
     * @param uuid uuid（允许为null）
     * @param problemUuid 问题的uuid（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getWuWeiYiTi(final String startDatetime, final String endDatetime) {
        final Message msg = new Message();
        try {
            final String templateFileName = "五位一体大监督体系工作报告.docx";
            final File templateFile = new File(Resource.REPORT_TEMPLATE_DIR_PATH + templateFileName);
            final Calendar cal = Calendar.getInstance();
            final Integer currentYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer currentMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer currentDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
            final Integer startYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer startMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer startDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(endDatetime).getTime());
            final Integer endYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer endMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer endDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            final HashMap<String, Object> datas = new HashMap<>();
            datas.put("current_year", currentYear);
            datas.put("current_month", currentMonth);
            datas.put("current_day", currentDay);
            datas.put("start_year", startYear);
            datas.put("start_month", startMonth);
            datas.put("start_day", startDay);
            datas.put("end_year", endYear);
            datas.put("end_month", endMonth);
            datas.put("end_day", endDay);
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            ConfigureBuilder cb = Configure.builder();
            /////////////////////////////////////////////////////////////////
            // 政治监督
            /////////////////////////////////////////////////////////////////
            final ArrayList<WuWeiYiTi> zhengzhichuList = new ArrayList<>();
            {
                final ArrayList<String> typeUuidList = new ArrayList<>();
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null, ProblemDepartmentType.SystemType.ZHENG_ZHI); // !!!!!!!!!!!!!!!!!!!
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                    }
                }
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, typeUuidList.toArray(new String[typeUuidList.size()]), null, null, null, null, null, null, null, null, null, null, null, null, null, null, startDatetime,
                            endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final WuWeiYiTi dataObj = new WuWeiYiTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    zhengzhichuList.add(dataObj);
                    index++;
                }
                Collections.sort(zhengzhichuList, new Comparator<WuWeiYiTi>() {
                    @Override
                    public int compare(WuWeiYiTi obj1, WuWeiYiTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < zhengzhichuList.size(); i++) {
                    zhengzhichuList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < zhengzhichuList.size()) {
                    datas.put("list_zhengzhichu", zhengzhichuList);
                    datas.put("zhengzhi_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < zhengzhichuList.size(); i++) {
                            final WuWeiYiTi tObj = zhengzhichuList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= zhengzhichuList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("zhengzhi_desc", desc);
                    }
                } else {
                    datas.put("zhengzhi_desc", "    本时间段无相关问题"); // new
                    datas.put("zhengzhi_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < zhengzhichuList.size()) {
                    cb = cb.bind("list_zhengzhichu", policy);
                }
            }
            /////////////////////////////////////////////////////////////////
            // 警务监督
            /////////////////////////////////////////////////////////////////
            final ArrayList<WuWeiYiTi> jingwuList = new ArrayList<>();
            {
                final ArrayList<String> typeUuidList = new ArrayList<>();
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null, ProblemDepartmentType.SystemType.JING_WU); // !!!!!!!!!!!!!!!!!!!
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                    }
                }
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, typeUuidList.toArray(new String[typeUuidList.size()]), null, null, null, null, null, null, null, null, null, null, null, null, null, null, startDatetime,
                            endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final WuWeiYiTi dataObj = new WuWeiYiTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    jingwuList.add(dataObj);
                    index++;
                }
                Collections.sort(jingwuList, new Comparator<WuWeiYiTi>() {
                    @Override
                    public int compare(WuWeiYiTi obj1, WuWeiYiTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < jingwuList.size(); i++) {
                    jingwuList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < jingwuList.size()) {
                    datas.put("list_jingwu", jingwuList);
                    datas.put("jingwu_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < jingwuList.size(); i++) {
                            final WuWeiYiTi tObj = jingwuList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= jingwuList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("jingwu_desc", desc);
                    }
                } else {
                    datas.put("jingwu_desc", "    本时间段无相关问题"); // new
                    datas.put("jingwu_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < jingwuList.size()) {
                    cb = cb.bind("list_jingwu", policy);
                }
            }
            /////////////////////////////////////////////////////////////////
            // 执法监督
            /////////////////////////////////////////////////////////////////
            final ArrayList<WuWeiYiTi> zhifaList = new ArrayList<>();
            {
                final ArrayList<String> typeUuidList = new ArrayList<>();
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null, ProblemDepartmentType.SystemType.ZHI_FA); // !!!!!!!!!!!!!!!!!!!
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                    }
                }
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, typeUuidList.toArray(new String[typeUuidList.size()]), null, null, null, null, null, null, null, null, null, null, null, null, null, null, startDatetime,
                            endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final WuWeiYiTi dataObj = new WuWeiYiTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    zhifaList.add(dataObj);
                    index++;
                }
                Collections.sort(zhifaList, new Comparator<WuWeiYiTi>() {
                    @Override
                    public int compare(WuWeiYiTi obj1, WuWeiYiTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < zhifaList.size(); i++) {
                    zhifaList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < zhifaList.size()) {
                    datas.put("list_zhifa", zhifaList);
                    datas.put("zhifa_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < zhifaList.size(); i++) {
                            final WuWeiYiTi tObj = zhifaList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= zhifaList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("zhifa_desc", desc);
                    }
                } else {
                    datas.put("zhifa_desc", "    本时间段无相关问题"); // new
                    datas.put("zhifa_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < zhifaList.size()) {
                    cb = cb.bind("list_zhifa", policy);
                }
            }
            /////////////////////////////////////////////////////////////////
            // 纪检监督
            /////////////////////////////////////////////////////////////////
            final ArrayList<WuWeiYiTi> jijianList = new ArrayList<>();
            {
                final ArrayList<String> typeUuidList = new ArrayList<>();
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null, ProblemDepartmentType.SystemType.JI_JIAN); // !!!!!!!!!!!!!!!!!!!
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                    }
                }
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, typeUuidList.toArray(new String[typeUuidList.size()]), null, null, null, null, null, null, null, null, null, null, null, null, null, null, startDatetime,
                            endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final WuWeiYiTi dataObj = new WuWeiYiTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    jijianList.add(dataObj);
                    index++;
                }
                Collections.sort(jijianList, new Comparator<WuWeiYiTi>() {
                    @Override
                    public int compare(WuWeiYiTi obj1, WuWeiYiTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < jijianList.size(); i++) {
                    jijianList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < jijianList.size()) {
                    datas.put("list_jijian", jijianList);
                    datas.put("jijian_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < jijianList.size(); i++) {
                            final WuWeiYiTi tObj = jijianList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= jijianList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("jijian_desc", desc);
                    }
                } else {
                    datas.put("jijian_desc", "    本时间段无相关问题"); // new
                    datas.put("jijian_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < jijianList.size()) {
                    cb = cb.bind("list_jijian", policy);
                }
            }
            /////////////////////////////////////////////////////////////////
            // 民意监督
            /////////////////////////////////////////////////////////////////
            final ArrayList<WuWeiYiTi> minyiList = new ArrayList<>();
            {
                final ArrayList<String> typeUuidList = new ArrayList<>();
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null, ProblemDepartmentType.SystemType.MIN_YI); // !!!!!!!!!!!!!!!!!!!
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                    }
                }
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, typeUuidList.toArray(new String[typeUuidList.size()]), null, null, null, null, null, null, null, null, null, null, null, null, null, null, startDatetime,
                            endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final WuWeiYiTi dataObj = new WuWeiYiTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    minyiList.add(dataObj);
                    index++;
                }
                Collections.sort(minyiList, new Comparator<WuWeiYiTi>() {
                    @Override
                    public int compare(WuWeiYiTi obj1, WuWeiYiTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < minyiList.size(); i++) {
                    minyiList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < minyiList.size()) {
                    datas.put("list_minyi", minyiList);
                    datas.put("minyi_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < minyiList.size(); i++) {
                            final WuWeiYiTi tObj = minyiList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= minyiList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("minyi_desc", desc);
                    }
                } else {
                    datas.put("minyi_desc", "    本时间段无相关问题"); // new
                    datas.put("minyi_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < minyiList.size()) {
                    cb = cb.bind("list_minyi", policy);
                }
            }
            Configure config = cb.build();
            XWPFTemplate template = XWPFTemplate.compile(templateFile, config);
            final String fileUuid = StringKit.getUuidStr(true) + ".docx";
            final String targetFileName = Resource.REPORT_FILE_DIR_PATH + fileUuid;
            template.render(datas);
            int removeCount = 0;
            // 删除表格
            {
                if (0 >= zhengzhichuList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(0 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= jingwuList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(1 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= zhifaList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(2 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= jijianList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(3 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= minyiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(4 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
            }
            template.writeToFile(targetFileName);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("file_name", fileUuid);
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
     * 获取高发问题统计工作报告
     * 
     * @param uuid uuid（允许为null）
     * @param problemUuid 问题的uuid（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getGaoFaWenTi(final String startDatetime, final String endDatetime) {
        final Message msg = new Message();
        try {
            final String templateFileName = "高发问题统计工作报告.docx";
            final File templateFile = new File(Resource.REPORT_TEMPLATE_DIR_PATH + templateFileName);
            final Calendar cal = Calendar.getInstance();
            final Integer currentYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer currentMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer currentDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
            final Integer startYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer startMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer startDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(endDatetime).getTime());
            final Integer endYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer endMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer endDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            final HashMap<String, Object> datas = new HashMap<>();
            datas.put("current_year", currentYear);
            datas.put("current_month", currentMonth);
            datas.put("current_day", currentDay);
            datas.put("start_year", startYear);
            datas.put("start_month", startMonth);
            datas.put("start_day", startDay);
            datas.put("end_year", endYear);
            datas.put("end_month", endMonth);
            datas.put("end_day", endDay);
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            ConfigureBuilder cb = Configure.builder();
            /////////////////////////////////////////////////////////////////
            // 问题类型统计
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> problemTypeList = new ArrayList<>();
            {
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final GaoFaWenTi dataObj = new GaoFaWenTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    problemTypeList.add(dataObj);
                    index++;
                }
                Collections.sort(problemTypeList, new Comparator<GaoFaWenTi>() {
                    @Override
                    public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < problemTypeList.size(); i++) {
                    problemTypeList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < problemTypeList.size()) {
                    datas.put("list_problem_type", problemTypeList);
                    datas.put("problem_type_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < problemTypeList.size(); i++) {
                            final GaoFaWenTi tObj = problemTypeList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= problemTypeList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("problem_type_desc", desc);
                    }
                } else {
                    datas.put("problem_type_desc", "    本时间段无相关问题"); // new
                    datas.put("problem_type_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < problemTypeList.size()) {
                    cb = cb.bind("list_problem_type", policy);
                }
            }
            /////////////////////////////////////////////////////////////////
            // 墙子派出所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> qiangziList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "墙子派出所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        qiangziList.add(dataObj);
                        index++;
                    }
                    Collections.sort(qiangziList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < qiangziList.size(); i++) {
                        qiangziList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < qiangziList.size()) {
                        datas.put("list_qiangzi", qiangziList);
                        datas.put("qiangzi_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < qiangziList.size(); i++) {
                                final GaoFaWenTi tObj = qiangziList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("qiangzi_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(qiangziList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("qiangzi_desc", "本时间段无相关问题"); // new
                        datas.put("qiangzi_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < qiangziList.size()) {
                        cb = cb.bind("list_qiangzi", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 光复道派出所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> guangfuList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "光复道派出所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        guangfuList.add(dataObj);
                        index++;
                    }
                    Collections.sort(guangfuList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < guangfuList.size(); i++) {
                        guangfuList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < guangfuList.size()) {
                        datas.put("list_guangfu", guangfuList);
                        datas.put("guangfu_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < guangfuList.size(); i++) {
                                final GaoFaWenTi tObj = guangfuList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("guangfu_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(guangfuList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("guangfu_desc", "本时间段无相关问题"); // new
                        datas.put("guangfu_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < guangfuList.size()) {
                        cb = cb.bind("list_guangfu", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 望海楼派出所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> wanghaiList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "望海楼派出所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        wanghaiList.add(dataObj);
                        index++;
                    }
                    Collections.sort(wanghaiList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < wanghaiList.size(); i++) {
                        wanghaiList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < wanghaiList.size()) {
                        datas.put("list_wanghai", wanghaiList);
                        datas.put("wanghai_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < wanghaiList.size(); i++) {
                                final GaoFaWenTi tObj = wanghaiList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("wanghai_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(wanghaiList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("wanghai_desc", "本时间段无相关问题"); // new
                        datas.put("wanghai_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < wanghaiList.size()) {
                        cb = cb.bind("list_wanghai", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 鸿顺里派出所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> hongshunList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "鸿顺里派出所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        hongshunList.add(dataObj);
                        index++;
                    }
                    Collections.sort(hongshunList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < hongshunList.size(); i++) {
                        hongshunList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < hongshunList.size()) {
                        datas.put("list_hongshun", hongshunList);
                        datas.put("hongshun_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < hongshunList.size(); i++) {
                                final GaoFaWenTi tObj = hongshunList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("hongshun_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(hongshunList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("hongshun_desc", "本时间段无相关问题"); // new
                        datas.put("hongshun_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < hongshunList.size()) {
                        cb = cb.bind("list_hongshun", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 新开河派出所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> xinheList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "新开河派出所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        xinheList.add(dataObj);
                        index++;
                    }
                    Collections.sort(xinheList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < xinheList.size(); i++) {
                        xinheList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < xinheList.size()) {
                        datas.put("list_xinhe", xinheList);
                        datas.put("xinhe_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < xinheList.size(); i++) {
                                final GaoFaWenTi tObj = xinheList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("xinhe_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(xinheList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("xinhe_desc", "本时间段无相关问题"); // new
                        datas.put("xinhe_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < xinheList.size()) {
                        cb = cb.bind("list_xinhe", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 月牙河派出所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> yueyaList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "月牙河派出所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        yueyaList.add(dataObj);
                        index++;
                    }
                    Collections.sort(yueyaList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < yueyaList.size(); i++) {
                        yueyaList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < yueyaList.size()) {
                        datas.put("list_yueya", yueyaList);
                        datas.put("yueya_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < yueyaList.size(); i++) {
                                final GaoFaWenTi tObj = yueyaList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("yueya_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(yueyaList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("yueya_desc", "本时间段无相关问题"); // new
                        datas.put("yueya_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < yueyaList.size()) {
                        cb = cb.bind("list_yueya", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 宁园街派出所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> ningyuanList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "宁园街派出所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        ningyuanList.add(dataObj);
                        index++;
                    }
                    Collections.sort(ningyuanList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < ningyuanList.size(); i++) {
                        ningyuanList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < ningyuanList.size()) {
                        datas.put("list_ningyuan", ningyuanList);
                        datas.put("ningyuan_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < ningyuanList.size(); i++) {
                                final GaoFaWenTi tObj = ningyuanList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("ningyuan_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(ningyuanList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("ningyuan_desc", "本时间段无相关问题"); // new
                        datas.put("ningyuan_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < ningyuanList.size()) {
                        cb = cb.bind("list_ningyuan", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 江都路派出所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> jiangduList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "江都路派出所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        jiangduList.add(dataObj);
                        index++;
                    }
                    Collections.sort(jiangduList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < jiangduList.size(); i++) {
                        jiangduList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < jiangduList.size()) {
                        datas.put("list_jiangdu", jiangduList);
                        datas.put("jiangdu_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < jiangduList.size(); i++) {
                                final GaoFaWenTi tObj = jiangduList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("jiangdu_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(jiangduList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("jiangdu_desc", "本时间段无相关问题"); // new
                        datas.put("jiangdu_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < jiangduList.size()) {
                        cb = cb.bind("list_jiangdu", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 建昌道派出所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> jianchangList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "建昌道派出所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        jianchangList.add(dataObj);
                        index++;
                    }
                    Collections.sort(jianchangList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < jianchangList.size(); i++) {
                        jianchangList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < jianchangList.size()) {
                        datas.put("list_jianchang", jianchangList);
                        datas.put("jianchang_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < jianchangList.size(); i++) {
                                final GaoFaWenTi tObj = jianchangList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("jianchang_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(jianchangList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("jianchang_desc", "本时间段无相关问题"); // new
                        datas.put("jianchang_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < jianchangList.size()) {
                        cb = cb.bind("list_jianchang", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 铁东路派出所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> tiedongList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "铁东路派出所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        tiedongList.add(dataObj);
                        index++;
                    }
                    Collections.sort(tiedongList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < tiedongList.size(); i++) {
                        tiedongList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < tiedongList.size()) {
                        datas.put("list_tiedong", tiedongList);
                        datas.put("tiedong_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < tiedongList.size(); i++) {
                                final GaoFaWenTi tObj = tiedongList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("tiedong_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(tiedongList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("tiedong_desc", "本时间段无相关问题"); // new
                        datas.put("tiedong_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < tiedongList.size()) {
                        cb = cb.bind("list_tiedong", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 指挥室
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> zhihuiList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "指挥室", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        zhihuiList.add(dataObj);
                        index++;
                    }
                    Collections.sort(zhihuiList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < zhihuiList.size(); i++) {
                        zhihuiList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < zhihuiList.size()) {
                        datas.put("list_zhihui", zhihuiList);
                        datas.put("zhihui_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < zhihuiList.size(); i++) {
                                final GaoFaWenTi tObj = zhihuiList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("zhihui_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(zhihuiList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("zhihui_desc", "本时间段无相关问题"); // new
                        datas.put("zhihui_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < zhihuiList.size()) {
                        cb = cb.bind("list_zhihui", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 政治处
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> zhengzhiList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "政治处", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        zhengzhiList.add(dataObj);
                        index++;
                    }
                    Collections.sort(zhengzhiList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < zhengzhiList.size(); i++) {
                        zhengzhiList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < zhengzhiList.size()) {
                        datas.put("list_zhengzhi", zhengzhiList);
                        datas.put("zhengzhi_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < zhengzhiList.size(); i++) {
                                final GaoFaWenTi tObj = zhengzhiList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("zhengzhi_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(zhengzhiList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("zhengzhi_desc", "本时间段无相关问题"); // new
                        datas.put("zhengzhi_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < zhengzhiList.size()) {
                        cb = cb.bind("list_zhengzhi", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 督察审计支队
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> duchaList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "督察审计支队", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        duchaList.add(dataObj);
                        index++;
                    }
                    Collections.sort(duchaList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < duchaList.size(); i++) {
                        duchaList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < duchaList.size()) {
                        datas.put("list_ducha", duchaList);
                        datas.put("ducha_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < duchaList.size(); i++) {
                                final GaoFaWenTi tObj = duchaList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("ducha_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(duchaList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("ducha_desc", "本时间段无相关问题"); // new
                        datas.put("ducha_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < duchaList.size()) {
                        cb = cb.bind("list_ducha", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 纪委办公室
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> jiweiList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "纪委办公室", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        jiweiList.add(dataObj);
                        index++;
                    }
                    Collections.sort(jiweiList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < jiweiList.size(); i++) {
                        jiweiList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < jiweiList.size()) {
                        datas.put("list_jiwei", jiweiList);
                        datas.put("jiwei_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < jiweiList.size(); i++) {
                                final GaoFaWenTi tObj = jiweiList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("jiwei_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(jiweiList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("jiwei_desc", "本时间段无相关问题"); // new
                        datas.put("jiwei_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < jiweiList.size()) {
                        cb = cb.bind("list_jiwei", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 警务保障室
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> jingwuList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "警务保障室", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        jingwuList.add(dataObj);
                        index++;
                    }
                    Collections.sort(jingwuList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < jingwuList.size(); i++) {
                        jingwuList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < jingwuList.size()) {
                        datas.put("list_jingwu", jingwuList);
                        datas.put("jingwu_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < jingwuList.size(); i++) {
                                final GaoFaWenTi tObj = jingwuList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("jingwu_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(jingwuList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("jingwu_desc", "本时间段无相关问题"); // new
                        datas.put("jingwu_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < jingwuList.size()) {
                        cb = cb.bind("list_jingwu", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 治安管理支队
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> zhianList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "治安管理支队", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        zhianList.add(dataObj);
                        index++;
                    }
                    Collections.sort(zhianList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < zhianList.size(); i++) {
                        zhianList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < zhianList.size()) {
                        datas.put("list_zhian", zhianList);
                        datas.put("zhian_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < zhianList.size(); i++) {
                                final GaoFaWenTi tObj = zhianList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("zhian_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(zhianList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("zhian_desc", "本时间段无相关问题"); // new
                        datas.put("zhian_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < zhianList.size()) {
                        cb = cb.bind("list_zhian", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 法制支队
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> fazhiList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "法制支队", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        fazhiList.add(dataObj);
                        index++;
                    }
                    Collections.sort(fazhiList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < fazhiList.size(); i++) {
                        fazhiList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < fazhiList.size()) {
                        datas.put("list_fazhi", fazhiList);
                        datas.put("fazhi_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < fazhiList.size(); i++) {
                                final GaoFaWenTi tObj = fazhiList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("fazhi_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(fazhiList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("fazhi_desc", "本时间段无相关问题"); // new
                        datas.put("fazhi_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < fazhiList.size()) {
                        cb = cb.bind("list_fazhi", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 国内安全保卫支队
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> guoneiList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "国内安全保卫支队", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        guoneiList.add(dataObj);
                        index++;
                    }
                    Collections.sort(guoneiList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < guoneiList.size(); i++) {
                        guoneiList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < guoneiList.size()) {
                        datas.put("list_guonei", guoneiList);
                        datas.put("guonei_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < guoneiList.size(); i++) {
                                final GaoFaWenTi tObj = guoneiList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("guonei_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(guoneiList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("guonei_desc", "本时间段无相关问题"); // new
                        datas.put("guonei_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < guoneiList.size()) {
                        cb = cb.bind("list_guonei", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 打击犯罪侦查支队
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> dajiList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "打击犯罪侦查支队", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        dajiList.add(dataObj);
                        index++;
                    }
                    Collections.sort(dajiList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < dajiList.size(); i++) {
                        dajiList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < dajiList.size()) {
                        datas.put("list_daji", dajiList);
                        datas.put("daji_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < dajiList.size(); i++) {
                                final GaoFaWenTi tObj = dajiList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("daji_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(dajiList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("daji_desc", "本时间段无相关问题"); // new
                        datas.put("daji_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < dajiList.size()) {
                        cb = cb.bind("list_daji", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 网络安全保卫支队
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> wangluoList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "网络安全保卫支队", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        wangluoList.add(dataObj);
                        index++;
                    }
                    Collections.sort(wangluoList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < wangluoList.size(); i++) {
                        wangluoList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < wangluoList.size()) {
                        datas.put("list_wangluo", wangluoList);
                        datas.put("wangluo_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < wangluoList.size(); i++) {
                                final GaoFaWenTi tObj = wangluoList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("wangluo_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(wangluoList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("wangluo_desc", "本时间段无相关问题"); // new
                        datas.put("wangluo_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < wangluoList.size()) {
                        cb = cb.bind("list_wangluo", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 科技信息化支队
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> kejiList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "科技信息化支队", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        kejiList.add(dataObj);
                        index++;
                    }
                    Collections.sort(kejiList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < kejiList.size(); i++) {
                        kejiList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < kejiList.size()) {
                        datas.put("list_keji", kejiList);
                        datas.put("keji_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < kejiList.size(); i++) {
                                final GaoFaWenTi tObj = kejiList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("keji_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(kejiList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("keji_desc", "本时间段无相关问题"); // new
                        datas.put("keji_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < kejiList.size()) {
                        cb = cb.bind("list_keji", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 特警支队
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> tejingList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "特警支队", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        tejingList.add(dataObj);
                        index++;
                    }
                    Collections.sort(tejingList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < tejingList.size(); i++) {
                        tejingList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < tejingList.size()) {
                        datas.put("list_tejing", tejingList);
                        datas.put("tejing_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < tejingList.size(); i++) {
                                final GaoFaWenTi tObj = tejingList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("tejing_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(tejingList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("tejing_desc", "本时间段无相关问题"); // new
                        datas.put("tejing_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < tejingList.size()) {
                        cb = cb.bind("list_tejing", policy);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////
            // 看守所
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaWenTi> kanshouList = new ArrayList<>();
            {
                String toDepartmentUuid = null;
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, null, "看守所", null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    if (0 < array.length()) {
                        toDepartmentUuid = array.getJSONObject(0).getString("uuid");
                    }
                }
                if (null != toDepartmentUuid) {
                    // 问题类型map
                    final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject problem = array.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                final JSONArray arr = new JSONArray();
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            } else {
                                final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                                arr.put(problem);
                                problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                            }
                        }
                    }
                    int index = 1;
                    for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaWenTi dataObj = new GaoFaWenTi();
                        dataObj.setIndex(String.valueOf(index));
                        dataObj.setName(key);
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                            } else {
                                toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        kanshouList.add(dataObj);
                        index++;
                    }
                    Collections.sort(kanshouList, new Comparator<GaoFaWenTi>() {
                        @Override
                        public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < kanshouList.size(); i++) {
                        kanshouList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < kanshouList.size()) {
                        datas.put("list_kanshou", kanshouList);
                        datas.put("kanshou_nothing", "");
                        // new
                        {
                            String desc = "";
                            for (int i = 0; i < kanshouList.size(); i++) {
                                final GaoFaWenTi tObj = kanshouList.get(i);
                                desc += String.format("%s%s条，", tObj.getName(), tObj.getCount());
                            }
                            datas.put("kanshou_desc", String.format("%d条问题，分别为：%s。", Integer.valueOf(kanshouList.size()), desc.substring(0, desc.length() - 1)));
                        }
                    } else {
                        datas.put("kanshou_desc", "本时间段无相关问题"); // new
                        datas.put("kanshou_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < kanshouList.size()) {
                        cb = cb.bind("list_kanshou", policy);
                    }
                }
            }
            // /////////////////////////////////////////////////////////////////
            // // 指挥室
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> zhihuiList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "指挥室", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // zhihuiList.add(dataObj);
            // index++;
            // }
            // Collections.sort(zhihuiList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < zhihuiList.size(); i++) {
            // zhihuiList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < zhihuiList.size()) {
            // datas.put("list_zhihui", zhihuiList);
            // datas.put("zhihui_nothing", "");
            // } else {
            // datas.put("zhihui_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < zhihuiList.size()) {
            // cb = cb.bind("list_zhihui", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 政治处
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> zhengzhiList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "政治处", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // zhengzhiList.add(dataObj);
            // index++;
            // }
            // Collections.sort(zhengzhiList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < zhengzhiList.size(); i++) {
            // zhengzhiList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < zhengzhiList.size()) {
            // datas.put("list_zhengzhi", zhengzhiList);
            // datas.put("zhengzhi_nothing", "");
            // } else {
            // datas.put("zhengzhi_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < zhengzhiList.size()) {
            // cb = cb.bind("list_zhengzhi", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 督察审计支队
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> dushenList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "督察审计支队", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // dushenList.add(dataObj);
            // index++;
            // }
            // Collections.sort(dushenList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < dushenList.size(); i++) {
            // dushenList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < dushenList.size()) {
            // datas.put("list_dushen", dushenList);
            // datas.put("dushen_nothing", "");
            // } else {
            // datas.put("dushen_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < dushenList.size()) {
            // cb = cb.bind("list_dushen", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 纪委办公室
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> jiweiList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "纪委办公室", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // jiweiList.add(dataObj);
            // index++;
            // }
            // Collections.sort(jiweiList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < jiweiList.size(); i++) {
            // jiweiList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < jiweiList.size()) {
            // datas.put("list_jiwei", jiweiList);
            // datas.put("jiwei_nothing", "");
            // } else {
            // datas.put("jiwei_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < jiweiList.size()) {
            // cb = cb.bind("list_jiwei", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 警务保障室
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> jingwuList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "警务保障室", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // jingwuList.add(dataObj);
            // index++;
            // }
            // Collections.sort(jingwuList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < jingwuList.size(); i++) {
            // jingwuList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < jingwuList.size()) {
            // datas.put("list_jingwu", jingwuList);
            // datas.put("jingwu_nothing", "");
            // } else {
            // datas.put("jingwu_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < jingwuList.size()) {
            // cb = cb.bind("list_jingwu", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 治安管理支队
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> zhianList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "治安管理支队", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // zhianList.add(dataObj);
            // index++;
            // }
            // Collections.sort(zhianList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < zhianList.size(); i++) {
            // zhianList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < zhianList.size()) {
            // datas.put("list_zhian", zhianList);
            // datas.put("zhian_nothing", "");
            // } else {
            // datas.put("zhian_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < zhianList.size()) {
            // cb = cb.bind("list_zhian", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 法制支队
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> fazhiList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "法制支队", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // fazhiList.add(dataObj);
            // index++;
            // }
            // Collections.sort(fazhiList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < fazhiList.size(); i++) {
            // fazhiList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < fazhiList.size()) {
            // datas.put("list_fazhi", fazhiList);
            // datas.put("fazhi_nothing", "");
            // } else {
            // datas.put("fazhi_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < fazhiList.size()) {
            // cb = cb.bind("list_fazhi", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 国内安全保卫支队
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> anquanbaoweiList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "国内安全保卫支队", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // anquanbaoweiList.add(dataObj);
            // index++;
            // }
            // Collections.sort(anquanbaoweiList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < anquanbaoweiList.size(); i++) {
            // anquanbaoweiList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < anquanbaoweiList.size()) {
            // datas.put("list_anquanbaowei", anquanbaoweiList);
            // datas.put("anquanbaowei_nothing", "");
            // } else {
            // datas.put("anquanbaowei_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < anquanbaoweiList.size()) {
            // cb = cb.bind("list_anquanbaowei", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 打击犯罪侦查支队
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> dajizuifanList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "打击犯罪侦查支队", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // dajizuifanList.add(dataObj);
            // index++;
            // }
            // Collections.sort(dajizuifanList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < dajizuifanList.size(); i++) {
            // dajizuifanList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < dajizuifanList.size()) {
            // datas.put("list_dajizuifan", dajizuifanList);
            // datas.put("dajizuifan_nothing", "");
            // } else {
            // datas.put("dajizuifan_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < dajizuifanList.size()) {
            // cb = cb.bind("list_dajizuifan", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 网络安全保卫支队
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> wangluoanquanList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "网络安全保卫支队", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // wangluoanquanList.add(dataObj);
            // index++;
            // }
            // Collections.sort(wangluoanquanList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < wangluoanquanList.size(); i++) {
            // wangluoanquanList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < wangluoanquanList.size()) {
            // datas.put("list_wangluoanquan", wangluoanquanList);
            // datas.put("wangluoanquan_nothing", "");
            // } else {
            // datas.put("wangluoanquan_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < wangluoanquanList.size()) {
            // cb = cb.bind("list_wangluoanquan", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 科技信息化支队
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> kejixinxiList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "科技信息化支队", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // kejixinxiList.add(dataObj);
            // index++;
            // }
            // Collections.sort(kejixinxiList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < kejixinxiList.size(); i++) {
            // kejixinxiList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < kejixinxiList.size()) {
            // datas.put("list_kejixinxi", kejixinxiList);
            // datas.put("kejixinxi_nothing", "");
            // } else {
            // datas.put("kejixinxi_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < kejixinxiList.size()) {
            // cb = cb.bind("list_kejixinxi", policy);
            // }
            // }
            // }
            // /////////////////////////////////////////////////////////////////
            // // 看守所
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<GaoFaWenTi> kanshousuoList = new ArrayList<>();
            // {
            // String toDepartmentUuid = null;
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, null, "看守所", null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 < array.length()) {
            // toDepartmentUuid = array.getJSONObject(0).getString("uuid");
            // }
            // }
            // if (null != toDepartmentUuid) {
            // // 问题类型map
            // final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // } else {
            // final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
            // arr.put(problem);
            // problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
            // }
            // }
            // }
            // int index = 1;
            // for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final GaoFaWenTi dataObj = new GaoFaWenTi();
            // dataObj.setIndex(String.valueOf(index));
            // dataObj.setName(key);
            // dataObj.setCount(String.valueOf(arr.length()));
            // final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
            // for (int i = 0; i < arr.length(); i++) {
            // final JSONObject problem = arr.getJSONObject(i);
            // if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
            // } else {
            // toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
            // }
            // }
            // final ArrayList<JSONObject> list = new ArrayList<>();
            // for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
            // final String key2 = entry2.getKey();
            // final Integer count = entry2.getValue();
            // final JSONObject obj = new JSONObject();
            // obj.put("name", key2);
            // obj.put("count", count);
            // list.add(obj);
            // }
            // Collections.sort(list, new Comparator<JSONObject>() {
            // @Override
            // public int compare(JSONObject obj1, JSONObject obj2) {
            // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
            // }
            // });
            // String str = "";
            // for (int i = 0; i < list.size(); i++) {
            // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
            // }
            // str = str.substring(0, str.length() - 1);
            // dataObj.setDetail(str);
            // kanshousuoList.add(dataObj);
            // index++;
            // }
            // Collections.sort(kanshousuoList, new Comparator<GaoFaWenTi>() {
            // @Override
            // public int compare(GaoFaWenTi obj1, GaoFaWenTi obj2) {
            // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
            // }
            // });
            // for (int i = 0; i < kanshousuoList.size(); i++) {
            // kanshousuoList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < kanshousuoList.size()) {
            // datas.put("list_kanshousuo", kanshousuoList);
            // datas.put("kanshousuo_nothing", "");
            // } else {
            // datas.put("kanshousuo_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < kanshousuoList.size()) {
            // cb = cb.bind("list_kanshousuo", policy);
            // }
            // }
            // }
            Configure config = cb.build();
            XWPFTemplate template = XWPFTemplate.compile(templateFile, config);
            final String fileUuid = StringKit.getUuidStr(true) + ".docx";
            final String targetFileName = Resource.REPORT_FILE_DIR_PATH + fileUuid;
            template.render(datas);
            int removeCount = 0;
            // 删除表格
            {
                if (0 >= problemTypeList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(0 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= qiangziList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(1 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= guangfuList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(2 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= wanghaiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(3 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= hongshunList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(4 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= xinheList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(5 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= yueyaList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(6 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= ningyuanList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(7 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= jiangduList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(8 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= jianchangList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(9 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= tiedongList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(10 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= zhihuiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(11 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= zhengzhiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(12 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= duchaList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(13 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= jiweiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(14 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= jingwuList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(15 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= zhianList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(16 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= fazhiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(17 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= guoneiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(18 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= dajiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(19 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= wangluoList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(20 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= kejiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(21 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= tejingList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(22 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= kanshouList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(23 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
            }
            template.writeToFile(targetFileName);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("file_name", fileUuid);
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
     * 获取各单位反馈统计报告
     * 
     * @param uuid uuid（允许为null）
     * @param problemUuid 问题的uuid（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getDanWeiFanKui(final String startDatetime, final String endDatetime) {
        final Message msg = new Message();
        try {
            final String templateFileName = "各单位反馈统计报告.docx";
            final File templateFile = new File(Resource.REPORT_TEMPLATE_DIR_PATH + templateFileName);
            final Calendar cal = Calendar.getInstance();
            final Integer currentYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer currentMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer currentDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
            final Integer startYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer startMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer startDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(endDatetime).getTime());
            final Integer endYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer endMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer endDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            final HashMap<String, Object> datas = new HashMap<>();
            datas.put("current_year", currentYear);
            datas.put("current_month", currentMonth);
            datas.put("current_day", currentDay);
            datas.put("start_year", startYear);
            datas.put("start_month", startMonth);
            datas.put("start_day", startDay);
            datas.put("end_year", endYear);
            datas.put("end_month", endMonth);
            datas.put("end_day", endDay);
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            ConfigureBuilder cb = Configure.builder();
            /////////////////////////////////////////////////////////////////
            // 当前
            /////////////////////////////////////////////////////////////////
            final ArrayList<DanWeiFanKui> currentList = new ArrayList<>();
            {
                final ArrayList<JSONObject> toDepartmentList = new ArrayList<>();
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, Integer.valueOf(2), null, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        toDepartmentList.add(array.getJSONObject(i));
                    }
                }
                for (int j = 0; j < toDepartmentList.size(); j++) {
                    final String toDepartmentUuid = toDepartmentList.get(j).getString("uuid");
                    // 问题map
                    final HashMap<String, JSONArray> problemHm = new HashMap<>();
                    // 反馈map
                    final HashMap<String, JSONArray> feedbackHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        if (0 >= array.length()) {
                            final DanWeiFanKui dataObj = new DanWeiFanKui();
                            dataObj.setName(toDepartmentList.get(j).getString("name"));
                            dataObj.setProblemCount("0");
                            dataObj.setFeedbackCount("0");
                            dataObj.setFeedbackPercent("0");
                            currentList.add(dataObj);
                        } else {
                            for (int i = 0; i < array.length(); i++) {
                                final JSONObject problem = array.getJSONObject(i);
                                if (null == problemHm.get(problem.getString("to_department_name"))) {
                                    final JSONArray arr = new JSONArray();
                                    arr.put(problem);
                                    problemHm.put(problem.getString("to_department_name"), arr);
                                } else {
                                    final JSONArray arr = problemHm.get(problem.getString("to_department_name"));
                                    arr.put(problem);
                                    problemHm.put(problem.getString("to_department_name"), arr);
                                }
                                if (problem.getString("status").equalsIgnoreCase("FEEDBACK")) {
                                    if (null == feedbackHm.get(problem.getString("to_department_name"))) {
                                        final JSONArray arr = new JSONArray();
                                        arr.put(problem);
                                        feedbackHm.put(problem.getString("to_department_name"), arr);
                                    } else {
                                        final JSONArray arr = feedbackHm.get(problem.getString("to_department_name"));
                                        arr.put(problem);
                                        feedbackHm.put(problem.getString("to_department_name"), arr);
                                    }
                                }
                            }
                        }
                    }
                    for (Map.Entry<String, JSONArray> entry : problemHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final DanWeiFanKui dataObj = new DanWeiFanKui();
                        dataObj.setName(key);
                        dataObj.setProblemCount(String.valueOf(arr.length()));
                        if (null != feedbackHm.get(key)) {
                            dataObj.setFeedbackCount(String.valueOf(feedbackHm.get(key).length()));
                        } else {
                            dataObj.setFeedbackCount("0");
                        }
                        dataObj.setFeedbackPercent(new BigDecimal(dataObj.getFeedbackCount()).divide(new BigDecimal(dataObj.getProblemCount()), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).toString() + "%");
                        currentList.add(dataObj);
                    }
                }
                Collections.sort(currentList, new Comparator<DanWeiFanKui>() {
                    @Override
                    public int compare(DanWeiFanKui obj1, DanWeiFanKui obj2) {
                        return Integer.valueOf(obj2.getProblemCount()).compareTo(Integer.valueOf(obj1.getProblemCount()));
                    }
                });
                for (int i = 0; i < currentList.size(); i++) {
                    currentList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < currentList.size()) {
                    datas.put("list_current", currentList);
                    datas.put("current_nothing", "");
                } else {
                    datas.put("current_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < currentList.size()) {
                    cb = cb.bind("list_current", policy);
                }
            }
            final ArrayList<DanWeiFanKui> huanbiList = new ArrayList<>();
            // /////////////////////////////////////////////////////////////////
            // // 环比
            // /////////////////////////////////////////////////////////////////
            // final ArrayList<DanWeiFanKui> huanbiList = new ArrayList<>();
            // {
            // final ArrayList<JSONObject> toDepartmentList = new ArrayList<>();
            // {
            // final Department obj = new Department(this.connection);
            // final Message resultMsg = obj.getDepartment(null, null, null, Integer.valueOf(2), null, null, null, null, null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // for (int i = 0; i < array.length(); i++) {
            // toDepartmentList.add(array.getJSONObject(i));
            // }
            // }
            // String newStartDatetime = null;
            // {
            // final Date d1 = this.simpleDateFormat.parse(startDatetime);
            // final Date d2 = this.simpleDateFormat.parse(endDatetime);
            // int day = (int) ((d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000));
            // cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
            // cal.add(Calendar.DAY_OF_YEAR, day * -1);
            // newStartDatetime = this.simpleDateFormat.format(cal.getTime());
            // }
            // for (int j = 0; j < toDepartmentList.size(); j++) {
            // final String toDepartmentUuid = toDepartmentList.get(j).getString("uuid");
            // // 问题map
            // final HashMap<String, JSONArray> problemHm = new HashMap<>();
            // // 反馈map
            // final HashMap<String, JSONArray> feedbackHm = new HashMap<>();
            // {
            // final Problem obj = new Problem(this.connection);
            // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, toDepartmentUuid, null, null, null, null, null, null, null, newStartDatetime, startDatetime, null, null, null,
            // null);
            // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            // return resultMsg;
            // }
            // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
            // if (0 >= array.length()) {
            // final DanWeiFanKui dataObj = new DanWeiFanKui();
            // dataObj.setName(toDepartmentList.get(j).getString("name"));
            // dataObj.setProblemCount("0");
            // dataObj.setFeedbackCount("0");
            // dataObj.setFeedbackPercent("0");
            // huanbiList.add(dataObj);
            // } else {
            // for (int i = 0; i < array.length(); i++) {
            // final JSONObject problem = array.getJSONObject(i);
            // if (null == problemHm.get(problem.getString("to_department_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // problemHm.put(problem.getString("to_department_name"), arr);
            // } else {
            // final JSONArray arr = problemHm.get(problem.getString("to_department_name"));
            // arr.put(problem);
            // problemHm.put(problem.getString("to_department_name"), arr);
            // }
            // if (problem.getString("status").equalsIgnoreCase("FEEDBACK")) {
            // if (null == feedbackHm.get(problem.getString("to_department_name"))) {
            // final JSONArray arr = new JSONArray();
            // arr.put(problem);
            // feedbackHm.put(problem.getString("to_department_name"), arr);
            // } else {
            // final JSONArray arr = feedbackHm.get(problem.getString("to_department_name"));
            // arr.put(problem);
            // feedbackHm.put(problem.getString("to_department_name"), arr);
            // }
            // }
            // }
            // }
            // }
            // for (Map.Entry<String, JSONArray> entry : problemHm.entrySet()) {
            // final String key = entry.getKey();
            // final JSONArray arr = entry.getValue();
            // final DanWeiFanKui dataObj = new DanWeiFanKui();
            // dataObj.setName(key);
            // dataObj.setProblemCount(String.valueOf(arr.length()));
            // if (null != feedbackHm.get(key)) {
            // dataObj.setFeedbackCount(String.valueOf(feedbackHm.get(key).length()));
            // } else {
            // dataObj.setFeedbackCount("0");
            // }
            // dataObj.setFeedbackPercent(new BigDecimal(dataObj.getFeedbackCount()).divide(new BigDecimal(dataObj.getProblemCount()), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).toString() + "%");
            // huanbiList.add(dataObj);
            // }
            // }
            // Collections.sort(huanbiList, new Comparator<DanWeiFanKui>() {
            // @Override
            // public int compare(DanWeiFanKui obj1, DanWeiFanKui obj2) {
            // return Integer.valueOf(obj2.getProblemCount()).compareTo(Integer.valueOf(obj1.getProblemCount()));
            // }
            // });
            // for (int i = 0; i < huanbiList.size(); i++) {
            // huanbiList.get(i).setIndex(String.valueOf(i + 1));
            // }
            // if (0 < huanbiList.size()) {
            // datas.put("list_huanbi", huanbiList);
            // datas.put("huanbi_nothing", "");
            // } else {
            // datas.put("huanbi_nothing", System.lineSeparator() + " 本时间段无相关问题");
            // }
            // if (0 < huanbiList.size()) {
            // cb = cb.bind("list_huanbi", policy);
            // }
            // }
            Configure config = cb.build();
            XWPFTemplate template = XWPFTemplate.compile(templateFile, config);
            final String fileUuid = StringKit.getUuidStr(true) + ".docx";
            final String targetFileName = Resource.REPORT_FILE_DIR_PATH + fileUuid;
            template.render(datas);
            int removeCount = 0;
            // 删除表格
            {
                if (0 >= currentList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(0 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                // if (0 >= huanbiList.size()) {
                //     final XWPFTable a = template.getXWPFDocument().getTables().get(1 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                //     if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                //         removeCount++;
                //     }
                // }
            }
            template.writeToFile(targetFileName);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("file_name", fileUuid);
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
     * 获取各单位自查统计报告
     * 
     * @param uuid uuid（允许为null）
     * @param problemUuid 问题的uuid（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getDanWeiZiCha(final String startDatetime, final String endDatetime) {
        final Message msg = new Message();
        try {
            final String templateFileName = "各单位自查统计报告.docx";
            final File templateFile = new File(Resource.REPORT_TEMPLATE_DIR_PATH + templateFileName);
            final Calendar cal = Calendar.getInstance();
            final Integer currentYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer currentMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer currentDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
            final Integer startYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer startMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer startDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(endDatetime).getTime());
            final Integer endYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer endMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer endDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            final HashMap<String, Object> datas = new HashMap<>();
            datas.put("current_year", currentYear);
            datas.put("current_month", currentMonth);
            datas.put("current_day", currentDay);
            datas.put("start_year", startYear);
            datas.put("start_month", startMonth);
            datas.put("start_day", startDay);
            datas.put("end_year", endYear);
            datas.put("end_month", endMonth);
            datas.put("end_day", endDay);
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            ConfigureBuilder cb = Configure.builder();
            /////////////////////////////////////////////////////////////////
            // 当前
            /////////////////////////////////////////////////////////////////
            final ArrayList<DanWeiZiCha> currentList = new ArrayList<>();
            {
                final ArrayList<JSONObject> toDepartmentList = new ArrayList<>();
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, Integer.valueOf(2), null, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        toDepartmentList.add(array.getJSONObject(i));
                    }
                }
                for (int j = 0; j < toDepartmentList.size(); j++) {
                    final String toDepartmentUuid = toDepartmentList.get(j).getString("uuid");
                    // 问题map
                    final HashMap<String, JSONArray> problemHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, Problem.ProblemType.SELF, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime,
                                null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        final DanWeiZiCha dataObj = new DanWeiZiCha();
                        dataObj.setName(toDepartmentList.get(j).getString("name"));
                        if (0 >= array.length()) {
                            dataObj.setCount("0");
                            dataObj.setDetail("无");
                            currentList.add(dataObj);
                        } else {
                            for (int i = 0; i < array.length(); i++) {
                                final JSONObject problem = array.getJSONObject(i);
                                if (null == problemHm.get(problem.getString("problem_department_type_name"))) {
                                    final JSONArray arr = new JSONArray();
                                    arr.put(problem);
                                    problemHm.put(problem.getString("problem_department_type_name"), arr);
                                } else {
                                    final JSONArray arr = problemHm.get(problem.getString("problem_department_type_name"));
                                    arr.put(problem);
                                    problemHm.put(problem.getString("problem_department_type_name"), arr);
                                }
                            }
                            final ArrayList<JSONObject> list = new ArrayList<>();
                            for (Map.Entry<String, JSONArray> entry2 : problemHm.entrySet()) {
                                final String key2 = entry2.getKey();
                                final JSONArray arr = entry2.getValue();
                                final JSONObject obj2 = new JSONObject();
                                obj2.put("name", key2);
                                obj2.put("count", arr.length());
                                list.add(obj2);
                            }
                            Collections.sort(list, new Comparator<JSONObject>() {
                                @Override
                                public int compare(JSONObject obj1, JSONObject obj2) {
                                    return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                                }
                            });
                            String str = "";
                            for (int i = 0; i < list.size(); i++) {
                                str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                            }
                            str = str.substring(0, str.length() - 1);
                            dataObj.setCount(String.valueOf(list.size()));
                            dataObj.setDetail(str);
                            currentList.add(dataObj);
                        }
                    }
                }
                Collections.sort(currentList, new Comparator<DanWeiZiCha>() {
                    @Override
                    public int compare(DanWeiZiCha obj1, DanWeiZiCha obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < currentList.size(); i++) {
                    currentList.get(i).setIndex(String.valueOf(i + 1));
                    currentList.get(i).setTongbi("0");
                }
                // if (0 < currentList.size()) {
                // datas.put("list_current", currentList);
                // datas.put("current_nothing", "");
                // } else {
                // datas.put("current_nothing", System.lineSeparator() + " 本时间段无相关问题");
                // }
                // if (0 < currentList.size()) {
                // cb = cb.bind("list_current", policy);
                // }
            }
            /////////////////////////////////////////////////////////////////
            // 环比
            /////////////////////////////////////////////////////////////////
            final ArrayList<DanWeiZiCha> huanbiList = new ArrayList<>();
            {
                final ArrayList<JSONObject> toDepartmentList = new ArrayList<>();
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, Integer.valueOf(2), null, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        toDepartmentList.add(array.getJSONObject(i));
                    }
                }
                String newStartDatetime = null;
                {
                    final Date d1 = this.simpleDateFormat.parse(startDatetime);
                    final Date d2 = this.simpleDateFormat.parse(endDatetime);
                    int day = (int) ((d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000));
                    cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
                    cal.add(Calendar.DAY_OF_YEAR, day * -1);
                    newStartDatetime = this.simpleDateFormat.format(cal.getTime());
                }
                for (int j = 0; j < toDepartmentList.size(); j++) {
                    final String toDepartmentUuid = toDepartmentList.get(j).getString("uuid");
                    // 问题map
                    final HashMap<String, JSONArray> problemHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, Problem.ProblemType.SELF, null, toDepartmentUuid, null, null, null, null, null, null, null, newStartDatetime, startDatetime,
                                null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        final DanWeiZiCha dataObj = new DanWeiZiCha();
                        dataObj.setName(toDepartmentList.get(j).getString("name"));
                        if (0 >= array.length()) {
                            dataObj.setCount("0");
                            dataObj.setDetail("无");
                            huanbiList.add(dataObj);
                        } else {
                            for (int i = 0; i < array.length(); i++) {
                                final JSONObject problem = array.getJSONObject(i);
                                if (null == problemHm.get(problem.getString("problem_department_type_name"))) {
                                    final JSONArray arr = new JSONArray();
                                    arr.put(problem);
                                    problemHm.put(problem.getString("problem_department_type_name"), arr);
                                } else {
                                    final JSONArray arr = problemHm.get(problem.getString("problem_department_type_name"));
                                    arr.put(problem);
                                    problemHm.put(problem.getString("problem_department_type_name"), arr);
                                }
                            }
                            final ArrayList<JSONObject> list = new ArrayList<>();
                            for (Map.Entry<String, JSONArray> entry2 : problemHm.entrySet()) {
                                final String key2 = entry2.getKey();
                                final JSONArray arr = entry2.getValue();
                                final JSONObject obj2 = new JSONObject();
                                obj2.put("name", key2);
                                obj2.put("count", arr.length());
                                list.add(obj2);
                            }
                            Collections.sort(list, new Comparator<JSONObject>() {
                                @Override
                                public int compare(JSONObject obj1, JSONObject obj2) {
                                    return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                                }
                            });
                            String str = "";
                            for (int i = 0; i < list.size(); i++) {
                                str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                            }
                            str = str.substring(0, str.length() - 1);
                            dataObj.setCount(String.valueOf(list.size()));
                            dataObj.setDetail(str);
                            huanbiList.add(dataObj);
                        }
                    }
                }
                Collections.sort(huanbiList, new Comparator<DanWeiZiCha>() {
                    @Override
                    public int compare(DanWeiZiCha obj1, DanWeiZiCha obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                // for (int i = 0; i < huanbiList.size(); i++) {
                // huanbiList.get(i).setIndex(String.valueOf(i + 1));
                // }
                // if (0 < huanbiList.size()) {
                // datas.put("list_huanbi", huanbiList);
                // datas.put("huanbi_nothing", "");
                // } else {
                // datas.put("huanbi_nothing", System.lineSeparator() + " 本时间段无相关问题");
                // }
                // if (0 < huanbiList.size()) {
                // cb = cb.bind("list_huanbi", policy);
                // }
            }
            // new
            for (int i = 0; i < currentList.size(); i++) {
                final DanWeiZiCha currentObj = currentList.get(i);
                for (int j = 0; j < huanbiList.size(); j++) {
                    final DanWeiZiCha huanbiObj = huanbiList.get(j);
                    // 环比增长率=(本期数-上期数)/上期数×100%，但上期数不能为0
                    if (currentObj.getName().equalsIgnoreCase(huanbiObj.getName())) {
                        final String prev = huanbiObj.getCount();
                        final String curr = currentObj.getCount();
                        if (0 >= Integer.valueOf(prev).byteValue()) {
                            currentObj.setHuanbi("0");
                        } else {
                            String signStr = "";
                            if (0 < Integer.valueOf(prev).compareTo(Integer.valueOf(curr))) {
                                signStr = "下降";
                            } else {
                                signStr = "上升";
                            }
                            try {
                                currentObj
                                        .setHuanbi(signStr + (new BigDecimal(curr).subtract(new BigDecimal(prev))).abs().divide(new BigDecimal(prev), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).toString() + "%");
                            } catch (final Exception e) {
                                currentObj.setHuanbi("-1");
                            }
                        }
                        break;
                    }
                }
            }
            // new
            if (0 < currentList.size()) {
                datas.put("list_current", currentList);
                datas.put("current_nothing", "");
                {
                    String desc = "";
                    for (int i = 0; i < currentList.size(); i++) {
                        final DanWeiZiCha tObj = currentList.get(i);
                        String end = System.lineSeparator();
                        if ((i + 1) >= currentList.size()) {
                            end = "";
                        }
                        if (!tObj.getCount().equalsIgnoreCase("0")) {
                            desc += String.format("    %d、%s自查问题数量%s条，分别为：%s。%s", Integer.valueOf(i + 1), tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        } else {
                            desc += String.format("    %d、%s自查问题数量%s条。%s", Integer.valueOf(i + 1), tObj.getName(), tObj.getCount(), end);
                        }
                    }
                    datas.put("zicha_desc", desc);
                }
            } else {
                datas.put("current_nothing", System.lineSeparator() + " 本时间段无相关问题");
                datas.put("zicha_desc", "    本时间段无相关问题");
            }
            if (0 < currentList.size()) {
                cb = cb.bind("list_current", policy);
            }
            Configure config = cb.build();
            XWPFTemplate template = XWPFTemplate.compile(templateFile, config);
            final String fileUuid = StringKit.getUuidStr(true) + ".docx";
            final String targetFileName = Resource.REPORT_FILE_DIR_PATH + fileUuid;
            template.render(datas);
            int removeCount = 0;
            // 删除表格
            {
                if (0 >= currentList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(0 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= huanbiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(1 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
            }
            template.writeToFile(targetFileName);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("file_name", fileUuid);
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
     * 获取各单位自查统计报告
     * 
     * @param uuid uuid（允许为null）
     * @param problemUuid 问题的uuid（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getDanWeiJianCha(final String startDatetime, final String endDatetime) {
        final Message msg = new Message();
        try {
            final String templateFileName = "各业务警种检查统计报告.docx";
            final File templateFile = new File(Resource.REPORT_TEMPLATE_DIR_PATH + templateFileName);
            final Calendar cal = Calendar.getInstance();
            final Integer currentYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer currentMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer currentDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
            final Integer startYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer startMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer startDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(endDatetime).getTime());
            final Integer endYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer endMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer endDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            final HashMap<String, Object> datas = new HashMap<>();
            datas.put("current_year", currentYear);
            datas.put("current_month", currentMonth);
            datas.put("current_day", currentDay);
            datas.put("start_year", startYear);
            datas.put("start_month", startMonth);
            datas.put("start_day", startDay);
            datas.put("end_year", endYear);
            datas.put("end_month", endMonth);
            datas.put("end_day", endDay);
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            ConfigureBuilder cb = Configure.builder();
            /////////////////////////////////////////////////////////////////
            // 当前
            /////////////////////////////////////////////////////////////////
            final ArrayList<DanWeiJianCha> currentList = new ArrayList<>();
            {
                final ArrayList<JSONObject> toDepartmentList = new ArrayList<>();
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, "0bdd5462a3c2443bbaf99e351b788371"/* 直属机构的type_uuid */, Integer.valueOf(2), null, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        if ((array.getJSONObject(i).getString("name").equalsIgnoreCase("看守所")) || (array.getJSONObject(i).getString("name").equalsIgnoreCase("特警支队"))) {
                            // 不包括看守所和特警支队
                            continue;
                        }
                        toDepartmentList.add(array.getJSONObject(i));
                    }
                }
                for (int j = 0; j < toDepartmentList.size(); j++) {
                    final String toDepartmentUuid = toDepartmentList.get(j).getString("uuid");
                    // 问题map
                    final HashMap<String, JSONArray> problemHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, Problem.ProblemType.CHECK, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime,
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, Problem.ProblemType.CHECK, toDepartmentUuid, null, null, null, null, null, null, null, null, startDatetime, endDatetime,
                                null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        final DanWeiJianCha dataObj = new DanWeiJianCha();
                        dataObj.setName(toDepartmentList.get(j).getString("name"));
                        if (0 >= array.length()) {
                            dataObj.setCount("0");
                            dataObj.setDetail("无");
                            currentList.add(dataObj);
                        } else {
                            for (int i = 0; i < array.length(); i++) {
                                final JSONObject problem = array.getJSONObject(i);
                                if (null == problemHm.get(problem.getString("to_department_name"))) {
                                    final JSONArray arr = new JSONArray();
                                    arr.put(problem);
                                    problemHm.put(problem.getString("to_department_name"), arr);
                                } else {
                                    final JSONArray arr = problemHm.get(problem.getString("to_department_name"));
                                    arr.put(problem);
                                    problemHm.put(problem.getString("to_department_name"), arr);
                                }
                            }
                            final ArrayList<JSONObject> list = new ArrayList<>();
                            for (Map.Entry<String, JSONArray> entry2 : problemHm.entrySet()) {
                                final String key2 = entry2.getKey();
                                final JSONArray arr = entry2.getValue();
                                final JSONObject obj2 = new JSONObject();
                                obj2.put("name", key2);
                                obj2.put("count", arr.length());
                                list.add(obj2);
                            }
                            Collections.sort(list, new Comparator<JSONObject>() {
                                @Override
                                public int compare(JSONObject obj1, JSONObject obj2) {
                                    return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                                }
                            });
                            String str = "";
                            for (int i = 0; i < list.size(); i++) {
                                str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                            }
                            str = str.substring(0, str.length() - 1);
                            dataObj.setCount(String.valueOf(list.size()));
                            dataObj.setDetail(str);
                            currentList.add(dataObj);
                        }
                    }
                }
                Collections.sort(currentList, new Comparator<DanWeiJianCha>() {
                    @Override
                    public int compare(DanWeiJianCha obj1, DanWeiJianCha obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < currentList.size(); i++) {
                    currentList.get(i).setIndex(String.valueOf(i + 1));
                    currentList.get(i).setTongbi("0");
                }
                // if (0 < currentList.size()) {
                // datas.put("list_current", currentList);
                // datas.put("current_nothing", "");
                // } else {
                // datas.put("current_nothing", System.lineSeparator() + " 本时间段无相关问题");
                // }
                // if (0 < currentList.size()) {
                // cb = cb.bind("list_current", policy);
                // }
            }
            /////////////////////////////////////////////////////////////////
            // 环比
            /////////////////////////////////////////////////////////////////
            final ArrayList<DanWeiJianCha> huanbiList = new ArrayList<>();
            {
                final ArrayList<JSONObject> toDepartmentList = new ArrayList<>();
                {
                    final Department obj = new Department(this.connection);
                    final Message resultMsg = obj.getDepartment(null, null, null, Integer.valueOf(2), null, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        toDepartmentList.add(array.getJSONObject(i));
                    }
                }
                String newStartDatetime = null;
                {
                    final Date d1 = this.simpleDateFormat.parse(startDatetime);
                    final Date d2 = this.simpleDateFormat.parse(endDatetime);
                    int day = (int) ((d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000));
                    cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
                    cal.add(Calendar.DAY_OF_YEAR, day * -1);
                    newStartDatetime = this.simpleDateFormat.format(cal.getTime());
                }
                for (int j = 0; j < toDepartmentList.size(); j++) {
                    final String toDepartmentUuid = toDepartmentList.get(j).getString("uuid");
                    // 问题map
                    final HashMap<String, JSONArray> problemHm = new HashMap<>();
                    {
                        final Problem obj = new Problem(this.connection);
                        final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, Problem.ProblemType.CHECK, null, toDepartmentUuid, null, null, null, null, null, null, null, newStartDatetime,
                                startDatetime, null, null, null, null);
                        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                            return resultMsg;
                        }
                        final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                        final DanWeiJianCha dataObj = new DanWeiJianCha();
                        dataObj.setName(toDepartmentList.get(j).getString("name"));
                        if (0 >= array.length()) {
                            dataObj.setCount("0");
                            dataObj.setDetail("无");
                            huanbiList.add(dataObj);
                        } else {
                            for (int i = 0; i < array.length(); i++) {
                                final JSONObject problem = array.getJSONObject(i);
                                if (null == problemHm.get(problem.getString("to_department_name"))) {
                                    final JSONArray arr = new JSONArray();
                                    arr.put(problem);
                                    problemHm.put(problem.getString("to_department_name"), arr);
                                } else {
                                    final JSONArray arr = problemHm.get(problem.getString("to_department_name"));
                                    arr.put(problem);
                                    problemHm.put(problem.getString("to_department_name"), arr);
                                }
                            }
                            final ArrayList<JSONObject> list = new ArrayList<>();
                            for (Map.Entry<String, JSONArray> entry2 : problemHm.entrySet()) {
                                final String key2 = entry2.getKey();
                                final JSONArray arr = entry2.getValue();
                                final JSONObject obj2 = new JSONObject();
                                obj2.put("name", key2);
                                obj2.put("count", arr.length());
                                list.add(obj2);
                            }
                            Collections.sort(list, new Comparator<JSONObject>() {
                                @Override
                                public int compare(JSONObject obj1, JSONObject obj2) {
                                    return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                                }
                            });
                            String str = "";
                            for (int i = 0; i < list.size(); i++) {
                                str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                            }
                            str = str.substring(0, str.length() - 1);
                            dataObj.setCount(String.valueOf(list.size()));
                            dataObj.setDetail(str);
                            huanbiList.add(dataObj);
                        }
                    }
                }
                Collections.sort(huanbiList, new Comparator<DanWeiJianCha>() {
                    @Override
                    public int compare(DanWeiJianCha obj1, DanWeiJianCha obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                // for (int i = 0; i < huanbiList.size(); i++) {
                // huanbiList.get(i).setIndex(String.valueOf(i + 1));
                // }
                // if (0 < huanbiList.size()) {
                // datas.put("list_huanbi", huanbiList);
                // datas.put("huanbi_nothing", "");
                // } else {
                // datas.put("huanbi_nothing", System.lineSeparator() + " 本时间段无相关问题");
                // }
                // if (0 < huanbiList.size()) {
                // cb = cb.bind("list_huanbi", policy);
                // }
            }
            // new
            for (int i = 0; i < currentList.size(); i++) {
                final DanWeiJianCha currentObj = currentList.get(i);
                for (int j = 0; j < huanbiList.size(); j++) {
                    final DanWeiJianCha huanbiObj = huanbiList.get(j);
                    // 环比增长率=(本期数-上期数)/上期数×100%，但上期数不能为0
                    if (currentObj.getName().equalsIgnoreCase(huanbiObj.getName())) {
                        final String prev = huanbiObj.getCount();
                        final String curr = currentObj.getCount();
                        if (0 >= Integer.valueOf(prev).byteValue()) {
                            currentObj.setHuanbi("0");
                        } else {
                            String signStr = "";
                            if (0 < Integer.valueOf(prev).compareTo(Integer.valueOf(curr))) {
                                signStr = "下降";
                            } else {
                                signStr = "上升";
                            }
                            try {
                                currentObj
                                        .setHuanbi(signStr + (new BigDecimal(curr).subtract(new BigDecimal(prev))).abs().divide(new BigDecimal(prev), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).toString() + "%");
                            } catch (final Exception e) {
                                currentObj.setHuanbi("-1");
                            }
                        }
                        break;
                    }
                }
            }
            // new
            if (0 < currentList.size()) {
                datas.put("list_current", currentList);
                datas.put("current_nothing", "");
                {
                    String desc = "";
                    for (int i = 0; i < currentList.size(); i++) {
                        final DanWeiJianCha tObj = currentList.get(i);
                        String end = System.lineSeparator();
                        if ((i + 1) >= currentList.size()) {
                            end = "";
                        }
                        if (!tObj.getCount().equalsIgnoreCase("0")) {
                            desc += String.format("    %d、%s检查问题数量%s条，分别为：%s。%s", Integer.valueOf(i + 1), tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        } else {
                            desc += String.format("    %d、%s检查问题数量%s条。%s", Integer.valueOf(i + 1), tObj.getName(), tObj.getCount(), end);
                        }
                    }
                    datas.put("jiancha_desc", desc);
                }
            } else {
                datas.put("current_nothing", System.lineSeparator() + " 本时间段无相关问题");
                datas.put("jiancha_desc", "    本时间段无相关问题");
            }
            if (0 < currentList.size()) {
                cb = cb.bind("list_current", policy);
            }
            Configure config = cb.build();
            XWPFTemplate template = XWPFTemplate.compile(templateFile, config);
            final String fileUuid = StringKit.getUuidStr(true) + ".docx";
            final String targetFileName = Resource.REPORT_FILE_DIR_PATH + fileUuid;
            template.render(datas);
            int removeCount = 0;
            // 删除表格
            {
                if (0 >= currentList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(0 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= huanbiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(1 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
            }
            template.writeToFile(targetFileName);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("file_name", fileUuid);
            msg.setStatus(Message.Status.SUCCESS);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    // /**
    // * 获取各单位自查统计报告
    // *
    // * @param uuid uuid（允许为null）
    // * @param problemUuid 问题的uuid（允许为null）
    // * @param fromUuid 来源的uuid（允许为null）
    // * @param offset 查询的偏移（允许为null）
    // * @param rows 查询的行数（允许为null）
    // * @return 消息对象
    // */
    // public Message getDanWeiJianCha2(final String startDatetime, final String endDatetime) {
    // final Message msg = new Message();
    // try {
    // final String templateFileName = "各业务警种检查统计报告.docx";
    // final File templateFile = new File(Resource.REPORT_TEMPLATE_DIR_PATH + templateFileName);
    // final Calendar cal = Calendar.getInstance();
    // final Integer currentYear = Integer.valueOf(cal.get(Calendar.YEAR));
    // final Integer currentMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
    // final Integer currentDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    // cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
    // final Integer startYear = Integer.valueOf(cal.get(Calendar.YEAR));
    // final Integer startMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
    // final Integer startDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    // cal.setTimeInMillis(this.simpleDateFormat.parse(endDatetime).getTime());
    // final Integer endYear = Integer.valueOf(cal.get(Calendar.YEAR));
    // final Integer endMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
    // final Integer endDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    // final HashMap<String, Object> datas = new HashMap<>();
    // datas.put("current_year", currentYear);
    // datas.put("current_month", currentMonth);
    // datas.put("current_day", currentDay);
    // datas.put("start_year", startYear);
    // datas.put("start_month", startMonth);
    // datas.put("start_day", startDay);
    // datas.put("end_year", endYear);
    // datas.put("end_month", endMonth);
    // datas.put("end_day", endDay);
    // LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
    // ConfigureBuilder cb = Configure.builder();
    // /////////////////////////////////////////////////////////////////
    // // 当前
    // /////////////////////////////////////////////////////////////////
    // final ArrayList<DanWeiJianCha> currentList = new ArrayList<>();
    // {
    // final ArrayList<JSONObject> toDepartmentList = new ArrayList<>();
    // {
    // final Department obj = new Department(this.connection);
    // final Message resultMsg = obj.getDepartment(null, null, null, Integer.valueOf(2), null, null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
    // for (int i = 0; i < array.length(); i++) {
    // toDepartmentList.add(array.getJSONObject(i));
    // }
    // }
    // for (int j = 0; j < toDepartmentList.size(); j++) {
    // final String toDepartmentUuid = toDepartmentList.get(j).getString("uuid");
    // // 问题map
    // final HashMap<String, JSONArray> problemHm = new HashMap<>();
    // {
    // final Problem obj = new Problem(this.connection);
    // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, Problem.ProblemType.CHECK, null, toDepartmentUuid, null, null, null, null, null, null, null, startDatetime, endDatetime,
    // null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
    // final DanWeiJianCha dataObj = new DanWeiJianCha();
    // dataObj.setName(toDepartmentList.get(j).getString("name"));
    // if (0 >= array.length()) {
    // dataObj.setCount("0");
    // dataObj.setDetail("无");
    // currentList.add(dataObj);
    // } else {
    // for (int i = 0; i < array.length(); i++) {
    // final JSONObject problem = array.getJSONObject(i);
    // if (null == problemHm.get(problem.getString("problem_department_type_name"))) {
    // final JSONArray arr = new JSONArray();
    // arr.put(problem);
    // problemHm.put(problem.getString("problem_department_type_name"), arr);
    // } else {
    // final JSONArray arr = problemHm.get(problem.getString("problem_department_type_name"));
    // arr.put(problem);
    // problemHm.put(problem.getString("problem_department_type_name"), arr);
    // }
    // }
    // final ArrayList<JSONObject> list = new ArrayList<>();
    // for (Map.Entry<String, JSONArray> entry2 : problemHm.entrySet()) {
    // final String key2 = entry2.getKey();
    // final JSONArray arr = entry2.getValue();
    // final JSONObject obj2 = new JSONObject();
    // obj2.put("name", key2);
    // obj2.put("count", arr.length());
    // list.add(obj2);
    // }
    // Collections.sort(list, new Comparator<JSONObject>() {
    // @Override
    // public int compare(JSONObject obj1, JSONObject obj2) {
    // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
    // }
    // });
    // String str = "";
    // for (int i = 0; i < list.size(); i++) {
    // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
    // }
    // str = str.substring(0, str.length() - 1);
    // dataObj.setCount(String.valueOf(list.size()));
    // dataObj.setDetail(str);
    // currentList.add(dataObj);
    // }
    // }
    // }
    // Collections.sort(currentList, new Comparator<DanWeiJianCha>() {
    // @Override
    // public int compare(DanWeiJianCha obj1, DanWeiJianCha obj2) {
    // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
    // }
    // });
    // for (int i = 0; i < currentList.size(); i++) {
    // currentList.get(i).setIndex(String.valueOf(i + 1));
    // }
    // if (0 < currentList.size()) {
    // datas.put("list_current", currentList);
    // datas.put("current_nothing", "");
    // } else {
    // datas.put("current_nothing", System.lineSeparator() + " 本时间段无相关问题");
    // }
    // if (0 < currentList.size()) {
    // cb = cb.bind("list_current", policy);
    // }
    // }
    // /////////////////////////////////////////////////////////////////
    // // 环比
    // /////////////////////////////////////////////////////////////////
    // final ArrayList<DanWeiJianCha> huanbiList = new ArrayList<>();
    // {
    // final ArrayList<JSONObject> toDepartmentList = new ArrayList<>();
    // {
    // final Department obj = new Department(this.connection);
    // final Message resultMsg = obj.getDepartment(null, null, null, Integer.valueOf(2), null, null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
    // for (int i = 0; i < array.length(); i++) {
    // toDepartmentList.add(array.getJSONObject(i));
    // }
    // }
    // String newStartDatetime = null;
    // {
    // final Date d1 = this.simpleDateFormat.parse(startDatetime);
    // final Date d2 = this.simpleDateFormat.parse(endDatetime);
    // int day = (int) ((d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000));
    // cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
    // cal.add(Calendar.DAY_OF_YEAR, day * -1);
    // newStartDatetime = this.simpleDateFormat.format(cal.getTime());
    // }
    // for (int j = 0; j < toDepartmentList.size(); j++) {
    // final String toDepartmentUuid = toDepartmentList.get(j).getString("uuid");
    // // 问题map
    // final HashMap<String, JSONArray> problemHm = new HashMap<>();
    // {
    // final Problem obj = new Problem(this.connection);
    // final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, Problem.ProblemType.CHECK, null, toDepartmentUuid, null, null, null, null, null, null, null, newStartDatetime,
    // startDatetime, null, null, null, null);
    // if (Message.Status.SUCCESS != resultMsg.getStatus()) {
    // return resultMsg;
    // }
    // final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
    // final DanWeiJianCha dataObj = new DanWeiJianCha();
    // dataObj.setName(toDepartmentList.get(j).getString("name"));
    // if (0 >= array.length()) {
    // dataObj.setCount("0");
    // dataObj.setDetail("无");
    // huanbiList.add(dataObj);
    // } else {
    // for (int i = 0; i < array.length(); i++) {
    // final JSONObject problem = array.getJSONObject(i);
    // if (null == problemHm.get(problem.getString("problem_department_type_name"))) {
    // final JSONArray arr = new JSONArray();
    // arr.put(problem);
    // problemHm.put(problem.getString("problem_department_type_name"), arr);
    // } else {
    // final JSONArray arr = problemHm.get(problem.getString("problem_department_type_name"));
    // arr.put(problem);
    // problemHm.put(problem.getString("problem_department_type_name"), arr);
    // }
    // }
    // final ArrayList<JSONObject> list = new ArrayList<>();
    // for (Map.Entry<String, JSONArray> entry2 : problemHm.entrySet()) {
    // final String key2 = entry2.getKey();
    // final JSONArray arr = entry2.getValue();
    // final JSONObject obj2 = new JSONObject();
    // obj2.put("name", key2);
    // obj2.put("count", arr.length());
    // list.add(obj2);
    // }
    // Collections.sort(list, new Comparator<JSONObject>() {
    // @Override
    // public int compare(JSONObject obj1, JSONObject obj2) {
    // return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
    // }
    // });
    // String str = "";
    // for (int i = 0; i < list.size(); i++) {
    // str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
    // }
    // str = str.substring(0, str.length() - 1);
    // dataObj.setCount(String.valueOf(list.size()));
    // dataObj.setDetail(str);
    // huanbiList.add(dataObj);
    // }
    // }
    // }
    // Collections.sort(huanbiList, new Comparator<DanWeiJianCha>() {
    // @Override
    // public int compare(DanWeiJianCha obj1, DanWeiJianCha obj2) {
    // return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
    // }
    // });
    // for (int i = 0; i < huanbiList.size(); i++) {
    // huanbiList.get(i).setIndex(String.valueOf(i + 1));
    // }
    // if (0 < huanbiList.size()) {
    // datas.put("list_huanbi", huanbiList);
    // datas.put("huanbi_nothing", "");
    // } else {
    // datas.put("huanbi_nothing", System.lineSeparator() + " 本时间段无相关问题");
    // }
    // if (0 < huanbiList.size()) {
    // cb = cb.bind("list_huanbi", policy);
    // }
    // }
    // Configure config = cb.build();
    // XWPFTemplate template = XWPFTemplate.compile(templateFile, config);
    // final String fileUuid = StringKit.getUuidStr(true) + ".docx";
    // final String targetFileName = Resource.REPORT_FILE_DIR_PATH + fileUuid;
    // template.render(datas);
    // int removeCount = 0;
    // // 删除表格
    // {
    // if (0 >= currentList.size()) {
    // final XWPFTable a = template.getXWPFDocument().getTables().get(0 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
    // removeCount++;
    // }
    // }
    // if (0 >= huanbiList.size()) {
    // final XWPFTable a = template.getXWPFDocument().getTables().get(1 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
    // removeCount++;
    // }
    // }
    // }
    // template.writeToFile(targetFileName);
    // final JSONObject resultObj = new JSONObject();
    // resultObj.put("file_name", fileUuid);
    // msg.setStatus(Message.Status.SUCCESS);
    // msg.setContent(resultObj);
    // return msg;
    // } catch (final Exception e) {
    // msg.setStatus(Message.Status.EXCEPTION);
    // msg.setContent(StringKit.getExceptionStackTrace(e));
    // return msg;
    // }
    // }

    /**
     * 获取问题高发人员统计报告
     * 
     * @param uuid uuid（允许为null）
     * @param problemUuid 问题的uuid（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getGaoFaRenYuan(final String startDatetime, final String endDatetime) {
        final Message msg = new Message();
        try {
            final String templateFileName = "问题高发人员统计报告.docx";
            final File templateFile = new File(Resource.REPORT_TEMPLATE_DIR_PATH + templateFileName);
            final Calendar cal = Calendar.getInstance();
            final Integer currentYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer currentMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer currentDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
            final Integer startYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer startMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer startDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(endDatetime).getTime());
            final Integer endYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer endMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer endDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            final HashMap<String, Object> datas = new HashMap<>();
            datas.put("current_year", currentYear);
            datas.put("current_month", currentMonth);
            datas.put("current_day", currentDay);
            datas.put("start_year", startYear);
            datas.put("start_month", startMonth);
            datas.put("start_day", startDay);
            datas.put("end_year", endYear);
            datas.put("end_month", endMonth);
            datas.put("end_day", endDay);
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            ConfigureBuilder cb = Configure.builder();
            /////////////////////////////////////////////////////////////////
            // 民警列表
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaRenYuan> minjingList = new ArrayList<>();
            /////////////////////////////////////////////////////////////////
            // 辅警列表
            /////////////////////////////////////////////////////////////////
            final ArrayList<GaoFaRenYuan> fujingList = new ArrayList<>();
            /////////////////////////////////////////////////////////////////
            // 民警map
            /////////////////////////////////////////////////////////////////
            final HashMap<String, JSONArray> minjingHm = new HashMap<>();
            /////////////////////////////////////////////////////////////////
            // 辅警map
            /////////////////////////////////////////////////////////////////
            final HashMap<String, JSONArray> fujingHm = new HashMap<>();
            {
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "true", null, startDatetime, endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (problem.has("responsible_uuid")) {
                            final String uuid = problem.getJSONArray("responsible_uuid").getString(0);
                            final String name = problem.getJSONArray("responsible_account_name").getString(0);
                            if (name.contains("-")) {
                                if (null == fujingHm.get(uuid)) {
                                    final JSONArray arr = new JSONArray();
                                    arr.put(problem);
                                    fujingHm.put(uuid, arr);
                                } else {
                                    final JSONArray arr = fujingHm.get(uuid);
                                    arr.put(problem);
                                    fujingHm.put(uuid, arr);
                                }
                            } else {
                                if (null == minjingHm.get(uuid)) {
                                    final JSONArray arr = new JSONArray();
                                    arr.put(problem);
                                    minjingHm.put(uuid, arr);
                                } else {
                                    final JSONArray arr = minjingHm.get(uuid);
                                    arr.put(problem);
                                    minjingHm.put(uuid, arr);
                                }
                            }
                        }
                    }
                }
                // minjing
                {
                    for (Map.Entry<String, JSONArray> entry : minjingHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaRenYuan dataObj = new GaoFaRenYuan();
                        dataObj.setName(String.format("%s（%s）", arr.getJSONObject(0).getJSONArray("responsible_real_name").getString(0), arr.getJSONObject(0).getJSONArray("responsible_account_name").getString(0)));
                        dataObj.setDepartment(arr.getJSONObject(0).getString("to_department_name"));
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> problemTypeHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                problemTypeHm.put(problem.getString("problem_department_type_name"), Integer.valueOf(1));
                            } else {
                                problemTypeHm.put(problem.getString("problem_department_type_name"), Integer.valueOf(problemTypeHm.get(problem.getString("problem_department_type_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : problemTypeHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        minjingList.add(dataObj);
                    }
                    Collections.sort(minjingList, new Comparator<GaoFaRenYuan>() {
                        @Override
                        public int compare(GaoFaRenYuan obj1, GaoFaRenYuan obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < minjingList.size(); i++) {
                        minjingList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < minjingList.size()) {
                        datas.put("list_minjing", minjingList);
                        datas.put("minjing_nothing", "");
                    } else {
                        datas.put("minjing_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < minjingList.size()) {
                        cb = cb.bind("list_minjing", policy);
                    }
                }
                // fujing
                {
                    for (Map.Entry<String, JSONArray> entry : fujingHm.entrySet()) {
                        final String key = entry.getKey();
                        final JSONArray arr = entry.getValue();
                        final GaoFaRenYuan dataObj = new GaoFaRenYuan();
                        dataObj.setName(String.format("%s（%s）", arr.getJSONObject(0).getJSONArray("responsible_real_name").getString(0), arr.getJSONObject(0).getJSONArray("responsible_account_name").getString(0)));
                        dataObj.setDepartment(arr.getJSONObject(0).getString("to_department_name"));
                        dataObj.setCount(String.valueOf(arr.length()));
                        final HashMap<String, Integer> problemTypeHm = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject problem = arr.getJSONObject(i);
                            if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                                problemTypeHm.put(problem.getString("problem_department_type_name"), Integer.valueOf(1));
                            } else {
                                problemTypeHm.put(problem.getString("problem_department_type_name"), Integer.valueOf(problemTypeHm.get(problem.getString("problem_department_type_name")).intValue() + 1));
                            }
                        }
                        final ArrayList<JSONObject> list = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry2 : problemTypeHm.entrySet()) {
                            final String key2 = entry2.getKey();
                            final Integer count = entry2.getValue();
                            final JSONObject obj = new JSONObject();
                            obj.put("name", key2);
                            obj.put("count", count);
                            list.add(obj);
                        }
                        Collections.sort(list, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject obj1, JSONObject obj2) {
                                return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                            }
                        });
                        String str = "";
                        for (int i = 0; i < list.size(); i++) {
                            str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                        }
                        str = str.substring(0, str.length() - 1);
                        dataObj.setDetail(str);
                        fujingList.add(dataObj);
                    }
                    Collections.sort(fujingList, new Comparator<GaoFaRenYuan>() {
                        @Override
                        public int compare(GaoFaRenYuan obj1, GaoFaRenYuan obj2) {
                            return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                        }
                    });
                    for (int i = 0; i < fujingList.size(); i++) {
                        fujingList.get(i).setIndex(String.valueOf(i + 1));
                    }
                    if (0 < fujingList.size()) {
                        datas.put("list_fujing", fujingList);
                        datas.put("fujing_nothing", "");
                    } else {
                        datas.put("fujing_nothing", System.lineSeparator() + "    本时间段无相关问题");
                    }
                    if (0 < fujingList.size()) {
                        cb = cb.bind("list_fujing", policy);
                    }
                }
            }
            // new
            if (0 < minjingList.size()) {
                String desc = "";
                for (int i = 0; i < minjingList.size(); i++) {
                    final GaoFaRenYuan tObj = minjingList.get(i);
                    String end = System.lineSeparator();
                    if ((i + 1) >= minjingList.size()) {
                        end = "";
                    }
                    desc += String.format("    %d、民警%s，所在单位：%s，问题数量%s条，分别为：%s。%s", Integer.valueOf(i + 1), tObj.getName(), tObj.getDepartment(), tObj.getCount(), tObj.getDetail(), end);
                }
                datas.put("minjing_desc", desc);
            } else {
                datas.put("minjing_desc", "    本时间段无相关问题");
            }
            if (0 < fujingList.size()) {
                String desc = "";
                for (int i = 0; i < fujingList.size(); i++) {
                    final GaoFaRenYuan tObj = fujingList.get(i);
                    String end = System.lineSeparator();
                    if ((i + 1) >= fujingList.size()) {
                        end = "";
                    }
                    desc += String.format("    %d、辅警%s，所在单位：%s，问题数量%s条，分别为：%s。%s", Integer.valueOf(i + 1), tObj.getName(), tObj.getDepartment(), tObj.getCount(), tObj.getDetail(), end);
                }
                datas.put("fujing_desc", desc);
            } else {
                datas.put("fujing_desc", "    本时间段无相关问题");
            }
            Configure config = cb.build();
            XWPFTemplate template = XWPFTemplate.compile(templateFile, config);
            final String fileUuid = StringKit.getUuidStr(true) + ".docx";
            final String targetFileName = Resource.REPORT_FILE_DIR_PATH + fileUuid;
            template.render(datas);
            int removeCount = 0;
            // 删除表格
            {
                if (0 >= minjingList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(0 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= fujingList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(1 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
            }
            template.writeToFile(targetFileName);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("file_name", fileUuid);
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
     * 获取五位一体大监督体系工作报告
     * 
     * @param uuid uuid（允许为null）
     * @param problemUuid 问题的uuid（允许为null）
     * @param fromUuid 来源的uuid（允许为null）
     * @param offset 查询的偏移（允许为null）
     * @param rows 查询的行数（允许为null）
     * @return 消息对象
     */
    public Message getPaiChuSuo(final String departmentUuid, final String departmentName, final String startDatetime, final String endDatetime) {
        final Message msg = new Message();
        try {
            final String templateFileName = "派出所工作报告.docx";
            final File templateFile = new File(Resource.REPORT_TEMPLATE_DIR_PATH + templateFileName);
            final Calendar cal = Calendar.getInstance();
            final Integer currentYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer currentMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer currentDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(startDatetime).getTime());
            final Integer startYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer startMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer startDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            cal.setTimeInMillis(this.simpleDateFormat.parse(endDatetime).getTime());
            final Integer endYear = Integer.valueOf(cal.get(Calendar.YEAR));
            final Integer endMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
            final Integer endDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            final HashMap<String, Object> datas = new HashMap<>();
            datas.put("dep_name", departmentName);
            datas.put("current_year", currentYear);
            datas.put("current_month", currentMonth);
            datas.put("current_day", currentDay);
            datas.put("start_year", startYear);
            datas.put("start_month", startMonth);
            datas.put("start_day", startDay);
            datas.put("end_year", endYear);
            datas.put("end_month", endMonth);
            datas.put("end_day", endDay);
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            ConfigureBuilder cb = Configure.builder();
            /////////////////////////////////////////////////////////////////
            // 政治监督
            /////////////////////////////////////////////////////////////////
            final ArrayList<WuWeiYiTi> zhengzhichuList = new ArrayList<>();
            {
                final ArrayList<String> typeUuidList = new ArrayList<>();
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null, ProblemDepartmentType.SystemType.ZHENG_ZHI); // !!!!!!!!!!!!!!!!!!!
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                    }
                }
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, typeUuidList.toArray(new String[typeUuidList.size()]), null, null, null, null, null, null, departmentUuid, null, null, null, null, null, null, null, startDatetime,
                            endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final WuWeiYiTi dataObj = new WuWeiYiTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    zhengzhichuList.add(dataObj);
                    index++;
                }
                Collections.sort(zhengzhichuList, new Comparator<WuWeiYiTi>() {
                    @Override
                    public int compare(WuWeiYiTi obj1, WuWeiYiTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < zhengzhichuList.size(); i++) {
                    zhengzhichuList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < zhengzhichuList.size()) {
                    datas.put("list_zhengzhichu", zhengzhichuList);
                    datas.put("zhengzhi_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < zhengzhichuList.size(); i++) {
                            final WuWeiYiTi tObj = zhengzhichuList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= zhengzhichuList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("zhengzhi_desc", desc);
                    }
                } else {
                    datas.put("zhengzhi_desc", "    本时间段无相关问题"); // new
                    datas.put("zhengzhi_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < zhengzhichuList.size()) {
                    cb = cb.bind("list_zhengzhichu", policy);
                }
            }
            /////////////////////////////////////////////////////////////////
            // 警务监督
            /////////////////////////////////////////////////////////////////
            final ArrayList<WuWeiYiTi> jingwuList = new ArrayList<>();
            {
                final ArrayList<String> typeUuidList = new ArrayList<>();
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null, ProblemDepartmentType.SystemType.JING_WU); // !!!!!!!!!!!!!!!!!!!
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                    }
                }
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, typeUuidList.toArray(new String[typeUuidList.size()]), null, null, null, null, null, null, departmentUuid, null, null, null, null, null, null, null, startDatetime,
                            endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final WuWeiYiTi dataObj = new WuWeiYiTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    jingwuList.add(dataObj);
                    index++;
                }
                Collections.sort(jingwuList, new Comparator<WuWeiYiTi>() {
                    @Override
                    public int compare(WuWeiYiTi obj1, WuWeiYiTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < jingwuList.size(); i++) {
                    jingwuList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < jingwuList.size()) {
                    datas.put("list_jingwu", jingwuList);
                    datas.put("jingwu_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < jingwuList.size(); i++) {
                            final WuWeiYiTi tObj = jingwuList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= jingwuList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("jingwu_desc", desc);
                    }
                } else {
                    datas.put("jingwu_desc", "    本时间段无相关问题"); // new
                    datas.put("jingwu_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < jingwuList.size()) {
                    cb = cb.bind("list_jingwu", policy);
                }
            }
            /////////////////////////////////////////////////////////////////
            // 执法监督
            /////////////////////////////////////////////////////////////////
            final ArrayList<WuWeiYiTi> zhifaList = new ArrayList<>();
            {
                final ArrayList<String> typeUuidList = new ArrayList<>();
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null, ProblemDepartmentType.SystemType.ZHI_FA); // !!!!!!!!!!!!!!!!!!!
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                    }
                }
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, typeUuidList.toArray(new String[typeUuidList.size()]), null, null, null, null, null, null, departmentUuid, null, null, null, null, null, null, null, startDatetime,
                            endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final WuWeiYiTi dataObj = new WuWeiYiTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    zhifaList.add(dataObj);
                    index++;
                }
                Collections.sort(zhifaList, new Comparator<WuWeiYiTi>() {
                    @Override
                    public int compare(WuWeiYiTi obj1, WuWeiYiTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < zhifaList.size(); i++) {
                    zhifaList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < zhifaList.size()) {
                    datas.put("list_zhifa", zhifaList);
                    datas.put("zhifa_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < zhifaList.size(); i++) {
                            final WuWeiYiTi tObj = zhifaList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= zhifaList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("zhifa_desc", desc);
                    }
                } else {
                    datas.put("zhifa_desc", "    本时间段无相关问题"); // new
                    datas.put("zhifa_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < zhifaList.size()) {
                    cb = cb.bind("list_zhifa", policy);
                }
            }
            /////////////////////////////////////////////////////////////////
            // 纪检监督
            /////////////////////////////////////////////////////////////////
            final ArrayList<WuWeiYiTi> jijianList = new ArrayList<>();
            {
                final ArrayList<String> typeUuidList = new ArrayList<>();
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null, ProblemDepartmentType.SystemType.JI_JIAN); // !!!!!!!!!!!!!!!!!!!
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                    }
                }
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, typeUuidList.toArray(new String[typeUuidList.size()]), null, null, null, null, null, null, departmentUuid, null, null, null, null, null, null, null, startDatetime,
                            endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final WuWeiYiTi dataObj = new WuWeiYiTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    jijianList.add(dataObj);
                    index++;
                }
                Collections.sort(jijianList, new Comparator<WuWeiYiTi>() {
                    @Override
                    public int compare(WuWeiYiTi obj1, WuWeiYiTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < jijianList.size(); i++) {
                    jijianList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < jijianList.size()) {
                    datas.put("list_jijian", jijianList);
                    datas.put("jijian_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < jijianList.size(); i++) {
                            final WuWeiYiTi tObj = jijianList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= jijianList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("jijian_desc", desc);
                    }
                } else {
                    datas.put("jijian_desc", "    本时间段无相关问题"); // new
                    datas.put("jijian_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < jijianList.size()) {
                    cb = cb.bind("list_jijian", policy);
                }
            }
            /////////////////////////////////////////////////////////////////
            // 民意监督
            /////////////////////////////////////////////////////////////////
            final ArrayList<WuWeiYiTi> minyiList = new ArrayList<>();
            {
                final ArrayList<String> typeUuidList = new ArrayList<>();
                {
                    final ProblemDepartmentType obj = new ProblemDepartmentType(this.connection);
                    final Message resultMsg = obj.getProblemDepartmentType(null, null, null, ProblemDepartmentType.SystemType.MIN_YI); // !!!!!!!!!!!!!!!!!!!
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        typeUuidList.add(array.getJSONObject(i).getString("uuid"));
                    }
                }
                // 问题类型map
                final HashMap<String, JSONArray> problemTypeHm = new HashMap<>();
                {
                    final Problem obj = new Problem(this.connection);
                    final Message resultMsg = obj.getProblemExact(null, null, null, null, null, null, typeUuidList.toArray(new String[typeUuidList.size()]), null, null, null, null, null, null, departmentUuid, null, null, null, null, null, null, null, startDatetime,
                            endDatetime, null, null, null, null);
                    if (Message.Status.SUCCESS != resultMsg.getStatus()) {
                        return resultMsg;
                    }
                    final JSONArray array = ((JSONObject) resultMsg.getContent()).getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject problem = array.getJSONObject(i);
                        if (null == problemTypeHm.get(problem.getString("problem_department_type_name"))) {
                            final JSONArray arr = new JSONArray();
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        } else {
                            final JSONArray arr = problemTypeHm.get(problem.getString("problem_department_type_name"));
                            arr.put(problem);
                            problemTypeHm.put(problem.getString("problem_department_type_name"), arr);
                        }
                    }
                }
                int index = 1;
                for (Map.Entry<String, JSONArray> entry : problemTypeHm.entrySet()) {
                    final String key = entry.getKey();
                    final JSONArray arr = entry.getValue();
                    final WuWeiYiTi dataObj = new WuWeiYiTi();
                    dataObj.setIndex(String.valueOf(index));
                    dataObj.setName(key);
                    dataObj.setCount(String.valueOf(arr.length()));
                    final HashMap<String, Integer> toDepartmentHm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject problem = arr.getJSONObject(i);
                        if (null == toDepartmentHm.get(problem.getString("to_department_name"))) {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(1));
                        } else {
                            toDepartmentHm.put(problem.getString("to_department_name"), Integer.valueOf(toDepartmentHm.get(problem.getString("to_department_name")).intValue() + 1));
                        }
                    }
                    final ArrayList<JSONObject> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry2 : toDepartmentHm.entrySet()) {
                        final String key2 = entry2.getKey();
                        final Integer count = entry2.getValue();
                        final JSONObject obj = new JSONObject();
                        obj.put("name", key2);
                        obj.put("count", count);
                        list.add(obj);
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject obj1, JSONObject obj2) {
                            return Integer.valueOf(obj2.getInt("count")).compareTo(Integer.valueOf(obj1.getInt("count")));
                        }
                    });
                    String str = "";
                    for (int i = 0; i < list.size(); i++) {
                        str += list.get(i).getString("name") + list.get(i).getInt("count") + "件，";
                    }
                    str = str.substring(0, str.length() - 1);
                    dataObj.setDetail(str);
                    minyiList.add(dataObj);
                    index++;
                }
                Collections.sort(minyiList, new Comparator<WuWeiYiTi>() {
                    @Override
                    public int compare(WuWeiYiTi obj1, WuWeiYiTi obj2) {
                        return Integer.valueOf(obj2.getCount()).compareTo(Integer.valueOf(obj1.getCount()));
                    }
                });
                for (int i = 0; i < minyiList.size(); i++) {
                    minyiList.get(i).setIndex(String.valueOf(i + 1));
                }
                if (0 < minyiList.size()) {
                    datas.put("list_minyi", minyiList);
                    datas.put("minyi_nothing", "");
                    // new
                    {
                        String desc = "";
                        for (int i = 0; i < minyiList.size(); i++) {
                            final WuWeiYiTi tObj = minyiList.get(i);
                            String titleIndex = "";
                            try {
                                titleIndex = Report.ChineseIndex[i];
                            } catch (final IndexOutOfBoundsException e) {
                                titleIndex = String.valueOf(i);
                            }
                            String end = System.lineSeparator();
                            if ((i + 1) >= minyiList.size()) {
                                end = "";
                            }
                            desc += String.format("    （%s）%s%s条问题，责任单位分别为：%s。%s", titleIndex, tObj.getName(), tObj.getCount(), tObj.getDetail(), end);
                        }
                        datas.put("minyi_desc", desc);
                    }
                } else {
                    datas.put("minyi_desc", "    本时间段无相关问题"); // new
                    datas.put("minyi_nothing", System.lineSeparator() + "    本时间段无相关问题");
                }
                if (0 < minyiList.size()) {
                    cb = cb.bind("list_minyi", policy);
                }
            }
            Configure config = cb.build();
            XWPFTemplate template = XWPFTemplate.compile(templateFile, config);
            final String fileUuid = StringKit.getUuidStr(true) + ".docx";
            final String targetFileName = Resource.REPORT_FILE_DIR_PATH + fileUuid;
            template.render(datas);
            int removeCount = 0;
            // 删除表格
            {
                if (0 >= zhengzhichuList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(0 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= jingwuList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(1 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= zhifaList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(2 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= jijianList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(3 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
                if (0 >= minyiList.size()) {
                    final XWPFTable a = template.getXWPFDocument().getTables().get(4 - removeCount); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (template.getXWPFDocument().removeBodyElement(template.getXWPFDocument().getPosOfTable(a))) {
                        removeCount++;
                    }
                }
            }
            template.writeToFile(targetFileName);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("file_name", fileUuid);
            msg.setStatus(Message.Status.SUCCESS);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }
}