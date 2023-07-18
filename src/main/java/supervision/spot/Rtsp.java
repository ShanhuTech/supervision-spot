package supervision.spot;

import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palestink.server.sdk.module.AbstractModule;
import com.palestink.server.sdk.module.annotation.Frequency;
import com.palestink.server.sdk.module.annotation.Method;
import com.palestink.server.sdk.module.annotation.Module;
import com.palestink.server.sdk.module.annotation.Parameter;
import com.palestink.server.sdk.module.annotation.ReturnResult;
import com.palestink.server.sdk.module.annotation.Returns;
import com.palestink.server.sdk.msg.Message;
import com.palestink.utils.net.RtspKit;

import supervision.spot.client.AccessSecurity;

@Module(description = "Rtsp")
public final class Rtsp extends AbstractModule {
    @SuppressWarnings("unused")
    private HttpServlet httpServlet;
    @SuppressWarnings("unused")
    private HttpServletRequest httpServletRequest;
    @SuppressWarnings("unused")
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Object> parameter;

    public Rtsp() {
    }

    public Rtsp(final HttpServlet httpServlet, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final HashMap<String, Object> parameter) throws Exception {
        super(httpServlet, httpServletRequest, httpServletResponse, parameter);
        this.httpServlet = httpServlet;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.parameter = parameter;
    }

    @Method(description = "检查", anonymousAccess = false, frequencys = { @Frequency(source = Frequency.Source.ACCOUNT, count = 1, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "rtsp_urls", text = "rtsp地址集合", type = Parameter.Type.STRING, allowNull = false, format = "^rtsp:\\/\\/[0-9a-zA-Z]*:[0-9a-zA-Z]*@((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}:[0-9]*\\/[a-zA-Z0-9]*(;rtsp:\\/\\/[0-9a-zA-Z]*:[0-9a-zA-Z]*@((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}:[0-9]*\\/[a-zA-Z0-9]*)*$", formatPrompt = "一个或多个rtsp地址集合", remark = "地址之间以分号分割，且集合中地址数量不能超过100个。") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "array_id", name = "results", type = "array", isNecessary = true, description = "结果"),
                    @ReturnResult(parentId = "array_id", id = "", name = "status", type = "string[1,10]", isNecessary = true, description = "状态：success和error"),
                    @ReturnResult(parentId = "array_id", id = "", name = "url", type = "string[1,]", isNecessary = true, description = "rtsp地址"),
                    @ReturnResult(parentId = "array_id", id = "", name = "result", type = "string[1,]", isNecessary = true, description = "结果。如果状态为success结果为验证的延迟时间（字符串解析数字越小延迟越短），如果状态为非success结果为错误代码。错误代码意义如下：INVALID_RTSP_URL: 非法rtsp地址;CONNECT_TIMEOUT: 连接超时;HOST_PORT_CLOSED: 主机端口关闭;GET_STATUS_FAIL: 获取状态失败;GET_AUTHORIZATION_FAIL: 获取认证失败;INVALID_STATUS_CODE: 非法状态码;PARSE_AUTH_FAIL: 解析身份认证失败;AUTH_FAIL: 身份认证失败OTHER_ERROR: 其他错误") }))
    public final Message check() {
        final Message msg = new Message();
        final String rtspUrls = (String) this.parameter.get("rtsp_urls");
        final String[] rtspUrlArray = rtspUrls.split(";");
        if (100 < rtspUrlArray.length) {
            msg.setStatus(Message.Status.ERROR);
            msg.setContent("RTSP_URLS_SIZE_TOO_MORE");
            msg.setAttach("rtsp地址集合数量太大");
            return msg;
        }
        final JSONArray array = new JSONArray();
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < rtspUrlArray.length; i++) {
            final String rtspUrl = rtspUrlArray[i];
            final String result = RtspKit.check(rtspUrl, null).toLowerCase();
            final String successSign = "success:";
            if (-1 != result.indexOf(successSign)) {
                final JSONObject obj = new JSONObject();
                obj.put("status", "success");
                obj.put("url", rtspUrl);
                obj.put("result", result.substring(successSign.length()));
                array.put(obj);
            } else {
                final JSONObject obj = new JSONObject();
                obj.put("status", "error");
                obj.put("url", rtspUrl);
                obj.put("result", result);
                array.put(obj);
            }
        }
        final long endTime = System.currentTimeMillis();
        System.out.println("Lost time: " + (endTime - startTime));
        final JSONObject obj = new JSONObject();
        obj.put("results", array);
        msg.setContent(obj);
        return msg;
    }

    @Method(description = "客户端检查", anonymousAccess = true, frequencys = { @Frequency(source = Frequency.Source.IP, count = 1000, unit = Frequency.Unit.SECOND) }, methodType = Method.Type.POST, parameters = {
            @Parameter(name = "token", text = "访问Token", type = Parameter.Type.STRING, allowNull = true, format = "^.{32}$", formatPrompt = "32位的任意字符", remark = ""),
            @Parameter(name = "rtsp_urls", text = "rtsp地址集合", type = Parameter.Type.STRING, allowNull = false, format = "^rtsp:\\/\\/[0-9a-zA-Z]*:[0-9a-zA-Z]*@((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}:[0-9]*\\/[a-zA-Z0-9]*(;rtsp:\\/\\/[0-9a-zA-Z]*:[0-9a-zA-Z]*@((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}:[0-9]*\\/[a-zA-Z0-9]*)*$", formatPrompt = "一个或多个rtsp地址集合", remark = "地址之间以分号分割，且集合中地址数量不能超过100个。") }, returns = @Returns(results = {
                    @ReturnResult(parentId = "", id = "array_id", name = "results", type = "array", isNecessary = true, description = "结果"),
                    @ReturnResult(parentId = "array_id", id = "", name = "status", type = "string[1,10]", isNecessary = true, description = "状态：success和error"),
                    @ReturnResult(parentId = "array_id", id = "", name = "url", type = "string[1,]", isNecessary = true, description = "rtsp地址"),
                    @ReturnResult(parentId = "array_id", id = "", name = "result", type = "string[1,]", isNecessary = true, description = "结果。如果状态为success结果为验证的延迟时间（字符串解析数字越小延迟越短），如果状态为非success结果为错误代码。错误代码意义如下：INVALID_RTSP_URL: 非法rtsp地址;CONNECT_TIMEOUT: 连接超时;HOST_PORT_CLOSED: 主机端口关闭;GET_STATUS_FAIL: 获取状态失败;GET_AUTHORIZATION_FAIL: 获取认证失败;INVALID_STATUS_CODE: 非法状态码;PARSE_AUTH_FAIL: 解析身份认证失败;AUTH_FAIL: 身份认证失败OTHER_ERROR: 其他错误") }))
    public Message checkOfClient() {
        // 客户端安全检查
        final Message resultMsg = AccessSecurity.check(this.parameter);
        if (Message.Status.SUCCESS != resultMsg.getStatus()) {
            return resultMsg;
        }
        return this.check();
    }
}