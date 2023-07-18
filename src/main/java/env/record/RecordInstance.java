package env.record;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;

import org.json.JSONObject;

import com.palestink.server.sdk.Framework;
import com.palestink.server.sdk.module.AbstractRecord;
import com.palestink.server.sdk.module.impl.Record;
import com.palestink.utils.string.StringKit;

public final class RecordInstance extends AbstractRecord {
    private static volatile RecordInstance INSTANCE = null;

    /**
     * 单例模式之私有构造函数
     */
    private RecordInstance() {
    }

    /**
     * 单例模式（线程安全）
     */
    public static final RecordInstance getInstance() {
        if (null == INSTANCE) {
            synchronized (RecordInstance.class) {
                if (null == INSTANCE) {
                    INSTANCE = new RecordInstance();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void httpRequest(HttpServletRequest httpRequest) {
        final String requestUuid = StringKit.getUuidStr(true);
        httpRequest.setAttribute("request_uuid", requestUuid);
        final JSONObject jo = Record.getHttpRequestInfo(httpRequest);
        jo.put("record_type", "request");
        Framework.Log.debug(String.format("[%s] [Request] %s", StringKit.getCurrentFormatDateTime("yyyy-MM-dd HH:mm:ss"), jo.toString()));
    }

    @Override
    public void httpResponse(HttpServletRequest httpRequest, String responseData) {
        final JSONObject jo = new JSONObject();
        final String requestUuid = (String) httpRequest.getAttribute("request_uuid");
        if (null != requestUuid) {
            jo.put("response_uuid", requestUuid);
            httpRequest.removeAttribute("request_uuid");
        } else {
            jo.put("response_uuid", "null");
        }
        jo.put("record_type", "response");
        jo.put("response_data", responseData);
        Framework.Log.debug(String.format("[%s] [Response] %s", StringKit.getCurrentFormatDateTime("yyyy-MM-dd HH:mm:ss"), jo.toString()));
    }

    @Override
    public void websocketRequest(Session session, String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void websocketResponse(Session session, String message) {
        // TODO Auto-generated method stub
        
    }
}