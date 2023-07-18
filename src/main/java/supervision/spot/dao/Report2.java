package supervision.spot.dao;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.encrypt.Base64;
import com.palestink.utils.encrypt.Md5;
import com.palestink.utils.io.IoKit;
import com.palestink.utils.string.StringKit;
import env.config.Resource;

/**
 * 报告
 */
public final class Report2 {
    @SuppressWarnings("unused")
    private Connection connection;
    private final HashMap<String, String> reportTemplateFileNameMap;

    public Report2(final Connection connection) throws Exception {
        this.connection = connection;
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
     * 获取模板文件名称
     * 
     * @return 消息对象
     */
    public final Message getTemplateFileName() {
        final Message msg = new Message();
        try {
            final JSONArray array = new JSONArray();
            this.reportTemplateFileNameMap.entrySet().stream().forEach((entry) -> {
                final String fileName = entry.getValue();
                final JSONObject obj = new JSONObject();
                obj.put("key", entry.getKey());
                obj.put("short_name", fileName.substring(0, fileName.indexOf(".")));
                obj.put("full_name", fileName);
                array.put(obj);
            });
            msg.setStatus(Message.Status.SUCCESS);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("array", array);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }

    /**
     * 获取模板文件
     * 
     * @param key key
     * @return 消息对象
     */
    public final Message getTemplateFile(final String key) {
        final Message msg = new Message();
        try {
            final String fileName = this.reportTemplateFileNameMap.get(key);
            // 资源上判断文件是否存在
            {
                if (null == fileName) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("FILE_NOT_EXIST");
                    msg.setAttach("文件不存在");
                    return msg;
                }
            }
            // 路径上判断文件是否存在
            {
                final File file = new File(Resource.REPORT_TEMPLATE_DIR_PATH + fileName);
                if (!file.exists()) {
                    msg.setStatus(Message.Status.ERROR);
                    msg.setContent("FILE_NOT_EXIST");
                    msg.setAttach("文件不存在");
                    return msg;
                }
            }
            msg.setStatus(Message.Status.SUCCESS);
            final byte[] data = IoKit.simpleBytesReadFile(Resource.REPORT_TEMPLATE_DIR_PATH + fileName);
            final String content = Base64.encode(data);
            final JSONObject resultObj = new JSONObject();
            resultObj.put("file_name", fileName);
            resultObj.put("data", content);
            msg.setContent(resultObj);
            return msg;
        } catch (final Exception e) {
            msg.setStatus(Message.Status.EXCEPTION);
            msg.setContent(StringKit.getExceptionStackTrace(e));
            return msg;
        }
    }
}